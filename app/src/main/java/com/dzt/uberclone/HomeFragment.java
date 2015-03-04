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
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by David on 2/10/2015.
 */
public class HomeFragment extends Fragment implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerDragListener {
    private GoogleMap map;
    private static View v;
    private GoogleApiClient mGoogleApiClient;
    double latitude, longitude;
    boolean centerOnCurrent = true;
    TextView selectPickup;
    Button requestuber;
    LatLng currentLocation;

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
        map.setOnMapClickListener(this);
        map.setOnMarkerDragListener(this);

        requestuber = (Button) v.findViewById(R.id.request_uber);

        SharedPreferences sp = getActivity().getSharedPreferences("Session", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = sp.edit();
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
                latitude = mLastLocation.getLatitude();
                longitude = mLastLocation.getLongitude();

            }
            LatLng latLng = new LatLng(latitude, longitude);
            currentLocation = latLng;

            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            map.animateCamera(CameraUpdateFactory.zoomTo(16));
            map.addMarker(new MarkerOptions().position(latLng).title("Pick me up here").draggable(true)); //.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon_top)).anchor(0.5,0.5)

            URLpetition petition = new URLpetition("geocoder inverse");
            petition.execute("http://maps.googleapis.com/maps/api/geocode/json?latlng="+latLng.latitude+","+latLng.longitude+"&sensor=false");
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

    @Override
    public void onMapClick(LatLng latLng) {
        //poner un pin y cambiar la direccion
        currentLocation = latLng;
        map.clear();
        map.addMarker(new MarkerOptions().position(latLng).title("Pick me up here").draggable(true)); //.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon_top)).anchor(0.5,0.5)

        URLpetition petition = new URLpetition("geocoder inverse");
        petition.execute("http://maps.googleapis.com/maps/api/geocode/json?latlng="+latLng.latitude+","+latLng.longitude+"&sensor=false");
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        LatLng latLng = marker.getPosition();
        currentLocation = latLng;
        map.clear();
        map.addMarker(new MarkerOptions().position(latLng).title("Pick me up here").draggable(true));

        URLpetition petition = new URLpetition("geocoder inverse");
        petition.execute("http://maps.googleapis.com/maps/api/geocode/json?latlng="+latLng.latitude+","+latLng.longitude+"&sensor=false");
    }


    private class URLpetition extends AsyncTask<String, Void, String>
    {
        String action;
        public URLpetition(String action)
        {
            this.action = action;
        }
        @Override
        protected String doInBackground(String... params) {
            HttpClient client = new DefaultHttpClient();
            Log.d("url = ", params[0]);
            HttpGet get = new HttpGet(params[0]);
            String retorno="";
            StringBuilder stringBuilder = new StringBuilder();
            try {
                HttpResponse response = client.execute(get);
                HttpEntity entity = response.getEntity();
                //InputStream stream = new InputStream(entity.getContent(),"UTF-8");
                InputStream stream = entity.getContent();
                BufferedReader r = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
                String line;
                while ((line= r.readLine()) != null) {
                    stringBuilder.append(line);
                }

                if(action.equals("geocoder inverse") || action.equals("get nearby ubers"))
                {
                    return stringBuilder.toString();
                }
            }
            catch(IOException e) {
                Log.d("Error: ", e.getMessage());
            }
            Log.d("Return text = ", retorno);
            return retorno;
        }

        @Override
        protected void onPostExecute(String result) {
            if (action.equals("geocoder inverse"))
            {
                String locationname="";
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    try {

                        locationname = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                                .getString("formatted_address");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                catch(JSONException e)
                {
                    e.printStackTrace();
                }
                Log.d("location = ", locationname);
                selectPickup.setText(locationname);
                getNearbyUbers();
            }
            else
            {
                if(action.equals("get nearby ubers"))
                {
                    try
                    {
                        JSONArray jsonArray = new JSONArray(result);
                        for(int i = 0; i<jsonArray.length(); i++)
                        {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            double lat = jsonObject.getDouble("pos_lat");
                            double lon = jsonObject.getDouble("pos_long");
                            addUberMarker(lat,lon);
                        }

                    }
                    catch(JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        protected void onPreExecute() {}
    }
    public void getNearbyUbers()
    {
        URLpetition petition = new URLpetition("get nearby ubers");
        StringBuilder sb = new StringBuilder();
        sb.append(getResources().getString(R.string.ip));
        sb.append("drivers/get/nearby?latitude=");
        sb.append(currentLocation.latitude);
        sb.append("&longitude=");
        sb.append(currentLocation.longitude);
        sb.append("&radius=0.005");

        petition.execute(sb.toString());
    }

    public void addUberMarker(double lat, double lon)
    {
        map.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon_top)).anchor(0.5f,0.5f)); //.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon_top)).anchor(0.5,0.5)
    }
}