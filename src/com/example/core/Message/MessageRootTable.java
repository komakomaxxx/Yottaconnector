package com.example.core.Message;

import java.util.ArrayList;
import com.example.core.Packet;

public class MessageRootTable {
	
	static public ArrayList<MessageRoot> MessageRootTable;
	
	static{
		MessageRootTable = new ArrayList<MessageRoot>();
	}
	
	//引数のパケットに関連付けられるルートテーブルを作成する
	static public synchronized void addRoot(Packet packet,String forwardMac){
		removeRoot(getRoot(packet));
		
		MessageRootTable.add(new MessageRoot(packet,forwardMac));
	}
	
	//引数のパケットに関連付けられるルートテーブルを返却する
	//ルートテーブルが存在しない場合nullを返す
	static public synchronized MessageRoot getRoot(Packet packet){
		
		MessageRoot findRoot =  new MessageRoot(packet);
		
		return findList(findRoot);
	}
	
	//引数のマックアドレスに関連付けられるルートテーブルを返却する
	//ルートテーブルが存在しない場合nullを返す
	static public synchronized MessageRoot getRoot(String sourceMac,String destMack){
		
		MessageRoot findRoot =  new MessageRoot(sourceMac,destMack);
	
		return findList(findRoot);
	}
	
	//引数のMessageRootをリストから検索する
	//リストにあればMessageRootを、なければnullを返す
	static private synchronized MessageRoot findList(MessageRoot findRoot){
		int index;
		
		index = MessageRootTable.lastIndexOf(findRoot);
		
		if(index < 0){
			return null;
		}
		
		return MessageRootTable.get(index);
	}
	
	
	//引数のパケットに関連付けられるルートテーブルのタイムアウトをリセットする
	//ルートテーブルが存在しない場合falseを返す
	static public synchronized MessageRoot resetTimeOut(Packet packet){
		MessageRoot tmp = getRoot(packet);
		
		if(tmp == null){
			return null;
		}
		
		tmp.timerReStart();
		return tmp;
	}

	//引数のルートを削除する
	static public synchronized void removeRoot(MessageRoot root){
		if(root != null){
			root.timerClear();
			MessageRootTable.remove(root);
		}
	}
}