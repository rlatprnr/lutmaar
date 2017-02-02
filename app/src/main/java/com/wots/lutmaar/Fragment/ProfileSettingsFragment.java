package com.wots.lutmaar.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.wots.lutmaar.R;
import com.wots.lutmaar.UtilClass.ConstantClass;
import com.wots.lutmaar.UtilClass.Singleton;
import com.wots.lutmaar.UtilClass.UtilityClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ProfileSettingsFragment extends Fragment  {

    UtilityClass utilityClass;
    ConstantClass constantClass;
    CallbackManager callbackManager;
    private TwitterSession session;
    TwitterAuthToken authToken;

    //View
    @InjectView(R.id.btnFaceboolSetting)
    LoginButton btnFaceboolSetting;
    //@InjectView(R.id.btnTwitterSetting)
    TwitterLoginButton btnTwitterSetting;
    @InjectView(R.id.tvSwitchLutMaarNewsLetterOnOff)
    TextView tvSwitchLutMaarNewsLetterOnOff;
    @InjectView(R.id.tvSwitchSimpleNewsCategoryOnOff)
    TextView tvSwitchSimpleNewsCategoryOnOff;
    @InjectView(R.id.tvSwitchWeeklyHotDealsOnOff)
    TextView tvSwitchWeeklyHotDealsOnOff;


    //Variable
    Boolean SwitchLutMaarNewsLetter=true,
            SwitchSimpleNewsCategory=true,
            SwitchWeeklyHotDeals=true;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        View view = inflater.inflate(R.layout.profile_settings_fragment, container, false);
        ButterKnife.inject(this, view);


        declaration();

        initialization();

        return view;
    }

    private void declaration() {
        utilityClass = new UtilityClass(getActivity());
    }

    private void initialization() {


    }
    @OnClick(R.id.tvSwitchLutMaarNewsLetterOnOff)
     public void setTvSwitchLutMaarNewsLetterOnOff(){
        if(SwitchLutMaarNewsLetter) {
            tvSwitchLutMaarNewsLetterOnOff.setText("OFF");
            tvSwitchLutMaarNewsLetterOnOff.setBackground(getResources().getDrawable(R.drawable.ios_off));
            tvSwitchLutMaarNewsLetterOnOff.setCompoundDrawablesWithIntrinsicBounds(R.drawable.switch_on_off_small, 0, 0, 0);
            SwitchLutMaarNewsLetter=false;
        }else{
            tvSwitchLutMaarNewsLetterOnOff.setText("ON");
            tvSwitchLutMaarNewsLetterOnOff.setBackground(getResources().getDrawable(R.drawable.ios_on));
            tvSwitchLutMaarNewsLetterOnOff.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.switch_on_off_small, 0);
            SwitchLutMaarNewsLetter=true;
        }

    }
    @OnClick(R.id.tvSwitchSimpleNewsCategoryOnOff)
    public void setTvSwitchSimpleNewsCategoryOnOff(){
        if(SwitchSimpleNewsCategory) {
            tvSwitchSimpleNewsCategoryOnOff.setText("OFF");
            tvSwitchSimpleNewsCategoryOnOff.setBackground(getResources().getDrawable(R.drawable.ios_off));
            tvSwitchSimpleNewsCategoryOnOff.setCompoundDrawablesWithIntrinsicBounds(R.drawable.switch_on_off_small, 0, 0, 0);
            SwitchSimpleNewsCategory=false;
        }else{
            tvSwitchSimpleNewsCategoryOnOff.setText("ON");
            tvSwitchSimpleNewsCategoryOnOff.setBackground(getResources().getDrawable(R.drawable.ios_on));
            tvSwitchSimpleNewsCategoryOnOff.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.switch_on_off_small, 0);
            SwitchSimpleNewsCategory=true;
        }

    }

    @OnClick(R.id.tvSwitchWeeklyHotDealsOnOff)
    public void setTvSwitchWeeklyHotDealsOnOff(){
        if(SwitchWeeklyHotDeals) {
            tvSwitchWeeklyHotDealsOnOff.setText("OFF");
            tvSwitchWeeklyHotDealsOnOff.setBackground(getResources().getDrawable(R.drawable.ios_off));
            tvSwitchWeeklyHotDealsOnOff.setCompoundDrawablesWithIntrinsicBounds(R.drawable.switch_on_off_small, 0, 0, 0);
            SwitchWeeklyHotDeals=false;
        }else{
            tvSwitchWeeklyHotDealsOnOff.setText("ON");
            tvSwitchWeeklyHotDealsOnOff.setBackground(getResources().getDrawable(R.drawable.ios_on));
            tvSwitchWeeklyHotDealsOnOff.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.switch_on_off_small, 0);
            SwitchWeeklyHotDeals = true;
        }

    }

    @OnClick(R.id.btnFaceboolSetting)
    public void setBtnFacebookSettings() {
        callbackManager = CallbackManager.Factory.create();
        // Set permissions
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "user_photos",
                "public_profile","user_about_me","user_birthday",  "user_hometown","user_location"));
       // btnFaceboolSetting.setFragment(this);
        btnFaceboolSetting.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Log.d("FB LogIn Success: ", loginResult.toString());
                utilityClass.toast("FaceBook Success");

                GraphRequest.newMeRequest(
                        loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject json, GraphResponse response) {
                                if (response.getError() != null) {
                                    // handle error
                                    System.out.println("ERROR");
                                } else {
                                    utilityClass.toast("FaceBook Success");
                                    try {

                                        String jsonresult = String.valueOf(json);
                                        Log.d("FaceBook JSON Result : ", jsonresult);

                                        String str_email = json.getString("email");
                                        String str_id = json.getString("id");
                                        String str_firstname = json.getString("first_name");
                                        String str_lastname = json.getString("last_name");
                                        utilityClass.toast("Wellcome  : " + str_firstname);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                        }).executeAsync();
            }

            @Override
            public void onCancel() {
                // App code
                Log.d("Facebook Log In Cancel ", "Cancell");
                utilityClass.toast("FaceBook Cancell");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.d("Facebook LogIn Error:", exception.toString());
                utilityClass.toast("FaceBook Error ");
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        Singleton.GetInstance().BottomMenuHide();
        Singleton.GetInstance().getTvMainHeading().setText("Settings");
    }

}
