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

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.infineon.airocbluetoothconnect.AIROCBluetoothConnectApp;
import com.infineon.airocbluetoothconnect.BLEConnectionServices.BluetoothLeService;
import com.infineon.airocbluetoothconnect.CommonUtils.Constants;
import com.infineon.airocbluetoothconnect.CommonUtils.GattAttributes;
import com.infineon.airocbluetoothconnect.CommonUtils.GattDbParser;
import com.infineon.airocbluetoothconnect.CommonUtils.Logger;
import com.infineon.airocbluetoothconnect.CommonUtils.ToastUtils;
import com.infineon.airocbluetoothconnect.CommonUtils.UUIDDatabase;
import com.infineon.airocbluetoothconnect.CommonUtils.Utils;
import com.infineon.airocbluetoothconnect.DataModelClasses.PairOnConnect;
import com.infineon.airocbluetoothconnect.HomePageActivity;
import com.infineon.airocbluetoothconnect.ListAdapters.CarouselPagerAdapter;
import com.infineon.airocbluetoothconnect.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Owns Carousel and reacts to BluetoothLeService events
 */
public class ProfileControlFragment extends Fragment {
    private AIROCBluetoothConnectApp mApplication;

    // Carousel fields. TODO: move to separate class
    public static final float BIG_SCALE = 1.0f;
    public static final float SMALL_SCALE = 0.7f;
    public static final float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;
    public static int LOOPS = 100;
    public static int mPages = 0;
    public static int FIRST_PAGE = mPages * LOOPS / 2;
    public ViewPager mPager; // ViewPager for CarouselView
    private CarouselPagerAdapter mAdapter; // Adapter for loading data to CarouselView
    private int mWidth = 0;
    public static boolean mIsInFragment = false;

