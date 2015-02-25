package com.dzt.uberclone;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterPaymentFragment extends Fragment implements View.OnClickListener{

    View v;
    String name, lastname, email, country, phone, password;

    public RegisterPaymentFragment()
    {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_register_payment, container, false);

        Button registerCard = (Button) v.findViewById(R.id.registerPayment_registerCard);
        registerCard.setOnClickListener(this);

        return v;
    }

    private class URLpetition extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... params) {
            for (int i = 0; i < 5; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

        }

        @Override
        protected void onPreExecute() {}
    }

    public void onClick(View v)
    {
        switch(v.getId())
        {
            default:
            case R.id.registerPayment_registerCard:
                //TODO validate card
                Bundle args = getArguments();
                name = args.getString("name");
                lastname = args.getString("lastname");
                email = args.getString("email");
                phone = args.getString("phone");
                password = args.getString("password");
                country = args.getString("country");

                try {
                    StringBuilder sb = new StringBuilder("");
                    StringBuilder jsonResults = new StringBuilder();

                    sb.append("https://127.0.0.1:8000/users/register?email=");
                    sb.append(email);
                    sb.append("&name=");
                    sb.append(name);
                    sb.append("&last_name=");
                    sb.append(lastname);
                    sb.append("&password=");
                    sb.append(password);
                    sb.append("&phone=");
                    sb.append(phone);
                    sb.append("&country=");
                    sb.append(country);


                    URL url = new URL(sb.toString());
                    HttpURLConnection conn = null;
                    conn = (HttpURLConnection) url.openConnection();
                    InputStreamReader in = new InputStreamReader(conn.getInputStream());
                    int read;
                    char[] buff = new char[1024];
                    while ((read = in.read(buff)) != -1) {
                        jsonResults.append(buff, 0, read);
                    }
                    Toast.makeText(getActivity(), jsonResults.toString(),Toast.LENGTH_SHORT).show();
                }
                catch (MalformedURLException e)
                {
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }


                //Toast.makeText(getActivity(),name + lastname + email + phone + password + country,Toast.LENGTH_SHORT).show();
                break;
        }
    }


}
