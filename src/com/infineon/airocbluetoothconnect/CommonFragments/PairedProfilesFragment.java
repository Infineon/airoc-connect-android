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

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
import java.util.Set;

public class PairedProfilesFragment extends FragmentWithPermissionCheck {

    //Delay Time out
    private static final long DELAY_MILLIS = 500;

    // Connection time out after 10 seconds.
    private static final long CONNECTION_TIMEOUT = 10000;
    private Handler mConnectTimeOutHandler = new Handler(Looper.getMainLooper());
    private Runnable mConnectTimeOutRunnable = new Runnable() {
        @Override
        public void run() {
            Logger.e("PPF: connect: connection time out");
            mConnectTimerON = false;
            BluetoothLeService.disconnect();
            dismissProgressDialog();
            if (getActivity() != null) {
                ToastUtils.makeText(R.string.profile_cannot_connect_message, Toast.LENGTH_SHORT);
                getBondedDevices();
            }
        }
    };
    private boolean mConnectTimerON;
    private ProgressDialog mProgressDialog;

    // device details
    private static String mDeviceName = "name";
    private static String mDeviceAddress = "address";
    private static String DEVICE_NAME_UNKNOWN;

    //Pair status button and variables
    public static Button mPairButton;

    // Devices list variables
    private static ArrayList<BluetoothDevice> mDevices;
    private DeviceListAdapter mDeviceListAdapter;
    private SwipeRefreshLayout mSwipeLayout;

    private boolean mFilteringActive = false;
    private ArrayList<BluetoothDevice> mFilteredDevices;

    //GUI elements
    private ListView mDeviceListView;
    private MenuItem mSearchMenuItem;

