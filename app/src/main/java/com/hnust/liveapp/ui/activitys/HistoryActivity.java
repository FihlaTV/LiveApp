package com.hnust.liveapp.ui.activitys;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.hnust.liveapp.R;
import com.hnust.liveapp.bean.RoomInfo;
import com.hnust.liveapp.contract.HistoryContract;
import com.hnust.liveapp.db.DBService;
import com.hnust.liveapp.presenter.HistoryPresenter;
import com.hnust.liveapp.ui.adapter.RecyclerViewAdapter;
import com.hnust.liveapp.util.GlobalConfig;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yonglong on 2017/4/24.
 */

public class HistoryActivity extends AppCompatActivity implements HistoryContract.View {

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

    HistoryPresenter historyPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);
        mToolbar.setTitle("观看历史");
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        historyPresenter = new HistoryPresenter(this, this);
        historyPresenter.getHistory();


        mRecyclerView.setGridLayout(1);//参数为列数
        recyclerViewAdapter = new RecyclerViewAdapter(this, roomInfos, GlobalConfig.ROOMS_HISTORY);
        mRecyclerView.setAdapter(recyclerViewAdapter);
        mRecyclerView.setPushRefreshEnable(false);
        mRecyclerView.setPullRefreshEnable(false);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.history_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_clear:
                historyPresenter.clearHistory();
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
