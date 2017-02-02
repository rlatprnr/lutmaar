package com.wots.lutmaar.Interface;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.wots.lutmaar.GetterSetter.SignUpGetterSetter;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Bhavesh on 20-03-2015.
 */
public interface RestServices {

    /*  @GET("/GetMovieList/{category}")
      public void getTrailerorSongCategoty(@Path("category") String category, Callback<ArrayList<TrailerSongGetterSetter>> callbackGetterSetter);

      @GET("/GetTrailerOrSong/{category}/{movieID}")
      public void getTrailerOrSongList(@Path("category") String category, @Path("movieID") String movieID, Callback<ArrayList<TrailerSongItemListGetterSetter>> callbackGetterSetter);

      @GET("/TotalViewCount/{ID}/{TotalCount}")
      public void setCount(@Path("ID") String ID, @Path("TotalCount") String TotalCount, Callback<Boolean> callbackBoolean);*/
    @Headers({
            "Content-type: application/json"
    })

    //Login User
    @POST("/deals_operations/user/login")
    public void userLogin(@Body HashMap<String, String> arguments, Callback<JsonElement> cb);

    //Register User
    @POST("/deals_operations/user/request_new_password")
    public void userRegistration(@Body HashMap<String, String> arguments, Callback<Object> cb);

    //Sign Up User
    @POST("/deals_operations/user")
    public void SignUp(@Body SignUpGetterSetter signUpGetterSetter, Callback<JsonElement> cb);

    //Edit An Image
    @PUT("/deals_operations/user/{UserID}")
    public void EditProfile(@Path("UserID") String UserID, @Body HashMap<String, Object> arguments, Callback<Object> cb);

    //Post An Image
    @POST("/deals_operations/file")
    public void PostAnImage(@Body HashMap<String, String> arguments, Callback<JsonObject> cb);

    //Edit An Image
    @PUT("/deals_operations/node/{DealID}")
    public void EditAnImage(@Path("DealID") String DealID, @Body HashMap<String, Object> arguments, Callback<JsonObject> cb);


    //Post A Deal Method
    @POST("/deals_operations/node")
    public void PostADeal(@Body HashMap<String, Object> arguments, Callback<JsonObject> cb);

    //Edit A Deal Method
    @PUT("/deals_operations/node/{DealID}")
    public void EditADeal(@Path("DealID") String DealID, @Body HashMap<String, Object> arguments, Callback<JsonElement> cb);

    @POST("/deals_operations/user/logout")
    public void LogOut(Callback<Object> cb);
    // public void LogOut(@Header("X-CSRF-Token") String authToken,Callback<Boolean> cb);

    //Get All Deals
    @GET("/all.json/{cityName}/{catID}")
    public void getAll(@Path("cityName") String cityName, @Path("catID") String CatID, @Query("page") String param, Callback<JsonObject> jsonObjectCallback);

    //Get All Deals
    @GET("/deals-all-service.json/{cityName}/{catID}")
    public void getAllDeals(@Path("cityName") String cityName, @Path("catID") String CatID, @Query("page") String param, Callback<JsonObject> jsonObjectCallback);

    //Get All Vouchers
    @GET("/vouchers-all-service.json/{cityName}/{catID}")
    public void getAllVouchers(@Path("cityName") String cityName, @Path("catID") String CatID, @Query("page") String param, Callback<JsonObject> jsonObjectCallback);

    //Get All Freebies
    @GET("/freebies-all-service.json/{cityName}/{catID}")
    public void getAllFreebies(@Path("cityName") String cityName, @Path("catID") String CatID, @Query("page") String param, Callback<JsonObject> jsonObjectCallback);

    //Get All Contests
    @GET("/competitions-all-service.json/{cityName}/{catID}")
    public void getAllCompetitions(@Path("cityName") String cityName, @Path("catID") String CatID, @Query("page") String param, Callback<JsonObject> jsonObjectCallback);

    //Get All Response
    @GET("/ask-service.json/{cityName}/{catID}")
    public void getAllAsk(@Path("cityName") String cityName, @Path("catID") String CatID, @Query("page") String param, Callback<JsonObject> jsonObjectCallback);

    //Get Single Deal Details
    @GET("/{DealTypeServicePath}/{nid}")
    public void getDealDetails(@Path("DealTypeServicePath") String DealTypeServicePath, @Path("nid") String nid, Callback<JsonObject> jsonObjectCallback);

