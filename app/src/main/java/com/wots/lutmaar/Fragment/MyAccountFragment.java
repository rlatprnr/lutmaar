package com.wots.lutmaar.Fragment;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;
import com.wots.lutmaar.Adapter.SelectCityAdapter;
import com.wots.lutmaar.CustomView.imagechooser.api.ChooserType;
import com.wots.lutmaar.CustomView.imagechooser.api.ChosenImage;
import com.wots.lutmaar.CustomView.imagechooser.api.ImageChooserListener;
import com.wots.lutmaar.CustomView.imagechooser.api.ImageChooserManager;
import com.wots.lutmaar.CustomView.imageshape.RoundedImageView;
import com.wots.lutmaar.Interface.RestServices;
import com.wots.lutmaar.R;
import com.wots.lutmaar.UtilClass.ConstantClass;
import com.wots.lutmaar.UtilClass.GPSTracker;
import com.wots.lutmaar.UtilClass.SaveSharedPreferences;
import com.wots.lutmaar.UtilClass.Singleton;
import com.wots.lutmaar.UtilClass.UtilityClass;
import com.wots.lutmaar.UtilClass.Validation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MyAccountFragment extends Fragment implements ImageChooserListener {
    //Class
    UtilityClass utilityClass;
    ConstantClass constantClass;
    JSONObject jsonObject = null;
    JSONObject userJsonObject = null;
    JSONArray addressJsonArray = null;
    LocationManager locationManager;
    GPSTracker gpsTracker;
    Location location;
    ImageChooserManager imageChooserManager;

    //Interface
    RestServices api;
    RestAdapter restAdapter;

    //View
    @InjectView(R.id.ivMyAccUserImage)
    RoundedImageView ivMyAccUserImage;
    @InjectView(R.id.tvMyAccCreated)
    TextView tvMyAccCreated;
    @InjectView(R.id.tvDealPostDetails)
    TextView tvDealPostDetails;
    @InjectView(R.id.tvMyAccountUserName)
    TextView tvMyAccountUserName;
    @InjectView(R.id.etMyAccountPassword)
    EditText etMyAccountPassword;
    @InjectView(R.id.etMyAccountConfPassword)
    EditText etMyAccountConfPassword;
    @InjectView(R.id.tvMyAccountEmail)
    TextView tvMyAccountEmail;
    @InjectView(R.id.ivMyAccImageView)
    ImageView ivMyAccImageView;
    @InjectView(R.id.etMyAccLocation)
    EditText etMyAccLocation;
    @InjectView(R.id.etMyAccStreet)
    EditText etMyAccStreet;
    @InjectView(R.id.etMyAccTown)
    EditText etMyAccTown;
    @InjectView(R.id.tvMyAccCity)
    TextView tvMyAccCity;
    @InjectView(R.id.etMyAccPinCode)
    EditText etMyAccPinCode;
    @InjectView(R.id.tvMyAccCountry)
    TextView tvMyAccCountry;
    @InjectView(R.id.tvMyAccSubmit)
    TextView tvMyAccSubmit;

    //Variable
    int maxLength = 0;
    int NetworkErrorCount = 0;
    String UserName = "";
    int chooserType;
    String mediaPath, imgPathAttached = "", fid = "";

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        View view = inflater.inflate(R.layout.my_account_fragment, container, false);
        ButterKnife.inject(this, view);
        declaration();
        initialization();
        return view;
    }

    private void declaration() {
        utilityClass = new UtilityClass(getActivity());
        gpsTracker = new GPSTracker(getActivity());
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        try {
            jsonObject = new JSONObject(SaveSharedPreferences.getLoginObj(getActivity()));
            userJsonObject = jsonObject.getJSONObject("user");
            UserName = userJsonObject.optString("name");
            tvMyAccountUserName.setText("Good Day, " + UserName);
            tvMyAccountEmail.setText(userJsonObject.optString("mail"));
        } catch (JSONException e) {
            Log.e("User Details User:", e.toString());
        }
        utilityClass.processDialogStart();
        NetworkErrorCount = 0;
        restServiceGetUserDetails();

    }

    private void initialization() {
        check_validations();
    }

    private void check_validations() {

        etMyAccountPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                maxLength = etMyAccountPassword.length();
                etMyAccountConfPassword.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
                if (maxLength < 6) {
                    etMyAccountPassword.setError("Minimum 6 character require");
                }

            }
        });

    }

    private boolean checkValidation() {
        boolean ret = true;
        if (!etMyAccountPassword.getText().toString().equalsIgnoreCase("")) {
            if (maxLength < 6) {
                etMyAccountPassword.setError("Minimum 6 character require");
                ret = false;
            } else if (!Validation.hasText(etMyAccountConfPassword, "confirm password")) {
                ret = false;
            } else if (!Validation.verify(etMyAccountPassword, etMyAccountConfPassword)) {
                ret = false;
            }
        }
        return ret;

    }

    @OnClick(R.id.tvMyAccSubmit)
    public void setTvMyAccSubmit() {
        if (checkValidation()) {
            if (utilityClass.isInternetConnection()) {
                tvMyAccSubmit.setEnabled(false);
                utilityClass.processDialogStart();
                EditProfileRestService();
            } else {

            }
        }
    }

    @OnClick(R.id.tvMyAccCity)
    public void setTvMyAccCity() {
        SelectCityDialog();
    }

    @OnClick(R.id.ivMyAccImageView)
    public void seIvMyAccImageView() {
        chooserType = ChooserType.REQUEST_PICK_PICTURE;
        imageChooserManager = new ImageChooserManager(this, ChooserType.REQUEST_PICK_PICTURE, true);
        imageChooserManager.setImageChooserListener(this);
        try {
            mediaPath = imageChooserManager.choose();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.ivMyAccCurrentLocation)
    public void setIvMyAccCurrentLocation() {
        if (utilityClass.isInternetConnection()) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                location = gpsTracker.getLocation();
                if (location != null && (gpsTracker.getLatitude() != 0.0 || gpsTracker.getLongitude() != 0.0)) {
                    utilityClass.processDialogStart();
                    getCurrentLocation(String.valueOf(gpsTracker.getLatitude()) + "," + String.valueOf(gpsTracker.getLongitude()));
                } else {
                    utilityClass.toast("Location can not found!!!");
                }
            } else {
                utilityClass.showGPSDisabledAlertToUser();
            }
        } else {
            utilityClass.toast("Please check your internet settings!!!");
        }
    }

    private void SelectCityDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.select_city_cell);
        final SelectCityAdapter adapter;
        final EditText etSearchCity = (EditText) dialog.findViewById(R.id.etSearchCity);
        ImageView ivGetCurrentCity = (ImageView) dialog.findViewById(R.id.ivGetCurrentCity);
        ListView lvSearchCity = (ListView) dialog.findViewById(R.id.lvSearchCity);
        adapter = new SelectCityAdapter(getActivity(), dialog, tvMyAccCity, "MyAccount", "City", Singleton.GetInstance().getSelectCityName());
        lvSearchCity.setAdapter(adapter);
        ivGetCurrentCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (utilityClass.isInternetConnection()) {
                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        location = gpsTracker.getLocation();
                        if (location != null && (gpsTracker.getLatitude() != 0.0 || gpsTracker.getLongitude() != 0.0)) {
                            utilityClass.processDialogStart();
                            getLocationCityName(etSearchCity, String.valueOf(gpsTracker.getLatitude()) + "," + String.valueOf(gpsTracker.getLongitude()));
                        } else {
                            utilityClass.toast("Location can not found!!!");
                        }
                    } else {
                        utilityClass.showGPSDisabledAlertToUser();
                    }
                } else {
                    utilityClass.toast("Please check your internet settings!!!");
                }
            }
        });
        etSearchCity.addTextChangedListener(new TextWatcher() {


            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                String text = etSearchCity.getText().toString().toLowerCase(Locale.getDefault());
                // adapter.filter(text);
                adapter.getFilter().filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

        dialog.show();
    }

    private void restServiceGetUserDetails() {

        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Content-Type", "application/json");
                request.addHeader("X-CSRF-Token", SaveSharedPreferences.getTokon(getActivity()));
                request.addHeader("Cookie", SaveSharedPreferences.getCookie(getActivity()));

                Log.i("MyDeal Data Tokon : ", SaveSharedPreferences.getTokon(getActivity()));
                Log.i("MyDeal Data Cookie : ", SaveSharedPreferences.getCookie(getActivity()));
            }
        };

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(constantClass.endPoints)
                .setRequestInterceptor(requestInterceptor)
                .build();
        api = restAdapter.create(RestServices.class);
        api.getUserDetails(new Callback<JsonObject>() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void success(JsonObject result, Response response) {
                utilityClass.processDialogStop();
                Log.d("User Details Result : ", String.valueOf(result));
                NetworkErrorCount = 0;
                try {
                    JSONArray jsonArrayNew = new JSONArray(result.getAsJsonArray("nodes").toString());
                    JSONObject newJsonObject = jsonArrayNew.getJSONObject(0).getJSONObject("node");
                    tvMyAccCreated.setText("Member: " + newJsonObject.optString("created_date"));
                    tvDealPostDetails.setText("Deal posted: " + newJsonObject.optString("deals_created"));
                    Picasso.with(getActivity()).load(newJsonObject.optString("picture")).placeholder(R.mipmap.ic_launcher).noFade().resize(200, 200).centerInside().into(ivMyAccUserImage);
                    Picasso.with(getActivity()).load(newJsonObject.optString("picture")).noFade().resize(200, 200).centerInside().into(ivMyAccImageView);
                    tvMyAccCity.setText(newJsonObject.optString("city"));
                    etMyAccLocation.setText(newJsonObject.optString("full_name"));
                    etMyAccStreet.setText(newJsonObject.optString("thoroughfare"));
                    etMyAccTown.setText(newJsonObject.optString("address2"));
                    etMyAccPinCode.setText(newJsonObject.optString("postal_code"));


                } catch (IllegalStateException e) {
                    Log.e("IllegalStateException:", e.toString());
                } catch (JSONException e) {
                    Log.e("JSONException:", e.toString());
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
                        restServiceGetUserDetails();
                    }
                } else {
                    utilityClass.processDialogStop();
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
                    } else {
                        utilityClass.toast("Image can not uploaded");
                    }
                }
            }
        });
    }

    private void EditProfileRestService() {

        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Content-Type", "application/json");
                request.addHeader("X-CSRF-Token", SaveSharedPreferences.getTokon(getActivity()));
                request.addHeader("Cookie", SaveSharedPreferences.getCookie(getActivity()));

                Log.i("Edit Prof. Tokon : ", SaveSharedPreferences.getTokon(getActivity()));
                Log.i("Edit Prof. Cookie : ", SaveSharedPreferences.getCookie(getActivity()));
            }
        };
        restAdapter = new RestAdapter.Builder()
                .setEndpoint(constantClass.endPoints)
                .setRequestInterceptor(requestInterceptor)
                .build();
        api = restAdapter.create(RestServices.class);
        HashMap<String, Object> HMProfileEdit = new HashMap<String, Object>();
        ArrayList<HashMap<String, String>> ALHMAdd = new ArrayList<HashMap<String, String>>();
        HashMap<String, Object> HMaddUnd = new HashMap<String, Object>();
        HashMap<String, String> HMaddress = new HashMap<String, String>();

        // HMImageEditUnd.put("und", ImageIDList);
        HMaddress.put("locality", tvMyAccCity.getText().toString());
        HMaddress.put("name_line", etMyAccLocation.getText().toString());
        HMaddress.put("thoroughfare", etMyAccStreet.getText().toString());
        HMaddress.put("premise", etMyAccTown.getText().toString());
        HMaddress.put("postal_code", etMyAccPinCode.getText().toString());


        ALHMAdd.add(HMaddress);
        HMaddUnd.put("und", ALHMAdd);

        HMProfileEdit.put("name", UserName);
        HMProfileEdit.put("current_pass", SaveSharedPreferences.getPassWord(getActivity()));
        HMProfileEdit.put("mail", tvMyAccountEmail.getText().toString());
        HMProfileEdit.put("field_user_address", HMaddUnd);
        HMProfileEdit.put("picture", fid);
        if (!etMyAccountPassword.getText().toString().equalsIgnoreCase(""))
            HMProfileEdit.put("pass", etMyAccountPassword.getText().toString());
        else
            HMProfileEdit.put("pass", SaveSharedPreferences.getPassWord(getActivity()));

        Log.i("Request Edit image:", new Gson().toJson(HMProfileEdit).toString());

        api.EditProfile(SaveSharedPreferences.getUserID(getActivity()), HMProfileEdit, new Callback<Object>() {
            @Override
            public void success(Object result, Response response) {
                utilityClass.processDialogStop();
                tvMyAccSubmit.setEnabled(true);
                NetworkErrorCount = 0;
                utilityClass.toast("Save success!!!");
                SaveSharedPreferences.setPassWord(getActivity(), etMyAccountPassword.getText().toString());
                resetDate();
            }

            @Override
            public void failure(RetrofitError error) {

                Log.i("Retrofit Error  : ", String.valueOf(error.toString() + " Network " + RetrofitError.Kind.NETWORK + " : " + error.isNetworkError()));

                if (error.isNetworkError()) {
                    if (NetworkErrorCount >= 3) {
                        utilityClass.toast("Network error, please try again!!!.");
                        utilityClass.processDialogStop();
                        tvMyAccSubmit.setEnabled(true);
                        NetworkErrorCount = 0;
                    } else {
                        NetworkErrorCount++;
                        EditProfileRestService();
                    }
                } else {
                    utilityClass.processDialogStop();
                    NetworkErrorCount = 0;
                    tvMyAccSubmit.setEnabled(true);
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
                    } else {
                        utilityClass.toast("Can not Success!!!");
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Singleton.GetInstance().BottomMenuHide();
        Singleton.GetInstance().getTvMainHeading().setText("My Account");
    }

    void resetDate() {
        etMyAccountPassword.setText("");
        etMyAccountConfPassword.setText("");
        etMyAccPinCode.setText("");
        etMyAccLocation.setText("");
        etMyAccStreet.setText("");
        etMyAccTown.setText("");

    }

    public void getLocationCityName(final EditText cityName, String latlng) {
        restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://maps.googleapis.com")
                .build();
        api = restAdapter.create(RestServices.class);
        Log.d("Old Location City ", SaveSharedPreferences.getCityName(getActivity()));
        api.getCityName(latlng, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject result, Response response) {
                utilityClass.processDialogStop();
                Log.d("City Find result :", result.toString());
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

                                    SaveSharedPreferences.setCityName(getActivity(), component.optString("long_name"));
                                    cityName.setText(component.optString("long_name"));
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Restrofit Error  : ", String.valueOf(error.toString()));
                utilityClass.processDialogStop();
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

    public void getCurrentLocation(String latlng) {
        restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://maps.googleapis.com")
                .build();
        api = restAdapter.create(RestServices.class);
        Log.d("Old Location City ", SaveSharedPreferences.getCityName(getActivity()));
        api.getCityName(latlng, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject result, Response response) {
                utilityClass.processDialogStop();
                Log.d("City Find result :", result.toString());
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
                                    SaveSharedPreferences.setCityName(getActivity(), component.optString("long_name"));
                                    tvMyAccCity.setText(component.optString("long_name"));
                                } else if (types.optString(j).equals("route")) {
                                    etMyAccStreet.setText(component.optString("long_name"));
                                } else if (types.optString(j).equals("sublocality")) {
                                    etMyAccTown.setText(component.optString("long_name"));
                                } else if (types.optString(j).equals("postal_code")) {
                                    etMyAccPinCode.setText(component.optString("long_name"));
                                } else if (types.optString(j).equals("country")) {
                                    tvMyAccCountry.setText(component.optString("long_name"));
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Restrofit Error  : ", String.valueOf(error.toString()));
                utilityClass.processDialogStop();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("On Activity Result", requestCode + "");
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            if (imageChooserManager == null) {
                imageChooserManager = new ImageChooserManager(this, requestCode, true);
                imageChooserManager.setImageChooserListener(this);
                imageChooserManager.reinitialize(mediaPath);
            }
            imageChooserManager.submit(requestCode, data);
        }
    }

    @Override
    public void onImageChosen(final ChosenImage image) {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (image != null) {
                    imgPathAttached = image.getFilePathOriginal();
                    File file = new File(imgPathAttached);
                    long length = file.length();
                    length = length / 1024;
                    Log.d(" Image Path : ", imgPathAttached);
                    Log.d(" Image size : ", length + " KB");
                    if (length <= 3070) {
                        Bitmap thumbnail = (BitmapFactory.decodeFile(imgPathAttached));
                        // Bitmap bmpPic1 = Bitmap.createBitmap(thumbnail, 0, 0, thumbnail.getWidth(), thumbnail.getHeight(), m, true);
                        // thumbnail = Bitmap.createScaledBitmap(thumbnail, 240, 260, true);
                        //thumbnail = Bitmap.createScaledBitmap(bmpPic1, 240, 260, true);

                        ivMyAccImageView.setImageBitmap(thumbnail);
                        NetworkErrorCount = 0;
                        restServiceUploadImage();
                    } else {
                        imgPathAttached = "";
                        utilityClass.toast("Image is not Attached!!! \n Image must be less then 3 MB.");
                    }
                }
            }
        });
    }

    @Override
    public void onError(String reason) {

    }

    private void restServiceUploadImage() {


        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Content-Type", "application/json");
                request.addHeader("X-CSRF-Token", SaveSharedPreferences.getTokon(getActivity()));
                request.addHeader("Cookie", SaveSharedPreferences.getCookie(getActivity()));

                Log.i("Image Upload Tokon : ", SaveSharedPreferences.getTokon(getActivity()));
                Log.i("Image Upload Cookie : ", SaveSharedPreferences.getCookie(getActivity()));
            }
        };

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(constantClass.endPoints)
                .setRequestInterceptor(requestInterceptor)
                .build();

        api = restAdapter.create(RestServices.class);

        HashMap<String, String> HMImageData = new HashMap<String, String>();

        HMImageData.put("filename", imgPathAttached.substring(imgPathAttached.lastIndexOf("/") + 1));
        HMImageData.put("target_uri", imgPathAttached);
        HMImageData.put("filemime", "image/jpeg");
        Bitmap thumbnail = (BitmapFactory.decodeFile(imgPathAttached));
        thumbnail = Bitmap.createScaledBitmap(thumbnail, 240, 260, true);
        HMImageData.put("file", utilityClass.ConvertBitmapToBase64Format(thumbnail));

        Log.i("Request image Data:", HMImageData.toString());
        api.PostAnImage(HMImageData, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject result, Response response) {
                // utilityClass.processDialogStop();
                Log.i("image Uploaded: ", result.toString());
                HashMap<String, String> imageDetails = new HashMap<String, String>();
                fid = result.get("fid").getAsString().toString();
                utilityClass.toast("Image is uploaded");
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
                        restServiceUploadImage();
                    }
                } else {
                    utilityClass.processDialogStop();
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
                        utilityClass.toast("Image can not uploaded");
                    }
                }
            }
        });
    }
}
