package com.wots.lutmaar.Activity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;
import com.wots.lutmaar.Adapter.NavDrawerListAdapter;
import com.wots.lutmaar.Adapter.SelectCityAdapter;
import com.wots.lutmaar.CustomView.CustomTextView;
import com.wots.lutmaar.CustomView.ImageLoader.ImageLoader;
import com.wots.lutmaar.CustomView.imageshape.RoundedImageView;
import com.wots.lutmaar.Fragment.AskFragment;
import com.wots.lutmaar.Fragment.ChatFragment;
import com.wots.lutmaar.Fragment.CompetitionFragment;
import com.wots.lutmaar.Fragment.DealDetailsAskFragment;
import com.wots.lutmaar.Fragment.DealDetailsReviewsFragment;
import com.wots.lutmaar.Fragment.DealDetailsVoucherFragment;
import com.wots.lutmaar.Fragment.DealsFragment;
import com.wots.lutmaar.Fragment.FragmentNavigationDrawer;
import com.wots.lutmaar.Fragment.FreebiesFragment;
import com.wots.lutmaar.Fragment.MapNearByFragment;
import com.wots.lutmaar.Fragment.MassagesFragment;
import com.wots.lutmaar.Fragment.MyAccountFragment;
import com.wots.lutmaar.Fragment.MyDealsFragment;
import com.wots.lutmaar.Fragment.MyPointsFragment;
import com.wots.lutmaar.Fragment.NewsFeed;
import com.wots.lutmaar.Fragment.PostADealFragment;
import com.wots.lutmaar.Fragment.ProfileSettingsFragment;
import com.wots.lutmaar.Fragment.RecentFragment;
import com.wots.lutmaar.Fragment.ReviewsFragment;
import com.wots.lutmaar.Fragment.SearchFragment;
import com.wots.lutmaar.Fragment.SearchResultFragment;
import com.wots.lutmaar.Fragment.Top25Fragment;
import com.wots.lutmaar.Fragment.VouchersFragment;
import com.wots.lutmaar.Fragment.abc;
import com.wots.lutmaar.GetterSetter.NavDrawerItem;
import com.wots.lutmaar.Interface.RestServices;
import com.wots.lutmaar.R;
import com.wots.lutmaar.UtilClass.ConstantClass;
import com.wots.lutmaar.UtilClass.GPSTracker;
import com.wots.lutmaar.UtilClass.MyApplication;
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

public class HomeActivity extends ActionBarActivity implements View.OnClickListener {

    //Class
    UtilityClass utilityClass;
    final Context context = this;
    private ArrayList<NavDrawerItem> navDrawerItems = new ArrayList<NavDrawerItem>();
    AlertDialog alertDialog;
    private LocationManager locationManager;
    GPSTracker gpsTracker;
    Location location;
    private ShowcaseView showcaseView;
    JSONObject userObj;
    public ImageLoader imageLoader;
    private Tracker mTracker;


    //View
    @InjectView(R.id.drawer_layout)
    FragmentNavigationDrawer dlDrawer;
    @InjectView(R.id.LLLeftDrawer)
    LinearLayout LLLeftDrawer;
    @InjectView(R.id.LLDrawer)
    LinearLayout LL;
    @InjectView(R.id.tvUserName)
    CustomTextView tvUserName;
    @InjectView(R.id.toolbarHome)
    Toolbar toolbarHome;
    @InjectView(R.id.lvMenu)
    ListView lvManu;
    @InjectView(R.id.lvDrawer)
    ListView lvDrawer;
    @InjectView(R.id.ivUserImage)
    RoundedImageView ivUserImage;
    @InjectView(R.id.ivOverFlowMenu)
    ImageView ivOverFlowMenu;
    @InjectView(R.id.ivSearchHome)
    ImageView ivSearchHome;
    @InjectView(R.id.HeaderToolbarHome)
    CustomTextView HeaderToolbarHome;
    @InjectView(R.id.LLBottomMenu)
    LinearLayout LLBottomMenu;
    @InjectView(R.id.LLBottomMenuOvel)
    LinearLayout LLBottomMenuOvel;
    @InjectView(R.id.LLSearchCity)
    LinearLayout LLSearchCity;
    @InjectView(R.id.tvSearchCity)
    TextView tvSearchCity;
    @InjectView(R.id.spDealsCategoryHome)
    Spinner spDealsCategoryHome;

    @InjectView(R.id.ivCityCurrentLocation)
    ImageView ivCityCurrentLocation;
    @InjectView(R.id.ivBottomMenuTop25)
    ImageView ivBottomMenuTop25;
    @InjectView(R.id.ivBottomMenuReviews)
    ImageView ivBottomMenuReviews;
    @InjectView(R.id.ivBottomMenuLocal)
    ImageView ivBottomMenuLocal;
    @InjectView(R.id.ivBottomMenuRecent)
    ImageView ivBottomMenuRecent;


