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

package com.infineon.airocbluetoothconnect.wearable.demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.infineon.airocbluetoothconnect.CommonUtils.Utils;
import com.infineon.airocbluetoothconnect.R;
import com.infineon.airocbluetoothconnect.wearable.model.Category;
import com.infineon.airocbluetoothconnect.wearable.model.Variable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DemoFragment extends CategoryListFragment {

    public static final String TAG = "Wearable Solution Demo";

    public static DemoFragment create() {
        Map<Category.Id, List<Variable.Id>> cats = new HashMap<>();
        cats.put(Category.Id.ACTIVITY, Arrays.asList(
                Variable.Id.ACT_STEPS, Variable.Id.ACT_DURATION, Variable.Id.ACT_CALORIES,
                Variable.Id.ACT_DISTANCE, Variable.Id.ACT_SPEED, Variable.Id.ACT_FLOORS,
                Variable.Id.ACT_SLEEP));
        cats.put(Category.Id.ENVIRONMENT, Arrays.asList(
                Variable.Id.ENV_TEMPERATURE, Variable.Id.ENV_UV, Variable.Id.ENV_AIR_QUALITY,
                Variable.Id.ENV_PRESSURE, Variable.Id.ENV_ALTITUDE
        ));
        cats.put(Category.Id.LOCATION, Arrays.asList(
                Variable.Id.LOC_POSITION, Variable.Id.LOC_ALTITUDE, Variable.Id.LOC_SPEED));
        DemoFragment f = new DemoFragment();
        f.mCategoriesToBuild = cats;
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        Utils.setUpActionBar((AppCompatActivity) getActivity(), R.string.wearable_demo_action_bar_title);
        return view;
    }
}
