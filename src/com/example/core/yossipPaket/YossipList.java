package yossipPaket;

import java.util.ArrayList;
import java.util.List;

public class YossipList {
	public static List<Yossip> y_list =  new ArrayList<Yossip>();;

	public YossipList() {
		// TODO 自動生成されたコンストラクター・スタブ
		y_list = new ArrayList<Yossip>();
	}
	public synchronized static void addYossip(Yossip y) {
		y_list.add(y);		
	}
}
