package io.nemesis.ninder.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;

import io.nemesis.ninder.R;
import io.nemesis.ninder.util.Util;

public class TabShipping extends Fragment {
    private static final String[] COUNTRIES = new String[] {
            "Country","Bulgaria", "Germany", "Spain", "France", "UK", "Italy", "Japan", "Netherlands", "Russia", "United States"
    };
    TextInputEditText field_first_name;
    TextInputEditText field_surname;
    TextInputEditText field_address_line_1;
    TextInputEditText field_address_line_2;
    TextInputEditText field_town_city;
    AutoCompleteTextView field_state_country;
    TextInputEditText field_zipcode;
    TextInputEditText field_phone;
    View mProgressView;
    View mShippingView;
    public TabShipping() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab_shipping, container, false);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, COUNTRIES);
        field_state_country = (AutoCompleteTextView)
                rootView.findViewById(R.id.field_state_country);
        field_state_country.setAdapter(adapter);
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
//                    if(position > 0){
//                        // get spinner value
//                    }else{
//                        // show toast select gender
//                    }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
        field_first_name = (TextInputEditText) rootView.findViewById(R.id.field_first_name);
        field_surname = (TextInputEditText) rootView.findViewById(R.id.field_surname);
        field_address_line_1 = (TextInputEditText) rootView.findViewById(R.id.field_address_line_1);
        field_address_line_2 = (TextInputEditText) rootView.findViewById(R.id.field_address_line_2);
        field_town_city = (TextInputEditText) rootView.findViewById(R.id.field_town_city);
        //field_state_country = (TextInputEditText) rootView.findViewById(R.id.field_state_country);
        field_zipcode = (TextInputEditText) rootView.findViewById(R.id.field_zipcode);
        field_phone = (TextInputEditText) rootView.findViewById(R.id.field_phone);
        mShippingView = rootView.findViewById(R.id.mShippingView);
        mProgressView = rootView.findViewById(R.id.progressBar);
        Button button_save_details = (Button) rootView.findViewById(R.id.button_save_details);
        button_save_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveDetails();
            }
        });
        return rootView;
    }
    private void ScanCard(){
        //Open a scanner
    }
    private void SaveDetails(){
        if(Util.isTextValid(getContext(),field_first_name)&&(Util.isTextValid(getContext(),field_surname))
                &&(Util.isTextValid(getContext(),field_address_line_1))&&(Util.isTextValid(getContext(),field_town_city))
                &&(Util.isTextValid(getContext(),field_zipcode)) &&(Util.isTextValid(getContext(),field_phone))){
            showProgress(true);
            //Send request
            field_first_name.setText("");
            field_surname.setText("");
            field_address_line_1.setText("");
            field_address_line_2.setText("");
            field_town_city.setText("");
            field_state_country.setText("");
            field_zipcode.setText("");
            field_phone.setText("");
        }
    }

    private void showProgress(final boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mShippingView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

}
