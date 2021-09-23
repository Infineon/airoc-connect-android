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

package com.cypress.cysmart.OTAFirmwareUpdate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.cypress.cysmart.BLEConnectionServices.BluetoothLeService;
import com.cypress.cysmart.CommonUtils.Constants;
import com.cypress.cysmart.CommonUtils.ConvertUtils;
import com.cypress.cysmart.CommonUtils.Logger;
import com.cypress.cysmart.CommonUtils.Utils;

/**
 * Receiver class for OTA response
 */
public class OTAResponseReceiver_v0 extends BroadcastReceiver {

    private Context mContext;

    //Substring Constants
    private static final int RESPONSE_START = 2;
    private static final int RESPONSE_END = 4;

    private static final int STATUS_START = 2;
    private static final int STATUS_END = 4;
    private static final int CHECKSUM_START = 8;
    private static final int CHECKSUM_END = 10;

    private static final int SILICON_ID_START = 8;
    private static final int SILICON_ID_END = 16;
    private static final int SILICON_REV_START = 16;
    private static final int SILICON_REV_END = 18;

    private static final int APP_VALID_START = 8;
    private static final int APP_VALID_END = 10;
    private static final int APP_ACTIVE_START = 10;
    private static final int APP_ACTIVE_END = 12;

    private static final int START_ROW_START = 8;
    private static final int START_ROW_END = 12;
    private static final int END_ROW_START = 12;
    private static final int END_ROW_END = 16;

    private static final int DATA_START = 8;
    private static final int DATA_END = 10;

    private static final int RADIX = 16;

    //Switch case Constants
    private static final int CASE_SUCCESS = 0;
    private static final int CASE_ERR_FILE = 1;
    private static final int CASE_ERR_EOF = 2;
    private static final int CASE_ERR_LENGTH = 3;
    private static final int CASE_ERR_DATA = 4;
    private static final int CASE_ERR_CMD = 5;
    private static final int CASE_ERR_DEVICE = 6;
    private static final int CASE_ERR_VERSION = 7;
    private static final int CASE_ERR_CHECKSUM = 8;
    private static final int CASE_ERR_ARRAY = 9;
    private static final int CASE_ERR_ROW = 10;
    private static final int CASE_BTLDR = 11;
    private static final int CASE_ERR_APP = 12;
    private static final int CASE_ERR_ACTIVE = 13;
    private static final int CASE_ERR_UNK = 14;
    private static final int CASE_ABORT = 15;

