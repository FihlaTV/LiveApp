package com.hnust.liveapp.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hnust.liveapp.R;
import com.hnust.liveapp.bean.RoomInfo;
import com.hnust.liveapp.contract.FocusContract;
import com.hnust.liveapp.contract.LivesContract;
import com.hnust.liveapp.model.UserModelImpl;
import com.hnust.liveapp.presenter.FocusPresenterImpl;
import com.hnust.liveapp.presenter.LivesPresenterImpl;
import com.hnust.liveapp.ui.activitys.LoginActivity;
import com.hnust.liveapp.ui.adapter.RecyclerViewAdapter;
import com.hnust.liveapp.util.GlobalConfig;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yonglong on 2017/3/28.
 */

public class FocusFragment extends Fragment implements FocusContract.View {

    @BindView(R.id.mRecyclerView)
    PullLoadMoreRecyclerView mPullLoadMoreRecyclerView;
    @BindView(R.id.tv_msg)
    TextView tv_msg;

    RecyclerViewAdapter mRecyclerViewAdapter;
    private List<RoomInfo> roomInfos = new ArrayList<>();

    FocusPresenterImpl focusPresenter;
    UserModelImpl userModel;

    public static FocusFragment newInstance(String title) {

        Bundle args = new Bundle();
        args.putString("title", title);
        FocusFragment fragment = new FocusFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_focus, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userModel = new UserModelImpl();
        //网格布局
        mPullLoadMoreRecyclerView.setGridLayout(1);//参数为列数
        mRecyclerViewAdapter = new RecyclerViewAdapter(getContext(), roomInfos, GlobalConfig.ROOMS_FOCUS);
        mPullLoadMoreRecyclerView.setAdapter(mRecyclerViewAdapter);
        mPullLoadMoreRecyclerView.setPushRefreshEnable(true);
        mPullLoadMoreRecyclerView.setPullRefreshEnable(false);
        mPullLoadMoreRecyclerView.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                tv_msg.setVisibility(View.VISIBLE);
                tv_msg.setText(getContext().getText(R.string.loading));
                focusPresenter.getRoomsInfo();
            }

            @Override
            public void onLoadMore() {
                mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
            }
        });

        focusPresenter = new FocusPresenterImpl(this, getContext());
        focusPresenter.getRoomsInfo();
    }

    @Override
    public void updateView() {
        tv_msg.setVisibility(View.GONE);
        mPullLoadMoreRecyclerView.setVisibility(View.VISIBLE);
        mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();

        if (userModel.getUserInfo(getContext()).getUsername() == null) {
            tv_msg.setVisibility(View.VISIBLE);
            tv_msg.setText("请点击登录");
            mPullLoadMoreRecyclerView.setVisibility(View.GONE);
            tv_msg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                }
            });
        } else if (roomInfos.size() == 0) {
            tv_msg.setVisibility(View.VISIBLE);
            tv_msg.setText("暂无关注");
            mRecyclerViewAdapter.notifyDataSetChanged();
        } else {
            tv_msg.setVisibility(View.GONE);
            mRecyclerViewAdapter.notifyDataSetChanged();
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
        roomInfos = lives;
        mRecyclerViewAdapter.setLiveInfos(lives);
        mRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateView();
    }
}
