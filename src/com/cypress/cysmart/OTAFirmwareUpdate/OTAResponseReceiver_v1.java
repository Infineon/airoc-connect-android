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
public class OTAResponseReceiver_v1 extends BroadcastReceiver {
    public static final int RESP_STATUS_CODE_START = 1;
    public static final int RESP_STATUS_CODE_SIZE = 1;
    public static final int RESP_DATA_START = 4;

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

    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        this.mContext = context;
        if (BluetoothLeService.ACTION_OTA_DATA_AVAILABLE_V1.equals(action)) {
            byte[] respBytes = intent.getByteArrayExtra(Constants.EXTRA_BYTE_VALUE);
            if ((Utils.getStringSharedPreference(mContext, Constants.PREF_BOOTLOADER_STATE)).equalsIgnoreCase("" + BootLoaderCommands_v1.ENTER_BOOTLOADER)) {
                parseEnterBootLoaderCmdResponse(respBytes);
            } else if ((Utils.getStringSharedPreference(mContext, Constants.PREF_BOOTLOADER_STATE)).equalsIgnoreCase(BootLoaderCommands_v1.POST_SYNC_ENTER_BOOTLOADER)) { //Bulletproof OTAFU
                parseEnterBootLoaderCmdResponse(respBytes);
            } else if ((Utils.getStringSharedPreference(mContext, Constants.PREF_BOOTLOADER_STATE)).equalsIgnoreCase("" + BootLoaderCommands_v1.SET_APP_METADATA)) {
                parseSetAppMetadataResponse(respBytes);
            } else if ((Utils.getStringSharedPreference(mContext, Constants.PREF_BOOTLOADER_STATE)).equalsIgnoreCase("" + BootLoaderCommands_v1.SEND_DATA)) {
                parseSendDataCmdResponse(respBytes);
            } else if ((Utils.getStringSharedPreference(mContext, Constants.PREF_BOOTLOADER_STATE)).equalsIgnoreCase("" + BootLoaderCommands_v1.PROGRAM_DATA)) {
                parseProgramDataCmdResponse(respBytes);
            } else if ((Utils.getStringSharedPreference(mContext, Constants.PREF_BOOTLOADER_STATE)).equalsIgnoreCase("" + BootLoaderCommands_v1.VERIFY_APP)) {
                parseVerifyAppResponse(respBytes);
            } else if ((Utils.getStringSharedPreference(mContext, Constants.PREF_BOOTLOADER_STATE)).equalsIgnoreCase("" + BootLoaderCommands_v1.EXIT_BOOTLOADER)) {
                parseExitBootloaderCmdResponse(respBytes);
            } else {
                Logger.e("Unknown PREF_BOOTLOADER_STATE: " + Utils.getStringSharedPreference(mContext, Constants.PREF_BOOTLOADER_STATE));
            }
        }
    }

    private void parseEnterBootLoaderCmdResponse(byte[] response) {
        Logger.e("EnterBootloader response>>>>>" + Utils.byteArrayToHex(response));
        int statusCode = ConvertUtils.byteArrayToIntLittleEndian(response, RESP_STATUS_CODE_START, RESP_STATUS_CODE_SIZE);
        switch (statusCode) {
            case CASE_SUCCESS:
                Logger.i("CYRET_SUCCESS");
                int dataPos = RESP_DATA_START;
                int siliconIdLength = 4;
                byte[] siliconId = ConvertUtils.byteArraySubset(response, dataPos, siliconIdLength);

                dataPos += siliconIdLength;
                int siliconRevLength = 1;
                byte[] siliconRev = ConvertUtils.byteArraySubset(response, dataPos, siliconRevLength);
                byte siliconRevision = siliconRev.length > 0 ? siliconRev[0] : 0;

                dataPos += siliconRevLength;
                int btldrSdkVerLength = 3;
                byte[] btldrSdkVer = ConvertUtils.byteArraySubset(response, dataPos, btldrSdkVerLength);

                Intent otaStatusV1Intent = new Intent(BluetoothLeService.ACTION_OTA_STATUS_V1);
                Bundle bundle = new Bundle();
                bundle.putByteArray(Constants.EXTRA_SILICON_ID, siliconId);
                bundle.putByte(Constants.EXTRA_SILICON_REV, siliconRevision);
                bundle.putByteArray(Constants.EXTRA_BTLDR_SDK_VER, btldrSdkVer);
                otaStatusV1Intent.putExtras(bundle);
                BluetoothLeService.sendGlobalBroadcastIntent(mContext, otaStatusV1Intent);
                break;
            default:
                broadCastErrors(statusCode);
                Logger.i("CYRET_ERROR");
                break;
        }
    }

    private void parseSetAppMetadataResponse(byte[] response) {
        Logger.e("SetActiveApplication response>>>>>" + Utils.byteArrayToHex(response));
        int statusCode = ConvertUtils.byteArrayToIntLittleEndian(response, RESP_STATUS_CODE_START, RESP_STATUS_CODE_SIZE);
        switch (statusCode) {
            case CASE_SUCCESS:
                Logger.i("CYRET_SUCCESS");
                Intent otaStatusV1Intent = new Intent(BluetoothLeService.ACTION_OTA_STATUS_V1);
                Bundle bundle = new Bundle();
                otaStatusV1Intent.putExtras(bundle);
                BluetoothLeService.sendGlobalBroadcastIntent(mContext, otaStatusV1Intent);
                break;
            default:
                broadCastErrors(statusCode);
                Logger.i("CYRET_ERROR");
                break;
        }
    }

    private void parseSendDataCmdResponse(byte[] response) {
        Logger.e("SendData response>>>>>" + Utils.byteArrayToHex(response));
        int statusCode = ConvertUtils.byteArrayToIntLittleEndian(response, RESP_STATUS_CODE_START, RESP_STATUS_CODE_SIZE);
        switch (statusCode) {
            case CASE_SUCCESS:
                Logger.i("CYRET_SUCCESS");
                Intent otaStatusV1Intent = new Intent(BluetoothLeService.ACTION_OTA_STATUS_V1);
                Bundle bundle = new Bundle();
                otaStatusV1Intent.putExtras(bundle);
                BluetoothLeService.sendGlobalBroadcastIntent(mContext, otaStatusV1Intent);
                break;
            default:
                broadCastErrors(statusCode);
                Logger.i("CYRET_ERROR");
                break;
        }
    }

    private void parseProgramDataCmdResponse(byte[] response) {
        Logger.e("ProgramData response>>>>>" + Utils.byteArrayToHex(response));
        int statusCode = ConvertUtils.byteArrayToIntLittleEndian(response, RESP_STATUS_CODE_START, RESP_STATUS_CODE_SIZE);
        switch (statusCode) {
            case CASE_SUCCESS:
                Logger.i("CYRET_SUCCESS");
                Intent otaStatusV1Intent = new Intent(BluetoothLeService.ACTION_OTA_STATUS_V1);
                Bundle bundle = new Bundle();
                otaStatusV1Intent.putExtras(bundle);
                BluetoothLeService.sendGlobalBroadcastIntent(mContext, otaStatusV1Intent);
                break;
            default:
                broadCastErrors(statusCode);
                Logger.i("CYRET_ERROR");
                break;
        }
    }

    private void parseVerifyAppResponse(byte[] response) {
        Logger.e("VerifyApplication response>>>>>" + Utils.byteArrayToHex(response));
        int statusCode = ConvertUtils.byteArrayToIntLittleEndian(response, RESP_STATUS_CODE_START, RESP_STATUS_CODE_SIZE);
        switch (statusCode) {
            case CASE_SUCCESS:
                Logger.i("CYRET_SUCCESS");
                Intent otaStatusV1Intent = new Intent(BluetoothLeService.ACTION_OTA_STATUS_V1);
                Bundle bundle = new Bundle();
                int verifyStatus = ConvertUtils.byteArrayToIntLittleEndian(response, RESP_DATA_START, 1);
                bundle.putByte(Constants.EXTRA_VERIFY_APP_STATUS, (byte) verifyStatus);
                otaStatusV1Intent.putExtras(bundle);
                BluetoothLeService.sendGlobalBroadcastIntent(mContext, otaStatusV1Intent);
                break;
            default:
                broadCastErrors(statusCode);
                Logger.i("CYRET_ERROR");
                break;
        }
    }

    private void parseExitBootloaderCmdResponse(byte[] response) {
        Logger.e("ExitBootloader response>>>>>" + Utils.byteArrayToHex(response));
        Intent otaStatusV1Intent = new Intent(BluetoothLeService.ACTION_OTA_STATUS_V1);
        Bundle bundle = new Bundle();
        otaStatusV1Intent.putExtras(bundle);
        BluetoothLeService.sendGlobalBroadcastIntent(mContext, otaStatusV1Intent);
    }

    private void broadCastErrorMessage(String errorMessage) {
        Intent otaStatusV1Intent = new Intent(BluetoothLeService.ACTION_OTA_STATUS_V1);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA_ERROR_OTA, errorMessage);
        otaStatusV1Intent.putExtras(bundle);
        BluetoothLeService.sendGlobalBroadcastIntent(mContext, otaStatusV1Intent);
    }

    private void broadCastErrors(int errorKey) {
        switch (errorKey) {
            case CASE_ERR_FILE:
                Logger.i("CYRET_ERR_FILE");
                broadCastErrorMessage(CYRET_ERR_FILE);
                break;
            case CASE_ERR_EOF:
                Logger.i("CYRET_ERR_EOF");
                broadCastErrorMessage(CYRET_ERR_EOF);
                break;
            case CASE_ERR_LENGTH:
                Logger.i("CYRET_ERR_LENGTH");
                broadCastErrorMessage(CYRET_ERR_LENGTH);
                break;
            case CASE_ERR_DATA:
                Logger.i("CYRET_ERR_DATA");
                broadCastErrorMessage(CYRET_ERR_DATA);
                break;
            case CASE_ERR_CMD:
                Logger.i("CYRET_ERR_CMD");
                broadCastErrorMessage(CYRET_ERR_CMD);
                break;
            case CASE_ERR_DEVICE:
                Logger.i("CYRET_ERR_DEVICE");
                broadCastErrorMessage(CYRET_ERR_DEVICE);
                break;
            case CASE_ERR_VERSION:
                Logger.i("CYRET_ERR_VERSION");
                broadCastErrorMessage(CYRET_ERR_VERSION);
                break;
            case CASE_ERR_CHECKSUM:
                Logger.i("CYRET_ERR_CHECKSUM");
                broadCastErrorMessage(CYRET_ERR_CHECKSUM);
                break;
            case CASE_ERR_ARRAY:
                Logger.i("CYRET_ERR_ARRAY");
                broadCastErrorMessage(CYRET_ERR_ARRAY);
                break;
            case CASE_ERR_ROW:
                Logger.i("CYRET_ERR_ROW");
                broadCastErrorMessage(CYRET_ERR_ROW);
                break;
            case CASE_BTLDR:
                Logger.i("CYRET_BTLDR");
                broadCastErrorMessage(CYRET_BTLDR);
                break;
            case CASE_ERR_APP:
                Logger.i("CYRET_ERR_APP");
                broadCastErrorMessage(CYRET_ERR_APP);
                break;
            case CASE_ERR_ACTIVE:
                Logger.i("CYRET_ERR_ACTIVE");
                broadCastErrorMessage(CYRET_ERR_ACTIVE);
                break;
            case CASE_ERR_UNK:
                Logger.i("CYRET_ERR_UNK");
                broadCastErrorMessage(CYRET_ERR_UNK);
                break;
            case CASE_ABORT:
                Logger.i("CYRET_ABORT");
                broadCastErrorMessage(CYRET_ABORT);
                break;
            default:
                Logger.i("CYRET_DEFAULT");
                break;
        }
    }
}
