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

package com.infineon.airocbluetoothconnect.ListAdapters;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.infineon.airocbluetoothconnect.BLEConnectionServices.BluetoothLeService;
import com.infineon.airocbluetoothconnect.CommonUtils.GattAttributes;
import com.infineon.airocbluetoothconnect.CommonUtils.UUIDDatabase;
import com.infineon.airocbluetoothconnect.CommonUtils.Utils;
import com.infineon.airocbluetoothconnect.R;

import java.util.List;

/**
 * Adapter class for GattCharacteristics ListView
 */
public class GattCharacteriscticsListAdapter extends BaseAdapter {
    /**
     * BluetoothGattCharacteristic list
     */
    private List<BluetoothGattCharacteristic> mGattCharacteristics;
    private Context mContext;

    public GattCharacteriscticsListAdapter(Context mContext,
                                           List<BluetoothGattCharacteristic> list) {
        this.mContext = mContext;
        this.mGattCharacteristics = list;
    }

    @Override
    public int getCount() {
        return mGattCharacteristics.size();
    }

    @Override
    public Object getItem(int i) {
        return mGattCharacteristics.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        // General ListView optimization code.
        if (view == null) {
            LayoutInflater mInflator = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = mInflator.inflate(R.layout.gattdb_characteristics_list_item,
                    viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.serviceName = (TextView) view
                    .findViewById(R.id.txtservicename);
            viewHolder.propertyName = (TextView) view
                    .findViewById(R.id.txtstatus);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.serviceName.setSelected(true);
        BluetoothGattCharacteristic item = mGattCharacteristics.get(i);

        String name = GattAttributes.lookupUUID(item.getUuid(), Utils.getUuidShort(item.getUuid().toString()));

        //Report Reference lookup based on InstanceId
        if (item.getUuid().equals(UUIDDatabase.UUID_REPORT)) {
            name = GattAttributes.lookupReferenceRDK(item.getInstanceId(), name);
        }

        viewHolder.serviceName.setText(name);
        String properties;
        String read = null, write = null, notify = null;

        /**
         * Checking the various GattCharacteristics and listing in the ListView
         */
        if (BluetoothLeService.isPropertySupported(item, BluetoothGattCharacteristic.PROPERTY_READ)) {
            read = mContext.getString(R.string.gatt_services_read);
        }
        if (BluetoothLeService.isPropertySupported(item, BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) {
            write = mContext.getString(R.string.gatt_services_write);
        }
        if (BluetoothLeService.isPropertySupported(item, BluetoothGattCharacteristic.PROPERTY_NOTIFY)) {
            notify = mContext.getString(R.string.gatt_services_notify);
        }
        if (BluetoothLeService.isPropertySupported(item, BluetoothGattCharacteristic.PROPERTY_INDICATE)) {
            notify = mContext.getString(R.string.gatt_services_indicate);
        }
        // Handling multiple properties listing in the ListView
        if (read != null) {
            properties = read;
            if (write != null) {
                properties = properties + " & " + write;
            }
            if (notify != null) {
                properties = properties + " & " + notify;
            }
        } else {
            if (write != null) {
                properties = write;

                if (notify != null) {
                    properties = properties + " & " + notify;
                }
            } else {
                properties = notify;
            }
        }
        viewHolder.propertyName.setText(properties);
        return view;
    }

    /**
     * Holder class for the ListView variable
     */
    class ViewHolder {
        TextView serviceName, propertyName;

    }
}
