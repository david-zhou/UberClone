package com.dzt.uberclone;


import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignupFragment extends Fragment implements View.OnClickListener{

    View v;
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

        return v;
    }

    public void onClick(View v)
    {
        switch(v.getId())
        {
            default:
            case R.id.signup_next:
                //TODO validate fields

                Fragment fragment = new RegisterPaymentFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.login_container, fragment).addToBackStack("registerCard")
                        .commit();

                //next fragment
                break;
        }
    }

}
