package com.example.sample.header;


public class ReceiveMessageNotify implements Runnable {

	private ReceiveMessageListener listener;
	private ReceiveMessageManager manager;
	private Thread thread;

	public ReceiveMessageNotify(ReceiveMessageListener listener) {
		// TODO 自動生成されたコンストラクター・スタブ
		this.listener = listener;
		thread = new Thread(this);
		thread.start();
	}

	public void run() {
		int length = 0;
		while (true) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(length != ReceiveMessageManager.size()){
				length = ReceiveMessageManager.size();
				listener.onReceiveChangeListener(length);
			}
			
		}
	}
}
