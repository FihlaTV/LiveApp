package com.hnust.liveapp.ui.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.hnust.liveapp.bean.CateInfo;
import com.hnust.liveapp.ui.fragments.LiveFragment;

import java.util.List;

/**
 * Created by yonglong on 2017/4/25.
 */

public class PagerAdapter extends FragmentPagerAdapter {

    List<CateInfo> cateInfoList;

    public PagerAdapter(FragmentManager fm, List<CateInfo> cateInfoList) {
        super(fm);
        this.cateInfoList = cateInfoList;
    }

    @Override
    public int getCount() {
        return cateInfoList.size();
    }

    @Override
    public Fragment getItem(int position) {
        return LiveFragment.newInstance(cateInfoList.get(position));
    }
}