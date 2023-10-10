/*
 * Copyright 2014-2023, Cypress Semiconductor Corporation (an Infineon company) or
 * an affiliate of Cypress Semiconductor Corporation.  All rights reserved.
 *
 * This software, including source code, documentation and related
 * materials ("Software") is owned by Cypress Semiconductor Corporation
 * or one of its affiliates ("Cypress") and is protected by and subject to
 * worldwide patent protection (United States and foreign),
 * United States copyright laws and international treaty provisions.
 * Therefore, you may use this Software only as provided in the license
 * agreement accompanying the software package from which you
 * obtained this Software ("EULA").
 * If no EULA applies, Cypress hereby grants you a personal, non-exclusive,
 * non-transferable license to copy, modify, and compile the Software
 * source code solely for use in connection with Cypress's
 * integrated circuit products.  Any reproduction, modification, translation,
 * compilation, or representation of this Software except as specified
 * above is prohibited without the express written permission of Cypress.
 *
 * Disclaimer: THIS SOFTWARE IS PROVIDED AS-IS, WITH NO WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, NONINFRINGEMENT, IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. Cypress
 * reserves the right to make changes to the Software without notice. Cypress
 * does not assume any liability arising out of the application or use of the
 * Software or any product or circuit described in the Software. Cypress does
 * not authorize its products for use in any products where a malfunction or
 * failure of the Cypress product may reasonably be expected to result in
 * significant property damage, injury or death ("High Risk Product"). By
 * including Cypress's product in a High Risk Product, the manufacturer
 * of such system or application assumes all risk of such use and in doing
 * so agrees to indemnify Cypress against all liability.
 */

package com.infineon.airocbluetoothconnect.CommonFragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.infineon.airocbluetoothconnect.CommonUtils.Constants;
import com.infineon.airocbluetoothconnect.CommonUtils.ToastUtils;
import com.infineon.airocbluetoothconnect.CommonUtils.Utils;
import com.infineon.airocbluetoothconnect.R;

public class SettingsFragment extends FragmentWithActionBarRestorer implements CompoundButton.OnCheckedChangeListener, NumberPicker.OnValueChangeListener {

    private int mNewVal;
    private boolean mNewValSet;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNewVal = 0;
        mNewValSet = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Clear Cache on Disconnect
        CheckBox clearCacheOnDisconnect = (CheckBox) view.findViewById(R.id.clear_cache_on_disconnect);
        clearCacheOnDisconnect.setChecked(Utils.getBooleanSharedPreference(getActivity(), Constants.PREF_CLEAR_CACHE_ON_DISCONNECT));
        clearCacheOnDisconnect.setOnCheckedChangeListener(this);

        // Delete Bond on Disconnect
        CheckBox unpairOnDisconnect = (CheckBox) view.findViewById(R.id.unpair_on_disconnect);
        unpairOnDisconnect.setChecked(Utils.getBooleanSharedPreference(getActivity(), Constants.PREF_UNPAIR_ON_DISCONNECT));
        unpairOnDisconnect.setOnCheckedChangeListener(this);

        // Initiate Pairing on Connection
        CheckBox pairOnConnect = (CheckBox) view.findViewById(R.id.pair_on_connect);
        pairOnConnect.setChecked(Utils.getBooleanSharedPreference(getActivity(), Constants.PREF_PAIR_ON_CONNECT));
        pairOnConnect.setOnCheckedChangeListener(this);

        // Wait for Pairing Request from Peer Device (seconds)
        NumberPicker np = (NumberPicker) view.findViewById(R.id.wait_for_pair_request_seconds);
        np.setMinValue(0);
        np.setMaxValue(10);
        np.setValue(Utils.getIntSharedPreference(getActivity(), Constants.PREF_WAIT_FOR_PAIRING_REQUEST_FROM_PERIPHERAL_SECONDS));
        np.setOnValueChangedListener(this);

        setHasOptionsMenu(true); // for the onCreateOptionsMenu to be invoked
        Utils.setUpActionBar((AppCompatActivity) getActivity(), R.string.fragment_settings);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.global, menu);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.clear_cache_on_disconnect:
                Utils.setBooleanSharedPreference(getActivity(), Constants.PREF_CLEAR_CACHE_ON_DISCONNECT, isChecked);
                ToastUtils.makeText(getString(R.string.clear_cache_on_disconnect) + (isChecked ? " enabled" : " disabled"), Toast.LENGTH_SHORT);
                break;
            case R.id.unpair_on_disconnect:
                Utils.setBooleanSharedPreference(getActivity(), Constants.PREF_UNPAIR_ON_DISCONNECT, isChecked);
                ToastUtils.makeText(getString(R.string.unpair_on_disconnect) + (isChecked ? " enabled" : " disabled"), Toast.LENGTH_SHORT);
                break;
            case R.id.pair_on_connect:
                Utils.setBooleanSharedPreference(getActivity(), Constants.PREF_PAIR_ON_CONNECT, isChecked);
                ToastUtils.makeText(getString(R.string.pair_on_connect) + (isChecked ? " enabled" : " disabled"), Toast.LENGTH_SHORT);
                break;
        }
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        Utils.setIntSharedPreference(getActivity(), Constants.PREF_WAIT_FOR_PAIRING_REQUEST_FROM_PERIPHERAL_SECONDS, newVal);
        // To prevent being spammed by toasts while scrolling the spinner we poll and display the value every second
        mNewVal = newVal; // remember most recent value
        if (!mNewValSet) {
            mNewValSet = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mNewValSet = false;
                    if (SettingsFragment.this.isResumed()) {
                        ToastUtils.makeText(
                                getString(R.string.wait_for_pairing_request_from_peripheral_seconds) + ": " + mNewVal,
                                Toast.LENGTH_SHORT);
                    }
                }
            }, 1000);
        }
    }
}
