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

import android.R.integer;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.infineon.airocbluetoothconnect.AIROCBluetoothConnectApp;
import com.infineon.airocbluetoothconnect.BLEConnectionServices.BluetoothLeService;
import com.infineon.airocbluetoothconnect.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class for commonly used methods in the project
 */
public class Utils {

    // Shared preference constant
    private static final String SHARED_PREF_NAME = "AIROCBluetoothConnectApp Shared Preference";

    private static final String BASE_UUID_FORMAT = "(((0000)|(\\d{4}))(\\d{4}))-0000-1000-8000-00805F9B34FB";
    private static final Pattern BASE_UUID_PATTERN = Pattern.compile(BASE_UUID_FORMAT, Pattern.CASE_INSENSITIVE);
    public static final Locale DATA_LOCALE = Locale.ROOT;
    private static final int BONDING_PROGRESS_DIALOG_TIMEOUT_MILLIS = 20000;
    public static final String DATA_LOGGER_FILENAME_PATTERN = "dd-MMM-yyyy";

    private static ProgressDialog mBondingProgressDialog;
    private static Handler mBondingProgressDialogTimer;
    private static Runnable mBondingProgressDialogTimerTask;

    /**
     * Checks if input UUID string is of base UUID format and if that is true returns the unique 16 or 32 bits of it
     *
     * @param uuid128 complete 128 bit UUID string
     * @return
     */
    public static String getUuidShort(String uuid128) {
        String result = uuid128;
        if (uuid128 != null) {
            Matcher m = BASE_UUID_PATTERN.matcher(uuid128);
            if (m.matches()) {
                boolean isUuid16 = m.group(3) != null;
                if (isUuid16) { // 0000xxxx
                    String uuid16 = m.group(5);
                    result = uuid16;
                } else { // xxxxxxxx
                    String uuid32 = m.group(1);
                    result = uuid32;
                }
            }
        }
        return result;
    }

    /**
     * Returns the manufacture name from the given characteristic
     */
    public static String getManufacturerName(BluetoothGattCharacteristic characteristic) {
        String manufacturerName = characteristic.getStringValue(0);
        return manufacturerName;
    }

    /**
     * Returns the model number from the given characteristic
     */
    public static String getModelNumber(BluetoothGattCharacteristic characteristic) {
        String modelNumber = characteristic.getStringValue(0);
        return modelNumber;
    }

    /**
     * Returns the serial number from the given characteristic
     */
    public static String getSerialNumber(BluetoothGattCharacteristic characteristic) {
        String serialNumber = characteristic.getStringValue(0);
        return serialNumber;
    }

    /**
     * Returns the hardware revision from the given characteristic
     */
    public static String getHardwareRevision(BluetoothGattCharacteristic characteristic) {
        String hardwareRevision = characteristic.getStringValue(0);
        return hardwareRevision;
    }

    /**
     * Returns the Firmware revision from the given characteristic
     */
    public static String getFirmwareRevision(BluetoothGattCharacteristic characteristic) {
        String firmwareRevision = characteristic.getStringValue(0);
        return firmwareRevision;
    }

    /**
     * Returns the software revision from the given characteristic
     */
    public static String getSoftwareRevision(BluetoothGattCharacteristic characteristic) {
        String softwareRevision = characteristic.getStringValue(0);
        return softwareRevision;
    }

    /**
     * Returns the SystemID from the given characteristic
     */
    public static String getSystemId(BluetoothGattCharacteristic characteristic) {
        final byte[] data = characteristic.getValue();
        final StringBuilder sb = new StringBuilder(data.length);
        if (data != null && data.length > 0) {
            for (byte b : data)
                sb.append(formatForRootLocale("%02X ", b));
        }
        return String.valueOf(sb);
    }

    /**
     * Returns the PNP ID from the given characteristic
     */
    public static String getPnPId(BluetoothGattCharacteristic characteristic) {
        final byte[] data = characteristic.getValue();
        final StringBuilder sb = new StringBuilder(data.length);
        if (data != null && data.length > 0) {
            for (byte b : data)
                sb.append(formatForRootLocale("%02X ", b));
        }
        return String.valueOf(sb);
    }

