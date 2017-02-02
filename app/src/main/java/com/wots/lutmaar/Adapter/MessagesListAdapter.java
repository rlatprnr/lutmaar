package com.wots.lutmaar.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wots.lutmaar.R;
import com.wots.lutmaar.UtilClass.SaveSharedPreferences;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MessagesListAdapter extends BaseAdapter {

    Context context;
    LinkedList<JSONObject> DealsArray;
    int UserCount =0 ;

    public MessagesListAdapter(Context context, int userCount,LinkedList<JSONObject> JArray) {
        this.context = context;
        this.DealsArray = JArray;
        UserCount = userCount;

    }

    @Override
    public int getCount() {
        return DealsArray.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final MyViewHolder mViewHolder;
        Date date = new Date();

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (DealsArray.get(position).optString("author_uid").equalsIgnoreCase(SaveSharedPreferences.getUserID(context))) {
            // message belongs to you, so load the right aligned layout
            convertView = mInflater.inflate(R.layout.list_item_message_right, null);
        } else {
            // message belongs to other person, load the left aligned layout
            convertView = mInflater.inflate(R.layout.list_item_message_left, null);
        }
        mViewHolder = new MyViewHolder(convertView);
        convertView.setTag(mViewHolder);

        SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy hh:mm");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa");
        SimpleDateFormat DateFormat = new SimpleDateFormat("dd MMM yyyy");

        try {
            date = format.parse(DealsArray.get(position).optString("sending_time"));


            if (DateFormat.format(date).equals(DateFormat.format(new Date()))){
                mViewHolder.tvChatSendingTime.setText(timeFormat.format(date));
            }else{
                mViewHolder.tvChatSendingTime.setText(DateFormat.format(date)+" "+timeFormat.format(date));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(UserCount==1){
            mViewHolder.tvChatUserName.setVisibility(View.GONE);
        }else {
            if (!DealsArray.get(position).optString("author").equalsIgnoreCase("You")) {
                mViewHolder.tvChatUserName.setText(DealsArray.get(position).optString("author"));
            }
        }
        mViewHolder.tvChatMassage.setText(DealsArray.get(position).optString("body"));


        return convertView;
    }

    public class MyViewHolder {
        @InjectView(R.id.tvChatUserName)
        TextView tvChatUserName;
        @InjectView(R.id.tvChatMassage)
        TextView tvChatMassage;
        @InjectView(R.id.tvChatSendingTime)
        TextView tvChatSendingTime;


        public MyViewHolder(View view) {
            ButterKnife.inject(this, view);

        }
    }
}
