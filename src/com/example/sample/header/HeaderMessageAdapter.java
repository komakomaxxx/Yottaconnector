package com.example.sample.header;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yottaconnecter.*;
import com.example.sample.message.MessageManager;

public class HeaderMessageAdapter extends ArrayAdapter<MessageManager.Message> {
	private LayoutInflater layoutInflater;

	public HeaderMessageAdapter(Context context, int textViewResourceId,
			List<MessageManager.Message> objects) {
		super(context, textViewResourceId, objects);
		layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MessageManager.Message item = (MessageManager.Message)getItem(position);

		if (null == convertView) {
			convertView = layoutInflater.inflate(R.layout.header_message_row,
					null);
		}
		convertView = layoutInflater.inflate(R.layout.header_message_row, null);
		
		Node n = NodeList.getNode(item.getMACAddr());

		if(n == null){
			
			TextView name = (TextView) convertView
					.findViewById(R.id.header_message_name);
			name.setText("unkown");
			TextView time = (TextView) convertView
					.findViewById(R.id.header_message_time);
			time.setText(item.getDate());

			TextView yossip = (TextView) convertView
					.findViewById(R.id.header_message_yossip);
			yossip.setText(item.getMessageText());
		}
		else{
			ImageView icon = (ImageView) convertView
					.findViewById(R.id.header_message_icon);
			icon.setImageBitmap(n.getIcon());

			TextView name = (TextView) convertView
					.findViewById(R.id.header_message_name);
			name.setText(n.getName());

			TextView time = (TextView) convertView
					.findViewById(R.id.header_message_time);
			time.setText(item.getDate());

			TextView yossip = (TextView) convertView
					.findViewById(R.id.header_message_yossip);
			yossip.setText(item.getMessageText());
			
		}
		
		




		return convertView;
	}

}
