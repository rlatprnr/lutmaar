package com.wots.lutmaar.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.wots.lutmaar.R;
import com.wots.lutmaar.UtilClass.Singleton;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Super Star on 25-07-2015.
 */
public class SelectCategoryAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<HashMap<String, String>> CategoryList;
    private Dialog dialog;
    private ItemFilter mFilter = new ItemFilter();
    TextView etCityDisplay;
    String CategoryID;

    public SelectCategoryAdapter(Context context, Dialog dialog, TextView etCity, String catID, ArrayList<HashMap<String, String>> CategoryList) {
        this.context = context;
        this.CategoryList = CategoryList;
        this.dialog = dialog;
        this.etCityDisplay = etCity;
        this.CategoryID = catID;

    }

    @Override
    public int getCount() {
        return CategoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return CategoryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;
        final String CatName, CatID;
        if (convertView == null) {
            LayoutInflater mInflater = LayoutInflater.from(context);

            convertView = mInflater.inflate(R.layout.city_cell, null);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }
        CatName = CategoryList.get(position).get("name");
        CatID = CategoryList.get(position).get("ID");
        mViewHolder.tvCityName.setText(CategoryList.get(position).get("name"));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etCityDisplay.setText(CatName);
                CategoryID = CatName;
                dialog.dismiss();
            }
        });

        return convertView;
    }

    public class MyViewHolder {

        @InjectView(R.id.tvCityName)
        TextView tvCityName;


        public MyViewHolder(View view) {
            ButterKnife.inject(this, view);

        }
    }

    public Filter getFilter() {

        return mFilter;
    }

    public class ItemFilter extends Filter {

        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();


            list = Singleton.GetInstance().getHMCategory();

            int count = list.size();
            final ArrayList<HashMap<String, String>> nlist = new ArrayList<HashMap<String, String>>(count);

            String filterableString;
            HashMap<String, String> data = new HashMap<String, String>();;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i).get("name");
                data.put("name",list.get(i).get("name"));
                data.put("ID",list.get(i).get("ID"));
                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(data);
                }
            }
            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            CategoryList = (ArrayList<HashMap<String, String>>) results.values;
            notifyDataSetChanged();
        }
    }


}
