package com.example.core.Message;

import java.util.ArrayList;

import android.util.Log;

import com.example.yottaconnecter.*;
import com.example.core.Packet;
import com.example.sample.message.*;

public class MessageSessionList {

	static private ArrayList<MessageSession> SessionList;
	
	static{
		SessionList = new ArrayList<MessageSession>();
	}
	
	static public void addSession(Packet packet){
		MessageSession result = new MessageSession(packet);
		SessionList.add(result);
		result.timerStart();
		String tag = "addSession";
		Log.d(tag,result.getOriginalSourceMac());
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
		String tag = "removeSession";
		Log.d(tag,messageSession.getOriginalSourceMac());
		messageSession.timerClear();
		SessionList.remove(messageSession);
		if(messageSession.getOriginalSourceMac().compareTo(YottaConnector.MyNode.getMACAddr()) == 0){
			//メッセージマネージャから該当のメッセージの状態を送信失敗に変更する
			MessageManager.getWaitMessage().setState(MessageManager.Message.FAILED);
		}
	}
}