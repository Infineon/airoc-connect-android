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

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.infineon.airocbluetoothconnect.DataModelClasses.GlucoseRecord;
import com.infineon.airocbluetoothconnect.R;

/**
 * Adapter class for Glucose Spinner
 */
public class GlucoseSpinnerAdapter extends BaseAdapter {

    // Your sent context
    private Context mContext;
    // Your custom values for the spinner (User)
    private SparseArray<GlucoseRecord> mRecords;

    private final LayoutInflater mInflater;

    public GlucoseSpinnerAdapter(Context context, SparseArray<GlucoseRecord> records) {
        this.mRecords = records;
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }

    public int getCount() {
        return mRecords.size();
    }

    public GlucoseRecord getItem(int position) {
        return mRecords.valueAt(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = mInflater.inflate(R.layout.listitem_glucose_spinner, parent, false);

            final ViewHolder holder = new ViewHolder();
            holder.recordSequenceNo = (TextView) view.findViewById(R.id.record_sequence_no);
            holder.recordTime = (TextView) view.findViewById(R.id.record_time);
            holder.recordTimeOffset = (TextView) view.findViewById(R.id.record_time_offset);
            view.setTag(holder);
        }
        final GlucoseRecord record = (GlucoseRecord) getItem(position);
        if (record == null)
            return view; // this may happen during closing the activity
        final ViewHolder holder = (ViewHolder) view.getTag();
        holder.recordSequenceNo.setText(String.valueOf(record.sequenceNumber));
        holder.recordTime.setText(String.valueOf(record.time));
        holder.recordTimeOffset.setText(String.valueOf(record.timeOffset) + "mins");
        return view;
    }


    private class ViewHolder {
        private TextView recordSequenceNo;
        private TextView recordTime;
        private TextView recordTimeOffset;
    }

}