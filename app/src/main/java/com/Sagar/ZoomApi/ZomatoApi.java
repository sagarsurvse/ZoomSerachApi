package com.Sagar.ZoomApi;

import com.Sagar.ZoomApi.models.Example;
import com.Sagar.ZoomApi.models.Restaurant_;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public class ZomatoApi {
    private static String URL = "https://developers.zomato.com/";

    private static Methods methods = null;

    public static Methods getService() {
        if(methods == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            methods = retrofit.create(Methods.class);
        }
        return methods;
    }

    public interface Methods{

        @Headers("user-key: fd3ec04e03098453e4457908a766aae1")
        @GET("api/v2.1/search")
        Call<Example> queryData(@Query("q") String query, @Query("start") int startFrom, @Query("entity_id") int entity_id);

        @Headers("user-key: fd3ec04e03098453e4457908a766aae1")
        @GET("api/v2.1/search")
        Call<Restaurant_> queryRestaurant(@Query("res_id") int res_id);

    }
}
