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
import com.cypress.cysmart.CommonUtils.Utils;

import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Class used for parsing Running speed related information
 */
public class RSCParser {

    private static final int FIRST_BITMASK = 0x01;
    public static final int SECOND_BITMASK = FIRST_BITMASK << 1;
    public static final int THIRD_BITMASK = FIRST_BITMASK << 2;
    public static final int FOURTH_BITMASK = FIRST_BITMASK << 3;
    public static final int FIFTH_BITMASK = FIRST_BITMASK << 4;
    public static final int SIXTH_BITMASK = FIRST_BITMASK << 5;
    public static final int SEVENTH_BITMASK = FIRST_BITMASK << 6;
    public static final int EIGTH_BITMASK = FIRST_BITMASK << 7;
    private static ArrayList<String> mRscInfo = new ArrayList<String>();

    private static final int FORMAT_TYPE_20 = 20;
    private static final int FORMAT_TYPE_18 = 18;
    private static final int FORMAT_TYPE_17 = 17;

    private static final float FLOAT_CONST_256 = 256F;
    private static final float FLOAT_CONST_1000 = 1000F;
    private static final float FLOAT_CONST_N1F = -1F;
    private static final float FLOAT_CONST_3D6 = 3.6F;
    private static final float FLOAT_CONST_10 = 10F;

    private static final int ARRAYLIST_INDEX_0 = 0;
    private static final int ARRAYLIST_INDEX_1 = 1;

    private static final byte INSTANTANEOUS_STRIDE_LENGTH_PRESENT = 0x01; // 1 bit
    private static final byte TOTAL_DISTANCE_PRESENT = 0x02; // 1 bit
    private static final byte WALKING_OR_RUNNING_STATUS_BITS = 0x04; // 1 bit

    /**
     * Get the Running Speed and Cadence
     *
     * @param characteristic
     * @return ArrayList<String>
     */
    public static ArrayList<String> getRunningSpeedAndCadence(
            BluetoothGattCharacteristic characteristic) {

        String runningSpeed;
        String distanceRan;
        // Decode the new data
        int offset = 0;
        final byte flags = characteristic.getValue()[offset]; // 1 byte
        offset += 1;

        final boolean islmPresent = (flags & INSTANTANEOUS_STRIDE_LENGTH_PRESENT) > 0;
        final boolean tdPreset = (flags & TOTAL_DISTANCE_PRESENT) > 0;
        final boolean running = (flags & WALKING_OR_RUNNING_STATUS_BITS) > 0;

        boolean strideFlag;
        boolean totalDistFlag;
        boolean runOrWalkFlag;
        float speedValue;
        int offsetInc;
        int offsetValue;
        int offsetIncT;
        float floatConst;
        float distanceValue;
        int runOrWalk;
        if (islmPresent) {
            strideFlag = true;
        } else {
            strideFlag = false;
        }
        if (tdPreset) {
            totalDistFlag = true;
        } else {
            totalDistFlag = false;
        }
        if (running) {
            runOrWalkFlag = true;
        } else {
            runOrWalkFlag = false;
        }

        int receivedVal = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset);
        Logger.i("Running value received " + receivedVal);
        float value = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset).floatValue();
        speedValue = FLOAT_CONST_3D6 * (value / FLOAT_CONST_256);
        Logger.i("Running value shown " + speedValue);
        runningSpeed = "" + speedValue;
        mRscInfo.add(ARRAYLIST_INDEX_0, runningSpeed);
        offsetInc = offset + 2;
        offsetValue = characteristic.getIntValue(FORMAT_TYPE_17, offsetInc).intValue();
        offsetIncT = offsetInc + 1;
        floatConst = FLOAT_CONST_N1F;
        if (strideFlag) {
            floatConst = characteristic.getIntValue(FORMAT_TYPE_18, offsetIncT).intValue();
            offsetIncT += 2;
        }
        distanceValue = FLOAT_CONST_N1F;
        if (totalDistFlag) {
            distanceValue = (float) characteristic.getIntValue(FORMAT_TYPE_20, offsetIncT).intValue() / FLOAT_CONST_10;
            distanceValue = distanceValue / FLOAT_CONST_1000;
            NumberFormat nf = Utils.getNumberFormatForRootLocale();
            nf.setMinimumFractionDigits(2);
            nf.setMaximumFractionDigits(3);
            distanceRan = nf.format(distanceValue);
            mRscInfo.add(ARRAYLIST_INDEX_1, distanceRan);
        }
        if (runOrWalkFlag) {
            runOrWalk = 1;
        } else {
            runOrWalk = 0;
        }
        Logger.d("Running Values are " + speedValue + " " + offsetValue + " " + distanceValue + " " + floatConst + " " + runOrWalk);
        return mRscInfo;
    }

}
