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

package com.infineon.airocbluetoothconnect.CommonFragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.infineon.airocbluetoothconnect.BLEConnectionServices.BluetoothLeService;
import com.infineon.airocbluetoothconnect.CommonUtils.Constants;
import com.infineon.airocbluetoothconnect.CommonUtils.Logger;
import com.infineon.airocbluetoothconnect.CommonUtils.ToastUtils;
import com.infineon.airocbluetoothconnect.CommonUtils.Utils;
import com.infineon.airocbluetoothconnect.HomePageActivity;
import com.infineon.airocbluetoothconnect.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileScanningFragment extends FragmentWithPermissionCheck implements View.OnClickListener {

    // Connection delay
    private static final long DELAY_MILLIS = 500;

    // Stops scanning after 2 seconds.
    private static final long SCAN_TIMEOUT = 2000;
    private boolean mScanning;
    private final Handler mScanTimeOutHandler = new Handler(Looper.getMainLooper());
    private final Runnable mScanTimeOutRunnable = new Runnable() {
        @Override
        public void run() {
            BluetoothLeScanner scanner = getScanner();
            if (scanner != null && getBluetoothAdapter().isEnabled()) {
                mScanning = false;
                stopScan();
            }
            mSwipeLayout.setRefreshing(false);
        }
    };

    // Connection time out after 10 seconds.
    private static final long CONNECTION_TIMEOUT = 10000;
    private final Handler mConnectTimeOutHandler = new Handler(Looper.getMainLooper());
    private final Runnable mConnectTimeOutRunnable = new Runnable() {
        @Override
        public void run() {
            Logger.e("PSF: connect: connection time out");
            mConnectTimerON = false;
            BluetoothLeService.disconnect();
            dismissProgressDialog();
            if (getActivity() != null) {
                ToastUtils.makeText(R.string.profile_cannot_connect_message, Toast.LENGTH_SHORT);
                clearDeviceList();
                setScanningState(true);
            }
        }
    };
    private boolean mConnectTimerON;
    private ProgressDialog mProgressDialog;

    // device details
    public static String mDeviceName = "name";
    public static String mDeviceAddress = "address";
    private static String DEVICE_NAME_UNKNOWN;

    //Pair status button and variables
    public static Button mPairButton;

    // Devices list variables
    private static ArrayList<BluetoothDevice> mDevices;
    private DeviceListAdapter mDeviceListAdapter;
    private SwipeRefreshLayout mSwipeLayout;
    private Map<String, Integer> mDevRssiValues;

    private boolean mFilteringActive = false;
    private ArrayList<BluetoothDevice> mFilteredDevices;

    //GUI elements
    private ListView mDeviceListView;
    private MenuItem mSearchMenuItem;

    private View mLocationDisabledAlertView;
    private Button mLocationEnableButton;
    private Button mLocationMoreButton;

    private final Handler mShowKeyboardHandler = new Handler(Looper.getMainLooper());
    private final Runnable mShowKeyboardRunnable = new Runnable() {
        @Override
        public void run() {
            View view = getActivity().getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            }
        }
    };

    /**
     * Call back for BLE Scan
     * This call back is called when a BLE device is found near by.
     */
    private final ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, final ScanResult result) {
            if (callbackType != ScanSettings.CALLBACK_TYPE_ALL_MATCHES) {
                // Should not happen.
                return;
            }
            ScanRecord scanRecord = result.getScanRecord();
            if (scanRecord == null) {
                return;
            }
            Activity activity = getActivity();
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDeviceListAdapter.addDevice(result.getDevice(), result.getRssi());
                        notifyDeviceListUpdated();
                    }
                });
            }
        }
    };

    /**
     * BroadcastReceiver for receiving the GATT communication status
     */
    private final BroadcastReceiver mGattConnectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            // Status received when connected to GATT Server
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                Logger.d("PSF: connect: BluetoothLeService.ACTION_GATT_CONNECTED");
                showConnectionEstablishedInfo();
                if (mScanning) {
                    stopScan();
                    mScanning = false;
                }
                dismissProgressDialog();
                cancelConnectTimer();
                clearDeviceList();
                updateWithNewFragment();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Logger.d("PSF: connect: BluetoothLeService.ACTION_GATT_DISCONNECTED");
                /*
                  Disconnect event.When the connect timer is ON, reconnect the device
                  else showToast disconnect message
                 */
                if (mConnectTimerON) {
                    BluetoothLeService.reconnectLastDevice();
                } else {
                    ToastUtils.makeText(R.string.profile_cannot_connect_message, Toast.LENGTH_SHORT);
                }
            } else if (LocationManager.PROVIDERS_CHANGED_ACTION.equals(action)) {
                Logger.d("PSF: location: LocationManager.PROVIDERS_CHANGED_ACTION");
                checkLocationEnabled();
            }
        }
    };

    /**
     * TextWatcher for filtering the list devices
     */
    private final TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mDeviceListAdapter.getFilter().filter(s.toString());
            mDeviceListAdapter.notifyDataSetInvalidated();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Utils.debug("PSF: lifecycle: onCreateView", this, getActivity());

        DEVICE_NAME_UNKNOWN = getContext().getResources().getText(R.string.device_unknown).toString();

        View rootView = inflater.inflate(R.layout.fragment_profile_scan, container, false);
        mLocationDisabledAlertView = rootView.findViewById(R.id.location_disabled_alert);
        mLocationEnableButton = rootView.findViewById(R.id.location_enable);
        mLocationEnableButton.setOnClickListener(this);
        mLocationMoreButton = rootView.findViewById(R.id.location_more);
        mLocationMoreButton.setOnClickListener(this);
        mDevRssiValues = new HashMap<>();
        mSwipeLayout = rootView.findViewById(R.id.swipe_container);
        mDeviceListView = rootView.findViewById(R.id.listView_profiles);
        mDeviceListAdapter = new DeviceListAdapter();
        mDeviceListView.setAdapter(mDeviceListAdapter);
        mDeviceListView.setTextFilterEnabled(true);
        setHasOptionsMenu(true);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle(getResources().getString(R.string.alert_message_connect_title));
        mProgressDialog.setCancelable(false);

        prepareDeviceList();

        /*
          Swipe listener,initiate a new scan on refresh. Stop the swipe refresh
          after 5 seconds
         */
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSearchMenuItem.collapseActionView();
                permissionManager.resetLocationAndBtPermissionsCanBeAsked();

                if (!mScanning) {
                    // Prepare list view and initiate scanning
                    clearDeviceList();
                    setScanningState(true);
                }
            }
        });

        /*
          Creating the dataLogger file and
          updating the dataLogger history
         */
        Logger.createDataLoggerFile(getActivity());
        mDeviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (mDeviceListAdapter.getCount() > 0) {
                    final BluetoothDevice device = mDeviceListAdapter.getDevice(position);
                    if (device != null) {
                        setScanningState(false);
                        connectDevice(device);
                    }
                }
            }
        });

        mDeviceListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private boolean scrollEnabled;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition = (mDeviceListView == null || mDeviceListView.getChildCount() == 0) ? 0 : mDeviceListView.getChildAt(0).getTop();
                boolean newScrollEnabled = firstVisibleItem == 0 && topRowVerticalPosition >= 0;
                if (mSwipeLayout != null && scrollEnabled != newScrollEnabled) {
                    // Start refreshing....
                    mSwipeLayout.setEnabled(newScrollEnabled);
                    scrollEnabled = newScrollEnabled;
                }
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Utils.debug("PSF: lifecycle: onStart", this, getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.debug("PSF: lifecycle: onResume", this, getActivity());
        BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            prepareDeviceList();
        }
        checkLocationEnabled();

        permissionManager.checkAndRequestNotificationPermission();
        Logger.d("PSF: connect: registering mGattConnectReceiver");
        BluetoothLeService.registerBroadcastReceiver(getActivity(), mGattConnectReceiver, Utils.makeGattUpdateIntentFilter());
    }

    @Override
    public void onPause() {
        Utils.debug("PSF: lifecycle: onPause", this, getActivity());
        dismissProgressDialog();
        Logger.d("PSF: connect: unregistering mGattConnectReceiver");
        BluetoothLeService.unregisterBroadcastReceiver(getActivity(), mGattConnectReceiver);
        super.onPause();
    }

    @Override
    public void onStop() {
        Utils.debug("PSF: lifecycle: onStop", this, getActivity());
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Utils.debug("PSF: lifecycle: onDestroy", this, getActivity());
        setScanningState(false);
        clearDeviceList();
        mSwipeLayout.setRefreshing(false);

        // Cancel tasks
        mScanTimeOutHandler.removeCallbacks(mScanTimeOutRunnable);
        mConnectTimeOutHandler.removeCallbacks(mConnectTimeOutRunnable);
        super.onDestroy();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.global, menu);

        mSearchMenuItem = menu.findItem(R.id.search);
        EditText actionView = (EditText) mSearchMenuItem.getActionView();
        actionView.addTextChangedListener(mTextWatcher);

        mSearchMenuItem.setVisible(true);
        mSearchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                mFilteringActive = false;
                // Clear filter text (quietly)
                ((EditText) mSearchMenuItem.getActionView()).removeTextChangedListener(mTextWatcher);
                ((EditText) mSearchMenuItem.getActionView()).setText("");
                ((EditText) mSearchMenuItem.getActionView()).addTextChangedListener(mTextWatcher);
                // Clear mFilteredDevices
                mFilteredDevices.clear();
                // Re-draw GUI from mLeDevices
                mDeviceListAdapter.notifyDataSetChanged();

                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                return true; // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                mFilteringActive = true;
                // Init mFilteredDevices
                mFilteredDevices.clear();
                for (int i = 0, count = mDevices.size(); i < count; i++) {
                    BluetoothDevice device = mDevices.get(i);
                    mDeviceListAdapter.addFilteredDevice(device, mDeviceListAdapter.getRssiValue());
                }
                // Re-draw GUI from mFilteredDevices
                mDeviceListAdapter.notifyDataSetChanged();

                mSearchMenuItem.getActionView().requestFocus();
                mShowKeyboardHandler.post(mShowKeyboardRunnable);
                return true; // Return true to expand action view
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.location_enable:
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                break;
            case R.id.location_more:
                final AlertDialog alert[] = {null};
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.alert_message_location_required_title).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        CheckBox checkBox = alert[0].findViewById(R.id.dont_ask_again);
                        if (checkBox.isChecked()) {
                            Utils.setBooleanSharedPreference(getActivity(), Constants.PREF_LOCATION_REQUIRED_DONT_ASK_AGAIN, true);
                            mLocationDisabledAlertView.setVisibility(View.GONE);
                        }
                    }
                }).setView(R.layout.dialog_location_required).setCancelable(false);
                alert[0] = builder.show();
                break;
            default:
                Logger.e("Unhandled click event");
                break;
        }
    }

    private void updateWithNewFragment() {
        clearDeviceList();
        Utils.replaceFragment(getActivity(), new ServiceDiscoveryFragment(), Constants.SERVICE_DISCOVERY_FRAGMENT_TAG);
    }

    /**
     * Method to connect to the device selected. The time allotted for having a
     * connection is 10 seconds. After 10 seconds it will disconnect if not
     * connected and initiate scan once more
     *
     * @param device : remote bluetooth device to connect
     */
    private void connectDevice(BluetoothDevice device) {
        mDeviceAddress = device.getAddress();
        mDeviceName = device.getName();
        connectDevice();
    }

    private void connectDevice() {
        HomePageActivity.mPairingStarted = false;
        HomePageActivity.mAuthenticatedPairing = false;
        // Get the connection status of the device
        if (BluetoothLeService.getConnectionState() == BluetoothLeService.STATE_DISCONNECTED) {
            Logger.d("PSF: connectDevice: BluetoothLeService.STATE_DISCONNECTED");
            // Disconnected, so connect
            BluetoothLeService.connect(mDeviceAddress, mDeviceName, getActivity());
            showConnectionInProgressInfo(mDeviceName, mDeviceAddress);
        } else {
            Logger.d("PSF: connectDevice: BLE OTHER STATE: " + BluetoothLeService.getConnectionState());
            // Connecting to some devices, so disconnect and then connect
            BluetoothLeService.disconnect();
            Handler delayHandler = new Handler();
            delayHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (getActivity() != null) {
                        BluetoothLeService.connect(mDeviceAddress, mDeviceName, getActivity());
                        showConnectionInProgressInfo(mDeviceName, mDeviceAddress);
                    }
                }
            }, DELAY_MILLIS);
        }
        startConnectTimer();
    }

    private void showConnectionInProgressInfo(String deviceName, String deviceAddress) {
        mProgressDialog.setMessage(getResources().getString(R.string.alert_message_connect) + "\n" + deviceName + "\n" + deviceAddress + "\n" + getResources().getString(R.string.alert_message_wait));
        showProgressDialog();
    }

    private void showConnectionEstablishedInfo() {
        mProgressDialog.setMessage(getString(R.string.alert_message_bluetooth_connect));
        showProgressDialog();
    }

    private void showProgressDialog() {
        mProgressDialog.show();
    }

    private void dismissProgressDialog() {
        mProgressDialog.dismiss();
    }

    /**
     * Method to scan BLE Devices. The status of the scan will be detected in
     * the BluetoothAdapter.LeScanCallback
     *
     * @param enable :
     *               true to start scanning, if not started already
     *               false to stop scanning, if we are scanning now
     */
    private void setScanningState(final boolean enable) {
        // Stop scanning
        if (!enable) {
            cancelScanTimer();
            mScanning = false;
            mSwipeLayout.setRefreshing(false);
            stopScan();
            return;
        }

        // Scanning not possible
        if (getScanner() == null) {
            mSwipeLayout.setRefreshing(false);
            return;
        }

        // Insufficient permissions
        if (!permissionManager.checkAndRequestPrimaryPermissions()) {
            mSwipeLayout.setRefreshing(false);
            return;
        }

        // Start scanning if we aren't scanning at the moment
        if (!mScanning) {
            startScanTimer();
            mScanning = true;
            mSwipeLayout.setRefreshing(true);
            startScan();
        }
    }

    private BluetoothLeScanner getScanner() {
        permissionManager.checkAndRequestPrimaryPermissions();
        BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();
        if (bluetoothAdapter == null) {
            Logger.e("PSF: getScanner: cannot get BluetoothAdapter");
            return null;
        }

        BluetoothLeScanner scanner = bluetoothAdapter.getBluetoothLeScanner();
        if (scanner == null) {
            Logger.e("PSF: getScanner: cannot get BluetoothLeScanner");
            return null;
        }
        return scanner;
    }

    private void startScan() {
        BluetoothLeScanner scanner = getScanner();
        if (scanner != null) {
            ScanSettings settings = new ScanSettings.Builder()
//                    .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY) // Scan using highest duty cycle
                    .build();

            scanner.startScan(null, settings, mScanCallback);
        }
    }

    private void stopScan() {
        BluetoothLeScanner scanner = getScanner();
        if (scanner != null) {
            scanner.stopScan(mScanCallback);
        }
    }


    /**
     * Swipe refresh timer
     */
    public void startScanTimer() {
        cancelScanTimer();
        mScanTimeOutHandler.postDelayed(mScanTimeOutRunnable, SCAN_TIMEOUT);
    }

    private void cancelScanTimer() {
        mScanTimeOutHandler.removeCallbacks(mScanTimeOutRunnable);
    }

    private void startConnectTimer() {
        cancelConnectTimer();
        mConnectTimeOutHandler.postDelayed(mConnectTimeOutRunnable, CONNECTION_TIMEOUT);
        mConnectTimerON = true;
    }

    private void cancelConnectTimer() {
        mConnectTimeOutHandler.removeCallbacks(mConnectTimeOutRunnable);
        mConnectTimerON = false;
    }

    /**
     * Preparing the BLE device list
     */
    public void prepareDeviceList() {
        // Initializes ActionBar as required
        Utils.setUpActionBar((AppCompatActivity) getActivity(), R.string.profile_scan_fragment);
        // Prepare list view and initiate scanning
        mDeviceListAdapter = new DeviceListAdapter();
        mDeviceListView.setAdapter(mDeviceListAdapter);
        setScanningState(true);
    }

    private void pairDevice(BluetoothDevice device) {
        boolean success = BluetoothLeService.pairDevice(device);
        if (!success) {
            dismissProgressDialog();
        }
    }

    private void unpairDevice(BluetoothDevice device) {
        boolean success = BluetoothLeService.unpairDevice(device);
        if (!success) {
            dismissProgressDialog();
        }
    }

    private void checkLocationEnabled() {
        // Since Marshmallow access to Location Service is required for BLE scan
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean dontAskAgain = Utils.getBooleanSharedPreference(getActivity(), Constants.PREF_LOCATION_REQUIRED_DONT_ASK_AGAIN);
            if (!dontAskAgain) {
                LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
//                if (!lm.isLocationEnabled()) { // Available in API 28
                if ((!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) && (!lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER))) {
                    mLocationDisabledAlertView.setVisibility(View.VISIBLE);
                } else {
                    mLocationDisabledAlertView.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * Holder class for the list view view widgets
     */
    private static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        TextView deviceRssi;
        Button pairStatus;
    }

    private void clearDeviceList() {
        if (mDeviceListAdapter != null) {
            mDeviceListAdapter.clear();
            notifyDeviceListUpdated();
        }
    }

    private void notifyDeviceListUpdated() {
        try {
            mDeviceListAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * List Adapter for holding devices found through scanning.
     */
    private class DeviceListAdapter extends BaseAdapter implements Filterable {

        private final LayoutInflater mInflater;
        private int mRssiValue;
        private final ItemFilter mFilter = new ItemFilter();

        public DeviceListAdapter() {
            super();
            mDevices = new ArrayList<>();
            mFilteredDevices = new ArrayList<>();
            mInflater = getActivity().getLayoutInflater();
        }

        private void addDevice(BluetoothDevice device, int rssi) {
            this.mRssiValue = rssi;
            // New device found
            mDevRssiValues.put(device.getAddress(), rssi);
            if (!mDevices.contains(device)) {
                mDevices.add(device);
            }
        }

        private void addFilteredDevice(BluetoothDevice device, int rssi) {
            this.mRssiValue = rssi;
            // New device found
            mDevRssiValues.put(device.getAddress(), rssi);
            if (!mFilteredDevices.contains(device)) {
                mFilteredDevices.add(device);
            }
        }

        public int getRssiValue() {
            return mRssiValue;
        }

        /**
         * Getter method to get the Bluetooth device
         *
         * @param position : index of device in list or filtered list
         * @return BluetoothDevice
         */
        public BluetoothDevice getDevice(int position) {
            final ArrayList<BluetoothDevice> devices = mFilteringActive ? mFilteredDevices : mDevices;
            return devices.get(position);
        }

        /**
         * Clearing all values in the device array list
         */
        public void clear() {
            mDevices.clear();
            mFilteredDevices.clear();
        }

        @Override
        public int getCount() {
            final ArrayList<BluetoothDevice> devices = mFilteringActive ? mFilteredDevices : mDevices;
            return devices.size();
        }


        @Override
        public Object getItem(int i) {
            final ArrayList<BluetoothDevice> devices = mFilteringActive ? mFilteredDevices : mDevices;
            return devices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {
            final ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflater.inflate(R.layout.listitem_device, viewGroup, false);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = view.findViewById(R.id.device_address);
                viewHolder.deviceName = view.findViewById(R.id.device_name);
                viewHolder.deviceRssi = view.findViewById(R.id.device_rssi);
                viewHolder.pairStatus = view.findViewById(R.id.btn_pair);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            /*
              Setting the name and the RSSI of the BluetoothDevice. provided it
              is a valid one
             */
            final ArrayList<BluetoothDevice> devices = mFilteringActive ? mFilteredDevices : mDevices;
            final BluetoothDevice device = devices.get(position);
            final String deviceName = device.getName();
            final String deviceAddress = device.getAddress();

            // Set Address
            viewHolder.deviceAddress.setText(deviceAddress);

            // Set RSSI
            if (mDevRssiValues.containsKey(deviceAddress)) {
                viewHolder.deviceRssi.setText(String.valueOf(mDevRssiValues.get(deviceAddress)));
            }

            // Set Name and Bond state
            if (deviceName != null && deviceName.length() > 0) {
                viewHolder.deviceName.setText(deviceName);

                try {
                    String pairStatus = (device.getBondState() == BluetoothDevice.BOND_BONDED) ? getActivity().getResources().getString(R.string.bluetooth_pair) : getActivity().getResources().getString(R.string.bluetooth_unpair);
                    viewHolder.pairStatus.setText(pairStatus);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                viewHolder.deviceName.setText(DEVICE_NAME_UNKNOWN);
            }

            viewHolder.pairStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPairButton = (Button) view;
                    mDeviceAddress = device.getAddress();
                    mDeviceName = device.getName();
                    String status = mPairButton.getText().toString();
                    if (status.equalsIgnoreCase(getResources().getString(R.string.bluetooth_pair))) {
                        unpairDevice(device);
                    } else {
                        pairDevice(device);
                    }
                }
            });
            return view;
        }

        @Override
        public Filter getFilter() {
            return mFilter;
        }

        private class ItemFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                // Not performing filtering here as this method is executed in non-main thread.
                // Instead performing filtering in publishResults which is executed in main thread.
                // This is to omit synchronized access to the mDevices variable.
                return new FilterResults();
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                String filterString = constraint.toString().toLowerCase();
                final ArrayList<BluetoothDevice> list = mDevices;
                final ArrayList<BluetoothDevice> filteredList = new ArrayList<>(list.size());

                for (int i = 0, n = list.size(); i < n; i++) {
                    String name = list.get(i).getName();
                    if (name == null) {
                        name = DEVICE_NAME_UNKNOWN;
                    }
                    name = name.toLowerCase();

                    if (name.contains(filterString)) {
                        filteredList.add(list.get(i));
                    }
                }

                mFilteredDevices.clear();
                for (int i = 0, n = filteredList.size(); i < n; i++) {
                    BluetoothDevice device = filteredList.get(i);
                    mDeviceListAdapter.addFilteredDevice(device, mDeviceListAdapter.getRssiValue());
                }
                notifyDataSetChanged(); // notifies the data with new filtered values
            }
        }

    }

    private static BluetoothAdapter getBluetoothAdapter() {
        return HomePageTabbedFragment.mBluetoothAdapter;
    }
}
