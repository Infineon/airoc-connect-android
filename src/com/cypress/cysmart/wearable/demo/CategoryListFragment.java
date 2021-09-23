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

package com.cypress.cysmart.wearable.demo;

import android.app.AlertDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.cypress.cysmart.BLEConnectionServices.BluetoothLeService;
import com.cypress.cysmart.CommonUtils.Constants;
import com.cypress.cysmart.CommonUtils.GattAttributes;
import com.cypress.cysmart.CommonUtils.UUIDDatabase;
import com.cypress.cysmart.GATTDBFragments.GattServicesFragment;
import com.cypress.cysmart.R;
import com.cypress.cysmart.wearable.AbstractFragment;
import com.cypress.cysmart.wearable.Const;
import com.cypress.cysmart.wearable.location.LocationFragment;
import com.cypress.cysmart.wearable.model.Category;
import com.cypress.cysmart.wearable.model.ValueWithoutUnit;
import com.cypress.cysmart.wearable.model.Variable;
import com.cypress.cysmart.wearable.model.environment.Elevation;
import com.cypress.cysmart.wearable.model.environment.PollenConcentration;
import com.cypress.cysmart.wearable.model.environment.Pressure;
import com.cypress.cysmart.wearable.model.environment.TemperatureVariable;
import com.cypress.cysmart.wearable.model.location.PositionVariable;
import com.cypress.cysmart.wearable.model.location.UtcTimeVariable;
import com.cypress.cysmart.wearable.model.motion.Calories;
import com.cypress.cysmart.wearable.model.motion.Distance;
import com.cypress.cysmart.wearable.model.motion.Duration;
import com.cypress.cysmart.wearable.model.motion.Floors;
import com.cypress.cysmart.wearable.model.motion.Sleep;
import com.cypress.cysmart.wearable.model.motion.Speed;
import com.cypress.cysmart.wearable.motion.MotionFragment;
import com.cypress.cysmart.wearable.parser.LocationDataFlagsParser;
import com.cypress.cysmart.wearable.parser.LocationDataFlagsParser.LocationDataFlags;
import com.cypress.cysmart.wearable.parser.LocationDataParser;
import com.cypress.cysmart.wearable.parser.LocationDataParser.LocationData;
import com.cypress.cysmart.wearable.parser.LocationFeatureParser;
import com.cypress.cysmart.wearable.parser.LocationFeatureParser.LocationFeature;
import com.cypress.cysmart.wearable.parser.MotionDataParser;
import com.cypress.cysmart.wearable.parser.MotionDataParser.MotionData;
import com.cypress.cysmart.wearable.parser.MotionFeatureParser;
import com.cypress.cysmart.wearable.parser.MotionFeatureParser.MotionFeature;
import com.cypress.cysmart.wearable.utils.Utilities;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

