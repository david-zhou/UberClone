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
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddWorkFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener{
    View v;
    //private AutoCompleteTextView actv;
    public AddWorkFragment() {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_add_work, container, false);

        /*
        actv = (AutoCompleteTextView) v.findViewById(R.id.add_work_autocomplete);
        String[] countries = new String [] {"Mexico", "United States", "Belgium", "Malasya", "Meeeee", "Metano", "Maaaaaaa", "Miiiiiii"};
        ArrayAdapter adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_dropdown_item_1line, countries);
        actv.setAdapter(adapter);
        */

        AutoCompleteTextView autoCompView = (AutoCompleteTextView) v.findViewById(R.id.add_work_autocomplete);
        autoCompView.setAdapter(new PlacesAutoCompleteAdapter(getActivity(), R.layout.list_item));
        autoCompView.setOnItemClickListener(this);
        Button removeWorkButton = (Button) v.findViewById(R.id.remove_work_button);
        SharedPreferences sp = getActivity().getSharedPreferences("Session", Context.MODE_PRIVATE);
        String work = sp.getString("work", "");

        if(work.equals(""))
        {
            removeWorkButton.setVisibility(View.INVISIBLE);
        }
        else
        {
            removeWorkButton.setOnClickListener(this);
            autoCompView.setText(work);
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
                double worklat = addresses.get(0).getLatitude();
                double worklong = addresses.get(0).getLongitude();
                String workwithoutspaces = str.replace(" ", "%20");
                StringBuilder sb = new StringBuilder();
                String email = sp.getString("email", "");
                sb.append(getResources().getString(R.string.ip));
                sb.append("users/add/work?email=");
                sb.append(email);
                sb.append("&work=");
                sb.append(workwithoutspaces);
                sb.append("&worklat=");
                sb.append(worklat);
                sb.append("&worklong=");
                sb.append(worklong);

                URLpetition petition = new URLpetition("add work");
                petition.execute(sb.toString());
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        SharedPreferences.Editor editor  = sp.edit();
        editor.putString("work", str);
        editor.commit();
        getActivity().getSupportFragmentManager().popBackStackImmediate();
        //Set Work
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
            if(action.equals("add work"))
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
            case R.id.remove_work_button:
                SharedPreferences sp = getActivity().getSharedPreferences("Session",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor  = sp.edit();
                editor.putString("work", "");
                editor.commit();

                StringBuilder sb = new StringBuilder();
                String email = sp.getString("email", "");
                sb.append(getResources().getString(R.string.ip));
                sb.append("users/add/work?email=");
                sb.append(email);
                sb.append("&work=null");

                URLpetition petition = new URLpetition("add work");
                petition.execute(sb.toString());
                getActivity().getSupportFragmentManager().popBackStackImmediate();
                //remove work code
                break;
        }
    }
}
