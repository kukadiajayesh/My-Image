package com.app.photobook.retro;

import com.app.photobook.model.AlbumActiveRes;
import com.app.photobook.model.AlbumRes;
import com.app.photobook.model.AppUpdateRes;
import com.app.photobook.model.Maintenance;
import com.app.photobook.model.PhotographerRes;
import com.app.photobook.model.PortfolioRes;
import com.app.photobook.model.UserRes;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

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
    @POST("album")
    Call<AlbumRes> getAlbum(@Field("pin") String pin,
                            @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("registerApp")
    Call<UserRes> registerUser(@Field("name") String name,
                               @Field("email") String email,
                               @Field("phone_no") String phone_no);

    @FormUrlEncoded
    @POST("addComment")
    Call<ResponseBody> addComment(@Field("event_id") int event_id,
                                  @Field("image_id") int image_id,
                                  @Field("user_id") int user_id,
                                  @Field("comment") String comment);

    @FormUrlEncoded
    @POST("getComment")
    Call<ResponseBody> getComments(@Field("user_id") String userId,
                                   @Field("event_id") int event_id,
                                   @Field("image_id") int image_id);

    @FormUrlEncoded
    @POST("getAllComment")
    Call<AlbumRes> getAlbumComments(@Field("user_id") String userId,
                                    @Field("event_id") String pb_id);

    @FormUrlEncoded
    @POST("getProfile")
    Call<PhotographerRes> getPhotographerDetail(@Field("email") String email);

    @FormUrlEncoded
    @POST("addSelection")
    Call<ResponseBody> submitImages(@Field("user_id") String userId,
                                    @Field("ids") String ids,
                                    @Field("event_id") String event_id);

    @FormUrlEncoded
    @POST("getMaxSelection")
    Call<ResponseBody> getMaxSelection(@Field("user_id") String userId,
                                       @Field("event_id") String event_id);

    @GET("getPortfolio")
    Call<PortfolioRes> getPortfolio();

    @FormUrlEncoded
    @POST("getVersion")
    Call<AppUpdateRes> getCheckVersion(@Field("type") String type);

    @FormUrlEncoded
    @POST("addInquiry")
    Call<ResponseBody> addInquiry(@Field("user_id") String userId,
                                  @Field("full_name") String full_name,
                                  @Field("email") String email,
                                  @Field("mobile") String mobile,
                                  @Field("event_date") String event_date,
                                  @Field("event_location") String event_location,
                                  @Field("event_type") String event_type,
                                  @Field("message") String message,
                                  @Field("reference_by") String reference_by);

    @FormUrlEncoded
    @POST("isEventActive")
    Call<AlbumActiveRes> checkIsAlbumActive(@Field("pin") String pin);

    @FormUrlEncoded
    @POST("getMaintenance")
    Call<Maintenance> getMaintenance(@Field("pin") String pin);
}
