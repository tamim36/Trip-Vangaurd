package com.example.tripvanguard;

import com.example.tripvanguard.Model.Results;
import com.example.tripvanguard.Model.Result;
import com.example.tripvanguard.Remote.IGoogleApiServices;
import com.example.tripvanguard.Remote.RetrofitClient;

public class Common {

    public static Results currentResults;

    private static final String Google_API_URL = "https://maps.googleapis.com/";

    public static IGoogleApiServices getGoogleAPIService()
    {
        return RetrofitClient.getClient(Google_API_URL).create(IGoogleApiServices.class);
    }
}
