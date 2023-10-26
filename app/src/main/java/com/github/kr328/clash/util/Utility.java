package com.github.kr328.clash.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.github.kr328.clash.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {
    private static final String PASSWORD_PATTERN = "^(((?=.*[A-Z])(?=.*?[0-9]))(.{6,15}))$";
     public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidpassword(final String password) {
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setAccessToken(Context context, String accesstoken) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("access_token", accesstoken);
        editor.commit();

    }
    public static String getAccessToken(Context context) {

        return getSharedPreferences(context).getString("access_token", "");
    }

    public static void setAuthData(Context context, String accesstoken) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("auth_data", accesstoken);
        editor.commit();

    }
    public static String getAuthData(Context context) {

        return getSharedPreferences(context).getString("auth_data", "");
    }



    public static String getUserid(Context context) {

        return getSharedPreferences(context).getString("Userid", "" + 0);

    }

    public static void setUserid(Context context, String Userid) {

        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("Userid", Userid);
        editor.commit();
    }

    public static String getNotificaitonToken(Context context) {

        return getSharedPreferences(context).getString("NotificaitonToken", "");

    }

    public static void setNotificaitonToken(Context context, String NotificaitonToken) {

        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("NotificaitonToken", NotificaitonToken);
        editor.commit();
    }



    public static void setUserDetails(Context context, String userDetails) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("userDetails", userDetails);
        editor.commit();
    }

    public static String getUserDetails(Context context) {

        return getSharedPreferences(context).getString("userDetails", "");
    }


    public static void setUserLogin(Context context, boolean isLogin) {

        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean("user_login", isLogin);
        editor.commit();
    }

    public static boolean isUserLogin(Context context) {

        return getSharedPreferences(context).getBoolean("user_login", false);
    }


    public static void okDialog(Context con, String taskTitle, String msg) {
      /*  AlertDialog.Builder builder = new AlertDialog.Builder(con, R.style.DialogTheme);
        builder.setMessage(msg).setTitle(taskTitle)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();*/


        Dialog exitDialog = new Dialog(con);
        exitDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        exitDialog.setContentView(R.layout.dialog_ok);
        exitDialog.setCancelable(true);

        int width = (int) (con.getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (con.getResources().getDisplayMetrics().heightPixels * 0.90);

        exitDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
        exitDialog.getWindow().setGravity(Gravity.CENTER);

        TextView tvmsgTitle = (TextView) exitDialog.findViewById(R.id.tvmsgTitle);
        tvmsgTitle.setText(taskTitle);

        TextView tvmsgtxt = (TextView) exitDialog.findViewById(R.id.tvmsgtxt);
        tvmsgtxt.setText(msg);

        TextView tvOk = (TextView) exitDialog.findViewById(R.id.tvOk);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitDialog.dismiss();
            }

        });
        exitDialog.show();

    }

}
