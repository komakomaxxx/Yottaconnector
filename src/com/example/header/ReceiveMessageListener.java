package com.example.header;

import java.util.EventListener;

public interface ReceiveMessageListener extends EventListener{
	public void onReceiveChangeListener(int length);
}