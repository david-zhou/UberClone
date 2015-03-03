package com.dzt.uberclone;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by David on 2/10/2015.
 */
public class HomeFragment extends Fragment implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private GoogleMap map;
    private static View v;
    private GoogleApiClient mGoogleApiClient;
    double latitude, longitude;
    boolean centerOnCurrent = true;
    TextView selectPickup;

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

        }

        map = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
        map.setMyLocationEnabled(true);

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        SharedPreferences sp = getActivity().getSharedPreferences("Session", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        String location = sp.getString("location","");

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        selectPickup = (TextView) v.findViewById(R.id.select_location);
        selectPickup.setOnClickListener(this);

        if(location.equals(""))
        {
            selectPickup.setText("Select pick up location");
        }
        else
        {
            String latstr = sp.getString("locationlat", "");
            String longstr = sp.getString("locationlong","");
            latitude = Double.parseDouble(latstr);
            longitude = Double.parseDouble(longstr);
            selectPickup.setText(location);

            editor.putString("location","");
            editor.putString("locationlat","");
            editor.putString("locationlong","");
            editor.commit();
            centerOnCurrent = false;
        }

        mGoogleApiClient.connect();

        return v;
        /*
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            //Criteria criteria = new Criteria();
            //String provider = locationManager.getBestProvider(criteria, true);
            //Location myLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            //LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);


            /*




            double latitude, longitude;

            if(location.equals(""))
            {
                latitude = myLocation.getLatitude();
                longitude = myLocation.getLongitude();
                selectPickup.setText("Select pick up location");
            }
            else
            {
                String latstr = sp.getString("locationlat", "");
                String longstr = sp.getString("locationlong","");
                latitude = Double.parseDouble(latstr);
                longitude = Double.parseDouble(longstr);
                // Log.d("latitude: ", latstr);
                // Log.d("longitude: ", longstr);
                selectPickup.setText(location);

                editor.putString("location","");
                editor.putString("locationlat","");
                editor.putString("locationlong","");
                editor.commit();
            }


            LatLng latLng = new LatLng(latitude, longitude);

            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            map.animateCamera(CameraUpdateFactory.zoomTo(16));
            map.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("You are here!"));

            return v;
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
            return v;
        }
        */
    }
    public void onClick(View v)
    {
        Fragment fragment;
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        switch(v.getId())
        {
            default:
            case R.id.select_location:
                //Toast.makeText(getActivity(), "select location", Toast.LENGTH_SHORT).show();
                fragment = new SelectLocationFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragment).addToBackStack("selectlocation")
                        .commit();
                break;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null)
        {
            if (centerOnCurrent)
            {
                //Toast.makeText(getActivity(), "Latitude = " + mLastLocation.getLatitude(), Toast.LENGTH_SHORT).show();
                latitude = mLastLocation.getLatitude();
                longitude = mLastLocation.getLongitude();

            }
            LatLng latLng = new LatLng(latitude, longitude);

            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            map.animateCamera(CameraUpdateFactory.zoomTo(16));
            map.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("You are here!"));
        }
        else
        {
            Toast.makeText(getActivity(), "Could not retrieve your location", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}