    //Error Constants
    private static final String CYRET_ERR_FILE = "CYRET_ERR_FILE";
    private static final String CYRET_ERR_EOF = "CYRET_ERR_EOF";
    private static final String CYRET_ERR_LENGTH = "CYRET_ERR_LENGTH";
    private static final String CYRET_ERR_DATA = "CYRET_ERR_DATA";
    private static final String CYRET_ERR_CMD = "CYRET_ERR_CMD";
    private static final String CYRET_ERR_DEVICE = "CYRET_ERR_DEVICE";
    private static final String CYRET_ERR_VERSION = "CYRET_ERR_VERSION";
    private static final String CYRET_ERR_CHECKSUM = "CYRET_ERR_CHECKSUM";
    private static final String CYRET_ERR_ARRAY = "CYRET_ERR_ARRAY";
    private static final String CYRET_BTLDR = "CYRET_BTLDR";
    private static final String CYRET_ERR_APP = "CYRET_ERR_APP";
    private static final String CYRET_ERR_ACTIVE = "CYRET_ERR_ACTIVE";
    private static final String CYRET_ERR_UNK = "CYRET_ERR_UNK";
    private static final String CYRET_ERR_ROW = "CYRET_ERR_ROW";
    private static final String CYRET_ABORT = "CYRET_ABORT";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        this.mContext = context;
        /**
         * Condition to execute the next command to execute
         * Checks the Shared preferences for the currently executing command
         */
        if (BluetoothLeService.ACTION_OTA_DATA_AVAILABLE.equals(action)) {
            byte[] responseArray = intent.getByteArrayExtra(Constants.EXTRA_BYTE_VALUE);
            String hexValue = Utils.byteArrayToHex(responseArray);
            if ((Utils.getStringSharedPreference(mContext, Constants.PREF_BOOTLOADER_STATE))
                    .equalsIgnoreCase("" + BootLoaderCommands_v0.ENTER_BOOTLOADER)) {
                parseEnterBootloaderCmdResponse(hexValue);
            } else if ((Utils.getStringSharedPreference(mContext, Constants.PREF_BOOTLOADER_STATE))
                    .equalsIgnoreCase("" + BootLoaderCommands_v0.GET_APP_STATUS)) {
                parseGetAppStatusCmdResponse(hexValue);
            } else if ((Utils.getStringSharedPreference(mContext, Constants.PREF_BOOTLOADER_STATE))
                    .equalsIgnoreCase("" + BootLoaderCommands_v0.GET_FLASH_SIZE)) {
                parseGetFlashSizeCmdResponse(hexValue);
            } else if ((Utils.getStringSharedPreference(mContext, Constants.PREF_BOOTLOADER_STATE))
                    .equalsIgnoreCase("" + BootLoaderCommands_v0.SEND_DATA)) {
                parseSendDataCmdResponse(hexValue);
            } else if ((Utils.getStringSharedPreference(mContext, Constants.PREF_BOOTLOADER_STATE))
                    .equalsIgnoreCase("" + BootLoaderCommands_v0.PROGRAM_ROW)) {
                parseProgramRowCmdResponse(hexValue);
            } else if ((Utils.getStringSharedPreference(mContext, Constants.PREF_BOOTLOADER_STATE))
                    .equalsIgnoreCase("" + BootLoaderCommands_v0.VERIFY_ROW)) {
                parseVerifyRowCmdResponse(hexValue);
            } else if ((Utils.getStringSharedPreference(mContext, Constants.PREF_BOOTLOADER_STATE))
                    .equalsIgnoreCase("" + BootLoaderCommands_v0.VERIFY_CHECK_SUM)) {
                parseVerifyChecksumCmdResponse(hexValue);
            } else if ((Utils.getStringSharedPreference(mContext, Constants.PREF_BOOTLOADER_STATE))
                    .equalsIgnoreCase("" + BootLoaderCommands_v0.SET_ACTIVE_APP)) {
                parseSetActiveAppCmdResponse(hexValue);
            } else if ((Utils.getStringSharedPreference(mContext, Constants.PREF_BOOTLOADER_STATE))
                    .equalsIgnoreCase("" + BootLoaderCommands_v0.EXIT_BOOTLOADER)) {
                parseExitBootloaderCmdResponse(hexValue);
            } else {
                Logger.i("In Receiver No case " + Utils.getStringSharedPreference(mContext, Constants.PREF_BOOTLOADER_STATE));
            }
        }
    }

    /**
     * Method parses the response String and executes the corresponding cases
     *
     * @param parse
     */
    private void parseEnterBootloaderCmdResponse(String parse) {
        String result = parse.trim().replace(" ", "");
        Logger.d("OTA response: Enter Bootloader: " + result);
        String response = result.substring(RESPONSE_START, RESPONSE_END);
        int responseBytes = Integer.parseInt(response, RADIX);
        switch (responseBytes) {
            case CASE_SUCCESS:
                String siliconID = result.substring(SILICON_ID_START, SILICON_ID_END);
                String siliconRev = result.substring(SILICON_REV_START, SILICON_REV_END);
                Intent otaStatusIntent = new Intent(BluetoothLeService.ACTION_OTA_STATUS);
                Bundle bundle = new Bundle();
                bundle.putString(Constants.EXTRA_SILICON_ID, siliconID);
                bundle.putString(Constants.EXTRA_SILICON_REV, siliconRev);
                otaStatusIntent.putExtras(bundle);
                BluetoothLeService.sendGlobalBroadcastIntent(mContext, otaStatusIntent);
                break;
            default:
                broadcastError(responseBytes);
                Logger.d("CYRET ERROR");
                break;
        }
    }

    /**
     * Method parses the response String and executes the corresponding cases
     *
     * @param parse
     */
    private void parseGetAppStatusCmdResponse(String parse) {
        String result = parse.trim().replace(" ", "");
        Logger.d("OTA response: Get App Status: " + result);
        String response = result.substring(RESPONSE_START, RESPONSE_END);
        int responseBytes = Integer.parseInt(response, RADIX);
        switch (responseBytes) {
            case CASE_SUCCESS:
                int appValid = Integer.parseInt(result.substring(APP_VALID_START, APP_VALID_END), RADIX);
                int appActive = Integer.parseInt(result.substring(APP_ACTIVE_START, APP_ACTIVE_END), RADIX);
                Intent otaStatusIntent = new Intent(BluetoothLeService.ACTION_OTA_STATUS);
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.EXTRA_APP_VALID, appValid);
                bundle.putInt(Constants.EXTRA_APP_ACTIVE, appActive);
                otaStatusIntent.putExtras(bundle);
                BluetoothLeService.sendGlobalBroadcastIntent(mContext, otaStatusIntent);
                break;
            default:
                broadcastError(responseBytes);
                Logger.d("CYRET ERROR");
                break;
        }
    }

    /**
     * Method parses the response String and executes the corresponding cases
     *
     * @param parse
     */
    private void parseGetFlashSizeCmdResponse(String parse) {
        String result = parse.trim().replace(" ", "");
        Logger.d("OTA response: Get Flash Size: " + result);
        String response = result.substring(RESPONSE_START, RESPONSE_END);
        int responseBytes = Integer.parseInt(response, RADIX);
        switch (responseBytes) {
            case CASE_SUCCESS:
                int startRow = ConvertUtils.swapShort(Integer.parseInt(result.substring(START_ROW_START, START_ROW_END), RADIX));
                int endRow = ConvertUtils.swapShort(Integer.parseInt(result.substring(END_ROW_START, END_ROW_END), RADIX));
                Intent otaStatusIntent = new Intent(BluetoothLeService.ACTION_OTA_STATUS);
                Bundle bundle = new Bundle();
                bundle.putString(Constants.EXTRA_START_ROW, "" + startRow);
                bundle.putString(Constants.EXTRA_END_ROW, "" + endRow);
                otaStatusIntent.putExtras(bundle);
                BluetoothLeService.sendGlobalBroadcastIntent(mContext, otaStatusIntent);
                break;
            default:
                broadcastError(responseBytes);
                Logger.d("CYRET ERROR");
                break;
        }
    }

    /**
     * Method parses the response String and executes the corresponding cases
     *
     * @param parse
     */
    private void parseSendDataCmdResponse(String parse) {
        String result = parse.trim().replace(" ", "");
        Logger.d("OTA response: Send Data: " + result);
        String response = result.substring(RESPONSE_START, RESPONSE_END);
        String status = result.substring(STATUS_START, STATUS_END);
        int responseBytes = Integer.parseInt(response, RADIX);
        switch (responseBytes) {
            case CASE_SUCCESS:
                Intent otaStatusIntent = new Intent(BluetoothLeService.ACTION_OTA_STATUS);
                Bundle bundle = new Bundle();
                bundle.putString(Constants.EXTRA_SEND_DATA_ROW_STATUS, status);
                otaStatusIntent.putExtras(bundle);
                BluetoothLeService.sendGlobalBroadcastIntent(mContext, otaStatusIntent);
                break;
            default:
                broadcastError(responseBytes);
                Logger.d("CYRET ERROR");
                break;
        }
    }

    /**
     * Method parses the response String and executes the corresponding cases
     *
     * @param parse
     */
    private void parseProgramRowCmdResponse(String parse) {
        String result = parse.trim().replace(" ", "");
        Logger.d("OTA response: Program Row: " + result);
        String response = result.substring(RESPONSE_START, RESPONSE_END);
        String status = result.substring(STATUS_START, STATUS_END);
        int responseBytes = Integer.parseInt(response, RADIX);
        switch (responseBytes) {
            case CASE_SUCCESS:
                Intent otaStatusIntent = new Intent(BluetoothLeService.ACTION_OTA_STATUS);
                Bundle bundle = new Bundle();
                bundle.putString(Constants.EXTRA_PROGRAM_ROW_STATUS, status);
                otaStatusIntent.putExtras(bundle);
                BluetoothLeService.sendGlobalBroadcastIntent(mContext, otaStatusIntent);
                break;
            default:
                broadcastError(responseBytes);
                Logger.d("CYRET ERROR");
                break;
        }
    }

    /**
     * Method parses the response String and executes the corresponding cases
     *
     * @param parse
     */
    private void parseVerifyRowCmdResponse(String parse) {
        String result = parse.trim().replace(" ", "");
        Logger.d("OTA response: Verify Row: " + result);
        String response = result.substring(RESPONSE_START, RESPONSE_END);
        String data = result.substring(DATA_START, DATA_END);
        int responseBytes = Integer.parseInt(response, RADIX);
        switch (responseBytes) {
            case CASE_SUCCESS:
                Intent otaStatusIntent = new Intent(BluetoothLeService.ACTION_OTA_STATUS);
                Bundle bundle = new Bundle();
                bundle.putString(Constants.EXTRA_VERIFY_ROW_STATUS, response);
                bundle.putString(Constants.EXTRA_VERIFY_ROW_CHECKSUM, data);
                otaStatusIntent.putExtras(bundle);
                BluetoothLeService.sendGlobalBroadcastIntent(mContext, otaStatusIntent);
                break;
            default:
                broadcastError(responseBytes);
                Logger.d("CYRET ERROR");
                break;
        }
    }

    /**
     * Method parses the response String and executes the corresponding cases
     *
     * @param parse
     */
    private void parseVerifyChecksumCmdResponse(String parse) {
        String result = parse.trim().replace(" ", "");
        Logger.d("OTA response: Verify Checksum: " + result);
        String response = result.substring(RESPONSE_START, RESPONSE_END);
        String checkSumStatus = result.substring(CHECKSUM_START, CHECKSUM_END);
        int responseBytes = Integer.parseInt(response, RADIX);
        switch (responseBytes) {
            case CASE_SUCCESS:
                Intent otaStatusIntent = new Intent(BluetoothLeService.ACTION_OTA_STATUS);
                Bundle bundle = new Bundle();
                bundle.putString(Constants.EXTRA_VERIFY_CHECKSUM_STATUS, checkSumStatus);
                otaStatusIntent.putExtras(bundle);
                BluetoothLeService.sendGlobalBroadcastIntent(mContext, otaStatusIntent);
                break;
            default:
                broadcastError(responseBytes);
                Logger.d("CYRET ERROR");
                break;
        }
    }

    /**
     * Method parses the response String and executes the corresponding cases
     *
     * @param parse
     */
    private void parseSetActiveAppCmdResponse(String parse) {
        String result = parse.trim().replace(" ", "");
        Logger.d("OTA response: Set Active App: " + result);
        String response = result.substring(RESPONSE_START, RESPONSE_END);
        Intent otaStatusIntent = new Intent(BluetoothLeService.ACTION_OTA_STATUS);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA_SET_ACTIVE_APP, response);
        otaStatusIntent.putExtras(bundle);
        BluetoothLeService.sendGlobalBroadcastIntent(mContext, otaStatusIntent);
    }

    /**
     * Method parses the response String and executes the corresponding cases
     *
     * @param parse
     */
    private void parseExitBootloaderCmdResponse(String parse) {
        String response = parse.trim().replace(" ", "");
        Logger.d("OTA response: Exit Bootloader: " + response);
        Intent otaStatusIntent = new Intent(BluetoothLeService.ACTION_OTA_STATUS);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA_VERIFY_EXIT_BOOTLOADER, response);
        otaStatusIntent.putExtras(bundle);
        BluetoothLeService.sendGlobalBroadcastIntent(mContext, otaStatusIntent);
    }

    public void broadcastErrorMessage(String errorMessage) {
        Intent otaStatusIntent = new Intent(BluetoothLeService.ACTION_OTA_STATUS);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA_ERROR_OTA, errorMessage);
        otaStatusIntent.putExtras(bundle);
        BluetoothLeService.sendGlobalBroadcastIntent(mContext, otaStatusIntent);
    }

    public void broadcastError(int errorCode) {
        switch (errorCode) {
            case CASE_ERR_FILE:
                Logger.i("CYRET_ERR_FILE");
                broadcastErrorMessage(CYRET_ERR_FILE);
                break;
            case CASE_ERR_EOF:
                Logger.i("CYRET_ERR_EOF");
                broadcastErrorMessage(CYRET_ERR_EOF);
                break;
            case CASE_ERR_LENGTH:
                Logger.i("CYRET_ERR_LENGTH");
                broadcastErrorMessage(CYRET_ERR_LENGTH);
                break;
            case CASE_ERR_DATA:
                Logger.i("CYRET_ERR_DATA");
                broadcastErrorMessage(CYRET_ERR_DATA);
                break;
            case CASE_ERR_CMD:
                Logger.i("CYRET_ERR_CMD");
                broadcastErrorMessage(CYRET_ERR_CMD);
                break;
            case CASE_ERR_DEVICE:
                Logger.i("CYRET_ERR_DEVICE");
                broadcastErrorMessage(CYRET_ERR_DEVICE);
                break;
            case CASE_ERR_VERSION:
                Logger.i("CYRET_ERR_VERSION");
                broadcastErrorMessage(CYRET_ERR_VERSION);
                break;
            case CASE_ERR_CHECKSUM:
                Logger.i("CYRET_ERR_CHECKSUM");
                broadcastErrorMessage(CYRET_ERR_CHECKSUM);
                break;
            case CASE_ERR_ARRAY:
                Logger.i("CYRET_ERR_ARRAY");
                broadcastErrorMessage(CYRET_ERR_ARRAY);
                break;
            case CASE_ERR_ROW:
                Logger.i("CYRET_ERR_ROW");
                broadcastErrorMessage(CYRET_ERR_ROW);
                break;
            case CASE_BTLDR:
                Logger.i("CYRET_BTLDR");
                broadcastErrorMessage(CYRET_BTLDR);
                break;
            case CASE_ERR_APP:
                Logger.i("CYRET_ERR_APP");
                broadcastErrorMessage(CYRET_ERR_APP);
                break;
            case CASE_ERR_ACTIVE:
                Logger.i("CYRET_ERR_ACTIVE");
                broadcastErrorMessage(CYRET_ERR_ACTIVE);
                break;
            case CASE_ERR_UNK:
                Logger.i("CYRET_ERR_UNK");
                broadcastErrorMessage(CYRET_ERR_UNK);
                break;
            case CASE_ABORT:
                Logger.i("CYRET_ABORT");
                broadcastErrorMessage(CYRET_ABORT);
                break;
            default:
                Logger.i("CYRET DEFAULT");
                break;
        }
    }
}
