package com.example.sample.header;

import java.util.EventListener;

public interface ReceiveMessageListener extends EventListener{
	public void onReceiveChangeListener(int length);
}