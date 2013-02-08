package com.example.core.Image;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.example.yottaconnecter.*;
import com.example.core.Packet;
import com.example.core.SendSocket;
import com.example.core.SplitImage;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Log;

public class ImageData{

	Thread thread;
	private static List<SplitImage> ImageList = null;
	private static byte[] bytes;
	
	
	//画像データバッファリング
	private static void setImageData(char[] ArrayImage,int piece,int sum,String NodeMac){
		if(ImageList == null){
			ImageList = new ArrayList<SplitImage>(sum);
			Log.d("ImageData", "first add [" + piece + "/" + ImageList.size() + "] sum=" + sum);
		}
		Node n = NodeList.getNode(NodeMac);
		Log.d("ImageData", n.getName() + "'s image add [" + piece + "/" + ImageList.size() + "] sum=" + sum);
		ImageList.add(new SplitImage(piece, ArrayImage));
		if(ImageList.size() == sum){
			List<Byte> ByteImage = new ArrayList<Byte>();
			Log.d("ImageData", "Make ImageList complete.List size is" + ImageList.size());
			
			Collections.sort(ImageList, new ImageComparator());
			
			for(int i = 0; i < ImageList.size();i++){
				char[] temp = ImageList.get(i).ArrayImage.clone();
				for(int j = 0;j < temp.length;j++){
					ByteImage.add(Integer.valueOf((int)temp[j]).byteValue());
				}
			}
			Byte[] Bytes = ByteImage.toArray(new Byte[ByteImage.size()]);
			
			bytes = new byte[Bytes.length];
			
			for(int i = 0; i < Bytes.length;i++){
				bytes[i] = Bytes[i].byteValue();
			}

			
			if(n != null){
				Log.d("ImageData", "Received Image ByteArraySize is" + bytes.length);
				Log.d("ImageData", "Get Image Node is " + n.getName());
				Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
				if(bm != null){
					n.setIcon(bm);
				}
			}
			
			
			ImageList.clear();
			bytes = null;
			
			//imageSYNを呼ぶ
			ImageSessionSYN.sendImageSYN();
		}
	}
	
	//セッションタイムアウト
	public static void ImageDataTimeOut(){
		Log.d("ImageData","imagedata is TimeOut");
		
		if(ImageList != null){
			ImageList.clear();
		}
		
		bytes = null;
		
		ImageSessionSYN.sendImageSYN();
	}
	
	//byte[]結合済み画像データ返却
	public static byte[] getImagebytes(){
		return bytes;
	}
	
	//パケット振り分け
	public static void setImagePacket(Packet p,List<Integer> imageBuf){
		//自分宛パケット受信
		if(p.getOriginalDestinationMac().equals(YottaConnector.MyNode.getMACAddr())){
			
			//自分宛0パケット受信
			if(imageBuf.size() == 0){
				ImageSession is = ImageSessionList.getSession(p);
				ImageSessionList.resetTimeOut(is);
				if(is != null){
					Log.d("ImageData","0 packet get");
					is.setStatus(0x02);
					Packet sendPacket = new Packet();
					sendPacket.setType(9);
					sendPacket.setOriginalSourceMac(YottaConnector.MyNode.getMACAddr());
					sendPacket.setOriginalDestinationMac(p.getOriginalSourceMac());
					sendPacket.setSourceMac(YottaConnector.MyNode.getMACAddr());
					sendPacket.setDestinationMac(p.getSourceMac());
					sendPacket.setTypeNum(p.getTypeNum());
					
					
					if(is.getFindMac().equals(YottaConnector.MyNode.getMACAddr())){
						SendImageData(YottaConnector.MyNode.getRadarIcon(), sendPacket);
						
						Log.d("ImageDataFind","Find mac is me");
					}else if((NodeList.getNode(is.getFindMac()) != null ) && (NodeList.getNode(is.getFindMac()).getRadarIcon() != null)){
						SendImageData(NodeList.getNode(is.getFindMac()).getRadarIcon(), sendPacket);
						
						Log.d("ImageDataFind","Find mac is " + NodeList.getNode(is.getFindMac()).getName());
					}else if(is.getFindMac() == null){
						Log.d("ImageData","ERR:find mac is null");
					}
					else{
						Log.d("ImageData","ERR:find mac is " + is.getFindMac());
					}
					
				}else{
					Log.d("ImageData","0 packet get.but ImageSession is null");
				}
			}
			
			//自分宛画像パケット受信
			else{
				ImageSession is = ImageSessionList.getSessionRe(p);
				ImageSessionList.resetTimeOut(is);
				if(is != null){
					Log.d("ImageData","image packet get");
					List<Integer> tempImageBuf = new ArrayList<Integer>(imageBuf);
					List<Character> CharacterImage = new ArrayList<Character>();
					
					for (Integer i : tempImageBuf) {
						CharacterImage.add((char)(i.intValue()));
					}
					
					char[] ca = new char[CharacterImage.size()];
					
					for(int i = 0;i < CharacterImage.size();i++){
						ca[i] = CharacterImage.get(i);
					}
					
					p.setImageArray(ca);
					List<String> data = p.putData();
					
					setImageData(p.getImageArray(), Integer.parseInt(data.get(0)), Integer.parseInt(data.get(1)),is.getFindMac());
				}
				else
				{
					Log.d("ImageData","image packet get.but ImageSession is null");
				}
			}
		}
		//中継
		else{
			//ホップリミットが0のときは中継しない
			if(p.getHopLimit() != 0){
				
				//中継0パケット受信
				if(imageBuf.size() == 0){
					ImageSession is = ImageSessionList.getSession(p);
					ImageSessionList.resetTimeOut(is);
					Log.d("ImageData","0 packet Relay");
					if(is != null && is.getStatus() == 0x01){
						is.setStatus(0x02);
						char[] kara = new char[2];
						kara[0] = 0xffd8;
						kara[1] = 0xffd9;
						p.setImageArray(kara);
						setRaleyPacket(p);
					}
				}
				
				//中継画像パケット受信
				else{
					ImageSession is = ImageSessionList.getSessionRe(p);
					ImageSessionList.resetTimeOut(is);
					Log.d("ImageData","image packet Relay");
					if(is != null && is.getStatus() == 0x02){
						p.setDestinationMac(is.getSourceMac());
						setRaleyPacket(p);	
					}
				}
			}
		}
	}
	
