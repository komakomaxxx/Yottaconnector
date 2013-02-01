package Message;

import java.util.ArrayList;

import com.example.client_test2.Client_test2;
import com.example.client_test2.Packet;

public class MessageSessionList {

	static private ArrayList<MessageSession> SessionList;
	
	static{
		SessionList = new ArrayList<MessageSession>();
	}
	
	static public void addSession(Packet packet){
		MessageSession result = new MessageSession(packet);
		SessionList.add(result);
		result.timerStart();
	}
	
	static public synchronized MessageSession getSession(Packet packet){
		int index;
		
		MessageSession findSession = new MessageSession(packet);
		index = SessionList.lastIndexOf(findSession);
		
		if(index < 0)
			return null;
		
		return SessionList.get(index);
	}
	
		
	static public synchronized MessageSession getSessionACK(Packet packet){
		int index;
		
		packet.exOriginalMac();
		MessageSession findSession = new MessageSession(packet);
		packet.exOriginalMac();
		index = SessionList.lastIndexOf(findSession);
		
		if(index < 0)
			return null;
		
		return SessionList.get(index);
	}
	
	static public synchronized void removeSession(MessageSession messageSession){
		messageSession.timerClear();
		SessionList.remove(messageSession);
		if(messageSession.getOriginalSourceMac().compareTo(Client_test2.myNodeData.getMACAddr()) == 0){
			//メッセージマネージャから該当のメッセージの状態を送信失敗に変更する
			MessageManager.getWaitMessage().setState(MessageManager.Message.FAILED);
		}
	}
}