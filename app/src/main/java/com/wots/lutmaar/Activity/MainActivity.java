package com.wots.lutmaar.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.wots.lutmaar.Interface.RestServices;
import com.wots.lutmaar.R;
import com.wots.lutmaar.UtilClass.ConstantClass;
import com.wots.lutmaar.UtilClass.MyApplication;
import com.wots.lutmaar.UtilClass.SaveSharedPreferences;
import com.wots.lutmaar.UtilClass.UtilityClass;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends ActionBarActivity implements LocationListener {
    //Class
    UtilityClass utilityClass;
    ConstantClass constantClass;
    private LocationManager locationManager;
    private LocationListener locListener;
    private Location mobileLocation;
    AlertDialog alertDialog;
    private Criteria criteria;
    Location location;
    MyApplication myApplication;

    //Interface
    RestServices api;
    RestAdapter restAdapter;

    //View
    @InjectView(R.id.etShowLocation)
    TextView etShowLocation;
    @InjectView(R.id.imageView)
    ImageView imageView;
    @InjectView(R.id.tvAddress)
    TextView tvAddress;
    @InjectView(R.id.tvWebView)
    WebView tvWebView;

    //Variable
    String provider;
    final int CAMERA_PIC_REQUEST = 0;
    private String imgPath, imgName, imgFile;
    String htmlCode = "<h3 class=\"sectionTitle\" style=\"margin: 0px; padding: 7px 0px 10px; border-width: 2px 0px 0px; border-top-style: solid; border-top-color: #333333; font-family: Museo, Helvetica, arial, san-serif; font-size: 17px; font-stretch: inherit; line-height: inherit; vertical-align: baseline; color: #333333;\">Specifications of Digiflip Pro XT 801 Tablet (Blue, 16 GB, Wi-Fi Only)</h3><table class=\"specTable\" style=\"margin: 0px 0px 30px; padding: 0px; border: 0px; font-family: arial, tahoma, verdana, sans-serif; font-size: 13px; font-stretch: inherit; line-height: 16.8999996185303px; vertical-align: baseline; border-collapse: collapse; border-spacing: 0px; width: 730px; color: #333333;\" cellspacing=\"0\"><tbody style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><tr style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><th class=\"groupHead\" style=\"margin: 0px; padding: 6px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top; text-transform: uppercase; background-color: #f2f2f2;\" colspan=\"2\">STORAGE</th></tr><tr style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><td class=\"specsKey\" style=\"margin: 0px; padding: 6px; border-width: 0px 1px 1px 0px; border-right-style: solid; border-bottom-style: dotted; border-right-color: #c9c9c9; border-bottom-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top; width: 170px;\">Memory Card Slot type</td><td class=\"specsValue\" style=\"margin: 0px; padding: 6px; border-width: 0px 0px 1px 1px; border-bottom-style: dotted; border-left-style: solid; border-bottom-color: #c9c9c9; border-left-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top;\">microSD</td></tr><tr style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><td class=\"specsKey\" style=\"margin: 0px; padding: 6px; border-width: 0px 1px 1px 0px; border-right-style: solid; border-bottom-style: dotted; border-right-color: #c9c9c9; border-bottom-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top; width: 170px;\">Expandable Storage Capacity</td><td class=\"specsValue\" style=\"margin: 0px; padding: 6px; border-width: 0px 0px 1px 1px; border-bottom-style: dotted; border-left-style: solid; border-bottom-color: #c9c9c9; border-left-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top;\">32 GB</td></tr><tr style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><td class=\"specsKey\" style=\"margin: 0px; padding: 6px; border-width: 0px 1px 1px 0px; border-right-style: solid; border-bottom-style: dotted; border-right-color: #c9c9c9; border-bottom-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top; width: 170px;\">Internal Storage</td><td class=\"specsValue\" style=\"margin: 0px; padding: 6px; border-width: 0px 0px 1px 1px; border-bottom-style: dotted; border-left-style: solid; border-bottom-color: #c9c9c9; border-left-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top;\">16 GB&nbsp;<span style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\">(Available user memory of the device may be lower than the stated memory due to default pre-installed apps and device OS)</span></td></tr></tbody></table><table class=\"specTable\" style=\"margin: 0px 0px 30px; padding: 0px; border: 0px; font-family: arial, tahoma, verdana, sans-serif; font-size: 13px; font-stretch: inherit; line-height: 16.8999996185303px; vertical-align: baseline; border-collapse: collapse; border-spacing: 0px; width: 730px; color: #333333;\" cellspacing=\"0\"><tbody style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><tr style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><th class=\"groupHead\" style=\"margin: 0px; padding: 6px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top; text-transform: uppercase; background-color: #f2f2f2;\" colspan=\"2\">MEMORY</th></tr><tr style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><td class=\"specsKey\" style=\"margin: 0px; padding: 6px; border-width: 0px 1px 1px 0px; border-right-style: solid; border-bottom-style: dotted; border-right-color: #c9c9c9; border-bottom-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top; width: 170px;\">RAM</td><td class=\"specsValue\" style=\"margin: 0px; padding: 6px; border-width: 0px 0px 1px 1px; border-bottom-style: dotted; border-left-style: solid; border-bottom-color: #c9c9c9; border-left-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top;\">1 GB</td></tr></tbody></table><table class=\"specTable\" style=\"margin: 0px 0px 30px; padding: 0px; border: 0px; font-family: arial, tahoma, verdana, sans-serif; font-size: 13px; font-stretch: inherit; line-height: 16.8999996185303px; vertical-align: baseline; border-collapse: collapse; border-spacing: 0px; width: 730px; color: #333333;\" cellspacing=\"0\"><tbody style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><tr style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><th class=\"groupHead\" style=\"margin: 0px; padding: 6px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top; text-transform: uppercase; background-color: #f2f2f2;\" colspan=\"2\">GENERAL</th></tr><tr style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><td class=\"specsKey\" style=\"margin: 0px; padding: 6px; border-width: 0px 1px 1px 0px; border-right-style: solid; border-bottom-style: dotted; border-right-color: #c9c9c9; border-bottom-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top; width: 170px;\">Brand</td><td class=\"specsValue\" style=\"margin: 0px; padding: 6px; border-width: 0px 0px 1px 1px; border-bottom-style: dotted; border-left-style: solid; border-bottom-color: #c9c9c9; border-left-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top;\">Digiflip Pro</td></tr><tr style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><td class=\"specsKey\" style=\"margin: 0px; padding: 6px; border-width: 0px 1px 1px 0px; border-right-style: solid; border-bottom-style: dotted; border-right-color: #c9c9c9; border-bottom-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top; width: 170px;\">In The Box</td><td class=\"specsValue\" style=\"margin: 0px; padding:\n" +
            " 6px; border-width: 0px 0px 1px 1px; border-bottom-style: dotted; border-left-style: solid; border-bottom-color: #c9c9c9; border-left-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top;\">Tablet, Earphone Connector, Quick Start Guide, OTG Cable, USB Cable, Charger</td></tr><tr style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><td class=\"specsKey\" style=\"margin: 0px; padding: 6px; border-width: 0px 1px 1px 0px; border-right-style: solid; border-bottom-style: dotted; border-right-color: #c9c9c9; border-bottom-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top; width: 170px;\">3G via Dongle</td><td class=\"specsValue\" style=\"margin: 0px; padding: 6px; border-width: 0px 0px 1px 1px; border-bottom-style: dotted; border-left-style: solid; border-bottom-color: #c9c9c9; border-left-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top;\">No</td></tr><tr style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><td class=\"specsKey\" style=\"margin: 0px; padding: 6px; border-width: 0px 1px 1px 0px; border-right-style: solid; border-bottom-style: dotted; border-right-color: #c9c9c9; border-bottom-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top; width: 170px;\">Graphics</td><td class=\"specsValue\" style=\"margin: 0px; padding: 6px; border-width: 0px 0px 1px 1px; border-bottom-style: dotted; border-left-style: solid; border-bottom-color: #c9c9c9; border-left-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top;\">PowerVR SGX 544MP2</td></tr><tr style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><td class=\"specsKey\" style=\"margin: 0px; padding: 6px; border-width: 0px 1px 1px 0px; border-right-style: solid; border-bottom-style: dotted; border-right-color: #c9c9c9; border-bottom-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top; width: 170px;\">Part Number</td><td class=\"specsValue\" style=\"margin: 0px; padding: 6px; border-width: 0px 0px 1px 1px; border-bottom-style: dotted; border-left-style: solid; border-bottom-color: #c9c9c9; border-left-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top;\">T08DFPRA14BL</td></tr><tr style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><td class=\"specsKey\" style=\"margin: 0px; padding: 6px; border-width: 0px 1px 1px 0px; border-right-style: solid; border-bottom-style: dotted; border-right-color: #c9c9c9; border-bottom-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top; width: 170px;\">Model ID</td><td class=\"specsValue\" style=\"margin: 0px; padding: 6px; border-width: 0px 0px 1px 1px; border-bottom-style: dotted; border-left-style: solid; border-bottom-color: #c9c9c9; border-left-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top;\">XT 801</td></tr><tr style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><td class=\"specsKey\" style=\"margin: 0px; padding: 6px; border-width: 0px 1px 1px 0px; border-right-style: solid; border-bottom-style: dotted; border-right-color: #c9c9c9; border-bottom-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top; width: 170px;\">Color</td><td class=\"specsValue\" style=\"margin: 0px; padding: 6px; border-width: 0px 0px 1px 1px; border-bottom-style: dotted; border-left-style: solid; border-bottom-color: #c9c9c9; border-left-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top;\">Blue</td></tr><tr style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><td class=\"specsKey\" style=\"margin: 0px; padding: 6px; border-width: 0px 1px 1px 0px; border-right-style: solid; border-bottom-style: dotted; border-right-color: #c9c9c9; border-bottom-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top; width: 170px;\">Processor</td><td class=\"specsValue\" style=\"margin: 0px; padding: 6px; border-width: 0px 0px 1px 1px; border-bottom-style: dotted; border-left-style: solid; border-bottom-color: #c9c9c9; border-left-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top;\">2 GHz Intel Atom Processor Z2580 (1M Cache, 2.00 GHz)</td></tr></tbody></table><table class=\"specTable\" style=\"margin: 0px 0px 30px; padding: 0px; border: 0px; font-family: arial, tahoma, verdana, sans-serif; font-size: 13px; font-stretch: inherit; line-height: 16.8999996185303px; vertical-align: baseline; border-collapse: collapse; border-spacing: 0px; width: 730px; color: #333333;\" cellspacing=\"0\"><tbody style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><tr style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><th class=\"groupHead\" style=\"margin: 0px; padding: 6px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top; text-transform: uppercase; background-color: #f2f2f2;\" colspan=\"2\">MULTIMEDIA</th></tr><tr style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><td class=\"specsKey\" style=\"margin: 0px; padding: 6px; border-width: 0px 1px 1px 0px; border-right-style: solid; border-bottom-style: dotted; border-right-color: #c9c9c9; border-bottom-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top; width: 170px;\">Video Playback</td><td class=\"specsValue\" style=\"margin: 0px; padding: 6px; border-width: 0px 0px 1px 1px; border-bottom-style: dotted; border-left-style: solid; border-bottom-color: #c9c9c9; border-left-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top;\">Yes , Full HD Playback</td></tr></tbody></table><table class=\"specTable\" style=\"margin: 0px 0px 30px; padding: 0px; border: 0px; font-family: arial, tahoma, verdana, sans-serif; font-size: 13px; font-stretch: inherit; line-height: 16.8999996185303px; vertical-align: baseline; border-collapse: collapse; border-spacing: 0px; width: 730px; color: #333333;\" cellspacing=\"0\"><tbody style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><tr style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><th class=\"groupHead\" style=\"margin: 0px; padding: 6px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top; text-transform: uppercase; background-color: #f2f2f2;\" colspan=\"2\">CAMERA</th></tr><tr\n" +
            " style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><td class=\"specsKey\" style=\"margin: 0px; padding: 6px; border-width: 0px 1px 1px 0px; border-right-style: solid; border-bottom-style: dotted; border-right-color: #c9c9c9; border-bottom-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top; width: 170px;\">Video Recording</td><td class=\"specsValue\" style=\"margin: 0px; padding: 6px; border-width: 0px 0px 1px 1px; border-bottom-style: dotted; border-left-style: solid; border-bottom-color: #c9c9c9; border-left-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top;\">Yes</td></tr><tr style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><td class=\"specsKey\" style=\"margin: 0px; padding: 6px; border-width: 0px 1px 1px 0px; border-right-style: solid; border-bottom-style: dotted; border-right-color: #c9c9c9; border-bottom-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top; width: 170px;\">Secondary Camera</td><td class=\"specsValue\" style=\"margin: 0px; padding: 6px; border-width: 0px 0px 1px 1px; border-bottom-style: dotted; border-left-style: solid; border-bottom-color: #c9c9c9; border-left-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top;\">2 megapixels</td></tr><tr style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><td class=\"specsKey\" style=\"margin: 0px; padding: 6px; border-width: 0px 1px 1px 0px; border-right-style: solid; border-bottom-style: dotted; border-right-color: #c9c9c9; border-bottom-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top; width: 170px;\">Other Camera Features</td><td class=\"specsValue\" style=\"margin: 0px; padding: 6px; border-width: 0px 0px 1px 1px; border-bottom-style: dotted; border-left-style: solid; border-bottom-color: #c9c9c9; border-left-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top;\">Auto Focus</td></tr><tr style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><td class=\"specsKey\" style=\"margin: 0px; padding: 6px; border-width: 0px 1px 1px 0px; border-right-style: solid; border-bottom-style: dotted; border-right-color: #c9c9c9; border-bottom-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top; width: 170px;\">Primary Camera</td><td class=\"specsValue\" style=\"margin: 0px; padding: 6px; border-width: 0px 0px 1px 1px; border-bottom-style: dotted; border-left-style: solid; border-bottom-color: #c9c9c9; border-left-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top;\">5 megapixels</td></tr></tbody></table><table class=\"specTable\" style=\"margin: 0px 0px 30px; padding: 0px; border: 0px; font-family: arial, tahoma, verdana, sans-serif; font-size: 13px; font-stretch: inherit; line-height: 16.8999996185303px; vertical-align: baseline; border-collapse: collapse; border-spacing: 0px; width: 730px; color: #333333;\" cellspacing=\"0\"><tbody style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><tr style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><th class=\"groupHead\" style=\"margin: 0px; padding: 6px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top; text-transform: uppercase; background-color: #f2f2f2;\" colspan=\"2\">DIMENSIONS</th></tr><tr style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><td class=\"specsKey\" style=\"margin: 0px; padding: 6px; border-width: 0px 1px 1px 0px; border-right-style: solid; border-bottom-style: dotted; border-right-color: #c9c9c9; border-bottom-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top; width: 170px;\">Weight</td><td class=\"specsValue\" style=\"margin: 0px; padding: 6px; border-width: 0px 0px 1px 1px; border-bottom-style: dotted; border-left-style: solid; border-bottom-color: #c9c9c9; border-left-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top;\">365 g</td></tr><tr style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><td class=\"specsKey\" style=\"margin: 0px; padding: 6px; border-width: 0px 1px 1px 0px; border-right-style: solid; border-bottom-style: dotted; border-right-color: #c9c9c9; border-bottom-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top; width: 170px;\">Dimensions</td><td class=\"specsValue\" style=\"margin: 0px; padding: 6px; border-width: 0px 0px 1px 1px; border-bottom-style: dotted; border-left-style: solid; border-bottom-color: #c9c9c9; border-left-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top;\">125 x 212.7 x 8.2 mm</td></tr></tbody></table><table class=\"specTable\" style=\"margin: 0px 0px 30px; padding: 0px; border: 0px; font-family: arial, tahoma, verdana, sans-serif; font-size: 13px; font-stretch: inherit; line-height: 16.8999996185303px; vertical-align: baseline; border-collapse: collapse; border-spacing: 0px; width: 730px; color: #333333;\" cellspacing=\"0\"><tbody style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><tr style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><th class=\"groupHead\" style=\"margin: 0px; padding: 6px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top; text-transform: uppercase; background-color: #f2f2f2;\" colspan=\"2\">INTERNET CONNECTIVITY</th></tr><tr style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><td class=\"specsKey\" style=\"margin: 0px; padding: 6px; border-width: 0px 1px 1px 0px; border-right-style: solid; border-bottom-style: dotted; border-right-color: #c9c9c9; border-bottom-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top; width: 170px;\">4G</td><td class=\"specsValue\" style=\"margin: 0px; padding: 6px; border-width: 0px 0px 1px 1px; border-bottom-style: dotted; border-left-style: solid; border-bottom-color: #c9c9c9; border-left-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top;\">No</td></tr><tr style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><td class=\"specsKey\" style=\"margin: 0px; padding: 6px; border-width: 0px 1px 1px 0px; border-right-style: solid; border-bottom-style: dotted; border-right-color: #c9c9c9; border-bottom-color: #c9c9c9; font-family:\n" +
            " inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top; width: 170px;\">Pre-Installed Browser</td><td class=\"specsValue\" style=\"margin: 0px; padding: 6px; border-width: 0px 0px 1px 1px; border-bottom-style: dotted; border-left-style: solid; border-bottom-color: #c9c9c9; border-left-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top;\">Android</td></tr><tr style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><td class=\"specsKey\" style=\"margin: 0px; padding: 6px; border-width: 0px 1px 1px 0px; border-right-style: solid; border-bottom-style: dotted; border-right-color: #c9c9c9; border-bottom-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top; width: 170px;\">Wi-Fi</td><td class=\"specsValue\" style=\"margin: 0px; padding: 6px; border-width: 0px 0px 1px 1px; border-bottom-style: dotted; border-left-style: solid; border-bottom-color: #c9c9c9; border-left-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top;\">Yes, 802.11 b/g/n</td></tr><tr style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><td class=\"specsKey\" style=\"margin: 0px; padding: 6px; border-width: 0px 1px 1px 0px; border-right-style: solid; border-bottom-style: dotted; border-right-color: #c9c9c9; border-bottom-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top; width: 170px;\">3G</td><td class=\"specsValue\" style=\"margin: 0px; padding: 6px; border-width: 0px 0px 1px 1px; border-bottom-style: dotted; border-left-style: solid; border-bottom-color: #c9c9c9; border-left-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top;\">No</td></tr></tbody></table><table class=\"specTable\" style=\"margin: 0px 0px 30px; padding: 0px; border: 0px; font-family: arial, tahoma, verdana, sans-serif; font-size: 13px; font-stretch: inherit; line-height: 16.8999996185303px; vertical-align: baseline; border-collapse: collapse; border-spacing: 0px; width: 730px; color: #333333;\" cellspacing=\"0\"><tbody style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><tr style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><th class=\"groupHead\" style=\"margin: 0px; padding: 6px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top; text-transform: uppercase; background-color: #f2f2f2;\" colspan=\"2\">DISPLAY</th></tr><tr style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><td class=\"specsKey\" style=\"margin: 0px; padding: 6px; border-width: 0px 1px 1px 0px; border-right-style: solid; border-bottom-style: dotted; border-right-color: #c9c9c9; border-bottom-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top; width: 170px;\">Display Type</td><td class=\"specsValue\" style=\"margin: 0px; padding: 6px; border-width: 0px 0px 1px 1px; border-bottom-style: dotted; border-left-style: solid; border-bottom-color: #c9c9c9; border-left-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top;\">8 inch 1280 X 800 pixels</td></tr><tr style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><td class=\"specsKey\" style=\"margin: 0px; padding: 6px; border-width: 0px 1px 1px 0px; border-right-style: solid; border-bottom-style: dotted; border-right-color: #c9c9c9; border-bottom-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top; width: 170px;\">Other Display Features</td><td class=\"specsValue\" style=\"margin: 0px; padding: 6px; border-width: 0px 0px 1px 1px; border-bottom-style: dotted; border-left-style: solid; border-bottom-color: #c9c9c9; border-left-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top;\">HD IPS Display, 5 Point Touch</td></tr></tbody></table><table class=\"specTable\" style=\"margin: 0px 0px 30px; padding: 0px; border: 0px; font-family: arial, tahoma, verdana, sans-serif; font-size: 13px; font-stretch: inherit; line-height: 16.8999996185303px; vertical-align: baseline; border-collapse: collapse; border-spacing: 0px; width: 730px; color: #333333;\" cellspacing=\"0\"><tbody style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><tr style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><th class=\"groupHead\" style=\"margin: 0px; padding: 6px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top; text-transform: uppercase; background-color: #f2f2f2;\" colspan=\"2\">WARRANTY</th></tr><tr style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><td class=\"specsKey\" style=\"margin: 0px; padding: 6px; border-width: 0px 1px 1px 0px; border-right-style: solid; border-bottom-style: dotted; border-right-color: #c9c9c9; border-bottom-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top; width: 170px;\">Warranty Summary</td><td class=\"specsValue\" style=\"margin: 0px; padding: 6px; border-width: 0px 0px 1px 1px; border-bottom-style: dotted; border-left-style: solid; border-bottom-color: #c9c9c9; border-left-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top;\">1 Year manufacturer warranty for the Tablet, 6 months manufacturer warranty for other inbox accessories</td></tr></tbody></table><table class=\"specTable\" style=\"margin: 0px 0px 30px; padding: 0px; border: 0px; font-family: arial, tahoma, verdana, sans-serif; font-size: 13px; font-stretch: inherit; line-height: 16.8999996185303px; vertical-align: baseline; border-collapse: collapse; border-spacing: 0px; width: 730px; color: #333333;\" cellspacing=\"0\"><tbody style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><tr style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><th class=\"groupHead\" style=\"margin: 0px; padding: 6px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top; text-transform: uppercase; background-color: #f2f2f2;\" colspan=\"2\">BATTERY</th></tr><tr style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><td class=\"specsKey\" style=\"margin: 0px; padding: 6px; border-width: 0px 1px 1px 0px; border-right-style: solid; border-bottom-style: dotted; border-right-color: #c9c9c9; border-bottom-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top; width: 170px;\">Standby Time</td><td class=\"specsValue\" style=\"margin: 0px; padding:\n" +
            " 6px; border-width: 0px 0px 1px 1px; border-bottom-style: dotted; border-left-style: solid; border-bottom-color: #c9c9c9; border-left-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top;\">168 hrs</td></tr><tr style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><td class=\"specsKey\" style=\"margin: 0px; padding: 6px; border-width: 0px 1px 1px 0px; border-right-style: solid; border-bottom-style: dotted; border-right-color: #c9c9c9; border-bottom-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top; width: 170px;\">Battery Type</td><td class=\"specsValue\" style=\"margin: 0px; padding: 6px; border-width: 0px 0px 1px 1px; border-bottom-style: dotted; border-left-style: solid; border-bottom-color: #c9c9c9; border-left-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top;\">4200 Lithium - Polymer</td></tr></tbody></table><table class=\"specTable\" style=\"margin: 0px 0px 30px; padding: 0px; border: 0px; font-family: arial, tahoma, verdana, sans-serif; font-size: 13px; font-stretch: inherit; line-height: 16.8999996185303px; vertical-align: baseline; border-collapse: collapse; border-spacing: 0px; width: 730px; color: #333333;\" cellspacing=\"0\"><tbody style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><tr style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><th class=\"groupHead\" style=\"margin: 0px; padding: 6px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top; text-transform: uppercase; background-color: #f2f2f2;\" colspan=\"2\">CONNECTIVITY</th></tr><tr style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><td class=\"specsKey\" style=\"margin: 0px; padding: 6px; border-width: 0px 1px 1px 0px; border-right-style: solid; border-bottom-style: dotted; border-right-color: #c9c9c9; border-bottom-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top; width: 170px;\">Audio Jack</td><td class=\"specsValue\" style=\"margin: 0px; padding: 6px; border-width: 0px 0px 1px 1px; border-bottom-style: dotted; border-left-style: solid; border-bottom-color: #c9c9c9; border-left-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top;\">3.5 Headphone Jack</td></tr><tr style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><td class=\"specsKey\" style=\"margin: 0px; padding: 6px; border-width: 0px 1px 1px 0px; border-right-style: solid; border-bottom-style: dotted; border-right-color: #c9c9c9; border-bottom-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top; width: 170px;\">USB</td><td class=\"specsValue\" style=\"margin: 0px; padding: 6px; border-width: 0px 0px 1px 1px; border-bottom-style: dotted; border-left-style: solid; border-bottom-color: #c9c9c9; border-left-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top;\">Yes, Micro USB 2.0</td></tr><tr style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><td class=\"specsKey\" style=\"margin: 0px; padding: 6px; border-width: 0px 1px 1px 0px; border-right-style: solid; border-bottom-style: dotted; border-right-color: #c9c9c9; border-bottom-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top; width: 170px;\">Bluetooth</td><td class=\"specsValue\" style=\"margin: 0px; padding: 6px; border-width: 0px 0px 1px 1px; border-bottom-style: dotted; border-left-style: solid; border-bottom-color: #c9c9c9; border-left-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top;\">v4</td></tr></tbody></table><table class=\"specTable\" style=\"margin: 0px 0px 30px; padding: 0px; border: 0px; font-family: arial, tahoma, verdana, sans-serif; font-size: 13px; font-stretch: inherit; line-height: 16.8999996185303px; vertical-align: baseline; border-collapse: collapse; border-spacing: 0px; width: 730px; color: #333333;\" cellspacing=\"0\"><tbody style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><tr style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><th class=\"groupHead\" style=\"margin: 0px; padding: 6px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top; text-transform: uppercase; background-color: #f2f2f2;\" colspan=\"2\">NAVIGATION</th></tr><tr style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><td class=\"specsKey\" style=\"margin: 0px; padding: 6px; border-width: 0px 1px 1px 0px; border-right-style: solid; border-bottom-style: dotted; border-right-color: #c9c9c9; border-bottom-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top; width: 170px;\">Map Support</td><td class=\"specsValue\" style=\"margin: 0px; padding: 6px; border-width: 0px 0px 1px 1px; border-bottom-style: dotted; border-left-style: solid; border-bottom-color: #c9c9c9; border-left-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top;\">Yes</td></tr><tr style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><td class=\"specsKey\" style=\"margin: 0px; padding: 6px; border-width: 0px 1px 1px 0px; border-right-style: solid; border-bottom-style: dotted; border-right-color: #c9c9c9; border-bottom-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top; width: 170px;\">GPS</td><td class=\"specsValue\" style=\"margin: 0px; padding: 6px; border-width: 0px 0px 1px 1px; border-bottom-style: dotted; border-left-style: solid; border-bottom-color: #c9c9c9; border-left-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top;\">Yes</td></tr></tbody></table><table class=\"specTable\" style=\"margin: 0px 0px 30px; padding: 0px; border: 0px; font-family: arial, tahoma, verdana, sans-serif; font-size: 13px; font-stretch: inherit; line-height: 16.8999996185303px; vertical-align: baseline; border-collapse: collapse; border-spacing: 0px; width: 730px; color: #333333;\" cellspacing=\"0\"><tbody style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><tr style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><th class=\"groupHead\" style=\"margin: 0px; padding: 6px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top; text-transform: uppercase; background-color: #f2f2f2;\" colspan=\"2\">PLATFORM</th></tr><tr style=\"margin: 0px; padding: 0px;\n" +
            " border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><td class=\"specsKey\" style=\"margin: 0px; padding: 6px; border-width: 0px 1px 1px 0px; border-right-style: solid; border-bottom-style: dotted; border-right-color: #c9c9c9; border-bottom-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top; width: 170px;\">Operating System</td><td class=\"specsValue\" style=\"margin: 0px; padding: 6px; border-width: 0px 0px 1px 1px; border-bottom-style: dotted; border-left-style: solid; border-bottom-color: #c9c9c9; border-left-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top;\">Android 4.2.2 (Jelly Bean) (Assured Upgrade to Kitkat 4.4)</td></tr><tr style=\"margin: 0px; padding: 0px; border: 0px; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: baseline;\"><td class=\"specsKey\" style=\"margin: 0px; padding: 6px; border-width: 0px 1px 1px 0px; border-right-style: solid; border-bottom-style: dotted; border-right-color: #c9c9c9; border-bottom-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top; width: 170px;\">Sensors</td><td class=\"specsValue\" style=\"margin: 0px; padding: 6px; border-width: 0px 0px 1px 1px; border-bottom-style: dotted; border-left-style: solid; border-bottom-color: #c9c9c9; border-left-color: #c9c9c9; font-family: inherit; font-size: inherit; font-style: inherit; font-variant: inherit; font-weight: inherit; font-stretch: inherit; line-height: inherit; vertical-align: top;\">G-Sensor</td></tr></tbody></table>\n";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        declaration();
        initialization();
    }

    private void declaration() {
        utilityClass = new UtilityClass(this);
        myApplication = (MyApplication) MainActivity.this.getApplication();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        criteria = new Criteria();
        // criteria.setAccuracy(Criteria.ACCURACY_FINE);   //default

    }

    public static String changedHeaderHtml(String htmlText) {

        String head = "<head><meta name=\"viewport\" content=\"width=device-width, user-scalable=yes\" /></head>";

        String closedTag = "</body></html>";
        String changeFontHtml = head + htmlText + closedTag;
        return changeFontHtml;
    }

    private void initialization() {
        // Getting the name of the provider that meets the criteria
        // WebSettings settings = tvWebView.getSettings();
        //  settings.setMinimumFontSize(15);
        // tvWebView.setInitialScale(1);
        //   settings.setLoadWithOverviewMode(false);
        //  settings.setUseWideViewPort(false);

        // tvWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        //   tvWebView.setScrollbarFadingEnabled(false);
       /* settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);*/
        //tvWebView.getSettings().setLoadWithOverviewMode(true);
        //tvWebView.getSettings().setUseWideViewPort(false);
        //     settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        // tvWebView.setScrollContainer(false);
        // tvWebView.setScrollY(utilityClass.GetWidth());
        // tvWebView.setInitialScale(100);

      /*  tvWebView.setInitialScale(100);
        tvWebView.setWebChromeClient(new WebChromeClient());
        String changeFontHtml = changedHeaderHtml(htmlCode);
        tvWebView.loadDataWithBaseURL(null, changeFontHtml,
                "text/html", "UTF-8", null);
*/
        //Only hide the scrollbar, not disables the scrolling:
        tvWebView.setVerticalScrollBarEnabled(false);
        tvWebView.setHorizontalScrollBarEnabled(false);

//Only disabled the horizontal scrolling:
        tvWebView.setInitialScale(100);
     //   tvWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);

//To disabled the horizontal and vertical scrolling:
      /*  tvWebView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });*/
        tvWebView.loadData(htmlCode,
                "text/html", "UTF-8");
        setBtnGetLocation();
    }

    @OnClick(R.id.btnTakePicture)
    public void setBtnTakePicture() {
        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("Error", ex.toString());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                myApplication.setPicUri(Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, CAMERA_PIC_REQUEST);
            }
        }


    }

    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_"; //new File(Environment.getExternalStorageDirectory()+File.separator+"ProjectmanagementPhotos");
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/LutMaar");
        File storageInnerDir = new File(Environment.getExternalStorageDirectory() + "/LutMaar/Upload");// getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);//Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        //  File storageDir =getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);//Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists()) {
            storageDir.mkdir();
            if (storageDir.exists()) {
                if (!storageInnerDir.exists()) {
                    storageInnerDir.mkdir();
                }
            }
        }
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageInnerDir      /* directory */
        );
        return image;
    }

    @OnClick(R.id.btnPostPicture)
    public void setBtnPostPicture() {
        utilityClass.processDialogStart();
        restService();
    }

    private void restService() {

        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Content-Type", "application/json");
                request.addHeader("X-CSRF-Token", SaveSharedPreferences.getTokon(MainActivity.this));
                request.addHeader("Cookie", SaveSharedPreferences.getCookie(MainActivity.this));

                Log.i("Tokon : ", SaveSharedPreferences.getTokon(MainActivity.this));
                Log.i("Cookie : ", SaveSharedPreferences.getCookie(MainActivity.this));
            }
        };

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(constantClass.endPoints)
                .setRequestInterceptor(requestInterceptor)
                .build();

        api = restAdapter.create(RestServices.class);
        HashMap<String, String> HMPostAnImage = new HashMap<String, String>();
        HMPostAnImage.put("filename", imgName);
        HMPostAnImage.put("target_uri", imgPath);
        HMPostAnImage.put("filemime", "image/jpeg");
        HMPostAnImage.put("file", imgFile);
        api.PostAnImage(HMPostAnImage, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject result, Response response) {
                utilityClass.processDialogStop();

                if (true) {
                    utilityClass.toast("User logout Success");
                    Log.i("User logout Success :  ", String.valueOf(result));

                } else {
                    Log.i("User logout Response :", String.valueOf(result));
                }

            }

            @Override
            public void failure(RetrofitError error) {
                utilityClass.processDialogStop();
                Log.i("Restrofit Error  : ", String.valueOf(error.toString()));

                if (error.toString().contains("No address associated with hostname")) {
                    utilityClass.toast("Please check your network connection or try again later.");
                } else if (error.toString().equals("retrofit.RetrofitError: 500 Internal Server Error")) {
                    utilityClass.toast("Internal Server Error");
                } else if (error.toString().contains("com.google.gson.JsonSyntaxException")) {
                    utilityClass.toast("Result not found form server");
                } else if (error.toString().contains("User is not logged in")) {
                    utilityClass.toast("User is not logged in");
                } else if (error.toString().contains("RetrofitError: 403 : Access denied")) {
                    utilityClass.toast("Access denied for this user");
                } else if (error.toString().contains("Missing data the file upload can not be completed")) {
                    utilityClass.toast("Missing data the file upload can not be completed");
                } else {
                    utilityClass.toast("User can not logout");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAMERA_PIC_REQUEST:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri selectedImage = myApplication.getPicUri();
                        String imgNameCapture = selectedImage.getPath();
                        try {
                            ExifInterface exif = new ExifInterface(imgNameCapture);
                            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                            Log.e("ExifInteface .........", "rotation =" + orientation);
                            Log.e("orientation", "" + orientation);
                            Matrix m = new Matrix();
                            if ((orientation == ExifInterface.ORIENTATION_ROTATE_180)) {
                                m.postRotate(180);
                            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                                m.postRotate(90);
                                Log.e("in orientation", "" + orientation);
                            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                                m.postRotate(270);
                                Log.e("in orientation", "" + orientation);
                            }
                            Bitmap thumbnailImage = (BitmapFactory.decodeFile(imgNameCapture));
                            Bitmap bmpPic1 = Bitmap.createBitmap(thumbnailImage, 0, 0, thumbnailImage.getWidth(), thumbnailImage.getHeight(), m, true);
                            //Bitmap bmpPic1 = Bitmap.createScaledBitmap(thumbnail, 240, 260, m, true);
                            thumbnailImage = Bitmap.createScaledBitmap(bmpPic1, 240, 260, true);
                            //  Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                            //  imgFile = ConvertBitmapToBase64Format(thumbnail);
                            //   thumbnail = Bitmap.createScaledBitmap(thumbnail, utilityClass.GetWidth() / 2, utilityClass.GetHeight() / 2, true);
                            //ImageView imageView = (ImageView) findViewById(R.id.yourImageView);
                            // imgFile = ConvertBitmapToBase64Format(thumbnail);
                            imageView.setImageBitmap(thumbnailImage);
                        } catch (IOException e) {
                            Log.e("Image IOException:", e.toString());
                        }
                        break;
                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }
                    //Picasso.with(this).load( thumbnail).fit().centerInside().into(imageView);
                    //imageView.setImageBitmap(thumbnail);
                }
        }
    }

    public String ConvertBitmapToBase64Format(Bitmap bitmap) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string
        String imageString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
        return imageString;
    }

    // then this imageString pass to json object
    // String encodedImage=ConvertBitmapToBase64Format(bitmap); // pass your image bitmap


    @OnClick(R.id.btnGetLocation)
    public void setBtnGetLocation() {
        if (utilityClass.isInternetConnection()) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                 /*location = appLocationService
                        .getLocation(LocationManager.GPS_PROVIDER);
                //double latitude = gpsLocation.getLatitude();
                //double longitude = gpsLocation.getLongitude();
                String result = "Latitude: " + location.getLatitude() +
                        " Longitude: " + location.getLongitude();
                etShowLocation.setText(result);*/

                provider = locationManager.getBestProvider(criteria, false);

                if (provider != null && !provider.equals("")) {


                    // Get the location from the given provider
                    location = locationManager.getLastKnownLocation(provider);

                    locationManager.requestLocationUpdates(provider, 20000, 1, this);

                    if (location != null)
                        onLocationChanged(location);
                    else
                        utilityClass.toast("Location can't be retrieved");

                } else {
                    utilityClass.toast("No Provider Found");
                    etShowLocation.setText("Sorry, location is not determined");
                }
                // setBrnGetLocationClick();
            } else {
                showGPSDisabledAlertToUser();
            }
        } else {
            utilityClass.toast("Please check your internet settings!!!");
        }
    }


    @OnClick(R.id.btnGetAddress)
    public void setBtnGetAddress() {
        if (utilityClass.isInternetConnection()) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {


                provider = locationManager.getBestProvider(criteria, false);

                if (provider != null && !provider.equals("")) {

                    // Get the location from the given provider
                    location = locationManager.getLastKnownLocation(provider);

                    locationManager.requestLocationUpdates(provider, 20000, 1, this);

                    if (location != null) {

                    } else
                        utilityClass.toast("Location can't be retrieved");

                } else {
                    utilityClass.toast("No Provider Found");
                    etShowLocation.setText("Sorry, location is not determined");
                }
                // setBrnGetLocationClick();
            } else {
                showGPSDisabledAlertToUser();
            }
        } else {
            utilityClass.toast("Please check your internet settings!!!");
        }
    }

    @OnClick(R.id.btnGetMap)
    public void setBtnGetMap() {
        if (utilityClass.isInternetConnection()) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Intent i = new Intent(this, MapsDemoActivity.class);
                startActivity(i);
            } else {
                showGPSDisabledAlertToUser();
            }
        } else {
            utilityClass.toast("Please check your internet settings!!!");
        }
    }

    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Setting", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent callGPSSettingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(callGPSSettingIntent);
                    }
                });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                dialog.cancel();
            }
        });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (alertDialog != null) {
            alertDialog.hide();
        }
        initialization();
    }


    @Override
    public void onLocationChanged(Location location) {
        String londitude = "Londitude: " + location.getLongitude();
        String latitude = "Latitude: " + location.getLatitude();
        String altitiude = "Altitiude: " + location.getAltitude();
        String accuracy = "Accuracy: " + location.getAccuracy();
        String time = "Time: " + location.getTime();

        etShowLocation.setText(londitude + " & " + latitude + " & "
                + altitiude + "\n" + accuracy + " & " + time);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            tvAddress.setText(locationAddress);

        }
    }
}
