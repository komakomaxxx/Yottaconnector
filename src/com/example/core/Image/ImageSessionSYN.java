package Image;

import com.example.client_test2.Client_test2;
import com.example.client_test2.Node;
import com.example.client_test2.NodeList;
import com.example.client_test2.Packet;
import com.example.client_test2.SendSocket;

public class ImageSessionSYN {
	
	private static int SessionID = 0;

	//パケット受信時動作振り分けルーチン
	public static void cntrol(Packet packet){
		
		//セッションがすでに保存されていれば
		if(ImageSessionList.getSession(packet) != null){
			return;
		}
		
		ImageSessionList.addSession(packet);
		
		//OriginalDestinationMacが自分宛のパケットでない場合
		if(packet.getOriginalDestinationMac().compareTo(Client_test2.myNodeData.getMACAddr()) != 0){
			
			//要求ノードの画像を自分が保持していれば
			Node node = NodeList.getNode(packet.getOriginalDestinationMac());
			if(node != null && node.getIcon() != null){
				//ACKを送信
				ImageSessionACK.sendACK(packet);
			}else{
				//転送処理
				packetRelay(packet);
			}
			
		}else{
		//OriginalDestinationMacが自分宛のパケットの場合
			//ACKを送信
			ImageSessionACK.sendACK(packet);
		}
		
	}
	
	public static void sendImageSYN(){
		Node node;
		
		int type = Packet.ImageSYN;
		String oSrcMac = Client_test2.myNodeData.getMACAddr();
		String oDestMac = null;
		String srcMac = oSrcMac;
		String destMac = Packet.broadCastMACaddr;
		int hopLimit = Packet.HopLimitMax;
		int typeNum = getSessionID();	
		
		//ノードリストからイメージのないノードを取得
		//取得できるまでルーブ
		while(true){
			 node = NodeList.getNoImageNode();
		
			if(node != null){
				break;
			}
			
			try {
				Thread.sleep(1000*60*2);
			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
		
		//ノードのMACアドレス取得
		oDestMac = node.getMACAddr();
		
		//パケットヘッダ作成
		Packet sendPacket = new Packet(type,oSrcMac,oDestMac,srcMac,destMac,hopLimit,typeNum);
		
		//セッション作成
		ImageSessionList.addSession(sendPacket);
		
		//パケット送信
		SendSocket send = new SendSocket(Client_test2.ip);
		send.makeNewPacket(sendPacket);
	}
	
	//パケット中継ルーチン
	private static void packetRelay(Packet packet){
		packet.setSourceMac(Client_test2.myNodeData.getMACAddr());
		
		//パケット送信
		SendSocket send = new SendSocket(Client_test2.ip);
		send.makeRaleyPacket(packet);
	}
	
	private static synchronized int getSessionID(){
		return SessionID++;
	}
	
}
