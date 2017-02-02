package com.wots.lutmaar.UtilClass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.wots.lutmaar.Interface.RestServices;
import com.wots.lutmaar.UtilClass.db.MySQLiteHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Super Star on 16-09-2015.
 */
public class ConnectivityChangedReceiver extends BroadcastReceiver {

    //Class
    UtilityClass utilityClass;
    private static boolean firstConnect = true;
    MySQLiteHelper mydb;
    Context context;
    Cursor rs;
    JSONObject jsonObject = new JSONObject();
    ArrayList<HashMap<String, String>> ImageIDList = new ArrayList<HashMap<String, String>>();
    HashMap<String, Object> HMPostADeal = new HashMap<String, Object>();


    //Interface
    RestServices api;
    RestAdapter restAdapter;

    //Variable
    String[] splitImagePath;
    String imagePathList = "", DealRecordId = "";
    int ImageUploadCount = 0, NetworkErrorCount = 0, TotalUploadedImages = 0, TotalDeals = 0;


    @Override
    public void onReceive(Context context, Intent intent) {

        if (SaveSharedPreferences.getTokon(context).length() != 0) {
            mydb = new MySQLiteHelper(context);
            this.context = context;
            utilityClass = new UtilityClass(context);
            if (utilityClass.isInternetConnection()) {
                if (firstConnect) {
                    firstConnect = false;
                  //  utilityClass.toast("Internet Connected!!!");
                    Log.d("Internet : ", "Connected ");
                    utilityClass.restServiceUnReadMessage();
                    rs = mydb.getAllOfflinePostDealData();
                    rs.moveToFirst();
                    if (rs.getCount() > 0) {
                        TotalDeals = rs.getColumnCount();
                        postAllOfflineDeals();
                    }
                }
            } else {
                firstConnect = true;
              //  utilityClass.toast("Internet Disconnected!!!");
                Log.d("Internet : ", " Disconnected ");
            }
        }
    }

    public void postAllOfflineDeals() {
        ImageUploadCount = 0;
        ImageIDList.clear();
        NetworkErrorCount = 0;
        DealRecordId = rs.getString(rs.getColumnIndex(MySQLiteHelper.D_ID));
        if (rs.getString(rs.getColumnIndex(MySQLiteHelper.DEAL_IMAGES)) != null &&
                !rs.getString(rs.getColumnIndex(MySQLiteHelper.DEAL_IMAGES)).equalsIgnoreCase("")) {
            imagePathList = rs.getString(rs.getColumnIndex(MySQLiteHelper.DEAL_IMAGES));
            splitImagePath = imagePathList.split(",");
            restServiceImageUpload();
        } else {
            DealOfflineRestService();
        }
    }

