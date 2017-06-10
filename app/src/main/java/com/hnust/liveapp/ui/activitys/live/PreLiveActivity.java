package com.hnust.liveapp.ui.activitys.live;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hnust.liveapp.R;
import com.hnust.liveapp.api.ApiManager;
import com.hnust.liveapp.bean.CateInfo;
import com.hnust.liveapp.bean.GsonCreateRoom;
import com.hnust.liveapp.bean.GsonRoomInfo;
import com.hnust.liveapp.bean.User;
import com.hnust.liveapp.contract.CateContract;
import com.hnust.liveapp.model.UserModelImpl;
import com.hnust.liveapp.presenter.CatePresenter;
import com.hnust.liveapp.ui.activitys.HosterActivity;
import com.hnust.liveapp.ui.adapter.CatetoryAdapter;
import com.hnust.liveapp.util.GlobalConfig;
import com.hnust.liveapp.util.PermissionsCheckUtil;
import com.zhy.m.permission.MPermissions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yonglong on 2017/4/20.
 */

public class PreLiveActivity extends AppCompatActivity implements CateContract.View {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.btn_start)
    Button btn_start;
    @BindView(R.id.et_title)
    EditText et_title;
    @BindView(R.id.et_content)
    EditText et_content;
    @BindView(R.id.sp_category)
    Spinner sp_category;

    @BindView(R.id.user_image)
    CircleImageView user_image;
    @BindView(R.id.user_name)
    TextView user_name;

    List<CateInfo> types = new ArrayList<>();

    @OnClick(R.id.btn_start)
    void start() {
        final String title = et_title.getText().toString();
        final String content = et_content.getText().toString();
        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "请输入房间名", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "请输入直播详情", Toast.LENGTH_SHORT).show();
        } else {
            final String category = types.get(sp_category.getSelectedItemPosition()).getType();
            Log.e(GlobalConfig.TAG, category);

            ApiManager.getInstence(this).apiService
                    .createRoom(title, category, content, userModel.getUserInfo(this).getName())
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<GsonCreateRoom>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("PreLiveActivity", e.getMessage());
                            Toast.makeText(PreLiveActivity.this, "服务器异常", Toast.LENGTH_SHORT);
                        }

                        @Override
                        public void onNext(GsonCreateRoom gsonRoomInfo) {
                            Log.e("PreLiveActivity", gsonRoomInfo.toString());
                            if (gsonRoomInfo.isStatus()) {
                                Intent intent = new Intent(PreLiveActivity.this, HosterActivity.class);
                                int room_id = gsonRoomInfo.getData().getRoom_id();
                                intent.putExtra("room_id", room_id);
                                startActivity(intent);
                            } else {
                                Toast.makeText(PreLiveActivity.this, gsonRoomInfo.getMessage(), Toast.LENGTH_SHORT);
                            }
                        }
                    });


        }
    }

    UserModelImpl userModel = new UserModelImpl();
    CatePresenter catePresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prelive);
        ButterKnife.bind(this);
        toolbar.setTitle("我要当主播");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        getDevicePermission();

        initView();
    }

    private void initView() {


        User user = userModel.getUserInfo(this);
        Glide.with(this)
                .load(user.getIcon())
                .placeholder(R.mipmap.ic_launcher_round)
                .into(user_image);
        user_name.setText(user.getName());
        et_content.setText("这是 " + user.getName() + " 的直播间");
        catePresenter = new CatePresenter(this, this);
        catePresenter.getTypesInfo();

        et_title.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    et_content.isFocused();
                    et_title.clearFocus();
                    return true;
                }
                return false;
            }
        });
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

    private static final int REQUECT_CODE_RECORD = 0;
    private static final int REQUECT_CODE_CAMERA = 1;

    /**
     * 获取摄像头和录音权限
     */
    private void getDevicePermission() {
        PermissionsCheckUtil.isOpenCarmaPermission(new PermissionsCheckUtil.RequestPermissionListener() {
            @Override
            public void requestPermissionSuccess() {

            }

            @Override
            public void requestPermissionFailed() {
                PermissionsCheckUtil.showMissingPermissionDialog(PreLiveActivity.this, "没有照相机权限");
            }

            @Override
            public void requestPermissionThanSDK23() {
                if (ContextCompat.checkSelfPermission(PreLiveActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

                } else {
                    MPermissions.requestPermissions(PreLiveActivity.this, REQUECT_CODE_CAMERA, Manifest.permission.CAMERA);
                }
            }
        });


        PermissionsCheckUtil.isOpenRecordAudioPermission(new PermissionsCheckUtil.RequestPermissionListener() {
            @Override
            public void requestPermissionSuccess() {

            }

            @Override
            public void requestPermissionFailed() {
                PermissionsCheckUtil.showMissingPermissionDialog(PreLiveActivity.this, "str_no_audio_record_permission");
            }

            @Override
            public void requestPermissionThanSDK23() {
                if (ContextCompat.checkSelfPermission(PreLiveActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {

                } else {
                    MPermissions.requestPermissions(PreLiveActivity.this, REQUECT_CODE_RECORD, Manifest.permission.RECORD_AUDIO);
                }
            }
        });
    }

    @Override
    public void returnError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void returnTypeData(List<CateInfo> types) {
        this.types = types;
        CatetoryAdapter myAdapter = new CatetoryAdapter(this, types);
        sp_category.setAdapter(myAdapter);
    }
}
