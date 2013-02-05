package com.example.sample.message;

import com.example.yottaconnecter.Node;
import com.example.yottaconnecter.NodeList;
import com.example.yottaconnecter.R;

import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * メッセージフラグメントのクラス
 * ダイアログフラグメントで表示させるようにしてあるため変更がある際は連絡ください。
 * 
 * @author Kazuki Hasegawa
 * @version 6
 * @since 1/23/2013
 */
public class MessageFragment extends DialogFragment implements TextWatcher, View.OnClickListener, MessageNotify.MessageListener, OnKeyListener {
	/**
	 * メッセージリストに使用するアダプタ
	 */
	private static MessageAdapter adapter;
	/**
	 * ハンドラー
	 */
	private Handler handler = new Handler();
	/**
	 * ノード保持
	 */
	private static Node node;
	/**
	 * イベント
	 */
	private MessageNotify notify;
	/**
	 * EditText
	 */
	private EditText edt;
	
	/**
	 * static初期化ブロック
	 */
	static {
		adapter = new MessageAdapter();
	}
	
	/**
	 * コンストラクタ
	 * 回転時に必要
	 */
	public MessageFragment() {
		setAdapter();
	}
	
	/**
	 * コンストラクタ
	 * 
	 * @param node ノード
	 */
	public MessageFragment(Node node) {
		MessageFragment.node = node;
		setAdapter();
	}
	
	/**
	 * このフラグメントのダイアログを作成し
	 * 設定を行う。
	 * 
	 * @param savedInstanceState
	 * 
	 * @return 作成したダイアログ
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog newDialog = super.onCreateDialog(savedInstanceState);
		newDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		newDialog.setContentView(onCreateContentView());
		newDialog.getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;
		newDialog.getWindow().getAttributes().height = LayoutParams.MATCH_PARENT;
		newDialog.getWindow().getAttributes().softInputMode = LayoutParams.SOFT_INPUT_STATE_HIDDEN;
		newDialog.setOnKeyListener(this);
		notify = new MessageNotify(getActivity());
		notify.setListener(this);
		return newDialog;
	}
	
	/**
	 * ビューの設定をする
	 * ビューを作成し詳細な設定をした上で返す
	 * 
	 * @return ビュー
	 */
	private View onCreateContentView() {
		View mesView = getActivity().getLayoutInflater().inflate(R.layout.message_fragment, null);
		
		ListView lview = (ListView) mesView.findViewById(R.id._MessageListView);
		lview.setAdapter(adapter);
		
		ImageButton ibtn = (ImageButton) mesView.findViewById(R.id._MessageButton);
		ibtn.setOnClickListener(this);
		ibtn.setClickable(false);
		
		edt = (EditText) mesView.findViewById(R.id._MessageTextArea);
		edt.addTextChangedListener(this);
		
		TextView tv = (TextView) mesView.findViewById(R.id._mName);
		tv.setText(node.getName() + "へのメッセージ");
		
		ImageView iv = (ImageView) mesView.findViewById(R.id._mIcon);
		iv.setImageBitmap(node.getRadarIcon());
		
		return mesView;
	}
	
	/**
	 * リストビューに使うアダプタを設定する
	 * 
	 * @param mac 宛先マックアドレス
	 */
	private void setAdapter() {
		if(adapter == null) {
			adapter = new MessageAdapter();
		}
		adapter.setList(node);
	}
	
	/**
	 * エディットテキストの中に変更があった後に行われるメソッド
	 * ボタンの活性非活性を行う
	 * 
	 * @param s
	 */
	public void afterTextChanged(Editable s) {
		ImageButton ibtn = (ImageButton) getDialog().getWindow().findViewById(R.id._MessageButton);
		if(s.length() <= 0) {
			ibtn.setClickable(false);
		} else {
			ibtn.setClickable(true);
		}
	}

	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub
	}

	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
	}
	
	/**
	 * クリックされた時の処理
	 * 送信待ち状態のメッセージがない場合にしか送信させない
	 * 送信待ち状態のメッセージがある場合にはエラーをトーストで表示させる
	 * 
	 * @param v クリックされたビュー
	 */
	public void onClick(View v) {
		if(v.getId() == R.id._MessageButton) {
			EditText edt = (EditText) getDialog().getWindow().findViewById(R.id._MessageTextArea);
			adapter.add(edt.getText().toString());
			edt.getEditableText().clear();
//			notify.start();
		}
	}
	
	/**
	 * waitMessageに残っている場合に呼ばれる
	 * StateがSUCCESであれば追加し
	 * StateがFALEDであれば削除する
	 */
	public synchronized void onCheckMessages() {
		MessageManager.onArrangeWaitMessage();
		handler.post(new Runnable() {
			public void run() {
				((ListView) getDialog().getWindow().findViewById(R.id._MessageListView)).invalidateViews();
			}
		});
	}
	
	/**
	 * onKeyメソッドの実装
	 * 
	 * @param dialog DialogInterface
	 * @param keyCode どのキーがタップされたのか
	 * @param keyEvent どんなキーイベントが発生したのかを調べる
	 * 
	 * @return boolean
	 */
	@Override
	public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent keyEvent) {
		if(keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
			if(0 < edt.getText().toString().length()) {
				AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
				alert.setTitle(R.string.mes_alert_title);
				alert.setMessage(R.string.mes_alert_message);
				alert.setPositiveButton("OK", new OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						onDialogClose();
					}
				
				});
				alert.setNegativeButton("Cancel", new OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						
					}
				
				});
				alert.show();
			} else {
				return false;
			}
		}
		return false;
	}
	
	/**
	 * ダイアログの終了メソッド
	 */
	public void onDialogClose() {
		getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
	}
	/**
	 * メッセージダイアログのビルダ
	 * 
	 * @author Kazuki Hasegawa
	 * @version 3
	 * @since 1/23/2013
	 */
	public static class Builder {
		public MessageFragment create(Context context, Node node) {
			MessageManager.setContext(context);
			MessageFragment dialog = new MessageFragment(node);
			return dialog;
		}
	}
}