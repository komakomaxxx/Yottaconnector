package com.example.core;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.R.integer;

import com.example.core.Session.*;
import com.example.yottaconnecter.YottaConnector;

public class SendSocket implements Runnable{

	private String host;
	private String text;
	private int type;
	private Thread thread;
	private static int SequenceNUM = 0;
	private char[] SendCharArray;
	private static integer i  = new integer();
	
	public SendSocket(String host,String Text,int type) {
		// TODO 自動生成されたコンストラクター・スタブ
		
		this.host = host;
		this.text = Text;
		this.type = type;
		thread = new Thread(this);
		thread.start();
	}
	

	public SendSocket() {
		this.host = YottaConnector.ip;
	}
	public SendSocket(String host) {
		// TODO 自動生成されたコンストラクター・スタブ
		
		this.host = YottaConnector.ip;
	}

	@Override
	public void run() {
		// TODO 自動生成されたメソッド・スタブ
		Socket socket;
		try {
			synchronized (i) {
				socket = new Socket(host,9999);
				
	
				OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());
				
				osw.write(SendCharArray);
				osw.close();

				socket.close();
			}
			
		} catch (UnknownHostException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		return;
	} 
	

	
	public static synchronized int getSequenceNUM(){
		return SequenceNUM++;
	}

	

	public synchronized integer name() {
		return i;
	}
	public void makeNewPacket(Packet packet){
		
		SendCharArray = makePacket(packet,getSequenceNUM());
		
		
		thread = new Thread(this);
		thread.start();
	}
	
	public void makeRaleyPacket(Packet packet){
		packet.hoplimitDecrement();
		//Log.d("SendSocket","SendCharArray length is" + SendCharArray.length);
		SendCharArray = makePacket(packet,packet.getSequenceNum());
		thread = new Thread(this);
		thread.start();
	}
	public char[] makePacket(Packet p,int snum){
		
		StringBuffer Buf = new StringBuffer(1500);
		char stx = 0x02;
		char etx = 0x03;
		
		
		//Stx追加
		Buf.append(String.valueOf(stx));
		
		//タイプを追加
		Buf.append(String.valueOf(p.getType()));
		
		//大元MAC
		Buf.append(p.getOriginalSourceMac());
		
		//大先MAC
		Buf.append(p.getOriginalDestinationMac());
		
		//元MAC
		Buf.append(p.getSourceMac());
		
		//先MAC
		Buf.append(p.getDestinationMac());
		
		//hopliit
		String hl = Integer.toHexString(p.getHopLimit());
		hl = String.format("%02x", Integer.parseInt(hl,16));	
		Buf.append(hl);

		//シーケンスナンバ
		String ssnum = Integer.toHexString(snum);
		ssnum = String.format("%08x", Integer.parseInt(ssnum,16));
		Buf.append(ssnum);
		
		//typeNum
		String tnum = Integer.toHexString(p.getTypeNum());
		tnum = String.format("%08x", Integer.parseInt(tnum,16));
		Buf.append(tnum);

		//data
		Buf.append(p.getData());
		
		//Etx追加
		Buf.append(String.valueOf(etx));
		
		if(p.getType() == 9){
			Buf.append(p.getImageArray());
		}

		//Log.d("SendSocket",Buf.toString());

		char [] cBuf;
		cBuf = Buf.toString().toCharArray();
		
		SessionCtl.addSession(p.getOriginalSourceMac(), Integer.parseInt(ssnum,16));
		return cBuf;
	}
	public static void newSend(Packet sendPacket) {
		int sNum = SendSocket.getSequenceNUM();
		sendPacket.setSequenceNum(sNum);

		for(int i=0;i<3;i++){
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sendPacket.setHopLimit(sendPacket.getHopLimit()+1);
			new SendSocket().makeRaleyPacket(sendPacket);
		}
	}
	public static void RelaySend(Packet sendPacket) {
		for(int i=0;i<3;i++){
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			new SendSocket().makeRaleyPacket(sendPacket);
		}
	}
}
