package com.hnust.liveapp.ui.activitys;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.codemybrainsout.onboarder.AhoyOnboarderActivity;
import com.codemybrainsout.onboarder.AhoyOnboarderCard;
import com.hnust.liveapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yonglong on 2017/4/25.
 */

public class WelcomeActivity extends AhoyOnboarderActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        AhoyOnboarderCard ahoyOnboarderCard1 = new AhoyOnboarderCard("看直播", "观看你感兴趣的直播", R.drawable.backpack);
        AhoyOnboarderCard ahoyOnboarderCard2 = new AhoyOnboarderCard("我要当主播", "分享专属于你的校园生活", R.drawable.chalk);
        AhoyOnboarderCard ahoyOnboarderCard3 = new AhoyOnboarderCard("聊天弹幕", "房间聊天室，给主播点赞", R.drawable.chat);

        ahoyOnboarderCard1.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard2.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard3.setBackgroundColor(R.color.black_transparent);

        List<AhoyOnboarderCard> pages = new ArrayList<>();

        pages.add(ahoyOnboarderCard1);
        pages.add(ahoyOnboarderCard2);
        pages.add(ahoyOnboarderCard3);

        for (AhoyOnboarderCard page : pages) {
            page.setTitleColor(R.color.white);
            page.setDescriptionColor(R.color.grey_200);
            page.setTitleTextSize(dpToPixels(12, this));
            page.setDescriptionTextSize(dpToPixels(8, this));
            //page.setIconLayoutParams(width, height, marginTop, marginLeft, marginRight, marginBottom);
        }

        setFinishButtonTitle("进入");
        showNavigationControls(true);
        setGradientBackground();

        //set the button style you created
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setFinishButtonDrawableStyle(ContextCompat.getDrawable(this, R.drawable.rounded_button));
        }

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        setFont(face);

        setOnboardPages(pages);

    }

    @Override
    public void onFinishButtonPressed() {

        Intent intent = new Intent(this, MainView.class);
        startActivity(intent);
        finish();
    }
}
