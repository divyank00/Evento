package com.example.collegeproject.addEvent.eventType;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collegeproject.R;
import com.example.collegeproject.models.TypeModel;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private final Context mContext;
    private final ArrayList<TypeModel> list;
    private final TypeModel selectedItem;
    private final itemClick itemClick;

    public Adapter(Context mContext, ArrayList<TypeModel> list, TypeModel selectedItem, itemClick itemClick) {
        this.mContext = mContext;
        this.list = list;
        this.selectedItem = selectedItem;
        this.itemClick = itemClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.event_info_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.name.setText(list.get(holder.getAbsoluteAdapterPosition()).getName());
        if (selectedItem!=null && list.get(position).getId().equals(selectedItem.getId())) {
            holder.tick.setVisibility(View.VISIBLE);
        } else {
            holder.tick.setVisibility(View.GONE);
        }
        holder.item.setOnClickListener(v -> itemClick.itemClicked(holder.getAbsoluteAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final ImageView tick;
        final LinearLayout item;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            tick = itemView.findViewById(R.id.tick);
            item = itemView.findViewById(R.id.item);
        }
    }

    interface itemClick {
        void itemClicked(int position);
    }
}