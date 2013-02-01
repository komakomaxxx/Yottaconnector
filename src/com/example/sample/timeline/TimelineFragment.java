package com.example.sample.timeline;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.example.yottaconnecter.*;
import com.example.sample.user.UserFragment;

/**
 * TimelineFragmentクラス
 * 
 * @author 	Kazuki Hasegawa
 * @version 8
 * @since	2012/11/15
 */
public final class TimelineFragment extends Fragment implements OnClickListener, OnItemClickListener, Yossiplistener {
	/**
	 * タイムラインで使用するアダプタ
	 */
	private static TimelineAdapter adapter;
	
	/**
	 * プロフィールがないときのプロフィール
	 */
	private static final String NON_PROFILE = "プロフィールを取得できませんでした。";
	
	/**
	 * OverrideしたonCreateView
	 * タイムライン画面のリスト
	 * リストビューにアダプタをセットし
	 * 画面下にあるボタンにタップされた場合のメソッド対応付けする
	 * 
	 * @param  timelineLayoutView	タイムライン画面のViewを取得する変数
	 * @param  tListView     タイムラインのリストビューを取得する変数
	 * @param  timelineAdapter      タイムラインのリストビューに適用するアダプタの変数
     * @param  timelineYossipButton タイムライン画面下にあるブタンクラス
	 * 
	 * @return timelineLayoutView
	 */
    @Override
    public View onCreateView(
    		LayoutInflater inflater,
    		ViewGroup container,
            Bundle savedInstanceState) {

    	setAdapter();
    	setListener();
    	View timelineLayoutView = setViews(inflater.inflate(R.layout.timeline_fragment, container, false));
    	
        return timelineLayoutView;
    }
    
    /**
     * ListViewに設定するアダプタを初期化する
     * nullでない場合は初期化を行わない
     */
    private void setAdapter() {
    	adapter = new TimelineAdapter(getActivity(), R.layout.timeline_list_row);
    	adapter.setList();
    }
    
    /**
     * ヨシップ関係のやつ
     */
    private void setListener() {
    	YossipListenerNotify yListener = new YossipListenerNotify(adapter.getCount());
		yListener.setListener(this);
    }
    
    /**
     * FragmentのViewをセットする
     * ListViewにはアダプタを設定し
     * Buttonにはリスナーをセットする
     * 
     * @param v 設定するView
     * @param tListView タイムラインに表示するリストビュー
     * @param timelineYossipButton ヨシップ画面を表示するためのボタン
     */
    private View setViews(View v) {
    	
    	/* ListView */
    	ListView tListView = (ListView) v.findViewById(R.id._TimelineListView);
    	tListView.setAdapter(adapter);
    	tListView.setOnItemClickListener(this);
    	
    	/* Button */
    	Button timelineYossipButton = (Button) v.findViewById(R.id._TimelineYossipButton);
    	timelineYossipButton.setOnClickListener(this);
    	
    	return v;
    }
    
    /**
     * タップされた時のリスナー
     * ヨシップボタンがタップされたならばsendYossipDialogShowを呼び出す
     * 
     * @param v 現在のビュー
     */
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id._TimelineYossipButton:
			new SendYossipDialog().show(getActivity().getSupportFragmentManager(), null);
			break;
		}
	}
    
    /**
     * リストビューのアイテムがタップされた時の処理
     * タップされたユーザのユーザフラグメントへ遷移
     * 
     * @param parent リストビュー(parent)
	 * @param view
	 * @param position タップされたリストの場所
	 * @param id
	 * @param listview parentを変更
	 * @param node タップされたリストに保持されているFriendNode
	 * @param uf ユーザフラグメント
     */
    public synchronized void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    	Node cNode = searchNode(adapter.getItem(position));
    	UserFragment.Builder bl = new UserFragment.Builder();
    	bl.setNode(cNode);
    	bl.create().show(getFragmentManager(), getTag());
	}
    
    /**
     * 指定されたヨシップのノードを探索する
     * ない場合はヨシップが保持している情報で最大限でNode情報を作成し返す
     * 
     * @param yossip
     * @return ノード
     */
    private Node searchNode(Yossip y) {
    	Node nNode = new Node(y.getYossipUserMac(), y.getYossipUser(), 1, 1, y.getYossipIcon(), NON_PROFILE);
    	Node node = NodeList.getNode(y.getYossipUserMac());
    	if(node != null)
    		nNode = node;
    	return nNode;
    }

    /**
     * 新規のヨシップを取得した際の長さが渡される
     * 
     * @param length 現在のヨシップの長さ
     */
	public void onYossipListChangeListener(int length) {
	}

	/**
	 * 新規のヨシップを取得した際に行うメソッド
	 * 取得した場合は新規にタイムラインのリストに対し
	 * 取得したヨシップを追加する
	 * 
	 * @param y 取得したヨシップ
	 */
	public synchronized void onNewYossipGetListener(Yossip yossip) {
		adapter.add(yossip);
	}
}