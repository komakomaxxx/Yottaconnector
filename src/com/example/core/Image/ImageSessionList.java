package com.example.core.Image;

import java.util.ArrayList;
import com.example.yottaconnecter.*;
import com.example.core.Packet;

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
		String tag = "ImageSessionList";
		imageSession.timerClear();
		SessionList.remove(imageSession);
		if(imageSession.getOriginalSourceMac().compareTo(YottaConnector.MyNode.getMACAddr()) == 0){
			//ImageSYNクラスを実行する
			ImageData.ImageDataTimeOut();
		}
	}
}
