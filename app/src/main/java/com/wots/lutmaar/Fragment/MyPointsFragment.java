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
import android.widget.ListView;

import com.google.gson.JsonArray;
import com.wots.lutmaar.Adapter.MyPointsAdapter;
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
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MyPointsFragment extends Fragment {

    //Class
    UtilityClass utilityClass;
    ConstantClass constantClass;
    MyPointsAdapter adapter;
    JSONArray jsonArray;
    private MySQLiteHelper mydb;

    //Interface
    RestServices api;
    RestAdapter restAdapter;

    //View
    @InjectView(R.id.lvMyPoints)
    ListView lvMyPoints;

    //Variable
    private int NetworkErrorCount = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.my_points_fragment, container, false);
        ButterKnife.inject(this, view);
        //  utilityClass = new UtilityClass(getActivity());
        declaration();

        initialization();

        return view;
    }

    private void declaration() {
        utilityClass = new UtilityClass(getActivity());
        mydb = new MySQLiteHelper(getActivity());
    }

    private void initialization() {

        if (utilityClass.isInternetConnection()) {
            utilityClass.processDialogStart();
            NetworkErrorCount = 0;
            restService();
        } else {
            fillDBData();
            // networkLossDialogShow();
        }
    }
    public void fillDBData() {
        try {
            Cursor rs = mydb.getAllData("MyPoints", mydb.TABLE_LUTMAAR);
            jsonArray = new JSONArray();
            rs.moveToFirst();
            if (rs.getCount() > 0) {
                do {
                    // jsonArray.put(jsonArrayNew.getJSONObject(i));
                    try {
                        jsonArray.put(new JSONObject(rs.getString(rs.getColumnIndex(MySQLiteHelper.JSONOBJECT))));
                    } catch (JSONException e) {
                        Log.e("DB getData Json:", e.toString());
                    }
                    // get  the  data into array,or class variable
                } while (rs.moveToNext());
                adapter = new MyPointsAdapter(getActivity(), jsonArray);
                lvMyPoints.setAdapter(adapter);
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

                Log.i("Tokon : ", SaveSharedPreferences.getTokon(getActivity()));
                Log.i("Cookie : ", SaveSharedPreferences.getCookie(getActivity()));
            }
        };

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(constantClass.endPoints)
                .setRequestInterceptor(requestInterceptor)
                .build();

        api = restAdapter.create(RestServices.class);
        HashMap<String, String> HMUserID = new HashMap<String, String>();
        HMUserID.put("uid", SaveSharedPreferences.getUserID(getActivity()));
        Log.i("Request image Data:", HMUserID.toString());
        api.getMyPoints(HMUserID, new Callback<JsonArray>() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void success(JsonArray result, Response response) {
                utilityClass.processDialogStop();
                Log.d("My Points Result : ", String.valueOf(result));

                try {
                    jsonArray = new JSONArray(result.getAsJsonArray().toString());
                    if (jsonArray.length() != 0) {
                        mydb.deleteDealTypeData("MyPoints", mydb.TABLE_LUTMAAR);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            mydb.insertJSONData("MyPoints", jsonArray.getJSONObject(i));
                        }
                    }
                    adapter = new MyPointsAdapter(getActivity(), jsonArray);
                    lvMyPoints.setAdapter(adapter);

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
                        restService();
                    }
                }else {
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
                        utilityClass.toast("Some thing went wrong!!!");
                    }
                }

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Singleton.GetInstance().BottomMenuHide();
        Singleton.GetInstance().getTvMainHeading().setText("My Points");
    }
}