	//パケット中継
	private static void setRaleyPacket(Packet p){
		SendSocket ss =new SendSocket(YottaConnector.ip);
		
		p.setSourceMac(YottaConnector.MyNode.getMACAddr());
		ss.makeRaleyPacket(p);
	}
	
	//バッファリングデータ破棄
	public void deleteBufImageData(){
		ImageList = null;
	}
	
	//空パケットの送信
	public static void SynAckPacketSend(Packet packet){
		Log.d("ImageData","0 packet send");
		ImageSession is = ImageSessionList.getSessionRe(packet);
		if(is.getStatus() != 0x01){
			return;
		}
		is.setFindMac(is.getOriginalDestinationMac());
		is.setOriginalDestinationMac(packet.getOriginalSourceMac());
		
		packet.setType(Packet.ImageDATA);
		
		packet.exOriginalMac();
		
		packet.setSourceMac(YottaConnector.MyNode.getMACAddr());
		
		packet.setDestinationMac(Packet.broadCastMACaddr);
		
		packet.setHopLimit(Packet.HopLimitMax);
		
		packet.setData(null);
		
		char[] kara = new char[2];
		kara[0] = 0xffd8;
		kara[1] = 0xffd9;
		packet.setImageArray(kara);
		
		
		SendSocket ss = new SendSocket(YottaConnector.ip);
		ss.makeNewPacket(packet);
	}
	
	//新規画像データ送信
	public static void SendImageData(Bitmap sendBitmap,Packet p){
		Log.d("ImageData","image packet send");
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		sendBitmap.compress(CompressFormat.JPEG, 100, bos);
		byte[] imageArray = bos.toByteArray();
		
		Log.d("ImageData", "Original Image ByteArraySize is" + imageArray.length);
		List<char[]> tmpList = new ArrayList<char[]>();
	 	tmpList = isolationImageData(imageArray);
	 	
		char sChar = 0x0E;
		char eChar = 0x0F;
	 	
	 	
	 	for(int i = 0;i < tmpList.size();i++){
	 		try {
				Thread.sleep(5);
				p.setData(String.valueOf(sChar) + i + String.valueOf(eChar) + String.valueOf(sChar) +  tmpList.size() + String.valueOf(eChar));
				p.setImageArray(tmpList.get(i));
				SendSocket ss = new SendSocket(YottaConnector.ip);	
				ss.makeNewPacket(p);
			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}			 		
	 	}
	}
	
	private static List<char[]> isolationImageData(byte[] imageArryay){
		
		List<Character> tmpSplitList = new ArrayList<Character>();
		List<char[]> tmpList = new ArrayList<char[]>();
		int size = 500; //分割サイズ
		char[] ca;
		
		
		for(int i = 0;i < imageArryay.length;i++){
			if(i % size == 0 && i != 0){
				tmpSplitList.add(0, (char)0xffd8);
				tmpSplitList.add(tmpSplitList.size(), (char)0xffd9);
				ca = new char[tmpSplitList.size()];
				for(int j = 0; j < tmpSplitList.size();j++){
					ca[j] = tmpSplitList.get(j).charValue();
				}
				tmpList.add(ca);
				
				ca = null;
				tmpSplitList.clear();
			}
			tmpSplitList.add((char)(imageArryay[i] & 0xff));
		}
		tmpSplitList.add(0, (char)0xffd8);
		tmpSplitList.add(tmpSplitList.size(), (char)0xffd9);

		ca = new char[tmpSplitList.size()];
		for(int j = 0; j < tmpSplitList.size();j++){
			ca[j] = tmpSplitList.get(j).charValue();
		}

		tmpList.add(ca);
		
		
		return tmpList;	
		
	}	
}

//ソート用クラス
class ImageComparator implements java.util.Comparator {
	public int compare(Object s, Object t) {
		return ((SplitImage) s).piece - ((SplitImage) t).piece;
	}
}
