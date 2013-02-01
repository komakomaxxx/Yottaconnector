package Image;

import com.example.client_test2.Client_test2;
import com.example.client_test2.Packet;
import com.example.client_test2.SendSocket;

public class ImageSessionACK {

	public static void cntrol(Packet packet){
		ImageSession session;
		
		session = ImageSessionList.getSessionRe(packet);
		
		//セッションが存在しないか、ステータスがSYNでない場合処理を行わない
		if(session == null || session.getStatus() != ImageSession.STS_SYN){
			return;
		}
		
		sessionReplace(session,packet);
		ImageSessionList.resetTimeOut(session);
		
		if(packet.getOriginalDestinationMac().compareTo(Client_test2.myNodeData.getMACAddr()) != 0){
			packetRelay(session,packet);
		}else{
			ImageData.SynAckPacketSend(packet);
		}
	}
	
	public static void sendACK(Packet packet){
		ImageSession session;
		
		session = ImageSessionList.getSession(packet);
		
		if(session == null){
			return;
		}
		
			
		//画像要求MACアドレスをセッションに設定
		session.setFindMac(packet.getOriginalDestinationMac());
		
		//画像提供MACアドレスをオリジナルディスティネーションに;
		session.setOriginalDestinationMac(Client_test2.myNodeData.getMACAddr());
		
		//セッションステータスをACKに変更
		session.setStatus(ImageSession.STS_ACK);
		
		//ACKパケット作成
		packet.setType(Packet.ImageACK);
		packet.setOriginalDestinationMac(Client_test2.myNodeData.getMACAddr());
		packet.setDestinationMac(packet.getOriginalDestinationMac());
		packet.exOriginalMac();
		packet.exMac();
		
		//パケット送信
		SendSocket send = new SendSocket(Client_test2.ip);
		send.makeNewPacket(packet);
	}
	
	public static void packetRelay(ImageSession session,Packet packet){
		//パケットヘッダ付け替え
		packet.exMac();
		packet.setDestinationMac(session.getSourceMac());
		
		//パケット送信
		SendSocket send = new SendSocket(Client_test2.ip);
		send.makeRaleyPacket(packet);
	}

	private static void sessionReplace(ImageSession session,Packet packet){
		
		//画像要求MACアドレスを設定
		session.setFindMac(session.getOriginalDestinationMac());
		
		//要求先MACアドレスを設定
		session.setOriginalDestinationMac(packet.getOriginalSourceMac());
		
		//セッションステータスをACKにに変更
		session.setStatus(ImageSession.STS_ACK);
		
	}
}
