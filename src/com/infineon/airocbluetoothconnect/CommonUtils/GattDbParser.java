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

package com.infineon.airocbluetoothconnect.CommonUtils;

import android.bluetooth.BluetoothGattService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Utility class to parse GATT DB services from into lists of logically connected services
 */
public class GattDbParser {
    // UUID key
    private static final String LIST_UUID = "UUID";
    // TODO: refactor this. Sensor Hub probably odd. Rest is also questionable
    private final ArrayList<HashMap<String, BluetoothGattService>> mGattServiceData = new ArrayList<>();
    private final ArrayList<HashMap<String, BluetoothGattService>> mGattServiceFindMeData = new ArrayList<>();
    private final ArrayList<HashMap<String, BluetoothGattService>> mGattServiceProximityData = new ArrayList<>();
    private final ArrayList<HashMap<String, BluetoothGattService>> mGattServiceSensorHubData = new ArrayList<>();
    private final ArrayList<HashMap<String, BluetoothGattService>> mGattDbServiceData = new ArrayList<>();
    private final ArrayList<HashMap<String, BluetoothGattService>> mGattServiceMasterData = new ArrayList<>();

    // Getters for the collections of services
    public ArrayList<HashMap<String, BluetoothGattService>> getGattServiceData() {
        return mGattServiceData;
    }

    public ArrayList<HashMap<String, BluetoothGattService>> getGattServiceFindMeData() {
        return mGattServiceFindMeData;
    }

    public ArrayList<HashMap<String, BluetoothGattService>> getGattServiceProximityData() {
        return mGattServiceProximityData;
    }

    public ArrayList<HashMap<String, BluetoothGattService>> getGattServiceSensorHubData() {
        return mGattServiceSensorHubData;
    }

    public ArrayList<HashMap<String, BluetoothGattService>> getGattDbServiceData() {
        return mGattDbServiceData;
    }

    public ArrayList<HashMap<String, BluetoothGattService>> getGattServiceMasterData() {
        return mGattServiceMasterData;
    }

    /**
     * Getting the GATT Services
     *
     * @param gattServices : list of {@link BluetoothGattService} to process
     */
    public void prepareGattServices(List<BluetoothGattService> gattServices) {
        // Optimization code for Sensor Hub
        if (isSensorHubPresent(gattServices)) {
            prepareSensorHubData(gattServices);
        } else {
            prepareData(gattServices);
        }
    }

    /**
     * Check whether SensorHub related services are present in the discovered
     * services
     *
     * @param gattServices: list of {@link BluetoothGattService} to process
     * @return {@link Boolean}
     */
    private boolean isSensorHubPresent(List<BluetoothGattService> gattServices) {
        boolean present = false;
        if(gattServices == null)
        {
            return present;
        }

        for (BluetoothGattService gattService : gattServices) {
            UUID uuid = gattService.getUuid();
            if (uuid.equals(UUIDDatabase.UUID_BAROMETER_SERVICE)) {
                present = true;
            }
        }
        return present;
    }