    @GET("/{DealTypeServicePath}/{nid}")
    public void getDealDetailsHTML(@Path("DealTypeServicePath") String DealTypeServicePath, @Path("nid") String nid, Callback<JsonObject> jsonObjectCallback);
    //Add Deals Comment
    @POST("/deals_operations/comment")
    public void addDealComment(@Body HashMap<String, Object> arguments, Callback<JsonObject> jsonObjectCallback);

    //Get Single Deal Details
    @GET("/comments-service.json/{nid}")
    public void getDealComments(@Path("nid") String nid, @Query("page") String param, Callback<JsonObject> jsonObjectCallback);

    //Set Votes for Hot & Cold
    @POST("/deals_operations/votingapi/set_votes")
    public void setVotes(@Body HashMap<String, Object> arguments, Callback<JsonObject> jsonObjectCallback);

    //Set Votes for Comment Flag
    @POST("/deals_operations/custom_flags/flag_an_entity")
    public void setFlags(@Body HashMap<String, String> arguments, Callback<Object> cb);

    //Get User's Points
    @POST("/deals_operations/userpoints/get_all_user_points")
    public void getMyPoints(@Body HashMap<String, String> arguments, Callback<JsonArray> jsonArrayCallback);

    //Get USer My Delas Details
    @GET("/user-posted-deals.json/{uid}")
    public void getMyDeals(@Path("uid") String uid,@Query("page") String param, Callback<JsonObject> jsonObjectCallback);


    //Get Top 25 Deals
    @GET("/top25-search-results.json/{cityName}")
    public void getTop25(@Path("cityName") String cityName, Callback<JsonObject> jsonObjectCallback);

    //Get Reviews Deals
    @GET("/reviewed-search-results.json/{cityName}")
    public void getReviews(@Path("cityName") String cityName, @Query("page") String param, Callback<JsonObject> jsonObjectCallback);

    //Get Points Deals
    @GET("/points-search-results.json")
    public void getPoints(Callback<JsonObject> jsonObjectCallback);

    //Get Recent Deals
    @GET("/latest-search-results.json/{cityName}")
    public void getRecent(@Path("cityName") String cityName, @Query("page") String param, Callback<JsonObject> jsonObjectCallback);

    //Set Votes for Hot & Cold
    @POST("/deals_operations/search_deals/get_search")
    public void getSearch(@Body HashMap<String, String> arguments, Callback<JsonArray> jsonArrayCallback);

    //Get Location Search by near Deals
    @GET("/all-deals-location.json/{LLAddress}_20/{DealID}")
    public void getAllTypeDealsNearByLocation(@Path("LLAddress") String LLAddress, @Path("DealID") String DealID, Callback<JsonObject> jsonObjectCallback);

    //Get Deals Category Deals
    @GET("/all-topics-service.json")
    public void getCategory(Callback<JsonObject> jsonObjectCallback);

    //Get City name from google
    @GET("/maps/api/geocode/json")
    public void getCityName(@Query("latlng") String param, Callback<JsonObject> jsonObjectCallback);

    //Get Longitude and Latitude from city name
    @GET("/maps/api/geocode/json")
    public void getLatLngLocation(@Query("address") String param, @Query("sensor") String param1, Callback<JsonObject> jsonObjectCallback);

    //Get all user for Inbox Message
    @POST("/deals_operations/custom_privatemsg/get_all_threads_list")
    public void getAllUserInbox(@Body HashMap<String, String> arguments, @Query("page") String param, Callback<JsonArray> jsonArrayCallback);

    //Get Chain of Message (One to One or Group)
    @POST("/deals_operations/custom_privatemsg/get_threads_messages")
    public void getChainOfMessage(@Body HashMap<String, String> arguments, @Query("page") String param, Callback<JsonArray> jsonArrayCallback);

    //Send replay to user (One to One)
    @POST("/deals_operations/custom_privatemsg/reply_to_message")
    public void sendReplayMessage(@Body HashMap<String, String> arguments, Callback<Object> objectCallback);

    //Delete user massage to user (One to One)
    @POST("/deals_operations/custom_privatemsg/delete_message")
    public void deleteMassageMessage(@Body HashMap<String, String> arguments, Callback<Object> objectCallback);

    //send New message to new
    @POST("/deals_operations/custom_privatemsg/send_new_message")
    public void newMessage(@Body HashMap<String, Object> arguments, Callback<JsonObject> objectCallback);

    //get User Details
    @GET("/user-detals-service.json")
    public void getUserDetails(Callback<JsonObject> jsonObjectCallback);

    //get User Unread Message
    @POST("/deals_operations/custom_privatemsg/count_user_new_messages")
    public void getUnreadMessage(@Body HashMap<String, String> arguments,Callback<Object> objectCallback);
}
