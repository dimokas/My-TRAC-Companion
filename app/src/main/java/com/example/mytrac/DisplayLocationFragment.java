package com.example.mytrac;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Arrays;
import java.util.List;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class DisplayLocationFragment extends Fragment implements OnMapReadyCallback {

    private TextView addressTv, areaTv;
    private Button chooseBtn;
    private LatLng latlng;
    private String address;

    public DisplayLocationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_display_location, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.choose_location); // set toolbar title
        // used to ensure the Up button is used instead of Drawer button
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((MainActivity)getActivity()).getDrawerToggle().setDrawerIndicatorEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        // get data (state, latlng, address) passed from previous activity
        Bundle bundle = this.getArguments();

        latlng = bundle.getParcelable("latlng");
        final String state = bundle.getString("state");
        address = bundle.getString("address");
        Log.i("Location data", "Location data: " + address + ", " + latlng);

        List<String> location = Arrays.asList(address.split(","));

        address = location.get(0).trim();

        addressTv = (TextView) view.findViewById(R.id.chooseLocationAddress);
        addressTv.setText(location.get(0).trim()); // display basic address
        if (location.size() > 1) {
            areaTv = (TextView) view.findViewById(R.id.chooseLocationArea);
            areaTv.setText(location.get(1).trim()); // display area
            address += ", " + location.get(1).trim();
        }
        final String addressToDisplay = address;

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.chooseLocationMap);
        mapFragment.getMapAsync(this);

        chooseBtn = (Button) view.findViewById(R.id.chooseLocationBtn);
        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (state.equals("add")) { // if adding new favorite, use 'add' method of favorite recycler adapter
                    Log.d(TAG,"add");
                    ((MainActivity)getActivity()).getAdapter().add(addressToDisplay);
                }
                else if (state.equals("edit")) { // if editing existing favorite (or adding home/work), use 'edit' method of favorite recycler adapter
                    int position = bundle.getInt("position"); // position in recycler view to edit
                    Log.d(TAG,"edit: " + position);
                    ((MainActivity)getActivity()).getAdapter().edit(position, addressToDisplay);
                }

                // after choosing location return to Home (MainActivity)
                getActivity().getSupportFragmentManager().popBackStack(); // remove this fragment
                getActivity().getSupportFragmentManager().popBackStack(); // remove previous (SearchLocationFragment)

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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Build custom marker icon
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier("ic_pointer_end", "drawable", getActivity().getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, 130, 170, false);

        googleMap.addMarker(new MarkerOptions().position(latlng).title(address).icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));
    }
}
