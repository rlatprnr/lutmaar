package com.wots.lutmaar.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wots.lutmaar.R;
import com.wots.lutmaar.UtilClass.UtilityClass;

import java.util.ArrayList;

/**
 * Created by Super Star on 28-07-2015.
 */
public class WelcomeImageListAdapter extends PagerAdapter {
    //Variable
    private ArrayList<Integer> ImageList;

    //Interface
    private Context context;
    private LayoutInflater inflater;
    Dialog dialog;
    //Class
    UtilityClass utilityClass;


    public WelcomeImageListAdapter(ArrayList<Integer> imageList,Dialog dialog, Context context) {
        super();
        ImageList = imageList;
        this.context = context;
        this.dialog = dialog;
        Log.i("Response", "Cunstructore");
        Log.i("Image List Size", String.valueOf(imageList.size()));
        utilityClass = new UtilityClass(context);
    }

    @Override
    public int getCount() {
        return ImageList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {

        return view == ((RelativeLayout) object);

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //super.destroyItem(container, position, object);
        ((ViewPager) container).removeView((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        Log.i("Response", "instantiateItem Function");
        //View
        final ImageView ivWelComeFullImage;
        final TextView tvWelComeSkip,tvWCPageTitle,WCTitle,WCSubTitle,tvWCContact;
        FrameLayout FLImageDisplay;
        View viewLayout;

        //Variable
        final int imgPath;


        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewLayout = inflater.inflate(R.layout.welcome_imageview_cell, container, false);
        FLImageDisplay = (FrameLayout) viewLayout.findViewById(R.id.FLImageDisplay);
        tvWCPageTitle = (TextView) viewLayout.findViewById(R.id.tvWCPageTitle);
        ivWelComeFullImage = (ImageView) viewLayout.findViewById(R.id.ivWelComeFullImage);
        WCTitle = (TextView) viewLayout.findViewById(R.id.WCTitle);
        WCSubTitle = (TextView) viewLayout.findViewById(R.id.WCSubTitle);
        tvWCContact = (TextView) viewLayout.findViewById(R.id.tvWCContact);
        tvWelComeSkip = (TextView) viewLayout.findViewById(R.id.tvWelComeSkip);

        if (position == 0) {

            FLImageDisplay.setBackgroundColor(context.getResources().getColor((R.color.wc1)));
            tvWCPageTitle.setText("Welcome to the\nLutMaar app!");
            WCSubTitle.setText(" Love to check out the coolest deals, gift vouchers, freebies, discounts and savings?\n" +
                    "        Want to grab that discount on your dream outfit? \nJust 'Tap' and 'Swipe' on your latest mobile app and indulge\n" +
                    "        in the luxury of \"window\" shopping in both the 'real' and the 'virtual' worlds!!! Yep!\n" +
                    "        We display all the best deals right in to your hands! (mobiles). Our mobile app ensures that there are no losers!");
            WCTitle.setVisibility(View.VISIBLE);
            tvWCContact.setVisibility(View.VISIBLE);
            tvWelComeSkip.setVisibility(View.VISIBLE);
        } else if (position == 1) {
            FLImageDisplay.setBackgroundColor(context.getResources().getColor((R.color.wc2)));
            tvWCPageTitle.setText("Blow Hot or Cold with your\nvaluable vote!");
            WCSubTitle.setText("Lutmaar.com believes in empowering shoppers!\nCast your vote - Create an impact!!\n" +
                    "Happy with the deal you found on lutmaar.com? Vote Hot - let others know!\n" +
                    "Not excited about a deal? Vote Cold!\n You and Your Reviews......that really make all the differences!!\n" +
                    "Use our Hote feature and create awareness.");
        } else if (position == 2) {
            FLImageDisplay.setBackgroundColor(context.getResources().getColor((R.color.wc3)));
            tvWCPageTitle.setText("Earn Rewards and Points");
            WCTitle.setText("Deals and steals.........will make you squeal with joy!!");
            WCSubTitle.setText("Bumped into a \"real cool deal\"?\n" +
                    "Use the lutmaar.com mobile app - Simply upload the deal!!\n" +
                    "Hold your breath.........Earn points and rewards!!\n" +
                    "With every deal that you upload - you will rewards!!\n" +
                    "Every deal that you check out on - you will points!!\n" +
                    "We believe in keeping track of your points!");
            WCTitle.setVisibility(View.VISIBLE);
            tvWCContact.setVisibility(View.VISIBLE);
        } else if (position == 3) {
            FLImageDisplay.setBackgroundColor(context.getResources().getColor((R.color.wc4)));
            tvWCPageTitle.setText("Have an Opinion - Share!!");
          //  WCTitle.setText("Deals and steals.........will make you squeal with joy!!");
            WCSubTitle.setText("There are 'virtually' million of shoppers you who want to discuss" +
                    "their experience regarding different deals.\nWe value your opinion and believe it makes a 'real' difference!!\n" +
                    "Our Deals Comments feature enables you to chat with other users openly!\n" +
                    "You can also upload deals that \"really\" or \"virtually\" wowed you on to your favourite social media.");


        } else if (position == 4) {
            FLImageDisplay.setBackgroundColor(context.getResources().getColor((R.color.wc5)));
            tvWCPageTitle.setText("Help the lutmaar.com\ncommunity");
            // WCTitle.setText("Deals and steals.........will make you squeal with joy!!");
            WCSubTitle.setText("Came across a real cool deal?\n" +
                    "Is that discount too good to pass over?\n" +
                    "Thousands of deals are uploaded daily by our users!\n" +
                    "Create a better community by submitting a deal you just came across and let others get the benefit too!\n\n" +
                    "User our like/dislike/spam flag/filter/comments feature and voice your thoughts openly!!\n" +
                    "Help us identify spam deals and users.........And Create a Really Reliable Platform!!  ");


        } else if (position == 5) {
            FLImageDisplay.setBackgroundColor(context.getResources().getColor((R.color.wc6)));
            tvWCPageTitle.setText("With Our Alerts\nYou are always Aware!!");
           // WCTitle.setText("Deals and steals.........will make you squeal with joy!!");
            WCSubTitle.setText("Interested in specific deals/members/vendors/products?\n" +
                    "Subscribe to our newsletter, alerts and/or follow other users to get instance notification!\n" +
                    "Grab your favourite deals before you miss out them!");


        } else if (position == 6) {
            FLImageDisplay.setBackgroundColor(context.getResources().getColor((R.color.wc1)));
            tvWCPageTitle.setText("Geo location and hyper local");
           // WCTitle.setText("Deals and steals.........will make you squeal with joy!!");
            WCSubTitle.setText("On an impulsive shopping expedition? Want to check out on the cool deals in the vicinity\n" +
                    "Use our lutmaar.com mobile app. Tap on the 'LOCAL' button and locate the hottest deals around you!\n" +
                    "Your GPS will direct you to the precise location!\n\n" +
                    "Shop! Splurge! Spend! Save!.....Indulge the shopaholic in you .....and SMILE.....Because now the \"loot\" is truly yours!\n" +
                    "It was entirely our pleasure.....to turn your dreams in a reality!");

            tvWelComeSkip.setVisibility(View.VISIBLE);
            tvWelComeSkip.setText("Start");
        }


        imgPath = ImageList.get(position);
        Log.i("Image Path", String.valueOf(imgPath));
        ivWelComeFullImage.setImageResource(imgPath);
        //  Picasso.with(context).load(imgPath).noFade().fit().centerInside().into(imgDisplay);
        tvWelComeSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ((ViewPager) container).addView(viewLayout);
        return viewLayout;

    }
}
