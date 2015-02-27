package com.dzt.uberclone;

//import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

/**
 * Created by David on 2/10/2015.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener{
    View v;
    boolean hasHome = false, hasWork = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView homeTextView = (TextView) v.findViewById(R.id.profile_account_home);
        homeTextView.setOnClickListener(this);
        TextView workTextView = (TextView) v.findViewById(R.id.profile_account_work);
        workTextView.setOnClickListener(this);
        Button logOut = (Button) v.findViewById(R.id.profile_logOut);
        logOut.setOnClickListener(this);

        SharedPreferences sp = getActivity().getSharedPreferences("Session", Context.MODE_PRIVATE);

        String home =  sp.getString("home","Add Home");
        homeTextView.setText(home);
        if(!home.equals("Add Home"))
        {
            hasHome = true;
            homeTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.home_icon, 0, android.R.drawable.ic_menu_edit,0);
            // set icon to edit
        }

        String work = sp.getString("work","Add Work");
        workTextView.setText(work);
        if (!work.equals("Add Work"))
        {
            hasWork = true;
            workTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.work_icon, 0, android.R.drawable.ic_menu_edit,0);
            // set icon to edit
        }

        TextView name = (TextView) v.findViewById(R.id.profile_account_name);
        TextView lastname = (TextView) v.findViewById(R.id.profile_account_lastName);
        TextView email = (TextView) v.findViewById(R.id.profile_account_email);
        TextView telephone = (TextView) v.findViewById(R.id.profile_account_phoneNumber);
        name.setText(sp.getString("name","default name"));
        lastname.setText(sp.getString("last name","default last name"));
        email.setText(sp.getString("email", "default email"));
        telephone.setText(sp.getString("phone", "default phone"));

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
                fragment = new AddHomeFragment(hasHome);
                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragment).addToBackStack("addhome")
                        .commit();
                break;
            case R.id.profile_account_work:
                fragment = new AddWorkFragment(hasWork);
                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragment).addToBackStack("addhome")
                        .commit();
                break;
            case R.id.profile_logOut:
                logout();
                break;
        }

    }
    public void logout()
    {
        SharedPreferences sp = getActivity().getSharedPreferences("Session", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();

        startActivity(new Intent(getActivity(), InitialActivity.class));
        getActivity().finish();
    }
}
