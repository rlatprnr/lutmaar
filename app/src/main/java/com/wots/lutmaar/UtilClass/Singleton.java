package com.wots.lutmaar.UtilClass;


import android.app.Dialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.wots.lutmaar.Fragment.FragmentNavigationDrawer;
import com.wots.lutmaar.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Bhavesh on 05-03-2015.
 */
public class Singleton {
    private static Singleton mInstance = null;

    public static Singleton GetInstance() {
        if (mInstance == null)
            mInstance = new Singleton();
        return mInstance;
    }

    FragmentNavigationDrawer dlDrawerFragment;
    LinearLayout LLBottomMenu, LLBottomMenuOvel, LLCitySearch;
    ImageView overFlowMenu, ivMainSearch,ivMessage;
    ListView lvManu;
    int OverFlowCheck = 1;
    int OnOffCheck = 1;
    int FragmentShake = 1;
    int ScreenHeight;
    double latitude = 0.0;
    double longitude = 0.0;
    String firstDate,SearchResultJsonQuery;
    Boolean checkRotation;
    Boolean startShake = false;
    Boolean WelcomeFirst = false;
    TextView ImgUpload, TvMainHeading, TvDisplayCityName,tvMessage;
    Spinner spDealCategoryName;
    int CurrentPosition = -1;
    int HomeMenuFragmentPosition = -1;
    String imgPath, imgName, imgFile;
    JSONObject jsonObject;
    ArrayList<String> SelectCityName = new ArrayList<String>(), CategoryList = new ArrayList<String>(), CategoryListHome = new ArrayList<String>();
    ArrayList<HashMap<String, String>> HMCategory = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> HMCategoryHome = new ArrayList<HashMap<String, String>>();
    Dialog dialog;
    String DealType = "all",DealSubCategory="all";


    ArrayList<HashMap<String, String>> HMimgFileList = new ArrayList<HashMap<String, String>>();


    public void setMenu(LinearLayout LLBottomMenu1, LinearLayout LLBottomMenuOvel1, ImageView overFlowMenu1, ListView lvManu1) {
        LLBottomMenu = LLBottomMenu1;
        LLBottomMenuOvel = LLBottomMenuOvel1;
        overFlowMenu = overFlowMenu1;
        lvManu = lvManu1;
    }

    public void MenuHide() {
        LLBottomMenu.setVisibility(View.GONE);
        LLBottomMenuOvel.setVisibility(View.GONE);
        lvManu.setEnabled(false);
        OverFlowCheck = 2;
        startShake = false;
        FragmentShake = 2;
        LLCitySearch.setVisibility(View.GONE);
        overFlowMenu.setImageResource(R.drawable.ic_backbotton);
    }

    public void MenuShow() {
        overFlowMenu.setImageResource(R.drawable.ic_ovewflowmenu);
        LLBottomMenu.setVisibility(View.VISIBLE);
        LLBottomMenuOvel.setVisibility(View.VISIBLE);
        lvManu.setEnabled(true);
        OverFlowCheck = 1;
        FragmentShake = 1;
        startShake = false;

    }

    public void LeftMenuHide() {
        lvManu.setEnabled(false);
        OverFlowCheck = 2;
        startShake = false;
        FragmentShake = 2;
        overFlowMenu.setImageResource(R.drawable.ic_backbotton);
    }

    public void BottomMenuHide() {
        LLBottomMenu.setVisibility(View.GONE);
        LLBottomMenuOvel.setVisibility(View.GONE);
        lvManu.setEnabled(true);
        OverFlowCheck = 1;
        startShake = false;
        FragmentShake = 2;
        LLCitySearch.setVisibility(View.GONE);
        overFlowMenu.setImageResource(R.drawable.ic_ovewflowmenu);
    }

    public Boolean getWelcomeFirst() {
        return WelcomeFirst;
    }

    public void setWelcomeFirst(Boolean welcomeFirst) {
        WelcomeFirst = welcomeFirst;
    }

    public ImageView getIvMainSearch() {
        return ivMainSearch;
    }

    public void setIvMainSearch(ImageView tvMainSearch) {
        this.ivMainSearch = tvMainSearch;
    }

    public TextView getTvMainHeading() {
        return TvMainHeading;
    }

    public void setTvMainHeading(TextView tvMainHeading) {
        TvMainHeading = tvMainHeading;
    }

    public int getScreenHeight() {
        return ScreenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        ScreenHeight = screenHeight;
    }

    public Boolean getStartShake() {
        return startShake;
    }

    public void setStartShake(Boolean startShake) {
        this.startShake = startShake;
    }

