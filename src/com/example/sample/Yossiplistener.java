package com.example.sample;

import java.util.EventListener;

public interface Yossiplistener extends EventListener{
	public void onYossipListChangeListener(int length);
	public void onNewYossipGetListener(Yossip y);
}
