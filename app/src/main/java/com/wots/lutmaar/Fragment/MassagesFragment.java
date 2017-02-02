package com.wots.lutmaar.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.wots.lutmaar.Adapter.MessageInboxAdapter;
import com.wots.lutmaar.Interface.RestServices;
import com.wots.lutmaar.R;
import com.wots.lutmaar.UtilClass.ConstantClass;
import com.wots.lutmaar.UtilClass.SaveSharedPreferences;
import com.wots.lutmaar.UtilClass.Singleton;
import com.wots.lutmaar.UtilClass.UtilityClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import fr.castorflex.android.circularprogressbar.CircularProgressDrawable;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MassagesFragment extends Fragment {

    //Class
    UtilityClass utilityClass;
    ConstantClass constantClass;
    MessageInboxAdapter adapter;
    LinkedList<JSONObject> linkListChat = new LinkedList<JSONObject>();

    //Interface
    RestServices api;
    RestAdapter restAdapter;
    //View
    @InjectView(R.id.lvMessages)
    ListView lvMessages;
    @InjectView(R.id.pbMessage)
    SmoothProgressBar pbMessage;
    @InjectView(R.id.pbRoundMessage)
    CircularProgressBar pbRoundMessage;
    @InjectView(R.id.tvMessageDataNotFound)
    TextView tvMessageDataNotFound;

    //Variable
    int pageNo = 0;
    int pageEnd = 0;
    boolean pageScroll = true;
    private int NetworkErrorCount = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.massages_fragment, container, false);
        ButterKnife.inject(this, view);
        //  utilityClass = new UtilityClass(getActivity());
        declaration();
        return view;
    }

    private void declaration() {
        utilityClass = new UtilityClass(getActivity());
        pbMessage.progressiveStart();
        ((CircularProgressDrawable) pbRoundMessage.getIndeterminateDrawable()).start();
        //jsonArray = new JSONArray();
        adapter = new MessageInboxAdapter(getActivity(), linkListChat);
        lvMessages.setAdapter(adapter);
        lvMessages.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                final int lastItem = firstVisibleItem + visibleItemCount;
                if (lastItem == totalItemCount && pageScroll) {
                    pageScroll = false;
                    if (pageEnd == 0)
                        initialization();

                }
            }
        });
    }

    private void initialization() {
        if (utilityClass.isInternetConnection()) {
            if (pageNo == 0) {
                pbMessage.setVisibility(View.VISIBLE);
            } else {
                pbRoundMessage.setVisibility(View.VISIBLE);
            }
            NetworkErrorCount = 0;
            restService();
        } else {
            pbMessage.setVisibility(View.GONE);
            pbRoundMessage.setVisibility(View.GONE);
            //networkLossDialogShow();
        }

    }

    private void restService() {

        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Content-Type", "application/json");
                request.addHeader("X-CSRF-Token", SaveSharedPreferences.getTokon(getActivity()));
                request.addHeader("Cookie", SaveSharedPreferences.getCookie(getActivity()));

                Log.i("User List Tokon : ", SaveSharedPreferences.getTokon(getActivity()));
                Log.i("User List Cookie : ", SaveSharedPreferences.getCookie(getActivity()));
            }
        };

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(constantClass.endPoints)
                .setRequestInterceptor(requestInterceptor)
                .build();
        api = restAdapter.create(RestServices.class);
        HashMap<String, String> HMUserID = new HashMap<String, String>();
        HMUserID.put("uid", SaveSharedPreferences.getUserID(getActivity()));
        api.getAllUserInbox(HMUserID, String.valueOf(pageNo), new Callback<JsonArray>() {
            @Override
            public void success(JsonArray result, Response response) {
                pbMessage.setVisibility(View.GONE);
                pbRoundMessage.setVisibility(View.GONE);
                NetworkErrorCount = 0;
                try {
                    JSONArray jsonArrayNew = new JSONArray(result.getAsJsonArray().toString());
                    if (jsonArrayNew.length() != 0) {

                        if (jsonArrayNew.length() < 10) {
                            pageEnd = 1;
                        }

                        for (int i = 0; i < jsonArrayNew.length(); i++) {
                            if (jsonArrayNew.getJSONObject(i).optString("has_new").equalsIgnoreCase("1")) {
                                linkListChat.addFirst(jsonArrayNew.getJSONObject(i));
                            } else {
                                linkListChat.addLast(jsonArrayNew.getJSONObject(i));
                            }
                        }

                        adapter.notifyDataSetChanged();
                        pageNo++;
                        pageScroll = true;
                    } else if (pageNo == 0) {
                        pageEnd = 1;
                        tvMessageDataNotFound.setVisibility(View.VISIBLE);
                    } else {
                        pageEnd = 1;
                    }
                } catch (IllegalStateException e) {
                    Log.e("IllegalStateException:", e.toString());
                } catch (JSONException e) {
                    Log.e("JSONException:", e.toString());
                }
            }

            @Override
            public void failure(RetrofitError error) {

                Log.i("Retrofit Error  : ", String.valueOf(error.toString() + " Network " + RetrofitError.Kind.NETWORK + " : " + error.isNetworkError()));
                if (error.isNetworkError()) {
                    if (NetworkErrorCount >= 3) {
                        utilityClass.toast("Network error, please try again!!!.");
                        pbMessage.setVisibility(View.GONE);
                        pbRoundMessage.setVisibility(View.GONE);
                        NetworkErrorCount = 0;
                    } else {
                        NetworkErrorCount++;
                        if (pageNo == 0) {
                            pbMessage.setVisibility(View.VISIBLE);
                        } else {
                            pbRoundMessage.setVisibility(View.VISIBLE);
                        }
                        restService();
                    }
                } else {
                    pbMessage.setVisibility(View.GONE);
                    pbRoundMessage.setVisibility(View.GONE);
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
            }
        });
    }

    @OnItemClick(R.id.lvMessages)
    public void setLvMessages(int position) {
        Singleton.GetInstance().setHomeMenuFragmentPosition(1);
        Singleton.GetInstance().MenuHide();
        Singleton.GetInstance().FragmentCall(20, String.valueOf(linkListChat.get(position).toString()));

    }

    @Override
    public void onResume() {
        super.onResume();
        Singleton.GetInstance().BottomMenuHide();
        Singleton.GetInstance().getTvMainHeading().setText("Massages");

    }


}
