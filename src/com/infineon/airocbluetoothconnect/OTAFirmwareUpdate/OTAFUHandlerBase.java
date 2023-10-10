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

package com.infineon.airocbluetoothconnect.OTAFirmwareUpdate;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import androidx.fragment.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.infineon.airocbluetoothconnect.CommonUtils.TextProgressBar;
import com.infineon.airocbluetoothconnect.R;

public abstract class OTAFUHandlerBase implements OTAFUHandler {

    protected final Fragment mFragment;
    protected final View mView;
    protected final TextView mProgressText;
    protected final TextProgressBar mProgressTop;
    protected final TextProgressBar mProgressBottom;
    protected final NotificationHandler mNotificationHandler;

    protected final BluetoothGattCharacteristic mOtaCharacteristic;
    protected final String mFilepath;
    private final OTAFUHandlerCallback mParent;
    protected boolean mPrepareFileWriteEnabled = true;
    protected int mProgressBarPosition;
    protected byte mActiveApp; //Dual-App Bootloader Active Application ID
    protected long mSecurityKey;

    public OTAFUHandlerBase(Fragment fragment, NotificationHandler notificationHandler, View view, BluetoothGattCharacteristic otaCharacteristic, byte activeApp, long securityKey, String filepath, OTAFUHandlerCallback parent) {
        this.mFragment = fragment;
        this.mNotificationHandler = notificationHandler;
        this.mView = view;
        this.mOtaCharacteristic = otaCharacteristic;
        this.mActiveApp = activeApp;
        this.mSecurityKey = securityKey;
        this.mFilepath = filepath;
        this.mParent = parent;
        mProgressText = (TextView) mView.findViewById(R.id.file_status);
        mProgressTop = (TextProgressBar) mView.findViewById(R.id.upgrade_progress_bar_top);
        mProgressBottom = (TextProgressBar) mView.findViewById(R.id.upgrade_progress_bar_bottom);
    }

    @Override
    public void setPrepareFileWriteEnabled(boolean enabled) {
        this.mPrepareFileWriteEnabled = enabled;
    }

    @Override
    public void setProgressBarPosition(int position) {
        this.mProgressBarPosition = position;
    }

    protected Activity getActivity() {
        return mFragment.getActivity();
    }

    protected Resources getResources() {
        return mFragment.getResources();
    }

    /**
     * Method to showToast progress bar
     */
    protected void showProgress(int fileStatus, float fileLineNos, float totalLines) {
        if (fileStatus == 1) {
            mProgressTop.setProgress((int) fileLineNos);   // Main Progress
            mProgressTop.setMax((int) totalLines); // Maximum Progress
            mProgressTop.setProgressText("" + (int) ((fileLineNos / totalLines) * 100) + "%");
            setProgress(getActivity(), 100, (int) ((fileLineNos / totalLines) * 100), false);
        } else if (fileStatus == 2) {
            mProgressTop.setProgress(100);
            mProgressTop.setMax(100);// Main Progress
            mProgressTop.setProgressText("100%");
            mProgressBottom.setProgress((int) fileLineNos);   // Main Progress
            mProgressBottom.setMax((int) totalLines);
            mProgressBottom.setProgressText("" + (int) ((fileLineNos / totalLines) * 100) + "%");
            setProgress(getActivity(), 100, (int) ((fileLineNos / totalLines) * 100), false);
        }
    }

    private void setProgress(Context context, int limit, int updateLimit, boolean flag) {
        mNotificationHandler.updateProgress(context, limit, updateLimit, flag);
    }

    protected void startActivity(Intent intent) {
        mFragment.startActivity(intent);
    }

    protected void showErrorDialogMessage(String errorMessage, final boolean stayOnPage) {
        mParent.showErrorDialogMessage(errorMessage, stayOnPage);
    }

    protected boolean isSecondFileUpdateNeeded() {
        return mParent.isSecondFileUpdateNeeded();
    }

    protected String storeAndReturnDeviceAddress() {
        return mParent.saveAndReturnDeviceAddress();
    }

    protected void setFileUpgradeStarted(boolean started) {
        mParent.setFileUpgradeStarted(started);
    }

    protected void generatePendingNotification(Context context) {
        mParent.generatePendingNotification(context);
    }
}
