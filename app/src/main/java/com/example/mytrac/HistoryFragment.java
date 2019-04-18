package com.example.mytrac;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.mytrac.ItemClickListeners.OnHistoryItemClickListener;
import com.example.mytrac.RecyclerAdapters.HistoryRecyclerAdapter;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {

    private List<PastDestination> historyList;
    private RecyclerView recyclerView;
    private HistoryRecyclerAdapter mAdapter;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.history); // set toolbar title
        // used to ensure the Up button is used instead of Drawer button
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((MainActivity)getActivity()).getDrawerToggle().setDrawerIndicatorEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        recyclerView = (RecyclerView) view.findViewById(R.id.historyRv);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        String todate = "03/04/2019";
        String yesterdate = "02/04/2019";
        String otherdate = "25/03/2019";

        historyList = new ArrayList<>();
        // use dummy data for testing purposes
        // list should be filled with data from back-end
        // first item should be the most recent aka reverse list returned by service (if it's in chronological order)
        historyList.add(new PastDestination("Andrea Papandreou 35", "Marousi 151 22", PastDestination.State.FAVORITE, todate));
        historyList.add(new PastDestination("Egialias 52", "Marousi", PastDestination.State.WORK, todate));
        historyList.add(new PastDestination("Dimarchou Metaxa 51", "Glifada 156 55", PastDestination.State.NON_FAVORITE, yesterdate));
        historyList.add(new PastDestination("27 Vosporou Street", "Vyronas", PastDestination.State.HOME, otherdate));

        mAdapter = new HistoryRecyclerAdapter(getActivity().getApplicationContext(), historyList);
        mAdapter.setOnHistoryItemClickListener(new OnHistoryItemClickListener() {
            @Override
            public void onItemClick(PastDestination destination) {
                //Toast.makeText(getActivity(), review.getTitle(), Toast.LENGTH_LONG).show();
                Log.d(TAG,"History item = " + destination.getAddress());

                Bundle bundle = HistoryFragment.this.getArguments();
                String state = bundle.getString("state");

                if (state.equals("add")) { // if adding new favorite, use 'add' method of favorite recycler adapter
                    Log.d(TAG,"add");
                    ((MainActivity)getActivity()).getAdapter().add(destination.getAddress() + ", " + destination.getArea());
                }
                else if (state.equals("edit")) { // if editing existing favorite (or adding home/work), use 'edit' method of favorite recycler adapter
                    int position = bundle.getInt("position"); // position in recycler view to edit
                    Log.d(TAG,"edit: " + position);
                    ((MainActivity)getActivity()).getAdapter().edit(position, destination.getAddress() + ", " + destination.getArea());
                }

                // after choosing location return to Home (MainActivity)
                getActivity().getSupportFragmentManager().popBackStack(); // remove this fragment
                getActivity().getSupportFragmentManager().popBackStack(); // remove previous (SearchLocationFragment)
            }
        });

        recyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // if returning to Home (MainActivity) enable navigation drawer and set appropriate toolbar title, will be overriden by SearchLocationFragment's autocomplete widget otherwise
        ((MainActivity)getActivity()).getDrawerToggle().setDrawerIndicatorEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.home);
    }

}
