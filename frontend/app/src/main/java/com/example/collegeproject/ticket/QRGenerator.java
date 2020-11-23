package com.example.collegeproject.ticket;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.collegeproject.R;
import com.example.collegeproject.models.TicketModel;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.List;

public class QRGenerator extends AppCompatActivity {

    List<TicketModel> tickets;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r_generator);
        tickets = getIntent().getParcelableArrayListExtra("tickets");
        title = getIntent().getStringExtra("title");
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        SliderView sliderView = findViewById(R.id.imageSlider);
        sliderView.setSliderAdapter(new Adapter(QRGenerator.this, tickets));

        sliderView.setIndicatorAnimation(IndicatorAnimationType.THIN_WORM); //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.CUBEINDEPTHTRANSFORMATION);
//        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setOnIndicatorClickListener(sliderView::setCurrentPagePosition);
        sliderView.setIndicatorSelectedColor(Color.BLACK);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
//        sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
//        sliderView.startAutoCycle();
//        sliderView.setAutoCycle(false);
        if(tickets.size()==1) {
            sliderView.setInfiniteAdapterEnabled(false);
        }
    }
}