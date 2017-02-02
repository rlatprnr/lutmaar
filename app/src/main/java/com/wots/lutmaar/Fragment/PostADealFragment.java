package com.wots.lutmaar.Fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;
import com.wots.lutmaar.Adapter.SelectCityAdapter;
import com.wots.lutmaar.CustomView.imagechooser.api.ChooserType;
import com.wots.lutmaar.CustomView.imagechooser.api.ChosenImage;
import com.wots.lutmaar.CustomView.imagechooser.api.ImageChooserListener;
import com.wots.lutmaar.CustomView.imagechooser.api.ImageChooserManager;
import com.wots.lutmaar.Interface.RestServices;
import com.wots.lutmaar.R;
import com.wots.lutmaar.UtilClass.ConstantClass;
import com.wots.lutmaar.UtilClass.GPSTracker;
import com.wots.lutmaar.UtilClass.MyApplication;
import com.wots.lutmaar.UtilClass.SaveSharedPreferences;
import com.wots.lutmaar.UtilClass.Singleton;
import com.wots.lutmaar.UtilClass.UtilityClass;
import com.wots.lutmaar.UtilClass.Validation;
import com.wots.lutmaar.UtilClass.db.MySQLiteHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class PostADealFragment extends Fragment implements View.OnClickListener, ImageChooserListener {
    //Class
    UtilityClass utilityClass;
    ConstantClass constantClass;
    JSONObject jsonObject;
    private LocationManager locationManager;
    GPSTracker gpsTracker;
    Location location;
    private ShowcaseView showcaseView;
    ArrayList<String> PeriodList = new ArrayList<String>();
    ArrayList<Bitmap> ImageList = new ArrayList<Bitmap>();
    ArrayList<HashMap<String, String>> HMimgFileList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> ImageIDList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> ImageIDListOld = new ArrayList<HashMap<String, String>>();
    MyApplication myApplication;
    private ImageChooserManager imageChooserManager;
    MySQLiteHelper mydb;
    // Capturandro capturandro = null;


    //View
    @InjectView(R.id.ivDealImageAttachment)
    ImageView ivDealImageAttachment;
    @InjectView(R.id.tvAddPhoto)
    TextView tvAddPhoto;
    @InjectView(R.id.ivDealImageCapture)
    ImageView ivDealImageCapture;
    @InjectView(R.id.tvTakePhoto)
    TextView tvTakePhoto;
    @InjectView(R.id.rgDealType)
    RadioGroup rgDealType;
    @InjectView(R.id.rBtnDeal)
    RadioButton rBtnDeal;
    @InjectView(R.id.rBtnVouchers)
    RadioButton rBtnVouchers;
    @InjectView(R.id.rBtnFreebies)
    RadioButton rBtnFreebies;
    @InjectView(R.id.rBtnAsk)
    RadioButton rBtnAsk;
    @InjectView(R.id.rBtnCompetition)
    RadioButton rBtnCompetition;


    @InjectView(R.id.LLOnlineOrOffline)
    LinearLayout LLOnlineOrOffline;
    @InjectView(R.id.tvDealTitle)
    TextView tvDealTitle;
    @InjectView(R.id.tvSwitchOnOff)
    TextView tvSwitchOnOff;
    /* @InjectView(R.id.tvOtherMerchentSite)
     TextView tvOtherMerchentSite;*/
    @InjectView(R.id.etOtherMerchentSite)
    EditText etOtherMerchentSite;
    @InjectView(R.id.etCompanyName)
    EditText etCompanyName;
    @InjectView(R.id.llAddressDealOffline1)
    LinearLayout llAddressDealOffline1;
    @InjectView(R.id.etAddressDealOffline1)
    EditText etAddressDealOffline1;
    @InjectView(R.id.ivGetCurrentAddress)
    ImageView ivGetCurrentAddress;
    @InjectView(R.id.etAddressDealOffline2)
    EditText etAddressDealOffline2;
    @InjectView(R.id.tvOnlinePostLocation)
    TextView tvOnlinePostLocation;
    /*@InjectView(R.id.spCity)
    Spinner spCity;*/
    @InjectView(R.id.tvCity)
    TextView tvCity;
    @InjectView(R.id.etPinCode)
    EditText etPinCode;
    @InjectView(R.id.LLPrice)
    LinearLayout LLPrice;
    @InjectView(R.id.LLDealAddPhoto)
    LinearLayout LLDealAddPhoto;
    @InjectView(R.id.LLDealTakePhoto)
    LinearLayout LLDealTakePhoto;
    @InjectView(R.id.LLDealDate)
    LinearLayout LLDealDate;
    @InjectView(R.id.etDealPrices)
    EditText etDealPrices;
    @InjectView(R.id.tvDealDetails)
    TextView tvDealDetails;
    @InjectView(R.id.tvCategory)
    TextView tvCategory;
    /* @InjectView(R.id.tvCategoryOffLine)
     TextView tvCategoryOffLine;*/

    /* @InjectView(R.id.etAddressDealOnline)
     EditText etAddressDealOnline;*/
    @InjectView(R.id.etDiscount)
    EditText etDiscount;
    @InjectView(R.id.etCode)
    EditText etCode;
    @InjectView(R.id.etSpend)
    EditText etSpend;
    @InjectView(R.id.etApplies)
    EditText etApplies;
    @InjectView(R.id.etPrize)
    EditText etPrize;
    @InjectView(R.id.tvPeriod)
    TextView tvPeriod;
    @InjectView(R.id.etValue)
    EditText etValue;
    @InjectView(R.id.etCompetitionRules)
    EditText etCompetitionRules;
    @InjectView(R.id.etCompetitionLinkRules)
    EditText etCompetitionLinkRules;
    @InjectView(R.id.LLImageList)
    LinearLayout LLImageList;
    @InjectView(R.id.LLImage1)
    LinearLayout LLImage1;
    @InjectView(R.id.ivTakeImage1)
    ImageView ivTakeImage1;
    @InjectView(R.id.ivImageCancel1)
    ImageView ivImageCancel1;
    @InjectView(R.id.LLImage2)
    LinearLayout LLImage2;
    @InjectView(R.id.ivTakeImage2)
    ImageView ivTakeImage2;
    @InjectView(R.id.ivImageCancel2)
    ImageView ivImageCancel2;
    @InjectView(R.id.LLImage3)
    LinearLayout LLImage3;
    @InjectView(R.id.ivTakeImage3)
    ImageView ivTakeImage3;
    @InjectView(R.id.ivImageCancel3)
    ImageView ivImageCancel3;
    @InjectView(R.id.LLImage4)
    LinearLayout LLImage4;
    @InjectView(R.id.ivTakeImage4)
    ImageView ivTakeImage4;
    @InjectView(R.id.ivImageCancel4)
    ImageView ivImageCancel4;
    @InjectView(R.id.LLImage5)
    LinearLayout LLImage5;
    @InjectView(R.id.ivTakeImage5)
    ImageView ivTakeImage5;
    @InjectView(R.id.ivImageCancel5)
    ImageView ivImageCance5;
    @InjectView(R.id.tvDealStartDate)
    TextView tvDealStartDate;
    @InjectView(R.id.tvDealEndDate)
    TextView tvDealEndDate;
    @InjectView(R.id.tvCleanAllDeal)
    TextView tvCleanAllDeal;


    //Interface
    RestServices api;
    RestAdapter restAdapter;

    //Variable
    ByteArrayOutputStream outputStream;
    File destination;
    private static List<Uri> uris = new ArrayList<>();
    private static int RESULT_LOAD_IMAGE = 1;
    final int CAMERA_PIC_REQUEST = 0;
    private String mediaPath;
    private int chooserType;
    private int Date_Start_End = 1, ImageIndex = 0, ImageUploadCount = 0, TotalImage = 0, TotalUploadedImages = 0;
    public static String imgPath, imgName, imgFile, imgNameCapture, imgUri;

    //   ArrayAdapter<String> DealsCategotyAdapter;  //, DealCityAdapter, DealsStateAdapter;

    public static String DealType = "deals", DealObject, CatID = "";
    public static String PostDealStatus = "dealsOnLine", NID, CheckDeal = "New";

    // public static int checkImageUpload = 1;
    public static boolean onLine = true;
    //For Date
    private Calendar cal;
    private int day, month, year;
    private int ShowCasecounter = 0, NetworkErrorCount = 0;
    int ScreenWidth;
    Double Longitude = 0.0, Latitude = 0.0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (!(getArguments().getString("DealID").equalsIgnoreCase("")))
            DealObject = getArguments().getString("DealID");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.post_adeal_fragment, container, false);
        ButterKnife.inject(this, view);

     /*   if (capturandro == null) {
            capturandro = new Capturandro(getActivity(), callback);
        }*/

        //   capturandro.onCreate(savedInstanceState);
        declaration();

        initialization();
        return view;
    }

    private void declaration() {
        utilityClass = new UtilityClass(getActivity());
        myApplication = (MyApplication) getActivity().getApplication();
        mydb = new MySQLiteHelper(getActivity());
        if (SaveSharedPreferences.getWCDealPost(getActivity()).length() == 0) {
            SaveSharedPreferences.setWCDealPost(getActivity(), "Already Done ");
            showcaseView = new ShowcaseView.Builder(getActivity(), true)
                    .setTarget(new ViewTarget(tvSwitchOnOff))
                    .setStyle(R.style.CustomShowcaseTheme)
                    .setOnClickListener(this)
                    .build();

            showcaseView.setButtonText("Next");
            showcaseView.setContentTitle(" Post a deal as online or offline");
            //showcaseView.setContentText("Type of deal");
        }
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        gpsTracker = new GPSTracker(getActivity());
        //  Singleton.GetInstance().getCategoryList().clear();
        // Singleton.GetInstance().getHMCategory().clear();

       /* getCategoryRestService();*/
        PeriodList.add("once");
        PeriodList.add("daily");
        PeriodList.add("weekly");
        PeriodList.add("monthly");
        PeriodList.add("unlimited");

        ScreenWidth = utilityClass.GetWidth() - 72;
        LinearLayout.LayoutParams Params = new LinearLayout.LayoutParams(ScreenWidth / 5, ScreenWidth / 5);
        LLImage1.setLayoutParams(Params);
        Params.setMargins(10, 0, 0, 0);
        LLImage2.setLayoutParams(Params);
        LLImage3.setLayoutParams(Params);
        LLImage4.setLayoutParams(Params);
        LLImage5.setLayoutParams(Params);

        cal = Calendar.getInstance();
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);

    }

    private void initialization() {


        tvDealStartDate.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(day).append(" ").append(getMonth(month)).append(" ")
                .append(year));
        tvDealEndDate.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(day).append(" ").append(getMonth(month)).append(" ")
                .append(year));
        Singleton.GetInstance().setFirstDate(year + "-" + String.valueOf(month + 1) + "-" + day);
        rgDealType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.e("Radio Button Change:", String.valueOf(checkedId));
                if (onLine) {
                    tvSwitchOnOff.setBackground(getResources().getDrawable(R.drawable.ios_on));
                    tvSwitchOnOff.setCompoundDrawablesWithIntrinsicBounds(R.drawable.switch_on_off_small, 0, 0, 0);
                } else {
                    tvSwitchOnOff.setBackground(getResources().getDrawable(R.drawable.ios_off));
                    tvSwitchOnOff.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.switch_on_off_small, 0);
                }
                switch (checkedId) {
                    case R.id.rBtnDeal:
                        DealType = "deals";
                        if (Singleton.GetInstance().getHMCategory().size() <= 0) {
                            if (utilityClass.isInternetConnection()) {
                                utilityClass.processDialogStart();
                                getCategoryRestService();
                            } else {
                                utilityClass.getDefaultCategory();
                            }
                        }
                        if (onLine) {
                            PostDealStatus = "dealsOnLine";
                            dealOnLineFormate();
                        } else {
                            PostDealStatus = "dealsOffLine";
                            dealOffLineFormate();
                        }
                        break;

                    case R.id.rBtnVouchers:
                        DealType = "vouchers";
                        if (Singleton.GetInstance().getHMCategory().size() <= 0) {
                            if (utilityClass.isInternetConnection()) {
                                utilityClass.processDialogStart();
                                getCategoryRestService();
                            } else {
                                utilityClass.getDefaultCategory();
                            }
                        }
                        if (onLine) {
                            PostDealStatus = "vouchersOnLine";
                            voucherOnLineFormate();
                        } else {
                            PostDealStatus = "vouchersOffLine";
                            voucherOffLineFormate();
                        }
                        break;

                    case R.id.rBtnFreebies:
                        DealType = "freebies";
                        if (Singleton.GetInstance().getHMCategory().size() <= 0) {
                            if (utilityClass.isInternetConnection()) {
                                utilityClass.processDialogStart();
                                getCategoryRestService();
                            } else {
                                utilityClass.getDefaultCategory();
                            }
                        }
                        if (onLine) {
                            PostDealStatus = "freebiesOnLine";
                            freebiesOnLineFormate();
                        } else {
                            PostDealStatus = "freebiesOffLine";
                            freebiesOffLineFormate();
                        }
                        break;
                    case R.id.rBtnAsk:
                        DealType = "ask";
                        if (Singleton.GetInstance().getHMCategory().size() <= 0) {

                            if (utilityClass.isInternetConnection()) {
                                utilityClass.processDialogStart();
                                getCategoryRestService();
                            } else {
                                utilityClass.getDefaultCategory();
                            }
                        }
                        PostDealStatus = "ask";
                        askFormate();
                        break;
                    case R.id.rBtnCompetition:
                        DealType = "competitions";
                        if (Singleton.GetInstance().getHMCategory().size() <= 0) {
                            if (utilityClass.isInternetConnection()) {
                                utilityClass.processDialogStart();
                                getCategoryRestService();
                            } else {
                                utilityClass.getDefaultCategory();
                            }
                        }
                        if (onLine) {
                            PostDealStatus = "competitionsOnLine";
                            competitionOnLineFormate();
                        } else {
                            PostDealStatus = "competitionsOffLine";
                            competitionOffLineFormate();
                        }
                        break;
                }
            }
        });
        try {
            if (!DealObject.equalsIgnoreCase("0")) {
                jsonObject = new JSONObject(DealObject);
                editSetValue(jsonObject);
            } else {
                CheckDeal = "New";
            }
        } catch (JSONException e) {
            Log.e("JSONObject : ", e.toString());
        } catch (NullPointerException e) {
            Log.e("Json Null accept ", e.toString());
            DealObject = "0";
            CheckDeal = "New";
        }
    }

    @OnClick(R.id.tvSwitchOnOff)
    public void setTvSwitchOnOff() {
        if (onLine) {
            tvSwitchOnOff.setBackground(getResources().getDrawable(R.drawable.ios_off));
            tvSwitchOnOff.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.switch_on_off_small, 0);
            onLine = false;
            if (DealType.equalsIgnoreCase("deals")) {
                PostDealStatus = "dealsOffLine";
                dealOffLineFormate();
            } else if (DealType.equalsIgnoreCase("vouchers")) {
                PostDealStatus = "vouchersOffLine";
                voucherOffLineFormate();
            } else if (DealType.equalsIgnoreCase("freebies")) {
                PostDealStatus = "freebiesOffLine";
                freebiesOffLineFormate();
            } else if (DealType.equalsIgnoreCase("ask")) {
                PostDealStatus = "askOffLine";
                askFormate();
            } else if (DealType.equalsIgnoreCase("competitions")) {
                PostDealStatus = "competitionsOffLine";
                competitionOffLineFormate();
            }

        } else {
            tvSwitchOnOff.setBackground(getResources().getDrawable(R.drawable.ios_on));
            tvSwitchOnOff.setCompoundDrawablesWithIntrinsicBounds(R.drawable.switch_on_off_small, 0, 0, 0);
            onLine = true;
            if (DealType.equalsIgnoreCase("deals")) {
                PostDealStatus = "dealsOnLine";
                dealOnLineFormate();

            } else if (DealType.equalsIgnoreCase("vouchers")) {
                PostDealStatus = "vouchersOnLine";
                voucherOnLineFormate();
            } else if (DealType.equalsIgnoreCase("freebies")) {
                PostDealStatus = "freebiesOnLine";
                freebiesOnLineFormate();

            } else if (DealType.equalsIgnoreCase("ask")) {
                PostDealStatus = "askOnLine";
                askFormate();
            } else if (DealType.equalsIgnoreCase("competitions")) {
                PostDealStatus = "competitionsOnLine";
                competitionOnLineFormate();
            }
        }

    }

    @OnClick(R.id.ivDealImageAttachment)
    public void setIvDealImageAttachmnet() {
       /* chooserType = ChooserType.REQUEST_PICK_PICTURE;
        imageChooserManager = new ImageChooserManager(this, ChooserType.REQUEST_PICK_PICTURE, true);
        imageChooserManager.setImageChooserListener(this);
        try {
            mediaPath = imageChooserManager.choose();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, RESULT_LOAD_IMAGE);

    }

    @OnClick(R.id.ivDealImageCapture)
    public void setIvDealImageCapture() {
        chooserType = ChooserType.REQUEST_CAPTURE_PICTURE;
        imageChooserManager = new ImageChooserManager(this, ChooserType.REQUEST_CAPTURE_PICTURE, true);
        imageChooserManager.setImageChooserListener(this);
        try {
            mediaPath = imageChooserManager.choose();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //  dispatchTakePictureIntent();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d("onActivityCreated:", "DB data postDeal");
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("media_path")) {
                mediaPath = savedInstanceState.getString("media_path");
            }
            if (savedInstanceState.containsKey("chooser_type")) {
                chooserType = savedInstanceState.getInt("chooser_type");
            }
        }

        try {
            if (CheckDeal.equalsIgnoreCase("New")) {
                Cursor rs = mydb.getAllPostDealData();
                rs.moveToFirst();
                Log.d("Cursor Data:", String.valueOf(rs));
                if (rs.getCount() >= 1) {
                    DealType = rs.getString(rs.getColumnIndex(MySQLiteHelper.DEALTYPE));
                    onLine = Boolean.valueOf(rs.getString(rs.getColumnIndex(MySQLiteHelper.ONLINEOFFLINE)));
                    if (rs.getString(rs.getColumnIndex(MySQLiteHelper.DEALTYPE)) != null) {
                        if (DealType.equalsIgnoreCase("deals")) {
                            rBtnDeal.setChecked(true);
                        } else if (DealType.equalsIgnoreCase("vouchers")) {
                            rBtnVouchers.setChecked(true);
                        } else if (DealType.equalsIgnoreCase("freebies")) {
                            rBtnFreebies.setChecked(true);
                        } else if (DealType.equalsIgnoreCase("ask")) {
                            rBtnAsk.setChecked(true);
                        } else if (DealType.equalsIgnoreCase("competitions")) {
                            rBtnCompetition.setChecked(true);
                        }
                    } else {
                        rBtnDeal.setChecked(true);
                    }
                    // setTvSwitchOnOff();
                    tvDealTitle.setText(rs.getString(rs.getColumnIndex(MySQLiteHelper.TITLE)));
                    Log.d("Resume Deal Title", rs.getString(rs.getColumnIndex(MySQLiteHelper.TITLE)));
                    tvDealDetails.setText(rs.getString(rs.getColumnIndex(MySQLiteHelper.BODY)));
                    etOtherMerchentSite.setText(rs.getString(rs.getColumnIndex(MySQLiteHelper.DEAL_URL)));
                    etCompanyName.setText(rs.getString(rs.getColumnIndex(MySQLiteHelper.ORG_NAME)));
                    tvCategory.setText(rs.getString(rs.getColumnIndex(MySQLiteHelper.DEAL_CAT)));
                    etAddressDealOffline1.setText(rs.getString(rs.getColumnIndex(MySQLiteHelper.ADD1)));
                    etAddressDealOffline2.setText(rs.getString(rs.getColumnIndex(MySQLiteHelper.ADD2)));
                    tvCity.setText(rs.getString(rs.getColumnIndex(MySQLiteHelper.CITY)));
                    etPinCode.setText(rs.getString(rs.getColumnIndex(MySQLiteHelper.PIN_CODE)));
                    etDealPrices.setText(rs.getString(rs.getColumnIndex(MySQLiteHelper.PRICE)));
                    etDiscount.setText(rs.getString(rs.getColumnIndex(MySQLiteHelper.DISCOUNT)));
                    etCode.setText(rs.getString(rs.getColumnIndex(MySQLiteHelper.CODE)));
                    etSpend.setText(rs.getString(rs.getColumnIndex(MySQLiteHelper.MIN_SPEND)));
                    etApplies.setText(rs.getString(rs.getColumnIndex(MySQLiteHelper.APPLIES_TO)));
                    tvPeriod.setText(rs.getString(rs.getColumnIndex(MySQLiteHelper.PERIOD)));
                    etPrize.setText(rs.getString(rs.getColumnIndex(MySQLiteHelper.PRIZE)));
                    etValue.setText(rs.getString(rs.getColumnIndex(MySQLiteHelper.C_VALUES)));
                    etCompetitionRules.setText(rs.getString(rs.getColumnIndex(MySQLiteHelper.RULES)));
                    etCompetitionLinkRules.setText(rs.getString(rs.getColumnIndex(MySQLiteHelper.LINK_TO_RULES)));
                    HMimgFileList = new ArrayList<HashMap<String, String>>();
                    HashMap<String, String> HMImageData = new HashMap<String, String>();
                    String fieldName = "img1";
                    for (int j = 1; j <= 5; j++) {
                        fieldName = "img" + j;
                        HMImageData = new HashMap<String, String>();
                        if (rs.getString(rs.getColumnIndex(fieldName)) != null) {
                            HMImageData.put("target_uri", rs.getString(rs.getColumnIndex(fieldName)));
                            HMimgFileList.add(HMImageData);
                        }
                    }

                    if (!HMimgFileList.isEmpty()) {
                        TotalImage = 0;
                        for (int j = 0; j < HMimgFileList.size(); j++) {
                            if (!HMimgFileList.get(j).isEmpty()) {
                                TotalImage++;
                                String Path = HMimgFileList.get(j).get("target_uri");


                                Bitmap thumbnailImage = (BitmapFactory.decodeFile(Path));
                                //  Bitmap bmpPic1 = Bitmap.createBitmap(thumbnailImage, 0, 0, thumbnailImage.getWidth(), thumbnailImage.getHeight(), m, true);
                                //Bitmap bmpPic1 = Bitmap.createScaledBitmap(thumbnail, 240, 260, m, true);
                                // thumbnailImage = Bitmap.createScaledBitmap(bmpPic1, 240, 260, true);
                                LLImageList.setVisibility(View.VISIBLE);
                                if (j == 0) {
                                    LLImage1.setVisibility(View.VISIBLE);
                                    ivTakeImage1.setImageBitmap(thumbnailImage);
                                } else if (j == 1) {
                                    LLImage2.setVisibility(View.VISIBLE);
                                    ivTakeImage2.setImageBitmap(thumbnailImage);
                                } else if (j == 2) {
                                    LLImage3.setVisibility(View.VISIBLE);
                                    ivTakeImage3.setImageBitmap(thumbnailImage);
                                } else if (j == 3) {
                                    LLImage4.setVisibility(View.VISIBLE);
                                    ivTakeImage4.setImageBitmap(thumbnailImage);
                                } else if (j == 4) {
                                    LLImage5.setVisibility(View.VISIBLE);
                                    ivTakeImage5.setImageBitmap(thumbnailImage);
                                }

                            }
                        }
                    }
                } else {
                    rBtnDeal.setChecked(true);
                    Log.d("DB have No record:", "empty postDeal");
                }
                if (!rs.isClosed()) {
                    rs.close();
                }
            }
        } catch (NullPointerException e) {
            Log.e("Restart NulPointer Error :", e.toString());
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (chooserType != 0) {
            outState.putInt("chooser_type", chooserType);
        }
        if (mediaPath != null) {
            outState.putString("media_path", mediaPath);
        }
    }

    @OnClick(R.id.tvDealStartDate)
    public void setTvDealStartDate() {
        Date_Start_End = 1;
        DatePickerDialog datePicker = new DatePickerDialog(getActivity(), datePickerListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));
        datePicker.setTitle("Select start date");
        datePicker.show();
    }

    @OnClick(R.id.tvDealEndDate)
    public void setTvDealEndDate() {
        Date_Start_End = 2;
        DatePickerDialog datePicker = new DatePickerDialog(getActivity(), datePickerListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));
        datePicker.setTitle("Select end date");
        datePicker.show();
    }

    @OnClick(R.id.tvSaveDeal)
    public void setTvSaveDeal() {
        if (utilityClass.isInternetConnection()) {
            if (checkValidation()) {
                ImageUploadCount = 0;
                if (CheckDeal.equalsIgnoreCase("Old")) {
                    for (int j = 0; j < HMimgFileList.size(); j++) {
                        if (HMimgFileList.get(j).containsKey("fid"))
                            HMimgFileList.get(j).clear();
                    }
                    boolean ImageData = true;
                    for (int j = 0; j < HMimgFileList.size(); j++) {
                        if (!HMimgFileList.get(j).isEmpty())
                            ImageData = false;
                    }
                    if (ImageData)
                        HMimgFileList.clear();
                }
                if (HMimgFileList.isEmpty()) {
                    if (CheckDeal.equalsIgnoreCase("Old")) {
                        ImageIDList.clear();
                        for (int j = 0; j < ImageIDListOld.size(); j++) {
                            if (!ImageIDListOld.get(j).isEmpty())
                                ImageIDList.add(ImageIDListOld.get(j));
                        }
                    }
                    //    ImageUploadCount = 0;
                    utilityClass.processDialogStart();
                    DealOnlineRestService();
                } else {

                    NetworkErrorCount = 0;
                    utilityClass.processDialogStart();
                    if (DealType.equalsIgnoreCase("deals")) {
                        //       ImageUploadCount = 0;
                        if (HMimgFileList.size() > 0) {
                            ImageIDList.clear();
                            restService();
                        } else {
                            DealOnlineRestService();
                        }
                    } else if (DealType.equalsIgnoreCase("vouchers")) {
                        //    ImageUploadCount = 0;
                        DealOnlineRestService();
                    } else if (DealType.equalsIgnoreCase("freebies")) {
                        //     ImageUploadCount = 0;
                        if (HMimgFileList.size() > 0) {
                            ImageIDList.clear();
                            restService();
                        } else {
                            DealOnlineRestService();
                        }
                    } else if (DealType.equalsIgnoreCase("ask")) {
                        //   ImageUploadCount = 0;
                        DealOnlineRestService();
                    } else if (DealType.equalsIgnoreCase("competitions")) {
                        //     ImageUploadCount = 0;
                        if (HMimgFileList.size() > 0) {
                            ImageIDList.clear();
                            restService();
                        } else {
                            DealOnlineRestService();
                        }
                    }
                }
            }
        } else {
            if (checkValidation()) {
                ImageIDList.clear();
                DealOnlineRestService();
                //networkLossDialogShow();
            }
        }
    }

    @OnClick(R.id.tvCity)
    public void setTvCity() {
        SelectCityDialog();
    }

    @OnClick(R.id.ivGetCurrentAddress)
    public void setIvGetCurrentAddress() {
        if (utilityClass.isInternetConnection()) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                location = gpsTracker.getLocation();
                Latitude = gpsTracker.getLatitude();
                Longitude = gpsTracker.getLongitude();
                if (location != null && (Latitude != 0.0 || Longitude != 0.0)) {
                    utilityClass.processDialogStart();
                    getLocationFullAddress(String.valueOf(gpsTracker.getLatitude()) + "," + String.valueOf(gpsTracker.getLongitude()));
                } else {
                    utilityClass.toast("Location can not found!!!");
                }
            } else {
                utilityClass.showGPSDisabledAlertToUser();
            }
        } else {
            utilityClass.toast("Please check your internet settings!!!");
        }
    }

    @OnClick(R.id.ivImageCancel1)
    public void setIvImageCancel1() {
        int cnt = 0;
        LLImage1.setVisibility(View.GONE);
        HMimgFileList.get(0).clear();
        TotalImage--;
        if (TotalImage < 5) {
            ivDealImageCapture.setEnabled(true);
            ivDealImageAttachment.setEnabled(true);
        }
        if (CheckDeal.equalsIgnoreCase("Old")) {
            if (ImageIDListOld.size() >= 1)
                ImageIDListOld.get(0).clear();
        }
        Log.d("HMImgList data:", HMimgFileList.toString());
        for (int j = 0; j < HMimgFileList.size(); j++) {
            if (HMimgFileList.get(j).isEmpty()) {
                cnt++;
                if (cnt == HMimgFileList.size()) {
                    LLImageList.setVisibility(View.GONE);
                    HMimgFileList.clear();
                    // checkImageUpload = 1;
                }

            }
        }
    }

    @OnClick(R.id.ivImageCancel2)
    public void setIvImageCancel2() {
        int cnt = 0;
        LLImage2.setVisibility(View.GONE);
        HMimgFileList.get(1).clear();
        TotalImage--;
        if (TotalImage < 5) {
            ivDealImageCapture.setEnabled(true);
            ivDealImageAttachment.setEnabled(true);
        }
        if (CheckDeal.equalsIgnoreCase("Old")) {
            if (ImageIDListOld.size() >= 2)
                ImageIDListOld.get(1).clear();
        }
        Log.d("HMImgList data:", HMimgFileList.toString());
        for (int j = 0; j < HMimgFileList.size(); j++) {
            if (HMimgFileList.get(j).isEmpty()) {
                cnt++;
                if (cnt == HMimgFileList.size()) {
                    LLImageList.setVisibility(View.GONE);
                    HMimgFileList.clear();
                    //checkImageUpload = 1;
                }
            }
        }
    }

    @OnClick(R.id.ivImageCancel3)
    public void setIvImageCancel3() {
        int cnt = 0;
        LLImage3.setVisibility(View.GONE);
        HMimgFileList.get(2).clear();
        TotalImage--;
        if (TotalImage < 5) {
            ivDealImageCapture.setEnabled(true);
            ivDealImageAttachment.setEnabled(true);
        }
        if (CheckDeal.equalsIgnoreCase("Old")) {
            if (ImageIDListOld.size() >= 3)
                ImageIDListOld.get(2).clear();
        }
        Log.d("HMImgList data:", HMimgFileList.toString());
        for (int j = 0; j < HMimgFileList.size(); j++) {
            if (HMimgFileList.get(j).isEmpty()) {
                cnt++;
                if (cnt == HMimgFileList.size()) {
                    LLImageList.setVisibility(View.GONE);
                    HMimgFileList.clear();
                    //  checkImageUpload = 1;
                }

            }
        }
    }

    @OnClick(R.id.ivImageCancel4)
    public void setIvImageCancel4() {
        int cnt = 0;
        LLImage4.setVisibility(View.GONE);
        HMimgFileList.get(3).clear();
        TotalImage--;
        if (TotalImage < 5) {
            ivDealImageCapture.setEnabled(true);
            ivDealImageAttachment.setEnabled(true);
        }
        if (CheckDeal.equalsIgnoreCase("Old")) {
            if (ImageIDListOld.size() >= 4)
                ImageIDListOld.get(3).clear();
        }
        Log.d("HMImgList data:", HMimgFileList.toString());
        for (int j = 0; j < HMimgFileList.size(); j++) {
            if (HMimgFileList.get(j).isEmpty()) {
                cnt++;
                if (cnt == HMimgFileList.size()) {
                    LLImageList.setVisibility(View.GONE);
                    HMimgFileList.clear();
                    //  checkImageUpload = 1;
                }

            }
        }
    }

    @OnClick(R.id.ivImageCancel5)
    public void setIvImageCancel5() {
        int cnt = 0;
        LLImage5.setVisibility(View.GONE);
        HMimgFileList.get(4).clear();

        TotalImage--;
        if (TotalImage < 5) {
            ivDealImageCapture.setEnabled(true);
            ivDealImageAttachment.setEnabled(true);
        }
        if (CheckDeal.equalsIgnoreCase("Old")) {
            if (ImageIDListOld.size() >= 5)
                ImageIDListOld.get(4).clear();
        }
        Log.d("HMImgList data:", HMimgFileList.toString());
        for (int j = 0; j < HMimgFileList.size(); j++) {
            if (HMimgFileList.get(j).isEmpty()) {
                cnt++;
                if (cnt == HMimgFileList.size()) {
                    LLImageList.setVisibility(View.GONE);
                    HMimgFileList.clear();
                    //checkImageUpload = 1;
                }

            }
        }
    }

    @OnClick(R.id.tvPeriod)
    public void setTvPeriod() {
        SelectPeriodDialog();
    }

    @OnClick(R.id.tvCategory)
    public void setTvCategory() {
        SelectCategoryDialog(tvCategory);
    }

    @OnClick(R.id.tvDealTitle)
    public void setTvDealTitle() {
        setTextDialog(tvDealTitle, "Title");
    }

    @OnClick(R.id.tvDealDetails)
    public void setTvDealDetails() {
        setTextDialog(tvDealDetails, "Details");
    }

    @OnClick(R.id.tvCleanAllDeal)
    public void setTvCleanAllDeal() {
        clearData();
    }

    private void SelectCityDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.select_city_cell);
        final SelectCityAdapter adapter;
        final EditText etSearchCity = (EditText) dialog.findViewById(R.id.etSearchCity);
        ImageView ivGetCurrentCity = (ImageView) dialog.findViewById(R.id.ivGetCurrentCity);
        ListView lvSearchCity = (ListView) dialog.findViewById(R.id.lvSearchCity);
        adapter = new SelectCityAdapter(getActivity(), dialog, tvCity, "DealPost", "City", Singleton.GetInstance().getSelectCityName());
        lvSearchCity.setAdapter(adapter);
        ivGetCurrentCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (utilityClass.isInternetConnection()) {
                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        location = gpsTracker.getLocation();
                        Latitude = gpsTracker.getLatitude();
                        Longitude = gpsTracker.getLongitude();
                        if (location != null && (Latitude != 0.0 || Longitude != 0.0)) {
                            utilityClass.processDialogStart();
                            getLocationCityName(etSearchCity, String.valueOf(gpsTracker.getLatitude()) + "," + String.valueOf(gpsTracker.getLongitude()));
                        } else {
                            utilityClass.toast("Location can not found!!!");
                        }
                    } else {
                        utilityClass.showGPSDisabledAlertToUser();
                    }
                } else {
                    utilityClass.toast("Please check your internet settings!!!");
                }
            }
        });
        etSearchCity.addTextChangedListener(new TextWatcher() {


            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                String text = etSearchCity.getText().toString().toLowerCase(Locale.getDefault());
                // adapter.filter(text);
                adapter.getFilter().filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

        dialog.show();
    }

    private void SelectPeriodDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.select_city_cell);
        final SelectCityAdapter adapter;
        final EditText etSearchCity = (EditText) dialog.findViewById(R.id.etSearchCity);
        ImageView ivGetCurrentCity = (ImageView) dialog.findViewById(R.id.ivGetCurrentCity);
        ListView lvSearchCity = (ListView) dialog.findViewById(R.id.lvSearchCity);
        adapter = new SelectCityAdapter(getActivity(), dialog, tvPeriod, "DealPost", "Period", PeriodList);
        lvSearchCity.setAdapter(adapter);
        etSearchCity.setText("Select Period");
        etSearchCity.setEnabled(false);
        ivGetCurrentCity.setVisibility(View.GONE);
        dialog.show();
    }

    private void setTextDialog(final TextView data, String DataType) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.set_text_cell);
        final EditText etSetText = (EditText) dialog.findViewById(R.id.etSetText);
        TextView tvSetTextOk = (TextView) dialog.findViewById(R.id.tvSetTextOk);
        // etSetText.setHint("Describe the deal in your own words and explain  lutmaar members why it’s a good deal. Please don’t just cut and paste details as this is a community site.");
        if (DataType.equalsIgnoreCase("Details"))
            etSetText.setHint("Describe the deal in your own words and explain  lutmaar members why it’s a good deal. Please don’t just cut and paste details as this is a community site.");
        else
            etSetText.setHint(data.getHint().toString());
        etSetText.setText(data.getText().toString());
        tvSetTextOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.setText(etSetText.getText().toString());
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void SelectCategoryDialog(TextView category) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.select_city_cell);
        final SelectCityAdapter adapter;
        final TextView etSearchCity = (TextView) dialog.findViewById(R.id.etSearchCity);
        TextView tvSelectCityLabel = (TextView) dialog.findViewById(R.id.tvSelectCityLabel);
        ImageView ivGetCurrentCity = (ImageView) dialog.findViewById(R.id.ivGetCurrentCity);
        ListView lvSearchCity = (ListView) dialog.findViewById(R.id.lvSearchCity);
        try {
            if (Singleton.GetInstance().getCategoryList().size() == 0) {
                utilityClass.toast("Category List Empty");
                dialog.dismiss();

            } else {
                adapter = new SelectCityAdapter(getActivity(), dialog, category, "DealPost", "Category", Singleton.GetInstance().getCategoryList());
                lvSearchCity.setAdapter(adapter);
                etSearchCity.setHint("Category name");
                tvSelectCityLabel.setText("Select Category");
                ivGetCurrentCity.setVisibility(View.GONE);

                etSearchCity.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                        // When user changed the Text
                        String text = etSearchCity.getText().toString().toLowerCase(Locale.getDefault());
                        // adapter.filter(text);
                        adapter.getFilter().filter(text);
                    }

                    @Override
                    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                    }

                    @Override
                    public void afterTextChanged(Editable arg0) {
                    }
                });
                dialog.show();
            }
        } catch (NullPointerException e) {
            Log.e("Category Null:", e.toString());
            utilityClass.toast("Category List Empty");
            dialog.dismiss();
        }
    }

    public String getMonth(int month) {
        String M = "Jan";
        switch (month) {
            case 0:
                M = "Jan";
                break;
            case 1:
                M = "Feb";
                break;
            case 2:
                M = "Mar";
                break;
            case 3:
                M = "Apr";
                break;
            case 4:
                M = "May";
                break;
            case 5:
                M = "Jan";
                break;
            case 6:
                M = "Jul";
                break;
            case 7:
                M = "Aug";
                break;
            case 8:
                M = "Sep";
                break;
            case 9:
                M = "Oct";
                break;
            case 10:
                M = "Nov";
                break;
            case 11:
                M = "Dec";
                break;
        }
        return M;
    }

    // Date Listener
    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            String year1 = String.valueOf(selectedYear);
            String month1 = String.valueOf(getMonth(selectedMonth));
            String day1 = String.valueOf(selectedDay);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            if (Date_Start_End == 1) {

                try {
                    Date date1 = sdf.parse(String.valueOf(new StringBuilder()
                            .append(year).append("-").append(month + 1).append("-")
                            .append(day)));

                    Date date2 = sdf.parse(year1 + "-" + String.valueOf(selectedMonth + 1) + "-" + day1);

                    System.out.println(sdf.format(date1));
                    System.out.println(sdf.format(date2));

                    if (date1.compareTo(date2) > 0) {
                        Log.i("Date Before Today:", "Date1 is after Date2" + date2.toString());
                        utilityClass.toast("First date can't before today!!!");
                    } else {
                        tvDealStartDate.setText(day1 + " " + month1 + " " + year1);
                        tvDealEndDate.setText(day1 + " " + month1 + " " + year1);
                        Singleton.GetInstance().setFirstDate(year1 + "-" + String.valueOf(selectedMonth + 1) + "-" + day1);

                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            } else {

                try {
                    Date date1 = sdf.parse(Singleton.GetInstance().getFirstDate());

                    Date date2 = sdf.parse(year1 + "-" + String.valueOf(selectedMonth + 1) + "-" + day1);


                    System.out.println(sdf.format(date1));
                    System.out.println(sdf.format(date2));

                    if (date1.compareTo(date2) > 0) {
                        Log.i("Date Befor Today:", "First Date is after Second Date" + date2.toString());
                        utilityClass.toast("End date can't before start date!!!");
                        // tvDealEndDate.setText(day + " " + getMonth(month) + " " + year);
                    } else {
                        tvDealEndDate.setText(day1 + " " + month1 + " " + year1);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //tvDealEndDate.setText(day1 + " " + month1 + " " + year1);
            }

        }
    };

    private boolean checkValidation() {
        boolean ret = true;

        if (PostDealStatus.equalsIgnoreCase("dealsOnLine")) {
            if (tvDealTitle.getText().toString().equalsIgnoreCase("")) {
                utilityClass.toast("Please deal enter title!!!");
                ret = false;
            } else if (!Validation.hasText(etOtherMerchentSite, "merchant site")) {
                ret = false;
            } else if (tvCity.getText().toString().equalsIgnoreCase("Select City...") || tvCity.getText().toString().equalsIgnoreCase("")) {
                utilityClass.toast("Please select city");
                ret = false;
            } else if (!Validation.hasText(etDealPrices, "price")) {
                ret = false;
            } else if (tvDealDetails.getText().toString().equalsIgnoreCase("")) {
                utilityClass.toast("Please deal enter details!!!");
                ret = false;
            } else if (tvCategory.getText().toString().equalsIgnoreCase("Select Category...") || tvCategory.getText().toString().equalsIgnoreCase("")) {
                utilityClass.toast("Please select category");
                ret = false;
            } /*else if (checkImageUpload == 1) {
                utilityClass.toast("Please attach image for deals");
                ret = false;
            }*/
        } else if (PostDealStatus.equalsIgnoreCase("dealsOffLine")) {
            if (tvDealTitle.getText().toString().equalsIgnoreCase("")) {
                utilityClass.toast("Please deal enter title!!!");
                ret = false;
            } else if (!Validation.hasText(etCompanyName, "Company Name")) {
                ret = false;
            } else if (!Validation.hasText(etAddressDealOffline1, "Address line 1")) {
                ret = false;
            } else if (!Validation.hasText(etAddressDealOffline2, "Address line 2")) {
                ret = false;
            } else if (tvCity.getText().toString().equalsIgnoreCase("Select City...") || tvCity.getText().toString().equalsIgnoreCase("")) {
                utilityClass.toast("Please select city");
                ret = false;
            } else if (!Validation.hasText(etPinCode, "PinCode")) {
                ret = false;
            } else if (!Validation.hasText(etDealPrices, "price")) {
                ret = false;
            } else if (tvDealDetails.getText().toString().equalsIgnoreCase("")) {
                utilityClass.toast("Please deal enter details!!!");
                ret = false;
            } else if (tvCategory.getText().toString().equalsIgnoreCase("Select Category...") || tvCategory.getText().toString().equalsIgnoreCase("")) {
                utilityClass.toast("Please select category");
                ret = false;
            } /*else if (checkImageUpload == 1) {
                utilityClass.toast("Please attach image for deals");
                ret = false;
            }*/

        } else if (PostDealStatus.equalsIgnoreCase("freebiesOnLine")) {
            if (tvDealTitle.getText().toString().equalsIgnoreCase("")) {
                utilityClass.toast("Please deal enter title!!!");
                ret = false;
            } else if (!Validation.hasText(etOtherMerchentSite, "merchant site")) {
                ret = false;
            } else if (tvCity.getText().toString().equalsIgnoreCase("Select City...") || tvCity.getText().toString().equalsIgnoreCase("")) {
                utilityClass.toast("Please select city");
                ret = false;
            } else if (tvDealDetails.getText().toString().equalsIgnoreCase("")) {
                utilityClass.toast("Please deal enter details!!!");
                ret = false;
            } else if (tvCategory.getText().toString().equalsIgnoreCase("Select Category...") || tvCategory.getText().toString().equalsIgnoreCase("")) {
                utilityClass.toast("Please select category");
                ret = false;
            }/* else if (checkImageUpload == 1) {
                utilityClass.toast("Please attach image for deals");
                ret = false;
            }*/
        } else if (PostDealStatus.equalsIgnoreCase("freebiesOffLine")) {
            if (tvDealTitle.getText().toString().equalsIgnoreCase("")) {
                utilityClass.toast("Please deal enter title!!!");
                ret = false;
            } else if (!Validation.hasText(etCompanyName, "Company Name")) {
                ret = false;
            } else if (!Validation.hasText(etAddressDealOffline1, "Address line 1")) {
                ret = false;
            } else if (!Validation.hasText(etAddressDealOffline2, "Address line 2")) {
                ret = false;
            } else if (tvCity.getText().toString().equalsIgnoreCase("Select City...") || tvCity.getText().toString().equalsIgnoreCase("")) {
                utilityClass.toast("Please select city");
                ret = false;
            } else if (!Validation.hasText(etPinCode, "PinCode")) {
                ret = false;
            } else if (tvDealDetails.getText().toString().equalsIgnoreCase("")) {
                utilityClass.toast("Please deal enter details!!!");
                ret = false;
            } else if (tvCategory.getText().toString().equalsIgnoreCase("Select Category...") || tvCategory.getText().toString().equalsIgnoreCase("")) {
                utilityClass.toast("Please select category");
                ret = false;
            } /*else if (checkImageUpload == 1) {
                utilityClass.toast("Please attach image for deals");
                ret = false;
            }*/

        } else if (PostDealStatus.equalsIgnoreCase("vouchersOnLine")) {
            if (tvDealTitle.getText().toString().equalsIgnoreCase("")) {
                utilityClass.toast("Please deal enter title!!!");
                ret = false;
            } else if (!Validation.hasText(etOtherMerchentSite, "merchant site")) {
                ret = false;
            } else if (tvCity.getText().toString().equalsIgnoreCase("Select City...") || tvCity.getText().toString().equalsIgnoreCase("")) {
                utilityClass.toast("Please select city");
                ret = false;
            } else if (tvDealDetails.getText().toString().equalsIgnoreCase("")) {
                utilityClass.toast("Please deal enter details!!!");
                ret = false;
            } else if (tvCategory.getText().toString().equalsIgnoreCase("Select Category...") || tvCategory.getText().toString().equalsIgnoreCase("")) {
                utilityClass.toast("Please select category");
                ret = false;
            } else if (!Validation.hasText(etDiscount, "discount")) {
                ret = false;
            } else if (!Validation.hasText(etCode, "code")) {
                ret = false;
            } else if (!Validation.hasText(etSpend, "minimum spend")) {
                ret = false;
            } else if (!Validation.hasText(etApplies, "applies")) {
                ret = false;
            }
        } else if (PostDealStatus.equalsIgnoreCase("vouchersOffLine")) {
            if (tvDealTitle.getText().toString().equalsIgnoreCase("")) {
                utilityClass.toast("Please deal enter title!!!");
                ret = false;
            } else if (!Validation.hasText(etCompanyName, "Company Name")) {
                ret = false;
            } else if (!Validation.hasText(etAddressDealOffline1, "Address line 1")) {
                ret = false;
            } else if (!Validation.hasText(etAddressDealOffline2, "Address line 2")) {
                ret = false;
            } else if (tvCity.getText().toString().equalsIgnoreCase("Select City...") || tvCity.getText().toString().equalsIgnoreCase("")) {
                utilityClass.toast("Please select city");
                ret = false;
            } else if (!Validation.hasText(etPinCode, "PinCode")) {
                ret = false;
            } else if (tvDealDetails.getText().toString().equalsIgnoreCase("")) {
                utilityClass.toast("Please deal enter details!!!");
                ret = false;
            } else if (tvCategory.getText().toString().equalsIgnoreCase("Select Category...") || tvCategory.getText().toString().equalsIgnoreCase("")) {
                utilityClass.toast("Please select category");
                ret = false;
            } else if (!Validation.hasText(etDiscount, "discount")) {
                ret = false;
            } else if (!Validation.hasText(etCode, "code")) {
                ret = false;
            } else if (!Validation.hasText(etSpend, "minimum spend")) {
                ret = false;
            } else if (!Validation.hasText(etApplies, "applies")) {
                ret = false;
            }
        } else if (PostDealStatus.equalsIgnoreCase("competitionsOnLine")) {
            if (tvDealTitle.getText().toString().equalsIgnoreCase("")) {
                utilityClass.toast("Please deal enter title!!!");
                ret = false;
            } else if (!Validation.hasText(etOtherMerchentSite, "merchant site")) {
                ret = false;
            } else if (tvCity.getText().toString().equalsIgnoreCase("Select City...") || tvCity.getText().toString().equalsIgnoreCase("")) {
                utilityClass.toast("Please select city");
                ret = false;
            } else if (tvDealDetails.getText().toString().equalsIgnoreCase("")) {
                utilityClass.toast("Please deal enter details!!!");
                ret = false;
            } else if (tvCategory.getText().toString().equalsIgnoreCase("Select Category...") || tvCategory.getText().toString().equalsIgnoreCase("")) {
                utilityClass.toast("Please select category");
                ret = false;
            } else if (tvPeriod.getText().toString().equalsIgnoreCase("Select Period...") || tvPeriod.getText().toString().equalsIgnoreCase("")) {
                utilityClass.toast("Please select period");
                ret = false;
            } else if (!Validation.hasText(etPrize, "prize")) {
                ret = false;
            } else if (!Validation.hasText(etValue, "value")) {
                ret = false;
            } else if (!Validation.hasText(etCompetitionRules, "rules")) {
                ret = false;
            } else if (!Validation.hasText(etCompetitionLinkRules, "link to rules")) {
                ret = false;
            } /*else if (checkImageUpload == 1) {
                utilityClass.toast("Please attach image for deals");
                ret = false;
            }*/
        } else if (PostDealStatus.equalsIgnoreCase("competitionsOffLine")) {
            if (tvDealTitle.getText().toString().equalsIgnoreCase("")) {
                utilityClass.toast("Please deal enter title!!!");
                ret = false;
            } else if (!Validation.hasText(etCompanyName, "Company Name")) {
                ret = false;
            } else if (!Validation.hasText(etAddressDealOffline1, "Address line 1")) {
                ret = false;
            } else if (!Validation.hasText(etAddressDealOffline2, "Address line 2")) {
                ret = false;
            } else if (tvCity.getText().toString().equalsIgnoreCase("Select City...") || tvCity.getText().toString().equalsIgnoreCase("")) {
                utilityClass.toast("Please select city");
                ret = false;
            } else if (!Validation.hasText(etPinCode, "PinCode")) {
                ret = false;
            } else if (tvDealDetails.getText().toString().equalsIgnoreCase("")) {
                utilityClass.toast("Please deal enter details!!!");
                ret = false;
            } else if (tvCategory.getText().toString().equalsIgnoreCase("Select Category...") || tvCategory.getText().toString().equalsIgnoreCase("")) {
                utilityClass.toast("Please select category");
                ret = false;
            } else if (tvPeriod.getText().toString().equalsIgnoreCase("Select Period...") || tvPeriod.getText().toString().equalsIgnoreCase("")) {
                utilityClass.toast("Please select period");
                ret = false;
            } else if (!Validation.hasText(etPrize, "prize")) {
                ret = false;
            } else if (!Validation.hasText(etValue, "value")) {
                ret = false;
            } else if (!Validation.hasText(etCompetitionRules, "rules")) {
                ret = false;
            } else if (!Validation.hasText(etCompetitionLinkRules, "link to rules")) {
                ret = false;
            } /*else if (checkImageUpload == 1) {
                utilityClass.toast("Please attach image for deals");
                ret = false;
            }*/

        } else if (PostDealStatus.equalsIgnoreCase("ask")) {
            if (tvDealTitle.getText().toString().equalsIgnoreCase("")) {
                utilityClass.toast("Please deal enter title!!!");
                ret = false;
            } else if (tvCity.getText().toString().equalsIgnoreCase("Select City...") || tvCity.getText().toString().equalsIgnoreCase("")) {
                utilityClass.toast("Please select city");
                ret = false;
            } else if (tvDealDetails.getText().toString().equalsIgnoreCase("")) {
                utilityClass.toast("Please deal enter details!!!");
                ret = false;
            } else if (tvCategory.getText().toString().equalsIgnoreCase("Select Category...") || tvCategory.getText().toString().equalsIgnoreCase("")) {
                utilityClass.toast("Please select category");
                ret = false;
            }
        }


        return ret;

    }

    private void restService() {


        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Content-Type", "application/json");
                request.addHeader("X-CSRF-Token", SaveSharedPreferences.getTokon(getActivity()));
                request.addHeader("Cookie", SaveSharedPreferences.getCookie(getActivity()));

                Log.i("Image Upload Tokon : ", SaveSharedPreferences.getTokon(getActivity()));
                Log.i("Image Upload Cookie : ", SaveSharedPreferences.getCookie(getActivity()));
            }
        };

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(constantClass.endPoints)
                .setRequestInterceptor(requestInterceptor)
                .build();

        api = restAdapter.create(RestServices.class);

        HashMap<String, String> HMImageData = new HashMap<String, String>();
        try {
            if (HMimgFileList.get(ImageUploadCount).isEmpty()) {
                ImageUploadCount++;
                if (HMimgFileList.get(ImageUploadCount).isEmpty()) {
                    ImageUploadCount++;
                    if (HMimgFileList.get(ImageUploadCount).isEmpty()) {
                        ImageUploadCount++;
                        if (HMimgFileList.get(ImageUploadCount).isEmpty()) {
                            ImageUploadCount++;
                            if (HMimgFileList.get(ImageUploadCount).isEmpty()) {
                                ImageUploadCount++;
                            }
                        }
                    }
                }
            }
        } catch (IndexOutOfBoundsException e) {
            Log.d("ImageArray IndexOut:", e.toString());
        }

        String imagePath = HMimgFileList.get(ImageUploadCount).get("target_uri");
        HMImageData.put("filename", imagePath.substring(imagePath.lastIndexOf("/") + 1));
        HMImageData.put("target_uri", imagePath);
        HMImageData.put("filemime", "image/jpeg");
        Bitmap thumbnail = (BitmapFactory.decodeFile(imagePath));
        thumbnail = Bitmap.createScaledBitmap(thumbnail, 240, 260, true);
        HMImageData.put("file", utilityClass.ConvertBitmapToBase64Format(thumbnail));

        Log.i("Request image Data:", HMImageData.toString());
        api.PostAnImage(HMImageData, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject result, Response response) {
                // utilityClass.processDialogStop();

                HashMap<String, String> imageDetails = new HashMap<String, String>();
                imageDetails.put("fid", result.get("fid").getAsString().toString());
                ImageIDList.add(imageDetails);
                ImageUploadCount++;
                NetworkErrorCount = 0;
                Log.i("Image Upload Success:", String.valueOf(result));
                try {
                    if (HMimgFileList.get(ImageUploadCount).isEmpty()) {
                        ImageUploadCount++;
                        if (HMimgFileList.get(ImageUploadCount).isEmpty()) {
                            ImageUploadCount++;
                            if (HMimgFileList.get(ImageUploadCount).isEmpty()) {
                                ImageUploadCount++;
                                if (HMimgFileList.get(ImageUploadCount).isEmpty()) {
                                    ImageUploadCount++;
                                } else {
                                    restService();
                                }
                            } else {
                                restService();
                            }
                        } else {
                            restService();
                        }
                    } else {
                        restService();
                    }
                } catch (IndexOutOfBoundsException e) {
                    Log.d("ImageArray IndexOut:", e.toString());
                }
                if (ImageUploadCount >= HMimgFileList.size()) {
                    if (CheckDeal.equalsIgnoreCase("Old")) {
                        for (int j = 0; j < ImageIDListOld.size(); j++) {
                            if (!ImageIDListOld.get(j).isEmpty())
                                ImageIDList.add(ImageIDListOld.get(j));
                        }
                    }
                    DealOnlineRestService();
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
                        restService();
                    }
                } else {
                    utilityClass.processDialogStop();
                    if (error.toString().contains("No address associated with hostname")) {
                        utilityClass.toast("Please check your network connection or try again later.");
                    } else if (error.toString().equals("retrofit.RetrofitError: 500 Internal Server Error")) {
                        utilityClass.toast("Internal Server Error");
                    } else if (error.toString().contains("com.google.gson.JsonSyntaxException")) {
                        utilityClass.toast("Result not found form server");
                    } else if (error.toString().contains("User is not logged in")) {
                        utilityClass.toast("User is not logged in");
                    } else if (error.toString().contains("RetrofitError: 403 : Access denied")) {
                        utilityClass.toast("Access denied for this user");
                    } else if (error.toString().contains("Missing data the file upload can not be completed")) {
                        utilityClass.toast("Missing data the file upload can not be completed");
                    } else {
                        utilityClass.toast("Image can not uploaded");
                    }
                }
            }
        });
    }


    private void DealOnlineRestService() {
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Content-Type", "application/json");
                request.addHeader("X-CSRF-Token", SaveSharedPreferences.getTokon(getActivity()));
                request.addHeader("Cookie", SaveSharedPreferences.getCookie(getActivity()));

                Log.i("Deal Upload Tokon : ", SaveSharedPreferences.getTokon(getActivity()));
                Log.i("deal Upload Cookie : ", SaveSharedPreferences.getCookie(getActivity()));
            }
        };

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(constantClass.endPoints)
                .setRequestInterceptor(requestInterceptor)
                .build();

        api = restAdapter.create(RestServices.class);

        HashMap<String, Object> HMPostADeal = new HashMap<String, Object>();
        HashMap<String, String> HMFieldCat = new HashMap<String, String>();
        HashMap<String, String> HMFieldDeals = new HashMap<String, String>();
        HashMap<String, String> HMBodyValue = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> ALHMBodyValue = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> ALHMFieldDealUrl = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> ALHMFieldPrice = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> ALHMImageFid = new ArrayList<HashMap<String, String>>();

        ArrayList<HashMap<String, Object>> ALHMStartDate = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> ALHMEndDate = new ArrayList<HashMap<String, Object>>();
        HashMap<String, String> HMStartDate = new HashMap<String, String>();
        HashMap<String, Object> HMStartDateValue = new HashMap<String, Object>();
        HashMap<String, Object> HMStartDateUnd = new HashMap<String, Object>();
        HashMap<String, Object> HMEndDateValue = new HashMap<String, Object>();
        HashMap<String, String> HMEndDate = new HashMap<String, String>();
        HashMap<String, Object> HMEndDateUnd = new HashMap<String, Object>();

        ArrayList<HashMap<String, String>> ALHMAddressLocality = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> ALHMDiscount = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> ALHMCode = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> ALHMSpend = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> ALHMApplies = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> ALHMPrize = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> ALHMValue = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> ALHMRules = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> ALHMLinkRule = new ArrayList<HashMap<String, String>>();

        HashMap<String, Object> HMBodyUnd = new HashMap<String, Object>();
        HashMap<String, String> HMFieldDealUrl = new HashMap<String, String>();
        HashMap<String, Object> HMFieldDealUnd = new HashMap<String, Object>();
        HashMap<String, String> HMFieldPriceValue = new HashMap<String, String>();
        HashMap<String, Object> HMFieldPriceUnd = new HashMap<String, Object>();

        HashMap<String, String> HMImageFid = new HashMap<String, String>();
        HashMap<String, Object> HMImageUnd = new HashMap<String, Object>();
        HashMap<String, String> HMAddressLocality = new HashMap<String, String>();
        HashMap<String, String> HMDiscount = new HashMap<String, String>();
        HashMap<String, String> HMCode = new HashMap<String, String>();
        HashMap<String, String> HMSpend = new HashMap<String, String>();
        HashMap<String, String> HMApplies = new HashMap<String, String>();
        HashMap<String, String> HMPrize = new HashMap<String, String>();
        HashMap<String, String> HMPeriod = new HashMap<String, String>();
        HashMap<String, String> HMValue = new HashMap<String, String>();
        HashMap<String, String> HMRules = new HashMap<String, String>();
        HashMap<String, String> HMLinkRule = new HashMap<String, String>();
        HashMap<String, Object> HMAddressUnd = new HashMap<String, Object>();
        HashMap<String, Object> HMDiscountUnd = new HashMap<String, Object>();
        HashMap<String, Object> HMCodeUnd = new HashMap<String, Object>();
        HashMap<String, Object> HMSpendUnd = new HashMap<String, Object>();
        HashMap<String, Object> HMAppliesUnd = new HashMap<String, Object>();
        HashMap<String, Object> HMPrizeUnd = new HashMap<String, Object>();
        HashMap<String, Object> HMValueUnd = new HashMap<String, Object>();
        HashMap<String, Object> HMRulesUnd = new HashMap<String, Object>();
        HashMap<String, Object> HMLinkRuleUnd = new HashMap<String, Object>();

        HashMap<String, String> HMLonLag = new HashMap<String, String>();
        HashMap<String, Object> HMGeom = new HashMap<String, Object>();
        ArrayList<HashMap<String, Object>> ALHMLocation = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> HMLocationUnd = new HashMap<String, Object>();

        HMLonLag.put("lon", String.valueOf(gpsTracker.getLatitude()));
        HMLonLag.put("lat", String.valueOf(gpsTracker.getLongitude()));
        HMGeom.put("geom", HMLonLag);
        ALHMLocation.add(HMGeom);
        HMLocationUnd.put("und", ALHMLocation);


        if (PostDealStatus.equalsIgnoreCase("dealsOffLine")) {
            HMFieldCat.put("und", "64");

            HMBodyValue.put("value", tvDealDetails.getText().toString());
            ALHMBodyValue.add(HMBodyValue);
            HMBodyUnd.put("und", ALHMBodyValue);

            // HMFieldDeals.put("und",String.valueOf(spCategoryDealOnLine.getSelectedItemPosition()));
            int index = Singleton.GetInstance().getCategoryList().indexOf(tvCategory.getText().toString());
            CatID = Singleton.GetInstance().getHMCategory().get(index).get("ID");
            HMFieldDeals.put("und", CatID);
          /*  HMFieldDealUrl.put("url", etOtherMerchentSite.getText().toString());
            ALHMFieldDealUrl.add(HMFieldDealUrl);
            HMFieldDealUnd.put("und", ALHMFieldDealUrl);*/


            HMFieldPriceValue.put("value", etDealPrices.getText().toString());
            ALHMFieldPrice.add(HMFieldPriceValue);
            HMFieldPriceUnd.put("und", ALHMFieldPrice);

            HMStartDate.put("date", tvDealStartDate.getText().toString());
            HMStartDateValue.put("value", HMStartDate);
            ALHMStartDate.add(HMStartDateValue);
            HMStartDateUnd.put("und", ALHMStartDate);

            HMEndDate.put("date", tvDealEndDate.getText().toString());
            HMEndDateValue.put("value", HMEndDate);
            ALHMEndDate.add(HMEndDateValue);
            HMEndDateUnd.put("und", ALHMEndDate);
          /*  HMImageFid.put("fid", ImageID);
            ALHMImageFid.add(HMImageFid);*/
            HMImageUnd.put("und", ImageIDList);


            HMAddressLocality.put("contry", "IN");
            HMAddressLocality.put("postal_code", etPinCode.getText().toString());
            HMAddressLocality.put("thoroughfare", etAddressDealOffline1.getText().toString());
            HMAddressLocality.put("premise", etAddressDealOffline2.getText().toString());
            HMAddressLocality.put("locality", tvCity.getText().toString());
            HMAddressLocality.put("name_line", etOtherMerchentSite.getText().toString());
            HMAddressLocality.put("organisation_name", etCompanyName.getText().toString());
            ALHMAddressLocality.add(HMAddressLocality);
            HMAddressUnd.put("und", ALHMAddressLocality);

            HMPostADeal.put("type", "products");
            HMPostADeal.put("field_category", HMFieldCat);
            HMPostADeal.put("title", tvDealTitle.getText().toString());
            HMPostADeal.put("body", HMBodyUnd);
            HMPostADeal.put("field_topics_1", HMFieldDeals);
            //  HMPostADeal.put("field_deal_url", HMFieldDealUnd);
            HMPostADeal.put("field_price", HMFieldPriceUnd);
            HMPostADeal.put("field_start_date", HMStartDateUnd);
            HMPostADeal.put("field_end_date", HMEndDateUnd);
            //  HMPostADeal.put("field_images", HMImageUnd);
            HMPostADeal.put("field_latitude_longitude", HMLocationUnd);
            HMPostADeal.put("field_postal_address", HMAddressUnd);
            Log.i("Request dealsOffLine Data:", new Gson().toJson(HMPostADeal).toString());

        } else if (PostDealStatus.equalsIgnoreCase("dealsOnLine")) {


            int index = Singleton.GetInstance().getCategoryList().indexOf(tvCategory.getText().toString());
            CatID = Singleton.GetInstance().getHMCategory().get(index).get("ID");

            HMFieldCat.put("und", "64");
            HMFieldDeals.put("und", CatID);

            HMBodyValue.put("value", tvDealDetails.getText().toString());
            ALHMBodyValue.add(HMBodyValue);
            HMBodyUnd.put("und", ALHMBodyValue);


            HMFieldDealUrl.put("url", etOtherMerchentSite.getText().toString());
            ALHMFieldDealUrl.add(HMFieldDealUrl);
            HMFieldDealUnd.put("und", ALHMFieldDealUrl);


            HMFieldPriceValue.put("value", etDealPrices.getText().toString());
            ALHMFieldPrice.add(HMFieldPriceValue);
            HMFieldPriceUnd.put("und", ALHMFieldPrice);

            HMStartDate.put("date", tvDealStartDate.getText().toString());
            HMStartDateValue.put("value", HMStartDate);
            ALHMStartDate.add(HMStartDateValue);
            HMStartDateUnd.put("und", ALHMStartDate);

            HMEndDate.put("date", tvDealEndDate.getText().toString());
            HMEndDateValue.put("value", HMEndDate);
            ALHMEndDate.add(HMEndDateValue);
            HMEndDateUnd.put("und", ALHMEndDate);
            /*HMImageFid.put("fid", ImageID);
            ALHMImageFid.add(HMImageFid);*/
            HMImageUnd.put("und", ImageIDList);


            HMAddressLocality.put("locality", tvCity.getText().toString());
            ALHMAddressLocality.add(HMAddressLocality);
            HMAddressUnd.put("und", ALHMAddressLocality);

            HMPostADeal.put("type", "products");
            HMPostADeal.put("field_category", HMFieldCat);
            //Log.i("Tokon : ", HMFieldCat.toString());
            HMPostADeal.put("title", tvDealTitle.getText().toString());
            HMPostADeal.put("body", HMBodyUnd);
            HMPostADeal.put("field_topics_1", HMFieldDeals);
            HMPostADeal.put("field_deal_url", HMFieldDealUnd);
            HMPostADeal.put("field_price", HMFieldPriceUnd);
            HMPostADeal.put("field_start_date", HMStartDateUnd);
            HMPostADeal.put("field_end_date", HMEndDateUnd);
            //    HMPostADeal.put("field_images", HMImageUnd);
            HMPostADeal.put("field_postal_address", HMAddressUnd);
            Log.i("Request dealsOnLine Data:", new Gson().toJson(HMPostADeal).toString());

        } else if (PostDealStatus.equalsIgnoreCase("freebiesOffLine")) {
            HMFieldCat.put("und", "68");

            HMBodyValue.put("value", tvDealDetails.getText().toString());
            ALHMBodyValue.add(HMBodyValue);
            HMBodyUnd.put("und", ALHMBodyValue);

            // HMFieldDeals.put("und",String.valueOf(spCategoryDealOnLine.getSelectedItemPosition()));
            int index = Singleton.GetInstance().getCategoryList().indexOf(tvCategory.getText().toString());
            CatID = Singleton.GetInstance().getHMCategory().get(index).get("ID");
            HMFieldDeals.put("und", CatID);


            HMStartDate.put("date", tvDealStartDate.getText().toString());
            HMStartDateValue.put("value", HMStartDate);
            ALHMStartDate.add(HMStartDateValue);
            HMStartDateUnd.put("und", ALHMStartDate);

            HMEndDate.put("date", tvDealEndDate.getText().toString());
            HMEndDateValue.put("value", HMEndDate);
            ALHMEndDate.add(HMEndDateValue);
            HMEndDateUnd.put("und", ALHMEndDate);
           /* HMImageFid.put("fid", ImageID);
            ALHMImageFid.add(HMImageFid);*/
            HMImageUnd.put("und", ImageIDList);

            HMAddressLocality.put("contry", "IN");
            HMAddressLocality.put("postal_code", etPinCode.getText().toString());
            HMAddressLocality.put("thoroughfare", etAddressDealOffline1.getText().toString());
            HMAddressLocality.put("premise", etAddressDealOffline2.getText().toString());
            HMAddressLocality.put("locality", tvCity.getText().toString());
            HMAddressLocality.put("administrative_area", "");
            HMAddressLocality.put("name_line", etOtherMerchentSite.getText().toString());
            HMAddressLocality.put("organisation_name", etCompanyName.getText().toString());
            ALHMAddressLocality.add(HMAddressLocality);
            HMAddressUnd.put("und", ALHMAddressLocality);

            HMPostADeal.put("type", "products");
            HMPostADeal.put("field_category", HMFieldCat);
            HMPostADeal.put("title", tvDealTitle.getText().toString());
            HMPostADeal.put("body", HMBodyUnd);
            HMPostADeal.put("field_topics_1", HMFieldDeals);
            HMPostADeal.put("field_start_date", HMStartDateUnd);
            HMPostADeal.put("field_end_date", HMEndDateUnd);
            // HMPostADeal.put("field_images", HMImageUnd);
            HMPostADeal.put("field_latitude_longitude", HMLocationUnd);
            HMPostADeal.put("field_postal_address", HMAddressUnd);
            Log.i("Request freebiesOffLine Data:", new Gson().toJson(HMPostADeal).toString());
        } else if (PostDealStatus.equalsIgnoreCase("freebiesOnLine")) {

            int index = Singleton.GetInstance().getCategoryList().indexOf(tvCategory.getText().toString());
            CatID = Singleton.GetInstance().getHMCategory().get(index).get("ID");

            HMFieldCat.put("und", "68");
            HMFieldDeals.put("und", CatID);

            HMBodyValue.put("value", tvDealDetails.getText().toString());
            ALHMBodyValue.add(HMBodyValue);
            HMBodyUnd.put("und", ALHMBodyValue);

            HMFieldDealUrl.put("url", etOtherMerchentSite.getText().toString());
            ALHMFieldDealUrl.add(HMFieldDealUrl);
            HMFieldDealUnd.put("und", ALHMFieldDealUrl);

            HMStartDate.put("date", tvDealStartDate.getText().toString());
            HMStartDateValue.put("value", HMStartDate);
            ALHMStartDate.add(HMStartDateValue);
            HMStartDateUnd.put("und", ALHMStartDate);

            HMEndDate.put("date", tvDealEndDate.getText().toString());
            HMEndDateValue.put("value", HMEndDate);
            ALHMEndDate.add(HMEndDateValue);
            HMEndDateUnd.put("und", ALHMEndDate);
           /* HMImageFid.put("fid", ImageID);
            ALHMImageFid.add(HMImageFid);*/
            HMImageUnd.put("und", ImageIDList);

            HMAddressLocality.put("locality", tvCity.getText().toString());
            HMAddressLocality.put("administrative_area", "");
            ALHMAddressLocality.add(HMAddressLocality);
            HMAddressUnd.put("und", ALHMAddressLocality);

            HMPostADeal.put("type", "products");
            HMPostADeal.put("field_category", HMFieldCat);
            //Log.i("Tokon : ", HMFieldCat.toString());
            HMPostADeal.put("title", tvDealTitle.getText().toString());
            HMPostADeal.put("body", HMBodyUnd);
            HMPostADeal.put("field_topics_1", HMFieldDeals);
            HMPostADeal.put("field_deal_url", HMFieldDealUnd);
            HMPostADeal.put("field_start_date", HMStartDateUnd);
            HMPostADeal.put("field_end_date", HMEndDateUnd);
            //  HMPostADeal.put("field_images", HMImageUnd);
            HMPostADeal.put("field_postal_address", HMAddressUnd);
            Log.i("Request freebiesOnLine Data:", new Gson().toJson(HMPostADeal).toString());

        } else if (PostDealStatus.equalsIgnoreCase("vouchersOffLine")) {
            HMFieldCat.put("und", "67");

            HMBodyValue.put("value", tvDealDetails.getText().toString());
            ALHMBodyValue.add(HMBodyValue);
            HMBodyUnd.put("und", ALHMBodyValue);

            // HMFieldDeals.put("und",String.valueOf(spCategoryDealOnLine.getSelectedItemPosition()));
            int index = Singleton.GetInstance().getCategoryList().indexOf(tvCategory.getText().toString());
            CatID = Singleton.GetInstance().getHMCategory().get(index).get("ID");
            HMFieldDeals.put("und", CatID);

            HMDiscount.put("value", etDiscount.getText().toString());
            ALHMDiscount.add(HMDiscount);
            HMDiscountUnd.put("und", ALHMDiscount);

            HMCode.put("value", etCode.getText().toString());
            ALHMCode.add(HMCode);
            HMCodeUnd.put("und", ALHMCode);

            HMSpend.put("value", etSpend.getText().toString());
            ALHMSpend.add(HMSpend);
            HMSpendUnd.put("und", ALHMSpend);

            HMApplies.put("value", etApplies.getText().toString());
            ALHMApplies.add(HMApplies);
            HMAppliesUnd.put("und", ALHMApplies);

            HMStartDate.put("date", tvDealStartDate.getText().toString());
            HMStartDateValue.put("value", HMStartDate);
            ALHMStartDate.add(HMStartDateValue);
            HMStartDateUnd.put("und", ALHMStartDate);

            HMEndDate.put("date", tvDealEndDate.getText().toString());
            HMEndDateValue.put("value", HMEndDate);
            ALHMEndDate.add(HMEndDateValue);
            HMEndDateUnd.put("und", ALHMEndDate);

            HMAddressLocality.put("contry", "IN");
            HMAddressLocality.put("postal_code", etPinCode.getText().toString());
            HMAddressLocality.put("thoroughfare", etAddressDealOffline1.getText().toString());
            HMAddressLocality.put("premise", etAddressDealOffline2.getText().toString());
            HMAddressLocality.put("locality", tvCity.getText().toString());
            HMAddressLocality.put("name_line", etOtherMerchentSite.getText().toString());
            HMAddressLocality.put("organisation_name", etCompanyName.getText().toString());
            ALHMAddressLocality.add(HMAddressLocality);
            HMAddressUnd.put("und", ALHMAddressLocality);

            HMPostADeal.put("type", "products");
            HMPostADeal.put("field_category", HMFieldCat);
            HMPostADeal.put("title", tvDealTitle.getText().toString());
            HMPostADeal.put("body", HMBodyUnd);
            HMPostADeal.put("field_latitude_longitude", HMLocationUnd);
            HMPostADeal.put("field_topics_1", HMFieldDeals);
            HMPostADeal.put("field_discount", HMDiscountUnd);
            HMPostADeal.put("field_code", HMCodeUnd);
            HMPostADeal.put("field_minimun_spend", HMSpendUnd);
            HMPostADeal.put("field_applies_to_", HMAppliesUnd);
            HMPostADeal.put("field_start_date", HMStartDateUnd);
            HMPostADeal.put("field_end_date", HMEndDateUnd);
            HMPostADeal.put("field_postal_address", HMAddressUnd);
            Log.i("Request vouchersOffLine Data:", new Gson().toJson(HMPostADeal).toString());

        } else if (PostDealStatus.equalsIgnoreCase("vouchersOnLine")) {

            int index = Singleton.GetInstance().getCategoryList().indexOf(tvCategory.getText().toString());
            CatID = Singleton.GetInstance().getHMCategory().get(index).get("ID");

            HMFieldCat.put("und", "67");
            HMFieldDeals.put("und", CatID);

            HMBodyValue.put("value", tvDealDetails.getText().toString());
            ALHMBodyValue.add(HMBodyValue);
            HMBodyUnd.put("und", ALHMBodyValue);

            HMFieldDealUrl.put("url", etOtherMerchentSite.getText().toString());
            ALHMFieldDealUrl.add(HMFieldDealUrl);
            HMFieldDealUnd.put("und", ALHMFieldDealUrl);

            HMDiscount.put("value", etDiscount.getText().toString());
            ALHMDiscount.add(HMDiscount);
            HMDiscountUnd.put("und", ALHMDiscount);

            HMCode.put("value", etCode.getText().toString());
            ALHMCode.add(HMCode);
            HMCodeUnd.put("und", ALHMCode);

            HMSpend.put("value", etSpend.getText().toString());
            ALHMSpend.add(HMSpend);
            HMSpendUnd.put("und", ALHMSpend);

            HMApplies.put("value", etApplies.getText().toString());
            ALHMApplies.add(HMApplies);
            HMAppliesUnd.put("und", ALHMApplies);

            HMStartDate.put("date", tvDealStartDate.getText().toString());
            HMStartDateValue.put("value", HMStartDate);
            ALHMStartDate.add(HMStartDateValue);
            HMStartDateUnd.put("und", ALHMStartDate);

            HMEndDate.put("date", tvDealEndDate.getText().toString());
            HMEndDateValue.put("value", HMEndDate);
            ALHMEndDate.add(HMEndDateValue);
            HMEndDateUnd.put("und", ALHMEndDate);

            HMAddressLocality.put("locality", tvCity.getText().toString());
            ALHMAddressLocality.add(HMAddressLocality);
            HMAddressUnd.put("und", ALHMAddressLocality);

            HMPostADeal.put("type", "products");
            HMPostADeal.put("field_category", HMFieldCat);
            HMPostADeal.put("title", tvDealTitle.getText().toString());
            HMPostADeal.put("body", HMBodyUnd);
            HMPostADeal.put("field_topics_1", HMFieldDeals);
            HMPostADeal.put("field_deal_url", HMFieldDealUnd);
            HMPostADeal.put("field_discount", HMDiscountUnd);
            HMPostADeal.put("field_code", HMCodeUnd);
            HMPostADeal.put("field_minimun_spend", HMSpendUnd);
            HMPostADeal.put("field_applies_to_", HMAppliesUnd);
            HMPostADeal.put("field_start_date", HMStartDateUnd);
            HMPostADeal.put("field_end_date", HMEndDateUnd);
            HMPostADeal.put("field_postal_address", HMAddressUnd);
            Log.i("Request vouchersOnLine Data:", new Gson().toJson(HMPostADeal).toString());

        } else if (PostDealStatus.equalsIgnoreCase("competitionsOffLine")) {
            HMFieldCat.put("und", "65");

            HMBodyValue.put("value", tvDealDetails.getText().toString());
            ALHMBodyValue.add(HMBodyValue);
            HMBodyUnd.put("und", ALHMBodyValue);

            // HMFieldDeals.put("und",String.valueOf(spCategoryDealOnLine.getSelectedItemPosition()));
            int index = Singleton.GetInstance().getCategoryList().indexOf(tvCategory.getText().toString());
            CatID = Singleton.GetInstance().getHMCategory().get(index).get("ID");
            HMFieldDeals.put("und", CatID);

            HMPrize.put("value", etPrize.getText().toString());
            ALHMPrize.add(HMPrize);
            HMPrizeUnd.put("und", ALHMPrize);

            HMPeriod.put("und", tvPeriod.getText().toString());

            HMValue.put("value", etValue.getText().toString());
            ALHMValue.add(HMValue);
            HMValueUnd.put("und", ALHMValue);

            HMRules.put("value", etCompetitionRules.getText().toString());
            ALHMRules.add(HMRules);
            HMRulesUnd.put("und", ALHMRules);

            HMLinkRule.put("url", etCompetitionLinkRules.getText().toString());
            ALHMLinkRule.add(HMLinkRule);
            HMLinkRuleUnd.put("und", ALHMLinkRule);

          /*  HMImageFid.put("fid", ImageID);
            ALHMImageFid.add(HMImageFid);*/
            HMImageUnd.put("und", ImageIDList);

            HMStartDate.put("date", tvDealStartDate.getText().toString());
            HMStartDateValue.put("value", HMStartDate);
            ALHMStartDate.add(HMStartDateValue);
            HMStartDateUnd.put("und", ALHMStartDate);

            HMEndDate.put("date", tvDealEndDate.getText().toString());
            HMEndDateValue.put("value", HMEndDate);
            ALHMEndDate.add(HMEndDateValue);
            HMEndDateUnd.put("und", ALHMEndDate);

            HMAddressLocality.put("contry", "IN");
            HMAddressLocality.put("postal_code", etPinCode.getText().toString());
            HMAddressLocality.put("thoroughfare", etAddressDealOffline1.getText().toString());
            HMAddressLocality.put("premise", etAddressDealOffline2.getText().toString());
            HMAddressLocality.put("locality", tvCity.getText().toString());
            //HMAddressLocality.put("administrative_area", "");
            //  HMAddressLocality.put("name_line", etOtherMerchentSite.getText().toString());
            HMAddressLocality.put("organisation_name", etCompanyName.getText().toString());
            ALHMAddressLocality.add(HMAddressLocality);
            HMAddressUnd.put("und", ALHMAddressLocality);

            HMPostADeal.put("type", "products");
            HMPostADeal.put("field_category", HMFieldCat);
            HMPostADeal.put("title", tvDealTitle.getText().toString());
            HMPostADeal.put("body", HMBodyUnd);
            HMPostADeal.put("field_topics_1", HMFieldDeals);
            HMPostADeal.put("field_prize", HMPrizeUnd);
            HMPostADeal.put("field_period", HMPeriod);
            HMPostADeal.put("field_value", HMValueUnd);
            HMPostADeal.put("field_rules", HMRulesUnd);
            HMPostADeal.put("field_link_to_rules", HMLinkRuleUnd);
            HMPostADeal.put("field_start_date", HMStartDateUnd);
            HMPostADeal.put("field_end_date", HMEndDateUnd);
            //  HMPostADeal.put("field_images", HMImageUnd);
            HMPostADeal.put("field_latitude_longitude", HMLocationUnd);
            HMPostADeal.put("field_postal_address", HMAddressUnd);
            Log.i("Request competitionsOffLine Data:", new Gson().toJson(HMPostADeal).toString());

        } else if (PostDealStatus.equalsIgnoreCase("competitionsOnLine")) {


            int index = Singleton.GetInstance().getCategoryList().indexOf(tvCategory.getText().toString());
            CatID = Singleton.GetInstance().getHMCategory().get(index).get("ID");

            HMFieldCat.put("und", "65");
            HMFieldDeals.put("und", CatID);

            HMBodyValue.put("value", tvDealDetails.getText().toString());
            ALHMBodyValue.add(HMBodyValue);
            HMBodyUnd.put("und", ALHMBodyValue);


            HMFieldDealUrl.put("url", etOtherMerchentSite.getText().toString());
            ALHMFieldDealUrl.add(HMFieldDealUrl);
            HMFieldDealUnd.put("und", ALHMFieldDealUrl);

            HMPrize.put("value", etPrize.getText().toString());
            ALHMPrize.add(HMPrize);
            HMPrizeUnd.put("und", ALHMPrize);

            HMPeriod.put("und", tvPeriod.getText().toString());

            HMValue.put("value", etValue.getText().toString());
            ALHMValue.add(HMValue);
            HMValueUnd.put("und", ALHMValue);

            HMRules.put("value", etCompetitionRules.getText().toString());
            ALHMRules.add(HMRules);
            HMRulesUnd.put("und", ALHMRules);

            HMLinkRule.put("url", etCompetitionLinkRules.getText().toString());
            ALHMLinkRule.add(HMLinkRule);
            HMLinkRuleUnd.put("und", ALHMLinkRule);

           /* HMImageFid.put("fid", ImageID);
            ALHMImageFid.add(HMImageFid);*/
            HMImageUnd.put("und", ImageIDList);

            HMStartDate.put("date", tvDealStartDate.getText().toString());
            HMStartDateValue.put("value", HMStartDate);
            ALHMStartDate.add(HMStartDateValue);
            HMStartDateUnd.put("und", ALHMStartDate);

            HMEndDate.put("date", tvDealEndDate.getText().toString());
            HMEndDateValue.put("value", HMEndDate);
            ALHMEndDate.add(HMEndDateValue);
            HMEndDateUnd.put("und", ALHMEndDate);

            HMAddressLocality.put("locality", tvCity.getText().toString());
            //HMAddressLocality.put("administrative_area", "");
            ALHMAddressLocality.add(HMAddressLocality);
            HMAddressUnd.put("und", ALHMAddressLocality);

            HMPostADeal.put("type", "products");
            HMPostADeal.put("field_category", HMFieldCat);
            HMPostADeal.put("title", tvDealTitle.getText().toString());
            HMPostADeal.put("body", HMBodyUnd);
            HMPostADeal.put("field_topics_1", HMFieldDeals);
            HMPostADeal.put("field_deal_url", HMFieldDealUnd);
            HMPostADeal.put("field_prize", HMPrizeUnd);
            HMPostADeal.put("field_period", HMPeriod);
            HMPostADeal.put("field_value", HMValueUnd);
            HMPostADeal.put("field_rules", HMRulesUnd);
            HMPostADeal.put("field_link_to_rules", HMLinkRuleUnd);
            HMPostADeal.put("field_start_date", HMStartDateUnd);
            HMPostADeal.put("field_end_date", HMEndDateUnd);
            //  HMPostADeal.put("field_images", HMImageUnd);
            HMPostADeal.put("field_postal_address", HMAddressUnd);
            Log.i("Request competitionsOnLine Data:", new Gson().toJson(HMPostADeal).toString());
        } else if (PostDealStatus.equalsIgnoreCase("ask")) {

            int index = Singleton.GetInstance().getCategoryList().indexOf(tvCategory.getText().toString());
            CatID = Singleton.GetInstance().getHMCategory().get(index).get("ID");

            HMFieldCat.put("und", "66");
            HMFieldDeals.put("und", CatID);

            HMBodyValue.put("value", tvDealDetails.getText().toString());
            ALHMBodyValue.add(HMBodyValue);
            HMBodyUnd.put("und", ALHMBodyValue);

            HMStartDate.put("date", tvDealStartDate.getText().toString());
            HMStartDateValue.put("value", HMStartDate);
            ALHMStartDate.add(HMStartDateValue);
            HMStartDateUnd.put("und", ALHMStartDate);

            HMEndDate.put("date", tvDealEndDate.getText().toString());
            HMEndDateValue.put("value", HMEndDate);
            ALHMEndDate.add(HMEndDateValue);
            HMEndDateUnd.put("und", ALHMEndDate);

            HMAddressLocality.put("contry", "IN");
            HMAddressLocality.put("locality", tvCity.getText().toString());
            ALHMAddressLocality.add(HMAddressLocality);
            HMAddressUnd.put("und", ALHMAddressLocality);

            HMPostADeal.put("type", "products");
            HMPostADeal.put("field_category", HMFieldCat);
            HMPostADeal.put("title", tvDealTitle.getText().toString());
            HMPostADeal.put("body", HMBodyUnd);
            HMPostADeal.put("field_topics_1", HMFieldDeals);
            HMPostADeal.put("field_start_date", HMStartDateUnd);
            HMPostADeal.put("field_end_date", HMEndDateUnd);
            HMPostADeal.put("field_postal_address", HMAddressUnd);
            Log.i("Request Ask Data:", new Gson().toJson(HMPostADeal).toString());
        }
      /*  try {

            Log.i("Original Hashmap:", HMPostADeal.toString());
            JSONObject jsonObjectPostdeal = new JSONObject(new Gson().toJson(HMPostADeal).toString());
            Log.i("JsonObj Postdeal:", jsonObjectPostdeal.toString());
            Log.i("Gson to HasMap: ", String.valueOf(new Gson().fromJson(String.valueOf(jsonObjectPostdeal.toString()),
                    new TypeToken<HashMap<String, Object>>() {
                    }.getType())));
        }catch (JSONException e){
            Log.e("Json to Map error : ",e.toString());
        }*/
        if (utilityClass.isInternetConnection()) {
            if (CheckDeal.equalsIgnoreCase("New")) {  // // For Post Deal
                api.PostADeal(HMPostADeal, new Callback<JsonObject>() {
                    @Override
                    public void success(JsonObject result, Response response) {
                        NetworkErrorCount = 0;
                        Log.i("Post Deal success:", String.valueOf(result));
                        if (ImageIDList.size() >= 1) {
                            TotalUploadedImages = ImageIDList.size();
                            EditImageRestService(result.getAsJsonObject().get("nid").getAsString());
                        } else {
                            utilityClass.processDialogStop();
                            utilityClass.toast("Deal is posted");
                            clearData();
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
                                DealOnlineRestService();
                            }
                        } else {
                            utilityClass.processDialogStop();
                            if (error.toString().contains("No address associated with hostname")) {
                                utilityClass.toast("Please check your network connection or try again later.");
                            } else if (error.toString().equals("retrofit.RetrofitError: 500 Internal Server Error")) {
                                utilityClass.toast("Internal Server Error");
                            } else if (error.toString().contains("com.google.gson.JsonSyntaxException")) {
                                utilityClass.toast("Result not found form server");
                            }/* else if (error.toString().contains("com.google.gson.stream.MalformedJsonException: Use JsonReader.setLenient(true) to accept malformed JSON")) {
                            utilityClass.toast("Deal is posted");
                            Log.e("Post Deal success:", String.valueOf(error.toString()));
                            clearData();
                        } */ else if (error.toString().contains("User is not logged in")) {
                                utilityClass.toast("User is not logged in");
                            } else if (error.toString().contains("RetrofitError: 403 : Access denied")) {
                                utilityClass.toast("Access denied for this user");
                            } else {
                                utilityClass.toast("Deal can not Post");
                            }
                        }
                    }
                });
            } else {   // For Edit Deal
                api.EditADeal(NID, HMPostADeal, new Callback<JsonElement>() {
                    @Override
                    public void success(JsonElement result, Response response) {

                        // utilityClass.toast("Deal is posted");
                        if (ImageIDList.size() >= 1) {
                            TotalUploadedImages = ImageIDList.size();
                            EditImageRestService(result.getAsJsonObject().get("nid").getAsString());
                        } else {
                            utilityClass.processDialogStop();
                            utilityClass.toast("Deal is posted");
                            clearData();
                        }
                        Log.i("Edit Deal success:", String.valueOf(result.getAsJsonObject()));
                        // clearData();
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
                                DealOnlineRestService();
                            }
                        } else {
                            utilityClass.processDialogStop();
                            if (error.toString().contains("No address associated with hostname")) {
                                utilityClass.toast("Please check your network connection or try again later.");
                            } else if (error.toString().equals("retrofit.RetrofitError: 500 Internal Server Error")) {
                                utilityClass.toast("Internal Server Error");
                            } else if (error.toString().contains("com.google.gson.JsonSyntaxException")) {
                                utilityClass.toast("Result not found form server");
                            } else if (error.toString().contains("User is not logged in")) {
                                utilityClass.toast("User is not logged in");
                            } else if (error.toString().contains("RetrofitError: 403 : Access denied")) {
                                utilityClass.toast("Access denied for this user");
                            } else {
                                utilityClass.toast("Deal can not Post");
                            }
                        }
                    }
                });

            }
        } else {
            if (CheckDeal.equalsIgnoreCase("New")) {
                String ImageListData = "";
                if (!HMimgFileList.isEmpty()) {
                    for (int i = 0; i < HMimgFileList.size(); i++) {
                        if (!HMimgFileList.get(i).isEmpty()) {
                            if (ImageListData.equalsIgnoreCase("")) {
                                ImageListData = HMimgFileList.get(i).get("target_uri");
                            } else {
                                ImageListData = ImageListData + "," + HMimgFileList.get(i).get("target_uri");
                            }
                        }
                    }
                }
                if (mydb.insertOfflinePostDeal(ImageListData, new Gson().toJson(HMPostADeal).toString())) {
                    utilityClass.processDialogStop();
                    utilityClass.toast("Offline deal is posted");
                    clearData();
                } else {
                    utilityClass.toast("Can not store data!!!");
                }
            } else {
                utilityClass.toast("Please check your internet settings!!!");
            }
        }
    }

    private void EditImageRestService(final String DealID) {

        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Content-Type", "application/json");
                request.addHeader("X-CSRF-Token", SaveSharedPreferences.getTokon(getActivity()));
                request.addHeader("Cookie", SaveSharedPreferences.getCookie(getActivity()));

                Log.i("Image Edit Tokon : ", SaveSharedPreferences.getTokon(getActivity()));
                Log.i("Image Edit Cookie : ", SaveSharedPreferences.getCookie(getActivity()));
            }
        };
        restAdapter = new RestAdapter.Builder()
                .setEndpoint(constantClass.endPoints)
                .setRequestInterceptor(requestInterceptor)
                .build();
        api = restAdapter.create(RestServices.class);
      /*  ArrayList<HashMap<String, Object>> ALHMStartDate = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> ALHMEndDate = new ArrayList<HashMap<String, Object>>();
        HashMap<String, String> HMStartDate = new HashMap<String, String>();
        HashMap<String, Object> HMStartDateValue = new HashMap<String, Object>();
        HashMap<String, Object> HMStartDateUnd = new HashMap<String, Object>();
        HashMap<String, Object> HMEndDateValue = new HashMap<String, Object>();
        HashMap<String, String> HMEndDate = new HashMap<String, String>();
        HashMap<String, Object> HMEndDateUnd = new HashMap<String, Object>();*/


    /*    HMStartDate.put("date", tvDealStartDate.getText().toString());
        HMStartDateValue.put("value", HMStartDate);
        ALHMStartDate.add(HMStartDateValue);
        HMStartDateUnd.put("und", ALHMStartDate);

        HMEndDate.put("date", tvDealEndDate.getText().toString());
        HMEndDateValue.put("value", HMEndDate);
        ALHMEndDate.add(HMEndDateValue);
        HMEndDateUnd.put("und", ALHMEndDate);*/

        HashMap<String, Object> HMImageEdit = new HashMap<String, Object>();
        HashMap<String, Object> HMImageEditUnd = new HashMap<String, Object>();

        HMImageEditUnd.put("und", ImageIDList);
        HMImageEdit.put("field_images", HMImageEditUnd);
       /* HMImageEdit.put("field_start_date", HMStartDateUnd);
        HMImageEdit.put("field_end_date", HMEndDateUnd);*/

        Log.i("Request Edit image:", new Gson().toJson(HMImageEdit).toString());
        api.EditAnImage(DealID, HMImageEdit, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject result, Response response) {
                NetworkErrorCount = 0;
                TotalUploadedImages--;
                Log.i("Image Edit success:", TotalUploadedImages + " Deal ID: " + String.valueOf(result));
                if (TotalUploadedImages > 0) {
                    EditImageRestService(DealID);
                } else {
                    utilityClass.processDialogStop();
                    utilityClass.toast("Deal is posted");
                    clearData();
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
                        EditImageRestService(DealID);
                    }
                } else {
                    utilityClass.processDialogStop();
                    if (error.toString().contains("No address associated with hostname")) {
                        utilityClass.toast("Please check your network connection or try again later.");
                    } else if (error.toString().equals("retrofit.RetrofitError: 500 Internal Server Error")) {
                        utilityClass.toast("Internal Server Error");
                    } else if (error.toString().contains("com.google.gson.JsonSyntaxException")) {
                        utilityClass.toast("Result not found form server");
                    } else if (error.toString().contains("User is not logged in")) {
                        utilityClass.toast("User is not logged in");
                    } else if (error.toString().contains("RetrofitError: 403 : Access denied")) {
                        utilityClass.toast("Access denied for this user");
                    } else if (error.toString().contains("Missing data the file upload can not be completed")) {
                        utilityClass.toast("Missing data the file upload can not be completed");
                    } else {
                        utilityClass.toast("Image can not uploaded");
                    }
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (CheckDeal.equalsIgnoreCase("New")) {
            mydb.insertPostData(DealType, String.valueOf(onLine), tvDealTitle.getText().toString(), tvDealDetails.getText().toString()
                    , etOtherMerchentSite.getText().toString(), etCompanyName.getText().toString(), tvCategory.getText().toString()
                    , etAddressDealOffline1.getText().toString(), etAddressDealOffline2.getText().toString(), tvCity.getText().toString()
                    , etPinCode.getText().toString(), etDealPrices.getText().toString(), etDiscount.getText().toString(), etCode.getText().toString()
                    , etApplies.getText().toString(), etSpend.getText().toString(), tvPeriod.getText().toString()
                    , etPrize.getText().toString(), etValue.getText().toString(), etCompetitionRules.getText().toString()
                    , etCompetitionLinkRules.getText().toString(), HMimgFileList);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("On Activity Result", requestCode + "");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            imgNameCapture = cursor.getString(columnIndex);
            cursor.close();
            File file = new File(imgNameCapture);
            long length = file.length();
            length = length / 1024;
            Log.d(" Image Path : ", imgNameCapture);
            Log.d(" Image size : ", length + " KB");
            if (length <= 3070) {
                HashMap<String, String> HMImageData = new HashMap<String, String>();
                HMImageData.put("target_uri", imgNameCapture);

                Bitmap thumbnail = (BitmapFactory.decodeFile(imgNameCapture));
                // Bitmap bmpPic1 = Bitmap.createBitmap(thumbnail, 0, 0, thumbnail.getWidth(), thumbnail.getHeight(), m, true);
                thumbnail = Bitmap.createScaledBitmap(thumbnail, 240, 260, true);
                //thumbnail = Bitmap.createScaledBitmap(bmpPic1, 240, 260, true);
                // imgFile = utilityClass.ConvertBitmapToBase64Format(thumbnail);
                ImageIndex = 0;
                for (int j = 0; j < HMimgFileList.size(); j++) {
                    if (HMimgFileList.get(j).isEmpty()) {
                        HMimgFileList.set(j, HMImageData);
                        TotalImage++;
                        ImageIndex = j;
                        break;
                    }
                    ImageIndex++;
                }
                if (ImageIndex == HMimgFileList.size()) {
                    HMimgFileList.add(HMImageData);
                    TotalImage++;
                }
                if (TotalImage >= 5) {
                    ivDealImageCapture.setEnabled(false);
                    ivDealImageAttachment.setEnabled(false);
                }
                Log.d("Image Path ListDetails:", HMimgFileList.toString());

                if (ImageIndex == 0) {
                    LLImageList.setVisibility(View.VISIBLE);
                    LLImage1.setVisibility(View.VISIBLE);
                    ivTakeImage1.setImageBitmap(thumbnail);
                } else if (ImageIndex == 1) {
                    LLImage2.setVisibility(View.VISIBLE);
                    ivTakeImage2.setImageBitmap(thumbnail);
                } else if (ImageIndex == 2) {
                    LLImage3.setVisibility(View.VISIBLE);
                    ivTakeImage3.setImageBitmap(thumbnail);
                } else if (ImageIndex == 3) {
                    LLImage4.setVisibility(View.VISIBLE);
                    ivTakeImage4.setImageBitmap(thumbnail);
                } else if (ImageIndex == 4) {
                    LLImage5.setVisibility(View.VISIBLE);
                    ivTakeImage5.setImageBitmap(thumbnail);
                }

                utilityClass.toast("Image is attached");
                tvAddPhoto.setText("Photo Added.");

            } else {
                utilityClass.toast("Image is not Attached!!! \n Image must be less then 3 MB.");
            }

        } else if (resultCode == getActivity().RESULT_OK) {
            if (imageChooserManager == null) {
                imageChooserManager = new ImageChooserManager(this, requestCode, true);
                imageChooserManager.setImageChooserListener(this);
                imageChooserManager.reinitialize(mediaPath);
            }
            imageChooserManager.submit(requestCode, data);
        }
    }


    public void clearData() {
        onLine = true;
        rBtnDeal.setChecked(true);
        tvDealTitle.setText("");
        etOtherMerchentSite.setText("");
        tvCategory.setText("");
        etCompanyName.setText("");
        etAddressDealOffline1.setText("");
        etAddressDealOffline2.setText("");
        etPinCode.setText("");
        tvCity.setText("");
        tvPeriod.setText("");
        etDealPrices.setText("");
        tvDealDetails.setText("");
        etDiscount.setText("");
        etCode.setText("");
        etSpend.setText("");
        etApplies.setText("");
        etPrize.setText("");
        etValue.setText("");
        etCompetitionRules.setText("");
        etCompetitionLinkRules.setText("");

        tvAddPhoto.setText("Add Photos");
        tvDealStartDate.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(day).append(" ").append(getMonth(month)).append(" ")
                .append(year));
        tvDealEndDate.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(day).append(" ").append(getMonth(month)).append(" ")
                .append(year));
        //   checkImageUpload = 1;
        HMimgFileList.clear();
        ImageIDList.clear();
        ImageIDListOld.clear();
        CheckDeal = "New";
        TotalImage = 0;
        ivDealImageCapture.setEnabled(true);
        ivDealImageAttachment.setEnabled(true);
        LLImageList.setVisibility(View.GONE);
        LLImage1.setVisibility(View.GONE);
        LLImage2.setVisibility(View.GONE);
        LLImage3.setVisibility(View.GONE);
        LLImage4.setVisibility(View.GONE);
        LLImage5.setVisibility(View.GONE);
        tvSwitchOnOff.setEnabled(true);
        rBtnDeal.setEnabled(true);
        rBtnVouchers.setEnabled(true);
        rBtnAsk.setEnabled(true);
        rBtnCompetition.setEnabled(true);
        rBtnFreebies.setEnabled(true);
        NetworkErrorCount = 0;

    }

    private void dealOnLineFormate() {
        dealHideAll();
        //    tvOtherMerchentSite.setVisibility(View.VISIBLE);
        etOtherMerchentSite.setVisibility(View.VISIBLE);
        tvOnlinePostLocation.setVisibility(View.VISIBLE);
        tvCity.setVisibility(View.VISIBLE);
        LLPrice.setVisibility(View.VISIBLE);
        tvDealDetails.setVisibility(View.VISIBLE);
        tvCategory.setVisibility(View.VISIBLE);
        LLDealAddPhoto.setVisibility(View.VISIBLE);
        LLDealDate.setVisibility(View.VISIBLE);
    }

    private void dealOffLineFormate() {
        dealHideAll();
        tvCategory.setVisibility(View.VISIBLE);
        tvOnlinePostLocation.setVisibility(View.VISIBLE);
        etCompanyName.setVisibility(View.VISIBLE);
        llAddressDealOffline1.setVisibility(View.VISIBLE);
        etAddressDealOffline1.setVisibility(View.VISIBLE);
        etAddressDealOffline2.setVisibility(View.VISIBLE);
        tvCity.setVisibility(View.VISIBLE);
        etPinCode.setVisibility(View.VISIBLE);
        LLPrice.setVisibility(View.VISIBLE);
        tvDealDetails.setVisibility(View.VISIBLE);
        LLDealAddPhoto.setVisibility(View.VISIBLE);
        LLDealTakePhoto.setVisibility(View.VISIBLE);
        LLDealDate.setVisibility(View.VISIBLE);

    }

    private void freebiesOnLineFormate() {
        dealHideAll();
        //  tvOtherMerchentSite.setVisibility(View.VISIBLE);
        etOtherMerchentSite.setVisibility(View.VISIBLE);
        tvOnlinePostLocation.setVisibility(View.VISIBLE);
        tvCity.setVisibility(View.VISIBLE);
        tvDealDetails.setVisibility(View.VISIBLE);
        tvCategory.setVisibility(View.VISIBLE);
        LLDealAddPhoto.setVisibility(View.VISIBLE);
        LLDealDate.setVisibility(View.VISIBLE);
    }

    private void freebiesOffLineFormate() {
        dealHideAll();
        tvCategory.setVisibility(View.VISIBLE);
        tvOnlinePostLocation.setVisibility(View.VISIBLE);
        etCompanyName.setVisibility(View.VISIBLE);
        llAddressDealOffline1.setVisibility(View.VISIBLE);
        etAddressDealOffline1.setVisibility(View.VISIBLE);
        etAddressDealOffline2.setVisibility(View.VISIBLE);
        tvCity.setVisibility(View.VISIBLE);
        etPinCode.setVisibility(View.VISIBLE);
        tvDealDetails.setVisibility(View.VISIBLE);
        LLDealAddPhoto.setVisibility(View.VISIBLE);
        LLDealTakePhoto.setVisibility(View.VISIBLE);
        LLDealDate.setVisibility(View.VISIBLE);

    }

    private void voucherOnLineFormate() {
        dealHideAll();
        // tvOtherMerchentSite.setVisibility(View.VISIBLE);
        etOtherMerchentSite.setVisibility(View.VISIBLE);
        tvOnlinePostLocation.setVisibility(View.VISIBLE);
        tvCity.setVisibility(View.VISIBLE);
        etDiscount.setVisibility(View.VISIBLE);
        etCode.setVisibility(View.VISIBLE);
        etApplies.setVisibility(View.VISIBLE);
        etSpend.setVisibility(View.VISIBLE);
        tvDealDetails.setVisibility(View.VISIBLE);
        tvCategory.setVisibility(View.VISIBLE);
        LLDealDate.setVisibility(View.VISIBLE);
    }

    private void voucherOffLineFormate() {
        dealHideAll();
        tvCategory.setVisibility(View.VISIBLE);
        tvOnlinePostLocation.setVisibility(View.VISIBLE);
        etCompanyName.setVisibility(View.VISIBLE);
        llAddressDealOffline1.setVisibility(View.VISIBLE);
        etAddressDealOffline1.setVisibility(View.VISIBLE);
        etAddressDealOffline2.setVisibility(View.VISIBLE);
        tvCity.setVisibility(View.VISIBLE);
        etPinCode.setVisibility(View.VISIBLE);
        etDiscount.setVisibility(View.VISIBLE);
        etCode.setVisibility(View.VISIBLE);
        etApplies.setVisibility(View.VISIBLE);
        etSpend.setVisibility(View.VISIBLE);
        tvDealDetails.setVisibility(View.VISIBLE);
        LLDealDate.setVisibility(View.VISIBLE);

    }

    private void competitionOnLineFormate() {
        dealHideAll();
        //   tvOtherMerchentSite.setVisibility(View.VISIBLE);
        etOtherMerchentSite.setVisibility(View.VISIBLE);
        tvOnlinePostLocation.setVisibility(View.VISIBLE);
        tvCity.setVisibility(View.VISIBLE);
        etPrize.setVisibility(View.VISIBLE);
        tvPeriod.setVisibility(View.VISIBLE);
        etValue.setVisibility(View.VISIBLE);
        etCompetitionLinkRules.setVisibility(View.VISIBLE);
        etCompetitionRules.setVisibility(View.VISIBLE);
        tvDealDetails.setVisibility(View.VISIBLE);
        tvCategory.setVisibility(View.VISIBLE);
        LLDealAddPhoto.setVisibility(View.VISIBLE);
        LLDealDate.setVisibility(View.VISIBLE);
    }

    private void competitionOffLineFormate() {
        dealHideAll();
        tvCategory.setVisibility(View.VISIBLE);
        tvOnlinePostLocation.setVisibility(View.VISIBLE);
        etCompanyName.setVisibility(View.VISIBLE);
        llAddressDealOffline1.setVisibility(View.VISIBLE);
        etAddressDealOffline1.setVisibility(View.VISIBLE);
        etAddressDealOffline2.setVisibility(View.VISIBLE);
        tvCity.setVisibility(View.VISIBLE);
        etPinCode.setVisibility(View.VISIBLE);
        tvDealDetails.setVisibility(View.VISIBLE);
        etPrize.setVisibility(View.VISIBLE);
        tvPeriod.setVisibility(View.VISIBLE);
        etValue.setVisibility(View.VISIBLE);
        etCompetitionLinkRules.setVisibility(View.VISIBLE);
        etCompetitionRules.setVisibility(View.VISIBLE);
        LLDealAddPhoto.setVisibility(View.VISIBLE);
        LLDealTakePhoto.setVisibility(View.VISIBLE);
        LLDealDate.setVisibility(View.VISIBLE);

    }

    private void askFormate() {
        dealHideAll();
        LLOnlineOrOffline.setVisibility(View.GONE);
        tvCategory.setVisibility(View.VISIBLE);
        tvCity.setVisibility(View.VISIBLE);
        tvDealDetails.setVisibility(View.VISIBLE);
    }

    private void dealHideAll() {
        //  tvCategoryOffLine.setVisibility(View.GONE);
        LLOnlineOrOffline.setVisibility(View.VISIBLE);
        //  tvOtherMerchentSite.setVisibility(View.GONE);
        etOtherMerchentSite.setVisibility(View.GONE);
        tvOnlinePostLocation.setVisibility(View.GONE);
        etCompanyName.setVisibility(View.GONE);
        llAddressDealOffline1.setVisibility(View.GONE);
        etAddressDealOffline1.setVisibility(View.GONE);
        etAddressDealOffline2.setVisibility(View.GONE);
        tvCity.setVisibility(View.GONE);
        //  spState.setVisibility(View.GONE);
        etPinCode.setVisibility(View.GONE);
        LLPrice.setVisibility(View.GONE);
        tvDealDetails.setVisibility(View.GONE);
        tvCategory.setVisibility(View.GONE);
        etDiscount.setVisibility(View.GONE);
        etCode.setVisibility(View.GONE);
        etApplies.setVisibility(View.GONE);
        etSpend.setVisibility(View.GONE);
        etPrize.setVisibility(View.GONE);
        tvPeriod.setVisibility(View.GONE);
        etValue.setVisibility(View.GONE);
        etCompetitionLinkRules.setVisibility(View.GONE);
        etCompetitionRules.setVisibility(View.GONE);
        LLDealAddPhoto.setVisibility(View.GONE);
        LLDealTakePhoto.setVisibility(View.GONE);
        LLDealDate.setVisibility(View.GONE);

    }

    @Override
    public void onResume() {
        super.onResume();
        Singleton.GetInstance().MenuHide();
        Singleton.GetInstance().getTvMainHeading().setText("Post a Deal");
    }

    public void editSetValue(JSONObject jsonObject) {

        clearData();
        tvSwitchOnOff.setEnabled(false);
        rBtnDeal.setEnabled(false);
        rBtnVouchers.setEnabled(false);
        rBtnAsk.setEnabled(false);
        rBtnCompetition.setEnabled(false);
        rBtnFreebies.setEnabled(false);
        utilityClass.processDialogStop();
        Log.d("Json Object Data : ", jsonObject.toString());
        CheckDeal = "Old";
        NID = jsonObject.optString("nid");
        tvDealTitle.setText(jsonObject.optString("title"));
        tvDealDetails.setText(jsonObject.optString("body"));
        tvCity.setText(jsonObject.optString("field_postal_address_locality"));
        DealType = jsonObject.optString("field_cat");
        tvCategory.setText(jsonObject.optString("sub_cat"));
        if (jsonObject.optString("field_deal_url").equalsIgnoreCase("")) {
            onLine = false;
            etCompanyName.setText(jsonObject.optString("field_postal_address_organisation_name"));
        } else {
            onLine = true;
            etOtherMerchentSite.setText(jsonObject.optString("field_deal_url"));

        }
        if (DealType.equalsIgnoreCase("deals")) {
            rBtnDeal.setChecked(true);
            etDealPrices.setText(jsonObject.optString("field_price"));
        } else if (DealType.equalsIgnoreCase("vouchers")) {
            rBtnVouchers.setChecked(true);
            etApplies.setText(jsonObject.optString("field_applies_to_"));
            etCode.setText(jsonObject.optString("field_code"));
            etDiscount.setText(jsonObject.optString("field_discount"));
            etSpend.setText(jsonObject.optString("field_minimun_spend"));

        } else if (DealType.equalsIgnoreCase("freebies")) {
            rBtnFreebies.setChecked(true);
        } else if (DealType.equalsIgnoreCase("ask")) {
            rBtnAsk.setChecked(true);
        } else if (DealType.equalsIgnoreCase("competitions")) {
            rBtnCompetition.setChecked(true);
            etCompetitionLinkRules.setText(jsonObject.optString("field_link_to_rules"));
            tvPeriod.setText(jsonObject.optString("field_period"));
            etPrize.setText(jsonObject.optString("field_prize"));
            etCompetitionRules.setText(jsonObject.optString("field_rules"));
            etValue.setText(jsonObject.optString("field_value"));
        }
        etAddressDealOffline1.setText(jsonObject.optString("field_postal_address_thoroughfare"));
        etAddressDealOffline2.setText(jsonObject.optString("address2"));
        etPinCode.setText(jsonObject.optString("field_postal_address_postal_code"));

        //Image set
        if (!DealType.equalsIgnoreCase("ask") && !DealType.equalsIgnoreCase("vouchers")) {
            try {
                JSONArray ArrayImage = jsonObject.getJSONArray("field_images");
                for (int k = 0; k < ArrayImage.length(); k++) {
                    if (k == 0) {
                        LLImageList.setVisibility(View.VISIBLE);
                        LLImage1.setVisibility(View.VISIBLE);
                        Picasso.with(getActivity()).load(ArrayImage.getJSONObject(0).optString("src")).noFade().resize(240, 260).centerInside().into(ivTakeImage1);
                    } else if (k == 1) {
                        LLImage2.setVisibility(View.VISIBLE);
                        Picasso.with(getActivity()).load(ArrayImage.getJSONObject(1).optString("src")).noFade().resize(240, 260).centerInside().into(ivTakeImage2);
                    } else if (k == 2) {
                        LLImage3.setVisibility(View.VISIBLE);
                        Picasso.with(getActivity()).load(ArrayImage.getJSONObject(2).optString("src")).noFade().resize(240, 260).centerInside().into(ivTakeImage3);
                    } else if (k == 3) {
                        LLImage4.setVisibility(View.VISIBLE);
                        Picasso.with(getActivity()).load(ArrayImage.getJSONObject(3).optString("src")).noFade().resize(240, 260).centerInside().into(ivTakeImage4);
                    } else if (k == 4) {
                        LLImage5.setVisibility(View.VISIBLE);
                        Picasso.with(getActivity()).load(ArrayImage.getJSONObject(4).optString("src")).noFade().resize(240, 260).centerInside().into(ivTakeImage5);
                    }
                }
            } catch (JSONException e) {
                Log.e("JSON Image ArrayError", e.toString());
                if (e.toString().contains("field_images of type org.json.JSONObject cannot be converted to JSONArray")) {
                    try {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("field_images");
                        LLImageList.setVisibility(View.VISIBLE);
                        LLImage1.setVisibility(View.VISIBLE);
                        Picasso.with(getActivity()).load(jsonObject1.optString("src")).noFade().resize(240, 260).centerInside().into(ivTakeImage1);

                    } catch (JSONException e1) {
                        Log.e("JSON Image ObjectError", e.toString());
                    }
                }
            }
            String[] ImageID = jsonObject.optString("field_images_fid").split(", ");
            for (int k = 0; k < ImageID.length; k++) {
                TotalImage++;
                HashMap<String, String> imageDetails = new HashMap<String, String>();
                HashMap<String, String> imageDetailsCopy = new HashMap<String, String>();
                imageDetails.put("fid", ImageID[k]);
                imageDetailsCopy.put("fid", ImageID[k]);
                ImageIDListOld.add(imageDetails);
                HMimgFileList.add(imageDetailsCopy);
            }
        }

       /* if (!jsonObject.optString("field_start_date").equalsIgnoreCase("")) {

            String[] splitStartDate = jsonObject.optString("field_start_date").split("\\s+");
            String[] splitEndDate = jsonObject.optString("field_end_date").split("\\s+");
            tvDealStartDate.setText(splitStartDate[2].substring(0, splitEndDate[2].length() - 1) + " " + splitStartDate[1].substring(0, 3) + " " + splitStartDate[3]);
            tvDealEndDate.setText(splitEndDate[2].substring(0, splitEndDate[2].length() - 1) + " " + splitEndDate[1].substring(0, 2) + " " + splitEndDate[3]);
        }*/


    }

    public void getLocationCityName(final EditText cityName, String latlng) {
        restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://maps.googleapis.com")
                .build();
        api = restAdapter.create(RestServices.class);
        Log.d("Old Location City ", SaveSharedPreferences.getCityName(getActivity()));
        api.getCityName(latlng, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject result, Response response) {
                utilityClass.processDialogStop();
                try {
                    // JSONArray array = result.getJSONArray("results");
                    JSONArray array = new JSONArray(result.getAsJsonArray("results").toString());
                    if (array.length() > 0) {
                        JSONObject place = array.getJSONObject(0);
                        JSONArray components = place.getJSONArray("address_components");
                        for (int i = 0; i < components.length(); i++) {
                            JSONObject component = components.getJSONObject(i);
                            JSONArray types = component.getJSONArray("types");
                            for (int j = 0; j < types.length(); j++) {
                                if (types.optString(j).equals("locality")) {
                                    //   return component.optString("long_name");

                                    SaveSharedPreferences.setCityName(getActivity(), component.optString("long_name"));
                                    cityName.setText(component.optString("long_name"));
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Restrofit Error  : ", String.valueOf(error.toString()));
                utilityClass.processDialogStop();
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

    public void getLocationFullAddress(String latlng) {
        restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://maps.googleapis.com")
                .build();
        api = restAdapter.create(RestServices.class);
        Log.d("GetCurrent Address Details: ", latlng);
        api.getCityName(latlng, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject result, Response response) {
                utilityClass.processDialogStop();
                try {
                    // JSONArray array = result.getJSONArray("results");
                    JSONArray array = new JSONArray(result.getAsJsonArray("results").toString());
                    if (array.length() > 0) {
                        JSONObject place = array.getJSONObject(0);
                        JSONArray components = place.getJSONArray("address_components");
                        etAddressDealOffline2.setText("");
                        for (int i = 0; i < components.length(); i++) {
                            JSONObject component = components.getJSONObject(i);
                            JSONArray types = component.getJSONArray("types");
                            for (int j = 0; j < types.length(); j++) {
                                if (types.optString(j).equals("locality")) {
                                    SaveSharedPreferences.setCityName(getActivity(), component.optString("long_name"));
                                    tvCity.setText(component.optString("long_name"));
                                }
                                if (types.optString(j).equals("postal_code")) {
                                    etPinCode.setText(component.optString("long_name"));
                                }
                                if (types.optString(j).equals("route") || types.optString(j).equals("sublocality_level_1")) {
                                    etAddressDealOffline2.setText(etAddressDealOffline2.getText().toString() + component.optString("long_name") + ", ");
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Restrofit Error  : ", String.valueOf(error.toString()));
                utilityClass.processDialogStop();
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

    public void getCategoryRestService() {
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Content-Type", "application/json");
                request.addHeader("X-CSRF-Token", SaveSharedPreferences.getTokon(getActivity()));
                request.addHeader("Cookie", SaveSharedPreferences.getCookie(getActivity()));

                Log.i("Category Tokon : ", SaveSharedPreferences.getTokon(getActivity()));
                Log.i("Category Cookie : ", SaveSharedPreferences.getCookie(getActivity()));
            }
        };

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(constantClass.endPoints)
                .setRequestInterceptor(requestInterceptor)
                .build();
        api = restAdapter.create(RestServices.class);
        api.getCategory(new Callback<JsonObject>() {
            @Override
            public void success(JsonObject result, Response response) {
                utilityClass.processDialogStop();
                ArrayList<HashMap<String, String>> LVHMDealCategory = new ArrayList<HashMap<String, String>>();
                HashMap<String, String> HMDealCategory = new HashMap<String, String>();
                ArrayList<String> CatList = new ArrayList<String>();

                try {

                    JSONArray jsonArrayNew = new JSONArray(result.getAsJsonArray("nodes").toString());
                    for (int i = 0; i < jsonArrayNew.length(); i++) {
                        HMDealCategory = new HashMap<String, String>();
                        CatList.add(jsonArrayNew.getJSONObject(i).getJSONObject("node").optString("name"));
                        HMDealCategory.put("name", jsonArrayNew.getJSONObject(i).getJSONObject("node").optString("name"));
                        HMDealCategory.put("ID", jsonArrayNew.getJSONObject(i).getJSONObject("node").optString("term_id"));
                        LVHMDealCategory.add(HMDealCategory);
                    }
                    Singleton.GetInstance().setCategoryList(CatList);
                    Singleton.GetInstance().setHMCategory(LVHMDealCategory);
                } catch (IllegalStateException e) {
                    Log.e("IllegalStateException:", e.toString());
                } catch (JSONException e) {
                    Log.e("JSONException:", e.toString());
                }
            }

            @Override
            public void failure(RetrofitError error) {
                utilityClass.processDialogStop();
                Log.i("Restrofit Error  : ", String.valueOf(error.toString()));

                if (error.toString().contains("No address associated with hostname")) {
                    utilityClass.toast("Please check your network connection or try again later.");
                } else if (error.toString().equals("retrofit.RetrofitError: 500 Internal Server Error")) {
                    utilityClass.toast("Internal Server Error");
                } else if (error.toString().contains("com.google.gson.JsonSyntaxException")) {
                    utilityClass.toast("Result not found form server");
                } else if (error.toString().contains("User is not logged in")) {
                    utilityClass.toast("User is not logged in");
                } else if (error.toString().contains("RetrofitError: 403 : Access denied")) {
                    utilityClass.toast("Access denied for this user");
                } else if (error.toString().contains("Missing data the file upload can not be completed")) {
                    utilityClass.toast("Missing data the file upload can not be completed");
                } else {
                    utilityClass.toast("Something went wrong");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (ShowCasecounter) {
            case 0:
                showcaseView.setShowcase(new ViewTarget(tvCleanAllDeal), true);
                showcaseView.setContentTitle("With a simple click you can clear all fields");
                showcaseView.setButtonText("Finish");
                ShowCasecounter++;
                break;
            case 1:
                showcaseView.setVisibility(View.GONE);
                showcaseView.hide();
                break;
        }
    }

    private void networkLossDialogShow() {
        final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.network_loss);
        //dialog.setCancelable(false);
        Singleton.GetInstance().setDialog(dialog);

        Button btnTryAgain = (Button) dialog.findViewById(R.id.btnTryAgain);

        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    @Override
    public void onImageChosen(final ChosenImage image) {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (image != null) {
                    imgNameCapture = image.getFilePathOriginal();
                    File file = new File(imgNameCapture);
                    long length = file.length();
                    length = length / 1024;
                    Log.d(" Image Path : ", imgNameCapture);
                    Log.d(" Image size : ", length + " KB");
                    if (length <= 3070) {
                        HashMap<String, String> HMImageData = new HashMap<String, String>();
                        HMImageData.put("target_uri", imgNameCapture);

                        Bitmap thumbnail = (BitmapFactory.decodeFile(imgNameCapture));
                        // Bitmap bmpPic1 = Bitmap.createBitmap(thumbnail, 0, 0, thumbnail.getWidth(), thumbnail.getHeight(), m, true);
                        thumbnail = Bitmap.createScaledBitmap(thumbnail, 240, 260, true);
                        //thumbnail = Bitmap.createScaledBitmap(bmpPic1, 240, 260, true);
                        // imgFile = utilityClass.ConvertBitmapToBase64Format(thumbnail);
                        ImageIndex = 0;
                        for (int j = 0; j < HMimgFileList.size(); j++) {
                            if (HMimgFileList.get(j).isEmpty()) {
                                HMimgFileList.set(j, HMImageData);
                                TotalImage++;
                                ImageIndex = j;
                                break;
                            }
                            ImageIndex++;
                        }
                        if (ImageIndex == HMimgFileList.size()) {
                            HMimgFileList.add(HMImageData);
                            TotalImage++;
                        }
                        if (TotalImage >= 5) {
                            ivDealImageCapture.setEnabled(false);
                            ivDealImageAttachment.setEnabled(false);
                        }
                        Log.d("Image Path ListDetails:", HMimgFileList.toString());

                        if (ImageIndex == 0) {
                            LLImageList.setVisibility(View.VISIBLE);
                            LLImage1.setVisibility(View.VISIBLE);
                            ivTakeImage1.setImageBitmap(thumbnail);
                        } else if (ImageIndex == 1) {
                            LLImage2.setVisibility(View.VISIBLE);
                            ivTakeImage2.setImageBitmap(thumbnail);
                        } else if (ImageIndex == 2) {
                            LLImage3.setVisibility(View.VISIBLE);
                            ivTakeImage3.setImageBitmap(thumbnail);
                        } else if (ImageIndex == 3) {
                            LLImage4.setVisibility(View.VISIBLE);
                            ivTakeImage4.setImageBitmap(thumbnail);
                        } else if (ImageIndex == 4) {
                            LLImage5.setVisibility(View.VISIBLE);
                            ivTakeImage5.setImageBitmap(thumbnail);
                        }

                        utilityClass.toast("Image is attached");
                        tvAddPhoto.setText("Photo Added.");

                    } else {
                        utilityClass.toast("Image is not Attached!!! \n Image must be less then 3 MB.");
                    }
                }
            }
        });
    }

    @Override
    public void onError(final String reason) {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                utilityClass.toast(reason);
            }
        });
    }
}
