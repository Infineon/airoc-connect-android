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
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.infineon.airocbluetoothconnect.R;
import com.infineon.airocbluetoothconnect.wearable.chart.TimeBasedChart;
import com.infineon.airocbluetoothconnect.wearable.chart.TimeBasedChartUpdater;

class MotionListAdapterHelper {

    static class GroupViewHolder {

        private TextView mName;
        private View mChartIndicator;
        private View mGroupIndicator;
    }

    static class ChildViewHolder {

        TimeBasedChart mChart;
        private TextView mX;
        private TextView mY;
        private TextView mZ;
        private TextView mSteps;
        private Button mApply;
        private EditText mHeight;
        private EditText mWeight;
        private EditText mAge;
        private TextView mCalories;
    }

    private static abstract class Container {

        final int mLayoutResourceId;
        final int mType;
        final int mPosition;

        public Container(int layoutResourceId, int type, int position) {
            this.mLayoutResourceId = layoutResourceId;
            this.mType = type;
            this.mPosition = position;
        }
    }

    static class Group extends Container {

        protected final Context mContext;
        protected final View.OnClickListener mOnClickListener;
        private final String mTitle;
        final Child[] mChildren;

        public Group(Context context, View.OnClickListener onClickListener, int type, int position, String title, Child... children) {
            this(context, onClickListener, R.layout.wearable_motion_list_group, type, position, title, children);
        }

        private Group(Context context, View.OnClickListener onClickListener, int layoutResourceId, int type, int position, String title, Child... children) {
            super(layoutResourceId, type, position);
            this.mContext = context;
            this.mOnClickListener = onClickListener;
            this.mTitle = title;
            this.mChildren = children;
            for (Child c : children) {
                c.setGroup(this);
            }
        }

        public GroupViewHolder createViewHolder(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupViewHolder holder = new GroupViewHolder();
            holder.mName = (TextView) convertView.findViewById(R.id.name);
            holder.mGroupIndicator = convertView.findViewById(R.id.group_indicator);
            holder.mGroupIndicator.setOnClickListener(mOnClickListener);
            return holder;
        }

        public void updateViewHolder(GroupViewHolder holder, int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            holder.mName.setText(mTitle);
            holder.mGroupIndicator.setSelected(isExpanded);
            holder.mGroupIndicator.setTag(groupPosition);
        }
    }

    static class ChartGroup extends Group {

        boolean mShowChart = true;
        boolean mResetChart;

        public ChartGroup(Context context, View.OnClickListener onClickListener, int type, int position, String title, Child... children) {
            super(context, onClickListener, R.layout.wearable_motion_list_group_chart, type, position, title, children);
        }

        @Override
        public GroupViewHolder createViewHolder(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupViewHolder holder = super.createViewHolder(groupPosition, isExpanded, convertView, parent);
            holder.mChartIndicator = convertView.findViewById(R.id.chart_indicator);
            holder.mChartIndicator.setOnClickListener(mOnClickListener);
            return holder;
        }

        @Override
        public void updateViewHolder(GroupViewHolder holder, int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            super.updateViewHolder(holder, groupPosition, isExpanded, convertView, parent);
            holder.mChartIndicator.setTag(groupPosition);
        }
    }

    abstract static class Child extends Container {

        protected Group mGroup;

        public Child(int layoutResourceId, int type, int position) {
            super(layoutResourceId, type, position);
        }

        public abstract ChildViewHolder createViewHolder(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent);

        public abstract void updateViewHolder(ChildViewHolder holder, int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent);

        public void setGroup(Group group) {
            this.mGroup = group;
        }
    }

    static class StepsChild extends Child {

        int mSteps;

        public StepsChild(int type, int position) {
            super(R.layout.wearable_motion_list_child_steps, type, position);
        }

        @Override
        public ChildViewHolder createViewHolder(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ChildViewHolder holder = new ChildViewHolder();
            holder.mSteps = (TextView) convertView.findViewById(R.id.steps);
            return holder;
        }

        @Override
        public void updateViewHolder(ChildViewHolder holder, int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            holder.mSteps.setText("" + mSteps);
        }
    }

    static class Calories1Child extends Child {

        Editable mWeightStr = new SpannableStringBuilder();
        Editable mHeightStr = new SpannableStringBuilder();
        Editable mAgeStr = new SpannableStringBuilder();
        int mFocusOwnerId;

        public Calories1Child(int type, int position) {
            super(R.layout.wearable_motion_list_child_calories1, type, position);
        }

