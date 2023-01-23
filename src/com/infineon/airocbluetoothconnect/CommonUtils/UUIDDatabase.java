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

package com.infineon.airocbluetoothconnect.CommonUtils;

import java.util.UUID;

/**
 * This class will store the UUID of the GATT services and characteristics
 */
public class UUIDDatabase {
    /**
     * Heart rate related UUID
     */
    public final static UUID UUID_HEART_RATE_SERVICE = UUID
            .fromString(GattAttributes.HEART_RATE_SERVICE);
    public final static UUID UUID_HEART_RATE_MEASUREMENT = UUID
            .fromString(GattAttributes.HEART_RATE_MEASUREMENT);
    public final static UUID UUID_BODY_SENSOR_LOCATION = UUID
            .fromString(GattAttributes.BODY_SENSOR_LOCATION);

    /**
     * Device Information Service
     */
    public final static UUID UUID_DEVICE_INFORMATION_SERVICE = UUID
            .fromString(GattAttributes.DEVICE_INFORMATION_SERVICE);
    public static final UUID UUID_MANUFACTURER_NAME = UUID
            .fromString(GattAttributes.MANUFACTURER_NAME);
    public static final UUID UUID_MODEL_NUMBER = UUID
            .fromString(GattAttributes.MODEL_NUMBER);
    public static final UUID UUID_SERIAL_NUMBER = UUID
            .fromString(GattAttributes.SERIAL_NUMBER);
    public static final UUID UUID_HARDWARE_REVISION = UUID
            .fromString(GattAttributes.HARDWARE_REVISION);
    public static final UUID UUID_FIRMWARE_REVISION = UUID
            .fromString(GattAttributes.FIRMWARE_REVISION);
    public static final UUID UUID_SOFTWARE_REVISION = UUID
            .fromString(GattAttributes.SOFTWARE_REVISION);
    public final static UUID UUID_SYSTEM_ID = UUID
            .fromString(GattAttributes.SYSTEM_ID);
    public static final UUID UUID_REGULATORY_CERTIFICATION_DATA_LIST = UUID
            .fromString(GattAttributes.REGULATORY_CERTIFICATION_DATA_LIST);
    public static final UUID UUID_PNP_ID = UUID
            .fromString(GattAttributes.UUID_PNP_ID);

    /**
     * Health thermometer related UUID
     */
    public final static UUID UUID_HEALTH_THERMOMETER_SERVICE = UUID
            .fromString(GattAttributes.HEALTH_THERMOMETER_SERVICE);
    public final static UUID UUID_TEMPERATURE_MEASUREMENT = UUID
            .fromString(GattAttributes.TEMPERATURE_MEASUREMENT);
    public final static UUID UUID_TEMPERATURE_TYPE = UUID
            .fromString(GattAttributes.TEMPERATURE_TYPE);
    public final static UUID UUID_INTERMEDIATE_TEMPERATURE = UUID
            .fromString(GattAttributes.INTERMEDIATE_TEMPERATURE);
    public final static UUID UUID_MEASUREMENT_INTERVAL = UUID
            .fromString(GattAttributes.MEASUREMENT_INTERVAL);

    /**
     * Battery Level related uuid
     */
    public final static UUID UUID_BATTERY_SERVICE = UUID
            .fromString(GattAttributes.BATTERY_SERVICE);
    public final static UUID UUID_BATTERY_LEVEL = UUID
            .fromString(GattAttributes.BATTERY_LEVEL);

    /**
     * Find me related uuid
     */
    public final static UUID UUID_IMMEDIATE_ALERT_SERVICE = UUID
            .fromString(GattAttributes.IMMEDIATE_ALERT_SERVICE);
    public final static UUID UUID_TRANSMISSION_POWER_SERVICE = UUID
            .fromString(GattAttributes.TRANSMISSION_POWER_SERVICE);
    public final static UUID UUID_ALERT_LEVEL = UUID
            .fromString(GattAttributes.ALERT_LEVEL);
    public final static UUID UUID_TRANSMISSION_POWER_LEVEL = UUID
            .fromString(GattAttributes.TX_POWER_LEVEL);
    public final static UUID UUID_LINK_LOSS_SERVICE = UUID
            .fromString(GattAttributes.LINK_LOSS_SERVICE);

