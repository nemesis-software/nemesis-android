package io.nemesis.ninder.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;

import com.google.gson.JsonObject;

import java.util.List;

import io.nemesis.ninder.NinderApplication;
import io.nemesis.ninder.R;
import io.nemesis.ninder.logic.ProductFacade;
import io.nemesis.ninder.util.Util;

public class TabShipping extends Fragment {

    //UI References
    private TextInputEditText field_first_name;
    private TextInputEditText field_surname;
    private TextInputEditText field_address_line_1;
    private TextInputEditText field_address_line_2;
    private TextInputEditText field_town_city;
    private AutoCompleteTextView field_state_country;
    private TextInputEditText field_zipcode;
    private TextInputEditText field_phone;
    private View mProgressView;
    private View mShippingView;
    private AlertDialog dialog;
    //AutoComplete values
    private static final String[] COUNTRIES = new String[] {
            "Country","Bulgaria", "Germany", "Spain", "France", "UK", "Italy", "Japan", "Netherlands", "Russia", "United States"
    };

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
        field_first_name = (TextInputEditText) rootView.findViewById(R.id.field_first_name);
        field_surname = (TextInputEditText) rootView.findViewById(R.id.field_surname);
        field_address_line_1 = (TextInputEditText) rootView.findViewById(R.id.field_address_line_1);
        field_address_line_2 = (TextInputEditText) rootView.findViewById(R.id.field_address_line_2);
        field_town_city = (TextInputEditText) rootView.findViewById(R.id.field_town_city);
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

    private void SaveDetails(){
        if(Util.isTextValid(getContext(),field_first_name)&&(Util.isTextValid(getContext(),field_surname))
                &&(Util.isTextValid(getContext(),field_address_line_1))&&(Util.isTextValid(getContext(),field_town_city))
                &&(Util.isTextValid(getContext(),field_zipcode)) &&(Util.isTextValid(getContext(),field_phone))){
            showProgress(true);
            JsonObject json = new JsonObject();
            json.addProperty("firstName",field_first_name.getText().toString());
            json.addProperty("lastName",field_surname.getText().toString());
            json.addProperty("townCity",field_town_city.getText().toString());
            json.addProperty("countryCode",field_state_country.getText().toString());
            json.addProperty("regionCode",field_zipcode.getText().toString());
            json.addProperty("regionCode",field_town_city.getText().toString());
            json.addProperty("phone",field_phone.getText().toString());
            json.addProperty("line1",field_address_line_1.getText().toString());
            ((NinderApplication) getActivity().getApplication()).getProductFacade().saveDeliveryAddress(json, new ProductFacade.AsyncCallback<String>() {
                @Override
                public void onSuccess(String products) {
                }

                @Override
                public void onFail(Throwable t) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showProgress(false);
                            dialog = new AlertDialog.Builder(getContext())
                                    .setTitle("Success")
                                    .setMessage("Your details has been successfully saved.")
                                    .setPositiveButton("OK",null)
                                    .create();
                            dialog.show();
                        }
                    });
                    t.printStackTrace();
                }
            });
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
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    private void showProgress(final boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mShippingView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

}
