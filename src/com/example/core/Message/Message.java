package com.example.core.Message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.util.Log;

import com.example.yottaconnecter.*;
import com.example.core.Packet;
import com.example.core.SendSocket;
import com.example.sample.header.ReceiveMessageManager;
import com.example.sample.message.*;

public class Message {
	public static final int INT_SHIFT;
	public static final int bitMask;
	public static final int SESSION_MAX;
	public static final SimpleDateFormat timeFormat = new SimpleDateFormat("kk'時'mm'分'ss'秒'");
	
	static{
		INT_SHIFT = Integer.SIZE-5;
		bitMask = 0x08000000;
		SESSION_MAX = bitMask;
	}
	
	//パケット受信時動作振り分けルーチン
	public static void cntrol(Packet packet){
		
		//セッションがすでに保存されていれば
		if(MessageSessionList.getSession(packet) != null){
			return;
		}
		
		//OriginalDestinationMacが自分宛のパケットでない場合
		if(packet.getOriginalDestinationMac().compareTo(YottaConnector.MyNode.getMACAddr()) != 0){
			
			//転送処理
			packetRelay(packet);
			
		}else{
		//OriginalDestinationMacが自分宛のパケットの場合
			
			//受信処理
			receptMessage(packet);
		}
		
	}
	
	//UIからのメッセージ送信ルーチン
	public static void sendMessage(String message,String oDestMac){
		MessageRoot root;
		int type = Packet.Message;
		String myMacAddr = YottaConnector.MyNode.getMACAddr();
		String srcMac = myMacAddr;
		String destMac = Packet.broadCastMACaddr;
		int hopLimit = Packet.HopLimitMax;
		int typeNum;
		String tag = "sendMessage";
		
		
		//メッセージ番号を取得
		typeNum = MessageManager.getCount(oDestMac) % SESSION_MAX;
		
		//ルーティングテーブル検索
		root = MessageRootTable.getRoot(myMacAddr,oDestMac);
		
		//ルーティングテーブルがなければ
		if(root == null){
			//先頭ビットを1に
			typeNum = exTypeNumFlag(typeNum);
			Log.d(tag, "[ROOTREQ]");
		}else{
		//ルーティングテーブルがあれば
			//宛先をルーティングテーブルで設定
			destMac = root.getForwardMac();
			Log.d(tag, "[ROOT]");
		}
		
		//パケットヘッダ作成
		Packet sendPacket = new Packet(type,myMacAddr,oDestMac,srcMac,destMac,hopLimit,typeNum);
		String session = "messageSession";
		Log.d(session, ""+myMacAddr+">"+oDestMac);
		
		//ペイロード付加
		List<String> sendData = new ArrayList<String>();
		sendData.add(message);
		sendData.add(timeFormat.format(new Date()));
		sendPacket.createData(sendData);
		
		//セッション登録
		MessageSessionList.addSession(sendPacket);
		
		//メッセージ登録
		MessageManager.add(sendPacket.getOriginalSourceMac(),sendData.get(0),sendData.get(1),true);
		
		//パケット送信
		SendSocket send = new SendSocket(YottaConnector.ip);
		send.makeNewPacket(sendPacket);
		
	}
	
	//自端末宛メッセージ処理
	private static void receptMessage(Packet packet){
		ArrayList<String> dataList;
		MessageManager.Message res;
		String tag = "receptMessage";
		
		//ペイロード切り分け
		dataList = packet.putData();
		
		//メッセージ登録
		res = MessageManager.add(packet.getOriginalSourceMac(),dataList.get(0),dataList.get(1),false);
		
		//ヘッダー登録
		ReceiveMessageManager.addReceiveMessage(res);
		Log.d(tag,"["+Integer.toHexString(packet.getTypeNum())+"]");
		if(checkFlg(packet.getTypeNum())){
			//セッション保存
			MessageSessionList.addSession(packet);
		}
		Log.d(tag,"["+checkFlg(packet.getTypeNum())+"]");
		Log.d(tag,"["+Integer.toHexString(packet.getTypeNum())+"]");
		//MessageACK
		MessageACK.sendMessageACK(packet);
	}
	
	//パケット転送メソッド
	private static void packetRelay(Packet packet){
		//ペイロード切り分け
		
		//ルーティングテーブル作成であれば
		if(checkFlg(packet.getTypeNum())){
			
			//ホップリミット０であれば
			if(packet.getHopLimit() == 0){
				return;
			}
			
			
			//セッション保存
			MessageSessionList.addSession(packet);
			
			//パケットセット(SourceMacを自分のアドレスに)
			packet.setSourceMac(YottaConnector.MyNode.getMACAddr());
			
		}else{
		//ルーティングテーブル使用であれば
			
			MessageRoot root;
			//ルーティングテーブル検索
			root = MessageRootTable.getRoot(packet);
			if(root == null){
				return;
			}
			
			//ルーティングテーブルのタイムアウトをリセット
			MessageRootTable.resetTimeOut(packet);
			
			//パケットセット(SouceMacを自分のアドレスに)
			packet.setSourceMac(YottaConnector.MyNode.getMACAddr());
			
			//パケットセット（DestinationMacをルーティングテーブルからセット）
			packet.setDestinationMac(root.getForwardMac());
		}
		
		//パケット送信
		SendSocket send = new SendSocket(YottaConnector.ip);
		send.makeRaleyPacket(packet);
	}
	
	//intの先頭１ビットが１であればtrue,０であればfalseを返す
	public static boolean checkFlg(int type){
		String tag = "checkFlg";
		Log.d(tag,"["+Integer.toHexString(type)+"]");
		Log.d(tag,"["+Integer.toHexString((type >>> INT_SHIFT))+"]");
		if((type >>> INT_SHIFT) == 1) return true;
		return false;
	}
	
	//intの先頭１ビットを反転させる
	public static int exTypeNumFlag(int num){
		num ^= bitMask;
		return num; 
	}
}
