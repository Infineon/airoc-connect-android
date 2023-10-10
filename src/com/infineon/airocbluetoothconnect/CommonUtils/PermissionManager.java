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

package com.infineon.airocbluetoothconnect.CommonUtils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;

import com.infineon.airocbluetoothconnect.R;

/**
 * Manage runtime permissions
 */
public class PermissionManager {
    private final Activity activity;

    private static boolean locationPermissionCanBeAsked = true;
    private static boolean bluetoothPermissionCanBeAsked = true;
    private static boolean notificationPermissionCanBeAsked = true;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int BLUETOOTH_PERMISSION_REQUEST_CODE = 3;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 4;

    private final String[] locationPermissions = getLocationPermissions();
    private final String[] bluetoothPermissions = getBtPermissions();
    private final String[] notificationPermissions = getNotificationPermissions();

    /**
     * Constructs PermissionManager object
     *
     * @param activity : parent activity to check and request permissions
     */
    public PermissionManager(Activity activity) {
        this.activity = activity;
    }

    /**
     * Check if Location permissions granted.
     * true if granted or doesn't required to ask
     * false if not granted or activity is null
     *
     * @return result of verification
     */
    public boolean isLocationPermissionGranted() {
        if (activity == null) {
            return false;
        }
        // Before Android 6.0 location permission were granted during installation
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        // Since Android 6.0 either ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION permission
        // is required for BLE scan, so check the permissions
        boolean isLocationPermissionGranted = true;
        for (String permission : locationPermissions) {
            isLocationPermissionGranted &= activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }
        return isLocationPermissionGranted;
    }

    /**
     * Check if Bluetooth permissions granted.
     * true if granted or doesn't required to ask
     * false if not granted or activity is null
     *
     * @return result of verification
     */
    public boolean isBluetoothPermissionGranted() {
        if (activity == null) {
            return false;
        }
        // Before Android 11 there were no Bluetooth permissions
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            return true;
        }
        // Check the permissions
        boolean isBtPermissionGranted = true;
        for (String permission : bluetoothPermissions) {
            isBtPermissionGranted &= activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }
        return isBtPermissionGranted;
    }

    /**
     * Check if Notification permissions granted.
     * true if granted or doesn't required to ask
     * false if not granted or activity is null
     *
     * @return result of verification
     */
    public boolean isNotificationPermissionGranted() {
        if (activity == null) {
            return false;
        }

        // Check the permissions
        boolean isNotificationPermissionGranted = true;
        for (String permission : notificationPermissions) {
            isNotificationPermissionGranted &= activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }
        return isNotificationPermissionGranted;
    }

    /**
     * Checks if Location and Bluetooth permissions are granted. It is impossible to work without them.
     * Requests absent permissions if possible.
     * Note: if permission request scheduled to be shown to user it is still considered as NOT granted.
     *
     * @return :
     * true if both permissions granted
     * false otherwise
     */
    public boolean checkAndRequestPrimaryPermissions() {
        return checkAndRequestLocationPermissions() &&
                checkAndRequestBluetoothPermissions();
    }

    /**
     * Check if notification permission is granted and request if not.
     *
     * @return : true if granted, false otherwise
     */
    public boolean checkAndRequestNotificationPermission() {
        if (activity == null) {
            return false;
        }
        if (isNotificationPermissionGranted()) {
            return true;
        }
        if (notificationPermissionCanBeAsked){
            notificationPermissionCanBeAsked = false;
            activity.requestPermissions(notificationPermissions, NOTIFICATION_PERMISSION_REQUEST_CODE);
        }
        return false;
    }

    /**
     * Should be called after user interaction with permission request dialog
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Logger.d("PSF: permission: REQUEST_PERMISSION_FINE_LOCATION: granted");
                } else {
                    Logger.d("PSF: permission: REQUEST_PERMISSION_FINE_LOCATION: denied");
                }
                break;
            }
            case BLUETOOTH_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Logger.d("PSF: permission: REQUEST_PERMISSION_BLUETOOTH_CONNECT: granted");
                } else {
                    Logger.d("PSF: permission: REQUEST_PERMISSION_BLUETOOTH_CONNECT: denied");
                }
                break;
            }
            case NOTIFICATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Logger.d("PSF: permission: POST_NOTIFICATIONS: granted");
                } else {
                    Logger.d("PSF: permission: POST_NOTIFICATIONS: denied");
                }
                break;
            }
            default:
                Logger.e("PSF: permission: unknown requestCode: " + requestCode);
                break;
        }
    }

    /**
     * Reset permissions request flags, so permissions can be asked one more time.
     */
    public void resetLocationAndBtPermissionsCanBeAsked() {
        locationPermissionCanBeAsked = true;
        bluetoothPermissionCanBeAsked = true;
    }

    private boolean checkAndRequestLocationPermissions() {
        if (activity == null) {
            return false;
        }
        if (isLocationPermissionGranted()) {
            // Permission already granted
            return true;
        }

        if (locationPermissionCanBeAsked) {
            locationPermissionCanBeAsked = false;
            // Show Rationale only once.
            final AlertDialog.Builder justificationDialog = new AlertDialog.Builder(activity)
                    .setTitle(R.string.alert_message_location_permission_required_title)
                    .setMessage(R.string.alert_message_location_permission_required_message)
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.ok, (dialog, id) -> activity.requestPermissions(locationPermissions, LOCATION_PERMISSION_REQUEST_CODE))
                    .setNegativeButton(android.R.string.cancel, (dialog, id) -> dialog.cancel());
            justificationDialog.show();
        }
        // Expect user to accept permission request.
        // Note that request window will not show immediately.
        return false;
    }

    private boolean checkAndRequestBluetoothPermissions() {
        if (activity == null) {
            return false;
        }
        if (isBluetoothPermissionGranted()) {
            // Permission already granted
            return true;
        }
        if (bluetoothPermissionCanBeAsked) {
            bluetoothPermissionCanBeAsked = false;
            // Show rationale only once
            final AlertDialog.Builder justificationDialog = new AlertDialog.Builder(activity)
                    .setTitle(R.string.alert_message_bluetooth_permission_required_title)
                    .setMessage(R.string.alert_message_bluetooth_permission_required_message)
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.ok, (dialog, id) -> activity.requestPermissions(bluetoothPermissions, BLUETOOTH_PERMISSION_REQUEST_CODE))
                    .setNegativeButton(android.R.string.cancel, (dialog, id) -> dialog.cancel());
            justificationDialog.show();
        }
        // Expect user to accept permission request.
        // Note that request window will not show immediately.
        return false;
    }

    private String[] getLocationPermissions() {
        return new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
        };
    }

    private String[] getBtPermissions() {
        String[] permissions = new String[]{};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions = new String[]{
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN,
            };
        }
        return permissions;
    }

    private String[] getNotificationPermissions() {
        // Starting from Android 13 notification permission has became runtime permission
        if (Build.VERSION.SDK_INT >= 33) {
            return new String[]{
                    Manifest.permission.POST_NOTIFICATIONS
            };
        }
        return new String[]{};
    }
}