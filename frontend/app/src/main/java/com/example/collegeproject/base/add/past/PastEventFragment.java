package com.example.collegeproject.base.add.past;

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
import com.example.collegeproject.models.GetEventsModel;
import com.example.collegeproject.models.RegisteredEventDetailsModel;
import com.example.collegeproject.network.ObjectModel;
import com.example.collegeproject.response.GetAllRegisteredEventsResponse;
import com.example.collegeproject.viewModel.GetRegisteredEventsViewModel;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PastEventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PastEventFragment extends Fragment implements Adapter.itemClick {

    private ShimmerRecyclerView shimmerRecyclerView;
    private Adapter adapter;
    private List<GetEventsModel> list;
    private GetRegisteredEventsViewModel getRegisteredEventsViewModel;
    private Observer<ObjectModel> observer;
    private PullRefreshLayout refresh;
    private LinearLayout noResult;

    public PastEventFragment() {
        // Required empty public constructor
    }

    public static PastEventFragment newInstance() {
        return new PastEventFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_past_event, container, false);
        getRegisteredEventsViewModel = new ViewModelProvider(this).get(GetRegisteredEventsViewModel.class);
        list = new ArrayList<>();
        observer = objectModel -> {
            if (objectModel.isStatus())
                successful((GetAllRegisteredEventsResponse) objectModel.getObj());
            else
                failed(objectModel.getMessage());
        };
        noResult = v.findViewById(R.id.noResult);
        shimmerRecyclerView = v.findViewById(R.id.pastEvents);
        shimmerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new Adapter(getContext(), list, this);
        shimmerRecyclerView.setAdapter(adapter);
        refresh = v.findViewById(R.id.refresh);
        refresh.setRefreshStyle(PullRefreshLayout.STYLE_SMARTISAN);
        refresh.setOnRefreshListener(this::fetchData);
        fetchData();
        return v;
    }

    private void fetchData() {
        noResult.setVisibility(View.GONE);
        shimmerRecyclerView.showShimmerAdapter();
        refresh.setEnabled(false);
        getRegisteredEventsViewModel.getRegisteredPastData()
                .observe(getViewLifecycleOwner(), observer);
    }

    private void successful(GetAllRegisteredEventsResponse model) {
        list.clear();
        for(RegisteredEventDetailsModel registeredEventDetailsModel : model.getRegisteredEvents()){
            registeredEventDetailsModel.getEventDetails().setStarted(registeredEventDetailsModel.isStarted());
            registeredEventDetailsModel.getEventDetails().setEnd(registeredEventDetailsModel.isEnd());
            registeredEventDetailsModel.getEventDetails().setLiked(registeredEventDetailsModel.isLiked());
            list.add(registeredEventDetailsModel.getEventDetails());
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

    @Override
    public void itemClicked(int position) {
        Intent intent = new Intent(getContext(), EventInfoActivity.class);
        intent.putExtra("eventModel", list.get(position));
        intent.putExtra("position", position);
        intent.putExtra("isOrganiser", true);
        startActivityForResult(intent, 100);
    }

    @Override
    public void viewUsers(int position) {

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