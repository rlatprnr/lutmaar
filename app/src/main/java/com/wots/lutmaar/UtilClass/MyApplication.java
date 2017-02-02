package com.wots.lutmaar.UtilClass;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import com.digits.sdk.android.Digits;
import com.facebook.FacebookSdk;
import com.google.android.gms.analytics.ExceptionReporter;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.wots.lutmaar.R;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

public class MyApplication extends Application {
    private Uri PicUri;
    private Tracker mTracker;

    public Uri getPicUri() {
        return PicUri;
    }

    public void setPicUri(Uri picUri) {
        PicUri = picUri;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        TwitterAuthConfig authConfig =
                new TwitterAuthConfig(ConstantClass.TWconsumerKey, ConstantClass.TWconsumerSecret);
        Fabric.with(this, new TwitterCore(authConfig), new Digits(), new TweetComposer());
        FacebookSdk.sdkInitialize(getApplicationContext());

    }
    public synchronized Tracker getTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            mTracker = analytics.newTracker(R.xml.analytics);
            mTracker.enableAutoActivityTracking(true);
            ArrayList<String> packages = new ArrayList<String>();
            packages.add("com.wots.lutmaar.Tracker");
            //if (Thread.getDefaultUncaughtExceptionHandler() instanceof ExceptionReporter) {
            Log.e("Log1", packages.toString());
            ExceptionReporter reporter = new ExceptionReporter(mTracker, Thread.getDefaultUncaughtExceptionHandler(), this);
            reporter.setExceptionParser(new AnalyticsExceptionParser(this, packages));
            Thread.setDefaultUncaughtExceptionHandler(reporter);
            Log.e("Log2", reporter.toString());
        }
        return mTracker;
    }


}
