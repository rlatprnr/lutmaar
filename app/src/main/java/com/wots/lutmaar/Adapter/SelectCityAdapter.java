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
import com.wots.lutmaar.UtilClass.SaveSharedPreferences;
import com.wots.lutmaar.UtilClass.Singleton;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Super Star on 18-07-2015.
 */
public class SelectCityAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> CityName;
    private Dialog dialog;
    private ItemFilter mFilter = new ItemFilter();
    TextView tvCityDisplay;
    String FromCall, CityOrCategory;

    public SelectCityAdapter(Context context, Dialog dialog, TextView tvCity, String fromCall, String CityOrCategory, ArrayList<String> CityName) {
        this.context = context;
        this.CityName = CityName;
        this.dialog = dialog;
        this.tvCityDisplay = tvCity;
        this.FromCall = fromCall;
        this.CityOrCategory = CityOrCategory;

    }

    @Override
    public int getCount() {
        return CityName.size();
    }

    @Override
    public Object getItem(int position) {
        return CityName.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;
        final String cityName;
        if (convertView == null) {
            LayoutInflater mInflater = LayoutInflater.from(context);

            convertView = mInflater.inflate(R.layout.city_cell, null);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }
        cityName = CityName.get(position);
        mViewHolder.tvCityName.setText(CityName.get(position));
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvCityDisplay.setText(cityName);
                if (FromCall.equalsIgnoreCase("Deal") && CityOrCategory.equalsIgnoreCase("City")) {
                    SaveSharedPreferences.setCityName(context, cityName);
                    Singleton.GetInstance().setCurrentPosition(Singleton.GetInstance().getCurrentPosition() - 1);
                    Singleton.GetInstance().FragmentCall(Singleton.GetInstance().getCurrentPosition() + 1, "");
                    dialog.dismiss();
                } else if (FromCall.equalsIgnoreCase("Deal") && CityOrCategory.equalsIgnoreCase("Category")) {
                    Singleton.GetInstance().setCurrentPosition(Singleton.GetInstance().getCurrentPosition() - 1);
                    Singleton.GetInstance().FragmentCall(Singleton.GetInstance().getCurrentPosition() + 1, "");
                    dialog.dismiss();
                } else if (FromCall.equalsIgnoreCase("SearchResult") && CityOrCategory.equalsIgnoreCase("Category")) {
                 /*   Singleton.GetInstance().setCurrentPosition(Singleton.GetInstance().getCurrentPosition() - 1);
                    Singleton.GetInstance().FragmentCall(Singleton.GetInstance().getCurrentPosition() + 1, "");*/
                    dialog.dismiss();
                }  else if (FromCall.equalsIgnoreCase("Search") && CityOrCategory.equalsIgnoreCase("City")) {
                 /*   Singleton.GetInstance().setCurrentPosition(Singleton.GetInstance().getCurrentPosition() - 1);
                    Singleton.GetInstance().FragmentCall(Singleton.GetInstance().getCurrentPosition() + 1, "");*/
                    dialog.dismiss();
                }  else if (FromCall.equalsIgnoreCase("MyAccount") && CityOrCategory.equalsIgnoreCase("City")) {
                 /*   Singleton.GetInstance().setCurrentPosition(Singleton.GetInstance().getCurrentPosition() - 1);
                    Singleton.GetInstance().FragmentCall(Singleton.GetInstance().getCurrentPosition() + 1, "");*/
                    dialog.dismiss();
                } else if (!cityName.equalsIgnoreCase("all")) {
                    dialog.dismiss();

                }


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

        ArrayList<String> list = new ArrayList<String>();

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            if (CityOrCategory.equalsIgnoreCase("City"))
                list = Singleton.GetInstance().getSelectCityName();
            else if (CityOrCategory.equalsIgnoreCase("Category"))
                list = Singleton.GetInstance().getCategoryList();

            // list = Singleton.GetInstance().getSelectCityName();

            int count = list.size();
            final ArrayList<String> nlist = new ArrayList<String>(count);

            String filterableString;
            String data;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i);
                data = list.get(i);
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
            CityName = (ArrayList<String>) results.values;
            notifyDataSetChanged();
        }
    }


}