    private void prepareSensorHubData(List<BluetoothGattService> gattServices) {
        boolean mGattSet = false;
        boolean mSensorHubSet = false;

        if (gattServices == null) {
            return;
        }
        // Clear all array list before entering values.
        mGattServiceData.clear();
        mGattServiceMasterData.clear();
        mGattServiceSensorHubData.clear();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, BluetoothGattService> mCurrentServiceData = new HashMap<>();
            UUID uuid = gattService.getUuid();
            // Optimization code for SensorHub Profile
            if (uuid.equals(UUIDDatabase.UUID_LINK_LOSS_SERVICE)
                    || uuid.equals(UUIDDatabase.UUID_TRANSMISSION_POWER_SERVICE)
                    || uuid.equals(UUIDDatabase.UUID_IMMEDIATE_ALERT_SERVICE)
                    || uuid.equals(UUIDDatabase.UUID_BAROMETER_SERVICE)
                    || uuid.equals(UUIDDatabase.UUID_ACCELEROMETER_SERVICE)
                    || uuid.equals(UUIDDatabase.UUID_ANALOG_TEMPERATURE_SERVICE)
                    || uuid.equals(UUIDDatabase.UUID_BATTERY_SERVICE)
                    || uuid.equals(UUIDDatabase.UUID_DEVICE_INFORMATION_SERVICE)) {
                mCurrentServiceData.put(LIST_UUID, gattService);
                mGattServiceMasterData.add(mCurrentServiceData);
                if (!mGattServiceSensorHubData.contains(mCurrentServiceData)) {
                    mGattServiceSensorHubData.add(mCurrentServiceData);
                }
                if (!mSensorHubSet
                        && uuid.equals(UUIDDatabase.UUID_BAROMETER_SERVICE)) {
                    mSensorHubSet = true;
                    mGattServiceData.add(mCurrentServiceData);
                }

            }
            // Optimization code for GATTDB
            else if (uuid.equals(UUIDDatabase.UUID_GENERIC_ACCESS_SERVICE)
                    || uuid.equals(UUIDDatabase.UUID_GENERIC_ATTRIBUTE_SERVICE)) {
                mCurrentServiceData.put(LIST_UUID, gattService);
                mGattDbServiceData.add(mCurrentServiceData);
                if (!mGattSet) {
                    mGattSet = true;
                    mGattServiceData.add(mCurrentServiceData);
                }
            } else {
                mCurrentServiceData.put(LIST_UUID, gattService);
                mGattServiceMasterData.add(mCurrentServiceData);
                mGattServiceData.add(mCurrentServiceData);
            }
        }
    }

    /**
     * Prepare GATTServices data.
     *
     * @param gattServices: list of {@link BluetoothGattService} to process
     */
    private void prepareData(List<BluetoothGattService> gattServices) {
        boolean mFindMeSet = false;
        boolean mProximitySet = false;
        boolean mGattSet = false;
        if (gattServices == null)
            return;
        // Clear all array list before entering values.
        mGattServiceData.clear();
        mGattServiceFindMeData.clear();
        mGattServiceMasterData.clear();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, BluetoothGattService> currentServiceData = new HashMap<>();
            UUID uuid = gattService.getUuid();
            // Optimization code for FindMe Profile
            if (uuid.equals(UUIDDatabase.UUID_IMMEDIATE_ALERT_SERVICE)) {
                currentServiceData.put(LIST_UUID, gattService);
                mGattServiceMasterData.add(currentServiceData);
                if (!mGattServiceFindMeData.contains(currentServiceData)) {
                    mGattServiceFindMeData.add(currentServiceData);
                }
                if (!mFindMeSet) {
                    mFindMeSet = true;
                    mGattServiceData.add(currentServiceData);
                }
            }
            // Optimization code for Proximity Profile
            else if (uuid.equals(UUIDDatabase.UUID_LINK_LOSS_SERVICE)
                    || uuid.equals(UUIDDatabase.UUID_TRANSMISSION_POWER_SERVICE)) {
                currentServiceData.put(LIST_UUID, gattService);
                mGattServiceMasterData.add(currentServiceData);
                if (!mGattServiceProximityData.contains(currentServiceData)) {
                    mGattServiceProximityData.add(currentServiceData);
                }
                if (!mProximitySet) {
                    mProximitySet = true;
                    mGattServiceData.add(currentServiceData);
                }
            }// Optimization code for GATTDB
            else if (uuid.equals(UUIDDatabase.UUID_GENERIC_ACCESS_SERVICE)
                    || uuid.equals(UUIDDatabase.UUID_GENERIC_ATTRIBUTE_SERVICE)) {
                currentServiceData.put(LIST_UUID, gattService);
                mGattDbServiceData.add(currentServiceData);
                if (!mGattSet) {
                    mGattSet = true;
                    mGattServiceData.add(currentServiceData);
                }
            } else {
                currentServiceData.put(LIST_UUID, gattService);
                mGattServiceMasterData.add(currentServiceData);
                mGattServiceData.add(currentServiceData);
            }
        }
    }
}
