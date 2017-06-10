package com.hnust.liveapp.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hnust.liveapp.R;
import com.hnust.liveapp.bean.RoomInfo;
import com.hnust.liveapp.contract.LivesContract;
import com.hnust.liveapp.presenter.LivesPresenterImpl;
import com.hnust.liveapp.ui.adapter.RecyclerViewAdapter;
import com.hnust.liveapp.util.GlobalConfig;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yonglong on 2017/3/28.
 */

public class MainFragment extends Fragment implements LivesContract.View {


    @BindView(R.id.mRecyclerView)
    PullLoadMoreRecyclerView mRecyclerView;

    @BindView(R.id.tv_msg)
    TextView tv_msg;

    RecyclerViewAdapter mAdapter;
    private List<RoomInfo> roomInfos = new ArrayList<>();
    LivesPresenterImpl livesPresenter;


    public static MainFragment newInstance(String title) {

        Bundle args = new Bundle();
        args.putString("title", title);
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        roomInfos.clear();

        mRecyclerView.setGridLayout(2);
        mAdapter = new RecyclerViewAdapter(getContext(), roomInfos, GlobalConfig.ROOMS_LIVE);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setPushRefreshEnable(true);
        mRecyclerView.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                tv_msg.setVisibility(View.VISIBLE);
                tv_msg.setText(getContext().getText(R.string.loading));
                livesPresenter.getRoomsInfo("");
            }

            @Override
            public void onLoadMore() {
                livesPresenter.getMoreDatas();
            }
        });

        livesPresenter = new LivesPresenterImpl(this, getContext());
        livesPresenter.getRoomsInfo("");
    }

    @Override
    public void updateView() {
        tv_msg.setVisibility(View.GONE);
        mRecyclerView.setPullLoadMoreCompleted();
        mAdapter.notifyDataSetChanged();
        if (roomInfos.size() == 0) {
            tv_msg.setVisibility(View.VISIBLE);
            tv_msg.setText("暂无直播信息");
        }
    }

    @Override
    public void returnError(String msg) {
        mRecyclerView.setPullLoadMoreCompleted();
        tv_msg.setText(msg);
        tv_msg.setVisibility(View.VISIBLE);
    }

    @Override
    public void returnLivesListData(List<RoomInfo> lives) {
//        mRecyclerView.setEmpty();
        Log.e("onNext---", lives.toString());
        roomInfos = lives;
        mAdapter = new RecyclerViewAdapter(getContext(), roomInfos, GlobalConfig.ROOMS_MAIN);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateView();
    }
}
