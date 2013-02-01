package com.example.core;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


import android.app.Activity;
import android.util.Log;


public class Socket_listen implements Runnable{

	private Thread thread;
    private String host;
    private List<Integer> bufQ = new ArrayList<Integer>(1000);
 	private List<Integer> imageBuf = new ArrayList<Integer>();
 	private int Status = 0;
 	
    //private Queue<Integer> bufQ = new LinkedList<Integer>();
	
    
	public Socket_listen(String ip) {
		// TODO 自動生成されたコンストラクター・スタブ
		this.host = ip;
		thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {
		// TODO 自動生成されたメソッド・スタブ
		
		int port = 10000;
		
		Socket socket;
		BufferedReader reader;
		
		Log.d("MyApp", "run");
		
		try {
			socket = new Socket(host, port);		
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()),10*1024);
			
			while(true){
				//Thread.sleep(100);
				int line = reader.read();
				
				ExchangeData(line);
				
			}
		} catch (UnknownHostException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			Log.d("MyApp", "UnknownHostException");
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			Log.d("MyApp", "IOException");
//		} catch (InterruptedException e) {
//			// TODO 自動生成された catch ブロック
//			e.printStackTrace();
		}
	}
	
	public void ExchangeData(int line) {
		int NodeInImageCode = 0x39;
		
		if(Status == 0 && line == 0x02){
			Status = 1;
			Log.d("Socket_Listen","Status = 1 start Packet Buffering");
		}else if(Status == 1){
			if(line != 0x03){
				bufQ.add(line);
			}else{
				if(bufQ.get(0) == NodeInImageCode){
					Status = 2;
				}else{
					//キューを振り分けスレッドに渡す
					makeReceivePacket();
					Log.d("Socket_Listen","Status = 0 end Packet Buffering");
					bufQ.clear();
					Status = 0;
				}
			}
		}
		else if (Status == 2) {
			if(line == 0xffd8){
				Status = 3;
				Log.d("Socket_Listen","Status = 3 start Image Buffering");
			}
		}
		else if(Status == 3){
			if(line == 0xFFD9){
					Status = 0;
					Log.d("Socket_Listen","Status = 0 end Image Buffering.ImageBuffer size is " + imageBuf.size());
					makeReceiveImagePacket();
					bufQ.clear();
					imageBuf.clear();
					
			}else{
				imageBuf.add(line);
			}
		}
	}
	
	public void makeReceivePacket(){
		new SettingData(bufQ);
	}
	
	public void makeReceiveImagePacket(){
		new SettingData(bufQ, imageBuf);
	}

}
