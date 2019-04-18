package com.example.mytrac;


import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchLocationFragment extends Fragment {

    private Toolbar myToolbar;
    private TextView chooseOnMap, history;
    private ConstraintLayout autocompleteSearchLayout;

    public SearchLocationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_location, container, false);

        myToolbar = (Toolbar) getActivity().findViewById(R.id.my_toolbar);

        // make the Places SDK autocomplete widget visible for this fragment
        autocompleteSearchLayout = myToolbar.findViewById(R.id.autocompleteSearchLayout);
        autocompleteSearchLayout.setVisibility(View.VISIBLE);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        // get data from bundle
        Bundle bundle = this.getArguments();

        final String state = bundle.getString("state");
        String location = bundle.getString("location");
        final int position = bundle.getInt("position");


        // Initialize Places.
        if (!Places.isInitialized()) {
            Places.initialize(getActivity().getApplicationContext(), getResources().getString(R.string.google_api_key));
        }

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getActivity().getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));
        autocompleteFragment.setText(location); // if editing an existing address, prefill the autocomplete widget
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("Place success", "Place: " + place.getName() + ", " + place.getId() + ", " + place.getAddress() + ", " + place.getLatLng());

                DisplayLocationFragment newFrag = new DisplayLocationFragment();
                Bundle bundle = new Bundle();
                bundle.putString("name", place.getName());
                bundle.putString("address", place.getAddress());
                bundle.putParcelable("latlng", place.getLatLng());
                bundle.putString("state", state);
                bundle.putInt("position", position);
                newFrag.setArguments(bundle);

                // send location data to be displayed in map
                FragmentManager fragmentManager =  getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main_screen, newFrag).addToBackStack(null).commit();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("Place error", "An error occurred: " + status);
            }
        });

        // return to main activity if dismiss (X) button is pressed
        ImageView dismissImg = autocompleteSearchLayout.findViewById(R.id.searchDismissImg);
        dismissImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        chooseOnMap = (TextView) view.findViewById(R.id.chooseOnMapTv);
        chooseOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a new Fragment to be placed in the activity layout
                CurrentLocationFragment nextFragment = new CurrentLocationFragment();

                Bundle bundle = new Bundle();
                bundle.putString("state", state);
                bundle.putInt("position", position);
                nextFragment.setArguments(bundle);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main_screen, nextFragment, "CurrentLocationFragment").addToBackStack("CurrentLocationFragment").commit();
            }
        });

        history = (TextView) view.findViewById(R.id.historyTv);
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a new Fragment to be placed in the activity layout
                HistoryFragment nextFragment = new HistoryFragment();

                Bundle bundle = new Bundle();
                bundle.putString("state", state);
                bundle.putInt("position", position);
                nextFragment.setArguments(bundle);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main_screen, nextFragment, "HistoryFragment").addToBackStack("HistoryFragment").commit();
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        autocompleteSearchLayout.setVisibility(View.GONE); // leaving this fragment so make Places SDK autocomplete widget GONE
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.home); // used when returning to Home (MainActivity), will be overriden by next fragment otherwise
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true); // used when loading next fragment, will be overriden by MainActivity otherwise
    }

}
