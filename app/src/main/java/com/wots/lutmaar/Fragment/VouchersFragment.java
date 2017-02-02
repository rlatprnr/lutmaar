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
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.wots.lutmaar.Adapter.AllTypeDealsAdapter;
import com.wots.lutmaar.Interface.AccelerometerListener;
import com.wots.lutmaar.Interface.RestServices;
import com.wots.lutmaar.R;
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
import fr.castorflex.android.circularprogressbar.CircularProgressDrawable;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class VouchersFragment extends Fragment implements AccelerometerListener {
    //Class
    UtilityClass utilityClass;
    ConstantClass constantClass;
    AllTypeDealsAdapter adapter;
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
    @InjectView(R.id.tvDataNotFound)
    TextView tvDataNotFound;

    //Variable
    int pageNo = 0;
    int pageEnd = 0;
    boolean pageScroll = true;
    String catID = "all";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.deals_fragment, container, false);
        ButterKnife.inject(this, view);
        //  utilityClass = new UtilityClass(getActivity());
        declaration();

        return view;
    }

    private void declaration() {
        utilityClass = new UtilityClass(getActivity());
        mydb = new MySQLiteHelper(getActivity());
        pbDeals.progressiveStart();
        ((CircularProgressDrawable) pbRoundDeals.getIndeterminateDrawable()).start();
        jsonArray = new JSONArray();
        adapter = new AllTypeDealsAdapter(getActivity(), "Vouchers", jsonArray);
        lvDeals.setAdapter(adapter);

        lvDeals.setOnScrollListener(new AbsListView.OnScrollListener() {
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


    private void initialization() {
        if (utilityClass.isInternetConnection()) {
            if (pageNo == 0) {
                pbDeals.setVisibility(View.VISIBLE);
            } else {
                pbRoundDeals.setVisibility(View.VISIBLE);
            }

            restService();
        } else {
            pbDeals.setVisibility(View.GONE);
            pbRoundDeals.setVisibility(View.GONE);
            fillDBData();
           // networkLossDialogShow();
        }

    }
    public void fillDBData() {
        try{
            Cursor rs = mydb.getAllData("Vouchers",mydb.TABLE_LUTMAAR);
            rs.moveToFirst();
            if(rs.getCount()>0) {
                if(jsonArray.length()!=0) {
                    jsonArray = null;
                    jsonArray = new JSONArray();
                    adapter = new AllTypeDealsAdapter(getActivity(), "Vouchers", jsonArray);
                    lvDeals.setAdapter(adapter);
                }
                do {
                    // jsonArray.put(jsonArrayNew.getJSONObject(i));
                    try {
                        jsonArray.put(new JSONObject(rs.getString(rs.getColumnIndex(MySQLiteHelper.JSONOBJECT))));
                    }catch (JSONException e){
                        Log.e("DB getData Json:", e.toString());
                    }
                    // get  the  data into array,or class variable
                } while (rs.moveToNext());
                adapter.notifyDataSetChanged();
            }
        }catch (NullPointerException e){
            Log.e("Null Pointer Error:",e.toString());
        }
        pageEnd = 1;

    }
    private void restService() {

        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Content-Type", "application/json");
                request.addHeader("X-CSRF-Token", SaveSharedPreferences.getTokon(getActivity()));
                request.addHeader("Cookie", SaveSharedPreferences.getCookie(getActivity()));

                Log.i("Vouchers Tokon : ", SaveSharedPreferences.getTokon(getActivity()));
                Log.i("Vouchers Cookie : ", SaveSharedPreferences.getCookie(getActivity()));
            }
        };

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(constantClass.endPoints)
                .setRequestInterceptor(requestInterceptor)
                .build();
        api = restAdapter.create(RestServices.class);
        Log.d("Page No Load : ", String.valueOf(pageNo));
        Log.d("Deal Type + CityName : ", "Vouchers in " + SaveSharedPreferences.getCityName(getActivity()));
        if (Singleton.GetInstance().getDealType().equalsIgnoreCase("vouchers") &&
                !Singleton.GetInstance().getDealSubCategory().equalsIgnoreCase("all")) {
            //  int index = Singleton.GetInstance().getAllHMCategory().get(Singleton.GetInstance().getDealType()).indexOf(Singleton.GetInstance().getTvDealCategoryName().getText().toString());
            int index = Singleton.GetInstance().getCategoryList().indexOf(Singleton.GetInstance().getTvDealCategoryName().getSelectedItem().toString());
            // catID = Singleton.GetInstance().getAllHMCategory().get("all").get(index).get("ID");
            catID = Singleton.GetInstance().getHMCategory().get(index).get("ID");
        } else {
            catID = "all";
        }
        Log.d("Cate ID : ", catID);
        api.getAllVouchers(SaveSharedPreferences.getCityName(getActivity()), catID, String.valueOf(pageNo), new Callback<JsonObject>() {
            @Override
            public void success(JsonObject result, Response response) {
                pbDeals.setVisibility(View.GONE);
                pbRoundDeals.setVisibility(View.GONE);
                Singleton.GetInstance().setStartShake(true);
                try {
                    JSONArray jsonArrayNew = new JSONArray(result.getAsJsonArray("nodes").toString());
                    if (jsonArrayNew.length() != 0) {
                        if (pageNo == 0) {
                            mydb.deleteDealTypeData("Vouchers", mydb.TABLE_LUTMAAR);
                        }
                        if (jsonArrayNew.length() < 10) {
                            pageEnd = 1;
                        }
                        for (int i = 0; i < jsonArrayNew.length(); i++) {
                            mydb.insertJSONData("Vouchers", jsonArrayNew.getJSONObject(i));
                            jsonArray.put(jsonArrayNew.getJSONObject(i));
                        }

                        adapter.notifyDataSetChanged();
                        pageNo++;
                        pageScroll = true;
                    } else if (pageNo == 0) {
                        pageEnd = 1;
                        tvDataNotFound.setVisibility(View.VISIBLE);
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
                Log.d("Retrofit Error  : ", String.valueOf(error.toString()));
                pbDeals.progressiveStop();
                pbDeals.setVisibility(View.GONE);
                pbRoundDeals.setVisibility(View.GONE);
                Singleton.GetInstance().setStartShake(true);
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
        // Singleton.GetInstance().getTvMainHeading().setText("Latest Vouchers");
        // Singleton.GetInstance().getLLCitySearch().setVisibility(View.VISIBLE);

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
        if (pageScroll) {
            Singleton.GetInstance().setStartShake(false);
            utilityClass.toast("Shake Vouchers");
            Log.d("Make Shake :", " Vouchers");
            pageNo = 0;
            pageEnd = 0;
            pageScroll = false;
            jsonArray = null;
            jsonArray = new JSONArray();
            adapter = new AllTypeDealsAdapter(getActivity(), "Vouchers", jsonArray);
            lvDeals.setAdapter(adapter);
            initialization();
        }
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
                pageScroll = true;
            }
        });
    }


}
