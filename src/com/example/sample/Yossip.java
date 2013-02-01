package com.example.sample;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;

@SuppressLint("SimpleDateFormat")
public class Yossip {

	private String yossip;
	private Date yossipTime;
	private String yossipUser;
	private String yossipUserMac;
	private Bitmap yossipIcon;
	public SimpleDateFormat timeFormat = new SimpleDateFormat("kk'時'mm'分'ss'秒'");


	public Yossip(String y, Date yt, String ym, String yu) {
		yossip = y;
		yossipTime = yt;
		yossipUserMac = ym;
		yossipUser = yu;
	}
	public Yossip(String y, Date yt, String ym, String yu, Bitmap yi) {
		yossip = y;
		yossipTime = yt;
		yossipUserMac = ym;
		yossipUser = yu;
		yossipIcon = yi;
	}
	public Yossip getYossip(){
		return this;
	}
	public String getYossipText(){
		return this.yossip;
	}
	public String getYossipUser(){
		return this.yossipUser;
	}
	public Date getYossipTime(){
		return this.yossipTime;
	}
	public Bitmap getYossipIcon() {
		return this.yossipIcon;
	}
	public String yossipTimeFarmat(){
		return timeFormat.format( this.yossipTime );
	}
	public String getYossipUserMac(){
		return yossipUserMac;
	}
}
