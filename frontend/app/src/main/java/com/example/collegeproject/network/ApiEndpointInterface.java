package com.example.collegeproject.network;

import com.example.collegeproject.models.BookEventModel;
import com.example.collegeproject.models.PostEventModel;
import com.example.collegeproject.models.GetAllEventsModel;
import com.example.collegeproject.models.LikeEventModel;
import com.example.collegeproject.models.LoginModel;
import com.example.collegeproject.models.PostReviewModel;
import com.example.collegeproject.models.RegisterModel;
import com.example.collegeproject.response.BookEventResponse;
import com.example.collegeproject.response.GetAllBookedEventsResponse;
import com.example.collegeproject.response.GetAllEventsResponse;
import com.example.collegeproject.response.GetAllRegisteredEventsResponse;
import com.example.collegeproject.response.GetFavouriteEventsResponse;
import com.example.collegeproject.response.GetTypesAndCategoriesResponse;
import com.example.collegeproject.response.PostEventResponse;
import com.example.collegeproject.response.StandardResponse;
import com.example.collegeproject.response.UserResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiEndpointInterface {

    @POST("user/login")
    Call<UserResponse> login(@Body LoginModel model);

    @POST("user/register")
    Call<UserResponse> register(@Body RegisterModel modelString);

    @Multipart
    @POST("user/profilepic")
    Call<StandardResponse> postUserImage(@Header("Authorization") String auth, @Part MultipartBody.Part image);

    @HTTP(method = "DELETE", path = "user/profilepic", hasBody = false)
    Call<StandardResponse> removeUserImage(@Header("Authorization") String auth);

    @GET("user/me")
    Call<UserResponse> getUserInfo(@Header("Authorization") String auth);

    @GET("reboot")
    Call<StandardResponse> rebootServer();

    @GET("typesAndCategories")
    Call<GetTypesAndCategoriesResponse> getTypesAndCategories();

    @POST("event/add")
    Call<PostEventResponse> postEvent(@Header("Authorization") String auth, @Body PostEventModel model);

    @Multipart
    @POST("event/{eventId}/image")
    Call<StandardResponse> postEventImage(@Header("Authorization") String auth, @Path("eventId") String eventId, @Part MultipartBody.Part image);

    @POST("event/sorted")
    Call<GetAllEventsResponse> getEvents(@Header("Authorization") String auth, @Body GetAllEventsModel model);

    @GET("user/events/liked")
    Call<GetFavouriteEventsResponse> getFavouriteEvents(@Header("Authorization") String auth);

    @POST("user/book")
    Call<BookEventResponse> bookEvent(@Header("Authorization") String auth, @Body BookEventModel model);

    @POST("user/events/like")
    Call<StandardResponse> likeEvent(@Header("Authorization") String auth, @Body LikeEventModel model);

    @HTTP(method = "DELETE", path = "user/events/dislike", hasBody = true)
    Call<StandardResponse> dislikeEvent(@Header("Authorization") String auth, @Body LikeEventModel model);

    @GET("user/events/booked/completed")
    Call<GetAllBookedEventsResponse> getBookedPastEvents(@Header("Authorization") String auth);

    @GET("user/events/booked/upcoming")
    Call<GetAllBookedEventsResponse> getBookedLiveEvents(@Header("Authorization") String auth);

    @GET("user/events/registered/completed")
    Call<GetAllRegisteredEventsResponse> getRegisteredPastEvents(@Header("Authorization") String auth);

    @GET("user/events/registered/upcoming")
    Call<GetAllRegisteredEventsResponse> getRegisteredLiveEvents(@Header("Authorization") String auth);

    @POST("user/events/review")
    Call<StandardResponse> reviewEvent(@Header("Authorization") String auth, @Body PostReviewModel model);

    @POST("user/logout")
    Call<StandardResponse> logOut(@Header("Authorization") String auth);
}