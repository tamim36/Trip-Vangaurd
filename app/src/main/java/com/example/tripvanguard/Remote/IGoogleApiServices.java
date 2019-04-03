package com.example.tripvanguard.Remote;

import com.example.tripvanguard.Model.MyPlace;
import com.example.tripvanguard.Model.PlaceDetails;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface IGoogleApiServices {
    @GET
    Call<MyPlace> getNearbyPlaces(@Url String url);

    @GET
    Call<PlaceDetails> getDetailsPlaces(@Url String url);
}
