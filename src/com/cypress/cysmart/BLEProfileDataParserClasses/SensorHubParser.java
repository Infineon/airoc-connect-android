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

package com.cypress.cysmart.BLEProfileDataParserClasses;

import android.bluetooth.BluetoothGattCharacteristic;

import com.cypress.cysmart.CommonUtils.Logger;

/**
 * Class used for parsing Sensor hub related information
 */
public class SensorHubParser {
    private static final int FIRST_BITMASK = 0x01;
    public static final int SECOND_BITMASK = FIRST_BITMASK << 1;
    public static final int THIRD_BITMASK = FIRST_BITMASK << 2;
    public static final int FOURTH_BITMASK = FIRST_BITMASK << 3;
    public static final int FIFTH_BITMASK = FIRST_BITMASK << 4;
    public static final int SIXTH_BITMASK = FIRST_BITMASK << 5;
    public static final int SEVENTH_BITMASK = FIRST_BITMASK << 6;
    public static final int EIGTH_BITMASK = FIRST_BITMASK << 7;

    public static int getAcceleroMeterXYZReading(
            BluetoothGattCharacteristic characteristic) {

        int mAccXYZ = characteristic.getIntValue(
                BluetoothGattCharacteristic.FORMAT_UINT16, 0);
        return mAccXYZ;
    }

    public static float getThermometerReading(
            BluetoothGattCharacteristic characteristic) {

        float mTemp = characteristic.getFloatValue(
                BluetoothGattCharacteristic.FORMAT_FLOAT, 0);
        return mTemp;
    }

    public static int getBarometerReading(
            BluetoothGattCharacteristic characteristic) {

        int mPressure = characteristic.getIntValue(
                BluetoothGattCharacteristic.FORMAT_UINT16, 0);
        Logger.w("pressure " + mPressure);
        return mPressure;
    }

    public static int getSensorScanIntervalReading(
            BluetoothGattCharacteristic characteristic) {

        int mScaninterval = characteristic.getIntValue(
                BluetoothGattCharacteristic.FORMAT_UINT8, 0);
        return mScaninterval;
    }

    public static int getSensorTypeReading(
            BluetoothGattCharacteristic characteristic) {

        int mSensorType = characteristic.getIntValue(
                BluetoothGattCharacteristic.FORMAT_UINT8, 0);
        return mSensorType;
    }

    public static int getFilterConfiguration(
            BluetoothGattCharacteristic characteristic) {
        int mFilterConfiguration = characteristic.getIntValue(
                BluetoothGattCharacteristic.FORMAT_UINT8, 0);
        return mFilterConfiguration;
    }

    public static int getThresholdValue(
            BluetoothGattCharacteristic characteristic) {
        int mThreshold = characteristic.getIntValue(
                BluetoothGattCharacteristic.FORMAT_UINT16, 0);
        return mThreshold;
    }
}
