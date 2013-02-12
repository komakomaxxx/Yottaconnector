package com.example.core.Hello;

import java.util.ArrayList;

import com.example.core.Packet;
import com.example.core.SendSocket;
import com.example.yottaconnecter.YottaConnector;




public class HelloAck {
	private final static String tag = "HelloAck";
	
	public static void recv(Packet recvPacket) {
		
		if(YottaConnector.MyNode.getMACAddr().equals(recvPacket.getOriginalDestinationMac()) ){
			int sessionNum = recvPacket.getTypeNum();
			
			
			ArrayList<String> dataList = recvPacket.putData();
			
			String macAddr = recvPacket.getOriginalSourceMac();
			String name = dataList.get(0);
			Double ido = Double.valueOf(dataList.get(1));
			Double keido = Double.valueOf(dataList.get(2));
			String profile = dataList.get(3);
			
			HelloNodeData n = new HelloNodeData(macAddr,name,ido,keido,null,profile);
			n.setNodeDirection(YottaConnector.MyNode.getIdo(),YottaConnector.MyNode.getKeido() );
			//node に追加
			Hello.addNearNode(n);
			
			
			
		}
	}
	
	public static void  sendHelloAck(Packet recvPacket) {
		//パケット生成
		
		/*
		大先、先を元のパケットの大本、元MAC
		大本、元を自身のMACaadrss
		ホップリミット=0
		タイプ別番号=元のタイプ別番号
		*/
		/*
		自身のノードからdata部生成  
		 
		 */
		//data 作成		
		ArrayList<String> dataList = new ArrayList<String>();
		dataList.add(YottaConnector.MyNode.getName());
		dataList.add(String.valueOf(YottaConnector.MyNode.getIdo()));
		dataList.add(String.valueOf(YottaConnector.MyNode.getKeido()));
		dataList.add(YottaConnector.MyNode.getProfile());
		
		int sessionNum = recvPacket.getTypeNum();
		int hopLimit =0;
		String srcMac = YottaConnector.MyNode.getMACAddr();
		String dstMac = recvPacket.getOriginalSourceMac();
		
		//paketと生成
		Packet sendPack = new Packet(Packet.HelloAck,srcMac,dstMac,srcMac,	dstMac,hopLimit,sessionNum);
		//data部設定
		sendPack.createData(dataList);
		
		new SendSocket().makeNewPacket(sendPack);
	}
}
