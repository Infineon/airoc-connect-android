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

package com.infineon.airocbluetoothconnect.OTAFirmwareUpdate;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.infineon.airocbluetoothconnect.BLEConnectionServices.BluetoothLeService;
import com.infineon.airocbluetoothconnect.CommonUtils.CheckSumUtils;
import com.infineon.airocbluetoothconnect.CommonUtils.Constants;
import com.infineon.airocbluetoothconnect.CommonUtils.ConvertUtils;
import com.infineon.airocbluetoothconnect.CommonUtils.Logger;
import com.infineon.airocbluetoothconnect.CommonUtils.Utils;
import com.infineon.airocbluetoothconnect.DataModelClasses.OTAFlashRowModel_v0;
import com.infineon.airocbluetoothconnect.R;

import java.util.ArrayList;

public class OTAFUHandler_v0 extends OTAFUHandlerBase implements FileReadStatusUpdater {

    //header data variables
    private static String mSiliconID;
    private static String mSiliconRev;
    private static String mCheckSumType;

    private OTAFirmwareWrite_v0 mOtaFirmwareWrite;

    private int mTotalLines = 0;
    private ArrayList<OTAFlashRowModel_v0> mFlashRowList;
    private int mStartRow;
    private int mEndRow;
    private final int mMaxDataSize;

    public OTAFUHandler_v0(Fragment fragment, NotificationHandler notificationHandler, View view, BluetoothGattCharacteristic otaCharacteristic, byte activeApp, long securityKey, String filepath, OTAFUHandlerCallback callback) {
        super(fragment, notificationHandler, view, otaCharacteristic, activeApp, securityKey, filepath, callback);
        //Prefer WriteNoResponse over WriteWithResponse
        if ((otaCharacteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) != 0) {
            this.mMaxDataSize = BootLoaderCommands_v0.WRITE_NO_RESP_MAX_DATA_SIZE;
        } else {
            this.mMaxDataSize = BootLoaderCommands_v0.WRITE_WITH_RESP_MAX_DATA_SIZE;
        }
    }

