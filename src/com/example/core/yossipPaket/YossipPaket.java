package com.example.core.yossipPaket;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;


import com.example.yottaconnecter.Yossip;
import com.example.yottaconnecter.YossipList;
import com.example.yottaconnecter.YottaConnector;
import com.example.core.Packet;
import com.example.core.SendSocket;

public class YossipPaket {
	private final static String tag = "YossipPacket";
	public static void recv(Packet recvPacket) {
		
		int hoplimit = recvPacket.getHopLimit();
		
		if(hoplimit != 0){
			relay(recvPacket);
		}
		/*
		 データ部から
		 ヨシップリストに追加
		 name取得
		 ヨシップ取得
		 time取得
		 ヘッダ部から大元MAC取得
		 */
		ArrayList<String> dataList = recvPacket.putData();
		
		String macAddr = recvPacket.getOriginalSourceMac();
		String yossip = dataList.get(0);
		String YossipTime = dataList.get(1);
		String name = dataList.get(2);
		
		Date d = null;
		try {
			d = Yossip.timeFormat.parse(YossipTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
		}
		YossipList.addYossip(new Yossip(yossip,d,macAddr,name));
		
		
		
	}
	public static void  relay(Packet recvPacket) {
		/*
		 パケット生成
		 */
		recvPacket.setSourceMac(YottaConnector.MyNode.getMACAddr());
		
		new SendSocket().makeRaleyPacket(recvPacket);
	}
	public static void sendYossip( String yos ) {
		
		String time = Yossip.timeFormat.format(new Date());
		
	
		ArrayList<String> dataList = new ArrayList<String>();
		dataList.add(yos);
		dataList.add(time);
		dataList.add(String.valueOf(YottaConnector.MyNode.getName()));
		
		/*
		 パケット生成
		 元、大元を自MAC
		 先、大先をブロードキャスト
		 hoplimit　MAX
		 シーケンス番号
		 */
		String srcMac = YottaConnector.MyNode.getMACAddr();
		String dstMac = Packet.broadCastMACaddr;
		int hopLimit=Packet.HopLimitMax;
		Packet sendPacket = new Packet(Packet.Yossip,srcMac,dstMac,srcMac,	dstMac,hopLimit,0,null);
		sendPacket.createData(dataList);
		
		new SendSocket().makeNewPacket(sendPacket);
	}
}
