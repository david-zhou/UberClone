package com.dzt.uberclone;


import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignupFragment extends Fragment implements View.OnClickListener{

    View v;
    EditText name, lastname, email, phone, password;
    Spinner country;

    public SignupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_signup, container, false);

        Button next = (Button) v.findViewById(R.id.signup_next);
        next.setOnClickListener(this);

        name = (EditText) v.findViewById(R.id.signup_name);
        lastname = (EditText) v.findViewById(R.id.signup_lastname);
        email = (EditText) v.findViewById(R.id.signup_email);
        phone = (EditText) v.findViewById(R.id.signup_phone);
        password = (EditText) v.findViewById(R.id.signup_password);
        country = (Spinner) v.findViewById(R.id.signup_country);

        return v;
    }

    public void onClick(View v)
    {
        switch(v.getId())
        {
            default:
            case R.id.signup_next:
                if(validate_signup_fields())
                {
                    Fragment fragment = new RegisterPaymentFragment();

                    Bundle args = new Bundle();
                    args.putString("name", name.getText().toString().trim());
                    args.putString("lastname", lastname.getText().toString().trim());
                    args.putString("email", email.getText().toString().trim());
                    args.putString("phone", phone.getText().toString().trim());
                    args.putString("password", password.getText().toString().trim());
                    args.putString("country", country.getSelectedItem().toString().trim());

                    fragment.setArguments(args);

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.login_container, fragment).addToBackStack("registerCard")
                            .commit();

                    //next fragment
                }
                break;
        }
    }

    boolean validate_signup_fields()
    {
        boolean valid = true;
        if(name.getText().toString().trim().equals(""))
        {
            name.setError("Name must not be empty");
            valid = false;
        }
        else
        {
            name.setError(null);
        }

        if(lastname.getText().toString().trim().equals(""))
        {
            lastname.setError("Last name must not be empty");
            valid = false;
        }
        else
        {
            lastname.setError(null);
        }

        if(email.getText().toString().trim().equals(""))
        {
            email.setError("Email must not be empty");
            valid = false;
        }
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches())
        {
            email.setError("Enter a valid email address");
            valid = false;
        }
        else
        {
            email.setError(null);
        }

        if(phone.getText().toString().trim().length() < 10 || !Patterns.PHONE.matcher(phone.getText()).matches())
        {
            phone.setError("Enter a 10 digit phone number");
            valid = false;
        }
        else
        {
            phone.setError(null);
        }

        if(password.getText().toString().trim().equals(""))
        {
            password.setError("Password can't be empty");
            valid = false;
        }
        else
        {
            password.setError(null);
        }

        return valid;
    }

}
