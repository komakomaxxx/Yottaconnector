package com.example.core.Message;

import java.util.Timer;
import java.util.TimerTask;

import com.example.core.Packet;

	
class MessageRoot extends TimerTask{
	private Timer timeOut;
	
	private String OriginalSourceMac;
	private String OriginalDestinationMac;	
	private String ForwardMac;
	
	public MessageRoot(Packet packet,String ForwardMac){
		setMember(packet.getOriginalSourceMac(),packet.getOriginalDestinationMac(),ForwardMac);
	}
	
	public MessageRoot(Packet packet){
		setMember(packet.getOriginalSourceMac(),packet.getOriginalDestinationMac(),null);
	}
	
	public MessageRoot(String OriginalSourceMac,String OriginalDestMac){
		setMember(OriginalSourceMac,OriginalDestinationMac,null);
	}
	
	private void setMember(String OriginalSourceMac,String OriginalDestinationMac,String ForwardMac){
		this.OriginalSourceMac = new String(OriginalSourceMac);
		this.OriginalDestinationMac = new String(OriginalDestinationMac);
		if(ForwardMac != null){
			this.ForwardMac = new String(ForwardMac);
		}else{
			this.ForwardMac = null;
		}
	}

	@Override
	public void run() {
		// TODO 自動生成されたメソッド・スタブ
		MessageRootTable.removeRoot(this);
	}
	
	public String getForwardMac(){
		return ForwardMac;
	}
	
	public void timerStart(){
		timeOut = new Timer(true);
		timeOut.schedule(this,100000);
	}
	
	public void timerReStart(){
		timerClear();
		timerStart();
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
		
		MessageRoot other = (MessageRoot)obj;
		
		if( OriginalDestinationMac.compareTo(other.OriginalDestinationMac) == 0
			&& OriginalSourceMac.compareTo(other.OriginalSourceMac) == 0){
			return true;
		}
		
		return false;
	}
}