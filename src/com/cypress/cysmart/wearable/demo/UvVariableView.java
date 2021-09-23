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

import android.content.Context;
import android.graphics.LightingColorFilter;
import android.util.AttributeSet;

import com.cypress.cysmart.wearable.model.Variable;

public class UvVariableView extends VariableView {

    private static final int GREEN = 0x00FF00;
    private static final int YELLOW = 0xFFFF00;
    private static final int ORANGE = 0xFFA500;
    private static final int RED = 0xFF0000;
    private static final int VIOLET = 0x8A2BE2;
    private static final int LOW = 3;
    private static final int MODERATE = 6;
    private static final int HIGH = 8;
    public static final int VERY_HIGH = 11;

    public UvVariableView(Context context) {
        super(context);
    }

    public UvVariableView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UvVariableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setVariable(Variable var) {
        super.setVariable(var);
        mProgress.setMinValue("" + var.getMinValue());
        mProgress.setMaxValue("" + var.getMaxValue() + "+");
        mProgress.setMax(1);
        mProgress.setProgress(1);
        mProgress.getProgressDrawable().setColorFilter(new LightingColorFilter(0xFF000000, getColor(var.getValue())));
    }

    private int getColor(double value) {
        int color;
        // low
        if (value < LOW) {
            color = GREEN;
        }
        // moderate
        else if (value < MODERATE) {
            color = YELLOW;
        }
        // high
        else if (value < HIGH) {
            color = ORANGE;
        }
        // very high
        else if (value < VERY_HIGH) {
            return RED;
        }
        // extreme
        else {
            color = VIOLET;
        }
        return color;
    }
}
