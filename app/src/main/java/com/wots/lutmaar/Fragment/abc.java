package com.wots.lutmaar.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.wots.lutmaar.Adapter.SearchResultAdapter;
import com.wots.lutmaar.Interface.RestServices;
import com.wots.lutmaar.R;
import com.wots.lutmaar.UtilClass.ConstantClass;
import com.wots.lutmaar.UtilClass.SaveSharedPreferences;
import com.wots.lutmaar.UtilClass.UtilityClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class abc extends Fragment {
    //Class
    UtilityClass utilityClass;
    ConstantClass constantClass;
    SearchResultAdapter adapter;
    JSONArray jsonArray, jsonArrayShort;
    JSONObject DealsObject;

    //Interface
    RestServices api;
    RestAdapter restAdapter;

    //View
    @InjectView(R.id.svChat)
    ScrollView svChat;
    @InjectView(R.id.tlChat)
    TableLayout tlChat;
    @InjectView(R.id.etChatBox)
    EditText etChatBox;
   /* @InjectView(R.id.btnChatAttach)
    Button btnChatAttach;*/
    @InjectView(R.id.tvChatUserName)
    TextView tvChatUserName;
    @InjectView(R.id.ivChatSend)
    ImageView btnChatSend;
    TextView tvDataSet;

    //Variable
    String DealJSONObject;
    String MessageData, threadID, SenderUserID;
    private int NetworkErrorCount = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        DealJSONObject = getArguments().getString("DealID");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.chat_fragment, container, false);
        ButterKnife.inject(this, view);

        //  utilityClass = new UtilityClass(getActivity());

        declaration();


        initialization();

        return view;
    }

    private void declaration() {
        utilityClass = new UtilityClass(getActivity());
        svChat.post(new Runnable() {
            @Override

            public void run() {
                svChat.fullScroll(ScrollView.FOCUS_DOWN);
                // svChat.scrollTo(0, svChat.getBottom());

            }
        });
        try {
            if (!DealJSONObject.equalsIgnoreCase("")) {
                DealsObject = new JSONObject(DealJSONObject);
                threadID = DealsObject.optString("thread_id");
                SenderUserID = DealsObject.optString("author_uid");
                tvChatUserName.setText(DealsObject.optString("author"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Json Converter Error:", e.toString());
        }
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(onNotice, new IntentFilter("Msg"));

    }

    private void initialization() {
        utilityClass.processDialogStart();
        OldChatRestService();
    }


    public void senderSetRow(JSONObject DisplayData) {
        TableRow tr1 = new TableRow(getActivity().getApplicationContext());
        tr1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        TextView textview = new TextView(getActivity().getApplicationContext());
        textview.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        textview.setTextSize(12);
        textview.setPadding(5, 5, 5, 5);
        //textview.setBackgroundResource(R.drawable.tv_round_shape);
        textview.setBackgroundColor(getActivity().getResources().getColor(R.color.orange));
        textview.setTextColor(getActivity().getResources().getColor(R.color.white));
        textview.setText(Html.fromHtml(DisplayData.optString("body")));
        textview.setMaxWidth((int) (utilityClass.GetWidth() / 0.75));
        textview.setMinWidth(TableRow.LayoutParams.WRAP_CONTENT);
        tr1.setGravity(Gravity.LEFT);

        tr1.addView(textview);
        tr1.setPadding(10, 10, 10, 10);

        tlChat.addView(tr1);
    }

    public void receiverSetRow(JSONObject DisplayData) {

        TableRow tr2 = new TableRow(getActivity().getApplicationContext());
        tr2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        TextView textview = new TextView(getActivity().getApplicationContext());
        textview.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        textview.setTextSize(12);
        textview.setPadding(5, 5, 5, 5);
       //textview.setBackgroundResource(R.drawable.tv_round_shape);
        textview.setBackgroundColor(getActivity().getResources().getColor(R.color.ColorPrimaryDark));
        textview.setTextColor(getActivity().getResources().getColor(R.color.white));
        textview.setText(Html.fromHtml(DisplayData.optString("body")));
        textview.setMaxWidth((int) (utilityClass.GetWidth() / 0.75));
        textview.setMinWidth(TableRow.LayoutParams.WRAP_CONTENT);
        tr2.setGravity(Gravity.RIGHT);

        tr2.setPadding(10, 10, 10, 10);
        tr2.addView(textview);
        tlChat.addView(tr2);
    }

    @OnClick(R.id.ivChatSend)
    public void setBtnChatSend() {
        TableRow tr2 = new TableRow(getActivity().getApplicationContext());
        tr2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        TextView textview = new TextView(getActivity().getApplicationContext());
        textview.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        textview.setTextSize(12);
        textview.setPadding(5, 5, 5, 5);
      //  textview.setBackgroundResource(R.drawable.tv_round_shape);
        textview.setBackgroundColor(getActivity().getResources().getColor(R.color.ColorPrimaryDark));
        textview.setTextColor(getActivity().getResources().getColor(R.color.white));
        textview.setMaxWidth((int) (utilityClass.GetWidth() / 0.75));
        textview.setMinWidth(TableRow.LayoutParams.WRAP_CONTENT);
        MessageData = etChatBox.getText().toString();
        etChatBox.setText("");
        textview.setText(Html.fromHtml(MessageData));
        tr2.setGravity(Gravity.RIGHT);
        tr2.setPadding(10, 10, 10, 10);
        tr2.addView(textview);
        tr2.setFocusable(true);
        tlChat.addView(tr2);
        chatRestService();
    }

    private BroadcastReceiver onNotice = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String str = intent.getStringExtra("msg");
            String str1 = intent.getStringExtra("fromname");
            String str2 = intent.getStringExtra("fromu");

            TableRow tr1 = new TableRow(getActivity().getApplicationContext());
            tr1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            TextView textview = new TextView(getActivity().getApplicationContext());
            textview.setTextSize(20);
            textview.setTextColor(Color.parseColor("#0B0719"));
            textview.setText(Html.fromHtml("<b>" + str1 + " : </b>" + str));
            tr1.addView(textview);
            tlChat.addView(tr1);


        }
    };

    private void chatRestService() {

        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Content-Type", "application/json");
                request.addHeader("X-CSRF-Token", SaveSharedPreferences.getTokon(getActivity()));
                request.addHeader("Cookie", SaveSharedPreferences.getCookie(getActivity()));

                Log.i("Chat replay Tokon : ", SaveSharedPreferences.getTokon(getActivity()));
                Log.i("Chat replay Cookie : ", SaveSharedPreferences.getCookie(getActivity()));
            }
        };
        restAdapter = new RestAdapter.Builder()
                .setEndpoint(constantClass.endPoints)
                .setRequestInterceptor(requestInterceptor)
                .build();
        api = restAdapter.create(RestServices.class);
        HashMap<String, String> HMReplay = new HashMap<String, String>();
        HMReplay.put("thread_id", threadID);
        HMReplay.put("body", MessageData);
        Log.d("Chat User Request:", HMReplay.toString());
        api.sendReplayMessage(HMReplay, new Callback<Object>() {
            @Override
            public void success(Object result, Response response) {
                try {
                    if (result == null) {
                        Log.d("Replay Success: ", String.valueOf(result));
                    }
                } catch (NullPointerException e) {
                    Log.e("IllegalStateException:", e.toString());
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("Retrofit Error  : ", String.valueOf(error.toString() + " Network " + RetrofitError.Kind.NETWORK + " : " + error.isNetworkError()));
                if (error.isNetworkError()) {
                    if (NetworkErrorCount >= 3) {
                        utilityClass.toast("Network error, please try again!!!.");
                        utilityClass.processDialogStop();
                        NetworkErrorCount = 0;
                    } else {
                        NetworkErrorCount++;
                        chatRestService();
                    }
                } else if (error.toString().contains("No address associated with hostname")) {
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

    private void OldChatRestService() {

        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Content-Type", "application/json");
                request.addHeader("X-CSRF-Token", SaveSharedPreferences.getTokon(getActivity()));
                request.addHeader("Cookie", SaveSharedPreferences.getCookie(getActivity()));

                Log.i("Chat Record Tokon : ", SaveSharedPreferences.getTokon(getActivity()));
                Log.i("Chat Record Cookie : ", SaveSharedPreferences.getCookie(getActivity()));
            }
        };

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(constantClass.endPoints)
                .setRequestInterceptor(requestInterceptor)
                .build();
        api = restAdapter.create(RestServices.class);
        HashMap<String, String> HMChatRecord = new HashMap<String, String>();
        HMChatRecord.put("cu_uid", SaveSharedPreferences.getUserID(getActivity()));
        HMChatRecord.put("puid", SenderUserID);
        Log.d("HMChatRecord User Request:", HMChatRecord.toString());
        api.getChainOfMessage(HMChatRecord,"", new Callback<JsonArray>() {
            @Override
            public void success(JsonArray result, Response response) {
                utilityClass.processDialogStop();
                try {
                    JSONArray jsonArrayNew = new JSONArray(result.getAsJsonArray().toString());
                    if (jsonArrayNew.length() != 0) {
                        for (int i = 0; i < jsonArrayNew.length(); i++) {
                            if (jsonArrayNew.getJSONObject(i).optString("author_uid")
                                    .equalsIgnoreCase(SaveSharedPreferences.getUserID(getActivity()))) {
                                receiverSetRow(jsonArrayNew.getJSONObject(i));
                            } else {
                                senderSetRow(jsonArrayNew.getJSONObject(i));
                            }
                        }

                    }
                } catch (IllegalStateException e) {
                    Log.e("IllegalStateException:", e.toString());
                } catch (JSONException e) {
                    Log.e("JSONException:", e.toString());
                }
            }

            @Override
            public void failure(RetrofitError error) {
                utilityClass.processDialogStop();
                Log.i("Retrofit Error  : ", String.valueOf(error.toString() + " Network " + RetrofitError.Kind.NETWORK + " : " + error.isNetworkError()));
                if (error.isNetworkError()) {
                    if (NetworkErrorCount >= 3) {
                        utilityClass.toast("Network error, please try again!!!.");
                        utilityClass.processDialogStop();
                        NetworkErrorCount = 0;
                    } else {
                        NetworkErrorCount++;
                        OldChatRestService();
                    }
                } else if (error.toString().contains("No address associated with hostname")) {
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
