package com.wots.lutmaar.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.wots.lutmaar.Activity.LoginActivity;
import com.wots.lutmaar.Adapter.NavDrawerListAdapter;
import com.wots.lutmaar.GetterSetter.NavDrawerItem;
import com.wots.lutmaar.Interface.RestServices;
import com.wots.lutmaar.R;
import com.wots.lutmaar.UtilClass.ConstantClass;
import com.wots.lutmaar.UtilClass.SaveSharedPreferences;
import com.wots.lutmaar.UtilClass.Singleton;
import com.wots.lutmaar.UtilClass.UtilityClass;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class FragmentNavigationDrawer extends DrawerLayout {

    private ActionBarDrawerToggle drawerToggle;
    private ListView lvDrawer;
    private Toolbar toolbar;
    private TextView HeaderToolbarHome;
    private LinearLayout LLRightDrawer, LLLeftDrawer;
    //  private ListView lvMenu;
    private NavDrawerListAdapter drawerAdapter;
    private ArrayList<NavDrawerItem> navDrawerItems;
    private ArrayList<FragmentNavItem> drawerNavItems;
    private int drawerContainerRes;
    Boolean lvPressMenu = true;
    UtilityClass utilityClass;
    Context context;
    ConstantClass constantClass;

    //Interface
    RestServices api;
    RestAdapter restAdapter;

    //Variable
    private int NetworkErrorCount = 0;


    public FragmentNavigationDrawer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public FragmentNavigationDrawer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FragmentNavigationDrawer(Context context) {
        super(context);
    }

    public void setupDrawerConfiguration(ListView drawerListView, Toolbar drawerToolbar,
                                         LinearLayout LL, LinearLayout LLLeftDrawer, TextView Header, Context context, int drawerItemRes, int drawerContainerResId) {

        this.context = context;
        utilityClass = new UtilityClass(context);
        // Setup navigation items array
        drawerNavItems = new ArrayList<FragmentNavItem>();
        navDrawerItems = new ArrayList<NavDrawerItem>();
        drawerContainerRes = drawerContainerResId;
        // Setup drawer list view
        lvDrawer = drawerListView;
        toolbar = drawerToolbar;
        LLRightDrawer = LL;
        this.LLLeftDrawer = LLLeftDrawer;
        //  this.lvMenu = lvMenu;
        HeaderToolbarHome = Header;
        // Setup item listener
        lvDrawer.setOnItemClickListener(new FragmentDrawerItemListener());
        // lvDrawer.setSelector(R.drawable.list_bg);
        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        drawerToggle = setupDrawerToggle();
        setDrawerListener(drawerToggle);
        //drawerToggle.onDrawerClosed(null);
        drawerToggle.setDrawerIndicatorEnabled(false);

        // Setup action buttons
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    // addNavItem("First", R.mipmap.ic_one, "First Fragment", FirstFragment.class)
    public void addNavItem(String navTitle, int icon, String windowTitle, Class<? extends Fragment> fragmentClass) {
        // adding nav drawer items to array
        navDrawerItems.add(new NavDrawerItem(navTitle, icon));
        // Set the adapter for the list view
        drawerAdapter = new NavDrawerListAdapter(getActivity(), "Right", navDrawerItems);
        lvDrawer.setAdapter(drawerAdapter);
        drawerNavItems.add(new FragmentNavItem(windowTitle, fragmentClass));
    }

    public void addNavItemLeft(String windowTitle, Class<? extends Fragment> fragmentClass) {

        drawerNavItems.add(new FragmentNavItem(windowTitle, fragmentClass));
    }

    /**
     * Swaps fragments in the main content view
     */
    public void selectDrawerItem(int position, String arg) {
        // Create a new fragment and specify the planet to show based on
        // position
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Bundle args = new Bundle();
        Fragment fragment = null;
        int CurruntPosion = Singleton.GetInstance().getCurrentPosition();

        if (position == 8 || position == 9 || position == 10 || position == 11 || position == 12) {
            Singleton.GetInstance().setCurrentPosition(position);
            Singleton.GetInstance().FragmentCall(7, String.valueOf(position - 7));
        } else {

            if (CurruntPosion != position) {

                FragmentNavItem navItem = drawerNavItems.get(position);

                try {
                    fragment = navItem.getFragmentClass().newInstance();
                    args = navItem.getFragmentArgs();
                    if (args == null) {
                        args = new Bundle();
                        args.putString("DealID", arg);
                        fragment.setArguments(args);
                    } else {
                        args.putString("DealID", arg);
                        fragment.setArguments(args);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(drawerContainerRes, fragment).commit();

                // Highlight the selected item, update the title, and close the drawer

                lvDrawer.setItemChecked(position, true);

                setTitle(navItem.getTitle());


                if (position == 0 || position == 1 || position == 2 || position == 4 || position == 5) {
                    Singleton.GetInstance().BottomMenuHide();
                } else if (position == 3) {
                    Singleton.GetInstance().setHomeMenuFragmentPosition(Singleton.GetInstance().getCurrentPosition());
                    Singleton.GetInstance().MenuHide();
                }
                if (position == 7) {
                    Singleton.GetInstance().MenuShow();
                    //   Singleton.GetInstance().getLLCitySearch().setVisibility(VISIBLE);
                } else {
                    Singleton.GetInstance().getLLCitySearch().setVisibility(GONE);
                }
                Singleton.GetInstance().setCurrentPosition(position);

            }
        }
        closeDrawer(LLRightDrawer);
        closeDrawer(LLLeftDrawer);

    }


    public ActionBarDrawerToggle getDrawerToggle() {
        return drawerToggle;
    }

    public int getDrawerContainerRes() {
        return drawerContainerRes;
    }


    private FragmentActivity getActivity() {
        return (FragmentActivity) getContext();
    }

    private ActionBar getSupportActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    private void setTitle(CharSequence title) {
        HeaderToolbarHome.setText(title.toString());
        getSupportActionBar().setTitle(title);
    }

    private class FragmentDrawerItemListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            view.setSelected(true);
            if (drawerNavItems.get(position).getTitle().equals("Log Out")) {
                utilityClass.processDialogStart();
                NetworkErrorCount = 0;
                restService();
            } else {
                if (position == 3) {
                    selectDrawerItem(position, "0");
                } else {
                    selectDrawerItem(position, "");
                }
                lvPressMenu = false;
            }
        }
    }

    private class FragmentNavItem {
        private Class<? extends Fragment> fragmentClass;
        private String title;
        private Bundle fragmentArgs;

        public FragmentNavItem(String title, Class<? extends Fragment> fragmentClass) {
            this(title, fragmentClass, null);
        }

        public FragmentNavItem(String title, Class<? extends Fragment> fragmentClass, Bundle args) {
            this.fragmentClass = fragmentClass;
            this.fragmentArgs = args;
            this.title = title;
        }

        public Class<? extends Fragment> getFragmentClass() {
            return fragmentClass;
        }

        public String getTitle() {
            return title;
        }

        public Bundle getFragmentArgs() {
            return fragmentArgs;
        }
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(getActivity(), this, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    }

    public boolean isDrawerOpen() {
        boolean checkOpen = false;
        if (isDrawerOpen(LLLeftDrawer))
            checkOpen = isDrawerOpen(LLLeftDrawer);
        else if (isDrawerOpen(LLRightDrawer))
            checkOpen = isDrawerOpen(LLRightDrawer);
        return checkOpen;

    }

    public void closeLLDrawer() {
        closeDrawer(LLRightDrawer);
        closeDrawer(LLLeftDrawer);
    }

    private void restService() {

        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Content-Type", "application/json");
                request.addHeader("X-CSRF-Token", SaveSharedPreferences.getTokon(context));
                request.addHeader("Cookie", SaveSharedPreferences.getCookie(context));

                // request.addHeader("User-Agent", "Android");

                Log.i("Tokon : ", SaveSharedPreferences.getTokon(context));
                Log.i("Cookie : ", SaveSharedPreferences.getCookie(context));
            }
        };

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(constantClass.endPoints)
                .setRequestInterceptor(requestInterceptor)
                .build();

        api = restAdapter.create(RestServices.class);

        api.LogOut(new Callback<Object>() {
            @Override
            public void success(Object result, Response response) {
                utilityClass.processDialogStop();
                NetworkErrorCount = 0;
                Log.i("User logout Res.  OBJ :", String.valueOf(result));
                if (String.valueOf(result).equalsIgnoreCase("[true]")) {
                    utilityClass.toast("User logout success");
                    Log.i("User logout Success :  ", String.valueOf(result));
                    SaveSharedPreferences.cleardata(context);
                    Intent loginIntent = new Intent(context, LoginActivity.class);
                    context.startActivity(loginIntent);
                    //getActivity().finish();
                    Singleton.GetInstance().setCurrentPosition(-1);

                } else {
                    utilityClass.toast("User can not logout success");
                    Log.i("User logout Response :", String.valueOf(result));
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
                } else {
                    utilityClass.processDialogStop();
                    if (error.toString().contains("No address associated with hostname")) {
                        utilityClass.toast("Please check your network connection or try again later.");
                    } else if (error.toString().equals("retrofit.RetrofitError: 500 Internal Server Error")) {
                        utilityClass.toast("Internal Server Error");
                    } else if (error.toString().contains("com.google.gson.JsonSyntaxException")) {
                        utilityClass.toast("Result not found form server");
                    } else if (error.toString().contains("User is not logged in")) {
                        SaveSharedPreferences.cleardata(context);
                        Intent loginIntent = new Intent(context, LoginActivity.class);
                        context.startActivity(loginIntent);
                        //getActivity().finish();
                        Singleton.GetInstance().setCurrentPosition(-1);
                        utilityClass.toast("User logout success");
                    } else {
                        utilityClass.toast("User can not logout");
                    }
                }
            }
        });
    }
}