package com.dzt.uberclone;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterPaymentFragment extends Fragment implements View.OnClickListener{

    View v;
    String name, lastname, email, country, phone, password;
    EditText creditcard, mm, yy, cvv, postalcode;
    Spinner cccountry;

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
        creditcard = (EditText) v.findViewById(R.id.registerPayment_credit_card);
        mm = (EditText) v.findViewById(R.id.registerPayment_mm);
        yy = (EditText) v.findViewById(R.id.registerPayment_yy);
        cvv = (EditText) v.findViewById(R.id.registerPayment_cvv);
        postalcode = (EditText) v.findViewById(R.id.registerPayment_postalCode);
        cccountry = (Spinner) v.findViewById(R.id.registerPayment_country);

        return v;
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
                    /*
                    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    Log.d("Flag 1 ", "Success");
                    while ((read = rd.readLine()) != null) {
                        retorno += read;
                    }*/
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
            if(action.equals("User"))
            {
                if (result.equals("Error"))
                {
                    Toast.makeText(getActivity(), "There was an error with your user data", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //Toast.makeText(getActivity(), "Sign up successful", Toast.LENGTH_SHORT).show();
                    StringBuilder sb = new StringBuilder("");
                    sb.append(getResources().getString(R.string.ip));
                    sb.append("creditcards/register?credit_card_number=");
                    sb.append(creditcard.getText());
                    sb.append("&email=");
                    sb.append(email);
                    sb.append("&mm=");
                    sb.append(mm.getText());
                    sb.append("&yy=");
                    sb.append(yy.getText());
                    sb.append("&cvv=");
                    sb.append(cvv.getText());
                    sb.append("&postal_code=");
                    sb.append(postalcode.getText());
                    sb.append("&mail=True");

                    URLpetition petition = new URLpetition("Credit Card");
                    petition.execute(sb.toString());
                    //registrar tarjeta de credito
                }
            }
            else if (action.equals("Credit Card"))
            {
                if (result.equals("Error"))
                {
                    Toast.makeText(getActivity(), "There was an error with your credit card", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //Toast.makeText(getActivity(), "Signup successful", Toast.LENGTH_SHORT).show();
                    SharedPreferences sp = getActivity().getSharedPreferences("Session", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor  = sp.edit();
                    editor.putString("name", name);
                    editor.putString("last name", lastname);
                    editor.putString("email", email);
                    editor.putString("phone", phone);

                    String newCC = creditcard.getText()+":"+mm.getText()+":"+yy.getText()+":"+cvv.getText()+":"+cccountry.getSelectedItem();
                    Set<String> creditCard = new HashSet<>();
                    creditCard.add(newCC);
                    editor.putStringSet("credit cards", creditCard);
                    editor.commit();

                    startActivity(new Intent(getActivity(), MainActivity.class));
                    getActivity().finish();
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
            default:
            case R.id.registerPayment_registerCard:
                //TODO validate card

                if(validateCard()) {

                    Bundle args = getArguments();
                    name = args.getString("name");
                    name = name.replace(" ", "%20");
                    lastname = args.getString("lastname");
                    lastname = lastname.replace(" ", "%20");
                    email = args.getString("email");
                    phone = args.getString("phone");
                    password = args.getString("password");
                    password = password.replace(" ", "%20");
                    country = args.getString("country");
                    country = country.replace(" ", "%20");

                    StringBuilder sb = new StringBuilder("");

                    sb.append(getResources().getString(R.string.ip));
                    sb.append("users/register?email=");
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

                    URLpetition petition = new URLpetition("User");
                    petition.execute(sb.toString());
                }
                break;
        }
    }
    public boolean validateCard()
    {
        boolean valid = true;

        //TODO some fancy credit card validation
        if(creditcard.getText().toString().trim().length()<16)
        {
            creditcard.setError("Invalid Credit Card");
            valid = false;
        }
        else
        {
            creditcard.setError(null);
        }

        if (mm.getText().toString().trim().length()==0)
        {
            mm.setError("Enter month");
            valid = false;
        }
        else if(Integer.parseInt(mm.getText().toString())>12 || Integer.parseInt(mm.getText().toString())<1)
        {
            mm.setError("Months from 1 to 12");
            valid = false;
        }
        else
        {
            mm.setError(null);
        }

        if(yy.getText().toString().trim().length()<2)
        {
            yy.setError("Please enter year");
            valid = false;
        }
        else
        {
            yy.setError(null);
        }

        if(cvv.getText().toString().trim().length()<3)
        {
            cvv.setError("Please enter your CVV");
            valid = false;
        }
        else
        {
            cvv.setError(null);
        }

        if(postalcode.getText().toString().trim().length()<5)
        {
            postalcode.setError("Enter a 5 digits postal code");
            valid = false;
        }
        else
        {
            postalcode.setError(null);
        }

        return valid;
    }


}
