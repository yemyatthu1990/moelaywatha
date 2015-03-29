package com.yemyatthu.moelaywatha.retrofitservice;

import com.google.gson.JsonObject;
import com.yemyatthu.moelaywatha.Config;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Query;

/**
 * Created by yemyatthu on 3/28/15.
 */
public interface WeatherRetrofitService {
  @Headers("x-api-key: "+ Config.API_KEY)
  @GET( "/data/2.5/weather")
  JsonObject getWeatherData(@Query("lat") float latitude,@Query("lon") float longitude);
}
