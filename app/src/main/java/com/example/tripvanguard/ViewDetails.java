package com.example.tripvanguard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.tripvanguard.Model.PlaceDetails;
import com.example.tripvanguard.Remote.IGoogleApiServices;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewDetails extends AppCompatActivity {

    ImageView photo;
    RatingBar ratingBar;
    TextView place_name, opening_hours, place_address;
    Button btnViewOnMap;

    PlaceDetails mPlace;
    IGoogleApiServices mServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_details);

        mServices = Common.getGoogleAPIService();

        photo = (ImageView)findViewById(R.id.view_photo);
        ratingBar = (RatingBar)findViewById(R.id.rating_bar);
        place_name = (TextView)findViewById(R.id.place_name);
        place_address = (TextView)findViewById(R.id.place_address);
        opening_hours = (TextView)findViewById(R.id.place_Open);
        btnViewOnMap = (Button) findViewById(R.id.btn_show_on_map);

        place_name.setText("");
        place_address.setText("");
        opening_hours.setText("");

        //Photo
        if (Common.currentResults.getPhotos() != null && Common.currentResults.getPhotos().length>0){
            Picasso.with(this)
                    .load(getPhotoOfPlace(Common.currentResults.getPhotos()[0].getPhoto_reference(),1000))
                    .placeholder(R.drawable.ic_image_black_24dp)
                    .error(R.drawable.ic_error_outline_black_24dp)
                    .into(photo);
        }

        //Rating

        if (Common.currentResults.getRating() != null && !TextUtils.isEmpty(Common.currentResults.getRating()))
        {
            ratingBar.setRating(Float.parseFloat(Common.currentResults.getRating()));
        }
        else
        {
            ratingBar.setVisibility(View.GONE);
        }

        //Opening Hours

        if (Common.currentResults.getName() != null)
        {
            place_name.setText("Open now : "+Common.currentResults.getName());
        }
        else
        {
            place_name.setVisibility(View.GONE);
        }
        if (Common.currentResults.getOpening_hours() != null)
        {
            opening_hours.setText("Open now : "+Common.currentResults.getOpening_hours().getOpen_now());
        }
        else
        {
            opening_hours.setVisibility(View.GONE);
        }

        /*if (mPlace.getResult().getFormatted_address() != null)
        {
            place_address.setText("Open now : "+mPlace.getResult().getFormatted_address());
        }
        else
        {
            place_address.setVisibility(View.GONE);
        }*/

        //User Service to fetch address and name
        mServices.getDetailsPlaces(getPlaceDetailUrl(Common.currentResults.getPlace_id()))
                .enqueue(new Callback<PlaceDetails>() {
                    @Override
                    public void onResponse(Call<PlaceDetails> call, Response<PlaceDetails> response) {
                        mPlace = response.body();

                        //place_address.setText(mPlace.getResult().getFormatted_address());
                        //place_name.setText(mPlace.getResult().getName());
                    }

                    @Override
                    public void onFailure(Call<PlaceDetails> call, Throwable t) {

                    }
                });
    }

    private String getPlaceDetailUrl(String place_id) {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json");
        url.append("?placeid="+place_id);
        url.append("&key="+getResources().getString(R.string.google_maps_key));
        Log.d("getPlaceDetailUrl", url.toString());
        return url.toString();
    }

    private String getPhotoOfPlace(String photo_reference, int maxWidth) {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo");
        url.append("?maxwidth="+maxWidth);
        url.append("&photoreference="+photo_reference);
        url.append("&key="+getResources().getString(R.string.google_maps_key));
        Log.d("getPhotoUrl", url.toString());
        return url.toString();
    }
}
