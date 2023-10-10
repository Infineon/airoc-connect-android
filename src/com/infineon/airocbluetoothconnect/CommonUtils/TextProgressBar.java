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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ProgressBar;

import com.infineon.airocbluetoothconnect.R;

/**
 * Custom progressBar with text
 */
public class TextProgressBar extends ProgressBar {
    private String mProgressText;
    private Paint mProgressPaint;
    private final Rect mBoundsProgressRect = new Rect();

    public TextProgressBar(Context context) {
        super(context);
        initializeProgressBar();
    }

    public TextProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeProgressBar();
    }

    public TextProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeProgressBar();
    }

    public void initializeProgressBar() {
        mProgressText = "0%";
        mProgressPaint = new Paint();

        int scaledSizeInPixels = getResources().getDimensionPixelSize(R.dimen.text_size_small);
        mProgressPaint.setTextSize(scaledSizeInPixels);
        mProgressPaint.setColor(getResources().getColor(R.color.text, getContext().getTheme()));
    }


    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Rect boundsProgress = mBoundsProgressRect;
        mProgressPaint.getTextBounds(mProgressText, 0, mProgressText.length(), boundsProgress);
        int xp;
        final int densityDpi = getResources().getDisplayMetrics().densityDpi;
        if (densityDpi <= DisplayMetrics.DENSITY_LOW) {
            xp = getWidth() - 70;
        } else if (densityDpi <= DisplayMetrics.DENSITY_MEDIUM) {
            xp = getWidth() - 70;
        } else if (densityDpi <= DisplayMetrics.DENSITY_TV) {
            xp = getWidth() - 70;
        } else if (densityDpi <= DisplayMetrics.DENSITY_HIGH) {
            xp = getWidth() - 80;
        } else if (densityDpi <= DisplayMetrics.DENSITY_XHIGH) {
            xp = getWidth() - 100;
        } else if (densityDpi <= DisplayMetrics.DENSITY_XXHIGH) {
            xp = getWidth() - 120;
        } else if (densityDpi <= DisplayMetrics.DENSITY_XXXHIGH) {
            xp = getWidth() - 140;
        } else {
            xp = getWidth() - 140;
        }

        int yp = getHeight() / 2 - boundsProgress.centerY();
        canvas.drawText(mProgressText, xp, yp, mProgressPaint);
    }

    public synchronized void setProgressText(String text) {
        this.mProgressText = text;
        drawableStateChanged();
    }
}