        @Override
        public ChildViewHolder createViewHolder(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ChildViewHolder holder = new ChildViewHolder();
            holder.mApply = (Button) convertView.findViewById(R.id.apply);
            holder.mApply.setOnClickListener(mGroup.mOnClickListener);

            View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        switch (v.getId()) {
                            case R.id.height:
                                mHeightStr = ((EditText) v).getText();
                                break;
                            case R.id.weight:
                                mWeightStr = ((EditText) v).getText();
                                break;
                            case R.id.age:
                                mAgeStr = ((EditText) v).getText();
                                break;
                        }
                        mFocusOwnerId = v.getId();
                    }
                }
            };
            holder.mHeight = (EditText) convertView.findViewById(R.id.height);
            holder.mHeight.setOnFocusChangeListener(onFocusChangeListener);
            holder.mWeight = (EditText) convertView.findViewById(R.id.weight);
            holder.mWeight.setOnFocusChangeListener(onFocusChangeListener);
            holder.mAge = (EditText) convertView.findViewById(R.id.age);
            holder.mAge.setOnFocusChangeListener(onFocusChangeListener);
            return holder;
        }

        @Override
        public void updateViewHolder(ChildViewHolder holder, int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            holder.mHeight.setText(mHeightStr);
            holder.mWeight.setText(mWeightStr);
            holder.mAge.setText(mAgeStr);
        }
    }

    static class Calories2Child extends Child {

        int mCalories;

        public Calories2Child(int type, int position) {
            super(R.layout.wearable_motion_list_child_calories2, type, position);
        }

        @Override
        public ChildViewHolder createViewHolder(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ChildViewHolder holder = new ChildViewHolder();
            holder.mCalories = (TextView) convertView.findViewById(R.id.calories);
            return holder;
        }

        @Override
        public void updateViewHolder(ChildViewHolder holder, int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            holder.mCalories.setText("" + mCalories);
        }
    }

    abstract static class ChartChild extends Child {

        private final int mChartTitleResourceId;
        String mChartTitle;
        final int mSeriesCount;

        public ChartChild(int layoutResourceId, int type, int position, int chartTitleResourceId, int seriesCount) {
            super(layoutResourceId, type, position);
            this.mChartTitleResourceId = chartTitleResourceId;
            this.mSeriesCount = seriesCount;
        }

        @Override
        public void setGroup(Group group) {
            super.setGroup(group);
            this.mChartTitle = group.mContext.getString(mChartTitleResourceId);
        }
    }

    private static abstract class XyzChartChild extends ChartChild {

        TimeBasedChartUpdater mUpdater;

        public XyzChartChild(int layoutResourceId, int type, int position, int chartTitleResourceId) {
            super(layoutResourceId, type, position, chartTitleResourceId, 3);
            this.mUpdater = new TimeBasedChartUpdater(mSeriesCount);
        }

        @Override
        public ChildViewHolder createViewHolder(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ChildViewHolder holder = new ChildViewHolder();
            holder.mX = (TextView) convertView.findViewById(R.id.x);
            holder.mY = (TextView) convertView.findViewById(R.id.y);
            holder.mZ = (TextView) convertView.findViewById(R.id.z);
            ((TextView) convertView.findViewById(R.id.xLabel))
                    .setTextColor(TimeBasedChart.sColors[0 % TimeBasedChart.sColors.length]);
            ((TextView) convertView.findViewById(R.id.yLabel))
                    .setTextColor(TimeBasedChart.sColors[1 % TimeBasedChart.sColors.length]);
            ((TextView) convertView.findViewById(R.id.zLabel))
                    .setTextColor(TimeBasedChart.sColors[2 % TimeBasedChart.sColors.length]);
            holder.mChart = createChart(mGroup.mContext);
            ViewGroup chartContainer = (ViewGroup) convertView.findViewById(R.id.chart_container);
            chartContainer.addView(holder.mChart.mChart);
            return holder;
        }

        protected TimeBasedChart createChart(Context context) {
            return TimeBasedChart.newXyzInstance(context, mChartTitle);
        }

        @Override
        public void updateViewHolder(ChildViewHolder holder, int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ViewGroup chartContainer = (ViewGroup) holder.mChart.mChart.getParent();
            ChartGroup chartGroup = (ChartGroup) mGroup;
            chartContainer.setVisibility(chartGroup.mShowChart ? View.VISIBLE : View.GONE);
            if (chartGroup.mShowChart) {
                if (chartGroup.mResetChart) {
                    chartGroup.mResetChart = false;
                    chartContainer.removeView(holder.mChart.mChart);
                    holder.mChart = TimeBasedChart.newInstanceFrom(holder.mChart);
                    mUpdater = TimeBasedChartUpdater.newInstanceFrom(mUpdater);
                    chartContainer.addView(holder.mChart.mChart);
                }
                mUpdater.repaint(holder.mChart);
            }
            holder.mX.setText("" + mUpdater.mCurrentValues[0]);
            holder.mY.setText("" + mUpdater.mCurrentValues[1]);
            holder.mZ.setText("" + mUpdater.mCurrentValues[2]);
        }
    }

    static class AccelerometerChartChild extends XyzChartChild {

        public AccelerometerChartChild(int type, int position) {
            super(R.layout.wearable_motion_list_child_xyz_chart, type, position, R.string.chart_title_accelerometer);
        }
    }

    static class GyroscopeChartChild extends XyzChartChild {

        public GyroscopeChartChild(int type, int position) {
            super(R.layout.wearable_motion_list_child_xyz_chart, type, position, R.string.chart_title_gyroscope);
        }
    }

    static class MagnetometerChartChild extends XyzChartChild {

        public MagnetometerChartChild(int type, int position) {
            super(R.layout.wearable_motion_list_child_xyz_chart, type, position, R.string.chart_title_magnetometer);
        }
    }

    static abstract class RollPitchYawChartChild extends XyzChartChild {

        public RollPitchYawChartChild(int layoutResourceId, int type, int position, int chartTitleResourceId) {
            super(layoutResourceId, type, position, chartTitleResourceId);
        }

        @Override
        public ChildViewHolder createViewHolder(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ChildViewHolder holder = super.createViewHolder(groupPosition, childPosition, isLastChild, convertView, parent);
            ((TextView) convertView.findViewById(R.id.xLabel))
                    .setText(mGroup.mContext.getString(R.string.roll));
            ((TextView) convertView.findViewById(R.id.yLabel))
                    .setText(mGroup.mContext.getString(R.string.pitch));
            ((TextView) convertView.findViewById(R.id.zLabel))
                    .setText(mGroup.mContext.getString(R.string.yaw));
            return holder;
        }

        @Override
        protected TimeBasedChart createChart(Context context) {
            return TimeBasedChart.newRollPitchYawInstance(context, mChartTitle);
        }
    }

    static class OrientationChartChild extends RollPitchYawChartChild {

        public OrientationChartChild(int type, int position) {
            super(R.layout.wearable_motion_list_child_xyz_chart, type, position, R.string.chart_title_orientation);
        }
    }
}
