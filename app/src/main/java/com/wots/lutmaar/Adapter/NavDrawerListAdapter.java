package com.wots.lutmaar.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.wots.lutmaar.CustomView.CustomTextView;
import com.wots.lutmaar.GetterSetter.NavDrawerItem;
import com.wots.lutmaar.R;
import com.wots.lutmaar.UtilClass.SaveSharedPreferences;
import com.wots.lutmaar.UtilClass.Singleton;

import java.util.ArrayList;

public class NavDrawerListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<NavDrawerItem> navDrawerItems;
    String LorR;

    public NavDrawerListAdapter(Context context, String lORr, ArrayList<NavDrawerItem> navDrawerItems) {
        this.context = context;
        this.navDrawerItems = navDrawerItems;
        this.LorR = lORr;
    }

    @Override
    public int getCount() {
        return navDrawerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return navDrawerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = LayoutInflater.from(context);
            if (LorR.equalsIgnoreCase("Left")) {
                convertView = mInflater.inflate(R.layout.drawer_nav_item_left, null);
            } else {
                convertView = mInflater.inflate(R.layout.drawer_nav_item_right, null);
            }
            // convertView.setBackgroundColor(Color.parseColor("#5C6BC0"));
            //convertView.setBackgroundColor(Color.TRANSPARENT);
        }
        if (LorR.equalsIgnoreCase("Left")) {
            setColor(convertView, position);

        }
        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.ivIcon);
        CustomTextView txtTitle = (CustomTextView) convertView.findViewById(R.id.tvTitle);
        if (position == 1 && LorR.equalsIgnoreCase("Right")) {
            Singleton.GetInstance().setIvMessage(imgIcon);
            Singleton.GetInstance().setTvMessage(txtTitle);
            if (!SaveSharedPreferences.getMessage(context).equalsIgnoreCase("no")) {
                imgIcon.setImageResource(R.drawable.ic_message1);
                txtTitle.setText("Messages"+SaveSharedPreferences.getMessage(context));
            } else {
                imgIcon.setImageResource(navDrawerItems.get(position).getIcon());
                txtTitle.setText(navDrawerItems.get(position).getTitle());
            }
        } else {
            imgIcon.setImageResource(navDrawerItems.get(position).getIcon());
            txtTitle.setText(navDrawerItems.get(position).getTitle());
        }


        return convertView;
    }

    void setColor(View cView, int p) {
        if (p == 0) {
            cView.setBackgroundColor(context.getResources().getColor(R.color.MenuOrange));
        } else if (p == 1) {
            cView.setBackgroundColor(context.getResources().getColor(R.color.MenuDarkBlue));
        } else if (p == 2) {
            cView.setBackgroundColor(context.getResources().getColor(R.color.MenuGreen));
        } else if (p == 3) {
            cView.setBackgroundColor(context.getResources().getColor(R.color.MenuRed));
        } else if (p == 4) {
            cView.setBackgroundColor(context.getResources().getColor(R.color.MenuMenuSkyBlue));
        } else if (p == 5) {
            cView.setBackgroundColor(context.getResources().getColor(R.color.MenuYello));
        } else {
            cView.setBackgroundColor(context.getResources().getColor(R.color.orange));
        }

    }
}