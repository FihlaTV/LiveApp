package com.hnust.liveapp.api;

import com.hnust.liveapp.bean.GsonCateInfo;
import com.hnust.liveapp.bean.GsonCreateRoom;
import com.hnust.liveapp.bean.GsonInfo;
import com.hnust.liveapp.bean.GsonOpenFj;
import com.hnust.liveapp.bean.GsonRoomInfo;
import com.hnust.liveapp.bean.GsonUserInfo;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by yonglong on 2017/3/28.
 */
//方便后面RxAndroid我把RxAndroid方式的接口也贴上来。只有返回类型不同而已
public interface ApiManagerService {

    //用户登录
    @FormUrlEncoded
    @POST("kdtv/dlu.php")
    Observable<GsonUserInfo> login(@FieldMap Map<String, String> params);

    //用户登录
    @POST("kdtv/zhux.php")
    Observable<GsonInfo> logout();

    //注册
    @FormUrlEncoded
    @POST("kdtv/zhuce.php")
    Observable<GsonInfo> register(@Field("username") String username,
                                  @Field("name") String name,
                                  @Field("password") String password);
    //反馈
    @FormUrlEncoded
    @POST("kdtv/fankui.php")
    Observable<GsonInfo> feedback(@Field("content") String content);


    //用户直播认证
    @FormUrlEncoded
    @POST("kdtv/renzheng.php")
    Observable<GsonRoomInfo> renzheng(@Field("num") String num,
                                      @Field("pw") String pw);  //用户直播认证

    //创建直播房间
    @FormUrlEncoded
    @POST("kdtv/room.php")
    Observable<GsonCreateRoom> createRoom(@Field("fj_name") String fj_name,
                                          @Field("type") String type,
                                          @Field("content") String content,
                                          @Field("name") String name);

    //开直播关直播
    @FormUrlEncoded
    @POST("kdtv/guanzb.php")
    Observable<GsonInfo> openLive(@Field("now") int now);


    //用户信息修改
    @FormUrlEncoded
    @POST("kdtv/xiugai.php")
    Observable<GsonInfo> xiugai(@FieldMap Map<String, String> options);
    //获取房间
    @GET("kdtv/sc_room.php")
    Observable<GsonRoomInfo> getRooms(@Query("type") String type);


    //获取分类
    @GET("kdtv/sc_type.php")
    Observable<GsonCateInfo> getTypes();

    //获取分类
    @GET("kdtv/sc_guanzhu.php")
    Observable<GsonRoomInfo> getGuanzhu();


    //签到
    @FormUrlEncoded
    @POST("kdtv/qiandao.php")
    Observable<GsonInfo> qiandao(@Field("num") int num);

    //获取签到
    @POST("kdtv/qiandao2.php")
    Observable<GsonInfo> qiandao2();


    //获取用户信息
    @POST("kdtv/user.php")
    Observable<GsonUserInfo> getUserInfo();


    //获取房间信息
    @FormUrlEncoded
    @POST("kdtv/openfj.php")
    Observable<GsonOpenFj> openFJ(@Field("room_id") int room_id);

    //关注
    @FormUrlEncoded
    @POST("kdtv/guanzhu.php")
    Observable<GsonInfo> guanzhu(@Field("room_id") int room_id);

    //历史
    @POST("kdtv/sc_lishi.php")
    Observable<GsonRoomInfo> gethistory();

    //禁播列表
    @POST("kdtv/sc_jinbo.php")
    Observable<GsonRoomInfo> getLock();

    //禁播
    @FormUrlEncoded
    @POST("kdtv/jinbo.php")
    Observable<GsonInfo> lockRoom(@Field("room_id") int room_id);


    //清空历史
    @POST("kdtv/clear.php")
    Observable<GsonInfo> clearhistory();

    //文件上传

    @POST("kdtv/shangch2.php")
    Observable<GsonInfo> uploadFile(@Part("file") RequestBody imgs);



}
