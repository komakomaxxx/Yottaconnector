package com.example.core.NodeExchange;

import java.util.ArrayList;

import com.example.core.Packet;
import com.example.core.SendSocket;
import com.example.yottaconnecter.Node;
import com.example.yottaconnecter.YottaConnector;

import android.util.Log;


public class NodeExchangeReplay {
	private final static String tag = "NodeExREP";
	
	
	public static void recv(Packet recvPacket) {
		String oDstMac = recvPacket.getOriginalDestinationMac();
		int tNum = recvPacket.getTypeNum();
		
		Log.d(tag,"recv:"+oDstMac +":"+ tNum);
		NodeExchangeSessionData session = NodeExchangeReqest.searchSession(oDstMac,tNum);
				
		if(session != null){
			Log.d(tag,"recv:"+oDstMac);
			if( oDstMac.equals( YottaConnector.MyNode.getMACAddr())){
				NodeExchangeReqest.deleteSession(session);
				/*ノードリスト追加*/
				ArrayList<String> dataList = recvPacket.putData();
				
				String macAddr = recvPacket.getOriginalSourceMac();
				String name = dataList.get(0);
				Double ido = Double.valueOf(dataList.get(1));
				Double keido = Double.valueOf(dataList.get(2));
				String profile = dataList.get(3);
				
				Node n = new Node(macAddr,name,ido,keido,null,profile);
				//node に追加
				NodeExchangeReqest.newNodeList.add(n); 
				
			}else{
				relay(recvPacket,session);
			}	
		}
		else{
			Log.d(tag,"no:"+oDstMac);
			
		}
	}
	public static void relay(Packet sendPacket,NodeExchangeSessionData session) {
		//パケット作成
		
		sendPacket.setSourceMac(YottaConnector.MyNode.getMACAddr());
		sendPacket.setDestinationMac(session.getSrcMac());
		
		new SendSocket().makeRaleyPacket(sendPacket);
	}
	public static void  sendReplay(Packet recvPacket) {
		//パケット生成
		/*data 作成*/		
		ArrayList<String> dataList = new ArrayList<String>();
		dataList.add(YottaConnector.MyNode.getName());
		dataList.add(String.valueOf(YottaConnector.MyNode.getIdo()));
		dataList.add(String.valueOf(YottaConnector.MyNode.getKeido()));
		dataList.add(YottaConnector.MyNode.getProfile());
		
		int sessionNum = recvPacket.getTypeNum();
		int hopLimit =Packet.HopLimitMax;
		String srcMac = YottaConnector.MyNode.getMACAddr();
		String oDstMac = recvPacket.getOriginalSourceMac();
		String dstMac = recvPacket.getSourceMac();
		
		//paketと生成
		Packet sendPacket = new Packet(Packet.NodeExREP,srcMac,oDstMac,srcMac,dstMac,hopLimit,sessionNum);
		//data部設定
		sendPacket.createData(dataList);
		Log.d(tag, "send REP");
		new SendSocket().makeNewPacket(sendPacket);	
	}
}