package com.dzt.uberclone;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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

    @Override
    public void onClick(View v)
    {
        //Toast.makeText(getActivity(),"Validating card....", Toast.LENGTH_SHORT).show();
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
