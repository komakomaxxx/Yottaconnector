package com.example.yottaconnecter;

import java.util.ArrayList;
import java.util.List;

public class YossipList {
	public static List<Yossip> y_list;

	public YossipList() {
		// TODO 自動生成されたコンストラクター・スタブ
		y_list = new ArrayList<Yossip>();
	}
	public synchronized static void addYossip(Yossip y) {
		for(Yossip yossip: y_list) {
			if(yossip.getYossipText().equals(y.getYossipText())
					&& yossip.getYossipUserMac().equals(y.getYossipUserMac())
						&& yossip.getYossipTime().equals(y.getYossipTime()))
				return;
		}
		y_list.add(y);
	}
}
