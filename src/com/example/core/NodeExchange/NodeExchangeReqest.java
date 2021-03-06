package com.example.core.NodeExchange;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.example.core.Packet;
import com.example.core.SendSocket;
import com.example.yottaconnecter.Node;
import com.example.yottaconnecter.NodeList;
import com.example.yottaconnecter.YottaConnector;

import android.graphics.Bitmap;



public class NodeExchangeReqest {

	static boolean sendFlag;
	static String tag  = " NodeExREQ";
	
	public static  int sendTime;
	static int typeSession =0;
	public static ArrayList<NodeExchangeSessionData> sessionList = new ArrayList<NodeExchangeSessionData>();	
	static Timer sendTimer;
	protected static ArrayList<NodeExData> newNodeList = new ArrayList<NodeExData>();
	
	//受信
	public static void  recv(Packet recvPacket) {
		int hopLimit;
		String oSrc = recvPacket.getOriginalSourceMac();
		String oDst =recvPacket.getOriginalDestinationMac();
		
		if(oSrc.equals(YottaConnector.MyNode.getMACAddr())){
			return;
		}
		if(oDst.equals(Packet.broadCastMACaddr) == false ){
			return;
			
		}
		

		//パケット解析
		hopLimit = recvPacket.getHopLimit();
		
		//返信
		NodeExchangeReplay.sendReplay(recvPacket);
		
		if(hopLimit != 0 ){
			//中継
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			relay(recvPacket);
		}
	}

	//中継
	public static void relay(Packet recvPacket) {
		//セッション登録
		String oSrcMac = recvPacket.getOriginalSourceMac();
		String srcMac =recvPacket.getSourceMac();
		int tNum =recvPacket.getTypeNum();
		addSession(oSrcMac,srcMac,tNum);
		
		//ヘッダ作成
		recvPacket.setSourceMac(YottaConnector.MyNode.getMACAddr());
		

		//送信
		SendSocket.RelaySend(recvPacket);
		//new SendSocket().makeRaleyPacket(recvPacket);
		//new SendSocket().makeRaleyPacket(recvPacket);
	}
	//起点として送信
	public static void  sendReqest() {
		
		//自分発のセッションがないことを確認
		if(searchSession(YottaConnector.MyNode.getMACAddr(),0) != null){
			return;
		}

		
		String srcMac = YottaConnector.MyNode.getMACAddr();
		String dstMac = Packet.broadCastMACaddr;
		
		int hopLimit =Packet.HopLimitMax;
		int tNum=getTypeSession();
		String data="NodeExchangeReqest";
		
		//パケット作成
		Packet sendPacket = new Packet(Packet.NodeExREQ,srcMac,dstMac,srcMac,dstMac,hopLimit,tNum,data);
		
		addSession(srcMac,srcMac,tNum);
		
	
		//new SendSocket().makeNewPacket(sendPacket);
		
		SendSocket.newSend(sendPacket);
		
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
		NodeExchangeSessionData sess = new NodeExchangeSessionData(orignalMac,dstMac,tNum);
		
		sessionList.add(sess);
		sess.startSessionTimer();
	}
	//セッション検索
	public synchronized static NodeExchangeSessionData searchSession(String oSrcMac ,int tNum) {
		/*
		 sessionListから一致するsessionDateを取得し返す
		 */
		/*
		if (sessionList == null){
			return null;
		}
		*/
		for( NodeExchangeSessionData sd: sessionList){
			if(sd.orignalMac.equals(oSrcMac)){
				//一応
				if(sd.sessionNumber == tNum){
					return sd;
				}
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
			updateNodeList();

			//NodeListの初期化
			//newNodeList.clear();
		}
		sessionList.remove(sd);	
	}
	
	//定期送信処理
	public static void startSendTimer(int time) {
		if (sendTimer == null){
		
			NodeExchangeSessionData.setTime(time);
			
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
		ArrayList<NodeExData> removeList = new ArrayList<NodeExData>();
		for(NodeExData nd : newNodeList){
			if(nd.ttlDecrement() == false){
				removeList.add(nd);
			}
		}
		newNodeList.removeAll(removeList);
		ArrayList<Node> nodeList = new ArrayList<Node>();
		for(NodeExData nd : newNodeList){
			  nodeList.add(nd);
		}
		NodeList.updateNodeList(nodeList);
		//UIに対して更新処理
	}
	public synchronized static void addNode(NodeExData ned) {
		
		int i;		
		i = newNodeList.indexOf(ned);
		if(i != -1){
			newNodeList.set(i, ned);
		}else{
			newNodeList.add(ned);
		}		
	}
	
	
	
}
class NodeExchangeSessionData{
	static int timeOutTime=1000000;
	
	public int sessionNumber;
	public String orignalMac;
	public String srcMac;
	
	private Timer timeOutTimer;
	public NodeExchangeSessionData(String oMac,String sMac,int tNum) {
		// TODO Auto-generated constructor stub
		this.sessionNumber = tNum;
		this.orignalMac = oMac;
		this.srcMac = sMac;
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
		timeOutTimer.cancel();
	}
	
	void startSessionTimer() {
		if(timeOutTimer == null){
			timeOutTimer = new Timer();    
			timeOutTimer.schedule(new sessionTimerTask(),timeOutTime);
		}
	}	
	//タイムスタンプ処理のスレッド
	class sessionTimerTask extends TimerTask {
	    
		public void run(){
			timeOutSession();
		}
	}
}
class NodeExData  extends Node{

	private int ttl = 6*5;
	

	NodeExData(String MACAddr, String Name, double ido, double keido,Bitmap Icon, String profile) {

		super(MACAddr,Name,ido,keido,Icon,profile);
	}
	public boolean ttlDecrement() {
		ttl = ttl -1;
		if(ttl <= 0){
			return false;
		}else{
			return true;
		}
	}
}