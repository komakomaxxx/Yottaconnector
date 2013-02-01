package com.example.yottaconnecter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.example.yottaconnecter.Node;
import com.example.yottaconnecter.R;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.util.Log;

public class NodeList {
	public static List<Node> nodelist;
	public static List<Node> nearnodelist;
	final static int NODE_MAX_SIZE =10000;

	static {
		nodelist = new ArrayList<Node>(NODE_MAX_SIZE);
		nearnodelist = new ArrayList<Node>(NODE_MAX_SIZE);
	}
	public synchronized static void updateNodeList(ArrayList<Node> newNodeList) {
		
		List<Node> removelist = new ArrayList<Node>(nodelist);
		//差分を削除する(消えたNodeの削除)
		removelist.removeAll(newNodeList);
		nodelist.removeAll(removelist);
		//差分の追加(増えたノードの追加)
		newNodeList.removeAll(nodelist);
		nodelist.addAll(newNodeList);
	}
	public synchronized static void updateNearNodeList(ArrayList<Node> newNearNodeList) {
		List<Node> removelist = new ArrayList<Node>(nearnodelist);
		//差分を削除する(消えたNodeの削除)
		removelist.removeAll(newNearNodeList);
		nodelist.removeAll(removelist);
		nodelist.addAll(newNearNodeList);
		nearnodelist = new ArrayList<Node>(newNearNodeList);
	
	}
	public synchronized static Node getNode(String searchMac ) {
		int i;
		 
		i = nodelist.indexOf(new Node(searchMac));
		
		if(i == -1){
			return null;
		}else{
			return nodelist.get(i);
		}
	}
	
	public synchronized static Node getNoImageNode(){
		int i = 0;
		int LIST_MAX = nodelist.size();
		Node node = null;
		
		while(i < LIST_MAX){
			node = nodelist.get(i);
			if(node.getIcon() == null){
				break;
			}
			++i;
		}
		
		if(i == LIST_MAX){
			node = null;
		}
		
		return node;
	}
	
	public synchronized static void addNode(Node n) {
		nodelist.add(n);
	}
	
	/**
	 * マックアドレスに対応するユーザアイコンを取得する
	 * ない場合はデフォルトの画像にする
	 * 
	 * @param parent ビューグループ
	 * @param mac アイコンを取得したいマックアドレス
	 * @return
	 */
	public static Bitmap searchIcon(Resources resource, String mac) {
		Bitmap bmp = BitmapFactory.decodeResource(resource, R.drawable.ic_launcher);
		for(Iterator<Node> it = NodeList.nodelist.iterator(); it.hasNext(); ) {
			Node node = it.next();
			if(mac.equals(node.getMACAddr())) {
				bmp = node.getIcon();
			}
		}
		return bmp;
	}
	
	/**
	 * マックアドレスに対応するユーザIDを取得する
	 * ない場合はUserIdとする
	 * 
	 * @param mac IDを取得したいノードのマックアドレス
	 * @return　UserID
	 */
	public static String searchId(String mac) {
		String str = "UserId";
		for(Iterator<Node> it = NodeList.nodelist.iterator(); it.hasNext(); ) {
			String id = it.next().getMACAddr();
			if(mac.equals(id)){
				str = id;
				break;
			}
		}
		return str;
	}	
	public void testMakeNodeList(){

		for(int i=0;i < 100;i++){
			Node d =new Node("Mac" + i,
					i + "NODE",
					i*10,
					i*20,
					Bitmap.createBitmap(30, 30, Config.RGB_565),
					"Profile" + i
					);
			nodelist.add(d);
		}
	}

	public void testMakeNodeList(double ido,double keido,String name){
		nodelist.add(new Node("Mac" + name,
				name,
				ido,
				keido,
				Bitmap.createBitmap(30, 30, Config.RGB_565),
				"Profile" + name
				));
	}
	
	public void testMakeNodeList(double ido,double keido,String name,Bitmap icon){
		nodelist.add(new Node("Mac" + name,
				name,
				ido,
				keido,
				icon,
				name + "です！"
				));
	}
}