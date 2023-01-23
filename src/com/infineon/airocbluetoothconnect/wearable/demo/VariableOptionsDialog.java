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

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.infineon.airocbluetoothconnect.R;
import com.infineon.airocbluetoothconnect.wearable.model.Variable;

import java.util.LinkedList;
import java.util.List;

public class VariableOptionsDialog extends Dialog implements CompoundButton.OnCheckedChangeListener, AdapterView.OnItemClickListener, DialogInterface.OnDismissListener {

    private static final String TARGET = "Target"; // TODO: externalize
    private static final String UNITS = "Units";
    private static final String RESET = "Reset";
    private final Variable mVariable;
    private ArrayAdapter<String> mListAdapter;
    private ListView mListView;

    public VariableOptionsDialog(Context context, Variable variable) {
        super(context);
        this.mVariable = variable;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        View root = getLayoutInflater().inflate(R.layout.wearable_dialog_list, null, false);
        mListView = (ListView) root.findViewById(R.id.listView1);
        mListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        List<String> opts = new LinkedList<>();
        if (mVariable.mHasTarget) {
            opts.add(TARGET);
        }
        if (mVariable.getSupportedUnits().length > 1) {
            opts.add(UNITS);
        }
        opts.add(RESET);

        mListAdapter = new ArrayAdapter<String>(getContext(),
                R.layout.wearable_dialog_list_item_checkbox, R.id.textView1, opts.toArray(new String[0])) {

            @Override
            public int getViewTypeCount() {
                return mVariable.mHasTarget ? 2 : 1;
            }

            @Override
            public int getItemViewType(int position) {
                return mVariable.mHasTarget ? (position == 0 ? 0 : 1) : 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder holder;
                if (convertView == null) {
                    LayoutInflater inflater = getLayoutInflater();
                    holder = new ViewHolder();
                    int itemViewType = getItemViewType(position);
                    if (mVariable.mHasTarget && itemViewType == 0) {
                        convertView = inflater.inflate(R.layout.wearable_dialog_list_item_checkbox, parent, false);
                        holder.mCheckBox = (CheckBox) convertView.findViewById(R.id.checkBox1);
                        holder.mCheckBox.setChecked(mVariable.getMaxValue() != 0);
                        holder.mCheckBox.setOnCheckedChangeListener(VariableOptionsDialog.this);
                    } else {
                        convertView = inflater.inflate(R.layout.wearable_dialog_list_item, parent, false);
                    }
                    holder.mText = (TextView) convertView.findViewById(R.id.textView1);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                String item = getItem(position);
                holder.mText.setText(item);
                return convertView;
            }

            class ViewHolder {

                TextView mText;
                CheckBox mCheckBox;
            }
        };
        mListView.setAdapter(mListAdapter);
        mListView.setOnItemClickListener(this);
        setContentView(root);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.checkBox1:
                if (isChecked) {
                    showTargetDialog();
                } else {
                    mVariable.setMaxValue(0);
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (mListAdapter.getItem(position)) {
            case TARGET:
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox1);
                if (checkBox.isChecked()) {
                    showTargetDialog();
                }
                break;
            case UNITS:
                new VariableUnitsDialog(getContext(), this, mVariable).show();
                break;
            case RESET:
                // TODO
                Toast.makeText(getContext(), "Value reset", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        // handle Target dialog's results
        if (mVariable.getMaxValue() == 0) {
            ((CheckBox) mListView.getChildAt(0).findViewById(R.id.checkBox1)).setChecked(false);
        }
    }

    private void showTargetDialog() {
        Dialog dialog = new VariableTargetDialogBuilder().build(getContext(), this, mVariable);
        dialog.setOnDismissListener(this);
        dialog.show();
    }
}
