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

package com.infineon.airocbluetoothconnect.wearable;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.infineon.airocbluetoothconnect.BLEConnectionServices.BluetoothLeService;
import com.infineon.airocbluetoothconnect.CommonUtils.ProgressIndicatorToggle;
import com.infineon.airocbluetoothconnect.R;

import java.util.Collection;

public abstract class AbstractFragment extends Fragment {

    private Handler mHandler = new Handler();
    protected ExpandableListView mListView;
    protected ProgressDialog mProgressDialog;
    protected int mBackstackCount;
    private boolean mReceiverRegistered;

    protected ProgressIndicatorToggle mProgressIndicatorToggle =
            new ProgressIndicatorToggle(
                    500,
                    new ProgressIndicatorToggle.ReadyTest() {
                        @Override
                        public boolean isReady() {
                            return true; // TODO
                        }
                    });

    private Runnable mDismissProgressDialogRunnable = new Runnable() {
        @Override
        public void run() {
            mProgressDialog.dismiss();
        }
    };

    private Runnable mShowProgressDialogRunnable = new Runnable() {
        @Override
        public void run() {
            mProgressDialog.show();
        }
    };

    protected final Runnable mResponseTimer = new Runnable() {
        @Override
        public void run() {
            BluetoothLeService.disconnect();
            if (getActivity() != null) {
                Intent intent = getActivity().getIntent();
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.slide_left, R.anim.push_left);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_right, R.anim.push_right);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        System.err.println("--BASE: CREATE");
        super.onCreate(savedInstanceState);
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setCancelable(false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        System.err.println("--BASE: ACTIVITY CREATED");
        super.onActivityCreated(savedInstanceState);
    }


    protected void toggleProgressOn(String message) {
        mProgressDialog.setMessage(message);
        mProgressIndicatorToggle.on(mShowProgressDialogRunnable);
        mHandler.postDelayed(mResponseTimer,
                Const.WAIT_FOR_BLE_RESPONSE_TIMEOUT_MILLIS);
    }

    protected void toggleProgressOff() {
        mHandler.removeCallbacks(mResponseTimer);
        mProgressIndicatorToggle.off(mDismissProgressDialogRunnable);
    }

    protected void refreshChildView(int groupPosition, int childPosition) {
        for (int start = mListView.getFirstVisiblePosition(), i = start, j = mListView.getLastVisiblePosition(); i <= j; i++) {
            long packedPos = mListView.getExpandableListPosition(i);
            int packedPosType = ExpandableListView.getPackedPositionType(packedPos);
            if (packedPosType != ExpandableListView.PACKED_POSITION_TYPE_NULL) {
                int groupPos = ExpandableListView.getPackedPositionGroup(packedPos);
                if (groupPos == groupPosition
                        && packedPosType == ExpandableListView.PACKED_POSITION_TYPE_CHILD
                        && ExpandableListView.getPackedPositionChild(packedPos) == childPosition) {
                    View view = mListView.getChildAt(i - start);
                    mListView.getAdapter().getView(i, view, mListView);
                    break;
                }
            }
        }
    }

    protected void addFragment(Fragment fragment, String tagName) {
        FragmentManager fm = getFragmentManager();
        mBackstackCount = fm.getBackStackEntryCount();
        fm.beginTransaction()
                .add(R.id.container, fragment, tagName)
                .addToBackStack(null)
                .commit();
    }

    protected void unregisterBroadcastReceiver(BroadcastReceiver receiver) {
        if (mReceiverRegistered) {
            mReceiverRegistered = false;
            System.err.println("--BASE: UNREGISTER RECEIVER");
            BluetoothLeService.unregisterBroadcastReceiver(getActivity(), receiver);
        }
    }

    protected void registerBroadcastReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        if (!mReceiverRegistered) {
            mReceiverRegistered = true;
            System.err.println("--BASE: REGISTER RECEIVER");
            BluetoothLeService.registerBroadcastReceiver(getActivity(), receiver, filter);
        }
    }

    @NonNull
    protected IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter(BluetoothLeService.ACTION_DATA_AVAILABLE);
        filter.addAction(BluetoothLeService.ACTION_WRITE_SUCCESS);
        filter.addAction(BluetoothLeService.ACTION_WRITE_COMPLETED);
        return filter;
    }

    protected void enableNotifications(Collection<BluetoothGattCharacteristic> characteristics) {
//        BluetoothLeService.mEnabledCharacteristics = new ArrayList<>(); // TODO
        BluetoothLeService.mDisableEnabledCharacteristicsFlag = false;
        if (BluetoothLeService.enableSelectedCharacteristics(characteristics)) {
            toggleProgressOn(getEnablingNotificationsMessage());
        }
    }

    protected void disableNotifications(Collection<BluetoothGattCharacteristic> characteristics) {
        if (BluetoothLeService.disableSelectedCharacteristics(characteristics)) {
            toggleProgressOn(getDisablingNotificationsMessage());
        }
    }

    protected void disableAllNotifications() {
        disableAllNotifications(true);
    }

    protected void disableAllNotifications(boolean toast) {
        if (BluetoothLeService.disableAllEnabledCharacteristics() && toast) {
            Toast.makeText(getActivity(), getString(R.string.profile_control_stop_both_notify_indicate_toast),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @NonNull
    protected String getEnablingNotificationsMessage() {
        return getString(R.string.message_notifications_enabling)
                + ".\n" + getString(R.string.alert_message_wait);
    }

    @NonNull
    protected String getNotificationsEnabledMessage() {
        return getString(R.string.message_notifications_enabled);
    }

    @NonNull
    protected String getDisablingNotificationsMessage() {
        return getString(R.string.message_notifications_disabling)
                + ".\n" + getString(R.string.alert_message_wait);
    }

    @NonNull
    protected String getNotificationsDisabledMessage() {
        return getString(R.string.message_notifications_disabled);
    }
}
