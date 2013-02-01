package com.example.yottaconnecter;

public class YossipListenerNotify implements Runnable{
	private Yossiplistener listener = null;
	private int length;
	
	public YossipListenerNotify(int size) {
		// TODO 自動生成されたコンストラクター・スタブ
		this.length = size;
	}
	
    public void setListener(Yossiplistener listener){
        this.listener = listener;
        Thread thread = new Thread(this);
        thread.start();
    }

    public void removeListener(){
        this.listener = null;
    }

	public void run() {
		// TODO 自動生成されたメソッド・スタブ
		while(true){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
			synchronized(YossipList.y_list) {
				if(length != YossipList.y_list.size()) {
					length = YossipList.y_list.size();
						listener.onNewYossipGetListener(YossipList.y_list.get(length - 1));
						listener.onYossipListChangeListener(YossipList.y_list.size());
				}
			}
        }
	}
}
