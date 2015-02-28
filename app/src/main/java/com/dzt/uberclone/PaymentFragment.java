package com.dzt.uberclone;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by David on 2/10/2015.
 */
public class PaymentFragment extends Fragment implements View.OnClickListener{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_payment, container, false);
        setHasOptionsMenu(true);
        TextView share = (TextView) v.findViewById(R.id.payment_share);
        share.setOnClickListener(this);



        SharedPreferences sp = getActivity().getSharedPreferences("Session", Context.MODE_PRIVATE);
        Set<String> defaultSet = new HashSet<String>();
        Set<String> ccSet = sp.getStringSet("credit cards", defaultSet);
        List<String> ccList = new ArrayList<String>(ccSet);

        ListView ccListView = (ListView) v.findViewById(R.id.payment_list_creditCards);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,ccList);
        ccListView.setAdapter(adapter);

        //TODO agregar el codigo de referral


        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.getItem(0).setVisible(true);
        inflater.inflate(R.menu.main, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Fragment fragment = new AddPaymentFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment).addToBackStack("addpayment")
                .commit();

        return super.onOptionsItemSelected(item);
    }

    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.payment_share:
                //Toast.makeText(getActivity(), "SHARE", Toast.LENGTH_SHORT).show();
                Fragment fragment;
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragment = new ShareFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragment).addToBackStack("share")
                        .commit();

                break;
        }
    }

}