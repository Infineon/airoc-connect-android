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

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

import com.infineon.airocbluetoothconnect.BLEConnectionServices.BluetoothLeService;
import com.infineon.airocbluetoothconnect.CommonUtils.Constants;
import com.infineon.airocbluetoothconnect.CommonUtils.DepthPageTransformer;
import com.infineon.airocbluetoothconnect.CommonUtils.Logger;
import com.infineon.airocbluetoothconnect.CommonUtils.PagerFooterview;
import com.infineon.airocbluetoothconnect.CommonUtils.UUIDDatabase;
import com.infineon.airocbluetoothconnect.CommonUtils.Utils;
import com.infineon.airocbluetoothconnect.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Fragment to display the CapSenseService
 */
public class CapsenseService extends Fragment {

    // Service and characteristics
    private static BluetoothGattService mService;
    private static BluetoothGattCharacteristic mNotifyCharacteristicProximity;
    private static BluetoothGattCharacteristic mNotifyCharacteristicSlider;
    private static BluetoothGattCharacteristic mNotifyCharacteristicButtons;

    private int mPositionCapsenseProximity = -1;
    private int mPositionCapsenseSlider = -1;
    private int mPositionCapsenseButtons = -1;

    // Flag for notify
    private boolean mNotifySet = false;

    // Separate fragments for each capsense service
    private CapsenseServiceProximity mCapsenseProximity;
    private CapsenseServiceSlider mCapsenseSlider;
    private CapsenseServiceButtons mCapsenseButtons;

    // ViewPager variables
    private static int mViewpagerCount;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private PagerFooterview mPagerView;
    private LinearLayout mPagerLayout;

    // Fragment list
    private ArrayList<Fragment> fragmentsList = new ArrayList<Fragment>();

    public static CapsenseService create(BluetoothGattService service, int pageCount) {
        mService = service;
        mViewpagerCount = pageCount;
        return new CapsenseService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.capsense_main, container,
                false);

