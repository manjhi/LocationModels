package com.example.autopalce;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface Api {

    @POST("maps/api/directions/json?")
    Call<Map> placedata(@QueryMap HashMap<String,String> data);

}
