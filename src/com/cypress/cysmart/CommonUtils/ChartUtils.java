/*
 * (c) 2020, Cypress Semiconductor Corporation or a subsidiary of 
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

import android.content.Context;
import android.view.MotionEvent;

import org.achartengine.GraphicalView;
import org.achartengine.chart.LineChart;
import org.achartengine.chart.XYChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

public class ChartUtils {

    public static final boolean PAN_X_ENABLED = true;
    public static final boolean PAN_Y_ENABLED = true;
    public static final boolean ZOOM_X_ENABLED = true;
    public static final boolean ZOOM_Y_ENABLED = true;

    public static final GraphicalView getLineChartView(Context context, final XYMultipleSeriesDataset dataset, XYMultipleSeriesRenderer multiRenderer) {
        checkParameters(dataset, multiRenderer);

        XYChart chart = new LineChart(dataset, multiRenderer);
        final boolean panEnabled = multiRenderer.isPanEnabled();
        return new GraphicalView(context, chart) {
            @Override
            public boolean onTouchEvent(MotionEvent event) {
                if (!panEnabled) { // No pan enabled -> fall back to default implementation
                    return super.onTouchEvent(event);
                }
                XYSeries[] series = dataset.getSeries();
                if (series != null && series.length > 0) {
                    if (series[0].getItemCount() > 4) { // panEnabled + empty series = non working chart
                        return super.onTouchEvent(event);
                    }
                }
                return true; // Ignoring touch event
            }
        };
//        return ChartFactory.getLineChartView(context, dataset, renderer);
    }

    private static void checkParameters(XYMultipleSeriesDataset dataset, XYMultipleSeriesRenderer renderer) {
        if (dataset == null || renderer == null || dataset.getSeriesCount() != renderer.getSeriesRendererCount()) {
            throw new IllegalArgumentException("Dataset and renderer should be not null and should have the same number of series");
        }
    }
}
