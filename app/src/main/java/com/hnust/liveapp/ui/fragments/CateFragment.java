package com.hnust.liveapp.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hnust.liveapp.R;
import com.hnust.liveapp.bean.CateInfo;
import com.hnust.liveapp.contract.CateContract;
import com.hnust.liveapp.presenter.CatePresenter;
import com.hnust.liveapp.ui.adapter.PagerAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yonglong on 2017/3/28.
 */

public class CateFragment extends Fragment implements CateContract.View {

    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.tv_msg)
    TextView tv_msg;
    @BindView(R.id.btn_reload)
    Button btn_reload;
    CatePresenter catePresenter;

    @OnClick(R.id.btn_reload)
    void reload() {
        catePresenter.getTypesInfo();
    }

    public static CateFragment newInstance(String title) {

        Bundle args = new Bundle();
        args.putString("title", title);
        CateFragment fragment = new CateFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cate, container, false);
        ButterKnife.bind(this, view);
        catePresenter = new CatePresenter(this, getContext());
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        catePresenter.getTypesInfo();
    }


    @Override
    public void returnError(String msg) {
        tabLayout.setVisibility(View.GONE);
        tv_msg.setVisibility(View.VISIBLE);
        btn_reload.setVisibility(View.VISIBLE);
        tv_msg.setText(msg);
    }

    @Override
    public void returnTypeData(List<CateInfo> types) {
        //显示TAB控件
        tabLayout.setVisibility(View.VISIBLE);
        tv_msg.setVisibility(View.GONE);
        btn_reload.setVisibility(View.GONE);
        for (int i = 0; i < types.size(); i++) {
            tabLayout.addTab(tabLayout.newTab().setText(types.get(i).getType()));
        }
        //设置模式
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        //设置viewPager的适配器、关联viewpager和tablayout
        final PagerAdapter adapter = new PagerAdapter(getChildFragmentManager(), types);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
