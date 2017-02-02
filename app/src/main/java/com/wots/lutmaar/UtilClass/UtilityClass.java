package com.wots.lutmaar.UtilClass;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.wots.lutmaar.Interface.RestServices;
import com.wots.lutmaar.R;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by NIK on 21-02-2015.
 */
public class UtilityClass {


    //Class
    Context context;
    private ProgressDialog pdialog;
    AlertDialog alertDialog;
    ArrayList<String> DealsCategory = new ArrayList<String>();
    ArrayList<String> DealsCity = new ArrayList<String>();
    ArrayList<String> DealsState = new ArrayList<String>();

    //Interface
    RestServices api;
    RestAdapter restAdapter;

    public ArrayList<String> getDealsCategory() {
        DealsCategory.add("Select Category...");
        DealsCategory.add("Food & Drinks");
        DealsCategory.add("Grocery shopping");
        DealsCategory.add("Health & Fitness");
        DealsCategory.add("Beauty");
        DealsCategory.add("Tattoos");
        DealsCategory.add("Travel");
        DealsCategory.add("online coupons");
        return DealsCategory;
    }


    public void setDealsCategory(ArrayList<String> dealsCategory) {
        DealsCategory = dealsCategory;
    }

    public ArrayList<String> getDealsState() {
        DealsState.add("Select State...");
        DealsState.add("Gujarat");
        DealsState.add("Maharastra");
        DealsState.add("Bihar");
        return DealsState;
    }

    public void setDealsState(ArrayList<String> dealsState) {
        DealsState = dealsState;
    }

    public ArrayList<String> getDealsCity() {
        DealsCity.add("Select City...");
        DealsCity.add("Bhavnagar");
        DealsCity.add("Surat");
        DealsCity.add("Ahmedabad");


        return DealsCity;
    }

    public void setDealsCity(ArrayList<String> dealsCity) {
        DealsCity = dealsCity;
    }
//Variable


    public UtilityClass(Context context) {
        this.context = context;
    }

    public void toast(String str) {

        Toast.makeText(context, str, Toast.LENGTH_LONG).show();

    }

    public boolean isInternetConnection() {

        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
           /* if (connectivityManager != null)
            {
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info != null)
                    for (int i = 0; i < info.length; i++)
                        if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        {
                            return true;
                        }

            }*/
            if ((connectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null && connectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED)
                    || (connectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null && connectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                    .getState() == NetworkInfo.State.CONNECTED)) {
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            Log.e("Network Check Error", e.toString());
            return false;
        }
    }

    public void processDialogStart() {
        pdialog = new ProgressDialog(context);
        pdialog.setMessage("Please Wait");
        pdialog.setIndeterminate(false);
        pdialog.setCancelable(true);
        pdialog.show();

    }

    public void processDialogStop() {
        if (pdialog != null) {
            if (pdialog.isShowing()) {
                pdialog.dismiss();
            }
        }

    }

