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

import com.infineon.airocbluetoothconnect.CommonUtils.Utils;

import java.util.Calendar;

public class DateTimeParser {
    /**
     * Parses the date and time info.
     *
     * @param characteristic
     * @return time in human readable format
     */
    public static String parse(final BluetoothGattCharacteristic characteristic) {
        return parse(characteristic, 0);
    }

    /**
     * Parses the date and time info. This data has 7 bytes
     *
     * @param characteristic
     * @param offset         offset to start reading the time
     * @return time in human readable format
     */
    /* package */
    static String parse(final BluetoothGattCharacteristic characteristic, final int offset) {
        final int year = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset);
        final int month = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 2);
        final int day = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 3);
        final int hours = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 4);
        final int minutes = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 5);
        final int seconds = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 6);

        final Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, hours, minutes, seconds);

        return Utils.formatForRootLocale("%1$te %1$tb %1$tY, %1$tH:%1$tM:%1$tS", calendar);
    }
}
