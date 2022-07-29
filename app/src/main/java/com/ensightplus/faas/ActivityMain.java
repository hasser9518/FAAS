package com.ensightplus.faas;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;

import com.ensightplus.faas.model.groups.Group;
import com.ensightplus.faas.model.groups.Organization;
import com.ensightplus.faas.model.groups.Root;
import com.ensightplus.faas.treeview.CustomExpandableListview;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ensightplus.faas.data.App;
import com.ensightplus.faas.model.Vehicle;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.Callback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.squareup.picasso.Picasso;
import com.ensightplus.faas.adapter.RideClassListAdapter;
import com.ensightplus.faas.data.Constant;
import com.ensightplus.faas.data.Tools;
import com.ensightplus.faas.fragment.FragmentDialogPayment;
import com.ensightplus.faas.fragment.FragmentDialogPromo;
import com.ensightplus.faas.model.Payment;
import com.ensightplus.faas.model.Promo;
import com.ensightplus.faas.model.RideClass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;

import static com.ensightplus.faas.data.Tools.getDatetime;

public class ActivityMain extends AppCompatActivity implements GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    private static final String TAG = ActivityMain.class.getSimpleName();
    private Toolbar toolbar;
    private ActionBar actionBar;
    private NavigationView navigationView;
    private TextView tv_odometer, tv_fuel, tv_speed, tv_vin, tv_driver;
    private EditText et_pickup, et_destination;
    private LinearLayout lyt_bottom;
    private ImageView img_status;

    private GoogleMap mMap;
    private Polyline polyline;

    private FragmentTransaction transaction;
    private FragmentManager fragmentManager;
    private Promo promo;
    private Payment payment;

    private ProgressDialog dialog;
    private Handler handler = new Handler();

    // Declare a variable for the cluster manager.
    //private List<Vehicle> vehicleList;
    private Vehicle selectedVehicle;
    private List<Vehicle> vehiclesList = new ArrayList<>();
    private List<Marker> markers = new ArrayList<>();
    private List<Marker> markersFiltered = new ArrayList<>();
    private LatLngBounds.Builder latLngBoundBuilder;
    private LatLngBounds latLngBounds;
    private boolean isFiltered = false;

    private List<String> locations;
    private List<Group> groups;
    private List<Group> vehicleGroups;
    private List<Organization> organizations;

    private Animation animShow, animHide;

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        App.getInstance().initOkGo();

        Intent getIntent = getIntent();
        //call a TextView object to set the string to
        String eventId = getIntent.getStringExtra("eventid");
        Log.d(TAG, "MyFirebaseMessagingService EVENT2: " + eventId);

        if (eventId != null && !eventId.isEmpty()) {
            Intent i = new Intent(this, ActivityEvent.class);
            Bundle bundle = new Bundle();
            bundle.putString("eventid", eventId);
            i.putExtras(bundle);
            startActivity(i);
        }

        initToolbar();
        initDrawerMenu();
        initMapFragment();
        initComponent();
        initAnimation();

        // Get groups and vehiclegroups for Locations
        getGroups();
    }

    private void initExpandableListView() {
        expandableListView = findViewById(R.id.expandableListView);
        expandableListTitle = new ArrayList<>(expandableListDetail.keySet());

        // Sorting arraylist in alphabetical order (case insensitive)
        Collections.sort(expandableListTitle, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });
        expandableListAdapter = new CustomExpandableListview(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                showLocations(expandableListDetail.get(expandableListTitle.get(groupPosition)).get(childPosition));

                Tools.showToastMiddle(getApplicationContext(), "Selected " + expandableListTitle.get(groupPosition)
                        + " -> "
                        + expandableListDetail.get(expandableListTitle.get(groupPosition)).get(childPosition));

                return false;
            }
        });
    }

    private void initAnimation() {
        animShow = AnimationUtils.loadAnimation(this, R.anim.anim_fragment_in);
        animHide = AnimationUtils.loadAnimation(this, R.anim.anim_fragment_out);
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 1);
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.grey_very_hard), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle(null);
        actionBar.setDisplayHomeAsUpEnabled(true);
        Tools.setCompleteSystemBarLight(this);
    }

    private void initDrawerMenu() {
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                onNavigationItemClick(menuItem);
                drawer.closeDrawers();
                return true;
            }
        });
    }

    private void onNavigationItemClick(MenuItem menuItem) {
        int id = menuItem.getItemId();
        Bundle bundle = new Bundle();
        switch (id) {
            case R.id.nav_booking:
                //startActivity(new Intent(this, ActivityBooking.class));
                break;
            case R.id.nav_notification:
                startActivity(new Intent(this, ActivityAlerts.class));
                break;
            case R.id.nav_assets:
                Intent mIntent = new Intent(this, ActivityAssets.class);
                mIntent.putExtra(getString(R.string.title_assets), (Serializable) vehiclesList);
                startActivity(mIntent);
                break;
            case R.id.nav_coupon:
                //showDialogPromo();
                break;
            case R.id.nav_help:
                //startActivity(new Intent(this, ActivityHelpCenter.class));
                break;
            case R.id.nav_settings:
                //startActivity(new Intent(this, ActivityHelpCenter.class));
                //startActivity(new Intent(this, ActivitySettings.class));
                break;
            case R.id.nav_about:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("About");
                builder.setMessage(getString(R.string.about_text));
                builder.setNeutralButton("OK", null);
                builder.show();
                break;
            case R.id.nav_logout:
                AlertDialog.Builder builderLogout = new AlertDialog.Builder(this);
                builderLogout.setTitle("Logout");
                builderLogout.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        App.getInstance().clearAll();
                    }
                });
                builderLogout.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                builderLogout.setMessage(getString(R.string.logout_text));
                builderLogout.show();
                break;
        }
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
        CameraUpdate center = CameraUpdateFactory.newLatLngZoom(new com.google.android.gms.maps.model.LatLng(37.2582097, -104.6548095), 3);
        mMap.moveCamera(center);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);
        latLngBoundBuilder = new LatLngBounds.Builder();

        dialog = ProgressDialog.show(ActivityMain.this, "", "Getting vehicles...", true);
        handler.postDelayed(runnable, 1);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // Get all vehicles
            getVehicles();
            handler.postDelayed(this, 30 * 1000);
        }
    };

    private void initComponent() {
        tv_odometer = findViewById(R.id.tv_odometer);
        tv_fuel = findViewById(R.id.tv_fuel);
        tv_speed = findViewById(R.id.tv_speed);
        tv_vin = findViewById(R.id.tv_vin);
        tv_driver = findViewById(R.id.tv_driver);
        img_status = findViewById(R.id.img_status);

        et_pickup = findViewById(R.id.et_pickup);
        lyt_bottom = findViewById(R.id.lyt_bottom);

        findViewById(R.id.lyt_ride).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showDialogRideClass();
            }
        });

        findViewById(R.id.lyt_note).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showDialogNote();
            }
        });

        findViewById(R.id.lyt_promo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showDialogPromo();
            }
        });

        findViewById(R.id.lyt_payment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showDialogPayment();
            }
        });

        findViewById(R.id.lyt_tracking_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Date:" + getDatetime());

                ActivityTrackingHistory.navigate(ActivityMain.this, selectedVehicle);

                //getTrackingHistory(String.valueOf(selectedVehicle.getVehicleId()), getDatetime());
            }
        });

        // Setting listener to handle callbacks
        et_pickup.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                latLngBoundBuilder = new LatLngBounds.Builder();
                markersFiltered = new ArrayList<>();

                if (s.length() != 0) {
                    showMarker(s.toString());
                    isFiltered = true;
                } else if (s.length() == 0) {
                    showMarker("");
                    isFiltered = false;
                } else {
                    View view = ActivityMain.this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
                // Update bounds
                if (latLngBoundBuilder != null && markersFiltered.size() > 0 && selectedVehicle == null) {
                    updateMapBounds();
                } else if (selectedVehicle != null) {
                    updateCameraMarker(new LatLng(selectedVehicle.getLatitude(), selectedVehicle.getLongitude()));
                }
            }
        });
    }

    private void updateMapBounds() {
        latLngBounds = latLngBoundBuilder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(latLngBounds, width, height, 60);
        mMap.animateCamera(cu);
    }

    private void updateCameraMarker(LatLng latLng) {
        CameraUpdate center = CameraUpdateFactory.newLatLngZoom(latLng, 17);
        mMap.animateCamera(center);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        Tools.changeMenuIconColor(menu, getResources().getColor(R.color.grey_very_hard));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else {
            toggleLocations();
            //Tools.showToastMiddle(getApplicationContext(), item.getTitle() + " clicked");
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleLocations() {
        isFiltered = true;
        if (expandableListView.getVisibility() == View.VISIBLE) {
            expandableListView.setVisibility(View.GONE);
            expandableListView.startAnimation(animHide);
        } else {
            expandableListView.setVisibility(View.VISIBLE);
            expandableListView.startAnimation(animShow);
        }
    }

    private void showDialogRideClass() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_ride_class);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        //set data and list adapter
        RideClassListAdapter mAdapter = new RideClassListAdapter(this, Constant.getRideClassData(this));
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new RideClassListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RideClass obj, int position) {
                changeRideClass(obj);
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void changeRideClass(RideClass obj) {
        Picasso.with(this).load(obj.image).into(((ImageView) findViewById(R.id.img_status)));
        ((TextView) findViewById(R.id.class_name)).setText(obj.class_name);
        ((TextView) findViewById(R.id.price)).setText(obj.price);
        ((TextView) findViewById(R.id.pax)).setText(obj.pax);
        ((TextView) findViewById(R.id.duration)).setText(obj.duration);
    }

    private void showDialogNote() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_note);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final TextInputEditText et_note = dialog.findViewById(R.id.et_note);
        String home_note = tv_odometer.getText().toString();
        if (!home_note.equals("None")) et_note.setText(home_note);

        dialog.findViewById(R.id.lyt_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_note.setText("");
            }
        });

        dialog.findViewById(R.id.bt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.bt_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String note = et_note.getText().toString().trim();
                if (note.equals("")) {
                    tv_odometer.setText("None");
                } else {
                    tv_odometer.setText(note);
                }
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void showDialogPromo() {
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        Fragment previous = getSupportFragmentManager().findFragmentByTag(FragmentDialogPromo.class.getName());
        if (previous != null) transaction.remove(previous);
        transaction.addToBackStack(null);

        fragmentManager = getSupportFragmentManager();
        final FragmentDialogPromo fragment = new FragmentDialogPromo();
        fragment.setRequestCode(1000);
        fragment.setPromo(promo);

        fragment.setOnCallbackResult(new FragmentDialogPromo.CallbackResult() {
            @Override
            public void sendResult(int requestCode, Promo p) {
                if (requestCode != 1000) return;
                Tools.showToastMiddle(getApplicationContext(), "Promo Applied");
                tv_fuel.setText(p.code);
                tv_fuel.setTextColor(getResources().getColor(R.color.price_color));
                promo = p;

            }

            @Override
            public void removePromo(int requestCode) {
                if (requestCode != 1000) return;
                Tools.showToastMiddle(getApplicationContext(), "Promo Removed");
                tv_fuel.setText("None");
                tv_fuel.setTextColor(getResources().getColor(R.color.grey_text));
                promo = null;
            }
        });


        fragment.show(transaction, FragmentDialogPromo.class.getName());
    }

    private void showDialogPayment() {
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        Fragment previous = getSupportFragmentManager().findFragmentByTag(FragmentDialogPayment.class.getName());
        if (previous != null) transaction.remove(previous);
        transaction.addToBackStack(null);

        final FragmentDialogPayment fragment = new FragmentDialogPayment();
        fragment.setRequestCode(2000);
        fragment.setPayment(payment);

        fragment.setOnCallbackResult(new FragmentDialogPayment.CallbackResult() {
            @Override
            public void sendResult(int requestCode, Payment p) {
                if (requestCode != 2000) return;
                payment = p;
                tv_speed.setText(p.type);
            }

        });

        fragment.show(transaction, FragmentDialogPayment.class.getName());
    }

    @Override
    public void onMapClick(LatLng latLng) {
        lyt_bottom.setVisibility(View.GONE);
        isFiltered = false;
    }

    @Override
    public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {
        // Calculate required horizontal shift for current screen density
        final int dX = getResources().getDimensionPixelSize(R.dimen.map_dx);
        // Calculate required vertical shift for current screen density
        final int dY = getResources().getDimensionPixelSize(R.dimen.map_dy);
        final Projection projection = mMap.getProjection();
        final Point markerPoint = projection.toScreenLocation(
                marker.getPosition()
        );
        // Shift the point we will use to center the map
        markerPoint.offset(dX, dY);
        // Buttery smooth camera swoop :)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude), 17));
        // Show the info window (as the overloaded method would)
        marker.showInfoWindow();

        isFiltered = true;
        getVehicleInfo(marker.getTag());

        return true; // Consume the event since it was dealt with
    }

    private void getVehicleInfo(Object tag) {
        // Set all params
        selectedVehicle = (Vehicle) tag;
        tv_vin.setText(selectedVehicle.vin);
        tv_driver.setText(selectedVehicle.driver);
        tv_odometer.setText(String.format("%smi", (int) selectedVehicle.odometer));
        tv_fuel.setText(String.format("%s%%", (int) selectedVehicle.fuelLevel));

        int speed = (int) selectedVehicle.speed;
        switch (selectedVehicle.status) {
            case "move":
                img_status.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.green_800), android.graphics.PorterDuff.Mode.MULTIPLY);
                break;
            case "offline":
                img_status.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.grey_80), android.graphics.PorterDuff.Mode.MULTIPLY);
                speed = 0;
                break;
            case "stop":
                img_status.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.red_500), android.graphics.PorterDuff.Mode.MULTIPLY);
                speed = 0;
                break;
            case "idle":
                img_status.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.blue_500), android.graphics.PorterDuff.Mode.MULTIPLY);
                speed = 0;
                break;
        }
        tv_speed.setText(String.format("%smph", speed));

        // Set bottom view Visible
        lyt_bottom.setVisibility(View.VISIBLE);
    }

    public void getVehicles() {
        OkGo.<JSONArray>get(Constant.API_URL + Constant.METHOD_VEHICLES).execute(new Callback<JSONArray>() {
            @Override
            public void onStart(Request<JSONArray, ? extends Request> request) {
                /*
                if (!CheckConnection.isNetworkAvailable(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), getString(R.string.no_network), Toast.LENGTH_SHORT).show();
                }
                 */
            }

            @Override
            public void onSuccess(Response<JSONArray> response) {
                try {
                    Log.e(TAG, "Response: " + response.body());
                    if (response.body() != null) {
                        JSONArray jsonArray = new JSONArray(response.body().toString());
                        vehiclesList = new ArrayList<>();
                        Gson gson = new Gson();
                        Vehicle vehicle = null;
                        latLngBoundBuilder = new LatLngBounds.Builder();
                        mMap.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            try {
                                vehicle = gson.fromJson(jsonObject.toString(), Vehicle.class);
                                vehiclesList.add(vehicle);

                                if (selectedVehicle != null) {
                                    if (selectedVehicle.getVin().equals(vehicle.getVin())) {
                                        selectedVehicle.setLatitude(vehicle.getLatitude());
                                        selectedVehicle.setLongitude(vehicle.getLongitude());
                                    }
                                }

                            } catch (Exception e) {
                                Log.e(TAG, "Catch: " + e.getMessage());
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            int id = jsonObject.getInt("vehicleId");
                            double lat = jsonObject.getDouble("latitude");
                            double lng = jsonObject.getDouble("longitude");
                            float rotation = (float) jsonObject.getDouble("course");
                            String driver = jsonObject.getString("driver");
                            String vin = jsonObject.getString("vin");
                            String status = jsonObject.getString("status");
                            String title = vin;
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

                            addMarker(vehicle, lat, lng, title, driver, rotation, resource);
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCacheSuccess(Response<JSONArray> response) {
                Log.e(TAG, "onCacheSuccess()");
            }

            @Override
            public void onError(Response<JSONArray> response) {
                Log.e(TAG, "onError()");
            }

            @Override
            public void onFinish() {
                if (dialog.isShowing())
                    dialog.dismiss();

                if (!isFiltered) {
                    if (latLngBoundBuilder != null && selectedVehicle == null && vehiclesList.size() > 0) {
                        updateMapBounds();
                    }
                } else if (isFiltered && selectedVehicle != null) {
                    updateCameraMarker(new LatLng(selectedVehicle.getLatitude(), selectedVehicle.longitude));
                }
            }

            @Override
            public void uploadProgress(Progress progress) {
            }

            @Override
            public void downloadProgress(Progress progress) {
            }

            @Override
            public JSONArray convertResponse(okhttp3.Response response) {
                try {
                    if (response.isSuccessful()) {
                        ResponseBody responseBody = response.body();
                        String res = responseBody.string();
                        JSONArray jsonObject = new JSONArray(res);
                        return jsonObject;
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                return null;
            }
        });
    }

    private void addMarker(Vehicle vehicle, double lat, double lon,
                           String title, String snippet, float rotation, int resource) {
        MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(lat, lon))
                .title(title)
                .snippet(snippet)
                .icon(bitmapDescriptorFromVector(getApplicationContext(), resource))
                .rotation(rotation);

        Marker marker = mMap.addMarker(markerOptions);
        markers.add(marker);
        marker.setTag(vehicle);

        latLngBoundBuilder.include(marker.getPosition());
    }

    private void showMarker(String search) {
        for (Marker marker : markers) {
            Log.d(TAG, "Marker title: " + marker.getTitle());
            Log.d(TAG, "Search title: " + search);

            // Search VIN
            if (marker.getTitle().toLowerCase().contains(search.toLowerCase())) {
                marker.setVisible(true);
                markersFiltered.add(marker);
                latLngBoundBuilder.include(marker.getPosition());
            }
            // Search Driver
            else if (marker.getSnippet().toLowerCase().contains(search.toLowerCase())) {
                marker.setVisible(true);
                markersFiltered.add(marker);
                latLngBoundBuilder.include(marker.getPosition());
            }
            // Search empty
            else if (search.isEmpty()) {
                marker.setVisible(true);
                markersFiltered.add(marker);
                latLngBoundBuilder.include(marker.getPosition());
            }
            // No match
            else {
                marker.setVisible(false);
            }
        }
    }

    private void showLocations(String location) {
        isFiltered = false;
        lyt_bottom.setVisibility(View.GONE);

        locations = new ArrayList<>();

        // Get groupid
        int groupid = 0;
        for (int j = 0; j < groups.size(); j++) {
            if (groups.get(j).getName() == location) {
                groupid = groups.get(j).getId();
            }
        }

        // Create new list with location
        for (int i = 0; i < vehicleGroups.size(); i++) {
            if (vehicleGroups.get(i).getGroupId() == groupid) {
                locations.add(vehicleGroups.get(i).getVin());
            }
        }

        String str = TextUtils.join(",", locations);

        latLngBoundBuilder = new LatLngBounds.Builder();

        for (Marker marker : markers) {
            Log.d(TAG, "Marker title: " + marker.getTitle());
            Log.d(TAG, "Search title: " + location);


            // Search VIN
            if (str.contains(marker.getTitle())) {
                marker.setVisible(true);
                markersFiltered.add(marker);
                latLngBoundBuilder.include(marker.getPosition());
            }
            // No match
            else {
                marker.setVisible(false);
            }
        }

        try{
            updateMapBounds();
        } catch (Exception ex) {

        }

        toggleLocations();
    }

    public void getGroups() {
        OkGo.<JSONObject>get(Constant.API_URL + Constant.METHOD_GROUPS).execute(new Callback<JSONObject>() {
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
                    //Log.e(TAG, "Response: " + response.body());
                    if (response.body() != null) {
                        Gson gson = new Gson();
                        Root roots = gson.fromJson(response.body().toString(), Root.class);

                        groups = roots.getGroups();
                        organizations = roots.getOrganizations();

                        Log.d(TAG, "groups: " + groups.size());
                        Log.d(TAG, "organizations: " + organizations.size());

                        getVehicleGroups();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCacheSuccess(Response<JSONObject> response) {
                Log.e(TAG, "onCacheSuccess()");
            }

            @Override
            public void onError(Response<JSONObject> response) {
                Log.e(TAG, "onError()");
            }

            @Override
            public void onFinish() {
                if (dialog.isShowing())
                    dialog.dismiss();

                if (latLngBoundBuilder != null && selectedVehicle == null && vehiclesList.size() > 0) {
                    updateMapBounds();
                }
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
                    Log.e(TAG, e.toString());
                }
                return null;
            }
        });
    }

    public void getVehicleGroups() {
        OkGo.<JSONObject>get(Constant.API_URL + Constant.METHOD_VEHICLE_GROUPS).execute(new Callback<JSONObject>() {
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
                    //Log.e(TAG, "Response: " + response.body());
                    if (response.body() != null) {
                        Gson gson = new Gson();
                        Root roots = gson.fromJson(response.body().toString(), Root.class);

                        vehicleGroups = roots.getGroups();
                        Log.d(TAG, "vehicleGroups: " + vehicleGroups.size());

                        expandableListDetail = new HashMap<>();
                        List<String> items;

                        // Loop organizations and show names as headers
                        for (int i = 0; i < organizations.size(); i++) {
                            items = new ArrayList<>();
                            // Loop groups, if is same as org, add to tree
                            for (int j = 0; j < groups.size(); j++) {

                                if (groups.get(j).getGroupId() == organizations.get(i).getId()) {
                                    items.add(groups.get(j).getName());
                                }
                            }
                            Log.d(TAG, "organizations: " + organizations.get(i).getName());
                            Log.d(TAG, "items: " + items.size());
                            expandableListDetail.put(organizations.get(i).getName(), items);
                        }
                        Log.d(TAG, "expandableListDetail: " + expandableListDetail.size());
                        initExpandableListView();

                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCacheSuccess(Response<JSONObject> response) {
                Log.e(TAG, "onCacheSuccess()");
            }

            @Override
            public void onError(Response<JSONObject> response) {
                Log.e(TAG, "onError()");
            }

            @Override
            public void onFinish() {
                if (dialog.isShowing())
                    dialog.dismiss();

                if (latLngBoundBuilder != null && selectedVehicle == null && vehiclesList.size() > 0) {
                    updateMapBounds();
                }
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
                    Log.e(TAG, e.toString());
                }
                return null;
            }
        });
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
