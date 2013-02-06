package com.example.sample.message;

import android.annotation.SuppressLint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * メッセージ関連を管理するクラス
 * 
 * @author Kazuki Hasegawa
 * @version 5
 * @since 1/21/2013
 */
public class MessageManager {
	/**
	 * このクラスで管理するメッセージリストのマップ
	 */
	private static Map<String, List<Message>> mesMap;
	/**
	 * 待ち状態のメッセージを保持するクラス変数
	 */
	private static Message waitMessage;

	/**
	 * static初期化ブロック
	 */
	static {
		mesMap = new HashMap<String, List<Message>>();
		waitMessage = null;
	}
	
	/**
	 * 渡されたマックアドレスのリストを返す
	 * あるマックアドレスに対するメッセージリストを取得したい場合は
	 * このメソッドを呼んでください
	 * 
	 * @param mac 宛先マックアドレス
	 * @return メッセージのリスト
	 */
	public static List<Message> getList(String mac) {
		synchronized(mesMap) {
			if(!mesMap.containsKey(mac)) {
				add(mac);
			}
			return mesMap.get(mac);
		}
	}
	
	/**
	 * 渡されたマックアドレスに対応するメッセージリストから
	 * 自分の送信したメッセージがいくつあるのかを取得する
	 * 
	 * @param mac 宛先マックアドレス
	 * @return 自分のメッセージ数
	 */
	public static int getCount(String mac) {
		int count = 0;
		for(Iterator<Message> it = MessageManager.getList(mac).iterator(); it.hasNext(); ) if(it.next().mine) count++;
		return count;
	}
	
	/**
	 * メッセージ追加メソッド
	 * 宛先マックアドレスをキーとしたListオブジェクトを
	 * 取得しメッセージを追加する
	 * もし、Mapの中に指定したキーのオブジェクトがマッピングされていない場合は
	 * 新規のリストを作成し、リストにメッセージをaddし
	 * それをMapにputする
	 * 通常時は待ちMapにputされる
	 * 
	 * @param mac 宛先マックアドレス
	 * @param message メッセージ
	 * @param mine 自ノードのメッセージであるかどうかのboolean
	 */
	public static Message add(String mac, String message, String date, boolean mine) {
		Message result;
		
		synchronized(mesMap) {
			if(!mesMap.containsKey(mac)) {
				add(mac);
			}
			if(mine) {
					waitMessage = new Message(mac, mesMap.get(mac).size(), message, date, mine, Message.WAIT);
					result = waitMessage;
			} else {
				List<Message> list = mesMap.get(mac);
				result = new Message(mac, mesMap.get(mac).size(), message, date, mine, Message.SUCCESS);
				list.add(result);
			}
			
			return result;
		}
	}
	
	/**
	 * メッセージ削除メソッド
	 * 宛先マックアドレスをキーとしたListオブジェクトを取得し
	 * メッセージを削除する
	 * もし、Mapの中に渡されたキーのオブジェクトがマッピングされていない場合は
	 * 何もしない
	 * 
	 * @param mac 宛先マックアドレス
	 * @param message 削除するメッセージ
	 */
	public static void remove(String mac, Message message) {
		synchronized(mesMap) {
			if(mesMap.containsKey(mac)) {
				mesMap.get(mac).remove(message);
			}
		}
	}
	
	/**
	 * メッセージリスト追加メソッド
	 * 渡されたマックアドレスをキーとするListをマッピングする
	 * 
	 * @param mac 宛先マックアドレス
	 */
	private static void add(String mac) {
		synchronized(mesMap) {
			if(!mesMap.containsKey(mac)) {
				List<Message> list = new ArrayList<Message>();
				mesMap.put(mac, list);
			}
		}
	}
	
	/**
	 * メッセージリスト削除
	 * 渡された宛先マックアドレスをキーとするListオブジェクトがマッピングされている場合、
	 * 対応するマップを削除する
	 * 
	 * @param mac 宛先マックアドレス
	 */
	public static void remove(String mac) {
		synchronized(mesMap) {
			if(mesMap.containsKey(mac)) {
				mesMap.remove(mac);
			}
		}
	}
	
	/**
	 * 現在の待ち状態のメッセージの状態によって
	 * 待ち状態のメッセージを処理する
	 * 送信が失敗した場合は、トーストを表示しその旨を伝える
	 */
	public static int onArrangeWaitMessage() {
		if(waitMessage != null) {
			int state = waitMessage.mStatus;
			if(state != Message.WAIT) {
				if(state == Message.SUCCESS) {
					mesMap.get(waitMessage.getMACAddr()).add(waitMessage);
				} else {
					return Message.FAILED;
				}
				waitMessage = null;
				return state;
			}
			return Message.WAIT;
		}
		return Message.WAIT;
	}
	
	/**
	 * Statusが待ち状態のメッセージを取得する
	 * 存在しない場合はnullを返す
	 * waitMessageの状態を変更するには
	 * これを呼び出し返ってきたメッセージにsetState(int)を呼び出してください
	 * 
	 * @return　待ち状態のメッセージ
	 */
	public static Message getWaitMessage() {
		return MessageManager.waitMessage;
	}
	
	/**
	 * 現在待ち状態のメッセージがあるかどうかをチェックする
	 * 待ち状態メッセージがあるならばtrueを
	 * なければfalseを返す
	 * 
	 * @return 待ち状態であるかを返す
	 */
	public static boolean isWaiting() {
		return waitMessage != null ? true : false;
	}

	/**
	 * 
	 * @author Kazuki Hasegawa
	 * @version 1
	 */
	@SuppressLint("SimpleDateFormat")
	public static class Message {
		public static final int WAIT = 0;
		public static final int SUCCESS = 1;
		public static final int FAILED = 2;
//		private final SimpleDateFormat timeFormat = new SimpleDateFormat("kk'時'mm'分'ss'秒'");
		private String mac;
		private int mesId;
		private String message;
		private String date;
		private boolean mine;
		private int mStatus;
		
		/**
		 * コンストラクタ
		 * 
		 * @param mac マックアドレス
		 * @param mesId メッセージID
		 * @param message メッセージの内容
		 * @param date 取得された日付
		 * @param mine 自ノードのものであるかのboolean
		 * @param state 現在の状態
		 */
		public Message(String mac, int mesId, String message, String date, boolean mine, int state) {
			this.mac = mac;
			this.mesId = mesId;
			this.message = message;
			this.date = date;
			this.mine = mine;
			this.mStatus = state;
		}
		
		/**
		 * メッセージの送り主のマックアドレスを返す
		 * 
		 * @return マックアドレス
		 */
		public String getMACAddr() {
			return this.mac;
		}
		
		/**
		 * メッセージIDを返す
		 * 
		 * @return メッセージID
		 */
		public int getMessageId() {
			return this.mesId;
		}
		
		/**
		 * メッセージ内容を返す
		 * 
		 * @return
		 */
		public String getMessageText() {
			return this.message;
		}
		
		/**
		 * 日付を指定されたフォーマット文字列に変更し返す
		 * 
		 * @return 日付の文字列
		 */
		public String getDate() {
			return date;
		}
		
		/**
		 * メッセージが自分のものであるかどうかを判断する
		 * 
		 * @return 自ノードのものであればtrue,他ノードのものであればfalse
		 */
		public boolean isMine() {
			return mine;
		}
		
		/**
		 * 状態をセットする
		 * 
		 * @param state
		 */
		public void setState(int state) {
			this.mStatus = state;
		}
		
		/**
		 * 状態を返す
		 * 
		 * @return 現在の状態
		 */
		public int getState() {
			return this.mStatus;
		}
	}
}