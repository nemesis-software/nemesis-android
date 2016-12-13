package io.nemesis.ninder.fragment;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import io.nemesis.ninder.R;
import io.nemesis.ninder.util.Util;

public class TabPayment extends Fragment {
    TextInputEditText field_name_on_card;
    TextInputEditText field_card_number;
    TextInputEditText field_expiry_date;
    TextInputEditText field_security_code;
    View mPaymentView;
    View mProgressView;
    public TabPayment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_tab_payment, container, false);

        field_name_on_card = (TextInputEditText) rootView.findViewById(R.id.field_name_on_card);
        field_card_number = (TextInputEditText) rootView.findViewById(R.id.field_card_number);
        field_expiry_date = (TextInputEditText) rootView.findViewById(R.id.field_expiry_date);
        field_security_code = (TextInputEditText) rootView.findViewById(R.id.field_security_code);

        mPaymentView = rootView.findViewById(R.id.paymentView);
        mProgressView = rootView.findViewById(R.id.progressBar);
        Button button_save_details = (Button) rootView.findViewById(R.id.button_save_details);
        button_save_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveDetails();
            }
        });
        Button button_scan_card = (Button) rootView.findViewById(R.id.button_scan_card);
        button_scan_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScanCard();
            }
        });
        return rootView;
    }
    private void ScanCard(){
        //Open a scanner
    }
    private void SaveDetails(){
        if(Util.isTextValid(getContext(),field_name_on_card)&&(Util.isTextValid(getContext(),field_card_number))&&(Util.isTextValid(getContext(),field_expiry_date))&&(Util.isTextValid(getContext(),field_security_code))){
            showProgress(true);
            //Send request
            field_name_on_card.setText("");
            field_card_number.setText("");
            field_expiry_date.setText("");
            field_security_code.setText("");
        }
    }

    private void showProgress(final boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mPaymentView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
}
