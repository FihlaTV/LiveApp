package com.hnust.liveapp.ui.activitys;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hnust.liveapp.R;
import com.hnust.liveapp.bean.User;
import com.hnust.liveapp.contract.SettingContract;
import com.hnust.liveapp.model.UserModelImpl;
import com.hnust.liveapp.presenter.SettingPresenter;
import com.hnust.liveapp.util.GlobalConfig;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends AppCompatActivity implements SettingContract.View {


    @BindView(R.id.cancel)
    Button btn_cancel;
    @BindView(R.id.feedback)
    RelativeLayout feedback;
    @BindView(R.id.about)
    RelativeLayout about;
    @BindView(R.id.rl_server)
    RelativeLayout rl_server;
    @BindView(R.id.rl_chat_server)
    RelativeLayout rl_chat_server;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    UserModelImpl userModel = new UserModelImpl();
    SettingPresenter settingPresenter;


    @OnClick(R.id.rl_server)
    void server_url() {
        show(0);
    }

    @OnClick(R.id.rl_chat_server)
    void chat_server_url() {
        show(1);
    }

    @OnClick(R.id.about)
    void about() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.feedback)
    void feedback() {
        User user = userModel.getUserInfo(this);
        if (user.getUsername() != null) {
            Intent intent = new Intent(this, FeedbackActivity.class);
            intent.putExtra("username", user.getUsername());
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @OnClick(R.id.cancel)
    void cancel() {

        settingPresenter.logout();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        mToolbar.setTitle("设置");
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        settingPresenter = new SettingPresenter(this, this);
        settingPresenter.getLoginStatus();

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

    AlertDialog mUploadDialog;

    private void show(final int flag) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_update, (ViewGroup) findViewById(R.id.dialog));
        final EditText et_url = (EditText) dialogView.findViewById(R.id.et_url);
        Button okBtn = (Button) dialogView.findViewById(R.id.ok);
        final Button cancelBtn = (Button) dialogView.findViewById(R.id.cancel);
        AlertDialog.Builder uploadBuilder = new AlertDialog.Builder(this);
        et_url.setVisibility(View.VISIBLE);
        uploadBuilder.setView(dialogView);
        final SharedPreferences sharedPreferences = getSharedPreferences("server", Context.MODE_PRIVATE);


        String chat_server_url = sharedPreferences.getString("chat_server", GlobalConfig.CHAT_SERVER_URL);
        String base_server_url = sharedPreferences.getString("base_server", GlobalConfig.CHAT_SERVER_URL);

        if (flag == 0) {
            et_url.setText(base_server_url);
        } else {
            et_url.setText(chat_server_url);
        }
        mUploadDialog = uploadBuilder.create();
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = sharedPreferences.edit();
                String url = et_url.getText().toString().trim();
                if (flag == 0) {
                    editor.putString("base_server", url);
                } else {
                    editor.putString("chat_server", url);
                }
                editor.apply();
                editor.commit();
                mUploadDialog.dismiss();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUploadDialog.dismiss();
            }
        });
        mUploadDialog.show();
    }

    @Override
    public void logout(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void returnError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showlogout(boolean isLogin) {
        if (isLogin) {
            btn_cancel.setVisibility(View.VISIBLE);
        } else {
            btn_cancel.setVisibility(View.GONE);
        }
    }
}
