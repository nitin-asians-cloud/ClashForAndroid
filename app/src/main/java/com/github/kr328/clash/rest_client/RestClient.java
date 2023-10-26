package com.github.kr328.clash.rest_client;

import android.util.Base64;
import android.util.Log;

import com.github.kr328.clash.BuildConfig;
import com.github.kr328.clash.util.Url;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.moczul.ok2curl.CurlInterceptor;
import com.moczul.ok2curl.logger.Loggable;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RestClient {
    public static Retrofit retrofit2;
    byte[] data = null;
    Map<String, String> params;
    Map<String, String> headers;
    private final int GET = 0, POST = 1;
    private String url, versionName;
    public static int TIMEOUT_CONNECTION = 60000;
    public static int TIMEOUT_SOCKET = 60000;
    protected static final long SECONDSOFFSET = 2 * 60 * 1000;
    private int responseCode;
    private String message;

    private String response;

    public String getResponse() {
        return response;
    }

    public String getErrorMessage() {
        return message;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public RestClient() {
        params = new HashMap<>();
        headers = new HashMap<>();

    }

    public RestClient(String url) {
        params = new HashMap<>();
        headers = new HashMap<>();
    /*    if (Utility.getAccessToken(MyApplication.getContext()).trim().length() > 0) {
            headers.put("X-Token",   Utility.getAccessToken(MyApplication.getContext()));
        }
        headers.put("X-API-KEY", "9C87E5B6273B53C57A3D258DFA6D34FC");
*/
        this.url = url;
    }

    public void addParam(String name, String value) {
        params.put(name, value);
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public void execute(int method) throws Exception {

        switch (method) {
            case GET: {

                Log.i(" Rest", "Execute GET  called");
                url = url.replace("%7C %7C", "%7C");
                url = url.replace("%7C&", "&");
                url = url.replace("%7C%7C", "%7C");
                url = url.replace("&&", "&");
                url = url.replace("=%7C", "=");

                ApiInterface apiService2 = RestClient.getClient2().create(ApiInterface.class);
                Log.d("getApi Url", " " + url);
                Call<JsonObject> callGetType;

                callGetType = apiService2.getJObjFromServer(url, headers);

                try {
                    Response<JsonObject> strResponse = callGetType.execute();
                    responseCode = strResponse.code();
                    if (strResponse.isSuccessful()) {
                        response = strResponse.body().toString();
                        Log.i("GETResponseSuccess ", "" + response);
                    } else {
                        response = strResponse.errorBody().string();
                        Log.i("GETResponseError ", "" + response);
                    }
                } catch (IOException e) {
                    response = "{\"error\":\"IOException\"}";
                    e.printStackTrace();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                break;
            }
        }
    }

    public JSONObject imageUpload(String url, String myfilepath, String imgFileName) {
        File file = new File(myfilepath);
        ApiInterface apiService2 = RestClient.getClient2().create(ApiInterface.class);
        RequestBody reqFile = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", imgFileName, reqFile);
        JSONObject imgResp = new JSONObject();
        Call<ResponseBody> req = apiService2.postImage(url, body, headers);
        try {
            Response<ResponseBody> imgRespose = req.execute();
            responseCode = imgRespose.code();
            if (imgRespose.isSuccessful()) {
                imgResp = new JSONObject(imgRespose.body().string());
                Log.i("SendImageSuccess: ", imgResp + "");
            } else {
                imgResp = new JSONObject(imgRespose.errorBody().string());
                Log.i("SendImageError: ", imgResp + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imgResp;
    }

    public static Retrofit getClient2() {
        if (retrofit2 == null) {
            /*HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);*/
            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .addInterceptor(new Interceptor() {
                        @Override
                        public okhttp3.Response intercept(Chain chain) throws IOException {
                      /*      Request.Builder retRequest = chain.request().newBuilder();
                            retRequest.header("Accept", "application/json");
                            retRequest.header("Content-Type", "application/x-www-form-urlencoded");
                      */
                            Request.Builder retRequest = chain.request().newBuilder();
                            createRetroRequest(retRequest, "application/x-www-form-urlencoded");
                            return chain.proceed(retRequest.build());
                        }
                    })
                    .addInterceptor(new CurlInterceptor(new Loggable() {
                        @Override
                        public void log(String message) {
                            if (BuildConfig.DEBUG) {
                                Log.v("Ok2Curl", message);
                            }
                        }
                    }))
                    .build();


            retrofit2 = new Retrofit.Builder()
                    .baseUrl(Url.baseUrl)
                    .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().serializeNulls().create()))
                    .client(httpClient)
                    .build();
        }
        return retrofit2;
    }


    public JSONObject postJDataToServer(String postUrl, JSONObject jPairs) {
        JSONObject retroResponseObj = null;
        ApiInterface apiService2 = RestClient.getClient2().create(ApiInterface.class);
        JsonObject gsonObject = null;

        try {
            JsonParser jsonParser = new JsonParser();
            gsonObject = (JsonObject) jsonParser.parse(jPairs.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Call<JsonObject> callPostURL = apiService2.allJPostCall(postUrl, gsonObject, headers);
        try {
            Response<JsonObject> strResponse = callPostURL.execute();
            responseCode = strResponse.code();
            if (strResponse.isSuccessful()) {

                retroResponseObj = new JSONObject(strResponse.body().toString());
                Log.i("ResponseSuccess", retroResponseObj + "");
            } else {
                retroResponseObj = new JSONObject(strResponse.errorBody().string());
                Log.i("postJDataResponseError ", responseCode + "\n" + retroResponseObj + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return retroResponseObj;
    }

    public JSONObject updateJDataToServer(String putUrl, JSONObject jsonPairs) {
        ApiInterface apiService2 = RestClient.getClient2().create(ApiInterface.class);
        JsonObject gsonObject = null;
        try {
            JsonParser jsonParser = new JsonParser();
            gsonObject = (JsonObject) jsonParser.parse(jsonPairs.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        Call<JsonObject> callEdAd = apiService2.allJPutCall(putUrl, gsonObject, headers);
        Log.i("updateJDataURL ", putUrl + "");
        Log.i("updateJDataRequest ", gsonObject + "");
        JSONObject responseObj = null;
        try {
            Response<JsonObject> strResponse = callEdAd.execute();
            responseCode = strResponse.code();
            responseObj = new JSONObject();
            if (strResponse.isSuccessful()) {
                responseObj = new JSONObject(strResponse.body().toString());
                Log.i("ResponseSuccess", responseObj + "");
            } else {
                responseObj = new JSONObject(strResponse.errorBody().string());
                Log.i("ResponseError", responseCode + "\n" + responseObj + "");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return responseObj;
    }

    public String getJDataToServer(String putUrl) {
        ApiInterface apiService2 = RestClient.getClient2().create(ApiInterface.class);
        Response<JsonArray> strResponse = null;
        Log.i("getJDataURL ", putUrl + "");

        Call<JsonArray> callEdAd = apiService2.allJGetCall(putUrl, headers);

        String responsearray = "";
        try {
            strResponse = callEdAd.execute();
            responseCode = strResponse.code();
            if (strResponse.isSuccessful()) {
                responsearray = strResponse.body() + "";
                Log.i("getJDataResponseSuccess", responsearray + "");
            } else {
                responsearray = null;
                Log.i("getJDataResponseError", strResponse.errorBody() + "");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responsearray;
    }


    public int getintegerfromserver(String putUrl) {
        ApiInterface apiService2 = RestClient.getClient2().create(ApiInterface.class);
        Response<Integer> strResponse = null;
        Log.i("getJDataURL ", putUrl + "");

        Call<Integer> callEdAd = apiService2.getintObjFromServer(putUrl, headers);

        int responsearray = 0;
        try {
            strResponse = callEdAd.execute();
            responseCode = strResponse.code();
            if (strResponse.isSuccessful()) {
                responsearray = strResponse.body();
                Log.i("getJDataResponseSuccess", responsearray + "");
            } else {
                responsearray = 0;
                Log.i("getJDataResponseError ", strResponse.errorBody() + "");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responsearray;
    }

    public JSONObject validateUser(String url, Map<String, String> params, String myfilepath, String imgFileName) {
        // url = "http://192.168.1.111/amit/156-PHP/ImageUpload.php";
        ApiInterface apiService2 = RestClient.getClient2().create(ApiInterface.class);
        File file = new File(myfilepath);

        //  RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("gym_image", file.getName(), requestFile);

        JSONObject imgResp = new JSONObject();

        RequestBody gym_name = RequestBody.create(MediaType.parse("text/plain"), params.get("gym_name"));
        RequestBody gym_address = RequestBody.create(MediaType.parse("text/plain"), params.get("gym_address"));
        RequestBody gym_mobile = RequestBody.create(MediaType.parse("text/plain"), params.get("gym_mobile"));
        RequestBody imagename = RequestBody.create(MediaType.parse("text/plain"), imgFileName);
        Call<ResponseBody> req = apiService2.gymRegister(url, body, headers, gym_name, gym_address, gym_mobile, imagename);

        try {
            Response<ResponseBody> imgRespose = req.execute();
            responseCode = imgRespose.code();
            if (imgRespose.isSuccessful()) {
                imgResp = new JSONObject(imgRespose.body().string());
                Log.i("SendImageSuccess: ", imgResp + "");
            } else {
                imgResp = new JSONObject(imgRespose.errorBody().string());
                Log.i("SendImageError: ", imgResp + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imgResp;
    }

    public JSONObject UpdateGymInfo(String url, Map<String, String> params, String myfilepath, String imgFileName) {
        // url = "http://192.168.1.111/amit/156-PHP/ImageUpload.php";
        ApiInterface apiService2 = RestClient.getClient2().create(ApiInterface.class);
        File file = new File(myfilepath);
        MultipartBody.Part body = null;
        if (myfilepath.trim().length() != 0) {
            //  RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            body = MultipartBody.Part.createFormData("gym_image", file.getName(), requestFile);
        }
        JSONObject imgResp = new JSONObject();

        RequestBody gym_name = RequestBody.create(MediaType.parse("text/plain"), params.get("gym_name"));
        RequestBody gym_address = RequestBody.create(MediaType.parse("text/plain"), params.get("gym_address"));
        RequestBody gym_mobile = RequestBody.create(MediaType.parse("text/plain"), params.get("gym_mobile"));
        RequestBody gym_id = RequestBody.create(MediaType.parse("text/plain"), params.get("gym_id"));
        RequestBody imagename = RequestBody.create(MediaType.parse("text/plain"), imgFileName);
        RequestBody _method = RequestBody.create(MediaType.parse("text/plain"), "PUT");

        Call<ResponseBody> req = apiService2.updateGymInfo(url, body, headers, gym_name, gym_address, gym_mobile, gym_id, imagename, _method);

        try {
            Response<ResponseBody> imgRespose = req.execute();
            responseCode = imgRespose.code();
            if (imgRespose.isSuccessful()) {
                imgResp = new JSONObject(imgRespose.body().string());
                Log.i("SendImageSuccess: ", imgResp + "");
            } else {
                imgResp = new JSONObject(imgRespose.errorBody().string());
                Log.i("SendImageError: ", imgResp + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imgResp;
    }

    public JSONObject UseerRegister(String url, Map<String, String> params, String myfilepath, String imgFileName) {
        // url = "http://192.168.1.111/amit/156-PHP/ImageUpload.php";
        ApiInterface apiService2 = RestClient.getClient2().create(ApiInterface.class);
        File file = new File(myfilepath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("user_image", file.getName(), requestFile);
        JSONObject imgResp = new JSONObject();

        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), params.get("user_name"));
        RequestBody parentID = RequestBody.create(MediaType.parse("text/plain"), params.get("user_email"));
        RequestBody documetntType = RequestBody.create(MediaType.parse("text/plain"), params.get("user_mobile"));
        RequestBody user_password = RequestBody.create(MediaType.parse("text/plain"), params.get("user_password"));
        RequestBody user_type = RequestBody.create(MediaType.parse("text/plain"), params.get("user_type"));
        RequestBody gym_id = RequestBody.create(MediaType.parse("text/plain"), params.get("gym_id"));

        // RequestBody imagename = RequestBody.create(MediaType.parse("text/plain"), imgFileName);
        Call<ResponseBody> req = apiService2.userRegister(url, body, headers, userId, parentID, documetntType, user_password, user_type, gym_id);

        try {
            Response<ResponseBody> imgRespose = req.execute();
            responseCode = imgRespose.code();
            if (imgRespose.isSuccessful()) {
                imgResp = new JSONObject(imgRespose.body().string());
                Log.i("SendImageSuccess: ", imgResp + "");
            } else {
                imgResp = new JSONObject(imgRespose.errorBody().string());
                Log.i("SendImageError: ", imgResp + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imgResp;
    }

    public JSONObject EditUserinfo(String url, Map<String, String> params, String myfilepath, String imgFileName) {
        // url = "http://192.168.1.111/amit/156-PHP/ImageUpload.php";
        ApiInterface apiService2 = RestClient.getClient2().create(ApiInterface.class);
        File file = new File(myfilepath);
        MultipartBody.Part body = null;
        if (myfilepath.trim().length() != 0) {
            //  RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        }
        JSONObject imgResp = new JSONObject();

        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), params.get("email"));
        RequestBody parentID = RequestBody.create(MediaType.parse("text/plain"), params.get("firstName"));
        RequestBody documetntType = RequestBody.create(MediaType.parse("text/plain"), params.get("lastName"));
        RequestBody user_id = RequestBody.create(MediaType.parse("text/plain"), params.get("contactNumber"));
        RequestBody _method = RequestBody.create(MediaType.parse("text/plain"), "POST");
        Call<ResponseBody> req = apiService2.EditUserInfo(url, body, headers, userId, parentID, documetntType, user_id, _method);

        try {
            Response<ResponseBody> imgRespose = req.execute();
            responseCode = imgRespose.code();
            if (imgRespose.isSuccessful()) {
                imgResp = new JSONObject(imgRespose.body().string());
                Log.i("SendImageSuccess: ", imgResp + "");
            } else {
                imgResp = new JSONObject(imgRespose.errorBody().string());
                Log.i("SendImageError: ", imgResp + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imgResp;
    }

    public JSONObject uploadImageOrFileWithPartMap(String url, Map<String, RequestBody> params, String myfilepath, String MultipartParamName) {
        // url = "http://192.168.1.111/amit/156-PHP/ImageUpload.php";
        ApiInterface apiService2 = RestClient.getClient2().create(ApiInterface.class);
        File file = new File(myfilepath);
        MultipartBody.Part body = null;
        if (myfilepath.trim().length() != 0) {
            //  RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            if (MultipartParamName.trim().length() > 0) {
                body = MultipartBody.Part.createFormData(MultipartParamName, file.getName(), requestFile);

            } else {
                body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
            }
        }
        JSONObject imgResp = new JSONObject();

        //  RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), params.get("email"));
        Call<ResponseBody> req = apiService2.uploadImageOrFileWithPartMap(url, body, headers, params);

        try {
            Response<ResponseBody> imgRespose = req.execute();
            responseCode = imgRespose.code();
            if (imgRespose.isSuccessful()) {
                imgResp = new JSONObject(imgRespose.body().string());
                Log.i("SendImageSuccess: ", imgResp + "");
            } else {
                imgResp = new JSONObject(imgRespose.errorBody().string());
                Log.i("SendImageError: ", imgResp + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imgResp;
    }

    public JSONObject uploadImageQrCodeWithPartMap(String url, Map<String, RequestBody> params, String myfilepath, String qrCodePath) {
        // url = "http://192.168.1.111/amit/156-PHP/ImageUpload.php";
        ApiInterface apiService2 = RestClient.getClient2().create(ApiInterface.class);
        File file = new File(myfilepath);
        MultipartBody.Part body = null;
        if (myfilepath.trim().length() != 0) {
            //  RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            body = MultipartBody.Part.createFormData("cover_letter", file.getName(), requestFile);
        }
        File fileqr = new File(qrCodePath);
        MultipartBody.Part bodyqr = null;
        if (qrCodePath.trim().length() != 0) {
            //  RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), fileqr);
            bodyqr = MultipartBody.Part.createFormData("resume", fileqr.getName(), requestFile);
        }
        JSONObject imgResp = new JSONObject();

        //  RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), params.get("email"));
        Call<ResponseBody> req = apiService2.uploadImageQrCodeWithPartMap(url, body, bodyqr, headers, params);

        try {
            Response<ResponseBody> imgRespose = req.execute();
            responseCode = imgRespose.code();
            if (imgRespose.isSuccessful()) {
                imgResp = new JSONObject(imgRespose.body().string());
                Log.i("SendImageSuccess: ", imgResp + "");
            } else {
                imgResp = new JSONObject(imgRespose.errorBody().string());
                Log.i("SendImageError: ", imgResp + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imgResp;
    }

    public static String getBase64FromPath(String path) {
        String base64 = "";
        try {
            File file = new File(path);
            byte[] buffer = new byte[(int) file.length() + 100];
            @SuppressWarnings("resource")
            int length = new FileInputStream(file).read(buffer);
            base64 = Base64.encodeToString(buffer, 0, length,
                    Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return base64;
    }

    public JSONObject postDataToServer(String postUrl, Map<String, String> nameValuePairs) {
        JSONObject retroResponseObj = null;
        Log.d("url", postUrl);
        ApiInterface apiService2 = RestClient.getClient2().create(ApiInterface.class);
        Call<JsonObject> callPostURL = apiService2.oldPostCall(postUrl, nameValuePairs, headers);

        try {
            Response<JsonObject> strResponse = callPostURL.execute();
            responseCode = strResponse.code();
            if (strResponse.isSuccessful()) {
                retroResponseObj = new JSONObject(strResponse.body().toString());
                Log.i("PostResponseSuccess ", retroResponseObj + "");
            } else {
                retroResponseObj = new JSONObject(strResponse.errorBody().string());
                Log.i("PostResponseError ", responseCode + "\n" + retroResponseObj + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        response = "" + retroResponseObj;
        return retroResponseObj;
    }

    public JSONObject PostBase64FromdataToServer(String postUrl, Map<String, String> nameValuePairs) {
        JSONObject loginResObj = null;
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request.Builder retRequest = chain.request().newBuilder();
                        createRetroRequest(retRequest, "application/x-www-form-urlencoded");
                        return chain.proceed(retRequest.build());
                    }
                })
                //.addInterceptor(logging)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Url.baseUrl)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().serializeNulls().create()))
                .client(httpClient)
                .build();

        ApiInterface apiService2 = retrofit.create(ApiInterface.class);
        JSONObject retroResponseObj = null;
        Call<JsonObject> callPostURL = apiService2.oldPostCall(postUrl, nameValuePairs, headers);
        try {
            Response<JsonObject> strResponse = callPostURL.execute();
            responseCode = strResponse.code();
            if (strResponse.isSuccessful()) {
                retroResponseObj = new JSONObject(strResponse.body().toString());
                Log.i("PostResponseSuccess ", retroResponseObj + "");
            } else {
                retroResponseObj = new JSONObject(strResponse.errorBody().string());
                Log.i("PostResponseError ", responseCode + "\n" + retroResponseObj + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        response = "" + retroResponseObj;
        return loginResObj;
    }

    public JSONObject postBodyParamdataToServer(String Urlss, String rebBody) {

        //HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        //logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        JSONObject loginResObj = null;
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request.Builder retRequest = chain.request().newBuilder();
                        createRetroRequest(retRequest, "application/x-www-form-urlencoded");
                        return chain.proceed(retRequest.build());
                    }
                })
                //.addInterceptor(logging)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Url.baseUrl)
                //.addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
                .client(httpClient)
                .build();

        ApiInterface apiService2 = retrofit.create(ApiInterface.class);
        headers.clear();
        RequestBody body = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), rebBody);

        Call<JsonObject> callPostURL = apiService2.postReqBodyparamCall(Urlss, body, headers);
        try {
            Response<JsonObject> strResponse = callPostURL.execute();
            responseCode = strResponse.code();
            if (strResponse.isSuccessful()) {

                loginResObj = new JSONObject(strResponse.body().toString());
                //{"success":false,"userExists":true,"userStatus":1,"error":{"message":"Email already registered."}}
                Log.i("RegisterSuccess  ", loginResObj + "");
                response = "" + loginResObj;
            } else {

                try {
                    loginResObj = new JSONObject(strResponse.errorBody().string());
                    Log.i("PostResponseError ", responseCode + "\n" + loginResObj + "");
                    response = "" + loginResObj;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Log.i("ResendConfirmPostcall ", loginResObj + "");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return loginResObj;
    }

    private static void createRetroRequest(Request.Builder retRequest, String cntntType) {
        retRequest.header("Accept", "application/json");
        retRequest.header("Content-Type", cntntType);
        retRequest.header("Accept-Language", "en");
        /*JSONObject deviceinfo = new JSONObject();
        try {
            deviceinfo.put("appBuildVersion", BuildConfig.VERSION_CODE);
            deviceinfo.put("height", Utility.actualHeight);
            deviceinfo.put("width", Utility.actualWidth);
            deviceinfo.put("osVersion", Build.VERSION.RELEASE);
            deviceinfo.put("userId", Utility.getUserid(MyApplication.getContext()));
            deviceinfo.put("scale", "");
            deviceinfo.put("os", "android");
            deviceinfo.put("modelName", Build.MODEL);
            deviceinfo.put("appVersion", BuildConfig.VERSION_NAME);
            deviceinfo.put("bundleID", MyApplication.getContext().getPackageName());
            deviceinfo.put("appName", MyApplication.getContext().getString(R.string.app_name));

        } catch (JSONException e) {
            e.printStackTrace();
        }
      //  Log.d("deviceInfo: ","" + deviceinfo);
        retRequest.header("deviceInfo", "" + deviceinfo);
*/
//{\"appBuildVersion\":\"6\",\"height\":844,\"width\":390,\"osVersion\":\"14.5\"
// ,\"branchId\":\"2\",\"userId\":\"8\",\"role\":3,\"scale\":3,\"os\":\"ios\",
// \"modelName\":\"Simulator iPhone 12 Pro\",\"appVersion\":\"4.0\",\"bundleID\":\"com.wip-lash.wip-lash\",\"appName\":\"Hydraulix USA\"}"


        /*retRequest.header("User-Agent", "Hydraulix/" + BuildConfig.VERSION_NAME
                + " (" + BuildConfig.APPLICATION_ID + ";"
                + "build:" + BuildConfig.VERSION_CODE
                + "Android " + Build.VERSION.RELEASE + ")");*/
    }

    public JSONObject getSocialLoginXauthDatafromServer(String url, String rawString) {
        JSONObject loginResObj = null;
        //HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        //logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request.Builder retRequest = chain.request().newBuilder();
                        createRetroRequest(retRequest, "application/x-www-form-urlencoded");
                        return chain.proceed(retRequest.build());
                    }
                })
                //.addInterceptor(logging)
                .build();
        Retrofit retrofitSoc = new Retrofit.Builder()
                .baseUrl(Url.baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().serializeNulls().create()))
                .client(httpClient)
                .build();

        ApiInterface apiServiceSoc = retrofitSoc.create(ApiInterface.class);

        Call<JsonObject> callPostURL = apiServiceSoc.postStringBodyparamCall(url, rawString, headers);
        try {
            Response<JsonObject> strResponse = callPostURL.execute();
            responseCode = strResponse.code();
            if (strResponse.isSuccessful()) {
                loginResObj = new JSONObject(strResponse.body().toString());

                Log.i("SocialResponse  ", loginResObj + "");

                if (loginResObj != null && loginResObj.has("token")) {

                }
            } else {
            }
            Log.i("SocialResponseError ", loginResObj + "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return loginResObj;
    }

    public JSONObject updateDataToServer(String putUrl, Map<String, String> nameValuePairs) {
        ApiInterface apiService2 = RestClient.getClient2().create(ApiInterface.class);
        Call<JsonObject> callEdAd = apiService2.allPutCall(putUrl, nameValuePairs, headers);
        JSONObject responseObj = null;
        try {
            Response<JsonObject> strResponse = callEdAd.execute();
            responseCode = strResponse.code();
            responseObj = new JSONObject();
            if (strResponse.isSuccessful()) {
                responseObj = new JSONObject(strResponse.body().toString());
                Log.i("PUTResponseSuccess ", responseObj + "");
            } else {
                responseObj = new JSONObject(strResponse.errorBody().string());
                Log.i("PUTResponseError ", responseCode + "\n" + responseObj + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseObj;
    }


    public JSONObject deleteUserSession(String urlToDelImg, boolean IspassAcesstokenInheader) {
        ApiInterface apiService2 = RestClient.getClient2().create(ApiInterface.class);
        Call<JsonObject> callDelOneImg;
        Log.i("DELRequestURL ", urlToDelImg + "");

        callDelOneImg = apiService2.removeItemfromServer(urlToDelImg, headers);
        JSONObject delResObj = new JSONObject();
        try {
            Response<JsonObject> strResponse1 = callDelOneImg.execute();
            responseCode = strResponse1.code();
            if (strResponse1.isSuccessful()) {
                delResObj = new JSONObject(strResponse1.body().toString());
                Log.i("DELResponseSuccess ", delResObj + "");
            } else {
                delResObj = new JSONObject(strResponse1.errorBody().string());
                Log.i("DELResponseError ", responseCode + "\n" + delResObj + "");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return delResObj;
    }

    public void setGetParams(String urlParams) {
        url += urlParams;
        Log.i("URL with params: ", url);
    }

    public String getType(final String filename) {
        // There does not seem to be a way to ask the OS or file itself for this
        // information, so unfortunately resorting to extension sniffing.

        int pos = filename.lastIndexOf('.');
        if (pos != -1) {
            String ext = filename.substring(filename.lastIndexOf('.') + 1,
                    filename.length());

            if (ext.equalsIgnoreCase("pdf"))
                return "application/pdf";
            if (ext.equalsIgnoreCase("htm"))
                return "text/html";
            if (ext.equalsIgnoreCase("html"))
                return "text/html";
            if (ext.equalsIgnoreCase("png"))
                return "image/png";
            if (ext.equalsIgnoreCase("jpg"))
                return "image/jpeg";
            if (ext.equalsIgnoreCase("jpe"))
                return "image/jpeg";
            if (ext.equalsIgnoreCase("jpeg"))
                return "image/jpeg";
            if (ext.equalsIgnoreCase("gif"))
                return "image/gif";
            if (ext.equalsIgnoreCase("bmp"))
                return "image/x-ms-bmp";
            if (ext.equalsIgnoreCase("WBMP"))
                return "image/image/vnd.wap.wbmp";
            if (ext.equalsIgnoreCase("WEBP"))
                return "image/webp";
            if (ext.equalsIgnoreCase("DOC"))
                return "application/msword";
            if (ext.equalsIgnoreCase("DOCX"))
                return "application/msword";
            if (ext.equalsIgnoreCase("XLS"))
                return "application/vnd.ms-excel";
            if (ext.equalsIgnoreCase("PPT"))
                return "application/mspowerpoint";
            if (ext.equalsIgnoreCase("ZIP"))
                return "application/zip";
            if (ext.equalsIgnoreCase("rar"))
                return "application/rar";
            if (ext.equalsIgnoreCase("txt"))
                return "text/plain";
            if (ext.equalsIgnoreCase("cfg"))
                return "text/plain";
            if (ext.equalsIgnoreCase("csv"))
                return "text/plain";
            if (ext.equalsIgnoreCase("conf"))
                return "text/plain";
            if (ext.equalsIgnoreCase("rc"))
                return "text/plain";
            if (ext.equalsIgnoreCase("xml"))
                return "text/xml";
            if (ext.equalsIgnoreCase("mp3"))
                return "audio/mpeg";
            if (ext.equalsIgnoreCase("aac"))
                return "audio/aac";
            if (ext.equalsIgnoreCase("wav"))
                return "audio/wav";
            if (ext.equalsIgnoreCase("ogg"))
                return "audio/ogg";
            if (ext.equalsIgnoreCase("mid"))
                return "audio/midi";
            if (ext.equalsIgnoreCase("midi"))
                return "audio/midi";
            if (ext.equalsIgnoreCase("wma"))
                return "audio/x-ms-wma";
            if (ext.equalsIgnoreCase("mp4"))
                return "video/mp4";
            if (ext.equalsIgnoreCase("avi"))
                return "video/x-msvideo";
            if (ext.equalsIgnoreCase("wmv"))
                return "video/x-ms-wmv";
            if (ext.equalsIgnoreCase("apk"))
                return "application/vnd.android.package-archive";
            // Additions and corrections are welcomed.
        }
        return "*/*";
    }

    public JSONObject GetFromDataFromServer(String url) {
        // url = "http://192.168.1.111/amit/156-PHP/ImageUpload.php";
        ApiInterface apiService2 = RestClient.getClient2().create(ApiInterface.class);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("Field", "All Field")
                .build();
        Call<JsonObject> req = apiService2.getDataServer(url, headers,requestBody);

        JSONObject imgResp = null;
        try {
            Response<JsonObject> imgRespose = req.execute();
            responseCode = imgRespose.code();
            if (imgRespose.isSuccessful()) {
                imgResp = new JSONObject(imgRespose.body().toString());
                Log.i("SendImageSuccess: ", imgResp + "");
            } else {
                imgResp = new JSONObject(imgRespose.errorBody().toString());
                Log.i("SendImageError: ", imgResp + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imgResp;
    }
}