package com.hnust.liveapp.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hnust.liveapp.R;
import com.hnust.liveapp.bean.CateInfo;
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

public class LiveFragment extends Fragment implements LivesContract.View {

    @BindView(R.id.mRecyclerView)
    PullLoadMoreRecyclerView mPullLoadMoreRecyclerView;
    @BindView(R.id.tv_msg)
    TextView tv_msg;

    RecyclerViewAdapter mRecyclerViewAdapter;
    private List<RoomInfo> roomInfos = new ArrayList<>();

    LivesPresenterImpl livesPresenter;
    int startpage = 1;

    public static LiveFragment newInstance(CateInfo cateInfo) {

        Bundle args = new Bundle();
        args.putInt("flag", cateInfo.getId());
        args.putString("type", cateInfo.getType());
        LiveFragment fragment = new LiveFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_live, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /**
         * 本地数据
         */
        roomInfos.clear();
        //网格布局
        mPullLoadMoreRecyclerView.setGridLayout(2);//参数为列数
        mRecyclerViewAdapter = new RecyclerViewAdapter(getContext(), roomInfos, GlobalConfig.ROOMS_LIVE);
        mPullLoadMoreRecyclerView.setAdapter(mRecyclerViewAdapter);
        mPullLoadMoreRecyclerView.setPushRefreshEnable(true);
        mPullLoadMoreRecyclerView.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                livesPresenter.getRoomsInfo(getArguments().getString("type"));
            }

            @Override
            public void onLoadMore() {
                livesPresenter.getMoreDatas();
            }
        });

        livesPresenter = new LivesPresenterImpl(this, getContext());
        livesPresenter.getRoomsInfo(getArguments().getString("type"));
    }


    @Override
    public void updateView() {
        tv_msg.setVisibility(View.GONE);
        mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
        mRecyclerViewAdapter.notifyDataSetChanged();
        if (roomInfos.size() == 0) {
            tv_msg.setVisibility(View.VISIBLE);
            tv_msg.setText("暂无直播信息");
        }
    }

    @Override
    public void returnError(String msg) {
        mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
        tv_msg.setText(msg);
        tv_msg.setVisibility(View.VISIBLE);
    }


    @Override
    public void returnLivesListData(List<RoomInfo> lives) {
        roomInfos=lives;
        mRecyclerViewAdapter.setLiveInfos(lives);
        mRecyclerViewAdapter.notifyDataSetChanged();
    }
}
