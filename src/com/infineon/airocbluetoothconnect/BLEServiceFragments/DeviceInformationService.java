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
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.infineon.airocbluetoothconnect.BLEConnectionServices.BluetoothLeService;
import com.infineon.airocbluetoothconnect.CommonUtils.Constants;
import com.infineon.airocbluetoothconnect.CommonUtils.Logger;
import com.infineon.airocbluetoothconnect.CommonUtils.UUIDDatabase;
import com.infineon.airocbluetoothconnect.CommonUtils.Utils;
import com.infineon.airocbluetoothconnect.R;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

/**
 * Fragment to display the Device Information Service
 */
public class DeviceInformationService extends Fragment {

    private static BluetoothGattService mService;
    private Queue<BluetoothGattCharacteristic> mReadCharacteristics = new LinkedList<>();

    // Data view variables
    private TextView mManufacturerName;
    private TextView mModelNumber;
    private TextView mSerialNumber;
    private TextView mHardwareRevision;
    private TextView mFirmwareRevision;
    private TextView mSoftwareRevision;
    private TextView mSystemId;
    private TextView mRegulatoryCertificationDataList;
    private TextView mPnPId;
    private ProgressDialog mProgressDialog;

    /**
     * BroadcastReceiver for receiving updates from the GATT server
     */
    private BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Bundle extras = intent.getExtras();

