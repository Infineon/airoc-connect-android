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

package com.cypress.cysmart.ListAdapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cypress.cysmart.DataModelClasses.NavigationDrawerModel;
import com.cypress.cysmart.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Expandable list adapter for navigation drawer elements
 */
public class NavDrawerExpandableListAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private ArrayList<NavigationDrawerModel> mHeaderData;
    private HashMap<NavigationDrawerModel,List<String>> mChildData;

    public NavDrawerExpandableListAdapter(Context context,
                                          ArrayList<NavigationDrawerModel> headerdata,
                                          HashMap<NavigationDrawerModel,List<String>> childdata){
        this.mContext=context;
        this.mHeaderData=headerdata;
        this.mChildData=childdata;
    }

    @Override
    public int getGroupCount() {
        return this.mHeaderData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.mChildData.get(this.mHeaderData.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.mHeaderData.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.mChildData.get(this.mHeaderData.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int position, boolean b, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) this.mContext
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.fragment_drawer_list_item,
                    parent, false);
        }

        TextView drawerTitle = (TextView) convertView.findViewById(R.id.title);
        ImageView drawericon = (ImageView) convertView.findViewById(R.id.icon);

        drawericon.setImageResource(mHeaderData.get(position).getIcon());
        drawerTitle.setText(mHeaderData.get(position).getTitle());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean b, View convertView, ViewGroup viewGroup) {
        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.fragment_list_child_item, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);
        ImageView txtListChildIcon = (ImageView) convertView
                .findViewById(R.id.lbicon);
        txtListChild.setText(childText);
        if(childText.equalsIgnoreCase(mContext.getResources().
                getString(R.string.navigation_drawer_child_ble))){
            txtListChildIcon.setImageResource(R.drawable.products);
        }else if(childText.equalsIgnoreCase(mContext.getResources().
                getString(R.string.navigation_drawer_child_home))){
            txtListChildIcon.setImageResource(R.drawable.home);
        }else if(childText.equalsIgnoreCase(mContext.getResources().
                getString(R.string.navigation_drawer_child_contact))){
            txtListChildIcon.setImageResource(R.drawable.contact);
        }else if(childText.equalsIgnoreCase(mContext.getResources().
                getString(R.string.navigation_drawer_child_mobile))){
            txtListChildIcon.setImageResource(R.drawable.mobile);
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

}
