package com.wots.lutmaar.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wots.lutmaar.Adapter.ViewPagerAdapter;
import com.wots.lutmaar.R;
import com.wots.lutmaar.UtilClass.Singleton;
import com.wots.lutmaar.UtilClass.UtilityClass;

import butterknife.ButterKnife;

public class NewsFeed extends Fragment {
    /*
    7874834903  0315
    */
    //Class
    UtilityClass utilityClass;
    // Declaring Your View and Variables
    ViewPager pager;
    ViewPagerAdapter adapter;
    // SlidingTabLayout tabs = new SlidingTabLayout(getActivity());
    CharSequence Titles[] = {"All Latest Deals", "Latest Deals", "Latest Vouchers", "Latest Freebies", "Latest Responses", "Latest Contests"};
    int Numboftabs = 6;
    int Position = 0;
    String Index = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            Index = getArguments().getString("DealID");
        } catch (NullPointerException e) {
            Log.e("Index is Null: ", e.toString());
        }
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.news_feed_fragment, container, false);
        ButterKnife.inject(this, view);
        try {
            if (!Index.equalsIgnoreCase(""))
                Position = Integer.parseInt(Index);
        } catch (NullPointerException e) {
            Log.e("Index is NullError: ", e.toString());
        }



        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), String.valueOf(Titles[Position]), Position, Titles, Numboftabs, getActivity());
        Singleton.GetInstance().getLLCitySearch().setVisibility(View.VISIBLE);
        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) view.findViewById(R.id.pager);

        pager.setAdapter(adapter);
        pager.setCurrentItem(Position);
        Singleton.GetInstance().getTvDealCategoryName().setVisibility(View.VISIBLE);
        // Singleton.GetInstance().getTvDealCategoryName().setSelection(0);
        //  Singleton.GetInstance().getTvDealCategoryName().setText("Deal Categories");
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Singleton.GetInstance().getTvMainHeading().setText(Titles[position]);
                Singleton.GetInstance().setCurrentPosition(position + 7);
                Singleton.GetInstance().getTvDealCategoryName().setSelection(0);
                Singleton.GetInstance().setDealSubCategory("all");
                if (position == 0) {

                    Singleton.GetInstance().setDealType("all");
                } else if (position == 1) {
                    Singleton.GetInstance().setDealType("deals");
                } else if (position == 2) {
                    Singleton.GetInstance().setDealType("vouchers");
                } else if (position == 3) {
                    Singleton.GetInstance().setDealType("freebies");
                } else if (position == 4) {
                    Singleton.GetInstance().setDealType("all");
                } else if (position == 5) {
                    Singleton.GetInstance().setDealType("all");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

     /*   // Assiging the Sliding Tab Layout View
        SlidingTabLayout tabs = (SlidingTabLayout) view.findViewById(R.id.
                tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.white);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);*/
        return view;
    }


}
