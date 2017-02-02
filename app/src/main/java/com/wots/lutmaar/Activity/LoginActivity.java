package com.wots.lutmaar.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.wots.lutmaar.Adapter.WelcomeImageListAdapter;
import com.wots.lutmaar.CustomView.CirclePageIndicator;
import com.wots.lutmaar.Interface.RestServices;
import com.wots.lutmaar.R;
import com.wots.lutmaar.UtilClass.ConstantClass;
import com.wots.lutmaar.UtilClass.GPSTracker;
import com.wots.lutmaar.UtilClass.MyApplication;
import com.wots.lutmaar.UtilClass.SaveSharedPreferences;
import com.wots.lutmaar.UtilClass.Singleton;
import com.wots.lutmaar.UtilClass.UtilityClass;
import com.wots.lutmaar.UtilClass.Validation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginActivity extends ActionBarActivity {

    //Class
    UtilityClass utilityClass;
    ConstantClass constantClass;
    CallbackManager callbackManager;
    private TwitterSession session;
    TwitterAuthToken authToken;
    TwitterAuthClient authClient;
    AlertDialog alertDialog;
    private LocationManager locationManager;
    Tracker mTracker;
    GPSTracker gpsTracker;
    ArrayList<Integer> welcomeImageList = new ArrayList<Integer>();

    //Interface
    RestServices api;
    RestAdapter restAdapter;

    //View
    @InjectView(R.id.etUser)
    EditText etUser;
    @InjectView(R.id.etPassword)
    EditText etPassword;
    @InjectView(R.id.cbRemember)
    CheckBox cbRemember;
    @InjectView(R.id.tvError)
    TextView tvError;
   /* @InjectView(R.id.llBackGroundLogin)
    LinearLayout llBackGroundLogin;*/
    @InjectView(R.id.btnFaceBook_login_button)
    LoginButton btnFaceBook_login_button;

    @InjectView(R.id.btnTwitter_login_button)
    TwitterLoginButton btnTwitter_login_button;


    //Variable
    private int NetworkErrorCount = 0;
    String token;
    String secret;
    String provider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_activity);
        ButterKnife.inject(this);
        declaration();
        initialization();
    }

    private void declaration() {
        utilityClass = new UtilityClass(this);
        MyApplication application = (MyApplication) getApplication();
        mTracker = application.getTracker();

        //Log.i(TAG, "Setting screen name: " + name);
        mTracker.setScreenName("Login Activity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        Singleton.GetInstance().setScreenHeight((int) (utilityClass.GetHeight() * 1.25));
        Singleton.GetInstance().setWelcomeFirst(true);
        if (SaveSharedPreferences.getWCLogin(LoginActivity.this).length() == 0) {

            welcomeImageList.add(R.drawable.a111);
            welcomeImageList.add(R.drawable.a222);
            welcomeImageList.add(R.drawable.a333);
            welcomeImageList.add(R.drawable.a444);
            welcomeImageList.add(R.drawable.a555);
            welcomeImageList.add(R.drawable.a666);
            welcomeImageList.add(R.drawable.a777);
            SaveSharedPreferences.setWCLogin(LoginActivity.this, "Already Done");
            welcomeDialog();
        } else {

            if (SaveSharedPreferences.getTokon(LoginActivity.this).length() != 0) {
                CheckGPSEnable();
            }

        }


        if (utilityClass.isTablet(this) || utilityClass.isTabletDevice(this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
        if (SaveSharedPreferences.getUID(LoginActivity.this).length() != 0) {
            etUser.setText(SaveSharedPreferences.getUID(LoginActivity.this));
            etPassword.setText(SaveSharedPreferences.getPassWord(LoginActivity.this));
            cbRemember.setChecked(true);
        }

        Log.d("Device Screen", "Height : " + utilityClass.GetHeight() + "  Width : " + utilityClass.GetWidth());
        // utilityClass.toast("Height : "+utilityClass.GetHeight()+"  Width : "+utilityClass.GetWidth());
       /* FrameLayout.LayoutParams Params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, Singleton.GetInstance().getScreenHeight());
        llBackGroundLogin.setLayoutParams(Params);*/


        btnTwitter_login_button.setCallback(new com.twitter.sdk.android.core.Callback<TwitterSession>() {


            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls

                session = result.data;
                authToken = session.getAuthToken();
                Log.d("Twitter Success ", result.toString());

                token = authToken.token;
                secret = authToken.secret;
                String userName = session.getUserName();

                Log.d("Twitter Data ", token + " : " + secret + " : " + userName);
                long userId = session.getUserId();

                //   saveCredentials(token, secret, userName, userId);
                utilityClass.toast("Welcome Mr. " + userName);

                Log.d("Twitter :", "" + "Token :: " + token + " || Secret :: " + secret + " || " + userName + " || " + userId);
                authClient = new TwitterAuthClient();
                authClient.requestEmail(session, new com.twitter.sdk.android.core.Callback<String>() {
                    @Override
                    public void success(Result<String> result) {
                        // Do something with the result, which provides the email address
                        Log.d("Email Data Twitter : ", result.data.toString());
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        // Do something on failure
                        Log.d("Fail Email Twitter : ", exception.toString());
                    }
                });

            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
                Log.d("Twitter failor  ", exception.toString());
            }
        });


    }

    private void initialization() {


    }


    @OnClick(R.id.btnLogin)
    public void setBtnLogin() {
        // tvError.setVisibility(View.GONE);
        if (checkValidation()) {
            if (utilityClass.isInternetConnection()) {
                utilityClass.processDialogStart();
                NetworkErrorCount = 0;
                restService();
            } else {
                utilityClass.toast("Please check your internet settings!!! ");
            }

        }
    }

    @OnClick(R.id.tvSignUp)
    public void setTvSignUp() {
       // Log.d("Hash Key :",String.valueOf(printKeyHash(LoginActivity.this)));
       Intent SignUpIntent = new Intent(LoginActivity.this, SignUpActivity.class);
       startActivity(SignUpIntent);

    }

    @OnClick(R.id.tvForgerPass)
    public void setTvForgetPass() {
        Intent ForgetPassIntent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
        startActivity(ForgetPassIntent);
    }

    @OnClick(R.id.tvFaceBook_login)
    public void setTvFaceBook_login() {
        if (utilityClass.isInternetConnection()) {

            callbackManager = CallbackManager.Factory.create();

            // Set permissions
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList( "public_profile","user_friends"));
           //         btnFaceBook_login_button.setReadPermissions("user_friends");
           // btnFaceBook_login_button.setReadPermissions(Arrays.asList("public_profile","email","user_location", "user_friends"));

            btnFaceBook_login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    // App code
                    Log.d("FB LogIn Success: ", loginResult.toString());
                    utilityClass.toast("FaceBook Success");

                    GraphRequest.newMeRequest(
                            loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject json, GraphResponse response) {
                                    if (response.getError() != null) {
                                        // handle error
                                        System.out.println("ERROR");
                                    } else {

                                        try {
                                            String jsonresult = String.valueOf(json);
                                            Log.d("FaceBook JSON Result : ", jsonresult);

                                            String str_email = json.optString("email");
                                            String str_id = json.optString("id");
                                            String str_name = json.optString("name");

                                            utilityClass.toast("Wellcome  : " + str_name);
                                        } catch (NullPointerException e) {
                                            Log.e("FaceBook Response  NullError : ", e.toString());
                                        }
                                    }
                                }

                            }).executeAsync();
                }

                @Override
                public void onCancel() {
                    // App code
                    Log.d("Facebook Log In Cancel ", "Cancel");
                    utilityClass.toast("FaceBook Cancel");
                }

                @Override
                public void onError(FacebookException exception) {
                    // App code
                    Log.d("Facebook LogIn Error:", exception.toString());
                    utilityClass.toast("FaceBook Error ");
                }
            });
        } else
            utilityClass.toast("Please check your internet settings!!!");
    }

    @OnClick(R.id.tvTwitter_login)
    public void setTvTwitter_login() {
        if (utilityClass.isInternetConnection()) {
            btnTwitter_login_button.performClick();
        } else {
            utilityClass.toast("Please check your internet settings!!! ");
        }

    }


    private void restService() {
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Content-Type", "application/json");
                //request.addHeader("User-Agent", "Your-App-Name");
                //  request.addHeader("Accept", "application/vnd.yourapi.v1.full+json");
            }
        };
        restAdapter = new RestAdapter.Builder()
                .setEndpoint(constantClass.endPoints)
                .setRequestInterceptor(requestInterceptor)
                .build();

        api = restAdapter.create(RestServices.class);
        HashMap<String, String> HMLogIn = new HashMap<String, String>();
        HMLogIn.put("username", etUser.getText().toString());
        HMLogIn.put("password", etPassword.getText().toString());
        api.userLogin(HMLogIn, new Callback<JsonElement>() {
            @Override
            public void success(JsonElement result, Response response) {
                utilityClass.processDialogStop();
                NetworkErrorCount = 0;
                if (cbRemember.isChecked()) {
                    SaveSharedPreferences.setUID(LoginActivity.this, etUser.getText().toString());
                }
                SaveSharedPreferences.setPassWord(LoginActivity.this, etPassword.getText().toString());
                try {
                    if (result.isJsonObject()) {
                        //JsonObject jsonObject = result.getAsJsonObject();
                        JSONObject jsonObject = new JSONObject(String.valueOf(result.getAsJsonObject().toString()));
                        String userData = String.valueOf(jsonObject.toString());


                        // Log.i("JsonObject Size :  ", String.valueOf(jsonObject));
                        Log.i("UserData : ", userData);

                        Log.i("Tokon ID Data : ", String.valueOf(jsonObject.optString("token").toString()));
                        SaveSharedPreferences.setLoginObj(LoginActivity.this, jsonObject.toString());
                        SaveSharedPreferences.setCookie(LoginActivity.this, String.valueOf(jsonObject.optString("session_name").toString())
                                + "=" + String.valueOf(jsonObject.optString("sessid").toString()));
                        SaveSharedPreferences.setTokon(LoginActivity.this, String.valueOf(jsonObject.optString("token").toString()));
                        SaveSharedPreferences.setUserID(LoginActivity.this, String.valueOf(jsonObject.getJSONObject("user").optString("uid")));
                        SaveSharedPreferences.setUserName(LoginActivity.this,String.valueOf(jsonObject.getJSONObject("user").optString("name")));

                        CheckGPSEnable();

                    } else {
                        //     Log.i("JsonArray Size :  ", String.valueOf(result));
                    }

                } catch (IllegalStateException e) {
                    Log.e("IllegalStateException:", e.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("Retrofit Error  : ", String.valueOf(error.toString() + " Network " + RetrofitError.Kind.NETWORK + " : " + error.isNetworkError()));
                if (error.isNetworkError()) {
                    if (NetworkErrorCount >= 3) {
                        utilityClass.toast("Network error, please try again!!!.");
                        utilityClass.processDialogStop();
                        NetworkErrorCount = 0;
                    } else {
                        NetworkErrorCount++;
                        restService();
                    }
                }else {
                    //  tvError.setVisibility(View.VISIBLE);
                    utilityClass.processDialogStop();
                    if (error.toString().contains("No address associated with hostname")) {
                        tvError.setText("Please check your network connection or try again later");
                        utilityClass.toast("Please check your network connection or try again later.");
                    } else if (error.toString().equals("retrofit.RetrofitError: 500 Internal Server Error")) {
                        utilityClass.toast("Internal Server Error");
                        tvError.setText("Internal Server Error");
                    } else if (error.toString().contains("com.google.gson.JsonSyntaxException")) {
                        tvError.setText("Result not found form server");
                        utilityClass.toast("Result not found form server");
                    } else if (error.toString().contains("RetrofitError: 404 Site or")) {
                        tvError.setText("Please check your internet settings!!!");
                        utilityClass.toast("Please check your internet settings!!!");
                    } else {
                        tvError.setText("Invalid user or password");
                        utilityClass.toast("Invalid user or password");
                    }
                }
            }
        });
    }


    private boolean checkValidation() {
        boolean ret = true;

        if (!Validation.hasText(etUser, "username")) ret = false;
        else if (!Validation.hasText(etPassword, "password")) ret = false;
        return ret;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();
       /* if (alertDialog != null) {
            alertDialog.hide();
        }*/
        if (!Singleton.GetInstance().getWelcomeFirst()) {
            if (SaveSharedPreferences.getTokon(LoginActivity.this).length() != 0) {
                CheckGPSEnable();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Activity Result : ", String.valueOf(resultCode));
        Log.d("Activity Data : ", String.valueOf(data));
        btnTwitter_login_button.onActivityResult(requestCode, resultCode, data);
        if (callbackManager != null) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        // Pass the activity result to the login button.

    }

    private void CheckGPSEnable() {
        if (utilityClass.isInternetConnection()) {
            utilityClass.processDialogStart();
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            gpsTracker = new GPSTracker(LoginActivity.this);
            Log.d("Check GPS Enable : ", "True Or False");
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                //  location = gpsTracker.getLocation();
                try {
                    getLocationCityName(String.valueOf(gpsTracker.getLatitude()) + "," + String.valueOf(gpsTracker.getLongitude()));

                    Log.d("Location Latitude : ", String.valueOf(gpsTracker.getLatitude()) + "  Longitude: " + String.valueOf(gpsTracker.getLongitude()));
                } catch (NullPointerException e) {
                    utilityClass.processDialogStop();
                    Log.e("Location Error Null:", e.toString());
                }

            } else {
                utilityClass.processDialogStop();
                showGPSDisabledAlertToUser();
            }
        } else {
            utilityClass.toast("Please check your internet settings!!!");
            if (SaveSharedPreferences.getTokon(LoginActivity.this).length() != 0) {
                Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(homeIntent);
            }
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
                if (SaveSharedPreferences.getTokon(LoginActivity.this).length() != 0) {
                    Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(homeIntent);
                }
                dialog.cancel();
            }
        });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    public void getLocationCityName(String latlng) {
        restAdapter = new RestAdapter.Builder()
                .setEndpoint(constantClass.googleEndPoints)
                .build();
        api = restAdapter.create(RestServices.class);
        Log.d("Old Location City ", SaveSharedPreferences.getCityName(this));
        api.getCityName(latlng, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject result, Response response) {
                utilityClass.processDialogStop();
                try {
                    // JSONArray array = result.getJSONArray("results");
                    JSONArray array = new JSONArray(result.getAsJsonArray("results").toString());
                    if (array.length() > 0) {
                        JSONObject place = array.getJSONObject(0);
                        JSONArray components = place.getJSONArray("address_components");
                        for (int i = 0; i < components.length(); i++) {
                            JSONObject component = components.getJSONObject(i);
                            JSONArray types = component.getJSONArray("types");
                            for (int j = 0; j < types.length(); j++) {
                                if (types.optString(j).equals("locality")) {
                                    //   return component.optString("long_name");
                                    SaveSharedPreferences.setCityName(LoginActivity.this, component.optString("long_name"));
                                }
                            }
                        }
                    } else {
                        if (SaveSharedPreferences.getCityName(LoginActivity.this).equalsIgnoreCase(""))
                            SaveSharedPreferences.setCityName(LoginActivity.this, "all");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (SaveSharedPreferences.getTokon(LoginActivity.this).length() != 0) {
                    Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(homeIntent);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Retrofit Error  : ", String.valueOf(error.toString()));
                utilityClass.processDialogStop();
                if (SaveSharedPreferences.getTokon(LoginActivity.this).length() != 0) {
                    Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(homeIntent);
                }
                if (error.toString().contains("No address associated with hostname")) {
                    //   tvError.setText("Please check your network connection or try again later");
                    utilityClass.toast("Please check your network connection or try again later.");
                } else if (error.toString().equals("retrofit.RetrofitError: 500 Internal Server Error")) {
                    utilityClass.toast("Internal Server Error");
                    //  tvError.setText("Internal Server Error");
                } else if (error.toString().contains("com.google.gson.JsonSyntaxException")) {
                    //tvError.setText("Result not found form server");
                    utilityClass.toast("Result not found form server");
                } else {
                    //tvError.setText("Invalid user or password");
                    utilityClass.toast("Something went wrong!!!");
                }
            }
        });
    }

    public void welcomeDialog() {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.deal_image_list_cell);
        Singleton.GetInstance().setDialog(dialog);

        // welcomeImageList.add(R.drawable.a8);
        ViewPager ivPagerDisplay = (ViewPager) dialog.findViewById(R.id.ivPagerDisplay);
        ImageView ivDealDetailsImageViewCancel = (ImageView) dialog.findViewById(R.id.ivDealDetailsImageViewCancel);
        CirclePageIndicator indicator = (CirclePageIndicator) dialog.findViewById(R.id.indicator);
        ivPagerDisplay.setAdapter(new WelcomeImageListAdapter(welcomeImageList, dialog, LoginActivity.this));
        ivDealDetailsImageViewCancel.setVisibility(View.GONE);
        indicator.setViewPager(ivPagerDisplay);
        dialog.setCancelable(false);


        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (SaveSharedPreferences.getTokon(LoginActivity.this).length() != 0) {
                    CheckGPSEnable();
                }
            }
        });
    }

    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.e("Package Name=", context.getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));
                Log.d("DEFAULT KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));

              //   String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        }
        catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }
}
