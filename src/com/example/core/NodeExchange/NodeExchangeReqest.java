package NodeExchange;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.R.bool;

import com.example.client_test2.Client_test2;
import com.example.client_test2.Node;
import com.example.client_test2.NodeList;
import com.example.client_test2.Packet;
import com.example.client_test2.SendSocket;

public class NodeExchangeReqest {

	static boolean sendFlag;
	
	static int typeSession =0;
	static ArrayList<NodeExchangeSessionData> sessionList = new ArrayList<NodeExchangeSessionData>();	
	static Timer sendTimer;
	protected static ArrayList<Node> newNodeList = new ArrayList<Node>();
	
	//受信
	public static void  recv(Packet recvPacket) {
		int hopLimit;
		
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
		recvPacket.setSourceMac(Client_test2.myNodeData.getMACAddr());
		
		//送信
		new SendSocket().makeRaleyPacket(recvPacket);
	}
	//起点として送信
	public static void  sendReqest() {
		
		//自分発のセッションがないことを確認
		if(searchSession(Client_test2.myNodeData.getMACAddr(),0) != null){
			return;
		}
		//NodeListの初期化
		newNodeList.clear();
		
		String srcMac = Client_test2.myNodeData.getMACAddr();
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
		if(Client_test2.myNodeData.getMACAddr().equals(sd.getSrcMac())){
			NodeList.updateNodeList(newNodeList);
		}
		sessionList.remove(sd);	
	}
	
	//定期送信処理
	public static void startSendTimer(int time) {
		if (sendTimer == null){
			
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
	final static int timeOutTime = 1000;
	
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
