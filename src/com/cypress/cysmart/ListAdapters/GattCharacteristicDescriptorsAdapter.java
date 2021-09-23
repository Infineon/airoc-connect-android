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

package com.cypress.cysmart.ListAdapters;

import android.bluetooth.BluetoothGattDescriptor;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cypress.cysmart.CommonUtils.GattAttributes;
import com.cypress.cysmart.CommonUtils.Utils;
import com.cypress.cysmart.R;

import java.util.List;

/**
 * Adapter class for listing the GATT Characteristics
 */
public class GattCharacteristicDescriptorsAdapter extends BaseAdapter {
    /**
     * BluetoothGattCharacteristic list
     */
    private List<BluetoothGattDescriptor> mGattCharacteristics;

    private Context mContext;

    public GattCharacteristicDescriptorsAdapter(Context mContext,
                                                List<BluetoothGattDescriptor> list) {
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
            viewHolder.parameter = (TextView) view
                    .findViewById(R.id.parameter);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.serviceName.setSelected(true);
        BluetoothGattDescriptor item = mGattCharacteristics.get(i);
        String name = GattAttributes.lookupUUID(item.getUuid(), Utils.getUuidShort(item.getUuid().toString()));
        viewHolder.serviceName.setText(name);
        viewHolder.propertyName.setText("" + Utils.getUuidShort(item.getUuid().toString()));
        viewHolder.parameter.setText("UUID :");

        return view;
    }

    /**
     * Holder class for the ListView variable
     */
    class ViewHolder {
        TextView serviceName, propertyName, parameter;

    }

}

