package com.example.sample.user;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.yottaconnecter.*;

public class YossipListAdapter extends ArrayAdapter<Yossip> {
	private LayoutInflater layoutInflater;

	 public YossipListAdapter(Context context, int textViewResourceId, List<Yossip> objects) {
		 super(context, textViewResourceId, objects);
		 layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	 }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Yossip item = (Yossip)getItem(position);

		if (null == convertView) {
			 convertView = layoutInflater.inflate(R.layout.yossiplist, null);
		}

		TextView time  = (TextView) convertView.findViewById(R.id.time);
		time.setText(item.yossipTimeFarmat());

		TextView yossip = (TextView) convertView.findViewById(R.id.yossip);
		yossip.setText(item.getYossipText());


		return convertView;
	}

}
