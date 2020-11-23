package com.example.collegeproject.base;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.collegeproject.R;
import com.example.collegeproject.base.add.AddEvent;
import com.example.collegeproject.base.favourite.FavouriteFragment;
import com.example.collegeproject.base.home.HomeFragment;
import com.example.collegeproject.base.profile.ProfileFragment;
import com.example.collegeproject.base.ticket.TicketFragment;
import com.example.collegeproject.models.GetAllEventsModel;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

import static com.example.collegeproject.base.home.HomeFragment.filterModel;
import static com.example.collegeproject.base.home.HomeFragment.prevFilterModel;

public class BaseActivity extends AppCompatActivity {

    private SpaceNavigationView spaceNavigationView;
    private boolean isCenterButtonSelected = false;
    public boolean isAdapterFiltered = false;
    static public final double animationPlaybackSpeed = 0.75;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        loadFragment(new HomeFragment(), "Home");
        spaceNavigationView = findViewById(R.id.space);
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.showIconOnly();
        spaceNavigationView.setCentreButtonSelectable(true);
        spaceNavigationView.setCentreButtonColor(getResources().getColor(R.color.black));
        spaceNavigationView.setActiveCentreButtonBackgroundColor(getResources().getColor(R.color.blue));
        spaceNavigationView.setActiveCentreButtonIconColor(getResources().getColor(R.color.black));
        spaceNavigationView.setInActiveCentreButtonIconColor(getResources().getColor(R.color.grey));
        spaceNavigationView.addSpaceItem(new SpaceItem("Home", R.drawable.home));
        spaceNavigationView.addSpaceItem(new SpaceItem("Favourite", R.drawable.heart_filled));
        spaceNavigationView.setCentreButtonIcon(R.drawable.add_event);
        spaceNavigationView.addSpaceItem(new SpaceItem("Tickets", R.drawable.ticket));
        spaceNavigationView.addSpaceItem(new SpaceItem("Profile", R.drawable.profile_filled));
        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                if (!isCenterButtonSelected) {
                    spaceNavigationView.changeCurrentItem(-1);
                    loadFragment(new AddEvent(), "Add");
                    isCenterButtonSelected = !isCenterButtonSelected;
                }
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                isCenterButtonSelected = false;
                switch (itemIndex) {
                    case 0:
                        loadFragment(new HomeFragment(), "Home");
                        break;
                    case 1:
                        loadFragment(new FavouriteFragment(), "Favourite");
                        break;
                    case 2:
                        loadFragment(new TicketFragment(), "Booked");
                        break;
                    case 3:
                        loadFragment(new ProfileFragment(), "Profile");
                        break;
                }
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {

            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        spaceNavigationView.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    void loadFragment(Fragment f, String tag) {
        if (f != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, f, tag)
                    .commitAllowingStateLoss();
        }
    }

    public void filter() {
        if (filterModel.isDifferent(prevFilterModel)) {
            HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag("Home");
            if (homeFragment != null) {
                homeFragment.isSearch = true;
                homeFragment.fetchData();
            }
        }
    }

    public void unFilter() {
        HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag("Home");
        if (homeFragment != null) {
            homeFragment.isSearch = false;
            filterModel = new GetAllEventsModel();
            homeFragment.fetchData();
        }
    }

    private void closeKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null) {
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public void enableTouch() {
        HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag("Home");
        if (homeFragment != null) {
            homeFragment.shimmerRecyclerView.suppressLayout(false);
        }
    }

    public void disableTouch() {
        HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag("Home");
        if (homeFragment != null) {
            homeFragment.shimmerRecyclerView.suppressLayout(true);
        }
    }
}