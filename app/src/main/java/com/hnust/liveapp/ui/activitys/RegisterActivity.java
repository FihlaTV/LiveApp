package com.hnust.liveapp.ui.activitys;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hnust.liveapp.R;
import com.hnust.liveapp.contract.RegisterContract;
import com.hnust.liveapp.presenter.LoginPresenterImpl;
import com.hnust.liveapp.presenter.RegisterPresenterImpl;
import com.hnust.liveapp.util.GlobalConfig;
import com.hnust.liveapp.util.Md5Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity implements RegisterContract.View {

    // UI references.
    @BindView(R.id.username)
    TextInputLayout mUserNameView;
    @BindView(R.id.name)
    TextInputLayout mNameView;
    @BindView(R.id.password)
    TextInputLayout mPasswordView;
    @BindView(R.id.login_progress)
    View mProgressView;
    @BindView(R.id.register)
    Button mRegister;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    RegisterPresenterImpl registerPresenter;

    @OnClick(R.id.register)
    void register() {
        String username = mUserNameView.getEditText().getText().toString();
        String name = mNameView.getEditText().getText().toString();
        String password = mPasswordView.getEditText().getText().toString();

        mUserNameView.setError(null);
        mNameView.setError(null);
        mPasswordView.setError(null);

        if (TextUtils.isEmpty(username)) {
            mUserNameView.setError(getString(R.string.error_invalid_email));
            mUserNameView.requestFocus();
        } else if (TextUtils.isEmpty(name)) {
            mNameView.setError(getString(R.string.error_invalid_email));
            mNameView.requestFocus();
        } else if (TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            mPasswordView.requestFocus();
        } else {
            password = Md5Util.EncoderByMd5(password);
            registerPresenter.setProgressBarVisiblity(true);
            registerPresenter.doRegister(username, name, password);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        mToolbar.setTitle("注册");
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //init
        registerPresenter = new RegisterPresenterImpl(this);


    }


    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 6 && password.length() <= 25;
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
    public void onResult(Boolean result, String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        if (result) {
            finish();
        }
    }

    @Override
    public void onSetProgressBarVisibility(boolean visibility) {
        mProgressView.setVisibility(visibility ? View.VISIBLE : View.GONE);
        if (visibility) {
            mUserNameView.clearFocus();
            mPasswordView.clearFocus();
            mNameView.clearFocus();
            mProgressView.setOnClickListener(null);
        }
    }
}
