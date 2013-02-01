package com.example.sample.timeline;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.example.sample.R;
import com.example.sample.Yossip;
import com.example.sample.YossipList;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * TimelineAdapterクラス
 *
 * @author	Kazuki Hasegawa
 * @version	6
 * @since 10/21/2012
 */
public class TimelineAdapter extends ArrayAdapter<Yossip> {
	/**
	 * タイムラインリスト
	 */
	private static List<Yossip> tList = new ArrayList<Yossip>();
	/**
	 * ハンドラー
	 */
	private Handler handler;
	
	/**
	 * コンストラクタ
	 * 
	 * @param context
	 * @param textViewResourceId
	 */
	public TimelineAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId, tList);
		handler = new Handler();
	}
	
	/**
	 * 新規のヨシップが追加された場合の処理
	 * 
	 * @param yossip 新規のヨシップ
	 */
	public synchronized void add(Yossip yossip) {
		if(sameYossip(yossip)) {
			tList.add(0, yossip);
			handler.post(new Runnable() {
				public void run() {
					notifyDataSetChanged();
					return;
				}
			});
		}
	}

	/**
	 * 引数であるconvertViewがnullであればtimeline_list_rowを設定し
	 * その後、フレームがタップされた場合ユーザ画面へ遷移するためのListnerをセットする
	 * 現在のViewの場所(position)に対応するObjectを取得し
	 * その後、レイアウトに必要なViewを設定していく
	 *
	 * @param  position 現在のViewの場所を示す
	 * @param  convertView
	 * @param  parent
	 * @param  v convertView
	 *
	 * @return v
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		View v = convertView;
		
		if(v == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		   	v = inflater.inflate(R.layout.timeline_list_row, null);
		   	holder = new ViewHolder();
		   	holder.timelineIcon		  = (ImageView) v.findViewById(R.id._TimelineIcon);
	    	holder.timelineName 	  = (TextView) v.findViewById(R.id._TimelineName);
	    	holder.timelineYossipTime = (TextView) v.findViewById(R.id._TimelineYossipTime);
	    	holder.timelineYossip     = (TextView) v.findViewById(R.id._TimelineYossip);
	    	v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
			holder.timelineYossip.setSingleLine(false);
		}
		
	    Yossip currentYossip = (Yossip) getItem(position);
	    holder.timelineIcon.setImageBitmap(currentYossip.getYossipIcon());
	    holder.timelineName.setText(currentYossip.getYossipUser());
	    holder.timelineYossipTime.setText(currentYossip.yossipTimeFarmat());
	    holder.timelineYossip.setText(currentYossip.getYossipText());
    	
	    return v;
	}
	
	/**
	 * リストをセットする
	 */
	public void setList() {
		Iterator<Yossip> i = tList.iterator();
		while(i.hasNext()) {
			YossipList.y_list.add(i.next());
		}
	}
	
	/**
	 * 追加されるヨシップが前回のヨシップでないか確認
	 * 
	 * @param yossip ヨシップ
	 */
	public boolean sameYossip(Yossip yossip) {
		Iterator<Yossip> i = tList.iterator();
		while(i.hasNext()) {
			Yossip cYossip = i.next();
			if(cYossip.getYossipUser().equals(yossip.getYossipUser())
					&& cYossip.getYossipText().equals(yossip.getYossipText())
						&& cYossip.getYossipTime().equals(yossip.getYossipTime())) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * ViewHolder Class
	 * 
	 * @author Kazuki Hasegawa
	 * @version 2
	 * @since 1/8/2013
	 */
	private static class ViewHolder {
		/**
		 * アイコンイメージビュー
		 */
		public ImageView timelineIcon;
		/**
		 * ユーザIDテキストビュー
		 */
		public TextView timelineName;
		/**
		 * タイムスタンプビュー
		 */
		public TextView timelineYossipTime;
		/**
		 * ヨシップテキストビュー
		 */
		public TextView timelineYossip;
	}
}