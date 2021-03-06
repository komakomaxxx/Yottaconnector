package com.example.sample.nodelist;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.example.yottaconnecter.Node;
import com.example.yottaconnecter.NodeList;
import com.example.yottaconnecter.NodeListListener;
import com.example.yottaconnecter.NodeListListenerNotify;
import com.example.yottaconnecter.R;
import com.example.yottaconnecter.YottaConnector;
import com.example.sample.header.HeaderFragment;
import com.example.sample.user.UserFragment;

public class NodeListFragment extends Fragment implements
		OnFocusChangeListener, NodeListListener {
	// ------------------------------
	// private GridView nodeListView;
	private ListView nodeListView;
	private ArrayAdapter<Node> adapter;
	private NodeListListenerNotify nlln;
	private EditText searchBox;
	private ImageButton updata;
	private Handler myHandler;

	// ------------------------------

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		nlln = new NodeListListenerNotify(NodeList.nodelist.size());
		nlln.setListener(this);
		
		myHandler = new Handler();

		return inflater.inflate(R.layout.nodelist_fragment, container, false);
		// return inflater.inflate(R.layout.nodelist_fragment_var2, container,
		// false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		searchBox = (EditText) getActivity().findViewById(R.id.search_box);
		searchBox.addTextChangedListener(new UITextWatcher());
		searchBox.setOnFocusChangeListener(this);
		searchBox.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (event != null
						&& event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					adapter.clear();
					for (Node nodeitem : NodeList.nodelist) {
						if (nodeitem.getName().indexOf(v.getText().toString()) != -1) {
							adapter.add(nodeitem);
						}
					}
				}
				return false;
			}
		});

		updata = (ImageButton) getActivity().findViewById(R.id.updata_node);
		updata.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				updata.setFocusable(true);
				updata.setFocusableInTouchMode(true);
				adapter.clear();
				for (Node yitem : NodeList.nodelist) {
					adapter.add(yitem);
				}
				nodeListView.setAdapter(adapter);
				searchBox.setText("");
			}
		});

	}

	public void onStart() {
		super.onStart();
		setNodeList();
	}

	@Override
	public void onResume() {
		super.onResume();
		int state = YottaConnector.mPager.getCurrentItem() % 4;
		switch (state) {
		case 0:
			HeaderFragment.setFragmentName("Rader");
			break;
		case 1:
			HeaderFragment.setFragmentName("TimeLine");
			break;
		case 2:
			HeaderFragment.setFragmentName("FriendList");

			break;
		case 3:
			HeaderFragment.setFragmentName("NodeList");

			break;
		}

	}


	public void onMyListenerEvent() {
		// TODO Auto-generated method stub

	}

	private void setNodeList() {
		List<Node> tempnl = new ArrayList<Node>();
		for (Node item : NodeList.nodelist) {
			tempnl.add(item);
		}
		adapter = new NodeListAdapter(getActivity(), R.layout.nodelist, tempnl);
		nodeListView = (ListView) getActivity().findViewById(R.id.nodelist);
		// nodeListView = (GridView)
		// getActivity().findViewById(R.id.nodelist_var2);
		nodeListView.setAdapter(adapter);
		nodeListView.setOnItemClickListener(new ClickEvent());
	}

	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus == false) {
			InputMethodManager imm = (InputMethodManager) getActivity()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	public void onNodeListChangeListener(int length) {
		updata.setFocusable(false);
		updata.setFocusableInTouchMode(false);

	}

	public void onNodeChangeListener(int length) {
		
		myHandler.post(new Runnable() {
			public void run() {
				adapter.clear();
				updata.callOnClick();
        	}
        });
	}

	// -------------------------inner--------------------------------
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
			// builder.create().show(getActivity().getFragmentManager(), null);
			builder.create().show(getFragmentManager(), getTag());
		}
	}

	// --------------------------inner---------------------------------
	public class UITextWatcher implements TextWatcher {
		public void afterTextChanged(Editable s) {
			adapter.clear();
			for (Node nodeitem : NodeList.nodelist) {
				if (nodeitem.getName().startsWith(s.toString())) {
					adapter.add(nodeitem);
				}
			}
			nodeListView.setAdapter(adapter);
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}
	}

	@Override
	public void onFirstNodeGet() {
		// TODO 自動生成されたメソッド・スタブ
	}
}