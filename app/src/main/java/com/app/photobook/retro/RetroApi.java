package com.app.photobook.retro;

import com.app.photobook.model.AlbumRes;
import com.app.photobook.model.PhotographerRes;
import com.app.photobook.model.PortfolioRes;
import com.app.photobook.model.UserRes;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by shree on 10-01-2017.
 */

public interface RetroApi {

/*
    @Multipart
    @POST("admin/upload_photo.php")
    Call<AnnouncementResponce> uploadPhotos(@Part("admin_id") RequestBody admin_id,
                                            @Part("uploaded_by") RequestBody uploaded_by,
                                            @Part("device_type") RequestBody device_type,
                                            @Part("title") RequestBody title,
                                            @Part("file\"; filename=\"_.jpg\" ") RequestBody file,
                                            @Part("upload_time") RequestBody upload_time,
                                            @Part("is_notify") RequestBody is_notify);
*/


    @FormUrlEncoded
    @POST("album/{id}")
    Call<AlbumRes> getAlbum(@Path("id") String id,
                            @Field("pin") String pin,
                            @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("registerApp/{id}")
    Call<UserRes> registerUser(@Path("id") String id,
                               @Field("name") String name,
                               @Field("email") String email,
                               @Field("phone_no") String phone_no);

    @FormUrlEncoded
    @POST("addComment/{id}")
    Call<ResponseBody> addComment(@Path("id") String id,
                                  @Field("event_id") int event_id,
                                  @Field("image_id") int image_id,
                                  @Field("user_id") int user_id,
                                  @Field("comment") String comment);

    @FormUrlEncoded
    @POST("getComment/{id}")
    Call<ResponseBody> getComments(@Path("id") String id,
                                   @Field("event_id") int event_id,
                                   @Field("image_id") int image_id);

    @FormUrlEncoded
    @POST("getAllComment/{id}")
    Call<AlbumRes> getAlbumComments(@Path("id") String id,
                                    @Field("event_id") String pb_id);

    @FormUrlEncoded
    @POST("signup")
    Call<ResponseBody> signupUser(@Field("name") String name,
                                  @Field("email") String email,
                                  @Field("mobile") String mobile);

    @FormUrlEncoded
    @POST("getProfile/{id}")
    Call<PhotographerRes> getPhotographerDetail(@Path("id") String id,
                                                @Field("email") String email);

    @FormUrlEncoded
    @POST("addSelection/{id}")
    Call<ResponseBody> submitImages(@Path("id") String id,
                                    @Field("ids") String ids,
                                    @Field("event_id") String event_id);

    @FormUrlEncoded
    @POST("update_token.php")
    Call<ResponseBody> updateToken(@Field("user_id") int user_id,
                                   @Field("token") String token,
                                   @Field("android_id") String android_id);

    @FormUrlEncoded
    @POST("delete_token.php")
    Call<ResponseBody> deleteToken(@Field("android_id") String android_id);


    @FormUrlEncoded
    @POST("getMaxSelection/{id}")
    Call<ResponseBody> getMaxSelection(@Path("id") String id,
                                       @Field("event_id") String event_id);

    @GET("getPortfolio/{id}")
    Call<PortfolioRes> getPortfolio(@Path("id") String id);
}
