package io.nemesis.ninder.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import io.nemesis.ninder.R;

public class AccountFragment extends Fragment {
    private FragmentTabHost mTabHost;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_search);
        item.setVisible(false);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //View rootView = inflater.inflate(R.layout.fragment_account, container, false);
        //FragmentTabHost mTabHost = (FragmentTabHost) rootView.findViewById(R.id.tabhost);
        mTabHost = new FragmentTabHost(getActivity());
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.tabhost);

        mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.tab_settings)).setIndicator(getString(R.string.tab_settings)),
                TabSettings.class, null);
        mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.tab_payment)).setIndicator(getString(R.string.tab_payment)),
                TabPayment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.tab_shipping)).setIndicator(getString(R.string.tab_shipping)),
                TabShipping.class, null);

        return mTabHost;
        //return rootView;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTabHost = null;
    }

}
