package com.ensightplus.faas;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ensightplus.faas.adapter.AssetsListAdapter;
import com.ensightplus.faas.data.Tools;
import com.ensightplus.faas.model.Vehicle;

import java.util.ArrayList;
import java.util.List;

public class ActivityAssets extends AppCompatActivity {

    private static final String TAG = ActivityAssets.class.getSimpleName();
    private List<Vehicle> vehiclesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        initToolbar();
        initComponent();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.title_assets));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setCompleteSystemBarLight(this);
    }

    private void initComponent() {
        Intent intent = getIntent();
        vehiclesList = (List<Vehicle>) intent.getSerializableExtra(getString(R.string.title_assets));
        for (int i = 0; i < vehiclesList.size(); i++) {
            Log.e(TAG, "Vin:" + vehiclesList.get(i).getVin());
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        AssetsListAdapter mAdapter = new AssetsListAdapter(this, vehiclesList);
        recyclerView.setAdapter(mAdapter);

        // on item list clicked
        mAdapter.setOnItemClickListener(new AssetsListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Vehicle obj, int position) {
                Toast.makeText(getApplicationContext(), obj.vin + " clicked", Toast.LENGTH_SHORT).show();
            }
        });
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
