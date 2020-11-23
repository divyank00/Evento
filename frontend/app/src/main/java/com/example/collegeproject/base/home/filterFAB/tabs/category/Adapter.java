package com.example.collegeproject.base.home.filterFAB.tabs.category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collegeproject.R;
import com.example.collegeproject.models.CategoryModel;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private final Context mContext;
    private ArrayList<CategoryModel> list;
    private final ArrayList<String> selectedIds;
    private final itemClick itemClick;

    public Adapter(Context mContext, ArrayList<CategoryModel> list, ArrayList<String> selectedIds, itemClick itemClick) {
        this.mContext = mContext;
        this.list = list;
        this.selectedIds = selectedIds;
        this.itemClick = itemClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.event_category_filter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.name.setText(list.get(holder.getAbsoluteAdapterPosition()).getName());
        if (selectedIds.contains(list.get(position).getId())) {
            holder.item.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_pill_selected));
        } else {
            holder.item.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_pill));
        }
        holder.item.setOnClickListener(v -> itemClick.itemClicked(list.get(holder.getAbsoluteAdapterPosition())));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void filter(ArrayList<CategoryModel> filterCategories) {
        this.list = filterCategories;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final LinearLayout item;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            item = itemView.findViewById(R.id.item);
        }
    }

    public interface itemClick {
        void itemClicked(CategoryModel model);
    }
}