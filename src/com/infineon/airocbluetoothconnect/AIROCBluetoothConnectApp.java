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

package com.infineon.airocbluetoothconnect;

import android.app.Application;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Application model data class
 */
public class AIROCBluetoothConnectApp extends Application {

    public static Application mApplication;

    private ArrayList<HashMap<String, BluetoothGattService>> mGattServiceMasterData =
            new ArrayList<HashMap<String, BluetoothGattService>>();

    private List<BluetoothGattCharacteristic> mGattCharacteristics;
    private BluetoothGattCharacteristic mBluetoothGattCharacteristic;
    private BluetoothGattDescriptor mBluetoothGattDescriptor;

    @Override
    public void onCreate() {
        super.onCreate();
        AIROCBluetoothConnectApp.mApplication = this;
    }

    /**
     * getter method for Blue tooth GATT characteristic
     *
     * @return {@link BluetoothGattCharacteristic}
     */
    public BluetoothGattCharacteristic getBluetoothGattCharacteristic() {
        return mBluetoothGattCharacteristic;
    }

    /**
     * setter method for Blue tooth GATT characteristics
     *
     * @param bluetoothgattcharacteristic
     */
    public void setBluetoothGattCharacteristic(
            BluetoothGattCharacteristic bluetoothgattcharacteristic) {
        this.mBluetoothGattCharacteristic = bluetoothgattcharacteristic;
    }

    /**
     * getter method for Blue tooth GATT characteristic
     *
     * @return {@link BluetoothGattCharacteristic}
     */
    public BluetoothGattDescriptor getBluetoothGattDescriptor() {
        return mBluetoothGattDescriptor;
    }

    /**
     * setter method for Blue tooth GATT Descriptor
     *
     * @param bluetoothGattDescriptor
     */
    public void setBluetoothGattDescriptor(
            BluetoothGattDescriptor bluetoothGattDescriptor) {
        this.mBluetoothGattDescriptor = bluetoothGattDescriptor;
    }

    /**
     * getter method for blue tooth GATT Characteristic list
     *
     * @return {@link List<BluetoothGattCharacteristic>}
     */
    public List<BluetoothGattCharacteristic> getGattCharacteristics() {
        return mGattCharacteristics;
    }

    /**
     * setter method for blue tooth GATT Characteristic list
     *
     * @param gattCharacteristics
     */
    public void setGattCharacteristics(
            List<BluetoothGattCharacteristic> gattCharacteristics) {
        this.mGattCharacteristics = gattCharacteristics;
    }

    public ArrayList<HashMap<String, BluetoothGattService>> getGattServiceMasterData() {
        return mGattServiceMasterData;
    }

    public void setGattServiceMasterData(
            ArrayList<HashMap<String, BluetoothGattService>> gattServiceMasterData) {
        this.mGattServiceMasterData = gattServiceMasterData;
    }
}
