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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.infineon.airocbluetoothconnect.R;
import com.infineon.airocbluetoothconnect.wearable.model.Category;
import com.infineon.airocbluetoothconnect.wearable.model.Variable;

import java.util.LinkedList;
import java.util.List;

public abstract class CategoryListAdapter extends BaseExpandableListAdapter
        implements CompoundButton.OnCheckedChangeListener {

    private final Context mContext;
    private final List<Category> mCategories = new LinkedList<>();

    public CategoryListAdapter(Context context) {
        this.mContext = context;
    }

    public void addCategory(Category category) {
        this.mCategories.add(category);
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return mCategories.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mCategories.get(groupPosition).mVariables.length;
    }

    @Override
    public Category getGroup(int groupPosition) {
        return mCategories.get(groupPosition);
    }

    @Override
    public Variable getChild(int groupPosition, int childPosition) {
        return mCategories.get(groupPosition).mVariables[childPosition];
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
    public int getChildTypeCount() {
        return 2;
    }

    @Override
    public int getChildType(int groupPosition, int childPosition) {
        return getChild(groupPosition, childPosition).mId == Variable.Id.ENV_UV ? 0 : 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    // TODO: broken caching
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.wearable_demo_list_group, null);
            holder = new GroupViewHolder();
            holder.mName = (TextView) convertView.findViewById(R.id.name);
            holder.mGroupIndicator = (CompoundButton) convertView.findViewById(R.id.group_indicator);
            holder.mGroupIndicator.setOnCheckedChangeListener(this);
            convertView.setTag(holder);
        } else {
            holder = (GroupViewHolder) convertView.getTag();
        }
        holder.mName.setText(getGroup(groupPosition).mName);
        holder.mGroupIndicator.setChecked(isExpanded);
        holder.mGroupIndicator.setTag(groupPosition);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            holder = new ChildViewHolder();
            if (getChildType(groupPosition, childPosition) == 0) {
                convertView = inflater.inflate(R.layout.wearable_demo_list_child_uv, null);
            } else {
                convertView = inflater.inflate(R.layout.wearable_demo_list_child, null);
            }
            holder.mVariable = (VariableView) convertView.findViewById(R.id.variable);
            convertView.setTag(holder);
        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }
        holder.mVariable.setVariable(getGroup(groupPosition).mVariables[childPosition]);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    protected abstract void onIndicatorClick(int position, boolean expanded);

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getTag() != null) {
            int groupPosition = (int) buttonView.getTag();
            System.err.println("---" + groupPosition + ", " + isChecked + ", " + buttonView.isChecked());
            onIndicatorClick(groupPosition, isChecked);
        }
    }

    private static class GroupViewHolder {

        private TextView mName;
        private CompoundButton mGroupIndicator;
    }

    private static class ChildViewHolder {

        private VariableView mVariable;
    }
}
