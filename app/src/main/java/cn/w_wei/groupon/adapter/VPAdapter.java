package cn.w_wei.groupon.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class VPAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments = new ArrayList<>();
    public VPAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(List<Fragment> list,boolean isClear){
        if(isClear){
            fragments.clear();
        }
        fragments.addAll(0,list);
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int i) {
        return fragments.get(i);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
