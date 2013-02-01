package com.example.sample.header;

import java.util.ArrayList;
import java.util.List;

import com.example.sample.message.MessageManager;

public class ReceiveMessageManager {
	private static List<MessageManager.Message> receiveMessageList = new ArrayList<MessageManager.Message>();

	/*
	 * @param yossip:新規メッセージヨシップ
	 */
	public static void addReceiveMessage(MessageManager.Message m) {
		synchronized (receiveMessageList) {
			receiveMessageList.add(m);
		}
	}

	
	/*
	 * @param yossip:受信リストから削除するヨシップ
	 */
	public static void removeReceiveMessage(MessageManager.Message m) {
		synchronized (receiveMessageList) {
			receiveMessageList.remove(m);
		}
	}

	public static void clearReceiveMessage() {
		synchronized (receiveMessageList) {
			receiveMessageList.clear();
		}
	}

	public static List<MessageManager.Message> getReceiveList() {
		return receiveMessageList;
	}
	
	public static int size() {
		// TODO 自動生成されたメソッド・スタブ
		return receiveMessageList.size();
	}

}
