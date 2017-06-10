package com.hnust.liveapp.ui.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hnust.liveapp.R;
import com.hnust.liveapp.api.ApiManager;
import com.hnust.liveapp.api.ApiManagerService;
import com.hnust.liveapp.bean.GsonInfo;
import com.hnust.liveapp.bean.GsonRoomInfo;
import com.hnust.liveapp.bean.User;
import com.hnust.liveapp.contract.UserContract;
import com.hnust.liveapp.model.UserModelImpl;
import com.hnust.liveapp.presenter.UserPresenterImpl;
import com.hnust.liveapp.ui.activitys.live.PreLiveActivity;
import com.hnust.liveapp.util.GlobalConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yonglong on 2017/5/6.
 */

public class AuthActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tv_auth)
    TextView tv_auth;
    @BindView(R.id.username)
    TextInputLayout mUserNameView;
    @BindView(R.id.password)
    TextInputLayout mPasswordView;
    @BindView(R.id.login)
    Button btn_login;

    @OnClick(R.id.login)
    void login() {
        mUserNameView.setError(null);
        mPasswordView.setError(null);
        String num = mUserNameView.getEditText().getText().toString();
        String pw = mPasswordView.getEditText().getText().toString();

        if (TextUtils.isEmpty(num)) {
            mUserNameView.setError(getString(R.string.error_empty_user));
            mUserNameView.requestFocus();
        } else if (TextUtils.isEmpty(pw)) {
            mPasswordView.setError(getString(R.string.error_empty_password));
            mPasswordView.requestFocus();
        } else {
            verify(num, pw);
        }
    }


    UserModelImpl userModel = new UserModelImpl();

    /**
     *
     * @param num
     * @param pw
     */
    private void verify(String num, String pw) {
        //校园网验证
        ApiManager apiManager = ApiManager.getInstence(this);
        apiManager.apiService
                .renzheng(num, pw)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GsonRoomInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(AuthActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                        Log.e("AuthActivity", "onFailure-==" + e.getMessage());
                        Log.e("AuthActivity", "onFailure-==" + e.getLocalizedMessage());
                    }

                    @Override
                    public void onNext(GsonRoomInfo gsonInfo) {
                        Log.e("AuthActivity", "onNext-==" + gsonInfo);
                        Toast.makeText(AuthActivity.this, gsonInfo.getMessage(), Toast.LENGTH_SHORT).show();
                        if (gsonInfo.isStatus()) {
                            User user = userModel.getUserInfo(AuthActivity.this);
                            user.setZhubo(1);
                            userModel.setUserInfo(AuthActivity.this, user);
                            toPreLive();
                        }
                    }
                });
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(this);

        mToolbar.setTitle("直播认证");
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initView();
    }

    private void initView() {
        String textStr = "为防止恶意直播，必须使用 <font color='#FF0000'>教务网的用户名和密码</font> 实名验证通过后才能开直播";
        tv_auth.setText(Html.fromHtml(textStr));
    }

    private void toPreLive() {
        Intent intent = new Intent(this, PreLiveActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 返回键
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
