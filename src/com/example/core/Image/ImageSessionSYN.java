package com.example.core.Image;

import android.util.Log;

import com.example.yottaconnecter.*;
import com.example.core.Packet;
import com.example.core.SendSocket;

public class ImageSessionSYN {
	
	private static int SessionID = 0;
	
	private static boolean StartFlug = true;
	
	private static String tag = "ImageSessionSYN";

	//パケット受信時動作振り分けルーチン
	public static void cntrol(Packet packet){
		
		//セッションがすでに保存されていれば
		if(ImageSessionList.getSession(packet) != null){
			return;
		}
		
		ImageSessionList.addSession(packet);
		Log.d(tag,"cntrol");
		
		//OriginalDestinationMacが自分宛のパケットでない場合
		if(packet.getOriginalDestinationMac().compareTo(YottaConnector.MyNode.getMACAddr()) != 0){
			
			//要求ノードの画像を自分が保持していれば
			Node node = NodeList.getNode(packet.getOriginalDestinationMac());
			if(node != null && node.getIcon() != null){
		Log.d(tag,"find Icon");
				//ACKを送信
				ImageSessionACK.sendACK(packet);
			}else{
				//転送処理
		Log.d(tag,"Relay SYN");
				packetRelay(packet);
			}
			
		}else{
		//OriginalDestinationMacが自分宛のパケットの場合
			//ACKを送信
		Log.d(tag,"send ACK myMAC");
			ImageSessionACK.sendACK(packet);
		}
		
	}
	
	public static void sendImageSYN(){
		
		(new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO 自動生成されたメソッド・スタブ
				Node node;
				
				int type = Packet.ImageSYN;
				String oSrcMac = YottaConnector.MyNode.getMACAddr();
				String oDestMac = null;
				String srcMac = oSrcMac;
				String destMac = Packet.broadCastMACaddr;
				int hopLimit = Packet.HopLimitMax;
				int typeNum = getSessionID();	
				
				//ノードリストからイメージのないノードを取得
				//取得できるまでルーブ
				while(true){
					 node = NodeList.getNoImageNode();
				
					if(node != null){
						StartFlug = false;
						break;
					}
					
		Log.d(tag,"roop");
					try {
						if(StartFlug){
							Thread.sleep(100);
						}else{
							Thread.sleep(1000*60*1);
						}
					} catch (InterruptedException e) {
						// TODO 自動生成された catch ブロック
						e.printStackTrace();
					}
				}
				
		Log.d(tag,"send SYN");
				//ノードのMACアドレス取得
				oDestMac = node.getMACAddr();
				
				//パケットヘッダ作成
				Packet sendPacket = new Packet(type,oSrcMac,oDestMac,srcMac,destMac,hopLimit,typeNum);
				
				//セッション作成
				ImageSessionList.addSession(sendPacket);
				
				//パケット送信
				SendSocket send = new SendSocket(YottaConnector.ip);
				send.makeNewPacket(sendPacket);
				
			}
		})).start();
		
	}
	
	//パケット中継ルーチン
	private static void packetRelay(Packet packet){
		packet.setSourceMac(YottaConnector.MyNode.getMACAddr());
		
		if(packet.getHopLimit() == 0){
			return;
		}
		
		//パケット送信
		SendSocket send = new SendSocket(YottaConnector.ip);
		send.makeRaleyPacket(packet);
	}
	
	private static synchronized int getSessionID(){
		return SessionID++;
	}
	
}
