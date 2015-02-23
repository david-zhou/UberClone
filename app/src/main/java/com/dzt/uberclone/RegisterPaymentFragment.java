package com.dzt.uberclone;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterPaymentFragment extends Fragment implements View.OnClickListener{

    View v;
    public RegisterPaymentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_register_payment, container, false);

        Button registerCard = (Button) v.findViewById(R.id.registerPayment_registerCard);
        registerCard.setOnClickListener(this);

        return v;
    }

    public void onClick(View v)
    {
        switch(v.getId())
        {
            default:
            case R.id.registerPayment_registerCard:
                //TODO validate card
                Toast.makeText(getActivity(),"Validating card",Toast.LENGTH_SHORT).show();
                break;
        }
    }


}
