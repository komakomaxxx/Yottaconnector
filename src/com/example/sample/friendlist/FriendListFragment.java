package com.example.sample.friendlist;

import com.example.yottaconnecter.R;
import com.example.yottaconnecter.YottaConnector;
import com.example.sample.header.HeaderFragment;
import com.example.sample.user.UserFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * 
 * @author Kazuki Hasegawa
 * @version 5
 * @since 2012/11/21
 */
public final class FriendListFragment extends Fragment implements OnItemClickListener {
	/**
	 * Constracter
	 * 
	 * @param savedInstanceState
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	/**
	 * ビューを生成し返す
	 * リストビューにアダプタを設定する
	 * 
	 * @param  friendListListView レイアウト内にあるリストビュー
	 * @param  friendListAdapter リストビューに設定するアダプタ
	 * 
	 * @return friendListView
	 */
	@Override
	public View onCreateView(
		LayoutInflater inflater, 
		ViewGroup container, 
		Bundle savedInstanceState) {
		
		View friendView = inflater.inflate(R.layout.friendlist_fragment, null);
		
		ListView friendListView = (ListView)friendView.findViewById(R.id._FriendListListView);
		friendListView.setOnItemClickListener(this);
		friendListView.setAdapter(FriendListManager.getAdapter(getActivity()));
		
		return friendView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		int state = YottaConnector.mPager.getCurrentItem() % 4;
		switch (state) {
		case 0:
			HeaderFragment.setFragmentName("Rader");
			break;
		case 1:
			HeaderFragment.setFragmentName("TimeLine");
			break;
		case 2:
			HeaderFragment.setFragmentName("FriendList");
			break;
		case 3:
			HeaderFragment.setFragmentName("NodeList");
			break;
		}
	}
	
	/**
	 * リストビューのアイテムがタップされた時の処理
	 * タップされたユーザのユーザフラグメントへ遷移する
	 * 
	 * @param parent リストビュー(parent)
	 * @param view
	 * @param position タップされたリストの場所
	 * @param id
	 * @param listview parentを変更
	 * @param friNode タップされたリストに保持されているFriendNode
	 * @param ft FragmentのFragmentTransaction
	 * @param f ユーザフラグメント
	 */
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		UserFragment.Builder bl = new UserFragment.Builder();
		bl.setNode(FriendListManager.getNode(position));
		UserFragment df = bl.create();
    	df.show(getFragmentManager(), getTag());
	}
}