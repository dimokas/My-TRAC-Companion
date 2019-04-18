package com.example.mytrac;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentLocationFragment extends Fragment implements OnMapReadyCallback {

    private TextView addressTv, areaTv;
    private Button chooseBtn;
    private ImageView icon;

    private String locationAddress = "";
    private String locationArea = "";
    private String locationCombined = "";
    private LatLng locationCoordinates;

    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 1;

    public CurrentLocationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_current_location, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.choose_on_map); // set toolbar title
        // used to ensure the Up button is used instead of Drawer button
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((MainActivity)getActivity()).getDrawerToggle().setDrawerIndicatorEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Use fields to define the data types to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG);

        // Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.builder(placeFields).build();

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(getActivity().getApplicationContext());

        // Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            placesClient.findCurrentPlace(request).addOnSuccessListener(((response) -> {

                for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                    Log.i(TAG, "Place " + placeLikelihood.getPlace().getAddress() + " has likelihood: " + placeLikelihood.getLikelihood());
                    locationCombined = placeLikelihood.getPlace().getAddress();

                    List<String> location = Arrays.asList(locationCombined.split(","));
                    locationAddress = location.get(0).trim(); // store basic address
                    locationCombined = locationAddress;
                    if (location.size() > 1) {
                        locationArea = location.get(1).trim(); // store area
                        locationCombined += ", " + locationArea;
                    }

                    locationCoordinates = placeLikelihood.getPlace().getLatLng();
                    break;
                }
                // Get the SupportMapFragment and request notification
                // when the map is ready to be used.
                if (isAdded()) {
                    SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.chooseOnMap);
                    mapFragment.getMapAsync(this);
                }

            })).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                }
            });
        } else {
            // A local method to request required permissions;
            // See https://developer.android.com/training/permissions/requesting
            getLocationPermission();
        }

        addressTv = (TextView) view.findViewById(R.id.chooseOnMapAddress);
        addressTv.setText(R.string.locating_address);

        areaTv = (TextView) view.findViewById(R.id.chooseOnMapArea);
        areaTv.setVisibility(View.INVISIBLE);

        icon = (ImageView) view.findViewById(R.id.chooseOnMapIcon);
        icon.setVisibility(View.INVISIBLE);

        chooseBtn = (Button) view.findViewById(R.id.chooseOnMapBtn);
        chooseBtn.setBackgroundResource(R.drawable.btn_grey_wide);
        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!locationCombined.equals("")) {

                    Bundle bundle = CurrentLocationFragment.this.getArguments();
                    String state = bundle.getString("state");

                    if (state.equals("add")) { // if adding new favorite, use 'add' method of favorite recycler adapter
                        Log.d(TAG,"add");
                        ((MainActivity)getActivity()).getAdapter().add(locationCombined);
                    }
                    else if (state.equals("edit")) { // if editing existing favorite (or adding home/work), use 'edit' method of favorite recycler adapter
                        int position = bundle.getInt("position"); // position in recycler view to edit
                        Log.d(TAG,"edit: " + position);
                        ((MainActivity)getActivity()).getAdapter().edit(position, locationCombined);
                    }

                    // after choosing location return to Home (MainActivity)
                    getActivity().getSupportFragmentManager().popBackStack(); // remove this fragment
                    getActivity().getSupportFragmentManager().popBackStack(); // remove previous (SearchLocationFragment)

                }

            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // if returning to Home (MainActivity) enable navigation drawer and set appropriate toolbar title, will be overriden by SearchLocationFragment's autocomplete widget otherwise
        ((MainActivity)getActivity()).getDrawerToggle().setDrawerIndicatorEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.home);
    }

    private void getLocationPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQUEST_ACCESS_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Build custom marker icon
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier("ic_pointer_end", "drawable", getActivity().getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, 130, 170, false);

        // Detect user's current position via GPS
        googleMap.addMarker(new MarkerOptions().position(locationCoordinates).title(locationCombined).icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationCoordinates, 15));

        Log.d(TAG, "MapTest 1: " + locationCombined);
        Log.d(TAG, "MapTest 2: " + locationCoordinates);

        addressTv.setText(locationAddress);
        areaTv.setText(locationArea);
        areaTv.setVisibility(View.VISIBLE);
        icon.setVisibility(View.VISIBLE);
        chooseBtn.setBackgroundResource(R.drawable.btn_orange_wide);
    }
}
