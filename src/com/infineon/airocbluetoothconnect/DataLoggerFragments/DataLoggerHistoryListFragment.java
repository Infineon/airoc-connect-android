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

package com.infineon.airocbluetoothconnect.DataLoggerFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.infineon.airocbluetoothconnect.CommonUtils.Constants;
import com.infineon.airocbluetoothconnect.CommonUtils.Logger;
import com.infineon.airocbluetoothconnect.CommonUtils.Utils;
import com.infineon.airocbluetoothconnect.DataModelClasses.DataLoggerModel;
import com.infineon.airocbluetoothconnect.ListAdapters.DataLoggerListAdapter;
import com.infineon.airocbluetoothconnect.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Fragment to show the DataLogger history
 */
public class DataLoggerHistoryListFragment extends Fragment {

    /**
     * ListView for loading the data logger files
     */
    private ListView mListFileNames;
    /**
     * Adapter for ListView
     */
    DataLoggerListAdapter mAdapter;
    /**
     * File names
     */
    private ArrayList<DataLoggerModel> mDataLoggerArrayList;
    DataLoggerModel mDataLoggerModel;
    /**
     * Directory of the file
     */
    private String mDirectory;
    /**
     * File
     */
    private File mFile;
    private static String mLastFragment;

    public DataLoggerHistoryListFragment create(String lastFrag) {
        this.mLastFragment = lastFrag;
        DataLoggerHistoryListFragment fragment = new DataLoggerHistoryListFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.datalogger_list, container,
                false);
        mListFileNames = (ListView) rootView
                .findViewById(R.id.data_logger_history_list);
        mDataLoggerArrayList = new ArrayList<DataLoggerModel>();
        mDataLoggerModel = new DataLoggerModel();

        // Getting the directory AIROC Bluetooth Connect App
        mDirectory = Utils.getApplicationDataDirectory(getContext());
        mFile = new File(mDirectory);

        String filePattern = ".txt";

        // Listing all files in the directory
        final File list[] = mFile.listFiles();
        if (list != null) { // Might be null on Android M and above when not granted Storage permission
            for (int i = 0; i < list.length; i++) {
                if (list[i].getName().toString().contains(filePattern)) {
                    Logger.i(list[i].getAbsolutePath());
                    mDataLoggerModel = new DataLoggerModel(list[i].getName(), list[i].lastModified(), list[i].getAbsolutePath());
                    mDataLoggerArrayList.add(mDataLoggerModel);
                }
            }
        }

        Collections.sort(mDataLoggerArrayList, new Comparator<DataLoggerModel>() {
            @Override
            public int compare(DataLoggerModel dataLoggerModel, DataLoggerModel dataLoggerModel2) {

//                return ((int) (dataLoggerModel2.getFileDate() - dataLoggerModel.getFileDate()));
                return dataLoggerModel2.getFileDate().compareTo(dataLoggerModel.getFileDate());
            }
        });

        // Adding data to adapter
        DataLoggerListAdapter adapter = new DataLoggerListAdapter(
                getActivity(), mDataLoggerArrayList);
        mListFileNames.setAdapter(adapter);
        mListFileNames.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                    long arg3) {
                /**
                 * Getting the absolute path. Adding the DataLogger fragment
                 * with the data of the file user selected
                 */
                String path = mDataLoggerArrayList.get(pos).getFilePath();
                Logger.i("Selected file path" + mDataLoggerArrayList.get(pos).getFilePath());
                Bundle bundle = new Bundle();
                bundle.putString(Constants.DATA_LOGGER_FILE_PATH, path);
                bundle.putBoolean(Constants.DATA_LOGGER_SHOW_HISTORY_FILES_FLAG, true);
                FragmentManager fragmentManager = getFragmentManager();
                DataLoggerFragment dataloggerfragment = new DataLoggerFragment();
                dataloggerfragment.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .add(R.id.container, dataloggerfragment)
                        .addToBackStack(null).commit();
            }
        });
        return rootView;
    }

    @Override
    public void onPause() {
        Utils.setUpActionBar((AppCompatActivity) getActivity(),
                getResources().getString(R.string.data_logger));
        super.onPause();
    }

    @Override
    public void onResume() {
        Utils.setUpActionBar((AppCompatActivity) getActivity(),
                Constants.DATALOGER_HISTORY);
        super.onResume();
    }
}
