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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.infineon.airocbluetoothconnect.CommonUtils.Utils;
import com.infineon.airocbluetoothconnect.R;

/**
 * Fragment to show the Contact information of CyPress
 */
public class ContactUsFragment extends Fragment {

    /**
     * WebView variable
     */
    private WebView mWebview;
    private ProgressDialog mProgressBar;
    public CharSequence mTitleBarActionToRestore;

    public static ContactUsFragment create(CharSequence titleBarActionToRestore) {
        ContactUsFragment fragment = new ContactUsFragment();
        fragment.mTitleBarActionToRestore = titleBarActionToRestore;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.contact_us_fragment, container, false);
        RelativeLayout layout = (RelativeLayout) rootView.findViewById(R.id.cont_parent);
        /**
         * Checking the network, Displaying the contact webPage for Cypress
         * Semiconductors.
         */
        layout.setVisibility(View.VISIBLE);

        setHasOptionsMenu(true); // for the onCreateOptionsMenu to be invoked
        Utils.setUpActionBar((AppCompatActivity) getActivity(), R.string.title_contact);
        return rootView;
    }

    @Override
    public void onResume() {
        getActivity().setProgressBarIndeterminateVisibility(false);
        super.onResume();
        Utils.setUpActionBar((AppCompatActivity) getActivity(), getResources().getString(R.string.title_contact));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.global, menu);
        menu.findItem(R.id.log).setVisible(false);
    }
}
