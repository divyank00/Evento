package com.example.collegeproject.base.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.baoyz.widget.PullRefreshLayout;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.example.collegeproject.R;
import com.example.collegeproject.base.home.filterFAB.filter.FiltersLayout;
import com.example.collegeproject.base.home.filterFAB.filter.FiltersMotionLayout;
import com.example.collegeproject.event.EventInfoActivity;
import com.example.collegeproject.models.EventDetailsModel;
import com.example.collegeproject.models.GetAllEventsModel;
import com.example.collegeproject.models.GetEventsModel;
import com.example.collegeproject.network.ObjectModel;
import com.example.collegeproject.response.GetAllEventsResponse;
import com.example.collegeproject.viewModel.GetEventsViewModel;
import com.example.collegeproject.viewModel.LikeEventViewModel;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment implements Adapter.itemClick {

    private GetEventsViewModel getEventsViewModel;
    public ShimmerRecyclerView shimmerRecyclerView;
    private Adapter adapter;
    private PullRefreshLayout refresh;
    private List<GetEventsModel> list;
    private Observer<ObjectModel> observer, likeObserver, dislikeObserver;
    private CollapsingToolbarLayout collapsingToolbar;
    private LikeEventViewModel likeEventViewModel;
    static public GetAllEventsModel filterModel, prevFilterModel;
    private FiltersLayout filtersLayout;
    private FiltersMotionLayout filtersMotionLayout;
    private LinearLayout noSearch, noResult;
    public boolean isSearch = false;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        setView(v);
        return v;
    }

    private void setView(View v) {
        filterModel = new GetAllEventsModel();
        prevFilterModel = new GetAllEventsModel();
        collapsingToolbar = v.findViewById(R.id.collapsing_toolbar);
        noSearch = v.findViewById(R.id.noSearch);
        noResult = v.findViewById(R.id.noResult);
        collapsingToolbar.setTitle("Upcoming\nEvents");
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
        getEventsViewModel = new ViewModelProvider(this).get(GetEventsViewModel.class);
        likeEventViewModel = new ViewModelProvider(this).get(LikeEventViewModel.class);
        observer = objectModel -> {
            if (objectModel.isStatus())
                successful((GetAllEventsResponse) objectModel.getObj());
            else
                failed(objectModel.getMessage());
            closeKeyboard();
        };
        likeObserver = objectModel -> {
            if (!objectModel.isStatus()) {
                failedLike(objectModel.getMessage());
            }
        };
        dislikeObserver = objectModel -> {
            if (!objectModel.isStatus()) {
                failedDisLike(objectModel.getMessage());
            }
        };
        filtersLayout = v.findViewById(R.id.filters_layout);
        filtersMotionLayout = v.findViewById(R.id.filters_motion_layout);
        useFiltersMotionLayout();
        list = new ArrayList<>();
        shimmerRecyclerView = v.findViewById(R.id.events);
        adapter = new Adapter(getContext(), list, this);
        shimmerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        shimmerRecyclerView.setAdapter(adapter);
        refresh = v.findViewById(R.id.refresh);
        refresh.setRefreshStyle(PullRefreshLayout.STYLE_SMARTISAN);
        refresh.setOnRefreshListener(this::fetchData);
        fetchData();
    }

    public void fetchData() {
        noSearch.setVisibility(View.GONE);
        noResult.setVisibility(View.GONE);
        shimmerRecyclerView.showShimmerAdapter();
        refresh.setEnabled(false);
        try {
            prevFilterModel = filterModel.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        getEventsViewModel.getEventsLiveData(filterModel)
                .observe(getViewLifecycleOwner(), observer);
    }

    private void successful(GetAllEventsResponse model) {
        list.clear();
        for (EventDetailsModel eventDetailsModel : model.getEvents()) {
            eventDetailsModel.getEventDetails().setLiked(eventDetailsModel.isLiked());
            eventDetailsModel.getEventDetails().setStarted(eventDetailsModel.isStarted());
            eventDetailsModel.getEventDetails().setEnd(eventDetailsModel.isEnd());
            list.add(eventDetailsModel.getEventDetails());
        }
        adapter.notifyDataSetChanged();
        shimmerRecyclerView.hideShimmerAdapter();
        refresh.setRefreshing(false);
        refresh.setEnabled(true);
        if (list.isEmpty())
            if (isSearch)
                noSearch.setVisibility(View.VISIBLE);
            else
                noResult.setVisibility(View.VISIBLE);
    }

    private void failed(String errorString) {
        shimmerRecyclerView.hideShimmerAdapter();
        refresh.setRefreshing(false);
        refresh.setEnabled(true);
        Toast.makeText(
                getContext(),
                errorString,
                Toast.LENGTH_SHORT).show();
        prevFilterModel = new GetAllEventsModel();
    }

    private void failedLike(String errorString) {
        Toast.makeText(
                getContext(),
                errorString,
                Toast.LENGTH_SHORT).show();
//        list.get(position).isLiked = false;
//        adapter.notifyItemChanged(position);
    }

    private void failedDisLike(String errorString) {
        Toast.makeText(
                getContext(),
                errorString,
                Toast.LENGTH_SHORT).show();
//        list.get(position).isLiked = true;
//        adapter.notifyItemChanged(position);
    }

    @Override
    public void itemClicked(int position) {
        Intent intent = new Intent(getContext(), EventInfoActivity.class);
        intent.putExtra("eventModel", list.get(position));
        intent.putExtra("position", position);
        startActivityForResult(intent, 100);
    }

    @Override
    public void itemLiked(int position) {
        likeEventViewModel.likeEvent(list.get(position).get_id())
                .observe(getViewLifecycleOwner(), likeObserver);
    }

    @Override
    public void itemDisliked(int position) {
        likeEventViewModel.dislikeEvent(list.get(position).get_id())
                .observe(getViewLifecycleOwner(), dislikeObserver);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            if (data != null) {
                int pos = data.getIntExtra("position", 0);
                GetEventsModel model = (GetEventsModel) data.getSerializableExtra("eventModel");
                list.set(pos, model);
                adapter.notifyItemChanged(pos);
            }
        }
    }

    private void useFiltersMotionLayout() {
        filtersLayout.setVisibility(View.VISIBLE);
        filtersMotionLayout.setVisibility(View.GONE);
    }

    private void closeKeyboard() {
        if (getActivity() != null) {
            View view = getActivity().getCurrentFocus();
            if (view != null) {
                InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputManager != null) {
                    inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        }
    }
}