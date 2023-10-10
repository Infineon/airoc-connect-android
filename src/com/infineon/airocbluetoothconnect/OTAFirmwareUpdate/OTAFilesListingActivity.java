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

package com.infineon.airocbluetoothconnect.OTAFirmwareUpdate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.view.MotionEvent;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import com.infineon.airocbluetoothconnect.CommonUtils.Constants;
import com.infineon.airocbluetoothconnect.CommonUtils.Logger;
import com.infineon.airocbluetoothconnect.CommonUtils.ToastUtils;
import com.infineon.airocbluetoothconnect.CommonUtils.Utils;
import com.infineon.airocbluetoothconnect.DataModelClasses.OTAFileModel;
import com.infineon.airocbluetoothconnect.ListAdapters.OTAFileListAdapter;
import com.infineon.airocbluetoothconnect.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Fragment that display the firmware files.User can select the firmware file for upgrade
 */
public class OTAFilesListingActivity extends AppCompatActivity {

    public static final String FILE_EXT_CYACD = ".cyacd";
    public static final String REGEX_CYACD = "(?i)cyacd2?";
    public static final String ENABLED_VIEW_BG_COLOR = "#FFFFFF";
    public static final String DISABLED_VIEW_BG_COLOR = "#E0E0E0";
    public static final String ENABLED_SECTION_TEXT_COLOR = "#000000";
    public static final String DISABLED_SECTION_TEXT_COLOR = "#A6A6A6";
    public static final float ENABLED_VIEW_ALPHA = 1f;
    public static final float DISABLED_VIEW_ALPHA = 0.8f;

    private static int mUpgradeMode;
    private final ArrayList<OTAFileModel> mArrayListFiles = new ArrayList<>();
    private final ArrayList<String> mArrayListPaths = new ArrayList<>();
    private final ArrayList<String> mArrayListFileNames = new ArrayList<>();

    private OTAFileListAdapter mFirmwareAdapter;
    private ListView mFileListView;
    private Button mUpgradeButton;
    private Button mNextButton;
    private TextView mHeadingTextView;
    private View mSecurityKeySection;
    private View mActiveAppSection;
    private CheckBox mSecurityKeyRequiredCheckBox;
    private TextView mSecurityKeyPrefixTextView;
    private EditText mSecurityKeyEditText;
    private AppCompatSpinner mActiveAppSpinner;
    private TextView mActiveAppTextView;

    //Dual-App Bootloader Active Application ID
    private byte mActiveApp;

