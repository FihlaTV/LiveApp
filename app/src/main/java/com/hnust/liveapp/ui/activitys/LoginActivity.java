package com.hnust.liveapp.ui.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hnust.liveapp.R;
import com.hnust.liveapp.bean.User;
import com.hnust.liveapp.contract.LoginContract;
import com.hnust.liveapp.presenter.LoginPresenterImpl;
import com.hnust.liveapp.util.GlobalConfig;
import com.hnust.liveapp.util.Md5Util;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoginContract.View {


    // UI references.
    @BindView(R.id.username)
    TextInputLayout mUserNameView;
    @BindView(R.id.password)
    TextInputLayout mPasswordView;
    @BindView(R.id.login_progress)
    View mProgressView;
    @BindView(R.id.login)
    Button mLogin;
    @BindView(R.id.register)
    TextView mRegister;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.iv_qq)
    ImageView iv_qq;

    /**
     * 数据
     */
    private LoginPresenterImpl loginPresenter;
    Tencent mTencent;
    IUiListener loginListener;

    Map<String, String> params = new HashMap<>();


    @OnClick(R.id.login)
    void login_normal() {
        String username = mUserNameView.getEditText().getText().toString();
        String passwd = mPasswordView.getEditText().getText().toString();

        if (TextUtils.isEmpty(username)) {
            onUsernameView(false, "请输入用户名");
            return;
        }
        if (TextUtils.isEmpty(passwd)) {
            onLoginResult(false, "请输入密码");
            return;
        }
        passwd = Md5Util.EncoderByMd5(passwd);

        params.put("username", username);
        params.put("password", passwd);

        //登录
        loginPresenter.doLogin(params);
    }

    @OnClick(R.id.register)
    void register() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }


    @OnClick(R.id.iv_qq)
    void login_qq() {
        // Tencent类是SDK的主要实现类，开发者可通过Tencent类访问腾讯开放的OpenAPI。
        // 其中APP_ID是分配给第三方应用的appid，类型为String。
        mTencent = Tencent.createInstance(GlobalConfig.APP_ID, this.getApplicationContext());
        onSetProgressBarVisibility(true);
        mTencent.login(this, "all", loginListener);
        loginListener = new IUiListener() {
            @Override
            public void onComplete(Object o) {
                //登录成功后回调该方法,可以跳转相关的页面
//                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                JSONObject object = (JSONObject) o;
                try {
                    String accessToken = object.getString("access_token");
                    String expires = object.getString("expires_in");
                    String openID = object.getString("openid");
                    Log.e(GlobalConfig.TAG, accessToken + "--" + expires + "--" + openID);
                    mTencent.setAccessToken(accessToken, expires);
                    mTencent.setOpenId(openID);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(UiError uiError) {
                onSetProgressBarVisibility(false);
            }
            @Override
            public void onCancel() {
                onSetProgressBarVisibility(false);
            }
        };
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mToolbar.setTitle("登录");
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //init
        loginPresenter = new LoginPresenterImpl(this);

        mPasswordView.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    login_normal();
                    return true;
                }
                return false;
            }
        });


    }

    @Override
    public void onUsernameView(Boolean result, String message) {

        mUserNameView.setError(null);

        mUserNameView.setError(getString(R.string.error_invalid_email));
        mUserNameView.requestFocus();
    }

    @Override
    public void onPasswordView(Boolean result, String message) {
        mPasswordView.setError(null);

        mPasswordView.setError(getString(R.string.error_incorrect_password));
        mPasswordView.requestFocus();
    }

    @Override
    public void onLoginResult(Boolean result, String message) {

        if (result) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 进度条显示
     *
     * @param show
     */
    @Override
    public void onSetProgressBarVisibility(final boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            mUserNameView.clearFocus();
            mPasswordView.clearFocus();
            mProgressView.setOnClickListener(null);
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //qq登录返回结果
        if (requestCode == com.tencent.connect.common.Constants.REQUEST_LOGIN) {
            if (resultCode == -1) {
                Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);
                Tencent.handleResultData(data, loginListener);
                com.tencent.connect.UserInfo info = new com.tencent.connect.UserInfo(this, mTencent.getQQToken());
                info.getUserInfo(new IUiListener() {
                    @Override
                    public void onComplete(Object o) {
                        try {
                            JSONObject info = (JSONObject) o;
                            String nickName = info.getString("nickname");//获取用户昵称
                            String iconUrl = info.getString("figureurl_qq_2");//获取用户头像的url
                            String gender = info.getString("gender");//获取用户性别

                            params.put("username", mTencent.getOpenId());
                            params.put("name", nickName);
                            params.put("icon", iconUrl);
                            params.put("sex", gender);
                            params.put("from", "qq");

                            Log.e(GlobalConfig.TAG, mTencent.getOpenId() + "--" + nickName + "\n" + iconUrl + "\n" +
                                    gender);
                            loginPresenter.doLogin(params);
                        } catch (JSONException e) {
                            loginPresenter.setProgressBarVisiblity(false);
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(UiError uiError) {
                        loginPresenter.setProgressBarVisiblity(false);
                    }

                    @Override
                    public void onCancel() {
                        loginPresenter.setProgressBarVisiblity(false);
                    }
                });
            }
            loginPresenter.setProgressBarVisiblity(false);
        } else {
            loginPresenter.setProgressBarVisiblity(false);
        }
    }

}

