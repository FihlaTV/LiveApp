package com.hnust.liveapp.ui.activitys;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.hnust.liveapp.R;
import com.hnust.liveapp.contract.MainContract;
import com.hnust.liveapp.ui.adapter.ViewPagerAdapter;
import com.hnust.liveapp.ui.fragments.CateFragment;
import com.hnust.liveapp.ui.fragments.FocusFragment;
import com.hnust.liveapp.ui.fragments.LiveFragment;
import com.hnust.liveapp.ui.fragments.MainFragment;
import com.hnust.liveapp.ui.fragments.UserFragment;
import com.hnust.liveapp.util.BottomNavigationViewHelper;
import com.hnust.liveapp.widget.CustomViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yonglong on 2017/03/27
 */

public class MainView extends AppCompatActivity implements MainContract.View {


    @BindView(R.id.navigation)
    BottomNavigationView mBottomNav;

    @BindView(R.id.viewpager)
    CustomViewPager viewPager;

    private MenuItem menuItem;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_live:
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_focus:
                    viewPager.setCurrentItem(2);
                    return true;
                case R.id.navigation_my:
                    viewPager.setCurrentItem(3);
                    return true;
            }
            return false;
        }

    };


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mBottomNav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.disableShiftMode(mBottomNav);

//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                if (menuItem != null) {
//                    menuItem.setChecked(false);
//                } else {
//                    mBottomNav.getMenu().getItem(0).setChecked(false);
//                }
//                mBottomNav.getMenu().getItem(position).setChecked(true);
//                menuItem = mBottomNav.getMenu().getItem(position);
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
//


        //禁止ViewPager滑动
//        viewPager.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return false;
//            }
//
//        });
        setupViewPager(viewPager);
    }


    /**
     * 设置一个时间变量记录第一次点击的时间
     * 当第二次点击返回键时比较记录的时间和当前的时间
     * 当两者时差在2000ms一类则退出
     */

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setupViewPager(ViewPager viewPager) {
        //页面适配器
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(MainFragment.newInstance("首页"));
        adapter.addFragment(CateFragment.newInstance("直播"));
        adapter.addFragment(FocusFragment.newInstance("关注"));
        adapter.addFragment(UserFragment.newInstance("我的"));
        viewPager.setAdapter(adapter);
    }


}