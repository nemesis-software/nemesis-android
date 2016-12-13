package io.nemesis.ninder.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import io.fabric.sdk.android.services.concurrency.Task;
import io.nemesis.ninder.R;
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
        mSettingsView = rootView.findViewById(R.id.settings_panel);
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
        if(Util.CheckField(getContext(),field_current_password)&&(Util.CheckField(getContext(),field_new_password))&&(Util.CheckField(getContext(),field_confirm_password))){
            showProgress(true);
            //Send request
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
