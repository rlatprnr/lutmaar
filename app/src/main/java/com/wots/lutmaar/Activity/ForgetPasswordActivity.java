package com.wots.lutmaar.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.wots.lutmaar.Interface.RestServices;
import com.wots.lutmaar.R;
import com.wots.lutmaar.UtilClass.ConstantClass;
import com.wots.lutmaar.UtilClass.MyApplication;
import com.wots.lutmaar.UtilClass.UtilityClass;
import com.wots.lutmaar.UtilClass.Validation;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ForgetPasswordActivity extends ActionBarActivity {
    //Class
    UtilityClass utilityClass;
    ConstantClass constantClass;
    private Tracker mTracker;

    //Interface
    RestServices api;
    RestAdapter restAdapter;
    //View
    @InjectView(R.id.toolbar_login)
    Toolbar toolbar;
    @InjectView(R.id.etEmailForgetPass)
    EditText etEmailForgetPass;

    //Variable
    private int NetworkErrorCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_password_activity);
        ButterKnife.inject(this);
        declaration();
        initialization();
    }

    private void declaration() {
        utilityClass = new UtilityClass(this);

        MyApplication application = (MyApplication) getApplication();
        mTracker = application.getTracker();

        //Log.i(TAG, "Setting screen name: " + name);
        mTracker.setScreenName("Forget Password Activity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Forget Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       /* FrameLayout.LayoutParams Params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, Singleton.GetInstance().getScreenHeight());
        llBackGroundForgetPass.setLayoutParams(Params);*/
        if (utilityClass.isTablet(this) || utilityClass.isTabletDevice(this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    private void initialization() {
    }

    @OnClick(R.id.btnForgetPass)
    public void setBtnForgetPass(){
        if (utilityClass.isInternetConnection()) {
            if (checkValidation()) {
                utilityClass.processDialogStart();
                NetworkErrorCount = 0;
                restService();

            }
        } else {
            utilityClass.toast("Please check your internet settings!!!");
        }

    }
    private boolean checkValidation() {
        boolean ret = true;
        if (!Validation.hasText(etEmailForgetPass, "username Or email")) {
            ret = false;
        }
        return ret;
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
        HashMap<String, String> HMRegisteration = new HashMap<String, String>();
        HMRegisteration.put("name",etEmailForgetPass.getText().toString() );
       // HMLogIn.put("password", etPassword.getText().toString());
        api.userRegistration(HMRegisteration, new Callback<Object>() {
            @Override
            public void success(Object result, Response response) {
                utilityClass.processDialogStop();
                NetworkErrorCount = 0;
                Log.d("ForgetPassword Success:", result.toString());
                utilityClass.toast("password reset link sending to your email!!!");
                Intent ForgetPassIntent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
                startActivity(ForgetPassIntent);
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
                } else {
                    //  tvError.setVisibility(View.VISIBLE);
                    utilityClass.processDialogStop();
                    if (error.toString().contains("No address associated with hostname")) {
                        //    tvError.setText("Please check your network connection or try again later");
                        utilityClass.toast("Please check your network connection or try again later.");
                    } else if (error.toString().equals("retrofit.RetrofitError: 500 Internal Server Error")) {
                        utilityClass.toast("Internal Server Error");
                        //     tvError.setText("Internal Server Error");
                    } else if (error.toString().contains("com.google.gson.JsonSyntaxException")) {
                        //    tvError.setText("Result not found form server");
                        utilityClass.toast("Result not found form server");
                    } else if (error.toString().contains("RetrofitError: 404 Site or")) {
                        //    tvError.setText("Please check your internet settings!!!");
                        utilityClass.toast("Please check your internet settings!!!");
                    } else if (error.toString().contains( "is not recognized as a user name or an e-mail address")) {
                        // tvErrorSignUp.setText("The e-mail address "+etEmailSignUp.getText().toString()+" is already registered. Have you forgotten your password?");
                        // utilityClass.toast("The e-mail address " + etEmailSignUp.getText().toString() + " is already registered");
                        errorDialog(etEmailForgetPass.getText().toString() + " is not recognized as a user name or an e-mail address");
                    }else {
                        //   tvError.setText("Invalid user or password");
                        utilityClass.toast("email was not unsuccessful.");
                    }
                }
            }
        });
    }
    public void errorDialog(String error){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(error);
        builder.setPositiveButton("OK", null);
        AlertDialog dialog = builder.show();
        TextView messageView = (TextView)dialog.findViewById(android.R.id.message);
        messageView.setGravity(Gravity.CENTER);
    }
  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_froget_password, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}
