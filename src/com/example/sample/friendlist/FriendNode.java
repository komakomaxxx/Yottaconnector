package com.example.sample.friendlist;

import com.example.yottaconnecter.Node;

/**
 *
 * @author Kazuki Hasegawa
 * @version 1
 */
public class FriendNode extends Node {
	/**
	 * このノードのオンライン状態をboolean型で保存すうための変数
	 */
	private boolean onlineState;
	
	/**
	 * コンストラクタ
	 * スーパクラス及びこのクラスのメンバ変数を初期化を行う
	 * 
	 * @param node セットするノード
	 *
	 * @param MACAddr ユーザのマックアドレス
	 * @param Name ユーザの名前
	 * @param ido 緯度
	 * @param keido 経度
	 * @param Icon ユーザのアイコン
	 * @param profile ユーザのプロフィール
	 *
	 */
	public FriendNode(Node node) {
		super(node.getMACAddr(),
			  node.getName(),
			  node.getIdo(),
			  node.getKeido(),
			  node.getIcon(),
			  node.getProfile());
		onlineState = false;
	}
	
	/**
	 * 現在の状態を取得する
	 */
	public boolean isOnline() {
		return onlineState;
	}
	
	/**
	 * 自身のオンライン状態を変更する
	 */
	public void setOnlineState(boolean state) {
		this.onlineState = state;
	}
}