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

package com.infineon.airocbluetoothconnect.CommonFragments;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.infineon.airocbluetoothconnect.BLEConnectionServices.BluetoothLeService;
import com.infineon.airocbluetoothconnect.CommonUtils.Constants;
import com.infineon.airocbluetoothconnect.CommonUtils.Logger;
import com.infineon.airocbluetoothconnect.CommonUtils.UUIDDatabase;
import com.infineon.airocbluetoothconnect.CommonUtils.Utils;
import com.infineon.airocbluetoothconnect.AIROCBluetoothConnectApp;
import com.infineon.airocbluetoothconnect.DataModelClasses.PairOnConnect;
import com.infineon.airocbluetoothconnect.HomePageActivity;
import com.infineon.airocbluetoothconnect.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ServiceDiscoveryFragment extends Fragment {
    // UUID key
    private static final String LIST_UUID = "UUID";
    // Stops scanning after 2 seconds.
    private static final long DELAY_MILLIS = 200;
    private static final long SERVICE_DISCOVERY_TIMEOUT = 10000;
    static ArrayList<HashMap<String, BluetoothGattService>> mGattServiceData = new ArrayList<>();
    static ArrayList<HashMap<String, BluetoothGattService>> mGattServiceFindMeData = new ArrayList<>();
    static ArrayList<HashMap<String, BluetoothGattService>> mGattServiceProximityData = new ArrayList<HashMap<String, BluetoothGattService>>();
    static ArrayList<HashMap<String, BluetoothGattService>> mGattServiceSensorHubData = new ArrayList<HashMap<String, BluetoothGattService>>();
    private static ArrayList<HashMap<String, BluetoothGattService>> mGattDbServiceData = new ArrayList<HashMap<String, BluetoothGattService>>();
    private static ArrayList<HashMap<String, BluetoothGattService>> mGattServiceMasterData = new ArrayList<>();
    // Application
    private AIROCBluetoothConnectApp mApplication;
    private ProgressDialog mProgressDialog;
    private TextView mNoServiceDiscovered;
    public static boolean mIsInFragment = false;
    private boolean mFirstTime = false;
    private boolean mServiceDiscoveryStatusReceiverRegistered = false;

    private Handler mServiceDiscoveryTimer = new Handler(Looper.getMainLooper());
    private Runnable mServiceDiscoveryTimerTask = new Runnable() {
        @Override
        public void run() {
            dismissProgressDialog();
            showNoServiceDiscoveredInfo();
        }
    };

    private Handler mServiceDiscoveryHandler = new Handler(Looper.getMainLooper());
    private Runnable mServiceDiscoveryTask = new Runnable() {
        @Override
        public void run() {
            if (BluetoothLeService.getConnectionState() == BluetoothLeService.STATE_CONNECTED) {
                boolean result = BluetoothLeService.discoverServices();
                Logger.d("SDF: discover: started Service Discovery: " + result);
            } else {
                Logger.e("SDF: discover: disconnected");
            }
        }
    };

    private final BroadcastReceiver mServiceDiscoveryStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                Logger.d("SDF: discover: BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED");
                cancelServiceDiscoveryTimer();
                dismissProgressDialog();
                hideNoServiceDiscoveredInfo();
                prepareGattServices(BluetoothLeService.getSupportedGattServices());
                /*
                 * Changes the MTU size to 512 in case LOLLIPOP and above devices
                 */
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    BluetoothLeService.exchangeGattMtu(512);
                }
            } else if (BluetoothLeService.ACTION_GATT_SERVICE_DISCOVERY_UNSUCCESSFUL.equals(action)) {
                Logger.d("SDF: discover: BluetoothLeService.ACTION_GATT_SERVICE_DISCOVERY_UNSUCCESSFUL");
                cancelServiceDiscoveryTimer();
                dismissProgressDialog();

                Toast.makeText(getActivity(), R.string.service_discovery_unsuccessful, Toast.LENGTH_SHORT).show();

                // Disconnect
                BluetoothLeService.disconnect();
                Toast.makeText(getActivity(), R.string.alert_message_bluetooth_disconnect, Toast.LENGTH_SHORT).show();

                // Get the user back to the profile scanning fragment
                Intent homePageIntent = getActivity().getIntent();
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.slide_left, R.anim.push_left);
                startActivity(homePageIntent);
                getActivity().overridePendingTransition(R.anim.slide_right, R.anim.push_right);