            // GATT Data available
            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                if (extras.containsKey(Constants.EXTRA_MANUFACTURER_NAME)) {
                    String receivedManufacturerName = intent.getStringExtra(Constants.EXTRA_MANUFACTURER_NAME);
                    mManufacturerName.setText(receivedManufacturerName);
                    readNextCharacteristic();
                }
                if (extras.containsKey(Constants.EXTRA_MODEL_NUMBER)) {
                    String receivedModelNumber = intent.getStringExtra(Constants.EXTRA_MODEL_NUMBER);
                    mModelNumber.setText(receivedModelNumber);
                    readNextCharacteristic();
                }
                if (extras.containsKey(Constants.EXTRA_SERIAL_NUMBER)) {
                    String receivedSerialNumber = intent.getStringExtra(Constants.EXTRA_SERIAL_NUMBER);
                    mSerialNumber.setText(receivedSerialNumber);
                    readNextCharacteristic();
                }
                if (extras.containsKey(Constants.EXTRA_HARDWARE_REVISION)) {
                    String receivedHardwareRevision = intent.getStringExtra(Constants.EXTRA_HARDWARE_REVISION);
                    mHardwareRevision.setText(receivedHardwareRevision);
                    readNextCharacteristic();
                }
                if (extras.containsKey(Constants.EXTRA_FIRMWARE_REVISION)) {
                    String receivedFirmwareRevision = intent.getStringExtra(Constants.EXTRA_FIRMWARE_REVISION);
                    mFirmwareRevision.setText(receivedFirmwareRevision);
                    readNextCharacteristic();
                }
                if (extras.containsKey(Constants.EXTRA_SOFTWARE_REVISION)) {
                    String receivedSowtwareRevision = intent.getStringExtra(Constants.EXTRA_SOFTWARE_REVISION);
                    mSoftwareRevision.setText(receivedSowtwareRevision);
                    readNextCharacteristic();
                }
                if (extras.containsKey(Constants.EXTRA_SYSTEM_ID)) {
                    String receivedSystemId = intent.getStringExtra(Constants.EXTRA_SYSTEM_ID);
                    mSystemId.setText(receivedSystemId);
                    readNextCharacteristic();
                }
                if (extras.containsKey(Constants.EXTRA_REGULATORY_CERTIFICATION_DATA_LIST)) {
                    String receivedRegulatoryCertificationDataList = intent.getStringExtra(Constants.EXTRA_REGULATORY_CERTIFICATION_DATA_LIST);
                    mRegulatoryCertificationDataList.setText(receivedRegulatoryCertificationDataList);
                    readNextCharacteristic();
                }
                if (extras.containsKey(Constants.EXTRA_PNP_ID)) {
                    String receivedPnPId = intent.getStringExtra(Constants.EXTRA_PNP_ID);
                    mPnPId.setText(receivedPnPId);
                    readNextCharacteristic();
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

    public static DeviceInformationService create(BluetoothGattService service) {
        mService = service;
        return new DeviceInformationService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.device_information_measurement, container, false);
        mManufacturerName = rootView.findViewById(R.id.div_manufacturer);
        mModelNumber = rootView.findViewById(R.id.div_model);
        mSerialNumber = rootView.findViewById(R.id.div_serial);
        mHardwareRevision = rootView.findViewById(R.id.div_hardware);
        mFirmwareRevision = rootView.findViewById(R.id.div_firmware);
        mSoftwareRevision = rootView.findViewById(R.id.div_software);
        mSystemId = rootView.findViewById(R.id.div_system);
        mRegulatoryCertificationDataList = rootView.findViewById(R.id.div_regulatory);
        mPnPId = rootView.findViewById(R.id.div_pnp);
        mProgressDialog = new ProgressDialog(getActivity());
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.device_info);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.setUpActionBar((AppCompatActivity) getActivity(), R.string.device_info);
        clearUI();
        BluetoothLeService.registerBroadcastReceiver(getActivity(), mGattUpdateReceiver, Utils.makeGattUpdateIntentFilter());
        getGattData();
    }

    @Override
    public void onPause() {
        BluetoothLeService.unregisterBroadcastReceiver(getActivity(), mGattUpdateReceiver);
        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.global, menu);
        MenuItem graph = menu.findItem(R.id.graph);
        MenuItem search = menu.findItem(R.id.search);
        graph.setVisible(false);
        search.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void getGattData() {
        collectReadCharacteristics();
        if (!mReadCharacteristics.isEmpty()) {
            readCharacteristic(mReadCharacteristics.peek());
        }
    }

    private void collectReadCharacteristics() {
        mReadCharacteristics.clear();
        List<BluetoothGattCharacteristic> characteristics = Utils.getServiceCharacteristics(mService);
        for (BluetoothGattCharacteristic characteristic : characteristics) {
            UUID uuid = characteristic.getUuid();
            if (uuid.equals(UUIDDatabase.UUID_MANUFACTURER_NAME)
                    || uuid.equals(UUIDDatabase.UUID_MODEL_NUMBER)
                    || uuid.equals(UUIDDatabase.UUID_SERIAL_NUMBER)
                    || uuid.equals(UUIDDatabase.UUID_HARDWARE_REVISION)
                    || uuid.equals(UUIDDatabase.UUID_FIRMWARE_REVISION)
                    || uuid.equals(UUIDDatabase.UUID_SOFTWARE_REVISION)
                    || uuid.equals(UUIDDatabase.UUID_SYSTEM_ID)
                    || uuid.equals(UUIDDatabase.UUID_REGULATORY_CERTIFICATION_DATA_LIST)
                    || uuid.equals(UUIDDatabase.UUID_PNP_ID)) {
                if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_READ) != 0) {
                    mReadCharacteristics.add(characteristic);
                }
            }
        }
    }

    private void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        BluetoothLeService.readCharacteristic(characteristic);
    }

    private void readNextCharacteristic() {
        mReadCharacteristics.poll();
        if (!mReadCharacteristics.isEmpty()) {
            readCharacteristic(mReadCharacteristics.peek());
        }
    }

    private void clearUI() {
        mManufacturerName.setText("");
        mModelNumber.setText("");
        mSerialNumber.setText("");
        mHardwareRevision.setText("");
        mFirmwareRevision.setText("");
        mSoftwareRevision.setText("");
        mSystemId.setText("");
        mRegulatoryCertificationDataList.setText("");
        mPnPId.setText("");
    }
}
