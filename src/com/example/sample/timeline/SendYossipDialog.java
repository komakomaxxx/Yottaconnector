package com.example.sample.timeline;

import java.util.Date;

import com.example.yottaconnecter.*;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * ヨシップ送信ダイアログクラス
 *
 * @author  Kazuki Hasegawa
 * @version 3
 */
public class SendYossipDialog extends DialogFragment implements View.OnClickListener, TextWatcher, OnKeyListener{
	/**
	 * レイアウトに対応する番号
	 */
	private final int SEND_YOSSIP_DIALOG_LAYOUT = R.layout.send_yossip_dialog;
	
	/**
	 * 入力の許容範囲の最大値
	 */
	private final int SEND_YOSSIP_STRING_MAX = 150;	
	
	/**
	 * 黒色
	 */
	private final int COLOR_BLACK = Color.BLACK;
	
	/**
	 * 赤色
	 */
	private final int COLOR_RED = Color.RED;
	/**
	 * EditText
	 */
	private EditText edt;
	
	/**
	 * このメソッドはonCreate(Bundle)の後に呼ばれます。
	 * そして、onCreateView(LayoutInflater, ViewGroup, Bundle)の前に呼び出されます。
	 * Dialogに関する基本的な設定はここで行います。
     *
	 * @param savedInstanceState The last saved instance state of the Fragment,
     * or null if this is a freshly created Fragment.
     *
     * @return Return a new Dialog instance to be displayed by the Fragment.
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(onCreateContentView());
		dialog.getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;
		dialog.getWindow().getAttributes().height = LayoutParams.MATCH_PARENT;
		dialog.getWindow().getAttributes().softInputMode = LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
		dialog.setOnKeyListener(this);
		return dialog;
	}

	/**
	 * 入力画面のViewを生成し設定する
	 * キャンセルボタンとヨシップボタンにはonClickListenerを設定
	 * 入力領域のEditTextにはaddTextChangedListenerを設定する。
	 *
	 * @param  v ビューを保持しておく
	 * @param  cancelButton Viewに存在するキャンセルボタンを保持
	 * @param  yossipButton Viewに存在するヨシップボタンを保持
	 * @param  ed Viewに存在するダイアログを保持
	 *
	 * @return v 設定したビューを返す
	 */
	private View onCreateContentView() {
		View v = getActivity().getLayoutInflater().inflate(SEND_YOSSIP_DIALOG_LAYOUT, null);

		Button cancelButton = (Button)v.findViewById(R.id._SendYossipCancelButton);
		cancelButton.setOnClickListener(this);

		Button yossipButton = (Button)v.findViewById(R.id._SendYossipYossipButton);
		yossipButton.setOnClickListener(this);

		edt = (EditText)v.findViewById(R.id._SendYossipText);
		edt.addTextChangedListener(this);

		return v;
	}

