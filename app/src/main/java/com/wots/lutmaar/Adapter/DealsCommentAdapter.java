package com.wots.lutmaar.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;
import com.wots.lutmaar.Interface.RestServices;
import com.wots.lutmaar.R;
import com.wots.lutmaar.UtilClass.ConstantClass;
import com.wots.lutmaar.UtilClass.SaveSharedPreferences;
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
 * Created by Super Star on 09-07-2015.
 */
public class DealsCommentAdapter extends BaseAdapter {

    Context context;
    UtilityClass utilityClass;
    JSONArray CommentArray;
    //  public ImageLoader imageLoader;

    //Interface
    RestServices api;
    RestAdapter restAdapter;

    public DealsCommentAdapter(Context context, JSONArray JArray) {
        this.context = context;
        this.CommentArray = JArray;
        utilityClass = new UtilityClass(context);
       /* try {
            imageLoader = new ImageLoader(context.getApplicationContext());
        } catch (NullPointerException e) {
            Log.e("Image Cache Error: ", e.toString());

        }*/
    }

    @Override
    public int getCount() {
        return CommentArray.length();
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
        String CID = "", UID = "", Flag = "", CommentBody = "", cLike = "", cDislike = "";

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.comment_list_cell, null);

            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        try {
            // mViewHolder.tvDealCommentDetails.setAlignment(Paint.Align.LEFT);
            //  mViewHolder.tvDealCommentDetails.setTextSize();
            jsonObject = CommentArray.getJSONObject(position).getJSONObject("node");
            CommentBody = jsonObject.optString("comment_body");
            mViewHolder.tvDealCommentUserName.setText(jsonObject.optString("name"));
            mViewHolder.tvDealCommentTime.setText(jsonObject.optString("created"));
            mViewHolder.tvUserTotalComment.setText("Comments:" + jsonObject.optString("author_comment_count"));
            mViewHolder.tvDealCommentDetails.setText(CommentBody);
            mViewHolder.tvDealCommentLike.setText("Like (" + jsonObject.optString("like") + ")");
            mViewHolder.tvDealCommentDisLike.setText("Dislike (" + jsonObject.optString("dislike") + ")");
            Picasso.with(context).load(jsonObject.optString("comment_author_image")).placeholder(R.mipmap.ic_launcher).noFade().fit().centerInside().into(mViewHolder.ivDealCommentUserImage);
            //  imageLoader.DisplayImage(jsonObject.optString("comment_author_image"), mViewHolder.ivDealCommentUserImage);
            Flag = jsonObject.optString("is_flagged_as_abusive");
            if (Flag.equalsIgnoreCase("Yes")) {
                mViewHolder.tvDealCommentFlag.setCompoundDrawablesWithIntrinsicBounds(R.drawable.deal_details_blue_flag, 0, 0, 0);
            } else {
                mViewHolder.tvDealCommentFlag.setCompoundDrawablesWithIntrinsicBounds(R.drawable.deal_details_red_flag, 0, 0, 0); // (Drawable) null, (Drawable) null, (Drawable) null);
            }
            cLike = jsonObject.optString("like");
            cDislike = jsonObject.optString("dislike");
            CID = jsonObject.optString("cid");
            UID = jsonObject.optString("uid");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String finalCID = CID;
        final String finalCommentBody = CommentBody;
        mViewHolder.tvDealCommentDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTextDialog(finalCommentBody);
            }
        });
        final String finalCLike = cLike;
        mViewHolder.tvDealCommentLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewHolder.tvDealCommentLike.setEnabled(false);
                mViewHolder.tvDealCommentDisLike.setEnabled(false);
                restServiceSetVote(finalCID, finalCLike, "like", mViewHolder.tvDealCommentLike, mViewHolder.tvDealCommentDisLike);
            }
        });
        final String finalCDislike = cDislike;
        mViewHolder.tvDealCommentDisLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewHolder.tvDealCommentLike.setEnabled(false);
                mViewHolder.tvDealCommentDisLike.setEnabled(false);
                restServiceSetVote(finalCID, finalCDislike, "dislike", mViewHolder.tvDealCommentLike, mViewHolder.tvDealCommentDisLike);
            }
        });
        final String finalUID = UID;
        final String finalFlag = Flag;
        mViewHolder.tvDealCommentFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewHolder.tvDealCommentFlag.setEnabled(false);
                restServiceSetFlag(finalCID, finalUID, finalFlag, mViewHolder.tvDealCommentFlag);
            }
        });

        // DealId = dealsGetterSetter.getComment();
      /*  mViewHolder.tvDealCommentFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
        //imageLoader.DisplayImage(dealsGetterSetter.getImgPath(), mViewHolder.ivDealCommentUserImage);
        //  Picasso.with(context).load(dealsGetterSetter.getImgPath()).placeholder(R.mipmap.ic_launcher).noFade().  resize(100, 100).centerInside().into(mViewHolder.ivDealCommentUserImage);


        return convertView;
    }

    private void restServiceSetVote(final String CID, final String cLikeOrDislike, final String LikeNDislike, final TextView tvLike, final TextView tvDislike) {
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

        HMVoteValue.put("entity_id", CID);
        HMVoteValue.put("entity_type", "comment");
        HMVoteValue.put("value_type", "points");
        HMVoteValue.put("tag", LikeNDislike);
        HMVoteValue.put("value", "1");

        ALHMVote.add(HMVoteValue);
        HMVotes.put("votes", ALHMVote);

        Log.i("Request Votes Data:", HMVotes.toString());
        api.setVotes(HMVotes, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject result, Response response) {
                tvLike.setEnabled(true);
                tvDislike.setEnabled(true);
                utilityClass.processDialogStop();
                try {
                    if (!result.toString().contains("You have already")) {
                        Log.d("Comment Result:", result.toString());
                        JSONObject JObject = new JSONObject(result.get("comment").toString());
                        JSONArray jsonArrayNew = JObject.getJSONArray(CID);
                        if (jsonArrayNew.length() == 3) {
                            if (LikeNDislike.equalsIgnoreCase("Like")) {
                                if (cLikeOrDislike.equalsIgnoreCase(jsonArrayNew.getJSONObject(2).optString("value")))
                                    utilityClass.toast("You have already liked for this comment");
                                else
                                    tvLike.setText("Like(" + jsonArrayNew.getJSONObject(2).optString("value") + ")");
                            } else {
                                if (cLikeOrDislike.equalsIgnoreCase(jsonArrayNew.getJSONObject(2).optString("value")))
                                    utilityClass.toast("You have already disliked for this comment");
                                else
                                    tvDislike.setText("Dislike(" + jsonArrayNew.getJSONObject(2).optString("value") + ")");
                            }
                        } else {
                            if (LikeNDislike.equalsIgnoreCase("Like")) {
                                if (cLikeOrDislike.equalsIgnoreCase(jsonArrayNew.getJSONObject(5).optString("value")))
                                    utilityClass.toast("You have already liked for this comment");
                            } else {
                                if (cLikeOrDislike.equalsIgnoreCase(jsonArrayNew.getJSONObject(2).optString("value")))
                                    utilityClass.toast("You have already disliked for this comment");
                            }
                            tvLike.setText("Like(" + jsonArrayNew.getJSONObject(5).optString("value") + ")");
                            tvDislike.setText("Dislike(" + jsonArrayNew.getJSONObject(2).optString("value") + ")");
                        }
                    } else {
                        if (result.toString().contains("disliked"))
                            utilityClass.toast("You have already disliked this comment!");
                        else
                            utilityClass.toast("You have already liked this comment!");
                    }
                } catch (IllegalStateException e) {
                    Log.e("IllegalStateException:", e.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Restrofit Error  : ", String.valueOf(error.toString()));
                tvLike.setEnabled(true);
                tvDislike.setEnabled(true);
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

    private void restServiceSetFlag(final String CID, String UID, String flag, final TextView tvFlag) {
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
        HashMap<String, String> HMFlagValue = new HashMap<String, String>();
        final Boolean[] bool = new Boolean[1];
        bool[0] = false;
        HMFlagValue.put("flag_name", "abusive_flag");
        HMFlagValue.put("entity_id", CID);
        if (flag.equalsIgnoreCase("Yes")) {
            HMFlagValue.put("action", "unflag");
        } else {
            HMFlagValue.put("action", "flag");
        }
        // HMFlagValue.put("uid", SaveSharedPreferences.getUserID(context));
        final String f = flag;

        Log.i("Request Votes Data:", HMFlagValue.toString());
        api.setFlags(HMFlagValue, new Callback<Object>() {
            @Override
            public void success(Object result, Response response) {
                utilityClass.processDialogStop();
                bool[0] = true;
                Log.i("Comment Flag OBJ :", String.valueOf(result));
                //  utilityClass.toast("User logout success");
                Log.i("Comment Flag Success:", String.valueOf(result));
                if (f.equalsIgnoreCase("Yes")) {
                    tvFlag.setCompoundDrawablesWithIntrinsicBounds(R.drawable.deal_details_red_flag, 0, 0, 0);
                } else {
                    tvFlag.setCompoundDrawablesWithIntrinsicBounds(R.drawable.deal_details_blue_flag, 0, 0, 0);
                }
                tvFlag.setEnabled(true);

            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Restrofit Error  : ", String.valueOf(error.toString()));
                tvFlag.setEnabled(true);
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

    private void setTextDialog(String CommentBody) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.set_text_cell);
        final EditText etSetText = (EditText) dialog.findViewById(R.id.etSetText);
        TextView tvSetTextOk = (TextView) dialog.findViewById(R.id.tvSetTextOk);
        etSetText.setFocusable(false);
        etSetText.setText(CommentBody);
        tvSetTextOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public class MyViewHolder {

        @InjectView(R.id.ivDealCommentUserImage)
        ImageView ivDealCommentUserImage;

        @InjectView(R.id.tvDealCommentUserName)
        TextView tvDealCommentUserName;
        @InjectView(R.id.tvDealCommentTime)
        TextView tvDealCommentTime;
        @InjectView(R.id.tvDealCommentDetails)
        TextView tvDealCommentDetails;
        @InjectView(R.id.tvDealCommentLike)
        TextView tvDealCommentLike;
        @InjectView(R.id.tvDealCommentDisLike)
        TextView tvDealCommentDisLike;
        @InjectView(R.id.tvDealCommentFlag)
        TextView tvDealCommentFlag;
        /*  @InjectView(R.id.tvDealCommentFollow)
          TextView tvDealCommentFollow;*/
        @InjectView(R.id.tvUserTotalComment)
        TextView tvUserTotalComment;


        public MyViewHolder(View view) {
            ButterKnife.inject(this, view);

        }
    }
}
