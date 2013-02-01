package com.example.sample.friendlist;

import java.util.List;
import com.example.sample.R;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.*;
import android.widget.*;

/**
 * フレンドリストのリストビューで使用するアダプタ
 *
 * @author	Kazuki Hasegawa
 * @version	3
 * @since 11/24/2012
 */
public final class FriendListAdapter extends ArrayAdapter<FriendNode> {
	/**
	 * コンストラクタ
	 * @param context
	 * @param objects
	 */
	public FriendListAdapter(Context context, int layoutId, List<FriendNode> objects) {
		super(context, layoutId, objects);
	}
	
	/**
	 * 引数であるconvertViewがnullであればfriend_list_rowのレイアウトを設定する。
	 * その後、フレームがタップされた場合ユーザ画面へ遷移するためのListenerを設定する。
	 * 現在のViewの場所(position)に対応するObjectを取得し
	 * その後、レイアウトに必要なViewを設定していくメソッドである。
	 *
	 * @param  position 現在のViewの場所を示す
	 * @param  convertView
	 * @param  parent
	 *
	 * @param  v convertView
	 * @param  currentFriendNode 現在位置のユーザリスト
	 * @param  friendOnlineState フレンドリストレイアウトにあるImageView
	 * @param  friendIcon フレンドリストレイアウトにあるImageView
	 * @param  friendName フレンドリストレイアウトにあるTextView
	 * @param  friendProfile フレンドリストレイアウトにあるTextView
	 *
	 * @return v
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		View v = convertView;
		
		if(v == null) {
			LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		   	v = inflater.inflate(R.layout.friend_list_row, null);
		   	holder = new ViewHolder();
		   	holder.friendOnlineState = (ImageView)v.findViewById(R.id._FriendOnlineState);
			holder.friendIcon = (ImageView)v.findViewById(R.id._FriendIcon);
			holder.friendName = (TextView)v.findViewById(R.id._FriendId);
			holder.friendProfile = (TextView)v.findViewById(R.id._FriendProfile);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
			holder.friendProfile.setSingleLine(false);
		}
		
	    FriendNode currentFriendNode = getItem(position);
	    if(currentFriendNode.isOnline()) {
		 	holder.friendOnlineState.setImageBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.online));
	    }
	    holder.friendIcon.setImageBitmap(currentFriendNode.getIcon());
	    holder.friendName.setText(currentFriendNode.getName());
	    holder.friendProfile.setText(currentFriendNode.getProfile());
	    
	    return v;
	}
	
	/**
	 * ViewHolderクラス
	 * 
	 * @author Kazuki Hasegawa
	 * @version 2
	 * @since 1/8/2013
	 */
	private class ViewHolder {
		public ImageView friendOnlineState;
		public ImageView friendIcon;
		public TextView friendName;
		public TextView friendProfile;
	}
}