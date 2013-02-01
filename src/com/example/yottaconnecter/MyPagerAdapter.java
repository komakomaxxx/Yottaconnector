package com.example.yottaconnecter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.sample.Radar.RadarFragment;
import com.example.sample.friendlist.FriendListFragment;
import com.example.sample.nodelist.NodeListFragment;
import com.example.sample.timeline.TimelineFragment;

public class MyPagerAdapter extends FragmentStatePagerAdapter {
	 /** �y�[�W�� */
    private static final int PAGE_SIZE = 4;
    public static final int LIMIT_SIZE = 1000;
    
    private static final int FRAGMENT1 = 0;
    private static final int FRAGMENT2 = 1;
    private static final int FRAGMENT3 = 2;
    private static final int FRAGMENT4 = 3;

	private Fragment ft1;
	private Fragment ft2;
	private Fragment ft3;
	private Fragment ft4;
	private TimelineFragment tlf;
	private FriendListFragment flf;
	private RadarFragment rf;

	public MyPagerAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
		rf = new RadarFragment();
		tlf = new TimelineFragment();
		flf = new FriendListFragment();
		ft1 = (Fragment)rf;
		ft2 = (Fragment)tlf;
		ft3 = (Fragment)flf;
		ft4 = new NodeListFragment();
	}
	public int getLimitSize(){
		return LIMIT_SIZE;
	}
	
	@Override
	public Fragment getItem(int pos) {
		// TODO Auto-generated method stub
		Fragment fragment = null;
        //�y�[�W���Ƃɂ��ꂼ��̉�ʂ��쐬
		int page = pos % PAGE_SIZE;
        switch(page){
            
            case FRAGMENT1:
            	fragment = ft1;	
                break;
           
            case FRAGMENT2:
                fragment = ft2;	
                break;
            
            case FRAGMENT3:
                fragment = ft3;	
                break;
       
            case FRAGMENT4:
                fragment = ft4;	
                break;
            default:
                fragment = null;
        }
        return fragment;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return LIMIT_SIZE;
	}
	
	public Fragment getRadarFragment(){
		return rf;
	}
}