    private void restServiceImageUpload() {
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Content-Type", "application/json");
                request.addHeader("X-CSRF-Token", SaveSharedPreferences.getTokon(context));
                request.addHeader("Cookie", SaveSharedPreferences.getCookie(context));

                Log.i("Image Upload Tokon : ", SaveSharedPreferences.getTokon(context));
                Log.i("Image Upload Cookie : ", SaveSharedPreferences.getCookie(context));
            }
        };

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(ConstantClass.endPoints)
                .setRequestInterceptor(requestInterceptor)
                .build();
        api = restAdapter.create(RestServices.class);

        HashMap<String, String> HMImageData = new HashMap<String, String>();
        String imagePath = splitImagePath[ImageUploadCount];
        File f = new File(imagePath);
        if (f.exists()) {

            HMImageData.put("filename", imagePath.substring(imagePath.lastIndexOf("/") + 1));
            HMImageData.put("target_uri", imagePath);
            HMImageData.put("filemime", "image/jpeg");
            // final BitmapFactory.Options options = new BitmapFactory.Options();
            //  options.inJustDecodeBounds = true;
            //   Bitmap thumbnail = BitmapFactory.decodeFile(imagePath, options);
            Bitmap thumbnail = (BitmapFactory.decodeFile(imagePath));
            thumbnail = Bitmap.createScaledBitmap(thumbnail, 240, 260, true);
            HMImageData.put("file", utilityClass.ConvertBitmapToBase64Format(thumbnail));

            Log.d("Request image Data:", HMImageData.toString());
            api.PostAnImage(HMImageData, new Callback<JsonObject>() {
                @Override
                public void success(JsonObject result, Response response) {
                    HashMap<String, String> imageDetails = new HashMap<String, String>();
                    imageDetails.put("fid", result.get("fid").getAsString().toString());
                    ImageIDList.add(imageDetails);
                    ImageUploadCount++;
                    NetworkErrorCount = 0;
                    Log.d("Image Upload Success:", String.valueOf(result));

                    if (ImageUploadCount >= splitImagePath.length) {
                        DealOfflineRestService();
                    } else {
                        restServiceImageUpload();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.i("Retrofit Error  : ", String.valueOf(error.toString() + " Network " + RetrofitError.Kind.NETWORK + " : " + error.isNetworkError()));

                    if (error.isNetworkError()) {
                        if (NetworkErrorCount >= 3) {
                            utilityClass.toast("Network error, please try again!!!.");
                            NetworkErrorCount = 0;
                        } else {
                            NetworkErrorCount++;
                            restServiceImageUpload();
                        }
                    } else {
                        if (error.toString().contains("No address associated with hostname")) {
                            utilityClass.toast("Please check your network connection or try again later.");
                        } else {
                            if (error.toString().equals("retrofit.RetrofitError: 500 Internal Server Error")) {
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
                                utilityClass.toast("Image can not uploaded");
                            }
                            ImageUploadCount++;
                            NetworkErrorCount = 0;
                            if (ImageUploadCount >= splitImagePath.length) {
                                DealOfflineRestService();
                            } else {
                                restServiceImageUpload();
                            }
                        }
                    }
                }
            });
        } else {
            Log.d("Image In Device :", "not exist");
            ImageUploadCount++;
            NetworkErrorCount = 0;
            if (ImageUploadCount >= splitImagePath.length) {
                DealOfflineRestService();
            } else {
                restServiceImageUpload();
            }
        }
    }

    public void DealOfflineRestService() {
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Content-Type", "application/json");
                request.addHeader("X-CSRF-Token", SaveSharedPreferences.getTokon(context));
                request.addHeader("Cookie", SaveSharedPreferences.getCookie(context));

                Log.i("Image Upload Tokon : ", SaveSharedPreferences.getTokon(context));
                Log.i("Image Upload Cookie : ", SaveSharedPreferences.getCookie(context));
            }
        };

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(ConstantClass.endPoints)
                .setRequestInterceptor(requestInterceptor)
                .build();
        api = restAdapter.create(RestServices.class);
        try {
            jsonObject = new JSONObject(rs.getString(rs.getColumnIndex(MySQLiteHelper.JSONOBJECT)));
            HMPostADeal = new Gson().fromJson(String.valueOf(jsonObject.toString()),
                    new TypeToken<HashMap<String, Object>>() {
                    }.getType());
        } catch (JSONException e) {
            Log.e("DB to JSONObj Error: ", e.toString());
        }
        api.PostADeal(HMPostADeal, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject result, Response response) {
                NetworkErrorCount = 0;
                Log.i("Post Deal success:", String.valueOf(result));
                if (ImageIDList.size() > 0) {
                    TotalUploadedImages = ImageIDList.size();
                    EditImageRestService(result.getAsJsonObject().get("nid").getAsString());
                } else {
                    if (rs.moveToNext()) {
                        mydb.deleteOfflinePostDealOneByOne(DealRecordId);
                        TotalDeals--;
                        Log.d("Remain Total deals: ", "Offline deal =" + TotalDeals);
                        postAllOfflineDeals();
                    } else {
                        Log.d("AllDeals Uploaded: ", "all");
                        mydb.deleteOfflinePostDealOneByOne(DealRecordId);

                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("Retrofit Error  : ", String.valueOf(error.toString() + " Network " + RetrofitError.Kind.NETWORK + " : " + error.isNetworkError()));

                if (error.isNetworkError()) {
                    if (NetworkErrorCount >= 3) {
                        utilityClass.toast("Network error, please try again!!!.");
                        NetworkErrorCount = 0;
                    } else {
                        NetworkErrorCount++;
                        DealOfflineRestService();
                    }
                } else {
                    if (error.toString().contains("No address associated with hostname")) {
                        utilityClass.toast("Please check your network connection or try again later.");
                    } else {
                        if (error.toString().equals("retrofit.RetrofitError: 500 Internal Server Error")) {
                            utilityClass.toast("Internal Server Error");
                        } else if (error.toString().contains("com.google.gson.JsonSyntaxException")) {
                            utilityClass.toast("Result not found form server");
                        } else if (error.toString().contains("User is not logged in")) {
                            utilityClass.toast("User is not logged in");
                        } else if (error.toString().contains("RetrofitError: 403 : Access denied")) {
                            utilityClass.toast("Access denied for this user");
                        } else {
                            utilityClass.toast("Deal can not Post");
                        }
                        if (rs.moveToNext()) {
                            mydb.deleteOfflinePostDealOneByOne(DealRecordId);
                            TotalDeals--;
                            Log.d("Remain Total deals: ", "Offline deal =" + TotalDeals);
                            postAllOfflineDeals();
                        } else {
                            Log.d("AllDeals Uploaded: ", "all");
                            mydb.deleteOfflinePostDealOneByOne(DealRecordId);
                        }
                    }
                }
            }
        });
    }

    private void EditImageRestService(final String DealID) {

        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Content-Type", "application/json");
                request.addHeader("X-CSRF-Token", SaveSharedPreferences.getTokon(context));
                request.addHeader("Cookie", SaveSharedPreferences.getCookie(context));

                Log.i("Image Edit Tokon : ", SaveSharedPreferences.getTokon(context));
                Log.i("Image Edit Cookie : ", SaveSharedPreferences.getCookie(context));
            }
        };
        restAdapter = new RestAdapter.Builder()
                .setEndpoint(ConstantClass.endPoints)
                .setRequestInterceptor(requestInterceptor)
                .build();
        api = restAdapter.create(RestServices.class);

        HashMap<String, Object> HMImageEdit = new HashMap<String, Object>();
        HashMap<String, Object> HMImageEditUnd = new HashMap<String, Object>();

        HMImageEditUnd.put("und", ImageIDList);
        HMImageEdit.put("field_images", HMImageEditUnd);

        Log.i("Request Edit image:", new Gson().toJson(HMImageEdit).toString());
        api.EditAnImage(DealID, HMImageEdit, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject result, Response response) {
                NetworkErrorCount = 0;
                TotalUploadedImages--;
                Log.i("Image Edit success:", TotalUploadedImages + " Deal ID: " + String.valueOf(result));
                if (TotalUploadedImages > 0) {
                    EditImageRestService(DealID);
                } else {
                    if (rs.moveToNext()) {
                        TotalDeals--;
                        mydb.deleteOfflinePostDealOneByOne(DealRecordId);
                        Log.d("Remain Total deals: ", "Offline deal =" + TotalDeals);
                        postAllOfflineDeals();
                    } else {
                        mydb.deleteOfflinePostDealOneByOne(DealRecordId);
                        Log.d("AllDeals Uploaded: ", "all");

                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {

                Log.i("Retrofit Error  : ", String.valueOf(error.toString() + " Network " + RetrofitError.Kind.NETWORK + " : " + error.isNetworkError()));

                if (error.isNetworkError()) {
                    if (NetworkErrorCount >= 3) {
                        utilityClass.toast("Network error, please try again!!!.");
                        NetworkErrorCount = 0;
                    } else {
                        NetworkErrorCount++;
                        EditImageRestService(DealID);
                    }
                } else {
                    if (error.toString().contains("No address associated with hostname")) {
                        utilityClass.toast("Please check your network connection or try again later.");
                    } else {
                        if (error.toString().equals("retrofit.RetrofitError: 500 Internal Server Error")) {
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
                            utilityClass.toast("Image can not uploaded");
                        }
                        NetworkErrorCount = 0;
                        TotalUploadedImages--;
                        if (TotalUploadedImages > 0) {
                            EditImageRestService(DealID);
                        } else {
                            if (rs.moveToNext()) {
                                TotalDeals--;
                                mydb.deleteOfflinePostDealOneByOne(DealRecordId);
                                Log.d("Remain Total deals: ", "Offline deal =" + TotalDeals);
                                postAllOfflineDeals();
                            } else {
                                mydb.deleteOfflinePostDealOneByOne(DealRecordId);
                                Log.d("AllDeals Uploaded: ", "all");
                            }
                        }
                    }
                }
            }
        });
    }
}
