package com.example.collegeproject.base.ticket.live;

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
import com.example.collegeproject.models.BookedEventDetailsModel;
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
    private final List<BookedEventDetailsModel> list;
    private final itemClick itemClick;

    public Adapter(Context mContext, List<BookedEventDetailsModel> list, itemClick itemClick) {
        this.mContext = mContext;
        this.list = list;
        this.itemClick = itemClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.ticket_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.viewTickets.setVisibility(View.VISIBLE);
        holder.venue.setText("Venue: " + list.get(position).getEventDetails().getLocation().getCity());
        holder.date.setText(formatDate(list.get(position).getEventDetails().getStartTime()));
        holder.name.setText(list.get(position).getEventDetails().getName());
        holder.price.setText("₹" + list.get(position).getEventDetails().getPrice() + " x " + list.get(position).getTickets().size());
        holder.orgName.setText("by " + list.get(position).getEventDetails().getOrganizer().getOrgName());
        if (list.get(position).getEventDetails().isImageExists()) {
            holder.image.setVisibility(View.VISIBLE);
            Picasso.get().load(ImagePath + list.get(position).getEventDetails().get_id()).placeholder(R.color.shimmerGrey).into(holder.image, new Callback() {
                @Override
                public void onSuccess() {
                    holder.image.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError(Exception e) {
                    holder.image.setVisibility(View.GONE);
                    list.get(position).getEventDetails().setImageExists(false);
                }
            });
        } else {
            holder.image.setVisibility(View.GONE);
        }
        holder.item.setOnClickListener(v -> itemClick.itemClicked(holder.getAbsoluteAdapterPosition()));
        holder.viewTickets.setOnClickListener(v -> itemClick.viewTickets(holder.getAbsoluteAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final LinearLayout item;
        final ImageView image;
        final TextView venue;
        final TextView date;
        final TextView name;
        final TextView orgName;
        final TextView price;
        final Button viewTickets;

        public ViewHolder(View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.item);
            image = itemView.findViewById(R.id.image);
            venue = itemView.findViewById(R.id.venue);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            price = itemView.findViewById(R.id.price);
            orgName = itemView.findViewById(R.id.orgName);
            viewTickets = itemView.findViewById(R.id.viewTickets);
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

        void viewTickets(int position);
    }
}