package com.example.yottaconnecter;

import android.util.Log;


public class NodeListListenerNotify implements Runnable{
	private NodeListListener listener;
	private int length;
	private Thread thread;
	private boolean firstnodeFlag = true;

	public NodeListListenerNotify(int size) {
		// TODO 自動生成されたコンストラクター・スタブ
		this.length = size;
	}
	
    public void setListener(NodeListListener listener){
        this.listener = listener;
        thread = new Thread(this);
        thread.start();
    }

    public void removeListener(){
        this.listener = null;
    }

	public void run() {
		// TODO 自動生成されたメソッド・スタブ
		while(true){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
			if(length != NodeList.nodelist.size()){
				listener.onNodeChangeListener(NodeList.nodelist.size());
				length = NodeList.nodelist.size();
			}
		}
	}
	
}