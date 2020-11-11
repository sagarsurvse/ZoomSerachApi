package com.Sagar.ZoomApi;


import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.Sagar.ZoomApi.models.Example;
import com.Sagar.ZoomApi.models.Restaurant;
import com.Sagar.ZoomApi.repository.ZomatoApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivityViewModel extends ViewModel {

    private static final String TAG = "debug";
    private MutableLiveData<List<Restaurant>> resroLiveData;
    private MutableLiveData<Integer> resultsFoundLiveData;
    private MutableLiveData<Integer> startFromLiveData;

    public MainActivityViewModel() {
        resroLiveData = new MutableLiveData<>();
        resultsFoundLiveData = new MutableLiveData<>();
        startFromLiveData = new MutableLiveData<>();
    }

    public LiveData<List<Restaurant>> getResroLiveData() {
        return resroLiveData;
    }

    public LiveData<Integer> getResultsFound() {
        return resultsFoundLiveData;
    }

    public MutableLiveData<Integer> getStartFrom() {
        return startFromLiveData;
    }

    public void queryRestaurants(String query, boolean loadMore) {

        int startFrom;
        if (loadMore) {
            startFrom = startFromLiveData.getValue() + 20;
        } else {
            startFrom = 0;
        }
        Call<Example> exampleCall = ZomatoApi.getService().queryData(query, startFrom, 3);

        exampleCall.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "onResponse: unsuccessful");
                    return;
                }
                Example example = response.body();
                List<Restaurant> restaurants;
                if (loadMore) {
                    restaurants = new ArrayList<>(resroLiveData.getValue());
                    restaurants.addAll(example.getRestaurants());
                } else {
                    resultsFoundLiveData.setValue(example.getResultsFound());
                    restaurants = example.getRestaurants();
                }

                resroLiveData.setValue(restaurants);
                startFromLiveData.setValue(example.getResultsStart());
                Log.d(TAG, "onResponse: Successful\n");

                StringBuilder s = new StringBuilder();
                s.append("Results found = ").append(example.getResultsFound());
                s.append("Results start = ").append(example.getResultsStart());
                s.append("Results shown = ").append(example.getResultsShown());
                s.append("No. of Restaurants = ").append(example.getRestaurants().size());
                s.append("\n\n");
                Log.d(TAG, "onResponse: \n" + s.toString());

            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {

                Log.d(TAG, "onFailure: " + t.toString());
            }
        });
    }
}
