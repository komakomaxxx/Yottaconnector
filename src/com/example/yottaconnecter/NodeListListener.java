package com.example.yottaconnecter;

import java.util.EventListener;

public interface NodeListListener extends EventListener{
	public void onNodeChangeListener(int length);
	public void onFirstNodeGet();
}
