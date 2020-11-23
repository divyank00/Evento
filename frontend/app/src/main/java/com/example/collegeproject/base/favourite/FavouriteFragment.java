package com.example.collegeproject.base.favourite;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.collegeproject.event.EventInfoActivity;
import com.example.collegeproject.models.EventDetailsModel;
import com.example.collegeproject.models.GetEventsModel;
import com.example.collegeproject.network.ObjectModel;
import com.example.collegeproject.response.GetFavouriteEventsResponse;
import com.example.collegeproject.viewModel.GetFavouriteEventsViewModel;
import com.example.collegeproject.viewModel.LikeEventViewModel;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class FavouriteFragment extends Fragment implements Adapter.itemClick {

    private GetFavouriteEventsViewModel getFavouriteEventsViewModel;
    private ShimmerRecyclerView shimmerRecyclerView;
    private Adapter adapter;
    private PullRefreshLayout refresh;
    private List<GetEventsModel> list;
    private Observer<ObjectModel> observer, likeObserver, dislikeObserver;
    private CollapsingToolbarLayout collapsingToolbar;
    private LikeEventViewModel likeEventViewModel;
    private LinearLayout noResult;

    public FavouriteFragment() {
        // Required empty public constructor
    }

    public static FavouriteFragment newInstance() {
        return new FavouriteFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_favourite, container, false);
        setView(v);
        return v;
    }

    private void setView(View v) {
        collapsingToolbar = v.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Favourites");
        noResult = v.findViewById(R.id.noResult);
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
        getFavouriteEventsViewModel = new ViewModelProvider(this).get(GetFavouriteEventsViewModel.class);
        likeEventViewModel = new ViewModelProvider(this).get(LikeEventViewModel.class);
        observer = objectModel -> {
            if (objectModel.isStatus())
                successful((GetFavouriteEventsResponse) objectModel.getObj());
            else
                failed(objectModel.getMessage());
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
        list = new ArrayList<>();
        shimmerRecyclerView = v.findViewById(R.id.favourites);
        adapter = new Adapter(getContext(), list, this);
        shimmerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        shimmerRecyclerView.setAdapter(adapter);
        refresh = v.findViewById(R.id.refresh);
        refresh.setRefreshStyle(PullRefreshLayout.STYLE_SMARTISAN);
        refresh.setOnRefreshListener(this::fetchData);
        fetchData();
    }

    private void fetchData() {
        noResult.setVisibility(View.GONE);
        shimmerRecyclerView.showShimmerAdapter();
        refresh.setEnabled(false);
        getFavouriteEventsViewModel.getFavouriteEventsLiveData()
                .observe(getViewLifecycleOwner(), observer);
    }

    private void successful(GetFavouriteEventsResponse model) {
        list.clear();
        for(EventDetailsModel likedModel : model.getLikedEvents()){
            likedModel.getEventDetails().setStarted(likedModel.isStarted());
            likedModel.getEventDetails().setEnd(likedModel.isEnd());
            likedModel.getEventDetails().setLiked(true);
            list.add(likedModel.getEventDetails());
        }
        adapter.notifyDataSetChanged();
        shimmerRecyclerView.hideShimmerAdapter();
        refresh.setRefreshing(false);
        refresh.setEnabled(true);
        if(list.isEmpty()){
            noResult.setVisibility(View.VISIBLE);
        }
    }

    private void failed(String errorString) {
        shimmerRecyclerView.hideShimmerAdapter();
        refresh.setRefreshing(false);
        refresh.setEnabled(true);
        Toast.makeText(
                getContext(),
                errorString,
                Toast.LENGTH_SHORT).show();
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
}