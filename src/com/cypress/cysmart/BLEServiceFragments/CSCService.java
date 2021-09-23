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

package com.cypress.cysmart.BLEServiceFragments;

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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.cypress.cysmart.BLEConnectionServices.BluetoothLeService;
import com.cypress.cysmart.BLEProfileDataParserClasses.CSCParser;
import com.cypress.cysmart.CommonUtils.ChartUtils;
import com.cypress.cysmart.CommonUtils.Constants;
import com.cypress.cysmart.CommonUtils.DecimalTextWatcher;
import com.cypress.cysmart.CommonUtils.GattAttributes;
import com.cypress.cysmart.CommonUtils.Logger;
import com.cypress.cysmart.CommonUtils.Utils;
import com.cypress.cysmart.R;

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
 * Fragment to display the Cycling speed cadence
 */
public class CSCService extends Fragment {

    // GATT Service and Characteristics
    private static BluetoothGattService mService;
    private static BluetoothGattCharacteristic mNotifyCharacteristic;

    // Data field variables
    private TextView mDistanceRan;
    private TextView mCadence;
    private TextView mCaloriesBurnt;
    private TextView mDistanceUnit;
    private Chronometer mTimer;
    private EditText mWeightEditText;
    private EditText mRadiusEditText;

    //ProgressDialog
    private ProgressDialog mProgressDialog;

    private double mWeightInt;
    private String mWeightString;
    public static double mRadiusInt = 0.0;
    private String mRadiusString;

    /**
     * Chart variables
     */
    private LinearLayout mGraphLayoutParent;
    private double mGraphLastXValue = 0;
    private double mPreviousTime = 0;
    private double mCurrentTime = 0;
    private GraphicalView mChart;
    private XYSeries mDataSeries;

    //Constants
    private static final int MAX_WEIGHT = 200;
    private static final int ZERO = 0;
    private static final float WEIGHT_ONE = 1;

