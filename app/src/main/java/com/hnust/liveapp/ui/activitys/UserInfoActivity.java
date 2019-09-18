package com.hnust.liveapp.ui.activitys;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hnust.liveapp.R;
import com.hnust.liveapp.bean.User;
import com.hnust.liveapp.contract.UserContract;
import com.hnust.liveapp.model.UserModelImpl;
import com.hnust.liveapp.presenter.UserPresenterImpl;
import com.hnust.liveapp.util.FileUtils;
import com.hnust.liveapp.util.Md5Util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by yonglong on 2017/4/24.
 */

public class UserInfoActivity extends AppCompatActivity implements UserContract.View {

    /**
     * 界面
     */
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.rl_icon)
    RelativeLayout rl_icon;
    @BindView(R.id.rl_nick)
    RelativeLayout rl_nick;
    @BindView(R.id.rl_pw)
    RelativeLayout rl_pw;
    @BindView(R.id.rl_sex)
    RelativeLayout rl_sex;

    @BindView(R.id.tv_password)
    TextView tv_password;

    @BindView(R.id.tv_nick)
    TextView tv_nick;

    @BindView(R.id.tv_sex)
    TextView tv_sex;

    @BindView(R.id.tv_gift)
    TextView tv_gift;


    @BindView(R.id.civ_person)
    CircleImageView civ_person;

    @OnClick(R.id.rl_icon)
    void updateImage() {
        showChooseDialog();
    }

    @OnClick(R.id.rl_nick)
    void updatenick() {
        showDialog(0, tv_nick.getText().toString());
    }

    @OnClick(R.id.rl_sex)
    void updatesex() {
        showDialog(1, tv_sex.getText().toString());
    }

    @OnClick(R.id.rl_pw)
    void updatepw() {
        showDialog(2, tv_password.getText().toString());
    }

    /**
     * 数据
     */
    UserModelImpl userModel = new UserModelImpl();
    UserPresenterImpl userPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        ButterKnife.bind(this);
        mToolbar.setTitle("个人中心");
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        userPresenter = new UserPresenterImpl(this, this);
        initView();
    }

    private void initView() {
        User userInfo = userModel.getUserInfo(this);
        tv_nick.setText(userInfo.getName());
        tv_sex.setText(userInfo.getSex());
        tv_password.setText(userInfo.getPassword());
        tv_gift.setText(userInfo.getGift());
        Glide.with(this).load(userInfo.getIcon().toString())
                .error(R.mipmap.dashi)
                .into(civ_person);
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

    private Bitmap mBitmap;
    private final int CHOOSE_PICTURE = 0;
    private final int TAKE_PICTURE = 1;
    private static final int CROP_SMALL_PICTURE = 2;
    protected static Uri tempUri;
    String newValue = "";


    /**
     * 获取相册权限
     */
    private static final int CROP_PHOTO = 2;
    private static final int REQUEST_CODE_PICK_IMAGE = 3;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 6;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE2 = 7;

    private void showChooseDialog() {

        AlertDialog.Builder uploadBuilder = new AlertDialog.Builder(this);
        String[] items = new String[]{"本地照片", "相机"};
        uploadBuilder.setTitle("更换头像");
        uploadBuilder.setNegativeButton("取消", null);
        uploadBuilder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case CHOOSE_PICTURE: // 选择本地照片
                        choosePhone();
                        break;
                    case TAKE_PICTURE: // 拍照
                        takePhone();
                        break;
                }
            }
        });
        uploadBuilder.show();
    }


    public void choosePhone() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE2);
        } else {
            choosePhoto();
        }
    }

    public void takePhone() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE2);
        } else {
            takePhoto();
        }

    }

    public void takePhoto() {
        File file = new File(Environment.getExternalStorageDirectory(), "kdtv");
        if (!file.exists()) {
            file.mkdir();
        }
        File output = new File(file, System.currentTimeMillis() + ".jpg");
        try {
            if (output.exists()) {
                output.delete();
            }
            output.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        tempUri = Uri.fromFile(output);
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
        startActivityForResult(intent, TAKE_PICTURE);
    }
    private void choosePhoto() {
        Intent openAlbumIntent = new Intent(
                Intent.ACTION_GET_CONTENT);
        openAlbumIntent.setType("image/*");
        //用startActivityForResult方法，待会儿重写onActivityResult()方法，拿到图片做裁剪操作
        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
    }

    AlertDialog mUploadDialog;

    /**
     * flag =0 修改昵称 1修改性别 2修改密码
     *
     * @param flag
     * @param arg1
     */
    private void showDialog(final int flag, String arg1) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_update, (ViewGroup) findViewById(R.id.dialog));
        final TextInputLayout et1 = (TextInputLayout) dialogView.findViewById(R.id.et1);
        final EditText et_password = (EditText) dialogView.findViewById(R.id.et_password);
        final Spinner sp_sex = (Spinner) dialogView.findViewById(R.id.sp_sex);
        Button okBtn = (Button) dialogView.findViewById(R.id.ok);
        final Button cancelBtn = (Button) dialogView.findViewById(R.id.cancel);
        AlertDialog.Builder uploadBuilder = new AlertDialog.Builder(this);

        uploadBuilder.setView(dialogView);
        if (flag == 1) {
            uploadBuilder.setTitle("修改性别");
            sp_sex.setVisibility(View.VISIBLE);
        } else if (flag == 0) {
            uploadBuilder.setTitle("修改昵称");
            et1.getEditText().setText(arg1);
            et1.setVisibility(View.VISIBLE);
        } else if (flag == 2) {
            uploadBuilder.setTitle("修改密码");
            et1.getEditText();
            et1.setVisibility(View.VISIBLE);
            et_password.setVisibility(View.VISIBLE);
        }
        mUploadDialog = uploadBuilder.create();
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et1.setError(null);
                Map<String, String> map = new HashMap<>();
                if (flag == 1) {
                    newValue = sp_sex.getSelectedItem().toString();
                    map.put("sex", newValue);
                } else if (flag == 0) {
                    newValue = et1.getEditText().getText().toString();
                    if (TextUtils.isEmpty(newValue)) {
                        Toast.makeText(UserInfoActivity.this, "请输入修改的值", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    map.put("name", newValue);
                } else if (flag == 2) {
                    String oldValue = et1.getEditText().getText().toString();
                    String newValue = et_password.getText().toString();
                    if (TextUtils.isEmpty(newValue) && TextUtils.isEmpty(oldValue)) {
                        Toast.makeText(UserInfoActivity.this, "内容不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    newValue = Md5Util.EncoderByMd5(newValue);
                    map.put("password", newValue);
                    map.put("oldpw", oldValue);
                }
                userPresenter.updateUserInfo(map, flag);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == UserInfoActivity.RESULT_OK) {
            switch (requestCode) {
                case TAKE_PICTURE:
                    cutImage(tempUri); // 对图片进行裁剪处理
                    break;
                case CHOOSE_PICTURE:
                    try {
                        cutImage(data.getData()); // 对图片进行裁剪处理
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case CROP_SMALL_PICTURE:
                    if (data != null) {
                        setImageToView(data); // 让刚才选择裁剪得到的图片显示在界面上
                    }
                    break;
            }
        }
    }

    /**
     * 裁剪图片方法实现
     */
    protected void cutImage(Uri uri) {
        if (uri == null) {
            Log.i("alanjet", "The uri is not exist.");
        }
        tempUri = uri;

        Intent intent = new Intent("com.android.camera.action.CROP");
        //com.android.camera.action.CROP这个action是用来裁剪图片用的
        intent.setDataAndType(tempUri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", true);
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_SMALL_PICTURE);


    }


    /**
     * 保存裁剪之后的图片数据
     */
    protected void setImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            mBitmap = extras.getParcelable("data");

            //这里图片是方形的，可以用一个工具类处理成圆形（很多头像都是圆形，这种工具类网上很多不再详述）
//            mImage.setImageBitmap(mBitmap);//显示图片
//            civ_person.setImageBitmap(mBitmap);
            //在这个地方可以写上上传该图片到服务器的代码，后期将单独写一篇这方面的博客，敬请期待...

            // 上传至服务器
            // ... 可以在这里把Bitmap转换成file，然后得到file的url，做文件上传操作
            // 注意这里得到的图片已经是圆形图片了
            // bitmap是没有做个圆形处理的，但已经被裁剪了
            FileUtils fileUtils = new FileUtils(this);
            String imagePath = fileUtils.cutPictureQuality(mBitmap, "kdtv");
            Log.e("dddddddddddddd", imagePath + "");
            File file = new File(imagePath);

            Log.e("dddddddddddddd", file.getAbsolutePath() + "");
            if (file.exists()) {
                userPresenter.uploadFile(imagePath);
            } else {
                returnInfo("图片不存在");
            }
        }
    }


    @Override
    public void updateView(User user, boolean isLogin) {
        if (isLogin) {
            tv_nick.setText(user.getName());
            tv_sex.setText(user.getSex());
            tv_password.setText(user.getPassword());
            tv_gift.setText(user.getGift());
            Glide.with(this).load(user.getIcon().toString())
                    .error(R.mipmap.dashi)
                    .into(civ_person);
        }
    }

    @Override
    public void returnInfo(String msg) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto();
            } else {
                // Permission Denied
                Toast.makeText(UserInfoActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }


        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                choosePhoto();
            } else {
                // Permission Denied
                Toast.makeText(UserInfoActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
