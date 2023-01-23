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

package com.infineon.airocbluetoothconnect.CommonUtils;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.infineon.airocbluetoothconnect.R;

/**
 * HexKey board to be displayed when writing a value to characteristics and descriptors
 */
public class ASCIIKeyBoard extends Dialog implements View.OnClickListener {

    // Converting to hex variables
    private String asciiValueString = "";
    private String asciiSubstring = "0x";

    // HexValue entered
    private EditText mAsciivalue;

    //Descriptor
    private BluetoothGattDescriptor mGattDescriptor;

    //Characteristic
    private BluetoothGattCharacteristic mGattCharacteristic;

    //Flag for Descriptor and characteristic
    private Boolean mIsDescriptor = false;
    private Boolean mIsCharacteristic = false;

    //Dialog listner
    private DialogListener mDialogListener;


    /**
     * Descriptor Constructor for the class
     *
     * @param activity
     * @param bluetoothGattDescriptor
     * @param isDescriptor
     */
    public ASCIIKeyBoard(Activity activity, BluetoothGattDescriptor bluetoothGattDescriptor,
                         Boolean isDescriptor) {
        super(activity);
        this.mGattDescriptor = bluetoothGattDescriptor;
        this.mIsDescriptor = isDescriptor;
    }

    /**
     * Characteristic Constructor for the class
     *
     * @param activity
     * @param bluetoothGattCharacteristic
     * @param isCharacteristic
     */
    public ASCIIKeyBoard(Activity activity, BluetoothGattCharacteristic bluetoothGattCharacteristic,
                         Boolean isCharacteristic) {
        super(activity);
        this.mGattCharacteristic = bluetoothGattCharacteristic;
        this.mIsCharacteristic = isCharacteristic;
    }

    public void setDialogListner(DialogListener mDialogListener) {
        this.mDialogListener = mDialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.ascii_value_popup);
        // Custom keyboard Buttons
        Button viewOk = (Button) findViewById(R.id.txtok);
        Button viewCancel = (Button) findViewById(R.id.txtcancel);
        mAsciivalue = (EditText) findViewById(R.id.ascii_edittext_text);
        mAsciivalue.setText("");
        mAsciivalue.setFocusable(true);
        mAsciivalue.requestFocus();
        mAsciivalue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }
            }
        });
        viewOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mAsciivalue.getText().toString().length() > 0) {
                    String hexValueString = mAsciivalue.getText().toString();
                    mDialogListener.dialogOkPressed(hexValueString);
                } else {
                    mAsciivalue.setText("");
                }
                cancel();
            }

        });
        viewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
                mDialogListener.dialogCancelPressed(true);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnhex:
                hexUpdate();
                break;

        }
    }

    /**
     * HexValue appending with hexSubstring
     */
    private void hexUpdate() {
        asciiSubstring = "0x";
        asciiValueString = asciiValueString + " " + asciiSubstring;
        asciiSubstring = "";
        mAsciivalue.setText(asciiValueString.trim());
        mAsciivalue.setSelection(asciiValueString.trim().length());
    }
}
