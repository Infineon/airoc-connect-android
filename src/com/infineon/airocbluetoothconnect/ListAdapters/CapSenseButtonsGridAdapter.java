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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.infineon.airocbluetoothconnect.DataModelClasses.CapSenseButtonsGridModel;
import com.infineon.airocbluetoothconnect.R;

import java.util.ArrayList;

/**
 * Adapter class for CapSense Buttons. Uses CapSenseButtonsGridModel data model
 */
public class CapSenseButtonsGridAdapter extends
        ArrayAdapter<CapSenseButtonsGridModel> {

    private Context mContext;
    /**
     * Resource identifier
     */
    private int mResourceId;
    /**
     * CapSenseButtonsGridModel data list.
     */
    private ArrayList<CapSenseButtonsGridModel> mData = new ArrayList<CapSenseButtonsGridModel>();
    private ArrayList<Integer> mStatus = new ArrayList<Integer>();

    public CapSenseButtonsGridAdapter(Context context,
                                      ArrayList<CapSenseButtonsGridModel> data,
                                      ArrayList<Integer> statusMapping) {
        super(context, R.layout.carousel_fragment_item, data);
        this.mContext = context;
        this.mResourceId = R.layout.carousel_fragment_item;
        this.mData = data;
        this.mStatus = statusMapping;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        ViewHolder holder = null;
        int status8bit;
        int status16bit;
        // General GridView Optimization code
        if (itemView == null) {
            final LayoutInflater layoutInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = layoutInflater.inflate(R.layout.capsense_buttons_item,
                    parent, false);

            holder = new ViewHolder();
            holder.imgItem = (ImageView) itemView
                    .findViewById(R.id.button_image);
            holder.txtItem = (TextView) itemView.findViewById(R.id.txtItem);
            itemView.setTag(holder);
        } else {
            holder = (ViewHolder) itemView.getTag();
        }
        /**
         * Setting the ImageResource and title using the data model.
         */
        CapSenseButtonsGridModel item = mData.get(position);
        if (item != null) {
            holder.imgItem.setImageResource(item.getImage());
            holder.txtItem.setText(item.getTitle());
        }
        // Getting the status
        status8bit = mStatus.get(1);
        status16bit = mStatus.get(2);

        // Setting the status indication on the image
        if (position > 7) {
            int k = 1 << position - 8;
            if ((status16bit & k) > 0) {
                holder.imgItem.setImageResource(R.drawable.green_color_btn);
            } else {
                holder.imgItem.setImageResource(R.drawable.capsense_btn_bg);
            }
        } else {
            int k = 1 << position;
            if ((status8bit & k) > 0) {
                holder.imgItem.setImageResource(R.drawable.green_color_btn);
            } else {
                holder.imgItem.setImageResource(R.drawable.capsense_btn_bg);
            }
        }

        return itemView;
    }

    /**
     * Holder class for GridView items
     */
    static class ViewHolder {
        ImageView imgItem;
        TextView txtItem;
    }
}
