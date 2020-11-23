package com.example.collegeproject.base.add.live;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collegeproject.R;
import com.example.collegeproject.models.GetEventsModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import static com.example.collegeproject.helper.StaticVariables.ImagePath;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private final Context mContext;
    private final List<GetEventsModel> list;
    private final itemClick itemClick;

    public Adapter(Context mContext, List<GetEventsModel> list, itemClick itemClick) {
        this.mContext = mContext;
        this.list = list;
        this.itemClick = itemClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.registered_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.attr.setVisibility(View.VISIBLE);
        holder.venue.setText(list.get(position).getLocation().getCity() + " | " + formatDate(list.get(position).getStartTime()));
        holder.name.setText(list.get(position).getName());
        holder.users.setText(list.get(position).getNoOfSeats() - list.get(position).getAvailableSeats() + "");
        holder.tickets.setText(list.get(position).getAvailableSeats() + "");
        if(list.get(position).isImageExists()){
            holder.image.setVisibility(View.VISIBLE);
            Picasso.get().load(ImagePath + list.get(position).get_id()).placeholder(R.color.shimmerGrey).into(holder.image, new Callback() {
                @Override
                public void onSuccess() {
                    holder.image.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError(Exception e) {
                    holder.image.setVisibility(View.GONE);
                    list.get(position).setImageExists(false);
                }
            });
        }else{
            holder.image.setVisibility(View.GONE);
        }
        holder.item.setOnClickListener(v -> itemClick.itemClicked(holder.getAbsoluteAdapterPosition()));
        holder.viewUsers.setOnClickListener(v -> itemClick.viewUsers(holder.getAbsoluteAdapterPosition()));
//        if(list.get(position).getNoOfSeats() > list.get(position).getAvailableSeats()) {
//            holder.viewUsers.setVisibility(View.VISIBLE);
//        }else{
        holder.viewUsers.setVisibility(View.GONE);
//        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final LinearLayout item;
        final ImageView image;
        final LinearLayout attr;
        final TextView venue;
        final TextView name;
        final TextView tickets;
        final TextView users;
        final Button viewUsers;

        public ViewHolder(View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.item);
            image = itemView.findViewById(R.id.image);
            venue = itemView.findViewById(R.id.venue);
            name = itemView.findViewById(R.id.name);
            tickets = itemView.findViewById(R.id.tickets);
            users = itemView.findViewById(R.id.users);
            viewUsers = itemView.findViewById(R.id.viewUsers);
            attr = itemView.findViewById(R.id.attr);
        }
    }

    private String formatDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("IST"));
        try {
            Date mDate = sdf.parse(date);
            long timeInMilliseconds = Objects.requireNonNull(mDate).getTime();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(timeInMilliseconds);
            String myFormat = "MMMM dd, yyyy ‣ hh:mm a";
            SimpleDateFormat sdf1 = new SimpleDateFormat(myFormat, Locale.US);
            return sdf1.format(cal.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    interface itemClick {
        void itemClicked(int position);

        void viewUsers(int position);
    }
}