    /**
     * CapSense related uuid
     */
    public final static UUID UUID_CAPSENSE_SERVICE = UUID
            .fromString(GattAttributes.CAPSENSE_SERVICE);
    public final static UUID UUID_CAPSENSE_SERVICE_CUSTOM = UUID
            .fromString(GattAttributes.CAPSENSE_SERVICE_CUSTOM);
    public final static UUID UUID_CAPSENSE_PROXIMITY = UUID
            .fromString(GattAttributes.CAPSENSE_PROXIMITY);
    public final static UUID UUID_CAPSENSE_SLIDER = UUID
            .fromString(GattAttributes.CAPSENSE_SLIDER);
    public final static UUID UUID_CAPSENSE_BUTTONS = UUID
            .fromString(GattAttributes.CAPSENSE_BUTTONS);
    public final static UUID UUID_CAPSENSE_PROXIMITY_CUSTOM = UUID
            .fromString(GattAttributes.CAPSENSE_PROXIMITY_CUSTOM);
    public final static UUID UUID_CAPSENSE_SLIDER_CUSTOM = UUID
            .fromString(GattAttributes.CAPSENSE_SLIDER_CUSTOM);
    public final static UUID UUID_CAPSENSE_BUTTONS_CUSTOM = UUID
            .fromString(GattAttributes.CAPSENSE_BUTTONS_CUSTOM);
    /**
     * RGB LED related uuid
     */
    public final static UUID UUID_RGB_LED_SERVICE = UUID
            .fromString(GattAttributes.RGB_LED_SERVICE);
    public final static UUID UUID_RGB_LED = UUID
            .fromString(GattAttributes.RGB_LED);
    public final static UUID UUID_RGB_LED_SERVICE_CUSTOM = UUID
            .fromString(GattAttributes.RGB_LED_SERVICE_CUSTOM);
    public final static UUID UUID_RGB_LED_CUSTOM = UUID
            .fromString(GattAttributes.RGB_LED_CUSTOM);

    /**
     * GlucoseService related uuid
     */
    public final static UUID UUID_GLUCOSE_MEASUREMENT = UUID
            .fromString(GattAttributes.GLUCOSE_MEASUREMENT);
    public final static UUID UUID_GLUCOSE_SERVICE = UUID
            .fromString(GattAttributes.GLUCOSE_SERVICE);
    public final static UUID UUID_GLUCOSE_MEASUREMENT_CONTEXT = UUID
            .fromString(GattAttributes.GLUCOSE_MEASUREMENT_CONTEXT);
    public final static UUID UUID_GLUCOSE_FEATURE = UUID
            .fromString(GattAttributes.GLUCOSE_FEATURE);
    public final static UUID UUID_RECORD_ACCESS_CONTROL_POINT = UUID
            .fromString(GattAttributes.RECORD_ACCESS_CONTROL_POINT);
    /**
     * Blood pressure related uuid
     */
    public final static UUID UUID_BLOOD_PRESSURE_SERVICE = UUID
            .fromString(GattAttributes.BLOOD_PRESSURE_SERVICE);
    public final static UUID UUID_BLOOD_PRESSURE_MEASUREMENT = UUID
            .fromString(GattAttributes.BLOOD_PRESSURE_MEASUREMENT);
    public final static UUID UUID_BLOOD_INTERMEDIATE_CUFF_PRESSURE = UUID
            .fromString(GattAttributes.BLOOD_INTERMEDIATE_CUFF_PRESSURE);
    public final static UUID UUID_BLOOD_PRESSURE_FEATURE = UUID
            .fromString(GattAttributes.BLOOD_PRESSURE_FEATURE);
    /**
     * Running Speed & Cadence related uuid
     */
    public final static UUID UUID_RSC_MEASURE = UUID
            .fromString(GattAttributes.RSC_MEASUREMENT);
    public final static UUID UUID_RSC_SERVICE = UUID
            .fromString(GattAttributes.RSC_SERVICE);
    public final static UUID UUID_RSC_FEATURE = UUID
            .fromString(GattAttributes.RSC_FEATURE);
    public final static UUID UUID_SC_CONTROL_POINT = UUID
            .fromString(GattAttributes.SC_CONTROL_POINT);
    public final static UUID UUID_SC_SENSOR_LOCATION = UUID
            .fromString(GattAttributes.SC_SENSOR_LOCATION);


