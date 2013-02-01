package com.example.sample;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;
import com.example.sample.Radar.RadarFragment;
import com.example.sample.friendlist.FriendListManager;
import com.example.sample.timeline.TimelineFragment;



public class MainActivity extends FragmentActivity implements SensorEventListener,LocationListener,SocketListener {

	private RadarFragment rf;
	private SensorManager sensorManager;
	private Sensor orientation;
	private LocationManager manager;
	private NodeList nl;
	private boolean view_flag = false;
	
	public static Node MyNode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Actionbar��\��
        requestWindowFeature(Window.FEATURE_NO_TITLE);
      	setContentView(R.layout.main_activity);

      	
      	/*
      	//FragmentTransaction tran = getSupportFragmentManager().beginTransaction();
      	//Fragment frag = new Fragment1();
      	//fragment_main.xml��framelayout��Ƀt���O�����g��z�u(������)
      	//tran.add(R.id.pager,frag);
      	//tran.commit();
      	*/
      	MyPagerAdapter mAdapter = new MyPagerAdapter(getSupportFragmentManager());
		ViewPager mPager = (ViewPager)findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);
		mPager.setCurrentItem(MyPagerAdapter.LIMIT_SIZE/2);

		new YossipList();

		//静的な仮ノードリストの作成
		nl = new NodeList();
		//nl.testMakeNodeList();
		//testNodeCreate(nl);
		SocketListenerNotify sln = new SocketListenerNotify(this);
		
		rf = (RadarFragment) mAdapter.getRadarFragment();
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> list;
		list = sensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
		if(list.isEmpty()== true){
			Toast.makeText(this,"Empty SensorList", Toast.LENGTH_SHORT).show();
		}
		else{
			orientation = list.get(0);
			manager = (LocationManager)getSystemService(LOCATION_SERVICE);
		}
		
        // フレンドリスト読み込み
        FriendListManager.onLoadFriendList(this);
        getMyNodeData();
    }

    @Override
	protected void onResume() {
		super.onResume();

		if (orientation != null) {
			sensorManager.registerListener(this, orientation,
					SensorManager.SENSOR_DELAY_NORMAL);
		}
		if(manager != null) {
		    //manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
			manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		}
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	FriendListManager.onFriendListIntoDatabase();
    }

	public void onLocationChanged(Location location) {
		// TODO 自動生成されたメソッド・スタブ
		
		double ido = location.getLatitude();
		double keido = location.getLongitude();
		MyNode.setIdo(ido);
		MyNode.setKeido(keido);
		for (Node nodedata : NodeList.nodelist) {
			nodedata.setNodeDirection(ido,keido);
			//nodedata.setNodeDirection(35.624937, 139.341917);
		}
		view_flag = true;
	}

	public void onProviderDisabled(String provider) {
		// TODO 自動生成されたメソッド・スタブ

	}

	public void onProviderEnabled(String provider) {
		// TODO 自動生成されたメソッド・スタブ

	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO 自動生成されたメソッド・スタブ

	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO 自動生成されたメソッド・スタブ

	}

	public void onSensorChanged(SensorEvent event) {
		// TODO 自動生成されたメソッド・スタブ
		if (event.sensor == orientation) {
			rf.setSensorVales(event,view_flag);
		}
	}

	private void testNodeCreate(NodeList nl){
		
		FileInputStream fis;
		Bitmap icon = null;
		try {
			//fis = new FileInputStream("data/data/" + getPackageName() + "/MyData/" + "icon.jpg");
			fis = new FileInputStream("/sdcard/Download/icon.jpg");
			icon = BitmapFactory.decodeStream(fis);
			fis.close();
		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		
		nl.testMakeNodeList(35.624937, 139.341917, "研究棟B",icon);
		nl.testMakeNodeList(35.624820, 139.342622, "講義棟D",icon);
		nl.testMakeNodeList(35.625125, 139.342086, "朝の調べ",icon);
		nl.testMakeNodeList(35.624671, 139.342483, "階段",icon);
		                                                                                                                                                                                                                                                    
	}

	public void onSocketListener(String s) {
		// TODO 自動生成されたメソッド・スタブ
		new SettingGetData(s);
	}
	
	private void getMyNodeData(){
		
		String path = "data/data/" + getPackageName() + "/MyData/";
		String data[] = new String[2];
		
		try {
			XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
			fac.setNamespaceAware(true);
			XmlPullParser xpp = fac.newPullParser();
			File file = new File(path + "UserData.xml");
			FileInputStream fis = new FileInputStream(file);
			
			xpp.setInput(fis,null);
			
			int i = 0;
		    
		   int eventType = xpp.getEventType();
		   while (eventType != XmlPullParser.END_DOCUMENT) {
			   if (eventType == XmlPullParser.START_DOCUMENT) {
				   Log.d("ReadXML", "START");
			   } else if (eventType == XmlPullParser.START_TAG) {
				   Log.d("ReadXML","Start tag " + xpp.getName());
			   } else if (eventType == XmlPullParser.END_TAG) {
				   Log.d("ReadXML","End tag " + xpp.getName());
			   } else if (eventType == XmlPullParser.TEXT) {
				   Log.d("ReadXML","Text " + xpp.getText());
				   data[i] = xpp.getText();
				   Log.d("MyData","text = " + data[i]);
			   }
			   eventType = xpp.next();
		   }
		} catch (XmlPullParserException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		String myMACaddrString = new String();
		WifiManager wfm = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		WifiInfo wfi = wfm.getConnectionInfo();
		String[] myMACaddr = wfi.getMacAddress().split(":");
		int[] imyMACaddr = new int[myMACaddr.length];
		for (int i = 0;i < myMACaddr.length;i++) {
			myMACaddrString += myMACaddr[i];
			imyMACaddr[i] = Integer.parseInt(myMACaddr[i], 16);
		}
		
		FileInputStream fis;
		Bitmap icon = null;
		try {
			fis = new FileInputStream(path + "icon.jpg");
			icon = BitmapFactory.decodeStream(fis);
			fis.close();
		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		
		MyNode = new Node(myMACaddrString, data[0], 0, 0, icon, data[1]);
		
	}
}
