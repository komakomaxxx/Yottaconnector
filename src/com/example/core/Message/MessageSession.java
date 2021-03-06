package com.example.core.Message;

import java.util.Timer;
import java.util.TimerTask;

import com.example.core.Packet;


class MessageSession extends TimerTask {
	private Timer timeOut;
	
	private String OriginalSourceMac;
	private String OriginalDestinationMac;
	private String SourceMac;
	private int TypeNum;
	
	public MessageSession(Packet packet){
		this.OriginalSourceMac = new String(packet.getOriginalSourceMac());
		this.OriginalDestinationMac = new String(packet.getOriginalDestinationMac());
		this.SourceMac = new String(packet.getSourceMac());
		this.TypeNum = packet.getTypeNum();
	}
	
	@Override
	public void run() {
		// TODO 自動生成されたメソッド・スタブ
		MessageSessionList.removeSession(this,false);
	}
	
	public String getSourceMac(){
		return SourceMac;
	}
	
	public String getOriginalSourceMac(){
		return OriginalSourceMac;
	}
	
	public String getOriginalDestinationMac(){
		return OriginalDestinationMac;
	}
	
	public void timerStart(){
		timeOut = new Timer(true);
		timeOut.schedule(this,10000);
	}
	
	public void timerClear(){
		timeOut.cancel();
		timeOut = null;
	}
	
	public boolean equals(Object obj){
		
		if(this == obj){
			return true;
		}
		
		if(obj == null){
			return false;
		}
		
		if(getClass() != obj.getClass()){
			return false;
		}
		
		MessageSession other = (MessageSession)obj;
		
		if( OriginalDestinationMac.compareTo(other.OriginalDestinationMac) == 0
			&& OriginalSourceMac.compareTo(other.OriginalSourceMac) == 0
			&& TypeNum == other.TypeNum){
			return true;
		}
		
		return false;
	}
	
}