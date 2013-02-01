package com.example.header;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sample.Yossip;
import com.example.yottaconnecter.R;

public class HeaderMessageAdapter extends ArrayAdapter<Yossip> {
	private LayoutInflater layoutInflater;

	public HeaderMessageAdapter(Context context, int textViewResourceId,
			List<Yossip> objects) {
		super(context, textViewResourceId, objects);
		layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Yossip item = (Yossip) getItem(position);

		if (null == convertView) {
			convertView = layoutInflater.inflate(R.layout.header_message_row,
					null);
		}
		convertView = layoutInflater.inflate(R.layout.header_message_row, null);

		ImageView icon = (ImageView) convertView
				.findViewById(R.id.header_message_icon);
		icon.setImageBitmap(item.getYossipIcon());

		TextView name = (TextView) convertView
				.findViewById(R.id.header_message_name);
		name.setText(item.getYossipUser());

		TextView time = (TextView) convertView
				.findViewById(R.id.header_message_time);
		time.setText(item.yossipTimeFarmat());

		TextView yossip = (TextView) convertView
				.findViewById(R.id.header_message_yossip);
		yossip.setText(item.getYossipText());

		return convertView;
	}

}
