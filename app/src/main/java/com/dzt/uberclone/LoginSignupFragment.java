package com.dzt.uberclone;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginSignupFragment extends Fragment implements View.OnClickListener{

    View v;
    public LoginSignupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_login_signup, container, false);

        Button login = (Button) v.findViewById(R.id.login_button);
        Button signup = (Button) v.findViewById(R.id.signup_button);
        login.setOnClickListener(this);
        signup.setOnClickListener(this);
        return v;
    }

    public void onClick(View v)
    {
        Fragment fragment;
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        switch(v.getId())
        {
            default:
            case R.id.login_button:
                //Toast.makeText(this, "Login", Toast.LENGTH_SHORT).show();
                fragment = new LoginFragment();

                break;
            case R.id.signup_button:
                fragment = new SignupFragment();
                //Toast.makeText(this, "Signup", Toast.LENGTH_SHORT).show();
                break;
        }
        fragmentManager.beginTransaction()
                .replace(R.id.login_container, fragment).addToBackStack("signup/login")
                .commit();

    }


}