        mCapsenseProximity = new CapsenseServiceProximity();
        mCapsenseSlider = new CapsenseServiceSlider();
        mCapsenseButtons = new CapsenseServiceButtons();

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) rootView.findViewById(R.id.capsenseViewpager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());

        mPagerLayout = (LinearLayout) rootView
                .findViewById(R.id.capsense_page_indicator);
        mPagerView = new PagerFooterview(getActivity(), mViewpagerCount,
                mPagerLayout.getWidth());
        mPagerLayout.addView(mPagerView);

        if (mViewpagerCount == 1) {
            mPagerLayout.setVisibility(View.INVISIBLE);
        }

        // get required characteristics from service
        int count = 0;
        List<BluetoothGattCharacteristic> gattCharacteristics = Utils.getServiceCharacteristics(mService);

        for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
            UUID uuidchara = gattCharacteristic.getUuid();
            if (uuidchara.equals(UUIDDatabase.UUID_CAPSENSE_PROXIMITY) || uuidchara.equals(UUIDDatabase.UUID_CAPSENSE_PROXIMITY_CUSTOM)) {
                Logger.i("UUID Characteristic Proximity"
                        + gattCharacteristic.getUuid().toString());
                mNotifyCharacteristicProximity = gattCharacteristic;
                if (!mNotifySet) {
                    mNotifySet = true;
                    prepareBroadcastDataNotify(mNotifyCharacteristicProximity);
                }
                fragmentsList.add(CapsenseServiceProximity.create(mService));
                mPositionCapsenseProximity = count++;
            } else if (uuidchara.equals(UUIDDatabase.UUID_CAPSENSE_SLIDER) || uuidchara.equals(UUIDDatabase.UUID_CAPSENSE_SLIDER_CUSTOM)) {
                Logger.i("UUID Characteristic Slider"
                        + gattCharacteristic.getUuid().toString());
                mNotifyCharacteristicSlider = gattCharacteristic;
                if (!mNotifySet) {
                    mNotifySet = true;
                    prepareBroadcastDataNotify(mNotifyCharacteristicSlider);
                }
                fragmentsList.add(CapsenseServiceSlider.create(mService));
                mPositionCapsenseSlider = count++;
            } else if (uuidchara.equals(UUIDDatabase.UUID_CAPSENSE_BUTTONS) || uuidchara.equals(UUIDDatabase.UUID_CAPSENSE_BUTTONS_CUSTOM)) {
                Logger.i("UUID Characteristic Buttons"
                        + gattCharacteristic.getUuid().toString());
                mNotifyCharacteristicButtons = gattCharacteristic;
                if (!mNotifySet) {
                    mNotifySet = true;
                    prepareBroadcastDataNotify(mNotifyCharacteristicButtons);
                }
                fragmentsList.add(CapsenseServiceButtons.create(mService));
                mPositionCapsenseButtons = count++;
            }
        }
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageTransformer(true, new DepthPageTransformer());
        mPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                mPagerView.Update(position);
                if (position == mPositionCapsenseProximity) {
                    ArrayList<BluetoothGattCharacteristic> disableList = new ArrayList<>();
                    if (mNotifyCharacteristicSlider != null) {
                        disableList.add(mNotifyCharacteristicSlider);
                    }
                    if (mNotifyCharacteristicButtons != null) {
                        disableList.add(mNotifyCharacteristicButtons);
                    }
                    BluetoothLeService.enableAndDisableSelectedCharacteristics(Arrays.asList(mNotifyCharacteristicProximity), disableList);
                } else if (position == mPositionCapsenseSlider) {
                    ArrayList<BluetoothGattCharacteristic> disableList = new ArrayList<>();
                    if (mNotifyCharacteristicProximity != null) {
                        disableList.add(mNotifyCharacteristicProximity);
                    }
                    if (mNotifyCharacteristicButtons != null) {
                        disableList.add(mNotifyCharacteristicButtons);
                    }
                    BluetoothLeService.enableAndDisableSelectedCharacteristics(Arrays.asList(mNotifyCharacteristicSlider), disableList);
                } else if (position == mPositionCapsenseButtons) {
                    ArrayList<BluetoothGattCharacteristic> disableList = new ArrayList<>();
                    if (mNotifyCharacteristicSlider != null) {
                        disableList.add(mNotifyCharacteristicSlider);
                    }
                    if (mNotifyCharacteristicProximity != null) {
                        disableList.add(mNotifyCharacteristicProximity);
                    }
                    BluetoothLeService.enableAndDisableSelectedCharacteristics(Arrays.asList(mNotifyCharacteristicButtons), disableList);
                } else {
                    Logger.e("Unknown position: " + position);
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                //Not needed
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                //Not needed
            }
        });
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.debug("CapSense: lifecycle: onResume", this, getActivity());
        mNotifySet = false;
        BluetoothLeService.registerBroadcastReceiver(getActivity(), mGattUpdateReceiver, Utils.makeGattUpdateIntentFilter());
        Utils.setUpActionBar((AppCompatActivity) getActivity(), R.string.capsense);
    }

    @Override
    public void onDestroy() {
        Utils.debug("CapSense: lifecycle: onDestroy", this, getActivity());
        BluetoothLeService.unregisterBroadcastReceiver(getActivity(), mGattUpdateReceiver);

        ArrayList<BluetoothGattCharacteristic> disableList = new ArrayList<>();
        if (mNotifyCharacteristicSlider != null) {
            disableList.add(mNotifyCharacteristicSlider);
        }
        if (mNotifyCharacteristicProximity != null) {
            disableList.add(mNotifyCharacteristicProximity);
        }
        if (mNotifyCharacteristicButtons != null) {
            disableList.add(mNotifyCharacteristicButtons);
        }
        BluetoothLeService.disableSelectedCharacteristics(disableList);
        super.onDestroy();
    }

    /**
     * A simple pager adapter that represents CapsenseFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentsList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentsList.size();
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
     * BroadcastReceiver for receiving the GATT server status
     */
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            // GATT Data Available
            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                Bundle extras = intent.getExtras();
                if (extras.containsKey(Constants.EXTRA_CAPPROX_VALUE)) {
                    int received_proximity_rate = extras
                            .getInt(Constants.EXTRA_CAPPROX_VALUE);
                    CapsenseServiceProximity.displayLiveData(context, received_proximity_rate);
                }
            }
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.global, menu);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setIcon(new ColorDrawable(getResources().getColor(
                    android.R.color.transparent)));
        }
        MenuItem graph = menu.findItem(R.id.graph);
        MenuItem search = menu.findItem(R.id.search);
        search.setVisible(false);
        graph.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
