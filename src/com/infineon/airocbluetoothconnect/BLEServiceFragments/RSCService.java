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
import android.os.SystemClock;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.infineon.airocbluetoothconnect.BLEConnectionServices.BluetoothLeService;
import com.infineon.airocbluetoothconnect.CommonUtils.ChartUtils;
import com.infineon.airocbluetoothconnect.CommonUtils.Constants;
import com.infineon.airocbluetoothconnect.CommonUtils.DecimalTextWatcher;
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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment to display the running speed and cadence
 */
public class RSCService extends Fragment {

    // GATT Services and characteristics
    private static BluetoothGattService mService;
    private static BluetoothGattCharacteristic mNotifyCharacteristic;

    // Data view variables
    private TextView mDistanceRan;
    private TextView mAverageSpeed;
    private TextView mCaloriesBurnt;
    private Chronometer mTimer;
    private EditText mWeightEdittext;

    //ProgressDialog
    private ProgressDialog mProgressDialog;

    /**
     * aChart variables
     */
    private LinearLayout mGraphLayoutParent;
    private double mGraphLastXValue = 0;
    private double mPreviousTime = 0;
    private double mCurrentTime = 0;
    private GraphicalView mChart;
    private XYSeries mDataSeries;

    private boolean mHandlerFlag = false;

    private double mWeightInt;
    private String mWeightString;

