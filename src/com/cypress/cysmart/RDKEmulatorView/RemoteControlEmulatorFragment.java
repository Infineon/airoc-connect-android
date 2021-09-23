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

package com.cypress.cysmart.RDKEmulatorView;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.cypress.cysmart.BLEConnectionServices.BluetoothLeService;
import com.cypress.cysmart.CommonUtils.Constants;
import com.cypress.cysmart.CommonUtils.GattAttributes;
import com.cypress.cysmart.CommonUtils.Logger;
import com.cypress.cysmart.CommonUtils.UUIDDatabase;
import com.cypress.cysmart.CommonUtils.Utils;
import com.cypress.cysmart.R;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Fragment class to showToast the emulator view of the Remote control RDK which has Human Interface
 * Device sservice
 */
public class RemoteControlEmulatorFragment extends Fragment {


    // GATT service and characteristics
    private static BluetoothGattService mService;
    //   flag
//    private boolean mNotificationsEnabled = false;
    //UI Elements
    private static ProgressDialog mProgressDialog;
    private static Timer mTimer;
    private static final long DIALOG_TIMEOUT = 30000;

    //Remote control emulator buttons
    private ImageButton mVolumePlusbtn;
    private ImageButton mVolumeMinusBtn;
    private ImageButton mChannelPlusBtn;
    private ImageButton mChannelMinusBtn;
    private ImageButton mLeftBtn;
    private ImageButton mRightBtn;
    private ImageButton mBackBtn;
    private ImageButton mExitBtn;
    private ImageButton mPowerBtn;
    private ImageButton mRecBtn;
    //PCM data
    // public static byte[] mPCMData;
    //Constants
    private static final int SAMPLE_RATE = 16000;


    //AudioTrack
    //public static AudioTrack mAudioTrack;
    // private AudioPlayBack mAudioPlayBack;

    //Switch case constants
    private static final int CASE_POWER = 101;
    private static final int CASE_VOLUME_PLUS = 102;
    private static final int CASE_VOLUME_MINUS = 103;
    private static final int CASE_CHANNEL_PLUS = 104;
    private static final int CASE_CHANNEL_MINUS = 105;
    private static final int CASE_MICRPHONE = 106;
    private static final int CASE_LEFT_CLICK_DOWN = 107;
    private static final int CASE_RIGHT_CLICK_DOWN = 108;
    private static final int CASE_RETURN = 109;
    private static final int CASE_SOURCE = 110;
    private static final int CASE_MICROPHONE_UP = 201;
    private static final int CASE_LEFT_RIGHT_CLICK_UP = 202;
    private static final int CASE_RETURN_UP = 203;

    /**
     * BroadcastReceiver for receiving the GATT server status
     */
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Bundle extras = intent.getExtras();
            // GATT Data available
            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                /**
                 * Byte information send through BLE received here
                 */
                if (extras.containsKey(Constants.EXTRA_BYTE_VALUE)) {
                    byte[] array = intent
                            .getByteArrayExtra(Constants.EXTRA_BYTE_VALUE);
                    /**
                     * Converting the received voice data to HEX value
                     * Update the value in the UI
                     */
                    String hexValue = getHexValue(array);
                    updateEmulatorView(hexValue);
                }
            } else if (action.equals(BluetoothLeService.ACTION_WRITE_COMPLETED)) {
                if (mProgressDialog != null && mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                stopDialogTimer();
            }
            /**
             * Bonding Action is in Process
             * Update the GUI with a Progress dialog
             * during bonding
             */
            else if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                if (state == BluetoothDevice.BOND_BONDED) {
                    getAllCharacteristicReportReference();
                }
            }
        }


    };
    //View
    private View mParentView;

    /**
     * Constructor
     *
     * @param bluetoothGattService
     * @return RemoteControlEmulatorService
     */
    public static RemoteControlEmulatorFragment create(BluetoothGattService bluetoothGattService) {
        mService = bluetoothGattService;
        return new RemoteControlEmulatorFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /**
         * Getting the current orientation of the screen
         * Loading different view for LandScape and portrait
         */
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            mParentView = inflater.inflate(R.layout.rdk_emulator_view_landscape, container,
                    false);
        } else {
            mParentView = inflater.inflate(R.layout.rdk_emulator_view_portrait, container,
                    false);
        }

        setUpControls();
        // Now the device is being paired after it is connected
//        initializeBondingIFnotBonded();
//        if (!mNotificationsEnabled) {
        getAllCharacteristicReportReference();
