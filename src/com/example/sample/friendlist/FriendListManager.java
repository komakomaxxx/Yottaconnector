package com.example.sample.friendlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.os.Handler;

import com.example.yottaconnecter.Node;
import com.example.yottaconnecter.R;
import com.example.sample.database.YossipDatabaseOpenHelper;

/**
 *
 * @author Kazuki Hasegawa
 * @version 5
 * @since 1/14/2013
 */
public final class FriendListManager {
	/**
	 * フレンドリスト
	 */
	private static List<FriendNode> friendNodeList;

	/**
	 * ヨシップデータベースヘルパーのインスタンス
	 */
	private static YossipDatabaseOpenHelper helper;
	
	/**
	 * フレンドリストのアダプター
	 */
	private static FriendListAdapter adapter;
	
	/**
	 * 
	 */
	private static Handler handler = new Handler();
	

	/**
	 * ソート方法
	 */
	private final static Comparator<FriendNode> SORT_COMPAR = new Comparator<FriendNode>() {
		public int compare(FriendNode node1, FriendNode node2) {
			Boolean nodeState1 = node1.isOnline();
			Boolean nodeState2 = node2.isOnline();
			return nodeState2.toString().compareTo(nodeState1.toString());
		}
	};
	
	/**
	 * アダプターを返す
	 * ない場合は新規の作成し追加
	 * 
	 * @param context
	 */
	public static FriendListAdapter getAdapter(Context context) {
		if(adapter == null) {
			adapter = new FriendListAdapter(context, R.layout.friend_list_row, friendNodeList);
		}
		return adapter;
	}

	/**
	 * 読み込み
	 * 
	 * @param context
	 */
	public synchronized static void onLoadFriendList(Context context) {
		friendNodeList = new ArrayList<FriendNode>();
		helper = new YossipDatabaseOpenHelper(context);
		synchronized(friendNodeList) {
			helper.onDataImport(friendNodeList);
			sortFriendList();
		}
	}

	/**
	 * 書き込み
	 * データベースに書き込む
	 */
	public synchronized static void onFriendListIntoDatabase() {
		if(friendNodeList != null) {
			helper.onDataOutport(friendNodeList);
		}
	}

	/**
	 * ノードを追加する
	 * 新規にお気に入り追加する場合はこのメソッドを呼び出してください。
	 * 
	 * @param node
	 */
	public static void add(Node node) {
		synchronized(friendNodeList) {
			FriendNode nNode = new FriendNode(node);
			if(sameNode(nNode)) {
				friendNodeList.add(nNode);
				handler.post(new Runnable() {
					public void run() {
						adapter.notifyDataSetChanged();
					}
				});
				helper.onDataOutport(friendNodeList);
			}
		}
	}
	
	/**
	 * ノードを削除する
	 * お気に入りを外す場合はこのメソッドを呼び出してください
	 * 
	 * @param node 削除するノード
	 */
	public static void remove(Node node) {
		synchronized(friendNodeList) {
			friendNodeList.remove(node);
			handler.post(new Runnable() {
				public void run() {
					adapter.notifyDataSetChanged();
				}
			});
			helper.onDataOutport(friendNodeList);
		}
	}
	
	/**
	 * ノード探索メソッド
	 * 同じノードがなければtrueを返す
	 * 同じノードがあるならばfalse
	 * 
	 * @param node
	 */
	public static boolean sameNode(Node node) {
		Iterator<FriendNode> i = friendNodeList.iterator();
		while( i.hasNext() ) {
			FriendNode cNode = i.next();
			if(cNode.getMACAddr().equals(node.getMACAddr())) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 
	 */
	public static Node getNode(int location) {
		return friendNodeList.get(location);
	}

	/**
	 * ソートメソッド
	 */
	private static void sortFriendList() {
		synchronized(friendNodeList) {
			Collections.sort(friendNodeList, SORT_COMPAR);
		}
	}
}