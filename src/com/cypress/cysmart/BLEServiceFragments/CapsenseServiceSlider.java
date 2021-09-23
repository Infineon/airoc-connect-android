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

package com.cypress.cysmart.BLEServiceFragments;

import android.app.ProgressDialog;
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
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cypress.cysmart.BLEConnectionServices.BluetoothLeService;
import com.cypress.cysmart.CommonUtils.Constants;
import com.cypress.cysmart.CommonUtils.Utils;
import com.cypress.cysmart.R;

/**
 * Fragment to display the CapSense Slider
 */
public class CapsenseServiceSlider extends Fragment {

    // GATT Service and characteristics
    private static BluetoothGattService mService;
    public static BluetoothGattCharacteristic mReadCharacteristic;

    // Data variables
    private ImageView mSlider;
    private LinearLayout mSliderLayout;

    //Progress Dialog
    private ProgressDialog mProgressDialog;
    private ImageView mFocusImage;

    /**
     * BroadcastReceiver for receiving the GATT server status
     */
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            // GATT Data available
            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                Bundle extras = intent.getExtras();
                // Check CapSense slider
                if (extras.containsKey(Constants.EXTRA_CAPSLIDER_VALUE)) {
                    int received_slider_rate = extras
                            .getInt(Constants.EXTRA_CAPSLIDER_VALUE);
                    if (received_slider_rate == 255) {
                        //displayLiveData(0);
                        mFocusImage.setVisibility(View.VISIBLE);
                    } else {
                        mFocusImage.setVisibility(View.INVISIBLE);
                        displayLiveData(received_slider_rate);
                    }
                }
            }
        }
    };

    public static CapsenseServiceSlider create(BluetoothGattService service) {
        CapsenseServiceSlider fragment = new CapsenseServiceSlider();
        mService = service;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.capsense_slider, container,
                false);
        mSlider = (ImageView) rootView.findViewById(R.id.slider_view_1);
        mProgressDialog = new ProgressDialog(getActivity());
        mSliderLayout = (LinearLayout) rootView
                .findViewById(R.id.slider_view_2);
        mFocusImage = (ImageView) rootView
                .findViewById(R.id.focus_view);
        mFocusImage.setVisibility(View.VISIBLE);
        setHasOptionsMenu(true);
        return rootView;
    }

    /**
     * Display the slider value
     *
     * @param sliding_data
     */
    private void displayLiveData(int sliding_data) {
        int slided;
        slided = 100 - sliding_data;
        mSlider.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT, sliding_data));
        mSliderLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT, slided));

    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.debug("CapSense Slider: lifecycle: onResume", this, getActivity());
        BluetoothLeService.registerBroadcastReceiver(getActivity(), mGattUpdateReceiver, Utils.makeGattUpdateIntentFilter());
    }

    @Override
    public void onDestroy() {
        Utils.debug("CapSense Slider: lifecycle: onDestroy", this, getActivity());
        BluetoothLeService.unregisterBroadcastReceiver(getActivity(), mGattUpdateReceiver);
        super.onDestroy();
    }
}
