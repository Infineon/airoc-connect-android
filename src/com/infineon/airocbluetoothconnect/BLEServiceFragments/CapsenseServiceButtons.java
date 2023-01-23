/*
 * Copyright 2014-2022, Cypress Semiconductor Corporation (an Infineon company) or
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

package com.infineon.airocbluetoothconnect.BLEServiceFragments;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.infineon.airocbluetoothconnect.BLEConnectionServices.BluetoothLeService;
import com.infineon.airocbluetoothconnect.CommonUtils.Constants;
import com.infineon.airocbluetoothconnect.CommonUtils.Logger;
import com.infineon.airocbluetoothconnect.CommonUtils.Utils;
import com.infineon.airocbluetoothconnect.DataModelClasses.CapSenseButtonsGridModel;
import com.infineon.airocbluetoothconnect.ListAdapters.CapSenseButtonsGridAdapter;
import com.infineon.airocbluetoothconnect.R;

import java.util.ArrayList;

/**
 * Fragment to display the CapSense Buttons
 */
public class CapsenseServiceButtons extends Fragment {

    // GATT Services and characteristics
    private static BluetoothGattService mService;
    public static BluetoothGattCharacteristic mNotifyCharacteristic;
    public static BluetoothGattCharacteristic mReadCharacteristic;

    // Data variables
    private int mCount = 1;
    private GridView mGridView;
    private CapSenseButtonsGridAdapter mCapsenseButtonsAdapter;
    private ArrayList<CapSenseButtonsGridModel> mData = new ArrayList<CapSenseButtonsGridModel>();
    private ArrayList<Integer> mReceivedButtons = new ArrayList<Integer>();

    /**
     * BroadcastReceiver for receiving the GATT server status
     */
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Bundle extras = intent.getExtras();

            // Data Available
            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {

                // Check for CapSense buttons
                if (extras.containsKey(Constants.EXTRA_CAPBUTTONS_VALUE)) {
                    mReceivedButtons = extras
                            .getIntegerArrayList(Constants.EXTRA_CAPBUTTONS_VALUE);
                    displayLiveData(mReceivedButtons);
                }
            }
        }
    };

    public static CapsenseServiceButtons create(BluetoothGattService service) {
        CapsenseServiceButtons fragment = new CapsenseServiceButtons();
        mService = service;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.capsense_buttons, container,
                false);
        mGridView = (GridView) rootView
                .findViewById(R.id.capsense_buttons_grid);
        setHasOptionsMenu(true);
        return rootView;
    }

    /**
     * Display buttons data
     *
     * @param button_data
     */
    private void displayLiveData(ArrayList<Integer> button_data) {
        int buttonCount = button_data.get(0);
        fillButtons(buttonCount);
        setDataAdapter();
        mCapsenseButtonsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.debug("CapSense Buttons: lifecycle: onResume", this, getActivity());
        BluetoothLeService.registerBroadcastReceiver(getActivity(), mGattUpdateReceiver, Utils.makeGattUpdateIntentFilter());
    }

    @Override
    public void onDestroy() {
        Utils.debug("CapSense Buttons: lifecycle: onDestroy", this, getActivity());
        BluetoothLeService.unregisterBroadcastReceiver(getActivity(), mGattUpdateReceiver);
        super.onDestroy();
    }

    /**
     * Insert The Data
     *
     * @param buttons
     */
    private void fillButtons(int buttons) {
        mData.clear();
        for (int i = 0; i < buttons; i++) {
            mData.add(new CapSenseButtonsGridModel("" + (mCount + i)
            ));
        }
    }

    /**
     * Set the Data Adapter
     */
    private void setDataAdapter() {
        mCapsenseButtonsAdapter = new CapSenseButtonsGridAdapter(getActivity(),
                mData, mReceivedButtons);
        mGridView.setAdapter(mCapsenseButtonsAdapter);
    }

}