    // Pairing fields. TODO: move to separate class
    public static final int PAIR_DELAY_MILLIS = 500;
    public static final int PAIRING_NO_BONDING_PROGRESS_DIALOG_TIME_OUT_MILLIS = 6000; // less than 6 seconds doesn't work for Nexus 5
    private boolean mFirstTime = false;
    private boolean mPairOnConnectStatusReceiverRegistered = false;
    private BluetoothGattCharacteristic mServiceChangedCharacteristic;
    private BluetoothGattDescriptor mServiceChangedCCCD;
    private final BroadcastReceiver mBtServiceEventsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                Bundle extras = intent.getExtras();
                if (extras.containsKey(Constants.EXTRA_DESCRIPTOR_BYTE_VALUE_UUID) && extras.containsKey(Constants.EXTRA_DESCRIPTOR_BYTE_VALUE_CHARACTERISTIC_UUID)) {

                    String descriptorUUID = extras.getString(Constants.EXTRA_DESCRIPTOR_BYTE_VALUE_UUID);
                    String characteristicUUID = extras.getString(Constants.EXTRA_DESCRIPTOR_BYTE_VALUE_CHARACTERISTIC_UUID);
                    if (GattAttributes.CLIENT_CHARACTERISTIC_CONFIG.equalsIgnoreCase(descriptorUUID) && GattAttributes.SERVICE_CHANGED.equalsIgnoreCase(characteristicUUID)) {

                        Logger.d("PCF: pair: onDescriptorRead(CCCD): SUCCESS");
                        if (mServiceChangedCharacteristic != null) {
                            BluetoothLeService.setCharacteristicIndication(mServiceChangedCharacteristic, true);
                        }
                    }
                }
            } else if (BluetoothLeService.ACTION_GATT_INSUFFICIENT_ENCRYPTION.equals(action)) { // this event is not being thrown for Samsung S7
                Logger.d("PCF: pair: onDescriptorWrite(CCCD): BluetoothLeService.ACTION_GATT_INSUFFICIENT_ENCRYPTION");
                // It is necessary to set characteristic indication for the 2nd time to kick off pairing
                if (mServiceChangedCharacteristic != null) {
                    BluetoothLeService.setCharacteristicIndication(mServiceChangedCharacteristic, true);
                    /*
                     * 1. For the case of pairing with bonding the ACTION_BOND_STATE_CHANGED event is being fired.
                     * As a result of ACTION_BOND_STATE_CHANGED event the HomePageActivity.mProgressDialog is being shown.
                     * Hence there is no sense to showToast ProfileControlFragment.mProgressDialog here.
                     *
                     * 2. For the case of pairing without bonding the ACTION_BOND_STATE_CHANGED event is not being fired.
                     * As a result the HomePageActivity.mProgressDialog is not being shown.
                     * Hence it is necessary to showToast ProfileControlFragment.mProgressDialog here.
                     *
                     * To satisfy both cases we are using HomePageActivity.mProgressDialog instead of ProfileControlFragment.mProgressDialog here.
                     */
                    HomePageActivity activity = (HomePageActivity) getActivity();
                    Utils.showBondingProgressDialog(activity, activity.mProgressDialog, PAIRING_NO_BONDING_PROGRESS_DIALOG_TIME_OUT_MILLIS);
                }
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Parse the services
                mApplication.getGattDbParser().prepareGattServices(BluetoothLeService.getSupportedGattServices());

                // Refresh carousel
                refreshCarouselView();

                // Fragments don't know how to react to GATT DB refresh, drop them to avoid weird bugs
                unwindBackStack();
            }
        }
    };

    private void unwindBackStack() {
        if (mIsInFragment) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.debug("PCF: lifecycle: onCreate", this, getActivity());

        mApplication = (AIROCBluetoothConnectApp) getActivity().getApplication();

        //Hiding the softkeyboard if visible
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        // Getting the width of the device
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mWidth = size.x;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Utils.debug("PCF: lifecycle: onCreateView", this, getActivity());
        View rootView = inflater.inflate(R.layout.profile_control, container, false);
        mPager = rootView.findViewById(R.id.myviewpager);
        mPages = 0;
        setCarouselView();
        setHasOptionsMenu(true);

        /**
         * Getting the orientation of the device. Set margin for pages as a
         * negative number, so a part of next and previous pages will be showed
         */
        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mPager.setPageMargin(-mWidth / 3);
        } else if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mPager.setPageMargin(-mWidth / 2);
        }

        mFirstTime = true;

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Utils.debug("PCF: lifecycle: onStart", this, getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.debug("PCF: lifecycle: onResume", this, getActivity());

        mIsInFragment = true;
        // Initialize ActionBar as per requirement
        Utils.setUpActionBar((AppCompatActivity) getActivity(), R.string.profile_control_fragment);

        if (BluetoothLeService.getConnectionState() == BluetoothLeService.STATE_DISCONNECTED) {
            // Get the user back to the profile scanning fragment
            Intent intent = getActivity().getIntent();
            getActivity().finish();
            getActivity().overridePendingTransition(R.anim.slide_left, R.anim.push_left);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_right, R.anim.push_right);
        } else {
            Logger.d("PCF: pair: registering mPairOnConnectStatusReceiver");
            BluetoothLeService.registerBroadcastReceiver(getActivity(), mBtServiceEventsReceiver, Utils.makeGattUpdateIntentFilter());
            mPairOnConnectStatusReceiverRegistered = true;

            if (mFirstTime) {
                mFirstTime = false;
                if (PairOnConnect.isPairOnConnect(getActivity())) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            initiatePairingIfSupported();
                        }
                    }, PAIR_DELAY_MILLIS);
                }
            }
        }
    }

    @Override
    public void onPause() {
        Utils.debug("PCF: lifecycle: onPause", this, getActivity());
        mIsInFragment = false;
        super.onPause();
    }

    @Override
    public void onStop() {
        Utils.debug("PCF: lifecycle: onStop", this, getActivity());
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Utils.debug("PCF: lifecycle: onDestroy", this, getActivity());

        // Dismiss the dialog if it is shown and we are leaving the fragment
        HomePageActivity activity = (HomePageActivity) getActivity();
        Utils.hideBondingProgressDialog(activity.mProgressDialog);

        if (mPairOnConnectStatusReceiverRegistered) {
            Logger.d("PCF: pair: unregistering mPairOnConnectStatusReceiver");
            BluetoothLeService.unregisterBroadcastReceiver(getActivity(), mBtServiceEventsReceiver);
            mPairOnConnectStatusReceiverRegistered = false;
        }

        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Getting the width on orientation changed
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        /**
         * Getting the orientation of the device. Set margin for pages as a
         * negative number, so a part of next and previous pages will be showed
         */
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mPager.setPageMargin(-width / 2);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mPager.setPageMargin(-width / 3);
        }

        // After the screen rotation the pager items aren't properly positioned.
        // After a lot of debugging re-setting the adapter was the simplest workaround that worked.
        // Though, here may be a better solution ...
        int currentItemIndexBeforeReset = mPager.getCurrentItem();
        mPager.setAdapter(mAdapter);
        mPager.setCurrentItem(currentItemIndexBeforeReset);
        mPager.refreshDrawableState();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.global, menu);
        MenuItem graph = menu.findItem(R.id.graph);
        MenuItem search = menu.findItem(R.id.search);
        MenuItem clearCache = menu.findItem(R.id.clearcache);

        if (false == search.isActionViewExpanded()) {
            search.collapseActionView();
            search.getActionView().clearFocus();
            Logger.e("Action view" + search.isActionViewExpanded());
            search.setActionView(null);
        }
        search.setVisible(false);
        graph.setVisible(false);
        clearCache.setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void refreshCarouselView() {
        setupCarousel();
        ToastUtils.makeText(R.string.gatt_db_services_updated, Toast.LENGTH_LONG);
    }

    /**
     * Setting the CarouselView with data
     */
    private void setCarouselView() {
        setupCarousel();

        if (mPages == 0) {
            ToastUtils.makeText(R.string.toast_no_services_found, Toast.LENGTH_LONG);
        } else {
            ToastUtils.makeText(R.string.toast_swipe_profiles, Toast.LENGTH_SHORT);
        }
    }

    private void setupCarousel() {
        // Getting the number of services discovered
        ArrayList<HashMap<String, BluetoothGattService>> gattServiceData = mApplication.getGattDbParser().getGattServiceData();
        mPages = gattServiceData.size();
        FIRST_PAGE = mPages * LOOPS / 2;

        // Setting the adapter
        mAdapter = new CarouselPagerAdapter(getActivity(), ProfileControlFragment.this, getActivity().getSupportFragmentManager(), gattServiceData);
        mPager.setAdapter(mAdapter);
        mPager.setOnPageChangeListener(mAdapter);

        // Set current item to the middle page so we can fling to both
        // directions left and right
        mPager.setCurrentItem(FIRST_PAGE);

        // Necessary or the pager will only have one extra page to showToast
        // make this at least however many pages you can see
        mPager.setOffscreenPageLimit(3);
    }

    /**
     * Enable indication on ServiceNameChanged characteristic to initiate pairing process
     */
    private void initiatePairingIfSupported() {
        List<BluetoothGattService> services = BluetoothLeService.getSupportedGattServices();
        if (services != null) {
            for (BluetoothGattService service : services) {
                if (UUIDDatabase.UUID_GENERIC_ATTRIBUTE_SERVICE.equals(service.getUuid())) {
                    mServiceChangedCharacteristic = service.getCharacteristic(UUIDDatabase.UUID_SERVICE_CHANGED);
                    if (mServiceChangedCharacteristic != null) {
                        mServiceChangedCCCD = mServiceChangedCharacteristic.getDescriptor(UUIDDatabase.UUID_CLIENT_CHARACTERISTIC_CONFIG);
                        if (mServiceChangedCCCD != null && (mServiceChangedCharacteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0) {
                            // If the peripheral uses pairing without bonding then simply enabling notification for the Service Changed characteristic works, i.e. pairing is being automatically started, but there is no
                            // notification from the Android that the pairing is in progress. To get some notification from Android that the pairing is in progress (to be able to showToast 'Pairing in progress' alert)
                            // the following trick is being used:
                            // 1. Read the Service Changed characteristic's CCCD - we should receive SUCCESS response.
                            // 2. Enable notification for the Service Changed characteristic - we get INSUFFICIENT_ENCRYPTION event, though the pairing is not automatically started.
                            // 3. Enable notification for the Service Changed characteristic once again - this time the pairing is automatically started.
                            BluetoothLeService.readDescriptor(mServiceChangedCCCD);
                        }
                    }
                    break;
                }
            }
        }
    }
}
