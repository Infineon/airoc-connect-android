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

package com.infineon.airocbluetoothconnect.BLEServiceFragments;

import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.infineon.airocbluetoothconnect.R;

/**
 * Fragment to display the CapSense Proximity
 */
public class CapsenseServiceProximity extends Fragment {

    // GATT Service
    private static BluetoothGattService mService;

    // Data variables
    private static ImageView mProximityViewForeground;
    private static ImageView mProximityViewBackground;
    private static MediaPlayer mMediaPlayer;

    private static final int PROXIMITY_WATERMARK_LOW = 0;
    private static final int PROXIMITY_WATERMARK_MAX = 255;
    private static final int PROXIMITY_WATERMARK_INDICATOR = 127;
    private static boolean mValueIncreased = false;

    public static CapsenseServiceProximity create(BluetoothGattService service) {
        CapsenseServiceProximity fragment = new CapsenseServiceProximity();
        mService = service;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.capsense_proximity,
                container, false);
        mProximityViewForeground = (ImageView) rootView
                .findViewById(R.id.proximity_view_1);
        mProximityViewBackground = (ImageView) rootView
                .findViewById(R.id.proximity_view_2);
        mMediaPlayer = MediaPlayer.create(getActivity(), R.raw.beep);
        setHasOptionsMenu(true);
        return rootView;
    }

    /**
     * Display the proximity value
     *
     * @param proximity_value
     */
    public static void displayLiveData(Context context, int proximity_value) {
        int priximity2value = PROXIMITY_WATERMARK_MAX - proximity_value;
        mProximityViewBackground.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, proximity_value));
        mProximityViewForeground.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, priximity2value));

        if (proximity_value >= PROXIMITY_WATERMARK_INDICATOR) {
            try {
                if (mValueIncreased) {
                    AudioManager audio = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
                    //CDT 204234: play no sound when in silent mode
                    if (audio != null && audio.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
                        mMediaPlayer.start();
                    }
                    mValueIncreased = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {//If music is playing already
                    mMediaPlayer.stop();//Stop playing the music
                }
            }
        }
        if (proximity_value >= PROXIMITY_WATERMARK_LOW && proximity_value <= PROXIMITY_WATERMARK_INDICATOR) {
            mValueIncreased = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMediaPlayer = MediaPlayer.create(getActivity(), R.raw.beep);
        mValueIncreased = true;
    }

    @Override
    public void onPause() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
        mValueIncreased = false;
        super.onPause();
    }

}
