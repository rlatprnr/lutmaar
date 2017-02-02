package com.wots.lutmaar.Activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.wots.lutmaar.GetterSetter.DealsGetterSetter;
import com.wots.lutmaar.Interface.RestServices;
import com.wots.lutmaar.R;
import com.wots.lutmaar.UtilClass.ConstantClass;
import com.wots.lutmaar.UtilClass.UtilityClass;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RestAdapter;

public class DealDetailsActivity extends ActionBarActivity {
    //Class
    UtilityClass utilityClass;
    ConstantClass constantClass;
    Bundle dealDetailsBundle = new Bundle();
    JSONObject jsonObject;
    JSONObject objImage;
    DealsGetterSetter dealsGetterSetter=new DealsGetterSetter();

    //Interface
    RestServices api;
    RestAdapter restAdapter;

    //View
    @InjectView(R.id.ivDealDetailsImage)
    ImageView ivDealDetailsImage;
    @InjectView(R.id.ivDealDetailsHot)
    ImageView ivDealDetailsHot;
    @InjectView(R.id.ivDealDetailsCold)
    ImageView ivDealDetailsCold;
    @InjectView(R.id.tvDealDetailsUserName)
    TextView tvDealDetailsUserName;
    @InjectView(R.id.tvDealDetailsTitle)
    TextView tvDealDetailsTitle;
    @InjectView(R.id.tvDealDetailsDetails)
    TextView tvDealDetailsDetails;
    @InjectView(R.id.tvDealDetailsHotCold)
    TextView tvDealDetailsHotCold;
    @InjectView(R.id.tvDealDetailsPrice)
    TextView tvDealDetailsPrice;
    @InjectView(R.id.tvDealDetailsComment)
    TextView tvDealDetailsComment;
    @InjectView(R.id.tvDealDetailsTime)
    TextView tvDealDetailsTime;
    @InjectView(R.id.toolbarDealDetails)
    Toolbar toolbarDealDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deal_details_activity);
        ButterKnife.inject(this);
        declaration();
        initialization();
    }

    private void declaration() {
        utilityClass = new UtilityClass(this);
        setSupportActionBar(toolbarDealDetails);
        getSupportActionBar().setTitle("     ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dealDetailsBundle = getIntent().getExtras();
        try {
            jsonObject = new JSONObject(dealDetailsBundle.getString("dealObject"));
            objImage = jsonObject.getJSONObject("field_images");
            dealsGetterSetter = new DealsGetterSetter(
                    String.valueOf(jsonObject.getString("name_1")), jsonObject.getString("title"),
                    jsonObject.getString("body"),
                    jsonObject.getString("value"), jsonObject.getString("nid"),
                    jsonObject.getString("created"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void initialization() {
       tvDealDetailsUserName.setText(dealsGetterSetter.getUserName());
        tvDealDetailsTitle.setText(dealsGetterSetter.getTitle());
        tvDealDetailsDetails.setText(dealsGetterSetter.getBody());
       // tvDealDetailsPrice.setText("$"+dealsGetterSetter.getPrice());
        tvDealDetailsHotCold.setText(dealsGetterSetter.getHotCold());
        tvDealDetailsComment.setText(dealsGetterSetter.getComment()+" Comment");
        tvDealDetailsTime.setText("Made Hot " + dealsGetterSetter.getCreated());
    }

  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_deal_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}
