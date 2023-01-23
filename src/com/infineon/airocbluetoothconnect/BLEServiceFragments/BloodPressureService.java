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

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.infineon.airocbluetoothconnect.BLEConnectionServices.BluetoothLeService;
import com.infineon.airocbluetoothconnect.CommonUtils.Constants;
import com.infineon.airocbluetoothconnect.CommonUtils.GattAttributes;
import com.infineon.airocbluetoothconnect.CommonUtils.Logger;
import com.infineon.airocbluetoothconnect.CommonUtils.Utils;
import com.infineon.airocbluetoothconnect.R;

import java.util.List;

/**
 * Fragment to display the blood pressure service
 */
public class BloodPressureService extends Fragment {

    // Service and characteristics
    private static BluetoothGattService mService;
    private static BluetoothGattCharacteristic mIndicateCharacteristic;

    // Data fields
    private TextView mSystolicPressure;
    private TextView mDiastolicPressure;
    private TextView mSystolicPressureUnit;
    private TextView mDiastolicPressureUnit;

    private ProgressDialog mProgressDialog;

    private Button mStartStopBtn;


    private BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Bundle extras = intent.getExtras();
            // GATT Data available
            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {

                // Check for SYSTOLIC pressure
                if (extras.containsKey(Constants.EXTRA_PRESURE_SYSTOLIC_VALUE)) {
                    String receivedSystolicPressure = extras
                            .getString(Constants.EXTRA_PRESURE_SYSTOLIC_VALUE);
                    displaySYSData(receivedSystolicPressure);

                }
                // Check for DIASTOLIC pressure
                if (extras.containsKey(Constants.EXTRA_PRESURE_DIASTOLIC_VALUE)) {
                    String receivedDiastolicPressure = extras
                            .getString(Constants.EXTRA_PRESURE_DIASTOLIC_VALUE);
                    displayDIAData(receivedDiastolicPressure);

                }
                // Check for SYSTOLIC pressure unit
                if (extras
                        .containsKey(Constants.EXTRA_PRESURE_SYSTOLIC_UNIT_VALUE)) {
                    String receivedSystolicPressure = extras
                            .getString(Constants.EXTRA_PRESURE_SYSTOLIC_UNIT_VALUE);
                    displaySYSUnitData(receivedSystolicPressure);

                }
                // Check for DIASTOLIC pressure unit
                if (extras
                        .containsKey(Constants.EXTRA_PRESURE_DIASTOLIC_UNIT_VALUE)) {
                    String receivedDiastolicPressure = extras
                            .getString(Constants.EXTRA_PRESURE_DIASTOLIC_UNIT_VALUE);
                    displayDIAUnitData(receivedDiastolicPressure);

                }
            }
            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDING) {
                    // Bonding...
                    Logger.i("Bonding is in process....");
                    Utils.showBondingProgressDialog(getActivity(), mProgressDialog);
                } else if (state == BluetoothDevice.BOND_BONDED) {
                    String dataLog = getResources().getString(R.string.dl_commaseparator)
                            + "[" + BluetoothLeService.getBluetoothDeviceName() + "|"
                            + BluetoothLeService.getBluetoothDeviceAddress() + "]" +
                            getResources().getString(R.string.dl_commaseparator) +
                            getResources().getString(R.string.dl_connection_paired);
                    Logger.dataLog(dataLog);
                    Utils.hideBondingProgressDialog(mProgressDialog);
                    getGattData();

                } else if (state == BluetoothDevice.BOND_NONE) {
                    String dataLog = getResources().getString(R.string.dl_commaseparator)
                            + "[" + BluetoothLeService.getBluetoothDeviceName() + "|"
                            + BluetoothLeService.getBluetoothDeviceAddress() + "]" +
                            getResources().getString(R.string.dl_commaseparator) +
                            getResources().getString(R.string.dl_connection_unpaired);
                    Logger.dataLog(dataLog);
                    Utils.hideBondingProgressDialog(mProgressDialog);
                }
            }

        }
    };

    public static BloodPressureService create(BluetoothGattService service) {
        mService = service;
        return new BloodPressureService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.blood_pressure_measurement,
                container, false);
        mProgressDialog = new ProgressDialog(getActivity());
        mSystolicPressure = (TextView) rootView.findViewById(R.id.bp_sys_value);
        mDiastolicPressure = (TextView) rootView
                .findViewById(R.id.bp_dia_value);

        mSystolicPressureUnit = (TextView) rootView
                .findViewById(R.id.bp_sys_value_unit);
        mDiastolicPressureUnit = (TextView) rootView
                .findViewById(R.id.bp_dia_value_unit);
        mStartStopBtn = (Button) rootView
                .findViewById(R.id.start_stop_btn);


        // Start/Stop button listener
        mStartStopBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Button btn = (Button) v;
                String buttonText = btn.getText().toString();
                String startText = getResources().getString(
                        R.string.blood_pressure_start_btn);
                String stopText = getResources().getString(
                        R.string.blood_pressure_stop_btn);
                if (buttonText.equalsIgnoreCase(startText)) {
                    btn.setText(stopText);
                    if (mIndicateCharacteristic != null) {
                        prepareBroadcastDataIndicate(mIndicateCharacteristic);
                    }
                    getGattData();
                } else {
                    btn.setText(startText);
                    stopBroadcastDataIndicate(mIndicateCharacteristic);
                }

            }
        });
        setHasOptionsMenu(true);
        return rootView;
    }

    /**
     * Display DIASTOLIC data
     *
     * @param received_diastolic_pressure
     */

    void displayDIAData(String received_diastolic_pressure) {
        mDiastolicPressure.setText(Utils.formatForDefaultLocale("%.2f", Float.parseFloat(received_diastolic_pressure)));

    }

    /**
     * Display SYSTOLIC Data
     *
     * @param received_systolic_pressure
     */
    void displaySYSData(String received_systolic_pressure) {
        mSystolicPressure.setText(Utils.formatForDefaultLocale("%.2f", Float.parseFloat(received_systolic_pressure)));

    }

    /**
     * Display the DIASTOLIC unit
     *
     * @param received_diastolic_pressure
     */
    void displayDIAUnitData(String received_diastolic_pressure) {
        mDiastolicPressureUnit.setText(received_diastolic_pressure);
    }

    /**
     * Display the SYSTOLIC unit
     *
     * @param received_systolic_pressure
     */
    void displaySYSUnitData(String received_systolic_pressure) {
        mSystolicPressureUnit.setText(received_systolic_pressure);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.global, menu);
        MenuItem graph = menu.findItem(R.id.graph);
        MenuItem log = menu.findItem(R.id.log);
        MenuItem search = menu.findItem(R.id.search);
        search.setVisible(false);
        graph.setVisible(false);
        log.setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
        BluetoothLeService.registerBroadcastReceiver(getActivity(), mGattUpdateReceiver, Utils.makeGattUpdateIntentFilter());
        Utils.setUpActionBar((AppCompatActivity) getActivity(), R.string.blood_pressure);
    }

    @Override
    public void onDestroy() {
        if (mIndicateCharacteristic != null) {
            stopBroadcastDataIndicate(mIndicateCharacteristic);
        }
        BluetoothLeService.unregisterBroadcastReceiver(getActivity(), mGattUpdateReceiver);
        super.onDestroy();
    }

    /**
     * Method to get required characteristics from service
     */
    void getGattData() {
        List<BluetoothGattCharacteristic> gattCharacteristics = mService
                .getCharacteristics();

        for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
            String uuidchara = gattCharacteristic.getUuid().toString();
            if (uuidchara
                    .equalsIgnoreCase(GattAttributes.BLOOD_PRESSURE_MEASUREMENT)) {
                mIndicateCharacteristic = gattCharacteristic;
                prepareBroadcastDataIndicate(gattCharacteristic);
                break;
            }

        }
    }

    /**
     * Preparing Broadcast receiver to broadcast indicate characteristics
     *
     * @param gattCharacteristic
     */
    void prepareBroadcastDataIndicate(BluetoothGattCharacteristic gattCharacteristic) {
        if (BluetoothLeService.isPropertySupported(gattCharacteristic, BluetoothGattCharacteristic.PROPERTY_INDICATE)) {
            if (mIndicateCharacteristic != null) {
                BluetoothLeService.setCharacteristicIndication(mIndicateCharacteristic, true);
            }
        }
    }

    /**
     * Stopping Broadcast receiver to broadcast indicate characteristics
     *
     * @param gattCharacteristic
     */
    void stopBroadcastDataIndicate(BluetoothGattCharacteristic gattCharacteristic) {
        if (BluetoothLeService.isPropertySupported(gattCharacteristic, BluetoothGattCharacteristic.PROPERTY_INDICATE)) {
            if (mIndicateCharacteristic != null) {
                BluetoothLeService.setCharacteristicIndication(mIndicateCharacteristic, false);
            }
        }
    }
}
