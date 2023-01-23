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

package com.infineon.airocbluetoothconnect.BLEProfileDataParserClasses;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;

import com.infineon.airocbluetoothconnect.CommonUtils.Constants;
import com.infineon.airocbluetoothconnect.CommonUtils.Logger;
import com.infineon.airocbluetoothconnect.CommonUtils.Utils;
import com.infineon.airocbluetoothconnect.R;

/**
 * Class used for parsing Blood pressure related information
 */
public class BloodPressureParser {

    /**
     * Get the Blood Pressure
     *
     * @param characteristic
     * @return string
     */
    public static String getSystolicBloodPressure(
            BluetoothGattCharacteristic characteristic) {
        String pressure;
        float valueSYS = characteristic.getFloatValue(
                BluetoothGattCharacteristic.FORMAT_SFLOAT, 1);
        pressure = Utils.formatForRootLocale("%3.3f", valueSYS);
        Logger.i("Systolic Pressure>>>>" + valueSYS);
        return pressure;
    }

    /**
     * Returns the Systolic pressure
     * @param characteristic
     * @return unit
     */
    public static String getSystolicBloodPressureUnit(
            BluetoothGattCharacteristic characteristic, Context context) {

        String unit;
        if (BloodPressureUnitsFlagSet(characteristic.getValue()[0])) {
            unit = context.getResources().getString(R.string.blood_pressure_kPa);

        } else {
            unit = context.getResources().getString(R.string.blood_pressure_mmHg);

        }
        return unit;
    }

    /**
     * Returns the  Diastolic pressure
     * @param characteristic
     * @return pressure
     */
    public static String getDiastolicBloodPressure(
            BluetoothGattCharacteristic characteristic) {
        float valueDIA = characteristic.getFloatValue(
                BluetoothGattCharacteristic.FORMAT_SFLOAT, 3);
        String pressure = Utils.formatForRootLocale("%3.3f", valueDIA);
        Logger.i("Diastolic Pressure>>>>" + valueDIA);
        return pressure;
    }


    /**
     * Returns the  Diastolic pressure
     * @param characteristic
     * @return unit
     */
    public static String getDiaStolicBloodPressureUnit(
            BluetoothGattCharacteristic characteristic, Context context) {

        String unit;
        if (BloodPressureUnitsFlagSet(characteristic.getValue()[0])) {
            unit = context.getResources().getString(R.string.blood_pressure_kPa);

        } else {
            unit = context.getResources().getString(R.string.blood_pressure_mmHg);

        }
        return unit;
    }


    /**
     * Checking the unitsFlag of blood Pressure
     *
     * @param flags
     * @return
     */
    private static boolean BloodPressureUnitsFlagSet(byte flags) {
        if ((flags & Constants.FIRST_BITMASK) != 0){
            return true;
        }else{
            return false;
        }

    }

}
