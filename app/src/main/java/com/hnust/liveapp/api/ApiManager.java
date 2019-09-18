package com.hnust.liveapp.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.hnust.liveapp.App;
import com.hnust.liveapp.util.GlobalConfig;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by yonglong on 2017/3/28.
 */

public class ApiManager {

    public ApiManagerService apiService;
    public Retrofit retrofit;


    private static ApiManager sApiManager;

    //获取ApiManager的单例
    public static ApiManager getInstence(Context context) {
        if (sApiManager == null) {
            synchronized (ApiManager.class) {
                if (sApiManager == null) {
                    sApiManager = new ApiManager(context);
                }
            }
        }
        return sApiManager;
    }

    private ApiManager(Context context) {


        //缓存
        File cacheFile = new File(App.getAppContext().getCacheDir(), "cache");
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 20); //20Mb

        //五秒超时
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .addInterceptor(new ReceivedCookiesInterceptor(context))
                .addInterceptor(new AddCookiesInterceptor(context))
                .cache(cache)
                .build();
        SharedPreferences sharedPreferences = context.getSharedPreferences("server", Context.MODE_PRIVATE);

        String base_server = sharedPreferences.getString("base_server", null);
        if (base_server == null) {
            base_server = GlobalConfig.BASE_SERVER_URL;
        }

        retrofit = new Retrofit.Builder()
                .baseUrl(base_server)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        apiService = retrofit.create(ApiManagerService.class);
    }

    public class ReceivedCookiesInterceptor implements Interceptor {
        private Context context;

        public ReceivedCookiesInterceptor(Context context) {
            super();
            this.context = context;

        }

        @Override
        public Response intercept(Chain chain) throws IOException {

            Response originalResponse = chain.proceed(chain.request());

            Log.e("Cookiew", originalResponse.headers().toString());
            //这里获取请求返回的cookie
            if (!originalResponse.headers("Set-Cookie").isEmpty()) {
                final StringBuffer cookieBuffer = new StringBuffer();
                Log.e("Cookiew", cookieBuffer.toString());
                //保存cookie数据
                Observable.from(originalResponse.headers("Set-Cookie"))
                        .map(new Func1<String, String>() {
                            @Override
                            public String call(String s) {
                                String[] cookieArray = s.split(";");
                                return cookieArray[0];
                            }
                        })
                        .subscribe(new Action1<String>() {
                            @Override
                            public void call(String cookie) {
                                Log.e("Cookiew", cookie);
                                cookieBuffer.append(cookie).append(";");
                                SharedPreferences sharedPreferences = context.getSharedPreferences("cookie", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("cookie", cookieBuffer.toString());
                                editor.commit();
                            }
                        });

            }

            return originalResponse;
        }
    }

    public class AddCookiesInterceptor implements Interceptor {
        private Context context;

        public AddCookiesInterceptor(Context context) {
            super();
            this.context = context;

        }

        @Override
        public Response intercept(Chain chain) throws IOException {

            final Request.Builder builder = chain.request().newBuilder();
            SharedPreferences sharedPreferences = context.getSharedPreferences("cookie", Context.MODE_PRIVATE);
            Observable.just(sharedPreferences.getString("cookie", ""))
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String cookie) {
                            //添加cookie
                            builder.addHeader("Cookie", cookie);
                        }
                    });
            return chain.proceed(builder.build());
        }
    }


}
