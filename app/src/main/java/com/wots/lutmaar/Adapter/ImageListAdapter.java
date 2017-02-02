package com.wots.lutmaar.Adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.wots.lutmaar.CustomView.ImageLoader.ImageLoader;
import com.wots.lutmaar.R;
import com.wots.lutmaar.UtilClass.UtilityClass;

import java.util.ArrayList;

/**
 * Created by NIK on 25-02-2015.
 */
public class ImageListAdapter extends PagerAdapter {
    //Variable
    private ArrayList<String> ImageList;

    //Interface
    private Context context;
    private LayoutInflater inflater;
    public ImageLoader imageLoader;
    //Class
    UtilityClass utilityClass;


    public ImageListAdapter(ArrayList<String> imageList, Context context) {
        super();
        ImageList = imageList;
        this.context = context;
        Log.i("Response", "Cunstructore");
        Log.i("Image List Size", String.valueOf(imageList.size()));
        utilityClass = new UtilityClass(context);
        try {
            imageLoader = new ImageLoader(context.getApplicationContext());
        } catch (NullPointerException e) {
            Log.e("Image Cache Error: ", e.toString());

        }
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
        final ImageView imgDisplay;
        final ProgressBar progressbarFullImage;
        View viewLayout;

        //Variable
        final String  imgPath;


        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewLayout = inflater.inflate(R.layout.imageview_cell, container, false);

        imgDisplay = (ImageView) viewLayout.findViewById(R.id.ivDealDetailsFullImage);
        progressbarFullImage = (ProgressBar) viewLayout.findViewById(R.id.progressbarFullImage);
        progressbarFullImage.setVisibility(View.VISIBLE);
        Log.i("Response", "Redy to Picasso");

        imgPath =  ImageList.get(position);
        Log.i("Image Path", imgPath);
      // imageLoader.DisplayImage(imgPath,imgDisplay);
       Picasso.with(context).load(imgPath).noFade()
               .resize(utilityClass.GetWidth(), utilityClass.GetHeight())
               .centerInside().into(imgDisplay,  new Callback() {

            @Override
            public void onSuccess() {
                progressbarFullImage.setVisibility(View.GONE);
            }
            @Override
            public void onError() {
                // TODO Auto-generated method stub
                utilityClass.toast("Image is not available!!");
            }
        });

        ((ViewPager) container).addView(viewLayout);
        return viewLayout;

    }


}
