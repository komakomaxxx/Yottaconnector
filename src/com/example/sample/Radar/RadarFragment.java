package com.example.sample.Radar;

import com.example.sample.R;
import android.support.v4.app.Fragment;
import android.hardware.SensorEvent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RadarFragment extends Fragment  {

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
	
	public void setSensorVales(SensorEvent event,boolean flag) {
		// TODO 自動生成されたメソッド・スタブ
		sensorValues[0] = (event.values[0]);
		sensorValues[1] = (event.values[1]);
		sensorValues[2] = (event.values[2]);
		if(radarView != null){
			radarView.drawScreen(sensorValues,flag);
		}
	}

}