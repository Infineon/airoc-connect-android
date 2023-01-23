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

import com.infineon.airocbluetoothconnect.BLEServiceFragments.CSCService;
import com.infineon.airocbluetoothconnect.CommonUtils.Logger;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Class used for parsing Cycling speed and cadence related information
 */
public class CSCParser {

    private static ArrayList<String> mCSCInfo = new ArrayList<>(Collections.<String>nCopies(CSCParser.ARRAYLIST_SIZE, null)); // Setting initial SIZE to avoid IndexOutOfBoundsException
    public static final int INDEX_CYCLING_DISTANCE = 0;
    public static final int INDEX_CYCLING_CADENCE = 1;
    public static final int INDEX_GEAR_RATIO = 2;
    public static final int INDEX_CYCLING_EXTRA_SPEED = 3;
    public static final int INDEX_CYCLING_EXTRA_DISTANCE = 4;
    private static final int ARRAYLIST_SIZE = 5;

    private static String mCyclingExtraDistance;
    private static String mCyclingExtraSpeed;
    private static String mCyclingDistance;
    private static String mCyclingCadence;
    private static String mGearRatio;
    private static int mInitialWheelRevolutions = -1;
    private static int mLastWheelRevolutions = -1;
    private static int mLastWheelEventTime = -1;
    private static float mWheelCadence = -1F;
    private static int mLastCrankRevolutions = -1;
    private static int mLastCrankEventTime = -1;
    private static final int WHEEL_CONST = 65535;

    private static final float FLOAT_CONST_1024 = 1024F;
    private static final float FLOAT_CONST_1000 = 1000F;
    private static final float FLOAT_CONST_60 = 60F;

    private static final byte WHEEL_REVOLUTION_DATA_PRESENT = 0x01; // 1 bit
    private static final byte CRANK_REVOLUTION_DATA_PRESENT = 0x02; // 1 bit

    /**
     * Get the Running Speed and Cadence
     *
     * @param characteristic
     * @return ArrayList<String>
     */
    public static ArrayList<String> getCyclingSpeedCadence(BluetoothGattCharacteristic characteristic) {
        // Decode the new data
        int offset = 0;
        final int flags = characteristic.getValue()[offset]; // 1 byte
        offset += 1;

        final boolean wheelRevolutionPresent = (flags & WHEEL_REVOLUTION_DATA_PRESENT) > 0;
        final boolean crankRevolutionPresent = (flags & CRANK_REVOLUTION_DATA_PRESENT) > 0;

        // Cleanup
        for (int i = 0; i < ARRAYLIST_SIZE; ++i) {
            mCSCInfo.set(i, null);
        }

        if (wheelRevolutionPresent) {
            int cumulativeWheelRevolutions = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, offset).intValue();
            offset += 4;
            int lastWheelEventTime = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset).intValue();
            offset += 2;
            onWheelMeasurementReceived(cumulativeWheelRevolutions, lastWheelEventTime);
        }
        if (crankRevolutionPresent) {
            int cumulativeCrankRevolutions = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset).intValue();
            offset += 2;
            int lastCrankEventTime = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset).intValue();
            onCrankMeasurementReceived(cumulativeCrankRevolutions, lastCrankEventTime);
        }
        return new ArrayList<>(mCSCInfo);
    }

    private static void onWheelMeasurementReceived(final int cumulativeWheelRevolutions, final int lastWheelEventTime) {
        final double wheelCircumference = (2 * 3.14 * CSCService.mRadiusInt);
        if (mInitialWheelRevolutions < 0) {
            mInitialWheelRevolutions = cumulativeWheelRevolutions;
        }
        if (mLastWheelEventTime == lastWheelEventTime) {
            return;
        }
        if (mLastWheelRevolutions >= 0) {
            float timeFromLastCheckpoint;
            if (lastWheelEventTime < mLastWheelEventTime) {
                timeFromLastCheckpoint = (float) ((WHEEL_CONST + lastWheelEventTime) - mLastWheelEventTime) / FLOAT_CONST_1024;
            } else {
                timeFromLastCheckpoint = (float) (lastWheelEventTime - mLastWheelEventTime) / FLOAT_CONST_1024;
            }
            final int revolutionsFromLastCheckpoint = cumulativeWheelRevolutions - mLastWheelRevolutions;
            final int revolutionsFromInitialCheckpoint = cumulativeWheelRevolutions - mInitialWheelRevolutions;
            final float distanceFromLastCheckpoint = (float) (wheelCircumference * revolutionsFromLastCheckpoint) / FLOAT_CONST_1000;
            final float distanceFromInitialCheckpoint = (float) (wheelCircumference * revolutionsFromInitialCheckpoint) / FLOAT_CONST_1000;
            final float cumulativeDistance = (float) (wheelCircumference * cumulativeWheelRevolutions) / FLOAT_CONST_1000;
            final float speed = distanceFromLastCheckpoint / timeFromLastCheckpoint;

            mWheelCadence = (FLOAT_CONST_60 * (float) revolutionsFromLastCheckpoint) / timeFromLastCheckpoint;

            mCyclingDistance = "" + cumulativeDistance;
            mCSCInfo.set(INDEX_CYCLING_DISTANCE, mCyclingDistance);

            mCyclingExtraDistance = "" + distanceFromInitialCheckpoint;
            mCSCInfo.set(INDEX_CYCLING_EXTRA_DISTANCE, mCyclingExtraDistance);

            mCyclingExtraSpeed = "" + speed;
            mCSCInfo.set(INDEX_CYCLING_EXTRA_SPEED, mCyclingExtraSpeed);
            Logger.d("Wheel values are " + mCyclingDistance + " " + mCyclingExtraSpeed + " " + mCyclingExtraDistance);
        }
        mLastWheelRevolutions = cumulativeWheelRevolutions;
        mLastWheelEventTime = lastWheelEventTime;
    }

    private static void onCrankMeasurementReceived(int cumulativeCrankRevolutions, int lastCrankEventTime) {
        if (mLastCrankEventTime == lastCrankEventTime) {
            return;
        }
        if (mLastCrankRevolutions >= 0) {
            float timeFromLastCheckpoint;
            if (lastCrankEventTime < mLastCrankEventTime) {
                timeFromLastCheckpoint = (float) ((WHEEL_CONST + lastCrankEventTime) - mLastCrankEventTime) / FLOAT_CONST_1024;
            } else {
                timeFromLastCheckpoint = (float) (lastCrankEventTime - mLastCrankEventTime) / FLOAT_CONST_1024;
            }
            final int revolutionsFromLastCheckpoint = cumulativeCrankRevolutions - mLastCrankRevolutions;
            final float crankCadence = (FLOAT_CONST_60 * (float) revolutionsFromLastCheckpoint) / timeFromLastCheckpoint;
            if (crankCadence > 0.0F) {
                float gearRatio = mWheelCadence / crankCadence;
                mGearRatio = "" + gearRatio;
                mCSCInfo.set(INDEX_GEAR_RATIO, mGearRatio);

                mCyclingCadence = "" + (int) crankCadence;
                mCSCInfo.set(INDEX_CYCLING_CADENCE, mCyclingCadence);
                Logger.d("Crank values are " + mGearRatio + " " + mCyclingCadence);
            }
        }
        mLastCrankRevolutions = cumulativeCrankRevolutions;
        mLastCrankEventTime = lastCrankEventTime;
    }
}
