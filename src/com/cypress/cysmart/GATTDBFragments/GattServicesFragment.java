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

package com.cypress.cysmart.GATTDBFragments;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.cypress.cysmart.BLEConnectionServices.BluetoothLeService;
import com.cypress.cysmart.CommonUtils.Constants;
import com.cypress.cysmart.CommonUtils.GattAttributes;
import com.cypress.cysmart.CommonUtils.Logger;
import com.cypress.cysmart.CommonUtils.UUIDDatabase;
import com.cypress.cysmart.CySmartApplication;
import com.cypress.cysmart.ListAdapters.GattServiceListAdapter;
import com.cypress.cysmart.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Fragment to showToast the GATT services details in GATT DB
 */
public class GattServicesFragment extends Fragment {

    // BluetoothGattService
    private static BluetoothGattService mService;

    // HashMap to store service
    private static ArrayList<HashMap<String, BluetoothGattService>> mGattServiceData;
    private static ArrayList<HashMap<String, BluetoothGattService>> mModifiedServiceData;

    // GattCharacteristics list
    private static List<BluetoothGattCharacteristic> mGattCharacteristics;

    // Application
    private CySmartApplication mApplication;

    // ListView
    private ListView mGattListView;

    //
    private ImageView mBackButton;
    private static final int HANDLER_DELAY = 500;

    public static GattServicesFragment create() {
        return new GattServicesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gatt_list,
                container, false);
        mApplication = (CySmartApplication) getActivity().getApplication();
        mGattListView = (ListView) rootView
                .findViewById(R.id.ListView_gatt_services);
        mBackButton = (ImageView) rootView.findViewById(R.id.imgback);
        mBackButton.setVisibility(View.GONE);

        // Getting the service data from the application
        mGattServiceData = mApplication.getGattServiceMasterData();

        // Preparing list data
        // GAP and GATT attributes are not displayed
        mModifiedServiceData = new ArrayList<HashMap<String, BluetoothGattService>>();
        for (int i = 0; i < mGattServiceData.size(); i++) {
            if (!(mGattServiceData.get(i).get("UUID").getUuid()
                    .equals(UUIDDatabase.UUID_GENERIC_ATTRIBUTE_SERVICE) || mGattServiceData
                    .get(i).get("UUID").getUuid()
                    .equals(UUIDDatabase.UUID_GENERIC_ACCESS_SERVICE))) {
                mModifiedServiceData.add(mGattServiceData.get(i));
            }
        }
        // Setting adapter
        GattServiceListAdapter adapter = new GattServiceListAdapter(
                getActivity(), mModifiedServiceData);
        mGattListView.setAdapter(adapter);

        // List listener
        mGattListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                    long arg3) {
                mService = mModifiedServiceData.get(pos).get("UUID");
                mGattCharacteristics = mService.getCharacteristics();
                String selectedServiceName = GattAttributes.lookupUUID(
                        mService.getUuid(),
                        getResources().getString(
                                R.string.profile_control_unknown_service));

                mApplication.setGattCharacteristics(mGattCharacteristics);

                // Passing service details to GattCharacteristicsFragment and
                // adding that fragment to the current view
                Bundle bundle = new Bundle();
                bundle.putString(Constants.GATTDB_SELECTED_SERVICE,
                        selectedServiceName);
                FragmentManager fragmentManager = getFragmentManager();
                GattCharacteristicsFragment gattcharacteristicsfragment = new GattCharacteristicsFragment()
                        .create();
                gattcharacteristicsfragment.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .add(R.id.container, gattcharacteristicsfragment)
                        .addToBackStack(null).commit();
            }
        });
        setHasOptionsMenu(true);
        BluetoothLeService.mEnabledCharacteristics=new ArrayList<>();
        BluetoothLeService.mDisableEnabledCharacteristicsFlag = false;
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.gatt_db);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.global, menu);
        MenuItem graph = menu.findItem(R.id.graph);
        MenuItem log = menu.findItem(R.id.log);
        MenuItem search = menu.findItem(R.id.search);
        search.setVisible(false);
        graph.setVisible(false);
        log.setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDestroy() {
        Logger.e("Enabled characteristic size-->" + BluetoothLeService.mEnabledCharacteristics.size());
        if(BluetoothLeService.mEnabledCharacteristics.size() > 0){
            BluetoothLeService.disableAllEnabledCharacteristics();
            Toast.makeText(getActivity(), getResources().
                            getString(R.string.profile_control_stop_both_notify_indicate_toast),
                    Toast.LENGTH_SHORT).show();
        }
        super.onDestroy();
    }

//    private void stopAllEnabledNotifications() {
//        List<BluetoothGattService> bluetoothGattServices = BluetoothLeService.
//                getSupportedGattServices();
//        for (int count = 0; count < bluetoothGattServices.size(); count++) {
//            List<BluetoothGattCharacteristic> bluetoothGattCharacteristics = bluetoothGattServices.
//                    get(count).getCharacteristics();
//            for (int pos = 0; pos < bluetoothGattCharacteristics.size(); pos++) {
//                BluetoothGattCharacteristic bluetoothGattCharacteristic =
//                        bluetoothGattCharacteristics.get(pos);
//                BluetoothLeService.setCharacteristicNotification(bluetoothGattCharacteristic,
//                        false, true);
//                if (android.os.Build.VERSION.SDK_INT < 21) {
//                    // only for below lollipop devices.
//                    // Delay needed to handle each charachersitic.
//                    Logger.e("Kitkat device");
//                    try {
//                        Thread.sleep(100);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//            }
//        }
//    }

//    private class stopAllNotifications extends AsyncTask<String, Boolean, String> {
//        @Override
//        protected void onPreExecute() {
//            Logger.e("onPreExecute");
//            super.onPreExecute();
//        }
//
//        @Override
//        protected String doInBackground(String... strings) {
//            stopAllEnabledNotifications();
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            Logger.e("onPostExecute");
//            super.onPostExecute(s);
//        }
//    }

}
