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

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.infineon.airocbluetoothconnect.CommonUtils.Constants;
import com.infineon.airocbluetoothconnect.CommonUtils.Logger;
import com.infineon.airocbluetoothconnect.CommonUtils.Utils;
import com.infineon.airocbluetoothconnect.DataModelClasses.NavigationDrawerModel;
import com.infineon.airocbluetoothconnect.ListAdapters.NavDrawerExpandableListAdapter;
import com.infineon.airocbluetoothconnect.ListAdapters.NavDrawerListAdapter;
import com.infineon.airocbluetoothconnect.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Fragment used for managing interactions for and presentation of a navigation
 * drawer.
 */
public class NavigationDrawerFragment extends Fragment {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    public interface ItemPosition {
        int BLE = 0;
        int CYPRESS = 1;
        int ABOUT = 2;
        int SETTINGS = 3;
    }

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    /**
     * NavigationDrawer Layout
     */
    private DrawerLayout mDrawerLayout;
    private ExpandableListView mDrawerListView;
    private View mFragmentContainerView;

    /**
     * Current user selected position in the NavigationDrawer list.
     */
    private int mCurrentSelectedPosition = 0;

    /**
     * ArrayList holding the NavigationDrawerModel data
     */
    private ArrayList<NavigationDrawerModel> mNavDrawerItems;
    private HashMap<NavigationDrawerModel, List<String>> mNavDrawerChildItems;

    /**
     * Adapter for holding the NavigationDrawer List.
     */
    private NavDrawerListAdapter mAdapter;
    private NavDrawerExpandableListAdapter mNavigationItemsAdapter;
    /**
     * NavigationDrawer menu item titles list.
     */
    private String[] mNavMenuTitles;
    /**
     * NavigationDrawer menu icons
     */
    private TypedArray mNavMenuIcons;