    @Override
    public void prepareFileWrite() {
        mOtaFirmwareWrite = new OTAFirmwareWrite_v0(mOtaCharacteristic);

        /**
         * Always start the programming from the first line
         */
        Utils.setIntSharedPreference(getActivity(), Constants.PREF_PROGRAM_ROW_NO, 0);
        Utils.setIntSharedPreference(getActivity(), Constants.PREF_PROGRAM_ROW_START_POS, 0);

        /**
         * Custom file read class initialisation
         */
        try {
            final CustomFileReader_v0 customFileReader = new CustomFileReader_v0(mFilepath);
            customFileReader.setFileReadStatusUpdater(this);

            /**
             * CYACD Header information
             */
            String[] headerData = customFileReader.analyseFileHeader();
            mSiliconID = headerData[0];
            mSiliconRev = headerData[1];
            mCheckSumType = headerData[2];

            /**
             * Reads the file content and provides a 1 second delay
             */
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mPrepareFileWriteEnabled) {

                        try {
                            //Getting the total lines to write
                            mTotalLines = customFileReader.getTotalLines();
                            //Getting the data lines
                            mFlashRowList = customFileReader.readDataLines();
                        } catch (IndexOutOfBoundsException e) {
                            /*
                            Catches invalid files
                             */
                            showErrorDialogMessage(getResources().getString(R.string.ota_alert_invalid_file), true);
                        } catch (NullPointerException e) {
                            /*
                            Catches invalid files
                             */
                            showErrorDialogMessage(getResources().getString(R.string.ota_alert_invalid_file), true);
                        }
                    }
                }
            }, 1000);
        } catch (IndexOutOfBoundsException e) {
            /*
            Catches invalid files
             */
            showErrorDialogMessage(getResources().getString(R.string.ota_alert_invalid_file), true);
        } catch (NullPointerException e) {
            /*
            Catches invalid files
             */
            showErrorDialogMessage(getResources().getString(R.string.ota_alert_invalid_file), true);
        }
    }

    @Override
    public void onFileReadProgressUpdate(int fileLine) {
        /**
         * All data lines read and stored to data model
         */
        if (mTotalLines == fileLine) {
            mProgressText.setText(getActivity().getResources().getText(R.string.ota_file_read_complete));
            Utils.setStringSharedPreference(getActivity(), Constants.PREF_BOOTLOADER_STATE, "" + BootLoaderCommands_v0.ENTER_BOOTLOADER);
            setFileUpgradeStarted(true);
            generatePendingNotification(getActivity());

            sendEnterBootloaderCmd();
        }
    }

    private void sendEnterBootloaderCmd() {
        byte[] securityKey = null;
        if (mSecurityKey != Constants.NO_SECURITY_KEY) {
            securityKey = new byte[Constants.SECURITY_KEY_SIZE];
            for (int i = 0; i < Constants.SECURITY_KEY_SIZE; ++i) {
                securityKey[i] = (byte) (mSecurityKey >> (8 * i));
            }
        }
        mOtaFirmwareWrite.OTAEnterBootLoaderCmd(mCheckSumType, securityKey);
        mProgressText.setText(getActivity().getResources().getText(R.string.ota_enter_bootloader));
    }

    @Override
    public void processOTAStatus(String status, Bundle extras) {
        if (status.equalsIgnoreCase("" + BootLoaderCommands_v0.ENTER_BOOTLOADER)) {
            if (extras.containsKey(Constants.EXTRA_SILICON_ID) && extras.containsKey(Constants.EXTRA_SILICON_REV)) {
                String siliconIDReceived = extras.getString(Constants.EXTRA_SILICON_ID);
                String siliconRevReceived = extras.getString(Constants.EXTRA_SILICON_REV);
                if (siliconIDReceived.equalsIgnoreCase(mSiliconID) && siliconRevReceived.equalsIgnoreCase(mSiliconRev)) {
                    /**
                     * SiliconID and SiliconRev Verified
                     */
                    if (mActiveApp != Constants.ACTIVE_APP_NO_CHANGE) {
                        sendGetAppStatusCmd();
                    } else {
                        sendGetFlashSizeCmd();
                    }
                } else {
                    /**
                     * Wrong Silicon ID and SiliconRev
                     */
                    showErrorDialogMessage(getActivity().getResources().getString(R.string.alert_message_silicon_id_mismatch_error), true);
                }
            }
        } else if (status.equalsIgnoreCase("" + BootLoaderCommands_v0.GET_APP_STATUS)) {
            int programRowNum = Utils.getIntSharedPreference(getActivity(), Constants.PREF_PROGRAM_ROW_NO);
            if (programRowNum == 0) {
                // The 1st time the GetAppStatus is called
                if (extras.containsKey(Constants.EXTRA_APP_ACTIVE)) {
                    int appActive = extras.getInt(Constants.EXTRA_APP_ACTIVE);
                    boolean isAppActive = appActive > 0;
                    if (isAppActive) {
                        /**
                         * Cannot re-program active application
                         */
                        showErrorDialogMessage(getActivity().getResources().getString(R.string.alert_programming_of_active_app_is_not_allowed), true);
                    } else {
                        sendGetFlashSizeCmd();
                    }
                }
            } else if (programRowNum == mFlashRowList.size() - 1) {
                // The 2nd time the GetAppStatus is called
                if (extras.containsKey(Constants.EXTRA_APP_VALID)) {
                    int appValid = extras.getInt(Constants.EXTRA_APP_VALID);
                    boolean isAppValid = appValid > 0;
                    if (isAppValid) { // It looks strange but it is so. The same logic is used by CySmart PC Tool.
                        showErrorDialogMessage(getActivity().getResources().getString(R.string.alert_invalid_active_app_programmed), true);
                    } else {
                        sendSetActiveAppCmd();
                    }
                }
            }
        } else if (status.equalsIgnoreCase("" + BootLoaderCommands_v0.GET_FLASH_SIZE)) {
            /**
             * Verifying the rows to be programmed within the bootloadable area of flash
             * not done for time being
             */
            if (extras.containsKey(Constants.EXTRA_START_ROW) && extras.containsKey(Constants.EXTRA_END_ROW)) {
                mStartRow = Integer.parseInt(extras.getString(Constants.EXTRA_START_ROW));
                mEndRow = Integer.parseInt(extras.getString(Constants.EXTRA_END_ROW));
            }
            int programRowNum = Utils.getIntSharedPreference(getActivity(), Constants.PREF_PROGRAM_ROW_NO);
            writeProgrammableData(programRowNum);
        } else if (status.equalsIgnoreCase("" + BootLoaderCommands_v0.SEND_DATA)) {
            /**
             * verifying the status and sending the next command
             * Changing the shared preference value
             */
            if (extras.containsKey(Constants.EXTRA_SEND_DATA_ROW_STATUS)) {
                String statusReceived = extras.getString(Constants.EXTRA_SEND_DATA_ROW_STATUS);
                if (statusReceived.equalsIgnoreCase("00")) {
                    //Success status received.Send programmable data
                    int programRowNum = Utils.getIntSharedPreference(getActivity(), Constants.PREF_PROGRAM_ROW_NO);
                    writeProgrammableData(programRowNum);
                }
            }
        } else if (status.equalsIgnoreCase("" + BootLoaderCommands_v0.PROGRAM_ROW)) {
            String statusReceived;
            if (extras.containsKey(Constants.EXTRA_PROGRAM_ROW_STATUS)) {
                statusReceived = extras.getString(Constants.EXTRA_PROGRAM_ROW_STATUS);
                if (statusReceived.equalsIgnoreCase("00")) {
                    sendVerifyRowCmd();
                }
            }
        } else if (status.equalsIgnoreCase("" + BootLoaderCommands_v0.VERIFY_ROW)) {
            String statusReceived, checksumReceived;
            if (extras.containsKey(Constants.EXTRA_VERIFY_ROW_STATUS) && extras.containsKey(Constants.EXTRA_VERIFY_ROW_CHECKSUM)) {
                statusReceived = extras.getString(Constants.EXTRA_VERIFY_ROW_STATUS);
                checksumReceived = extras.getString(Constants.EXTRA_VERIFY_ROW_CHECKSUM);

                if (statusReceived.equalsIgnoreCase("00")) {
                    /**
                     * Program Row Status Verified
                     * Sending Next command
                     */
                    int programRowNum = Utils.getIntSharedPreference(getActivity(), Constants.PREF_PROGRAM_ROW_NO);
                    //Getting the arrayID
                    OTAFlashRowModel_v0 modelData = mFlashRowList.get(programRowNum);
                    long rowMSB = Long.parseLong(modelData.mRowNo.substring(0, 2), 16);
                    long rowLSB = Long.parseLong(modelData.mRowNo.substring(2, 4), 16);

                    byte[] checksumVerify = new byte[6];
                    checksumVerify[0] = (byte) modelData.mRowCheckSum;
                    checksumVerify[1] = (byte) modelData.mArrayId;
                    checksumVerify[2] = (byte) rowMSB;
                    checksumVerify[3] = (byte) rowLSB;
                    checksumVerify[4] = (byte) (modelData.mDataLength);
                    checksumVerify[5] = (byte) ((modelData.mDataLength) >> 8);
                    String fileChecksumCalculated = Integer.toHexString(CheckSumUtils.calculateChecksumVerifyRow(6, checksumVerify));
                    int fileChecksumCalculatedLength = fileChecksumCalculated.length();
                    String fileChecksumByte = null;
                    if (fileChecksumCalculatedLength >= 2) {
                        fileChecksumByte = fileChecksumCalculated.substring((fileChecksumCalculatedLength - 2), fileChecksumCalculatedLength);
                    } else {
                        fileChecksumByte = "0" + fileChecksumCalculated;
                    }
                    if (fileChecksumByte.equalsIgnoreCase(checksumReceived)) {
                        programRowNum++;
                        //Shows ProgressBar status
                        showProgress(mProgressBarPosition, programRowNum, mFlashRowList.size());
                        if (programRowNum < mFlashRowList.size()) {
                            Utils.setIntSharedPreference(getActivity(), Constants.PREF_PROGRAM_ROW_NO, programRowNum);
                            Utils.setIntSharedPreference(getActivity(), Constants.PREF_PROGRAM_ROW_START_POS, 0);
                            writeProgrammableData(programRowNum);
                        }
                        if (programRowNum == mFlashRowList.size()) {
                            if (mActiveApp != Constants.ACTIVE_APP_NO_CHANGE) {
                                sendGetAppStatusCmd();
                            } else {
                                sendVerifyChecksumCmd();
                            }
                        }
                    } else {
                        showErrorDialogMessage(getActivity().getResources().getString(R.string.alert_message_checksum_error), false);
                    }
                }
            }
        } else if (status.equalsIgnoreCase("" + BootLoaderCommands_v0.VERIFY_CHECK_SUM)) {
            String statusReceived;
            if (extras.containsKey(Constants.EXTRA_VERIFY_CHECKSUM_STATUS)) {
                statusReceived = extras.getString(Constants.EXTRA_VERIFY_CHECKSUM_STATUS);
                if (statusReceived.equalsIgnoreCase("01")) {
                    /*
                     * Verify Status Verified
                     */
                    sendExitBootloaderCmd();
                }
            }
        } else if (status.equalsIgnoreCase("" + BootLoaderCommands_v0.SET_ACTIVE_APP)) {
            String statusReceived;
            if (extras.containsKey(Constants.EXTRA_SET_ACTIVE_APP)) {
                statusReceived = extras.getString(Constants.EXTRA_SET_ACTIVE_APP);
                if (statusReceived.equalsIgnoreCase("00")) {
                    sendExitBootloaderCmd();
                }
            }
        } else if (status.equalsIgnoreCase("" + BootLoaderCommands_v0.EXIT_BOOTLOADER)) {
            String statusReceived;
            if (extras.containsKey(Constants.EXTRA_VERIFY_EXIT_BOOTLOADER)) {
                statusReceived = extras.getString(Constants.EXTRA_VERIFY_EXIT_BOOTLOADER);
                Logger.e("Fragment Exit bootloader response>>" + statusReceived);
            }
            final BluetoothDevice device = BluetoothLeService.getRemoteDevice();
            mProgressText.setText(getActivity().getResources().getText(R.string.ota_end_success));
            if (isSecondFileUpdateNeeded()) {
                mNotificationHandler.completeProgress(getActivity(), R.string.ota_notification_stack_file);
            } else {
                mNotificationHandler.completeProgress(getActivity(), R.string.ota_end_success);
            }

            setFileUpgradeStarted(false);
            storeAndReturnDeviceAddress();
            BluetoothLeService.disconnect();
            BluetoothLeService.unpairDevice(device);
            Toast.makeText(getActivity(), getResources().getString(R.string.alert_message_bluetooth_disconnect), Toast.LENGTH_SHORT).show();
            Intent finishIntent = getActivity().getIntent();
            getActivity().finish();
            getActivity().overridePendingTransition(R.anim.slide_right, R.anim.push_right);
            startActivity(finishIntent);
            getActivity().overridePendingTransition(R.anim.slide_left, R.anim.push_left);
        }

        if (extras.containsKey(Constants.EXTRA_ERROR_OTA)) {
            String errorMessage = extras.getString(Constants.EXTRA_ERROR_OTA);
            showErrorDialogMessage(getActivity().getResources().getString(
                    R.string.alert_message_ota_error) + errorMessage, false);
        }
    }

    private void sendGetAppStatusCmd() {
        mOtaFirmwareWrite.OTAGetAppStatusCmd(mCheckSumType, mActiveApp);
        Utils.setStringSharedPreference(getActivity(), Constants.PREF_BOOTLOADER_STATE, "" + BootLoaderCommands_v0.GET_APP_STATUS);
        mProgressText.setText(getActivity().getResources().getText(R.string.ota_get_app_status));
    }

    private void sendGetFlashSizeCmd() {
        // Getting the arrayID
        OTAFlashRowModel_v0 modelData = mFlashRowList.get(0);
        byte[] data = new byte[1];
        data[0] = (byte) modelData.mArrayId;
        // Saving the array id locally
        Utils.setIntSharedPreference(getActivity(), Constants.PREF_ARRAY_ID, Byte.valueOf(data[0]));
        mOtaFirmwareWrite.OTAGetFlashSizeCmd(mCheckSumType, data);
        Utils.setStringSharedPreference(getActivity(), Constants.PREF_BOOTLOADER_STATE, "" + BootLoaderCommands_v0.GET_FLASH_SIZE);
        mProgressText.setText(getActivity().getResources().getText(R.string.ota_get_flash_size));
    }

    private void sendVerifyRowCmd() {
        int programRowNum = Utils.getIntSharedPreference(getActivity(), Constants.PREF_PROGRAM_ROW_NO);
        OTAFlashRowModel_v0 modelData = mFlashRowList.get(programRowNum);
        long rowMSB = Long.parseLong(modelData.mRowNo.substring(0, 2), 16);
        long rowLSB = Long.parseLong(modelData.mRowNo.substring(2, 4), 16);
        mOtaFirmwareWrite.OTAVerifyRowCmd(mCheckSumType, rowMSB, rowLSB, modelData);
        Utils.setStringSharedPreference(getActivity(), Constants.PREF_BOOTLOADER_STATE, "" + BootLoaderCommands_v0.VERIFY_ROW);
        mProgressText.setText(getActivity().getResources().getText(R.string.ota_verify_row));
    }

    private void sendVerifyChecksumCmd() {
        Utils.setIntSharedPreference(getActivity(), Constants.PREF_PROGRAM_ROW_NO, 0);
        Utils.setIntSharedPreference(getActivity(), Constants.PREF_PROGRAM_ROW_START_POS, 0);
        Utils.setStringSharedPreference(getActivity(), Constants.PREF_BOOTLOADER_STATE, "" + BootLoaderCommands_v0.VERIFY_CHECK_SUM);
        mOtaFirmwareWrite.OTAVerifyCheckSumCmd(mCheckSumType);
        mProgressText.setText(getActivity().getResources().
                getText(R.string.ota_verify_checksum));
    }

    private void sendSetActiveAppCmd() {
        mOtaFirmwareWrite.OTASetActiveAppCmd(mCheckSumType, mActiveApp);
        Utils.setStringSharedPreference(getActivity(), Constants.PREF_BOOTLOADER_STATE, "" + BootLoaderCommands_v0.SET_ACTIVE_APP);
        mProgressText.setText(getActivity().getResources().getText(R.string.ota_set_active_application));
    }

    private void sendExitBootloaderCmd() {
        mOtaFirmwareWrite.OTAExitBootloaderCmd(mCheckSumType);
        Utils.setStringSharedPreference(getActivity(), Constants.PREF_BOOTLOADER_STATE, "" + BootLoaderCommands_v0.EXIT_BOOTLOADER);
        mProgressText.setText(getActivity().getResources().getText(R.string.ota_end_bootloader));
    }

    private void writeProgrammableData(int rowPosition) {
        int startPosition = Utils.getIntSharedPreference(getActivity(), Constants.PREF_PROGRAM_ROW_START_POS);
        OTAFlashRowModel_v0 modelData = mFlashRowList.get(rowPosition);
        int rowNum = ConvertUtils.swapShort(Integer.parseInt(modelData.mRowNo.substring(0, 4), 16));
        Logger.e("CYACD Row: " + rowPosition + ", Start Pos: " + startPosition + ", Array Start Row: " + mStartRow + ", Array End Row: " + mEndRow + ", Array Row: " + rowNum);
        Logger.e("Array id: " + modelData.mArrayId + ", Shared Array id: " + Utils.getIntSharedPreference(getActivity(), Constants.PREF_ARRAY_ID));

        if (modelData.mArrayId != Utils.getIntSharedPreference(getActivity(), Constants.PREF_ARRAY_ID)) {
            /**
             * Writing the get flash command again to get the new row numbers
             * Changing the shared preference value
             */
            Utils.setIntSharedPreference(getActivity(), Constants.PREF_ARRAY_ID, modelData.mArrayId);
            byte[] data = new byte[1];
            data[0] = (byte) modelData.mArrayId;
            mOtaFirmwareWrite.OTAGetFlashSizeCmd(mCheckSumType, data);
            Utils.setStringSharedPreference(getActivity(), Constants.PREF_BOOTLOADER_STATE, "" + BootLoaderCommands_v0.GET_FLASH_SIZE);
            mProgressText.setText(getActivity().getResources().getText(R.string.ota_get_flash_size));
        } else {
            /**
             * Verify weather the program row number is within the acceptable range
             */
            if (rowNum >= mStartRow && rowNum <= mEndRow) {
                int verifyDataLength = modelData.mDataLength - startPosition;
                if (checkProgramRowCommandToSend(verifyDataLength)) {
                    long rowMSB = Long.parseLong(modelData.mRowNo.substring(0, 2), 16);
                    long rowLSB = Long.parseLong(modelData.mRowNo.substring(2, 4), 16);
                    int dataLength = modelData.mDataLength - startPosition;
                    byte[] dataToSend = new byte[dataLength];
                    for (int pos = 0; pos < dataLength; pos++) {
                        if (startPosition < modelData.mData.length) {
                            byte data = modelData.mData[startPosition];
                            dataToSend[pos] = data;
                            startPosition++;
                        } else {
                            break;
                        }
                    }
                    mOtaFirmwareWrite.OTAProgramRowCmd(mCheckSumType, rowMSB, rowLSB, modelData.mArrayId, dataToSend);
                    Utils.setStringSharedPreference(getActivity(), Constants.PREF_BOOTLOADER_STATE, "" + BootLoaderCommands_v0.PROGRAM_ROW);
                    Utils.setIntSharedPreference(getActivity(), Constants.PREF_PROGRAM_ROW_START_POS, 0);
                    mProgressText.setText(getActivity().getResources().
                            getText(R.string.ota_program_row));
                } else {
                    int dataLength = mMaxDataSize;
                    byte[] dataToSend = new byte[dataLength];
                    for (int pos = 0; pos < dataLength; pos++) {
                        if (startPosition < modelData.mData.length) {
                            byte data = modelData.mData[startPosition];
                            dataToSend[pos] = data;
                            startPosition++;
                        } else {
                            break;
                        }
                    }
                    mOtaFirmwareWrite.OTASendDataCmd(mCheckSumType, dataToSend);
                    Utils.setStringSharedPreference(getActivity(), Constants.PREF_BOOTLOADER_STATE, "" + BootLoaderCommands_v0.SEND_DATA);
                    Utils.setIntSharedPreference(getActivity(), Constants.PREF_PROGRAM_ROW_START_POS, startPosition);
                    mProgressText.setText(getActivity().getResources().
                            getText(R.string.ota_program_row));
                }
            } else {
                showErrorDialogMessage(getActivity().getResources().
                        getString(R.string.alert_message_row_out_of_bounds_error), true);
            }
        }
    }

    private boolean checkProgramRowCommandToSend(int totalSize) {
        if (totalSize <= mMaxDataSize) {
            return true;
        } else {
            return false;
        }
    }
}
