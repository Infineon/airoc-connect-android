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

package com.cypress.cysmart.wearable.model.location;

import android.content.res.Resources;

import com.cypress.cysmart.R;
import com.cypress.cysmart.wearable.model.ValueWithUnit;
import com.cypress.cysmart.wearable.model.Variable;

import java.beans.PropertyChangeListener;

public class PositionVariable extends Variable {

    private ValueWithUnit mLatitude = new Latitude();
    private ValueWithUnit mLongitude = new Longitude();

    public PositionVariable(Resources resources, PropertyChangeListener listener) {
        super(Id.LOC_POSITION, resources.getString(R.string.var_loc_position), false, null, listener);
    }

    @Override
    public void setUnresolvedValue(double unresolvedValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double getMinValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public double getMaxValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setMaxValue(double maxValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double getValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getValueString() {
        return mLatitude.getValueString() + ", " + mLongitude.getValueString();
    }

    public ValueWithUnit.Unit[] getSupportedUnits() {
        return mLatitude.getSupportedUnits();
    }

    public ValueWithUnit.Unit getUnit() {
        return mLatitude.getUnit();
    }

    public void setUnit(ValueWithUnit.Unit unit) {
        if (getUnit() != unit) {
            mLatitude.setUnit(unit);
            mLongitude.setUnit(unit);
            mPropertyChangeSupport.firePropertyChange("unit", null, null); // TODO: optimize
        }
    }

    public void setUnresolvedLatitudeAndLongitude(double unresolvedLatitude, double unresolvedLongitude) {
        if (mLatitude.getUnresolvedValueInDefaultUnit() != unresolvedLatitude
                || mLongitude.getUnresolvedValueInDefaultUnit() != unresolvedLongitude) {
            mLatitude.setUnresolvedValueInDefaultUnit(unresolvedLatitude);
            mLongitude.setUnresolvedValueInDefaultUnit(unresolvedLongitude);
            mPropertyChangeSupport.firePropertyChange("value", null, null); // TODO: optimize
        }
    }
}
