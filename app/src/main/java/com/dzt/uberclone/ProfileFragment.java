package com.dzt.uberclone;

//import android.app.Fragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

/**
 * Created by David on 2/10/2015.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener{
    View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView homeTextView = (TextView) v.findViewById(R.id.profile_account_home);
        homeTextView.setOnClickListener(this);
        TextView workTextView = (TextView) v.findViewById(R.id.profile_account_work);
        workTextView.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v)
    {
        Fragment fragment;
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        switch(v.getId())
        {
            default:
            case R.id.profile_account_home:
                fragment = new AddHomeFragment(true);

                break;
            case R.id.profile_account_work:
                fragment = new AddWorkFragment(false);

                break;
        }
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment).addToBackStack("addhome")
                .commit();
    }
}
