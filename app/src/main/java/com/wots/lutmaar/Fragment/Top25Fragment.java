package com.wots.lutmaar.Fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.JsonObject;
import com.wots.lutmaar.Adapter.DealsAdapter;
import com.wots.lutmaar.Interface.RestServices;
import com.wots.lutmaar.R;
import com.wots.lutmaar.Interface.AccelerometerListener;
import com.wots.lutmaar.UtilClass.AccelerometerManager;
import com.wots.lutmaar.UtilClass.ConstantClass;
import com.wots.lutmaar.UtilClass.SaveSharedPreferences;
import com.wots.lutmaar.UtilClass.Singleton;
import com.wots.lutmaar.UtilClass.UtilityClass;
import com.wots.lutmaar.UtilClass.db.MySQLiteHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class Top25Fragment extends Fragment implements AccelerometerListener {

    //Class
    UtilityClass utilityClass;
    ConstantClass constantClass;
    DealsAdapter adapter;
    JSONArray jsonArray;
    private MySQLiteHelper mydb;

    //Interface
    RestServices api;
    RestAdapter restAdapter;
    //View
    @InjectView(R.id.lvDeals)
    ListView lvDeals;
    @InjectView(R.id.pbDeals)
    SmoothProgressBar pbDeals;
    @InjectView(R.id.pbRoundDeals)
    CircularProgressBar pbRoundDeals;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.top25_fragment, container, false);
        ButterKnife.inject(this, view);
        declaration();
        initialization();
        return view;
    }

    private void declaration() {
        utilityClass = new UtilityClass(getActivity());
        mydb = new MySQLiteHelper(getActivity());
        pbDeals.progressiveStart();
        pbRoundDeals.setVisibility(View.GONE);
    }

    private void initialization() {

        if (utilityClass.isInternetConnection()) {

            pbDeals.setVisibility(View.VISIBLE);
            restService();
        } else {
            pbDeals.setVisibility(View.GONE);
            fillDBData();
            // networkLossDialogShow();
        }

    }

    public void fillDBData() {
        try {
            Cursor rs = mydb.getAllData("Top25", mydb.TABLE_LUTMAAR);
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
                adapter = new DealsAdapter(getActivity(), "Top25", jsonArray);
                lvDeals.setAdapter(adapter);
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
        Log.d("Top25 CityName : ", SaveSharedPreferences.getCityName(getActivity()));
        api.getTop25(SaveSharedPreferences.getCityName(getActivity()), new Callback<JsonObject>() {
            @Override
            public void success(JsonObject result, Response response) {
                pbDeals.setVisibility(View.GONE);
                Singleton.GetInstance().setStartShake(true);
                try {
                    jsonArray = new JSONArray(result.getAsJsonArray("nodes").toString());
                    if (jsonArray.length() != 0) {
                        mydb.deleteDealTypeData("Top25", mydb.TABLE_LUTMAAR);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            mydb.insertJSONData("Top25", jsonArray.getJSONObject(i));
                        }
                    }
                    adapter = new DealsAdapter(getActivity(), "Top25", jsonArray);
                    lvDeals.setAdapter(adapter);

                } catch (IllegalStateException e) {
                    Log.e("IllegalStateException:", e.toString());
                } catch (JSONException e) {
                    Log.e("JSONException:", e.toString());
                }


            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Restrofit Error  : ", String.valueOf(error.toString()));
                pbDeals.setVisibility(View.GONE);
                Singleton.GetInstance().setStartShake(true);
                pbDeals.setVisibility(View.GONE);
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
    public void onResume() {
        super.onResume();
        //Check device supported Accelerometer senssor or not
        Singleton.GetInstance().getTvMainHeading().setText("Top 25");
        //  Singleton.GetInstance().getLLCitySearch().setVisibility(View.VISIBLE);
        if (AccelerometerManager.isSupported(getActivity())) {
            //Start Accelerometer Listening
            AccelerometerManager.startListening(this);
        }

    }


    @Override
    public void onPause() {
        super.onPause();
        if (AccelerometerManager.isListening()) {
            //Start Accelerometer Listening
            AccelerometerManager.stopListening();
        }


    }

    @Override
    public void onAccelerationChanged(float x, float y, float z) {

    }

    @Override
    public void onShake() {

        Singleton.GetInstance().setStartShake(false);
        utilityClass.toast("Shake Top25");
        Log.d("Make Shake :", "Top25");
        initialization();
    }

    private void networkLossDialogShow() {
        final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.network_loss);
        //dialog.setCancelable(false);
        Singleton.GetInstance().setDialog(dialog);

        Button btnTryAgain = (Button) dialog.findViewById(R.id.btnTryAgain);

        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initialization();
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
            }
        });
    }

}
