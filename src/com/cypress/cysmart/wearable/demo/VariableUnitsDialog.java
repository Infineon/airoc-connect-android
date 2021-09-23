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

package com.cypress.cysmart.wearable.demo;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

import com.cypress.cysmart.R;
import com.cypress.cysmart.wearable.model.ValueWithUnit;
import com.cypress.cysmart.wearable.model.Variable;

public class VariableUnitsDialog extends Dialog implements AdapterView.OnItemClickListener, DialogInterface.OnCancelListener {

    private final Dialog mParent;
    private final Variable mVariable;
    private ArrayAdapter<ValueWithUnit.Unit> mListAdapter;

    public VariableUnitsDialog(Context context, Dialog parent, Variable variable) {
        super(context);
        this.mParent = parent;
        this.mVariable = variable;
        setOnCancelListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        View root = getLayoutInflater().inflate(R.layout.wearable_dialog_list, null, false);
        ListView listView = (ListView) root.findViewById(R.id.listView1);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        mListAdapter = new ArrayAdapter(getContext(),
                android.R.layout.simple_list_item_single_choice, mVariable.getSupportedUnits()) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                CheckedTextView root = (CheckedTextView) super.getView(position, convertView, parent);
                return root;
            }
        };
        listView.setAdapter(mListAdapter);
        for (int i = 0; i < mListAdapter.getCount(); i++) {
            ValueWithUnit.Unit item = mListAdapter.getItem(i);
            if (mVariable.getUnit().equals(item)) {
                listView.setItemChecked(i, true);
            }
        }
        listView.setOnItemClickListener(this);
        setContentView(root);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mVariable.setUnit(mListAdapter.getItem(position));
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        mParent.cancel(); // dismiss parent dialog as well
    }
}
