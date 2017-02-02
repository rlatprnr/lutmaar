package com.wots.lutmaar.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.wots.lutmaar.Adapter.AllTypeDealsAdapter;
import com.wots.lutmaar.GetterSetter.DealsGetterSetter;
import com.wots.lutmaar.Interface.RestServices;
import com.wots.lutmaar.R;
import com.wots.lutmaar.UtilClass.ConstantClass;
import com.wots.lutmaar.UtilClass.SaveSharedPreferences;
import com.wots.lutmaar.UtilClass.UtilityClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class UserDetailsActivity extends ActionBarActivity {
    //Class

    UtilityClass utilityClass;
    ConstantClass constantClass;
    Bundle homeBundle = new Bundle();
    ArrayList<DealsGetterSetter> dealsGetterSetters = new ArrayList<DealsGetterSetter>();

    //Interface
    RestServices api;
    RestAdapter restAdapter;
    AllTypeDealsAdapter adapter;

    //View
    @InjectView(R.id.tvUserId)
    TextView tvUsetID;
    @InjectView(R.id.tvUserName)
    TextView tvUserName;
    @InjectView(R.id.tvEmail)
    TextView tvEmail;
    @InjectView(R.id.tv)
    TextView tv;
    @InjectView(R.id.toolbar_login)
    Toolbar toolbar;
    @InjectView(R.id.lvDeals1)
    ListView lvDeals;

    //Variable
    String userData;
    int l = 0;
    public static final String JSON_STRING = "http://lutmaar.com/deals-all-service.json/all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_details_activity);
        ButterKnife.inject(this);
        declaration();
        initialization();
    }

    private void declaration() {
        utilityClass = new UtilityClass(this);
        homeBundle = getIntent().getExtras();
        userData = homeBundle.getString("user");
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(" User Log In");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    private void initialization() {
        // restService123();
       // new GetData().execute();
        userData = homeBundle.getString("user");
        if (userData.equals("Already Log In")) {
            tvUserName.setText("User Name :" + userData);
        } else {
            try {
                JSONObject jsonObject = new JSONObject(userData);
                tvUsetID.setText("User ID: " + String.valueOf(jsonObject.getString("uid")));
                tvUserName.setText("User Name :" + String.valueOf(jsonObject.getString("name")));
                tvEmail.setText("Email : " + String.valueOf(jsonObject.getString("mail")));

            } catch (JSONException e) {
                Log.e("Error : ", e.toString());
                e.printStackTrace();
            }
        }
    }




    @OnClick(R.id.btnLogOut)
    public void setBtnLogOut() {
      /*  utilityClass.toast("User logout Success");
        Log.e("User logout Success :  ", String.valueOf("True"));
        SaveSharedPreferences.cleardata(UserDetailsActivity.this);
        Log.e("Tokon Null : ", String.valueOf(SaveSharedPreferences.getTokon(this)));
        Intent loginIntent = new Intent(UserDetailsActivity.this, LoginActivity.class);
        startActivity(loginIntent);*/
        utilityClass.processDialogStart();
        restService();
    }

    private void restService() {
       /* RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("User-Agent", "Your-App-Name");
                request.addHeader("Accept", "application/vnd.yourapi.v1.full+json");
            }
        };*/
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Content-Type", "application/json");
                request.addHeader("X-CSRF-Token", SaveSharedPreferences.getTokon(UserDetailsActivity.this));
                request.addHeader("Cookie", SaveSharedPreferences.getCookie(UserDetailsActivity.this));

               // request.addHeader("User-Agent", "Android");

                Log.i("Tokon : ", SaveSharedPreferences.getTokon(UserDetailsActivity.this));
                Log.i("Cookie : ", SaveSharedPreferences.getCookie(UserDetailsActivity.this));
            }
        };

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(constantClass.endPoints)
                .setRequestInterceptor(requestInterceptor)
                .build();
               // .setRequestInterceptor(requestInterceptor)

                /*setRequestInterceptor(new RequestInterceptor() {
            @Override
            public void intercept(RequestInterceptor.RequestFacade request) {
                request.addHeader("Content-Type", "application/json");
            }
        })*/
        api = restAdapter.create(RestServices.class);

        api.LogOut( new Callback<Object>() {
            @Override
            public void success(Object result, Response response) {
                utilityClass.processDialogStop();

                if (true) {
                    utilityClass.toast("User logout Success");
                    Log.i("User logout Success :  ", String.valueOf(result));
                    SaveSharedPreferences.cleardata(UserDetailsActivity.this);
                    Intent loginIntent = new Intent(UserDetailsActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                } else {
                    Log.i("User logout Response :", String.valueOf(result));
                }

            }

            @Override
            public void failure(RetrofitError error) {
                utilityClass.processDialogStop();
                Log.i("Restrofit Error  : ", String.valueOf(error.toString()));
                utilityClass.processDialogStop();
                if (error.toString().contains("No address associated with hostname")) {
                    utilityClass.toast("Please check your network connection or try again later.");
                } else if (error.toString().equals("retrofit.RetrofitError: 500 Internal Server Error")) {
                    utilityClass.toast("Internal Server Error");
                } else if (error.toString().contains("com.google.gson.JsonSyntaxException")) {
                    utilityClass.toast("Result not found form server");
                } else if (error.toString().contains("User is not logged in")) {
                    utilityClass.toast("User is not logged in");
                } else {
                    utilityClass.toast("User can not logout");
                }
            }
        });
    }

    public class SessionRequestInterceptor implements RequestInterceptor {
        @Override
        public void intercept(RequestFacade request) {
            request.addHeader("Content-Type", "application/json");
            request.addHeader("X-CSRF-Token", SaveSharedPreferences.getTokon(UserDetailsActivity.this));
            request.addHeader("User-Agent", "Custom-Agent 1.0");

            Log.i("Tokon : ", SaveSharedPreferences.getTokon(UserDetailsActivity.this));
        }
    }

}