//                showNoServiceDiscoveredInfo();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Utils.debug("SDF: lifecycle: onCreateView", this, getActivity());
        View rootView = inflater.inflate(R.layout.servicediscovery_temp_fragment, container, false);
        mNoServiceDiscovered = rootView.findViewById(R.id.no_service_text);
        mApplication = (AIROCBluetoothConnectApp) getActivity().getApplication();
        mProgressDialog = new ProgressDialog(getActivity());

        mFirstTime = true;

        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Utils.debug("SDF: lifecycle: onStart", this, getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.debug("SDF: lifecycle: onResume", this, getActivity());

        mIsInFragment = true;
        Utils.setUpActionBar((AppCompatActivity) getActivity(), R.string.profile_control_fragment);

        if (BluetoothLeService.getConnectionState() == BluetoothLeService.STATE_DISCONNECTED) {
            // Getting the user back to the profile scanning fragment
            Intent intent = getActivity().getIntent();
            getActivity().finish();
            getActivity().overridePendingTransition(R.anim.slide_left, R.anim.push_left);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_right, R.anim.push_right);
        } else {
            Logger.d("SDF: discover: registering mServiceDiscoveryStatusReceiver");
            BluetoothLeService.registerBroadcastReceiver(getActivity(), mServiceDiscoveryStatusReceiver, Utils.makeGattUpdateIntentFilter());
            mServiceDiscoveryStatusReceiverRegistered = true;

            if (mFirstTime) {
                mFirstTime = false;
                if (HomePageActivity.mPairingStarted) {
                    // BluetoothDevice.BOND_BONDING is received before BluetoothLeService.ACTION_GATT_CONNECTED which means pairing is in progress.
                    // Do nothing here. Service Discovery will be kicked off in onBondStateChanged() method.
                } else {
                    startServiceDiscoveryTimer();
                    showServiceDiscoveryInProgressInfo();
                    // Wait for possible pairing request from the peripheral before doing Service Discovery
                    int waitMillis = PairOnConnect.getWaitForPairingRequestFromPeripheralMillis(getActivity());
                    long delay = Math.max(DELAY_MILLIS, waitMillis);
                    mServiceDiscoveryHandler.postDelayed(mServiceDiscoveryTask, delay);
                }
            }

            // Authenticated Pairing Dialog is being shown
            if (HomePageActivity.mPairingStarted && HomePageActivity.mAuthenticatedPairing) {
                // Display "No Services found"
                // This is necessary for the case when user rejects the Pairing Dialog (which asks for user password)
                showNoServiceDiscoveredInfo();
            }
        }
    }

    @Override
    public void onPause() {
        Utils.debug("SDF: lifecycle: onPause", this, getActivity());
        mIsInFragment = false;
        if (mServiceDiscoveryStatusReceiverRegistered) {
            Logger.d("SDF: discover: unregistering mServiceDiscoveryStatusReceiver");
            BluetoothLeService.unregisterBroadcastReceiver(getActivity(), mServiceDiscoveryStatusReceiver);
            mServiceDiscoveryStatusReceiverRegistered = false;
        }
        super.onPause();
    }

    @Override
    public void onStop() {
        Utils.debug("SDF: lifecycle: onStop", this, getActivity());
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Utils.debug("SDF: lifecycle: onDestroy", this, getActivity());

        // Cancel tasks
        mServiceDiscoveryTimer.removeCallbacks(mServiceDiscoveryTimerTask);
        mServiceDiscoveryHandler.removeCallbacks(mServiceDiscoveryTask);

        // Dismiss the dialog if it is shown and we are leaving the fragment
        mProgressDialog.dismiss();
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.global, menu);
        MenuItem graph = menu.findItem(R.id.graph);
        MenuItem search = menu.findItem(R.id.search);
        graph.setVisible(false);
        search.setVisible(false);
    }

    // Callback method
    public void onBondStateChanged(int bondState, int previousBondState) {
        Utils.debug("SDF: pair: onBondStateChanged from " + previousBondState + " to " + bondState);
        if (bondState == BluetoothDevice.BOND_BONDING) {
            // Postpone Service Discovery until the pairing process completes
            cancelServiceDiscoveryTimer();
            dismissProgressDialog();
            mServiceDiscoveryHandler.removeCallbacks(mServiceDiscoveryTask);
        } else if (bondState == BluetoothDevice.BOND_BONDED) {
            // Proceed to Service Discovery
            startServiceDiscoveryTimer();
            showServiceDiscoveryInProgressInfo();
            hideNoServiceDiscoveredInfo();
            mServiceDiscoveryHandler.postDelayed(mServiceDiscoveryTask, DELAY_MILLIS);
        } else if (bondState == BluetoothDevice.BOND_NONE) {
            // Do nothing?
            Logger.e("SDF: pair: BluetoothDevice.BOND_NONE");
        }
    }

    private void showServiceDiscoveryInProgressInfo() {
        mProgressDialog.setTitle(getString(R.string.progress_tile_service_discovering));
        mProgressDialog.setMessage(getString(R.string.progress_message_service_discovering));
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        showProgressDialog();
    }

    private void showNoServiceDiscoveredInfo() {
        mNoServiceDiscovered.setVisibility(View.VISIBLE);
    }

    private void hideNoServiceDiscoveredInfo() {
        mNoServiceDiscovered.setVisibility(View.GONE);
    }

    private void showProgressDialog() {
        mProgressDialog.show();
    }

    private void dismissProgressDialog() {
        mProgressDialog.dismiss();
    }

    private void startServiceDiscoveryTimer() {
        cancelServiceDiscoveryTimer();
        mServiceDiscoveryTimer.postDelayed(mServiceDiscoveryTimerTask, SERVICE_DISCOVERY_TIMEOUT);
    }

    private void cancelServiceDiscoveryTimer() {
        mServiceDiscoveryTimer.removeCallbacks(mServiceDiscoveryTimerTask);
    }

    private void updateWithNewFragment() {
        Utils.replaceFragment(getActivity(), new ProfileControlFragment(), Constants.PROFILE_CONTROL_FRAGMENT_TAG, false);
    }

    /**
     * Getting the GATT Services
     *
     * @param gattServices
     */
    private void prepareGattServices(List<BluetoothGattService> gattServices) {
        // Optimization code for Sensor HUb
        if (isSensorHubPresent(gattServices)) {
            prepareSensorHubData(gattServices);
        } else {
            prepareData(gattServices);
        }
    }

    /**
     * Check whether SensorHub related services are present in the discovered
     * services
     *
     * @param gattServices
     * @return {@link Boolean}
     */
    private boolean isSensorHubPresent(List<BluetoothGattService> gattServices) {
        boolean present = false;
        for (BluetoothGattService gattService : gattServices) {
            UUID uuid = gattService.getUuid();
            if (uuid.equals(UUIDDatabase.UUID_BAROMETER_SERVICE)) {
                present = true;
            }
        }
        return present;
    }

    private void prepareSensorHubData(List<BluetoothGattService> gattServices) {
        boolean mGattSet = false;
        boolean mSensorHubSet = false;

        if (gattServices == null) {
            return;
        }
        // Clear all array list before entering values.
        mGattServiceData.clear();
        mGattServiceMasterData.clear();
        mGattServiceSensorHubData.clear();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, BluetoothGattService> mCurrentServiceData = new HashMap<String, BluetoothGattService>();
            UUID uuid = gattService.getUuid();
            // Optimization code for SensorHub Profile
            if (uuid.equals(UUIDDatabase.UUID_LINK_LOSS_SERVICE)
                    || uuid.equals(UUIDDatabase.UUID_TRANSMISSION_POWER_SERVICE)
                    || uuid.equals(UUIDDatabase.UUID_IMMEDIATE_ALERT_SERVICE)
                    || uuid.equals(UUIDDatabase.UUID_BAROMETER_SERVICE)
                    || uuid.equals(UUIDDatabase.UUID_ACCELEROMETER_SERVICE)
                    || uuid.equals(UUIDDatabase.UUID_ANALOG_TEMPERATURE_SERVICE)
                    || uuid.equals(UUIDDatabase.UUID_BATTERY_SERVICE)
                    || uuid.equals(UUIDDatabase.UUID_DEVICE_INFORMATION_SERVICE)) {
                mCurrentServiceData.put(LIST_UUID, gattService);
                mGattServiceMasterData.add(mCurrentServiceData);
                if (!mGattServiceSensorHubData.contains(mCurrentServiceData)) {
                    mGattServiceSensorHubData.add(mCurrentServiceData);
                }
                if (!mSensorHubSet
                        && uuid.equals(UUIDDatabase.UUID_BAROMETER_SERVICE)) {
                    mSensorHubSet = true;
                    mGattServiceData.add(mCurrentServiceData);
                }

            }
            // Optimization code for GATTDB
            else if (uuid.equals(UUIDDatabase.UUID_GENERIC_ACCESS_SERVICE)
                    || uuid.equals(UUIDDatabase.UUID_GENERIC_ATTRIBUTE_SERVICE)) {
                mCurrentServiceData.put(LIST_UUID, gattService);
                mGattDbServiceData.add(mCurrentServiceData);
                if (!mGattSet) {
                    mGattSet = true;
                    mGattServiceData.add(mCurrentServiceData);
                }
            } else {
                mCurrentServiceData.put(LIST_UUID, gattService);
                mGattServiceMasterData.add(mCurrentServiceData);
                mGattServiceData.add(mCurrentServiceData);
            }
        }
        mApplication.setGattServiceMasterData(mGattServiceMasterData);
        if (mGattDbServiceData.size() > 0) {
            updateWithNewFragment();
        } else {
            Logger.e("SDF: discover: no service found");
            dismissProgressDialog();
            showNoServiceDiscoveredInfo();
        }
    }

    /**
     * Prepare GATTServices data.
     *
     * @param gattServices
     */
    private void prepareData(List<BluetoothGattService> gattServices) {
        boolean mFindMeSet = false;
        boolean mProximitySet = false;
        boolean mGattSet = false;
        if (gattServices == null)
            return;
        // Clear all array list before entering values.
        mGattServiceData.clear();
        mGattServiceFindMeData.clear();
        mGattServiceMasterData.clear();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, BluetoothGattService> currentServiceData = new HashMap<String, BluetoothGattService>();
            UUID uuid = gattService.getUuid();
            // Optimization code for FindMe Profile
            if (uuid.equals(UUIDDatabase.UUID_IMMEDIATE_ALERT_SERVICE)) {
                currentServiceData.put(LIST_UUID, gattService);
                mGattServiceMasterData.add(currentServiceData);
                if (!mGattServiceFindMeData.contains(currentServiceData)) {
                    mGattServiceFindMeData.add(currentServiceData);
                }
                if (!mFindMeSet) {
                    mFindMeSet = true;
                    mGattServiceData.add(currentServiceData);
                }
            }
            // Optimization code for Proximity Profile
            else if (uuid.equals(UUIDDatabase.UUID_LINK_LOSS_SERVICE)
                    || uuid.equals(UUIDDatabase.UUID_TRANSMISSION_POWER_SERVICE)) {
                currentServiceData.put(LIST_UUID, gattService);
                mGattServiceMasterData.add(currentServiceData);
                if (!mGattServiceProximityData.contains(currentServiceData)) {
                    mGattServiceProximityData.add(currentServiceData);
                }
                if (!mProximitySet) {
                    mProximitySet = true;
                    mGattServiceData.add(currentServiceData);
                }
            }// Optimization code for GATTDB
            else if (uuid.equals(UUIDDatabase.UUID_GENERIC_ACCESS_SERVICE)
                    || uuid.equals(UUIDDatabase.UUID_GENERIC_ATTRIBUTE_SERVICE)) {
                currentServiceData.put(LIST_UUID, gattService);
                mGattDbServiceData.add(currentServiceData);
                if (!mGattSet) {
                    mGattSet = true;
                    mGattServiceData.add(currentServiceData);
                }
            } //Optimization code for HID
            else if (uuid.equals(UUIDDatabase.UUID_HID_SERVICE)) {
                /**
                 * Special handling for KITKAT devices
                 */
                if (android.os.Build.VERSION.SDK_INT < 21) {
                    Logger.e("Kitkat RDK device found");
                    List<BluetoothGattCharacteristic> allCharacteristics = gattService.getCharacteristics();
                    List<BluetoothGattCharacteristic> RDKCharacteristics = new ArrayList<BluetoothGattCharacteristic>();
                    List<BluetoothGattDescriptor> RDKDescriptors = new ArrayList<BluetoothGattDescriptor>();

                    //Find all Report characteristics
                    for (BluetoothGattCharacteristic characteristic : allCharacteristics) {
                        if (characteristic.getUuid().equals(UUIDDatabase.UUID_REPORT)) {
                            RDKCharacteristics.add(characteristic);
                        }
                    }

                    //Find all Report descriptors
                    for (BluetoothGattCharacteristic rdkcharacteristic : RDKCharacteristics) {
                        List<BluetoothGattDescriptor> descriptors = rdkcharacteristic.
                                getDescriptors();
                        for (BluetoothGattDescriptor descriptor : descriptors) {
                            RDKDescriptors.add(descriptor);
                        }
                    }
                    /**
                     * Wait for all  descriptors to receive
                     */
                    if (RDKDescriptors.size() == RDKCharacteristics.size() * 2) {

                        for (int pos = 0, descPos = 0; descPos < RDKCharacteristics.size(); pos++, descPos++) {
                            BluetoothGattCharacteristic rdkCharacteristic = RDKCharacteristics.get(descPos);
                            //Mapping the characteristic and descriptors
                            Logger.e("Pos-->" + pos);
                            Logger.e("Pos+1-->" + (pos + 1));
                            BluetoothGattDescriptor clientdescriptor = RDKDescriptors.get(pos);
                            BluetoothGattDescriptor reportdescriptor = RDKDescriptors.get(pos + 1);
                            if (!rdkCharacteristic.getDescriptors().contains(clientdescriptor)) {
                                rdkCharacteristic.addDescriptor(clientdescriptor);
                            }
                            if (!rdkCharacteristic.getDescriptors().contains(reportdescriptor)) {
                                rdkCharacteristic.addDescriptor(reportdescriptor);
                            }
                            pos++;
                        }
                    }
                    currentServiceData.put(LIST_UUID, gattService);
                    mGattServiceMasterData.add(currentServiceData);
                    mGattServiceData.add(currentServiceData);
                } else {
                    currentServiceData.put(LIST_UUID, gattService);
                    mGattServiceMasterData.add(currentServiceData);
                    mGattServiceData.add(currentServiceData);
                }
            } else {
                currentServiceData.put(LIST_UUID, gattService);
                mGattServiceMasterData.add(currentServiceData);
                mGattServiceData.add(currentServiceData);
            }
        }
        mApplication.setGattServiceMasterData(mGattServiceMasterData);
        if (mGattDbServiceData.size() > 0) {
            updateWithNewFragment();
        } else {
            dismissProgressDialog();
            showNoServiceDiscoveredInfo();
        }
    }
}
