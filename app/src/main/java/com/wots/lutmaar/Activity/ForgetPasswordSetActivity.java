package com.wots.lutmaar.Activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.widget.EditText;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.wots.lutmaar.Interface.RestServices;
import com.wots.lutmaar.R;
import com.wots.lutmaar.UtilClass.ConstantClass;
import com.wots.lutmaar.UtilClass.MyApplication;
import com.wots.lutmaar.UtilClass.UtilityClass;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.RestAdapter;

public class ForgetPasswordSetActivity extends ActionBarActivity {
    //Class
    UtilityClass utilityClass;
    ConstantClass constantClass;
    private Tracker mTracker;

    //Interface
    RestServices api;
    RestAdapter restAdapter;
    //View
    @InjectView(R.id.etPasswordForgetPassSet)
    EditText etPasswordForgetPassSet;
    @InjectView(R.id.etConfPasswordForgetPassSet)
    EditText etConfPasswordForgetPassSet;
    @InjectView(R.id.toolbar_login)
    Toolbar toolbar;
    /* @InjectView(R.id.lvAnimation)
     ListView lvAnimation;*/
   /* @InjectView(R.id.llBackGroundForgetPassSet)
    LinearLayout llBackGroundForgetPassSet;*/

    //Variable
    int maxLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_password_set_activity);
        ButterKnife.inject(this);
        declaration();
        initialization();
    }

    private void declaration() {
        utilityClass = new UtilityClass(this);
        MyApplication application = (MyApplication) getApplication();
        mTracker = application.getTracker();

        //Log.i(TAG, "Setting screen name: " + name);
        mTracker.setScreenName("Forget Password Set Activity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Forget Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      /*  FrameLayout.LayoutParams Params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, Singleton.GetInstance().getScreenHeight());
        llBackGroundForgetPassSet.setLayoutParams(Params);*/
        if (utilityClass.isTablet(this) || utilityClass.isTabletDevice(this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    private void initialization() {
        check_validations();

        // lvAnimation.setOnScrollListener((AbsListView.OnScrollListener) onScrollListener);
    }

    /* public RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
         boolean hideToolBar = false;
         @Override
         public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
             super.onScrollStateChanged(recyclerView, newState);
             if (hideToolBar) {
                 getSupportActionBar().hide();
             } else {
                 getSupportActionBar().show();
             }
         }

         @Override
         public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
             super.onScrolled(recyclerView, dx, dy);
             if (dy > 20) {
                 hideToolBar = true;

             } else if (dy < -5) {
                 hideToolBar = false;
             }
         }
     };*/
    private void check_validations() {


        etPasswordForgetPassSet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                maxLength = etPasswordForgetPassSet.length();
                etConfPasswordForgetPassSet.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
                if (maxLength < 6) {
                    etPasswordForgetPassSet.setError("Minimum 6 character require");
                }

            }
        });

    }

    @OnClick(R.id.btnForgetPassSet)
    public void setBtnForgetPassSet() {
      //  utilityClass.processDialogStart();
        //restService();
    }

   /* private void restService() {

        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Content-Type", "application/json");
                request.addHeader("X-CSRF-Token", SaveSharedPreferences.getTokon(ForgetPasswordSetActivity.this));
                request.addHeader("Cookie", SaveSharedPreferences.getCookie(ForgetPasswordSetActivity.this));

                Log.i("Tokon : ", SaveSharedPreferences.getTokon(ForgetPasswordSetActivity.this));
                Log.i("Cookie : ", SaveSharedPreferences.getCookie(ForgetPasswordSetActivity.this));
            }
        };

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(constantClass.endPoints)
                .setRequestInterceptor(requestInterceptor)
                .build();

        api = restAdapter.create(RestServices.class);
        HashMap<String, Object> HMPostADeal = new HashMap<String, Object>();
        HashMap<String, String> HMFieldCat = new HashMap<String, String>();
        HashMap<String, String> HMFieldDeals = new HashMap<String, String>();
        HashMap<String, String> HMBodyValue = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> ALHMBodyValue = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> ALHMFieldDealUrl = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> ALHMFieldPrice = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> ALHMImageFid = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> ALHMAddressLocality = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, Object>> ALHMStartDate = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> ALHMEndDate = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> HMBodyUnd = new HashMap<String, Object>();
        HashMap<String, String> HMFieldDealUrl = new HashMap<String, String>();
        HashMap<String, Object> HMFieldDealUnd = new HashMap<String, Object>();
        HashMap<String, String> HMFieldPriceValue = new HashMap<String, String>();
        HashMap<String, Object> HMFieldPriceUnd = new HashMap<String, Object>();
        HashMap<String, String> HMStartDate = new HashMap<String, String>();
        HashMap<String, Object> HMStartDateValue = new HashMap<String, Object>();
        HashMap<String, Object> HMStartDateUnd = new HashMap<String, Object>();
        HashMap<String, Object> HMEndDateValue = new HashMap<String, Object>();
        HashMap<String, String> HMEndDate = new HashMap<String, String>();
        HashMap<String, Object> HMEndDateUnd = new HashMap<String, Object>();
        HashMap<String, String> HMImageFid = new HashMap<String, String>();
        HashMap<String, Object> HMImageUnd = new HashMap<String, Object>();
        HashMap<String, String> HMAddressLocality = new HashMap<String, String>();
        HashMap<String, Object> HMAddressUnd = new HashMap<String, Object>();


        HMFieldCat.put("und", "deals");
        HMFieldDeals.put("und", "5");

        HMBodyValue.put("value", "Testing for rest services...sdhfhfew,fsdjkhcd,fklhrd,fjekwhcdl,fjhijhsdllf,frhoncus magna. Cras in mattis");
        ALHMBodyValue.add(HMBodyValue);
        HMBodyUnd.put("und", ALHMBodyValue);


        HMFieldDealUrl.put("url", "http://uk.lipsum.com/feed/html");
        ALHMFieldDealUrl.add(HMFieldDealUrl);
        HMFieldDealUnd.put("und", ALHMFieldDealUrl);


        HMFieldPriceValue.put("value", "120");
        ALHMFieldPrice.add(HMFieldPriceValue);
        HMFieldPriceUnd.put("und", ALHMFieldPrice);

        HMStartDate.put("date", "01 Jul 2015");
        HMStartDateValue.put("value", HMStartDate);
        ALHMStartDate.add(HMStartDateValue);
        HMStartDateUnd.put("und", ALHMStartDate);

        HMEndDate.put("date", "20 Jul 2015");
        HMEndDateValue.put("value", HMEndDate);
        ALHMEndDate.add(HMEndDateValue);
        HMEndDateUnd.put("und", ALHMEndDate);


        HMImageFid.put("fid", "71");
        ALHMImageFid.add(HMImageFid);
        HMImageUnd.put("und", ALHMImageFid);


        HMAddressLocality.put("locality", "Kolkata");
        ALHMAddressLocality.add(HMAddressLocality);
        HMAddressUnd.put("und", ALHMAddressLocality);

        HMPostADeal.put("type", "products");
        HMPostADeal.put("field_cat", HMFieldCat);
        //Log.i("Tokon : ", HMFieldCat.toString());
        HMPostADeal.put("title", "Testing Some new title");
        HMPostADeal.put("body", HMBodyUnd);
        HMPostADeal.put("field_deals", HMFieldDeals);
        HMPostADeal.put("field_deal_url", HMFieldDealUnd);
        HMPostADeal.put("field_price", HMFieldPriceUnd);
        HMPostADeal.put("field_start_date", HMStartDateUnd);
        HMPostADeal.put("field_end_date", HMEndDateUnd);
        HMPostADeal.put("field_images", HMImageUnd);
        HMPostADeal.put("field_postal_address", HMAddressUnd);

        api.PostADeal(HMPostADeal, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject result, Response response) {
                utilityClass.processDialogStop();

                if (true) {
                    utilityClass.toast("User logout Success");
                    Log.i("User logout Success :  ", String.valueOf(result));

                } else {
                    Log.i("User logout Response :", String.valueOf(result));
                }

            }

            @Override
            public void failure(RetrofitError error) {
                utilityClass.processDialogStop();
                Log.i("Restrofit Error  : ", String.valueOf(error.toString()));

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
                    utilityClass.toast("User can not logout");
                }
            }
        });
    }*/

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_forget_password_set, menu);
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
