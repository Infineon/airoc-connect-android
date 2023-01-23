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
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.infineon.airocbluetoothconnect.BLEConnectionServices.BluetoothLeService;
import com.infineon.airocbluetoothconnect.CommonUtils.Constants;
import com.infineon.airocbluetoothconnect.CommonUtils.CustomSpinner;
import com.infineon.airocbluetoothconnect.CommonUtils.GattAttributes;
import com.infineon.airocbluetoothconnect.CommonUtils.Logger;
import com.infineon.airocbluetoothconnect.CommonUtils.Utils;
import com.infineon.airocbluetoothconnect.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FindMeService extends Fragment {

    private ImageView mTxPower;
    private TextView mTxPowerValue;
    private static String mFragmentTitle;
    private View mRootView;
    /**
     * Flag to handle the handler
     */
    private boolean mHandlerFlag = true;

    //ProgressDialog
    private ProgressDialog mProgressDialog;

    // GATT service and characteristic
    private static BluetoothGattService mCurrentService;
    private static ArrayList<HashMap<String, BluetoothGattService>> mServiceData;
    private static BluetoothGattCharacteristic mReadCharacteristicTxPower;

    // Immediate alert constants
    private static final String IMM_NO_ALERT = "0x00";
    private static final String IMM_MILD_ALERT = "0x01";
    private static final String IMM_HIGH_ALERT = "0x02";

    // Immediate alert text
    private static final String IMM_NO_ALERT_TEXT = " No Alert ";
    private static final String IMM_MILD_ALERT_TEXT = " Mild Alert ";
    private static final String IMM_HIGH_ALERT_TEXT = " High Alert ";

    //Selected spinner position
    private int mSelectedLinkLossPosition = 3;
    private int mSelectedImmediateAlertPosition = 3;

    private CustomSpinner mSpinnerLinkLoss;
    private CustomSpinner mSpinnerImmediateAlert;

    //Constants
    private static final int TX_POWER_DELAY = 500;

    /**
     * BroadcastReceiver for receiving the GATT server status
     */
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Bundle extras = intent.getExtras();
            // GATT Data available
            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                // Check power value
                if (extras.containsKey(Constants.EXTRA_POWER_VALUE)) {
                    int received_pwr_data = intent.getIntExtra(
                            Constants.EXTRA_POWER_VALUE, 246);
                    Handler handler = new Handler();
                    Runnable mrun = new Runnable() {

                        @Override
                        public void run() {
                            if (mHandlerFlag) {
                                prepareBroadcastDataReadTxPower(mReadCharacteristicTxPower);
                            }

                        }
                    };
                    handler.postDelayed(mrun, TX_POWER_DELAY);
                    if (received_pwr_data != 246) {
                        float value = received_pwr_data;
                        float flval = (float) 1 / 120;
                        float scaleVal = (value + 100) * flval;
                        Logger.i("scaleVal " + scaleVal);
                        mTxPower.animate().setDuration(TX_POWER_DELAY)
                                .scaleX(scaleVal);
                        mTxPower.animate().setDuration(TX_POWER_DELAY)
                                .scaleY(scaleVal);
                        mTxPowerValue.setText(String
                                .valueOf(received_pwr_data));

                    }

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

    public static FindMeService create(BluetoothGattService currentService, ArrayList<HashMap<String, BluetoothGattService>> serviceData, String fragmentTitle) {
        mCurrentService = currentService;
        mServiceData = serviceData;
        mFragmentTitle = fragmentTitle;
        Logger.i("mFragmentTitle-->" + mFragmentTitle);
        return new FindMeService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.profile_findme, container, false);
        mProgressDialog = new ProgressDialog(getActivity());
        setHasOptionsMenu(true);
        return mRootView;
    }

    /**
     * Prepare Broadcast receiver to broadcast read characteristics Transmission
     * power
     *
     * @param gattCharacteristic
     */
    void prepareBroadcastDataReadTxPower(BluetoothGattCharacteristic gattCharacteristic) {
        if (BluetoothLeService.isPropertySupported(gattCharacteristic, BluetoothGattCharacteristic.PROPERTY_READ)) {
            BluetoothLeService.readCharacteristic(gattCharacteristic);
        }
    }

    @Override
    public void onResume() {
        getGattData();
        updateSpinners();
        mHandlerFlag = true;
        BluetoothLeService.registerBroadcastReceiver(getActivity(), mGattUpdateReceiver, Utils.makeGattUpdateIntentFilter());
        Utils.setUpActionBar((AppCompatActivity) getActivity(), mFragmentTitle);
        super.onResume();
    }

    private void updateSpinners() {
        Logger.i("mSelectedLinkLossPosition-->" + mSelectedLinkLossPosition);
        if (mSpinnerImmediateAlert != null) {
            if (mSelectedImmediateAlertPosition != 3)
                mSpinnerImmediateAlert.setSelection(mSelectedImmediateAlertPosition);
        }

        if (mSpinnerLinkLoss != null) {
            if (mSelectedLinkLossPosition != 3)
                mSpinnerLinkLoss.setSelection(mSelectedLinkLossPosition);
        }
    }

    /**
     * Method to get required characteristics from service
     */
    private void getGattData() {
        LinearLayout ll_layout = (LinearLayout) mRootView
                .findViewById(R.id.linkloss_layout);
        LinearLayout im_layout = (LinearLayout) mRootView
                .findViewById(R.id.immalert_layout);
        LinearLayout tp_layout = (LinearLayout) mRootView
                .findViewById(R.id.transmission_layout);
        RelativeLayout tpr_layout = (RelativeLayout) mRootView
                .findViewById(R.id.transmission_rel_layout);

        for (int position = 0; position < mServiceData.size(); position++) {
            HashMap<String, BluetoothGattService> item = mServiceData
                    .get(position);
            BluetoothGattService bgs = item.get("UUID");
            List<BluetoothGattCharacteristic> gattCharacteristicsCurrent = bgs
                    .getCharacteristics();
            for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristicsCurrent) {
                String uuidchara = gattCharacteristic.getUuid().toString();
                if (uuidchara.equalsIgnoreCase(GattAttributes.ALERT_LEVEL)) {
                    if (bgs.getUuid().toString()
                            .equalsIgnoreCase(GattAttributes.LINK_LOSS_SERVICE)) {
                        ll_layout.setVisibility(View.VISIBLE);
                        mSpinnerLinkLoss = (CustomSpinner) mRootView
                                .findViewById(R.id.linkloss_spinner);
                        // Create an ArrayAdapter using the string array and a
                        // default
                        // spinner layout
                        ArrayAdapter<CharSequence> adapter_linkloss = ArrayAdapter
                                .createFromResource(getActivity(),
                                        R.array.findme_immediate_alert_array,
                                        android.R.layout.simple_spinner_item);
                        // Specify the layout to use when the list of choices
                        // appears
                        adapter_linkloss
                                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        // Apply the adapter to the spinner
                        mSpinnerLinkLoss.setAdapter(adapter_linkloss);
                        mSpinnerLinkLoss
                                .setOnItemSelectedListener(new OnItemSelectedListener() {

                                    @Override
                                    public void onItemSelected(
                                            AdapterView<?> parent, View view,
                                            int position, long id) {

                                        if (parent.getItemAtPosition(position)
                                                .toString()
                                                .equalsIgnoreCase(getResources().getString(R.string.find_me_no_alert))) {
                                            byte[] convertedBytes = convertingTobyteArray(
                                                    IMM_NO_ALERT);
                                            BluetoothLeService
                                                    .writeCharacteristicNoResponse(
                                                            gattCharacteristic,
                                                            convertedBytes);
                                            Toast.makeText(
                                                    getActivity(),
                                                    getResources().getString(R.string.find_value_written_toast)
                                                            + IMM_NO_ALERT_TEXT
                                                            + getResources().getString(R.string.find_value_success_toast),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                        if (parent.getItemAtPosition(position)
                                                .toString()
                                                .equalsIgnoreCase(getResources().getString(R.string.find_me_mild_alert))) {
                                            byte[] convertedBytes = convertingTobyteArray(
                                                    IMM_MILD_ALERT);
                                            BluetoothLeService
                                                    .writeCharacteristicNoResponse(
                                                            gattCharacteristic,
                                                            convertedBytes);
                                            Toast.makeText(
                                                    getActivity(),
                                                    getResources().getString(R.string.find_value_written_toast)
                                                            + IMM_MILD_ALERT_TEXT
                                                            + getResources().getString(R.string.find_value_success_toast),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                        if (parent.getItemAtPosition(position)
                                                .toString()
                                                .equalsIgnoreCase(getResources().getString(R.string.find_me_high_alert))) {
                                            byte[] convertedBytes = convertingTobyteArray(
                                                    IMM_HIGH_ALERT);
                                            BluetoothLeService
                                                    .writeCharacteristicNoResponse(
                                                            gattCharacteristic,
                                                            convertedBytes);
                                            Toast.makeText(
                                                    getActivity(),
                                                    getResources().getString(R.string.find_value_written_toast)
                                                            + IMM_HIGH_ALERT_TEXT
                                                            + getResources().getString(R.string.find_value_success_toast),
                                                    Toast.LENGTH_SHORT).show();
                                        }

                                    }

                                    @Override
                                    public void onNothingSelected(
                                            AdapterView<?> parent) {
                                        // TODO Auto-generated method stub

                                    }
                                });
                    }
                    if (bgs.getUuid()
                            .toString()
                            .equalsIgnoreCase(
                                    GattAttributes.IMMEDIATE_ALERT_SERVICE)) {
                        im_layout.setVisibility(View.VISIBLE);
                        mSpinnerImmediateAlert = (CustomSpinner) mRootView
                                .findViewById(R.id.immediate_spinner);
                        // Create an ArrayAdapter using the string array and a
                        // default
                        // spinner layout
                        ArrayAdapter<CharSequence> adapter_immediate_alert = ArrayAdapter
                                .createFromResource(getActivity(),
                                        R.array.findme_immediate_alert_array,
                                        android.R.layout.simple_spinner_item);
                        // Specify the layout to use when the list of choices
                        // appears
                        adapter_immediate_alert
                                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        // Apply the adapter to the spinner
                        mSpinnerImmediateAlert
                                .setAdapter(adapter_immediate_alert);
                        mSpinnerImmediateAlert
                                .setOnItemSelectedListener(new OnItemSelectedListener() {

                                    @Override
                                    public void onItemSelected(
                                            AdapterView<?> parent, View view,
                                            int position, long id) {

                                        if (parent.getItemAtPosition(position)
                                                .toString()
                                                .equalsIgnoreCase(getResources().getString(R.string.find_me_no_alert))) {
                                            byte[] convertedBytes = convertingTobyteArray(
                                                    IMM_NO_ALERT);
                                            BluetoothLeService
                                                    .writeCharacteristicNoResponse(
                                                            gattCharacteristic,
                                                            convertedBytes);
                                            Toast.makeText(
                                                    getActivity(),
                                                    getResources().getString(R.string.find_value_written_toast)
                                                            + IMM_NO_ALERT_TEXT
                                                            + getResources().getString(R.string.find_value_success_toast),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                        if (parent.getItemAtPosition(position)
                                                .toString()
                                                .equalsIgnoreCase(getResources().getString(R.string.find_me_mild_alert))) {
                                            byte[] convertedBytes = convertingTobyteArray(
                                                    IMM_MILD_ALERT);
                                            BluetoothLeService
                                                    .writeCharacteristicNoResponse(
                                                            gattCharacteristic,
                                                            convertedBytes);
                                            Toast.makeText(
                                                    getActivity(),
                                                    getResources().getString(R.string.find_value_written_toast)
                                                            + IMM_MILD_ALERT_TEXT
                                                            + getResources().getString(R.string.find_value_success_toast),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                        if (parent.getItemAtPosition(position)
                                                .toString()
                                                .equalsIgnoreCase(getResources().getString(R.string.find_me_high_alert))) {
                                            byte[] convertedBytes = convertingTobyteArray(
                                                    IMM_HIGH_ALERT);
                                            BluetoothLeService
                                                    .writeCharacteristicNoResponse(
                                                            gattCharacteristic,
                                                            convertedBytes);
                                            Toast.makeText(
                                                    getActivity(),
                                                    getResources().getString(R.string.find_value_written_toast)
                                                            + IMM_HIGH_ALERT_TEXT
                                                            + getResources().getString(R.string.find_value_success_toast),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(
                                            AdapterView<?> parent) {
                                        // TODO Auto-generated method stub
                                    }
                                });
                    }

                }
                if (uuidchara.equalsIgnoreCase(GattAttributes.TX_POWER_LEVEL)) {
                    tp_layout.setVisibility(View.VISIBLE);
                    tpr_layout.setVisibility(View.VISIBLE);
                    mReadCharacteristicTxPower = gattCharacteristic;
                    mTxPower = (ImageView) mRootView
                            .findViewById(R.id.findme_tx_power_img);
                    mTxPowerValue = (TextView) mRootView
                            .findViewById(R.id.findme_tx_power_txt);
                    if (mReadCharacteristicTxPower != null) {
                        prepareBroadcastDataReadTxPower(mReadCharacteristicTxPower);
                    }
                }
            }
        }
    }

    @Override
    public void onPause() {
        mHandlerFlag = false;
        if (mSpinnerImmediateAlert != null)
            mSelectedImmediateAlertPosition = mSpinnerImmediateAlert.getSelectedItemPosition();
        if (mSpinnerLinkLoss != null)
            mSelectedLinkLossPosition = mSpinnerLinkLoss.getSelectedItemPosition();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        // mReadCharacteristic_ll = null;
        mReadCharacteristicTxPower = null;
        BluetoothLeService.unregisterBroadcastReceiver(getActivity(), mGattUpdateReceiver);
        super.onDestroy();
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

    /**
     * Method to convert hex to byteArray
     */
    private byte[] convertingTobyteArray(String result) {
        String[] splited = result.split("\\s+");
        byte[] valueByte = new byte[splited.length];
        for (int i = 0; i < splited.length; i++) {
            if (splited[i].length() > 2) {
                String trimmedByte = splited[i].split("x")[1];
                valueByte[i] = (byte) convertstringtobyte(trimmedByte);
            }

        }
        return valueByte;
    }

    /**
     * Convert the string to byte
     *
     * @param string
     * @return
     */
    private int convertstringtobyte(String string) {
        return Integer.parseInt(string, 16);
    }

}
