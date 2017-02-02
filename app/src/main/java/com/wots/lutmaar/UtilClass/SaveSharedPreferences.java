package com.wots.lutmaar.UtilClass;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Super Star on 03-06-2015.
 */
public class SaveSharedPreferences {

    static final String LoginObj = "LoginObject";
    static final String TOKON = "tokon";
    static final String Cookie = "cookie";
    static final String UserID = "UserID";
    static final String UID = "UID";
    static final String UserName = "UserName";
    static final String PassWord = "PassWord";
    static final String CityName = "City";
    static final String Message = "no";

    static final String WCLogin = "LoginLevel";
    static final String WCHome = "HomeLevel";
    static final String WCDealPost = "PostADeal";
    static final String WCSearch = "Search";
    static final String WCDealDetails = "DealDetails";


    //

    static SharedPreferences getSharedPreferences(Context ctx) {

        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setLoginObj(Context ctx, String tokon) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(LoginObj, tokon);
        editor.commit();
    }
    public static void setTokon(Context ctx, String tokon) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(TOKON, tokon);
        editor.commit();
    }
    public static void setCookie(Context ctx, String cookie) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(Cookie, cookie);
        editor.commit();
    }
    public static void setUserID(Context ctx, String usetID) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(UserID, usetID);
        editor.commit();
    }
    public static void setUserName(Context ctx, String userName) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(UserName, userName);
        editor.commit();
    }
    public static void setUID(Context ctx, String usetID) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(UID, usetID);
        editor.commit();
    }
    public static void setPassWord(Context ctx, String usetID) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PassWord, usetID);
        editor.commit();
    }
    public static void setCityName(Context ctx, String cityName) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(CityName, cityName);
        editor.commit();
    }
    public static void setWCLogin(Context ctx, String cityName) {
        SharedPreferences.Editor WelComeEditor = getSharedPreferences(ctx).edit();
        WelComeEditor.putString(WCLogin, cityName);
        WelComeEditor.commit();
    }
    public static void setWCHome(Context ctx, String cityName) {
        SharedPreferences.Editor WelComeEditor = getSharedPreferences(ctx).edit();
        WelComeEditor.putString(WCHome, cityName);
        WelComeEditor.commit();
    }
    public static void setWCSearch(Context ctx, String cityName) {
        SharedPreferences.Editor WelComeEditor = getSharedPreferences(ctx).edit();
        WelComeEditor.putString(WCSearch, cityName);
        WelComeEditor.commit();
    }
    public static void setWCDealPost(Context ctx, String cityName) {
        SharedPreferences.Editor WelComeEditor = getSharedPreferences(ctx).edit();
        WelComeEditor.putString(WCDealPost, cityName);
        WelComeEditor.commit();
    }
    public static void setWCDealDetails(Context ctx, String cityName) {
        SharedPreferences.Editor WelComeEditor = getSharedPreferences(ctx).edit();
        WelComeEditor.putString(WCDealDetails, cityName);
        WelComeEditor.commit();
    }
    public static void setMessage(Context ctx, String message) {
        SharedPreferences.Editor WelComeEditor = getSharedPreferences(ctx).edit();
        WelComeEditor.putString(Message, message);
        WelComeEditor.commit();
    }

    public static String getLoginObj(Context ctx) {
        return getSharedPreferences(ctx).getString(LoginObj, "");
    }
    public static String getUserID(Context ctx) {
        return getSharedPreferences(ctx).getString(UserID, "");
    }
    public static String getUserName(Context ctx) {
        return getSharedPreferences(ctx).getString(UserName, "");
    }
    public static String getUID(Context ctx) {
        return getSharedPreferences(ctx).getString(UID, "");
    }
    public static String getPassWord(Context ctx) {
        return getSharedPreferences(ctx).getString(PassWord, "");
    }
    public static String getTokon (Context ctx) {
        return getSharedPreferences(ctx).getString(TOKON, "");
    }
    public static String getCookie (Context ctx) {
        return getSharedPreferences(ctx).getString(Cookie, "");
    }
    public static String getCityName(Context ctx) {
        return getSharedPreferences(ctx).getString(CityName, "");
    }

    public static String getWCLogin(Context ctx) {
         return getSharedPreferences(ctx).getString(WCLogin, "");
    }

    public static String getWCHome(Context ctx) {
        return getSharedPreferences(ctx).getString(WCHome, "");
    }

    public static String getWCDealPost(Context ctx) {
        return getSharedPreferences(ctx).getString(WCDealPost, "");
    }

    public static String getWCSearch(Context ctx) {
        return getSharedPreferences(ctx).getString(WCSearch, "");
    }

    public static String getWCDealDetails(Context ctx) {
        return getSharedPreferences(ctx).getString(WCDealDetails, "");
    }
    public static String getMessage(Context ctx) {
        return getSharedPreferences(ctx).getString(Message, "");
    }

    public static void cleardata(Context ctx) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(LoginObj, "");
        editor.putString(TOKON, "");
        editor.putString(Cookie, "");
        editor.putString(UserID, "");
        editor.putString(CityName, "");
      //  editor.clear(); //clear all stored data
        editor.commit();
    }
}
