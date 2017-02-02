package com.wots.lutmaar.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.JsonObject;
import com.wots.lutmaar.Adapter.AllTypeDealsAdapter;
import com.wots.lutmaar.Interface.RestServices;
import com.wots.lutmaar.R;
import com.wots.lutmaar.UtilClass.ConstantClass;
import com.wots.lutmaar.UtilClass.SaveSharedPreferences;
import com.wots.lutmaar.UtilClass.Singleton;
import com.wots.lutmaar.UtilClass.UtilityClass;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Timer;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PointsFragment extends Fragment  {

    //Class
    UtilityClass utilityClass;
    ConstantClass constantClass;
    Bundle homeBundle = new Bundle();
    AllTypeDealsAdapter adapter;
    Timer timer;
    JSONArray jsonArray;

    //Interface
    RestServices api;
    RestAdapter restAdapter;
    //View
    @InjectView(R.id.lvDeals)
    ListView lvDeals;
    @InjectView(R.id.pbDeals)
    SmoothProgressBar pbDeals;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.deals_fragment, container, false);
        ButterKnife.inject(this, view);
        //  utilityClass = new UtilityClass(getActivity());
        declaration();

        initialization();

        return view;
    }

    private void declaration() {
        utilityClass = new UtilityClass(getActivity());

    }

    private void initialization() {
        pbDeals.progressiveStart();
        pbDeals.setSmoothProgressDrawableColors(getResources().getIntArray(R.array.gplus_colors));
        restService();


    }


    private void restService() {

        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Content-Type", "application/json");
                request.addHeader("X-CSRF-Token", SaveSharedPreferences.getTokon(getActivity()));
                request.addHeader("Cookie", SaveSharedPreferences.getCookie(getActivity()));

                Log.i("Tokon : ", SaveSharedPreferences.getTokon(getActivity()));
                Log.i("Cookie : ", SaveSharedPreferences.getCookie(getActivity()));
            }
        };

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(constantClass.endPoints)
                .setRequestInterceptor(requestInterceptor)
                .build();
        api = restAdapter.create(RestServices.class);

        api.getPoints(new Callback<JsonObject>() {
            @Override
            public void success(JsonObject result, Response response) {
                pbDeals.progressiveStop();
                Singleton.GetInstance().setStartShake(true);
                try {

                    jsonArray = new JSONArray(result.getAsJsonArray("nodes").toString());
                    adapter = new AllTypeDealsAdapter(getActivity(), "Points", jsonArray);
                    lvDeals.setAdapter(adapter);
                } catch (IllegalStateException e) {
                    Log.e("IllegalStateException:", e.toString());
                } catch (JSONException e) {
                    Log.e("JSONException:", e.toString());
                }


            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Restrofit Error  : ", String.valueOf(error.toString()));
                pbDeals.progressiveStop();
                Singleton.GetInstance().setStartShake(true);

                if (error.toString().contains("No address associated with hostname")) {
                    //   tvError.setText("Please check your network connection or try again later");
                    utilityClass.toast("Please check your network connection or try again later.");
                } else if (error.toString().equals("retrofit.RetrofitError: 500 Internal Server Error")) {
                    utilityClass.toast("Internal Server Error");
                    //  tvError.setText("Internal Server Error");
                } else if (error.toString().contains("com.google.gson.JsonSyntaxException")) {
                    //tvError.setText("Result not found form server");
                    utilityClass.toast("Result not found form server");
                } else {
                    //tvError.setText("Invalid user or password");
                    utilityClass.toast("Something went wrong!!!");
                }
            }
        });
    }







}
