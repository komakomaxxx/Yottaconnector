package com.example.sample.message;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;

/**
 * 
 * @author Kazuki Hasegawa
 * @version 3
 * @since 1/21/2013
 */
public class MessageNotify implements Runnable{
	private MessageListener listener = null;
	private Thread thread;
	private ProgressDialog progressDialog;
	
	/**
	 * コンストラクタ
	 * プログレスの準備をする
	 * 
	 * @param context コンテキスト
	 */
	public MessageNotify(Context context) {
		this.progressDialog = new ProgressDialog(context);
		this.progressDialog.setOnKeyListener(new OnKeyListener() { 
		    @Override
		    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
		        return true;
		    }
		});
		this.progressDialog.setMessage("メッセージ送信中");
	}
	
	/**
	 * リスナをセットする
	 * 
	 * @param listener メッセージのリスナ
	 */
    public void setListener(MessageListener listener){
        this.listener = listener;
    }
    
    /**
     * スレッドをスタートをする
     * スレッドを初期化し、スレッドを開始する
     */
    public void start() {
		this.progressDialog.show();
		thread = new Thread(this);
    	thread.start();
    }

    /**
     * リスナを削除する
     */
    public void removeListener(){
        this.listener = null;
    }

    /**
     * run Method
     */
    @Override
	public void run() {
    	while(true) {
    		try {
    			Thread.sleep(1000);
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		}
    		if(listener == null) break;
    		listener.onCheckMessages();
    		if(!MessageManager.isWaiting()) {
    			break;
    		}
		}
    	progressDialog.dismiss();
	}
	
	/**
	 * 
	 * @author Kazuki Hasegawa
	 * @version 1
	 * @since 1/21/2013
	 */
	public interface MessageListener {
		/**
		 * メッセージをチェックする
		 */
		public void onCheckMessages();
	}
}