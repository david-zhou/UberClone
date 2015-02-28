package com.dzt.uberclone;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements View.OnClickListener{

    View v;
    EditText email, password;
    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_login, container, false);
        email = (EditText) v.findViewById(R.id.login_email);
        password = (EditText) v.findViewById(R.id.login_password);
        Button login = (Button) v.findViewById(R.id.login_login);
        login.setOnClickListener(this);
        return v;
    }

    public void onClick(View v)
    {
        switch(v.getId())
        {
            default:
            case R.id.login_login:
                if(validateFields())
                {
                    //create async task
                    URLpetition petition = new URLpetition("login");

                    StringBuilder sb = new StringBuilder("");
                    sb.append(getResources().getString(R.string.ip));
                    sb.append("users/login?email=");
                    sb.append(email.getText());
                    sb.append("&password=");
                    String pass = password.getText().toString().replace(" ", "%20");
                    sb.append(pass);

                    petition.execute(sb.toString());
                }
                break;
        }
    }

    private class URLpetition extends AsyncTask<String, Void, String>
    {
        String action;
        int statusCode;
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
                statusCode = statusLine.getStatusCode();

                if (statusCode == 201) {

                    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    Log.d("Flag 1 ", "Success");
                    while ((read = rd.readLine()) != null) {
                        retorno += read;
                    }
                }
                else if (statusCode == 401)
                {
                    return "Incorrect Password";
                }
                else if(statusCode == 402)
                {
                    return "Email not found";
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
            if(action.equals("login"))
            {
                if(statusCode == 201)
                {
                    try
                    {
                        Toast.makeText(getActivity(), "Login Successful", Toast.LENGTH_SHORT).show();
                        JSONArray creditcards;
                        JSONObject jsonObject = new JSONObject(result);
                        creditcards = jsonObject.getJSONArray("creditcards");
                        Set<String> creditCardSet = new HashSet<>();
                        for (int i = 0; i< creditcards.length(); i++)
                        {
                            JSONObject cc = creditcards.getJSONObject(i);
                            String ccnumber = cc.getString("credit_card_number");
                            String mm = cc.getString("mm");
                            String yy = cc.getString("yy");
                            String cvv = cc.getString("cvv");
                            String newCC = ccnumber + ":" + mm + ":" + yy + ":" + cvv;
                            creditCardSet.add(newCC);
                        }

                        SharedPreferences sp = getActivity().getSharedPreferences("Session", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor  = sp.edit();

                        editor.putStringSet("credit cards", creditCardSet);

                        JSONObject userdata = jsonObject.getJSONObject("userdata");
                        String email = userdata.getString("email");
                        String name = userdata.getString("name");
                        String lastname = userdata.getString("last_name");
                        String phone = userdata.getString("phone");
                        editor.putString("email", email);
                        editor.putString("name", name);
                        editor.putString("last name", lastname);
                        editor.putString("phone", phone);

                        String home = userdata.getString("home");
                        if(!home.equals(""))
                        {
                            String homelat = userdata.getString("home_lat");
                            String homelong = userdata.getString("home_long");

                            editor.putString("home", home);
                            editor.putString("homelat", homelat);
                            editor.putString("homelong", homelong);
                        }

                        String work = userdata.getString("work");
                        if(!work.equals(""))
                        {
                            String worklat = userdata.getString("work_lat");
                            String worklong = userdata.getString("work_long");

                            editor.putString("work", work);
                            editor.putString("worklat", worklat);
                            editor.putString("worklong", worklong);
                        }

                        editor.commit();


                        startActivity(new Intent(getActivity(), MainActivity.class));
                        getActivity().finish();
                    }
                    catch(JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
                else if (statusCode == 401)
                {
                    password.setError("Incorrect Password");
                }
                else if (statusCode == 402)
                {
                    email.setError("This email is not registered");
                }
            }
        }

        @Override
        protected void onPreExecute() {}
    }

    public boolean validateFields()
    {
        boolean valid = true;

        if (email.getText().toString().trim().length() == 0)
        {
            email.setError("Please enter your email");
            valid = false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches())
        {
            email.setError("Please enter a valid email");
            valid = false;
        }
        else
        {
            email.setError(null);
        }

        if(password.getText().toString().trim().length()==0)
        {
            password.setError("Please enter your password");
            valid = false;
        }
        else
        {
            password.setError(null);
        }

        return valid;
    }

}
