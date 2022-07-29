package com.ensightplus.faas;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ensightplus.faas.data.Constant;
import com.ensightplus.faas.data.Tools;
import com.ensightplus.faas.model.Vehicle;
import com.ensightplus.faas.model.tracking_history.Alarm;
import com.ensightplus.faas.model.tracking_history.Detail;
import com.ensightplus.faas.model.tracking_history.TrackingHistory;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.Callback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;

import static com.ensightplus.faas.data.Tools.convertSeconds;

public class ActivityEvent extends AppCompatActivity {

    private static final String TAG = ActivityEvent.class.getSimpleName();
    private GoogleMap mMap;
    private Polyline polyline;
    private BottomSheetBehavior bottomSheetBehavior;
    private ProgressDialog dialog;
    private FloatingActionButton btnCalendar;
    private DatePickerDialog picker;

    public static final String EXTRA_OBJECT = "VEHICLE_OBJ";
    private TextView tvVin, tvDriver, tvFirstIgnition, tvLastIgnition, tvDistance, tvMaxSpeed, tvAvgSpeed, tvWorkingTime;

    private Vehicle vehicleObj;
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_history);

        Intent getIntent = getIntent();
        //call a TextView object to set the string to
        eventId = getIntent.getStringExtra("eventid");
        Log.d(TAG, "MyFirebaseMessagingService EVENT2: " + eventId);


        initMapFragment();
        initToolbar();
        initComponent();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Tracking History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setCompleteSystemBarLight(this);
    }


    private void initComponent() {
        tvVin = findViewById(R.id.tv_vin);
        tvDriver = findViewById(R.id.tv_driver);
        tvFirstIgnition = findViewById(R.id.tv_first_ignition);
        tvLastIgnition = findViewById(R.id.tv_last_turn_off);
        tvDistance = findViewById(R.id.tv_distance);
        tvMaxSpeed = findViewById(R.id.tv_max_speed);
        tvAvgSpeed = findViewById(R.id.tv_avg_speed);
        tvWorkingTime = findViewById(R.id.tv_working_time);
        btnCalendar = findViewById(R.id.btn_calendar);

        tvDriver.setText(eventId);

        LinearLayout llBottomSheet = findViewById(R.id.bottom_sheet);
        final View top_shadow = findViewById(R.id.top_shadow);

        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setHideable(false);

        // set callback for changes
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    top_shadow.setVisibility(View.GONE);
                } else {
                    top_shadow.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        tvVin.setText(vehicleObj.getVin());
        tvDriver.setText(vehicleObj.getDriver());

        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(ActivityEvent.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                dialog = ProgressDialog.show(ActivityEvent.this, "", "Loading Tracking History...", true);
                                getTrackingHistory(String.valueOf(vehicleObj.getVehicleId()), year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

    }

    private void initMapFragment() {
        Tools.checkInternetConnection(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                configureMap(googleMap);
            }
        });
    }

    public void configureMap(GoogleMap googleMap) {
        mMap = Tools.configBasicGoogleMap(googleMap);

        dialog = ProgressDialog.show(ActivityEvent.this, "", "Loading Tracking History...", true);
        //getTrackingHistory(String.valueOf(vehicleObj.getVehicleId()), getDatetime());
    }

    private void drawPolyLine(LatLng origin, LatLng destination) {

        GeoApiContext context = new GeoApiContext().setApiKey(getString(R.string.google_maps_key));
        context.setConnectTimeout(10, TimeUnit.SECONDS);
        DirectionsApiRequest d = DirectionsApi.newRequest(context);
        d.origin(origin).destination(destination).mode(TravelMode.DRIVING).alternatives(false);
        d.setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                final PolylineOptions polylineOptions = new PolylineOptions().width(8).color(getResources().getColor(R.color.colorAccent)).geodesic(true);
                for (DirectionsRoute d : result.routes) {
                    for (LatLng l : d.overviewPolyline.decodePath()) {
                        polylineOptions.add(new com.google.android.gms.maps.model.LatLng(l.lat, l.lng));
                    }
                }
                // draw polyline
                runOnUiThread(new Runnable() {
                    public void run() {
                        polyline = mMap.addPolyline(polylineOptions);
                    }
                });
            }

            @Override
            public void onFailure(Throwable e) {

            }
        });
    }

    private void displayMarker(LatLng location, boolean isOrigin) {
        // make current location marker
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new com.google.android.gms.maps.model.LatLng(location.lat, location.lng));

        /*
        if (isOrigin) {
            markerOptions.icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_marker_start));
        } else {
            markerOptions.icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_marker_end));
        }
         */
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View marker_view = inflater.inflate(R.layout.maps_marker, null);
        ImageView marker = marker_view.findViewById(R.id.marker);

        marker.setBackgroundResource(isOrigin ? R.drawable.marker_origin : R.drawable.marker_destination);

        if (isOrigin) {
            marker.setImageResource(R.drawable.ic_origin);
        }

        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(Tools.createBitmapFromView(this, marker_view)));
        mMap.addMarker(markerOptions);
    }

    private void displayMarkerAlarm(Alarm alarm) {
        // make current location marker
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new com.google.android.gms.maps.model.LatLng(alarm.getLatitude(), alarm.getLongitude()));
        markerOptions.title(alarm.getEvent());
        markerOptions.snippet(String.format("%.0f mph", alarm.getSpeed()));

        int resource = R.drawable.ic_alarm_hard_acc;
        int color;
        switch (alarm.getEvent()) {
            case "hardAcceleration":
                resource = R.drawable.ic_alarm_hard_acc;
                break;
            case "hardCornering":
                resource = R.drawable.ic_alarm_hard_corner;
                break;
            case "hardBraking":
                resource = R.drawable.ic_alarm_hard_brake;
                break;
            case "idle":
                resource = R.drawable.ic_alarm_idle;
                break;
            case "stopped":
                resource = R.drawable.ic_alarm_stopped;
                break;
            case "outOfRange":
                resource = R.drawable.ic_alarm_out_range;
                break;
            case "overspeed":
                resource = R.drawable.ic_alarm_overspeed;
                break;
            case "deviceOverspeed":
                resource = R.drawable.ic_alarm_device_overspeed;
                break;
            case "laneChange":
                resource = R.drawable.ic_alarm_fast_lane;
                break;

        }
        markerOptions.icon(bitmapDescriptorFromVector(getApplicationContext(), resource));
        mMap.addMarker(markerOptions);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_booking_active_details, menu);
        Tools.changeMenuIconColor(menu, getResources().getColor(R.color.grey_very_hard));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else {
            Tools.showToastMiddle(getApplicationContext(), item.getTitle() + " clicked");
        }
        return super.onOptionsItemSelected(item);
    }

    public void getTrackingHistory(String vehicleId, String date) {

        HashMap<String, String> params = new HashMap<>();
        params.put("vehicleId", vehicleId);
        params.put("reportDate", date);
        JSONObject jsonObject = new JSONObject(params);

        OkGo.<JSONObject>post(Constant.API_URL + Constant.METHOD_TRACKING_HISTORY).upJson(jsonObject).execute(new Callback<JSONObject>() {
            @Override
            public void onStart(Request<JSONObject, ? extends Request> request) {
                /*
                if (!CheckConnection.isNetworkAvailable(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), getString(R.string.no_network), Toast.LENGTH_SHORT).show();
                }
                 */
            }

            @Override
            public void onSuccess(Response<JSONObject> response) {
                try {
                    if (response.body() != null) {
                        mMap.clear();
                        Gson gson = new Gson();
                        TrackingHistory trackingHistory = gson.fromJson(response.body().toString(), TrackingHistory.class);
                        if (trackingHistory.getDeviceId() > 0){
                            List<Detail> details = trackingHistory.getDetails();
                            List<Alarm> alarms = trackingHistory.getAlarms();

                            tvFirstIgnition.setText(trackingHistory.getFirstIgnition());
                            tvLastIgnition.setText(trackingHistory.getLastIgnition());
                            tvDistance.setText(String.format("%.0f mi", trackingHistory.getDistance()));
                            tvMaxSpeed.setText(String.format("%.0f mph", trackingHistory.getMaxSpeed()));
                            tvAvgSpeed.setText( String.format("%.0f mph", trackingHistory.getAvgSpeed()));
                            tvWorkingTime.setText(convertSeconds((int) trackingHistory.workingTime));

                            // Create Polyline
                            PolylineOptions polylineOptions = new PolylineOptions().width(8).color(getResources().getColor(R.color.colorPrimary)).geodesic(true);

                            for (int i = 0; i < details.size(); i++) {

                                if (i == 0) {
                                    displayMarker(new LatLng(details.get(i).getLatitude(), details.get(i).getLongitude()), true);
                                } else if (i == details.size() - 1) {
                                    displayMarker(new LatLng(details.get(i).getLatitude(), details.get(i).getLongitude()), false);
                                }

                                String status = details.get(i).getStatus();
                                int resource = R.drawable.ic_marker_move;

                                switch (status) {
                                    case "offline":
                                        resource = R.drawable.ic_marker_offline;
                                        break;
                                    case "stop":
                                        resource = R.drawable.ic_marker_stop;
                                        break;
                                    case "idle":
                                        resource = R.drawable.ic_marker_idle;
                                        break;
                                }
                                com.google.android.gms.maps.model.LatLng point = new com.google.android.gms.maps.model.LatLng(details.get(i).getLatitude(), details.get(i).getLongitude());

                                polylineOptions.add(point);
                            }
                            Polyline polyline = mMap.addPolyline(polylineOptions);
                            moveToBounds(polyline);

                            // Show Alarms
                            for (int i = 0; i < alarms.size(); i++) {
                                displayMarkerAlarm(alarms.get(i));
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "No history, try a different date.", Toast.LENGTH_SHORT).show();
                        }

                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCacheSuccess(Response<JSONObject> response) {
                Log.e("TrackingHistory", "cache sucess");
            }


            @Override
            public void onError(Response<JSONObject> response) {
                Log.e("TrackingHistory", " error");
            }

            @Override
            public void onFinish() {
                if (dialog.isShowing())
                    dialog.dismiss();
            }

            @Override
            public void uploadProgress(Progress progress) {
            }

            @Override
            public void downloadProgress(Progress progress) {
            }

            @Override
            public JSONObject convertResponse(okhttp3.Response response) {
                try {
                    if (response.isSuccessful()) {
                        ResponseBody responseBody = response.body();
                        String res = responseBody.string();
                        JSONObject jsonObject = new JSONObject(res);
                        return jsonObject;
                    }
                } catch (Exception e) {
                    Log.e("catch", e.toString());
                }

                return null;
            }
        });
    }

    private void moveToBounds(Polyline p){

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(int i = 0; i < p.getPoints().size();i++){
            builder.include(p.getPoints().get(i));
        }

        LatLngBounds bounds = builder.build();
        int padding = 40; // offset from edges of the map in pixels

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(cu);
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private Bitmap changeBitmapColor(Bitmap sourceBitmap, int color) {

        Bitmap resultBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0,
                sourceBitmap.getWidth() - 1, sourceBitmap.getHeight() - 1);
        Paint p = new Paint();
        ColorFilter filter = new LightingColorFilter(color, 1);
        p.setColorFilter(filter);
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(resultBitmap, 0, 0, p);

        return resultBitmap;
    }

}
