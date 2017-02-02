package com.wots.lutmaar.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;
import com.wots.lutmaar.Adapter.SelectCityAdapter;
import com.wots.lutmaar.CustomView.ImageLoader.ImageLoader;
import com.wots.lutmaar.Interface.RestServices;
import com.wots.lutmaar.R;
import com.wots.lutmaar.UtilClass.ConstantClass;
import com.wots.lutmaar.UtilClass.GPSTracker;
import com.wots.lutmaar.UtilClass.SaveSharedPreferences;
import com.wots.lutmaar.UtilClass.Singleton;
import com.wots.lutmaar.UtilClass.UtilityClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MapNearByFragment extends Fragment {
    //Class
    UtilityClass utilityClass;
    JSONArray jsonArray;
    JSONObject jsonObject;
    ImageLoader imageLoader;
    Marker markerTemp;
    GPSTracker gpsTracker;
    LocationManager locationManager;
    Location location;

    //Interface
    RestServices api;
    RestAdapter restAdapter;

    //View
    View view = null, mapView = null;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    @InjectView(R.id.LLAllDeals)
    LinearLayout LLAllDeals;
    @InjectView(R.id.LLDeals)
    LinearLayout LLDeals;
    @InjectView(R.id.LLVouchers)
    LinearLayout LLVouchers;
    @InjectView(R.id.LLFreebies)
    LinearLayout LLFreebies;
    @InjectView(R.id.LLCompetition)
    LinearLayout LLCompetition;
    @InjectView(R.id.ivMapAll)
    ImageView ivMapAll;
    @InjectView(R.id.ivMapDeals)
    ImageView ivMapDeals;
    @InjectView(R.id.ivMapVouchers)
    ImageView ivMapVouchers;
    @InjectView(R.id.ivMapFreebies)
    ImageView ivMapFreebies;
    @InjectView(R.id.ivMapCompetition)
    ImageView ivMapCompetition;
    @InjectView(R.id.tvMapAll)
    TextView tvMapAll;
    @InjectView(R.id.tvMapDeals)
    TextView tvMapDeals;
    @InjectView(R.id.tvMapVouchers)
    TextView tvMapVouchers;
    @InjectView(R.id.tvMapFreebies)
    TextView tvMapFreebies;
    @InjectView(R.id.tvMapCompetition)
    TextView tvMapCompetition;
    //LenearLayout MapDeal
    @InjectView(R.id.LLMapDeal)
    LinearLayout LLMapDeal;
    @InjectView(R.id.ivMapDeal)
    ImageView ivMapDeal;
    @InjectView(R.id.tvMapDealTitle)
    TextView tvMapDealTitle;
    @InjectView(R.id.tvMapDealDetails)
    TextView tvMapDealDetails;
    @InjectView(R.id.tvMapDealPrice)
    TextView tvMapDealPrice;
    @InjectView(R.id.tvMapDealComment)
    TextView tvMapDealComment;
    @InjectView(R.id.tvMapDealTime)
    TextView tvMapDealTime;
    @InjectView(R.id.tvMapDealColdHot)
    TextView tvMapDealColdHot;

    //LenearLayout MapDeal Popup
    @InjectView(R.id.LLMapDealPopup)
    LinearLayout LLMapDealPopup;
    @InjectView(R.id.ivMapDealDetailsImageViewCancelPopup)
    ImageView ivMapDealDetailsImageViewCancelPopup;
    @InjectView(R.id.ivMapDealsPopup)
    ImageView ivMapDealsPopup;
    @InjectView(R.id.tvMapDealTitlePopup)
    TextView tvMapDealTitlePopup;
    @InjectView(R.id.tvMapDealDetailsPopup)
    TextView tvMapDealDetailsPopup;
    @InjectView(R.id.tvMapDealReviewsPopup)
    TextView tvMapDealReviewsPopup;
    @InjectView(R.id.tvMapDealDetailsHotColdValuePopupPopup)
    TextView tvMapDealDetailsHotColdValuePopupPopup;
    @InjectView(R.id.tvMapDealDetailsReviewPointsPopup)
    TextView tvMapDealDetailsReviewPointsPopup;
    @InjectView(R.id.tvMapDealDetailsShare)
    TextView tvMapDealDetailsShare;
    @InjectView(R.id.tvMapDealPricePopup)
    TextView tvMapDealPricePopup;
    @InjectView(R.id.ivMapDealDetailsHotPopup)
    ImageView ivMapDealDetailsHotPopup;
    @InjectView(R.id.ivMapDealDetailsColdPopup)
    ImageView ivMapDealDetailsColdPopup;
    @InjectView(R.id.tvMapDealCommentPopup)
    TextView tvMapDealCommentPopup;
    @InjectView(R.id.tvMapDealTimePopup)
    TextView tvMapDealTimePopup;

    //Search City Component
    @InjectView(R.id.LLMapSearchCity)
    LinearLayout LLMapSearchCity;
    @InjectView(R.id.ivMapSearchCityCancel)
    ImageView ivMapSearchCityCancel;
    @InjectView(R.id.tvMapSearchCity)
    TextView tvMapSearchCity;
    EditText etMapSearchCity;


    //Variable
    String DealType = " ", DealTypeServicePath, CityName;
    double latitude, longitude;
    int currentDataItem = 0, CurrentLocation = 0, CurrentItemSelected = -1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.map_near_by_fragment, container, false);
        ButterKnife.inject(this, view);

        //  Fragment fragment = getChildFragmentManager().findFragmentById(R.id.mapNearBy);
        // supportMapFragment = (SupportMapFragment) fragment;
        mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapNearBy)).getMap();

        declaration();
        return view;
    }

    private void declaration() {
        utilityClass = new UtilityClass(getActivity());
        gpsTracker = new GPSTracker(getActivity());
        imageLoader = new ImageLoader(getActivity().getApplicationContext());
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        LLMapDeal.setVisibility(View.GONE);
        LLMapDealPopup.setVisibility(View.GONE);
        LLMapSearchCity.setVisibility(View.GONE);
        DealTypeServicePath = "deal-view-service.json";
        if (SaveSharedPreferences.getCityName(getActivity()).equalsIgnoreCase("all")) {
            CityName = SaveSharedPreferences.getCityName(getActivity());
            getLocationCityName(String.valueOf(gpsTracker.getLatitude()) + "," + String.valueOf(gpsTracker.getLongitude()));
        } else {
            CityName = SaveSharedPreferences.getCityName(getActivity());
            tvMapSearchCity.setText(CityName);
            setLLAllDeals();
        }
        setUpMap();

    }

    @OnClick(R.id.LLAllDeals)
    public void setLLAllDeals() {
        if (!DealType.equalsIgnoreCase("all")) {
            resetMenu();
            ivMapAll.setImageResource(R.drawable.ic_allblue);
            tvMapAll.setTextColor(getResources().getColor(R.color.ColorPrimaryDark));
            DealType = "all";
            utilityClass.processDialogStart();
            AllDealsRestServiceWithCity();
        }
    }

    @OnClick(R.id.LLDeals)
    public void setLLDeals() {
        if (!DealType.equalsIgnoreCase("64")) {
            resetMenu();
            ivMapDeals.setImageResource(R.drawable.ic_dealsblue);
            tvMapDeals.setTextColor(getResources().getColor(R.color.ColorPrimaryDark));
            DealType = "64";
            utilityClass.processDialogStart();
            DealsRestService();
            //  AllDealsRestServiceWithCity();
        }
    }

    @OnClick(R.id.LLVouchers)
    public void setLLVouchers() {
        if (!DealType.equalsIgnoreCase("67")) {
            resetMenu();
            ivMapVouchers.setImageResource(R.drawable.ic_vouchersblue);
            tvMapVouchers.setTextColor(getResources().getColor(R.color.ColorPrimaryDark));
            DealType = "67";
            utilityClass.processDialogStart();
            DealsRestService();
            // AllDealsRestServiceWithCity();
        }
    }

    @OnClick(R.id.LLFreebies)
    public void setLLFreebies() {
        resetMenu();
        if (!DealType.equalsIgnoreCase("68")) {
            ivMapFreebies.setImageResource(R.drawable.ic_freebiesblue);
            tvMapFreebies.setTextColor(getResources().getColor(R.color.ColorPrimaryDark));
            DealType = "68";
            utilityClass.processDialogStart();
            DealsRestService();
            //AllDealsRestServiceWithCity();
        }
    }

    @OnClick(R.id.LLCompetition)
    public void setLLCompetition() {
        resetMenu();
        if (!DealType.equalsIgnoreCase("65")) {
            ivMapCompetition.setImageResource(R.drawable.ic_competitionblue);
            tvMapCompetition.setTextColor(getResources().getColor(R.color.ColorPrimaryDark));
            DealType = "65";
            utilityClass.processDialogStart();
            DealsRestService();
            // AllDealsRestServiceWithCity();
        }
    }

    @OnClick(R.id.ivMapDealDetailsImageViewCancelPopup)
    public void setIvMapDealDetailsImageViewCancelPopup() {
        LLMapDeal.setVisibility(View.VISIBLE);
        LLMapDealPopup.setVisibility(View.GONE);
        //  DealDialogShow();
    }

    @OnClick(R.id.LLMapDeal)
    public void setLLMapDeal() {
        LLMapDeal.setVisibility(View.GONE);
        LLMapDealPopup.setVisibility(View.VISIBLE);
        //  DealDialogShow();
    }

    @OnClick(R.id.ivMapSearchCityCancel)
    public void setIvMapSearchCityCancel() {
        LLMapSearchCity.setVisibility(View.GONE);
    }

    @OnClick(R.id.LLMapSearchCity)
    public void setLLMapSearchCity() {
        SelectCityDialog();
    }

   /* private void DealDialogShow() {
        final Dialog dialog = new Dialog(getActivity());// android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.map_deals_cell);

        TextView tvMapDealTitlePopup = (TextView) dialog.findViewById(R.id.tvMapDealTitlePopup);
        TextView tvMapDealDetailsPopup = (TextView) dialog.findViewById(R.id.tvMapDealDetailsPopup);
        ImageView ivMapDealsPopup = (ImageView) dialog.findViewById(R.id.ivMapDealsPopup);
        ImageView tvMapDealGetPopup = (ImageView) dialog.findViewById(R.id.tvMapDealGetPopup);
        ImageView ivMapDealDetailsHotPopup = (ImageView) dialog.findViewById(R.id.ivMapDealDetailsHotPopup);
        ImageView ivMapDealDetailsColdPopup = (ImageView) dialog.findViewById(R.id.ivMapDealDetailsColdPopup);
        TextView tvMapDealDetailsShare = (TextView) dialog.findViewById(R.id.tvMapDealDetailsShare);
        TextView tvMapDealDetailsReviewPointsPopup = (TextView) dialog.findViewById(R.id.tvMapDealDetailsReviewPointsPopup);
        TextView tvMapDealReviewsPopup = (TextView) dialog.findViewById(R.id.tvMapDealReviewsPopup);
        TextView tvMapDealDetailsHotColdValuePopupPopup = (TextView) dialog.findViewById(R.id.tvMapDealDetailsHotColdValuePopupPopup);
        TextView tvMapDealPricePopup = (TextView) dialog.findViewById(R.id.tvMapDealPricePopup);
        TextView tvMapDealTimePopup = (TextView) dialog.findViewById(R.id.tvMapDealTimePopup);
        TextView tvMapDealCommentPopup = (TextView) dialog.findViewById(R.id.tvMapDealCommentPopup);
        try {
            JSONArray arrayImageDialog = jsonObject.getJSONArray("field_images");
            tvMapDealTitlePopup.setText(jsonObject.getString("title"));
            tvMapDealDetailsPopup.setText(jsonObject.getString("body"));
            Picasso.with(getActivity()).load(arrayImageDialog.getJSONObject(1).getString("src"))
                    .placeholder(R.drawable.login).noFade().resize(100, 100).centerInside().into(ivMapDealsPopup);

            tvMapDealReviewsPopup.setText(jsonObject.getString("comment_count") + " Reviews");
            tvMapDealDetailsHotColdValuePopupPopup.setText(jsonObject.getString("value") + "o");
            tvMapDealPricePopup.setText("$" + jsonObject.getString("field_price"));
            tvMapDealCommentPopup.setText(jsonObject.getString("comment_count") + " Comments");
            tvMapDealTimePopup.setText(jsonObject.getString("created"));
        } catch (JSONException e) {
            Log.e("Json Error : ", e.toString());
        }

        dialog.show();
    }
*/

    public void AllDealsRestServiceWithCity() {
        restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://maps.googleapis.com")
                .build();
        api = restAdapter.create(RestServices.class);
        Log.d("Map Old Location City ", CityName);
        api.getLatLngLocation(CityName, "true", new Callback<JsonObject>() {
            @Override
            public void success(JsonObject result, Response response) {
                try {
                    // JSONArray array = result.getJSONArray("results");
                    JSONArray array = new JSONArray(result.getAsJsonArray("results").toString());
                    if (array.length() > 0) {
                        JSONObject place = array.getJSONObject(0);
                        //  JSONArray components = place.getJSONArray("address_components");
                        JSONObject geometry = place.getJSONObject("geometry");
                        JSONObject location = geometry.getJSONObject("location");
                        Log.d("City and LatLng : ", CityName + location.toString());
                        latitude = location.getDouble("lat");
                        longitude = location.getDouble("lng");
                        DealsRestService();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Retrofit Error  : ", String.valueOf(error.toString()));
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


    private void DealsRestService() {
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Content-Type", "application/json");
                request.addHeader("X-CSRF-Token", SaveSharedPreferences.getTokon(getActivity()));
                request.addHeader("Cookie", SaveSharedPreferences.getCookie(getActivity()));

                Log.i("Tokon : ", SaveSharedPreferences.getTokon(getActivity()));
                Log.i("Cookie : ", SaveSharedPreferences.getCookie(getActivity()));
            }
        };

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(ConstantClass.endPoints)
                .setRequestInterceptor(requestInterceptor)
                .build();
        api = restAdapter.create(RestServices.class);
        Log.d("Map DealType : ", DealType);
        api.getAllTypeDealsNearByLocation(String.valueOf(latitude) + "," +
                String.valueOf(longitude), DealType,new Callback<JsonObject>() {
            @Override
            public void success(JsonObject result, Response response) {
                utilityClass.processDialogStop();
                try {
                    jsonArray = new JSONArray(result.getAsJsonArray("nodes").toString());
                    setMapValue();
                } catch (IllegalStateException e) {
                    Log.e("IllegalStateException:", e.toString());
                } catch (JSONException e) {
                    Log.e("JSONException:", e.toString());
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Retrofit Error  : ", String.valueOf(error.toString()));
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


    private void setUpMap() {

        // Enable MyLocation Layer of Google Map
        mMap.setMyLocationEnabled(true);

        // Get LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        // Create a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Get the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // Get Current Location
        Location myLocation = locationManager.getLastKnownLocation(provider);

        // set map type
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    public void setMapValue() {
        mMap.clear();
        markerTemp = null;
        if (CurrentLocation == 0) {
            LatLng myCoordinates = new LatLng(latitude, longitude);
            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(myCoordinates, 20);
            mMap.animateCamera(yourLocation);

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(myCoordinates)      // Sets the center of the map to LatLng (refer to previous snippet)
                    .zoom(17)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            // Create a LatLng object for the current location
            LatLng latLng = new LatLng(latitude, longitude);

            // Show the current location in Google Map
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            // Zoom in the Google Map
            mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
            // ArrayList<LatLng> PlaceLocation = new ArrayList<LatLng>();
        } else {
            CurrentLocation = 0;
        }
        Log.d("Deal Data : ", jsonArray.toString());
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                mMap.addMarker(new MarkerOptions().position(new LatLng(
                        Double.parseDouble(jsonArray.getJSONObject(i).getJSONObject("node").getString("Latitude")),
                        Double.parseDouble(jsonArray.getJSONObject(i).getJSONObject("node").getString("Longitude"))))
                        .title(jsonArray.getJSONObject(i).getJSONObject("node").getString("title"))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location))
                        .anchor(0.5f, 1.0f)
                        .snippet(jsonArray.getJSONObject(i).getJSONObject("node").getString("body")));
                currentDataItem++;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                int markerId = Integer.parseInt(marker.getId().substring(1));
                markerId = markerId - (currentDataItem - jsonArray.length());
                if (CurrentItemSelected != markerId) {
                    String DealID = "", DealType = "";
                    LLMapDealPopup.setVisibility(View.GONE);
                    try {
                        if (markerTemp != null)
                            markerTemp.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location));
                        DealID = jsonArray.getJSONObject(markerId).getJSONObject("node").getString("nid");
                        DealType = jsonArray.getJSONObject(markerId).getJSONObject("node").getString("field_cat");
                    } catch (NullPointerException e) {
                        Log.e("Null Error : ", e.toString());
                    } catch (JSONException e) {
                        Log.e("JSON Error : ", e.toString());
                    }
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_selected_location));
                    marker.showInfoWindow();
                    utilityClass.processDialogStart();
                    restServiceSingleDeal(DealID, DealType);
                    CurrentItemSelected = markerId;
                    markerTemp = marker;
                }
                return true;
            }
        });
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {

                latitude = gpsTracker.getLatitude();
                longitude = gpsTracker.getLongitude();
                LLMapSearchCity.setVisibility(View.VISIBLE);
                utilityClass.processDialogStart();
                getLocationCityName(String.valueOf(gpsTracker.getLatitude()) + "," + String.valueOf(gpsTracker.getLongitude()));
                CurrentLocation = 1;
                DealsRestService();
                return false;
            }
        });
    }


    private void restServiceSingleDeal(String DealID, final String DealsType) {

        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Content-Type", "application/json");
                request.addHeader("X-CSRF-Token", SaveSharedPreferences.getTokon(getActivity()));
                request.addHeader("Cookie", SaveSharedPreferences.getCookie(getActivity()));

                Log.i("Tokon : ", SaveSharedPreferences.getTokon(getActivity()));
                Log.i("Cookie : ", SaveSharedPreferences.getCookie(getActivity()));
            }
        };

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(ConstantClass.endPoints)
                .setRequestInterceptor(requestInterceptor)
                .build();
        api = restAdapter.create(RestServices.class);
        if (DealsType.equalsIgnoreCase("Deals")) {
            DealTypeServicePath = "deal-view-service.json";
        } else if (DealsType.equalsIgnoreCase("Vouchers")) {
            DealTypeServicePath = "voucher-view-service.json";
        } else if (DealsType.equalsIgnoreCase("Freebies")) {
            DealTypeServicePath = "freebee-view-service.json";
        } else if (DealsType.equalsIgnoreCase("Ask")) {
            DealTypeServicePath = "ask-view-service.json";
        } else if (DealsType.equalsIgnoreCase("Competitions")) {
            DealTypeServicePath = "competition-view-service.json";
        } else {
            DealTypeServicePath = "deal-view-service.json";
        }

        api.getDealDetails(DealTypeServicePath, DealID, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject result, Response response) {
                utilityClass.processDialogStop();
                Log.d("Map Deal Details: ", result.toString());
                try {
                    JSONArray jsonArrayNew = new JSONArray(result.getAsJsonArray("nodes").toString());
                    try {
                        tvMapDealTitle.setText("");
                        tvMapDealDetails.setText("");
                        tvMapDealPrice.setText("");
                        tvMapDealColdHot.setText("");
                        tvMapDealComment.setText("");
                        tvMapDealTime.setText("");

                        jsonObject = jsonArrayNew.getJSONObject(0).getJSONObject("node");
                        Singleton.GetInstance().setJsonObject(jsonObject);

                        tvMapDealTitle.setText(jsonObject.getString("title"));
                        tvMapDealTitlePopup.setText(jsonObject.getString("title"));
                        tvMapDealDetails.setText(jsonObject.getString("body"));
                        tvMapDealDetailsPopup.setText(jsonObject.getString("body"));
                        tvMapDealColdHot.setText(jsonObject.getString("value") + "°");
                        tvMapDealDetailsHotColdValuePopupPopup.setText(jsonObject.getString("value") + "°");
                        tvMapDealComment.setText(jsonObject.getString("comment_count") + " Comments");
                        tvMapDealCommentPopup.setText(jsonObject.getString("comment_count") + " Comments");
                        tvMapDealTime.setText(jsonObject.getString("created"));
                        tvMapDealTimePopup.setText(jsonObject.getString("created"));
                        tvMapDealReviewsPopup.setText(jsonObject.getString("comment_count") + " Reviews");
                        if (DealsType.equalsIgnoreCase("Deals")) {
                            tvMapDealPrice.setText("$" + jsonObject.getString("field_price"));
                            tvMapDealPricePopup.setText("$" + jsonObject.getString("field_price"));
                        }
                        try {
                            JSONArray arrayImage = jsonObject.getJSONArray("field_images");
                            imageLoader.DisplayImage(arrayImage.getJSONObject(0).getString("src"), ivMapDeal);
                            imageLoader.DisplayImage(arrayImage.getJSONObject(0).getString("src"), ivMapDealsPopup);
                        } catch (JSONException e) {
                            Log.e("JSON Image ArrayError", e.toString());
                            if (e.toString().contains("field_images of type org.json.JSONObject cannot be converted to JSONArray")) {
                                try {
                                    JSONObject jsonObject1 = jsonObject.getJSONObject("field_images");
                                    imageLoader.DisplayImage(jsonObject1.getString("src"), ivMapDeal);
                                    imageLoader.DisplayImage(jsonObject1.getString("src"), ivMapDealsPopup);

                                } catch (JSONException e1) {
                                    Log.e("JSON Image ObjectError", e.toString());
                                }
                            }
                        }

                    } catch (JSONException e) {

                        e.printStackTrace();
                        Log.e("Error : ", e.toString());
                    }
                    LLMapDeal.setVisibility(View.VISIBLE);

                } catch (IllegalStateException e) {
                    Log.e("IllegalStateException:", e.toString());
                } catch (JSONException e) {
                    Log.e("JSONException:", e.toString());
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

    private void resetMenu() {
        LLMapDeal.setVisibility(View.GONE);
        LLMapDealPopup.setVisibility(View.GONE);
        ivMapAll.setImageResource(R.drawable.ic_allorange);
        ivMapDeals.setImageResource(R.drawable.ic_dealsorange);
        ivMapVouchers.setImageResource(R.drawable.ic_vouchersorange);
        ivMapFreebies.setImageResource(R.drawable.ic_freebiesorange);
        ivMapCompetition.setImageResource(R.drawable.ic_competitionorange);
        tvMapAll.setTextColor(getResources().getColor(R.color.orange));
        tvMapDeals.setTextColor(getResources().getColor(R.color.orange));
        tvMapVouchers.setTextColor(getResources().getColor(R.color.orange));
        tvMapFreebies.setTextColor(getResources().getColor(R.color.orange));
        tvMapCompetition.setTextColor(getResources().getColor(R.color.orange));
    }

    private void SelectCityDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.select_city_cell);
        final SelectCityAdapter adapter;

        etMapSearchCity = (EditText) dialog.findViewById(R.id.etSearchCity);
        ImageView ivGetCurrentCity = (ImageView) dialog.findViewById(R.id.ivGetCurrentCity);
        ListView lvSearchCity = (ListView) dialog.findViewById(R.id.lvSearchCity);
        adapter = new SelectCityAdapter(getActivity(), dialog, tvMapSearchCity, "Map", "City", Singleton.GetInstance().getSelectCityName());
        lvSearchCity.setAdapter(adapter);
        etMapSearchCity.setText(tvMapSearchCity.getText().toString());
        ivGetCurrentCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    location = gpsTracker.getLocation();
                    if (location != null) {
                        utilityClass.processDialogStart();
                        getLocationCityName(String.valueOf(gpsTracker.getLatitude()) + "," + String.valueOf(gpsTracker.getLongitude()));
                        /*locationAddress.getAddressFromLocation(gpsTracker.getLatitude(), gpsTracker.getLongitude(),
                                getApplicationContext(), new GeocoderHandler(etSearchCity));*/
                    } else {

                        utilityClass.toast("Location can not found!!!");
                    }
                } else {
                    utilityClass.showGPSDisabledAlertToUser();
                }
            }
        });
        etMapSearchCity.addTextChangedListener(new TextWatcher() {


            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                String text = etMapSearchCity.getText().toString().toLowerCase(Locale.getDefault());
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
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (!tvMapSearchCity.getText().toString().toString().equalsIgnoreCase(CityName)) {
                    CityName = tvMapSearchCity.getText().toString();
                    utilityClass.processDialogStart();
                    AllDealsRestServiceWithCity();
                }
            }
        });
        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        Singleton.GetInstance().MenuHide();
        Singleton.GetInstance().getTvMainHeading().setText("Local");
        Singleton.GetInstance().getIvMainSearch().setVisibility(View.GONE);
    }

    public void getLocationCityName(String latlng) {

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(ConstantClass.googleEndPoints)
                .build();
        api = restAdapter.create(RestServices.class);
        Log.d("Map Old Location City ", CityName);
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
                                if (types.getString(j).equals("locality")) {
                                    if (!component.getString("long_name").equalsIgnoreCase(CityName)) {
                                        CityName = component.getString("long_name");
                                        if (SaveSharedPreferences.getCityName(getActivity()).equalsIgnoreCase("all")) {
                                            tvMapSearchCity.setText(CityName);
                                            setLLAllDeals();
                                        } else {
                                            tvMapSearchCity.setText(CityName);
                                        }
                                        Log.d("Map Current City ", component.getString("long_name"));
                                    }
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
}
