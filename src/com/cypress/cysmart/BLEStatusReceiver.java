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

package com.cypress.cysmart;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.cypress.cysmart.BLEConnectionServices.BluetoothLeService;
import com.cypress.cysmart.CommonFragments.HomePageTabbedFragment;
import com.cypress.cysmart.CommonFragments.ProfileControlFragment;
import com.cypress.cysmart.CommonFragments.ServiceDiscoveryFragment;
import com.cypress.cysmart.CommonUtils.Constants;
import com.cypress.cysmart.CommonUtils.Logger;
import com.cypress.cysmart.CommonUtils.ToastUtils;
import com.cypress.cysmart.CommonUtils.Utils;
import com.cypress.cysmart.DataLoggerFragments.DataLoggerHistoryList;
import com.cypress.cysmart.OTAFirmwareUpdate.OTAFilesListingActivity;
import com.cypress.cysmart.OTAFirmwareUpdate.OTAFirmwareUpgradeFragment;


/**
 * Receiver class for BLE disconnect Event.
 * This receiver will be called when a disconnect message from the connected peripheral
 * is received by the application
 */
public class BLEStatusReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
            Logger.d("BLEStatusReceiver.onReceive HPA.inBackground: " + HomePageActivity.mApplicationInBackground
                    + ", HPTF.inFragment: " + HomePageTabbedFragment.mIsInFragment + ", SDF.inFragment: " + ServiceDiscoveryFragment.mIsInFragment
                    + ", PCF.inFragment: " + ProfileControlFragment.mIsInFragment + ", Connection State: " + BluetoothLeService.getConnectionState());
            if (!HomePageActivity.mApplicationInBackground
                    || !OTAFilesListingActivity.mApplicationInBackground
                    || !DataLoggerHistoryList.mApplicationInBackground) {
                ToastUtils.showToast(R.string.alert_message_bluetooth_disconnect, Toast.LENGTH_SHORT);
                if (OTAFirmwareUpgradeFragment.mFileUpgradeStarted) {
                    //Resetting all preferences on Stop Button
                    Utils.setStringSharedPreference(context, Constants.PREF_OTA_FILE_ONE_NAME, "Default");
                    Utils.setStringSharedPreference(context, Constants.PREF_OTA_FILE_TWO_PATH, "Default");
                    Utils.setStringSharedPreference(context, Constants.PREF_OTA_FILE_TWO_NAME, "Default");
                    Utils.setStringSharedPreference(context, Constants.PREF_OTA_ACTIVE_APP_ID, "Default");
                    Utils.setStringSharedPreference(context, Constants.PREF_OTA_SECURITY_KEY, "Default");
                    Utils.setStringSharedPreference(context, Constants.PREF_BOOTLOADER_STATE, "Default");
                    Utils.setIntSharedPreference(context, Constants.PREF_PROGRAM_ROW_NO, 0);
                }
                if (!HomePageTabbedFragment.mIsInFragment &&
                        !HomePageActivity.mApplicationInBackground) {
                    if (BluetoothLeService.getConnectionState() == BluetoothLeService.STATE_DISCONNECTED) {
                        Logger.e("Disconnected -> navigating to the PSF");
                        Intent homePage = new Intent(context, HomePageActivity.class);
                        homePage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(homePage);
                    }
                }
            }
        }
    }
}
