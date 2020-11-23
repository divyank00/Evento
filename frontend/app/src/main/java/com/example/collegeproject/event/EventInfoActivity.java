package com.example.collegeproject.event;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Path;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.PathInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.collegeproject.R;
import com.example.collegeproject.addEvent.Add2;
import com.example.collegeproject.addEvent.Review;
import com.example.collegeproject.addEvent.map.WorkaroundMapFragment;
import com.example.collegeproject.event.feedback.FeedbackActivity;
import com.example.collegeproject.helper.SharedPref;
import com.example.collegeproject.helper.StaticVariables;
import com.example.collegeproject.models.BookEventModel;
import com.example.collegeproject.models.CategoryModel;
import com.example.collegeproject.models.GetEventsModel;
import com.example.collegeproject.models.TicketModel;
import com.example.collegeproject.models.TypeModel;
import com.example.collegeproject.network.ObjectModel;
import com.example.collegeproject.response.BookEventResponse;
import com.example.collegeproject.roomDB.TaskRunner;
import com.example.collegeproject.roomDB.userCities.DbMethods;
import com.example.collegeproject.sendMail.MailService;
import com.example.collegeproject.sendMail.SendMailTask;
import com.example.collegeproject.ticket.QRGenerator;
import com.example.collegeproject.viewModel.BookEventViewModel;
import com.example.collegeproject.viewModel.LikeEventViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import adil.dev.lib.materialnumberpicker.dialog.NumberPickerDialog;

import static com.example.collegeproject.helper.StaticVariables.ImagePath;

public class EventInfoActivity extends AppCompatActivity implements OnMapReadyCallback {

