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

package com.cypress.cysmart.wearable.chart;

import java.util.LinkedList;

public class TimeBasedChartUpdater {

    private final int mSeriesCount;
    private double mChartLastXValue = 0;
    private double mPreviousTime = 0;
    private double mCurrentTime = 0;
    private LinkedList<double[]> mDeque = new LinkedList<>();
    public final double[] mCurrentValues;

    public static TimeBasedChartUpdater newInstanceFrom(TimeBasedChartUpdater old) {
        return new TimeBasedChartUpdater(old.mSeriesCount);
    }

    public TimeBasedChartUpdater(int seriesCount) {
        this.mSeriesCount = seriesCount;
        this.mCurrentValues = new double[seriesCount];
    }

    public void update(double... values) {
        if (values.length != mSeriesCount) {
            throw new IllegalArgumentException();
        }
        if (mCurrentTime == 0) {
            mChartLastXValue = 0;
            mCurrentTime = System.currentTimeMillis();
        } else {
            mPreviousTime = mCurrentTime;
            mCurrentTime = System.currentTimeMillis();
            mChartLastXValue = mChartLastXValue + (mCurrentTime - mPreviousTime) / 1000;
        }
        double[] tuple = new double[mSeriesCount + 1];
        tuple[0] = mChartLastXValue;
        for (int i = 0; i < mSeriesCount; i++) {
            tuple[1 + i] = values[i];
            mCurrentValues[i] = values[i];
        }
        mDeque.addLast(tuple);
    }

    public void repaint(TimeBasedChart chart) {
        chart.repaint(mDeque);
    }
}
