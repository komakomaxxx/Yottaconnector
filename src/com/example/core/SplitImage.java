package com.example.core;

public class SplitImage {
	public int piece;
	public char[] ArrayImage;
	
	public SplitImage(int piece,char[] ArrayImage) {
		// TODO 自動生成されたコンストラクター・スタブ
		this.piece =piece;
		this.ArrayImage = ArrayImage.clone();
	}

}
