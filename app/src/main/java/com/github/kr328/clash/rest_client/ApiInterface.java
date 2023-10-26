package com.github.kr328.clash.rest_client;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiInterface {

    @GET
    Call<JsonObject> allGetCall(@Url String url);

    @FormUrlEncoded
    @POST
    Call<JsonObject> oldPostCall(@Url String url, @FieldMap Map<String, String> options, @HeaderMap Map<String, String> headers);

    @Multipart
    @POST
    Call<ResponseBody> postImage(@Url String url, @Part MultipartBody.Part image, @HeaderMap Map<String, String> headers);

    @Multipart
    @POST
    Call<ResponseBody> gymRegister(@Url String url, @Part MultipartBody.Part image, @HeaderMap Map<String, String> headers, @Part("gym_name") RequestBody gym_name, @Part("gym_address") RequestBody gym_address, @Part("gym_mobile") RequestBody gym_mobile, @Part("user_image") RequestBody user_image);

    @Multipart
    @POST
    Call<ResponseBody> updateGymInfo(@Url String url, @Part MultipartBody.Part image, @HeaderMap Map<String, String> headers, @Part("gym_name") RequestBody gym_name, @Part("gym_address") RequestBody gym_address, @Part("gym_mobile") RequestBody gym_mobile, @Part("gym_id") RequestBody gym_id, @Part("user_image") RequestBody user_image, @Part("_method") RequestBody _method);

    @Multipart
    @POST()
    Call<ResponseBody> uploadImageOrFileWithPartMap(@Url String url, @Part MultipartBody.Part image, @HeaderMap Map<String, String> headers, @PartMap() Map<String, RequestBody> partMap);

    @Multipart
    @POST()
    Call<ResponseBody> uploadImageQrCodeWithPartMap(@Url String url, @Part MultipartBody.Part image, @Part MultipartBody.Part qrCode, @HeaderMap Map<String, String> headers, @PartMap() Map<String, RequestBody> partMap);

    @Multipart
    @POST
    Call<ResponseBody> userRegister(@Url String url, @Part MultipartBody.Part image, @HeaderMap Map<String, String> headers, @Part("user_name") RequestBody userId, @Part("user_email") RequestBody parentID, @Part("user_mobile") RequestBody documetntType, @Part("user_password") RequestBody imgname, @Part("user_type") RequestBody user_type, @Part("gym_id") RequestBody gym_id);

    @Multipart
    @POST
    Call<ResponseBody> EditUserInfo(@Url String url, @Part MultipartBody.Part image, @HeaderMap Map<String, String> headers, @Part("email") RequestBody userId, @Part("firstName") RequestBody parentID, @Part("lastName") RequestBody documetntType, @Part("contactNumber") RequestBody gym_id, @Part("_method") RequestBody _method);

    @FormUrlEncoded
    @PUT
    Call<JsonObject> allPutCall(@Url String url, @FieldMap Map<String, String> options, @HeaderMap Map<String, String> headers);

    @DELETE
    Call<JsonObject> comDelCall(@Url String url, @Query("access_token") String acctkn);

    @DELETE
    Call<JsonObject> removeItemfromServer(@Url String url, @HeaderMap Map<String, String> headers);

    @POST
    Call<JsonObject> allJPostCall(@Url String url, @Body JsonObject jsonObject, @HeaderMap Map<String, String> headers);

    @PUT
    Call<JsonObject> allJPutCall(@Url String url, @Body JsonObject jsonObject, @HeaderMap Map<String, String> headers);

    @GET
    Call<JsonArray> allJGetCall(@Url String url, @HeaderMap Map<String, String> body);

    @GET
    Call<Integer> getintObjFromServer(@Url String url, @HeaderMap Map<String, String> body);

    @GET
    Call<JsonObject> getJObjFromServer(@Url String url, @HeaderMap Map<String, String> body);

    @POST
    Call<JsonObject> postStringBodyparamCall(@Url String url, @Body String rawdata, @HeaderMap Map<String, String> headers);

    @POST
    Call<JsonObject> postReqBodyparamCall(@Url String url, @Body RequestBody rawdata, @HeaderMap Map<String, String> headers);
    @GET
    Call<JsonObject> getDataServer(@Url String url, @HeaderMap Map<String, String> header,@Body RequestBody body);

}
