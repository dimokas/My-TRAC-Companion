package com.example.mytrac;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mytrac.DialogFragments.AddToFavoritesDialogFragment;
import com.example.mytrac.DialogFragments.LoginDialogFragment;
import com.example.mytrac.ItemClickListeners.OnFavoriteItemClickListener;
import com.example.mytrac.RecyclerAdapters.FavoritesRecyclerAdapter;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TwitterLoginButton tLoginBtn;

    private Toolbar myToolbar;

    private DrawerLayout drawerLayout;
    public ActionBarDrawerToggle drawerToggle;

    private TextView searchLocationTv;
    private ImageView addFavoriteIcon;

    private List<String> favoriteList;
    private RecyclerView recyclerView;
    private FavoritesRecyclerAdapter mAdapter;

    //for themes
    public static MainActivity mainActivity;
    public static String uID;
    public static int userCategory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPrefs = getSharedPreferences("mytrac.user.settings", MODE_PRIVATE);
        userCategory = sharedPrefs.getInt("userCategory", 0);
        uID = sharedPrefs.getString("uID", null);

        if (userCategory == Constants.DEFAULT_USER)
            setTheme(R.style.DefaultTheme);
        else if (userCategory == Constants.LOW_VISION_USER)
            setTheme(R.style.LowVisionTheme);

        super.onCreate(savedInstanceState);
        mainActivity = this;

        initTwitter();
        setContentView(R.layout.activity_main);

        // set up toolbar as actionbar
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(R.string.home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // set up navigation drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.open_navigation_drawer,
                R.string.close_navigation_drawer);

        drawerLayout.addDrawerListener(drawerToggle);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        //menuItem.setChecked(true);
                        // close drawer when item is tapped
                        drawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        // To add or edit menu items go to res/menu/drawer_view.xml
                        switch (menuItem.getItemId()) {
                            case R.id.nav_dialog:
                                // display dialog for testing purposes
                                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                Fragment prev = getSupportFragmentManager().findFragmentByTag("login_dialog");
                                if (prev != null) {
                                    ft.remove(prev);
                                }
                                AddToFavoritesDialogFragment favFragment = new AddToFavoritesDialogFragment();
                                favFragment.show(ft, "fav_dialog");
                                return true;
                            case R.id.nav_logout:
                                // log out of Twitter for testing purposes
                                twitterLogOut();
                                return true;
                        }

                        return true;
                    }
                });

        if (uID == null) {
            // Load login dialog
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment prev = getSupportFragmentManager().findFragmentByTag("login_dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            DialogFragment dialogFragment = new LoginDialogFragment();
            dialogFragment.show(ft, "login_dialog");
        }

        searchLocationTv = (TextView) findViewById(R.id.searchLocationTv);
        addFavoriteIcon = (ImageView) findViewById(R.id.favoritesAddIcon);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findFavoriteDestination();
            }
        };
        // Add searching functionality to main search bar and plus icon
        searchLocationTv.setOnClickListener(listener); // this should be REPLACED with a different listener if main search bar will not be used to add favorites
        addFavoriteIcon.setOnClickListener(listener);

        // set up recycler view displaying favorites
        recyclerView = (RecyclerView) findViewById(R.id.favoritesRv);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // list containing addresses (=strings)
        // first two items (home & work) must always exist ("Tap to set" if those are not set by user)
        favoriteList = new ArrayList<>();
        // use dummy data for testing purposes
        // list should be filled with data from back-end
        favoriteList.add(getResources().getString(R.string.tap_to_set));
        favoriteList.add(getResources().getString(R.string.tap_to_set));
//        favoriteList.add("27 Vosporou Street, Vyronas");
//        favoriteList.add("Egialias 52, Marousi");
//        favoriteList.add("Leof. Dimarchou Aggelou Metropoleos");
//        favoriteList.add("Andrea Papandreou 35, Marousi");

        mAdapter = new FavoritesRecyclerAdapter(this, favoriteList);
        mAdapter.setOnFavoriteItemClickListener(new OnFavoriteItemClickListener() { // will be called to add Home or Work favorite
            @Override
            public void onItemClick(int position) {
                SearchLocationFragment newFrag = new SearchLocationFragment();

                Bundle bundle = new Bundle();
                bundle.putString("state", "edit"); // edits existing list items
                bundle.putInt("position", position);
                newFrag.setArguments(bundle);

                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main_screen, newFrag).addToBackStack(null).commit();
            }
        });

        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item))
            return true;

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) { // if navigation drawer is open, back button closes it
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result to the fragment, which will then pass the result to the login
        // button. Handle login fragment twitter login.
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("login_dialog");
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    //initialise twitter config
    private void initTwitter() {
        TwitterConfig config = new TwitterConfig.Builder(MainActivity.this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(getResources().getString(R.string.twitter_api_key), getResources().getString(R.string.twitter_api_secret)))
                .debug(true)
                .build();
        Twitter.initialize(config);
    }

    // clear active twitter session on logout
    public void twitterLogOut() {
        clearSharedPreferences();
        TwitterCore.getInstance().getSessionManager().clearActiveSession();
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private void findFavoriteDestination() {
        // Create a new Fragment to be placed in the activity layout
        SearchLocationFragment nextFragment = new SearchLocationFragment();

        Bundle bundle = new Bundle();
        bundle.putString("state", "add"); // will add new favorite item to list
        nextFragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_screen, nextFragment, "SearchLocationFragment").addToBackStack("SearchLocationFragment").commit();
    }

    public FavoritesRecyclerAdapter getAdapter() {
        return mAdapter;
    }

    public ActionBarDrawerToggle getDrawerToggle() {
        return drawerToggle;
    }

    private void clearSharedPreferences(){
        SharedPreferences.Editor editor = MainActivity.mainActivity.getSharedPreferences("mytrac.user.settings", MODE_PRIVATE).edit();
        editor.remove("userCategory");
        editor.remove("uID");
        editor.commit();

    }
}
