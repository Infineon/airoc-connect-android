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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cypress.cysmart.DataModelClasses.DataLoggerModel;
import com.cypress.cysmart.R;

import java.util.ArrayList;

/**
 * Adapter class for DataLogger ListView
 */
public class DataLoggerListAdapter extends BaseAdapter {
    /**
     * ListView title name
     */
    private ArrayList<DataLoggerModel> mListItemName;
    private Context mContext;

    public DataLoggerListAdapter(Context context, ArrayList<DataLoggerModel> list) {
        this.mContext = context;
        this.mListItemName = list;
    }

    @Override
    public int getCount() {
        return mListItemName.size();
    }

    @Override
    public Object getItem(int i) {
        return mListItemName.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        ViewHolderclass viewHolder;
        final DataLoggerModel dataLogger = mListItemName.get(position);
        // General ListView optimization code.
        if (view == null) {
            LayoutInflater mInflator = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = mInflator.inflate(R.layout.datalogger_list_item,
                    viewGroup, false);
            viewHolder = new ViewHolderclass();
            viewHolder.serviceName = (TextView) view
                    .findViewById(R.id.txtservicename);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderclass) view.getTag();
        }
        viewHolder.serviceName.setSelected(true);
        viewHolder.serviceName.setText(dataLogger.getLogName());
        return view;
    }

    /**
     * Holder class for ListView items
     */

    class ViewHolderclass {
        TextView serviceName;
      }

}
