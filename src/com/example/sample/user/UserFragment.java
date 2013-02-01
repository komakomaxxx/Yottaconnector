package com.example.sample.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.sample.Node;
import com.example.yottaconnecter.*;
import com.example.sample.Yossip;
import com.example.sample.YossipList;
import com.example.sample.friendlist.FriendListManager;
import com.example.sample.message.MessageFragment;

public class UserFragment extends DialogFragment implements OnClickListener, OnCheckedChangeListener {
	private ToggleButton favoriteButton;
	private Button messageButton;
	private Button fileButton;
	private Node node;


	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);

		//状態を保存
		//outState.putParcelable("node", parcelNode);
	}
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		//状態を読み込む
		//parcelNode = savedInstanceState.getParcelable("node");

	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog newDialog = super.onCreateDialog(savedInstanceState);
		newDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		newDialog.setContentView(onCreateContentView());
		newDialog.getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;
		newDialog.getWindow().getAttributes().height = LayoutParams.MATCH_PARENT;

		return newDialog;
	}

	private View onCreateContentView() {
		View view = (View) getActivity().getLayoutInflater().inflate(R.layout.user_fragment, null);
		node = getArguments().getParcelable("node");
		setNodeUserData(view);
		setNodeYossipList(view);
		favoriteButton = (ToggleButton) view.findViewById(R.id.button_user_favorite);
		favoriteButton.setOnCheckedChangeListener(this);

		if(FriendListManager.sameNode(node) ){
			favoriteButton.setChecked(false);
		}
		else{
			favoriteButton.setChecked(true);
		}

		messageButton = (Button) view.findViewById(R.id.button_user_sendmessage);
		messageButton.setOnClickListener(this);
		fileButton = (Button) view.findViewById(R.id.button_user_sendfile);
		fileButton.setOnClickListener(this);

		return view;
	}

	private void setNodeUserData(View view) {
		ImageView iconView = (ImageView) view.findViewById(R.id.icon);
		TextView nameView = (TextView) view.findViewById(R.id.name);
		TextView profView = (TextView) view.findViewById(R.id.profile);

		iconView.setImageBitmap(node.getIcon());
		nameView.setText(node.getName());
		profView.setText(node.getProfile());
	}

	private void setNodeYossipList(View view) {

		List<Yossip> reverseYL = new ArrayList<Yossip>();
		String nodeMac = node.getMACAddr();
		for (Yossip yossipitem : YossipList.y_list) {
			if (yossipitem.getYossipUserMac().equals(nodeMac)) {
				reverseYL.add(yossipitem);
			}
		}
		Collections.reverse(reverseYL);
		ListView nodeYossipList = (ListView) view.findViewById(R.id.yossiplist);
		ArrayAdapter<Yossip> adapter = new YossipListAdapter(view.getContext(), R.layout.yossiplist, reverseYL);
		nodeYossipList.setAdapter(adapter);
	}

	public void onMyListenerEvent() {
		// TODO Auto-generated method stub

	}

	public void onClick(View v) {
		if (v == messageButton) {
			new MessageFragment.Builder().create(getActivity(), node.getMACAddr()).show(getFragmentManager(), getTag());
		}
		else if (v == fileButton) {
			Toast.makeText(getActivity(), "onClickFileButton", Toast.LENGTH_SHORT).show();
		}
	}

	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(isChecked){
			FriendListManager.add(node);
		}
		else{
			FriendListManager.remove(node);
		}
	}

	//--------------------------
	public static class Builder {
		private Node buildNode;

		public void setNode(Node node) {
			buildNode = node;
		}

		public UserFragment create() {
			UserFragment dialog = new UserFragment();
			Bundle args = new Bundle();
			args.putParcelable("node", buildNode);
			dialog.setArguments(args);

			return dialog;
		}
	}
}