    /**
     * Cycling Speed & Cadence related uuid
     */
    public final static UUID UUID_CSC_SERVICE = UUID
            .fromString(GattAttributes.CSC_SERVICE);
    public final static UUID UUID_CSC_MEASURE = UUID
            .fromString(GattAttributes.CSC_MEASUREMENT);
    public final static UUID UUID_CSC_FEATURE = UUID
            .fromString(GattAttributes.CSC_FEATURE);

    /**
     * Barometer related uuid
     */
    public final static UUID UUID_BAROMETER_SERVICE = UUID
            .fromString(GattAttributes.BAROMETER_SERVICE);
    public final static UUID UUID_BAROMETER_DIGITAL_SENSOR = UUID
            .fromString(GattAttributes.BAROMETER_DIGITAL_SENSOR);
    public final static UUID UUID_BAROMETER_SENSOR_SCAN_INTERVAL = UUID
            .fromString(GattAttributes.BAROMETER_SENSOR_SCAN_INTERVAL);
    public final static UUID UUID_BAROMETER_THRESHOLD_FOR_INDICATION = UUID
            .fromString(GattAttributes.BAROMETER_THRESHOLD_FOR_INDICATION);
    public final static UUID UUID_BAROMETER_DATA_ACCUMULATION = UUID
            .fromString(GattAttributes.BAROMETER_DATA_ACCUMULATION);
    public final static UUID UUID_BAROMETER_READING = UUID
            .fromString(GattAttributes.BAROMETER_READING);
    /**
     * Accelerometer related uuid
     */
    public final static UUID UUID_ACCELEROMETER_SERVICE = UUID
            .fromString(GattAttributes.ACCELEROMETER_SERVICE);
    public final static UUID UUID_ACCELEROMETER_ANALOG_SENSOR = UUID
            .fromString(GattAttributes.ACCELEROMETER_ANALOG_SENSOR);
    public final static UUID UUID_ACCELEROMETER_DATA_ACCUMULATION = UUID
            .fromString(GattAttributes.ACCELEROMETER_DATA_ACCUMULATION);
    public final static UUID UUID_ACCELEROMETER_READING_X = UUID
            .fromString(GattAttributes.ACCELEROMETER_READING_X);
    public final static UUID UUID_ACCELEROMETER_READING_Y = UUID
            .fromString(GattAttributes.ACCELEROMETER_READING_Y);
    public final static UUID UUID_ACCELEROMETER_READING_Z = UUID
            .fromString(GattAttributes.ACCELEROMETER_READING_Z);
    public final static UUID UUID_ACCELEROMETER_SENSOR_SCAN_INTERVAL = UUID
            .fromString(GattAttributes.ACCELEROMETER_SENSOR_SCAN_INTERVAL);
    /**
     * Analog temperature  related uuid
     */
    public final static UUID UUID_ANALOG_TEMPERATURE_SERVICE = UUID
            .fromString(GattAttributes.ANALOG_TEMPERATURE_SERVICE);
    public final static UUID UUID_TEMPERATURE_ANALOG_SENSOR = UUID
            .fromString(GattAttributes.TEMPERATURE_ANALOG_SENSOR);
    public final static UUID UUID_TEMPERATURE_READING = UUID
            .fromString(GattAttributes.TEMPERATURE_READING);
    public final static UUID UUID_TEMPERATURE_SENSOR_SCAN_INTERVAL = UUID
            .fromString(GattAttributes.TEMPERATURE_SENSOR_SCAN_INTERVAL);

    /**
     * RDK related UUID
     */
    public final static UUID UUID_REPORT = UUID
            .fromString(GattAttributes.REPORT);

