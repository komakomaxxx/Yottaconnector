package com.example.header;

import java.util.Date;

import com.example.header.ReceiveMessageListener;
import com.example.header.ReceiveMessageManager;
import com.example.header.ReceiveMessageNotify;
import com.example.yottaconnecter.*;
import com.example.sample.message.MessageFragment;
import com.example.sample.message.MessageManager;
import android.support.v4.app.Fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class HeaderFragment extends Fragment implements ReceiveMessageListener {
	private ImageView receiveMessageIcon;
	private TextView receiveCount;
	Handler handler;

	ArrayAdapter<MessageManager.Message> adapter;
	ListView messageList;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = getActivity().getLayoutInflater().inflate(R.layout.header_fragment, null);
		
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		messageList = (ListView) getActivity().findViewById(R.id.h_MessageList);
		messageList.setOnItemClickListener(new ClickEvent());
		
		new ReceiveMessageNotify(this);
		handler = new Handler();

		receiveMessageIcon = (ImageView) getActivity().findViewById(R.id.H_Message_icon);
		
		receiveCount = (TextView) getActivity().findViewById(
				R.id.receive_message_countIcon);

		getActivity().findViewById(R.id.H_Message_icon).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						
						Button b = (Button)getActivity().findViewById(R.id.h_MessagePull);
						b.callOnClick();
					}
				});
		getActivity().findViewById(R.id.h_Message).setOnTouchListener(
				new OnTouchListener() {
					public boolean onTouch(View v, MotionEvent event) {
						// TODO Auto-generated method stub
						return true;
					}
				});

		getActivity().findViewById(R.id.setting_icon).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						// TODO Auto-generated method stub
						
						DialogFragment dialog = new Setting_fragment();
						dialog.show(getActivity().getFragmentManager(), null);
					}
				});
		getActivity().findViewById(R.id.receive_test_button2)
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						ReceiveMessageManager.addReceiveMessage(new MessageManager.Message(
								"aa:aa:aa:aa:aa", 0, "message", new Date(),false,0));
					}
				});
		getActivity().findViewById(R.id.receive_test_button1)
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						ReceiveMessageManager.clearReceiveMessage();
					}
				});
	}

	public void onReceiveChangeListener(final int len) {
		handler.post(new Runnable() {

			public void run() {
				int length = len;
				if (length == 0) {
					receiveCount.setText("");
					receiveMessageIcon.setVisibility(View.INVISIBLE);
				} else {
					receiveCount.setText(Integer.toString(length));
					receiveMessageIcon.setVisibility(View.VISIBLE);
				}
				adapter = new HeaderMessageAdapter(getActivity(),
						R.layout.header_message_row,
						ReceiveMessageManager.getReceiveList());
				messageList.setAdapter(adapter);
			}
		});

	}
	class ClickEvent implements OnItemClickListener {
		// onItemClickメソッドには、AdapterView(adapter)、選択した項目View、選択された位置のint値、IDを示すlong値が渡される
		public void onItemClick(AdapterView<?> adapter, View view,
				int position, long id) {
			ListView listView = (ListView) adapter;
			MessageManager.Message item = (MessageManager.Message) listView.getItemAtPosition(position);
			
			ReceiveMessageManager.removeReceiveMessage(item);
			MessageManager.setContext(getActivity());
			new MessageFragment(item.getMACAddr()).show(getFragmentManager(), getTag());
		}
	}
}