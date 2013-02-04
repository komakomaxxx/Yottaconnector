package com.example.core.NodeExchange;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.example.core.Packet;
import com.example.core.SendSocket;
import com.example.yottaconnecter.Node;
import com.example.yottaconnecter.NodeList;
import com.example.yottaconnecter.YottaConnector;

import android.R.bool;
import android.util.Log;



public class NodeExchangeReqest {

	static boolean sendFlag;
	static String tag  = " NodeExREQ";
	
	public static  int sendTime;
	static int typeSession =0;
	static ArrayList<NodeExchangeSessionData> sessionList = new ArrayList<NodeExchangeSessionData>();	
	static Timer sendTimer;
	protected static ArrayList<Node> newNodeList = new ArrayList<Node>();
	
	//受信
	public static void  recv(Packet recvPacket) {
		int hopLimit;
		

		Log.d(tag, recvPacket.getOriginalSourceMac());
		//パケット解析
		hopLimit = recvPacket.getHopLimit();
		
		if(hopLimit != 0 ){
			//中継
			relay(recvPacket);
		}
		//返信
		NodeExchangeReplay.sendReplay(recvPacket);
	}

	//中継
	public static void relay(Packet recvPacket) {
		//セッション登録
		String oSrcMac = recvPacket.getOriginalSourceMac();
		String srcMac =recvPacket.getSourceMac();
		int tMun =recvPacket.getTypeNum();
		addSession(oSrcMac,srcMac,tMun);
		//ヘッダ作成
		recvPacket.setSourceMac(YottaConnector.MyNode.getMACAddr());
		
		//送信
		new SendSocket().makeRaleyPacket(recvPacket);
	}
	//起点として送信
	public static void  sendReqest() {
		
		//自分発のセッションがないことを確認
		if(searchSession(YottaConnector.MyNode.getMACAddr(),0) != null){
			return;
		}
		//NodeListの初期化
		newNodeList.clear();
		
		String srcMac = YottaConnector.MyNode.getMACAddr();
		String dstMac = Packet.broadCastMACaddr;
		
		int hopLimit =Packet.HopLimitMax;
		int tNum=getTypeSession();
		String data="NodeExchangeReqest";
		
		//パケット作成
		Packet sendPacket = new Packet(Packet.NodeExREQ,srcMac,dstMac,srcMac,dstMac,hopLimit,tNum,data);
		
		addSession(srcMac,srcMac,tNum);
		
		new SendSocket().makeNewPacket(sendPacket);
		
	}
	
	public synchronized static int  getTypeSession() {
		
		int tmp = typeSession;
		typeSession++;
		
		return tmp;
	}
	
	//セッション登録
	public synchronized static void  addSession(String orignalMac, String dstMac, int tNum ) {
		
		/* 
		sessionDataを作成
		sessionListにadd
		*/
		//int tNum = getTypeSession();
		
		sessionList.add(new NodeExchangeSessionData(orignalMac,dstMac,tNum));
	}
	//セッション検索
	public synchronized static NodeExchangeSessionData searchSession(String oSrcMac ,int tNum) {
		/*
		 sessionListから一致するsessionDateを取得し返す
		 */
		if (sessionList == null){
			return null;
			
		}
		for(int i=0;i < sessionList.size();i++){
			if(sessionList.get(i).orignalMac.equals(oSrcMac)){
				/*
				//一応
				if(sessionList.get(i).sessionNumber == tNum){
					
				}
				*/
				return sessionList.get(i);
			}
		}
		return null;
	}
	//セッション削除
	public synchronized static void deleteSession(NodeExchangeSessionData sd ) {
		/*
		 一致するsessionDataをListから破棄する
		 */
		if(YottaConnector.MyNode.getMACAddr().equals(sd.getSrcMac())){
			Log.d(tag, "remove");
			for(Node n : newNodeList){
				Log.d("newNode", n.getMACAddr()+":"+n.getName());
			}
			NodeList.updateNodeList(newNodeList);
		}
		sessionList.remove(sd);	
	}
	
	//定期送信処理
	public static void startSendTimer(int time) {
		if (sendTimer == null){
		
			NodeExchangeSessionData.setTime(time);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			final int sendTime=time;
			(new Thread(new Runnable(){
				public void run() {
					sendTimer = new Timer();    
					sendTimer.schedule(new sendTimerTask(), 0,sendTime);
				}
			})).start();
		}
	}
	public static void stopSendTimer() {
		sendTimer.cancel();
		
	}
	//定期送信処理のスレッド
	static class sendTimerTask extends TimerTask {
    
		public void run(){
			sendReqest();
		}
	}
	//一応同期処理
	public synchronized static void updateNodeList() {
		//NodeListにnewNodeListを設定
		NodeList.updateNodeList(newNodeList);
		//UIに対して更新処理
	}
	
	
	
}
class NodeExchangeSessionData{
	static int timeOutTime = 10000;
	
	public int sessionNumber;
	public String orignalMac;
	public String srcMac;
	
	private Timer timeOutTimer;
	public NodeExchangeSessionData(String oMac,String sMac,int tNum) {
		// TODO Auto-generated constructor stub
		this.sessionNumber = tNum;
		this.orignalMac = oMac;
		this.srcMac = sMac;
		startSessionTimer();
	}
	public String getSrcMac() {
		return this.srcMac;
	}
	public static void setTime(int t) {
		timeOutTime = t;
		
	}

	//セッションのタイムスタンプチェック
	public void  timeOutSession() {
		NodeExchangeReqest.deleteSession(this);
		NodeExchangeReqest.updateNodeList();
		timeOutTimer.cancel();
	}
	
	void startSessionTimer() {
		if(timeOutTimer == null){
			timeOutTimer = new Timer();    
			timeOutTimer.schedule(new sessionTimerTask(), 0,timeOutTime);
		}
	}	
	//タイムスタンプ処理のスレッド
	class sessionTimerTask extends TimerTask {
	    
		public void run(){
			timeOutSession();
		}
	}
}
