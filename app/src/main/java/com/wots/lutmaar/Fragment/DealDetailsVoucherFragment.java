package com.wots.lutmaar.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.plus.PlusShare;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.wots.lutmaar.Adapter.DealsCommentAdapter;
import com.wots.lutmaar.CustomView.CustomTextView;
import com.wots.lutmaar.CustomView.ImageLoader.ImageLoader;
import com.wots.lutmaar.Interface.RestServices;
import com.wots.lutmaar.R;
import com.wots.lutmaar.UtilClass.ConstantClass;
import com.wots.lutmaar.UtilClass.SaveSharedPreferences;
import com.wots.lutmaar.UtilClass.Singleton;
import com.wots.lutmaar.UtilClass.UtilityClass;
import com.wots.lutmaar.UtilClass.Validation;
import com.wots.lutmaar.UtilClass.db.MySQLiteHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class DealDetailsVoucherFragment extends Fragment {
    //Class
    UtilityClass utilityClass;
    ConstantClass constantClass;
    String DealID, DealTitle, DealJSONObject, CommentDetails;
    JSONObject dealJObject;
    JSONArray jsonArray, jsonArrayComment;
    DealsCommentAdapter adapter;
    public ImageLoader imageLoader;
    String ImagePath = "", ShareLinkPath;
    static int ListViewCellHeighte = 0;
    private MySQLiteHelper mydb;
    //Interface
    RestServices api;
    RestAdapter restAdapter;

    //View
    @InjectView(R.id.tvDealDetailsOrganisationName)
    CustomTextView tvDealDetailsOrganisationName;
    @InjectView(R.id.tvDealLocation)
    TextView tvDealLocation;
    @InjectView(R.id.btnGoToDeal)
    Button btnGoToDeal;
    @InjectView(R.id.tvDealDetailsReviews)
    TextView tvDealDetailsReviews;
    @InjectView(R.id.tvDealDetailsVoucherCode)
    TextView tvDealDetailsVoucherCode;
    @InjectView(R.id.tvVoucherPerOff)
    TextView tvVoucherPerOff;
    @InjectView(R.id.tvUserName)
    TextView tvUserName;
    @InjectView(R.id.tvDealCreated)
    TextView tvDealCreated;
    @InjectView(R.id.tvDealDetailsHotColdValue)
    TextView tvDealDetailsHotColdValue;
    @InjectView(R.id.tvAddComment)
    TextView tvAddComment;
    @InjectView(R.id.ivUserPicture)
    ImageView ivUserPicture;
    @InjectView(R.id.ivDealSpamFlag)
    ImageView ivDealSpamFlag;
    @InjectView(R.id.ivDealDetailsHot)
    ImageView ivDealDetailsHot;
    @InjectView(R.id.ivDealDetailsCold)
    ImageView ivDealDetailsCold;
    @InjectView(R.id.lvDealComment)
    ListView lvDealComment;
    @InjectView(R.id.SCView)
    ScrollView SCView;

    //Variable
    String DealTypeServicePath, DealsType, DealTypeServicePathHTML, spam_flag, expired_flag, dealUserName, dealTitle;

    int pageNo = 0;
    int pageEnd = 0;
    int start = 0;
    boolean pageScroll = true;
    private int NetworkErrorCount = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        DealJSONObject = getArguments().getString("DealID");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.deal_details_voucher_fragment, container, false);
        ButterKnife.inject(this, view);

        declaration();
        return view;
    }

    private void declaration() {
        utilityClass = new UtilityClass(getActivity());
        mydb = new MySQLiteHelper(getActivity());

        imageLoader = new ImageLoader(getActivity().getApplicationContext());
        try {
            dealJObject = new JSONObject(DealJSONObject);
            DealID = dealJObject.optString("nid");
            Log.d("Deal Object : ", dealJObject.toString());
            DealTypeServicePath = "voucher-view-service.json";

        } catch (JSONException e) {
            Log.e("JSONObject Error :", e.toString());
        } catch (NullPointerException e) {
            Log.e("JSONObject NULL Error :", e.toString());
        }
        utilityClass.processDialogStart();
        jsonArrayComment = new JSONArray();
        adapter = new DealsCommentAdapter(getActivity(), jsonArrayComment);
        lvDealComment.setAdapter(adapter);

        lvDealComment.setOnScrollListener(new AbsListView.OnScrollListener() {
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
        lvDealComment.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //  Log.e("Lisview *************", "focused");
                SCView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

    }

    private void initialization() {
        if (utilityClass.isInternetConnection()) {
            restServiceGetComment();
        } else {
            pageEnd = 1;
            fillDBData();
        }
    }

    @OnClick(R.id.btnGoToDeal)
    public void setBtnGoToDeal() {
        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ShareLinkPath));
        startActivity(myIntent);
    }

    @OnClick(R.id.tvDealDetailsShare)
    public void setTvDealDetailsShare() {
        shareDialogShow();
    }

    @OnClick(R.id.ivDealDetailsHot)
    public void SetIvDealDetailsHot() {
        if (utilityClass.isInternetConnection()) {
            ivDealDetailsHot.setEnabled(false);
            ivDealDetailsCold.setEnabled(false);
            restServiceSetVote(1);
        } else {
            utilityClass.toast("Please check your internet connection!!!");
        }

    }

    @OnClick(R.id.ivDealDetailsCold)
    public void setIvDealDetailsCold() {
        if (utilityClass.isInternetConnection()) {
            ivDealDetailsHot.setEnabled(false);
            ivDealDetailsCold.setEnabled(false);
            restServiceSetVote(-1);
        } else {
            utilityClass.toast("Please check your internet connection!!!");
        }
    }

    @OnClick(R.id.ivDealSpamFlag)
    public void setIvDealSpamFlag() {
        if (utilityClass.isInternetConnection()) {
            ivDealSpamFlag.setEnabled(false);
            restServiceSetDealFlag("spam", spam_flag, ivDealSpamFlag);
        } else {
            utilityClass.toast("Please check your internet connection!!!");
        }
    }

    @OnClick(R.id.tvAddComment)
    public void setTvAddComment() {
        addCommentDialogShow();
    }

    @OnClick(R.id.ivSendPrivateMessage)
    public void setIvSendPrivateMessage() {
        if (utilityClass.isInternetConnection()) {
            if(!dealUserName.equalsIgnoreCase(SaveSharedPreferences.getUserName(getActivity()))) {
                utilityClass.processDialogStart();
                restServiceSendMessageToNewUser();
            }else{
                utilityClass.toast("This deal posted by you!!!");
            }
        } else {
            utilityClass.toast("Please check your internet connection!!!");
        }
    }

    private void addCommentDialogShow() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_comment_cell);

        // final EditText etAddCommentTitle = (EditText) dialog.findViewById(R.id.etAddCommentTitle);
        final EditText etAddCommentDetails = (EditText) dialog.findViewById(R.id.etAddCommentDetails);
        TextView tvAddComment = (TextView) dialog.findViewById(R.id.tvAddComment);


        tvAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidationComment(etAddCommentDetails)) {
                    if (utilityClass.isInternetConnection()) {
                        CommentDetails = etAddCommentDetails.getText().toString();
                        dialog.dismiss();
                        utilityClass.processDialogStart();
                        NetworkErrorCount = 0;
                        restServiceAddComment();
                    } else {
                        utilityClass.toast("Please check your internet settings!!!");
                    }
                }
            }
        });

        dialog.show();
    }

    private boolean checkValidationComment(EditText tv) {
        boolean ret = true;
        if (!Validation.hasText(tv, "comment")) {
            ret = false;
        }
        return ret;
    }

    public void fillDBData() {
        utilityClass.processDialogStop();
        tvDealDetailsOrganisationName.setText(dealJObject.optString("field_deal_url") + dealJObject.optString("field_postal_address_organisation_name"));
        if (dealJObject.optString("field_deal_url").equalsIgnoreCase("")) {
            tvDealLocation.setText(dealJObject.optString("field_postal_address_organisation_name") + ", "
                    + dealJObject.optString("field_postal_address_thoroughfare") + ", " + dealJObject.optString("address2") + ", "
                    + dealJObject.optString("field_postal_address_locality") + ", " + dealJObject.optString("field_postal_address_postal_code"));

        } else {
            tvDealLocation.setText(dealJObject.optString("field_postal_address_locality"));
            btnGoToDeal.setText("get Online Deal");
            btnGoToDeal.setBackgroundColor(getActivity().getResources().getColor(R.color.green));
        }
        dealUserName = dealJObject.optString("name_1");
        dealTitle = dealJObject.optString("title");
        //  imageLoader.DisplayImage(dealJObject.optString("picture"), ivUserPicture);
        tvUserName.setText(dealUserName);
        tvDealDetailsVoucherCode.setText(dealJObject.optString("CODE"));
        tvVoucherPerOff.setText(dealJObject.optString("DISCOUNT") + "%");
        tvDealDetailsReviews.setText(dealJObject.optString("comment_count") + " Reviews");
        tvDealDetailsHotColdValue.setText(dealJObject.optString("value") + "°");
        tvDealCreated.setText(dealJObject.optString("created"));
        ShareLinkPath = dealJObject.optString("path");

        try {
            Cursor rsComments = mydb.getAllData(DealID, mydb.TABLE_LUTMAAR);
            rsComments.moveToFirst();
            if (rsComments.getCount() > 0) {
                if (jsonArrayComment.length() != 0) {
                    jsonArrayComment = null;
                    jsonArrayComment = new JSONArray();
                    adapter = new DealsCommentAdapter(getActivity(), jsonArrayComment);
                    lvDealComment.setAdapter(adapter);
                }
                do {
                    // jsonArray.put(jsonArrayNew.getJSONObject(i));
                    try {
                        jsonArrayComment.put(new JSONObject(rsComments.getString(rsComments.getColumnIndex(MySQLiteHelper.JSONOBJECT))));
                    } catch (JSONException e) {
                        Log.e("DB getData Json:", e.toString());
                    }
                    // get  the  data into array,or class variable
                } while (rsComments.moveToNext());
                adapter.notifyDataSetChanged();
            }
        } catch (NullPointerException e) {
            Log.e("Null Pointer Error:", e.toString());
        }
        LinearLayout.LayoutParams Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        ListViewCellHeighte = calculateHeight(lvDealComment);
        if (ListViewCellHeighte >= (utilityClass.GetHeight() / 2)) {
            if (utilityClass.isTablet(getActivity()) || utilityClass.isTabletDevice(getActivity())) {
                Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (utilityClass.GetHeight() / 1.6));
                lvDealComment.setLayoutParams(Params);
            } else {
                Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (utilityClass.GetHeight() / 2));
                lvDealComment.setLayoutParams(Params);
            }
        } else {
            Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ListViewCellHeighte);
            lvDealComment.setLayoutParams(Params);
        }
    }

    private int calculateHeight(ListView list) {
        int height = 0;
        for (int i = 0; i < list.getCount(); i++) {
            View childView = list.getAdapter().getView(i, null, list);
            childView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            height += childView.getMeasuredHeight();
        }
        //dividers height
        height += list.getDividerHeight() * list.getCount();
        return height;
    }

    private void restServiceGetComment() {
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
        Log.d("Vouchers Comm. PageNo:", String.valueOf(pageNo));
        api.getDealComments(DealID, String.valueOf(pageNo), new Callback<JsonObject>() {
            @Override
            public void success(JsonObject result, Response response) {
                LinearLayout.LayoutParams Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
                Log.d("Vouchers CommentList:", result.toString());
                try {
                    JSONArray jsonArrayNew = new JSONArray(result.getAsJsonArray("nodes").toString());
                    if (jsonArrayNew.length() != 0) {
                        if (pageNo == 0) {
                            mydb.deleteDealTypeData(DealID, mydb.TABLE_LUTMAAR);
                        }
                        if (jsonArrayNew.length() < 10) {
                            pageEnd = 1;
                        }
                        for (int i = 0; i < jsonArrayNew.length(); i++) {
                            mydb.insertJSONData(DealID, jsonArrayNew.getJSONObject(i));
                            jsonArrayComment.put(jsonArrayNew.getJSONObject(i));
                        }
                        adapter.notifyDataSetChanged();

                        if (pageNo == 0) {
                            ListViewCellHeighte = calculateHeight(lvDealComment);
                            if (ListViewCellHeighte >= (utilityClass.GetHeight() / 2)) {
                                if (utilityClass.isTablet(getActivity()) || utilityClass.isTabletDevice(getActivity())) {
                                    Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (utilityClass.GetHeight() / 1.6));
                                    lvDealComment.setLayoutParams(Params);
                                } else {
                                    Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (utilityClass.GetHeight() / 2));
                                    lvDealComment.setLayoutParams(Params);
                                }
                            } else {
                                Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ListViewCellHeighte);
                                lvDealComment.setLayoutParams(Params);
                            }
                        }
                        pageNo++;
                        pageScroll = true;
                    } else {
                        pageEnd = 1;
                    }
                } catch (IllegalStateException e) {
                    Log.e("IllegalStateException:", e.toString());
                } catch (JSONException e) {
                    Log.e("JSONException:", e.toString());
                }
                if (start == 0) {
                    start = 1;
                    restService();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Retrofit Error  : ", String.valueOf(error.toString()));
                if (start == 0) {
                    start = 1;
                    restService();
                }
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

    private void restService() {

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

        api.getDealDetails(DealTypeServicePath, DealID, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject result, Response response) {
                utilityClass.processDialogStop();


                try {

                    jsonArray = new JSONArray(result.getAsJsonArray("nodes").toString());
                    JSONObject jsonObject = null;
                    //  DealsGetterSetter dealsGetterSetter = new DealsGetterSetter();
                    try {

                        jsonObject = jsonArray.getJSONObject(0).getJSONObject("node");
                        Singleton.GetInstance().setJsonObject(jsonObject);

                        tvDealDetailsOrganisationName.setText(jsonObject.optString("field_deal_url") + jsonObject.optString("field_postal_address_organisation_name"));
                        if (jsonObject.optString("field_deal_url").equalsIgnoreCase("")) {
                            tvDealLocation.setText(jsonObject.optString("field_postal_address_organisation_name") + ", "
                                    + jsonObject.optString("field_postal_address_thoroughfare") + ", " + jsonObject.optString("address2") + ", "
                                    + jsonObject.optString("field_postal_address_locality") + ", " + jsonObject.optString("field_postal_address_postal_code"));

                        } else {
                            tvDealLocation.setText(jsonObject.optString("field_postal_address_locality"));
                            btnGoToDeal.setText("get Online Deal");
                            btnGoToDeal.setBackgroundColor(getActivity().getResources().getColor(R.color.green));
                        }
                        //  tvDealLocation.setText(jsonObject.optString("field_postal_address_locality") + ", " + jsonObject.optString("field_postal_address_administrative_area"));
                        dealUserName = jsonObject.optString("Name");
                        dealTitle = jsonObject.optString("title");
                        Picasso.with(getActivity()).load(jsonObject.optString("picture")).noFade()
                                .resize((int) (utilityClass.GetWidth() / 4), (int) (utilityClass.GetHeight() / 4))
                                .centerInside().into(ivUserPicture);
                        //imageLoader.DisplayImage(dealJObject.optString("picture"), ivUserPicture);
                        tvUserName.setText(dealUserName);
                        tvDealDetailsVoucherCode.setText(jsonObject.optString("field_code"));
                        tvVoucherPerOff.setText(jsonObject.optString("field_discount") + "%");
                        spam_flag = jsonObject.optString("spam_flag");
                        if (!spam_flag.equalsIgnoreCase("0"))
                            ivDealSpamFlag.setImageResource(R.drawable.deal_details_blue_flag);

                        tvDealDetailsReviews.setText(jsonObject.optString("comment_count") + " Reviews");
                        tvDealDetailsHotColdValue.setText(jsonObject.optString("value") + "°");
                        tvDealCreated.setText(jsonObject.optString("created"));
                        ShareLinkPath = constantClass.endPoints + jsonObject.optString("path");

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("Error : ", e.toString());
                    }

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

    private void shareDialogShow() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.share_cell);
        //CustomDialog Data

        ImageView shareFaceBook = (ImageView) dialog.findViewById(R.id.shareFaceBook);
        ImageView shareTwitter = (ImageView) dialog.findViewById(R.id.shareTwitter);
        ImageView sharePinterest = (ImageView) dialog.findViewById(R.id.sharePinterest);
        ImageView shareGoogle = (ImageView) dialog.findViewById(R.id.shareGoogle);

        shareFaceBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareLinkContent content = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse(ShareLinkPath))
                        .build();
                ShareDialog.show(getActivity(), content);
                dialog.dismiss();
            }
        });
        shareTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TweetComposer.Builder builder = new TweetComposer.Builder(getActivity())
                        .text(DealTitle + "\n\n" + ShareLinkPath)
                        .image(Uri.parse(ImagePath));
                builder.show();
                dialog.dismiss();
            }
        });
        sharePinterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = String.format(
                        "https://www.pinterest.com/pin/create/button/?url=%s&media=%s&description=%s",
                        urlEncode(ShareLinkPath), urlEncode(ImagePath), DealTitle);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                filterByPackageName(getActivity(), intent, "com.pinterest");
                getActivity().startActivity(intent);

                dialog.dismiss();
            }
        });
        shareGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new PlusShare.Builder(getActivity())
                        .setType("text/plain")
                        .setText(DealTitle)
                        .setContentUrl(Uri.parse(ShareLinkPath))
                        .getIntent();

                startActivityForResult(shareIntent, 0);

                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.wtf("", "UTF-8 should always be supported", e);
            return "";
        }
    }

    public static void filterByPackageName(Context context, Intent intent, String prefix) {
        List<ResolveInfo> matches = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : matches) {
            if (info.activityInfo.packageName.toLowerCase().startsWith(prefix)) {
                intent.setPackage(info.activityInfo.packageName);
                return;
            }
        }
    }

    private void restServiceSetVote(final int Value) {
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
        HashMap<String, Object> HMVotes = new HashMap<String, Object>();
        ArrayList<HashMap<String, String>> ALHMVote = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> HMVoteValue = new HashMap<String, String>();

        HMVoteValue.put("entity_id", DealID);
        HMVoteValue.put("value_type", "points");
        HMVoteValue.put("tag", "vote");
        HMVoteValue.put("value", String.valueOf(Value));

        ALHMVote.add(HMVoteValue);
        HMVotes.put("votes", ALHMVote);

        Log.i("Request Votes Data:", HMVotes.toString());
        api.setVotes(HMVotes, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject result, Response response) {
                ivDealDetailsHot.setEnabled(true);
                ivDealDetailsCold.setEnabled(true);
                try {
                    JSONObject JObject = new JSONObject(result.get("node").toString());
                    JSONArray jsonArrayNew = JObject.getJSONArray(DealID);
                    tvDealDetailsHotColdValue.setText(jsonArrayNew.getJSONObject(2).optString("value") + "°");
                    //    tvDealDetailsReviewPoints.setText(jsonArrayNew.getJSONObject(1).optString("value"));
                    if (!tvDealDetailsHotColdValue.getText().toString().equalsIgnoreCase(jsonArrayNew.getJSONObject(2).optString("value") + "o")) {
                        if (Value == 1)
                            utilityClass.toast("Thanks for voting a Deal Hot");
                        else
                            utilityClass.toast("Thanks for voting a Deal Cold");
                    }
                    Log.i("Set Votes is success:", String.valueOf(result));
                } catch (IllegalStateException e) {
                    Log.e("IllegalStateException:", e.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Restrofit Error  : ", String.valueOf(error.toString()));
                ivDealDetailsHot.setEnabled(true);
                ivDealDetailsCold.setEnabled(true);
                // tvError.setVisibility(View.VISIBLE);
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
                } else if (error.toString().contains("User can post only one comment per deal or question")) {
                    //tvError.setText("Result not found form server");
                    utilityClass.toast("User can post only one comment per deal or question.");
                } else if (error.toString().contains("Verification e-mail address field is required")) {
                    //tvError.setText("Result not found form server");
                    utilityClass.toast("Verification e-mail address field is required.");
                } else {
                    //tvError.setText("Invalid user or password");
                    utilityClass.toast("Something went wrong!!!");
                }
            }
        });
    }

    private void restServiceSetDealFlag(String SpamOrExpire, final String flag, final ImageView ivFlag) {
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
                .setEndpoint(ConstantClass.endPoints)
                .setRequestInterceptor(requestInterceptor)
                .build();

        api = restAdapter.create(RestServices.class);
        HashMap<String, String> HMFlagValue = new HashMap<String, String>();
        final Boolean[] bool = new Boolean[1];
        bool[0] = false;
        if (SpamOrExpire.equalsIgnoreCase("spam")) {
            HMFlagValue.put("flag_name", "spam_flag");
            if (flag.equalsIgnoreCase("0")) {
                HMFlagValue.put("action", "flag");
            } else {
                HMFlagValue.put("action", "unflag");
            }
        } else {
            HMFlagValue.put("flag_name", "expired_deal_visible_flag");
            HMFlagValue.put("action", "unflag");
        }
        HMFlagValue.put("entity_id", DealID);
        Log.i("Request Votes Data:", HMFlagValue.toString());
        api.setFlags(HMFlagValue, new Callback<Object>() {
            @Override
            public void success(Object result, Response response) {
                utilityClass.processDialogStop();
                bool[0] = true;
                Log.i("Comment Flag OBJ :", String.valueOf(result));
                //  utilityClass.toast("User logout success");
                Log.i("Comment Flag Success:", String.valueOf(result));
                if (result.toString().contains("true")) {
                    if (flag.equalsIgnoreCase("0")) {
                        ivFlag.setImageResource(R.drawable.deal_details_blue_flag);
                    } else {
                        ivFlag.setImageResource(R.drawable.deal_details_red_flag);
                    }
                } else {
                    utilityClass.toast("You have already spam this deal!");
                }
                ivFlag.setEnabled(true);

            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Restrofit Error  : ", String.valueOf(error.toString()));
                ivFlag.setEnabled(true);
                bool[0] = false;
                // tvError.setVisibility(View.VISIBLE);
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
                } else if (error.toString().contains("User can post only one comment per deal or question")) {
                    //tvError.setText("Result not found form server");
                    utilityClass.toast("User can post only one comment per deal or question.");
                } else if (error.toString().contains("Verification e-mail address field is required")) {
                    //tvError.setText("Result not found form server");
                    utilityClass.toast("Verification e-mail address field is required.");
                } else {
                    //tvError.setText("Invalid user or password");
                    utilityClass.toast("Something went wrong!!!");
                }
            }

        });
    }

    private void restServiceAddComment() {
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
        HashMap<String, Object> HMCommentBody = new HashMap<String, Object>();
        ArrayList<HashMap<String, Object>> ALHMCommentBody = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> HMCommentBodyValue = new HashMap<String, Object>();
        HashMap<String, Object> HMCommentBodyUnd = new HashMap<String, Object>();

        HMCommentBodyValue.put("value", CommentDetails);
        ALHMCommentBody.add(HMCommentBodyValue);
        HMCommentBodyUnd.put("und", ALHMCommentBody);

        HMCommentBody.put("nid", DealID);
        HMCommentBody.put("comment_body", HMCommentBodyUnd);

        Log.i("Request Comment Data:", HMCommentBody.toString());
        api.addDealComment(HMCommentBody, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject result, Response response) {
                utilityClass.processDialogStop();
                try {

                    utilityClass.toast("Comment is posted");
                    NetworkErrorCount = 0;
                    Log.i("Comment Deal success:", String.valueOf(result));
                    pageNo = 0;
                    pageEnd = 0;
                    pageScroll = false;
                    jsonArrayComment = null;
                    jsonArrayComment = new JSONArray();
                    adapter = new DealsCommentAdapter(getActivity(), jsonArrayComment);
                    lvDealComment.setAdapter(adapter);
                    restServiceGetComment();


                } catch (IllegalStateException e) {
                    Log.e("IllegalStateException:", e.toString());
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
                        restServiceAddComment();
                    }
                } else {
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
                    } else if (error.toString().contains("User can post only one comment per deal or question")) {
                        //tvError.setText("Result not found form server");
                        utilityClass.toast("User can post only one comment per deal or question.");
                    } else if (error.toString().contains("Verification e-mail address field is required")) {
                        //tvError.setText("Result not found form server");
                        utilityClass.toast("Verification e-mail address field is required.");
                    } else {
                        //tvError.setText("Invalid user or password");
                        utilityClass.toast("Something went wrong!!!");
                    }
                }
            }
        });
    }

    private void restServiceSendMessageToNewUser() {
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
                .setEndpoint(ConstantClass.endPoints)
                .setRequestInterceptor(requestInterceptor)
                .build();

        api = restAdapter.create(RestServices.class);
        ArrayList<String> userNameList = new ArrayList<String>();
        HashMap<String, Object> HMNewMessage = new HashMap<String, Object>();
        userNameList.add(dealUserName);
        HMNewMessage.put("recipients", userNameList);
        HMNewMessage.put("subject", dealTitle);
        HMNewMessage.put("body", "Hi!!!");
        Log.d("New Message Request:", HMNewMessage.toString());
        api.newMessage(HMNewMessage, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject result, Response response) {
                utilityClass.processDialogStop();
                Singleton.GetInstance().setHomeMenuFragmentPosition(1);
                Singleton.GetInstance().MenuHide();
                Singleton.GetInstance().FragmentCall(20, String.valueOf(result.getAsJsonObject().toString()));
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Restrofit Error  : ", String.valueOf(error.toString()));

                // tvError.setVisibility(View.VISIBLE);
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
                } else if (error.toString().contains("User can post only one comment per deal or question")) {
                    //tvError.setText("Result not found form server");
                    utilityClass.toast("User can post only one comment per deal or question.");
                } else if (error.toString().contains("Verification e-mail address field is required")) {
                    //tvError.setText("Result not found form server");
                    utilityClass.toast("Verification e-mail address field is required.");
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
        Singleton.GetInstance().getTvMainHeading().setText("Deal Details");
    }
}
