package com.example.crowdit;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends Activity implements OnMapReadyCallback {

    private GoogleMap map;
    private UiSettings uiSettings;
    private boolean myLocationPermissionDenied = false;
    private static final int MY_LOCATION_REQUEST_CODE = 1;
    private Marker marker;
    private LinearLayout mainLayout;
    private float color = BitmapDescriptorFactory.HUE_AZURE;

    private int mWidth;
    private int mHeight;
    private View popupView;
    private PopupWindow popupWindow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);
        mainLayout = (LinearLayout) findViewById(R.id.activity_main);
    }

    private boolean isMapReady(){
        return map != null;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap){
        map = googleMap;
        marker = map.addMarker(new MarkerOptions()
                .position(new LatLng(38.986918,-76.942554))
                .title("University of Maryland, College Park"));
        marker.setIcon(BitmapDescriptorFactory
                .defaultMarker(color));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng
                (38.986918,-76.942554), 15.1f));
        uiSettings = map.getUiSettings();
        uiSettings.setZoomGesturesEnabled(true);
        uiSettings.setZoomControlsEnabled(true);

        // Override method so it doesn't center on marker touch
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker m1) {
                marker = m1;
                marker.showInfoWindow();
                if (popupWindow != null) {
                    uiSettings.setZoomGesturesEnabled(true);
                    uiSettings.setZoomControlsEnabled(true);
                    uiSettings.setScrollGesturesEnabled(true);
                    popupWindow.dismiss();
                    popupWindow = null;
                } else {
                    LayoutInflater inflater = (LayoutInflater) getBaseContext()
                            .getSystemService(LAYOUT_INFLATER_SERVICE);
                    popupView = inflater.inflate(R.layout.activity_pop_up, null);
                    popupWindow = new PopupWindow(popupView,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);

                    Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    popupView.measure(size.x, size.y);
                    mWidth = popupView.getMeasuredWidth();
                    mHeight = popupView.getMeasuredHeight();

                    Button delete = (Button) popupView.findViewById(R.id.delete);

                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            uiSettings.setZoomGesturesEnabled(true);
                            uiSettings.setZoomControlsEnabled(true);
                            uiSettings.setScrollGesturesEnabled(true);
                            popupWindow.dismiss();
                            popupWindow = null;
                            marker.remove();
                            marker = null;
                        }
                    });
                    updatePopup();
                }
                return true;
            }
        });

        // Sets marker on map, opens fragment for user to specify info
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng arg0) {
                if (popupWindow != null) {
                    uiSettings.setZoomGesturesEnabled(true);
                    uiSettings.setZoomControlsEnabled(true);
                    uiSettings.setScrollGesturesEnabled(true);
                    popupWindow.dismiss();
                }
                marker = map.addMarker(new MarkerOptions()
                        .position(arg0)
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(color)));
            }
        });
    }

    private void updatePopup() {
        if (marker != null && popupWindow != null) {
            // marker is visible
            uiSettings.setZoomGesturesEnabled(false);
            uiSettings.setZoomControlsEnabled(false);
            uiSettings.setScrollGesturesEnabled(false);
            popupWindow.showAtLocation(popupView, Gravity.NO_GRAVITY, 0, 0);
            Point p = map.getProjection().toScreenLocation(marker
                    .getPosition());
            popupWindow.update(p.x - mWidth / 2, p.y - mHeight / 2, -1,
                    -1);
        }
    }

    public void setMyLocation(View view){
        if(!isMapReady()){
            return;
        }
        CheckBox myLocationCheckbox = (CheckBox) view;
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)==
                PackageManager.PERMISSION_GRANTED){
            boolean isChecked = myLocationCheckbox.isChecked();
            map.setMyLocationEnabled(isChecked);
            uiSettings.setMyLocationButtonEnabled(isChecked);
            // Set a marker at user location and open fragment for editing
            if (isChecked) {
                LocationManager svc = (LocationManager)
                        getSystemService(LOCATION_SERVICE);
                Location loc = svc.getLastKnownLocation(LocationManager
                        .PASSIVE_PROVIDER);
                LatLng userLoc = new LatLng(loc.getLatitude(),
                        loc.getLongitude());
                marker = map.addMarker(new MarkerOptions().position(userLoc));
                marker.setIcon(BitmapDescriptorFactory
                        .defaultMarker(color));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLoc,
                        15.1f));
            } else if (marker != null) {
                marker.remove();
                marker = null;
            }
        } else {
            myLocationCheckbox.setChecked(false);
            if(!myLocationPermissionDenied){
                ActivityCompat.requestPermissions(this,new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION
                },MY_LOCATION_REQUEST_CODE);
            }
        }
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        String selection = "";
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_low:
                if (checked) {
                    color = BitmapDescriptorFactory.HUE_GREEN;
                }

                selection = "low";

                break;
            case R.id.radio_med:
                if (checked) {
                    color = BitmapDescriptorFactory.HUE_YELLOW;
                }
                selection = "medium";

                break;
            case R.id.radio_high:
                if (checked) {
                    color = BitmapDescriptorFactory.HUE_RED;
                }
                selection = "high";
                break;
        }

        if (marker != null) {
            Client c = new Client(marker.getPosition().toString() + selection);
            c.execute();
            marker.setIcon(BitmapDescriptorFactory
                    .defaultMarker(color));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantedResults){
        switch(requestCode){
            case MY_LOCATION_REQUEST_CODE:{
                if (grantedResults.length == 0 || grantedResults[0] !=
                        PackageManager.PERMISSION_GRANTED){
                    myLocationPermissionDenied = true;
                }
            }
        }
    }
}