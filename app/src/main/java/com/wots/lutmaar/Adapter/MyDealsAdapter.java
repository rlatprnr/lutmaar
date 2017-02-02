package com.wots.lutmaar.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.wots.lutmaar.Interface.RestServices;
import com.wots.lutmaar.R;
import com.wots.lutmaar.UtilClass.ConstantClass;
import com.wots.lutmaar.UtilClass.SaveSharedPreferences;
import com.wots.lutmaar.UtilClass.Singleton;
import com.wots.lutmaar.UtilClass.UtilityClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Super Star on 13-07-2015.
 */
public class MyDealsAdapter extends BaseAdapter {

    Context context;
    UtilityClass utilityClass;
    JSONArray PointsArray;

    //Interface
    RestServices api;
    RestAdapter restAdapter;

    public MyDealsAdapter(Context context, JSONArray JArray) {
        this.context = context;
        this.PointsArray = JArray;
        utilityClass = new UtilityClass(context);
    }

    @Override
    public int getCount() {
        return PointsArray.length();
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
        MyViewHolder mViewHolder;
        JSONObject jsonObject = null;
        String DealCategory = null;
        String DealID = null;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.my_deals_cell, null);

            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        try {
            jsonObject = PointsArray.getJSONObject(position).getJSONObject("node");

            mViewHolder.tvMyDealsTitle.setText(jsonObject.getString("Title"));
            mViewHolder.tvMyDealsCategory.setText(jsonObject.getString("field_cat"));
            mViewHolder.tvMyDealsDate.setText(jsonObject.getString("Post date"));
            mViewHolder.tvMyDealsHotOrCold.setText(jsonObject.getString("Hot or Cold"));
            DealCategory = jsonObject.getString("field_cat");
            DealID = jsonObject.getString("nid");
            if(DealID.equalsIgnoreCase("-1")){
                mViewHolder.tvMyDealsEdit.setText("Draft");
                mViewHolder.tvMyDealsEdit.setEnabled(false);
            }else{
                mViewHolder.tvMyDealsEdit.setText("Edit");
            }
            final String finalDealCategory = DealCategory;
            final String finalDealID = DealID;
            mViewHolder.tvMyDealsEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (utilityClass.isInternetConnection()) {
                        utilityClass.processDialogStart();
                        restService(finalDealCategory, finalDealID);
                    } else {
                       utilityClass.toast("Please check your internet settings!!!");
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }

    public class MyViewHolder {

        @InjectView(R.id.tvMyDealsTitle)
        TextView tvMyDealsTitle;
        @InjectView(R.id.tvMyDealsCategory)
        TextView tvMyDealsCategory;
        @InjectView(R.id.tvMyDealsDate)
        TextView tvMyDealsDate;
        @InjectView(R.id.tvMyDealsHotOrCold)
        TextView tvMyDealsHotOrCold;
        @InjectView(R.id.tvMyDealsEdit)
        TextView tvMyDealsEdit;


        public MyViewHolder(View view) {
            ButterKnife.inject(this, view);

        }
    }

    private void restService(String DealsType, String DealID) {
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Content-Type", "application/json");
                request.addHeader("X-CSRF-Token", SaveSharedPreferences.getTokon(context));
                request.addHeader("Cookie", SaveSharedPreferences.getCookie(context));

                Log.i(" Deal Details Tokon : ", SaveSharedPreferences.getTokon(context));
                Log.i("Deal Details Cookie : ", SaveSharedPreferences.getCookie(context));
            }
        };

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(ConstantClass.endPoints)
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
        api.getDealDetails(DealTypeServicePath, DealID, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject result, Response response) {
                utilityClass.processDialogStop();
                try {

                    JSONArray jsonArray = new JSONArray(result.getAsJsonArray("nodes").toString());
                    JSONObject jsonObject = jsonArray.getJSONObject(0).getJSONObject("node");
                    Singleton.GetInstance().setJsonObject(jsonObject);
                    Singleton.GetInstance().FragmentCall(3,jsonObject.toString());
                    Singleton.GetInstance().MenuHide();
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
}
