package com.ensightplus.faas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.ensightplus.faas.data.Tools;
import com.ensightplus.faas.model.Booking;

public class ActivityBookingHistoryDetails extends AppCompatActivity {

    public static final String EXTRA_OBJECT = "extra.data.BOOKING_OBJ";

    // give preparation animation activity transition
    public static void navigate(Activity activity, Booking obj) {
        Intent intent = new Intent(activity, ActivityBookingHistoryDetails.class);
        intent.putExtra(EXTRA_OBJECT, obj);
        activity.startActivity(intent);
    }

    private Booking booking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history_details);

        // get extra object
        booking = (Booking) getIntent().getSerializableExtra(EXTRA_OBJECT);

        initToolbar();
        initComponent();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setCompleteSystemBarLight(this);
    }

    private void initComponent() {
        TextView status = (TextView) findViewById(R.id.status);
        TextView payment = (TextView) findViewById(R.id.tv_first_ignition);
        TextView booking_code = (TextView) findViewById(R.id.booking_code);
        TextView ride_class = (TextView) findViewById(R.id.tv_distance);
        TextView pickup = (TextView) findViewById(R.id.pickup);
        TextView destination = (TextView) findViewById(R.id.destination);
        TextView fare = (TextView) findViewById(R.id.fare);

        status.setText(booking.status);
        if (booking.status.equals("FINISHED")) {
            status.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_rectangle_finished));
        } else if (booking.status.equals("CANCELED")) {
            status.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_rectangle_canceled));
        }

        payment.setText(booking.payment);
        booking_code.setText(booking.booking_code);
        ride_class.setText(booking.ride_class);
        pickup.setText(booking.pickup);
        destination.setText(booking.destination);
        fare.setText(booking.fare);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
