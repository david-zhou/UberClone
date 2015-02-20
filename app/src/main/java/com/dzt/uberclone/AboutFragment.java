package com.dzt.uberclone;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by David on 2/10/2015.
 */
public class AboutFragment extends Fragment{
    View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_about, container, false);
        /*
        Set<String> creditCards = new HashSet<String>();
        creditCards.add("creditcard1");
        creditCards.add("1234567890123456:12:34:567");
        SharedPreferences sp = getActivity().getSharedPreferences("Session", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet("credit cards",creditCards);
        editor.commit();
        */
        return v;
    }
}