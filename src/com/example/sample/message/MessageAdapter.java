package com.example.sample.message;

import java.util.ArrayList;

import java.util.List;

import com.example.yottaconnecter.Node;
import com.example.yottaconnecter.R;
import com.example.sample.message.MessageManager.Message;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * メッセージ画面で使用するリストビューのアダプタ
 * 
 * @author Kazuki Hasegawa
 * @version 4
 * @since 1/22/2013
 */
public class MessageAdapter extends BaseAdapter {
	/**
	 * メッセージのリスト
	 */
	private static List<Message> mList;
	/**
	 * ノード
	 */
	private Node node;
	
	/**
	 * static初期化ブロック
	 */
	static {
		mList = new ArrayList<Message>();
	}
	
	/**
	 * 現在のリストをセットする
	 * 
	 * @param mac 宛先マックアドレス
	 */
	public void setList(Node node) {
		MessageAdapter.mList = MessageManager.getList(node.getMACAddr());
		this.node = node;
	}
	
	
	/**
	 * 現在のリストの個数を返す
	 * 
	 * @return リストのサイズ
	 */
	public int getCount() {
		return mList.size();
	}

	/**
	 * 対応するロケーションのアイテムを返す
	 * 
	 * @param locaiton 対応するロケーション
	 * 
	 * @return 対応するロケーションのObject
	 */
	public Object getItem(int location) {
		return mList.get(location);
	}

	/**
	 * Idを返す
	 * 
	 * @param id ID
	 * 
	 * @return ID
	 */
	public long getItemId(int id) {
		return id;
	}
	
	/**
	 * add
	 * 
	 * @param message
	 */
	public void add(Message message) {
		mList.add(message);
	}
	
	/**
	 * getView
	 * 
	 * @param locaiton 場所
	 * @param convertView ビュー
	 * @param parent 親ビュー
	 * 
	 * @return ビュー
	 */
	public View getView(int location, View convertView, ViewGroup parent) {
		ViewHolder holder;
		View v = convertView;
		Message currentMessage = (Message) getItem(location);
		if(currentMessage.isMine()) {
			if(v == null || v.getId() != R.layout.message_mine_row) {
				LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.message_mine_row, null);
				holder = new ViewHolder();
				holder.viewMesText = (TextView) v.findViewById(R.id._mMessageText);
				holder.viewTime = (TextView) v.findViewById(R.id._mMessageTime);
				v.setTag(holder);
			} else {
				holder = (ViewHolder) v.getTag();
			}
		} else {
			if(v == null || v.getId() != R.layout.message_other_row) {
				LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.message_other_row, null);
				holder = new ViewHolder();
				holder.viewMesText = (TextView) v.findViewById(R.id._oMessageText);
				holder.viewTime = (TextView) v.findViewById(R.id._oMessageTime);
				holder.viewIcon = (ImageView) v.findViewById(R.id._oMessageIcon);
				holder.viewUserId = (TextView) v.findViewById(R.id._oMessageName);
		 		v.setTag(holder);
			} else {
				holder = (ViewHolder) v.getTag();
			}
			IconTask task = new IconTask(holder.viewIcon);
			task.execute(node.getRadarIcon());
			holder.viewUserId.setText(node.getName());
		}
		holder.viewMesText.setText(currentMessage.getMessageText());
		holder.viewTime.setText(currentMessage.getDate());
		return v;
	}
	
	/**
	 * ViewHolder
	 * 
	 * @author Kazuki Hasegawa
	 * @version 2
	 * @since 1/23/2013
	 */
	class ViewHolder {
		/**
		 * タイムスタンプテキストビュー
		 */
		public TextView viewTime;
		/**
		 * ユーザIDテキストビュー
		 */
		public TextView viewUserId;
		/**
		 * メッセージテキストビュー
		 */
		public TextView viewMesText;
		/**
		 * ユーザアイコンイメージビュー
		 */
		public ImageView viewIcon;
	}
	
	/**
	 * 
	 * @author Kazuki Hasegawa
	 * @version 1
	 * @since 2/1/2013
	 */
	private class IconTask extends AsyncTask<Bitmap, Void, Bitmap> {
		private ImageView mView;
		public IconTask(ImageView view) {
			this.mView = view;
		}
		@Override
	    protected void onPostExecute(Bitmap result) {
	        mView.setImageBitmap(result);
	    }
		@Override
		protected Bitmap doInBackground(Bitmap... params) {
			return params[0];
		}
	}
}