// TODO:
// targets (SLQ)
// units conversion
// reset
// UV index color coding
// comments
// refactor (move code up/down)
// externalize strings
// replace type checking with polymorphism
// verify enabling/disabling with help of BLE analyzer
// min/max/value double to int
public abstract class CategoryListFragment extends AbstractFragment
        implements ExpandableListView.OnChildClickListener,
        ExpandableListView.OnGroupExpandListener,
        ExpandableListView.OnGroupCollapseListener,
        AdapterView.OnItemLongClickListener,
        PropertyChangeListener, FragmentManager.OnBackStackChangedListener {

    private static final String[] MOTION_CHARACTERISTICS = {
            GattAttributes.WEARABLE_MOTION_FEATURE_CHARACTERISTIC,
            GattAttributes.WEARABLE_MOTION_DATA_CHARACTERISTIC,
            GattAttributes.WEARABLE_MOTION_CONTROL_CHARACTERISTIC
    };
    private static final String[] ENV_CHARACTERISTICS = {
            GattAttributes.TEMPERATURE,
            GattAttributes.UV_INDEX,
            GattAttributes.POLLEN_CONCENTRATION,
            GattAttributes.PRESSURE,
            GattAttributes.ELEVATION
    };
    private static final String[] LOC_CHARACTERISTICS = {
            GattAttributes.LN_FEATURE,
            GattAttributes.LOCATION_AND_SPEED
    };

    protected Map<Category.Id, List<Variable.Id>> mCategoriesToBuild;
    protected Set<BluetoothGattCharacteristic> mCategoryCharacteristicsToDisable = new HashSet<>();

    private BluetoothGattService mMotionService;
    private Map<String, BluetoothGattCharacteristic> mMotionCharacteristics = new HashMap<>();

    private BluetoothGattService mEnvService;
    private Map<String, BluetoothGattCharacteristic> mEnvCharacteristics = new HashMap<>();

    private BluetoothGattService mLocService;
    private Map<String, BluetoothGattCharacteristic> mLocCharacteristics = new HashMap<>();

    private CategoryListAdapter mListAdapter;
    private Map<Category.Id, Integer> mCatIdx = new HashMap<>(); // TODO: Integer -> int
    private Map<Category.Id, Map<Variable.Id, Integer>> mVarIdx = new HashMap<>(); // TODO: Integer -> int
    private Handler mHandler = new Handler();
    private boolean mDisablingCategoryCharacteristics;
    private boolean mDisablingAllCharacteristics;
    private boolean mWaitingForAllCharacteristicsToBeDisabled;
    private Category.Id mNavigateToFragment;
    private LocationFeature mLocFeatures;

    private boolean mBulkUpdate;
    private LinkedList<Variable> mBulkUpdateList = new LinkedList<>();
    private Runnable mBulkUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            Variable var;
            while ((var = mBulkUpdateList.pollFirst()) != null) {
                Integer catIdx = mCatIdx.get(var.mCategory.mId);
                Integer varIdx = mVarIdx.get(var.mCategory.mId).get(var.mId);
                refreshChildView(catIdx, varIdx);
            }
        }
    };

    // TODO
    private void reset() {
        mCatIdx.clear();
        mVarIdx.clear();
        mCategoryBuilders.clear();
    }

    private LinkedList<Callable<Boolean>> mCategoryBuilders = new LinkedList<>();

    private Callable<Boolean> mActivityCategoryBuilder = new Callable<Boolean>() {
        @Override
        public Boolean call() throws Exception {
            boolean consumed = mMotionService != null
                    && mMotionCharacteristics.containsKey(GattAttributes.WEARABLE_MOTION_FEATURE_CHARACTERISTIC);
            if (consumed) {
                toggleProgressOn("Reading Motion Features,\nPlease wait..."); // TODO: externalize
                BluetoothLeService.readCharacteristic(
                        mMotionCharacteristics.get(GattAttributes.WEARABLE_MOTION_FEATURE_CHARACTERISTIC));
            }
            return consumed;
        }
    };

    private Callable<Boolean> mEnvironmentCategoryBuilder = new Callable<Boolean>() {
        @Override
        public Boolean call() throws Exception {
            boolean consumed = false;
            if (mEnvService != null) {
                buildAndAddEnvironmentCategory();
            }
            return consumed;
        }
    };

    private Callable<Boolean> mLocationCategoryBuilder = new Callable<Boolean>() {
        @Override
        public Boolean call() throws Exception {
            boolean consumed = mLocService != null
                    && mLocCharacteristics.containsKey(GattAttributes.LN_FEATURE);
            if (consumed) {
                toggleProgressOn("Reading Location and Navigation Features,\nPlease wait..."); // TODO: externalize
                BluetoothLeService.readCharacteristic(
                        mLocCharacteristics.get(GattAttributes.LN_FEATURE));
            }
            return consumed;
        }
    };

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Bundle extras = intent.getExtras();
            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                mBulkUpdate = true;
                try {
                    if (extras.containsKey(Constants.EXTRA_BYTE_SERVICE_UUID_VALUE)
                            && extras.containsKey(Constants.EXTRA_BYTE_UUID_VALUE)) {

                        String serviceUuid = extras.getString(Constants.EXTRA_BYTE_SERVICE_UUID_VALUE);
                        String charactUuid = extras.getString(Constants.EXTRA_BYTE_UUID_VALUE);
                        byte[] b = extras.getByteArray(Constants.EXTRA_BYTE_VALUE);
                        if (GattAttributes.WEARABLE_MOTION_SERVICE.equalsIgnoreCase(serviceUuid)) {

                            if (GattAttributes.WEARABLE_MOTION_FEATURE_CHARACTERISTIC.equalsIgnoreCase(charactUuid)) {

                                toggleProgressOff();
                                MotionFeature f = MotionFeatureParser.parse(b);
                                buildAndAddActivityCategory(f);
                                buildNextCategory();
                            } else if (GattAttributes.WEARABLE_MOTION_DATA_CHARACTERISTIC.equalsIgnoreCase(charactUuid)) {

                                MotionFeature f = MotionFeatureParser.parse(b);
                                MotionData d = MotionDataParser.parse(b);
                                updateActivityCategory(f, d);
                            } else if (GattAttributes.WEARABLE_MOTION_CONTROL_CHARACTERISTIC.equalsIgnoreCase(charactUuid)) {

                                // TODO
                            }
                        } else if (GattAttributes.ENVIRONMENTAL_SENSING_SERVICE.equalsIgnoreCase(serviceUuid)) {

                            updateEnvironmentCategory(b, charactUuid);
                        } else if (GattAttributes.LOCATION_NAVIGATION_SERVICE.equalsIgnoreCase(serviceUuid)) {

                            if (GattAttributes.LN_FEATURE.equalsIgnoreCase(charactUuid)) {

                                toggleProgressOff();
                                mLocFeatures = LocationFeatureParser.parse(b);
                                buildAndAddLocationCategory(mLocFeatures);
                                buildNextCategory();
                            } else if (GattAttributes.LOCATION_AND_SPEED.equalsIgnoreCase(charactUuid)) {

                                LocationDataFlags f = LocationDataFlagsParser.parse(b);
                                LocationData d = LocationDataParser.parse(b);
                                updateLocationCategory(f, d);
                            }
                        }
                    }
                } finally {
                    mBulkUpdate = false;
                }
                bulkUpdate();
            } else if (BluetoothLeService.ACTION_WRITE_SUCCESS.equals(action)) {

                // Category collapsed
                if (mDisablingCategoryCharacteristics) {
                    boolean allCharacteristicsDisabled = true;
                    for (BluetoothGattCharacteristic c : mCategoryCharacteristicsToDisable) {
                        if (BluetoothLeService.mEnabledCharacteristics.contains(c)) {
                            allCharacteristicsDisabled = false;
                            break;
                        }
                    }
                    if (allCharacteristicsDisabled) {
                        mDisablingCategoryCharacteristics = false;
                        toggleProgressOff();
                        Toast.makeText(getActivity(), getNotificationsDisabledMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
                // User clicked on a variable within some category
                // Wait for all characteristics to become disabled and navigate user to corresponding BLE profile view
                else if (mDisablingAllCharacteristics) {
                    if (BluetoothLeService.mEnabledCharacteristics.size() == 0) {
                        mDisablingAllCharacteristics = false;
                        toggleProgressOff();
                        Toast.makeText(getActivity(), getString(R.string.profile_control_stop_both_notify_indicate_toast),
                                Toast.LENGTH_SHORT).show();
                        // cannot use switch (mNavigateToFragment) due to nullable nature of mNavigateToFragment
                        if (mNavigateToFragment == Category.Id.ACTIVITY) {
                            unregisterReceiverAndAddFragment(new MotionFragment(), MotionFragment.TAG);
                        } else if (mNavigateToFragment == Category.Id.LOCATION) {
                            unregisterReceiverAndAddFragment(LocationFragment.create(), LocationFragment.TAG);
                        } else {
                            unregisterReceiverAndAddFragment(new GattServicesFragment().create(), getString(R.string.gatt_db));
                        }
                    }
                }
                // We are back on the page (via back button)
                // All characteristics which were disabled before leaving the page should be enabled back
                else if (mWaitingForAllCharacteristicsToBeDisabled) {
                    if (BluetoothLeService.mEnabledCharacteristics.size() == 0) {
                        mWaitingForAllCharacteristicsToBeDisabled = false;
                        enableNotifications(getAllCharacteristicsToEnable());
                    }
                }
            } else if (BluetoothLeService.ACTION_WRITE_COMPLETED.equals(action)) {

                toggleProgressOff();
                Toast.makeText(getActivity(), getNotificationsEnabledMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        System.err.println("--DEMO: CREATE");
        super.onCreate(savedInstanceState);
        initGatt();
        mListAdapter = new CategoryListAdapter(getActivity()) {
            @Override
            protected void onIndicatorClick(int position, boolean expand) {
                if (expand) {
                    mListView.expandGroup(position);
                } else {
                    mListView.collapseGroup(position);
                }
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.err.println("--DEMO: CREATE_VIEW");
        reset(); // TODO
        View root = inflater.inflate(R.layout.wearable_demo_fragment, container, false);
        mListView = (ExpandableListView) root.findViewById(R.id.categories);
        // TODO
//        mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
//            @Override
//            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
//                return true; // disable group click, instead use group indicator to expand/collapse
//            }
//        });
        mListView.setOnChildClickListener(this);
        mListView.setOnItemLongClickListener(this);
        mListView.setAdapter(mListAdapter);

        if (mCategoriesToBuild.containsKey(Category.Id.ACTIVITY)) {
            mCategoryBuilders.add(mActivityCategoryBuilder);
        }
        if (mCategoriesToBuild.containsKey(Category.Id.ENVIRONMENT)) {
            mCategoryBuilders.add(mEnvironmentCategoryBuilder);
        }
        if (mCategoriesToBuild.containsKey(Category.Id.LOCATION)) {
            mCategoryBuilders.add(mLocationCategoryBuilder);
        }
        registerBroadcastReceiver(mReceiver, getIntentFilter());
        buildNextCategory();

        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // TODO
    }

    @Override
    public void onResume() {
        System.err.println("--DEMO: RESUME");
        super.onResume();
        enableListeners();
    }

    @Override
    public void onPause() {
        System.err.println("--DEMO: PAUSE");
        disableListeners();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        System.err.println("--DEMO: DESTROY");
        disableAllNotifications();
        super.onDestroy();
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        Category.Id catId = mListAdapter.getGroup(groupPosition).mId;
        switch (catId) {
            case ACTIVITY:
                if (BluetoothLeService.mEnabledCharacteristics.size() == 0) {
                    unregisterReceiverAndAddFragment(new MotionFragment(), MotionFragment.TAG);
                } else {
                    mDisablingAllCharacteristics = true;
                    mNavigateToFragment = catId;
                    toggleProgressOn(getDisablingNotificationsMessage());
                    disableAllNotifications(false);
                }
                return true;
            case LOCATION:
                if (BluetoothLeService.mEnabledCharacteristics.size() == 0) {
                    unregisterReceiverAndAddFragment(LocationFragment.create(), LocationFragment.TAG);
                } else {
                    mDisablingAllCharacteristics = true;
                    mNavigateToFragment = catId;
                    toggleProgressOn(getDisablingNotificationsMessage());
                    disableAllNotifications(false);
                }
                return true;
            default:
                showWarningMessage();
                return false; // TODO
        }
    }

    @Override
    public void onGroupExpand(int groupPosition) {
        switch (mListAdapter.getGroup(groupPosition).mId) {
            case ACTIVITY:
                enableNotifications(getActivityNotifiableCharacteristics());
                break;
            case ENVIRONMENT:
                enableNotifications(getEnvironmentNotifiableCharacteristics());
                break;
            case LOCATION:
                enableNotifications(getLocationNotifiableCharacteristics());
                break;
        }
    }

    @Override
    public void onGroupCollapse(int groupPosition) {
        switch (mListAdapter.getGroup(groupPosition).mId) {
            case ACTIVITY:
                mCategoryCharacteristicsToDisable.clear();
                mCategoryCharacteristicsToDisable.addAll(getActivityNotifiableCharacteristics());
                mDisablingCategoryCharacteristics = true;
                disableNotifications(mCategoryCharacteristicsToDisable);
                break;
            case ENVIRONMENT:
                mCategoryCharacteristicsToDisable.clear();
                mCategoryCharacteristicsToDisable.addAll(getEnvironmentNotifiableCharacteristics());
                mDisablingCategoryCharacteristics = true;
                disableNotifications(mCategoryCharacteristicsToDisable);
                break;
            case LOCATION:
                mCategoryCharacteristicsToDisable.clear();
                mCategoryCharacteristicsToDisable.addAll(getLocationNotifiableCharacteristics());
                mDisablingCategoryCharacteristics = true;
                disableNotifications(mCategoryCharacteristicsToDisable);
                break;
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        long packedPos = mListView.getExpandableListPosition(position);
        int postType = ExpandableListView.getPackedPositionType(packedPos);
        if (postType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            int childPosition = ExpandableListView.getPackedPositionChild(packedPos);
            int groupPosition = ExpandableListView.getPackedPositionGroup(packedPos);
            final Variable var = mListAdapter.getChild(groupPosition, childPosition);
            new VariableOptionsDialog(getActivity(), var).show();
            return true;
        }
        return false;
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        Variable var = (Variable) event.getSource();
        if (mBulkUpdate) {
            if (!mBulkUpdateList.contains(var)) {
                mBulkUpdateList.addLast(var);
            }
        } else {
            final Integer catIdx = mCatIdx.get(var.mCategory.mId);
            final Integer varIdx = mVarIdx.get(var.mCategory.mId).get(var.mId);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    refreshChildView(catIdx, varIdx);
                }
            });
        }
    }

    @Override
    public void onBackStackChanged() {
        if (isVisible() && mBackstackCount == getFragmentManager().getBackStackEntryCount()) {
            System.err.println("--DEMO: BACK STACK CHANGED: " + BluetoothLeService.mEnabledCharacteristics.size());
            if (BluetoothLeService.mEnabledCharacteristics.size() > 0) {
                mWaitingForAllCharacteristicsToBeDisabled = true;
                registerBroadcastReceiver(mReceiver, getIntentFilter());
            } else {
                registerBroadcastReceiver(mReceiver, getIntentFilter());
                enableNotifications(getAllCharacteristicsToEnable());
            }
        }
    }

    // TODO: handle missing service/char
    private void initGatt() {
        mMotionService = mEnvService = mLocService = null;
        mLocFeatures = null;
        for (BluetoothGattService s : BluetoothLeService.getSupportedGattServices()) {
            if (UUIDDatabase.UUID_WEARABLE_MOTION_SERVICE.equals(s.getUuid())) {
                mMotionService = s;
                for (BluetoothGattCharacteristic c : mMotionService.getCharacteristics()) {
                    for (String k : MOTION_CHARACTERISTICS) {
                        if (k.equalsIgnoreCase(c.getUuid().toString())) {
                            //-- TODO: bug in BLE project - 2 characteristics (data and control) share same UUID
                            if (!mMotionCharacteristics.containsKey(k)) {
                                mMotionCharacteristics.put(k, c);
                            }
                            //--
                            break;
                        }
                    }
                }
            } else if (UUIDDatabase.UUID_ENVIRONMENTAL_SENSING_SERVICE.equals(s.getUuid())) {
                mEnvService = s;
                for (BluetoothGattCharacteristic c : mEnvService.getCharacteristics()) {
                    for (String k : ENV_CHARACTERISTICS) {
                        if (k.equalsIgnoreCase(c.getUuid().toString())) {
                            mEnvCharacteristics.put(k, c);
                            break;
                        }
                    }
                }
            } else if (UUIDDatabase.UUID_LOCATION_NAVIGATION_SERVICE.equals(s.getUuid())) {
                mLocService = s;
                for (BluetoothGattCharacteristic c : mLocService.getCharacteristics()) {
                    for (String k : LOC_CHARACTERISTICS) {
                        if (k.equalsIgnoreCase(c.getUuid().toString())) {
                            mLocCharacteristics.put(k, c);
                            break;
                        }
                    }
                }
            }
        }
        // TODO: check all characteristics found
    }

    private void bulkUpdate() {
        if (mBulkUpdateList.size() > 0) {
            mHandler.post(mBulkUpdateRunnable);
        }
    }

    private void updateVariableModelAndView(Category.Id catId, Variable.Id varId, double value) {
        int catIdx = mCatIdx.get(catId);
        int varIdx = mVarIdx.get(catId).get(varId);
        Variable var = mListAdapter.getChild(catIdx, varIdx);
        var.setUnresolvedValue(value);
    }

    private void updatePositionVariableModelAndView(Category.Id catId, int latitude, int longitude) {
        int catIdx = mCatIdx.get(catId);
        int varIdx = mVarIdx.get(catId).get(Variable.Id.LOC_POSITION);
        PositionVariable var = (PositionVariable) mListAdapter.getChild(catIdx, varIdx);
        var.setUnresolvedLatitudeAndLongitude(latitude, longitude);
    }

    private void updateUtcTimeVariableModelAndView(Category.Id catId, int year, short month, short day, short hours, short minutes, short seconds) {
        int catIdx = mCatIdx.get(catId);
        int varIdx = mVarIdx.get(catId).get(Variable.Id.LOC_UTC_TIME);
        UtcTimeVariable var = (UtcTimeVariable) mListAdapter.getChild(catIdx, varIdx);
        var.setUnresolvedDatetime(year, month, day, hours, minutes, seconds);
    }

    private void addVariable(Variable var, Category.Id catId, List<Variable> vars, int varIdx) {
        vars.add(var);
        if (!mVarIdx.containsKey(catId)) {
            mVarIdx.put(catId, new HashMap<Variable.Id, Integer>());
        }
        mVarIdx.get(catId).put(var.mId, varIdx);
    }

    private void unregisterReceiverAndAddFragment(Fragment fragment, String tag) {
        unregisterBroadcastReceiver(mReceiver);
        addFragment(fragment, tag);
    }

    private void enableListeners() {
        registerBroadcastReceiver(mReceiver, getIntentFilter());
        getFragmentManager().addOnBackStackChangedListener(this);
    }

    private void disableListeners() {
        unregisterBroadcastReceiver(mReceiver);
        getFragmentManager().removeOnBackStackChangedListener(this);
    }

    private void showWarningMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.alert_message_unknown_title);
        builder
                .setMessage(R.string.alert_message_unkown)
                .setCancelable(false)
                .setPositiveButton(R.string.alert_message_yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (BluetoothLeService.mEnabledCharacteristics.size() == 0) {
                                    unregisterReceiverAndAddFragment(new GattServicesFragment().create(), getString(R.string.gatt_db));
                                } else if (!mDisablingAllCharacteristics) {
                                    mDisablingAllCharacteristics = true;
                                    mNavigateToFragment = null;
                                    toggleProgressOn(getDisablingNotificationsMessage());
                                    disableAllNotifications(false);
                                }
                            }
                        })
                .setNegativeButton(R.string.alert_message_no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void buildAndAddActivityCategory(MotionFeature f) {
        List<Variable> vars = new LinkedList<>();
        int childCount = 0;
        final Category.Id catId = Category.Id.ACTIVITY;
        for (Variable.Id id : mCategoriesToBuild.get(catId)) {
            switch (id) {
                case ACT_STEPS:
                    if (f.mIsSteps) {
                        addVariable(new Variable(id, getString(R.string.var_act_steps), true, new ValueWithoutUnit(), CategoryListFragment.this),
                                catId, vars, childCount++);
                    }
                    break;
                case ACT_DURATION:
                    if (f.mIsDuration) {
                        addVariable(new Variable(id, getString(R.string.var_act_duration), true, new Duration(), CategoryListFragment.this),
                                catId, vars, childCount++);
                    }
                    break;
                case ACT_CALORIES:
                    if (f.mIsCalories) {
                        addVariable(new Variable(id, getString(R.string.var_act_calories), true, new Calories(), CategoryListFragment.this),
                                catId, vars, childCount++);
                    }
                    break;
                case ACT_DISTANCE:
                    if (f.mIsDistance) {
                        addVariable(new Variable(id, getString(R.string.var_act_distance), true, new Distance(), CategoryListFragment.this),
                                catId, vars, childCount++);
                    }
                    break;
                case ACT_SPEED:
                    if (f.mIsSpeed) {
                        addVariable(new Variable(id, getString(R.string.var_act_speed), false, new Speed(), CategoryListFragment.this),
                                catId, vars, childCount++);
                    }
                    break;
                case ACT_FLOORS:
                    if (f.mIsFloors) {
                        addVariable(new Variable(id, getString(R.string.var_act_floors), false, new Floors(), CategoryListFragment.this),
                                catId, vars, childCount++);
                    }
                    break;
                case ACT_SLEEP:
                    if (f.mIsSleep) {
                        addVariable(new Variable(id, getString(R.string.var_act_sleep), false, new Sleep(), CategoryListFragment.this),
                                catId, vars, childCount++);
                    }
                    break;
            }
        }
        if (vars.size() > 0) {
            int groupPosition = mCatIdx.size();
            mCatIdx.put(catId, groupPosition);
            mListAdapter.addCategory(new Category(catId, getString(R.string.cat_activity), true, vars.toArray(new Variable[0])));
            if (mListAdapter.getGroup(groupPosition).mEnabled) {
                mListView.expandGroup(groupPosition);
            }
        }
    }

    private void updateActivityCategory(MotionFeature f, MotionData d) {
        final Category.Id catId = Category.Id.ACTIVITY;
        if (mCategoriesToBuild.get(catId).contains(Variable.Id.ACT_STEPS)
                && f.mIsSteps) {
            updateVariableModelAndView(catId, Variable.Id.ACT_STEPS, d.mSteps);
        }
        if (mCategoriesToBuild.get(catId).contains(Variable.Id.ACT_DURATION)
                && f.mIsDuration) {
            updateVariableModelAndView(catId, Variable.Id.ACT_DURATION, d.mDuration);
        }
        if (mCategoriesToBuild.get(catId).contains(Variable.Id.ACT_CALORIES)
                && f.mIsCalories) {
            updateVariableModelAndView(catId, Variable.Id.ACT_CALORIES, d.mCalories);
        }
        if (mCategoriesToBuild.get(catId).contains(Variable.Id.ACT_DISTANCE)
                && f.mIsDistance) {
            updateVariableModelAndView(catId, Variable.Id.ACT_DISTANCE, d.mDistance);
        }
        if (mCategoriesToBuild.get(catId).contains(Variable.Id.ACT_SPEED)
                && f.mIsSpeed) {
            updateVariableModelAndView(catId, Variable.Id.ACT_SPEED, d.mSpeed);
        }
        if (mCategoriesToBuild.get(catId).contains(Variable.Id.ACT_FLOORS)
                && f.mIsFloors) {
            updateVariableModelAndView(catId, Variable.Id.ACT_FLOORS, d.mFloors);
        }
        if (mCategoriesToBuild.get(catId).contains(Variable.Id.ACT_SLEEP)
                && f.mIsSleep) {
            updateVariableModelAndView(catId, Variable.Id.ACT_SLEEP, d.mSleep);
        }
    }

    private void buildAndAddEnvironmentCategory() {
        LinkedList<Variable> vars = new LinkedList<>();
        int childCount = 0;
        final Category.Id catId = Category.Id.ENVIRONMENT;
        for (Variable.Id id : mCategoriesToBuild.get(catId)) {
            switch (id) {
                case ENV_TEMPERATURE:
                    if (mEnvCharacteristics.containsKey(GattAttributes.TEMPERATURE)) {
                        addVariable(new TemperatureVariable(getResources(), this), catId, vars, childCount++);
                    }
                    break;
                case ENV_UV:
                    if (mEnvCharacteristics.containsKey(GattAttributes.UV_INDEX)) {
                        Variable var = new Variable(id, getString(R.string.var_env_uv), false, new ValueWithoutUnit(), this);
                        var.setMaxValue(UvVariableView.VERY_HIGH); // TODO
                        addVariable(var, catId, vars, childCount++);
                    }
                    break;
                case ENV_AIR_QUALITY:
                    if (mEnvCharacteristics.containsKey(GattAttributes.POLLEN_CONCENTRATION)) {
                        addVariable(new Variable(id, getString(R.string.var_env_air_quality), false, new PollenConcentration(), this),
                                catId, vars, childCount++);
                    }
                    break;
                case ENV_PRESSURE:
                    if (mEnvCharacteristics.containsKey(GattAttributes.PRESSURE)) {
                        addVariable(new Variable(id, getString(R.string.var_env_pressure), false, new Pressure(), this),
                                catId, vars, childCount++);
                    }
                    break;
                case ENV_ALTITUDE:
                    if (mEnvCharacteristics.containsKey(GattAttributes.ELEVATION)) {
                        addVariable(new Variable(id, getString(R.string.var_env_altitude), false, new Elevation(), this),
                                catId, vars, childCount++);
                    }
                    break;
            }
        }
        if (vars.size() > 0) {
            int groupPosition = mCatIdx.size();
            mCatIdx.put(catId, groupPosition);
            mListAdapter.addCategory(new Category(catId, getString(R.string.cat_environment), true, vars.toArray(new Variable[0])));
            if (mListAdapter.getGroup(groupPosition).mEnabled) {
                mListView.expandGroup(groupPosition);
            }
        }
    }

    private void updateEnvironmentCategory(byte[] b, String charactUuid) {
        final Category.Id catId = Category.Id.ENVIRONMENT;
        if (mCategoriesToBuild.get(catId).contains(Variable.Id.ENV_TEMPERATURE)
                && GattAttributes.TEMPERATURE.equalsIgnoreCase(charactUuid)) {
            // sint16
            ByteBuffer bb = ByteBuffer.allocate(2).order(Const.BYTE_ORDER);
            bb.put(b).rewind();
            updateVariableModelAndView(catId, Variable.Id.ENV_TEMPERATURE, bb.getShort());
        } else if (mCategoriesToBuild.get(catId).contains(Variable.Id.ENV_UV)
                && GattAttributes.UV_INDEX.equalsIgnoreCase(charactUuid)) {
            // uint8
            ByteBuffer bb = ByteBuffer.allocate(1).order(Const.BYTE_ORDER);
            bb.put(b).rewind();
            updateVariableModelAndView(catId, Variable.Id.ENV_UV, bb.get());
        } else if (mCategoriesToBuild.get(catId).contains(Variable.Id.ENV_AIR_QUALITY)
                && GattAttributes.POLLEN_CONCENTRATION.equalsIgnoreCase(charactUuid)) {
            // uint24
            updateVariableModelAndView(catId, Variable.Id.ENV_AIR_QUALITY, Utilities.getLong(b, 3));
        } else if (mCategoriesToBuild.get(catId).contains(Variable.Id.ENV_PRESSURE)
                && GattAttributes.PRESSURE.equalsIgnoreCase(charactUuid)) {
            // uint32
            ByteBuffer bb = ByteBuffer.allocate(4).order(Const.BYTE_ORDER);
            bb.put(b).rewind();
            updateVariableModelAndView(catId, Variable.Id.ENV_PRESSURE, bb.getInt());
        } else if (mCategoriesToBuild.get(catId).contains(Variable.Id.ENV_ALTITUDE)
                && GattAttributes.ELEVATION.equalsIgnoreCase(charactUuid)) {
            // sint24
            updateVariableModelAndView(catId, Variable.Id.ENV_ALTITUDE, Utilities.getLong(b, 3));
        }
    }

    private void buildAndAddLocationCategory(LocationFeature f) {
        LinkedList<Variable> vars = new LinkedList<>();
        int childCount = 0;
        final Category.Id catId = Category.Id.LOCATION;
        for (Variable.Id varId : mCategoriesToBuild.get(catId)) {
            switch (varId) {
                case LOC_SPEED:
                    if (f.mIsInstantaneousSpeed) {
                        addVariable(new Variable(varId, getString(R.string.var_loc_speed), false, new com.cypress.cysmart.wearable.model.location.Speed(), CategoryListFragment.this),
                                catId, vars, childCount++);
                    }
                    break;
                case LOC_TOTAL_DISTANCE:
                    if (f.mIsTotalDistance) {
                        addVariable(new Variable(varId, "Total distance", false, new ValueWithoutUnit(), CategoryListFragment.this),
                                catId, vars, childCount++);
                    }
                    break;
                case LOC_POSITION:
                    if (f.mIsLocation) {
                        addVariable(new PositionVariable(getResources(), CategoryListFragment.this),
                                catId, vars, childCount++);
                    }
                    break;
                case LOC_ALTITUDE:
                    if (f.mIsElevation) {
                        addVariable(new Variable(varId, getString(R.string.var_loc_altitude), false, new Elevation(), CategoryListFragment.this),
                                catId, vars, childCount++);
                    }
                    break;
                case LOC_HEADING:
                    if (f.mIsHeading) {
                        addVariable(new Variable(Variable.Id.LOC_HEADING, "Heading", false, new ValueWithoutUnit(), CategoryListFragment.this),
                                catId, vars, childCount++);
                    }
                    break;
                case LOC_ROLLING_TIME:
                    if (f.mIsRollingTime) {
                        addVariable(new Variable(Variable.Id.LOC_ROLLING_TIME, "Rolling time", false, new ValueWithoutUnit(), CategoryListFragment.this),
                                catId, vars, childCount++);
                    }
                    break;
                case LOC_UTC_TIME:
                    if (f.mIsUtcTime) {
                        addVariable(new UtcTimeVariable(getResources(), CategoryListFragment.this),
                                catId, vars, childCount++);
                    }
                    break;
            }
        }
        if (vars.size() > 0) {
            int groupPosition = mCatIdx.size();
            mCatIdx.put(Category.Id.LOCATION, groupPosition);
            mListAdapter.addCategory(new Category(Category.Id.LOCATION, getString(R.string.cat_location), true, vars.toArray(new Variable[0])));
            if (mListAdapter.getGroup(groupPosition).mEnabled) {
                mListView.expandGroup(groupPosition);
            }
        }
    }

    private void updateLocationCategory(LocationDataFlags f, LocationData d) {
        final Category.Id catId = Category.Id.LOCATION;
        // seems there is a defect in BLE FW in that features and flags may not match
        if (mCategoriesToBuild.get(catId).contains(Variable.Id.LOC_SPEED)
                && mLocFeatures.mIsInstantaneousSpeed
                && f.mIsInstantaneousSpeed) {
            updateVariableModelAndView(catId, Variable.Id.LOC_SPEED, d.mInstantaneousSpeed);
        }
        if (mCategoriesToBuild.get(catId).contains(Variable.Id.LOC_TOTAL_DISTANCE)
                && mLocFeatures.mIsTotalDistance
                && f.mIsTotalDistance) {
            updateVariableModelAndView(catId, Variable.Id.LOC_TOTAL_DISTANCE, d.mTotalDistance);
        }
        if (mCategoriesToBuild.get(catId).contains(Variable.Id.LOC_POSITION)
                && mLocFeatures.mIsLocation
                && f.mIsLocation) {
            updatePositionVariableModelAndView(catId, d.mLocationLatitude, d.mLocationLongitude);
        }
        if (mCategoriesToBuild.get(catId).contains(Variable.Id.LOC_ALTITUDE)
                && mLocFeatures.mIsElevation
                && f.mIsElevation) {
            updateVariableModelAndView(catId, Variable.Id.LOC_ALTITUDE, d.mElevation);
        }
        if (mCategoriesToBuild.get(catId).contains(Variable.Id.LOC_HEADING)
                && mLocFeatures.mIsHeading
                && f.mIsHeading) {
            updateVariableModelAndView(catId, Variable.Id.LOC_HEADING, d.mHeading);
        }
        if (mCategoriesToBuild.get(catId).contains(Variable.Id.LOC_ROLLING_TIME)
                && mLocFeatures.mIsRollingTime
                && f.mIsRollingTime) {
            updateVariableModelAndView(catId, Variable.Id.LOC_ROLLING_TIME, d.mRollingTime);
        }
        if (mCategoriesToBuild.get(catId).contains(Variable.Id.LOC_UTC_TIME)
                && mLocFeatures.mIsUtcTime
                && f.mIsUtcTime) {
            updateUtcTimeVariableModelAndView(catId, d.mYear, d.mMonth, d.mDay, d.mHours, d.mMinutes, d.mSeconds);
//            updateVariableModelAndView(catId, Variable.Id.LOC_UTC_TIME_YEAR, d.mYear);
//            updateVariableModelAndView(catId, Variable.Id.LOC_UTC_TIME_MONTH, d.mMonth);
//            updateVariableModelAndView(catId, Variable.Id.LOC_UTC_TIME_DAY, d.mDay);
//            updateVariableModelAndView(catId, Variable.Id.LOC_UTC_TIME_HOURS, d.mHours);
//            updateVariableModelAndView(catId, Variable.Id.LOC_UTC_TIME_MINUTES, d.mMinutes);
//            updateVariableModelAndView(catId, Variable.Id.LOC_UTC_TIME_SECONDS, d.mSeconds);
        }
    }

    private void buildNextCategory() {
        try {
            Callable<Boolean> call = null;
            do {
                call = mCategoryBuilders.pollFirst();
                if (call == null) {
                    // We don't want enable/disable notification logic to be triggered during automatic category expansion in onCreateView
                    // Hence registering expand/collapse listeners after automatic category expansion completed
                    mListView.setOnGroupExpandListener(this);
                    mListView.setOnGroupCollapseListener(this);

                    System.err.println("--DEMO: ENABLING NOTIFICATIONS");
                    enableNotifications(getAllCharacteristicsToEnable());
                }
            } while (call != null && !call.call());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    private Collection<BluetoothGattCharacteristic> getActivityNotifiableCharacteristics() {
        List<BluetoothGattCharacteristic> list = new LinkedList<>();
        if (mMotionCharacteristics.containsKey(GattAttributes.WEARABLE_MOTION_DATA_CHARACTERISTIC)) {
            list.add(mMotionCharacteristics.get(GattAttributes.WEARABLE_MOTION_DATA_CHARACTERISTIC));
        }
        return list;
    }

    @NonNull
    private Collection<BluetoothGattCharacteristic> getEnvironmentNotifiableCharacteristics() {
        List<BluetoothGattCharacteristic> list = new LinkedList<>();
        for (String k : ENV_CHARACTERISTICS) {
            if (mEnvCharacteristics.containsKey(k)) {
                list.add(mEnvCharacteristics.get(k));
            }
        }
        return list;
    }

    @NonNull
    private Collection<BluetoothGattCharacteristic> getLocationNotifiableCharacteristics() {
        Collection<BluetoothGattCharacteristic> list = new LinkedList<>();
        if (mLocCharacteristics.containsKey(GattAttributes.LOCATION_AND_SPEED)) {
            list.add(mLocCharacteristics.get(GattAttributes.LOCATION_AND_SPEED));
        }
        return list;
    }

    @NonNull
    private Collection<BluetoothGattCharacteristic> getAllCharacteristicsToEnable() {
        Collection<BluetoothGattCharacteristic> list = new LinkedList<>();
        CategoryListAdapter adapter = (CategoryListAdapter) mListView.getExpandableListAdapter();
        for (int i = 0; i < adapter.getGroupCount(); i++) {
            if (mListView.isGroupExpanded(i)) {
                switch (adapter.getGroup(i).mId) {
                    case ACTIVITY:
                        list.addAll(getActivityNotifiableCharacteristics());
                        break;
                    case ENVIRONMENT:
                        list.addAll(getEnvironmentNotifiableCharacteristics());
                        break;
                    case LOCATION:
                        list.addAll(getLocationNotifiableCharacteristics());
                        break;
                }
            }
        }
        return list;
    }
}
