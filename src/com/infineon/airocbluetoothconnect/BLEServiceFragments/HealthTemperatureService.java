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

package com.infineon.airocbluetoothconnect.BLEServiceFragments;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.infineon.airocbluetoothconnect.BLEConnectionServices.BluetoothLeService;
import com.infineon.airocbluetoothconnect.CommonUtils.ChartUtils;
import com.infineon.airocbluetoothconnect.CommonUtils.Constants;
import com.infineon.airocbluetoothconnect.CommonUtils.GattAttributes;
import com.infineon.airocbluetoothconnect.CommonUtils.Logger;
import com.infineon.airocbluetoothconnect.CommonUtils.Utils;
import com.infineon.airocbluetoothconnect.R;

import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment to display Health Thermometer Service
 */
public class HealthTemperatureService extends Fragment {

    // GATT service and characteristics
    private static BluetoothGattService mService;
    private static BluetoothGattCharacteristic mTemperatureMeasurementCharacteristic;
    private static BluetoothGattCharacteristic mTemperatureTypeCharacteristic;

    // Data view variables
    private TextView mTemperatureValue;
    private TextView mSensorLocation;
    private TextView mTemperatureUnit;

    /**
     * Chart variables
     */
    private LinearLayout mGraphLayoutParent;
    private double mGraphLastXValue = 0;
    private double mPreviousTime = 0;
    private double mCurrentTime = 0;
    private GraphicalView mChart;
    private XYSeries mTemperatureDataSeries;

    //ProgressDialog
    private ProgressDialog mProgressDialog;