    //Constants
    private static final int MAX_WEIGHT = 200;
    private static final int ZERO = 0;
    private static final float WEIGHT_ONE = 1;

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
                // Check CSC service
                if (extras.containsKey(Constants.EXTRA_RSC_VALUE)) {
                    ArrayList<String> received_rsc_data = intent
                            .getStringArrayListExtra(Constants.EXTRA_RSC_VALUE);
                    displayLiveData(received_rsc_data);
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

    public static RSCService create(BluetoothGattService service) {
        mService = service;
        return new RSCService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.runningspeed_n_cadence,
                container, false);
        mDistanceRan = (TextView) rootView.findViewById(R.id.running_distance);
        mAverageSpeed = (TextView) rootView.findViewById(R.id.running_speed);
        mCaloriesBurnt = (TextView) rootView.findViewById(R.id.calories_burnt);
        mTimer = (Chronometer) rootView.findViewById(R.id.time_counter);
        mWeightEdittext = (EditText) rootView.findViewById(R.id.weight_data);
        mWeightEdittext.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mWeightEdittext.addTextChangedListener(new DecimalTextWatcher(mWeightEdittext));
        mProgressDialog = new ProgressDialog(getActivity());

        // Setting up chart
        setupChart(rootView);

        // Start/Stop listener
        Button start_stop_btn = (Button) rootView
                .findViewById(R.id.start_stop_btn);
        start_stop_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Button mBtn = (Button) v;
                String buttonText = mBtn.getText().toString();
                String startText = getResources().getString(
                        R.string.blood_pressure_start_btn);
                String stopText = getResources().getString(
                        R.string.blood_pressure_stop_btn);
                mWeightString = mWeightEdittext.getText().toString();
                try {
                    mWeightInt = Double.parseDouble(mWeightString);
                } catch (NumberFormatException e) {
                    mWeightInt = ZERO;
                }

                //Weight Entered validation
                if ((mWeightString.equalsIgnoreCase("") || mWeightString.equalsIgnoreCase(".")) && buttonText.equalsIgnoreCase(startText)) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.csc_weight_toast_empty), Toast.LENGTH_SHORT).show();
                    mCaloriesBurnt.setText("0.00");
                }

                if ((mWeightString.equalsIgnoreCase("0.")
                        || mWeightString.equalsIgnoreCase("0")) && buttonText.equalsIgnoreCase(startText)) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.csc_weight_toast_zero), Toast.LENGTH_SHORT).show();
                }

                if (mWeightInt < WEIGHT_ONE && mWeightInt > ZERO && buttonText.equalsIgnoreCase(startText)) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.csc_weight_toast_zero), Toast.LENGTH_SHORT).show();
                    mCaloriesBurnt.setText("0.00");
                }

                if (mWeightInt <= MAX_WEIGHT && buttonText.equalsIgnoreCase(startText)) {
                    if (buttonText.equalsIgnoreCase(startText)) {
                        mBtn.setText(stopText);
                        mCaloriesBurnt.setText("0.00");
                        mWeightEdittext.setEnabled(false);
                        getGattData();
                        mTimer.start();
                        mTimer.setBase(SystemClock.elapsedRealtime());
                        mTimer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                            @Override
                            public void onChronometerTick(Chronometer chronometer) {
                                showCaloriesBurnt();
                            }
                        });
                    } else {
                        mWeightEdittext.setEnabled(true);
                        mBtn.setText(startText);
                        stopBroadcastDataNotify(mNotifyCharacteristic);
                        mTimer.stop();
                    }
                } else {
                    if (buttonText.equalsIgnoreCase(startText)) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.csc_weight_toast_greater), Toast.LENGTH_SHORT).show();
                        mBtn.setText(stopText);
                        mCaloriesBurnt.setText("0.00");
                        mWeightEdittext.setEnabled(false);
                        getGattData();
                        mTimer.start();
                        mTimer.setBase(SystemClock.elapsedRealtime());
                        mTimer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                            @Override
                            public void onChronometerTick(Chronometer chronometer) {
                                showCaloriesBurnt();
                            }
                        });
                    } else {
                        mWeightEdittext.setEnabled(true);
                        mBtn.setText(startText);
                        stopBroadcastDataNotify(mNotifyCharacteristic);
                        mCaloriesBurnt.setText("0.00");
                        mTimer.stop();
                    }
                }
                if (buttonText.equalsIgnoreCase(startText)){
                    // Reset chart
                    mDataSeries.clear();
                    mCurrentTime = 0;
                }
            }
        });
        setHasOptionsMenu(true);
        return rootView;
    }

    /**
     * Display live running data
     */
    private void displayLiveData(final ArrayList<String> rsc_data) {
        if (rsc_data != null) {
            try {
                /**
                 * Number formatting to two fractional decimals
                 */
                NumberFormat rootNF = Utils.getNumberFormatForRootLocale();
                Number cycledDist = rootNF.parse(rsc_data.get(1));
                Number avgSpeed = rootNF.parse(rsc_data.get(0));

                NumberFormat defNF = Utils.getNumberFormatForDefaultLocale();
                defNF.setMinimumFractionDigits(2);
                defNF.setMaximumFractionDigits(2);
                String cycledDistStr = defNF.format(cycledDist);
                String avgSpeedStr = defNF.format(avgSpeed);
                mDistanceRan.setText(cycledDistStr);
                mAverageSpeed.setText(avgSpeedStr);

                if (mCurrentTime == 0) {
                    mGraphLastXValue = 0;
                    mCurrentTime = Utils.getTimeInSeconds();
                } else {
                    mPreviousTime = mCurrentTime;
                    mCurrentTime = Utils.getTimeInSeconds();
                    mGraphLastXValue = mGraphLastXValue + (mCurrentTime - mPreviousTime) / 1000;
                }

                float val = avgSpeed.floatValue();
                mDataSeries.add(mGraphLastXValue, val);
                mChart.repaint();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private float showElapsedTime() {
        float SECOND = 1000;
        float MINUTE = 60 * SECOND;
        float elapsedMillis = SystemClock.elapsedRealtime() - mTimer.getBase();
        elapsedMillis = elapsedMillis / MINUTE;
        return elapsedMillis;
    }

    @Override
    public void onResume() {
        super.onResume();
        mHandlerFlag = true;
        BluetoothLeService.registerBroadcastReceiver(getActivity(), mGattUpdateReceiver, Utils.makeGattUpdateIntentFilter());
        Utils.setUpActionBar((AppCompatActivity) getActivity(), R.string.rsc_fragment);
    }

    @Override
    public void onPause() {
        mHandlerFlag = false;
        if (mAverageSpeed != null && mDistanceRan != null) {
            mAverageSpeed.setText("");
            mDistanceRan.setText("");
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        BluetoothLeService.unregisterBroadcastReceiver(getActivity(), mGattUpdateReceiver);
        if (mNotifyCharacteristic != null) {
            stopBroadcastDataNotify(mNotifyCharacteristic);
        }
        super.onDestroy();
    }

    /**
     * Preparing Broadcast receiver to broadcast notify characteristics
     *
     * @param characteristic
     */
    void prepareBroadcastDataNotify(BluetoothGattCharacteristic characteristic) {
        if (BluetoothLeService.isPropertySupported(characteristic, BluetoothGattCharacteristic.PROPERTY_NOTIFY)) {
            BluetoothLeService.setCharacteristicNotification(characteristic, true);
        }
    }

    /**
     * Stopping Broadcast receiver to broadcast notify characteristics
     *
     * @param characteristic
     */
    void stopBroadcastDataNotify(BluetoothGattCharacteristic characteristic) {
        if (BluetoothLeService.isPropertySupported(characteristic, BluetoothGattCharacteristic.PROPERTY_NOTIFY)) {
            if (characteristic != null) {
                BluetoothLeService.setCharacteristicNotification(characteristic, false);
                mNotifyCharacteristic = null;
            }
        }
    }

    /**
     * Method to get required characteristics from service
     */
    void getGattData() {
        List<BluetoothGattCharacteristic> characteristics = mService.getCharacteristics();
        for (BluetoothGattCharacteristic characteristic : characteristics) {
            String uuid = characteristic.getUuid().toString();
            if (uuid.equalsIgnoreCase(GattAttributes.RSC_MEASUREMENT)) {
                mNotifyCharacteristic = characteristic;
                prepareBroadcastDataNotify(characteristic);
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
        String graphTitle = getResources().getString(R.string.rsc_fragment);
        String graphXAxis = getResources().getString(R.string.health_temperature_time);
        String graphYAxis = getResources().getString(R.string.rsc_avg_speed);

        // Creating an  XYSeries for running speed
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
        renderer.setLineWidth(5);

        // Creating a XYMultipleSeriesRenderer to customize the whole chart
        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
        int deviceDPi = getResources().getDisplayMetrics().densityDpi;
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
                if (deviceDPi > DisplayMetrics.DENSITY_XXHIGH && deviceDPi <=
                        DisplayMetrics.DENSITY_XXXHIGH) {
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
        multiRenderer.setYAxisMin(0);
        multiRenderer.setXAxisMin(0);
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

    private void showCaloriesBurnt() {
        try {
            NumberFormat rootNF = Utils.getNumberFormatForRootLocale();
            Number numWeight = rootNF.parse(mWeightString);
            mWeightInt = numWeight.doubleValue();
            double caloriesBurnt = (((showElapsedTime()) * mWeightInt) * 8);
            caloriesBurnt = caloriesBurnt / 1000;

            NumberFormat defNF = Utils.getNumberFormatForDefaultLocale();
            defNF.setMinimumFractionDigits(4);
            defNF.setMaximumFractionDigits(4);
            String finalBurn = defNF.format(caloriesBurnt);
            mCaloriesBurnt.setText(finalBurn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
