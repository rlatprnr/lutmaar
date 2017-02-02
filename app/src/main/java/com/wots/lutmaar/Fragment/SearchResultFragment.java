package com.wots.lutmaar.Fragment;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.wots.lutmaar.Adapter.SearchResultAdapter;
import com.wots.lutmaar.Interface.RestServices;
import com.wots.lutmaar.R;
import com.wots.lutmaar.UtilClass.ConstantClass;
import com.wots.lutmaar.UtilClass.SaveSharedPreferences;
import com.wots.lutmaar.UtilClass.Singleton;
import com.wots.lutmaar.UtilClass.UtilityClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import fr.castorflex.android.circularprogressbar.CircularProgressDrawable;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SearchResultFragment extends Fragment {

    //Class
    UtilityClass utilityClass;
    ConstantClass constantClass;
    SearchResultAdapter adapter;
    JSONArray jsonArray, jsonArrayShort;
    JSONObject DealsObject, jsonObject;
    ArrayAdapter<String> catAdapter;

    //View
    @InjectView(R.id.gv_SearchResult)
    GridView gv_SearchResult;
    @InjectView(R.id.pbDeals)
    SmoothProgressBar pbDeals;
    @InjectView(R.id.pbRoundDeals)
    CircularProgressBar pbRoundDeals;
    @InjectView(R.id.tvCountSearchResult)
    TextView tvCountSearchResult;
    @InjectView(R.id.spDealsCategorySearchResult)
    Spinner spDealsCategorySearchResult;
    @InjectView(R.id.tvShortingType)
    TextView tvShortingType;
    @InjectView(R.id.LLSortSearchResult)
    LinearLayout LLSortSearchResult;


    //Interface
    RestServices api;
    RestAdapter restAdapter;

    //Variable
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String DealJSONObject;
    String dealType;
    String DealIDOnject;
    int RadioPosion = 0, NetworkErrorCount = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        DealJSONObject = getArguments().getString("DealID");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.search_result_fragment, container, false);
        ButterKnife.inject(this, view);

        //  utilityClass = new UtilityClass(getActivity());

        declaration();


        initialization();

        return view;
    }

    private void declaration() {
        utilityClass = new UtilityClass(getActivity());
        if (Singleton.GetInstance().getHMCategoryHome().size() <= 0) {
            utilityClass.getDefaultCategoryHome();
        }
        catAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, Singleton.GetInstance().getCategoryListHome());
        spDealsCategorySearchResult.setAdapter(catAdapter);


        pbDeals.progressiveStop();
        ((CircularProgressDrawable) pbRoundDeals.getIndeterminateDrawable()).progressiveStop();
        try {
            if (!DealJSONObject.equalsIgnoreCase("")) {
                jsonArray = new JSONArray(DealJSONObject);
                jsonArrayShort = jsonArray;
                dealType = jsonArrayShort.getJSONObject(0).optString("field_cat");
                tvCountSearchResult.setText("Showing " + jsonArray.length() + " Result");
                adapter = new SearchResultAdapter(getActivity(), jsonArrayShort);
                gv_SearchResult.setAdapter(adapter);
                pbDeals.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Json Converter Error:", e.toString());
        }
        //  Singleton.GetInstance().getHMCategory().clear();
    }

    private void initialization() {

        spDealsCategorySearchResult.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    utilityClass.processDialogStart();
                    NetworkErrorCount = 0;
                    restServiceNewCategoryWise();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    @OnClick(R.id.LLSortSearchResult)
    public void setLLSortSearchResult() {
        SortDialog();
    }

    @OnItemClick(R.id.gv_SearchResult)
    public void setGv_SearchResult(int position) {

        try {
            dealType = jsonArrayShort.getJSONObject(position).optString("field_cat");
            DealIDOnject = jsonArrayShort.getJSONObject(position).optString("nid");
        } catch (JSONException e) {
            Log.e("JsonError: ", e.toString());
        }
        restService(dealType, DealIDOnject);
    }


    private void DealDialogShow() {
        final Dialog dialog = new Dialog(getActivity());// android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.map_deals_cell);
        ImageView ivSearchDealDetailsImageViewCancelPopup = (ImageView) dialog.findViewById(R.id.ivSearchDealDetailsImageViewCancelPopup);
        TextView tvSearchDealTitlePopup = (TextView) dialog.findViewById(R.id.tvSearchDealTitlePopup);
        TextView tvSearchDealDetailsPopup = (TextView) dialog.findViewById(R.id.tvSearchDealDetailsPopup);
        ImageView ivSearchDealsPopup = (ImageView) dialog.findViewById(R.id.ivSearchDealsPopup);
        ImageView tvSearchDealGetPopup = (ImageView) dialog.findViewById(R.id.tvSearchDealGetPopup);
        ImageView ivSearchDealDetailsHotPopup = (ImageView) dialog.findViewById(R.id.ivSearchDealDetailsHotPopup);
        ImageView ivSearchDealDetailsColdPopup = (ImageView) dialog.findViewById(R.id.ivSearchDealDetailsColdPopup);
        TextView tvSearchDealDetailsShare = (TextView) dialog.findViewById(R.id.tvSearchDealDetailsShare);
        TextView tvSearchDealDetailsReviewPointsPopup = (TextView) dialog.findViewById(R.id.tvSearchDealDetailsReviewPointsPopup);
        Button btnGoToDealDetails = (Button) dialog.findViewById(R.id.btnGoToDealDetails);
        TextView tvSearchDealReviewsPopup = (TextView) dialog.findViewById(R.id.tvSearchDealReviewsPopup);
        TextView tvSearchDealDetailsHotColdValuePopupPopup = (TextView) dialog.findViewById(R.id.tvSearchDealDetailsHotColdValuePopupPopup);
        TextView tvSearchDealPricePopup = (TextView) dialog.findViewById(R.id.tvSearchDealPricePopup);
        TextView tvSearchDealTimePopup = (TextView) dialog.findViewById(R.id.tvSearchDealTimePopup);
        TextView tvSearchDealCommentPopup = (TextView) dialog.findViewById(R.id.tvSearchDealCommentPopup);


        try {
            JSONArray arrayImage = DealsObject.getJSONArray("field_images");
            Picasso.with(getActivity()).load(arrayImage.getJSONObject(0).optString("src"))
                    .placeholder(R.mipmap.ic_launcher).noFade().resize(240, 260).centerInside().into(ivSearchDealsPopup);
        } catch (JSONException e) {
            Log.e("JSON Image ArrayError", e.toString());
            if (e.toString().contains("field_images of type org.json.JSONObject cannot be converted to JSONArray")) {
                try {
                    JSONObject jsonObject1 = DealsObject.getJSONObject("field_images");
                    Picasso.with(getActivity()).load(jsonObject1.optString("src"))
                            .placeholder(R.mipmap.ic_launcher).noFade().resize(240, 260).centerInside().into(ivSearchDealsPopup);
                } catch (JSONException e1) {
                    Log.e("JSON Image ObjectError", e.toString());
                }
            }
        }
        tvSearchDealTitlePopup.setText(DealsObject.optString("title"));
        tvSearchDealDetailsPopup.setText(DealsObject.optString("body"));

        tvSearchDealReviewsPopup.setText(DealsObject.optString("comment_count") + " Reviews");
        tvSearchDealDetailsHotColdValuePopupPopup.setText(DealsObject.optString("value") + "o");
        tvSearchDealPricePopup.setText("$" + DealsObject.optString("field_price"));
        tvSearchDealCommentPopup.setText(DealsObject.optString("comment_count") + " Comments");
        tvSearchDealTimePopup.setText(DealsObject.optString("created"));

        btnGoToDealDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Singleton.GetInstance().setHomeMenuFragmentPosition(7);
                Singleton.GetInstance().FragmentCall(13, DealsObject.toString());
                Singleton.GetInstance().MenuHide();
                dialog.dismiss();
            }
        });
        ivSearchDealDetailsImageViewCancelPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void restServiceNewCategoryWise() {
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Content-Type", "application/json");
                request.addHeader("X-CSRF-Token", SaveSharedPreferences.getTokon(getActivity()));
                request.addHeader("Cookie", SaveSharedPreferences.getCookie(getActivity()));

                Log.i("Search Result Tokon : ", SaveSharedPreferences.getTokon(getActivity()));
                Log.i("Search Result Cookie : ", SaveSharedPreferences.getCookie(getActivity()));
            }
        };

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(ConstantClass.endPoints)
                .setRequestInterceptor(requestInterceptor)
                .build();

        api = restAdapter.create(RestServices.class);
        HashMap<String, String> HMSearchDeal = new HashMap<String, String>();
        try {
            jsonObject = new JSONObject(Singleton.GetInstance().getSearchResultJsonQuery());
            jsonObject.put("topic", spDealsCategorySearchResult.getSelectedItem().toString());
            HMSearchDeal = new Gson().fromJson(String.valueOf(jsonObject.toString()),
                    new TypeToken<HashMap<String, Object>>() {
                    }.getType());
        } catch (JSONException e) {
            Log.e("Json Error:", e.toString());
        } catch (NullPointerException e){
            Log.e("Null Pointer Error:", e.toString());
        }

        Log.i("Request image Data:", HMSearchDeal.toString());
        Singleton.GetInstance().setSearchResultJsonQuery(new Gson().toJson(HMSearchDeal).toString());
        api.getSearch(HMSearchDeal, new Callback<JsonArray>() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void success(JsonArray result, Response response) {
                utilityClass.processDialogStop();
                NetworkErrorCount = 0;
                Log.d("Searched Result : ", String.valueOf(result));
                jsonArrayShort = null;
                try {
                    jsonArrayShort = new JSONArray(result.getAsJsonArray().toString());
                } catch (JSONException e) {
                    Log.e("Json Error: ", e.toString());
                }
                adapter = new SearchResultAdapter(getActivity(), jsonArrayShort);
                gv_SearchResult.setAdapter(adapter);
                //  adapter.notifyDataSetChanged();

                // utilityClass.toast("Data found");
               /* Singleton.GetInstance().FragmentCall(19, String.valueOf(result));
                Singleton.GetInstance().MenuHide();
                Singleton.GetInstance().getIvMainSearch().setVisibility(View.GONE);*/

            }

            @Override
            public void failure(RetrofitError error) {
                utilityClass.processDialogStop();
                Log.i("Retrofit Error  : ", String.valueOf(error.toString() + " Network " + error.isNetworkError()));
                if (error.isNetworkError()) {
                    if (NetworkErrorCount >= 3) {
                        utilityClass.toast("Network error, please try again!!!.");
                        NetworkErrorCount = 0;
                    } else {
                        NetworkErrorCount++;
                        restServiceNewCategoryWise();
                    }
                } else if (error.toString().contains("No address associated with hostname")) {
                    utilityClass.toast("Please check your network connection or try again later.");
                } else if (error.toString().equals("retrofit.RetrofitError: 500 Internal Server Error")) {
                    utilityClass.toast("Internal Server Error");
                } else if (error.toString().contains("com.google.gson.JsonSyntaxException")) {
                    utilityClass.toast("Result not found form server");
                } else if (error.toString().contains("User is not logged in")) {
                    utilityClass.toast("User is not logged in");
                } else if (error.toString().contains("RetrofitError: 403 : Access denied")) {
                    utilityClass.toast("Access denied for this user");
                } else if (error.toString().contains("404 Not Found")) {
                    utilityClass.toast("Result not found!!!");
                } else {
                    utilityClass.toast("Something went wrong!!!");
                }
            }
        });
    }

    private void restService(String DealsType, String nid) {

        utilityClass.processDialogStart();
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
        String DealTypeServicePath;
        if (DealsType.equalsIgnoreCase("Deals")) {
            DealTypeServicePath = "deal-view-service.json";
        } else if (DealsType.equalsIgnoreCase("Vouchers")) {
            DealTypeServicePath = "voucher-view-service.json";
        } else if (DealsType.equalsIgnoreCase("Freebies")) {
            DealTypeServicePath = "freebee-view-service.json";
        } else if (DealsType.equalsIgnoreCase("Ask")) {
            DealTypeServicePath = "ask-view-service.json";
        } else if (DealsType.equalsIgnoreCase("Competitions")) {
            DealTypeServicePath = "competition-view-service.json";
        } else {
            DealTypeServicePath = "deal-view-service.json";
        }

        api.getDealDetails(DealTypeServicePath, nid, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject result, Response response) {
                utilityClass.processDialogStop();


                try {

                    JSONArray jsonArrayOne = new JSONArray(result.getAsJsonArray("nodes").toString());
                    DealsObject = jsonArrayOne.getJSONObject(0).getJSONObject("node");
                    DealDialogShow();

                } catch (IllegalStateException e) {
                    Log.e("IllegalStateException:", e.toString());
                } catch (JSONException e) {
                    Log.e("JSONException:", e.toString());
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


    @Override
    public void onResume() {
        super.onResume();
        Singleton.GetInstance().MenuHide();
        Singleton.GetInstance().getTvMainHeading().setText("Search Result");
        Singleton.GetInstance().getIvMainSearch().setVisibility(View.GONE);
    }

    private void SortDialog() {
        final Dialog dialog = new Dialog(getActivity());// android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.search_sort_cell);

        RadioGroup rgSearchShort = (RadioGroup) dialog.findViewById(R.id.rgSearchShort);
        RadioButton rBtnShortVoteValue = (RadioButton) dialog.findViewById(R.id.rBtnShortVoteValue);
        RadioButton rBtnShortByDate = (RadioButton) dialog.findViewById(R.id.rBtnShortByDate);
        RadioButton rBtnShortByReview = (RadioButton) dialog.findViewById(R.id.rBtnShortByReview);
        RadioButton rBtnShortPriceLowHigh = (RadioButton) dialog.findViewById(R.id.rBtnShortPriceLowHigh);
        RadioButton rBtnShortPriceHighLow = (RadioButton) dialog.findViewById(R.id.rBtnShortPriceHighLow);
        //    RadioButton rBtnShortDiscount = (RadioButton) dialog.findViewById(R.id.rBtnShortDiscount);
        if (!dealType.equalsIgnoreCase("Deals")) {
            rBtnShortPriceLowHigh.setVisibility(View.GONE);
            rBtnShortPriceHighLow.setVisibility(View.GONE);
        }
        if (RadioPosion == 0)
            rBtnShortVoteValue.setChecked(true);
        else if (RadioPosion == 1)
            rBtnShortByDate.setChecked(true);
        else if (RadioPosion == 2)
            rBtnShortByReview.setChecked(true);
        else if (RadioPosion == 3)
            rBtnShortPriceLowHigh.setChecked(true);
        else if (RadioPosion == 4)
            rBtnShortPriceHighLow.setChecked(true);
       /* else if (RadioPosion == 5)
            rBtnShortDiscount.setChecked(true);*/

        rgSearchShort.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rBtnShortVoteValue) {
                   // jsonArrayShort = jsonArray;
                    jsonArrayShort = sortHotColdJsonArray(jsonArrayShort, "Asce");
                    //  adapter.notifyDataSetChanged();
                    adapter = new SearchResultAdapter(getActivity(), jsonArrayShort);
                    gv_SearchResult.setAdapter(adapter);
                    pbDeals.setVisibility(View.GONE);
                    tvShortingType.setText("Sort by vote value");
                    RadioPosion = 0;
                } else if (checkedId == R.id.rBtnShortByDate) {
                    jsonArrayShort = sortDateJsonArray(jsonArrayShort, "desce");
                    // adapter.notifyDataSetChanged();
                    adapter = new SearchResultAdapter(getActivity(), jsonArrayShort);
                    gv_SearchResult.setAdapter(adapter);
                    pbDeals.setVisibility(View.GONE);
                    tvShortingType.setText("Sort by date");
                    RadioPosion = 1;
                } else if (checkedId == R.id.rBtnShortByReview) {
                    jsonArrayShort = sortReviewJsonArray(jsonArrayShort, "desce");
                    //adapter.notifyDataSetChanged();
                    adapter = new SearchResultAdapter(getActivity(), jsonArrayShort);
                    gv_SearchResult.setAdapter(adapter);
                    pbDeals.setVisibility(View.GONE);
                    tvShortingType.setText("Sort by reviews");
                    RadioPosion = 2;
                } else if (checkedId == R.id.rBtnShortPriceLowHigh) {
                    jsonArrayShort = sortJsonArrayPrice(jsonArrayShort, "Asce");
                    //  adapter.notifyDataSetChanged();
                    adapter = new SearchResultAdapter(getActivity(), jsonArrayShort);
                    gv_SearchResult.setAdapter(adapter);
                    pbDeals.setVisibility(View.GONE);
                    tvShortingType.setText("Price Low to high");
                    RadioPosion = 3;
                } else if (checkedId == R.id.rBtnShortPriceHighLow) {
                    jsonArrayShort = sortJsonArrayPrice(jsonArrayShort, "desce");
                    // adapter.notifyDataSetChanged();
                    adapter = new SearchResultAdapter(getActivity(), jsonArrayShort);
                    gv_SearchResult.setAdapter(adapter);
                    pbDeals.setVisibility(View.GONE);
                    tvShortingType.setText("Price high to low");
                    RadioPosion = 4;
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public JSONArray sortJsonArrayPrice(JSONArray array, final String AsceOrDesce) {
        List<JSONObject> jsons = new ArrayList<JSONObject>();
        try {

            for (int i = 0; i < array.length(); i++) {

                jsons.add(array.getJSONObject(i));

            }
            Collections.sort(jsons, new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject lhs, JSONObject rhs) {
                    String lid = null;
                    String rid = null;
                    lid = lhs.optString("field_price");
                    rid = rhs.optString("field_price");

                    // Here you could parse string id to integer and then compare.
                    if (AsceOrDesce.equalsIgnoreCase("desce"))
                        return rid.compareTo(lid);
                    return lid.compareTo(rid);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONArray(jsons);
    }

    public JSONArray sortDateJsonArray(JSONArray array, final String AsceOrDesce) {
        List<JSONObject> jsons = new ArrayList<JSONObject>();
        try {

            for (int i = 0; i < array.length(); i++) {

                jsons.add(array.getJSONObject(i));

            }
            Collections.sort(jsons, new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject lhs, JSONObject rhs) {
                    Date lid = null;
                    Date rid = null;
                    try {
                        lid = dateFormat.parse(lhs.optString("field_start_date"));
                        rid = dateFormat.parse(lhs.optString("field_start_date"));
                    } catch (ParseException e) {
                        Log.e("Date COnvert Error:", e.toString());
                    }
                    // Here you could parse string id to integer and then compare.
                    if (AsceOrDesce.equalsIgnoreCase("desce"))
                        return rid.compareTo(lid);
                    return lid.compareTo(rid);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONArray(jsons);
    }

    public JSONArray sortReviewJsonArray(JSONArray array, final String AsceOrDesce) {
        List<JSONObject> jsons = new ArrayList<JSONObject>();
        try {

            for (int i = 0; i < array.length(); i++) {

                jsons.add(array.getJSONObject(i));

            }
            Collections.sort(jsons, new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject lhs, JSONObject rhs) {
                    String lid = null;
                    String rid = null;
                    lid = lhs.optString("comment_count");
                    rid = rhs.optString("comment_count");
                    // Here you could parse string id to integer and then compare.
                    if (AsceOrDesce.equalsIgnoreCase("desce"))
                        return rid.compareTo(lid);
                    return lid.compareTo(rid);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONArray(jsons);
    }

    public JSONArray sortHotColdJsonArray(JSONArray array, final String AsceOrDesce) {
        List<JSONObject> jsons = new ArrayList<JSONObject>();
        try {

            for (int i = 0; i < array.length(); i++) {

                jsons.add(array.getJSONObject(i));

            }
            Collections.sort(jsons, new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject lhs, JSONObject rhs) {
                    String lid = null;
                    String rid = null;
                    lid = lhs.optString("value");
                    rid = rhs.optString("value");
                    // Here you could parse string id to integer and then compare.
                    if (AsceOrDesce.equalsIgnoreCase("desce"))
                        return rid.compareTo(lid);
                    return lid.compareTo(rid);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONArray(jsons);
    }
}