    /**
     * OTA related UUID
     */
    public final static UUID UUID_OTA_UPDATE_SERVICE = UUID
            .fromString(GattAttributes.OTA_UPDATE_SERVICE);
    public final static UUID UUID_OTA_UPDATE_CHARACTERISTIC = UUID
            .fromString(GattAttributes.OTA_CHARACTERISTIC);

    /**
     * Descriptor UUID
     */
    public final static UUID UUID_CLIENT_CHARACTERISTIC_CONFIG = UUID
            .fromString(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG);
    public final static UUID UUID_CHARACTERISTIC_EXTENDED_PROPERTIES = UUID
            .fromString(GattAttributes.CHARACTERISTIC_EXTENDED_PROPERTIES);
    public final static UUID UUID_CHARACTERISTIC_USER_DESCRIPTION = UUID
            .fromString(GattAttributes.CHARACTERISTIC_USER_DESCRIPTION);
    public final static UUID UUID_SERVER_CHARACTERISTIC_CONFIGURATION = UUID
            .fromString(GattAttributes.SERVER_CHARACTERISTIC_CONFIGURATION);
    public final static UUID UUID_REPORT_REFERENCE = UUID
            .fromString(GattAttributes.REPORT_REFERENCE);
    public final static UUID UUID_CHARACTERISTIC_PRESENTATION_FORMAT = UUID
            .fromString(GattAttributes.CHARACTERISTIC_PRESENTATION_FORMAT);

    /**
     * GATT related UUID
     */
    public final static UUID UUID_GENERIC_ACCESS_SERVICE = UUID
            .fromString(GattAttributes.GENERIC_ACCESS_SERVICE);
    public final static UUID UUID_GENERIC_ATTRIBUTE_SERVICE = UUID
            .fromString(GattAttributes.GENERIC_ATTRIBUTE_SERVICE);
    public final static UUID UUID_SERVICE_CHANGED = UUID
            .fromString(GattAttributes.SERVICE_CHANGED);

    /**
     * HID UUID
     */
    public final static UUID UUID_HID_SERVICE = UUID
            .fromString(GattAttributes.HUMAN_INTERFACE_DEVICE_SERVICE);
    public final static UUID UUID_PROTOCOL_MODE = UUID
            .fromString(GattAttributes.PROTOCOL_MODE);
    public final static UUID UUID_REPORT_MAP = UUID
            .fromString(GattAttributes.REPORT_MAP);
    public final static UUID UUID_BOOT_KEYBOARD_INPUT_REPORT = UUID
            .fromString(GattAttributes.BOOT_KEYBOARD_INPUT_REPORT);
    public final static UUID UUID_BOOT_KEYBOARD_OUTPUT_REPORT = UUID
            .fromString(GattAttributes.BOOT_KEYBOARD_OUTPUT_REPORT);
    public final static UUID UUID_BOOT_MOUSE_INPUT_REPORT = UUID
            .fromString(GattAttributes.BOOT_MOUSE_INPUT_REPORT);
    public final static UUID UUID_HID_CONTROL_POINT = UUID
            .fromString(GattAttributes.HID_CONTROL_POINT);
    public final static UUID UUID_HID_INFORMATION = UUID
            .fromString(GattAttributes.HID_INFORMATION);
    public final static UUID UUID_OTA_CHARACTERISTIC = UUID
            .fromString(GattAttributes.OTA_CHARACTERISTIC);

