package com.example.core;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Packet {
	//private List<Integer> rawPacket = new ArrayList<Integer>();
	private int Type;
	private String OriginalSourceMac = new String();
	private String OriginalDestinationMac = new String();
	private String SourceMac = new String();
	private String DestinationMac = new String();
	private int HopLimit;
	private int SequenceNum;
	private int TypeNum;
	private String Data = new String();
	private char[] ImageArray;
	
	public final char sChar = 0x0E;
	public final char eChar = 0x0F;
	public final static int HopLimitMax=10;
	public final static String broadCastMACaddr = "FFFFFFFFFFFF";
	
	public static int Hello = 0x00;
	public static int HelloAck = 0x01;
	public static int Yossip = 0x02;
	public static int Message = 0x03;
	public static int MessageAck = 0x04;
	public static int NodeExREQ = 0x05;
	public static int NodeExREP = 0x06;
	public static int ImageSYN = 0x07;
	public static int ImageACK = 0x08;
	public static int ImageDATA = 0x09;

	
	public Packet() {
		// TODO 自動生成されたコンストラクター・スタブ
	 	
	}
	public Packet(int type,String orignalSrcMac,String originalDstMac,String SrcMac,String DstMac,int hopLimit,int typeNum,String data){

		this.Type=type;
		this.OriginalSourceMac = orignalSrcMac;
		this.OriginalDestinationMac = originalDstMac;
		this.SourceMac = SrcMac;
		this.DestinationMac = DstMac;
		this.HopLimit = hopLimit;
		this.TypeNum = typeNum;
		this.Data = data;
	}
	public Packet(int type,String orignalSrcMac,String originalDstMac,String SrcMac,String DstMac,int hopLimit,int typeNum){

		this.Type=type;
		this.OriginalSourceMac = orignalSrcMac;
		this.OriginalDestinationMac = originalDstMac;
		this.SourceMac = SrcMac;
		this.DestinationMac = DstMac;
		this.HopLimit = hopLimit;
		this.TypeNum = typeNum;
	}
	
	public void setType(int t){
		this.Type = t;
	}
	
	public void setOriginalSourceMac(String osm){
		this.OriginalSourceMac = osm;
	}
	
	public void setOriginalDestinationMac(String odm){
		this.OriginalDestinationMac = odm;
	}
	
	public void setSourceMac(String sm){
		this.SourceMac = sm;
	}
	
	public void setDestinationMac(String dm){
		this.DestinationMac = dm;
	}
	
	public void setHopLimit(int hl){
		this.HopLimit = hl;
	}
	
	public void setSequenceNum(int sn){
		this.SequenceNum = sn;
	}
	
	public void setTypeNum(int tm){
		this.TypeNum = tm;
	}
	
	public void setData(String data){
		this.Data = data;
	}
	
//	public void setRawPacket(List<Integer> l){
//		this.rawPacket = l;
//	}
//	
	public void setImageArray(char[] ia){
		ImageArray = ia.clone();
	}
	
	public int getType(){
			return this.Type;
	}
	
	
	public String getOriginalSourceMac(){
		return this.OriginalSourceMac;
	}
	
	public String getOriginalDestinationMac(){
		return this.OriginalDestinationMac;
	}
	
	public String getSourceMac(){
		return this.SourceMac;
	}
	
	public String getDestinationMac(){
		return this.DestinationMac;
	}
	
	public int getHopLimit(){
		return this.HopLimit;
	}
	
	public int getSequenceNum(){
		return this.SequenceNum;
	}
	
	public int getTypeNum(){
		return this.TypeNum;
	}
	
	public String getData(){
		return this.Data;
	}
	
//	public List<Integer> getRawPacket(){
//		return this.rawPacket;
//	}
	
	public char[] getImageArray(){
		return ImageArray;
	}
	
	public void hoplimitDecrement(){
		this.HopLimit--;
	}
	
	public void exOriginalMac(){
		String tmp = OriginalSourceMac;
		OriginalSourceMac = OriginalDestinationMac;
		OriginalDestinationMac = tmp;
	}
	
	public void exMac(){
		String tmp = SourceMac;
		SourceMac = DestinationMac;
		DestinationMac = tmp;
	}
	
	public ArrayList<String> putData() {
		
		ArrayList<String> element = new ArrayList<String>();

		
		String regex = "\\" + String.valueOf(sChar) + ".+?\\"+String.valueOf(eChar);
		Pattern pattern = Pattern.compile(regex,Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(this.Data);
		
		while(matcher.find()){
			String temp = matcher.group();
			int index = temp.indexOf(String.valueOf(eChar));
			element.add(temp.substring(1, index));
		}
		
		return element;
	}
	public void createData(List<String> dataList ) {
		String buf = new String();
		
		for(int i=0; i < dataList.size();i++){
			buf += String.valueOf(sChar) + dataList.get(i) +  String.valueOf(eChar);
		}
		Data = buf;
		
		return;
	}

}