    /**
     * Adding the necessary Intent filters for Broadcast receivers
     *
     * @return {@link IntentFilter}
     */
    public static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        filter.addAction(BluetoothLeService.ACTION_PAIRING_CANCEL);
        filter.addAction(BluetoothLeService.ACTION_OTA_STATUS);//CYACD
        filter.addAction(BluetoothLeService.ACTION_OTA_STATUS_V1);//CYACD2
        filter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        filter.addAction(BluetoothLeService.ACTION_GATT_CONNECTING);
        filter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        filter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTING);
        filter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        filter.addAction(BluetoothLeService.ACTION_GATT_SERVICE_DISCOVERY_UNSUCCESSFUL);
        filter.addAction(BluetoothLeService.ACTION_GATT_CHARACTERISTIC_ERROR);
        filter.addAction(BluetoothLeService.ACTION_GATT_INSUFFICIENT_ENCRYPTION);
        filter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        filter.addAction(BluetoothLeService.ACTION_WRITE_SUCCESS);
        filter.addAction(BluetoothLeService.ACTION_WRITE_FAILED);
        filter.addAction(BluetoothLeService.ACTION_WRITE_COMPLETED);
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        return filter;
    }

    public static String byteArrayToHex(byte[] bytes) {
        if (bytes != null) {
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                // Previously was using the following line but it fires "JavaBinder: !!! FAILED BINDER TRANSACTION !!!" with TPUT 2M code example ...
//                sb.append(formatForRootLocale("%02X ", b));
                // ... hence rewrote the line above with the following two lines
                sb.append(Character.toUpperCase(Character.forDigit((b >> 4) & 0xF, 16)));
                sb.append(Character.toUpperCase(Character.forDigit((b & 0xF), 16)) + " ");
            }
            return sb.toString();
        }
        return "";
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static String getMSB(String string) {
        StringBuilder msbString = new StringBuilder();
        for (int i = string.length(); i > 0; i -= 2) {
            String str = string.substring(i - 2, i);
            msbString.append(str);
        }
        return msbString.toString();
    }

    /**
     * Method to convert hex to byteArray
     */
    public static byte[] convertingToByteArray(String result) {
        String[] splitted = result.split("\\s+");
        byte[] valueByte = new byte[splitted.length];
        for (int i = 0; i < splitted.length; i++) {
            if (splitted[i].length() > 2) {
                String trimmedByte = splitted[i].split("x")[1];
                valueByte[i] = (byte) convertStringToByte(trimmedByte);
            }
        }
        return valueByte;
    }


    /**
     * Convert the string to byte
     *
     * @param string
     * @return
     */
    private static int convertStringToByte(String string) {
        return Integer.parseInt(string, 16);
    }

    /**
     * Returns the battery level information from the characteristics
     *
     * @param characteristics
     * @return {@link String}
     */
    public static String getBatteryLevel(BluetoothGattCharacteristic characteristics) {
        int batteryLevel = characteristics.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
        return String.valueOf(batteryLevel);
    }

    /**
     * Returns the Alert level information from the characteristics
     *
     * @param characteristics
     * @return {@link String}
     */
    public static String getAlertLevel(BluetoothGattCharacteristic characteristics) {
        int alert_level = characteristics.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
        return String.valueOf(alert_level);
    }

    /**
     * Returns the Transmission power information from the characteristic
     *
     * @param characteristics
     * @return {@link integer}
     */
    public static int getTransmissionPower(BluetoothGattCharacteristic characteristics) {
        int txPower = characteristics.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT8, 0);
        return txPower;
    }

    /**
     * Get the data from milliseconds
     *
     * @return {@link String}
     */
    public static String GetDateFromMilliseconds() {
        DateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        Calendar calendar = Calendar.getInstance();
        return formatter.format(calendar.getTime());
    }

    /**
     * Get the date
     *
     * @return {@link String}
     */
    public static String GetDate() {
        SimpleDateFormat formatter = new SimpleDateFormat(DATA_LOGGER_FILENAME_PATTERN);
        Calendar calendar = Calendar.getInstance();
        return formatter.format(calendar.getTime());
    }

    /**
     * Get the time in seconds
     *
     * @return {@link String}
     */
    public static int getTimeInSeconds() {
        int seconds = (int) System.currentTimeMillis();
        return seconds;
    }

    /**
     * Get the time from milliseconds
     *
     * @return {@link String}
     */
    public static String GetTimeFromMilliseconds() {
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
        Calendar calendar = Calendar.getInstance();
        return formatter.format(calendar.getTime());
    }

    /**
     * Get time and date
     *
     * @return {@link String}
     */
    public static String GetTimeAndDate() {
        DateFormat formatter = new SimpleDateFormat("[dd-MMM-yyyy|HH:mm:ss]");
        Calendar calendar = Calendar.getInstance();
        return formatter.format(calendar.getTime());
    }

    /**
     * Get time and date without datalogger format
     *
     * @return {@link String}
     */
    public static String GetTimeandDateUpdate() {
        DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        return formatter.format(calendar.getTime());
    }

    /**
     * Setting the shared preference with values provided as parameters
     *
     * @param context
     * @param key
     * @param value
     */
    public static final void setStringSharedPreference(Context context, String key, String value) {
        SharedPreferences pref = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * Returning the stored values in the shared preference with values provided
     * as parameters
     *
     * @param context
     * @param key
     * @return
     */
    public static final String getStringSharedPreference(Context context, String key) {
        if (context != null) {
            SharedPreferences pref = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return pref.getString(key, "");
        }
        return "";
    }

    /**
     * Setting the shared preference with values provided as parameters
     *
     * @param context
     * @param key
     * @param value
     */
    public static final void setIntSharedPreference(Context context, String key, int value) {
        SharedPreferences pref = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * Returning the stored values in the shared preference with values provided
     * as parameters
     *
     * @param context
     * @param key
     * @return
     */
    public static final int getIntSharedPreference(Context context, String key) {
        if (context != null) {
            SharedPreferences pref = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return pref.getInt(key, 0);
        }
        return 0;
    }

    public static final void setBooleanSharedPreference(Context context, String key, boolean value) {
        SharedPreferences pref = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static final boolean getBooleanSharedPreference(Context context, String key) {
        SharedPreferences Preference = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return Preference.getBoolean(key, false);
    }

    public static final boolean containsSharedPreference(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return pref.contains(key);
    }

    /**
     * Take the screen shot of the device
     *
     * @param view
     * @param file
     */
    public static void takeScreenshotAndSaveToFile(View view, File file) throws IOException {
        if (view != null) {
            view.setDrawingCacheEnabled(true);
            view.buildDrawingCache(true);
            Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] bytes = baos.toByteArray();

                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(bytes);
                }
            }
        }
    }

    /**
     * Method to detect whether the device is phone or tablet
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static void showBondingProgressDialog(Context context, ProgressDialog dialog) {
        showBondingProgressDialog(context, dialog, BONDING_PROGRESS_DIALOG_TIMEOUT_MILLIS);
    }

    public static void showBondingProgressDialog(Context context, ProgressDialog dialog, long timeOutMillis) {
        if (mBondingProgressDialogTimer == null) {
            mBondingProgressDialogTimer = new Handler();
            mBondingProgressDialogTimerTask = new Runnable() {
                @Override
                public void run() {
                    Logger.d("BondingProgressDialog: pair: time out, dismissing dialog " + System.identityHashCode(mBondingProgressDialog));
                    mBondingProgressDialog.dismiss();
                }
            };
        }

        // Dismiss previous dialog
        if (mBondingProgressDialog != null && mBondingProgressDialog != dialog) {
            hideBondingProgressDialog(mBondingProgressDialog);
        }

        mBondingProgressDialog = dialog;

        // Show new dialog
        Logger.d("BondingProgressDialog: pair: showing dialog " + System.identityHashCode(mBondingProgressDialog));
        mBondingProgressDialog.setTitle(context.getResources().getString(R.string.alert_message_bonding_title));
        mBondingProgressDialog.setMessage((context.getResources().getString(R.string.alert_message_bonding_message)));
        mBondingProgressDialog.setCancelable(false);
        mBondingProgressDialog.show();
        mBondingProgressDialogTimer.postDelayed(mBondingProgressDialogTimerTask, timeOutMillis);
    }

    public static void hideBondingProgressDialog(ProgressDialog dialog) {
        // Dismiss previous dialog
        if (mBondingProgressDialogTimer != null) {
            mBondingProgressDialogTimer.removeCallbacks(mBondingProgressDialogTimerTask);
        }

        if (mBondingProgressDialog != null && mBondingProgressDialog != dialog) {
            Logger.d("BondingProgressDialog: pair: dismissing dialog " + System.identityHashCode(mBondingProgressDialog));
            mBondingProgressDialog.dismiss();
        }
        mBondingProgressDialog = null;

        // Dismiss current dialog
        Logger.d("BondingProgressDialog: pair: dismissing dialog " + System.identityHashCode(dialog));
        dialog.dismiss();
    }

    /**
     * Setting up the action bar with values provided as parameters
     *
     * @param context
     * @param title
     * @deprecated use setUpActionBar(Activity, int)
     */
    @Deprecated
    public static void setUpActionBar(AppCompatActivity context, String title) {
        ActionBar actionBar = context.getSupportActionBar();
        actionBar.setIcon(new ColorDrawable(context.getResources().getColor(android.R.color.transparent)));
        actionBar.setTitle(title);
    }

    /**
     * Setting up the action bar with values provided as parameters
     *
     * @param context
     * @param titleId
     */
    public static void setUpActionBar(AppCompatActivity context, int titleId) {
        ActionBar actionBar = context.getSupportActionBar();
        actionBar.setIcon(new ColorDrawable(context.getResources().getColor(android.R.color.transparent)));
        actionBar.setTitle(titleId);
    }

    /**
     * Check whether Internet connection is enabled on the device
     *
     * @param context
     * @return
     */
    public static final boolean checkNetwork(Context context) {
        if (context != null) {
            boolean result = true;
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnectedOrConnecting()) {
                result = false;
            }
            return result;
        } else {
            return false;
        }
    }

    public static void replaceFragment(FragmentActivity activity, Fragment newFragment, String newFragmentTag) {
        replaceFragment(activity, newFragment, newFragmentTag, false);
    }

    /**
     * Used for replacing the main content of the view with provided fragment
     */
    public static void replaceFragment(FragmentActivity activity, Fragment newFragment, String newFragmentTag, boolean addToBackStack) {
        FragmentTransaction txn = activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, newFragment, newFragmentTag);
        if (addToBackStack) {
            txn.addToBackStack(null);
        }
        txn.commit();
    }

    public static void moveToFragment(FragmentActivity activity, Fragment newFragment, String newFragmentTag, boolean addToBackStack){
        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, newFragment, newFragmentTag);

        if (addToBackStack) {
            fragmentTransaction.addToBackStack(newFragmentTag);
        }

        fragmentTransaction.commit();
    }

    // See CDT 251485
    // This method returns number format in default en_US locale.
    // This method fixes NumberFormatException thrown when the following condition holds true
    // 1. System's locale is different from en_US whose decimal point representation
    // is different from en_US's '.' (e.g. ',' in ua_UK for Ukraine).
    // 2. There are places in code where floating number is first converted to string via NumberFormat.format(float)
    // and then parsed back to floating number via Float.valueOf(string) which in turn is locale independent
    // and only respects '.' as a decimal point (and throws NFE for ',' as a decimal point).
    // So previously it was possible to get a NFE in the following case
    // 1. Set default locale to ua_UK.
    // 2. Get locale-specific NumberFormat instance via NumberFormat.getInstance().
    // 3. Get locale-specific floating number string by formatting it via NumberFormat.format(float).
    // 4. Parse string back to number via Float.valueOf(string)... here NFE is thrown
    public static NumberFormat getNumberFormatForRootLocale() {
        return NumberFormat.getNumberInstance(DATA_LOCALE);
    }

    public static NumberFormat getNumberFormatForDefaultLocale() {
        return NumberFormat.getNumberInstance();
    }

    public static String formatForRootLocale(String format, Object... args) {
        return String.format(DATA_LOCALE, format, args);
    }

    public static String formatForDefaultLocale(String format, Object... args) {
        return String.format(format, args);
    }

    /**
     * Read version name from the manifest
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        String versionName = "";
        try {
            String packageName = context.getPackageName();
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static void debug(String msg, Object... objects) {
        StringBuilder sb = new StringBuilder(msg);
        for (Object o : objects) {
            sb.append(" " + o.getClass().getSimpleName() + "(" + System.identityHashCode(o) + ")");
        }
        Logger.d(sb.toString());
    }

    public static String getApplicationDataDirectory(Context context){
        if (context == null) return "";
        // Use Media dirs as they are visible by other apps
        File[] externalMediaDirs = context.getExternalMediaDirs();
        if (externalMediaDirs.length < 1) return "";
        String applicationDataDirectory = externalMediaDirs[0].getAbsolutePath();
        return applicationDataDirectory;
    }

    public static boolean isHardwareKeyboardAvailable(Context context) {
        return context.getResources().getConfiguration().keyboard != Configuration.KEYBOARD_NOKEYS;
    }

    @NonNull
    public static String getDefaultLogFilePath(Context context) {
        String path = getApplicationDataDirectory(context) + File.separator + GetDate() + ".txt";
        return path;
    }

    /*
     * Checks whether a file exists in the folder specified
     */
    public static boolean fileExists(String name, File file) {
        File[] list = file.listFiles();
        if (list != null) { // Might be null on Android M and above when not granted Storage permission
            for (File fil : list) {
                if (fil.isDirectory()) {
                    fileExists(name, fil);
                } else if (name.equalsIgnoreCase(fil.getName())) {
                    Logger.e("File>>" + fil.getName());
                    return true;
                }
            }
        }
        return false;
    }

    /*
     * If сharacteristics null, return empty list
     */
    public static List<BluetoothGattCharacteristic> getServiceCharacteristics(BluetoothGattService service) {
        List<BluetoothGattCharacteristic> сharacteristics = service.getCharacteristics();
        if(сharacteristics == null)
        {
            сharacteristics = Collections.emptyList();
        }
        return сharacteristics;
    }

    /*
     * If сharacteristics null, return empty list
     */
    public static List<BluetoothGattCharacteristic> getApplicationCharacteristics(AIROCBluetoothConnectApp application) {
        List<BluetoothGattCharacteristic> сharacteristics = application.getGattCharacteristics();
        if(сharacteristics == null)
        {
            сharacteristics = Collections.emptyList();
        }
        return сharacteristics;
    }
}
