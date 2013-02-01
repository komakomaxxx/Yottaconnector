package com.example.sample.header;

import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.yottaconnecter.*;
import com.example.sample.user.UserFragment;

public class HeaderMessageFragment_ extends Fragment{

	HeaderMessageFragment_ my;
	ArrayAdapter<Yossip> adapter;
	ListView messageList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		my = this;

		return inflater.inflate(R.layout.header_message, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setMessageList();
		// messageList.setOnItemClickListener(new ClickEvent());
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// ReceiveMessageManager.notifyStateClosed();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		// ReceiveMessageManager.notifyStateClosed();

	}

	public void setMessageList() {
//		messageList = (ListView) getActivity().findViewById(
//				R.id.h_MessageList);
//		adapter = new HeaderMessageAdapter(getActivity(),
//				R.layout.header_message_row,
//				ReceiveMessageManager.getReceiveList());
//		ReceiveMessageManager.addReceiveMessage(new Yossip("a", new Date(),
//				"NODEA", "11", Bitmap.createBitmap(30, 30, Config.RGB_565)));
//		ReceiveMessageManager.addReceiveMessage(new Yossip("b", new Date(),
//				"NODEB", "22", Bitmap.createBitmap(30, 30, Config.RGB_565)));
//		ReceiveMessageManager.addReceiveMessage(new Yossip("c", new Date(),
//				"NODEC", "33", Bitmap.createBitmap(30, 30, Config.RGB_565)));
//		messageList.setAdapter(adapter);
	}

	class ClickEvent implements OnItemClickListener {
		// onItemClickメソッドには、AdapterView(adapter)、選択した項目View、選択された位置のint値、IDを示すlong値が渡される
		public void onItemClick(AdapterView<?> adapter, View view,
				int position, long id) {
			ListView listView = (ListView) adapter;
			Node item = (Node) listView.getItemAtPosition(position);
			userDialogShow(item);
		}

		private void userDialogShow(Node node) {
			UserFragment.Builder builder = new UserFragment.Builder();
			builder.setNode(node);
			builder.create().show(getFragmentManager(), getTag());
		}
	}


}