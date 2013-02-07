package com.example.core.Image;

import android.util.Log;

import com.example.yottaconnecter.*;
import com.example.core.Packet;
import com.example.core.SendSocket;

public class ImageSessionACK {
	
	public static String tag = "ImageSesionACK";

	public static void cntrol(Packet packet){
		ImageSession session;
		
		session = ImageSessionList.getSessionRe(packet);
		
		Log.d(tag,"control");
		//セッションが存在しないか、ステータスがSYNでない場合処理を行わない
		if(session == null || session.getStatus() != ImageSession.STS_SYN){
			return;
		}
		
		sessionReplace(session,packet);
		ImageSessionList.resetTimeOut(session);
		
		if(packet.getOriginalDestinationMac().compareTo(YottaConnector.MyNode.getMACAddr()) != 0){
			packetRelay(session,packet);
		}else{
			ImageData.SynAckPacketSend(packet);
		}
	}
	
	public static void sendACK(Packet packet){
		ImageSession session;
		
		session = ImageSessionList.getSession(packet);
		
		if(session == null){
			return;
		}
		Log.d(tag,"sendACK");
			
		//画像要求MACアドレスをセッションに設定
		session.setFindMac(packet.getOriginalDestinationMac());
		
		//画像提供MACアドレスをオリジナルディスティネーションに;
		session.setOriginalDestinationMac(YottaConnector.MyNode.getMACAddr());
		
		//セッションステータスをACKに変更
		session.setStatus(ImageSession.STS_ACK);
		
		//ACKパケット作成
		packet.setType(Packet.ImageACK);
		packet.setOriginalDestinationMac(YottaConnector.MyNode.getMACAddr());
		packet.setDestinationMac(packet.getOriginalDestinationMac());
		packet.exOriginalMac();
		packet.exMac();
		
		//パケット送信
		SendSocket send = new SendSocket(YottaConnector.ip);
		send.makeNewPacket(packet);
	}
	
	public static void packetRelay(ImageSession session,Packet packet){
		
		Log.d(tag,"packetRelay");
		//パケットヘッダ付け替え
		packet.exMac();
		packet.setDestinationMac(session.getSourceMac());
		
		//パケット送信
		SendSocket send = new SendSocket(YottaConnector.ip);
		send.makeRaleyPacket(packet);
	}

	private static void sessionReplace(ImageSession session,Packet packet){
		
		Log.d(tag,"sessionReplace");
		//画像要求MACアドレスを設定
		session.setFindMac(session.getOriginalDestinationMac());
		
		//要求先MACアドレスを設定
		session.setOriginalDestinationMac(packet.getOriginalSourceMac());
		
		//セッションステータスをACKにに変更
		session.setStatus(ImageSession.STS_ACK);
		
	}
}