    //Interface
    RestServices api;
    RestAdapter restAdapter;

    //Variable
    ArrayAdapter<String> catAdapter;
    private static final int TIME_INTERVAL = 2000;
    private long mBackPressed;
    ArrayList<String> CityNameList = new ArrayList<String>();
    private int ShowCasecounter = 0;
    boolean checkFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        ButterKnife.inject(this);
        if (savedInstanceState != null)
            checkFirst = false;
        deaclaration();
        initialization();


        // Select default
        if (savedInstanceState == null) {
            dlDrawer.selectDrawerItem(7, "0");
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void deaclaration() {
        utilityClass = new UtilityClass(this);
        MyApplication application = (MyApplication) getApplication();
        mTracker = application.getTracker();
        utilityClass.restServiceUnReadMessage();
        if (utilityClass.isInternetConnection()) {
            if (Singleton.GetInstance().getHMCategoryHome().size() > 0) {
                catAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Singleton.GetInstance().getCategoryListHome());
                spDealsCategoryHome.setAdapter(catAdapter);
            } else {
                utilityClass.processDialogStart();
                getCategoryRestService();
            }
        } else {
            setDefaultCat();
        }

        //Log.i(TAG, "Setting screen name: " + name);
        mTracker.setScreenName("Home Activity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        if (SaveSharedPreferences.getWCHome(HomeActivity.this).length() == 0) {
            SaveSharedPreferences.setWCHome(HomeActivity.this, "Already Done");
            showcaseView = new ShowcaseView.Builder(this, true)
                    .setTarget(new ViewTarget(ivOverFlowMenu))
                    .setStyle(R.style.CustomShowcaseTheme)
                    .setOnClickListener(this)
                    .build();
            showcaseView.setButtonText("Next");
            showcaseView.setContentTitle(" Type of deal");
            //showcaseView.setContentText("Type of deal");
        } else {
            if (checkFirst)
                shackDialogShow();
        }
        try {
            /*if (SaveSharedPreferences.getCityName(this).equalsIgnoreCase(""))
                SaveSharedPreferences.setCityName(this, "all");*/
            userObj = new JSONObject(SaveSharedPreferences.getLoginObj(HomeActivity.this));
            tvUserName.setText(userObj.getJSONObject("user").getString("name"));
            Picasso.with(context).load(userObj.getJSONObject("user").getJSONObject("picture").getString("url")).placeholder(R.drawable.login).noFade().resize(200, 200).centerInside().into(ivUserImage);
            //imageLoader.DisplayImage(userObj.getJSONObject("user").getJSONObject("picture").getString("url"),ivUserImage);

        } catch (JSONException e) {
            Log.d("User Object: ", e.toString());
        } catch (NullPointerException e) {
            Log.d("Null City: ", e.toString());
            SaveSharedPreferences.setCityName(this, "all");
        }
        imageLoader = new ImageLoader(HomeActivity.this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        gpsTracker = new GPSTracker(HomeActivity.this);
        Singleton.GetInstance().setMenu(LLBottomMenu, LLBottomMenuOvel, ivOverFlowMenu, lvManu);
        Singleton.GetInstance().setDlDrawerFragment(dlDrawer);
        Singleton.GetInstance().setIvMainSearch(ivSearchHome);
        Singleton.GetInstance().setTvMainHeading(HeaderToolbarHome);
        Singleton.GetInstance().setTvDisplayCityName(tvSearchCity);
        Singleton.GetInstance().setLLCitySearch(LLSearchCity);
        Singleton.GetInstance().setTvDealCategoryName(spDealsCategoryHome);
        Singleton.GetInstance().getTvDisplayCityName().setText(SaveSharedPreferences.getCityName(this));
        setSupportActionBar(toolbarHome);


        if (utilityClass.isTablet(this) || utilityClass.isTabletDevice(this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }

        addCity();

    }

    private void shackDialogShow() {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.shake_cell);
        dialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.cancel();
            }
        }, 5000);
    }

    private void initialization() {


        dlDrawer.setupDrawerConfiguration(lvDrawer, toolbarHome,
                LL, LLLeftDrawer, HeaderToolbarHome, HomeActivity.this, R.layout.drawer_nav_item_right, R.id.flContent);
        // Add nav items for Right Menu Slider
        dlDrawer.addNavItem("My Points", R.drawable.ic_points, "My Points", MyPointsFragment.class);
        dlDrawer.addNavItem("Messages", R.drawable.ic_messages, "Messages", MassagesFragment.class);

        dlDrawer.addNavItem("My Account", R.drawable.ic_myaccount, "My Account", MyAccountFragment.class);
        dlDrawer.addNavItem("Post a Deal", R.drawable.ic_postadeal, "Post a Deal", PostADealFragment.class);

        dlDrawer.addNavItem("My Deals", R.drawable.ic_mydeal, "My Deals", MyDealsFragment.class);
        dlDrawer.addNavItem("Settings", R.drawable.ic_settings, "", ProfileSettingsFragment.class);
        dlDrawer.addNavItem("Log Out", R.drawable.ic_logout, "Log Out", abc.class);

        // Add nav items for Left Menu Class Slider
        dlDrawer.addNavItemLeft("All Latest Deals", NewsFeed.class);
        dlDrawer.addNavItemLeft("Latest Deals", DealsFragment.class);
        dlDrawer.addNavItemLeft("Latest Vouchers", VouchersFragment.class);
        dlDrawer.addNavItemLeft("Latest Freebies", FreebiesFragment.class);
        dlDrawer.addNavItemLeft("Latest Responses", AskFragment.class);
        dlDrawer.addNavItemLeft("Latest Contests", CompetitionFragment.class);

        //Deal Details
        dlDrawer.addNavItemLeft("Deal Details", DealDetailsReviewsFragment.class);

        // Bottom Menu Fragment
        dlDrawer.addNavItemLeft("Top 25", Top25Fragment.class);
        dlDrawer.addNavItemLeft("Reviews", ReviewsFragment.class);
        dlDrawer.addNavItemLeft("Local", MapNearByFragment.class);
        dlDrawer.addNavItemLeft("Recent", RecentFragment.class);

        //search at Toolbar Home
        dlDrawer.addNavItemLeft("Search", SearchFragment.class);
        dlDrawer.addNavItemLeft("Search Result", SearchResultFragment.class);
        dlDrawer.addNavItemLeft("Massages", ChatFragment.class);
        dlDrawer.addNavItemLeft("Deal Details", DealDetailsVoucherFragment.class);
        dlDrawer.addNavItemLeft("Deal Details", DealDetailsAskFragment.class);

        navDrawerItems.add(new NavDrawerItem("ALL", R.drawable.ic_all));
        navDrawerItems.add(new NavDrawerItem("DEALS", R.drawable.ic_deals));
        navDrawerItems.add(new NavDrawerItem("VOUCHERS", R.drawable.ic_vouchers));
        navDrawerItems.add(new NavDrawerItem("FREEBIES", R.drawable.ic_freebies));
        navDrawerItems.add(new NavDrawerItem("ASK", R.drawable.ic_ask));
        navDrawerItems.add(new NavDrawerItem("COMPETITIONS", R.drawable.ic_competition));
        lvManu.setAdapter(new NavDrawerListAdapter(this, "Left", navDrawerItems));
        // lvManu.setBackgroundColor(Color.parseColor("#5C6BC0"));
        lvManu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Singleton.GetInstance().getTvDealCategoryName().setSelection(0);
                Singleton.GetInstance().setDealSubCategory("all");
                switch (position) {

                    case 0:
                        dlDrawer.selectDrawerItem(position + 7, "");
                        Singleton.GetInstance().setDealType("all");
                        break;
                    case 1:
                        dlDrawer.selectDrawerItem(position + 7, "");
                        Singleton.GetInstance().setDealType("deals");
                        break;
                    case 2:
                        dlDrawer.selectDrawerItem(position + 7, "");
                        Singleton.GetInstance().setDealType("vouchers");
                        break;
                    case 3:
                        dlDrawer.selectDrawerItem(position + 7, "");
                        Singleton.GetInstance().setDealType("freebies");
                        break;
                    case 4:
                        dlDrawer.selectDrawerItem(position + 7, "");
                        break;
                    case 5:
                        dlDrawer.selectDrawerItem(position + 7, "");
                        Singleton.GetInstance().setDealType("all");
                        break;
                }
            }
        });

        spDealsCategoryHome.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (utilityClass.isInternetConnection()) {
                    Singleton.GetInstance().setDealSubCategory(parent.getSelectedItem().toString());
                    if (parent.getSelectedItemPosition() != 0) {
                        Singleton.GetInstance().setCurrentPosition(Singleton.GetInstance().getCurrentPosition() - 1);
                        Singleton.GetInstance().FragmentCall(Singleton.GetInstance().getCurrentPosition() + 1, "");
                    }

                } else {
                    utilityClass.toast("Please check your internet settings!!!");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


    @OnClick(R.id.ivOverFlowMenu)
    public void setIvOverFlowMenu() {
        if (Singleton.GetInstance().getOverFlowCheck() == 1) {
            if (dlDrawer.isDrawerOpen(Gravity.LEFT)) {
                dlDrawer.closeDrawer(Gravity.LEFT);
            } else {
                dlDrawer.openDrawer(Gravity.LEFT);
            }
        } else {
            int position = Singleton.GetInstance().getHomeMenuFragmentPosition();
            ivSearchHome.setVisibility(View.VISIBLE);
            if (position == 7 || position == 8 || position == 9 || position == 10 || position == 11 ||
                    position == 12 || position == 14 || position == 15 || position == 17) {
                dlDrawer.selectDrawerItem(position, "");
            } else if (position == 1) {
                dlDrawer.selectDrawerItem(position, "");
            } else {
                dlDrawer.selectDrawerItem(7, "");
            }
            Singleton.GetInstance().MenuShow();
        }
    }

    @OnClick(R.id.ivSearchHome)
    public void setIvSearchHome() {
        Singleton.GetInstance().setHomeMenuFragmentPosition(Singleton.GetInstance().getCurrentPosition());
        Singleton.GetInstance().MenuShow();
        Singleton.GetInstance().LeftMenuHide();
        Singleton.GetInstance().FragmentCall(18, "");
        ivSearchHome.setVisibility(View.GONE);
    }

    @OnClick(R.id.tvSearchCity)
    public void setTvSearchCity() {
        if (utilityClass.isInternetConnection()) {
            SelectCityDialog();
        } else {
            utilityClass.toast("Please check your internet settings!!!");
        }
    }

   /* @OnClick(R.id.tvDealsCategoryHome)
    public void setTvDealsCategoryHome() {
        if (utilityClass.isInternetConnection()) {
            if (Singleton.GetInstance().getHMCategory().size() > 0) {
                if (Singleton.GetInstance().getHMCategory().get(0).containsValue("all"))
                    SelectCategoryDialog();
                else {
                    utilityClass.processDialogStart();
                    getCategoryRestService();
                }
            } else {
                utilityClass.processDialogStart();
                getCategoryRestService();
            }
        } else {
            utilityClass.toast("Please check your internet settings!!!");
        }
    }*/

    @OnClick(R.id.ivCityCurrentLocation)
    public void setIvCityCurrentLocation() {
        if (utilityClass.isInternetConnection()) {
            utilityClass.processDialogStart();
            getCurrentCityName(String.valueOf(gpsTracker.getLatitude()) + "," + String.valueOf(gpsTracker.getLongitude()));
        } else {
            utilityClass.toast("Please check your internet settings!!!");
        }
    }

    @OnClick(R.id.ivBottomMenuTop25)
    public void setIvBottomMenuTop25() {
        Singleton.GetInstance().FragmentCall(14, "");
    }

    @OnClick(R.id.ivBottomMenuReviews)
    public void setIvBottomMenuReviews() {
        Singleton.GetInstance().FragmentCall(15, "");
    }

    @OnClick(R.id.ivBottomMenuLocal)
    public void setIvBottomMenuLocal() {
        CheckGPSEnable();
        /*Intent main = new Intent(HomeActivity.this,MainActivity.class);
        startActivity(main);*/
       /* Singleton.GetInstance().FragmentCall(16, "");
        Singleton.GetInstance().MenuHide();
        ivSearchHome.setVisibility(View.GONE);*/
    }

    @OnClick(R.id.ivBottomMenuRecent)
    public void setIvBottomMenuRecent() {
        Singleton.GetInstance().FragmentCall(17, "");

    }

    @OnClick(R.id.rightMenuHome)
    public void setRightMenuHome() {
        if (dlDrawer.isDrawerOpen(Gravity.RIGHT)) {
            dlDrawer.closeDrawer(Gravity.RIGHT);
        } else {
            dlDrawer.openDrawer(Gravity.RIGHT);
        }
    }

    @OnClick(R.id.ivPostADealHome)
    public void setIvPostADealHome() {
        dlDrawer.selectDrawerItem(3, "0");
        Singleton.GetInstance().MenuHide();
    }

    private void SelectCityDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.select_city_cell);
        final SelectCityAdapter adapter;

        final EditText etSearchCity = (EditText) dialog.findViewById(R.id.etSearchCity);
        ImageView ivGetCurrentCity = (ImageView) dialog.findViewById(R.id.ivGetCurrentCity);
        ListView lvSearchCity = (ListView) dialog.findViewById(R.id.lvSearchCity);
        adapter = new SelectCityAdapter(HomeActivity.this, dialog, tvSearchCity, "Deal", "City", CityNameList);
        lvSearchCity.setAdapter(adapter);
        etSearchCity.setText(tvSearchCity.getText().toString());
        ivGetCurrentCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    location = gpsTracker.getLocation();
                    if (location != null) {
                        utilityClass.processDialogStart();
                        getLocationCityName(etSearchCity, String.valueOf(gpsTracker.getLatitude()) + "," + String.valueOf(gpsTracker.getLongitude()));
                        /*locationAddress.getAddressFromLocation(gpsTracker.getLatitude(), gpsTracker.getLongitude(),
                                getApplicationContext(), new GeocoderHandler(etSearchCity));*/
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


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content
        if (dlDrawer.isDrawerOpen()) {
            // Uncomment to hide menu items
            // menu.findItem(R.id.mi_test).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        /*
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) HomeActivity.this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchView == null) {
            searchView=(SearchView)searchItem.getActionView();
        }
        if(searchView!=null){
            searchView.setSearchableInfo(searchManager.getSearchableInfo(HomeActivity.this.getComponentName()));
        }
        searchView.setOnQueryTextListener(HomeActivity.this);*/
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
       /* int id = item.getItemId();
        if (id == R.id.btnMyMenu) {
            if (dlDrawer.isDrawerOpen(Gravity.RIGHT)) {
                dlDrawer.closeDrawer(Gravity.RIGHT);
            } else {
                dlDrawer.openDrawer(Gravity.RIGHT);
            }
            return true;

        }*/
        if (dlDrawer.getDrawerToggle().onOptionsItemSelected(item)) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        dlDrawer.getDrawerToggle().syncState();
        // dlDrawer.getDrawerToggle().syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        dlDrawer.getDrawerToggle().onConfigurationChanged(newConfig);
        // dlDrawer.getDrawerToggle().onConfigurationChanged(newConfig);
    }


    private void shareLink() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        // share.putExtra(Intent.EXTRA_SUBJECT,"Have you tried TrailersNSongs for Android? It's awesome! ");
        share.putExtra(Intent.EXTRA_TEXT, "Have you tried LutMaar for Android? It's awesome! https://play.google.com/store/apps/details?id=com.wots.movietrailers");
        startActivity(Intent.createChooser(share, " Share link!"));
    }

    @Override
    public void onBackPressed() {

        if (dlDrawer.isDrawerOpen())
            dlDrawer.closeLLDrawer();
        else {
            if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
                Intent i = new Intent(Intent.ACTION_MAIN);
                i.addCategory(Intent.CATEGORY_HOME);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            } else {
                if (dlDrawer.isDrawerOpen())
                    dlDrawer.closeLLDrawer();
                else
                    utilityClass.toast("Double tap to exit!");
            }
            mBackPressed = System.currentTimeMillis();
        }
    }

    /* @Override
     public void onDestroy() {
         if (Singleton.GetInstance().getCheckRotation() != null) {
             android.provider.Settings.System.putInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0);
             Toast.makeText(getApplicationContext(), "Rotation ON", Toast.LENGTH_SHORT).show();
         } else {
             Toast.makeText(getApplicationContext(), "Rotation OFF", Toast.LENGTH_SHORT).show();
         }
         super.onDestroy();
     }
 */
    public void addCity() {
        //  CityNameList.add("all");
        CityNameList.add("Mumbai");
        CityNameList.add("Delhi");
        CityNameList.add("Bangalore");
        CityNameList.add("Hyderabad");
        CityNameList.add("Ahmedabad");
        CityNameList.add("Chennai");
        CityNameList.add("Kolkata");

        CityNameList.add("Agartala");
        CityNameList.add("Agra");
        CityNameList.add("Ahmednagar");
        CityNameList.add("Aizawl");
        CityNameList.add("Ajmer");
        CityNameList.add("Akola");
        CityNameList.add("Aligarh");
        CityNameList.add("Allahabad");
        CityNameList.add("Alwar");
        CityNameList.add("Ambattur");
        CityNameList.add("Ambernath");
        CityNameList.add("Amravati");
        CityNameList.add("Amritsar");
        CityNameList.add("Anantapur");
        CityNameList.add("Arrah");
        CityNameList.add("Asansol");
        CityNameList.add("Aurangabad");
        CityNameList.add("Avadi");
        CityNameList.add("Bally");
        CityNameList.add("Baranagar");
        CityNameList.add("Barasat");
        CityNameList.add("Bardhaman");
        CityNameList.add("Bareilly");
        CityNameList.add("Bathinda");
        CityNameList.add("Begusarai");
        CityNameList.add("Belgaum");
        CityNameList.add("Bellary");
        CityNameList.add("Bhagalpur");
        CityNameList.add("Bharatpur");
        CityNameList.add("Bhatpara");
        CityNameList.add("Bhavnagar");
        CityNameList.add("Bhilai");
        CityNameList.add("Bhilwara");
        CityNameList.add("Bhiwandi");
        CityNameList.add("Bhopal");
        CityNameList.add("Bhubaneswar");
        CityNameList.add("Bihar Sharif");
        CityNameList.add("Bijapur");
        CityNameList.add("Bikaner");
        CityNameList.add("Bilaspur");
        CityNameList.add("Bokaro");
        CityNameList.add("Brahmapur");
        CityNameList.add("Bulandshahr");
        CityNameList.add("Chandigarh");
        CityNameList.add("Chandrapur");
        CityNameList.add("Coimbatore");
        CityNameList.add("Cuttack");
        CityNameList.add("Darbhanga");
        CityNameList.add("Davanagere");
        CityNameList.add("Dehradun");
        CityNameList.add("Dewas");
        CityNameList.add("Dhanbad");
        CityNameList.add("Dhule");
        CityNameList.add("Durg");
        CityNameList.add("Durgapur");
        CityNameList.add("Etawah");
        CityNameList.add("Faridabad");
        CityNameList.add("Farrukhabad");
        CityNameList.add("Firozabad");
        CityNameList.add("Gandhidham");
        CityNameList.add("Gaya");
        CityNameList.add("Ghaziabad");
        CityNameList.add("Gopalpur");
        CityNameList.add("Gorakhpur");
        CityNameList.add("Gulbarga");
        CityNameList.add("Guntur[3]");
        CityNameList.add("Gurgaon");
        CityNameList.add("Guwahati");
        CityNameList.add("Gwalior");
        CityNameList.add("Hapur");
        CityNameList.add("Haridwar");
        CityNameList.add("Hisar");
        CityNameList.add("Howrah");
        CityNameList.add("Hubballi-Dharwad");
        CityNameList.add("Ichalkaranji");
        CityNameList.add("Imphal");
        CityNameList.add("Indore");
        CityNameList.add("Jabalpur");
        CityNameList.add("Jaipur");
        CityNameList.add("Jalandhar");
        CityNameList.add("Jalgaon");
        CityNameList.add("Jalna");
        CityNameList.add("Jammu");
        CityNameList.add("Jamnagar");
        CityNameList.add("Jamshedpur");
        CityNameList.add("Jhansi");
        CityNameList.add("Jodhpur");
        CityNameList.add("Junagadh");
        CityNameList.add("Kadapa");
        CityNameList.add("Kakinada");
        CityNameList.add("Kalyan-Dombivali");
        CityNameList.add("Kamarhati");
        CityNameList.add("Kanpur");
        CityNameList.add("Karawal Nagar");
        CityNameList.add("Karimnagar");
        CityNameList.add("Karnal");
        CityNameList.add("Katihar");
        CityNameList.add("Khammam");
        CityNameList.add("Kirari Suleman Nagar");
        CityNameList.add("Kochi");
        CityNameList.add("Kolhapur");
        CityNameList.add("Kollam");
        CityNameList.add("Korba");
        CityNameList.add("Kota");
        CityNameList.add("Kozhikode");
        CityNameList.add("Kulti");
        CityNameList.add("Kurnool");
        CityNameList.add("Latur");
        CityNameList.add("London");
        CityNameList.add("Loni");
        CityNameList.add("Lucknow");
        CityNameList.add("Ludhiana");
        CityNameList.add("Madurai");
        CityNameList.add("Maheshtala");
        CityNameList.add("Malegaon");
        CityNameList.add("Mangalore");
        CityNameList.add("Mango");
        CityNameList.add("Mathura");
        CityNameList.add("Mau");
        CityNameList.add("Meerut");
        CityNameList.add("Mira-Bhayandar");
        CityNameList.add("Mirzapur");
        CityNameList.add("Moradabad");
        CityNameList.add("Muzaffarnagar");
        CityNameList.add("Muzaffarpur");
        CityNameList.add("Mysore");
        CityNameList.add("Nagercoil");
        CityNameList.add("Nagpur");
        CityNameList.add("Nanded");
        CityNameList.add("Nashik");
        CityNameList.add("Navi Mumbai");
        CityNameList.add("Nellore");
        CityNameList.add("New Delhi");
        CityNameList.add("Nizamabad");
        CityNameList.add("Noida");
        CityNameList.add("North Dumdum");
        CityNameList.add("Ozhukarai");
        CityNameList.add("Pali");
        CityNameList.add("Panihati");
        CityNameList.add("Panipat");
        CityNameList.add("Parbhani");
        CityNameList.add("Patiala");
        CityNameList.add("Patna");
        CityNameList.add("Pimpri-Chinchwad");
        CityNameList.add("Puducherry");
        CityNameList.add("Pune");
        CityNameList.add("Purnia");
        CityNameList.add("Raichur");
        CityNameList.add("Raipur");
        CityNameList.add("Rajahmundry");
        CityNameList.add("Rajkot");
        CityNameList.add("Rajpur Sonarpur");
        CityNameList.add("Rampur");
        CityNameList.add("Ranchi");
        CityNameList.add("Ratlam");
        CityNameList.add("Rewa");
        CityNameList.add("Rohtak");
        CityNameList.add("Rourkela");
        CityNameList.add("Sagar");
        CityNameList.add("Saharanpur");
        CityNameList.add("Salem");
        CityNameList.add("Sangli-Miraj & Kupwad");
        CityNameList.add("Satna");
        CityNameList.add("Shahjahanpur");
        CityNameList.add("Shivamogga (Shimoga)");
        CityNameList.add("Sikar");
        CityNameList.add("Siliguri");
        CityNameList.add("Solapur");
        CityNameList.add("Sonipat");
        CityNameList.add("South Dumdum");
        CityNameList.add("Sri Ganganagar");
        CityNameList.add("Srinagar");
        CityNameList.add("Surat");
        CityNameList.add("Thane");
        CityNameList.add("Thanjavur");
        CityNameList.add("Thiruvananthapuram");
        CityNameList.add("Thoothukudi");
        CityNameList.add("Thrissur");
        CityNameList.add("Tiruchirappalli");
        CityNameList.add("Tirunelveli");
        CityNameList.add("Tirupati");
        CityNameList.add("Tirupur");
        CityNameList.add("Tiruvottiyur");
        CityNameList.add("Tumkur");
        CityNameList.add("Udaipur");
        CityNameList.add("Ujjain");
        CityNameList.add("Ulhasnagar");
        CityNameList.add("Vadodara");
        CityNameList.add("Varanasi");
        CityNameList.add("Vasai-Virar");
        CityNameList.add("Vijayawada");
        CityNameList.add("Visakhapatnam");
        CityNameList.add("Vizianagaram");
        CityNameList.add("Warangal");
        Singleton.GetInstance().setSelectCityName(CityNameList);
    }

    private void CheckGPSEnable() {
        if (utilityClass.isInternetConnection()) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                try {

                    Singleton.GetInstance().setLatitude(gpsTracker.getLatitude());
                    Singleton.GetInstance().setLongitude(gpsTracker.getLongitude());
                } catch (NullPointerException e) {
                    Log.e("Location Null : ", e.toString());
                }
                Singleton.GetInstance().setHomeMenuFragmentPosition(Singleton.GetInstance().getCurrentPosition());
                Singleton.GetInstance().FragmentCall(16, "");
                Singleton.GetInstance().MenuHide();
                ivSearchHome.setVisibility(View.GONE);

            } else {
                utilityClass.showGPSDisabledAlertToUser();
            }
        } else {
            utilityClass.toast("Please check your internet settings!!!");
        }
    }


    /* private class GeocoderHandler extends Handler {
         TextView cityName;

         public GeocoderHandler(TextView cityName) {
             this.cityName = cityName;
         }

         @Override
         public void handleMessage(Message message) {
             String locationAddress;
             switch (message.what) {
                 case 1:
                     Bundle bundle = message.getData();
                     locationAddress = bundle.getString("address");
                     break;
                 default:
                     locationAddress = null;
             }
             if (locationAddress.equalsIgnoreCase("all")) {
                 utilityClass.processDialogStop();
                 utilityClass.toast("Location can't found");
             } else {
                 utilityClass.processDialogStop();
                 SaveSharedPreferences.setCityName(HomeActivity.this, locationAddress);
                 cityName.setText(locationAddress);
             }

         }
     }*/
    public void getLocationCityName(final EditText cityName, String latlng) {
        restAdapter = new RestAdapter.Builder()
                .setEndpoint(ConstantClass.googleEndPoints)
                .build();
        api = restAdapter.create(RestServices.class);
        Log.d("Old Location City ", SaveSharedPreferences.getCityName(this));
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

                                    SaveSharedPreferences.setCityName(HomeActivity.this, component.getString("long_name"));
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

    public void getCurrentCityName(String latlng) {
        restAdapter = new RestAdapter.Builder()
                .setEndpoint(ConstantClass.googleEndPoints)
                .build();
        api = restAdapter.create(RestServices.class);
        Log.d("Old Location City ", SaveSharedPreferences.getCityName(this));
        api.getCityName(latlng, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject result, Response response) {
                utilityClass.processDialogStop();
                Log.d("Current Location Result", result.toString());
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

                                    if (component.getString("long_name").equalsIgnoreCase(SaveSharedPreferences.getCityName(HomeActivity.this))) {

                                    } else {
                                        SaveSharedPreferences.setCityName(HomeActivity.this, component.getString("long_name"));
                                        tvSearchCity.setText(component.getString("long_name"));
                                        Singleton.GetInstance().setCurrentPosition(Singleton.GetInstance().getCurrentPosition() - 1);
                                        Singleton.GetInstance().FragmentCall(Singleton.GetInstance().getCurrentPosition() + 1, "");
                                    }
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

    public void getCategoryRestService() {
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Content-Type", "application/json");
                request.addHeader("X-CSRF-Token", SaveSharedPreferences.getTokon(HomeActivity.this));
                request.addHeader("Cookie", SaveSharedPreferences.getCookie(HomeActivity.this));

                Log.i("Category Tokon : ", SaveSharedPreferences.getTokon(HomeActivity.this));
                Log.i("Category Cookie : ", SaveSharedPreferences.getCookie(HomeActivity.this));
            }
        };

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(ConstantClass.endPoints)
                .setRequestInterceptor(requestInterceptor)
                .build();

        api = restAdapter.create(RestServices.class);
        Log.e("Category Name: ", Singleton.GetInstance().getDealType());
        api.getCategory(new Callback<JsonObject>() {
            @Override
            public void success(JsonObject result, Response response) {
                utilityClass.processDialogStop();
                ArrayList<HashMap<String, String>> LVHMDealCategory = new ArrayList<HashMap<String, String>>();
                HashMap<String, String> HMDealCategory = new HashMap<String, String>();
                ArrayList<String> CatList = new ArrayList<String>();
                Log.e("Category Type : ", result.toString());
                HMDealCategory.put("name", "All");
                HMDealCategory.put("ID", "all");
                LVHMDealCategory.add(HMDealCategory);
                CatList.add("All");
                try {

                    JSONArray jsonArrayNew = new JSONArray(result.getAsJsonArray("nodes").toString());
                    for (int i = 0; i < jsonArrayNew.length(); i++) {
                        HMDealCategory = new HashMap<String, String>();
                        CatList.add(jsonArrayNew.getJSONObject(i).getJSONObject("node").getString("name"));
                        HMDealCategory.put("name", jsonArrayNew.getJSONObject(i).getJSONObject("node").getString("name"));
                        HMDealCategory.put("ID", jsonArrayNew.getJSONObject(i).getJSONObject("node").getString("term_id"));
                        LVHMDealCategory.add(HMDealCategory);
                    }
                    Singleton.GetInstance().setCategoryListHome(CatList);
                    Singleton.GetInstance().setHMCategoryHome(LVHMDealCategory);
                    //  catAdapter.notifyDataSetChanged();
                    catAdapter = new ArrayAdapter<String>(HomeActivity.this, android.R.layout.simple_spinner_item, Singleton.GetInstance().getCategoryListHome());
                    // catAdapter.setDropDownViewResource(R.layout.city_cell);
                    spDealsCategoryHome.setAdapter(catAdapter);
                } catch (IllegalStateException e) {
                    Log.e("IllegalStateException:", e.toString());
                } catch (JSONException e) {
                    Log.e("JSONException:", e.toString());
                }
            }

            @Override
            public void failure(RetrofitError error) {
                utilityClass.processDialogStop();
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
                setDefaultCat();
            }
        });
    }



    @Override
    public void onClick(View v) {
        switch (ShowCasecounter) {
            case 0:
                showcaseView.setShowcase(new ViewTarget((ImageView) findViewById(R.id.rightMenuHome)), true);
                showcaseView.setContentTitle("Navigation Menu");
                //  showcaseView.setContentText("Navigation Menu");
                ShowCasecounter++;
                break;
            case 1:
                showcaseView.setShowcase(new ViewTarget((ImageView) findViewById(R.id.ivPostADealHome)), true);
                showcaseView.setContentTitle("You can add a new deal from the + button");
                showcaseView.setButtonText("Finish");
                //  showcaseView.setContentText("You can add a new deal from the + button");
                ShowCasecounter++;
                break;
            case 2:
                showcaseView.setVisibility(View.GONE);
                showcaseView.hide();
                shackDialogShow();
                break;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View view = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (view instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

            if (event.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom())) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
    }
    public void setDefaultCat(){
        utilityClass.getDefaultCategoryHome();
        catAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Singleton.GetInstance().getCategoryListHome());
        spDealsCategoryHome.setAdapter(catAdapter);
    }
}
