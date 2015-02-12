package com.dzt.uberclone;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by David on 2/10/2015.
 */
public class HomeFragment extends Fragment{
    private GoogleMap map;
    private static View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        if (v != null) {
            ViewGroup parent = (ViewGroup) v.getParent();
            if (parent != null)
                parent.removeView(v);
        }
        try {
            v = inflater.inflate(R.layout.fragment_home, container, false);
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }

        map = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
        map.setMyLocationEnabled(true);

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        Location myLocation = locationManager.getLastKnownLocation(provider);

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        double latitude = myLocation.getLatitude();
        double longitude = myLocation.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);

        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        map.animateCamera(CameraUpdateFactory.zoomTo(14));
        map.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("You are here!"));

        return v;
    }
}