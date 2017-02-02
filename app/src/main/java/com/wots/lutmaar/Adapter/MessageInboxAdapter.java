package com.wots.lutmaar.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wots.lutmaar.CustomView.CustomTextView;
import com.wots.lutmaar.R;
import com.wots.lutmaar.UtilClass.UtilityClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Super Star on 25-08-2015.
 */
public class MessageInboxAdapter extends BaseAdapter {
    Context context;
    UtilityClass utilityClass;
    LinkedList<JSONObject> MessagesArray;


    public MessageInboxAdapter(Context context, LinkedList<JSONObject> JArray) {
        this.context = context;
        this.MessagesArray = JArray;
    }

    @Override
    public int getCount() {
        return MessagesArray.size();
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
        JSONObject jsonObject = null ;
        String UserNameList = "";

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.message_list_cell, null);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }
        try {
            jsonObject = MessagesArray.get(position);
            JSONArray jsonArray = jsonObject.getJSONArray("participants");
            // mViewHolder.tvMessageUserName.setText("");
            if(jsonArray.length()==1){
                Picasso.with(context).load(jsonArray.getJSONObject(0).optString("picture")).placeholder(R.mipmap.ic_launcher).noFade().fit().centerInside().into(mViewHolder.ivMessageUserImage);
            }else{
                Picasso.with(context).load(R.drawable.ic_group_chat).placeholder(R.drawable.ic_group_chat).noFade().fit().centerInside().into(mViewHolder.ivMessageUserImage);
            }
            for (int i = 0; i < jsonArray.length(); i++) {
                if (i == (jsonArray.length() - 1)) {
                    UserNameList = UserNameList + jsonArray.getJSONObject(i).optString("participant");

                } else {
                    UserNameList = UserNameList + jsonArray.getJSONObject(i).optString("participant") + ", ";

                }

                //  mViewHolder.tvMessageUserName.setText(mViewHolder.tvMessageUserName.getText().toString()+jsonArray.getJSONObject(i).optString("participant")+", ");
            }
            mViewHolder.tvMessageUserName.setText(UserNameList);
            if (jsonObject.optString("has_new").equalsIgnoreCase("1")) {
                setFontStyle(mViewHolder.tvMessageUserName, mViewHolder.tvMessageDetails);
            } else {
                setFontStyleNormal(mViewHolder.tvMessageUserName, mViewHolder.tvMessageDetails);
            }
            mViewHolder.tvMessageTime.setText(jsonObject.optString("last_updated"));
            mViewHolder.tvMessageDetails.setText(jsonObject.optString("subject"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }

    public void setFontStyle(TextView name, TextView Sub) {
        name.setTypeface(null, Typeface.BOLD);
        name.setTextColor(context.getResources().getColor(R.color.orange));
        Sub.setTextColor(context.getResources().getColor(R.color.orange));
        Sub.setTypeface(null, Typeface.BOLD);
    }

    public void setFontStyleNormal(TextView name, TextView Sub) {
        name.setTypeface(null, Typeface.NORMAL);
        name.setTextColor(context.getResources().getColor(R.color.black));
        Sub.setTextColor(context.getResources().getColor(R.color.tw__medium_gray));
        Sub.setTypeface(null, Typeface.NORMAL);
    }

    public class MyViewHolder {

        @InjectView(R.id.ivMessageUserImage)
        ImageView ivMessageUserImage;

        @InjectView(R.id.tvMessageUserName)
        CustomTextView tvMessageUserName;
        @InjectView(R.id.tvMessageTime)
        CustomTextView tvMessageTime;
        @InjectView(R.id.tvMessageDetails)
        CustomTextView tvMessageDetails;


        public MyViewHolder(View view) {
            ButterKnife.inject(this, view);

        }
    }
}
