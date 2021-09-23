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

package com.cypress.cysmart.GATTDBFragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.cypress.cysmart.BLEConnectionServices.BluetoothLeService;
import com.cypress.cysmart.BLEProfileDataParserClasses.DescriptorParser;
import com.cypress.cysmart.CommonUtils.Constants;
import com.cypress.cysmart.CommonUtils.DialogListener;
import com.cypress.cysmart.CommonUtils.GattAttributes;
import com.cypress.cysmart.CommonUtils.HexKeyBoard;
import com.cypress.cysmart.CommonUtils.Logger;
import com.cypress.cysmart.CommonUtils.UUIDDatabase;
import com.cypress.cysmart.CommonUtils.Utils;
import com.cypress.cysmart.CySmartApplication;
import com.cypress.cysmart.R;

import java.util.UUID;

public class GattDetailsFragment extends Fragment implements DialogListener, OnClickListener {

    // Indicate/Notify/Read Flag
    public static boolean mIsNotifyEnabled;
    public static boolean mIsIndicateEnabled;

    //characteristics
    private BluetoothGattCharacteristic mCharacteristic;

    // GUI variables
    private TextView mServiceName;
    private TextView mCharacteristicName;
    private TextView mHexValue;
    private EditText mAsciiValue;
    private TextView mDateValue;
    private TextView mTimeValue;
    private TextView mBtnRead;
    private TextView mBtnWrite;
    private TextView mBtnNotify;
    private TextView mBtnIndicate;
    private ImageView mBtnBack;
    private ProgressDialog mProgressDialog;

    // Application
    private CySmartApplication mApplication;
    private boolean mIsReadEnabled;

    //Descriptor button
    private Button mBtnDescriptor;

