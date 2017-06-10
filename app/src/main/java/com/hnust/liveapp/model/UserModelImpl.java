package com.hnust.liveapp.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.hnust.liveapp.bean.User;
import com.hnust.liveapp.contract.UserContract;

/**
 * Created by yonglong on 2017/03/29
 */

public class UserModelImpl implements UserContract.Model {


    @Override
    public User getUserInfo(Context mContext) {
        SharedPreferences sp = mContext.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        User user = new User();
        user.setUsername(sp.getString("username", null));
        user.setPassword(sp.getString("password", null));
        user.setGift(sp.getString("gift", null));
        user.setId(sp.getString("id", null));
        user.setSex(sp.getString("sex", null));
        user.setName(sp.getString("name", null));
        user.setIcon(sp.getString("icon", null));
        user.setTime(sp.getString("time", null));
        user.setFrom(sp.getString("from", null));
        user.setZhubo(sp.getInt("zhubo", -1));

        Log.e("getUserInfo", user.toString());
        return user;
    }

    @Override
    public void setUserInfo(Context mContext, User userInfo) {

        Log.e("userInfo", userInfo.toString());

        SharedPreferences sp = mContext.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("id", userInfo.getId());
        editor.putString("username", userInfo.getUsername());
        editor.putString("password", userInfo.getPassword());
        editor.putString("name", userInfo.getName());
        editor.putString("sex", userInfo.getSex());
        editor.putString("icon", userInfo.getIcon());
        editor.putString("time", userInfo.getTime());
        editor.putString("gift", userInfo.getGift());
        editor.putString("from", userInfo.getFrom());
        editor.putInt("zhubo", userInfo.getZhubo());
        editor.apply();
        editor.commit();
    }

    @Override
    public void clearInfo(Context mContext) {
        SharedPreferences sp = mContext.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
        editor.commit();
    }
}