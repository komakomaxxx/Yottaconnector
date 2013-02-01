package com.example.sample;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingGetData {

	public SettingGetData(String data) {
		// TODO 自動生成されたコンストラクター・スタブ
		String str = data;
		String statusstr = data.substring(0, 1);
		int status = Integer.parseInt(statusstr);
		if(status == 0){
			nodeSet(str);
		}else if(status == 1){
			yossipSet(str);
		}
	}
	
	private void nodeSet(String data){
		List<String> element = new ArrayList<String>();
		String regex = "\\(.+?\\)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(data);
		
		while(matcher.find()){
			String temp = matcher.group();
			int index = temp.indexOf(")");
			element.add(temp.substring(1, index));
		}
		
		Node node = new Node(element.get(0), element.get(1), Double.parseDouble(element.get(3)), Double.parseDouble(element.get(4)), null, element.get(2));
		node.setNodeDirection(MainActivity.MyNode.getIdo(),MainActivity.MyNode.getKeido());
		NodeList.nodelist.add(node);
	}
	
	private void yossipSet(String data){
		System.out.println(data);
		List<String> element = new ArrayList<String>();
		String regex = "\\(.+?\\)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(data);
		
		while(matcher.find()){
			String temp = matcher.group();
			int index = temp.indexOf(")");
			element.add(temp.substring(1, index));
		}
		DateFormat format = new SimpleDateFormat("yyyy/MM/dd"); 
		Date date = null;
		try {
			date = format.parse(element.get(1));
		} catch (ParseException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		//Yossip yossip = new Yossip(element.get(0),date	, element.get(2), null);
		Yossip yossip = new Yossip(element.get(0), date, element.get(3), element.get(2),null);
		YossipList.y_list.add(yossip);
	}

}
