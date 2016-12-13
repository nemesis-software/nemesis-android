package io.nemesis.ninder.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import io.nemesis.ninder.NinderApplication;
import io.nemesis.ninder.R;
import io.nemesis.ninder.activity.TextActivity;
import io.nemesis.ninder.logic.ProductFacade;
import io.nemesis.ninder.logic.model.Product;
import io.nemesis.ninder.util.Util;

public class TabSettings extends Fragment {
    TextInputEditText field_current_password;
    TextInputEditText field_new_password;
    TextInputEditText field_confirm_password;
    View mSettingsView;
    View mProgressView;

    public TabSettings() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_tab_settings, container, false);

        field_current_password = (TextInputEditText) rootView.findViewById(R.id.field_current_password);
        field_new_password = (TextInputEditText) rootView.findViewById(R.id.field_new_password);
        field_confirm_password = (TextInputEditText) rootView.findViewById(R.id.field_confirm_password);
        mSettingsView = rootView.findViewById(R.id.settingsView);
        mProgressView = rootView.findViewById(R.id.progressBar);
        Button button_save_details = (Button) rootView.findViewById(R.id.button_save_details);
        button_save_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveDetails();
            }
        });
        Button button_privacy = (Button) rootView.findViewById(R.id.button_privacy);
        button_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent new_intent = new Intent(getContext(), TextActivity.class);
                new_intent.putExtra("TEXT_TYPE",getString(R.string.button_privacy));
                startActivity(new_intent);
            }
        });
        Button button_terms = (Button) rootView.findViewById(R.id.button_terms);
        button_terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent new_intent = new Intent(getContext(), TextActivity.class);
                new_intent.putExtra("TEXT_TYPE",getString(R.string.button_terms));
                startActivity(new_intent);
            }
        });
        Button button_returns = (Button) rootView.findViewById(R.id.button_returns);
        button_returns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent new_intent = new Intent(getContext(), TextActivity.class);
                new_intent.putExtra("TEXT_TYPE",getString(R.string.button_returns));
                startActivity(new_intent);
            }
        });

        return rootView;
    }

    private void SaveDetails(){
        if(Util.isPasswordValid(getContext(),field_current_password)&&(Util.isPasswordValid(getContext(),field_new_password))&&(Util.isPasswordValid(getContext(),field_confirm_password))){
            showProgress(true);
            ((NinderApplication) getActivity().getApplication()).getProductFacade().getAccountInfo(new ProductFacade.AsyncCallback<Product>() {
                @Override
                public void onSuccess(List<Product> products) {
                    Log.d("Success","");
                }

                @Override
                public void onFail(Exception e) {
                    showProgress(false);
                    e.printStackTrace();
                }
            });
            field_current_password.setText("");
            field_new_password.setText("");
            field_confirm_password.setText("");
        }
    }

    private void showProgress(final boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mSettingsView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
}
