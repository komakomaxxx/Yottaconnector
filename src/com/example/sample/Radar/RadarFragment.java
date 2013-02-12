package com.example.sample.Radar;

import com.example.sample.header.HeaderFragment;
import com.example.sample.user.UserFragment;
import com.example.yottaconnecter.Node;
import com.example.yottaconnecter.R;
import com.example.yottaconnecter.YottaConnector;

import android.support.v4.app.Fragment;
import android.hardware.SensorEvent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RadarFragment extends Fragment  implements RadarTouch{

	private View RadarLayoutView;
	private RadarView radarView;
	private float[] sensorValues = new float[3];
	
	
	public RadarFragment() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public View onCreateView(
		LayoutInflater inflater, 
		ViewGroup container, 
		Bundle savedInstanceState) {
		
		
		RadarLayoutView = inflater.inflate(R.layout.radar_fragment, container, false);
		
		radarView = (RadarView) RadarLayoutView.findViewById(R.id.RadarView);
		return RadarLayoutView;
	}

	@Override
	public void onResume() {
		super.onResume();
		int state = YottaConnector.mPager.getCurrentItem() % 4;
		switch (state) {
		case 0:
			HeaderFragment.setFragmentName("Rader");
			break;
		case 1:
			HeaderFragment.setFragmentName("TimeLine");
			break;
		case 2:
			HeaderFragment.setFragmentName("FriendList");
			break;
		case 3:
			HeaderFragment.setFragmentName("NodeList");
			break;
		}
	}	
	
	public void setSensorVales(SensorEvent event,boolean flag) {
		// TODO 自動生成されたメソッド・スタブ
		sensorValues[0] = (event.values[0]);
		sensorValues[1] = (event.values[1]);
		sensorValues[2] = (event.values[2]);
		if(radarView != null){
			radarView.drawScreen(sensorValues,flag,this);
		}
	}

	@Override
	public void onRadarTouchNodeEvent(Node n) {
		// TODO 自動生成されたメソッド・スタブ
    	UserFragment.Builder bl = new UserFragment.Builder();
    	bl.setNode(n);
    	bl.create().show(getFragmentManager(), getTag());
	}

	@Override
	public void onRadarTouchNodeEvent(String str) {
		// TODO 自動生成されたメソッド・スタブ
	}

}