//        }

        return mParentView;
    }

    private void setUpControls() {
        mProgressDialog = new ProgressDialog(getActivity());
        /**
         * Getting the ID's of all Emulator view UI elements
         */
        Button mTrackpadView = (Button) mParentView.findViewById(R.id.trackpad_btn);
        Button mMicrophoneView = (Button) mParentView.findViewById(R.id.microphone_btn);
        mVolumePlusbtn = (ImageButton) mParentView.findViewById(R.id.volume_plus_btn);
        mVolumeMinusBtn = (ImageButton) mParentView.findViewById(R.id.volume_minus_btn);
        mChannelPlusBtn = (ImageButton) mParentView.findViewById(R.id.channel_plus_btn);
        mChannelMinusBtn = (ImageButton) mParentView.findViewById(R.id.channel_minus_btn);
        mLeftBtn = (ImageButton) mParentView.findViewById(R.id.left_btn);
        mRightBtn = (ImageButton) mParentView.findViewById(R.id.right_btn);
        mBackBtn = (ImageButton) mParentView.findViewById(R.id.back_btn);
        mExitBtn = (ImageButton) mParentView.findViewById(R.id.exit_btn);
        mPowerBtn = (ImageButton) mParentView.findViewById(R.id.power_btn);
        mRecBtn = (ImageButton) mParentView.findViewById(R.id.record_btn);
        mRecBtn = (ImageButton) mParentView.findViewById(R.id.record_btn);
        /**
         * AudioTrack class initialisation as follows
         *  streamType- AudioManager.STREAM_MUSIC,
         *  sampleRateInHz- 16000,
         *  channelConfig- AudioFormat.CHANNEL_OUT_MONO,
         *  audioFormat-AudioFormat.ENCODING_PCM_16BIT,
         *  bufferSizeInBytes-8000,
         *  mode- AudioTrack.MODE_STREAM
         *
         */
        int intSize = android.media.AudioTrack.getMinBufferSize(SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        Logger.e("MinSize---" + intSize);

//        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
//                SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO,
//                AudioFormat.ENCODING_PCM_16BIT, intSize,
//                AudioTrack.MODE_STREAM);
        /**
         * TrackPAd button click listner
         */
        mTrackpadView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TrackpadEmulatorFragment trackpadService = new TrackpadEmulatorFragment();
                try {
                    displayView(trackpadService);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        /**
         * Microphone Button click listner
         */
        mMicrophoneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MicrophoneEmulatorFragment microphoneService = new MicrophoneEmulatorFragment();
                microphoneService.create(mService);
                displayView(microphoneService);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.setUpActionBar((AppCompatActivity) getActivity(), R.string.rdk_emulator_view);
        BluetoothLeService.registerBroadcastReceiver(getActivity(), mGattUpdateReceiver, Utils.makeGattUpdateIntentFilter());

    }

    @Override
    public void onPause() {
        if (mProgressDialog.isShowing() && mProgressDialog != null)
            mProgressDialog.dismiss();
        super.onPause();
    }

    @Override
    public void onDestroy() {
//        mNotificationsEnabled = false;
        // stopBroadcastAllNotifications();
        Logger.e("Enabled characteristic size-->" + BluetoothLeService.mEnabledCharacteristics.size());
        if (BluetoothLeService.mEnabledCharacteristics.size() > 0) {
            BluetoothLeService.disableAllEnabledCharacteristics();
        }
        BluetoothLeService.unregisterBroadcastReceiver(getActivity(), mGattUpdateReceiver);
        super.onDestroy();
    }

    private void stopBroadcastAllNotifications() {
        List<BluetoothGattCharacteristic> gattCharacteristics = mService
                .getCharacteristics();
        for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
            String uuidchara = gattCharacteristic.getUuid().toString();
            if (uuidchara.equalsIgnoreCase(GattAttributes.REPORT)) {
                final int charaProp = gattCharacteristic.getProperties();
                if (BluetoothLeService.isPropertySupported(gattCharacteristic, BluetoothGattCharacteristic.PROPERTY_NOTIFY)) {
                    BluetoothLeService.setCharacteristicNotification(gattCharacteristic, false);
                }
            }
        }
    }

    /**
     * Method to get all Characteristic with report reference
     */
    private void getAllCharacteristicReportReference() {
        BluetoothLeService.mRDKCharacteristics = new ArrayList<>();
        BluetoothLeService.mEnabledCharacteristics = new ArrayList<>();
        BluetoothLeService.mDisableEnabledCharacteristicsFlag = false;
        List<BluetoothGattCharacteristic> characteristics = mService.getCharacteristics();
        for (BluetoothGattCharacteristic characteristic : characteristics) {
            if (characteristic.getUuid().equals(UUIDDatabase.UUID_REPORT) &&
                    !BluetoothLeService.mRDKCharacteristics.contains(characteristic)) {
                BluetoothLeService.mRDKCharacteristics.add(characteristic);
            }
        }
        BluetoothLeService.enableAllRDKCharacteristics();
        if (!getActivity().isDestroyed())
            showAlertMessage();
    }

    private void showAlertMessage() {
        mProgressDialog.setTitle(getResources().getString(
                R.string.alert_message_prepare_title));
        mProgressDialog.setMessage(getResources().getString(
                R.string.alert_message_prepare_message)
                + "\n"
                + getResources().getString(R.string.alert_message_wait));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        startDialogTimer();
    }

    private void showNotificationFailedAlertMessage() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());
        // set title
        alertDialogBuilder
                .setTitle(R.string.alert_message_prepare_title);
        // set dialog message
        alertDialogBuilder
                .setMessage(R.string.enable_notify_failed)
                .setCancelable(false)
                .setPositiveButton(R.string.alert_message_retry,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                getAllCharacteristicReportReference();
                            }
                        })
                .setNegativeButton(R.string.gatt_details_popup_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * Connect Timer
     */
    private void startDialogTimer() {
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mProgressDialog.dismiss();
                Logger.v("CONNECTION TIME OUT");
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            Toast.showToast(getActivity(),
//                                    R.string.enable_notify_failed,
//                                    Toast.LENGTH_LONG).showToast();
                            showNotificationFailedAlertMessage();
                            BluetoothLeService.mEnableRDKCharacteristicsFlag = false;
                            BluetoothLeService.mRDKCharacteristics.clear();
                        }
                    });
                }
            }
        }, DIALOG_TIMEOUT);
    }

    public static void stopDialogTimer() {
        if (mTimer != null) {
            Logger.e("Stopped Dialog Timer");
            mTimer.cancel();
        }
    }

    /**
     * Method to update the GUI with the corresponding report received
     *
     * @param buttonValue
     */

    private void updateEmulatorView(String buttonValue) {
        int value = ReportAttributes.lookupReportValues(buttonValue);
        switch (value) {
            case CASE_POWER:
                mPowerBtn.setPressed(true);
                break;
            case CASE_VOLUME_PLUS:
                mVolumePlusbtn.setPressed(true);
                break;
            case CASE_VOLUME_MINUS:
                mVolumeMinusBtn.setPressed(true);
                break;
            case CASE_CHANNEL_PLUS:
                mChannelPlusBtn.setPressed(true);
                break;
            case CASE_CHANNEL_MINUS:
                mChannelMinusBtn.setPressed(true);
                break;
            case CASE_MICRPHONE:
                mRecBtn.setPressed(true);
                //mAudioTrack.play();
                break;
            case CASE_LEFT_CLICK_DOWN:
                mLeftBtn.setPressed(true);
                break;
            case CASE_RIGHT_CLICK_DOWN:
                mRightBtn.setPressed(true);
                break;
            case CASE_RETURN:
                mBackBtn.setPressed(true);
                break;
            case CASE_SOURCE:
                mExitBtn.setPressed(true);
                break;
            case CASE_MICROPHONE_UP:
                mRecBtn.setPressed(false);
                //mAudioTrack.stop();
                break;
            case CASE_LEFT_RIGHT_CLICK_UP:
                mLeftBtn.setPressed(false);
                mRightBtn.setPressed(false);
                break;
            case CASE_RETURN_UP:
                mBackBtn.setPressed(false);
                break;
            default:
                mPowerBtn.setPressed(false);
                mVolumePlusbtn.setPressed(false);
                mVolumeMinusBtn.setPressed(false);
                mChannelPlusBtn.setPressed(false);
                mChannelMinusBtn.setPressed(false);
                mLeftBtn.setPressed(false);
                mRightBtn.setPressed(false);
                mBackBtn.setPressed(false);
                mExitBtn.setPressed(false);
                break;
        }
    }

    private String getHexValue(byte[] array) {
        StringBuffer sb = new StringBuffer();
        for (byte byteChar : array) {
            sb.append(Utils.formatForRootLocale("%02x", byteChar));
        }
        return "" + sb;
    }


    /**
     * Used for replacing the main content of the view with provided fragments
     *
     * @param fragment
     */
    void displayView(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().add(R.id.container, fragment)
                .addToBackStack(null).commit();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            LayoutInflater inflater = (LayoutInflater) getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mParentView = inflater.inflate(R.layout.rdk_emulator_view_landscape, null);
            ViewGroup rootViewG = (ViewGroup) getView();
            // Remove all the existing views from the root view.
            try {
                assert rootViewG != null;
                rootViewG.removeAllViews();
                rootViewG.addView(mParentView);
                setUpControls();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            LayoutInflater inflater = (LayoutInflater) getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mParentView = inflater.inflate(R.layout.rdk_emulator_view_portrait, null);
            ViewGroup rootViewG = (ViewGroup) getView();
            // Remove all the existing views from the root view.
            try {
                assert rootViewG != null;
                rootViewG.removeAllViews();
                rootViewG.addView(mParentView);
                setUpControls();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void createPCMFile(byte[] data) {
        String filename = MicrophoneEmulatorFragment.mfilePCM;
        FileOutputStream output;
        try {
            output = new FileOutputStream(filename, true);
            output.write(data);
            output.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}