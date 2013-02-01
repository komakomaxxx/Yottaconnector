package com.example.core.Hello;



import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.util.Log;
import com.example.core.Packet;
import com.example.core.SendSocket;
import com.example.yottaconnecter.Node;
import com.example.yottaconnecter.NodeList;
import com.example.yottaconnecter.YottaConnector;



public class Hello {
	private final static String tag = "Hello";
	
	static int typeSession = 0; 
	static ArrayList<Node> nearNodeList = new ArrayList<Node>(10000);
	static Timer sendTimer; 
	
	public static void recv(Packet recvPacket) {
	
		ArrayList<String> dataList = recvPacket.putData();
		
		String macAddr = recvPacket.getOriginalSourceMac();
		String name = dataList.get(0);
		Double ido = Double.valueOf(dataList.get(1));
		Double keido = Double.valueOf(dataList.get(2));
		String profile = dataList.get(3);
		
		Log.d(tag,"[" +macAddr+":"+name+":"+ido+":"+keido+":"+profile  );
		Node n = new Node(macAddr,name,ido,keido,null,profile);
		//node に追加
		addNode(n);
		HelloAck.sendHelloAck(recvPacket);		
	}
	private static void sendHello() {
		
		NodeList.updateNearNodeList(nearNodeList);
		nearNodeList.clear();
	/*
		パケット生成
		大先、先をブロードキャスト
		大本、元を自身のMACaadrss
		ホップリミット=0
		タイプ別番号=getTypeSession()	
	*/
		
		/*data 作成*/		
		ArrayList<String> dataList = new ArrayList<String>();
		dataList.add(YottaConnector.MyNode.getName());
		dataList.add(String.valueOf(YottaConnector.MyNode.getIdo()));
		dataList.add(String.valueOf(YottaConnector.MyNode.getKeido()));
		dataList.add(YottaConnector.MyNode.getProfile());
		
		int sessionNum = getTypeSession();
		int hopLimit =0;
		String srcMac = YottaConnector.MyNode.getMACAddr();
		String dstMac = Packet.broadCastMACaddr;
		
		//paketと生成
		Packet sendPacket = new Packet(Packet.Hello,srcMac,dstMac,srcMac,dstMac,hopLimit,sessionNum);
		//data部設定
		sendPacket.createData(dataList);		
		//送信
		//new testSendSocket(ip,sendPacket);
		new SendSocket().makeNewPacket(sendPacket);
	}
	public synchronized static void  addNode(Node n) {
		
		nearNodeList.add(n);
	}	
	public synchronized static int  getTypeSession() {
		
		int tmp = typeSession;
		typeSession++;
		
		return tmp;
	}
	//定期送信処理
	public static void startSendHello(int time) {
		if (sendTimer == null){
			final int sendTime=time;
			(new Thread(new Runnable(){
				public void run() {
					sendTimer = new Timer();    
					sendTimer.schedule(new sendTimerTask(), 0,sendTime);
				}
			})).start();
		}
	}
	public static void stopSendHello() {
		if (sendTimer != null){
			sendTimer.cancel();
			sendTimer = null;
		}
	}
	
	//定期送信処理のスレッド
	static class sendTimerTask extends TimerTask {
    
		public void run(){
			sendHello();
		}
	}
	
}