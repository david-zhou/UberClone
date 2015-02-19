package com.dzt.uberclone;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddWorkFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener{
    View v;
    //private AutoCompleteTextView actv;
    boolean home;
    public AddWorkFragment() {

    }

    public AddWorkFragment(boolean home) {
        this.home = home;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_add_work, container, false);

        /*
        actv = (AutoCompleteTextView) v.findViewById(R.id.add_work_autocomplete);
        String[] countries = new String [] {"Mexico", "United States", "Belgium", "Malasya", "Meeeee", "Metano", "Maaaaaaa", "Miiiiiii"};
        ArrayAdapter adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_dropdown_item_1line, countries);
        actv.setAdapter(adapter);
        */

        AutoCompleteTextView autoCompView = (AutoCompleteTextView) v.findViewById(R.id.add_work_autocomplete);
        autoCompView.setAdapter(new PlacesAutoCompleteAdapter(getActivity(), R.layout.list_item));
        autoCompView.setOnItemClickListener(this);
        Button removeWorkButton = (Button) v.findViewById(R.id.remove_work_button);
        if(!home)
        {
            removeWorkButton.setVisibility(View.INVISIBLE);
        }
        else
        {
            removeWorkButton.setOnClickListener(this);
        }

        return v;
    }
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        String str = (String) adapterView.getItemAtPosition(position);
        //Set Work
    }

    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.remove_work_button:
                //remove work code
                break;
        }
    }
}
