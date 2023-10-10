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

package com.infineon.airocbluetoothconnect.BLEProfileDataParserClasses;

import android.bluetooth.BluetoothGattDescriptor;
import android.content.Context;

import com.infineon.airocbluetoothconnect.CommonUtils.Constants;
import com.infineon.airocbluetoothconnect.CommonUtils.GattAttributes;
import com.infineon.airocbluetoothconnect.R;

import java.nio.charset.Charset;
import java.util.HashMap;

/**
 * Parser class for parsing the descriptor value
 */
public class DescriptorParser {

    //Switch case Constants
    public static final int CASE_NOTIFY_DISABLED_IND_DISABLED = 0;
    public static final int CASE_NOTIFY_ENABLED_IND_DISABLED = 1;
    public static final int CASE_IND_ENABLED_NOTIFY_DISABLED = 2;
    public static final int CASE_IND_ENABLED_NOTIFY_ENABLED = 3;

    public static String getClientCharacteristicConfiguration(BluetoothGattDescriptor descriptor, Context context) {
        String valueConverted = "";
        byte[] array = descriptor.getValue();
        switch (array[0]) {
            case CASE_NOTIFY_DISABLED_IND_DISABLED:
                valueConverted = context.getResources().getString(R.string.descriptor_notification_disabled)
                        + "\n" + context.getResources().getString(R.string.descriptor_indication_disabled);
                break;
            case CASE_NOTIFY_ENABLED_IND_DISABLED:
                valueConverted = context.getResources().getString(R.string.descriptor_notification_enabled)
                        + "\n" + context.getResources().getString(R.string.descriptor_indication_disabled);
                break;
            case CASE_IND_ENABLED_NOTIFY_DISABLED:
                valueConverted = context.getResources().getString(R.string.descriptor_indication_enabled)
                        + "\n" + context.getResources().getString(R.string.descriptor_notification_disabled);
                break;
            case CASE_IND_ENABLED_NOTIFY_ENABLED:
                valueConverted = context.getResources().getString(R.string.descriptor_indication_enabled)
                        + "\n" + context.getResources().getString(R.string.descriptor_notification_enabled);
                break;
        }
        return valueConverted;
    }

    public static HashMap<String, String> getCharacteristicExtendedProperties(BluetoothGattDescriptor descriptor, Context context) {
        HashMap<String, String> valuesMap = new HashMap<String, String>();

        String reliableWriteStatus;
        String writableAuxiliariesStatus;
        byte firstByte = descriptor.getValue()[0];

        if ((firstByte & 0x01) != 0) {
            reliableWriteStatus = context.getResources().getString(R.string.descriptor_reliablewrite_enabled);
        } else {
            reliableWriteStatus = context.getResources().getString(R.string.descriptor_reliablewrite_disabled);
        }
        if ((firstByte & 0x02) != 0) {
            writableAuxiliariesStatus = context.getResources().getString(R.string.descriptor_writableauxillary_enabled);
        } else {
            writableAuxiliariesStatus = context.getResources().getString(R.string.descriptor_writableauxillary_disabled);
        }
        valuesMap.put(Constants.FIRST_BIT_KEY_VALUE, reliableWriteStatus);
        valuesMap.put(Constants.SECOND_BIT_KEY_VALUE, writableAuxiliariesStatus);
        return valuesMap;
    }

    public static String getCharacteristicUserDescription(BluetoothGattDescriptor descriptor) {
        Charset UTF8_CHARSET = Charset.forName("UTF-8");
        byte[] valueEncoded = descriptor.getValue();
        return new String(valueEncoded, UTF8_CHARSET);
    }

    public static String getServerCharacteristicConfiguration(BluetoothGattDescriptor descriptor, Context context) {
        byte firstBit = descriptor.getValue()[0];
        String broadcastStatus;
        if ((firstBit & 0x01) != 0) {
            broadcastStatus = context.getResources().getString(R.string.descriptor_broadcast_enabled);
        } else {
            broadcastStatus = context.getResources().getString(R.string.descriptor_broadcast_disabled);
        }
        return broadcastStatus;
    }

    public static String getCharacteristicPresentationFormat(BluetoothGattDescriptor descriptor, Context context) {
        String value = "";
        String formatKey = String.valueOf(descriptor.getValue()[0]);
        String formatValue = GattAttributes.lookCharacteristicPresentationFormat(formatKey);
        String exponentValue = String.valueOf(descriptor.getValue()[1]);
        byte unit1 = descriptor.getValue()[2];
        byte unit2 = descriptor.getValue()[3];
        String unitValue = String.valueOf(((unit1 & 0xFF) | unit2 << 8));
        String namespaceValue = String.valueOf(descriptor.getValue()[4]);
        if (namespaceValue.equalsIgnoreCase("1")) {
            namespaceValue = context.getResources().getString(R.string.descriptor_bluetoothSIGAssignedNo);
        } else {
            namespaceValue = context.getResources().getString(R.string.descriptor_reservedforFutureUse);
        }
        String descriptionValue = String.valueOf(descriptor.getValue()[5]);

        value = String.join("\n",
                String.format("%s = %s", context.getResources().getString(R.string.descriptor_format), formatValue),
                String.format("%s = %s", context.getResources().getString(R.string.exponent), exponentValue),
                String.format("%s = %s", context.getResources().getString(R.string.unit), unitValue),
                String.format("%s = %s", context.getResources().getString(R.string.namespace), namespaceValue),
                String.format("%s = %s", context.getResources().getString(R.string.description), descriptionValue)
        );

        return value;
    }
}
