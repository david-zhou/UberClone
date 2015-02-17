package com.dzt.uberclone;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.Toast;

public class AddPaymentFragment extends Fragment implements View.OnClickListener{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_payment, container, false);
        Button addPayment = (Button) v.findViewById(R.id.addPayment_submit);
        addPayment.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v)
    {
        Toast.makeText(getActivity(),"Validating card....", Toast.LENGTH_SHORT).show();
    }

}
