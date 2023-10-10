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

package com.infineon.airocbluetoothconnect.BLEProfileDataParserClasses;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;

import com.infineon.airocbluetoothconnect.CommonUtils.Utils;
import com.infineon.airocbluetoothconnect.R;

import java.util.ArrayList;

/**
 * Parser class for parsing the data related to HRM Profile
 */
public class HRMParser {

    private static final int FIRST_BITMASK = 0x01;
    private static final int FOURTH_BITMASK = FIRST_BITMASK << 3;
    private static final int FIFTH_BITMASK = FIRST_BITMASK << 4;

    //Switch case constants
    private static final int CASE_OTHER = 0;
    private static final int CASE_CHEST = 1;
    private static final int CASE_WRIST = 2;
    private static final int CASE_FINGER = 3;
    private static final int CASE_HAND = 4;
    private static final int CASE_EAR_LOBE = 5;
    private static final int CASE_FOOT = 6;
    private static final int HRM_FLAGS_LEN = 1;

    public static boolean isValidValue(BluetoothGattCharacteristic characteristic) {
        return characteristic.getValue().length > HRM_FLAGS_LEN;
    }

    /**
     * Getting the heart rate
     *
     * @param characteristic
     * @return String
     */
    public static String getHeartRate(BluetoothGattCharacteristic characteristic) {
        String result = "";
        if (isValidValue(characteristic)) {
            int format = -1;
            if (isHeartRateInUINT16(characteristic.getValue()[0])) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
            }
            final int heartRate = characteristic.getIntValue(format, 1);
            result = String.valueOf(heartRate);
        }
        return result;
    }
    /**
     * Getting the Energy Expended
     *
     * @param characteristic
     * @return String
     */
    public static String getEnergyExpended(BluetoothGattCharacteristic characteristic) {
        String result = "";
        if (isValidValue(characteristic)) {
            int eeval = 0;
            if (isEEpresent(characteristic.getValue()[0])) {
                if (isHeartRateInUINT16(characteristic.getValue()[0])) {
                    eeval = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 3);
                } else {
                    eeval = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 2);
                }
            }
            return String.valueOf(eeval);
        }
        return result;
    }

    /**
     * Getting the RR-Interval
     *
     * @param characteristic
     * @return ArrayList
     */
    public static ArrayList<Integer> getRRInterval(BluetoothGattCharacteristic characteristic) {
        ArrayList<Integer> rrinterval = new ArrayList<Integer>();
        if (isValidValue(characteristic)) {
            int length = characteristic.getValue().length;
            if (isEEpresent(characteristic.getValue()[0])) {
                if (isHeartRateInUINT16(characteristic.getValue()[0])) {
                    if (isRRintpresent(characteristic.getValue()[0])) {
                        int startoffset = 5;
                        for (int i = startoffset; i < length; i += 2) {
                            rrinterval.add(characteristic.getIntValue(
                                    BluetoothGattCharacteristic.FORMAT_UINT16, i));
                        }
                    }
                } else {
                    if (isRRintpresent(characteristic.getValue()[0])) {
                        int startoffset = 4;
                        for (int i = startoffset; i < length; i += 2) {
                            rrinterval.add(characteristic.getIntValue(
                                    BluetoothGattCharacteristic.FORMAT_UINT16, i));
                        }
                    }
                }
            } else {
                if (isHeartRateInUINT16(characteristic.getValue()[0])) {
                    if (isRRintpresent(characteristic.getValue()[0])) {
                        int startoffset = 3;
                        for (int i = startoffset; i < length; i += 2) {
                            rrinterval.add(characteristic.getIntValue(
                                    BluetoothGattCharacteristic.FORMAT_UINT16, i));
                        }
                    }
                } else {
                    if (isRRintpresent(characteristic.getValue()[0])) {
                        int startoffset = 2;
                        for (int i = startoffset; i < length; i += 2) {
                            rrinterval.add(characteristic.getIntValue(
                                    BluetoothGattCharacteristic.FORMAT_UINT16, i));
                        }
                    }
                }
            }
        }
        return rrinterval;
    }

    /**
     * Checking the RR-Interval Flag
     *
     * @param flags
     * @return boolean
     */
    private static boolean isRRintpresent(byte flags) {
        if ((flags & FIFTH_BITMASK) != 0)
            return true;
        return false;
    }

    /**
     * Checking the Energy Expended Flag
     *
     * @param flags
     * @return boolean
     */
    private static boolean isEEpresent(byte flags) {
        if ((flags & FOURTH_BITMASK) != 0)
            return true;
        return false;
    }

    /**
     * Checking the Heart rate value format Flag
     *
     * @param flags
     * @return boolean
     */
    private static boolean isHeartRateInUINT16(byte flags) {
        return (flags & 1) != 0;
    }

    public static String getSensorContactStatus(BluetoothGattCharacteristic characteristic) {
        String sensorContactStatus = "";
        byte[] data = characteristic.getValue();
        if (data != null && data.length >= HRM_FLAGS_LEN) {
            if ((data[0] & 0x06) == 0x06) {
                sensorContactStatus = "Detected";
            } else if ((data[0] & 0x06) == 0x04) {
                sensorContactStatus = "Not detected";
            } else {
                sensorContactStatus = "Not supported";
            }
        }
        return sensorContactStatus;
    }

    public static String getBodySensorLocation(
            BluetoothGattCharacteristic characteristic, Context context) {
        String bodySensorLocation = "";
        final byte[] data = characteristic.getValue();
        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for (byte byteChar : data)
                stringBuilder.append(Utils.formatForRootLocale("%02X ", byteChar));
            int bodySensor = Integer.valueOf(stringBuilder.toString().trim());
            switch (bodySensor) {
                case CASE_OTHER:
                    bodySensorLocation = context.getString(R.string.hrm_OTHER);
                    break;
                case CASE_CHEST:
                    bodySensorLocation = context.getString(R.string.hrm_CHEST);
                    break;
                case CASE_WRIST:
                    bodySensorLocation = context.getString(R.string.hrm_WRIST);
                    break;
                case CASE_FINGER:
                    bodySensorLocation = context.getString(R.string.hrm_FINGER);
                    break;
                case CASE_HAND:
                    bodySensorLocation = context.getString(R.string.hrm_HAND);
                    break;
                case CASE_EAR_LOBE:
                    bodySensorLocation = context.getString(R.string.hrm_EAR_LOBE);
                    break;
                case CASE_FOOT:
                    bodySensorLocation = context.getString(R.string.hrm_FOOT);
                    break;
                default:
                    bodySensorLocation = context.getString(R.string.hrm_RESERVED);
                    break;
            }

        }
        return bodySensorLocation;
    }
}
