package com.example.core.Hello;



import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Bitmap;
import com.example.core.Packet;
import com.example.core.SendSocket;
import com.example.yottaconnecter.Node;
import com.example.yottaconnecter.NodeList;
import com.example.yottaconnecter.YottaConnector;



public class Hello {
	private final static String tag = "Hello";
	
	static int typeSession = 0; 
	static ArrayList<HelloNodeData> nearNodeList = new ArrayList<HelloNodeData>(10000);
	static Timer sendTimer; 
	
	public static void recv(Packet recvPacket) {
	
		ArrayList<String> dataList = recvPacket.putData();
		
		String macAddr = recvPacket.getOriginalSourceMac();
		String name = dataList.get(0);
		Double ido = Double.valueOf(dataList.get(1));
		Double keido = Double.valueOf(dataList.get(2));
		String profile = dataList.get(3);
		
		//HelloNodeData n = new HelloNodeData(macAddr,name,ido,keido,null,profile);
		HelloNodeData n = new HelloNodeData(macAddr,name,ido,keido,null,profile);
		//node に追加
		NodeList.addNode(n);
		addNearNode(n);
		HelloAck.sendHelloAck(recvPacket);
	}
	private static void sendHello() {
		
		updateNearNodeList();
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
		new SendSocket().makeNewPacket(sendPacket);
	}
	
	public synchronized static int  getTypeSession() {
		
		int tmp = typeSession;
		typeSession++;
		
		return tmp;
	}
	
	//一応同期処理
	public synchronized static void updateNearNodeList() {
		//NodeListにnewNodeListを設定
	
		ArrayList<HelloNodeData> removeNodeList = new ArrayList<HelloNodeData>(1000);
		for(HelloNodeData nd : nearNodeList){
			if(nd.ttlDecrement() == false){
				removeNodeList.add(nd);
			}
		}
		nearNodeList.removeAll(removeNodeList);
		ArrayList<Node> nodeList = new ArrayList<Node>();
		for(HelloNodeData nd : nearNodeList){
			  nodeList.add(nd);
		}
		
		NodeList.updateNearNodeList(nodeList);
		//UIに対して更新処理
	}
	public synchronized static void addNearNode(HelloNodeData n) {
		
		int i;		
		i = nearNodeList.indexOf(n);
		if(i != -1){
			nearNodeList.set(i, n);
		}else{
			nearNodeList.add(n);
		}		
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
class HelloNodeData  extends Node{

	private int ttl = 6*5;
	

	HelloNodeData(String MACAddr, String Name, double ido, double keido,Bitmap Icon, String profile) {

		super(MACAddr,Name,ido,keido,Icon,profile);
	}
	protected boolean ttlDecrement() {
		ttl = ttl -1;
		if(ttl <= 0){
			return false;
		}
		return true;
	}
}