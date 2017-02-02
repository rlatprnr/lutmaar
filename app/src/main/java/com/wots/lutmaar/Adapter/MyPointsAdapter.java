package com.wots.lutmaar.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wots.lutmaar.R;
import com.wots.lutmaar.UtilClass.UtilityClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Super Star on 13-07-2015.
 */
public class MyPointsAdapter extends BaseAdapter {

    Context context;
    UtilityClass utilityClass;
    JSONArray PointsArray;

    public MyPointsAdapter(Context context, JSONArray JArray) {
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
        String[] splitPointDate;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.my_points_cell, null);

            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        try {
            jsonObject = PointsArray.getJSONObject(position);

            mViewHolder.tvMyPointsPoints.setText(jsonObject.getString("points"));
            mViewHolder.tvMyPointsCategory.setText("Deal");
            splitPointDate = jsonObject.getString("time_stamp").split("-");
            mViewHolder.tvMyPointsDate.setText(splitPointDate[1]+"-"+splitPointDate[0]+"-"+splitPointDate[2]);
            mViewHolder.tvMyPointsReason.setText("Won Contest");
            mViewHolder.tvMyPointsStatus.setText( jsonObject.getString("status")  );
            mViewHolder.tvMyPointsActions.setText(jsonObject.getString("operation"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return convertView;
    }

    public class MyViewHolder {

        @InjectView(R.id.tvMyPointsPoints)
        TextView tvMyPointsPoints;
        @InjectView(R.id.tvMyPointsCategory)
        TextView tvMyPointsCategory;
        @InjectView(R.id.tvMyPointsDate)
        TextView tvMyPointsDate;
        @InjectView(R.id.tvMyPointsReason)
        TextView tvMyPointsReason;
        @InjectView(R.id.tvMyPointsStatus)
        TextView tvMyPointsStatus;
        @InjectView(R.id.tvMyPointsActions)
        TextView tvMyPointsActions;

        public MyViewHolder(View view) {
            ButterKnife.inject(this, view);

        }
    }
}
