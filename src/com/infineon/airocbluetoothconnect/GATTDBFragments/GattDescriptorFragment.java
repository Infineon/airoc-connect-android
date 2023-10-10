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

package com.infineon.airocbluetoothconnect.GATTDBFragments;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.infineon.airocbluetoothconnect.CommonUtils.Constants;
import com.infineon.airocbluetoothconnect.CommonUtils.Logger;
import com.infineon.airocbluetoothconnect.AIROCBluetoothConnectApp;
import com.infineon.airocbluetoothconnect.ListAdapters.GattCharacteristicDescriptorsAdapter;
import com.infineon.airocbluetoothconnect.R;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Fragment class for GATT Descriptor
 */
public class GattDescriptorFragment extends Fragment {

    private List<BluetoothGattDescriptor> mBluetoothGattDescriptors;
    private BluetoothGattCharacteristic mBluetoothGattCharacteristic;

    // Application
    private AIROCBluetoothConnectApp mApplication;
    // Text Heading
    private TextView mTextHeading;
    // GATT Service name
    private String mGattServiceName = "";
    // ListView
    private ListView mGattListView;
    // Back button
    private ImageView mBackButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gatt_list,
                container, false);
        mApplication = (AIROCBluetoothConnectApp) getActivity().getApplication();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.gatt_db);
        mGattListView = (ListView) rootView
                .findViewById(R.id.ListView_gatt_services);
        mTextHeading = (TextView) rootView.findViewById(R.id.txtservices);
        mTextHeading.setText(getString(R.string.gatt_descriptors_heading));
        mBackButton = (ImageView) rootView.findViewById(R.id.imgback);
        mBluetoothGattCharacteristic = mApplication.getBluetoothGattCharacteristic();

        // Back button listener
        mBackButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();

            }
        });

        // Getting the selected service from the arguments
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mGattServiceName = bundle
                    .getString(Constants.GATTDB_SELECTED_SERVICE);
        }
        //Preparing list data
        List<BluetoothGattDescriptor> tempList = mBluetoothGattCharacteristic.getDescriptors();
        mBluetoothGattDescriptors = new ArrayList<BluetoothGattDescriptor>();

        for (BluetoothGattDescriptor tempDesc : tempList) {
            int mainListSize = mBluetoothGattDescriptors.size();
            if (mainListSize > 0) {
                //Getting the UUID of list descriptors
                ArrayList<UUID> mainUUID = new ArrayList<UUID>();
                for (int incr = 0; incr < mainListSize; incr++) {
                    mainUUID.add(mBluetoothGattDescriptors.get(incr).getUuid());
                }
                if (!mainUUID.contains(tempDesc.getUuid())) {
                    mBluetoothGattDescriptors.add(tempDesc);
                }
            } else {
                mBluetoothGattDescriptors.add(tempDesc);
            }
        }
        GattCharacteristicDescriptorsAdapter gattCharacteristicDescriptorsAdapter =
                new GattCharacteristicDescriptorsAdapter(getActivity(), mBluetoothGattDescriptors);
        if (gattCharacteristicDescriptorsAdapter != null) {
            mGattListView.setAdapter(gattCharacteristicDescriptorsAdapter);
        }
        mGattListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Logger.i("Descriptor selected " + mBluetoothGattDescriptors.get(position).getUuid());
                mApplication.setBluetoothGattDescriptor(mBluetoothGattDescriptors.get(position));
                FragmentManager fragmentManager = getFragmentManager();
                GattDescriptorDetails gattDescriptorDetails = new GattDescriptorDetails()
                        .create();
                fragmentManager.beginTransaction()
                        .add(R.id.container, gattDescriptorDetails)
                        .addToBackStack(null).commit();
            }
        });
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.global, menu);
        MenuItem graph = menu.findItem(R.id.graph);
        MenuItem search = menu.findItem(R.id.search);
        search.setVisible(false);
        graph.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

}