    public static Boolean mIsStarted = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ota_firmware_files_list);

        if (Utils.isTablet(this)) {
            Logger.d("tablet");
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        } else {
            Logger.d("Phone");
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mUpgradeMode = extras.getInt(Constants.REQ_FILE_COUNT);
        }

        mFileListView = findViewById(R.id.listView);
        mUpgradeButton = findViewById(R.id.upgrade_button);
        mNextButton = findViewById(R.id.next_button);
        mHeadingTextView = findViewById(R.id.heading_2);

        /*
          Shows the cyacd files in the device
         */
        mFirmwareAdapter = new OTAFileListAdapter(this, mArrayListFiles, mUpgradeMode);
        mFileListView.setAdapter(mFirmwareAdapter);
        File fileDir = new File(Utils.getApplicationDataDirectory(getApplicationContext()));
        searchRequiredFile(fileDir);

        if (mUpgradeMode == OTAFirmwareUpgradeFragment.APP_AND_STACK_SEPARATE) {
            mHeadingTextView.setText(getResources().getString((R.string.ota_stack_file)));
            mUpgradeButton.setVisibility(View.GONE);
            mNextButton.setVisibility(View.VISIBLE);
        } else {
            mUpgradeButton.setVisibility(View.VISIBLE);
            mNextButton.setVisibility(View.GONE);
        }

        mSecurityKeySection = findViewById(R.id.security_key_section);
        mActiveAppSection = findViewById(R.id.active_app_section);
        mSecurityKeyRequiredCheckBox = findViewById(R.id.security_key_required);
        mSecurityKeyPrefixTextView = findViewById(R.id.security_key_hex_prefix);
        mSecurityKeyEditText = findViewById(R.id.security_key);
        mActiveAppSpinner = findViewById(R.id.active_app);
        mActiveAppTextView = findViewById(R.id.active_app_text);

        setSecurityKeySectionEnabled(false);

        mActiveApp = Constants.ACTIVE_APP_NO_CHANGE;
        setActiveAppSectionEnabled(false);

        if (mUpgradeMode == OTAFirmwareUpgradeFragment.APP_AND_STACK_COMBINED) {
            mActiveAppSection.setVisibility(View.GONE);
        }

        // Security Key
        mSecurityKeyRequiredCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mSecurityKeySection.setAlpha(ENABLED_VIEW_ALPHA);
                    mSecurityKeySection.setBackgroundColor(Color.parseColor(ENABLED_VIEW_BG_COLOR));
                } else {
                    mSecurityKeySection.setAlpha(DISABLED_VIEW_ALPHA);
                    mSecurityKeySection.setBackgroundColor(Color.parseColor(DISABLED_VIEW_BG_COLOR));
                }
                setSecurityKeyEditTextEnabled(isChecked);
                setSecurityKeyPrefixEnabled(isChecked);
            }
        });

        mSecurityKeyEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    boolean hardwareKeyboardAvailable = Utils.isHardwareKeyboardAvailable(OTAFilesListingActivity.this);
                    Logger.d("Hardware keyboard available: " + hardwareKeyboardAvailable);
                    if (!hardwareKeyboardAvailable) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                        }
                    }
                }
            }
        });

        // Active App
        int[] values = getResources().getIntArray(R.array.dropdownValue_activeApp);
        String[] displayValues = getResources().getStringArray(R.array.dropdownDisplayValue_activeApp);
        DisplayableValue[] displayableValues = new DisplayableValue[values.length];
        for (int i = 0; i < values.length; i++) {
            displayableValues[i] = new DisplayableValue(values[i], displayValues[i]);
        }
        ArrayAdapter<DisplayableValue> adapter = new ArrayAdapter<>(this, R.layout.simple_spinner_item, displayableValues);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        mActiveAppSpinner.setAdapter(adapter);
        mActiveAppSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DisplayableValue item = (DisplayableValue) parent.getItemAtPosition(position);
                mActiveApp = (byte) item.getValue();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // NOOP
            }
        });

        /*
          File Selection click event
         */
        mFileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OTAFileModel model = mArrayListFiles.get(position);
                model.setSelected(!model.isSelected());
                for (int i = 0; i < mArrayListFiles.size(); i++) {
                    if (position != i) {
                        mArrayListFiles.get(i).setSelected(false);
                    }
                }

                boolean cyacdFileSelected = model.isSelected() && model.getFileName().toLowerCase().endsWith(FILE_EXT_CYACD);

                boolean securityKeySectionEnabled = mUpgradeButton.getVisibility() == View.VISIBLE && cyacdFileSelected;
                setSecurityKeySectionEnabled(securityKeySectionEnabled);

                boolean activeAppSectionEnabled = mUpgradeMode != OTAFirmwareUpgradeFragment.APP_AND_STACK_COMBINED // App Only + App/Stack separate
                        && mUpgradeButton.getVisibility() == View.VISIBLE && cyacdFileSelected;
                setActiveAppSectionEnabled(activeAppSectionEnabled);

                mFirmwareAdapter.notifyDataSetChanged();
            }
        });

        /*
          returns to the type selection fragment by selecting the required files
         */
        mUpgradeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mUpgradeMode == OTAFirmwareUpgradeFragment.APP_AND_STACK_SEPARATE) {
                    if (mArrayListPaths.size() > 1) {
                        String path = mArrayListPaths.get(0);
                        mArrayListPaths.clear();
                        mArrayListPaths.add(path);
                        String fileName = mArrayListFileNames.get(0);
                        mArrayListFileNames.clear();
                        mArrayListFileNames.add(fileName);
                    }
                    for (int i = 0; i < mArrayListFiles.size(); i++) {
                        if (mArrayListFiles.get(i).isSelected()) {
                            mArrayListPaths.add(1, mArrayListFiles.get(i).getFilePath());
                            mArrayListFileNames.add(1, mArrayListFiles.get(i).getFileName());
                        }
                    }
                } else {
                    if (mArrayListPaths.size() > 0) {
                        mArrayListPaths.clear();
                        mArrayListFileNames.clear();
                    }
                    for (int i = 0; i < mArrayListFiles.size(); i++) {
                        if (mArrayListFiles.get(i).isSelected()) {
                            mArrayListPaths.add(0, mArrayListFiles.get(i).getFilePath());
                            mArrayListFileNames.add(0, mArrayListFiles.get(i).getFileName());
                        }
                    }
                }

                if (mUpgradeMode == OTAFirmwareUpgradeFragment.APP_AND_STACK_SEPARATE) { // App/Stack separate
                    if (mArrayListPaths.size() == 2) {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra(Constants.SELECTION_FLAG, true);
                        returnIntent.putExtra(Constants.ARRAYLIST_SELECTED_FILE_PATHS, mArrayListPaths);
                        returnIntent.putExtra(Constants.ARRAYLIST_SELECTED_FILE_NAMES, mArrayListFileNames);

                        if (!submitSecurityKey(returnIntent)) {
                            return;
                        }
                        addActiveAppToIntent(returnIntent);

                        setResult(RESULT_OK, returnIntent);
                        finish();
                    } else {
                        alertFileSelection(getResources().getString(R.string.ota_alert_file_applicationstacksep_app_sel));
                    }
                } else if (mUpgradeMode != OTAFirmwareUpgradeFragment.APP_AND_STACK_SEPARATE // App Only + App/Stack combined
                        && mArrayListPaths.size() == 1) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(Constants.SELECTION_FLAG, true);
                    returnIntent.putExtra(Constants.ARRAYLIST_SELECTED_FILE_PATHS, mArrayListPaths);
                    returnIntent.putExtra(Constants.ARRAYLIST_SELECTED_FILE_NAMES, mArrayListFileNames);

                    if (!submitSecurityKey(returnIntent)) {
                        return;
                    }
                    if (mUpgradeMode == OTAFirmwareUpgradeFragment.APP_ONLY) {
                        addActiveAppToIntent(returnIntent); // App/Stack combined has no Active App option
                    }

                    setResult(RESULT_OK, returnIntent);
                    finish();
                } else {
                    if (mUpgradeMode != OTAFirmwareUpgradeFragment.APP_AND_STACK_COMBINED) {
                        alertFileSelection(getResources().getString(R.string.ota_alert_file_application));
                    } else {
                        alertFileSelection(getResources().getString(R.string.ota_alert_file_applicationstackcomb));
                    }
                }
            }
        });

        /*
          returns to the type selection fragment by selecting the required files
         */
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < mArrayListFiles.size(); i++) {
                    if (mArrayListFiles.get(i).isSelected()) {
                        mArrayListPaths.add(0, mArrayListFiles.get(i).getFilePath());
                        mArrayListFileNames.add(0, mArrayListFiles.get(i).getFileName());
                        mHeadingTextView.setText(getResources().getString((R.string.ota_app_file)));
                        mArrayListFiles.remove(i);
                        mFirmwareAdapter.addFiles(mArrayListFiles);
                        mFirmwareAdapter.notifyDataSetChanged();
                        mUpgradeButton.setVisibility(View.VISIBLE);
                        mNextButton.setVisibility(View.GONE);
                    }
                }

                if (mArrayListPaths.size() == 0) {
                    alertFileSelection(getResources().getString(R.string.ota_alert_file_applicationstacksep_stack_sel));
                } else if (mUpgradeButton.getVisibility() == View.VISIBLE) {
                    mSecurityKeySection.setVisibility(View.VISIBLE);
                    mActiveAppSection.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /*
    * checks if screen was touched outside the edit text to lose focus and hide keyboard.
    */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (view == mSecurityKeyEditText) {
                Rect outRect = new Rect();
                view.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    view.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private void setSecurityKeySectionEnabled(boolean enabled) {
        mSecurityKeyRequiredCheckBox.setEnabled(enabled);
        if (enabled) {
            mSecurityKeyRequiredCheckBox.setBackgroundColor(Color.parseColor(ENABLED_VIEW_BG_COLOR));
        } else {
            mSecurityKeySection.setAlpha(DISABLED_VIEW_ALPHA);
            mSecurityKeySection.setBackgroundColor(Color.parseColor(DISABLED_VIEW_BG_COLOR));
            mSecurityKeyRequiredCheckBox.setBackgroundColor(Color.parseColor(DISABLED_VIEW_BG_COLOR));
            // Uncheck radio button
            mSecurityKeyRequiredCheckBox.setChecked(false);
        }
        setSecurityKeyPrefixEnabled(enabled && mSecurityKeyRequiredCheckBox.isChecked());
        setSecurityKeyEditTextEnabled(enabled && mSecurityKeyRequiredCheckBox.isChecked());
    }

    private void setSecurityKeyPrefixEnabled(boolean enabled) {
        if (enabled) {
            mSecurityKeyPrefixTextView.setTextColor(Color.parseColor(ENABLED_SECTION_TEXT_COLOR));
        } else {
            mSecurityKeyPrefixTextView.setTextColor(Color.parseColor(DISABLED_SECTION_TEXT_COLOR));
        }
    }

    private void setSecurityKeyEditTextEnabled(boolean enabled) {
        mSecurityKeyEditText.setEnabled(enabled);
        mSecurityKeyEditText.setFocusableInTouchMode(enabled);
        mSecurityKeyEditText.setFocusable(enabled);
        if (!enabled) {
            // Clear validation error
            mSecurityKeyEditText.setError(null);
            // Key value should be gray if the Security key is disabled
            mSecurityKeyEditText.setTextColor(Color.parseColor(DISABLED_SECTION_TEXT_COLOR));
        } else {
            mSecurityKeyEditText.setTextColor(Color.parseColor(ENABLED_SECTION_TEXT_COLOR));
        }
    }

    private void setActiveAppSectionEnabled(boolean enabled) {
        mActiveAppSpinner.setEnabled(enabled);
        if (enabled) {
            mActiveAppSection.setAlpha(ENABLED_VIEW_ALPHA);
            mActiveAppSection.setBackgroundColor(Color.parseColor(ENABLED_VIEW_BG_COLOR));
        } else {
            mActiveAppSection.setAlpha(DISABLED_VIEW_ALPHA);
            mActiveAppSection.setBackgroundColor(Color.parseColor(DISABLED_VIEW_BG_COLOR));
        }
        mActiveAppTextView.setTextColor(Color.parseColor(enabled ? ENABLED_SECTION_TEXT_COLOR : DISABLED_SECTION_TEXT_COLOR));
    }

    private boolean submitSecurityKey(Intent returnIntent) {
        boolean success = true;
        if (mSecurityKeyRequiredCheckBox.isChecked()) {
            String securityKeyString = mSecurityKeyEditText.getText().toString();
            success = securityKeyString.length() == (Constants.SECURITY_KEY_SIZE * 2);
            if (success) {
                try {
                    long securityKey = Long.parseLong(securityKeyString, 16);
                    returnIntent.putExtra(Constants.EXTRA_SECURITY_KEY, securityKey);
                } catch (NumberFormatException e) {
                    success = false;
                }
            }
            if (!success) {
                mSecurityKeyEditText.setError(getResources().getString(R.string.ota_security_key_invalid));
            }
        }
        return success;
    }

    private void addActiveAppToIntent(Intent returnIntent) {
        if (mActiveApp > Constants.ACTIVE_APP_NO_CHANGE) {
            returnIntent.putExtra(Constants.EXTRA_ACTIVE_APP, mActiveApp);
        }
    }

    /**
     * Search for .cyacd/.cyacd2 files
     *
     * @param dir - search for files in this directory
     */
    void searchRequiredFile(File dir) {
        if (dir.exists()) {
            File[] allFiles = dir.listFiles();
            if (allFiles != null) { // Might be null on Android M and above when not granted Storage permission
                for (int pos = 0; pos < allFiles.length; pos++) {
                    File analyseFile = allFiles[pos];
                    if (analyseFile != null) {
                        if (analyseFile.isDirectory()) {
                            searchRequiredFile(analyseFile);
                        } else {
                            Uri selectedUri = Uri.fromFile(analyseFile);
                            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(selectedUri.toString());
                            if (fileExtension.matches(REGEX_CYACD)) {
                                OTAFileModel fileModel = new OTAFileModel(analyseFile.getName(), analyseFile.getAbsolutePath(), false, analyseFile.getParent());
                                mArrayListFiles.add(fileModel);
                                mFirmwareAdapter.addFiles(mArrayListFiles);
                                mFirmwareAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }
        } else {
            ToastUtils.makeText(R.string.directory_does_not_exist, Toast.LENGTH_SHORT);
        }
    }

    void alertFileSelection(String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle(R.string.app_name).setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onResume() {
        mIsStarted = true;
        super.onResume();
    }

    @Override
    protected void onPause() {
        mIsStarted = false;
        super.onPause();
    }

    private static class DisplayableValue {
        private final int mValue;
        private final String mDisplayValue;

        public DisplayableValue(int value, String displayValue) {
            mValue = value;
            mDisplayValue = displayValue;
        }

        public int getValue() {
            return mValue;
        }

        public String getDisplayValue() {
            return mDisplayValue;
        }

        @Override
        public String toString() {
            return getDisplayValue();
        }
    }
}


