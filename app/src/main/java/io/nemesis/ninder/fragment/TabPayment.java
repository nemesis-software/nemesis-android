package io.nemesis.ninder.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.gson.JsonObject;

import java.util.Random;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;
import io.nemesis.ninder.NinderApplication;
import io.nemesis.ninder.R;
import io.nemesis.ninder.logic.NemesisFacadeImpl;
import io.nemesis.ninder.util.Util;

public class TabPayment extends Fragment {
    TextInputEditText field_name_on_card;
    TextInputEditText field_card_number;
    TextInputEditText field_expiry_date;
    TextInputEditText field_security_code;
    View mPaymentView;
    View mProgressView;
    private int MY_SCAN_REQUEST_CODE;
    public TabPayment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MY_SCAN_REQUEST_CODE = new Random().nextInt(9999);
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
        Intent scanIntent = new Intent(getContext(), CardIOActivity.class);

        // customize these values to suit your needs.
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, false); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false

        // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
        startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_SCAN_REQUEST_CODE) {
            String resultDisplayStr;
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

                // Never log a raw card number. Avoid displaying it, but if necessary use getFormattedCardNumber()
                field_card_number.setText(scanResult.getRedactedCardNumber());

                if (scanResult.isExpiryValid()) {
                    field_expiry_date.setText(scanResult.expiryMonth + "/" + scanResult.expiryYear);
                }

                if (scanResult.cvv != null) {
                    // Never log or display a CVV
                    field_security_code.setText(scanResult.cvv);
                }

                if (scanResult.cardholderName != null) {
                    field_name_on_card.setText(scanResult.cardholderName);
                }
            }
            else {
                resultDisplayStr = "Scan was canceled.";
            }
            // do something with resultDisplayStr, maybe display it in a textView
            // resultTextView.setText(resultDisplayStr);
        }
        // else handle other activity results
    }

    private void SaveDetails(){
        if(Util.isTextValid(getContext(),field_name_on_card)&&(Util.isTextValid(getContext(),field_card_number))&&(Util.isTextValid(getContext(),field_expiry_date))&&(Util.isTextValid(getContext(),field_security_code))){
            showProgress(true);
            JsonObject json = new JsonObject();
            json.addProperty("nameOnCard",field_name_on_card.getText().toString());
            json.addProperty("cardNumber",field_card_number.getText().toString());
            json.addProperty("expiryDate",field_expiry_date.getText().toString());
            json.addProperty("issueNumber",field_security_code.getText().toString());
            ((NinderApplication) getActivity().getApplication()).getProductFacade().savePaymentDetails(json);
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
