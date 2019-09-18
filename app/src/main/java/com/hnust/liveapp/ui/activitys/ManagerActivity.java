package com.hnust.liveapp.ui.activitys;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.widget.Toast;

import com.hnust.liveapp.R;
import com.hnust.liveapp.bean.RoomInfo;
import com.hnust.liveapp.contract.ManagerContract;
import com.hnust.liveapp.presenter.ManagerPresenter;
import com.hnust.liveapp.ui.adapter.RecyclerViewAdapter;
import com.hnust.liveapp.util.GlobalConfig;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yonglong on 2017/5/27.
 */

public class ManagerActivity extends AppCompatActivity implements ManagerContract.View {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.rv_history)
    PullLoadMoreRecyclerView mRecyclerView;

    /**
     * Android群英传:神兵利器
     * 数据相关
     */
    List<RoomInfo> roomInfos = new ArrayList<>();
    RecyclerViewAdapter recyclerViewAdapter;

    ManagerPresenter historyPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);
        mToolbar.setTitle("禁播列表");
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        historyPresenter = new ManagerPresenter(this, this);
        historyPresenter.getHistory();


        mRecyclerView.setGridLayout(2);//参数为列数
        recyclerViewAdapter = new RecyclerViewAdapter(this, roomInfos, GlobalConfig.ROOMS_HISTORY);
        mRecyclerView.setAdapter(recyclerViewAdapter);
        mRecyclerView.setPushRefreshEnable(false);
        mRecyclerView.setPullRefreshEnable(false);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }


    @Override
    public void updateView() {
        recyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void returnError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void returnLivesListData(List<RoomInfo> lives) {
        if (lives == null) {
            roomInfos.clear();
        } else {
            roomInfos = lives;
        }
        recyclerViewAdapter.setLiveInfos(roomInfos);
        recyclerViewAdapter.notifyDataSetChanged();
    }
}
