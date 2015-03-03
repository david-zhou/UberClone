package com.dzt.uberclone;


import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class SelectLocationFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener{

    View v;
    TextView homeTextView, workTextView;
    String home, work;
    double latitude, longitude;
    String location;
    public SelectLocationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_select_location, container, false);

        AutoCompleteTextView autoCompView = (AutoCompleteTextView) v.findViewById(R.id.select_pickup_location_autocomplete);
        autoCompView.setAdapter(new PlacesAutoCompleteAdapter(getActivity(), R.layout.list_item));
        autoCompView.setOnItemClickListener(this);

        SharedPreferences sp = getActivity().getSharedPreferences("Session", Context.MODE_PRIVATE);
        home = sp.getString("home", "");
        work = sp.getString("work", "");

        homeTextView = (TextView) v.findViewById(R.id.select_location_home);
        workTextView = (TextView) v.findViewById(R.id.select_location_work);

        if(!home.equals(""))
        {
            homeTextView.setOnClickListener(this);
            homeTextView.setText(home);
        }
        else
        {
            homeTextView.setVisibility(View.GONE);
        }

        if(!work.equals(""))
        {
            workTextView.setOnClickListener(this);
            workTextView.setText(work);
        }
        else
        {
            workTextView.setVisibility(View.GONE);
        }

        return v;
    }

    public void onClick(View v)
    {
        switch(v.getId())
        {
            default:
            case R.id.select_location_home:
                location = home;
                //Toast.makeText(getActivity(), "Home: "+home, Toast.LENGTH_SHORT).show();
                getCoordinates();
                break;
            case R.id.select_location_work:
                location = work;
                //Toast.makeText(getActivity(), "Work: "+work, Toast.LENGTH_SHORT).show();
                getCoordinates();
                break;
        }
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
    {
        String str = (String) adapterView.getItemAtPosition(position);
        location = str;
        getCoordinates();
        //Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();

    }

    public void getCoordinates()
    {
        URLpetition petition = new URLpetition("geocoder");
        petition.execute("http://maps.google.com/maps/api/geocode/json?address=" + location.replace(" ", "%20") + "&sensor=false");
    }
    public void centerMap()
    {

        SharedPreferences sp = getActivity().getSharedPreferences("Session", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        //Log.d("lat = ",latitude+"");
        //Log.d("long = ",longitude+"");
        //Log.d("Location = ", location);
        editor.putString("location", location);
        editor.putString("locationlat", latitude + "");
        editor.putString("locationlong", longitude+"");
        editor.commit();

        getActivity().getSupportFragmentManager().popBackStackImmediate();
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
            String retorno="", read;
            StringBuilder stringBuilder = new StringBuilder();
            try {
                HttpResponse response = client.execute(get);
                HttpEntity entity = response.getEntity();
                InputStream stream = entity.getContent();
                int b;
                while ((b = stream.read()) != -1) {
                    stringBuilder.append((char) b);
                }

                if(action.equals("geocoder"))
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
            if(action.equals("geocoder"))
            {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    double lon = 0, lat = 0;
                    try {

                        lon = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                                .getJSONObject("geometry").getJSONObject("location")
                                .getDouble("lng");

                        lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                                .getJSONObject("geometry").getJSONObject("location")
                                .getDouble("lat");

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    latitude = lat;
                    longitude = lon;
                }
                catch(JSONException e)
                {
                    e.printStackTrace();
                }
                centerMap();
            }

        }

        @Override
        protected void onPreExecute() {}
    }

}
