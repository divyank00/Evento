package com.example.collegeproject.base.ticket.live;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.example.collegeproject.R;
import com.example.collegeproject.event.EventInfoActivity;
import com.example.collegeproject.models.BookedEventDetailsModel;
import com.example.collegeproject.models.GetEventsModel;
import com.example.collegeproject.models.TicketModel;
import com.example.collegeproject.network.ObjectModel;
import com.example.collegeproject.response.GetAllBookedEventsResponse;
import com.example.collegeproject.ticket.QRGenerator;
import com.example.collegeproject.viewModel.GetBookedEventsViewModel;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LiveTicketFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LiveTicketFragment extends Fragment implements Adapter.itemClick {

    private ShimmerRecyclerView shimmerRecyclerView;
    private Adapter adapter;
    private List<BookedEventDetailsModel> list;
    private GetBookedEventsViewModel getBookedEventsViewModel;
    private Observer<ObjectModel> observer;
    private PullRefreshLayout refresh;
    private LinearLayout noResult;

    public LiveTicketFragment() {
        // Required empty public constructor
    }

    public static LiveTicketFragment newInstance() {
        return new LiveTicketFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_live_ticket, container, false);
        getBookedEventsViewModel = new ViewModelProvider(this).get(GetBookedEventsViewModel.class);
        list = new ArrayList<>();
        observer = objectModel -> {
            if (objectModel.isStatus())
                successful((GetAllBookedEventsResponse) objectModel.getObj());
            else
                failed(objectModel.getMessage());
        };
        noResult = v.findViewById(R.id.noResult);
        shimmerRecyclerView = v.findViewById(R.id.liveTickets);
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
        getBookedEventsViewModel.getBookedLiveData()
                .observe(getViewLifecycleOwner(), observer);
    }

    private void successful(GetAllBookedEventsResponse model) {
        list.clear();
        for (BookedEventDetailsModel bookedEventDetailsModel : model.getBookedEvents()) {
            bookedEventDetailsModel.getEventDetails().setEnd(bookedEventDetailsModel.isEnd());
            bookedEventDetailsModel.getEventDetails().setStarted(bookedEventDetailsModel.isStarted());
            bookedEventDetailsModel.getEventDetails().setLiked(bookedEventDetailsModel.isLiked());
            list.add(bookedEventDetailsModel);
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
        intent.putExtra("eventModel", list.get(position).getEventDetails());
        intent.putParcelableArrayListExtra("ticketModel", list.get(position).getTickets());
        intent.putExtra("position", position);
        startActivityForResult(intent, 100);
    }

    @Override
    public void viewTickets(int position) {
        Intent intent = new Intent(getContext(), QRGenerator.class);
        intent.putParcelableArrayListExtra("tickets", list.get(position).getTickets());
        intent.putExtra("title", list.get(position).getEventDetails().getName());
        startActivity(intent);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            if (data != null) {
                int pos = data.getIntExtra("position", 0);
                GetEventsModel model = (GetEventsModel) data.getSerializableExtra("eventModel");
                ArrayList<TicketModel> tickets = data.getParcelableArrayListExtra("ticketModel");
                list.get(pos).setEventDetails(model);
                list.get(pos).setTickets(tickets);
                adapter.notifyItemChanged(pos);
            }
        }
    }
}