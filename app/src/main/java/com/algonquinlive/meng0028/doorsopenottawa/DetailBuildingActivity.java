package com.algonquinlive.meng0028.doorsopenottawa;

/**
 * Created by Yanming Meng (meng0028)  on 2018/1/10.
 */

import android.app.Activity;
import android.app.Fragment;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import android.location.Geocoder;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;



import com.algonquinlive.meng0028.doorsopenottawa.model.BuildingPOJO;


public class DetailBuildingActivity extends FragmentActivity implements OnMapReadyCallback {

    private TextView tvName;
    private TextView tvOpenHours;
    private SupportMapFragment fragBuildingMap;
    private TextView tvDescription;
    private GoogleMap mMap;
    private Geocoder mGeocoder;
    BuildingPOJO mBuilding;
    String mAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_detail);
        BuildingPOJO mBuilding = getIntent().getExtras().getParcelable(BuildingAdapter.BUILDING_KEY);
        if (mBuilding == null) {
            throw new AssertionError("Null data item received!");
        }
        tvName = (TextView) findViewById(R.id.tvName);
        tvOpenHours = (TextView) findViewById(R.id.tvOpenHours);
        fragBuildingMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragBuildingMap);
        tvDescription = (TextView) findViewById(R.id.tvDescription);

        tvName.setText(mBuilding.getNameEN());
        tvOpenHours.setText("Open At : " + mBuilding.getSaturdayStart().toString());
        mAddress = mBuilding.getAddressEN() + " " + mBuilding.getPostalCode();
        mGeocoder = new Geocoder(this, Locale.CANADA);
        fragBuildingMap.getMapAsync(this);
        tvDescription.setText(mBuilding.getDescriptionEN());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        pin(mAddress);
    }

    private void pin( String locationName ) {
        try {
            LatLng ll = new LatLng( mBuilding.getLatitude(), mBuilding.getLongitude() );
            mMap.addMarker( new MarkerOptions().position(ll).title(locationName) );
            mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(ll, 10.F) );
            Toast.makeText(this, "Pinned: " + locationName, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Not found: " + locationName, Toast.LENGTH_SHORT).show();
        }
    }
}