    /**
     * Alert Notification UUID
     */
    public final static UUID UUID_ALERT_NOTIFICATION_SERVICE = UUID
            .fromString(GattAttributes.ALERT_NOTIFICATION_SERVICE);
    /**
     * Unused Service UUID's
     */
    public final static UUID UUID_BODY_COMPOSITION_SERVICE = UUID
            .fromString(GattAttributes.BODY_COMPOSITION_SERVICE);
    public final static UUID UUID_BOND_MANAGEMENT_SERVICE = UUID
            .fromString(GattAttributes.BOND_MANAGEMENT_SERVICE);
    public final static UUID UUID_CONTINUOUS_GLUCOSE_MONITORING_SERVICE = UUID
            .fromString(GattAttributes.CONTINUOUS_GLUCOSE_MONITORING_SERVICE);
    public final static UUID UUID_CURRENT_TIME_SERVICE = UUID
            .fromString(GattAttributes.CURRENT_TIME_SERVICE);
    public final static UUID UUID_CYCLING_POWER_SERVICE = UUID
            .fromString(GattAttributes.CYCLING_POWER_SERVICE);
    public final static UUID UUID_ENVIRONMENTAL_SENSING_SERVICE = UUID
            .fromString(GattAttributes.ENVIRONMENTAL_SENSING_SERVICE);
    public final static UUID UUID_LOCATION_NAVIGATION_SERVICE = UUID
            .fromString(GattAttributes.LOCATION_NAVIGATION_SERVICE);
    public final static UUID UUID_NEXT_DST_CHANGE_SERVICE = UUID
            .fromString(GattAttributes.NEXT_DST_CHANGE_SERVICE);
    public final static UUID UUID_PHONE_ALERT_STATUS_SERVICE = UUID
            .fromString(GattAttributes.PHONE_ALERT_STATUS_SERVICE);
    public final static UUID UUID_REFERENCE_TIME_UPDATE_SERVICE = UUID
            .fromString(GattAttributes.REFERENCE_TIME_UPDATE_SERVICE);
    public final static UUID UUID_SCAN_PARAMETERS_SERVICE = UUID
            .fromString(GattAttributes.SCAN_PARAMETERS_SERVICE);
    public final static UUID UUID_USER_DATA_SERVICE = UUID
            .fromString(GattAttributes.USER_DATA_SERVICE);
    public final static UUID UUID_WEIGHT = UUID
            .fromString(GattAttributes.WEIGHT);
    public final static UUID UUID_WEIGHT_SCALE_SERVICE = UUID
            .fromString(GattAttributes.WEIGHT_SCALE_SERVICE);
    public final static UUID UUID_HEART_RATE_CONTROL_POINT = UUID
            .fromString(GattAttributes.HEART_RATE_CONTROL_POINT);

