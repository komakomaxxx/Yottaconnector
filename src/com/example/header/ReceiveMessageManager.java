package com.example.header;

import java.util.ArrayList;
import java.util.List;

import com.example.sample.Yossip;

public class ReceiveMessageManager {
	// 　現在開いているメッセージ画面のMAC
	private static String openedViewMAC;
	private static List<Yossip> receiveMessageList = new ArrayList<Yossip>();

	/*
	 * @param yossip:新規メッセージヨシップ
	 */
	public static void addReceiveMessage(Yossip yossip) {
		synchronized (receiveMessageList) {
			// 　どのメッセージ画面も開いていないとき
			/*
			 * if (openedViewMAC == null) { 
			 * 	search(yossip);
			 * 	receiveMessageList.add(yossip); 
			 * } 
			 * 	//　だれかのメッセージ画面を開いている場合 
			 * else if (!yossip.getYossipUserMac().equals(openedViewMAC)) {
			 * search(yossip); 
			 * receiveMessageList.add(yossip); 
			 * } 
			 * 送信者の画面を開いている場合 else{ }
			 */
			search(yossip);
			receiveMessageList.add(yossip);
		}
	}

	/*
	 * @param yossip:新規メッセージヨシップ
	 */
	private static void search(Yossip yossip) {
		synchronized (receiveMessageList) {
			for (Yossip item : receiveMessageList) {
				if (item.getYossipUserMac().equals(yossip.getYossipUserMac())) {
					receiveMessageList.remove(item);
					break;
				}
			}
		}
	}

	/*
	 * @param yossip:受信リストから削除するヨシップ
	 */
	public static void removeReceiveMessage(Yossip yossip) {
		synchronized (receiveMessageList) {
			receiveMessageList.remove(yossip);
		}
	}

	public static void clearReceiveMessage() {
		synchronized (receiveMessageList) {
			receiveMessageList.clear();
		}
	}

	public static List<Yossip> getReceiveList() {
		return receiveMessageList;
	}

	/*
	 * @param openViewNode:現在開いているメッセージ画面のMAC
	 */
	public static void notifyStateOpened(Yossip yossip) {
		// ReceiveMessageManager.openedViewMAC = openedViewMAC;
		//removeReceiveMessage(yossip);
	}
	/*
	 * メッセージ画面を閉じた際に通知
	 */
	// public static void notifyStateClosed() {
	// openedViewMAC = null;
	// }

	public static int size() {
		// TODO 自動生成されたメソッド・スタブ
		return receiveMessageList.size();
	}

}
