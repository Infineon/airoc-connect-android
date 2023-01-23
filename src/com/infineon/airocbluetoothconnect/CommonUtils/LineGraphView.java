/*
 * Copyright (c) 2015, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.infineon.airocbluetoothconnect.CommonUtils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.Point;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

/**
 * This class uses external library AChartEngine to showToast dynamic real time line graph for HR values
 */
public class LineGraphView {
	//TimeSeries will hold the data in x,y format for single chart
	private TimeSeries mSeries = new TimeSeries("Heart Rate");
	//XYMultipleSeriesDataset will contain all the TimeSeries
	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	//XYMultipleSeriesRenderer will contain all XYSeriesRenderer and it can be used to set the properties of whole Graph
	private XYMultipleSeriesRenderer mMultiRenderer = new XYMultipleSeriesRenderer();
	private static LineGraphView mInstance = null;

	/**
	 * singleton implementation of LineGraphView class
	 */
	public static synchronized LineGraphView getLineGraphView() {
		if (mInstance == null) {
			mInstance = new LineGraphView();
		}
		return mInstance;
	}

	/**
	 * This constructor will set some properties of single chart and some properties of whole graph
	 */
	public LineGraphView() {
		//add single line chart mSeries
		mDataset.addSeries(mSeries);

		//XYSeriesRenderer is used to set the properties like chart color, style of each point, etc. of single chart
		final XYSeriesRenderer seriesRenderer = new XYSeriesRenderer();
		//set line chart color to Black
		seriesRenderer.setColor(Color.BLACK);
		//set line chart style to square points
		seriesRenderer.setPointStyle(PointStyle.SQUARE);
		seriesRenderer.setFillPoints(true);

		final XYMultipleSeriesRenderer renderer = mMultiRenderer;
		//set whole graph background color to transparent color
		renderer.setBackgroundColor(Color.TRANSPARENT);
		renderer.setMargins(new int[] { 50, 65, 40, 5 }); // top, left, bottom, right
		renderer.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01));
		renderer.setAxesColor(Color.BLACK);
		renderer.setAxisTitleTextSize(24);
		renderer.setShowGrid(true);
		renderer.setGridColor(Color.LTGRAY);
		renderer.setLabelsColor(Color.BLACK);
		renderer.setYLabelsColor(0, Color.DKGRAY);
		renderer.setYLabelsAlign(Align.RIGHT);
		renderer.setXLabelsColor(Color.DKGRAY);
		renderer.setLabelsTextSize(20);
		renderer.setLegendTextSize(20);
		//Disable zoom
		renderer.setPanEnabled(false, false);
		renderer.setZoomEnabled(false, false);
		//set title to x-axis and y-axis
		renderer.setXTitle("    Time (seconds)");
		renderer.setYTitle("               BPM");
		renderer.addSeriesRenderer(seriesRenderer);
	}

	/**
	 * return graph view to activity
	 */
	public GraphicalView getView(Context context) {
		final GraphicalView graphView = ChartFactory.getLineChartView(context, mDataset, mMultiRenderer);
		return graphView;
	}

	/**
	 * add new x,y value to chart
	 */
	public void addValue(Point p) {
		mSeries.add(p.x, p.y);
	}

	/**
	 * clear all previous values of chart
	 */
	public void clearGraph() {
		mSeries.clear();
	}

}
