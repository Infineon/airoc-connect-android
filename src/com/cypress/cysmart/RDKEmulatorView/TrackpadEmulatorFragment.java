/*
 * (c) 2014-2020, Cypress Semiconductor Corporation or a subsidiary of 
 * Cypress Semiconductor Corporation.  All rights reserved.
 * 
 * This software, including source code, documentation and related 
 * materials ("Software"),  is owned by Cypress Semiconductor Corporation 
 * or one of its subsidiaries ("Cypress") and is protected by and subject to 
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

package com.cypress.cysmart.RDKEmulatorView;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cypress.cysmart.BLEConnectionServices.BluetoothLeService;
import com.cypress.cysmart.CommonUtils.Constants;
import com.cypress.cysmart.CommonUtils.Logger;
import com.cypress.cysmart.CommonUtils.Utils;
import com.cypress.cysmart.R;

/**
 * Fragment class to show the emulator view of the Remote control RDK which has Human Interface
 * Device sservice
 */
public class TrackpadEmulatorFragment extends Fragment {


    //Switch case constants
    private static final int CASE_KEYBOARD_LEFT_CTRL = 0;
    private static final int CASE_KEYBOARD_LEFT_SHIFT = 1;
    private static final int CASE_KEYBOARD_LEFT_ALT = 2;
    private static final int CASE_KEYBOARD_LEFT_GUI = 3;
    private static final int CASE_KEYBOARD_RIGHT_CTRL = 4;
    private static final int CASE_KEYBOARD_RIGHT_SHIFT = 5;
    private static final int CASE_KEYBOARD_RIGHT_ALT = 6;
    private static final int CASE_KEYBOARD_RIGHT_GUI = 7;
    private static final int CASE_POWER = 101;
    private static final int CASE_VOLUME_PLUS = 102;
    private static final int CASE_VOLUME_MINUS = 103;
    private static final int CASE_CHANNEL_PLUS = 104;
    private static final int CASE_CHANNEL_MINUS = 105;
    private static final int CASE_SOURCE = 110;
    int mMouseZZValue = 0;
    int mouseTiltValue = 0;
    //UI Elements
    private TextView mXValue;
    private TextView mYValue;
    private TextView mZValue;
    private TextView mTiltValue;
    private TextView mLeftDownValue;
    private TextView mLeftUpValue;
    private TextView mRightDownValue;
    private TextView mRightUpValue;
    private TextView mKeycodeValue;
    //Temporary Variables
    private int mLefty = 0;
    private int mRighty = 0;
    private int mLeftyUp = 0;
    private int mRightyUp = 0;
    private boolean mLeftClicked = false;
    private boolean mRightClicked = false;
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
                /**
                 * Byte information send through BLE received here
                 */
                if (extras.containsKey(Constants.EXTRA_BYTE_VALUE)) {
                    byte[] array = intent
                            .getByteArrayExtra(Constants.EXTRA_BYTE_VALUE);
                    /**
                     * Report reference descriptor received
                     */
                    if (extras.containsKey(Constants.EXTRA_DESCRIPTOR_REPORT_REFERENCE_ID)) {
                        String reportReference = intent.getStringExtra
                                (Constants.EXTRA_DESCRIPTOR_REPORT_REFERENCE_ID);
                        /**
                         * Mouse report reference data received
                         */
                        if (reportReference.equalsIgnoreCase(ReportAttributes.
                                MOUSE_REPORT_REFERENCE_STRING)) {
                            /**
                             * Update the value in the UI
                             */
                            displayData(array);
                        }
                        /**
                         * Keyboard report reference data received
                         */
                        else if (reportReference.equalsIgnoreCase(ReportAttributes.
                                KEYBOARD_REPORT_REFERENCE_STRING)) {
                            Logger.e("KEYBOARD_KEYCODE");
                            displayKeycode(array);
                        } else {
                            /**
                             * Converting the received voice data to HEX value
                             * Update the value in the UI
                             */
                            String hexValue = getHexValue(array);
                            updateKeyCodeValues(hexValue);
                        }
                    }
                }
            }
        }

    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.rdk_motion_sensor, container,
                false);
        /**
         * Getting the ID'S of the GUI elements
         */
        mXValue = (TextView) rootView.findViewById(R.id.x_value);
        mYValue = (TextView) rootView.findViewById(R.id.y_value);
        mZValue = (TextView) rootView.findViewById(R.id.z_wheel_value);
        mTiltValue = (TextView) rootView.findViewById(R.id.tilt_value);
        mLeftDownValue = (TextView) rootView.findViewById(R.id.left_click_down_value);
        mLeftUpValue = (TextView) rootView.findViewById(R.id.left_click_upp_value);
        mRightDownValue = (TextView) rootView.findViewById(R.id.right_click_down_value);
        mRightUpValue = (TextView) rootView.findViewById(R.id.right_click_up_value);
        mKeycodeValue = (TextView) rootView.findViewById(R.id.keycode_value);
        Button mClearCountersBtn = (Button) rootView.findViewById(R.id.clear_counters);
        /**
         * Clear counters button click listner
         * Replacing all value in the GUI with 00
         */
        mClearCountersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLeftDownValue.setText("00");
                mLeftUpValue.setText("00");
                mRightDownValue.setText("00");
                mRightUpValue.setText("00");
                mKeycodeValue.setText("00");
                mZValue.setText("00");
                mTiltValue.setText("00");
                mXValue.setText("00");
                mYValue.setText("00");
                mLefty = 0;
                mRighty = 0;
                mLeftyUp = 0;
                mRightyUp = 0;
                mouseTiltValue = 0;
                mMouseZZValue = 0;
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        BluetoothLeService.registerBroadcastReceiver(getActivity(), mGattUpdateReceiver, Utils.makeGattUpdateIntentFilter());
        Utils.setUpActionBar((AppCompatActivity) getActivity(), R.string.rdk_emulator_view);
    }

    @Override
    public void onDestroy() {
        BluetoothLeService.unregisterBroadcastReceiver(getActivity(), mGattUpdateReceiver);
        super.onDestroy();
    }

    /**
     * Method to get the Hex value from bytes
     *
     * @param bytes
     * @return String
     */
    private String getHexValueByte(byte bytes) {
        StringBuffer sb = new StringBuffer();
        sb.append(Utils.formatForRootLocale("%02x", bytes));
        return "" + sb;
    }

    /**
     * Method to update the GUI page with received values through BLE
     *
     * @param trackpadValues
     */
    private void displayData(byte[] trackpadValues) {
        String mouse_XX_value = getHexValueByte(trackpadValues[1]);
        mXValue.setText("" + trackpadValues[1]);
        String mouse_YY_value = getHexValueByte(trackpadValues[2]);
        mYValue.setText("" + trackpadValues[2]);
        int mouse_ZZ_value_temp = trackpadValues[3];
        mMouseZZValue = mouse_ZZ_value_temp + mMouseZZValue ;
        mZValue.setText("" + mMouseZZValue);
        int mouse_tilt_value_temp = trackpadValues[4];
        mouseTiltValue = mouse_tilt_value_temp + mouseTiltValue;
        mTiltValue.setText("" + mouseTiltValue);
        if (trackpadValues[0] == 1) {
            if(!mLeftClicked){
                mLefty++;
                mLeftDownValue.setText("" + mLefty);
                mLeftClicked = true;
                mRightClicked = false;
            }
        }
       else if (trackpadValues[0] == 2) {
            if(!mRightClicked){
                mRighty++;
                mRightDownValue.setText("" + mRighty);
                mRightClicked = true;
                mLeftClicked = false;
            }
        }
       else if (trackpadValues[0] == 0) {
            if (mLeftClicked) {
                mLeftyUp++;
                mLeftUpValue.setText("" + mLeftyUp);
                mLeftClicked = false;
            } else if (mRightClicked) {
                mRightyUp++;
                mRightUpValue.setText("" + mRightyUp);
                mRightClicked = false;
            }
        }
    }

    private void displayKeycode(byte[] keycodeReceived) {
        StringBuilder keyCodeStringBuilder = new StringBuilder();
        for (int pos = 0; pos < keycodeReceived.length; pos++) {
            int bytevalue = keycodeReceived[pos];
            if (pos == 0) {
                for (int count = 0; count < 8; count++) {
                    if (isSet((byte) bytevalue, count)) {
                        keyCodeStringBuilder.append(getModifierValue(count));
                    }
                }
            } else {
                if (bytevalue != 0) {
                    String value = KeyBoardAttributes.lookupKeycodeDescription(bytevalue & 0xFF);
                    keyCodeStringBuilder.append(value);
                }
            }
        }

        if (keyCodeStringBuilder.toString() != "") {
            mKeycodeValue.setText(keyCodeStringBuilder);
        }
    }

    private String getModifierValue(int count) {
        String modifier = "";
        switch (count) {
            case CASE_KEYBOARD_LEFT_CTRL:
                modifier = getActivity().getResources().getString(R.string.key_left_ctrl);
                break;
            case CASE_KEYBOARD_LEFT_SHIFT:
                modifier = getActivity().getResources().getString(R.string.key_left_shift);
                break;
            case CASE_KEYBOARD_LEFT_ALT:
                modifier = getActivity().getResources().getString(R.string.key_left_alt);
                break;
            case CASE_KEYBOARD_LEFT_GUI:
                modifier = getActivity().getResources().getString(R.string.key_left_gui);
                break;
            case CASE_KEYBOARD_RIGHT_CTRL:
                modifier = getActivity().getResources().getString(R.string.key_right_ctrl);
                break;
            case CASE_KEYBOARD_RIGHT_SHIFT:
                modifier = getActivity().getResources().getString(R.string.key_right_shift);
                break;
            case CASE_KEYBOARD_RIGHT_ALT:
                modifier = getActivity().getResources().getString(R.string.key_right_alt);
                break;
            case CASE_KEYBOARD_RIGHT_GUI:
                modifier = getActivity().getResources().getString(R.string.key_right_gui);
                break;
            default:
                break;
        }
        return modifier;
    }


    /**
     * Method to update the GUI with the corresponding report received
     *
     * @param buttonValue
     */

    private void updateKeyCodeValues(String buttonValue) {
        int value = ReportAttributes.lookupReportValues(buttonValue);
        switch (value) {
            case CASE_POWER:
                mKeycodeValue.setText(getResources().getString(R.string.power_on));
                break;
            case CASE_VOLUME_PLUS:
                mKeycodeValue.setText(getResources().getString(R.string.volume_up));
                break;
            case CASE_VOLUME_MINUS:
                mKeycodeValue.setText(getResources().getString(R.string.volume_down));
                break;
            case CASE_CHANNEL_PLUS:
                mKeycodeValue.setText(getResources().getString(R.string.channel_up));
                break;
            case CASE_CHANNEL_MINUS:
                mKeycodeValue.setText(getResources().getString(R.string.channel_down));
                break;
            case CASE_SOURCE:
                mKeycodeValue.setText(getResources().getString(R.string.source));
                break;
            default:
                break;
        }
    }

    private String getHexValue(byte[] array) {
        StringBuffer sb = new StringBuffer();
        for (byte byteChar : array) {
            sb.append(Utils.formatForRootLocale("%02x", byteChar));
        }
        return "" + sb;
    }
    // tests if bit is set in value
    boolean isSet(byte value, int bit) {
        return (value & (1 << bit)) != 0;
    }
}

