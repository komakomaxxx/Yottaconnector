package Hello;

import java.util.ArrayList;

import android.util.Log;

import com.example.client_test2.Client_test2;
import com.example.client_test2.Node;
import com.example.client_test2.Packet;
import com.example.client_test2.SendSocket;

public class HelloAck {
	private final static String tag = "HelloAck";
	
	public static void recv(Packet recvPacket) {
		
		if(Client_test2.myNodeData.getMACAddr().equals(recvPacket.getOriginalDestinationMac()) ){
			int sessionNum = recvPacket.getTypeNum();
			
			
			ArrayList<String> dataList = recvPacket.putData();
			
			String macAddr = recvPacket.getOriginalSourceMac();
			String name = dataList.get(0);
			Double ido = Double.valueOf(dataList.get(1));
			Double keido = Double.valueOf(dataList.get(2));
			String profile = dataList.get(3);
			
			Node n = new Node(macAddr,name,ido,keido,null,profile);
			//node に追加
			Hello.addNode(n);
			
			Log.d(tag,"tNum = "+sessionNum + "[" +macAddr+":"+name+":"+ido+":"+keido+":"+profile  );
			
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
		dataList.add(Client_test2.myNodeData.getName());
		dataList.add(String.valueOf(Client_test2.myNodeData.getIdo()));
		dataList.add(String.valueOf(Client_test2.myNodeData.getKeido()));
		dataList.add(Client_test2.myNodeData.getProfile());
		
		int sessionNum = recvPacket.getTypeNum();
		int hopLimit =0;
		String srcMac = Client_test2.myNodeData.getMACAddr();
		String dstMac = Packet.broadCastMACaddr;
		
		//paketと生成
		Packet sendPack = new Packet(Packet.HelloAck,srcMac,dstMac,srcMac,	dstMac,hopLimit,sessionNum);
		//data部設定
		sendPack.createData(dataList);
		
		new SendSocket().makeNewPacket(sendPack);
	}
}
