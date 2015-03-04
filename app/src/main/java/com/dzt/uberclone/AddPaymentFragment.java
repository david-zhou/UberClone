package com.dzt.uberclone;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddPaymentFragment extends Fragment implements View.OnClickListener{
    EditText ccNumber, ccMM, ccYY, ccCVV;
    Spinner country;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_payment, container, false);
        Button addPayment = (Button) v.findViewById(R.id.addPayment_submit);
        addPayment.setOnClickListener(this);

        ccNumber = (EditText) v.findViewById(R.id.addPayment_credit_card);
        ccMM = (EditText) v.findViewById(R.id.addPayment_mm);
        ccYY = (EditText) v.findViewById(R.id.addPayment_yy);
        ccCVV = (EditText) v.findViewById(R.id.addPayment_cvv);
        country = (Spinner) v.findViewById(R.id.addPayment_country);

        return v;
    }

    public boolean validateCard()
    {
        boolean valid = true;

        //TODO some fancy credit card validation
        if(ccNumber.getText().toString().trim().length()<16)
        {
            ccNumber.setError("Invalid Credit Card");
            valid = false;
        }
        else
        {
            ccNumber.setError(null);
        }

        if (ccMM.getText().toString().trim().length()==0)
        {
            ccMM.setError("Enter month");
            valid = false;
        }
        else if(Integer.parseInt(ccMM.getText().toString())>12 || Integer.parseInt(ccMM.getText().toString())<1)
        {
            ccMM.setError("Months from 1 to 12");
            valid = false;
        }
        else
        {
            ccMM.setError(null);
        }

        if(ccYY.getText().toString().trim().length()<2)
        {
            ccYY.setError("Please enter year");
            valid = false;
        }
        else
        {
            ccYY.setError(null);
        }

        if(ccCVV.getText().toString().trim().length()<3)
        {
            ccCVV.setError("Please enter your CVV");
            valid = false;
        }
        else
        {
            ccCVV.setError(null);
        }

        return valid;
    }

    @Override
    public void onClick(View v)
    {
        //Toast.makeText(getActivity(),"Validating card....", Toast.LENGTH_SHORT).show();


        SharedPreferences sp = getActivity().getSharedPreferences("Session", Context.MODE_PRIVATE);

        String email = sp.getString("email", "");
        StringBuilder sb = new StringBuilder("");
        sb.append(getResources().getString(R.string.ip));
        sb.append("creditcards/register?credit_card_number=");
        sb.append(ccNumber.getText());
        sb.append("&email=");
        sb.append(email);
        sb.append("&mm=");
        sb.append(ccMM.getText());
        sb.append("&yy=");
        sb.append(ccYY.getText());
        sb.append("&cvv=");
        sb.append(ccCVV.getText());
        sb.append("&mail=False");

        URLpetition petition = new URLpetition("creditcard");
        petition.execute(sb.toString());
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
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();

                if (statusCode == 201)
                {
                    return "Success";
                }
                else if (statusCode == 400)
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
            if(action.equals("creditcard"))
            {
                if (result.equals("Error"))
                {
                    Toast.makeText(getActivity(), "There was an error with your credit card", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    addCCPreferences();
                }
            }
        }

        @Override
        protected void onPreExecute() {}
    }
    public void addCCPreferences()
    {
        String newCC = ccNumber.getText()+":"+ccMM.getText()+":"+ccYY.getText()+":"+ccCVV.getText()+":"+country.getSelectedItem();

        SharedPreferences sp = getActivity().getSharedPreferences("Session", Context.MODE_PRIVATE);
        Set<String> defaultSet = new HashSet<String>();
        Set<String> creditCards = new HashSet<>(sp.getStringSet("credit cards", defaultSet));
        creditCards.add(newCC);

        SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet("credit cards",creditCards);
        editor.commit();
        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }
}
