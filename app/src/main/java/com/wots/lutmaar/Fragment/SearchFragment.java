package com.wots.lutmaar.Fragment;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
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
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wots.lutmaar.Adapter.SelectCityAdapter;
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

public class SearchFragment extends Fragment implements View.OnClickListener {
    //Class
    UtilityClass utilityClass;
    JSONArray jsonArray;
    private LocationManager locationManager;
    GPSTracker gpsTracker;
    Location location;
    private ShowcaseView showcaseView;


    //View
    @InjectView(R.id.rgDealTypeSearch)
    RadioGroup rgDealTypeSearch;
    @InjectView(R.id.rgSortType)
    RadioGroup rgSortType;
    @InjectView(R.id.tvSwitchOnOffSearch)
    TextView tvSwitchOnOffSearch;
    @InjectView(R.id.etSearchTitle)
    TextView etSearchTitle;
    @InjectView(R.id.tvCategorySearch)
    TextView tvCategorySearch;
    @InjectView(R.id.tvCitySearch)
    TextView tvCitySearch;

    //Interface
    RestServices api;
    RestAdapter restAdapter;


    //Variable
    boolean onLine = true;
    int OnOff = 1;
    public static String DealType = "deals", DealObject, CategotyID;
    int ShowCasecounter = 0, NetworkErrorCount = 0, SortID = 0;
    ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.search_fragment, container, false);
        ButterKnife.inject(this, view);
        //  utilityClass = new UtilityClass(getActivity());
        declaration();

        initialization();

        return view;
    }

    private void declaration() {
        utilityClass = new UtilityClass(getActivity());
        if (SaveSharedPreferences.getWCSearch(getActivity()).length() == 0) {
            SaveSharedPreferences.setWCSearch(getActivity(), "Already Done ");
            showcaseView = new ShowcaseView.Builder(getActivity(), true)
                    .setTarget(new ViewTarget(tvSwitchOnOffSearch))
                    .setStyle(R.style.CustomShowcaseTheme)
                    .setOnClickListener(this)
                    .build();
            showcaseView.setButtonText("Next");
            showcaseView.setContentTitle(" Search online or offline");
            //showcaseView.setContentText("Type of deal");
        }
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        gpsTracker = new GPSTracker(getActivity());

        if (Singleton.GetInstance().getHMCategory().size() <= 0) {
            if (utilityClass.isInternetConnection()) {
                getCategoryRestService();
            } else {
                utilityClass.getDefaultCategory();
            }
        }

     /*   DealsCategotyAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, utilityClass.getDealsCategory());
        DealsCategotyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategorySearch.setAdapter(DealsCategotyAdapter);

        //  FilterSpinnerAdapter cityAdapter = new FilterSpinnerAdapter(getActivity(),android.R.layout.simple_spinner_item, Singleton.GetInstance().getSelectCityName());

        //  cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // spinnerRegion.setAdapter(regionadapter);
        DealCityAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, Singleton.GetInstance().getSelectCityName());
        DealCityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCitySearch.setAdapter(DealCityAdapter);*/

        rgDealTypeSearch.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbtnDeal:
                        DealType = "deals";

                        if (Singleton.GetInstance().getHMCategory().size() <= 0) {
                            tvCategorySearch.setText("");
                            tvCategorySearch.setHint("Select Category...");
                            getCategoryRestService();
                        }

                        break;

                    case R.id.rBtnVouchers:
                        DealType = "vouchers";
                        if (Singleton.GetInstance().getHMCategory().size() <= 0) {
                            tvCategorySearch.setText("");
                            tvCategorySearch.setHint("Select Category...");
                            getCategoryRestService();
                        }
                        break;

                    case R.id.rBtnFreebies:
                        DealType = "freebies";
                        if (Singleton.GetInstance().getHMCategory().size() <= 0) {
                            tvCategorySearch.setText("");
                            tvCategorySearch.setHint("Select Category...");
                            getCategoryRestService();
                        }
                        break;
                    case R.id.rBtnAsk:
                        DealType = "ask";
                        if (Singleton.GetInstance().getHMCategory().size() <= 0) {
                            tvCategorySearch.setText("");
                            tvCategorySearch.setHint("Select Category...");
                            getCategoryRestService();
                        }
                        break;
                    case R.id.rBtnCompetition:
                        DealType = "competitions";
                        if (Singleton.GetInstance().getHMCategory().size() <= 0) {
                            tvCategorySearch.setText("");
                            tvCategorySearch.setHint("Select Category...");
                            getCategoryRestService();
                        }
                        break;
                }
            }
        });
        rgSortType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbtnSortRelevanceSearch:
                        SortID = 0;
                        break;
                   /* case R.id.rBtnSortDistanceSearch:
                        SortID = 3;
                        break;*/

                    case R.id.rBtnSortRatingSearch:
                        SortID = 2;
                        break;
                    case R.id.rBtnSortDealTimeSearch:
                        SortID = 1;
                        break;
                }
            }
        });

    }

    private void initialization() {

    }

    @OnClick(R.id.tvSwitchOnOffSearch)
    public void setTvSwitchOnOffSearch() {
        if (onLine) {
            tvSwitchOnOffSearch.setBackground(getResources().getDrawable(R.drawable.ios_off));
            tvSwitchOnOffSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.switch_on_off_small, 0);
            onLine = false;
            OnOff = 0;

        } else {
            tvSwitchOnOffSearch.setBackground(getResources().getDrawable(R.drawable.ios_on));
            tvSwitchOnOffSearch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.switch_on_off_small, 0, 0, 0);
            onLine = true;
            OnOff = 1;

        }

    }

    @OnClick(R.id.tvCitySearch)
    public void setTvCitySearch() {
        SelectCityDialog();
    }

    @OnClick(R.id.tvCategorySearch)
    public void setTvCategorySearch() {
        SelectCategoryDialog();
    }

    @OnClick(R.id.tvDoneSearch)
    public void setTvDoneSearch() {
        if (utilityClass.isInternetConnection()) {
            if (checkValidation()) {
                utilityClass.processDialogStart();
                restService();
            }
        } else {
            utilityClass.toast("Please check your internet settings!!!");
        }
    }

    @OnClick(R.id.tvCleanAllSearch)
    public void setTvCleanAllSearch() {
        clearData();
    }

    @Override
    public void onResume() {
        super.onResume();
        Singleton.GetInstance().MenuHide();
        Singleton.GetInstance().getTvMainHeading().setText("Search");
        Singleton.GetInstance().getIvMainSearch().setVisibility(View.GONE);
    }

    public void clearData() {
        etSearchTitle.setText("");
        tvCategorySearch.setText("");
        tvCitySearch.setText("");
        tvCategorySearch.setHint("Select Category...");
        tvCitySearch.setHint("Select City...");
        rgDealTypeSearch.check(0);
        rgSortType.check(0);
        NetworkErrorCount = 0;
    }

    public void getCategoryRestService() {
        utilityClass.processDialogStart();
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
        api.getCategory(new Callback<JsonObject>() {
            @Override
            public void success(JsonObject result, Response response) {
                utilityClass.processDialogStop();
                ArrayList<HashMap<String, String>> LVHMDealCategory = new ArrayList<HashMap<String, String>>();
                HashMap<String, String> HMDealCategory = new HashMap<String, String>();
                ArrayList<String> CatList = new ArrayList<String>();

                try {

                    JSONArray jsonArrayNew = new JSONArray(result.getAsJsonArray("nodes").toString());
                    for (int i = 0; i < jsonArrayNew.length(); i++) {
                        HMDealCategory = new HashMap<String, String>();
                        CatList.add(jsonArrayNew.getJSONObject(i).getJSONObject("node").getString("name"));
                        HMDealCategory.put("name", jsonArrayNew.getJSONObject(i).getJSONObject("node").getString("name"));
                        HMDealCategory.put("ID", jsonArrayNew.getJSONObject(i).getJSONObject("node").getString("term_id"));
                        LVHMDealCategory.add(HMDealCategory);
                    }
                    Singleton.GetInstance().setCategoryList(CatList);
                    Singleton.GetInstance().setHMCategory(LVHMDealCategory);
                } catch (IllegalStateException e) {
                    Log.e("IllegalStateException:", e.toString());
                } catch (JSONException e) {
                    Log.e("JSONException:", e.toString());
                }
            }

            @Override
            public void failure(RetrofitError error) {
                utilityClass.processDialogStop();
                utilityClass.getDealsCategory();
                Log.i("Retrofit Error  : ", String.valueOf(error.toString()));
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
                    utilityClass.toast("Something went wrong");
                }
            }
        });
    }

    private void restService() {
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
        HashMap<String, String> HMSearchDeal = new HashMap<String, String>();

        HMSearchDeal.put("category", DealType);
        HMSearchDeal.put("title", etSearchTitle.getText().toString());
        HMSearchDeal.put("topic", tvCategorySearch.getText().toString());
        HMSearchDeal.put("city", tvCitySearch.getText().toString());
        HMSearchDeal.put("online", String.valueOf(OnOff));
        HMSearchDeal.put("sorting", String.valueOf(SortID));
        Log.i("Request image Data:", HMSearchDeal.toString());
        Singleton.GetInstance().setSearchResultJsonQuery(new Gson().toJson(HMSearchDeal).toString());
        api.getSearch(HMSearchDeal, new Callback<JsonArray>() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void success(JsonArray result, Response response) {
                utilityClass.processDialogStop();
                Log.d("My Points Result : ", String.valueOf(result));
                utilityClass.toast("Data found");
                Singleton.GetInstance().FragmentCall(19, String.valueOf(result));
                Singleton.GetInstance().MenuHide();
                Singleton.GetInstance().getIvMainSearch().setVisibility(View.GONE);

            }

            @Override
            public void failure(RetrofitError error) {

                Log.i("Retrofit Error  : ", String.valueOf(error.toString() + " Network " + error.isNetworkError()));
                if (error.isNetworkError()) {
                    if (NetworkErrorCount >= 3) {
                        utilityClass.toast("Network error, please try again!!!.");
                        utilityClass.processDialogStop();
                        NetworkErrorCount = 0;
                    } else {
                        NetworkErrorCount++;
                        restService();
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
                    } else if (error.toString().contains("404 Not Found")) {
                        utilityClass.toast("Result not found!!!");
                    } else {
                        utilityClass.toast("Something went wrong!!!");
                    }
                }
            }
        });
    }

    private boolean checkValidation() {
        boolean ret = true;


        if (tvCitySearch.getText().toString().equalsIgnoreCase("Select City...") || tvCitySearch.getText().toString().equalsIgnoreCase("")) {
            utilityClass.toast("Please select City");
            ret = false;
        }

        return ret;

    }

    private void SelectCityDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.select_city_cell);
        final SelectCityAdapter adapter;
        final EditText etSearchCity = (EditText) dialog.findViewById(R.id.etSearchCity);
        ImageView ivGetCurrentCity = (ImageView) dialog.findViewById(R.id.ivGetCurrentCity);
        ListView lvSearchCity = (ListView) dialog.findViewById(R.id.lvSearchCity);
        adapter = new SelectCityAdapter(getActivity(), dialog, tvCitySearch, "Search", "City", Singleton.GetInstance().getSelectCityName());
        lvSearchCity.setAdapter(adapter);
        ivGetCurrentCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    location = gpsTracker.getLocation();
                    if (location != null) {
                        utilityClass.processDialogStart();
                        getLocationCityName(etSearchCity, String.valueOf(gpsTracker.getLatitude()) + "," + String.valueOf(gpsTracker.getLongitude()));
                      /*  locationAddress.getAddressFromLocation(gpsTracker.getLatitude(), gpsTracker.getLongitude(),
                                getActivity().getApplicationContext(), new GeocoderHandler(etSearchCity));*/
                    } else {
                        utilityClass.toast("Location can not found!!!");
                    }
                } else {
                    utilityClass.showGPSDisabledAlertToUser();
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

    private void SelectCategoryDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.select_city_cell);
        final SelectCityAdapter adapter;
        final TextView etSearchCity = (TextView) dialog.findViewById(R.id.etSearchCity);
        TextView tvSelectCityLabel = (TextView) dialog.findViewById(R.id.tvSelectCityLabel);
        ImageView ivGetCurrentCity = (ImageView) dialog.findViewById(R.id.ivGetCurrentCity);
        ListView lvSearchCity = (ListView) dialog.findViewById(R.id.lvSearchCity);
        try {
            if (Singleton.GetInstance().getCategoryList().size() == 0) {
                utilityClass.toast("Category List Empty");
                dialog.dismiss();

            } else {
                adapter = new SelectCityAdapter(getActivity(), dialog, tvCategorySearch, "Search", "Category", Singleton.GetInstance().getCategoryList());
                lvSearchCity.setAdapter(adapter);
                etSearchCity.setHint("Category name");
                tvSelectCityLabel.setText("Select Category");
                ivGetCurrentCity.setVisibility(View.GONE);

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
        } catch (NullPointerException e) {
            Log.e("Category Null:", e.toString());
            utilityClass.toast("Category List Empty");
            dialog.dismiss();
        }
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
                                    //   return component.getString("long_name");

                                    SaveSharedPreferences.setCityName(getActivity(), component.getString("long_name"));
                                    cityName.setText(component.getString("long_name"));
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
    public void onClick(View v) {

        switch (ShowCasecounter) {
            case 0:
                showcaseView.setShowcase(new ViewTarget(rgSortType), true);
                showcaseView.setContentTitle("Short the search result with the simple tap");
                showcaseView.setButtonText("Finish");
                ShowCasecounter++;
                break;
            case 1:
                showcaseView.setVisibility(View.GONE);
                showcaseView.hide();
                break;
        }
    }

}
