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
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.SeekBar;



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
		
Log.d(tag,""+recvPacket.getTypeNum() +":"+ recvPacket.getOriginalSourceMac()+"->"+recvPacket.getSourceMac()+"->"+recvPacket.getOriginalDestinationMac());

		//パケット解析
		hopLimit = recvPacket.getHopLimit();
		
		//返信
		NodeExchangeReplay.sendReplay(recvPacket);
		
		if(hopLimit != 0 ){
			//中継
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
		
Log.d(tag,"relay:"+recvPacket.getTypeNum() +":"+ recvPacket.getOriginalSourceMac()+"->"+recvPacket.getSourceMac()+"->"+recvPacket.getOriginalDestinationMac());

		//送信
		new SendSocket().makeRaleyPacket(recvPacket);
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
		
		
		int sNum = SendSocket.getSequenceNUM();
		sendPacket.setSequenceNum(sNum);
//		new SendSocket().makeRaleyPacket(sendPacket);
//		new SendSocket().makeRaleyPacket(sendPacket);
		
Log.d(tag, "start send "+sendPacket.getTypeNum());
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
Log.d("nodeExREQ","recv Node Size:"+newNodeList.size());
			updateNodeList();

			//NodeListの初期化
			newNodeList.clear();
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
	
		for(NodeExData nd : newNodeList){
			if(nd.ttlDecrement() == false){
				newNodeList.remove(nd);
Log.d("NodeRemove", "remove");
			}
		}
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

	private int ttl = 2;
	

	NodeExData(String MACAddr, String Name, double ido, double keido,Bitmap Icon, String profile) {

		super(MACAddr,Name,ido,keido,Icon,profile);
	}
	protected boolean ttlDecrement() {
		ttl--;
		if(ttl <= 0){
			return false;
		}
		return true;
		
	}
}