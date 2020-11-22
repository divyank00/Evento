package com.example.collegeproject.base;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.PersistableBundle;

import com.example.collegeproject.R;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

import static androidx.navigation.Navigation.findNavController;

public class BaseActivity extends AppCompatActivity {

    SpaceNavigationView spaceNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        spaceNavigationView = (SpaceNavigationView) findViewById(R.id.space);
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.showIconOnly();
        spaceNavigationView.setCentreButtonSelectable(true);
        spaceNavigationView.setCentreButtonColor(getResources().getColor(R.color.black));
        spaceNavigationView.setActiveCentreButtonBackgroundColor(getResources().getColor(R.color.blue));
        spaceNavigationView.setActiveCentreButtonIconColor(getResources().getColor(R.color.black));
        spaceNavigationView.setInActiveCentreButtonIconColor(getResources().getColor(R.color.grey));
        spaceNavigationView.addSpaceItem(new SpaceItem("Home", R.drawable.home));
        spaceNavigationView.addSpaceItem(new SpaceItem("Favourite", R.drawable.heart));
        spaceNavigationView.setCentreButtonIcon(R.drawable.add_event);
        spaceNavigationView.addSpaceItem(new SpaceItem("Tickets", R.drawable.ticket));
        spaceNavigationView.addSpaceItem(new SpaceItem("Profile", R.drawable.profile));
        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                spaceNavigationView.changeCurrentItem(-1);
//                findNavController(BaseActivity.this,R.id.fragment).popBackStack(R.id.addEventFragment,true);
                findNavController(BaseActivity.this, R.id.fragment).navigate(R.id.addEventFragment);
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                switch (itemIndex) {
                    case 0:
//                        findNavController(BaseActivity.this,R.id.fragment).popBackStack(R.id.homeFragment,true);
                        findNavController(BaseActivity.this, R.id.fragment).navigate(R.id.homeFragment);
                        break;
                    case 1:
//                        findNavController(BaseActivity.this,R.id.fragment).popBackStack(R.id.favouriteFragment,true);
                        findNavController(BaseActivity.this, R.id.fragment).navigate(R.id.favouriteFragment);
                        break;
                    case 2:
//                        findNavController(BaseActivity.this,R.id.fragment).popBackStack(R.id.ticketsFragment,true);
                        findNavController(BaseActivity.this, R.id.fragment).navigate(R.id.ticketsFragment);
                        break;
                    case 3:
//                        findNavController(BaseActivity.this,R.id.fragment).popBackStack(R.id.profileFragment,true);
                        findNavController(BaseActivity.this, R.id.fragment).navigate(R.id.profileFragment);
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
//        NavDestination source = findNavController(BaseActivity.this,R.id.fragment).getCurrentDestination();
//        if (source != null && source.getId() == R.id.homeFragment) finish();
//        super.onBackPressed();
//        NavDestination destination = findNavController(BaseActivity.this,R.id.fragment).getCurrentDestination();
//        if (destination != null) {
//            Log.d("Current",destination.getId()+"");
//            switch (destination.getId()){
//                case R.id.homeFragment:
//                    spaceNavigationView.changeCurrentItem(0);
//                    break;
//                case R.id.favouriteFragment:
//                    spaceNavigationView.changeCurrentItem(1);
//                    break;
//                case R.id.addEventFragment:
//                    spaceNavigationView.changeCurrentItem(-1);
//                    break;
//                case R.id.ticketsFragment:
//                    spaceNavigationView.changeCurrentItem(2);
//                    break;
//                case R.id.profileFragment:
//                    spaceNavigationView.changeCurrentItem(3);
//                    break;
//            }
//        }
    }
}