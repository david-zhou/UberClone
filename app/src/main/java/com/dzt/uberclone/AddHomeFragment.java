package com.dzt.uberclone;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddHomeFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    View v;


    public AddHomeFragment()
    {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_add_home, container, false);
        AutoCompleteTextView autoCompView = (AutoCompleteTextView) v.findViewById(R.id.add_home_autocomplete);
        autoCompView.setAdapter(new PlacesAutoCompleteAdapter(getActivity(), R.layout.list_item));
        autoCompView.setOnItemClickListener(this);
        Button removeHomeButton = (Button) v.findViewById(R.id.remove_home_button);
        SharedPreferences sp = getActivity().getSharedPreferences("Session", Context.MODE_PRIVATE);
        String home = sp.getString("home", "Add Home");

        if(home.equals(""))
        {
            removeHomeButton.setVisibility(View.INVISIBLE);
        }
        else
        {
            removeHomeButton.setOnClickListener(this);
            autoCompView.setText(home);
        }


        return v;
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        String str = (String) adapterView.getItemAtPosition(position);

        Geocoder geocoder = new Geocoder(getActivity());
        List<Address> addresses = new ArrayList<Address>();
        SharedPreferences sp = getActivity().getSharedPreferences("Session",Context.MODE_PRIVATE);
        try
        {
            addresses = geocoder.getFromLocationName(str, 1);
            if(addresses.size() > 0)
            {
                double homelat = addresses.get(0).getLatitude();
                double homelong = addresses.get(0).getLongitude();
                String homewithoutspaces = str.replace(" ", "%20");
                StringBuilder sb = new StringBuilder();
                String email = sp.getString("email", "");
                sb.append(getResources().getString(R.string.ip));
                sb.append("users/add/home?email=");
                sb.append(email);
                sb.append("&home=");
                sb.append(homewithoutspaces);
                sb.append("&homelat=");
                sb.append(homelat);
                sb.append("&homelong=");
                sb.append(homelong);

                URLpetition petition = new URLpetition("add home");
                petition.execute(sb.toString());
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        SharedPreferences.Editor editor  = sp.edit();
        editor.putString("home", str);
        editor.commit();
        getActivity().getSupportFragmentManager().popBackStackImmediate();
        //Set Home
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
            try {
                HttpResponse response = client.execute(get);

                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();

                if (statusCode == 201) {
                    return "Success";
                }
                else
                {
                    return "Error";
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
            if(action.equals("add home"))
            {
                if(result.equals("Success"))
                {

                }
            }
        }

        @Override
        protected void onPreExecute() {}
    }

    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.remove_home_button:
                //remove home code
                SharedPreferences sp = getActivity().getSharedPreferences("Session",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor  = sp.edit();
                editor.putString("home", "");
                editor.commit();

                StringBuilder sb = new StringBuilder();
                String email = sp.getString("email", "");
                sb.append(getResources().getString(R.string.ip));
                sb.append("users/add/home?email=");
                sb.append(email);
                sb.append("&home=null");

                URLpetition petition = new URLpetition("add home");
                petition.execute(sb.toString());

                getActivity().getSupportFragmentManager().popBackStackImmediate();
                break;
        }
    }
}