    private Handler mShowKeyboardHandler = new Handler(Looper.getMainLooper());
    private Runnable mShowKeyboardRunnable = new Runnable() {
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
     * BroadcastReceiver for receiving the GATT communication status
     */
    private final BroadcastReceiver mGattConnectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            // Status received when connected to GATT Server
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                Logger.d("PPF: connect: BluetoothLeService.ACTION_GATT_CONNECTED");
                showConnectionEstablishedInfo();
                dismissProgressDialog();
                cancelConnectTimer();
                clearDeviceList();
                updateWithNewFragment();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Logger.d("PPF: connect: BluetoothLeService.ACTION_GATT_DISCONNECTED");
                /**
                 * Disconnect event.When the connect timer is ON, reconnect the device
                 * else showToast disconnect message
                 */
                if (mConnectTimerON) {
                    BluetoothLeService.reconnectLastDevice();
                } else {
                    ToastUtils.makeText(R.string.profile_cannot_connect_message, Toast.LENGTH_SHORT);
                }
            } else if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                getBondedDevices();
            }
        }
    };

    /**
     * TextWatcher for filtering the list devices
     */
    private TextWatcher mTextWatcher = new TextWatcher() {
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
        Utils.debug("PPF: lifecycle: onCreateView", this, getActivity());

        DEVICE_NAME_UNKNOWN = getContext().getResources().getText(R.string.device_unknown).toString();

        View rootView = inflater.inflate(R.layout.fragment_profile_scan, container, false);
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

        /**
         * Swipe listener,initiate a new scan on refresh. Stop the swipe refresh
         * after 5 seconds
         */
        mSwipeLayout
                .setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        mSearchMenuItem.collapseActionView();
                        // Prepare list view and initiate scanning
                        getBondedDevices();
                        mSwipeLayout.setRefreshing(false);
                    }
                });

        /**
         * Creating the dataLogger file and
         * updating the dataLogger history
         */
        Logger.createDataLoggerFile(getActivity());
        mDeviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (mDeviceListAdapter.getCount() > 0) {
                    final BluetoothDevice device = mDeviceListAdapter.getDevice(position);
                    if (device != null) {
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
                boolean newScrollEnabled = (firstVisibleItem == 0 && topRowVerticalPosition >= 0) ? true : false;
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
        Utils.debug("PPF: lifecycle: onStart", this, getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.debug("PPF: lifecycle: onResume", this, getActivity());

        BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            prepareDeviceList();
        }

        Logger.d("PPF: connect: registering mGattConnectReceiver");
        BluetoothLeService.registerBroadcastReceiver(getActivity(), mGattConnectReceiver, Utils.makeGattUpdateIntentFilter());
    }

    @Override
    public void onPause() {
        Utils.debug("PPF: lifecycle: onPause", this, getActivity());
        dismissProgressDialog();
        Logger.d("PPF: connect: unregistering mGattConnectReceiver");
        BluetoothLeService.unregisterBroadcastReceiver(getActivity(), mGattConnectReceiver);
        super.onPause();
    }

    @Override
    public void onStop() {
        Utils.debug("PPF: lifecycle: onStop", this, getActivity());
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Utils.debug("PPF: lifecycle: onDestroy", this, getActivity());
        clearDeviceList();
        mSwipeLayout.setRefreshing(false);

        // Cancel tasks
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
                    mDeviceListAdapter.addFilteredDevice(device);
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

    private void updateWithNewFragment() {
        clearDeviceList();
        Utils.replaceFragment(getActivity(), new ServiceDiscoveryFragment(), Constants.SERVICE_DISCOVERY_FRAGMENT_TAG);
    }

    /**
     * Method to connect to the device selected. The time allotted for having a
     * connection is 10 seconds. After 10 seconds it will disconnect if not
     * connected and initiate scan once more
     *
     * @param device
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
            Logger.d("PPF: connectDevice: BluetoothLeService.STATE_DISCONNECTED");
            // Disconnected, so connect
            BluetoothLeService.connect(mDeviceAddress, mDeviceName, getActivity());
            showConnectionInProgressInfo(mDeviceName, mDeviceAddress);
        } else {
            Logger.d("PPF: connectDevice: BLE OTHER STATE: " + BluetoothLeService.getConnectionState());
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
        mProgressDialog.setMessage(getResources().getString(
                R.string.alert_message_connect)
                + "\n"
                + deviceName
                + "\n"
                + deviceAddress
                + "\n"
                + getResources().getString(R.string.alert_message_wait));
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
     * Preparing the BLE device list
     */
    public void prepareDeviceList() {
        // Initializes ActionBar as required
        Utils.setUpActionBar((AppCompatActivity) getActivity(), R.string.profile_scan_fragment);
        // Prepare list view and initiate scanning
        mDeviceListAdapter = new DeviceListAdapter();
        mDeviceListView.setAdapter(mDeviceListAdapter);
        getBondedDevices();
    }

    private void getBondedDevices() {
        mDeviceListAdapter.clear();

        if (!permissionManager.isBluetoothPermissionGranted()) {
            return;
        }

        BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();
        if (bluetoothAdapter == null) {
            return;
        }

        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        if (bondedDevices == null) {
            return;
        }
        for (BluetoothDevice device : bondedDevices) {
            mDeviceListAdapter.addDevice(device);
        }
        notifyDeviceListUpdated();
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

    private void pairDevice(BluetoothDevice device) {
        boolean success = BluetoothLeService.pairDevice(device);
        if (false == success) {
            dismissProgressDialog();
        }
    }

    private void unpairDevice(BluetoothDevice device) {
        boolean success = BluetoothLeService.unpairDevice(device);
        if (false == success) {
            dismissProgressDialog();
        }
    }

    /**
     * Holder class for the list view view widgets
     */
    private static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
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

        private LayoutInflater mInflator;
        private ItemFilter mFilter = new ItemFilter();

        public DeviceListAdapter() {
            super();
            mDevices = new ArrayList<>();
            mFilteredDevices = new ArrayList<>();
            mInflator = getActivity().getLayoutInflater();
        }

        private void addDevice(BluetoothDevice device) {
            // New device found
            if (false == mDevices.contains(device)) {
                mDevices.add(device);
            }
        }

        private void addFilteredDevice(BluetoothDevice device) {
            // New device found
            if (false == mFilteredDevices.contains(device)) {
                mFilteredDevices.add(device);
            }
        }

        /**
         * Getter method to get the Bluetooth device
         *
         * @param position
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
                view = mInflator.inflate(R.layout.listitem_device, viewGroup, false);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = view.findViewById(R.id.device_address);
                viewHolder.deviceName = view.findViewById(R.id.device_name);
                viewHolder.pairStatus = view.findViewById(R.id.btn_pair);
                view.findViewById(R.id.rssi_label).setVisibility(View.GONE);
                view.findViewById(R.id.device_rssi).setVisibility(View.GONE);
                view.findViewById(R.id.rssi_unit).setVisibility(View.GONE);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            /**
             * Setting the name and the RSSI of the BluetoothDevice. provided it
             * is a valid one
             */
            final ArrayList<BluetoothDevice> devices = mFilteringActive ? mFilteredDevices : mDevices;
            final BluetoothDevice device = devices.get(position);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0) {
                try {
                    viewHolder.deviceName.setText(deviceName);
                    viewHolder.deviceAddress.setText(device.getAddress());
                    String pairStatus = (device.getBondState() == BluetoothDevice.BOND_BONDED) ? getActivity().getResources().getString(R.string.bluetooth_pair) : getActivity().getResources().getString(R.string.bluetooth_unpair);
                    viewHolder.pairStatus.setText(pairStatus);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                viewHolder.deviceName.setText(DEVICE_NAME_UNKNOWN);
                viewHolder.deviceName.setSelected(true);
                viewHolder.deviceAddress.setText(device.getAddress());
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
                    mDeviceListAdapter.addFilteredDevice(device);
                }
                notifyDataSetChanged(); // notifies the data with new filtered values
            }
        }

    }

    private static BluetoothAdapter getBluetoothAdapter() {
        return HomePageTabbedFragment.mBluetoothAdapter;
    }

}
