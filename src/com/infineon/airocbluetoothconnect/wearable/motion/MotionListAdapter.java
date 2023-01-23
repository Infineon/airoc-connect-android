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

package com.infineon.airocbluetoothconnect.wearable.motion;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.Toast;

import com.infineon.airocbluetoothconnect.R;
import com.infineon.airocbluetoothconnect.wearable.Const;
import com.infineon.airocbluetoothconnect.wearable.parser.MotionDataParser;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MotionListAdapter extends BaseExpandableListAdapter implements View.OnClickListener {

    private static final int MAX_UINT16 = 65536;
    private static final int MAX_UINT8 = 256;
    private static final int DELAY_MILLIS = 1000;

    public interface Listener {

        void onRefreshChildView(int groupPosition, int childPosition);

        void onCollapseGroup(int groupPosition);

        void onExpandGroup(int groupPosition);

        void onApplyHeight(byte[] b);

        void onApplyWeight(byte[] b);

        void onApplyAge(byte[] b);
    }

    private final Context mContext;
    private final Listener mListener;
    private final MotionListAdapterHelper.Group[] mGroups;
    private final int mGroupTypeCount;
    private final int mChildTypeCount;

    private MotionListAdapterHelper.AccelerometerChartChild mAccelerometerChartChild;
    private MotionListAdapterHelper.GyroscopeChartChild mGyroscopeChartChild;
    private MotionListAdapterHelper.MagnetometerChartChild mMagnetometerChartChild;
    private MotionListAdapterHelper.OrientationChartChild mOrientationChartChild;
    private MotionListAdapterHelper.StepsChild mStepsChild;
    private MotionListAdapterHelper.Calories1Child mCalories1Child;
    private MotionListAdapterHelper.Calories2Child mCalories2Child;

    public MotionListAdapter(Context context,
                             Listener listener,
                             boolean isAcc,
                             boolean isGyr,
                             boolean isMag,
                             boolean isOrientation,
                             boolean isSteps,
                             boolean isCalories) {
        this.mContext = context;
        this.mListener = listener;
        List<MotionListAdapterHelper.Group> groups = new ArrayList<>();
        int groupType = 0;
        int childType = 0;
        int groupPosition = 0;
        if (isAcc) {
            mAccelerometerChartChild = new MotionListAdapterHelper.AccelerometerChartChild(childType++, 0);
            groups.add(new MotionListAdapterHelper.ChartGroup(context, this, groupType, groupPosition++, mContext.getString(R.string.group_title_accelerometer), mAccelerometerChartChild));
        }
        if (isGyr) {
            mGyroscopeChartChild = new MotionListAdapterHelper.GyroscopeChartChild(childType++, 0);
            groups.add(new MotionListAdapterHelper.ChartGroup(context, this, groupType, groupPosition++, mContext.getString(R.string.group_title_gyroscope), mGyroscopeChartChild));
        }
        if (isMag) {
            mMagnetometerChartChild = new MotionListAdapterHelper.MagnetometerChartChild(childType++, 0);
            groups.add(new MotionListAdapterHelper.ChartGroup(context, this, groupType, groupPosition++, mContext.getString(R.string.group_title_magnetometer), mMagnetometerChartChild));
        }
        if (isOrientation) {
            mOrientationChartChild = new MotionListAdapterHelper.OrientationChartChild(childType++, 0);
            groups.add(new MotionListAdapterHelper.ChartGroup(context, this, groupType, groupPosition++, mContext.getString(R.string.group_title_orientation), mOrientationChartChild));
        }
        if (groups.size() > 0) {
            groupType++;
        }
        if (isSteps) {
            mStepsChild = new MotionListAdapterHelper.StepsChild(childType++, 0);
            groups.add(new MotionListAdapterHelper.Group(context, this, groupType, groupPosition++, mContext.getString(R.string.group_title_steps), mStepsChild));
        }
        if (isCalories) {
            mCalories1Child = new MotionListAdapterHelper.Calories1Child(childType++, 0);
            mCalories2Child = new MotionListAdapterHelper.Calories2Child(childType++, 1);
            groups.add(new MotionListAdapterHelper.Group(context, this, groupType, groupPosition++, mContext.getString(R.string.group_title_calories), mCalories1Child, mCalories2Child));
        }
        mGroups = groups.toArray(new MotionListAdapterHelper.Group[0]);

        Set<Integer> groupTypeCountSet = new HashSet<>();
        Set<Integer> childTypeCountSet = new HashSet<>();
        for (MotionListAdapterHelper.Group g : mGroups) {
            groupTypeCountSet.add(g.mType);
            for (MotionListAdapterHelper.Child c : g.mChildren) {
                childTypeCountSet.add(c.mType);
            }
        }
        mGroupTypeCount = groupTypeCountSet.size();
        mChildTypeCount = childTypeCountSet.size();
    }

    @Override
    public int getGroupCount() {
        return mGroups.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mGroups[groupPosition].mChildren.length;
    }

    @Override
    public MotionListAdapterHelper.Group getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition * 10 + childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public int getGroupTypeCount() {
        return mGroupTypeCount;
    }

    @Override
    public int getGroupType(int groupPosition) {
        return mGroups[groupPosition].mType;
    }

    @Override
    public int getChildTypeCount() {
        return mChildTypeCount;
    }

    @Override
    public int getChildType(int groupPosition, int childPosition) {
        return mGroups[groupPosition].mChildren[childPosition].mType;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        MotionListAdapterHelper.GroupViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mGroups[groupPosition].mLayoutResourceId, null);
            holder = mGroups[groupPosition].createViewHolder(groupPosition, isExpanded, convertView, parent);
            convertView.setTag(holder);
        } else {
            holder = (MotionListAdapterHelper.GroupViewHolder) convertView.getTag();
        }
        mGroups[groupPosition].updateViewHolder(holder, groupPosition, isExpanded, convertView, parent);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        MotionListAdapterHelper.ChildViewHolder holder;
        MotionListAdapterHelper.Child child = mGroups[groupPosition].mChildren[childPosition];
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(child.mLayoutResourceId, null);
            holder = child.createViewHolder(groupPosition, childPosition, isLastChild, convertView, parent);
            convertView.setTag(holder);
        } else {
            holder = (MotionListAdapterHelper.ChildViewHolder) convertView.getTag();
        }
        child.updateViewHolder(holder, groupPosition, childPosition, isLastChild, convertView, parent);
        return convertView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chart_indicator:
                int groupPosition = (int) v.getTag();
                MotionListAdapterHelper.ChartGroup chartGroup = (MotionListAdapterHelper.ChartGroup) mGroups[groupPosition];
                chartGroup.mShowChart = !chartGroup.mShowChart;
                MotionListAdapterHelper.ChartChild chartChild = null;
                for (MotionListAdapterHelper.Child c : chartGroup.mChildren) {
                    if (c instanceof MotionListAdapterHelper.ChartChild) {
                        chartChild = (MotionListAdapterHelper.ChartChild) c;
                        break;
                    }
                }
                mListener.onRefreshChildView(groupPosition, chartChild.mPosition);
                break;
            case R.id.group_indicator:
                int groupPosition2 = (int) v.getTag();
                if (v.isSelected()) {
                    mListener.onCollapseGroup(groupPosition2);
                    if (mGroups[groupPosition2] instanceof MotionListAdapterHelper.ChartGroup) {
                        ((MotionListAdapterHelper.ChartGroup) mGroups[groupPosition2]).mResetChart = true;
                    }
                } else {
                    mListener.onExpandGroup(groupPosition2);
                }
                break;
            case R.id.apply:
                boolean heightValid = false, weightValid = false, ageValid = false;
                int height = 0, weight = 0;
                short age = 0;
                try {
                    height = Integer.parseInt(mCalories1Child.mHeightStr.toString());
                    if (heightValid = height >= 0 && height <= MAX_UINT16) {
                        mCalories1Child.mHeightStr.clear();
                        mCalories1Child.mHeightStr.append("" + height); // to trim trailing 0-s
                    }

                    weight = Integer.parseInt(mCalories1Child.mWeightStr.toString());
                    if (weightValid = weight >= 0 && weight <= MAX_UINT16) {
                        mCalories1Child.mWeightStr.clear();
                        mCalories1Child.mWeightStr.append("" + weight); // to trim trailing 0-s
                    }

                    age = Short.parseShort(mCalories1Child.mAgeStr.toString());
                    if (ageValid = age >= 0 && age <= MAX_UINT8) {
                        mCalories1Child.mAgeStr.clear();
                        mCalories1Child.mAgeStr.append("" + age); // to trim trailing 0-s
                    }
                } catch (NumberFormatException e) {
                }
                if (!heightValid || !weightValid || !ageValid) {
                    boolean reportHeight = false, reportWeight = false, reportAge = false;
                    switch (mCalories1Child.mFocusOwnerId) {
                        case R.id.height:
                            if (!heightValid) {
                                reportHeight = true;
                            } else if (!weightValid) {
                                reportWeight = true;
                            } else {
                                reportAge = true;
                            }
                            break;
                        case R.id.weight:
                            if (!weightValid) {
                                reportWeight = true;
                            } else if (!heightValid) {
                                reportHeight = true;
                            } else {
                                reportAge = true;
                            }
                            break;
                        case R.id.age:
                            if (!ageValid) {
                                reportAge = true;
                            } else if (!heightValid) {
                                reportHeight = true;
                            } else {
                                reportWeight = true;
                            }
                            break;
                    }
                    String field;
                    int max;
                    if (reportHeight) {
                        field = "Height";
                        max = MAX_UINT16;
                    } else if (reportWeight) {
                        field = "Weight";
                        max = MAX_UINT16;
                    } else if (reportAge) {
                        field = "Age";
                        max = MAX_UINT8;
                    } else {
                        // no field was set yet (hence no focus owner)
                        field = "Height";
                        max = MAX_UINT16;
                    }
                    Toast.makeText(mContext,
                            field + ": Valid range is from 0 to " + max, Toast.LENGTH_SHORT).show();
                    break;
                }

                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                ByteBuffer bb = ByteBuffer.allocate(2).order(Const.BYTE_ORDER);
                byte[] heightArr = ((ByteBuffer) bb.putShort((short) height).rewind()).array();
                final byte[] weightArr = ((ByteBuffer) bb.putShort((short) weight).rewind()).array();
                final byte[] ageArr = {(byte) age};

                mListener.onApplyHeight(heightArr);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mListener.onApplyWeight(weightArr);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mListener.onApplyAge(ageArr);
                            }
                        }, DELAY_MILLIS);
                    }
                }, DELAY_MILLIS);
                break;
        }
    }

    public void processAgeData(byte[] b) {
        mCalories1Child.mAgeStr.clear();
        mCalories1Child.mAgeStr.append("" + b[0]);
        mListener.onRefreshChildView(mCalories1Child.mGroup.mPosition, mCalories1Child.mPosition);
    }

    public void processWeightData(byte[] b) {
        mCalories1Child.mWeightStr.clear();
        mCalories1Child.mWeightStr.append("" + (b[0] | (b[1] << 8)));
        mListener.onRefreshChildView(mCalories1Child.mGroup.mPosition, mCalories1Child.mPosition);
    }

    public void processHeightData(byte[] b) {
        mCalories1Child.mHeightStr.clear();
        mCalories1Child.mHeightStr.append("" + (b[0] | (b[1] << 8)));
        mListener.onRefreshChildView(mCalories1Child.mGroup.mPosition, mCalories1Child.mPosition);
    }

    public void processCaloriesData(MotionDataParser.MotionData data) {
        mCalories2Child.mCalories = data.mCalories;
        mListener.onRefreshChildView(mCalories2Child.mGroup.mPosition, mCalories2Child.mPosition);
    }

    public void processStepsData(MotionDataParser.MotionData data) {
        mStepsChild.mSteps = data.mSteps;
        mListener.onRefreshChildView(mStepsChild.mGroup.mPosition, mStepsChild.mPosition);
    }

    public void processOrientationData(MotionDataParser.MotionData data) {
        mOrientationChartChild.mUpdater.update(data.mRoll, data.mPitch, data.mYaw);
        mListener.onRefreshChildView(mOrientationChartChild.mGroup.mPosition, mOrientationChartChild.mPosition);
    }

    public void processMagnetometerData(MotionDataParser.MotionData data) {
        mMagnetometerChartChild.mUpdater.update(data.mMagX, data.mMagY, data.mMagZ);
        mListener.onRefreshChildView(mMagnetometerChartChild.mGroup.mPosition, mMagnetometerChartChild.mPosition);
    }

    public void processGyroscopeData(MotionDataParser.MotionData data) {
        mGyroscopeChartChild.mUpdater.update(data.mGyrX, data.mGyrY, data.mGyrZ);
        mListener.onRefreshChildView(mGyroscopeChartChild.mGroup.mPosition, mGyroscopeChartChild.mPosition);
    }

    public void processAccelerometerData(MotionDataParser.MotionData data) {
        mAccelerometerChartChild.mUpdater.update(data.mAccX, data.mAccY, data.mAccZ);
        mListener.onRefreshChildView(mAccelerometerChartChild.mGroup.mPosition, mAccelerometerChartChild.mPosition);
    }
}