    private LinearLayout orgMail, orgContact, dateOfEvent, locationOfEvent;
    private TextView ticketSection;
    private TextView titleTV, descTV, orgNameTV, orgMailTV, orgContactTV, startDateOfEventTV, endDateOfEventTV, locationOfEventTV, typeOfEventTV, categoryOfEventTV, availableTickets, priceTicketTV;
    private GetEventsModel eventModel;
    private ArrayList<TicketModel> tickets;
    private ImageView image;
    private BookEventViewModel bookEventViewModel;
    private LikeEventViewModel likeEventViewModel;
    private Observer<ObjectModel> payObserver, likeObserver, dislikeObserver;
    private FloatingActionButton pay, like;
    private final AnimatorSet setPay = new AnimatorSet(), setLike = new AnimatorSet();
    private boolean isLiked = false;
    private int selectedSeats = 0, position;
    private LinearLayout ticketsLL, ratingLL;
    private TextView rating, users;
    private Button viewButton;
    private boolean isOrganiser;
    private GoogleMap mMap;
    private Marker marker;
    private TaskRunner taskRunner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);
        Intent callingIntent = getIntent();
        eventModel = (GetEventsModel) callingIntent.getSerializableExtra("eventModel");
        position = callingIntent.getIntExtra("position", 0);
        tickets = callingIntent.getParcelableArrayListExtra("ticketModel");
        isOrganiser = callingIntent.getBooleanExtra("isOrganiser", false);
        init();
        setData();
    }

    private void init() {
        bookEventViewModel = new ViewModelProvider(this).get(BookEventViewModel.class);
        likeEventViewModel = new ViewModelProvider(this).get(LikeEventViewModel.class);
        taskRunner = new TaskRunner();
        payObserver = objectModel -> {
            if (objectModel.isStatus()) {
                successfulBook((BookEventResponse) objectModel.getObj());
            } else
                failedBook(objectModel.getMessage());
            pay.clearAnimation();
            pay.setEnabled(true);
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
        image = findViewById(R.id.image);
        orgMail = findViewById(R.id.orgMail);
        orgContact = findViewById(R.id.orgContact);
        dateOfEvent = findViewById(R.id.dateOfEvent);
        locationOfEvent = findViewById(R.id.locationOfEvent);
        titleTV = findViewById(R.id.titleTV);
        descTV = findViewById(R.id.descTV);
        orgNameTV = findViewById(R.id.orgNameTV);
        orgMailTV = findViewById(R.id.orgMailTV);
        orgContactTV = findViewById(R.id.orgContactTV);
        startDateOfEventTV = findViewById(R.id.startDateOfEventTV);
        endDateOfEventTV = findViewById(R.id.endDateOfEventTV);
        locationOfEventTV = findViewById(R.id.locationOfEventTV);
        typeOfEventTV = findViewById(R.id.typeOfEventTV);
        categoryOfEventTV = findViewById(R.id.categoryOfEventTV);
        availableTickets = findViewById(R.id.availableTickets);
        priceTicketTV = findViewById(R.id.priceTicketTV);
        ticketSection = findViewById(R.id.ticketSection);
        ticketsLL = findViewById(R.id.ticketsLL);
        ratingLL = findViewById(R.id.ratingLL);
        rating = findViewById(R.id.rating);
        users = findViewById(R.id.users);
        viewButton = findViewById(R.id.viewTickets);
        pay = findViewById(R.id.pay);
        like = findViewById(R.id.like);
        addAnimationToLikeFAB();
        addAnimationToPayFAB();
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
            String myFormat = "EEEE, dd MMMM, yyyy\nhh:mm a";
            SimpleDateFormat sdf1 = new SimpleDateFormat(myFormat, Locale.US);
            return sdf1.format(cal.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void onBackPressed() {
        finishActivity();
    }

    private void finishActivity() {
        Intent intent = new Intent();
        intent.putExtra("eventModel", eventModel);
        intent.putParcelableArrayListExtra("ticketModel", tickets);
        intent.putExtra("position", position);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void updateData() {
        if (eventModel != null) {
            if (!eventModel.isStarted()) {
                ratingLL.setVisibility(View.GONE);
            } else {
                users.setText(eventModel.getTotalRating() + "");
                rating.setText(String.format("%.1f", eventModel.getAvgRating()));
                ratingLL.setVisibility(View.VISIBLE);
            }
            if (tickets != null) {
                priceTicketTV.setText(eventModel.getPrice() + " x " + tickets.size() + " = " + eventModel.getPrice() * tickets.size());
                if (!eventModel.isEnd()) {
                    availableTickets.setText(tickets.size() + "\nAvailable Tickets: " + eventModel.getAvailableSeats());
                    if (eventModel.getAvailableSeats() == 0) {
                        if (eventModel.isStarted()) {
                            pay.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));
                            pay.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.star));
                            pay.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.golden)));
                        } else {
                            pay.setVisibility(View.GONE);
                        }
                    }
                } else {
                    availableTickets.setText(tickets.size() + "");
                }
            } else {
                if (!isOrganiser) {
                    if (eventModel.getAvailableSeats() != -1)
                        availableTickets.setText(eventModel.getAvailableSeats() + "");
                    if (eventModel.getAvailableSeats() == 0) {
                        if (eventModel.isStarted()) {
                            pay.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));
                            pay.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.star));
                            pay.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.golden)));
                        } else {
                            pay.setVisibility(View.GONE);
                        }
                    }
                    if (eventModel.getPrice() != -1)
                        priceTicketTV.setText(eventModel.getPrice() + "");
                    if (eventModel.getAvailableSeats() == 0)
                        pay.setVisibility(View.GONE);
                }
            }
        }
    }

    private void setData() {
        if (eventModel != null) {
            isLiked = eventModel.isLiked();
            like.setImageTintList(ColorStateList.valueOf(isLiked ? getResources().getColor(R.color.red) : getResources().getColor(R.color.white)));
            Picasso.get().load(ImagePath + eventModel.get_id()).into(image);
            if (!eventModel.getName().isEmpty()) {
                titleTV.setText(eventModel.getName());
                setToolbars();
            }
            if (!eventModel.getDescription().isEmpty())
                descTV.setText(eventModel.getDescription());
            if (!eventModel.getOrganizer().getOrgName().isEmpty())
                orgNameTV.setText(eventModel.getOrganizer().getOrgName());
            if (!eventModel.getOrganizer().getOrgEmail().isEmpty())
                orgMailTV.setText(eventModel.getOrganizer().getOrgEmail());
            if (!eventModel.getOrganizer().getOrgContactNo().isEmpty())
                orgContactTV.setText(eventModel.getOrganizer().getOrgContactNo());
            if (!eventModel.getStartTime().isEmpty())
                startDateOfEventTV.setText(formatDate(eventModel.getStartTime()));
            if (!eventModel.getStartTime().isEmpty())
                endDateOfEventTV.setText(formatDate(eventModel.getEndTime()));
            if (!eventModel.getLocation().getCity().isEmpty())
                locationOfEventTV.setText(eventModel.getLocation().getCity());
            if (eventModel.getLocation().getCity() != null && !eventModel.getLocation().getCity().equals("")) {
                WorkaroundMapFragment mapFragment = (WorkaroundMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                if (mapFragment != null) {
                    mapFragment.getMapAsync(this);
                }
            }
            if (eventModel.getEventType() != null && StaticVariables.eventTypes != null) {
                for (TypeModel model : StaticVariables.eventTypes) {
                    if (model.getId().equals(eventModel.getEventType())) {
                        typeOfEventTV.setText(model.getName());
                        break;
                    }
                }
            }
            if (eventModel.getCategories() != null && StaticVariables.eventCategories != null) {
                StringBuilder toPut = new StringBuilder();
                for (CategoryModel model : StaticVariables.eventCategories) {
                    if (eventModel.getCategories().contains(model.getId())) {
                        if (toPut.length() == 0) {
                            toPut.append(model.getName());
                        } else
                            toPut.append(",\n").append(model.getName());
                    }
                }
                categoryOfEventTV.setText(toPut.toString());
            }
            if (!eventModel.isStarted()) {
                ratingLL.setVisibility(View.GONE);
            } else {
                users.setText(eventModel.getTotalRating() + "");
                rating.setText(String.format("%.1f", eventModel.getAvgRating()));
                ratingLL.setVisibility(View.VISIBLE);
            }
            if (tickets != null) {
                ticketSection.setText("Bought Tickets & Pricing");
                priceTicketTV.setText(eventModel.getPrice() + " x " + tickets.size() + " = " + eventModel.getPrice() * tickets.size());
                if (!eventModel.isEnd()) {
                    viewButton.setText("View Tickets");
                    viewButton.setVisibility(View.VISIBLE);
                    viewButton.setOnClickListener(v -> {
                        Intent intent = new Intent(EventInfoActivity.this, QRGenerator.class);
                        intent.putParcelableArrayListExtra("tickets", tickets);
                        intent.putExtra("title", eventModel.getName());
                        startActivity(intent);
                    });
                    availableTickets.setText(tickets.size() + "\nAvailable Tickets: " + eventModel.getAvailableSeats());
                    if (eventModel.getAvailableSeats() == 0) {
                        if (eventModel.isStarted()) {
                            pay.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));
                            pay.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.star));
                            pay.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.golden)));
                        } else {
                            pay.setVisibility(View.GONE);
                        }
                    }
                } else {
                    availableTickets.setText(tickets.size() + "");
                    pay.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));
                    pay.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.star));
                    pay.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.golden)));
                }
            } else {
                if (isOrganiser) {
                    int sold = eventModel.getNoOfSeats() - eventModel.getAvailableSeats();
                    ticketSection.setText("Sold Tickets & Pricing");
                    availableTickets.setText(sold + "\nAvailable Tickets: " + eventModel.getAvailableSeats());
                    priceTicketTV.setText(eventModel.getPrice() + " x " + sold + " = " + eventModel.getPrice() * sold);
                    viewButton.setText("View Participants");
                    viewButton.setVisibility(View.GONE);
                    viewButton.setOnClickListener(v -> {
//                        TODO Add Participant Listener
                    });
                    pay.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));
                    pay.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.star));
                    pay.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.golden)));
                    ratingLL.setVisibility(View.GONE);
                } else {
                    ticketSection.setText("Available Tickets & Pricing");
                    if (eventModel.getAvailableSeats() != -1)
                        availableTickets.setText(eventModel.getAvailableSeats() + "");
                    if (eventModel.getAvailableSeats() == 0) {
                        if (eventModel.isStarted()) {
                            pay.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));
                            pay.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.star));
                            pay.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.golden)));
                        } else {
                            pay.setVisibility(View.GONE);
                        }
                    }
                    if (eventModel.getPrice() != -1)
                        priceTicketTV.setText(eventModel.getPrice() + "");
                    if (eventModel.getAvailableSeats() == 0)
                        pay.setVisibility(View.GONE);
                    if (eventModel.isEnd()) {
                        pay.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));
                        pay.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.star));
                        pay.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.golden)));
                    }
                }
            }
            like.setOnClickListener(v -> {
                if (!isLiked)
                    likeEventViewModel.likeEvent(eventModel.get_id())
                            .observe(EventInfoActivity.this, likeObserver);
                else
                    likeEventViewModel.dislikeEvent(eventModel.get_id())
                            .observe(EventInfoActivity.this, dislikeObserver);
                isLiked = !isLiked;
                eventModel.setLiked(isLiked);
                setLike.start();
            });
            pay.setOnClickListener(v -> {
                setPay.start();
                if (tickets != null) {
                    if (eventModel.isEnd()) {
                        Intent intent = new Intent(EventInfoActivity.this, FeedbackActivity.class);
                        intent.putExtra("eventModel", eventModel);
                        startActivityForResult(intent, 100);
                    } else {
                        if (eventModel.getAvailableSeats() == 0) {
                            if (eventModel.isStarted()) {
                                Intent intent = new Intent(EventInfoActivity.this, FeedbackActivity.class);
                                intent.putExtra("eventModel", eventModel);
                                startActivityForResult(intent, 100);
                            }
                        } else {
                            NumberPickerDialog dialog = new NumberPickerDialog(EventInfoActivity.this,
                                    1,
                                    eventModel.getAvailableSeats(),
                                    this::buyTickets);
                            dialog.show();
                        }
                    }
                } else if (isOrganiser) {
                    Intent intent = new Intent(EventInfoActivity.this, FeedbackActivity.class);
                    intent.putExtra("eventModel", eventModel);
                    startActivityForResult(intent, 100);

                } else {
                    if (eventModel.getAvailableSeats() == 0) {
                        if (eventModel.isStarted()) {
                            Intent intent = new Intent(EventInfoActivity.this, FeedbackActivity.class);
                            intent.putExtra("eventModel", eventModel);
                            startActivityForResult(intent, 100);
                        }
                    } else {
                        NumberPickerDialog dialog = new NumberPickerDialog(EventInfoActivity.this,
                                1,
                                eventModel.getAvailableSeats(),
                                this::buyTickets);
                        dialog.show();
                    }
                }
            });
            ratingLL.setOnClickListener(v -> {
                Intent intent = new Intent(EventInfoActivity.this, FeedbackActivity.class);
                intent.putExtra("eventModel", eventModel);
                startActivityForResult(intent, 100);
            });
            orgMail.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri data = Uri.parse("mailto:" + eventModel.getOrganizer().getOrgEmail());
                intent.setData(data);
                startActivity(intent);
            });
            orgContact.setOnClickListener(v -> {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DIAL); // Action for what intent called for
                intent.setData(Uri.parse("tel: " + eventModel.getOrganizer().getOrgContactNo())); // Data with intent respective action on intent
                startActivity(intent);
            });
            dateOfEvent.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra(CalendarContract.Events.TITLE, eventModel.getName());
                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                        getMilliSeconds(eventModel.getStartTime()));
                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                        getMilliSeconds(eventModel.getEndTime()));
                intent.putExtra(CalendarContract.Events.ALL_DAY, false);
                intent.putExtra(CalendarContract.Events.DESCRIPTION, eventModel.getDescription());
                intent.putExtra(CalendarContract.Events.EVENT_LOCATION, eventModel.getLocation().getCity());
                startActivity(intent);
            });
            locationOfEvent.setOnClickListener(v -> {
                String geoUri = "http://maps.google.com/maps?q=loc:" + eventModel.getLocation().getLatitude() + "," + eventModel.getLocation().getLongitude();
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                startActivity(mapIntent);
            });
        }
    }

    private void setToolbars() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
