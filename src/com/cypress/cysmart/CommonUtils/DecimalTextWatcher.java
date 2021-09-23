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

package com.cypress.cysmart.CommonUtils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.NumberFormat;

public class DecimalTextWatcher implements TextWatcher {
    private NumberFormat mRootNF = Utils.getNumberFormatForRootLocale();
    private EditText mEditText;
    private String mTmp = "";
    private int moveCaretTo;
    private static final int INTEGER_CONSTRAINT = 3;
    private static final int FRACTION_CONSTRAINT = 1;
    private static final int MAX_LENGTH = INTEGER_CONSTRAINT + FRACTION_CONSTRAINT + 1;

    public DecimalTextWatcher(EditText et) {
        this.mEditText = et;
        mRootNF.setMaximumIntegerDigits(INTEGER_CONSTRAINT);
        mRootNF.setMaximumFractionDigits(FRACTION_CONSTRAINT);
        mRootNF.setGroupingUsed(false);
    }

    public int countOccurrences(String str, char c) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == c) {
                count++;
            }
        }
        return count;
    }

    @Override
    public void afterTextChanged(Editable s) {
        mEditText.removeTextChangedListener(this); // remove to prevent stackoverflow
        String ss = s.toString();
        int len = ss.length();
        int dots = countOccurrences(ss, '.');
        boolean shouldParse = dots <= 1 && (dots == 0 ? len != (INTEGER_CONSTRAINT + 1) : len < (MAX_LENGTH + 1));
        if (shouldParse) {
            if (len > 1 && ss.lastIndexOf(".") != len - 1) {
                try {
                    Double d = Double.parseDouble(ss);
                    if (d != null) {
                        mEditText.setText(mRootNF.format(d));
                    }
                } catch (NumberFormatException e) {
                }
            }
        } else {
            mEditText.setText(mTmp);
        }
        mEditText.addTextChangedListener(this); // reset listener

        //tried to fix caret positioning after key type:
        if (mEditText.getText().toString().length() > 0) {
            if (dots == 0 && len >= INTEGER_CONSTRAINT && moveCaretTo > INTEGER_CONSTRAINT) {
                moveCaretTo = INTEGER_CONSTRAINT;
            } else if (dots > 0 && len >= (MAX_LENGTH) && moveCaretTo > (MAX_LENGTH)) {
                moveCaretTo = MAX_LENGTH;
            }
            try {
                mEditText.setSelection(mEditText.getText().toString().length());
                // et.setSelection(moveCaretTo); <- almost had it :))
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        moveCaretTo = mEditText.getSelectionEnd();
        mTmp = s.toString();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        int length = mEditText.getText().toString().length();
        if (length > 0) {
            moveCaretTo = start + count - before;
        }
    }
}
