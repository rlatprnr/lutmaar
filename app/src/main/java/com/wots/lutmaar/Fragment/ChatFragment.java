package com.wots.lutmaar.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.wots.lutmaar.Adapter.MessagesListAdapter;
import com.wots.lutmaar.CustomView.CustomTextView;
import com.wots.lutmaar.Interface.RestServices;
import com.wots.lutmaar.R;
import com.wots.lutmaar.UtilClass.ConstantClass;
import com.wots.lutmaar.UtilClass.SaveSharedPreferences;
import com.wots.lutmaar.UtilClass.Singleton;
import com.wots.lutmaar.UtilClass.UtilityClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemLongClick;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import fr.castorflex.android.circularprogressbar.CircularProgressDrawable;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class ChatFragment extends Fragment {
    //Class
    UtilityClass utilityClass;
    ConstantClass constantClass;
    //  JSONArray jsonArray, jsonArrayDescending;
    JSONObject jsonObjectSend = new JSONObject();
    JSONObject DealsObject;
    JSONArray jsonArrayUserList;
    AlertDialog alertDialog;
    LinkedList<JSONObject> linkListChat = new LinkedList<JSONObject>();
    MessagesListAdapter adapter;

    //Interface
    RestServices api;
    RestAdapter restAdapter;

    //View
    @InjectView(R.id.pbRoundChat)
    CircularProgressBar pbRoundChat;
    @InjectView(R.id.lvChatList)
    ListView lvChatList;
    @InjectView(R.id.etChatBox)
    EditText etChatBox;
    /*@InjectView(R.id.btnChatAttach)
    Button btnChatAttach;*/
    @InjectView(R.id.tvChatUserName)
    CustomTextView tvChatUserName;
    @InjectView(R.id.ivChatSend)
    ImageView ivChatSend;
    TextView tvDataSet;

    //Variable
    String DealJSONObject;
    String MessageData, threadID, SenderUserID;
    private int NetworkErrorCount = 0;
    int pageNo = 0, pageEnd = 0, userCount = 0;

    boolean pageScroll = true;
    String catID = "all";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        DealJSONObject = getArguments().getString("DealID");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.chat_fragment, container, false);
        ButterKnife.inject(this, view);

        declaration();

        return view;
    }

    private void declaration() {
        utilityClass = new UtilityClass(getActivity());
        utilityClass.restServiceUnReadMessage();
        ((CircularProgressDrawable) pbRoundChat.getIndeterminateDrawable()).start();
        try {
            if (!DealJSONObject.equalsIgnoreCase("")) {
                DealsObject = new JSONObject(DealJSONObject);
                threadID = DealsObject.optString("thread_id");
                jsonArrayUserList = DealsObject.getJSONArray("participants");
                userCount = (jsonArrayUserList.length());
                if (userCount == 1) {
                    tvChatUserName.setText(jsonArrayUserList.getJSONObject(0).optString("participant"));
                } else {
                    for (int i = 0; i < userCount; i++) {
                        tvChatUserName.setText(tvChatUserName.getText().toString() + jsonArrayUserList.getJSONObject(i).optString("participant") + ", ");
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Json Converter Error:", e.toString());
        }

        // jsonArray = new JSONArray();
        adapter = new MessagesListAdapter(getActivity(), userCount, linkListChat);
        lvChatList.setAdapter(adapter);
        lvChatList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                final int lastItem = firstVisibleItem + visibleItemCount;
                if (firstVisibleItem == 0 && pageScroll) {
                    pageScroll = false;
                    if (pageEnd == 0)
                        initialization();
                }
            }
        });
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(onNotice, new IntentFilter("Msg"));

    }

    private void initialization() {
        if (utilityClass.isInternetConnection()) {
            pbRoundChat.setVisibility(View.VISIBLE);
            OldChatRestService();
        } else {
            pbRoundChat.setVisibility(View.GONE);
            utilityClass.toast("Please check your internet settings!!!");
        }
    }

    @OnClick(R.id.ivChatSend)
    public void setIvChatSend() {
        if (utilityClass.isInternetConnection()) {
            if (!etChatBox.getText().toString().equalsIgnoreCase("")) {
                ivChatSend.setEnabled(false);
                MessageData = etChatBox.getText().toString();
                NetworkErrorCount = 0;
                chatRestService();
            }
        } else {
            utilityClass.toast("Please check your internet settings!!!");
        }
    }

    @OnItemLongClick(R.id.lvChatList)
    public boolean setLvChatList(int position) {
        if (utilityClass.isInternetConnection()) {
            NetworkErrorCount = 0;
            showDeleteMassageDialog(position);
        } else {
            utilityClass.toast("Please check your internet settings!!!");
        }
        return true;
    }

    private void showDeleteMassageDialog(final int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("Do you want to delete this massage?")
                .setCancelable(false)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteMessage(position);
                    }
                });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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
                ivChatSend.setEnabled(true);
               /* lvChatList.setStackFromBottom(true);
                lvChatList.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);*/
                try {
                    SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
                    jsonObjectSend.put("author_uid", SaveSharedPreferences.getUserID(getActivity()));
                    jsonObjectSend.put("body", MessageData);
                    jsonObjectSend.put("sending_time", String.valueOf(format.format(new Date())));
                    jsonObjectSend.put("mid", "");
                    linkListChat.addLast(jsonObjectSend);
                    adapter.notifyDataSetChanged();
                    etChatBox.setText("");
                } catch (JSONException e) {
                    Log.e("JSONObject add LinkList", e.toString());
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("Retrofit Error  : ", String.valueOf(error.toString() + " Network " + RetrofitError.Kind.NETWORK + " : " + error.isNetworkError()));
                if (error.isNetworkError()) {
                    if (NetworkErrorCount >= 3) {
                        utilityClass.toast("Network error, please try again!!!.");
                        ivChatSend.setEnabled(true);
                        NetworkErrorCount = 0;
                    } else {
                        NetworkErrorCount++;
                        chatRestService();
                    }
                } else {
                    ivChatSend.setEnabled(true);
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

    private void deleteMessage(final int position) {

        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Content-Type", "application/json");
                request.addHeader("X-CSRF-Token", SaveSharedPreferences.getTokon(getActivity()));
                request.addHeader("Cookie", SaveSharedPreferences.getCookie(getActivity()));

                Log.i("delete message Tokon :", SaveSharedPreferences.getTokon(getActivity()));
                Log.i("delete massage Cookie:", SaveSharedPreferences.getCookie(getActivity()));
            }
        };
        restAdapter = new RestAdapter.Builder()
                .setEndpoint(constantClass.endPoints)
                .setRequestInterceptor(requestInterceptor)
                .build();
        api = restAdapter.create(RestServices.class);
        HashMap<String, String> HMdelete = new HashMap<String, String>();
        HMdelete.put("mid", linkListChat.get(position).optString("mid"));
        Log.d("delete Message Request:", HMdelete.toString());
        api.deleteMassageMessage(HMdelete, new Callback<Object>() {
            @Override
            public void success(Object result, Response response) {
                linkListChat.remove(position);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("Retrofit Error  : ", String.valueOf(error.toString() + " Network " + RetrofitError.Kind.NETWORK + " : " + error.isNetworkError()));
                if (error.isNetworkError()) {
                    if (NetworkErrorCount >= 3) {
                        utilityClass.toast("Network error, please try again!!!.");
                        NetworkErrorCount = 0;
                    } else {
                        NetworkErrorCount++;
                        deleteMessage(position);
                    }
                } else {
                    if (error.toString().contains("No address associated with hostname")) {
                        //   tvError.setText("Please check your network connection or try again later");
                        utilityClass.toast("Please check your network connection or try again later.");
                    } else if (error.toString().equals("retrofit.RetrofitError: 500 Internal Server Error")) {
                        utilityClass.toast("Internal Server Error");
                        //  tvError.setText("Internal Server Error");
                    } else if (error.toString().contains("com.google.gson.JsonSyntaxException")) {
                        //tvError.setText("Result not found form server");
                        utilityClass.toast("Result not found form server");
                    } else if (error.toString().contains("you have deleted all your messages")) {
                        //tvError.setText("Result not found form server");
                        // utilityClass.toast("Result not found form server");
                    } else {
                        //tvError.setText("Invalid user or password");
                        utilityClass.toast("Something went wrong!!!");

                    }
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
        HMChatRecord.put("thread_id", threadID);
        Log.d("Record Tread ID Req:", HMChatRecord.toString());
        Log.d("Chatting Page:", String.valueOf(pageNo));


        api.getChainOfMessage(HMChatRecord, String.valueOf(pageNo), new Callback<JsonArray>() {
            @Override
            public void success(JsonArray result, Response response) {
                pbRoundChat.setVisibility(View.GONE);
                NetworkErrorCount = 0;
               /* if (pageNo!= 0) {
                    lvChatList.setStackFromBottom(false);
                    lvChatList.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);
                }*/
                try {
                    int i = 0;

                    JSONArray jsonArrayNew = new JSONArray(result.getAsJsonArray().toString());
                    if (jsonArrayNew.length() != 0) {
                        if (jsonArrayNew.length() < 10) {
                            pageEnd = 1;

                        }
                        for (i = 0; i < jsonArrayNew.length(); i++) {
                            linkListChat.addFirst(jsonArrayNew.getJSONObject(i));
                        }
                        adapter.notifyDataSetChanged();
                        pageNo++;
                        pageScroll = true;
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
                        pbRoundChat.setVisibility(View.GONE);

                        NetworkErrorCount = 0;
                    } else {
                        NetworkErrorCount++;
                        pbRoundChat.setVisibility(View.VISIBLE);
                        OldChatRestService();
                    }
                } else {
                    pbRoundChat.setVisibility(View.GONE);

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

    @Override
    public void onResume() {
        super.onResume();
        Singleton.GetInstance().MenuHide();
        Singleton.GetInstance().getTvMainHeading().setText("Massages");
    }
}
