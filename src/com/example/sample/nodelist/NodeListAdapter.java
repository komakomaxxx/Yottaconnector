package com.example.sample.nodelist;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sample.Node;
import com.example.sample.NodeList;
import com.example.sample.R;

public class NodeListAdapter extends ArrayAdapter<Node> {

	private LayoutInflater layoutInflater;

	public NodeListAdapter(Context context, int textViewResourceId, List<Node> objects) {
		super(context, textViewResourceId, objects);
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Node item = (Node) getItem(position);

		if (null == convertView) {
			convertView = layoutInflater.inflate(R.layout.nodelist, null);
		}

		ImageView icon = (ImageView) convertView.findViewById(R.id.nodelist_user_icon);
		icon.setImageBitmap(item.getIcon());

		TextView name = (TextView) convertView.findViewById(R.id.nodelist_user_id);
		name.setText(item.getName());

		TextView prof = (TextView) convertView.findViewById(R.id.nodelist_user_profile);
		prof.setText(item.getProfile());

		return convertView;
	}

	public void addNodeItem(Node node) {
		int i = 0;
		for (Node n : NodeList.nodelist) {
			if (n.getMACAddr().equals(node.getMACAddr()))
				break;
			i++;
		}
		if (i == NodeList.nodelist.size())
			NodeList.nodelist.add(node);
	}
}
