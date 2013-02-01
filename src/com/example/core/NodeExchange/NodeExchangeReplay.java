package NodeExchange;

import java.util.ArrayList;

import android.util.Log;

import com.example.client_test2.Client_test2;
import com.example.client_test2.Node;
import com.example.client_test2.Packet;
import com.example.client_test2.SendSocket;

public class NodeExchangeReplay {
	private final static String tag = "NodeExchengeReplay";
	
	
	public static void recv(Packet recvPacket) {
		String oDstMac = recvPacket.getOriginalDestinationMac();
		int tNum = recvPacket.getTypeNum();
		
		Log.d(tag, oDstMac);
		NodeExchangeSessionData session = NodeExchangeReqest.searchSession(oDstMac,tNum);
				
		if(session != null){
			if( oDstMac.equals( Client_test2.myNodeData.getMACAddr())){
				NodeExchangeReqest.deleteSession(session);
				/*ノードリスト追加*/
				ArrayList<String> dataList = recvPacket.putData();
				
				String macAddr = recvPacket.getOriginalSourceMac();
				String name = dataList.get(0);
				Double ido = Double.valueOf(dataList.get(1));
				Double keido = Double.valueOf(dataList.get(2));
				String profile = dataList.get(3);
				
				Node n = new Node(macAddr,name,ido,keido,null,profile);
				//node に追加
				NodeExchangeReqest.newNodeList.add(n); 
				
			}else{
				relay(recvPacket,session);
			}	
		}
	}
	public static void relay(Packet sendPacket,NodeExchangeSessionData session) {
		//パケット作成
		
		sendPacket.setSourceMac(Client_test2.myNodeData.getMACAddr());
		sendPacket.setDestinationMac(session.getSrcMac());
		
		new SendSocket().makeRaleyPacket(sendPacket);
	}
	public static void  sendReplay(Packet recvPacket) {
		//パケット生成
		/*data 作成*/		
		ArrayList<String> dataList = new ArrayList<String>();
		dataList.add(Client_test2.myNodeData.getName());
		dataList.add(String.valueOf(Client_test2.myNodeData.getIdo()));
		dataList.add(String.valueOf(Client_test2.myNodeData.getKeido()));
		dataList.add(Client_test2.myNodeData.getProfile());
		
		int sessionNum = recvPacket.getTypeNum();
		int hopLimit =0;
		String srcMac = Client_test2.myNodeData.getMACAddr();
		String oDstMac = recvPacket.getOriginalSourceMac();
		String dstMac = recvPacket.getSourceMac();
		
		//paketと生成
		Packet sendPacket = new Packet(Packet.NodeExREP,srcMac,oDstMac,srcMac,dstMac,hopLimit,sessionNum);
		//data部設定
		sendPacket.createData(dataList);
		
		new SendSocket().makeNewPacket(sendPacket);	
	}
}