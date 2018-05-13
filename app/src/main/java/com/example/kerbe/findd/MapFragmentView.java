package com.example.kerbe.findd;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.here.android.mpa.common.GeoBoundingBox;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.GeoPolygon;
import com.here.android.mpa.common.GeoPolyline;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapCircle;
import com.here.android.mpa.mapping.MapFragment;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapPolygon;
import com.here.android.mpa.mapping.MapPolyline;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MapFragmentView {
    private MapFragment m_mapFragment;
    private Activity m_activity;
    private Map m_map;
    private MapPolygon m_polygon;
    private MapPolyline m_polyline;
    private MapCircle m_circle;
    private MapMarker m_map_marker;

    private Button m_polygon_button;
    private Button m_polyline_button;
    private Button m_circle_button;
    private Button m_marker_button;
    public MapFragmentView(Activity activity) {
        m_activity = activity;
        initMapFragment();
        initCreatePolygonButton();
        initCreatePolylineButton();
        initCreateCircleButton();
        initCreateMapMarkerButton();
    }

    private void initMapFragment() {
        /* Locate the mapFragment UI element */
        m_mapFragment = (MapFragment) m_activity.getFragmentManager()
                .findFragmentById(R.id.mapfragment);

        // Set path of isolated disk cache
        String diskCacheRoot = Environment.getExternalStorageDirectory().getPath()
                + File.separator + ".isolated-here-maps";
        // Retrieve intent name from manifest
        String intentName = "";
        try {
            ApplicationInfo ai = m_activity.getPackageManager().getApplicationInfo(m_activity.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;

           // Log.d("MAp0", bundle.getString("android.intent.action.MAIN"));
            //if(bundle.containsKey("android.intent.action.MAIN")){
                Log.d("MAP", "lala");
            intentName = bundle.getString("INTENT_NAME");//}
           // Log.d("MAp0", bundle.getString("android.intent.action.MAIN"));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(this.getClass().toString(), "Failed to find intent name, NameNotFound: " + e.getMessage());
        }

        boolean success = com.here.android.mpa.common.MapSettings.setIsolatedDiskCacheRootPath(diskCacheRoot, intentName);
        if (!success) {
            // Setting the isolated disk cache was not successful, please check if the path is valid and
            // ensure that it does not match the default location
            // (getExternalStorageDirectory()/.here-maps).
            // Also, ensure the provided intent name does not match the default intent name.
        } else {
            if (m_mapFragment != null) {
            Log.d("MAPA", "lala");
                /* Initialize the MapFragment, results will be given via the called back. */
                m_mapFragment.init(new OnEngineInitListener() {
                    @Override
                    public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {

                        Log.d("MAPA", "none error");
                        if (error == Error.NONE) {

                            Log.d("MAPA", "none");
                            /*
                             * If no error returned from map fragment initialization, the map will be
                             * rendered on screen at this moment.Further actions on map can be provided
                             * by calling Map APIs.
                             */
                            Log.d("MAPA", "=");
                            m_map = m_mapFragment.getMap();

                            /*
                             * Set the map center to the 4350 Still Creek Dr Burnaby BC (no animation).
                             */
                            m_map.setCenter(new GeoCoordinate(43.2220146, 76.8512485, 0.0),
                                    Map.Animation.NONE);

                            /* Set the zoom level to the average between min and max zoom level. */
                            m_map.setZoomLevel(14);

                        }
                        else{
                            Log.d("MAPA", "error" + error);
                            Log.d("MAPA", "??");
                            m_map = m_mapFragment.getMap();
                        }
                    }
                });
            }
        }
    }

    /**
     * Initialize Create Polygon Button to add/remove MapPolygon.
     */
    private void initCreatePolygonButton() {
        m_polygon_button = (Button) m_activity.findViewById(R.id.polygon_button);

        m_polygon_button.setOnClickListener(new View.OnClickListener() {
            // if MapPolygon already exist on map, then remove MapPolygon, otherwise create
            // MapPolygon.
            @Override
            public void onClick(View v) {
                if (m_map != null && m_polygon != null) {
                    m_map.removeMapObject(m_polygon);
                    m_polygon = null;
                } else {
                    createPolygon();
                }
            }
        });
    }

    /**
     * Create a MapPolygon and add the MapPolygon to active map view.
     */
    private void createPolygon() {
        // create an bounding box centered at current cent
        //Log.d("MAP", m_map.getCenter().toString());
        GeoBoundingBox boundingBox = new GeoBoundingBox(m_map.getCenter(), 1000, 1000);
        // add boundingbox's four vertices to list of Geocoordinates.
        List<GeoCoordinate> coordinates = new ArrayList<GeoCoordinate>();
        coordinates.add(boundingBox.getTopLeft());
        coordinates.add(new GeoCoordinate(boundingBox.getTopLeft().getLatitude(),
                boundingBox.getBottomRight().getLongitude(),
                boundingBox.getTopLeft().getAltitude()));
        coordinates.add(boundingBox.getBottomRight());
        coordinates.add(new GeoCoordinate(boundingBox.getBottomRight().getLatitude(),
                boundingBox.getTopLeft().getLongitude(), boundingBox.getTopLeft().getAltitude()));
        // create GeoPolygon with list of GeoCoordinates.
        GeoPolygon geoPolygon = new GeoPolygon(coordinates);
        // create MapPolygon with GeoPolygon.
        m_polygon = new MapPolygon(geoPolygon);
        // set line color, fill color and line width
        m_polygon.setLineColor(Color.RED);
        m_polygon.setFillColor(Color.GRAY);
        m_polygon.setLineWidth(12);
        // add MapPolygon to map.
        m_map.addMapObject(m_polygon);
    }

    /**
     * Initialize Create Polyline Button to add/remove MapPolyline.
     */
    private void initCreatePolylineButton() {
        m_polyline_button = (Button) m_activity.findViewById(R.id.polyline_button);

        m_polyline_button.setOnClickListener(new View.OnClickListener() {
            // if MapPolyline already exists on map, then remove MapPolyline, otherwise create
            // MapPolyline.
            @Override
            public void onClick(View v) {
                if (m_map != null && m_polyline != null) {
                    m_map.removeMapObject(m_polyline);
                    m_polyline = null;
                } else {
                    createPolyline();
                }
            }
        });
    }

    /**
     * Create a MapPolyline and add the MapPolyline to active map view.
     */
    private void createPolyline() {
        // create boundingBox centered at current location
        GeoBoundingBox boundingBox = new GeoBoundingBox(m_map.getCenter(), 1000, 1000);
        // add boundingBox's top left and bottom right vertices to list of GeoCoordinates
        List<GeoCoordinate> coordinates = new ArrayList<GeoCoordinate>();
        coordinates.add(boundingBox.getTopLeft());
        coordinates.add(boundingBox.getBottomRight());
        // create GeoPolyline with list of GeoCoordinates
        GeoPolyline geoPolyline = new GeoPolyline(coordinates);
        m_polyline = new MapPolyline(geoPolyline);
        m_polyline.setLineColor(Color.BLUE);
        m_polyline.setLineWidth(12);
        // add GeoPolyline to current active map
        m_map.addMapObject(m_polyline);
    }

    /**
     * Initialize Create Circle Button to add/remove MapCircle.
     */
    private void initCreateCircleButton() {
        m_circle_button = (Button) m_activity.findViewById(R.id.circle_button);

        m_circle_button.setOnClickListener(new View.OnClickListener() {
            // if MapCircle already exist on map, then remove MapCircle, or else create MapCircle.
            @Override
            public void onClick(View v) {
                if (m_map != null && m_circle != null) {
                    m_map.removeMapObject(m_circle);
                    m_circle = null;
                } else {
                    createCircle();
                }
            }
        });
    }

    /**
     * create a MapCircle and add the MapCircle to active map view.
     */
    private void createCircle() {
        // create a MapCircle centered at current location with radius 400
        m_circle = new MapCircle(400.0, m_map.getCenter());
        m_circle.setLineColor(Color.BLUE);
        m_circle.setFillColor(Color.GRAY);
        m_circle.setLineWidth(12);
        m_map.addMapObject(m_circle);
    }

    /**
     * Initialize Create MapMarker Button to add/remove MapMarker.
     */
    private void initCreateMapMarkerButton() {
        m_marker_button = (Button) m_activity.findViewById(R.id.marker_button);

        m_marker_button.setOnClickListener(new View.OnClickListener() {
            // if MapMarker already exist on map, then remove MapMarker, other create MapMarker.
            @Override
            public void onClick(View v) {
                if (m_map != null && m_map_marker != null) {
                    m_map.removeMapObject(m_map_marker);
                    m_map_marker = null;
                } else {
                    createMapMarker();
                }
            }
        });
    }

    /**
     * create a MapMarker and add the MapMarker to active map view.
     */
    private void createMapMarker() {
        // create an image from cafe.png.
        Image marker_img = new Image();
        try {
            marker_img.setImageResource(R.drawable.cafe);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // create a MapMarker centered at current location with png image.
        m_map_marker = new MapMarker(m_map.getCenter(), marker_img);
        // add a MapMarker to current active map.
        m_map.addMapObject(m_map_marker);
    }
}
