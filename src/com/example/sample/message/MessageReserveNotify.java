package com.example.sample.message;

public class MessageReserveNotify implements Runnable{
	private MessageReserve listener = null;
	private Thread thread;
	
	/**
	 * リスナをセットする
	 * 
	 * @param listener メッセージのリスナ
	 */
    public void setListener(MessageReserve listener){
        this.listener = listener;
    }
    
    /**
     * スレッドをスタートをする
     * スレッドを初期化し、スレッドを開始する
     */
    public void start() {
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
	public void run() {
		while(true) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			listener.checkMessageList();
		}
	}
	
	/**
	 * 
	 * @author Kazuki Hasegawa
	 * @version 1
	 * @since 2/5/2013
	 */
	public interface MessageReserve {
		public void checkMessageList();
	}
}