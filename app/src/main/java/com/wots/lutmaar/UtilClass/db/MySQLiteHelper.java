package com.wots.lutmaar.UtilClass.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Bhavesh on 23-01-2015.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {
    public static final String TABLE_DEAL = "tbl_deal";
    public static final String TABLE_VOUCHERS = "tbl_vouchers";
    public static final String TABLE_FREEBIES = "tbl_freebies";
    public static final String TABLE_COMPETITION = "tbl_competition";
    public static final String TABLE_POST_DEAL = "tbl_postdeal";
    public static final String TABLE_OFFLINE_POST_DEAL = "tbl_offline_postdeal";
    public static final String TABLE_LUTMAAR = "tbl_lutmaar";
    private static final String DATABASE_NAME = "lutmaar.db";
    public static final String JSONOBJECT = "jsonObject";
    public static final String D_ID = "did";
    public static final String N_ID = "nid";
    public static final String TITLE = "title";
    public static final String BODY = "body";
    public static final String USER_NAME = "user_name";
    public static final String USER_PICTURE = "user_picture";
    public static final String VALUE = "value";
    public static final String CREATED = "created";
    public static final String START_DATE = "start_date";
    public static final String END_DATE = "end_date";
    public static final String COMMENT_COUNT = "comment_count";
    public static final String DEAL_CAT = "deal_cat";
    public static final String DEAL_SUB_CAT = "deal_sub_cat";
    public static final String DEAL_IMAGES = "deal_images";
    public static final String DEAL_IMAGES_ID = "deal_images_id";
    public static final String ORG_NAME = "org_name";
    public static final String ADD1 = "add1";
    public static final String ADD2 = "add2";
    public static final String CITY = "city";
    public static final String STATE = "state";
    public static final String PIN_CODE = "pin_code";
    public static final String DEAL_PATH = "deal_path";
    public static final String DEAL_URL = "deal_url";
    public static final String TIME_STAMP = "time_stamp";
    public static final String USER_VOTE = "user_vote";
    public static final String PRICE = "price";
    public static final String APPLIES_TO = "applies_to";
    public static final String CODE = "code";
    public static final String DISCOUNT = "discount";
    public static final String MIN_SPEND = "min_spend";
    public static final String LINK_TO_RULES = "link_to_rules";
    public static final String PERIOD = "period";
    public static final String PRIZE = "prize";
    public static final String RULES = "rules";
    public static final String C_VALUES = "c_values";
    public static final String DEALTYPE = "dealtype";
    public static final String ONLINEOFFLINE = "onlineoffline";


    private static final int DATABASE_VERSION = 1;
    // Database deal listing record
    private static final String TBL_LUTMAAR_CREATE = "create table "
            + TABLE_LUTMAAR + "(" + D_ID + " integer primary key autoincrement, " + DEAL_CAT + " varchar, " + JSONOBJECT + " varchar);";
    // Database deal post record
    private static final String TBL_POST_DEAL = "create table "
            + TABLE_POST_DEAL + "(" + D_ID + " integer primary key autoincrement," +
            TITLE + " varchar, " + BODY + " varchar, " + DEALTYPE + " varchar, " + ONLINEOFFLINE + " varchar, " + DEAL_URL + " varchar, " +
            ORG_NAME + " varchar, " + DEAL_CAT + " varchar ," + ADD1 + " varchar, " + ADD2 + " varchar, " + CITY + " varchar, " +
            PIN_CODE + " varchar, " + PRICE + " varchar ," + DISCOUNT + " varchar, " + CODE + " varchar, " + MIN_SPEND + " varchar, " +
            APPLIES_TO + " varchar, " + PERIOD + " varchar, " + PRIZE + " varchar, " + C_VALUES + " varchar, " + RULES + " varchar, " + LINK_TO_RULES + " varchar, " +
            "img1 varchar, img2 varchar, img3 varchar, img4 varchar, img5 varchar );";
    // Database deal post Offline record
    private static final String TBL_OFFLINE_POST_DEAL_CREATE = "create table "
            + TABLE_OFFLINE_POST_DEAL + "(" + D_ID + " integer primary key autoincrement, " + DEAL_IMAGES + " varchar, " + JSONOBJECT + " varchar);";

    // Database deal table creation sql statement
    private static final String TBL_DEAL_CREATE = "create table "
            + TABLE_DEAL + "(" + D_ID + " integer primary key autoincrement," +
            N_ID + " varchar," + TITLE + " varchar," + BODY + " varchar," + USER_NAME + " varchar," + USER_PICTURE + " varchar," + VALUE + " varchar," +
            CREATED + " varchar," + START_DATE + " varchar," + END_DATE + " varchar," + COMMENT_COUNT + " varchar," + DEAL_CAT + " varchar," +
            DEAL_SUB_CAT + " varchar," + DEAL_IMAGES + " varchar," + DEAL_IMAGES_ID + " varchar," + ORG_NAME + " varchar," + ADD1 + " varchar," +
            ADD2 + " varchar," + CITY + " varchar," + STATE + " varchar," + PIN_CODE + " varchar," + DEAL_PATH + " varchar," + DEAL_URL + " varchar," +
            TIME_STAMP + " varchar," + USER_VOTE + " varchar," + PRICE + " varchar);";

    // Database freebies table sql statement
    private static final String TBL_FREEBIES_CREATE = "create table "
            + TABLE_FREEBIES + "(" + D_ID + " integer primary key autoincrement," +
            N_ID + " varchar," + TITLE + " varchar," + BODY + " varchar," + USER_NAME + " varchar," + USER_PICTURE + " varchar," + VALUE + " varchar," +
            CREATED + " varchar," + START_DATE + " varchar," + END_DATE + " varchar," + COMMENT_COUNT + " varchar," + DEAL_CAT + " varchar," +
            DEAL_SUB_CAT + " varchar," + DEAL_IMAGES + " varchar," + DEAL_IMAGES_ID + " varchar," + ORG_NAME + " varchar," + ADD1 + " varchar," +
            ADD2 + " varchar," + CITY + " varchar," + STATE + " varchar," + PIN_CODE + " varchar," + DEAL_PATH + " varchar," + DEAL_URL + " varchar," +
            TIME_STAMP + " varchar," + USER_VOTE + " varchar);";

    // Database freebies table sql statement
    private static final String TBL_VOUCHERS_CREATE = "create table "
            + TABLE_VOUCHERS + "(" + D_ID + " integer primary key autoincrement," +
            N_ID + " varchar," + TITLE + " varchar," + BODY + " varchar," + USER_NAME + " varchar," + USER_PICTURE + " varchar," + VALUE + " varchar," +
            CREATED + " varchar," + START_DATE + " varchar," + END_DATE + " varchar," + COMMENT_COUNT + " varchar," + DEAL_CAT + " varchar," +
            DEAL_SUB_CAT + " varchar," + DEAL_IMAGES + " varchar," + DEAL_IMAGES_ID + " varchar," + ORG_NAME + " varchar," + ADD1 + " varchar," +
            ADD2 + " varchar," + CITY + " varchar," + STATE + " varchar," + PIN_CODE + " varchar," + DEAL_PATH + " varchar," + DEAL_URL + " varchar," +
            TIME_STAMP + " varchar," + USER_VOTE + " varchar," + APPLIES_TO + " varchar," + CODE + " varchar," + DISCOUNT + " varchar," + MIN_SPEND + " varchar);";

    // Database freebies table sql statement
    private static final String TBL_COMPETITION_CREATE = "create table "
            + TABLE_COMPETITION + "(" + D_ID + " integer primary key autoincrement," +
            N_ID + " varchar," + TITLE + " varchar," + BODY + " varchar," + USER_NAME + " varchar," + USER_PICTURE + " varchar," + VALUE + " varchar," +
            CREATED + " varchar," + START_DATE + " varchar," + END_DATE + " varchar," + COMMENT_COUNT + " varchar," + DEAL_CAT + " varchar," +
            DEAL_SUB_CAT + " varchar," + DEAL_IMAGES + " varchar," + DEAL_IMAGES_ID + " varchar," + ORG_NAME + " varchar," + ADD1 + " varchar," +
            ADD2 + " varchar," + CITY + " varchar," + STATE + " varchar," + PIN_CODE + " varchar," + DEAL_PATH + " varchar," + DEAL_URL + " varchar," +
            TIME_STAMP + " varchar," + USER_VOTE + " varchar," + LINK_TO_RULES + " varchar," + PRIZE + " varchar," + PERIOD + " varchar,"
            + RULES + " varchar," + C_VALUES + " varchar);";


    @Override
    public void onCreate(SQLiteDatabase db) {
     /*   db.execSQL(TBL_DEAL_CREATE);
        db.execSQL(TBL_FREEBIES_CREATE);
        db.execSQL(TBL_VOUCHERS_CREATE);
        db.execSQL(TBL_COMPETITION_CREATE);*/
        db.execSQL(TBL_LUTMAAR_CREATE);
        db.execSQL(TBL_POST_DEAL);
        db.execSQL(TBL_OFFLINE_POST_DEAL_CREATE);

        Log.i("Lutmaar:createDBTable=", TBL_LUTMAAR_CREATE);
        Log.i("Lutmaar:createDBTabl=", TBL_POST_DEAL);
        Log.i("Lutmaar:createDBTabl=", TBL_OFFLINE_POST_DEAL_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
     /*   db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEAL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VOUCHERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPETITION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FREEBIES);*/
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LUTMAAR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POST_DEAL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OFFLINE_POST_DEAL);
        onCreate(db);
    }

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /*  public Cursor getData(String name) {
          SQLiteDatabase db = this.getReadableDatabase();

          Cursor res = db.rawQuery("select * from " + TABLE_DEAL+ " where " + D_ID + "='" + name + "'", null);
          return res;
      }
  */
    public void insertJSONData(String dealType, JSONObject dataObject) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues Values = new ContentValues();
        Values.put(DEAL_CAT, dealType);
        Values.put(JSONOBJECT, String.valueOf(dataObject));
        db.insert(TABLE_LUTMAAR, null, Values);
    }

    public boolean insertOfflinePostDeal(String DealImages, String dataObject) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues Values = new ContentValues();
        Values.put(DEAL_IMAGES, DealImages);
        Values.put(JSONOBJECT, dataObject);
        db.insert(TABLE_OFFLINE_POST_DEAL, null, Values);
        return true;
    }

    public void insertPostData(String pdDealType, String pdOnlineOffline, String pdTitle, String pdBody, String pdMerchantPath
            , String pdCompanyName, String pdCategory, String pdAdd1, String pdAdd2, String pdCity, String pdPinCode, String pdPrice
            , String pdDiscount, String pdCode, String pdAppliesTo, String pdMinSpend, String pdPeriod, String pdPrize, String pdValue, String pdRules
            , String pdLinkToRules, ArrayList<HashMap<String, String>> HMImageList) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues Values = new ContentValues();
        db.execSQL("delete from " + TABLE_POST_DEAL);

        Values.put(DEALTYPE, pdDealType);
        Values.put(ONLINEOFFLINE, pdOnlineOffline);
        Values.put(TITLE, pdTitle);
        Values.put(BODY, pdBody);
        Values.put(DEAL_URL, pdMerchantPath);
        Values.put(ORG_NAME, pdCompanyName);
        Values.put(DEAL_CAT, pdCategory);
        Values.put(ADD1, pdAdd1);
        Values.put(ADD2, pdAdd2);
        Values.put(CITY, pdCity);
        Values.put(PIN_CODE, pdPinCode);
        Values.put(PRICE, pdPrice);
        Values.put(DISCOUNT, pdDiscount);
        Values.put(CODE, pdCode);
        Values.put(APPLIES_TO, pdAppliesTo);
        Values.put(MIN_SPEND, pdMinSpend);
        Values.put(PERIOD, pdPeriod);
        Values.put(PRIZE, pdPrize);
        Values.put(C_VALUES, pdValue);
        Values.put(RULES, pdRules);
        Values.put(LINK_TO_RULES, pdLinkToRules);
        for (int i = 0; i < HMImageList.size(); i++) {
            if (HMImageList.get(i).isEmpty()) {
                continue;
            }
            if (i == 0)
                Values.put("img1", HMImageList.get(0).get("target_uri"));
            else if (i == 1)
                Values.put("img2", HMImageList.get(1).get("target_uri"));
            else if (i == 2)
                Values.put("img3", HMImageList.get(2).get("target_uri"));
            else if (i == 3)
                Values.put("img4", HMImageList.get(3).get("target_uri"));
            else if (i == 4)
                Values.put("img5", HMImageList.get(4).get("target_uri"));
        }
        db.insert(TABLE_POST_DEAL, null, Values);
    }

    public int numberOfRows(String tblName) {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, tblName);
        return numRows;
    }


    public void deleteDealTypeData(String DealType, String TableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TableName + " where " + DEAL_CAT + "='" + DealType + "'");
    }


    public Cursor getAllData(String DealType, String tblName) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.rawQuery("select * from " + tblName + " where " + DEAL_CAT + "='" + DealType + "'", null);
        return res;
    }

    public Cursor getAllPostDealData() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.rawQuery("select * from " + TABLE_POST_DEAL, null);
        return res;
    }

    public Cursor getAllOfflinePostDealData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_OFFLINE_POST_DEAL, null);
        return res;
    }

    public boolean deleteOfflinePostDealOneByOne( String recordId) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from "+TABLE_OFFLINE_POST_DEAL + " where "+D_ID+ " = "+ recordId);
      //  Cursor res = db.rawQuery("select * from " + TABLE_OFFLINE_POST_DEAL, null);
        return true;
    }


}
