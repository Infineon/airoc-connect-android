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

package com.infineon.airocbluetoothconnect.CommonUtils;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.infineon.airocbluetoothconnect.R;

/**
 * HexKey board to be displayed when writing a value to characteristics and descriptors
 */
public class HexKeyBoard extends Dialog implements View.OnClickListener {

    // Write dialog buttons
    private Button mBttwo;
    private Button mBtthree;
    private Button mBtfour;
    private Button mBtfive;
    private Button mBtsix;
    private Button mBtseven;
    private Button mBteight;
    private Button mBtnine;
    private Button mBtzero;
    private Button mBta;
    private Button mBtb;
    private Button mBtc;
    private Button mBtd;
    private Button mBte;
    private Button mBtf;
    private Button mBthex;
    private Button mBtone;
    private ImageButton mBtnback;

    // Converting to hex variables
    private String hexValueString = "";
    private String hexsubstring = "0x";

    // HexValue entered
    private EditText mHexvalue;

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
    public HexKeyBoard(Activity activity, BluetoothGattDescriptor bluetoothGattDescriptor,
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
    public HexKeyBoard(Activity activity, BluetoothGattCharacteristic bluetoothGattCharacteristic,
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
        setContentView(R.layout.hex_value_popup);
        // Custom keyboard Buttons
        Button viewOk = (Button) findViewById(R.id.txtok);
        Button viewCancel = (Button) findViewById(R.id.txtcancel);
        mBta = (Button) findViewById(R.id.btna);
        mBtb = (Button) findViewById(R.id.btnb);
        mBtc = (Button) findViewById(R.id.btnc);
        mBtd = (Button) findViewById(R.id.btnd);
        mBte = (Button) findViewById(R.id.btne);
        mBtf = (Button) findViewById(R.id.btnf);
        mBtzero = (Button) findViewById(R.id.btnzero);
        mBtnback = (ImageButton) findViewById(R.id.btnback);
        mBtone = (Button) findViewById(R.id.btnone);
        mBttwo = (Button) findViewById(R.id.btntwo);
        mBtthree = (Button) findViewById(R.id.btnthree);
        mBtfour = (Button) findViewById(R.id.btnfour);
        mBtfive = (Button) findViewById(R.id.btnfive);
        mBtsix = (Button) findViewById(R.id.btnsix);
        mBtseven = (Button) findViewById(R.id.btnseven);
        mBteight = (Button) findViewById(R.id.btneight);
        mBtnine = (Button) findViewById(R.id.btnnine);
        mBthex = (Button) findViewById(R.id.btnhex);
        mHexvalue = (EditText) findViewById(R.id.edittext_text);
        mHexvalue.setText("");

        // Custom keyboard listeners
        mBta.setOnClickListener(this);
        mBtb.setOnClickListener(this);
        mBtc.setOnClickListener(this);
        mBtd.setOnClickListener(this);
        mBte.setOnClickListener(this);
        mBtf.setOnClickListener(this);
        mBtzero.setOnClickListener(this);
        mBtone.setOnClickListener(this);
        mBttwo.setOnClickListener(this);
        mBtthree.setOnClickListener(this);
        mBtfour.setOnClickListener(this);
        mBtfive.setOnClickListener(this);
        mBtsix.setOnClickListener(this);
        mBtseven.setOnClickListener(this);
        mBteight.setOnClickListener(this);
        mBtnine.setOnClickListener(this);
        mBtnback.setOnClickListener(this);
        mBthex.setOnClickListener(this);

        // EditText touch listener
        mHexvalue.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.onTouchEvent(event);
                InputMethodManager imm = (InputMethodManager) v.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                return true;
            }
        });
        viewOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mHexvalue.getText().toString().length() > 0) {
                    String hexValueString = mHexvalue.getText().toString();
                    mDialogListener.dialogOkPressed(hexValueString);
                } else {
                    mHexvalue.setText("");
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
            case R.id.btna:
                hexValueUpatetemp("A");
                break;
            case R.id.btnb:
                hexValueUpatetemp("B");
                break;
            case R.id.btnc:
                hexValueUpatetemp("C");
                break;
            case R.id.btnd:
                hexValueUpatetemp("D");
                break;
            case R.id.btne:
                hexValueUpatetemp("E");
                break;
            case R.id.btnf:
                hexValueUpatetemp("F");
                break;
            case R.id.btnzero:
                hexValueUpatetemp("0");
                break;
            case R.id.btnone:
                hexValueUpatetemp("1");
                break;
            case R.id.btntwo:
                hexValueUpatetemp("2");
                break;
            case R.id.btnthree:
                hexValueUpatetemp("3");
                break;
            case R.id.btnfour:
                hexValueUpatetemp("4");
                break;
            case R.id.btnfive:
                hexValueUpatetemp("5");
                break;
            case R.id.btnsix:
                hexValueUpatetemp("6");
                break;
            case R.id.btnseven:
                hexValueUpatetemp("7");
                break;
            case R.id.btneight:
                hexValueUpatetemp("8");
                break;
            case R.id.btnnine:
                hexValueUpatetemp("9");
                break;
            case R.id.btnback:
                backbuttonpressed();
                break;
            case R.id.btnhex:
                hexUpdate();
                break;

        }
    }

    /**
     * HexValue appending with hexSubstring
     */
    private void hexUpdate() {
        hexsubstring = "0x";
        hexValueString = hexValueString + " " + hexsubstring;
        hexsubstring = "";
        mHexvalue.setText(hexValueString.trim());
        mHexvalue.setSelection(hexValueString.trim().length());
    }

    /**
     * Update the editText field with hexValues
     *
     * @param string
     */
    private void hexValueUpatetemp(String string) {
        if (hexValueString.length() != 0) {

            String[] splited = hexValueString.split("\\s+");

            int arrayCount = splited.length;
            if (arrayCount != 0) {
                String lastValue = splited[arrayCount - 1];
                int last = lastValue.length();
                if (last == 4) {
                    hexValueString = hexValueString + " 0x" + string;
                } else if (last == 3 || last == 2) {
                    hexValueString = hexValueString + string;
                }

                mHexvalue.setText(hexValueString.trim());
                mHexvalue.setSelection(hexValueString.trim().length());
            }
        } else {
            hexValueString = "0x" + string;
            mHexvalue.setText(hexValueString.trim());
            mHexvalue.setSelection(hexValueString.trim().length());
        }
    }

    /**
     * Custom keyboard back pressed action
     */
    private void backbuttonpressed() {

        if (hexValueString.length() != 0) {

            String[] splited = hexValueString.split("\\s+");

            int last = splited.length;
            if (last != 0) {
                String substring = splited[last - 1];
                if ((substring.length() == 4) || (substring.length() == 3)) {
                    substring = substring.substring(0, substring.length() - 1);
                    splited[last - 1] = substring;
                    hexValueString = "";
                    for (int i = 0; i < splited.length; i++) {
                        hexValueString = hexValueString + " " + splited[i];
                    }
                } else if (substring.length() == 2) {
                    hexValueString = "";
                    for (int i = 0; i < splited.length - 1; i++) {
                        hexValueString = hexValueString + " " + splited[i];
                    }
                }
                mHexvalue.setText(hexValueString.trim());
                mHexvalue.setSelection(hexValueString.trim().length());
            }
        }
    }
}
