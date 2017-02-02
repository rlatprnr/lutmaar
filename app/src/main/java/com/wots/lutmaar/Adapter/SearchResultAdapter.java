package com.wots.lutmaar.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wots.lutmaar.CustomView.ImageLoader.ImageLoader;
import com.wots.lutmaar.Interface.RestServices;
import com.wots.lutmaar.R;
import com.wots.lutmaar.UtilClass.UtilityClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RestAdapter;

/**
 * Created by Super Star on 30-07-2015.
 */
public class SearchResultAdapter extends BaseAdapter {

    Context context;
    JSONArray DealsArray;
    JSONObject DealsObject;
    UtilityClass utilityClass;
    String DealID;
    public ImageLoader imageLoader;

    //Interface
    RestServices api;
    RestAdapter restAdapter;

    public SearchResultAdapter(Context context, JSONArray JArray) {
        this.context = context;
        this.DealsArray = JArray;
        utilityClass = new UtilityClass(context);
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
        final String DealIDOnject,dealType;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.search_result_cell, null);

            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }
        try {

            jsonObject = DealsArray.getJSONObject(position);

            imgPath = jsonObject.getString("field_images");
            mViewHolder.tvTitleSearchResult.setText(jsonObject.getString("title"));
            mViewHolder.tvCommentSearchResult.setText(jsonObject.getString("comment_count") + " Comments");

            mViewHolder.tvColdHotSearchResult.setText(String.valueOf(jsonObject.getInt("value")) + "o");
           // DealOnject = String.valueOf(jsonObject.toString());
            DealIDOnject = jsonObject.getString("nid");
            dealType = jsonObject.getString("field_cat");
            /*convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    utilityClass.processDialogStart();
                    restService(dealType, DealIDOnject);
                }
            });*/
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(imgPath.equalsIgnoreCase("0")){
            mViewHolder.ivSearchResult.setImageDrawable(context.getResources().getDrawable(R.drawable.image_not_available));
        }else {
            imageLoader.DisplayImage(imgPath, mViewHolder.ivSearchResult);
        }
        //Picasso.with(context).load(imgPath).placeholder(R.mipmap.ic_launcher).noFade(). resize(300, 300).centerInside().into(mViewHolder.ivImageDisplay);


        return convertView;
    }

   /* private void DealDialogShow() {
        final Dialog dialog = new Dialog(context);// android.R.style.Theme_Translucent_NoTitleBar);
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

            // JSONArray arrayImageDialog = jsonObject.getJSONArray("field_images");
            try {
                JSONArray arrayImage = DealsObject.getJSONArray("field_images");
                Picasso.with(context).load(arrayImage.getJSONObject(0).getString("src"))
                        .placeholder(R.mipmap.ic_launcher).noFade().resize(240, 260).centerInside().into(ivSearchDealsPopup);
            } catch (JSONException e) {
                Log.e("JSON Image ArrayError", e.toString());
                if (e.toString().contains("field_images of type org.json.JSONObject cannot be converted to JSONArray")) {
                    try {
                        JSONObject jsonObject1 = DealsObject.getJSONObject("field_images");
                        Picasso.with(context).load(jsonObject1.getString("src"))
                                .placeholder(R.mipmap.ic_launcher).noFade().resize(240, 260).centerInside().into(ivSearchDealsPopup);
                    } catch (JSONException e1) {
                        Log.e("JSON Image ObjectError", e.toString());
                    }
                }
            }
            tvSearchDealTitlePopup.setText(DealsObject.getString("title"));
            tvSearchDealDetailsPopup.setText(DealsObject.getString("body"));

            tvSearchDealReviewsPopup.setText(DealsObject.getString("comment_count") + " Reviews");
            tvSearchDealDetailsHotColdValuePopupPopup.setText(DealsObject.getString("value") + "o");
            tvSearchDealPricePopup.setText("$" + DealsObject.getString("field_price"));
            tvSearchDealCommentPopup.setText(DealsObject.getString("comment_count") + " Comments");
            tvSearchDealTimePopup.setText(DealsObject.getString("created"));
        } catch (JSONException e) {
            Log.e("Json Error : ", e.toString());
        }
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
    }*/

    public class MyViewHolder {
        @InjectView(R.id.ivSearchResult)
        ImageView ivSearchResult;


        @InjectView(R.id.tvTitleSearchResult)
        TextView tvTitleSearchResult;

        @InjectView(R.id.tvColdHotSearchResult)
        TextView tvColdHotSearchResult;

        @InjectView(R.id.tvCommentSearchResult)
        TextView tvCommentSearchResult;


        public MyViewHolder(View view) {
            ButterKnife.inject(this, view);

        }
    }

    /*private void restService(String DealsType, String nid) {

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(ConstantClass.endPoints)
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

                   JSONArray  jsonArrayOne = new JSONArray(result.getAsJsonArray("nodes").toString());
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
    }*/
}