    //Status buttons
    private String mStartNotifyText;
    private String mStopNotifyText;
    private String mStartIndicateText;
    private String mStopIndicateText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gattdb_details, container,
                false);
        mApplication = (CySmartApplication) getActivity().getApplication();
        mServiceName = (TextView) rootView.findViewById(R.id.txtservicename);
        mHexValue = (TextView) rootView.findViewById(R.id.txthex);
        mCharacteristicName = (TextView) rootView
                .findViewById(R.id.txtcharatrname);
        mBtnNotify = (TextView) rootView.findViewById(R.id.txtnotify);
        mBtnIndicate = (TextView) rootView.findViewById(R.id.txtindicate);
        mBtnRead = (TextView) rootView.findViewById(R.id.txtread);
        mBtnWrite = (TextView) rootView.findViewById(R.id.txtwrite);
        mAsciiValue = (EditText) rootView.findViewById(R.id.txtascii);
        mTimeValue = (TextView) rootView.findViewById(R.id.txttime);
        mDateValue = (TextView) rootView.findViewById(R.id.txtdate);
        mBtnBack = (ImageView) rootView.findViewById(R.id.imgback);
        mProgressDialog = new ProgressDialog(getActivity());
        mBtnDescriptor = (Button) rootView.findViewById(R.id.characteristic_descriptors);
        /**
         * Soft back button listner
         */
        mBtnBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();

            }
        });

        /**
         * Checking descriptors available in the current characteristic
         */

        if (mApplication.getBluetoothGattCharacteristic().getDescriptors().size() == 0) {
            mBtnDescriptor.setVisibility(View.GONE);
        }
        /**
         * Descriptor button listner
         */
        mBtnDescriptor.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * Passing the characteristic details to GattDetailsFragment and
                 * adding that fragment to the view
                 */
                Bundle bundle = new Bundle();
                bundle.putString(Constants.GATTDB_SELECTED_SERVICE,
                        mServiceName.getText().toString());
                bundle.putString(Constants.GATTDB_SELECTED_CHARACTERISTIC,
                        mCharacteristicName.getText().toString());
                FragmentManager fragmentManager = getFragmentManager();
                GattDescriptorFragment gattDescriptorFragment = new GattDescriptorFragment();
                gattDescriptorFragment.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .add(R.id.container, gattDescriptorFragment)
                        .addToBackStack(null).commit();
            }
        });

        /**
         * button listeners
         */
        mBtnRead.setOnClickListener(this);
        mBtnNotify.setOnClickListener(this);
        mBtnIndicate.setOnClickListener(this);
        mBtnWrite.setOnClickListener(this);
        mHexValue.setOnClickListener(this);
        mAsciiValue.setOnClickListener(this);
        mAsciiValue.setEnabled(false);
        mHexValue.setEnabled(false);
        /**
         * Ascii done click listner
         */
        mAsciiValue.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String result = mAsciiValue.getText().toString();
                    String hexValue = asciiToHex(result);
                    Logger.e("Hex value-->" + hexValue);
                    byte[] convertedBytes = Utils.hexStringToByteArray(hexValue);
                    // Displaying the hex value in hex field
                    displayHexValue(convertedBytes);
                    writeCharacteristicValue(convertedBytes);
                    hideAsciiKeyboard();
                    return true;
                }
                return false;
            }
        });
        mAsciiValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasfocus) {
                if (hasfocus) {
                    clearAll();
                }
            }
        });
        /**
         * GUI Updates
         */
        mServiceName.setSelected(true);
        mCharacteristicName.setSelected(true);
        mAsciiValue.setSelected(true);
        mHexValue.setSelected(true);

        // Getting the characteristics from the application class
        mCharacteristic = mApplication.getBluetoothGattCharacteristic();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mServiceName.setText(bundle.getString(Constants.GATTDB_SELECTED_SERVICE));
            mCharacteristicName.setText(Utils.getUuidShort(bundle.getString(Constants.GATTDB_SELECTED_CHARACTERISTIC)));
        }

        //Getting the button text from resources
        mStartNotifyText = getResources().getString(R.string.gatt_services_notify);
        mStopNotifyText = getResources().getString(R.string.gatt_services_stop_notify);
        mStartIndicateText = getResources().getString(R.string.gatt_services_indicate);
        mStopIndicateText = getResources().getString(R.string.gatt_services_stop_indicate);

        //Check the available properties in the characteristics and provide corresponding buttons
        uiButtonVisibility();

        /**
         * Check for HID Service
         */
        BluetoothGattService service = mCharacteristic.getService();
        if (service.getUuid().toString().
                equalsIgnoreCase(GattAttributes.HUMAN_INTERFACE_DEVICE_SERVICE)) {
            showHidWarningMessage();
        }

        setHasOptionsMenu(true);
        return rootView;
    }

    /**
     * Method to hide the ascii keyboard
     */
    private void hideAsciiKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        mAsciiValue.clearFocus();
    }

    @Override
    public void onResume() {
        super.onResume();
        BluetoothLeService.registerBroadcastReceiver(getActivity(), mGattUpdateReceiver, Utils.makeGattUpdateIntentFilter());
        mIsNotifyEnabled = false;
        mIsIndicateEnabled = false;
    }


    @Override
    public void onDestroy() {
        BluetoothLeService.unregisterBroadcastReceiver(getActivity(), mGattUpdateReceiver);
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.global, menu);
        MenuItem graph = menu.findItem(R.id.graph);
        MenuItem log = menu.findItem(R.id.log);
        MenuItem search = menu.findItem(R.id.search);
        search.setVisible(false);
        graph.setVisible(false);
        log.setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Method to make the Buttons visible to the user based on the available properties in the
     * characteristic
     */
    private void uiButtonVisibility() {

        // Check read supported on the Characteristic
        if (BluetoothLeService.isPropertySupported(mCharacteristic, BluetoothGattCharacteristic.PROPERTY_READ)) {
            // Read property available
            mBtnRead.setVisibility(View.VISIBLE);
        }
        // Check write supported on the Characteristic
        if (BluetoothLeService.isPropertySupported(mCharacteristic, BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) {
            // Write property available
            mBtnWrite.setVisibility(View.VISIBLE);
            mAsciiValue.setEnabled(true);
            mHexValue.setEnabled(true);
        }
        // Check notify supported on the Characteristic
        if (BluetoothLeService.isPropertySupported(mCharacteristic, BluetoothGattCharacteristic.PROPERTY_NOTIFY)) {
            // Notify property available
            mBtnNotify.setVisibility(View.VISIBLE);
            BluetoothGattDescriptor descriptor = mCharacteristic.getDescriptor(UUIDDatabase.UUID_CLIENT_CHARACTERISTIC_CONFIG);
            if (descriptor != null) {
                BluetoothLeService.readDescriptor(descriptor);
            }
        }
        // Check indicate supported on the Characteristic
        if (BluetoothLeService.isPropertySupported(mCharacteristic, BluetoothGattCharacteristic.PROPERTY_INDICATE)) {
            // Indicate property available
            mBtnIndicate.setVisibility(View.VISIBLE);
            BluetoothGattDescriptor descriptor = mCharacteristic.getDescriptor(UUIDDatabase.UUID_CLIENT_CHARACTERISTIC_CONFIG);
            if (descriptor != null) {
                BluetoothLeService.readDescriptor(descriptor);
            }
        }
    }

    /**
     * Preparing Broadcast receiver to broadcast read characteristics
     *
     * @param gattCharacteristic
     */
    void prepareBroadcastDataRead(BluetoothGattCharacteristic gattCharacteristic) {
        if (BluetoothLeService.isPropertySupported(gattCharacteristic, BluetoothGattCharacteristic.PROPERTY_READ)) {
            BluetoothLeService.readCharacteristic(gattCharacteristic);
        }
    }

    /**
     * Preparing Broadcast receiver to broadcast notify characteristics
     *
     * @param gattCharacteristic
     */
    void prepareBroadcastDataNotify(BluetoothGattCharacteristic gattCharacteristic) {
        if (BluetoothLeService.isPropertySupported(gattCharacteristic, BluetoothGattCharacteristic.PROPERTY_NOTIFY)) {
            BluetoothLeService.setCharacteristicNotification(gattCharacteristic, true);
        }
    }

    /**
     * Stopping Broadcast receiver to broadcast notify characteristics
     *
     * @param gattCharacteristic
     */
    void stopBroadcastDataNotify(BluetoothGattCharacteristic gattCharacteristic) {
        if (BluetoothLeService.isPropertySupported(gattCharacteristic, BluetoothGattCharacteristic.PROPERTY_NOTIFY)) {
            BluetoothLeService.setCharacteristicNotification(gattCharacteristic, false);
        }
    }

    /**
     * Preparing Broadcast receiver to broadcast indicate characteristics
     *
     * @param gattCharacteristic
     */
    void prepareBroadcastDataIndicate(BluetoothGattCharacteristic gattCharacteristic) {
        if (BluetoothLeService.isPropertySupported(gattCharacteristic, BluetoothGattCharacteristic.PROPERTY_INDICATE)) {
            BluetoothLeService.setCharacteristicIndication(gattCharacteristic, true);
        }
    }

    /**
     * Stopping Broadcast receiver to broadcast indicate characteristics
     *
     * @param gattCharacteristic
     */
    void stopBroadcastDataIndicate(BluetoothGattCharacteristic gattCharacteristic) {
        if (BluetoothLeService.isPropertySupported(gattCharacteristic, BluetoothGattCharacteristic.PROPERTY_INDICATE)) {
            BluetoothLeService.setCharacteristicIndication(gattCharacteristic, false);
        }
    }

    /**
     * Method to convert the hexvalue to ascii value and displaying to the user
     *
     * @param hexValue
     */
    void displayAsciiValue(String hexValue) {
        mAsciiValue.setText("");
        StringBuilder output = new StringBuilder("");
        try {
            for (int i = 0; i < hexValue.length(); i += 2) {
                String str = hexValue.substring(i, i + 2);
                output.append((char) Integer.parseInt(str, 16));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mAsciiValue.setText(output.toString());
    }

    /**
     * Method to display the hexValue after converting from byte array
     *
     * @param array
     */
    void displayHexValue(byte[] array) {
        StringBuffer sb = new StringBuffer();
        for (byte byteChar : array) {
            sb.append(Utils.formatForRootLocale("%02x", byteChar));
        }
        mHexValue.setText(sb.toString());
    }

    /**
     * Method to display time and date
     */
    private void displayTimeAndDate() {
        mTimeValue.setText(Utils.GetTimeFromMilliseconds());
        mDateValue.setText(Utils.GetDateFromMilliseconds());
    }

    /**
     * Clearing all fields
     */
    private void clearAll() {
        mTimeValue.setText("");
        mDateValue.setText("");
        mAsciiValue.setText("");
        mHexValue.setText("");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtwrite:
            case R.id.txthex:
                clearAll();
                HexKeyBoard hexKeyBoard = new HexKeyBoard(getActivity(), mCharacteristic, true);
                hexKeyBoard.setDialogListner(this);
                hexKeyBoard.show();
                mAsciiValue.clearFocus();
                break;

            case R.id.txtascii:
                clearAll();
                break;

            case R.id.txtread:
                clearAll();
                prepareBroadcastDataRead(mCharacteristic);
                mIsReadEnabled = true;
                mAsciiValue.clearFocus();
                break;

            case R.id.txtnotify:
                clearAll();
                mAsciiValue.clearFocus();
                String buttonNotifyText = ((TextView) v).getText().toString();
                if (buttonNotifyText.equalsIgnoreCase(mStartNotifyText)) {
                    prepareBroadcastDataNotify(mCharacteristic);
                    mBtnNotify.setText(mStopNotifyText);
                    mIsNotifyEnabled = true;
                } else if (buttonNotifyText.equalsIgnoreCase(mStopNotifyText)) {
                    stopBroadcastDataNotify(mCharacteristic);
                    mBtnNotify.setText(mStartNotifyText);
                    mIsNotifyEnabled = false;
                }
                break;

            case R.id.txtindicate:
                clearAll();
                mAsciiValue.clearFocus();
                String buttonIndicateText = ((TextView) v).getText().toString();
                if (buttonIndicateText.equalsIgnoreCase(mStartIndicateText)) {
                    prepareBroadcastDataIndicate(mCharacteristic);
                    mBtnIndicate.setText(mStopIndicateText);
                    mIsIndicateEnabled = true;
                } else if (buttonIndicateText.equalsIgnoreCase(mStopIndicateText)) {
                    stopBroadcastDataIndicate(mCharacteristic);
                    mBtnIndicate.setText(mStartIndicateText);
                    mIsIndicateEnabled = false;
                }
                break;
        }
    }

    @Override
    public void dialogOkPressed(String result) {
        byte[] convertedBytes = Utils.convertingToByteArray(result);
        // Displaying the hex and ASCII values
        displayHexValue(convertedBytes);
        displayAsciiValue(mHexValue.getText().toString());
        writeCharacteristicValue(convertedBytes);
    }

    @Override
    public void dialogCancelPressed(Boolean aBoolean) {

    }

    /**
     * Method to write the byte value to the characteristic
     *
     * @param value
     */
    private void writeCharacteristicValue(byte[] value) {
        displayTimeAndDate();
        // Writing the hexValue to the characteristic
        try {
            BluetoothLeService.writeCharacteristicGattDb(mCharacteristic, value);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to convert ascii to hex
     *
     * @param asciiValue
     * @return
     */
    private String asciiToHex(String asciiValue) {
        char[] chars = asciiValue.toCharArray();
        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            hex.append(Integer.toHexString((int) chars[i]));
        }
        return hex.toString();
    }

    /**
     * Method to update the Notify/Indicate button status based on the descriptor value received
     *
     * @param array
     */
    private void updateButtonStatus(byte[] array) {
        int status = array[0];
        switch (status) {
            case DescriptorParser
                    .CASE_NOTIFY_DISABLED_IND_DISABLED:
                if (mBtnNotify.getVisibility() == View.VISIBLE)
                    mBtnNotify.setText(mStartNotifyText);
                if (mBtnIndicate.getVisibility() == View.VISIBLE)
                    mBtnIndicate.setText(mStartIndicateText);
                break;
            case DescriptorParser
                    .CASE_NOTIFY_ENABLED_IND_DISABLED:
                if (mBtnNotify.getVisibility() == View.VISIBLE)
                    mBtnNotify.setText(mStopNotifyText);
                if (mBtnIndicate.getVisibility() == View.VISIBLE)
                    mBtnIndicate.setText(mStartIndicateText);
                break;
            case DescriptorParser
                    .CASE_IND_ENABLED_NOTIFY_DISABLED:
                if (mBtnIndicate.getVisibility() == View.VISIBLE)
                    mBtnIndicate.setText(mStopIndicateText);
                if (mBtnNotify.getVisibility() == View.VISIBLE)
                    mBtnNotify.setText(mStartNotifyText);
                break;
            case DescriptorParser
                    .CASE_IND_ENABLED_NOTIFY_ENABLED:
                if (mBtnIndicate.getVisibility() == View.VISIBLE)
                    mBtnIndicate.setText(mStopIndicateText);
                if (mBtnNotify.getVisibility() == View.VISIBLE)
                    mBtnNotify.setText(mStopNotifyText);
                break;
        }
    }

    /**
     * HID characteristic alert message
     */
    void showHidWarningMessage() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        // set title
        alertDialogBuilder
                .setTitle(R.string.app_name);
        // set dialog message
        alertDialogBuilder
                .setMessage(R.string.alert_message_hid_warning)
                .setCancelable(false)
                .setPositiveButton(R.string.alert_message_exit_ok,
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
     * Error code alert message
     *
     * @param errorcode
     */
    private void displayAlertWithMessage(String errorcode) {
        String errorMessage = getResources().getString(R.string.alert_message_write_error) +
                "\n" + getResources().getString(R.string.alert_message_write_error_code) + errorcode +
                "\n" + getResources().getString(R.string.alert_message_try_again);
        AlertDialog alert;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        TextView myMsg = new TextView(getActivity());
        myMsg.setText(errorMessage);
        myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
        builder.setView(myMsg);
        builder.setTitle(getActivity().getResources().getString(R.string.app_name))
                .setCancelable(false)
                .setPositiveButton(
                        getActivity().getResources().getString(
                                R.string.alert_message_exit_ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
        alert = builder.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    private long mLastTimeMillis = 0;

    /**
     * Broadcast receiver class to receives the broadcast from the service class
     */
    private BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Getting the intent action and extras
            final String action = intent.getAction();
            Bundle extras = intent.getExtras();
            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                // Data Received
                if (extras.containsKey(Constants.EXTRA_BYTE_VALUE)) {

                    // For very frequent updates (e.g. Throughput code example) we only want to update GUI 20 times per second
                    long delta = System.currentTimeMillis() - mLastTimeMillis;
                    if (delta >= 0 && delta < 50) {
                        return;
                    }
                    mLastTimeMillis = System.currentTimeMillis();

                    BluetoothGattCharacteristic requiredCharacteristic = mApplication.getBluetoothGattCharacteristic();
                    String requiredUUID = requiredCharacteristic.getUuid().toString();
                    String receivedUUID = "";
                    if (extras.containsKey(Constants.EXTRA_BYTE_UUID_VALUE)) {
                        receivedUUID = intent.getStringExtra(Constants.EXTRA_BYTE_UUID_VALUE);
                    }
                    String requiredServiceUUID = requiredCharacteristic.getService().getUuid().toString();
                    String receivedServiceUUID = "";
                    if (extras.containsKey(Constants.EXTRA_BYTE_SERVICE_UUID_VALUE)) {
                        receivedServiceUUID = intent.getStringExtra(Constants.EXTRA_BYTE_SERVICE_UUID_VALUE);
                    }
                    int requiredInstanceID = requiredCharacteristic.getInstanceId();
                    int receivedInstanceID = -1;
                    if (extras.containsKey(Constants.EXTRA_BYTE_INSTANCE_VALUE)) {
                        receivedInstanceID = intent.getIntExtra(Constants.EXTRA_BYTE_INSTANCE_VALUE, -1);
                    }
                    int requiredServiceInstanceID = requiredCharacteristic.getService().getInstanceId();
                    int receivedServiceInstanceID = -1;
                    if (extras.containsKey(Constants.EXTRA_BYTE_SERVICE_INSTANCE_VALUE)) {
                        receivedServiceInstanceID = intent.getIntExtra(Constants.EXTRA_BYTE_SERVICE_INSTANCE_VALUE, -1);
                    }
                    if (requiredUUID.equalsIgnoreCase(receivedUUID)
                            && requiredServiceUUID.equalsIgnoreCase(receivedServiceUUID)
                            && requiredInstanceID == receivedInstanceID
                            && requiredServiceInstanceID == receivedServiceInstanceID) {

                        byte[] array = intent.getByteArrayExtra(Constants.EXTRA_BYTE_VALUE);
                        displayHexValue(array);
                        displayAsciiValue(mHexValue.getText().toString());
                        displayTimeAndDate();
                    }
                }
                if (extras.containsKey(Constants.EXTRA_DESCRIPTOR_BYTE_VALUE)) {
                    if (extras.containsKey(Constants.EXTRA_DESCRIPTOR_BYTE_VALUE_CHARACTERISTIC_UUID)) {
                        BluetoothGattCharacteristic requiredCharacteristic = mApplication.getBluetoothGattCharacteristic();
                        /**
                         * Checking the received characteristic UUID and expected UUID are same
                         */
                        String requiredUUID = requiredCharacteristic.getUuid().toString();
                        String receivedUUID = intent.getStringExtra(Constants.EXTRA_DESCRIPTOR_BYTE_VALUE_CHARACTERISTIC_UUID);
                        if (requiredUUID.equalsIgnoreCase(receivedUUID)) {
                            if (extras.containsKey(Constants.EXTRA_BYTE_DESCRIPTOR_INSTANCE_VALUE)) {
                                /**
                                 * Checking the received characteristic instance ID and Expected
                                 * characteristic instance ID
                                 */
                                int requiredInstanceID = requiredCharacteristic.getInstanceId();
                                int receivedInstanceID = intent.getIntExtra(Constants.EXTRA_BYTE_DESCRIPTOR_INSTANCE_VALUE, -1);
                                if (requiredInstanceID == receivedInstanceID) {
                                    byte[] array = intent.getByteArrayExtra(Constants.EXTRA_DESCRIPTOR_BYTE_VALUE);
                                    updateButtonStatus(array);
                                }
                            }
                        }
                    }
                }
            } else if (action.equals(BluetoothLeService.ACTION_WRITE_SUCCESS)) {
                // Only do the work here if this fragment is on a top in the fragment stack.
                // This is due to the fact that the GattDescriptorDetails, a child fragment, does the same work.
                if (!GattDescriptorDetails.mIsInFragment) {
                    if (mCharacteristic.getDescriptor(UUID.fromString(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG)) != null) {
                        BluetoothGattDescriptor descriptor = mCharacteristic.getDescriptor(UUID.fromString(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
                        BluetoothLeService.readDescriptor(descriptor);
                    }
                }
            } else if (action.equals(BluetoothLeService.ACTION_GATT_CHARACTERISTIC_ERROR)) {
                if (extras.containsKey(Constants.EXTRA_CHARACTERISTIC_ERROR_MESSAGE)) {
                    String errorMessage = extras.getString(Constants.EXTRA_CHARACTERISTIC_ERROR_MESSAGE);
                    displayAlertWithMessage(errorMessage);
                    mAsciiValue.setText("");
                    mHexValue.setText("");
                }
            } else if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                if (state == BluetoothDevice.BOND_BONDING) {
                    // Bonding...
                    Logger.i("Bonding is in process....");
                    Utils.showBondingProgressDialog(getActivity(), mProgressDialog);
                } else if (state == BluetoothDevice.BOND_BONDED) {
                    // Bonded...
                    String dataLog = getResources().getString(R.string.dl_commaseparator)
                            + "[" + BluetoothLeService.getBluetoothDeviceName() + "|"
                            + BluetoothLeService.getBluetoothDeviceAddress() + "]" +
                            getResources().getString(R.string.dl_commaseparator) +
                            getResources().getString(R.string.dl_connection_paired);
                    Logger.dataLog(dataLog);
                    Utils.hideBondingProgressDialog(mProgressDialog);
                    if (mIsIndicateEnabled) {
                        prepareBroadcastDataIndicate(mCharacteristic);
                    }
                    if (mIsNotifyEnabled) {
                        prepareBroadcastDataNotify(mCharacteristic);
                    }
                    if (mIsReadEnabled) {
                        prepareBroadcastDataRead(mCharacteristic);
                    }
                } else if (state == BluetoothDevice.BOND_NONE) {
                    String dataLog = getResources().getString(R.string.dl_commaseparator)
                            + "[" + BluetoothLeService.getBluetoothDeviceName() + "|"
                            + BluetoothLeService.getBluetoothDeviceAddress() + "]" +
                            getResources().getString(R.string.dl_commaseparator) +
                            getResources().getString(R.string.dl_connection_unpaired);
                    Logger.dataLog(dataLog);
                }
            }
        }
    };
}
