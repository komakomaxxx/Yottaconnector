package com.example.core.NodeExchange;

import java.util.ArrayList;

import com.example.core.Packet;
import com.example.core.SendSocket;
import com.example.yottaconnecter.Node;
import com.example.yottaconnecter.YottaConnector;

import android.content.res.Resources.Theme;
import android.util.Log;


public class NodeExchangeReplay {
	private final static String tag = "NodeExREP";
	
	
	public static void recv(Packet recvPacket) {
		String oDstMac = recvPacket.getOriginalDestinationMac();
		String dstMac = recvPacket.getDestinationMac();
		int tNum = recvPacket.getTypeNum();
		

		NodeExchangeSessionData session = NodeExchangeReqest.searchSession(oDstMac,tNum);
		
		if(session != null){
Log.d(tag,"recv"+recvPacket.getTypeNum() +":"+ recvPacket.getOriginalSourceMac()+"->"+recvPacket.getSourceMac()+"->"+recvPacket.getOriginalDestinationMac());

			if( oDstMac.equals(YottaConnector.MyNode.getMACAddr())){
				Log.d(tag, "Get Replay to "+recvPacket.getTypeNum() +":"+ recvPacket.getOriginalSourceMac());
				
				/*ノードリスト追加*/
				ArrayList<String> dataList = recvPacket.putData();
				
				String macAddr = recvPacket.getOriginalSourceMac();
				String name = dataList.get(0);
				Double ido = Double.valueOf(dataList.get(1));
				Double keido = Double.valueOf(dataList.get(2));
				String profile = dataList.get(3);
				
				NodeExData n = new NodeExData(macAddr,name,ido,keido,null,profile);
				//node に追加
				NodeExchangeReqest.addNode(n); 
				
			}else if (dstMac.equals(YottaConnector.MyNode.getMACAddr())){
				relay(recvPacket,session);
			}	
		}
		else{
Log.d(tag, "NoSession"+recvPacket.getTypeNum() +":"+ recvPacket.getOriginalSourceMac()+"->"+recvPacket.getSourceMac()+"->"+recvPacket.getOriginalDestinationMac());

		}
	}
	public static void relay(Packet sendPacket,NodeExchangeSessionData session) {
		//パケット作成
		
		sendPacket.setSourceMac(YottaConnector.MyNode.getMACAddr());
		sendPacket.setDestinationMac(session.getSrcMac());
		
//		new SendSocket().makeRaleyPacket(sendPacket);
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

		
		new SendSocket().makeNewPacket(sendPacket);	
Log.d(tag,"send"+recvPacket.getTypeNum() +":"+ sendPacket.getOriginalSourceMac()+"->"+sendPacket.getSourceMac()+"->"+sendPacket.getDestinationMac()+"->"+sendPacket.getOriginalDestinationMac());
		
	}
	private static void newSend(Packet sendPacket) {
		int sNum = SendSocket.getSequenceNUM();
		sendPacket.setSequenceNum(sNum);

		for(int i=0;i<5;i++){
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
	private static void RelaySend(Packet sendPacket) {
		for(int i=0;i<5;i++){
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