    private static final float MINIMUM_RADIUS = 300;
    private static final float MAXIMUM_RADIUS = 725;

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
                // Check CSC value
                if (extras.containsKey(Constants.EXTRA_CSC_VALUE)) {
                    ArrayList<String> cscDataReceived = intent.getStringArrayListExtra(Constants.EXTRA_CSC_VALUE);
                    displayLiveData(cscDataReceived);
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

    public static CSCService create(BluetoothGattService service) {
        mService = service;
        return new CSCService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.cyclingspeed_n_cadence, container, false);
        mDistanceRan = rootView.findViewById(R.id.cycling_distance);
        mCadence = rootView.findViewById(R.id.cadence);
        mDistanceUnit = rootView.findViewById(R.id.distance_unit);
        mCaloriesBurnt = rootView.findViewById(R.id.calories_burnt);
        mTimer = rootView.findViewById(R.id.time_counter);
        mWeightEditText = rootView.findViewById(R.id.weight_data);
        mWeightEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mWeightEditText.addTextChangedListener(new DecimalTextWatcher(mWeightEditText));
        mRadiusEditText = rootView.findViewById(R.id.radius_data);
        mRadiusEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mRadiusEditText.addTextChangedListener(new DecimalTextWatcher(mRadiusEditText));
        mProgressDialog = new ProgressDialog(getActivity());

        // Setting up chart
        setupChart(rootView);

        // Start/Stop listener
        Button startStopButton = (Button) rootView.findViewById(R.id.start_stop_btn);
        startStopButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Button button = (Button) v;
                String buttonText = button.getText().toString();
                String startText = getResources().getString(R.string.blood_pressure_start_btn);
                String stopText = getResources().getString(R.string.blood_pressure_stop_btn);
                mWeightString = mWeightEditText.getText().toString();
                mRadiusString = mRadiusEditText.getText().toString();
                try {
                    mWeightInt = Double.parseDouble(mWeightString);
                    mRadiusInt = Double.parseDouble(mRadiusString);
                } catch (NumberFormatException e) {
                    mRadiusInt = ZERO;
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

                //Radius Entered Validation
                if ((mRadiusString.equalsIgnoreCase("") || mRadiusString.equalsIgnoreCase(".")) && buttonText.equalsIgnoreCase(startText)) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.csc_radius_toast_empty), Toast.LENGTH_SHORT).show();
                }

                if ((mRadiusString.equalsIgnoreCase("0.")
                        || mRadiusString.equalsIgnoreCase("0")) && buttonText.equalsIgnoreCase(startText)) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.csc_radius_toast_less), Toast.LENGTH_SHORT).show();
                }

                if (mRadiusInt > ZERO && mRadiusInt < MINIMUM_RADIUS && buttonText.equalsIgnoreCase(startText)) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.csc_radius_toast_less), Toast.LENGTH_SHORT).show();
                }

                if ((mRadiusInt > MAXIMUM_RADIUS && buttonText.equalsIgnoreCase(startText))) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.csc_radius_toast_greater), Toast.LENGTH_SHORT).show();
                }

                if (mWeightInt <= MAX_WEIGHT) {
                    if (buttonText.equalsIgnoreCase(startText)) {
                        button.setText(stopText);
                        mCaloriesBurnt.setText("0.00");
                        mWeightEditText.setEnabled(false);
                        mRadiusEditText.setEnabled(false);
                        getGattData();
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
                        mTimer.start();
                        mTimer.setBase(SystemClock.elapsedRealtime());
                        mTimer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                            @Override
                            public void onChronometerTick(Chronometer chronometer) {
                                showCaloriesBurnt();
                            }
                        });
                    } else {
                        mWeightEditText.setEnabled(true);
                        mRadiusEditText.setEnabled(true);
                        button.setText(startText);
                        stopBroadcastDataNotify(mNotifyCharacteristic);
                        mTimer.stop();
                    }
                } else {
                    if (buttonText.equalsIgnoreCase(startText)) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.csc_weight_toast_greater), Toast.LENGTH_SHORT).show();
                        button.setText(stopText);
                        mCaloriesBurnt.setText("0.00");
                        mWeightEditText.setEnabled(false);
                        mRadiusEditText.setEnabled(false);
                        getGattData();
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
                        mTimer.start();
                        mTimer.setBase(SystemClock.elapsedRealtime());
                        mTimer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                            @Override
                            public void onChronometerTick(Chronometer chronometer) {
                                showCaloriesBurnt();
                            }
                        });
                    } else {
                        mWeightEditText.setEnabled(true);
                        mRadiusEditText.setEnabled(true);
                        button.setText(startText);
                        stopBroadcastDataNotify(mNotifyCharacteristic);
                        mCaloriesBurnt.setText("0.00");
                        mTimer.stop();
                    }
                }

            }
        });
        setHasOptionsMenu(true);
        return rootView;
    }

    /**
     * Display live cycling data
     */
    private void displayLiveData(final ArrayList<String> cscData) {
        if (cscData != null) {
            String distanceString = cscData.get(CSCParser.INDEX_CYCLING_DISTANCE);
            String cadenceString = cscData.get(CSCParser.INDEX_CYCLING_CADENCE);

            boolean distanceValid = false;
            double distance = 0;
            if (distanceString != null) {
                try {
                    distance = Double.parseDouble(distanceString);
                    distanceValid = true;
                } catch (NumberFormatException e) {
                }
            }
            if (!distanceValid) {
                Logger.e("Invalid Cycling Distance: " + distanceString);
            }

            boolean cadenceValid = false;
            float cadence = 0;
            if (cadenceString != null) {
                try {
                    cadence = Float.parseFloat(cadenceString);
                    cadenceValid = true;
                } catch (NumberFormatException e) {
                }
            }
            if (!distanceValid) {
                Logger.e("Invalid Cycling Cadence: " + cadenceString);
            }

            if (distanceValid) {
                if (distance < 1000) { // 1 km in m
                    mDistanceRan.setText(Utils.formatForDefaultLocale("%.0f", distance));
                    mDistanceUnit.setText(R.string.csc_distance_unit_m);
                } else {
                    mDistanceRan.setText(Utils.formatForDefaultLocale("%.2f", distance / 1000.0f));
                    mDistanceUnit.setText(R.string.csc_distance_unit_km);
                }
            }

            if (cadenceValid) {
                mCadence.setText(cadenceString);
                if (mCurrentTime == 0) {
                    mCurrentTime = Utils.getTimeInSeconds();
                    mGraphLastXValue = 0;
                } else {
                    mPreviousTime = mCurrentTime;
                    mCurrentTime = Utils.getTimeInSeconds();
                    mGraphLastXValue += (mCurrentTime - mPreviousTime) / 1000;
                }
                mDataSeries.add(mGraphLastXValue, cadence);
                mChart.repaint();
            }
        }
    }

    private float showElapsedTime() {
        final float SECOND = 1000;
        float MINUTE = 60 * SECOND;
        float elapsedMillis = SystemClock.elapsedRealtime() - mTimer.getBase();
        elapsedMillis = elapsedMillis / MINUTE;
        return elapsedMillis;
    }

    @Override
    public void onResume() {
        super.onResume();
        BluetoothLeService.registerBroadcastReceiver(getActivity(), mGattUpdateReceiver, Utils.makeGattUpdateIntentFilter());
        Utils.setUpActionBar((AppCompatActivity) getActivity(), R.string.csc_fragment);
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
            mNotifyCharacteristic = characteristic;
            BluetoothLeService.setCharacteristicNotification(characteristic, true);
        }
    }

    /**
     * Stopping Broadcast receiver to broadcast notify characteristics
     *
     * @param characteristic
     */
    void stopBroadcastDataNotify(BluetoothGattCharacteristic characteristic) {
        if (characteristic != null) {
            if (BluetoothLeService.isPropertySupported(characteristic, BluetoothGattCharacteristic.PROPERTY_NOTIFY)) {
                BluetoothLeService.setCharacteristicNotification(characteristic, false);
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
            if (uuid.equalsIgnoreCase(GattAttributes.CSC_MEASUREMENT)) {
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
        String graphTitle = getResources().getString(R.string.csc_fragment);
        String graphXAxis = getResources().getString(R.string.health_temperature_time);
        String graphYAxis = getResources().getString(R.string.csc_cadence_graph);

        // Creating an  XYSeries for running speed
        mDataSeries = new XYSeries(graphTitle);

        // Creating a dataset to hold each series
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

        // Adding temperature Series to the dataset
        dataset.addSeries(mDataSeries);

        // Creating XYSeriesRenderer to customize
        XYSeriesRenderer mRenderer = new XYSeriesRenderer();
        mRenderer.setColor(getResources().getColor(R.color.main_bg_color));
        mRenderer.setPointStyle(PointStyle.CIRCLE);
        mRenderer.setFillPoints(true);
        mRenderer.setLineWidth(5);

        // Creating a XYMultipleSeriesRenderer to customize the whole chart
        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
        int deviceDPI = getResources().getDisplayMetrics().densityDpi;
        switch (deviceDPI) {
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
        multiRenderer.addSeriesRenderer(mRenderer);

        // Getting a reference to LinearLayout of the MainActivity Layout
        mGraphLayoutParent = parent.findViewById(R.id.chart_container);

        mChart = ChartUtils.getLineChartView(getActivity(), dataset, multiRenderer);

        // Adding the Line Chart to the LinearLayout
        mGraphLayoutParent.addView(mChart);
    }

    private void showCaloriesBurnt() {
        try {
            NumberFormat rootNF = Utils.getNumberFormatForRootLocale();
            Number numWeight = rootNF.parse(mWeightString);
            mWeightInt = numWeight.floatValue();
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
