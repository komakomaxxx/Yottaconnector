package com.example.core.Message;

import java.util.Timer;
import java.util.TimerTask;

import com.example.core.Packet;

	
class MessageRoot{
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
		setMember(OriginalSourceMac,OriginalDestMac,null);
	}
	
	private void setMember(String OriginalSourceMac,String OriginalDestinationMac,String ForwardMac){
		this.OriginalSourceMac = new String(OriginalSourceMac);
		
		if(OriginalDestinationMac != null){
			this.OriginalDestinationMac = new String(OriginalDestinationMac);
		}else{
			this.OriginalDestinationMac = null;
		}
		
		if(ForwardMac != null){
			this.ForwardMac = new String(ForwardMac);
		}else{
			this.ForwardMac = null;
		}
	}

	public String getForwardMac(){
		return ForwardMac;
	}
	
	public String getOriginalSourceMac(){
		return OriginalSourceMac;
	}
	
	public String getOriginalDestinationMac(){
		return OriginalDestinationMac;
	}
	
	public void timerStart(){
		timeOut = new Timer(true);
		timeOut.schedule(new TimerTask(){
			@Override
			public void run() {
				// TODO 自動生成されたメソッド・スタブ
				MessageRootTable.removeRoot(getInstance());
			}
		},100000);
	}
	
	public void timerReStart(){
		timerClear();
		timerStart();
	}
	
	public void timerClear(){
		timeOut.cancel();
	}
	
	private MessageRoot getInstance(){
		return this;
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