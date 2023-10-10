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

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.infineon.airocbluetoothconnect.BLEConnectionServices.BluetoothLeService;
import com.infineon.airocbluetoothconnect.CommonUtils.Constants;
import com.infineon.airocbluetoothconnect.CommonUtils.Logger;
import com.infineon.airocbluetoothconnect.CommonUtils.ToastUtils;
import com.infineon.airocbluetoothconnect.CommonUtils.Utils;
import com.infineon.airocbluetoothconnect.R;
import com.google.android.material.tabs.TabLayout;

public class HomePageTabbedFragment extends FragmentWithPermissionCheck {

    // Activity request constant
    private static final int REQUEST_ENABLE_BT = 1;

    //Bluetooth adapter
    protected static BluetoothAdapter mBluetoothAdapter;

    //  Flags
    public static boolean mIsInFragment = false;

    private TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Utils.debug("HPTF: lifecycle: onCreateView", this, getActivity());

        checkBleSupportAndInitialize();

        View rootView = inflater.inflate(R.layout.fragment_home_page_tabbed, container, false);

        // Give the TabLayout the ViewPager
        tabLayout = rootView.findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        getChildFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, new ProfileScanningFragment(), Constants.PROFILE_SCANNING_FRAGMENT_TAG)
                                .commit();
                        break;
                    case 1:
                        getChildFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, new PairedProfilesFragment(), Constants.PAIRED_PROFILES_FRAGMENT_TAG)
                                .commit();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // Trick part 1.
        TabLayout.Tab tab = tabLayout.getTabAt(1);
        tab.select();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Utils.debug("HPTF: lifecycle: onStart", this, getActivity());

        // Trick part 2. The intent is to kick off the OnTabSelectedListener.onTabSelected(0) method
        TabLayout.Tab tab = tabLayout.getTabAt(0);
        tab.select();
    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.debug("HPTF: lifecycle: onResume", this, getActivity());
        Utils.setUpActionBar((AppCompatActivity) getActivity(), R.string.profile_scan_fragment);
        checkBluetoothStatus();
        mIsInFragment = true;
    }

    @Override
    public void onPause() {
        Utils.debug("HPTF: lifecycle: onPause", this, getActivity());
        mIsInFragment = false;
        super.onPause();
    }

    @Override
    public void onStop() {
        Utils.debug("HPTF: lifecycle: onStop", this, getActivity());
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Utils.debug("HPTF: lifecycle: onDestroy", this, getActivity());
        super.onDestroy();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Fragment currentFragment = getChildFragmentManager().findFragmentById(R.id.container);
        if (currentFragment != null) {
            currentFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {
                ToastUtils.makeText(R.string.device_bluetooth_on, Toast.LENGTH_SHORT);
                if (Utils.getBooleanSharedPreference(getActivity(), Constants.PREF_UNPAIR_ON_DISCONNECT)) {
                    boolean unpaired = false;
                    try {
                        unpaired = BluetoothLeService.unpairDevice(BluetoothLeService.getRemoteDevice());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String status = "HPTF: pair: unpair status for device " + BluetoothLeService.getBluetoothDeviceAddress() + " after BT OFF-ON cycle: " + unpaired;
                    if (unpaired) {
                        Logger.v(status);
                    } else {
                        Logger.e(status);
                    }
                }
                Fragment currentFragment = getChildFragmentManager().findFragmentById(R.id.container);
                if (currentFragment instanceof ProfileScanningFragment) {
                    ProfileScanningFragment psf = (ProfileScanningFragment) currentFragment;
                    psf.prepareDeviceList();
                } else if (currentFragment instanceof PairedProfilesFragment) {
                    PairedProfilesFragment ppf = (PairedProfilesFragment) currentFragment;
                    ppf.prepareDeviceList();
                }
            }
            // User chose not to enable Bluetooth.
            else {
                Logger.e("User chose not to enable Bluetooth");
                getActivity().finish();
            }
        }
    }

    private void checkBleSupportAndInitialize() {
        // Use this check to determine whether BLE is supported on the device.
        if (false == getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            ToastUtils.makeText(R.string.device_ble_not_supported, Toast.LENGTH_LONG);
        }
        // Initializes a Bluetooth adapter.
        final BluetoothManager bluetoothManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Logger.e("Device does not support Bluetooth");
            ToastUtils.makeText(R.string.device_bluetooth_not_supported, Toast.LENGTH_LONG);
        }
    }

    private boolean checkBluetoothStatus() {
        /**
         * Ensures Bluetooth is enabled on the device. If Bluetooth is not
         * currently enabled, fire an intent to display a dialog asking the user
         * to grant permission to enable it.
         */
        if (permissionManager.isBluetoothPermissionGranted()) {
            if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                return false;
            }
        }
        return true;
    }
}