    /**
     * BroadcastReceiver for receiving GATT server status
     */
    private BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Bundle extras = intent.getExtras();
            // GATT data available
            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                // Check temperature measurement
                if (extras.containsKey(Constants.EXTRA_HTM_TEMPERATURE_MEASUREMENT_VALUE)) {
                    ArrayList<String> htmData = intent.getStringArrayListExtra(Constants.EXTRA_HTM_TEMPERATURE_MEASUREMENT_VALUE);
                    displayTemperatureMeasurement(htmData);
                    if (mTemperatureTypeCharacteristic != null) {
                        BluetoothLeService.readCharacteristic(mTemperatureTypeCharacteristic);
                    } else {
                        displayTemperatureType(null);//setting "---" as a value
                    }
                }
                // Check sensor location
                else if (extras.containsKey(Constants.EXTRA_HTM_TEMPERATURE_TYPE_VALUE)) {
                    String hslData = intent.getStringExtra(Constants.EXTRA_HTM_TEMPERATURE_TYPE_VALUE);
                    displayTemperatureType(hslData);
                }
            }
            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                if (state == BluetoothDevice.BOND_BONDING) {
                    // Bonding...
                    Logger.i("Bonding is in process....");
                    Utils.showBondingProgressDialog(getActivity(), mProgressDialog);
                } else if (state == BluetoothDevice.BOND_BONDED) {
                    String dataLog = getResources().getString(R.string.dl_commaseparator)
                            + "[" + BluetoothLeService.getBluetoothDeviceName() + "|"
                            + BluetoothLeService.getBluetoothDeviceAddress() + "]" +
                            getResources().getString(R.string.dl_commaseparator) +
                            getResources().getString(R.string.dl_connection_paired);
                    Logger.dataLog(dataLog);
                    Utils.hideBondingProgressDialog(mProgressDialog);
                    getGattData();
                } else if (state == BluetoothDevice.BOND_NONE) {
                    String dataLog = getResources().getString(R.string.dl_commaseparator)
                            + "[" + BluetoothLeService.getBluetoothDeviceName() + "|"
                            + BluetoothLeService.getBluetoothDeviceAddress() + "]" +
                            getResources().getString(R.string.dl_commaseparator) +
                            getResources().getString(R.string.dl_connection_unpaired);
                    Logger.dataLog(dataLog);
                    Utils.hideBondingProgressDialog(mProgressDialog);
                }
            }
        }
    };

    public static HealthTemperatureService create(BluetoothGattService service) {
        HealthTemperatureService fragment = new HealthTemperatureService();
        mService = service;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.health_temp_measurement, container, false);
        mTemperatureValue = rootView.findViewById(R.id.temperature_value);
        mSensorLocation = rootView.findViewById(R.id.sensor_location_value);
        mTemperatureUnit = rootView.findViewById(R.id.temperature_unit);
        mProgressDialog = new ProgressDialog(getActivity());
        mSensorLocation.setSelected(true);
        setHasOptionsMenu(true);
        // Setting up chart
        setupChart(rootView);
        getGattData();
        return rootView;
    }

    private void displayTemperatureMeasurement(final ArrayList<String> htmData) {
        if (htmData != null) {
            try {
                mTemperatureValue.setText(htmData.get(0));
                mTemperatureUnit.setText(htmData.get(1));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (mCurrentTime == 0) {
                mCurrentTime = Utils.getTimeInSeconds();
                mGraphLastXValue = 0;
            } else {
                mPreviousTime = mCurrentTime;
                mCurrentTime = Utils.getTimeInSeconds();
                mGraphLastXValue = mGraphLastXValue + (mCurrentTime - mPreviousTime) / 1000;
            }
            try {
                double value = Double.valueOf(htmData.get(0));
                String unit = htmData.get(1);
                if (unit.equalsIgnoreCase(getActivity().getString(R.string.tt_fahrenheit))) {
                    value = convertFahrenheitToCelsius((float) value);
                    Logger.i("convertFahrenheitToCelcius--->" + value);
                }
                mTemperatureDataSeries.add(mGraphLastXValue, value);
                mChart.repaint();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void displayTemperatureType(String hslData) {
        if (hslData != null) {
            mSensorLocation.setText(hslData);
        } else {
            mSensorLocation.setText("---");
        }
    }

    // Converts to celsius
    private float convertFahrenheitToCelsius(float fahrenheit) {
        return ((fahrenheit - 32) * 5 / 9);
    }

    @Override
    public void onResume() {
        super.onResume();
        BluetoothLeService.registerBroadcastReceiver(getActivity(), mGattUpdateReceiver, Utils.makeGattUpdateIntentFilter());
        Utils.setUpActionBar((AppCompatActivity) getActivity(), R.string.health_thermometer_fragment);
    }

    @Override
    public void onDestroy() {
        BluetoothLeService.unregisterBroadcastReceiver(getActivity(), mGattUpdateReceiver);
        disableCharacteristicIndication();
        super.onDestroy();
    }


    private void disableCharacteristicIndication() {
        if (mTemperatureMeasurementCharacteristic != null) {
            BluetoothLeService.setCharacteristicIndication(mTemperatureMeasurementCharacteristic, false);
            mTemperatureMeasurementCharacteristic = null;
        }
    }

    /**
     * Get required characteristics from the service
     */
    private void getGattData() {
        mTemperatureMeasurementCharacteristic = mTemperatureTypeCharacteristic = null;
        List<BluetoothGattCharacteristic> characteristics = mService.getCharacteristics();
        for (BluetoothGattCharacteristic c : characteristics) {
            String uuid = c.getUuid().toString();
            if (uuid.equalsIgnoreCase(GattAttributes.TEMPERATURE_TYPE)) {
                mTemperatureTypeCharacteristic = c;
            }
            if (uuid.equalsIgnoreCase(GattAttributes.TEMPERATURE_MEASUREMENT)) {
                mTemperatureMeasurementCharacteristic = c;
                BluetoothLeService.setCharacteristicIndication(mTemperatureMeasurementCharacteristic, true);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.global, menu);
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        }
        MenuItem graph = menu.findItem(R.id.graph);
        MenuItem log = menu.findItem(R.id.log);
        MenuItem search = menu.findItem(R.id.search);
        search.setVisible(false);
        graph.setVisible(true);
        log.setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here
        switch (item.getItemId()) {
            case R.id.graph:
                if (mGraphLayoutParent.getVisibility() != View.VISIBLE) {
                    mGraphLayoutParent.setVisibility(View.VISIBLE);
                } else {
                    mGraphLayoutParent.setVisibility(View.GONE);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Setting up the aChart Third party library
     *
     * @param parent
     */
    private void setupChart(View parent) {
        /**
         * Setting graph titles
         */
        String graphTitle = getResources().getString(R.string.health_temperature_graph);
        String graphXAxis = getResources().getString(R.string.health_temperature_time);
        String graphYAxis = getResources().getString(R.string.health_temperature_temperature);

        // Creating an  XYSeries for temperature
        mTemperatureDataSeries = new XYSeries(graphTitle);

        // Creating a dataset to hold each series
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

        // Adding temperature Series to the dataset
        dataset.addSeries(mTemperatureDataSeries);

        // Creating XYSeriesRenderer to customize
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setColor(getResources().getColor(R.color.main_bg_color));
        renderer.setPointStyle(PointStyle.CIRCLE);
        renderer.setFillPoints(true);
        renderer.setLineWidth(5);

        // Creating a XYMultipleSeriesRenderer to customize the whole chart
        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
        int deviceDPI = getResources().getDisplayMetrics().densityDpi;
        switch (getResources().getDisplayMetrics().densityDpi) {
            case DisplayMetrics.DENSITY_XHIGH:
                multiRenderer.setMargins(new int[]{Constants.GRAPH_MARGIN_40, Constants.GRAPH_MARGIN_90,
                        Constants.GRAPH_MARGIN_25, Constants.GRAPH_MARGIN_10});
                multiRenderer.setAxisTitleTextSize(Constants.TEXT_SIZE_XHDPI);
                multiRenderer.setChartTitleTextSize(Constants.TEXT_SIZE_XHDPI);
                multiRenderer.setLabelsTextSize(Constants.TEXT_SIZE_XHDPI);
                multiRenderer.setLegendTextSize(Constants.TEXT_SIZE_XHDPI);
                break;
            case DisplayMetrics.DENSITY_HIGH:
                multiRenderer.setMargins(new int[]{Constants.GRAPH_MARGIN_30, Constants.GRAPH_MARGIN_50,
                        Constants.GRAPH_MARGIN_25, Constants.GRAPH_MARGIN_10});
                multiRenderer.setAxisTitleTextSize(Constants.TEXT_SIZE_HDPI);
                multiRenderer.setChartTitleTextSize(Constants.TEXT_SIZE_HDPI);
                multiRenderer.setLabelsTextSize(Constants.TEXT_SIZE_HDPI);
                multiRenderer.setLegendTextSize(Constants.TEXT_SIZE_HDPI);
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                multiRenderer.setMargins(new int[]{Constants.GRAPH_MARGIN_50, Constants.GRAPH_MARGIN_100,
                        Constants.GRAPH_MARGIN_35, Constants.GRAPH_MARGIN_20});
                multiRenderer.setAxisTitleTextSize(Constants.TEXT_SIZE_XXHDPI);
                multiRenderer.setChartTitleTextSize(Constants.TEXT_SIZE_XXHDPI);
                multiRenderer.setLabelsTextSize(Constants.TEXT_SIZE_XXHDPI);
                multiRenderer.setLegendTextSize(Constants.TEXT_SIZE_XXHDPI);
                break;
            default:
                if (deviceDPI > DisplayMetrics.DENSITY_XXHIGH && deviceDPI <= DisplayMetrics.DENSITY_XXXHIGH) {
                    multiRenderer.setMargins(new int[]{Constants.GRAPH_MARGIN_70, Constants.GRAPH_MARGIN_130,
                            Constants.GRAPH_MARGIN_35, Constants.GRAPH_MARGIN_20});
                    multiRenderer.setAxisTitleTextSize(Constants.TEXT_SIZE_XXXHDPI);
                    multiRenderer.setChartTitleTextSize(Constants.TEXT_SIZE_XXXHDPI);
                    multiRenderer.setLabelsTextSize(Constants.TEXT_SIZE_XXXHDPI);
                    multiRenderer.setLegendTextSize(Constants.TEXT_SIZE_XXXHDPI);
                } else {
                    multiRenderer.setMargins(new int[]{Constants.GRAPH_MARGIN_30, Constants.GRAPH_MARGIN_50,
                            Constants.GRAPH_MARGIN_25, Constants.GRAPH_MARGIN_10});
                    multiRenderer.setAxisTitleTextSize(Constants.TEXT_SIZE_LDPI);
                    multiRenderer.setChartTitleTextSize(Constants.TEXT_SIZE_LDPI);
                    multiRenderer.setLabelsTextSize(Constants.TEXT_SIZE_LDPI);
                    multiRenderer.setLegendTextSize(Constants.TEXT_SIZE_LDPI);
                }
                break;
        }
        multiRenderer.setXTitle(graphXAxis);
        multiRenderer.setLabelsColor(Color.BLACK);
        multiRenderer.setYTitle(graphYAxis);
        multiRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));
        multiRenderer.setPanEnabled(ChartUtils.PAN_X_ENABLED, ChartUtils.PAN_Y_ENABLED);
        multiRenderer.setZoomEnabled(ChartUtils.ZOOM_X_ENABLED, ChartUtils.ZOOM_Y_ENABLED);
        multiRenderer.setGridColor(Color.LTGRAY);
        multiRenderer.setLabelsColor(Color.BLACK);
        multiRenderer.setYLabelsColor(0, Color.DKGRAY);
        multiRenderer.setYLabelsAlign(Paint.Align.RIGHT);
        multiRenderer.setXLabelsColor(Color.DKGRAY);
        multiRenderer.setYLabelsColor(0, Color.BLACK);
        multiRenderer.setXLabelsColor(Color.BLACK);
        multiRenderer.setApplyBackgroundColor(true);
        multiRenderer.setBackgroundColor(Color.WHITE);
        multiRenderer.setGridColor(Color.BLACK);
        multiRenderer.setShowGrid(true);
        multiRenderer.setShowLegend(false);

        // Adding mRenderer to multipleRenderer
        multiRenderer.addSeriesRenderer(renderer);

        // Getting a reference to LinearLayout of the MainActivity Layout
        mGraphLayoutParent = parent.findViewById(R.id.chart_container);

        mChart = ChartUtils.getLineChartView(getActivity(), dataset, multiRenderer);

        // Adding the Line Chart to the LinearLayout
        mGraphLayoutParent.addView(mChart);
    }
}
