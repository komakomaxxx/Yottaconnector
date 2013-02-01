package com.example.yottaconnecter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import android.util.Log;

public class SocketListenerNotify implements Runnable{

	private SocketListener listener;
	private Thread thread;
	
	public SocketListenerNotify(SocketListener listener) {
		// TODO 自動生成されたコンストラクター・スタブ
		this.listener = listener;
		thread = new Thread(this);
		thread.start();
	}

	public void run() {
		// TODO 自動生成されたメソッド・スタブ
		ServerSocket sock = null;
    	Boolean runFlag = true;
    	try {
			
    		while(runFlag){
    			sock = new ServerSocket(10000);
				Log.d("MyApp", "server wait...");
				//message = "wait...";
				Socket socket = sock.accept();
				
				BufferedReader br =
	                    new BufferedReader(
	                            new InputStreamReader(socket.getInputStream()));
				String str;
				while( (str = br.readLine()) != null ){
                    // exitという文字列を受け取ったら終了する
					listener.onSocketListener(str);
					if( "exit".equals(str)){
                        runFlag = false;
                    }
                }
				if( sock != null){
		            try {
		            	sock.close();
		            	sock = null;
		            } catch (IOException e) {
		                e.printStackTrace();
		            }
		        }
    		}
	    } catch (IOException e) {
	    	Log.d("MyApp", "!!!Exception!!!");
            e.printStackTrace();
        }
	}

}
