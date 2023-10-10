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

package com.infineon.airocbluetoothconnect.ListAdapters;

import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.infineon.airocbluetoothconnect.CommonUtils.GattAttributes;
import com.infineon.airocbluetoothconnect.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Adapter class for GattService ListView
 */
public class GattServiceListAdapter extends BaseAdapter {
    private ArrayList<HashMap<String, BluetoothGattService>> mGattServiceData;
    private Context mContext;

    public GattServiceListAdapter(Context mContext,
                                  ArrayList<HashMap<String, BluetoothGattService>> list) {
        this.mContext = mContext;
        this.mGattServiceData = list;
    }

    @Override
    public int getCount() {
        return mGattServiceData.size();
    }

    @Override
    public Object getItem(int i) {
        return mGattServiceData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolderclass viewHolder;
        // General ListView optimization code.
        if (view == null) {
            LayoutInflater mInflator = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = mInflator.inflate(R.layout.gattdb_services_list_item,
                    viewGroup, false);
            viewHolder = new ViewHolderclass();
            viewHolder.serviceName = (TextView) view
                    .findViewById(R.id.txtservicename);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderclass) view.getTag();
        }
        viewHolder.serviceName.setSelected(true);
        HashMap<String, BluetoothGattService> item = mGattServiceData.get(i);
        BluetoothGattService bgs = item.get("UUID");
        String name = GattAttributes.lookupUUID(
                bgs.getUuid(),
                bgs.getUuid().toString());


        viewHolder.serviceName.setText(name);
        return view;
    }

    /**
     * Holder class for holding the ListView elements
     */
    class ViewHolderclass {
        TextView serviceName;

    }

}

