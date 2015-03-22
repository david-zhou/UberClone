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
import android.os.Handler;
import android.os.Message;
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
import java.util.ArrayList;

/**
 * Created by David on 2/10/2015.
 */
public class HomeFragment extends Fragment implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerDragListener {
    private GoogleMap map;
    private static View v;
    private GoogleApiClient mGoogleApiClient;
    double latitude, longitude;
    boolean centerOnCurrent = true, syncWithServer = true, trackDriverBoolean = true, waitForRideToEnd = true;
    TextView selectPickup, statusText;
    Button requestuber;
    LatLng currentLocation;
    int shortestTime;
    int nearbyUbers, ubercount;
    String driverid = "", currentRideId = "";
    ArrayList<Marker> markers = new ArrayList<Marker>();

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
            e.printStackTrace();
        }

        map = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
        map.setMyLocationEnabled(true);

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setOnMapClickListener(this);
        map.setOnMarkerDragListener(this);

        requestuber = (Button) v.findViewById(R.id.request_uber);
        requestuber.setOnClickListener(this);
        statusText = (TextView) v.findViewById(R.id.status_text);
        statusText.setVisibility(View.INVISIBLE);

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
            case R.id.request_uber:
                SharedPreferences sp = getActivity().getSharedPreferences("Session", Context.MODE_PRIVATE);
                String email = sp.getString("email", "");

                StringBuilder sb = new StringBuilder();
                sb.append(getResources().getString(R.string.ip));
                sb.append("uber/request/send?user_id=");
                sb.append(email);
                sb.append("&user_lat=");
                sb.append(currentLocation.latitude);
                sb.append("&user_lon=");
                sb.append(currentLocation.longitude);

                URLpetition petition = new URLpetition("send uber request");
                petition.execute(sb.toString());

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

        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        map.animateCamera(CameraUpdateFactory.zoomTo(16));
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

    private void postSendUberRequest(String json)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(json);
            int pendingrideid = jsonObject.getInt("pending_ride_id");
            waitForUberDriver(pendingrideid);
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }

        showMSG("Waiting for a driver to accept");

        requestuber.setVisibility(View.INVISIBLE);
    }

    private void showShortestTime(String json)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(json);
            int shortestTimeTemp = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getInt("value");
            shortestTime += shortestTimeTemp;
            ubercount++;
            if(ubercount == nearbyUbers)
            {
                displayShortestTime(shortestTime, ubercount);
            }
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
    }
    private void setLocationName(String json)
    {
        String locationname="";
        try {
            JSONObject jsonObject = new JSONObject(json);
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

    private void showUberMarkers(String json)
    {
        try
        {
            markers.clear();
            shortestTime = 0;
            JSONArray jsonArray = new JSONArray(json);
            nearbyUbers = jsonArray.length();
            ubercount = 0;
            if(nearbyUbers == 0)
            {
                displayNoUbersMessage();
            }
            for(int i = 0; i<nearbyUbers; i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                double lat = jsonObject.getDouble("pos_lat");
                double lon = jsonObject.getDouble("pos_long");
                addUberMarker(lat,lon);
                getShortestTime(lat, lon);
            }
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void waitForUberDriver(final int pendingrideid)
    {
        Thread timer = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true)
                {
                    if (syncWithServer)
                    {
                        String response = syncForUber(pendingrideid);
                        threadMsg(response);
                    }
                    else
                    {
                        return;
                    }

                    try
                    {
                        Thread.sleep(1000);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            private void threadMsg(String msg) {
                if (!msg.equals(null) && !msg.equals("")) {
                    Message msgObj = handler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("message", msg);
                    msgObj.setData(b);
                    handler.sendMessage(msgObj);
                }
            }

            private final Handler handler = new Handler() {

                public void handleMessage(Message msg) {

                    String aResponse = msg.getData().getString("message");
                    Log.i("aresponse", aResponse);
                    if ((null != aResponse))
                    {
                        if(aResponse.equals("Waiting"))
                        {
                            statusText.setVisibility(View.VISIBLE);
                            statusText.setText("Waiting for a driver to accept");
                        }
                        else
                        {
                            showDriverData(aResponse);
                            syncWithServer = false;
                        }
                    }
                    else
                    {
                        Toast.makeText(getActivity(),"Not Got Response From Server.",Toast.LENGTH_SHORT).show();
                    }
                }
            };
        });
        timer.start();
    }

    private void showDriverData(String json)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(json);
            driverid = jsonObject.getString("driver_id");
            String name = jsonObject.getString("driver_name");
            String lastname = jsonObject.getString("driver_last_name");
            String vehicle = jsonObject.getString("vehicle");
            String plate = jsonObject.getString("license_plate");
            currentRideId = jsonObject.getString("rideid");

            StringBuilder sb = new StringBuilder();
            sb.append("Your driver is ");
            sb.append(name);
            sb.append(" " + lastname);
            sb.append("\n");
            sb.append(vehicle);
            sb.append(" " + plate);

            statusText.setVisibility(View.VISIBLE);
            statusText.setText(sb.toString());

            removeUbers();
            trackDriver();
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }

    }

    private void removeUbers()
    {
        int l = markers.size();
        for (int i = 0; i < l; i++)
        {
            markers.get(i).remove();
        }
    }

    private void trackDriver()
    {
        trackDriverBoolean = true;
        Thread trackdriverthread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true)
                {
                    if (trackDriverBoolean)
                    {
                        String response = trackDriverPosition();
                        threadMsg(response);
                    }
                    else
                    {
                        return;
                    }

                    try
                    {
                        Thread.sleep(1000);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            private void threadMsg(String msg) {
                if (!msg.equals(null) && !msg.equals("")) {
                    Message msgObj = handler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("message", msg);
                    msgObj.setData(b);
                    handler.sendMessage(msgObj);
                }
            }

            private final Handler handler = new Handler() {

                public void handleMessage(Message msg) {

                    String aResponse = msg.getData().getString("message");
                    Log.i("aresponse", aResponse);
                    if ((null != aResponse))
                    {
                        handleDriverTracking(aResponse);
                    }
                    else
                    {
                        Toast.makeText(getActivity(),"Not Got Response From Server.",Toast.LENGTH_SHORT).show();
                    }
                }
            };
        });
        trackdriverthread.start();
    }

    private String trackDriverPosition()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(getResources().getString(R.string.ip));
        sb.append("uber/ride/track?rideid=");
        sb.append(currentRideId);

        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(sb.toString());
        Log.i("URL = ",sb.toString());
        StringBuilder stringBuilder = new StringBuilder();
        try {
            HttpResponse response = client.execute(get);
            HttpEntity entity = response.getEntity();
            //InputStream stream = new InputStream(entity.getContent(),"UTF-8");
            InputStream stream = entity.getContent();
            BufferedReader r = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            String line;
            while ((line = r.readLine()) != null) {
                stringBuilder.append(line);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    private void handleDriverTracking(String json)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.has("fee"))
            {
                trackDriverBoolean = false;
                double initialLat = jsonObject.getDouble("initial_lat");
                double initialLng = jsonObject.getDouble("initial_lng");
                double finalLat = jsonObject.getDouble("final_lat");
                double finalLng = jsonObject.getDouble("final_lng");
                String distance = jsonObject.getString("distance");
                String time = jsonObject.getString("time");
                String fee = jsonObject.getString("fee");
                String finalFee = jsonObject.getString("final_fee");

                Bundle params = new Bundle();
                params.putString("originText", initialLat+","+initialLng);
                params.putString("destinationText", finalLat+","+finalLng);
                params.putString("timeText", time);
                params.putString("distanceText", distance);
                params.putString("feeText", fee);
                params.putString("finalFeeText", finalFee);
                params.putString("rideId", currentRideId);
                /*

                StringBuilder sb = new StringBuilder();
                sb.append("You went from ");
                sb.append(initialLat);
                sb.append(",");
                sb.append(initialLng);
                sb.append(" to ");
                sb.append(finalLat);
                sb.append(",");
                sb.append(finalLng);
                sb.append(". Your time was ");
                sb.append(time);
                sb.append(" minutes and rode a distance of ");
                sb.append(distance);
                sb.append(" KM. Your fee is $");
                sb.append(fee);
                sb.append(" and your adjusted fee is $");
                sb.append(finalFee);

                Log.i("ride details", sb.toString());
                Toast.makeText(getActivity(), sb.toString(), Toast.LENGTH_LONG).show();

                */

                Intent intent = new Intent(getActivity(), RideDetailsActivity.class);
                intent.putExtras(params);
                startActivity(intent);
                getActivity().finish();

            }
            else
            {
                double lat = jsonObject.getDouble("latitude");
                double lng = jsonObject.getDouble("longitude");
                addAssignedUberMarker(lat, lng);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public String syncForUber(int pendingrideid)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(getResources().getString(R.string.ip));
        sb.append("uber/request/pending?pendingrideid=");
        sb.append(pendingrideid);

        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(sb.toString());
        Log.i("URL = ",sb.toString());
        StringBuilder stringBuilder = new StringBuilder();
        try {
            HttpResponse response = client.execute(get);
            HttpEntity entity = response.getEntity();
            //InputStream stream = new InputStream(entity.getContent(),"UTF-8");
            InputStream stream = entity.getContent();
            BufferedReader r = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            String line;
            while ((line = r.readLine()) != null) {
                stringBuilder.append(line);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return stringBuilder.toString();
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
        Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon_top)).anchor(0.5f,0.5f));
        markers.add(marker);
    }

    private void addAssignedUberMarker(double lat, double lon)
    {
        map.clear();
        map.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon_top)).anchor(0.5f,0.5f));
    }

    public void getShortestTime(double uberlat, double uberlon)
    {
        URLpetition petition = new URLpetition("get shortest time");
        StringBuilder sb = new StringBuilder();
        sb.append("http://maps.googleapis.com/maps/api/directions/json?origin=");
        sb.append(uberlat);
        sb.append(",");
        sb.append(uberlon);
        sb.append("&destination=");
        sb.append(currentLocation.latitude);
        sb.append(",");
        sb.append(currentLocation.longitude);
        sb.append("&mode=driving&sensor=false");
        petition.execute(sb.toString());
    }

    public void displayShortestTime(int time, int ubers)
    {
        int avg = time/ubers;
        Toast.makeText(getActivity(), "Estimate time = "+avg, Toast.LENGTH_SHORT).show();
    }

    public void displayNoUbersMessage()
    {
        Toast.makeText(getActivity(), "There are no Ubers near this location", Toast.LENGTH_SHORT).show();
    }

    public void showMSG(String msg)
    {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
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
                return stringBuilder.toString();

            }
            catch(IOException e) {
                Log.d("Error: ", e.getMessage());
            }
            Log.d("Return text = ", retorno);
            return retorno;
        }

        @Override
        protected void onPostExecute(String result) {
            switch (action)
            {
                default:
                    break;
                case "geocoder inverse":
                    setLocationName(result);
                    break;
                case "get nearby ubers":
                    showUberMarkers(result);
                    break;
                case "get shortest time":
                    showShortestTime(result);
                    break;
                case "send uber request":
                    postSendUberRequest(result);
                    break;
            }
        }

        @Override
        protected void onPreExecute() {}
    }
}