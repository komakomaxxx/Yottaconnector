package com.example.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.wifi.WifiManager;
import android.util.Log;



public class SendSocket implements Runnable{

	private String host;
	private String text;
	private int type;
	private Thread thread;
	private static int SequenceNUM = 0;
	private char[] SendCharArray;
	
	public SendSocket(String host,String Text,int type) {
		// TODO 自動生成されたコンストラクター・スタブ
		
		this.host = host;
		this.text = Text;
		this.type = type;
		thread = new Thread(this);
		thread.start();
	}
	

	public SendSocket() {
		this.host = "192.168.0.101";
	}
	public SendSocket(String host) {
		// TODO 自動生成されたコンストラクター・スタブ
		
		this.host = "192.168.0.101";
		/*
		this.ImageBuf = new byte[tmpList.length];
		for(int i = 0;i < tmpList.length;i++){
			this.ImageBuf[i] = tmpList[i];
		}
		*/
		
		
	}

	@Override
	public void run() {
		// TODO 自動生成されたメソッド・スタブ
		Socket socket;
		try {
			socket = new Socket(host,9999);
			
			OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());
			
			osw.write(SendCharArray);
			//Thread.sleep(10);
			//osw.flush();
			
			osw.close();
			socket.close();
			Log.d("SendSocket","SendCharArray count : " + name());
			
		} catch (UnknownHostException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		return;
	} 
	

	
	private synchronized int getSequenceNUM(){
		return SequenceNUM++;
	}
	
//	public char[] makePacket(int Type,int hopLimit,String osm
//			,String odm,String sm,String dm,int typenum,String text,Integer[] ImageBuf){
//		
//		List<Integer> iBufQ = new ArrayList<Integer>();
//		List<Character> packetQ = new ArrayList<Character>();
//		char[] packet;
//		int stx = 0x02;
//		int etx = 0x03;
//		int SOI = 0xFFD8;
//		int EOI = 0xFFD9;
//		char[] HopLimit; //ホップリミット10
//		char[] SequenceNum;
//		char[] caMactmp;
//		char[] TypeNum;
//		String[] Mymac = osm.split(":");
//		String[] DummyMac0 = odm.split(":");
//		String[] DummyMac1 = sm.split(":");
//		String[] DummyMac2 = dm.split(":");
//		
//		
//		String hl = Integer.toHexString(hopLimit);
//		hl = String.format("%02x", Integer.parseInt(hl,16));
//		HopLimit = hl.toCharArray();
//
//		
//		String ssnum = Integer.toHexString(getSequenceNUM());
//		ssnum = String.format("%04x", Integer.parseInt(ssnum,16));
//		SequenceNum = ssnum.toCharArray();
//		
//		String stnum = Integer.toHexString(typenum);
//		stnum = String.format("%04x", Integer.parseInt(stnum,16));
//		TypeNum = stnum.toCharArray();
//		
//		
//		//stxを追加
//		iBufQ.add(stx);
//		//タイプを追加
//		iBufQ.add((int)(String.valueOf(Type).charAt(0)));
//		
//		//大元MACアドレスに自分のMACアドレスを追加
//		for (int i = 0 ; i < Mymac.length;i++) {
//			caMactmp = Mymac[i].toCharArray();
//			for (char c : caMactmp) {
//				iBufQ.add((int)c);
//			}
//		}
//		//大先MACアドレスにDummyMac0を追加
//		for (int i = 0 ; i < DummyMac0.length;i++) {
//			caMactmp = DummyMac0[i].toCharArray();
//			for (char c : caMactmp) {
//				iBufQ.add((int)c);
//			}
//		}
//		//元MACアドレスにDummyMac1を追加
//		for (int i = 0 ; i < DummyMac1.length;i++) {
//			caMactmp = DummyMac1[i].toCharArray();
//			for (char c : caMactmp) {
//				iBufQ.add((int)c);
//			}
//		}
//		//先MACアドレスにDummyMac2を追加
//		for (int i = 0 ; i < DummyMac2.length;i++) {
//			caMactmp = DummyMac2[i].toCharArray();
//			for (char c : caMactmp) {
//				iBufQ.add((int)c);
//			}
//		}
//		//ホップリミットを追加
//		iBufQ.add((int)HopLimit[0]);
//		iBufQ.add((int)HopLimit[1]);
//		//シーケンス番号を追加
//		for (char c : SequenceNum) {
//			iBufQ.add((int)c);
//		}
//		//タイプ別番号を追加
//		for (char c : TypeNum) {
//			iBufQ.add((int)c);
//		}
//		//データ部を追加
//		if(text != null){
//			char[] Ctext =  text.toCharArray();
//			for (char c : Ctext) {
//				iBufQ.add((int)c);
//			}
//		}
//		
//		//etxを追加
//		iBufQ.add(etx);
//		if(ImageBuf != null){
//			for (Integer i : ImageBuf) {
//				iBufQ.add(i);
//			}
//		}
//		
//		
//		
//		for(int i = 0;i < iBufQ.size();i++){
//			//packet[i] = (char)iBufQ.poll().intValue();
//			packetQ.add((char)iBufQ.get(i).intValue());
//		}
//		
//		
//		
//		packet = new char[packetQ.size()];
//		for(int i = 0;i < packetQ.size();i++){
//			packet[i] = packetQ.get(i).charValue();
//		}
//		return packet;
//	}
//	
	
	private static int i = 0;
	public synchronized int name() {
		return i++;
	}
	public void makeNewPacket(Packet packet){
		
		/*
		SendCharArray = makePacket(packet.getType()
				, packet.getHopLimit()
				, packet.getOriginalSourceMac()
				, packet.getOriginalDestinationMac()
				, packet.getSourceMac()
				, packet.getDestinationMac()
				, packet.getTypeNum()
				, packet.getData()
				,packet.getImageArray()
				).clone();
		 */
		

		SendCharArray = makePacket(packet);

		thread = new Thread(this);
		thread.start();
	}
	
	public void makeRaleyPacket(Packet packet){
		packet.hoplimitDecrement();
		/*
		SendCharArray = makePacket(packet.getType()
				, packet.getHopLimit()
				, packet.getOriginalSourceMac()
				, packet.getOriginalDestinationMac()
				, packet.getSourceMac()
				, packet.getDestinationMac()
				, packet.getTypeNum()
				, packet.getData()
				,packet.getImageArray()
				).clone();
		*/
		Log.d("SendSocket","SendCharArray length is" + SendCharArray.length);
		SendCharArray = makePacket(packet);
		thread = new Thread(this);
		thread.start();
	}
	public char[] makePacket(Packet p){
		
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
		String ssnum = Integer.toHexString(getSequenceNUM());
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
		
		return cBuf;
	}
}
