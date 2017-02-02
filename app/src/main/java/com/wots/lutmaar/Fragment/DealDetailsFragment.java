package com.wots.lutmaar.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wots.lutmaar.Adapter.ViewPagerAdapter;
import com.wots.lutmaar.CustomView.SlidingTabLayout;
import com.wots.lutmaar.R;
import com.wots.lutmaar.UtilClass.UtilityClass;

import butterknife.ButterKnife;

public class DealDetailsFragment extends Fragment {

    //Class
    UtilityClass utilityClass;
    // Declaring Your View and Variables
    ViewPager pagerDealDetails;
    ViewPagerAdapter adapter;
    // SlidingTabLayout tabs = new SlidingTabLayout(getActivity());
    CharSequence Titles[] = {"INFO", "REVIEWS"};
    int Numboftabs = 2;
    String DealID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        DealID = getArguments().getString("DealID");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.deal_details_fragment, container, false);
        ButterKnife.inject(this, view);
        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), "DealDetails", 1, Titles, Numboftabs, getActivity());

        // Assigning ViewPager View and setting the adapter

        pagerDealDetails = (ViewPager) view.findViewById(R.id.pagerDealDetails);

        pagerDealDetails.setAdapter(adapter);
        pagerDealDetails.setCurrentItem(1);


        // Assiging the Sliding Tab Layout View
        SlidingTabLayout tabs = (SlidingTabLayout) view.findViewById(R.id.
                tabsDealDetails);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.white);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pagerDealDetails);
        return view;
    }


}
