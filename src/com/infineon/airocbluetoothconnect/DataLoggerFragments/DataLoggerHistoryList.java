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

package com.infineon.airocbluetoothconnect.DataLoggerFragments;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

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
 * Activity to select the history file
 */
public class DataLoggerHistoryList extends AppCompatActivity {
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

    public static Boolean mApplicationInBackground = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datalogger_list);

        if (Utils.isTablet(this)) {
            Logger.d("tablet");
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        } else {
            Logger.d("Phone");
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        mListFileNames = (ListView) findViewById(R.id.data_logger_history_list);
        mDataLoggerArrayList = new ArrayList<DataLoggerModel>();
        mDataLoggerModel = new DataLoggerModel();

        // Getting the directory CySmart
        mDirectory = Utils.getApplicationDataDirectory(getApplicationContext());
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
                this, mDataLoggerArrayList);
        mListFileNames.setAdapter(adapter);
        mListFileNames.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                    long arg3) {
                /**
                 * Getting the absolute path. Adding the DataLogger fragment
                 * with the data of the file user selected
                 */
                String path = mDataLoggerArrayList.get(pos).getFilePath();
                Logger.i("Selected file path" + mDataLoggerArrayList.get(pos).getFilePath());
                Intent returnIntent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.DATA_LOGGER_FILE_NAME, path);
                bundle.putBoolean(Constants.DATA_LOGGER_FLAG, true);
                returnIntent.putExtras(bundle);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        mApplicationInBackground = false;
        super.onResume();
    }

    @Override
    protected void onPause() {
        mApplicationInBackground = true;
        super.onPause();
    }
}
