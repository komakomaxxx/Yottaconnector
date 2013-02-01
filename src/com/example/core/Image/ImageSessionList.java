package Image;

import java.util.ArrayList;

import com.example.client_test2.Client_test2;
import com.example.client_test2.Packet;

public class ImageSessionList {
	
	static private ArrayList<ImageSession> SessionList;
	
	static{
		SessionList = new ArrayList<ImageSession>();
	}
	
	static public void addSession(Packet packet){
		ImageSession result = new ImageSession(packet);
		SessionList.add(result);
		result.timerStart();
	}
	
	static public synchronized ImageSession getSession(Packet packet){
		int index;
		
		ImageSession findSession = new ImageSession(packet);
		index = SessionList.lastIndexOf(findSession);
		
		if(index < 0)
			return null;
		
		return SessionList.get(index);
	}
	
	static public synchronized ImageSession getSessionRe(Packet packet){
		int index;
		
		packet.exOriginalMac();
		ImageSession findSession = new ImageSession(packet);
		packet.exOriginalMac();
		index = SessionList.lastIndexOf(findSession);
		
		if(index < 0)
			return null;
		
		return SessionList.get(index);
	}

	static public synchronized void resetTimeOut(ImageSession session){
		
		if(session == null){
			return;
		}
		
		session.timerReStart();
	}	
	
	static public synchronized void removeSession(ImageSession imageSession){
		imageSession.timerClear();
		SessionList.remove(imageSession);
		if(imageSession.getOriginalSourceMac().compareTo(Client_test2.myNodeData.getMACAddr()) == 0){
			//ImageSYNクラスを実行する
		}
	}
}
