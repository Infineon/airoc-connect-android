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

package com.infineon.airocbluetoothconnect.wearable.demo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.infineon.airocbluetoothconnect.R;
import com.infineon.airocbluetoothconnect.wearable.model.Variable;

public class VariableView extends LinearLayout {

    private TextView mName;
    protected VariableTargetProgressBar mProgress;

    public VariableView(Context context) {
        super(context);
        initializeViews(context);
    }

    public VariableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public VariableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
    }

    public void setVariable(Variable var) {
        mName.setText(var.mName);
        mProgress.reset();
        mProgress.setValue(var.getValueString());
        if (var.mHasTarget) {
            mProgress.setMinValue("" + var.getMinValue());
            mProgress.setMaxValue("" + var.getMaxValue());
            if (var.getMaxValue() == 0) {
                mProgress.setProgress(0);
            } else {
                mProgress.setMax((int) var.getMaxValue()); // TODO
                mProgress.setProgress((int) var.getValue()); // TODO
            }
        }
        mProgress.setUnit(var.getUnit() != null ? var.getUnit().toString() : null);
        mProgress.redraw(); // required for the case when setProgress() is not being called
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mName = (TextView) findViewById(R.id.name);
        mProgress = (VariableTargetProgressBar) findViewById(R.id.progress);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.wearable_demo_variable_view, this);
    }
}
