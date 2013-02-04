package com.example.yottaconnecter;

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

import android.R.string;
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

import com.example.core.Socket_listen;
import com.example.core.Hello.Hello;
import com.example.sample.Radar.RadarFragment;
import com.example.sample.friendlist.FriendListManager;
import com.example.sample.timeline.TimelineFragment;
import com.example.yottaconnecter.R;



public class YottaConnector extends FragmentActivity implements SensorEventListener,LocationListener{

	private RadarFragment rf;
	private SensorManager sensorManager;
	private Sensor orientation;
	private LocationManager manager;
	private NodeList nl;
	private boolean view_flag = false;
	
	public static Node MyNode;
	public static String ip = "192.168.0.101";
	public static ViewPager mPager;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
      	setContentView(R.layout.main_activity);



      	MyPagerAdapter mAdapter = new MyPagerAdapter(getSupportFragmentManager());
      	mPager = (ViewPager)findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);
		mPager.setCurrentItem(MyPagerAdapter.LIMIT_SIZE/2);

		new YossipList();

		//静的な仮ノードリストの作成
		nl = new NodeList();
		//nl.testMakeNodeList();
		//testNodeCreate(nl);
		
		new Socket_listen(ip);
		
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

      	//Helloの定期送信
      	Hello.startSendHello(10000);
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
		Bitmap[] icon = new Bitmap[6];
		
		icon[0] = BitmapFactory.decodeResource(getResources(), R.drawable.t0);
		icon[1] = BitmapFactory.decodeResource(getResources(), R.drawable.t1);
		icon[2] = BitmapFactory.decodeResource(getResources(), R.drawable.t2);
		icon[3] = BitmapFactory.decodeResource(getResources(), R.drawable.t3);
		icon[4] = BitmapFactory.decodeResource(getResources(), R.drawable.t4);
		icon[5] = BitmapFactory.decodeResource(getResources(), R.drawable.t5);
		
		nl.testMakeNodeList(35.625219, 139.341520, "東郷 茂朗",icon[2],"ディズニーランド好きです！");
		nl.testMakeNodeList(35.624870, 139.341257, "花輪 龍之介",icon[3],"山梨出身です！");
		nl.testMakeNodeList(35.624569, 139.341740, "長谷川 和樹",icon[4],"今日はチェック柄～");
		nl.testMakeNodeList(35.624626, 139.342631, "小林 凌",icon[5],"髪染めました！");
		nl.testMakeNodeList(35.625144, 139.343269, "大滝 みや子",icon[1],"私も山梨～");
		nl.testMakeNodeList(35.625986,139.342695, "駒井 覚",icon[0],"今日は研究の発表会");		                                                                                                                                                                                                                                                    
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
				   i++;
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