//        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
//        collapsingToolbar.setTitle(eventModel.getName());
//        collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
//        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
        AppBarLayout appBar = findViewById(R.id.appBar);
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }

                if (scrollRange + verticalOffset == 0) {
                    like.hide();
                } else {
                    like.show();
                }

            }
        });
    }

    private void buyTickets(int value) {
        pay.setEnabled(false);
        pay.startAnimation(AnimationUtils.loadAnimation(EventInfoActivity.this, R.anim.rotate));
        selectedSeats = value;
        bookEventViewModel.bookEvent(new BookEventModel(eventModel.get_id(), value))
                .observe(EventInfoActivity.this, payObserver);
    }

    private long getMilliSeconds(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("IST"));
        Date mDate;
        try {
            mDate = sdf.parse(date);
            if (mDate != null) {
                return mDate.getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void addAnimationToLikeFAB() {
        final float from = 1.0f;
        final float to = 1.3f;

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(like, View.SCALE_X, from, to);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(like, View.SCALE_Y, from, to);
        ObjectAnimator translationZ = ObjectAnimator.ofFloat(like, View.TRANSLATION_Z, from, to);

        AnimatorSet set1 = new AnimatorSet();
        set1.playTogether(scaleX, scaleY, translationZ);
        set1.setDuration(100);
        set1.setInterpolator(new AccelerateInterpolator());
        set1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                like.setImageTintList(ColorStateList.valueOf(isLiked ? getResources().getColor(R.color.red) : getResources().getColor(R.color.white)));
//                like.setBackgroundTintList(ColorStateList.valueOf(isLiked ? getResources().getColor(R.color.grey) : getResources().getColor(R.color.blue)));
            }
        });

        ObjectAnimator scaleXBack = ObjectAnimator.ofFloat(like, View.SCALE_X, to, from);
        ObjectAnimator scaleYBack = ObjectAnimator.ofFloat(like, View.SCALE_Y, to, from);
        ObjectAnimator translationZBack = ObjectAnimator.ofFloat(like, View.TRANSLATION_Z, to, from);

        Path path = new Path();
        path.moveTo(0.0f, 0.0f);
        path.lineTo(0.5f, 1.3f);
        path.lineTo(0.75f, 0.8f);
        path.lineTo(1.0f, 1.0f);
        PathInterpolator pathInterpolator = new PathInterpolator(path);

        AnimatorSet set2 = new AnimatorSet();
        set2.playTogether(scaleXBack, scaleYBack, translationZBack);
        set2.setDuration(300);
        set2.setInterpolator(pathInterpolator);
        setLike.playSequentially(set1, set2);
        setLike.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                like.setClickable(true);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                like.setClickable(false);
            }
        });
    }

    private void addAnimationToPayFAB() {
        final float from = 1.0f;
        final float to = 1.3f;

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(pay, View.SCALE_X, from, to);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(pay, View.SCALE_Y, from, to);
        ObjectAnimator translationZ = ObjectAnimator.ofFloat(pay, View.TRANSLATION_Z, from, to);

        AnimatorSet set1 = new AnimatorSet();
        set1.playTogether(scaleX, scaleY, translationZ);
        set1.setDuration(100);
        set1.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator scaleXBack = ObjectAnimator.ofFloat(pay, View.SCALE_X, to, from);
        ObjectAnimator scaleYBack = ObjectAnimator.ofFloat(pay, View.SCALE_Y, to, from);
        ObjectAnimator translationZBack = ObjectAnimator.ofFloat(pay, View.TRANSLATION_Z, to, from);

        Path path = new Path();
        path.moveTo(0.0f, 0.0f);
        path.lineTo(0.5f, 1.3f);
        path.lineTo(0.75f, 0.8f);
        path.lineTo(1.0f, 1.0f);
        PathInterpolator pathInterpolator = new PathInterpolator(path);

        AnimatorSet set2 = new AnimatorSet();
        set2.playTogether(scaleXBack, scaleYBack, translationZBack);
        set2.setDuration(300);
        set2.setInterpolator(pathInterpolator);
        setPay.playSequentially(set1, set2);
        setPay.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                pay.setClickable(true);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                pay.setClickable(false);
            }
        });
    }

    private void successfulBook(BookEventResponse model) {
        if (tickets != null) {
            tickets.addAll(model.getTickets());
        }
        eventModel.setAvailableSeats(eventModel.getAvailableSeats() - selectedSeats);
        updateData();
        if (selectedSeats == 1)
            Toast.makeText(this.getApplicationContext(), "Ticket Booked!", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this.getApplicationContext(), "Tickets Booked!", Toast.LENGTH_LONG).show();
        if (StaticVariables.user != null) {
            taskRunner.executeAsync(new SendMailTask(StaticVariables.user.getEmail(), eventModel, model.getTickets()), result -> {

            });
        } else {
            taskRunner.executeAsync(new SendMailTask(SharedPref.getMail(EventInfoActivity.this), eventModel, model.getTickets()), result -> {

            });
        }
    }

    private void failedBook(String errorString) {
        Toast.makeText(
                this,
                errorString,
                Toast.LENGTH_SHORT).show();
    }

    private void failedLike(String errorString) {
        Toast.makeText(
                this,
                errorString,
                Toast.LENGTH_SHORT).show();
        isLiked = false;
        eventModel.setLiked(false);
        setLike.start();
    }

    private void failedDisLike(String errorString) {
        Toast.makeText(
                this,
                errorString,
                Toast.LENGTH_SHORT).show();
        isLiked = true;
        eventModel.setLiked(true);
        setLike.start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            if (data != null) {
                GetEventsModel model = (GetEventsModel) data.getSerializableExtra("eventModel");
                if (model != null) {
                    eventModel = model;
                    updateData();
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Respond to the action bar's Up/Home button
        if (item.getItemId() == android.R.id.home) {
            supportFinishAfterTransition();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        NestedScrollView parentSV = findViewById(R.id.parentSV);
        ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                .setListener(new WorkaroundMapFragment.OnTouchListener() {
                    @Override
                    public void onTouch() {
                        parentSV.requestDisallowInterceptTouchEvent(true);
                    }
                });
        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (marker != null)
            marker.remove();
        if (eventModel.getLocation().getCity() != null && !eventModel.getLocation().getCity().isEmpty()) {
            LatLng loc = new LatLng(Double.parseDouble(eventModel.getLocation().getLatitude()), Double.parseDouble(eventModel.getLocation().getLongitude()));
            marker = mMap.addMarker(new MarkerOptions().position(loc).title("Event Location"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 12.0f));
        }
        mMap.setOnMapClickListener(latLng -> {
            String geoUri = "http://maps.google.com/maps?q=loc:" + eventModel.getLocation().getLatitude() + "," + eventModel.getLocation().getLongitude();
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
            startActivity(mapIntent);
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String geoUri = "http://maps.google.com/maps?q=loc:" + eventModel.getLocation().getLatitude() + "," + eventModel.getLocation().getLongitude();
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                startActivity(mapIntent);
                return false;
            }
        });
    }
}