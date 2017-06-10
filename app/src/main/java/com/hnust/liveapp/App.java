package com.hnust.liveapp;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.hnust.liveapp.util.GlobalConfig;

import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by yonglong on 2017/4/16.
 */

public class App extends Application {
    private static App instance;
    private static Context mContext;
    private SharedPreferences sharedPreferences;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mContext = this;


        sharedPreferences = mContext.getSharedPreferences("server", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (sharedPreferences.getString("chat_server", null) == null
                && sharedPreferences.getString("base_server", null) == null) {
            editor.putString("chat_server", GlobalConfig.CHAT_SERVER_URL);
            editor.putString("base_server", GlobalConfig.BASE_SERVER_URL);
            editor.apply();
            editor.commit();
        }
    }

    private Socket mSocket;

    {
        try {

            mSocket = IO.socket(GlobalConfig.CHAT_SERVER_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }

    public static Context getAppContext() {
        return mContext;
    }
}