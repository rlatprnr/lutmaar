package com.wots.lutmaar.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.wots.lutmaar.CustomView.ImageLoader.ImageLoader;
import com.wots.lutmaar.Interface.RestServices;
import com.wots.lutmaar.R;
import com.wots.lutmaar.UtilClass.ConstantClass;
import com.wots.lutmaar.UtilClass.SaveSharedPreferences;
import com.wots.lutmaar.UtilClass.Singleton;
import com.wots.lutmaar.UtilClass.UtilityClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Super Star on 29-07-2015.
 */
public class DealsAdapter extends BaseAdapter {

    Context context;
    JSONArray DealsArray;
    UtilityClass utilityClass;
    String DealType, DealID;
    public ImageLoader imageLoader;
    JSONObject jsonObjectGloble = null;
    int Position = 0;

    //Interface
    RestServices api;
    RestAdapter restAdapter;

    public DealsAdapter(Context context, String dealType, JSONArray JArray) {
        this.context = context;
        this.DealsArray = JArray;
        utilityClass = new UtilityClass(context);
        DealType = dealType;
        try {
            imageLoader = new ImageLoader(context.getApplicationContext());
        } catch (NullPointerException e) {
            Log.e("Image Cache Error: ", e.toString());

        }
    }

    @Override
    public int getCount() {
        return DealsArray.length();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final MyViewHolder mViewHolder;
        JSONObject jsonObject = null;
        String imgPath = "";
        final String DealOnject;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.deals_cell, null);

            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }
        try {

            jsonObject = DealsArray.getJSONObject(position).getJSONObject("node");


            mViewHolder.tvDealUserName.setText(jsonObject.getString("name_1"));
            mViewHolder.tvDealTitle.setText(jsonObject.getString("title"));
            if (DealType.equalsIgnoreCase("Deals")) {
                mViewHolder.tvDealComment.setText(jsonObject.getString("comment_count") + " Comments");
            }
            else {
                mViewHolder.tvDealComment.setText(jsonObject.getString("comment_count"));
            }

            mViewHolder.tvDealHotCold.setText(jsonObject.getString("value") + "°");
            mViewHolder.tvDealTime.setText("Made Hot " + jsonObject.getString("created"));

            DealOnject = String.valueOf(jsonObject.toString());

            try {
                JSONArray ArrayImage = jsonObject.getJSONArray("field_images");
                imageLoader.DisplayImage(ArrayImage.getJSONObject(0).getString("src"), mViewHolder.ivImageDisplay);
            } catch (JSONException e) {
                JSONObject JObjectImage = jsonObject.getJSONObject("field_images");
                imageLoader.DisplayImage(JObjectImage.getString("src"), mViewHolder.ivImageDisplay);
            }

            mViewHolder.tvDealTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                 /*   if (DealType.equalsIgnoreCase("Deals")) {
                        Singleton.GetInstance().setHomeMenuFragmentPosition(8);
                    } else {*/
                    Singleton.GetInstance().setHomeMenuFragmentPosition(Singleton.GetInstance().getCurrentPosition());
                    Singleton.GetInstance().FragmentCall(13, DealOnject);
                    Singleton.GetInstance().MenuHide();

                }
            });

            mViewHolder.ivImageDisplay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (DealType.equalsIgnoreCase("Deals")) {
                        Singleton.GetInstance().setHomeMenuFragmentPosition(8);
                    } else {
                        Singleton.GetInstance().setHomeMenuFragmentPosition(Singleton.GetInstance().getCurrentPosition());
                    }
                    Singleton.GetInstance().FragmentCall(13, DealOnject);
                    Singleton.GetInstance().MenuHide();

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mViewHolder.ivDealCold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Position = position;
                    jsonObjectGloble = DealsArray.getJSONObject(Position).getJSONObject("node");
                    DealID = jsonObjectGloble.optString("nid");
                    restServiceSetVote(-1, mViewHolder.tvDealHotCold, DealID);
                } catch (JSONException e) {
                    Log.e("JsonObject Create Error: ", e.toString());
                }
            }
        });
        mViewHolder.ivDealHot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Position = position;
                    jsonObjectGloble = DealsArray.getJSONObject(Position).getJSONObject("node");
                    DealID = jsonObjectGloble.optString("nid");
                    restServiceSetVote(1, mViewHolder.tvDealHotCold, DealID);
                } catch (JSONException e) {
                    Log.e("JsonObject Create Error: ", e.toString());
                }
            }
        });


        return convertView;
    }

    private void restServiceSetVote(final int Value, final TextView ColdOrHot, final String DealID) {
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Content-Type", "application/json");
                request.addHeader("X-CSRF-Token", SaveSharedPreferences.getTokon(context));
                request.addHeader("Cookie", SaveSharedPreferences.getCookie(context));

                Log.i("Tokon : ", SaveSharedPreferences.getTokon(context));
                Log.i("Cookie : ", SaveSharedPreferences.getCookie(context));
            }
        };

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(ConstantClass.endPoints)
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
                utilityClass.processDialogStop();
                try {
                    JSONObject JObject = new JSONObject(result.get("node").toString());
                    JSONArray jsonArrayNew = JObject.getJSONArray(DealID);
                    ColdOrHot.setText(jsonArrayNew.getJSONObject(2).optString("value")+"°");
                    // tvDealDetailsReviewPoints.setText(jsonArrayNew.getJSONObject(1).optString("value"));
                    if(!jsonArrayNew.getJSONObject(2).optString("value").equalsIgnoreCase(jsonObjectGloble.optString("value"))) {
                        if (Value == 1)
                            utilityClass.toast("Thanks for voting a Deal Hot");
                        else
                            utilityClass.toast("Thanks for voting a Deal Cold");
                    }
                    JSONObject JObjectUpdate = new JSONObject();
                    jsonObjectGloble.put("value", String.valueOf(jsonArrayNew.getJSONObject(2).optString("value")));
                    JObjectUpdate.put("node", jsonObjectGloble);
                    DealsArray.put(Position, JObjectUpdate);
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

    public class MyViewHolder {
        @InjectView(R.id.ivImageDisplay)
        ImageView ivImageDisplay;
        @InjectView(R.id.ivDealHot)
        ImageView ivDealHot;
        @InjectView(R.id.ivDealCold)
        ImageView ivDealCold;
        @InjectView(R.id.tvDealUserName)
        TextView tvDealUserName;
        @InjectView(R.id.tvDealTitle)
        TextView tvDealTitle;
        /*   @InjectView(R.id.tvDealDetails)
           TextView tvDealDetails;*/
        @InjectView(R.id.tvDealHotCold)
        TextView tvDealHotCold;
        /*@InjectView(R.id.tvDealPrice)
        TextView tvDealPrice;*/
        @InjectView(R.id.tvDealComment)
        TextView tvDealComment;
        @InjectView(R.id.tvDealTime)
        TextView tvDealTime;
        @InjectView(R.id.tvDealType)
        TextView tvDealType;
       /* @InjectView(R.id.tvGetDeal)
        TextView tvGetDeal;
        @InjectView(R.id.tvViewDeal)
        TextView tvViewDeal;*/


        public MyViewHolder(View view) {
            ButterKnife.inject(this, view);

        }
    }
}
