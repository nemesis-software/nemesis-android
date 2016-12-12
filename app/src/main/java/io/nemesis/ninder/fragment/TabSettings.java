package io.nemesis.ninder.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.nemesis.ninder.R;

public class TabSettings extends Fragment {
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
        TextInputEditText field_current_password = (TextInputEditText) rootView.findViewById(R.id.field_current_password);
        TextInputEditText field_new_password = (TextInputEditText) rootView.findViewById(R.id.field_new_password);
        TextInputEditText field_confirm_password = (TextInputEditText) rootView.findViewById(R.id.field_confirm_password);

        return rootView;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }
}
