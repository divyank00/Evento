package com.example.collegeproject.ticket;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.collegeproject.R;
import com.example.collegeproject.models.TicketModel;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.List;

class Adapter extends SliderViewAdapter<Adapter.SliderAdapterVH> {

    private Context context;
    private List<TicketModel> list;

    public Adapter(Context context, List<TicketModel> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.qr_card, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(list.get(position).getTicketId(), BarcodeFormat.QR_CODE, 400, 400);
            viewHolder.image.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    static class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView image;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            this.itemView = itemView;
            image = itemView.findViewById(R.id.image);
        }
    }
}