    public int getFragmentShake() {
        return FragmentShake;
    }

    public void setFragmentShake(int fragmentShake) {
        FragmentShake = fragmentShake;
    }

    public void FragmentCall(int FragmentID, String DealID) {
        dlDrawerFragment.selectDrawerItem(FragmentID, DealID);
    }

    public FragmentNavigationDrawer getDlDrawerFragment() {
        return dlDrawerFragment;
    }

    public void setDlDrawerFragment(FragmentNavigationDrawer dlDrawerFragment) {
        this.dlDrawerFragment = dlDrawerFragment;
    }

    public int getOnOffCheck() {
        return OnOffCheck;
    }

    public void setOnOffCheck(int onOffCheck) {
        OnOffCheck = onOffCheck;
    }

    public int getOverFlowCheck() {
        return OverFlowCheck;
    }

    public void setOverFlowCheck(int overFlowCheck) {
        OverFlowCheck = overFlowCheck;
    }

    public String getFirstDate() {
        return firstDate;
    }

    public void setFirstDate(String firstDate) {
        this.firstDate = firstDate;
    }



    public int getCurrentPosition() {
        return CurrentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        CurrentPosition = currentPosition;
    }

    public int getHomeMenuFragmentPosition() {
        return HomeMenuFragmentPosition;
    }

    public void setHomeMenuFragmentPosition(int homeMenuFragmentPosition) {
        HomeMenuFragmentPosition = homeMenuFragmentPosition;
    }

    public ArrayList<String> getSelectCityName() {
        return SelectCityName;
    }

    public void setSelectCityName(ArrayList<String> selectCityName) {
        SelectCityName = selectCityName;
    }

    public TextView getTvDisplayCityName() {
        return TvDisplayCityName;
    }

    public void setTvDisplayCityName(TextView tvDisplayCityName) {
        TvDisplayCityName = tvDisplayCityName;
    }

    public Spinner getTvDealCategoryName() {
        return spDealCategoryName;
    }

    public void setTvDealCategoryName(Spinner SpDealCategoryName) {
        spDealCategoryName = SpDealCategoryName;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Dialog getDialog() {
        return dialog;
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    public LinearLayout getLLCitySearch() {
        return LLCitySearch;
    }

    public void setLLCitySearch(LinearLayout LLCitySearch) {
        this.LLCitySearch = LLCitySearch;
    }

    public ArrayList<HashMap<String, String>> getHMCategory() {
        return HMCategory;
    }

    public void setHMCategory(ArrayList<HashMap<String, String>> HMCategory) {
        //   AllHMCategory.put(DealType, HMCategory);
        this.HMCategory = HMCategory;
    }

    public ArrayList<String> getCategoryList() {
        return CategoryList;
    }

    public void setCategoryList(ArrayList<String> categoryList) {
        CategoryList = categoryList;
    }

    public ArrayList<String> getCategoryListHome() {
        return CategoryListHome;
    }

    public void setCategoryListHome(ArrayList<String> categoryListHome) {
        CategoryListHome = categoryListHome;
    }

    public ArrayList<HashMap<String, String>> getHMCategoryHome() {
        return HMCategoryHome;
    }

    public void setHMCategoryHome(ArrayList<HashMap<String, String>> HMCategoryHome) {
        this.HMCategoryHome = HMCategoryHome;
    }

    public String getDealType() {
        return DealType;
    }

    public void setDealType(String dealType) {
        DealType = dealType;
    }



    public ArrayList<HashMap<String, String>> getHMimgFileList() {
        return HMimgFileList;
    }

    public void setHMimgFileList(ArrayList<HashMap<String, String>> HMimgFileList) {
        this.HMimgFileList = HMimgFileList;
    }

    public String getDealSubCategory() {
        return DealSubCategory;
    }

    public void setDealSubCategory(String dealSubCategory) {
        DealSubCategory = dealSubCategory;
    }

    public String getSearchResultJsonQuery() {
        return SearchResultJsonQuery;
    }

    public void setSearchResultJsonQuery(String searchResultJsonQuery) {
        SearchResultJsonQuery = searchResultJsonQuery;
    }

    public ImageView getIvMessage() {
        return ivMessage;
    }

    public void setIvMessage(ImageView ivMessage) {
        this.ivMessage = ivMessage;
    }

    public TextView getTvMessage() {
        return tvMessage;
    }

    public void setTvMessage(TextView tvMessage) {
        this.tvMessage = tvMessage;
    }

    public void clearlist() {
       /* HMTrailerMovielist.clear();
        HMSongMovielist.clear();
        TrailerMovielist.clear();
        SongMovielist.clear();*/
    }
}
