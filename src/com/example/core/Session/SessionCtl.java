package com.example.core.Session;




public class SessionCtl {
	final static int QueueSize = 10000;
	
	private static int setP=0;
	private static String[] sessionQueue = new String[QueueSize];
	
	public synchronized static boolean sreachSession(String macAddr,int sNum) {
		
		String searchData = macAddr + sNum;
		int getP = setP;
		for( int i=0;i < QueueSize;i++){
			if (sessionQueue[getP] != null ){
				if(sessionQueue[getP].equals(searchData)){
					return true;			
				}
			}
			
			getP = (getP-1+QueueSize) % QueueSize;
		}
		setP = (setP + 1) % QueueSize;
		sessionQueue[setP] = searchData;
		return false;
	}
	public synchronized static void addSession(String macAddr,int sNum) {
		setP = (setP + 1) % QueueSize;
		sessionQueue[setP] =macAddr + sNum;
		
	}
}
