package com.example.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.example.core.yossipPaket.*;
import com.example.core.Message.*;
import com.example.core.Hello.*;
import com.example.core.Image.*;
import com.example.core.NodeExchange.*;
import com.example.core.Session.*;
import android.provider.MediaStore.Images;
import android.service.textservice.SpellCheckerService.Session;

public class SettingData implements Runnable{
	private Thread thread;
	//List<Integer> iBuf = new ArrayList<Integer>(2000);
	List<Integer> iBuf;
	List<Integer> imageBuf;
	
	
	public SettingData(List<Integer> buf) {
		// TODO 自動生成されたコンストラクター・スタブ
		
		/*遅い?
		for (Integer i : buf) {
			iBuf.add(i);
		}
		*/
		iBuf=new ArrayList<Integer>(buf);
		
		thread = new Thread(this);
		thread.start();
	}
	
	public SettingData(List<Integer> buf,List<Integer> imageBuf) {
		// TODO 自動生成されたコンストラクター・スタブ

		iBuf=new ArrayList<Integer>(buf);
		this.imageBuf = new ArrayList<Integer>(imageBuf);
		
		thread = new Thread(this);
		thread.start();
	}
	
	
	@Override
	public void run() {
		// TODO 自動生成されたメソッド・スタブ
		
		Packet packet = new Packet();

		//ヘッダ取得
		int HeaderSize = 67;
		char[] cbuf = new char[HeaderSize];
		for(int i=0;i<HeaderSize;i++){
			cbuf[i] = (char)iBuf.get(i).intValue();
		}
		
		//ヘッダ切り分け
		String tmpT = new String(cbuf,0,1);
		String tmpOSM = new String(cbuf,1,12);
		String tmpODM = new String(cbuf,13,12);
		String tmpSM = new String(cbuf,25,12);
		String tmpDM = new String(cbuf,37,12);
		String tmpHL = new String(cbuf,49,2);
		String tmpSN = new String(cbuf,51,8);
		String tmpTN = new String(cbuf,59,8);
		
		
		//deta部取得
		char[] dbuf = new char[1500];
		int countMax = iBuf.size()-HeaderSize;
		for(int i=0;i<countMax;i++){
			dbuf[i] = (char)iBuf.get(HeaderSize+i).intValue();
		}
		String tmpData = new String(dbuf,0,countMax);
		
		//ブロックするデバイス
		//String hanawa="3085a9df87d1";
		//String komai="3085a9dae2f7";
		//String togo="3085a9dd58af";

//		if(tmpSM.equals(komai)){
//			return;
//		}
		//セッションチェック
		if(SessionCtl.sreachSession(tmpOSM,Integer.parseInt(tmpSN,16)) == true){	
			return;
		}
		else{
		}

		if(Integer.parseInt(tmpT,16) == 0x06){
		}
		if(Integer.parseInt(tmpT,16) == 0x05){
		}
		
		
		//パケットクラスに格納
		packet.setType(Integer.parseInt(tmpT,16));
		packet.setOriginalSourceMac(tmpOSM);
		packet.setOriginalDestinationMac(tmpODM);
		packet.setSourceMac(tmpSM);
		packet.setDestinationMac(tmpDM);
		packet.setHopLimit(Integer.parseInt(tmpHL,16));
		packet.setSequenceNum(Integer.parseInt(tmpSN,16));
		packet.setTypeNum(Integer.parseInt(tmpTN,16));
		packet.setData(tmpData);
				
	
		int type = packet.getType();
		
		if (type == Packet.Hello) {
			Hello.recv(packet);
		} else if (type == Packet.HelloAck) {
			HelloAck.recv(packet);
		} else if (type == Packet.Yossip) {
			YossipPaket.recv(packet);
		} else if (type == Packet.Message) {
			Message.cntrol(packet);
		} else if (type == Packet.MessageAck) {
			MessageACK.cntrol(packet);
		} else if (type == Packet.NodeExREQ) {
			NodeExchangeReqest.recv(packet);
		} else if (type == Packet.NodeExREP) {
			NodeExchangeReplay.recv(packet);
		} else if (type == Packet.ImageSYN) {
			ImageSessionSYN.cntrol(packet);
		} else if (type == Packet.ImageACK) {
			ImageSessionACK.cntrol(packet);
		} else if (type == Packet.ImageDATA) {
			ImageData.setImagePacket(packet, imageBuf);
		} else {
		}
		return;
		
	
	}
}


