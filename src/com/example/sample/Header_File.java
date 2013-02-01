package com.example.sample;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.sample.R;
import com.example.sample.R.drawable;
import com.example.sample.R.id;
import com.example.sample.R.layout;



import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;


import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class Header_File extends Fragment {

	Header_File my;
	
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
		
		my=this;
		return inflater.inflate(R.layout.header_file, container, false);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		getActivity().findViewById(R.id.fileheader).setOnClickListener(new OnClickListener() {		
			public void onClick(View v) {	
			
				FragmentTransaction tran = getActivity().getSupportFragmentManager().beginTransaction();
				tran.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
				tran.remove(my);
				tran.commit();
			}
		});
	}
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		setFileList();
	}
	private void setFileList() {
		ArrayList<HashMap<String,Object>> outputArray = new ArrayList<HashMap<String,Object>>();
		 
        for( int i = 1; i <= 10; i++ ) {
        	HashMap<String, Object> item = new HashMap<String, Object>();
        	// �摜�̐ݒ�i�Ƃ肠�����S�Ă̍��ڂɓ����摜����Ă��܂��B�j
        	item.put("iconKey", R.drawable.ic_launcher);
        	// ������̐ݒ�i�Ƃ肠�����A���[�v�̃J�E���^��\�������Ă��܂��B�j
        	item.put("nameKey", i + "�Ԗ�name");
        	item.put("profileKey", i + "�Ԗ�file��");
        	// �\���p��ArrayList�ɐݒ�
        	outputArray.add(item);
        }
 
        // �摜�\���p�ɍ쐬����CustomAdapter�ɁA��LArrayList��ݒ�
        SimpleAdapter myAdapter = new SimpleAdapter(
               getActivity(),
               outputArray,
               R.layout.row, 
               new String[]{"iconKey","nameKey","profileKey"},
               new int[]{R.id.userIcon,R.id.userName,R.id.userprofile}
        );
        ListView listView = (ListView)getActivity().findViewById(R.id.filelist);
        if(listView != null){
        	listView.setAdapter(myAdapter);
        }
	}
	}