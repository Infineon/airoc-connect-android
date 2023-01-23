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
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

//Fragment to display the heart rate service
public class HeartRateService extends Fragment {

    public static final int MAX_NUM_RR_INTERVALS = 3; // Display up to 3 RR-intervals
    // Data view variables
    private TextView mDataFieldTemperature;
    private TextView mDataFieldSensorContact;
    private TextView mDataFieldEnergyExpended;
    private TextView mDataFieldRRInterval;
    private TextView mDataFieldBodySensorLocation;
    private ImageView mHeartView;

    // GATT service and characteristics
    private static BluetoothGattService mService;
    private BluetoothGattCharacteristic mHeartRateMeasurementCharacteristic;
    private BluetoothGattCharacteristic mBodySensorLocationCharacteristic;

    /**
     * aChart variables
     */
    private LinearLayout mGraphLayoutParent;
    private double mGraphLastXValue = 0;
    private double mPreviousTime = 0;
    private double mCurrentTime = 0;
    private GraphicalView mChart;
    private XYSeries mDataSeries;

    //ProgressDialog
    private ProgressDialog mProgressDialog;

    /**
     * BroadcastReceiver for receiving the GATT server status
     */
    private BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Bundle extras = intent.getExtras();
            // GATT Data available
            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                // Check body sensor location
                if (extras.containsKey(Constants.EXTRA_HRM_BODY_SENSOR_LOCATION_VALUE)) {
                    String bodySensorLocation = intent.getStringExtra(Constants.EXTRA_HRM_BODY_SENSOR_LOCATION_VALUE);
                    displayBodySensorLocation(bodySensorLocation);
                }
                // Check heart rate
                if (extras.containsKey(Constants.EXTRA_HRM_HEART_RATE_VALUE)) {
                    String heartRate = extras.getString(Constants.EXTRA_HRM_HEART_RATE_VALUE);
                    displayHeartRate(heartRate);
                    if (mBodySensorLocationCharacteristic != null) {
                        BluetoothLeService.readCharacteristic(mBodySensorLocationCharacteristic);
                    } else {
                        displayBodySensorLocation(null);//setting "---" as a value
                    }
                }
                // Check sensor contact
                if (extras.containsKey(Constants.EXTRA_HRM_SENSOR_CONTACT_VALUE)) {
                    String sensorContact = extras.getString(Constants.EXTRA_HRM_SENSOR_CONTACT_VALUE);
                    displaySensorContact(sensorContact);
                }
                // Check energy expended
                if (extras.containsKey(Constants.EXTRA_HRM_ENERGY_EXPENDED_VALUE)) {
                    String energyExpended = extras.getString(Constants.EXTRA_HRM_ENERGY_EXPENDED_VALUE);
                    displayEnergyExpended(energyExpended);
                }
                // Check rr interval
                if (extras.containsKey(Constants.EXTRA_HRM_RR_INTERVAL_VALUE)) {
                    ArrayList<Integer> RRInterval = extras.getIntegerArrayList(Constants.EXTRA_HRM_RR_INTERVAL_VALUE);
                    displayRRInterval(RRInterval);
                }
            }
            //Received when the bond state is changed
            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                if (state == BluetoothDevice.BOND_BONDING) {
                    // Bonding...
                    String dataLog = getResources().getString(R.string.dl_commaseparator)
                            + "[" + BluetoothLeService.getBluetoothDeviceName() + "|"
                            + BluetoothLeService.getBluetoothDeviceAddress() + "]" +
                            getResources().getString(R.string.dl_commaseparator) +
                            getResources().getString(R.string.dl_connection_pairing_request_received);
                    Logger.dataLog(dataLog);
                    String dataLog2 = getResources().getString(R.string.dl_commaseparator)
                            + "[" + BluetoothLeService.getBluetoothDeviceAddress() + "|"
                            + BluetoothLeService.getBluetoothDeviceName() + "]" +
                            getResources().getString(R.string.dl_commaseparator) +
                            getResources().getString(R.string.dl_connection_pairing_request);
                    Logger.dataLog(dataLog2);
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

    public static HeartRateService create(BluetoothGattService service) {
        mService = service;
        return new HeartRateService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.hrm_measurement, container, false);
        mDataFieldTemperature = rootView.findViewById(R.id.hrm_heartrate);
        mDataFieldSensorContact = rootView.findViewById(R.id.hrm_sensor_contact);
        mDataFieldEnergyExpended = rootView.findViewById(R.id.heart_rate_ee);
        mDataFieldRRInterval = rootView.findViewById(R.id.heart_rate_rr);
        mDataFieldBodySensorLocation = rootView.findViewById(R.id.hrm_sensor_location);
        mHeartView = rootView.findViewById(R.id.heart_icon);
        mProgressDialog = new ProgressDialog(getActivity());
        setHasOptionsMenu(true);
        Animation pulse = AnimationUtils.loadAnimation(getActivity(), R.anim.pulse);
        mHeartView.startAnimation(pulse);
        // Setting up chart
        setupChart(rootView);
        getGattData();
        return rootView;
    }


    private void displayBodySensorLocation(String bodySensorLocation) {
        if (bodySensorLocation != null) {
            mDataFieldBodySensorLocation.setText(bodySensorLocation);
        } else {
            mDataFieldBodySensorLocation.setText("---");
        }
    }

    private void displayHeartRate(final String heartRate) {
        if (heartRate != null) {
            mDataFieldTemperature.setText(heartRate);
            if (mCurrentTime == 0) {
                mGraphLastXValue = 0;
                mCurrentTime = Utils.getTimeInSeconds();
            } else {
                mPreviousTime = mCurrentTime;
                mCurrentTime = Utils.getTimeInSeconds();
                mGraphLastXValue = mGraphLastXValue + (mCurrentTime - mPreviousTime) / 1000;
            }
            double val = Integer.valueOf(heartRate);
            mDataSeries.add(mGraphLastXValue, val);
            mChart.repaint();
        }
    }

    private void displaySensorContact(String sensorContact) {
        if (sensorContact != null) {
            mDataFieldSensorContact.setText(sensorContact);
        }
    }

    private void displayEnergyExpended(String energyExpended) {
        if (energyExpended != null) {
            mDataFieldEnergyExpended.setText(energyExpended);
        }
    }

    private void displayRRInterval(ArrayList<Integer> rrInterval) {
        if (rrInterval != null) {
            String rr = "";
            for (int i = 0; i < rrInterval.size() && i < MAX_NUM_RR_INTERVALS; i++) { // Limit number of RR-intervals displayed
                String data = String.valueOf(rrInterval.get(i));
                if (i == 0) {
                    rr = data;
                } else {
                    rr = rr + "\n" + data;
                }
            }
            mDataFieldRRInterval.setText(rr);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.debug("HRM: lifecycle: onResume", this, getActivity());
        BluetoothLeService.registerBroadcastReceiver(getActivity(), mGattUpdateReceiver, Utils.makeGattUpdateIntentFilter());
        Utils.setUpActionBar((AppCompatActivity) getActivity(), R.string.heart_rate);
    }

    @Override
    public void onDestroy() {
        Utils.debug("HRM: lifecycle: onDestroy", this, getActivity());
        BluetoothLeService.unregisterBroadcastReceiver(getActivity(), mGattUpdateReceiver);
        disableCharacteristicNotification();
        super.onDestroy();
    }

    /**
     * Stopping Broadcast receiver to broadcast notify characteristics
     *
     */
    void disableCharacteristicNotification() {
        if (mHeartRateMeasurementCharacteristic != null) {
            BluetoothLeService.setCharacteristicNotification(mHeartRateMeasurementCharacteristic, false);
            mHeartRateMeasurementCharacteristic = null;
        }
    }

    /**
     * Method to get required characteristics from service
     */
    void getGattData() {
        List<BluetoothGattCharacteristic> characteristics = mService.getCharacteristics();
        for (BluetoothGattCharacteristic c : characteristics) {
            String uuid = c.getUuid().toString();
            if (uuid.equalsIgnoreCase(GattAttributes.BODY_SENSOR_LOCATION)) {
                mBodySensorLocationCharacteristic = c;
            }
            if (uuid.equalsIgnoreCase(GattAttributes.HEART_RATE_MEASUREMENT)) {
                mHeartRateMeasurementCharacteristic = c;
                BluetoothLeService.setCharacteristicNotification(mHeartRateMeasurementCharacteristic, true);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.global, menu);
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
        // Handle action bar item clicks here.
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
        String graphTitle = getResources().getString(R.string.hrm_graph_label);
        String graphXAxis = getResources().getString(R.string.health_temperature_time);
        String graphYAxis = getResources().getString(R.string.hrm_graph_label);

        // Creating an  XYSeries for temperature
        mDataSeries = new XYSeries(graphTitle);

        // Creating a dataset to hold each series
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

        // Adding temperature Series to the dataset
        dataset.addSeries(mDataSeries);

        // Creating XYSeriesRenderer to customize
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setColor(getResources().getColor(R.color.main_bg_color));
        renderer.setPointStyle(PointStyle.CIRCLE);
        renderer.setFillPoints(true);
        renderer.setLineWidth(3);
        // Creating a XYMultipleSeriesRenderer to customize the whole chart
        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
        int deviceDPi = getResources().getDisplayMetrics().densityDpi;
        Logger.e("Device Density>>" + deviceDPi);
        switch (deviceDPi) {
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
                if (deviceDPi > DisplayMetrics.DENSITY_XXHIGH && deviceDPi <= DisplayMetrics.DENSITY_XXXHIGH) {
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
        mGraphLayoutParent = (LinearLayout) parent.findViewById(R.id.chart_container);

        mChart = ChartUtils.getLineChartView(getActivity(), dataset, multiRenderer);

        // Adding the Line Chart to the LinearLayout
        mGraphLayoutParent.addView(mChart);
    }
}
