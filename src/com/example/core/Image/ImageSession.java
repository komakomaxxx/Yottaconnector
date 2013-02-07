package com.example.core.Image;

import java.util.Timer;
import java.util.TimerTask;

import com.example.core.Packet;

public class ImageSession{
	public static final int STS_SYN = 0x00;
	public static final int STS_ACK = 0x01;
	public static final int STS_OK = 0x02;
	
	private Timer timeOut;
	
	private String OriginalSourceMac;
	private String OriginalDestinationMac;
	private String SourceMac;
	private String FindMac;
	private int TypeNum;
	private int SessionStatus;
	
	public ImageSession(Packet packet){
		this.OriginalSourceMac = new String(packet.getOriginalSourceMac());
		this.OriginalDestinationMac = new String(packet.getOriginalDestinationMac());
		this.SourceMac = new String(packet.getSourceMac());
		this.TypeNum = packet.getTypeNum();
		this.SessionStatus = STS_SYN;
	}
	
	public void setStatus(int status){
		SessionStatus = status;
	}
	
	public int getStatus(){
		return SessionStatus;
	}
	
	public void setFindMac(String mac){
		FindMac = mac;
	}
	
	public String getFindMac(){
		return FindMac;
	}
	
	public String getSourceMac(){
		return SourceMac;
	}
	
	public String getOriginalSourceMac(){
		return OriginalDestinationMac;
	}
	
	public void setOriginalDestinationMac(String mac){
		OriginalDestinationMac = mac;
	}
	
	public String getOriginalDestinationMac(){
		return OriginalDestinationMac;
	}
	
	private ImageSession getInstance(){
		return this;
	}
	
	public void timerStart(){
		timeOut = new Timer(true);
		timeOut.schedule(new TimerTask(){
			@Override
			public void run() {
				// TODO 自動生成されたメソッド・スタブ
				ImageSessionList.removeSession(getInstance());
			}
		},30000);
	}
	
	public void timerClear(){
		timeOut.cancel();
		timeOut = null;
	}
	
	public void timerReStart(){
		timerClear();
		timerStart();
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
		
		ImageSession other = (ImageSession)obj;
		
		if( OriginalSourceMac.compareTo(other.OriginalSourceMac) == 0
			&& TypeNum == other.TypeNum){
			return true;
		}
		
		return false;
	}

}