	/**
	 * このダイアログのonClickListnerです。
	 * キャンセルボタンがタップされた場合、
	 */
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id._SendYossipCancelButton:
				if(isStringLengthInTheEditText(v)) {
					onAlertDialogShow();
				} else {
					onDialogClose();
				}
				break;
			case R.id._SendYossipYossipButton:
				// TODO Send Yossip
				addYossip(edt.getText().toString());
				onDialogClose();
				break;
		}
	}

	/**
	 * ヨシップ入力画面を終了する
	 */
	private void onDialogClose() {
		getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
	}

	/**
	 * テキストが変更される前に行われる処理です。
	 * 空の状態です。
	 */
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		// TODO Auto-generated method stub
	}

	/**
	 * テキストが変更されている最中に行われる処理です。
	 * 空の状態です。
	 */
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
	}

	/**
	 * テキストが変更された後に行われる処理です。
	 * ダイアログにあるEditTextの文字数が1文字以上かつ150文字以下である場合、
	 * yossipButtonを活性化させる。
	 * また、その場合文字カウントのTextViewの文字色がCOLOR_BLACK以外であるならば、
	 * 文字カウントの文字色をCOLOR_BLACKにする。
	 * それ以外の場合は、yossipButtonを非活性化し、
	 * また、その場合文字カウントTextViewの文字色がCOLOR_RED以外かつ150文字を超えているならば、
	 * 文字カウントの文字色をCOLOR_REDにする。
	 * 最後に、文字カウントにSEND_YOSSIP_STRING_MAX - textWordCountを表示させる。
	 * （残りの文字数とする）
	 *
	 *
	 * @param textWordCount EditTextの文字数を保持しておく変数
	 * @param yossipButton ヨシップボタンのViewを保持しておく変数
	 * @param countText 文字カウントを表示しているTextViewのViewを保持しておく変数
	 *
	 */
	public void afterTextChanged(Editable s) {
		int textWordCount = s.length();
    	Button yossipButton = (Button)getDialog().getWindow().getDecorView().findViewById(R.id._SendYossipYossipButton);
    	TextView countText = (TextView)getDialog().getWindow().getDecorView().findViewById(R.id._SendYossipCounters);

    	if(isWordCountWithinStringMaxLimit(textWordCount)) {
    		yossipButton.setEnabled(true);

    		if(countText.getCurrentTextColor() != COLOR_BLACK) {
    			countText.setTextColor(COLOR_BLACK);
    		}
    	} else {
    		yossipButton.setEnabled(false);

    		if(textWordCount > SEND_YOSSIP_STRING_MAX && countText.getCurrentTextColor() != COLOR_RED) {
    			countText.setTextColor(COLOR_RED);
    		}
    	}

    	/* 現在の文字数をセット */
    	countText.setText(String.valueOf(SEND_YOSSIP_STRING_MAX - textWordCount));
    }
	
	/**
	 * ヨシップリストへ追加するメソッド
	 * 
	 * @param s ヨシップされた文字列
	 * 
	 */
	private void addYossip(String s) {
		synchronized(YossipList.y_list) {
			YossipList.y_list.add(createYossip(s));
		}
	}
	
	/**
	 * ヨシップを生成する
	 * 
	 * @param yText
	 * @return 新規のヨシップ
	 */
	private Yossip createYossip(String yText){
		String name = "UserName";
		String path = "default";
		Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		if(!path.equals("default")) {
			icon = BitmapFactory.decodeFile(path);
		}
		WifiManager wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE); 
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		return new Yossip(yText, new Date(), wifiInfo.getMacAddress(), name, icon);
	}

	/**
	 * このメソッドは同じView内に現存するEditTextの文字列の長さがあるかどうかチェックするメソッド
	 *
	 * @param  v　渡されたView
	 * @param  ed エディットテキストのView
	 * @param  sb エディットテキストに入力されているバッファを取得
	 * @param  bool あるならばtrueをないならばfalseを保持する変数
	 *
	 * @return bool
	 */
	private boolean isStringLengthInTheEditText(View v)
	{
		EditText ed = (EditText)getDialog().getWindow().findViewById(R.id._SendYossipText);
		boolean bool = false;


		if(0 < ed.getText().length()) {
			bool = true;
		}

		return bool;
	}

	/**
	 * 渡された文字数が既定の範囲内であるかどうかチェックするメソッド
	 *
	 * @param  textWordCount 現在のエディットテキストの文字数
	 * @param  bool 範囲内ならtrueを範囲外ならばfalseを格納する変数
	 *
	 * @return bool
	 */
	private boolean isWordCountWithinStringMaxLimit(int textWordCount) {
		boolean bool = false;

		if(0 < textWordCount && textWordCount <= SEND_YOSSIP_STRING_MAX) {
			bool = true;
		}

		return bool;
	}

	/**
	 * 入力終了警告画面を設定し表示するメソッド
	 * 
	 * @param alert AlertDialog
	 */
	private void onAlertDialogShow() {
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		alert.setTitle(R.string.yossip_alert_title);
		alert.setMessage(R.string.yossip_alert_message);
		alert.setPositiveButton("OK",new OnClickListener() {
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
	}

	/**
	 * このダイアログのバックキー操作を制御するメソッド
	 * 
	 * @param dialog DialogInterface
	 * @param keyCode どのキーがタップされたか
	 * @param keyEvent どんなキーベントが発生したのかどうか
	 * 
	 * @return boolean
	 */
	@Override
	public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent keyEvent) {
		if(keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
			if(0 < edt.getText().toString().length()) {
				onAlertDialogShow();
			} else {
				return false;
			}
		}
		return false;
	}
}