    public int GetHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public int GetWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }


    public void shareLink() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        // share.putExtra(Intent.EXTRA_SUBJECT,"Have you tried TrailersNSongs for Android? It's awesome! ");
        share.putExtra(Intent.EXTRA_TEXT, "Have you tried Indian Raas Garba for Android? It's awesome! https://play.google.com/store/apps/details?id=com.wots.indianraasgarba");
        context.startActivity(Intent.createChooser(share, " Share link!"));
    }

    public void moreApps() {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/search?q=mehul%20savaliya")));
    }

    public void rateUs() {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.wots.indianraasgarba")));
    }


    public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
        int targetWidth = 150;
        int targetHeight = 150;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap,
                new Rect(0, 0, sourceBitmap.getWidth(),
                        sourceBitmap.getHeight()),
                new Rect(0, 0, targetWidth, targetHeight), null);
        return targetBitmap;
    }

    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }

    public boolean isTablet(Context context) {
        TelephonyManager manager =
                (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (manager.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
            //Tablet
            return true;
        } else {
            //Mobile
            return false;
        }
    }

    public boolean isTabletDevice(Context activityContext) {
        // Verifies if the Generalized Size of the device is XLARGE to be
        // considered a Tablet
        boolean xlarge =
                ((activityContext.getResources().getConfiguration().screenLayout &
                        Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE);

        // If XLarge, checks if the Generalized Density is at least MDPI (160dpi)
        if (xlarge) {
            DisplayMetrics metrics = new DisplayMetrics();
            Activity activity = (Activity) activityContext;
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

            // MDPI=160, DEFAULT=160, DENSITY_HIGH=240, DENSITY_MEDIUM=160,
            // DENSITY_TV=213, DENSITY_XHIGH=320
            if (metrics.densityDpi == DisplayMetrics.DENSITY_DEFAULT
                    || metrics.densityDpi == DisplayMetrics.DENSITY_HIGH
                    || metrics.densityDpi == DisplayMetrics.DENSITY_MEDIUM
                    || metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH) {

                // Yes, this is a tablet!
                return true;
            }
        }

        // No, this is not a tablet!
        return false;
    }

    public void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Setting", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent callGPSSettingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(callGPSSettingIntent);
                    }
                });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //  utilityClass.toast("Must GPS enable for map.");
                dialog.cancel();
            }
        });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void getDefaultCategory() {
        ArrayList<HashMap<String, String>> LVHMDealCategory = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> HMDealCategory = new HashMap<String, String>();
        ArrayList<String> CatList = new ArrayList<String>();
        ArrayList<String> CatIDList = new ArrayList<String>();
        CatList.add("AudioVisuals");
        CatIDList.add("29");
        CatList.add("Beauty");
        CatIDList.add("5");
        CatList.add("Computers");
        CatIDList.add("35");
        CatList.add("Entertainment");
        CatIDList.add("36");
        CatList.add("Fashion");
        CatIDList.add("38");
        CatList.add("Gaming");
        CatIDList.add("34");
        CatList.add("Groceries");
        CatIDList.add("40");
        CatList.add("Home");
        CatIDList.add("37");
        CatList.add("Kids");
        CatIDList.add("39");
        CatList.add("Mobiles");
        CatIDList.add("33");
        CatList.add("Restaurants");
        CatIDList.add("42");
        CatList.add("Travel");
        CatIDList.add("41");

        for (int i = 0; i < CatList.size(); i++) {
            HMDealCategory = new HashMap<String, String>();
            HMDealCategory.put("name", CatList.get(i));
            HMDealCategory.put("ID", CatIDList.get(i));
            LVHMDealCategory.add(HMDealCategory);
        }
        Singleton.GetInstance().setCategoryList(CatList);
        Singleton.GetInstance().setHMCategory(LVHMDealCategory);

    }

    public void getDefaultCategoryHome() {
        ArrayList<HashMap<String, String>> LVHMDealCategory = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> HMDealCategory = new HashMap<String, String>();
        ArrayList<String> CatList = new ArrayList<String>();
        ArrayList<String> CatIDList = new ArrayList<String>();
        CatList.add("All");
        CatIDList.add("all");
        CatList.add("AudioVisuals");
        CatIDList.add("29");
        CatList.add("Beauty");
        CatIDList.add("5");
        CatList.add("Computers");
        CatIDList.add("35");
        CatList.add("Entertainment");
        CatIDList.add("36");
        CatList.add("Fashion");
        CatIDList.add("38");
        CatList.add("Gaming");
        CatIDList.add("34");
        CatList.add("Groceries");
        CatIDList.add("40");
        CatList.add("Home");
        CatIDList.add("37");
        CatList.add("Kids");
        CatIDList.add("39");
        CatList.add("Mobiles");
        CatIDList.add("33");
        CatList.add("Restaurants");
        CatIDList.add("42");
        CatList.add("Travel");
        CatIDList.add("41");

        for (int i = 0; i < CatList.size(); i++) {
            HMDealCategory = new HashMap<String, String>();
            HMDealCategory.put("name", CatList.get(i));
            HMDealCategory.put("ID", CatIDList.get(i));
            LVHMDealCategory.add(HMDealCategory);
        }
        Singleton.GetInstance().setCategoryListHome(CatList);
        Singleton.GetInstance().setHMCategoryHome(LVHMDealCategory);

    }

    public String ConvertBitmapToBase64Format(Bitmap bitmap) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteFormat = stream.toByteArray();

        // get the base 64 string
        String imageString = Base64.encodeToString(byteFormat, Base64.DEFAULT);
        return imageString;
    }

    public void restServiceUnReadMessage() {
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Content-Type", "application/json");
                request.addHeader("X-CSRF-Token", SaveSharedPreferences.getTokon(context));
                request.addHeader("Cookie", SaveSharedPreferences.getCookie(context));

                Log.i("Category Tokon : ", SaveSharedPreferences.getTokon(context));
                Log.i("Category Cookie : ", SaveSharedPreferences.getCookie(context));
            }
        };

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(ConstantClass.endPoints)
                .setRequestInterceptor(requestInterceptor)
                .build();

        api = restAdapter.create(RestServices.class);
        HashMap<String, String> HMUnreadMessage = new HashMap<String, String>();
        HMUnreadMessage.put("uid", SaveSharedPreferences.getUserID(context));
        Log.d("UnreadMessage Request: ", HMUnreadMessage.toString());
        api.getUnreadMessage(HMUnreadMessage, new Callback<Object>() {
            @Override
            public void success(Object result, Response response) {
                Log.d("UnreadMessage Result: ", result.toString());
                if (result.toString().contains("There is no new messages for you")) {
                    SaveSharedPreferences.setMessage(context, "no");
                    if (Singleton.GetInstance().getIvMessage() != null && Singleton.GetInstance().getTvMessage() != null) {
                        Singleton.GetInstance().getIvMessage().setImageResource(R.drawable.ic_messages);
                        Singleton.GetInstance().getTvMessage().setText("Messages");
                    }
                } else {
                    SaveSharedPreferences.setMessage(context, result.toString());
                    if (Singleton.GetInstance().getIvMessage() != null && Singleton.GetInstance().getTvMessage() != null) {
                        Singleton.GetInstance().getIvMessage().setImageResource(R.drawable.ic_message1);
                        Singleton.GetInstance().getTvMessage().setText("Messages"+result.toString());
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("Retrofit Error  : ", String.valueOf(error.toString()));

            }
        });
    }


}