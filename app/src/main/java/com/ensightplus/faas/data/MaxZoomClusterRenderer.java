package com.ensightplus.faas.data;

import android.content.Context;

import com.ensightplus.faas.model.MyMarker;
import com.google.android.gms.maps.GoogleMap;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

public class MaxZoomClusterRenderer extends DefaultClusterRenderer<MyMarker> implements ClusterManager.OnClusterItemClickListener<MyMarker>, GoogleMap.OnCameraMoveListener {

    private final GoogleMap mMap;
    private float currentZoomLevel, maxZoomLevel;

    public MaxZoomClusterRenderer(Context context, GoogleMap map, ClusterManager<MyMarker> clusterManager, float currentZoomLevel, float maxZoomLevel) {
        super(context, map, clusterManager);

        this.mMap = map;
        this.currentZoomLevel = currentZoomLevel;
        this.maxZoomLevel = maxZoomLevel;
    }

    @Override
    public void onCameraMove() {
        currentZoomLevel = mMap.getCameraPosition().zoom;
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<MyMarker> cluster) {
        // determine if superclass would cluster first, based on cluster size
        boolean superWouldCluster = super.shouldRenderAsCluster(cluster);

        // if it would, then determine if you still want it to based on zoom level
        if (superWouldCluster) {
            superWouldCluster = currentZoomLevel < maxZoomLevel;
        }

        return superWouldCluster;
    }

    @Override
    public boolean onClusterItemClick(MyMarker marker) {
        return false;
    }
}
