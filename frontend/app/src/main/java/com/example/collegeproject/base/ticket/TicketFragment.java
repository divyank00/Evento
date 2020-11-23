package com.example.collegeproject.base.ticket;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.collegeproject.R;
import com.example.collegeproject.base.ticket.live.LiveTicketFragment;
import com.example.collegeproject.base.ticket.past.PastTicketFragment;
import com.example.collegeproject.helper.PagerAdapter;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TicketFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TicketFragment extends Fragment {

    private ViewPager mViewPager;
    private PagerAdapter adapter;
    private LiveTicketFragment liveTicketFragment;
    private PastTicketFragment pastTicketFragment;
    private CollapsingToolbarLayout collapsingToolbar;

    public TicketFragment() {
        // Required empty public constructor
    }

    public static TicketFragment newInstance() {
        return new TicketFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_ticket, container, false);
        setView(v);
        mViewPager = v.findViewById(R.id.containerVP);
        setupViewPager(mViewPager);
        TabLayout tabLayout = v.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        return v;
    }

    private void setView(View v) {
        collapsingToolbar = v.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Tickets");
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new PagerAdapter(getChildFragmentManager());
        liveTicketFragment = LiveTicketFragment.newInstance();
        pastTicketFragment = PastTicketFragment.newInstance();
        adapter.addFragment(liveTicketFragment, "Live");
        adapter.addFragment(pastTicketFragment, "Past");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);
    }
}