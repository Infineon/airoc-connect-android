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

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.infineon.airocbluetoothconnect.DataModelClasses.NavigationDrawerModel;
import com.infineon.airocbluetoothconnect.R;

import java.util.ArrayList;

/**
 * Navigation drawer list adapter to display the list items inside navigation
 * drawer list view
 */
public class NavDrawerListAdapter extends BaseAdapter {

    private Context mContext;
    /**
     * Array list which holds NavigationDrawer data model
     */
    private ArrayList<NavigationDrawerModel> mNavDrawerItems;

    /**
     * Constructor for the NavDrawerListAdapter
     *
     * @param context
     * @param navDrawerItems
     */
    public NavDrawerListAdapter(Context context,
                                ArrayList<NavigationDrawerModel> navDrawerItems) {
        this.mContext = context;
        this.mNavDrawerItems = navDrawerItems;
    }

    @Override
    public int getCount() {
        return mNavDrawerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mNavDrawerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.fragment_drawer_list_item,
                    parent, false);
        }

        TextView itemTitle = (TextView) convertView.findViewById(R.id.title);
        ImageView itemIcon = (ImageView) convertView.findViewById(R.id.icon);

        itemIcon.setImageResource(mNavDrawerItems.get(position).getIcon());
        itemTitle.setText(mNavDrawerItems.get(position).getTitle());

        return convertView;
    }

}