    /**
     * Unused Characteristic UUID's
     */
    public final static UUID UUID_AEROBIC_HEART_RATE_LOWER_LIMIT = UUID
            .fromString(GattAttributes.AEROBIC_HEART_RATE_LOWER_LIMIT);
    public final static UUID UUID_AEROBIC_HEART_RATE_UPPER_LIMIT = UUID
            .fromString(GattAttributes.AEROBIC_HEART_RATE_UPPER_LIMIT);
    public final static UUID UUID_AGE = UUID
            .fromString(GattAttributes.AGE);
    public final static UUID UUID_ALERT_CATEGORY_ID = UUID
            .fromString(GattAttributes.ALERT_CATEGORY_ID);
    public final static UUID UUID_ALERT_CATEGORY_ID_BIT_MASK = UUID
            .fromString(GattAttributes.ALERT_CATEGORY_ID_BIT_MASK);
    public final static UUID UUID_ALERT_STATUS = UUID
            .fromString(GattAttributes.ALERT_STATUS);
    public final static UUID UUID_ANAEROBIC_HEART_RATE_LOWER_LIMIT = UUID
            .fromString(GattAttributes.ANAEROBIC_HEART_RATE_LOWER_LIMIT);
    public final static UUID UUID_ANAEROBIC_HEART_RATE_UPPER_LIMIT = UUID
            .fromString(GattAttributes.ANAEROBIC_HEART_RATE_UPPER_LIMIT);
    public final static UUID UUID_ANAEROBIC_THRESHOLD = UUID
            .fromString(GattAttributes.ANAEROBIC_THRESHOLD);
    public final static UUID UUID_APPARENT_WIND_DIRECTION = UUID
            .fromString(GattAttributes.APPARENT_WIND_DIRECTION);
    public final static UUID UUID_APPARENT_WIND_SPEED = UUID
            .fromString(GattAttributes.APPARENT_WIND_SPEED);
    public final static UUID UUID_APPEARANCE = UUID
            .fromString(GattAttributes.APPEARANCE);
    public final static UUID UUID_BAROMETRIC_PRESSURE_TREND = UUID
            .fromString(GattAttributes.BAROMETRIC_PRESSURE_TREND);
    public final static UUID UUID_BODY_COMPOSITION_FEATURE = UUID
            .fromString(GattAttributes.BODY_COMPOSITION_FEATURE);
    public final static UUID UUID_BODY_COMPOSITION_MEASUREMENT = UUID
            .fromString(GattAttributes.BODY_COMPOSITION_MEASUREMENT);
    public final static UUID UUID_BOND_MANAGEMENT_CONTROL_POINT = UUID
            .fromString(GattAttributes.BOND_MANAGEMENT_CONTROL_POINT);
    public final static UUID UUID_BOND_MANAGEMENT_FEATURE = UUID
            .fromString(GattAttributes.BOND_MANAGEMENT_FEATURE);
    public final static UUID UUID_CGM_FEATURE = UUID
            .fromString(GattAttributes.CGM_FEATURE);
    public final static UUID UUID_CENTRAL_ADDRESS_RESOLUTION = UUID
            .fromString(GattAttributes.CENTRAL_ADDRESS_RESOLUTION);
    public final static UUID UUID_FIRSTNAME = UUID
            .fromString(GattAttributes.FIRSTNAME);
    public final static UUID UUID_GUST_FACTOR = UUID
            .fromString(GattAttributes.GUST_FACTOR);
    public final static UUID UUID_CGM_MEASUREMENT = UUID
            .fromString(GattAttributes.CGM_MEASUREMENT);
    public final static UUID UUID_CGM_SESSION_RUN_TIME = UUID
            .fromString(GattAttributes.CGM_SESSION_RUN_TIME);
    public final static UUID UUID_CGM_SESSION_START_TIME = UUID
            .fromString(GattAttributes.CGM_SESSION_START_TIME);
    public final static UUID UUID_CGM_SPECIFIC_OPS_CONTROL_POINT = UUID
            .fromString(GattAttributes.CGM_SPECIFIC_OPS_CONTROL_POINT);
    public final static UUID UUID_CGM_STATUS = UUID
            .fromString(GattAttributes.CGM_STATUS);
    public final static UUID UUID_CYCLING_POWER_CONTROL_POINT = UUID
            .fromString(GattAttributes.CYCLING_POWER_CONTROL_POINT);
    public final static UUID UUID_CYCLING_POWER_VECTOR = UUID
            .fromString(GattAttributes.CYCLING_POWER_VECTOR);
    public final static UUID UUID_CYCLING_POWER_FEATURE = UUID
            .fromString(GattAttributes.CYCLING_POWER_FEATURE);
    public final static UUID UUID_CYCLING_POWER_MEASUREMENT = UUID
            .fromString(GattAttributes.CYCLING_POWER_MEASUREMENT);
    public final static UUID UUID_DATABASE_CHANGE_INCREMENT = UUID
            .fromString(GattAttributes.DATABASE_CHANGE_INCREMENT);
    public final static UUID UUID_DATE_OF_BIRTH = UUID
            .fromString(GattAttributes.DATE_OF_BIRTH);
    public final static UUID UUID_DATE_OF_THRESHOLD_ASSESSMENT = UUID
            .fromString(GattAttributes.DATE_OF_THRESHOLD_ASSESSMENT);
    public final static UUID UUID_DATE_TIME = UUID
            .fromString(GattAttributes.DATE_TIME);
    public final static UUID UUID_DAY_DATE_TIME = UUID
            .fromString(GattAttributes.DAY_DATE_TIME);
    public final static UUID UUID_DAY_OF_WEEK = UUID
            .fromString(GattAttributes.DAY_OF_WEEK);
    public final static UUID UUID_DESCRIPTOR_VALUE_CHANGED = UUID
            .fromString(GattAttributes.DESCRIPTOR_VALUE_CHANGED);
    public final static UUID UUID_DEVICE_NAME = UUID
            .fromString(GattAttributes.DEVICE_NAME);
    public final static UUID UUID_DEW_POINT = UUID
            .fromString(GattAttributes.DEW_POINT);
    public final static UUID UUID_DST_OFFSET = UUID
            .fromString(GattAttributes.DST_OFFSET);
    public final static UUID UUID_ELEVATION = UUID
            .fromString(GattAttributes.ELEVATION);
    public final static UUID UUID_EMAIL_ADDRESS = UUID
            .fromString(GattAttributes.EMAIL_ADDRESS);
    public final static UUID UUID_EXACT_TIME_256 = UUID
            .fromString(GattAttributes.EXACT_TIME_256);
    public final static UUID UUID_FAT_BURN_HEART_RATE_LOWER_LIMIT = UUID
            .fromString(GattAttributes.FAT_BURN_HEART_RATE_LOWER_LIMIT);
    public final static UUID UUID_FAT_BURN_HEART_RATE_UPPER_LIMIT = UUID
            .fromString(GattAttributes.FAT_BURN_HEART_RATE_UPPER_LIMIT);
    public final static UUID UUID_FIVE_ZONE_HEART_RATE_LIMITS = UUID
            .fromString(GattAttributes.FIVE_ZONE_HEART_RATE_LIMITS);
    public final static UUID UUID_GENDER = UUID
            .fromString(GattAttributes.GENDER);
    public final static UUID UUID_HEART_RATE_MAX = UUID
            .fromString(GattAttributes.HEART_RATE_MAX);
    public final static UUID UUID_HEAT_INDEX = UUID
            .fromString(GattAttributes.HEAT_INDEX);
    public final static UUID UUID_HEIGHT = UUID
            .fromString(GattAttributes.HEIGHT);
    public final static UUID UUID_HIP_CIRCUMFERENCE = UUID
            .fromString(GattAttributes.HIP_CIRCUMFERENCE);
    public final static UUID UUID_HUMIDITY = UUID
            .fromString(GattAttributes.HUMIDITY);
    public final static UUID UUID_INTERMEDIATE_CUFF_PRESSURE = UUID
            .fromString(GattAttributes.INTERMEDIATE_CUFF_PRESSURE);
    public final static UUID UUID_IRRADIANCE = UUID
            .fromString(GattAttributes.IRRADIANCE);
    public final static UUID UUID_LANGUAGE = UUID
            .fromString(GattAttributes.LANGUAGE);
    public final static UUID UUID_LAST_NAME = UUID
            .fromString(GattAttributes.LAST_NAME);
    public final static UUID UUID_LN_CONTROL_POINT = UUID
            .fromString(GattAttributes.LN_CONTROL_POINT);
    public final static UUID UUID_LN_FEATURE = UUID
            .fromString(GattAttributes.LN_FEATURE);
    public final static UUID UUID_LOCAL_TIME_INFORMATION = UUID
            .fromString(GattAttributes.LOCAL_TIME_INFORMATION);
    public final static UUID UUID_LOCATION_AND_SPEED = UUID
            .fromString(GattAttributes.LOCATION_AND_SPEED);
    public final static UUID UUID_MAGNETIC_DECLINATION = UUID
            .fromString(GattAttributes.MAGNETIC_DECLINATION);
    public final static UUID UUID_MAGNETIC_FLUX_DENSITY_2D = UUID
            .fromString(GattAttributes.MAGNETIC_FLUX_DENSITY_2D);
    public final static UUID UUID_MAGNETIC_FLUX_DENSITY_3D = UUID
            .fromString(GattAttributes.MAGNETIC_FLUX_DENSITY_3D);
    public final static UUID UUID_MAXIMUM_RECOMMENDED_HEART_RATE = UUID
            .fromString(GattAttributes.MAXIMUM_RECOMMENDED_HEART_RATE);
    public final static UUID UUID_NEW_ALERT = UUID
            .fromString(GattAttributes.NEW_ALERT);
    public final static UUID UUID_NAVIGATION = UUID
            .fromString(GattAttributes.NAVIGATION);
    public final static UUID UUID_PERIPHERAL_PREFERRED_CONNECTION_PARAMETERS = UUID
            .fromString(GattAttributes.PERIPHERAL_PREFERRED_CONNECTION_PARAMETERS);
    public final static UUID UUID_PERIPHERAL_PRIVACY_FLAG = UUID
            .fromString(GattAttributes.PERIPHERAL_PRIVACY_FLAG);
    public final static UUID UUID_POLLEN_CONCENTRATION = UUID
            .fromString(GattAttributes.POLLEN_CONCENTRATION);
    public final static UUID UUID_POSITION_QUALITY = UUID
            .fromString(GattAttributes.POSITION_QUALITY);
    public final static UUID UUID_PRESSURE = UUID
            .fromString(GattAttributes.PRESSURE);
    public final static UUID UUID_TEMPERATURE = UUID
            .fromString(GattAttributes.TEMPERATURE);
    public final static UUID UUID_UV_INDEX = UUID
            .fromString(GattAttributes.UV_INDEX);

