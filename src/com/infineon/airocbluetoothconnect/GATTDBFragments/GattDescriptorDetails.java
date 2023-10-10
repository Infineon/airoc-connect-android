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

package com.infineon.airocbluetoothconnect.GATTDBFragments;


import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.infineon.airocbluetoothconnect.BLEConnectionServices.BluetoothLeService;
import com.infineon.airocbluetoothconnect.BLEProfileDataParserClasses.DescriptorParser;
import com.infineon.airocbluetoothconnect.CommonUtils.Constants;
import com.infineon.airocbluetoothconnect.CommonUtils.GattAttributes;
import com.infineon.airocbluetoothconnect.CommonUtils.Logger;
import com.infineon.airocbluetoothconnect.CommonUtils.Utils;
import com.infineon.airocbluetoothconnect.AIROCBluetoothConnectApp;
import com.infineon.airocbluetoothconnect.R;

/**
 * Descriptor Details Class
 */
public class GattDescriptorDetails extends Fragment implements
        View.OnClickListener {

    //Characteristic
    private BluetoothGattCharacteristic mBluetoothGattCharacteristic;

    //Descriptor
    private BluetoothGattDescriptor mDescriptor;

    // View
    private ViewGroup mContainer;

    // Application
    private AIROCBluetoothConnectApp mApplication;

    //View fields
    private TextView mCharacteristicName;
    private TextView mDescriptorName;
    private TextView mDescriptorValue;
    private TextView mHexValue;
    private Button mReadButton;
    private Button mNotifyButton;
    private Button mIndicateButton;
    private ImageView mBackBtn;
    private ProgressDialog mProgressDialog;
    private String mDescriptorStatus = "";
    private String mStartNotifyText;
    private String mStopNotifyText;
    private String mStartIndicateText;
    private String mStopIndicateText;

    // flags
    private boolean mGUIUpdateFlag = false;
    public static boolean mIsInFragment = false;

    private BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Bundle extras = intent.getExtras();
            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                if (extras.containsKey(Constants.EXTRA_DESCRIPTOR_BYTE_VALUE_UUID)) {
                    if (mApplication != null) {
                        BluetoothGattDescriptor descriptor = mApplication.getBluetoothGattDescriptor();
                        String requiredUUID = descriptor.getUuid().toString();
                        String receivedUUID = intent.
                                getStringExtra(Constants.EXTRA_DESCRIPTOR_BYTE_VALUE_UUID);
                        if (requiredUUID.equalsIgnoreCase(receivedUUID)) {
                            // Data Received
                            if (extras.containsKey(Constants.EXTRA_DESCRIPTOR_VALUE)) {
                                mDescriptorStatus = intent.getStringExtra(Constants.EXTRA_DESCRIPTOR_VALUE);
                                displayDescriptorValue(mDescriptorStatus);
                            }
                            if (extras.containsKey(Constants.EXTRA_DESCRIPTOR_BYTE_VALUE)) {
                                byte[] array = intent
                                        .getByteArrayExtra(Constants.EXTRA_DESCRIPTOR_BYTE_VALUE);
                                displayHexValue(array);
                                updateButtonStatus(array);
                            }
                        }
                    }
                }
            }
            if (action.equals(BluetoothLeService.ACTION_WRITE_SUCCESS)) {
                if (mGUIUpdateFlag) {
                    BluetoothLeService.readDescriptor(mDescriptor);
                    mGUIUpdateFlag = false;
                }
            }
            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE,
                        BluetoothDevice.ERROR);

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

    public GattDescriptorDetails create() {
        GattDescriptorDetails fragment = new GattDescriptorDetails();
        return fragment;
    }

    private void updateButtonStatus(byte[] array) {
        int status = array[0];
        switch (status) {
            case DescriptorParser
                    .CASE_NOTIFY_DISABLED_IND_DISABLED:
                if (mNotifyButton.getVisibility() == View.VISIBLE)
                    mNotifyButton.setText(mStartNotifyText);
                if (mIndicateButton.getVisibility() == View.VISIBLE)
                    mIndicateButton.setText(mStartIndicateText);
                break;
            case DescriptorParser
                    .CASE_NOTIFY_ENABLED_IND_DISABLED:
                if (mNotifyButton.getVisibility() == View.VISIBLE)
                    mNotifyButton.setText(mStopNotifyText);
                if (mIndicateButton.getVisibility() == View.VISIBLE)
                    mIndicateButton.setText(mStartIndicateText);
                break;
            case DescriptorParser
                    .CASE_IND_ENABLED_NOTIFY_DISABLED:
                if (mIndicateButton.getVisibility() == View.VISIBLE)
                    mIndicateButton.setText(mStopIndicateText);
                if (mNotifyButton.getVisibility() == View.VISIBLE)
                    mNotifyButton.setText(mStartNotifyText);
                break;
            case DescriptorParser
                    .CASE_IND_ENABLED_NOTIFY_ENABLED:
                if (mIndicateButton.getVisibility() == View.VISIBLE)
                    mIndicateButton.setText(mStopIndicateText);
                if (mNotifyButton.getVisibility() == View.VISIBLE)
                    mNotifyButton.setText(mStopNotifyText);
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gattdb_descriptor_details, container,
                false);
        this.mContainer = container;
        mApplication = (AIROCBluetoothConnectApp) getActivity().getApplication();
        mCharacteristicName = (TextView) rootView.findViewById(R.id.txtcharacteristicname);
        mDescriptorName = (TextView) rootView.findViewById(R.id.txtdescriptorname);
        mDescriptorValue = (TextView) rootView.findViewById(R.id.txtdescriptorvalue);
        mHexValue = (TextView) rootView.findViewById(R.id.txtdescriptorHexvalue);
        mBackBtn = (ImageView) rootView.findViewById(R.id.imgback);

        mProgressDialog = new ProgressDialog(getActivity());

        mBluetoothGattCharacteristic = mApplication.getBluetoothGattCharacteristic();
        String characteristicUUID = mBluetoothGattCharacteristic.getUuid().toString();
        mCharacteristicName.setText(GattAttributes.lookupUUID(mBluetoothGattCharacteristic.getUuid(), Utils.getUuidShort(characteristicUUID)));

        mDescriptor = mApplication.getBluetoothGattDescriptor();
        String descriptorUUID = mDescriptor.getUuid().toString();
        mDescriptorName.setText(GattAttributes.lookupUUID(mDescriptor.getUuid(), Utils.getUuidShort(descriptorUUID)));

        mReadButton = rootView.findViewById(R.id.btn_read);
        mNotifyButton = rootView.findViewById(R.id.btn_write_notify);
        mIndicateButton = rootView.findViewById(R.id.btn_write_indicate);
        if (descriptorUUID.equalsIgnoreCase(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG)) {
            if (BluetoothLeService.isPropertySupported(mBluetoothGattCharacteristic, BluetoothGattCharacteristic.PROPERTY_NOTIFY)) {
                mNotifyButton.setVisibility(View.VISIBLE);
                mNotifyButton.setText(getResources().getString(R.string.gatt_services_notify));
            }
            if (BluetoothLeService.isPropertySupported(mBluetoothGattCharacteristic, BluetoothGattCharacteristic.PROPERTY_INDICATE)) {
                mIndicateButton.setVisibility(View.VISIBLE);
                mIndicateButton.setText(getResources().getString(R.string.gatt_services_indicate));
            }
        } else {
            mNotifyButton.setVisibility(View.GONE);
        }
        mStartNotifyText = getResources().getString(R.string.gatt_services_notify);
        mStopNotifyText = getResources().getString(R.string.gatt_services_stop_notify);
        mStartIndicateText = getResources().getString(R.string.gatt_services_indicate);
        mStopIndicateText = getResources().getString(R.string.gatt_services_stop_indicate);
        mReadButton.setOnClickListener(this);
        mNotifyButton.setOnClickListener(this);
        mIndicateButton.setOnClickListener(this);
        mBackBtn.setOnClickListener(this);
        if (mDescriptor != null) {
            BluetoothLeService.readDescriptor(mDescriptor);
        }
        return rootView;
    }

    private void displayDescriptorValue(String value) {
        mDescriptorValue.setText(value);
    }

    void displayHexValue(byte[] array) {
        String descriptorValue = Utils.byteArrayToHex(array);
        mHexValue.setText(descriptorValue);
    }

    @Override
    public void onResume() {
        super.onResume();
        mIsInFragment = true;
        BluetoothLeService.registerBroadcastReceiver(getActivity(), mGattUpdateReceiver, Utils.makeGattUpdateIntentFilter());
    }

    @Override
    public void onDestroy() {
        mIsInFragment = false;
        BluetoothLeService.unregisterBroadcastReceiver(getActivity(), mGattUpdateReceiver);
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_write_notify:
                Button btnNotify = (Button) view;
                String btnTextNotify = btnNotify.getText().toString();
                mGUIUpdateFlag = true;
                if (btnTextNotify.equalsIgnoreCase(mStartNotifyText)) {
                    prepareBroadcastDataNotify(mBluetoothGattCharacteristic);
                    GattDetailsFragment.mIsNotifyEnabled = true;
                    btnNotify.setText(mStopNotifyText);
                }
                if (btnTextNotify.equalsIgnoreCase(mStopNotifyText)) {
                    stopBroadcastDataNotify(mBluetoothGattCharacteristic);
                    GattDetailsFragment.mIsNotifyEnabled = false;
                    btnNotify.setText(mStartNotifyText);
                }
                break;
            case R.id.btn_write_indicate:
                Button btnIndicate = (Button) view;
                String btnTextIndicate = btnIndicate.getText().toString();
                mGUIUpdateFlag = true;
                if (btnTextIndicate.equalsIgnoreCase(mStartIndicateText)) {
                    prepareBroadcastDataIndicate(mBluetoothGattCharacteristic);
                    GattDetailsFragment.mIsIndicateEnabled = true;
                    btnIndicate.setText(mStopIndicateText);
                }
                if (btnTextIndicate.equalsIgnoreCase(mStopIndicateText)) {
                    stopBroadcastDataIndicate(mBluetoothGattCharacteristic);
                    GattDetailsFragment.mIsIndicateEnabled = false;
                    btnIndicate.setText(mStartIndicateText);
                }
                break;
            case R.id.btn_read:
                if (mDescriptor != null) {
                    BluetoothLeService.readDescriptor(mDescriptor);
                }
                break;
            case R.id.imgback:
                getActivity().onBackPressed();
                break;
        }
    }


    /**
     * Preparing Broadcast receiver to broadcast notify characteristics
     *
     * @param gattCharacteristic
     */
    void prepareBroadcastDataNotify(BluetoothGattCharacteristic gattCharacteristic) {
        if (BluetoothLeService.isPropertySupported(gattCharacteristic, BluetoothGattCharacteristic.PROPERTY_NOTIFY)) {
            BluetoothLeService.setCharacteristicNotification(gattCharacteristic, true);
        }
    }

    /**
     * Stopping Broadcast receiver to broadcast notify characteristics
     *
     * @param gattCharacteristic
     */
    void stopBroadcastDataNotify(BluetoothGattCharacteristic gattCharacteristic) {
        if (BluetoothLeService.isPropertySupported(gattCharacteristic, BluetoothGattCharacteristic.PROPERTY_NOTIFY)) {
            BluetoothLeService.setCharacteristicNotification(gattCharacteristic, false);
        }
    }

    /**
     * Preparing Broadcast receiver to broadcast indicate characteristics
     *
     * @param gattCharacteristic
     */
    void prepareBroadcastDataIndicate(BluetoothGattCharacteristic gattCharacteristic) {
        if (BluetoothLeService.isPropertySupported(gattCharacteristic, BluetoothGattCharacteristic.PROPERTY_INDICATE)) {
            BluetoothLeService.setCharacteristicIndication(gattCharacteristic, true);
        }
    }

    /**
     * Stopping Broadcast receiver to broadcast indicate characteristics
     *
     * @param gattCharacteristic
     */
    void stopBroadcastDataIndicate(BluetoothGattCharacteristic gattCharacteristic) {
        if (BluetoothLeService.isPropertySupported(gattCharacteristic, BluetoothGattCharacteristic.PROPERTY_INDICATE)) {
            BluetoothLeService.setCharacteristicIndication(gattCharacteristic, false);
        }
    }
}
