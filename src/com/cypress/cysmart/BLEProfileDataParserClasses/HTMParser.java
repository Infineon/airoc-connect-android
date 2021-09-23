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
import android.content.Context;

import com.cypress.cysmart.CommonUtils.Logger;
import com.cypress.cysmart.CommonUtils.Utils;
import com.cypress.cysmart.R;

import java.util.ArrayList;

/**
 * Class used for parsing Health temperature related information
 */
public class HTMParser {

    private static ArrayList<String> mTempInfo = new ArrayList<String>();

    //Byte character format
    private static final String BYTE_CHAR_FORMAT = "%02X ";

    //Switch case Constants
    private static final int CASE_ARMPIT = 1;
    private static final int CASE_BODY = 2;
    private static final int CASE_EAR_LOBE = 3;
    private static final int CASE_FINGER = 4;
    private static final int CASE_GASTRO_INTESTINAL_TRACT = 5;
    private static final int CASE_MOUTH = 6;
    private static final int CASE_RECTUM = 7;
    private static final int CASE_TOE = 8;
    private static final int CASE_TYMPANUM = 9;

    /**
     * Parse the value of the Temperature Measurement characteristic
     *
     * @param characteristic
     * @return
     */
    public static ArrayList<String> parseTemperatureMeasurement(BluetoothGattCharacteristic characteristic, Context context) {
        String temperatureUnit = "";
        // For all other profiles, writes the data formatted in HEX.
        final byte[] data = characteristic.getValue();
        if (data != null && data.length > 0) {
            byte flagByte = data[0];
            if ((flagByte & 0x01) == 0) {
                temperatureUnit = context.getString(R.string.tt_celcius);
            } else {
                temperatureUnit = context.getString(R.string.tt_fahrenheit);
            }
        }
        final float temperatureValue = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_FLOAT, 1);
        Logger.i("temperature value: " + temperatureValue);

        mTempInfo.add(0, "" + temperatureValue);
        mTempInfo.add(1, temperatureUnit);
        return mTempInfo;
    }

    /**
     * Parse the value of the Temperature Type characteristic
     *
     * @param characteristic
     * @return
     */
    public static String parseTemperatureType(BluetoothGattCharacteristic characteristic, Context context) {
        String sensorLocation = "";
        final byte[] data = characteristic.getValue();
        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for (byte byteChar : data) {
                stringBuilder.append(Utils.formatForRootLocale(BYTE_CHAR_FORMAT, byteChar));
            }
            int location = Integer.valueOf(stringBuilder.toString().trim());
            switch (location) {
                case CASE_ARMPIT:
                    sensorLocation = context.getString(R.string.armpit);
                    break;
                case CASE_BODY:
                    sensorLocation = context.getString(R.string.body);
                    break;
                case CASE_EAR_LOBE:
                    sensorLocation = context.getString(R.string.ear);
                    break;
                case CASE_FINGER:
                    sensorLocation = context.getString(R.string.finger);
                    break;
                case CASE_GASTRO_INTESTINAL_TRACT:
                    sensorLocation = context.getString(R.string.intestine);
                    break;
                case CASE_MOUTH:
                    sensorLocation = context.getString(R.string.mouth);
                    break;
                case CASE_RECTUM:
                    sensorLocation = context.getString(R.string.rectum);
                    break;
                case CASE_TOE:
                    sensorLocation = context.getString(R.string.toe_1);
                    break;
                case CASE_TYMPANUM:
                    sensorLocation = context.getString(R.string.tympanum);
                    break;
                default:
                    sensorLocation = context.getString(R.string.reserverd);
                    break;
            }
        }
        return sensorLocation;
    }
}
