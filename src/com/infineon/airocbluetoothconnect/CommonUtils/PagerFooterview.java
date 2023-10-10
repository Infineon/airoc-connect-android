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
import android.graphics.RectF;
import android.view.View;

import com.infineon.airocbluetoothconnect.R;

/**
 * ViewPager page indicator
 */
public class PagerFooterview extends View {
    private Paint mPaint;
    private RectF mRect;
    private int mViewsCount;
    private int mPosition;
    private float mId;
    private static final int STROKE_WIDTH = 2;
    private static final int VIEWPORT_TOP = 10;
    private static final int VIEWPORT_RIGHT = 13;
    private static final int VIEWPORT_BOTTOM = 23;
    private static final int RED = 255;
    private static final int GREEN = 255;
    private static final int BLUE = 255;
    private static final int LEFT_MARGIN = 18;

    public PagerFooterview(Context context, int numOfViews, int width) {
        super(context);
        mPaint = new Paint();
        mPaint.setColor(getResources().getColor(R.color.primary, getContext().getTheme()));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(STROKE_WIDTH);
        this.mViewsCount = numOfViews;
        mId = width / 2;
        mRect = new RectF(mId, VIEWPORT_TOP, mId + VIEWPORT_RIGHT, VIEWPORT_BOTTOM);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRGB(RED, GREEN, BLUE);
        for (int i = 0; i < mViewsCount; i++) {
            if (mPosition == i)
                mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mRect.set(mId + i * LEFT_MARGIN, VIEWPORT_TOP, mId + i * LEFT_MARGIN + VIEWPORT_RIGHT, VIEWPORT_BOTTOM);
            canvas.drawOval(mRect, mPaint);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(STROKE_WIDTH);
        }
        super.onDraw(canvas);
    }

    public void Update(int p) {
        mPosition = p;
        invalidate();
    }

    @Override
    public void onSizeChanged(int w, int h, int oldW, int oldH) {
        // Set the movement bounds for the ball
        mId = w / 2;
        mId = mId - mViewsCount * 9;
    }
}
