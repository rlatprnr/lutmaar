package com.wots.lutmaar.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.JsonElement;
import com.wots.lutmaar.GetterSetter.SignUpGetterSetter;
import com.wots.lutmaar.Interface.RestServices;
import com.wots.lutmaar.R;
import com.wots.lutmaar.UtilClass.ConstantClass;
import com.wots.lutmaar.UtilClass.MyApplication;
import com.wots.lutmaar.UtilClass.UtilityClass;
import com.wots.lutmaar.UtilClass.Validation;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SignUpActivity extends ActionBarActivity {
    //Class
    UtilityClass utilityClass;
    ConstantClass constantClass;
    Tracker mTracker;
    //Interface
    RestServices api;
    RestAdapter restAdapter;

    //View
    @InjectView(R.id.etUserSignUp)
    EditText etUserSignUp;
    /* @InjectView(R.id.etPasswordSignUp)
     EditText etPasswordSignUp;
     @InjectView(R.id.etConfPasswordSignUp)
     EditText etConfPasswordSignUp;*/
    @InjectView(R.id.etEmailSignUp)
    EditText etEmailSignUp;
    @InjectView(R.id.btnSignUp)
    Button btnSignUp;
    /* @InjectView(R.id.tvErrorSignUp)
     TextView tvErrorSignUp;*/
    @InjectView(R.id.toolbar_login)
    Toolbar toolbar;
    @InjectView(R.id.pbSignUp)
    ProgressBar pbSignUp;
    /*@InjectView(R.id.llBackGroundSignUp)
    LinearLayout llBackGroundSignUp;*/


    //Variable
    int maxLength;
    private int NetworkErrorCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);
        ButterKnife.inject(this);
        declaration();
        initialization();
    }

    private void declaration() {
        utilityClass = new UtilityClass(this);
        MyApplication application = (MyApplication) getApplication();
        mTracker = application.getTracker();

        //Log.i(TAG, "Setting screen name: " + name);
        mTracker.setScreenName("Sign UP Activity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sign Up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      /*  FrameLayout.LayoutParams Params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, Singleton.GetInstance().getScreenHeight());
        llBackGroundSignUp.setLayoutParams(Params);*/
      /*  if (utilityClass.isTablet(this) || utilityClass.isTabletDevice(this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            utilityClass.toast("Tablet");
        }else
            utilityClass.toast("Phone");*/
    }

    private void initialization() {
    }

    @OnClick(R.id.btnSignUp)
    public void setBtnSignUp() {
        // tvErrorSignUp.setVisibility(View.GONE);
        if (checkValidation()) {
            if (utilityClass.isInternetConnection()) {
                pbSignUp.setVisibility(View.VISIBLE);
                allDesable();
                NetworkErrorCount = 0;
                restService();
            } else {
                utilityClass.toast("Please check your internet settings!!! ");
            }

        }
    }

    @OnClick(R.id.tvTermsNConditions)
    public void setTvTermsNConditions() {
        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://lutmaar.com/content/terms/"));
        startActivity(myIntent);
    }


    public void allDesable() {
        etUserSignUp.setEnabled(false);
        etEmailSignUp.setEnabled(false);
        btnSignUp.setEnabled(false);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(false);

    }

    public void allenable() {
        etUserSignUp.setEnabled(true);
        etEmailSignUp.setEnabled(true);
        btnSignUp.setEnabled(true);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    private boolean checkValidation() {
        boolean ret = true;
        if (!Validation.hasText(etUserSignUp, "username")) {
            ret = false;
        } else if (!Validation.hasText(etEmailSignUp, "email")) {
            ret = false;
        } else if (!Validation.isEmailAddress(etEmailSignUp)) {
            ret = false;
        }
        return ret;

    }

    private void restService() {

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(constantClass.endPoints)
                .build();

        api = restAdapter.create(RestServices.class);
        SignUpGetterSetter signUpGetterSetter = new SignUpGetterSetter(etUserSignUp.getText().toString(), etEmailSignUp.getText().toString());

        api.SignUp(signUpGetterSetter, new Callback<JsonElement>() {
            @Override
            public void success(JsonElement result, Response response) {
                pbSignUp.setVisibility(View.GONE);
                allenable();
                NetworkErrorCount = 0;
                try {
                    if (result.isJsonObject()) {
                        //JsonObject jsonObject = result.getAsJsonObject();
                        JSONObject jsonObject = new JSONObject(String.valueOf(result.getAsJsonObject().toString()));
                        String userData = String.valueOf(jsonObject.toString());

                        // Bundle homeBundle = new Bundle();
                        Log.i("JsonObject Size :  ", String.valueOf(jsonObject));
                        Log.i("Sign Up User Id : ", userData);
                        Log.i("User ID : ", String.valueOf(jsonObject.getString("uid").toString()));
                        Log.i("User URL : ", String.valueOf(jsonObject.getString("uri").toString()));
                        utilityClass.toast("Thank you for applying for an account. A welcome message with password has been sent to your e-mail address");
                        finish();
                       /* homeBundle.putString("user", String.valueOf(jsonObject.getJSONObject("user").toString()));
                        Intent homeIntent = new Intent(LoginActivity.this, UserDetailsActivity.class);
                        homeIntent.putExtras(homeBundle);
                        startActivity(homeIntent);*/

                    } else {
                        Log.i("JsonArray Size :  ", String.valueOf(result));
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
                        allenable();
                        pbSignUp.setVisibility(View.GONE);
                        NetworkErrorCount = 0;
                    } else {
                        NetworkErrorCount++;
                        restService();
                    }
                } else {
                    utilityClass.processDialogStop();
                    allenable();
                    pbSignUp.setVisibility(View.GONE);
                    if (error.toString().contains("No address associated with hostname")) {
                        //   tvErrorSignUp.setText("Please check your network connection or try again later");
                        utilityClass.toast("Please check your network connection or try again later.");
                    } else if (error.toString().equals("retrofit.RetrofitError: 500 Internal Server Error")) {
                        utilityClass.toast("Internal Server Error");
                        // tvErrorSignUp.setText("Internal Server Error");
                    } else if (error.toString().contains("com.google.gson.JsonSyntaxException")) {
                        //  tvErrorSignUp.setText("Result not found form server");
                        utilityClass.toast("Result not found form server");
                    } else if (error.toString().contains("The name " + etUserSignUp.getText().toString() + " is already taken")) {
                        //  tvErrorSignUp.setText("The name "+etUserSignUp.getText().toString()+" is already taken, please select another username");
                        //utilityClass.toast("The name " + etUserSignUp.getText().toString() + " is already taken");
                        errorDialog("The name " + etUserSignUp.getText().toString() + " is already taken");
                    } else if (error.toString().contains("The e-mail address " + etEmailSignUp.getText().toString() + " is already registered")) {
                        // tvErrorSignUp.setText("The e-mail address "+etEmailSignUp.getText().toString()+" is already registered. Have you forgotten your password?");
                       // utilityClass.toast("The e-mail address " + etEmailSignUp.getText().toString() + " is already registered");
                        errorDialog("The e-mail address " + etEmailSignUp.getText().toString() + " is already registered");
                    } else {
                        //  tvErrorSignUp.setText("Sign Up Unsuccess");
                        utilityClass.toast("SignUp Un success");
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
   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
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
