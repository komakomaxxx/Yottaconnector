package com.example.sample;

import com.example.yottaconnecter.*;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;

public class FileFragment extends Fragment  {
	public FileFragment() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public View onCreateView(
		LayoutInflater inflater, 
		ViewGroup container, 
		Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.file_fragment ,container, false);
	}

	public void onMyListenerEvent() {
		// TODO Auto-generated method stub
		
	}

}