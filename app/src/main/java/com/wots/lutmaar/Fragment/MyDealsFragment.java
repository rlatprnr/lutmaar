package com.wots.lutmaar.Fragment;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wots.lutmaar.Adapter.MyDealsAdapter;
import com.wots.lutmaar.Interface.RestServices;
import com.wots.lutmaar.R;
import com.wots.lutmaar.UtilClass.ConstantClass;
import com.wots.lutmaar.UtilClass.SaveSharedPreferences;
import com.wots.lutmaar.UtilClass.Singleton;
import com.wots.lutmaar.UtilClass.UtilityClass;
import com.wots.lutmaar.UtilClass.db.MySQLiteHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import fr.castorflex.android.circularprogressbar.CircularProgressDrawable;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MyDealsFragment extends Fragment {

    //Class
    UtilityClass utilityClass;
    ConstantClass constantClass;
    MyDealsAdapter adapter;
    JSONArray jsonArray;
    JSONObject jsonObjectOfflineDeals;
    Cursor curDealRS;
    private MySQLiteHelper mydb;

    //Interface
    RestServices api;
    RestAdapter restAdapter;

    //View
    @InjectView(R.id.lvMyDeals)
    ListView lvMyDeals;
    @InjectView(R.id.pbRoundUserDeals)
    CircularProgressBar pbRoundUserDeals;

    //Variable
    private int NetworkErrorCount = 0;
    int pageNo = 0, pageEnd = 0, TotalDeals = 0;
    boolean pageScroll = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.my_deals_fragment, container, false);
        ButterKnife.inject(this, view);
        declaration();
        return view;
    }

    private void declaration() {
        utilityClass = new UtilityClass(getActivity());
        mydb = new MySQLiteHelper(getActivity());
        ((CircularProgressDrawable) pbRoundUserDeals.getIndeterminateDrawable()).start();
        jsonArray = new JSONArray();
        adapter = new MyDealsAdapter(getActivity(), jsonArray);
        lvMyDeals.setAdapter(adapter);
        lvMyDeals.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                final int lastItem = firstVisibleItem + visibleItemCount;
                if (lastItem == totalItemCount && pageScroll) {
                    pageScroll = false;
                    if (pageEnd == 0)
                        initialization();
                }
            }
        });
    }

    public void setDraftData() {
        do {
            try {
                jsonObjectOfflineDeals = new JSONObject(curDealRS.getString(curDealRS.getColumnIndex(MySQLiteHelper.JSONOBJECT)));
                HashMap<String, String> dealData = new HashMap<String, String>();
                HashMap<String, Object> dealOfflineData = new HashMap<String, Object>();
                String date = jsonObjectOfflineDeals.getJSONObject("field_start_date").getJSONArray("und")
                        .getJSONObject(0).getJSONObject("value").optString("date");
                String DealCat = jsonObjectOfflineDeals.getJSONObject("field_category").optString("und");
                String DealCatType = "";

                if (DealCat.equalsIgnoreCase("64")) {
                    DealCatType = "Deals";
                } else if (DealCat.equalsIgnoreCase("65")) {
                    DealCatType = "Competitions";
                } else if (DealCat.equalsIgnoreCase("66")) {
                    DealCatType = "Ask";
                } else if (DealCat.equalsIgnoreCase("67")) {
                    DealCatType = "Vouchers";
                } else if (DealCat.equalsIgnoreCase("68")) {
                    DealCatType = "Freebies";
                }


                dealData.put("Title", jsonObjectOfflineDeals.optString("title"));
                dealData.put("Post date", date);
                dealData.put("Hot or Cold", "0");
                dealData.put("nid", "-1");
                dealData.put("field_cat", DealCatType);
                dealOfflineData.put("node", dealData);

                jsonArray.put(new JSONObject(new Gson().toJson(dealOfflineData).toString()));

            } catch (JSONException e) {
                Log.e("Deal details DB:", e.toString());
            }
            // get  the  data into array,or class variable
        } while (curDealRS.moveToNext());
        fillDBData();
    }


    private void initialization() {

        if (utilityClass.isInternetConnection()) {
            if (pageNo == 0) {
                utilityClass.processDialogStart();
            } else {
                pbRoundUserDeals.setVisibility(View.VISIBLE);
            }
            NetworkErrorCount = 0;
            restService();
        } else {
            utilityClass.processDialogStop();
            pbRoundUserDeals.setVisibility(View.GONE);
            pageEnd = 1;
            if (jsonArray.length() != 0) {
                jsonArray = null;
                jsonArray = new JSONArray();
                adapter = new MyDealsAdapter(getActivity(), jsonArray);
                lvMyDeals.setAdapter(adapter);
            }
            curDealRS = mydb.getAllOfflinePostDealData();
            curDealRS.moveToFirst();
            if (curDealRS.getCount() > 0) {
                setDraftData();
            } else {
                fillDBData();
            }

        }
    }

    public void fillDBData() {

        try {
            Cursor rs = mydb.getAllData("MyDeals", mydb.TABLE_LUTMAAR);
            rs.moveToFirst();
            if (rs.getCount() > 0) {

                do {
                    try {
                        jsonArray.put(new JSONObject(rs.getString(rs.getColumnIndex(MySQLiteHelper.JSONOBJECT))));
                    } catch (JSONException e) {
                        Log.e("DB getData Json:", e.toString());
                    }
                    // get  the  data into array,or class variable
                } while (rs.moveToNext());
                adapter.notifyDataSetChanged();
            }
        } catch (NullPointerException e) {
            Log.e("Null Pointer Error:", e.toString());
        }
    }

    private void restService() {

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
        Log.d("Page No Load : ", String.valueOf(pageNo));
        Log.d("User ID : ", SaveSharedPreferences.getUserID(getActivity()));
        api.getMyDeals(SaveSharedPreferences.getUserID(getActivity()), String.valueOf(pageNo), new Callback<JsonObject>() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void success(JsonObject result, Response response) {
                utilityClass.processDialogStop();
                pbRoundUserDeals.setVisibility(View.GONE);
                Log.d("My Deals Result : ", String.valueOf(result));
                NetworkErrorCount = 0;
                try {
                    JSONArray jsonArrayNew = new JSONArray(result.getAsJsonArray("nodes").toString());
                    if (jsonArrayNew.length() != 0) {
                        if (pageNo == 0) {
                            mydb.deleteDealTypeData("MyDeals", mydb.TABLE_LUTMAAR);
                        }
                        if (jsonArrayNew.length() < 20) {
                            pageEnd = 1;
                        }
                        for (int i = 0; i < jsonArrayNew.length(); i++) {
                            mydb.insertJSONData("MyDeals", jsonArrayNew.getJSONObject(i));
                            jsonArray.put(jsonArrayNew.getJSONObject(i));
                        }
                        adapter.notifyDataSetChanged();
                        pageNo++;
                        pageScroll = true;
                    } else {
                        pageEnd = 1;
                    }

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
                        pbRoundUserDeals.setVisibility(View.GONE);
                        NetworkErrorCount = 0;
                    } else {
                        NetworkErrorCount++;
                        restService();
                    }
                } else {
                    utilityClass.processDialogStop();
                    pbRoundUserDeals.setVisibility(View.GONE);
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

    @Override
    public void onResume() {
        super.onResume();
        Singleton.GetInstance().BottomMenuHide();
        Singleton.GetInstance().getTvMainHeading().setText("My Deals");
    }
}
