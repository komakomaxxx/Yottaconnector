package com.example.yottaconnecter;

import java.io.ByteArrayOutputStream;


import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Node implements Parcelable {
	private String MACAddr;
	private String Name;
	public double ido;
	public double keido;
	private Bitmap Icon;
	private Bitmap RadarIcon;
	private String profile;
	private double azim;
	private double dist;
	private double erth_r = 6378.137;
	private boolean GPSflag;
	
	public Node(String mac){
		this.MACAddr = mac;
	}

	public Node(String MACAddr, String Name, double ido, double keido,
			Bitmap Icon, String profile) {
		// TODO 自動生成されたコンストラクター・スタブ
		this.MACAddr = MACAddr;
		this.Name = Name;
		this.ido = ido;
		this.keido = keido;
		this.Icon = Icon;
		this.profile = profile;
		
		if(Icon != null){
			int size = 100;
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			Icon.compress(CompressFormat.JPEG, 100, bos);
			byte[] imageArray = bos.toByteArray();
			
			Bitmap _bm = makeBitmap(0, imageArray, size, size);
			
			int w = _bm.getWidth();
	        int h = _bm.getHeight();
	        float scale = Math.min((float) size / w, (float) size / h);
	        Matrix matrix = new Matrix();
	        
	        matrix.postScale(scale, scale);
	        this.RadarIcon = Bitmap.createBitmap(_bm, 0, 0, w, h, matrix, true);
		}
	}

	public Node(Parcel source) {
		this(source.readString(), source.readString(), source.readDouble(),source.readDouble(),
				(Bitmap) source.readParcelable(Bitmap.class.getClassLoader()), source.readString());
	}

	public Node getNode() {
		return this;
	}

	public String getMACAddr() {
		return this.MACAddr;
	}

	public String getName() {
		return this.Name;
	}

	public double getIdo() {
		return this.ido;
	}

	public double getKeido() {
		return this.keido;
	}

	public Bitmap getIcon() {
		return this.Icon;
	}

	public String getProfile() {
		return this.profile;
	}

	public double getAzim() {
		return azim;
	}

	public double getDist() {
		return dist;
	}
	
	public Bitmap getRadarIcon() {
		return this.RadarIcon;
	}
	
	public void setIdo(double ido){
		this.ido = ido;
	}
	
	public void setKeido(double keido){
		this.keido = keido;
	}
	
	public void setName(String name){
		this.Name = name;
	}
	
	public void setProfile(String profile){
		this.profile = profile;
	}
	public void nodeUpdate(String name, double ido, double keido,String profile ) {
		this.Name = name;
		this.profile = profile;
		this.ido = ido;
		this.keido = keido;
	}
	

	public void setNodeDirection(double cido, double ckeido) {
		// TODO 自動生成されたコンストラクター・スタブ
		double idosa = (cido - ido) / 180 * Math.PI;

		double keidosa = (ckeido - keido) / 180 * Math.PI;

		double n_dist = erth_r * idosa;
		double t_dist = Math.cos(cido / 180 * Math.PI) * erth_r * keidosa;

		double d = Math.sqrt(Math.pow(n_dist, 2) + Math.pow(t_dist, 2));
		dist = d * 2500; // テスト用に距離を短縮
		azim = getDirection(cido, ckeido, ido, keido);
		
		Log.d("Node" + this.Name, "azim = " + azim + "  dist=" + dist + "  keido=" + this.keido);
	}

	private double getDirection(double lat1, double lng1, double lat2,
			double lng2) {
		double y = Math.cos(lng2 * Math.PI / 180)
				* Math.sin(lat2 * Math.PI / 180 - lat1 * Math.PI / 180);
		double x = Math.cos(lng1 * Math.PI / 180)
				* Math.sin(lng2 * Math.PI / 180)
				- Math.sin(lng1 * Math.PI / 180)
				* Math.cos(lng2 * Math.PI / 180)
				* Math.cos(lat2 * Math.PI / 180 - lat1 * Math.PI / 180);
		double direct = 180 * Math.atan2(y, x) / Math.PI;

		if (direct < 0) {
			direct = direct + 360;
		}

		return (direct) % 360;
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dist, int flags) {
		dist.writeString(getMACAddr());
		dist.writeString(getName());
		dist.writeDouble(getIdo());
		dist.writeDouble(getKeido());
		dist.writeParcelable(getIcon(), flags);
		// dist.writeByteArray(bitmapToByteArray(getIcon()));
		dist.writeString(getProfile());
		dist.writeDouble(getAzim());
		dist.writeDouble(getDist());
	}

	public static final Parcelable.Creator<Node> CREATOR = new Parcelable.Creator<Node>() {
		public Node createFromParcel(Parcel source) {
			return new Node(source);
		}

		public Node[] newArray(int size) {
			return new Node[size];
		}
	};
	
    public Bitmap makeBitmap(int wDpi, byte[] imageArray,
            int MaxWidth, int MaxHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        
        BitmapFactory.decodeByteArray(imageArray, 0, imageArray.length, options);
        
        int _oDpi = options.inDensity;
        float scale = (float) _oDpi / wDpi;
        options.inJustDecodeBounds = false;
        float max = Math.max((float) options.outWidth * scale / MaxWidth,
                (float) options.outHeight * scale / MaxHeight);
        Log.e("log_w", Float.toString(max));
        max = (float) Math.floor(max);
        if (max > 1) {
            options.inSampleSize = (int) max;
        } else if (max < 1) {
            // Log.e("makeBitmap", "low Size");
        }
        options.inPurgeable = true;
        Bitmap _bm = BitmapFactory.decodeByteArray(imageArray, 0, imageArray.length, options);
        return _bm;
    }
    @Override
	public boolean equals(Object n) {
		String nMacaddr = ((Node)n).getMACAddr();
		if (MACAddr.equals(nMacaddr)){ 
			return true;
		}else{
			return false;
		}
	}
}