    // Descriptors UUID's
    public final static UUID UUID_CHARACTERISTIC_AGGREGATE_FORMAT = UUID
            .fromString(GattAttributes.CHARACTERISTIC_AGGREGATE_FORMAT);
    public final static UUID UUID_VALID_RANGE = UUID
            .fromString(GattAttributes.VALID_RANGE);
    public final static UUID UUID_EXTERNAL_REPORT_REFERENCE = UUID
            .fromString(GattAttributes.EXTERNAL_REPORT_REFERENCE);
    public final static UUID UUID_ENVIRONMENTAL_SENSING_CONFIGURATION = UUID
            .fromString(GattAttributes.ENVIRONMENTAL_SENSING_CONFIGURATION);
    public final static UUID UUID_ENVIRONMENTAL_SENSING_MEASUREMENT = UUID
            .fromString(GattAttributes.ENVIRONMENTAL_SENSING_MEASUREMENT);
    public final static UUID UUID_ENVIRONMENTAL_SENSING_TRIGGER_SETTING = UUID
            .fromString(GattAttributes.ENVIRONMENTAL_SENSING_TRIGGER_SETTING);

    // Wearable Solution Demo
    public static final UUID UUID_WEARABLE_DEMO_SERVICE = UUID
            .fromString(GattAttributes.WEARABLE_DEMO_SERVICE);
    public static final UUID UUID_WEARABLE_MOTION_SERVICE = UUID
            .fromString(GattAttributes.WEARABLE_MOTION_SERVICE);
    public static final UUID UUID_WEARABLE_MOTION_FEATURE_CHARACTERISTIC = UUID
            .fromString(GattAttributes.WEARABLE_MOTION_FEATURE_CHARACTERISTIC);
    public static final UUID UUID_WEARABLE_MOTION_DATA_CHARACTERISTIC = UUID
            .fromString(GattAttributes.WEARABLE_MOTION_DATA_CHARACTERISTIC);
    public static final UUID UUID_WEARABLE_MOTION_CONTROL_CHARACTERISTIC = UUID
            .fromString(GattAttributes.WEARABLE_MOTION_CONTROL_CHARACTERISTIC);
    public static final UUID UUID_WEARABLE_MOTION_LIFETIME_STEPS_CHARACTERISTIC = UUID
            .fromString(GattAttributes.WEARABLE_MOTION_LIFETIME_STEPS_CHARACTERISTIC);
    public static final UUID UUID_WEARABLE_MOTION_STEPS_GOAL_CHARACTERISTIC = UUID
            .fromString(GattAttributes.WEARABLE_MOTION_STEPS_GOAL_CHARACTERISTIC);
    public static final UUID UUID_WEARABLE_MOTION_CALORIES_GOAL_CHARACTERISTIC = UUID
            .fromString(GattAttributes.WEARABLE_MOTION_CALORIES_GOAL_CHARACTERISTIC);
    public static final UUID UUID_WEARABLE_MOTION_FITNESS_TRACKER_CMD_CHARACTERISTIC = UUID
            .fromString(GattAttributes.WEARABLE_MOTION_FITNESS_TRACKER_CMD_CHARACTERISTIC);
    public static final UUID UUID_WEARABLE_MOTION_DURATION_GOAL_CHARACTERISTIC = UUID
            .fromString(GattAttributes.WEARABLE_MOTION_DURATION_GOAL_CHARACTERISTIC);
    public static final UUID UUID_WEARABLE_MOTION_DISTANCE_GOAL_CHARACTERISTIC = UUID
            .fromString(GattAttributes.WEARABLE_MOTION_DISTANCE_GOAL_CHARACTERISTIC);
}
