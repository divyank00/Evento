package com.example.collegeproject.base.add;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.collegeproject.R;
import com.example.collegeproject.addEvent.Add1;
import com.example.collegeproject.base.add.live.LiveEventFragment;
import com.example.collegeproject.base.add.past.PastEventFragment;
import com.example.collegeproject.helper.PagerAdapter;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddEvent#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddEvent extends Fragment {

    private FloatingActionButton add;
    private ViewPager mViewPager;
    private PagerAdapter adapter;
    private LiveEventFragment liveEventFragment;
    private PastEventFragment pastEventFragment;
    private CollapsingToolbarLayout collapsingToolbar;

    public AddEvent() {
        // Required empty public constructor
    }

    public static AddEvent newInstance() {
        return new AddEvent();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_event, container, false);
        setView(v);
        mViewPager = v.findViewById(R.id.containerVP);
        setupViewPager(mViewPager);
        TabLayout tabLayout = v.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        return v;
    }

    private void setView(View v) {
        collapsingToolbar = v.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Organised\nEvents");
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
        add = v.findViewById(R.id.add);
        add.setOnClickListener(v1 -> startActivity(new Intent(getContext(), Add1.class)));
    }


    private void setupViewPager(ViewPager viewPager) {
        adapter = new PagerAdapter(getChildFragmentManager());
        liveEventFragment = LiveEventFragment.newInstance();
        pastEventFragment = PastEventFragment.newInstance();
        adapter.addFragment(liveEventFragment, "Live");
        adapter.addFragment(pastEventFragment, "Past");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);
    }
}