package com.wots.lutmaar.Adapter;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.wots.lutmaar.Fragment.AllDealsFragment;
import com.wots.lutmaar.Fragment.AskFragment;
import com.wots.lutmaar.Fragment.CompetitionFragment;
import com.wots.lutmaar.Fragment.DealsFragment;
import com.wots.lutmaar.Fragment.FreebiesFragment;
import com.wots.lutmaar.Fragment.VouchersFragment;
import com.wots.lutmaar.UtilClass.Singleton;

/**
 * Created by Bhavesh on 19-04-2015.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created
    Context context;


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm, String HeadingName,int index,  CharSequence mTitles[], int mNumbOfTabsumb, Context con1) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;
        this.context = con1;
        Singleton.GetInstance().getTvMainHeading().setText(HeadingName);
        Singleton.GetInstance().setCurrentPosition(index + 7);

    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        Bundle arg = new Bundle();
        switch (position) {

            case 0:
               /* arg.putString("cinemaType", "Bollywood");
                if (TrailersOrSongs.equalsIgnoreCase("Trailers"))
                    fragment = new CategoryTrailerMovieListFragment();
                else if (TrailersOrSongs.equalsIgnoreCase("Songs"))*/
                    fragment = new AllDealsFragment();
                break;
            case 1:
               /* arg.putString("cinemaType", "Hollywood");
                if (TrailersOrSongs.equalsIgnoreCase("Trailers"))
                    fragment = new CategoryTrailerMovieListFragment();
                else if (TrailersOrSongs.equalsIgnoreCase("Songs"))*/
                    fragment = new DealsFragment();
                break;
            case 2:
                    fragment = new VouchersFragment();
                break;
            case 3:
                fragment = new FreebiesFragment();
                break;
            case 4:
                fragment = new AskFragment();
                break;
            case 5:
                fragment = new CompetitionFragment();
                break;
        }
        fragment.setArguments(arg);
        return fragment;



    }

    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return NumbOfTabs;
    }

}
