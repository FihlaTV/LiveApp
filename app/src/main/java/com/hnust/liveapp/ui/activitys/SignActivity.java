package com.hnust.liveapp.ui.activitys;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hnust.liveapp.R;
import com.hnust.liveapp.contract.SignContract;
import com.hnust.liveapp.presenter.SignPresenter;
import com.hnust.liveapp.ui.adapter.SignCardAdapter;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yonglong on 2017/4/26.
 */

public class SignActivity extends AppCompatActivity implements SignContract.View {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.mRecyclerView)
    PullLoadMoreRecyclerView mRecyclerView;

    SignCardAdapter signCardAdapter;
    @BindView(R.id.sign)
    TextView tv_sign;

    SignPresenter signPresenter;
    boolean cliclkable = false;
    int clickid = 0;
    int n[] = new int[6];


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        ButterKnife.bind(this);
        mToolbar.setTitle("每日签到");
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        for (int i = 0; i < n.length; i++) {
            n[i] = (int) (Math.random() * 1000 + 1);
        }
        //网格布局
        mRecyclerView.setGridLayout(3);//参数为列数
        signCardAdapter = new SignCardAdapter(this, n);
        mRecyclerView.setAdapter(signCardAdapter);
        mRecyclerView.setPushRefreshEnable(false);
        mRecyclerView.setPullRefreshEnable(false);

        signCardAdapter.setOnItemClickListener(new SignCardAdapter.MyItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (cliclkable) {
                    clickid = position;
                    signPresenter.qiandao(n[position]);
                } else {
                    Toast.makeText(SignActivity.this, "今日已签到", Toast.LENGTH_SHORT).show();
                }

            }
        });
        signPresenter = new SignPresenter(this, this);
        signPresenter.qiandao2();

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
    }

    @Override
    public void returnError(String msg) {
        tv_sign.setText(msg);
    }

    @Override
    public void returnSuccess(String msg) {
        if (msg.equals("已签到")) {
            cliclkable = false;
            signCardAdapter.setmIsShowBack(true);
            tv_sign.setText(msg);
        } else if (msg.equals("未签到")) {
            cliclkable = true;
            signCardAdapter.setmIsShowBack(false);
            tv_sign.setText(msg);
        } else {
            Toast.makeText(SignActivity.this, "签到获得" + n[clickid] + "克狗粮", Toast.LENGTH_SHORT).show();
        }
    }

}
