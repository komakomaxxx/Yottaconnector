package com.example.core.Message;

import java.util.ArrayList;

import android.util.Log;

import com.example.yottaconnecter.*;
import com.example.core.Packet;
import com.example.core.SendSocket;
import com.example.sample.message.*;

public class MessageACK {
	
	public static void cntrol(Packet packet){
		
		//OriginalDestinationMacが自分宛のパケットでない場合
		if(packet.getOriginalDestinationMac().compareTo(YottaConnector.MyNode.getMACAddr()) != 0){
			
			//転送処理
			packetRelay(packet);
		}else{
		//OriginalDestinationMacが自分宛のパケットの場合
			receptMessageACK(packet);
		}

	}
	
	//Message受信時にメッセージACKを返す
	public static void sendMessageACK(Packet packet){
		MessageRoot root;
		
		
		packet.setType(Packet.MessageAck);
		packet.setHopLimit(Packet.HopLimitMax);
		packet.exOriginalMac();
		
		//ルートテーブル作成であれば
		if(Message.checkFlg(packet.getTypeNum())){
			setSimplexRootTable(packet);
		}else{
		//ルートテーブル使用であれば
			root = updateSimplexRootTable(packet);
			if(root == null){
				setSimplexRootTable(packet);
			}
		}
		
		packet.exMac();
		
		packet.setData(null);
		
		//パケット送信
		SendSocket send = new SendSocket(YottaConnector.ip);
		send.makeNewPacket(packet);
	}
	
	private static void receptMessageACK(Packet packet){
		MessageSession session;
		
		//セッションテーブルからセッション取得
		session = MessageSessionList.getSessionACK(packet);
		
		//セッションがなければ
		if(session == null){
			return;
		}
		
		//セッション削除
		MessageSessionList.removeSession(session,true);
		
		//パケット送信
		//ルーティングテーブル作成であれば
		if(Message.checkFlg(packet.getTypeNum())){
			//ルーティングテーブルを作成する
			setSimplexRootTableRe(packet);
		}else{
		//ルートテーブルを使用であれば
			updateSimlexRootTableRe(packet);
		}
	}
	
	private static void packetRelay(Packet packet){
		MessageSession session;
		
		//ルートテーブル作成であれば
		if(Message.checkFlg(packet.getTypeNum())){
			
			//セッションテーブルからセッションを取得
			session = MessageSessionList.getSessionACK(packet);
			
			//セッションテーブルがなければ
			if(session == null){
				return;
			}
				
			packet.exMac();
			packet.setDestinationMac(session.getSourceMac());
			
			setDuplexRootTable(packet,session.getSourceMac());
		}else{
		//ルートテーブル使用であれば
			MessageRoot root;
			updateDuplexRootTable(packet);
			root = MessageRootTable.getRoot(packet);
			
			if(root == null){
				return;
			}
			
			packet.exMac();
			packet.setDestinationMac(root.getForwardMac());
		}
		
		SendSocket send = new SendSocket(YottaConnector.ip);
		send.makeRaleyPacket(packet);
	}
	
	//単方向ルートテーブル作成
	private static void setSimplexRootTable(Packet packet){
		MessageRootTable.addRoot(packet, packet.getSourceMac());
	}
	
	//単方向ルートテーブル作成（MAC反転）
	private static void setSimplexRootTableRe(Packet packet){
		packet.exOriginalMac();
		MessageRootTable.addRoot(packet, packet.getSourceMac());
		packet.exOriginalMac();
	}
	
	//双方向ルートテーブル作成
	private static void setDuplexRootTable(Packet packet,String SessionSourceMac){
		//パケット受信側ルートテーブル作成
		setSimplexRootTable(packet);
		
		//オリジナルマックアドレス反転
		packet.exOriginalMac();
		
		//セッション側ルートテーブル作成
		MessageRootTable.addRoot(packet, SessionSourceMac);
		
		//オリジナルマックアドレス反転
		packet.exOriginalMac();
	}
	
	//単方向ルートテーブル更新
	private static MessageRoot updateSimplexRootTable(Packet packet){
		return MessageRootTable.resetTimeOut(packet);
	}
	
	//単方向ルートテーブル更新（MAC反転）
	private static MessageRoot updateSimlexRootTableRe(Packet packet){
		MessageRoot root;
		
		packet.exOriginalMac();
		root = MessageRootTable.resetTimeOut(packet);
		packet.exOriginalMac();
		
		return root;
	}
	
	//双方向ルートテーブル更新
	private static void updateDuplexRootTable(Packet packet){
		//パケット受信側更新
		MessageRootTable.resetTimeOut(packet);
		
		//オリジナルマックアドレス反転
		packet.exOriginalMac();
		
		//パケット送信側更新
		MessageRootTable.resetTimeOut(packet);
		
		//オリジナルマックアドレス反転
		packet.exOriginalMac();
	}
	
}
