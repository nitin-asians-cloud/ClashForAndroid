package com.github.kr328.clash;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.kr328.clash.design.model.ProfileProvider;
import com.github.kr328.clash.rest_client.RestClient;
import com.github.kr328.clash.util.Url;
import com.github.kr328.clash.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;


public class ActivityLogin extends Activity {
    EditText edtTxtLoginEmail, edtTxtLoginPassword;
    TextView tvLoginShowPWD, tvLoginForgotPWD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (Utility.isUserLogin(ActivityLogin.this)) {
            startActivity(new Intent(ActivityLogin.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }
        edtTxtLoginEmail = findViewById(R.id.edtTxtLoginEmail);
        edtTxtLoginPassword = findViewById(R.id.edtTxtLoginPassword);
        tvLoginShowPWD = findViewById(R.id.tvLoginShowPWD);

        tvLoginShowPWD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtTxtLoginPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                    tvLoginShowPWD.setText("Hide");
                    //Show Password
                    edtTxtLoginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    tvLoginShowPWD.setText("Show");
                    //Hide Password
                    edtTxtLoginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

                }
            }
        });


        findViewById(R.id.btnSubmitLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtTxtLoginEmail.getText().toString().trim().length() == 0) {
                    //       Toast.makeText(ActivityLogin.this, "Please Enter Email", Toast.LENGTH_LONG).show();
                    Utility.okDialog(ActivityLogin.this, "Login", "Please enter email");
                } else if (!Utility.isValidEmail(edtTxtLoginEmail.getText().toString())) {
                    //     Toast.makeText(ActivityLogin.this, "Please Enter Valid Email", Toast.LENGTH_LONG).show();
                    Utility.okDialog(ActivityLogin.this, "Login", "Please enter valid email");
                } else {
                    //startActivity(new Intent(ActivityLogin.this, MainActivity.class));
                    new UserLogin().execute();
                }
            }
        });
    }

    private class UserLogin extends AsyncTask<String, String, Boolean> {
        Dialog pDialog;
        JSONObject resObj;

        @Override
        protected void onPreExecute() {
            pDialog = new Dialog(ActivityLogin.this);
            pDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            pDialog.setContentView(R.layout.view_asyncdialog);
            pDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            RestClient userloginlient = new RestClient();
            HashMap<String, String> loginParam = new HashMap<>();
            loginParam.put("email", edtTxtLoginEmail.getText().toString());
            loginParam.put("password", edtTxtLoginPassword.getText().toString());
            resObj = userloginlient.postDataToServer(Url.loginUrl, loginParam);
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (pDialog != null) {
                pDialog.dismiss();
            }

            try {
                if (resObj != null && resObj.has("data")) {
                    Toast.makeText(ActivityLogin.this, "Login successful", Toast.LENGTH_SHORT).show();
                    JSONObject userInfo = resObj.getJSONObject("data");
                    Utility.setUserLogin(ActivityLogin.this, true);
                    Utility.setUserDetails(ActivityLogin.this, "" + userInfo);
                    Utility.setAccessToken(ActivityLogin.this, userInfo.getString("token"));
                    Utility.setAuthData(ActivityLogin.this, userInfo.getString("auth_data"));

                    startActivity(new Intent(ActivityLogin.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    // finish();

                } else if (resObj != null && resObj.has("message")) {
                    // Toast.makeText(ActivityLogin.this, "" + resObj.getString("message"), Toast.LENGTH_LONG).show();
                    Utility.okDialog(ActivityLogin.this, "LogIn", "" + resObj.getString("message"));
                } else {
                    // Toast.makeText(ActivityLogin.this, "" + resObj, Toast.LENGTH_LONG).show();
                    Utility.okDialog(ActivityLogin.this, "LogIn", "" + resObj);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}