    private Boolean mSearchVisible = null;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Getting the savedInstanceState and through that the user selected
        // position
        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState
                    .getInt(STATE_SELECTED_POSITION);
        }

        // Select either the default item (0) or the last selected item.
        //  selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of
        // actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDrawerListView = (ExpandableListView) inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);
        // Navigation drawer title with custom layout
        ViewGroup header = (ViewGroup) inflater.inflate(
                R.layout.fragment_drawer_header, mDrawerListView, false);
        mDrawerListView.addHeaderView(header, null, false);
        // load slide menu items
        mNavMenuTitles = getResources()
                .getStringArray(R.array.nav_drawer_items);
        // Navigation drawer icons from resources
        mNavMenuIcons = getResources().obtainTypedArray(
                R.array.nav_drawer_icons);
        /**
         * Adding NavigationDrawer items to array
         */
        mNavDrawerItems = new ArrayList<>();
        mNavDrawerChildItems = new HashMap<>();

        // BLE Devices
        mNavDrawerItems.add(new NavigationDrawerModel(mNavMenuTitles[ItemPosition.BLE],
                mNavMenuIcons.getResourceId(ItemPosition.BLE, -1)));

        // Cypress
        mNavDrawerItems.add(new NavigationDrawerModel(mNavMenuTitles[ItemPosition.CYPRESS],
                mNavMenuIcons.getResourceId(ItemPosition.CYPRESS, -1)));
        //Cypress subitems
        List<String> subitems = new ArrayList<String>();
        subitems.add(getResources().getString(R.string.navigation_drawer_child_home));
        subitems.add(getResources().getString(R.string.navigation_drawer_child_ble));
        subitems.add(getResources().getString(R.string.navigation_drawer_child_mobile));
        subitems.add(getResources().getString(R.string.navigation_drawer_child_contact));
        mNavDrawerChildItems.put(mNavDrawerItems.get(ItemPosition.CYPRESS), subitems);

        // About
        mNavDrawerItems.add(new NavigationDrawerModel(mNavMenuTitles[ItemPosition.ABOUT],
                mNavMenuIcons.getResourceId(ItemPosition.ABOUT, -1)));

        // Settings
        mNavDrawerItems.add(new NavigationDrawerModel(mNavMenuTitles[ItemPosition.SETTINGS],
                mNavMenuIcons.getResourceId(ItemPosition.SETTINGS, -1)));

        // Setting the NavigationDrawer list adapter
        mAdapter = new NavDrawerListAdapter(getActivity(), mNavDrawerItems);
        mNavigationItemsAdapter = new NavDrawerExpandableListAdapter(getActivity(), mNavDrawerItems,
                mNavDrawerChildItems);
        mDrawerListView.setAdapter(mNavigationItemsAdapter);
        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
        mDrawerListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView,
                                        View view, int groupPosition, long l) {
                if (groupPosition == ItemPosition.CYPRESS) {
                    return false;
                } else {
                    selectItem(groupPosition);
                    return true;
                }
            }
        });
        mDrawerListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView,
                                        View view, int groupPosition, int childPosition, long l) {
                if (groupPosition == ItemPosition.CYPRESS) {
                    TextView childText = (TextView) view.findViewById(R.id.lblListItem);
                    selectChildView(childText.getText().toString());
                    return true;
                } else {
                    return false;
                }
            }
        });
        mDrawerListView.expandGroup(ItemPosition.CYPRESS);

        return mDrawerListView;
    }

    private void selectChildView(String childText) {
        if (childText.equalsIgnoreCase(getResources().
                getString(R.string.navigation_drawer_child_ble))) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.LINK_BLE_PRODUCTS));
            startActivity(intent);
        } else if (childText.equalsIgnoreCase(getResources().
                getString(R.string.navigation_drawer_child_home))) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.LINK_CYPRESS_HOME));
            startActivity(intent);
        } else if (childText.equalsIgnoreCase(getResources().
                getString(R.string.navigation_drawer_child_contact))) {
            if (Utils.checkNetwork(getActivity())) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.LINK_CONTACT_US));
                startActivity(intent);
            } else {
                CharSequence actionBarTitleToRestore = ((AppCompatActivity) getActivity()).getSupportActionBar().getTitle();
                ContactUsFragment contactFragment = ContactUsFragment.create(actionBarTitleToRestore);
                displayView(contactFragment);
                if (mDrawerLayout != null) {
                    mDrawerLayout.closeDrawer(mFragmentContainerView);
                }
            }
        } else if (childText.equalsIgnoreCase(getResources().
                getString(R.string.navigation_drawer_child_mobile))) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.LINK_CYSMART_MOBILE));
            startActivity(intent);
        }
    }

    /**
     * Used for replacing the main content of the view with provided fragments
     *
     * @param fragment
     */
    void displayView(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.container);
        // Prohibit re-entering the same fragment
        if (!(currentFragment instanceof ContactUsFragment)) {
            fragmentManager.beginTransaction().add(R.id.container, fragment)
                    .addToBackStack(null).commit();
        }
    }

    /**
     * Check whether NavigationDrawer is opened or closed
     *
     * @return {@link Boolean}
     */
    public boolean isDrawerOpen() {
        return mDrawerLayout != null
                && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation
     * drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer
        // opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(), /* host Activity */
                mDrawerLayout, /* DrawerLayout object */
                R.string.navigation_drawer_open, /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().invalidateOptionsMenu(); // calls
                // onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().invalidateOptionsMenu(); // calls
                // onPrepareOptionsMenu()
            }
        };

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int position) {
        Logger.e("selectItem--" + position);
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    "Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        mCallbacks = null;
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        if (isDrawerOpen()) {
            mSearchVisible = menu.findItem(R.id.search).isVisible();
            menu.findItem(R.id.search).setVisible(false);
        } else if (mSearchVisible != null) {
            menu.findItem(R.id.search).setVisible(mSearchVisible);
            mSearchVisible = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * CallBacks interface that all activities using this fragment must
     * implement.
     */
    public interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }
}
