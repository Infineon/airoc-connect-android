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

package com.infineon.airocbluetoothconnect.CommonUtils;

import com.infineon.airocbluetoothconnect.R;

import java.util.HashMap;
import java.util.UUID;

/**
 * This class includes a subset of standard GATT attributes and carousel image
 * mapping
 */
public class GattAttributes {

    public static final HashMap<UUID, Integer> attributesImageMap = new HashMap<>();
    public static final HashMap<UUID, Integer> attributesCapSenseImageMap = new HashMap<>();
    public static final HashMap<UUID, String> attributesCapSense = new HashMap<>();
    private static final HashMap<String, String> descriptorAttributes = new HashMap<>();
    private static final HashMap<UUID, String> attributesUUID = new HashMap<>();
    /**
     * Services
     */
    public static final String HEART_RATE_SERVICE = "0000180d-0000-1000-8000-00805f9b34fb";
    public static final String DEVICE_INFORMATION_SERVICE = "0000180a-0000-1000-8000-00805f9b34fb";
    public static final String HEALTH_THERMOMETER_SERVICE = "00001809-0000-1000-8000-00805f9b34fb";
    public static final String BATTERY_SERVICE = "0000180f-0000-1000-8000-00805f9b34fb";
    public static final String IMMEDIATE_ALERT_SERVICE = "00001802-0000-1000-8000-00805f9b34fb";
    public static final String CAPSENSE_SERVICE = "0000cab5-0000-1000-8000-00805f9b34fb";
    public static final String CAPSENSE_SERVICE_CUSTOM = "0003cab5-0000-1000-8000-00805f9b0131";
    public static final String RGB_LED_SERVICE = "0000cbbb-0000-1000-8000-00805f9b34fb";
    public static final String RGB_LED_SERVICE_CUSTOM = "0003cbbb-0000-1000-8000-00805f9b0131";
    public static final String LINK_LOSS_SERVICE = "00001803-0000-1000-8000-00805f9b34fb";
    public static final String TRANSMISSION_POWER_SERVICE = "00001804-0000-1000-8000-00805f9b34fb";
    public static final String BLOOD_PRESSURE_SERVICE = "00001810-0000-1000-8000-00805f9b34fb";
    public static final String GLUCOSE_SERVICE = "00001808-0000-1000-8000-00805f9b34fb";
    public static final String RSC_SERVICE = "00001814-0000-1000-8000-00805f9b34fb";
    public static final String BAROMETER_SERVICE = "00040001-0000-1000-8000-00805f9b0131";
    public static final String ACCELEROMETER_SERVICE = "00040020-0000-1000-8000-00805f9b0131";
    public static final String ANALOG_TEMPERATURE_SERVICE = "00040030-0000-1000-8000-00805f9b0131";
    public static final String CSC_SERVICE = "00001816-0000-1000-8000-00805f9b34fb";
    public static final String HUMAN_INTERFACE_DEVICE_SERVICE = "00001812-0000-1000-8000-00805f9b34fb";
    public static final String SCAN_PARAMETERS_SERVICE = "00001813-0000-1000-8000-00805f9b34fb";
    public static final String OTA_UPDATE_SERVICE = "00060000-f8ce-11e4-abf4-0002a5d5c51b";
    /**
     * Unused service UUIDS
     */
    public static final String ALERT_NOTIFICATION_SERVICE = "00001811-0000-1000-8000-00805f9b34fb";
    public static final String BODY_COMPOSITION_SERVICE = "0000181b-0000-1000-8000-00805f9b34fb";
    public static final String BODY_MANAGEMENT_SERVICE = "0000181e-0000-1000-8000-00805f9b34fb";
    public static final String CONTINUOUS_GLUCOSE_MONITORING_SERVICE = "0000181f-0000-1000-8000-00805f9b34fb";
    public static final String CURRENT_TIME_SERVICE = "00001805-0000-1000-8000-00805f9b34fb";
    public static final String CYCLING_POWER_SERVICE = "00001818-0000-1000-8000-00805f9b34fb";
    public static final String ENVIRONMENTAL_SENSING_SERVICE = "0000181a-0000-1000-8000-00805f9b34fb";
    public static final String LOCATION_NAVIGATION_SERVICE = "00001819-0000-1000-8000-00805f9b34fb";
    public static final String NEXT_DST_CHANGE_SERVICE = "00001807-0000-1000-8000-00805f9b34fb";
    public static final String PHONE_ALERT_STATUS_SERVICE = "0000180e-0000-1000-8000-00805f9b34fb";
    public static final String REFERENCE_TIME_UPDATE_SERVICE = "00001806-0000-1000-8000-00805f9b34fb";
    public static final String USER_DATA_SERVICE = "0000181c-0000-1000-8000-00805f9b34fb";
    public static final String WEIGHT_SCALE_SERVICE = "0000181d-0000-1000-8000-00805f9b34fb";
    /**
     * Heart rate characteristics
     */
    public static final String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    public static final String BODY_SENSOR_LOCATION = "00002a38-0000-1000-8000-00805f9b34fb";
    /**
     * Device information characteristics
     */
    public static final String MANUFACTURER_NAME = "00002a29-0000-1000-8000-00805f9b34fb";
    public static final String MODEL_NUMBER = "00002a24-0000-1000-8000-00805f9b34fb";
    public static final String SERIAL_NUMBER = "00002a25-0000-1000-8000-00805f9b34fb";
    public static final String HARDWARE_REVISION = "00002a27-0000-1000-8000-00805f9b34fb";
    public static final String FIRMWARE_REVISION = "00002a26-0000-1000-8000-00805f9b34fb";
    public static final String SOFTWARE_REVISION = "00002a28-0000-1000-8000-00805f9b34fb";
    public static final String SYSTEM_ID = "00002a23-0000-1000-8000-00805f9b34fb";
    public static final String REGULATORY_CERTIFICATION_DATA_LIST = "00002a2a-0000-1000-8000-00805f9b34fb";
    public static final String UUID_PNP_ID = "00002a50-0000-1000-8000-00805f9b34fb";
    /**
     * Battery characteristics
     */
    public static final String BATTERY_LEVEL = "00002a19-0000-1000-8000-00805f9b34fb";
    /**
     * Health Thermometer characteristics
     */
    public static final String TEMPERATURE_MEASUREMENT = "00002a1c-0000-1000-8000-00805f9b34fb";
    public static final String TEMPERATURE_TYPE = "00002a1d-0000-1000-8000-00805f9b34fb";
    public static final String INTERMEDIATE_TEMPERATURE = "00002a1e-0000-1000-8000-00805f9b34fb";

    public static final String MEASUREMENT_INTERVAL = "00002a21-0000-1000-8000-00805f9b34fb";

    /**
     * Gatt services
     */
    public static final String GENERIC_ACCESS_SERVICE = "00001800-0000-1000-8000-00805f9b34fb";
    public static final String GENERIC_ATTRIBUTE_SERVICE = "00001801-0000-1000-8000-00805f9b34fb";
    /**
     * Find me characteristics
     */
    public static final String ALERT_LEVEL = "00002a06-0000-1000-8000-00805f9b34fb";
    public static final String TX_POWER_LEVEL = "00002a07-0000-1000-8000-00805f9b34fb";
    /**
     * Capsense characteristics
     */
    public static final String CAPSENSE_PROXIMITY = "0000caa1-0000-1000-8000-00805f9b34fb";
    public static final String CAPSENSE_SLIDER = "0000caa2-0000-1000-8000-00805f9b34fb";
    public static final String CAPSENSE_BUTTONS = "0000caa3-0000-1000-8000-00805f9b34fb";
    public static final String CAPSENSE_PROXIMITY_CUSTOM = "0003caa1-0000-1000-8000-00805f9b0131";
    public static final String CAPSENSE_SLIDER_CUSTOM = "0003caa2-0000-1000-8000-00805f9b0131";
    public static final String CAPSENSE_BUTTONS_CUSTOM = "0003caa3-0000-1000-8000-00805f9b0131";
    /**
     * RGB characteristics
     */
    public static final String RGB_LED = "0000cbb1-0000-1000-8000-00805f9b34fb";
    public static final String RGB_LED_CUSTOM = "0003cbb1-0000-1000-8000-00805f9b0131";
    /**
     * Glucose Measurement characteristics
     */
    public static final String GLUCOSE_MEASUREMENT = "00002a18-0000-1000-8000-00805f9b34fb";
    /**
     * Blood Pressure service Characteristics
     */
    public static final String BLOOD_PRESSURE_MEASUREMENT = "00002a35-0000-1000-8000-00805f9b34fb";
    /**
     * Running Speed & Cadence Characteristics
     */
    public static final String RSC_MEASUREMENT = "00002a53-0000-1000-8000-00805f9b34fb";
    /**
     * Cycling Speed & Cadence Characteristics
     */
    public static final String CSC_MEASUREMENT = "00002a5b-0000-1000-8000-00805f9b34fb";
    /**
     * Barometer service characteristics
     */
    public static final String BAROMETER_DIGITAL_SENSOR = "00040002-0000-1000-8000-00805f9b0131";
    public static final String BAROMETER_SENSOR_SCAN_INTERVAL = "00040004-0000-1000-8000-00805f9b0131";
    public static final String BAROMETER_DATA_ACCUMULATION = "00040007-0000-1000-8000-00805f9b0131";
    public static final String BAROMETER_READING = "00040009-0000-1000-8000-00805f9b0131";
    public static final String BAROMETER_THRESHOLD_FOR_INDICATION = "0004000d-0000-1000-8000-00805f9b0131";
    /**
     * Accelerometer service characteristics
     */
    public static final String ACCELEROMETER_ANALOG_SENSOR = "00040021-0000-1000-8000-00805f9b0131";
    public static final String ACCELEROMETER_SENSOR_SCAN_INTERVAL = "00040023-0000-1000-8000-00805f9b0131";
    public static final String ACCELEROMETER_DATA_ACCUMULATION = "00040026-0000-1000-8000-00805f9b0131";
    public static final String ACCELEROMETER_READING_X = "00040028-0000-1000-8000-00805f9b0131";
    public static final String ACCELEROMETER_READING_Y = "0004002b-0000-1000-8000-00805f9b0131";
    public static final String ACCELEROMETER_READING_Z = "0004002d-0000-1000-8000-00805f9b0131";
    /**
     * Analog Temperature service characteristics
     */
    public static final String TEMPERATURE_ANALOG_SENSOR = "00040031-0000-1000-8000-00805f9b0131";
    public static final String TEMPERATURE_SENSOR_SCAN_INTERVAL = "00040032-0000-1000-8000-00805f9b0131";
    public static final String TEMPERATURE_READING = "00040033-0000-1000-8000-00805f9b0131";
    /**
     * HID Characteristics
     */
    public static final String PROTOCOL_MODE = "00002a4e-0000-1000-8000-00805f9b34fb";
    public static final String REPORT = "00002a4d-0000-1000-8000-00805f9b34fb";
    public static final String REPORT_MAP = "00002a4b-0000-1000-8000-00805f9b34fb";
    public static final String BOOT_KEYBOARD_INPUT_REPORT = "00002a22-0000-1000-8000-00805f9b34fb";
    public static final String BOOT_KEYBOARD_OUTPUT_REPORT = "00002a32-0000-1000-8000-00805f9b34fb";
    public static final String BOOT_MOUSE_INPUT_REPORT = "00002a33-0000-1000-8000-00805f9b34fb";
    public static final String HID_CONTROL_POINT = "00002a4c-0000-1000-8000-00805f9b34fb";
    public static final String HID_INFORMATION = "00002a4a-0000-1000-8000-00805f9b34fb";
    /**
     * OTA Characteristic
     */
    public static final String OTA_CHARACTERISTIC = "00060001-f8ce-11e4-abf4-0002a5d5c51b";
    /**
     * Descriptor UUID's
     */
    public static final String CHARACTERISTIC_EXTENDED_PROPERTIES = "00002900-0000-1000-8000-00805f9b34fb";
    public static final String CHARACTERISTIC_USER_DESCRIPTION = "00002901-0000-1000-8000-00805f9b34fb";
    public static final String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    public static final String SERVER_CHARACTERISTIC_CONFIGURATION = "00002903-0000-1000-8000-00805f9b34fb";
    public static final String CHARACTERISTIC_PRESENTATION_FORMAT = "00002904-0000-1000-8000-00805f9b34fb";
    public static final String CHARACTERISTIC_AGGREGATE_FORMAT = "00002905-0000-1000-8000-00805f9b34fb";
    public static final String VALID_RANGE = "00002906-0000-1000-8000-00805f9b34fb";
    public static final String EXTERNAL_REPORT_REFERENCE = "00002907-0000-1000-8000-00805f9b34fb";
    public static final String REPORT_REFERENCE = "00002908-0000-1000-8000-00805f9b34fb";
    public static final String ENVIRONMENTAL_SENSING_CONFIGURATION = "0000290B-0000-1000-8000-00805f9b34fb";
    public static final String ENVIRONMENTAL_SENSING_MEASUREMENT = "0000290C-0000-1000-8000-00805f9b34fb";
    public static final String ENVIRONMENTAL_SENSING_TRIGGER_SETTING = "0000290D-0000-1000-8000-00805f9b34fb";
    public static final String BOND_MANAGEMENT_SERVICE = "0000181e-0000-1000-8000-00805f9b34fb";
    public static final String HEART_RATE_CONTROL_POINT = "00002a39-0000-1000-8000-00805f9b34fb";
    public static final String GLUCOSE_MEASUREMENT_CONTEXT = "00002a34-0000-1000-8000-00805f9b34fb";
    public static final String GLUCOSE_FEATURE = "00002a51-0000-1000-8000-00805f9b34fb";
    public static final String RECORD_ACCESS_CONTROL_POINT = "00002a52-0000-1000-8000-00805f9b34fb";
    public static final String BLOOD_INTERMEDIATE_CUFF_PRESSURE = "00002a36-0000-1000-8000-00805f9b34fb";
    public static final String BLOOD_PRESSURE_FEATURE = "00002a49-0000-1000-8000-00805f9b34fb";
    public static final String RSC_FEATURE = "00002a54-0000-1000-8000-00805f9b34fb";
    public static final String SC_SENSOR_LOCATION = "00002a5d-0000-1000-8000-00805f9b34fb";
    public static final String SC_CONTROL_POINT = "00002a55-0000-1000-8000-00805f9b34fb";
    public static final String CSC_FEATURE = "00002a5c-0000-1000-8000-00805f9b34fb";
    /**
     * Unused Service characteristics
     */
    public static final String AEROBIC_HEART_RATE_LOWER_LIMIT = "00002a7e-0000-1000-8000-00805f9b34fb";
    public static final String AEROBIC_HEART_RATE_UPPER_LIMIT = "00002a84-0000-1000-8000-00805f9b34fb";
    public static final String AEROBIC_THRESHOLD = "00002a7f-0000-1000-8000-00805f9b34fb";
    public static final String AGE = "00002a80-0000-1000-8000-00805f9b34fb";
    public static final String ALERT_CATEGORY_ID = "00002a43-0000-1000-8000-00805f9b34fb";
    public static final String ALERT_CATEGORY_ID_BIT_MASK = "00002a42-0000-1000-8000-00805f9b34fb";
    public static final String ALERT_STATUS = "00002a3F-0000-1000-8000-00805f9b34fb";
    public static final String ANAEROBIC_HEART_RATE_LOWER_LIMIT = "00002a81-0000-1000-8000-00805f9b34fb";
    public static final String ANAEROBIC_HEART_RATE_UPPER_LIMIT = "00002a82-0000-1000-8000-00805f9b34fb";
    public static final String ANAEROBIC_THRESHOLD = "00002aA83-0000-1000-8000-00805f9b34fb";
    public static final String APPARENT_WIND_DIRECTION = "00002a73-0000-1000-8000-00805f9b34fb";
    public static final String APPARENT_WIND_SPEED = "00002a72-0000-1000-8000-00805f9b34fb";
    public static final String APPEARANCE = "00002a01-0000-1000-8000-00805f9b34fb";
    public static final String BAROMETRIC_PRESSURE_TREND = "00002aa3-0000-1000-8000-00805f9b34fb";
    public static final String BODY_COMPOSITION_FEATURE = "00002a9B-0000-1000-8000-00805f9b34fb";
    public static final String BODY_COMPOSITION_MEASUREMENT = "00002a9C-0000-1000-8000-00805f9b34fb";
    public static final String BOND_MANAGEMENT_CONTROL_POINT = "00002aa4-0000-1000-8000-00805f9b34fb";
    public static final String BOND_MANAGEMENT_FEATURE = "00002aa5-0000-1000-8000-00805f9b34fb";
    public static final String CENTRAL_ADDRESS_RESOLUTION = "00002aa6-0000-1000-8000-00805f9b34fb";
    public static final String CGM_FEATURE = "00002aa8-0000-1000-8000-00805f9b34fb";
    public static final String CGM_MEASUREMENT = "00002aa7-0000-1000-8000-00805f9b34fb";
    public static final String CGM_SESSION_RUN_TIME = "00002aab-0000-1000-8000-00805f9b34fb";
    public static final String CGM_SESSION_START_TIME = "00002aaa-0000-1000-8000-00805f9b34fb";
    public static final String CGM_SPECIFIC_OPS_CONTROL_POINT = "00002aaC-0000-1000-8000-00805f9b34fb";
    public static final String CGM_STATUS = "00002aa9-0000-1000-8000-00805f9b34fb";
    public static final String CYCLING_POWER_CONTROL_POINT = "00002a66-0000-1000-8000-00805f9b34fb";
    public static final String CYCLING_POWER_FEATURE = "00002a65-0000-1000-8000-00805f9b34fb";
    public static final String CYCLING_POWER_MEASUREMENT = "00002a63-0000-1000-8000-00805f9b34fb";
    public static final String CYCLING_POWER_VECTOR = "00002a64-0000-1000-8000-00805f9b34fb";
    public static final String DATABASE_CHANGE_INCREMENT = "00002a99-0000-1000-8000-00805f9b34fb";
    public static final String DATE_OF_BIRTH = "00002a85-0000-1000-8000-00805f9b0131";
    public static final String DATE_OF_THRESHOLD_ASSESSMENT = "00002a86-0000-1000-8000-00805f9b0131";
    public static final String DATE_TIME = "00002a08-0000-1000-8000-00805f9b34fb";
    public static final String DAY_DATE_TIME = "00002a0a-0000-1000-8000-00805f9b34fb";
    public static final String DAY_OF_WEEK = "00002A09-0000-1000-8000-00805f9b34fb";
    public static final String DESCRIPTOR_VALUE_CHANGED = "00002a7d-0000-1000-8000-00805f9b34fb";
    public static final String DEVICE_NAME = "00002a00-0000-1000-8000-00805f9b34fb";
    public static final String DEW_POINT = "00002a7b-0000-1000-8000-00805f9b34fb";
    public static final String DST_OFFSET = "00002a0d-0000-1000-8000-00805f9b34fb";
    public static final String ELEVATION = "00002a6c-0000-1000-8000-00805f9b34fb";
    public static final String EMAIL_ADDRESS = "00002a87-0000-1000-8000-00805f9b34fb";
    public static final String EXACT_TIME_256 = "00002a0c-0000-1000-8000-00805f9b34fb";
    public static final String FAT_BURN_HEART_RATE_LOWER_LIMIT = "00002a88-0000-1000-8000-00805f9b34fb";
    public static final String FAT_BURN_HEART_RATE_UPPER_LIMIT = "00002a89-0000-1000-8000-00805f9b34fb";
    public static final String FIRSTNAME = "00002a8a-0000-1000-8000-00805f9b34fb";
    public static final String FIVE_ZONE_HEART_RATE_LIMITS = "00002A8b-0000-1000-8000-00805f9b34fb";
    public static final String GENDER = "00002a8c-0000-1000-8000-00805f9b34fb";
    public static final String GUST_FACTOR = "00002a74-0000-1000-8000-00805f9b34fb";
    public static final String HEAT_INDEX = "00002a89-0000-1000-8000-00805f9b34fb";
    public static final String HEIGHT = "00002a8e-0000-1000-8000-00805f9b34fb";
    public static final String HEART_RATE_MAX = "00002a8d-0000-1000-8000-00805f9b34fb";
    public static final String HIP_CIRCUMFERENCE = "00002a8f-0000-1000-8000-00805f9b34fb";
    public static final String HUMIDITY = "00002a6f-0000-1000-8000-00805f9b34fb";
    public static final String INTERMEDIATE_CUFF_PRESSURE = "00002a36-0000-1000-8000-00805f9b34fb";
    public static final String IRRADIANCE = "00002a77-0000-1000-8000-00805f9b34fb";
    public static final String LANGUAGE = "00002aa2-0000-1000-8000-00805f9b34fb";
    public static final String LAST_NAME = "00002a90-0000-1000-8000-00805f9b34fb";
    public static final String LN_CONTROL_POINT = "00002a6b-0000-1000-8000-00805f9b34fb";
    public static final String LN_FEATURE = "00002a6a-0000-1000-8000-00805f9b34fb";
    public static final String LOCAL_TIME_INFORMATION = "00002a0f-0000-1000-8000-00805f9b34fb";
    public static final String LOCATION_AND_SPEED = "00002a67-0000-1000-8000-00805f9b34fb";
    public static final String MAGNETIC_DECLINATION = "00002a2c-0000-1000-8000-00805f9b34fb";
    public static final String MAGNETIC_FLUX_DENSITY_2D = "00002aa0-0000-1000-8000-00805f9b34fb";
    public static final String MAGNETIC_FLUX_DENSITY_3D = "00002aa1-0000-1000-8000-00805f9b34fb";
    public static final String MANUFACTURE_NAME_STRING = "00002a29-0000-1000-8000-00805f9b34fb";
    public static final String MAXIMUM_RECOMMENDED_HEART_RATE = "00002a91-0000-1000-8000-00805f9b34fb";
    public static final String NAVIGATION = "00002a68-0000-1000-8000-00805f9b34fb";
    public static final String NEW_ALERT = "00002a46-0000-1000-8000-00805f9b34fb";
    public static final String PERIPHERAL_PREFERRED_CONNECTION_PARAMETERS = "00002a04-0000-1000-8000-00805f9b34fb";
    public static final String PERIPHERAL_PRIVACY_FLAG = "00002a02-0000-1000-8000-00805f9b34fb";
    public static final String POLLEN_CONCENTRATION = "00002a75-0000-1000-8000-00805f9b34fb";
    public static final String POSITION_QUALITY = "00002a69-0000-1000-8000-00805f9b34fb";
    public static final String PRESSURE = "00002a6d-0000-1000-8000-00805f9b34fb";
    public static final String RAINFALL = "00002a78-0000-1000-8000-00805f9b34fb";
    public static final String RECONNECTION_ADDRESS = "00002a03-0000-1000-8000-00805f9b34fb";
    public static final String REFERNCE_TIME_INFORMATION = "00002a14-0000-1000-8000-00805f9b34fb";
    public static final String RESTING_HEART_RATE = "00002a92-0000-1000-8000-00805f9b34fb";
    public static final String RINGER_CONTROL_POINT = "00002a40-0000-1000-8000-00805f9b34fb";
    public static final String RINGER_SETTING = "00002a41-0000-1000-8000-00805f9b34fb";
    public static final String SCAN_INTERVAL_WINDOW = "00002a4F-0000-1000-8000-00805f9b34fb";
    public static final String SENSOR_LOCATION = "00002a5d-0000-1000-8000-00805f9b34fb";
    public static final String SERVICE_CHANGED = "00002a05-0000-1000-8000-00805f9b34fb";
    public static final String SPORT_TYPE_FOR_AEROBIN_AND_ANAEROBIC_THRESHOLDS = "00002a93-0000-1000-8000-00805f9b34fb";
    public static final String SUPPORTED_NEW_ALERT_CATEGORY = "00002a47-0000-1000-8000-00805f9b34fb";
    public static final String SUPPORTED_UNREAD_ALERT_CATEGORY = "00002a48-0000-1000-8000-00805f9b34fb";
    public static final String TEMPERATURE = "00002a6e-0000-1000-8000-00805f9b34fb";
    public static final String THREE_ZONE_HEART_RATE_LIMITS = "00002a94-0000-1000-8000-00805f9b34fb";
    public static final String TIME_ACCURACY = "00002a12-0000-1000-8000-00805f9b34fb";
    public static final String TIME_SOURCE = "00002a13-0000-1000-8000-00805f9b34fb";
    public static final String TIME_UPDATE_CONTROL_POINT = "00002a16-0000-1000-8000-00805f9b34fb";
    public static final String TIME_UPDATE_STATE = "00002a17-0000-1000-8000-00805f9b34fb";
    public static final String TIME_WITH_DST = "00002a11-0000-1000-8000-00805f9b34fb";
    public static final String TIME_ZONE = "00002a0e-0000-1000-8000-00805f9b34fb";
    public static final String TRUE_WIND_DIRECTION = "00002a71-0000-1000-8000-00805f9b34fb";
    public static final String TRUE_WIND_SPEED = "00002a70-0000-1000-8000-00805f9b34fb";
    public static final String TWO_ZONE_HEART_RATE = "00002a95-0000-1000-8000-00805f9b34fb";
    public static final String TX_POWER = "00002a07-0000-1000-8000-00805f9b34fb";
    public static final String UNCERTAINITY = "00002ab4-0000-1000-8000-00805f9b34fb";
    public static final String UNREAD_ALERT_STATUS = "00002a45-0000-1000-8000-00805f9b34fb";
    public static final String USER_CONTROL_POINT = "00002a9f-0000-1000-8000-00805f9b34fb";
    public static final String USER_INDEX = "00002a9a-0000-1000-8000-00805f9b34fb";
    public static final String UV_INDEX = "00002a76-0000-1000-8000-00805f9b34fb";
    public static final String VO2_MAX = "00002a96-0000-1000-8000-00805f9b34fb";
    public static final String WAIST_CIRCUMFERENCE = "00002a97-0000-1000-8000-00805f9b34fb";
    public static final String WEIGHT = "00002a98-0000-1000-8000-00805f9b34fb";
    public static final String WEIGHT_SCALE_FEATURE = "00002a9e-0000-1000-8000-00805f9b34fb";
    public static final String WIND_CHILL = "00002a7-0000-1000-8000-00805f9b34fb";

    static {
        // Services.
        attributesUUID.put(UUIDDatabase.UUID_HEART_RATE_SERVICE, "Heart Rate Service");
        attributesUUID.put(UUIDDatabase.UUID_HEALTH_THERMOMETER_SERVICE, "Health Thermometer Service");
        attributesUUID.put(UUIDDatabase.UUID_GENERIC_ACCESS_SERVICE, "Generic Access Service");
        attributesUUID.put(UUIDDatabase.UUID_GENERIC_ATTRIBUTE_SERVICE, "Generic Attribute Service");
        attributesUUID
                .put(UUIDDatabase.UUID_DEVICE_INFORMATION_SERVICE, "Device Information Service");
        attributesUUID.put(UUIDDatabase.UUID_BATTERY_SERVICE, "Battery Service");
        attributesUUID.put(UUIDDatabase.UUID_IMMEDIATE_ALERT_SERVICE, "Immediate Alert");
        attributesUUID.put(UUIDDatabase.UUID_LINK_LOSS_SERVICE, "Link Loss");
        attributesUUID.put(UUIDDatabase.UUID_TRANSMISSION_POWER_SERVICE, "Tx Power");
        attributesUUID.put(UUIDDatabase.UUID_CAPSENSE_SERVICE, "CAPSENSE™ Service");
        attributesUUID.put(UUIDDatabase.UUID_CAPSENSE_SERVICE_CUSTOM, "CAPSENSE™ Service");
        attributesUUID.put(UUIDDatabase.UUID_RGB_LED_SERVICE, "RGB LED Service");
        attributesUUID.put(UUIDDatabase.UUID_RGB_LED_SERVICE_CUSTOM, "RGB LED Service");
        attributesUUID.put(UUIDDatabase.UUID_GLUCOSE_SERVICE, "Glucose Service");
        attributesUUID.put(UUIDDatabase.UUID_BLOOD_PRESSURE_SERVICE, "Blood Pressure Service");
        attributesUUID.put(UUIDDatabase.UUID_RSC_SERVICE, "Running Speed & Cadence Service");
        attributesUUID.put(UUIDDatabase.UUID_BAROMETER_SERVICE, "Barometer Service");
        attributesUUID.put(UUIDDatabase.UUID_ACCELEROMETER_SERVICE, "Accelerometer Service");
        attributesUUID
                .put(UUIDDatabase.UUID_ANALOG_TEMPERATURE_SERVICE, "Analog Temperature Service");
        attributesUUID.put(UUIDDatabase.UUID_CSC_SERVICE, "Cycling Speed & Cadence Service");

        // Unused Services
        attributesUUID
                .put(UUIDDatabase.UUID_ALERT_NOTIFICATION_SERVICE, "Alert notification Service");
        attributesUUID.put(UUIDDatabase.UUID_BODY_COMPOSITION_SERVICE, "Body Composition Service");
        attributesUUID.put(UUIDDatabase.UUID_BOND_MANAGEMENT_SERVICE, "Bond Management Service");
        attributesUUID.put(UUIDDatabase.UUID_CONTINUOUS_GLUCOSE_MONITORING_SERVICE,
                "Continuous Glucose Monitoring Service");
        attributesUUID.put(UUIDDatabase.UUID_CURRENT_TIME_SERVICE, "Current Time Service");
        attributesUUID.put(UUIDDatabase.UUID_CYCLING_POWER_SERVICE, "Cycling Power Service");
        attributesUUID.put(UUIDDatabase.UUID_ENVIRONMENTAL_SENSING_SERVICE,
                "Environmental Sensing Service");
        attributesUUID.put(UUIDDatabase.UUID_HID_SERVICE,
                "Human Interface Device Service");
        attributesUUID.put(UUIDDatabase.UUID_LOCATION_NAVIGATION_SERVICE,
                "Location and Navigation Service");
        attributesUUID.put(UUIDDatabase.UUID_NEXT_DST_CHANGE_SERVICE, "Next DST Change Service");
        attributesUUID
                .put(UUIDDatabase.UUID_PHONE_ALERT_STATUS_SERVICE, "Phone Alert Status Service");
        attributesUUID.put(UUIDDatabase.UUID_REFERENCE_TIME_UPDATE_SERVICE,
                "Reference Time Update Service");
        attributesUUID.put(UUIDDatabase.UUID_SCAN_PARAMETERS_SERVICE, "Scan Paramenters Service");
        attributesUUID.put(UUIDDatabase.UUID_USER_DATA_SERVICE, "User Data Service");
        attributesUUID.put(UUIDDatabase.UUID_WEIGHT, "Weight");
        attributesUUID.put(UUIDDatabase.UUID_WEIGHT_SCALE_SERVICE, "Weight Scale Service");

        // Heart Rate Characteristics.
        attributesUUID.put(UUIDDatabase.UUID_HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
        attributesUUID.put(UUIDDatabase.UUID_BODY_SENSOR_LOCATION, "Body Sensor Location");
        attributesUUID.put(UUIDDatabase.UUID_HEART_RATE_CONTROL_POINT, "Heart Rate Control Point");

        // Health thermometer Characteristics.
        attributesUUID.put(UUIDDatabase.UUID_TEMPERATURE_MEASUREMENT, "Health Thermometer Measurement");
        attributesUUID.put(UUIDDatabase.UUID_TEMPERATURE_TYPE, "Temperature Type");
        attributesUUID.put(UUIDDatabase.UUID_INTERMEDIATE_TEMPERATURE, "Intermediate Temperature");
        attributesUUID.put(UUIDDatabase.UUID_MEASUREMENT_INTERVAL, "Measurement Interval");

        // Device Information Characteristics
        attributesUUID.put(UUIDDatabase.UUID_SYSTEM_ID, "System ID");
        attributesUUID.put(UUIDDatabase.UUID_MODEL_NUMBER, "Model Number String");
        attributesUUID.put(UUIDDatabase.UUID_SERIAL_NUMBER, "Serial Number String");
        attributesUUID.put(UUIDDatabase.UUID_FIRMWARE_REVISION, "Firmware Revision String");
        attributesUUID.put(UUIDDatabase.UUID_HARDWARE_REVISION, "Hardware Revision String");
        attributesUUID.put(UUIDDatabase.UUID_SOFTWARE_REVISION, "Software Revision String");
        attributesUUID.put(UUIDDatabase.UUID_MANUFACTURER_NAME, "Manufacturer Name String");
        attributesUUID.put(UUIDDatabase.UUID_PNP_ID, "PnP ID");
        attributesUUID.put(UUIDDatabase.UUID_REGULATORY_CERTIFICATION_DATA_LIST,
                "IEEE 11073-20601 Regulatory Certification Data List");

        // Battery service characteristics
        attributesUUID.put(UUIDDatabase.UUID_BATTERY_LEVEL, "Battery Level");

        // Find me service characteristics
        attributesUUID.put(UUIDDatabase.UUID_ALERT_LEVEL, "Alert Level");
        attributesUUID.put(UUIDDatabase.UUID_TRANSMISSION_POWER_LEVEL, "Tx Power Level");

        // Capsense Characteristics
        attributesUUID.put(UUIDDatabase.UUID_CAPSENSE_BUTTONS, "CAPSENSE™ Button");
        attributesUUID.put(UUIDDatabase.UUID_CAPSENSE_PROXIMITY, "CAPSENSE™ Proximity");
        attributesUUID.put(UUIDDatabase.UUID_CAPSENSE_SLIDER, "CAPSENSE™ Slider");
        attributesUUID.put(UUIDDatabase.UUID_CAPSENSE_BUTTONS_CUSTOM, "CAPSENSE™ Button");
        attributesUUID.put(UUIDDatabase.UUID_CAPSENSE_PROXIMITY_CUSTOM, "CAPSENSE™ Proximity");
        attributesUUID.put(UUIDDatabase.UUID_CAPSENSE_SLIDER_CUSTOM, "CAPSENSE™ Slider");

        // RGB Characteristics
        attributesUUID.put(UUIDDatabase.UUID_RGB_LED, "RGB LED");
        attributesUUID.put(UUIDDatabase.UUID_RGB_LED_CUSTOM, "RGB LED");

        // Glucose Characteristics
        attributesUUID.put(UUIDDatabase.UUID_GLUCOSE_MEASUREMENT, "Glucose Measurement");
        attributesUUID.put(UUIDDatabase.UUID_GLUCOSE_MEASUREMENT_CONTEXT,
                "Glucose Measurement Context");
        attributesUUID.put(UUIDDatabase.UUID_GLUCOSE_FEATURE, "Glucose Feature");
        attributesUUID.put(UUIDDatabase.UUID_RECORD_ACCESS_CONTROL_POINT,
                "Record Access Control Point");

        // Blood pressure service characteristics
        attributesUUID.put(UUIDDatabase.UUID_BLOOD_INTERMEDIATE_CUFF_PRESSURE,
                "Intermediate Cuff Pressure");
        attributesUUID.put(UUIDDatabase.UUID_BLOOD_PRESSURE_FEATURE, "Blood Pressure Feature");
        attributesUUID
                .put(UUIDDatabase.UUID_BLOOD_PRESSURE_MEASUREMENT, "Blood Pressure Measurement");

        // Running Speed Characteristics
        attributesUUID.put(UUIDDatabase.UUID_RSC_MEASURE, "Running Speed and Cadence Measurement");
        attributesUUID.put(UUIDDatabase.UUID_RSC_FEATURE, "Running Speed and Cadence Feature");
        attributesUUID.put(UUIDDatabase.UUID_SC_CONTROL_POINT, "Speed and Cadence Control Point");
        attributesUUID.put(UUIDDatabase.UUID_SC_SENSOR_LOCATION, "Speed and Cadence Sensor Location");

        // Cycling Speed Characteristics
        attributesUUID.put(UUIDDatabase.UUID_CSC_MEASURE, "Cycling Speed and Cadence Measurement");
        attributesUUID.put(UUIDDatabase.UUID_CSC_FEATURE, "Cycling Speed and Cadence Feature");


        // SensorHub Characteristics
        attributesUUID.put(UUIDDatabase.UUID_ACCELEROMETER_ANALOG_SENSOR,
                "Accelerometer Analog Sensor");
        attributesUUID.put(UUIDDatabase.UUID_ACCELEROMETER_DATA_ACCUMULATION,
                "Accelerometer Data Accumulation");
        attributesUUID.put(UUIDDatabase.UUID_ACCELEROMETER_READING_X, "Accelerometer X Reading");
        attributesUUID.put(UUIDDatabase.UUID_ACCELEROMETER_READING_Y, "Accelerometer Y Reading");
        attributesUUID.put(UUIDDatabase.UUID_ACCELEROMETER_READING_Z, "Accelerometer Z Reading");
        attributesUUID.put(UUIDDatabase.UUID_ACCELEROMETER_SENSOR_SCAN_INTERVAL,
                "Accelerometer Sensor Scan Interval");
        attributesUUID.put(UUIDDatabase.UUID_BAROMETER_DATA_ACCUMULATION,
                "Barometer Data Accumulation");
        attributesUUID.put(UUIDDatabase.UUID_BAROMETER_DIGITAL_SENSOR, "Barometer Digital Sensor");
        attributesUUID.put(UUIDDatabase.UUID_BAROMETER_READING, "Barometer Reading");
        attributesUUID.put(UUIDDatabase.UUID_BAROMETER_SENSOR_SCAN_INTERVAL,
                "Barometer Sensor Scan Interval");
        attributesUUID.put(UUIDDatabase.UUID_BAROMETER_THRESHOLD_FOR_INDICATION,
                "Barometer Threshold for Indication");
        attributesUUID.put(UUIDDatabase.UUID_TEMPERATURE_ANALOG_SENSOR, "Temperature Analog Sensor");
        attributesUUID.put(UUIDDatabase.UUID_TEMPERATURE_READING, "Temperature Reading");
        attributesUUID.put(UUIDDatabase.UUID_TEMPERATURE_SENSOR_SCAN_INTERVAL,
                "Temperature Sensor Scan Interval");

        //HID Characteristics
        attributesUUID.put(UUIDDatabase.UUID_PROTOCOL_MODE, "Protocol Mode");
        attributesUUID.put(UUIDDatabase.UUID_REPORT, "Report");
        attributesUUID.put(UUIDDatabase.UUID_REPORT_MAP, "Report Map");
        attributesUUID.put(UUIDDatabase.UUID_BOOT_KEYBOARD_INPUT_REPORT, "Boot Keyboard Input Report");
        attributesUUID.put(UUIDDatabase.UUID_BOOT_KEYBOARD_OUTPUT_REPORT, "Boot Keyboard Output Report");
        attributesUUID.put(UUIDDatabase.UUID_BOOT_MOUSE_INPUT_REPORT, "Boot Mouse Input Report");
        attributesUUID.put(UUIDDatabase.UUID_HID_CONTROL_POINT, "HID Control Point");
        attributesUUID.put(UUIDDatabase.UUID_HID_INFORMATION, "HID Information");

        //OTA Characteristics
        attributesUUID.put(UUIDDatabase.UUID_OTA_UPDATE_SERVICE, "Bootloader Service");
        attributesUUID.put(UUIDDatabase.UUID_OTA_CHARACTERISTIC, "Bootloader Data Characteristic");

        // Unused Characteristics
        attributesUUID.put(UUIDDatabase.UUID_AEROBIC_HEART_RATE_LOWER_LIMIT,
                "Aerobic Heart Rate Lower Limit");
        attributesUUID.put(UUIDDatabase.UUID_AEROBIC_HEART_RATE_UPPER_LIMIT,
                "Aerobic Heart Rate Upper Limit");
        attributesUUID.put(UUIDDatabase.UUID_AGE, "Age");
        attributesUUID.put(UUIDDatabase.UUID_ALERT_CATEGORY_ID, "Alert Category Id");
        attributesUUID
                .put(UUIDDatabase.UUID_ALERT_CATEGORY_ID_BIT_MASK, "Alert Category_id_Bit_Mask");
        attributesUUID.put(UUIDDatabase.UUID_ALERT_STATUS, "Alert_Status");
        attributesUUID.put(UUIDDatabase.UUID_ANAEROBIC_HEART_RATE_LOWER_LIMIT,
                "Anaerobic Heart Rate Lower Limit");
        attributesUUID.put(UUIDDatabase.UUID_ANAEROBIC_HEART_RATE_UPPER_LIMIT,
                "Anaerobic Heart Rate Upper Limit");
        attributesUUID.put(UUIDDatabase.UUID_ANAEROBIC_THRESHOLD, "Anaerobic Threshold");
        attributesUUID.put(UUIDDatabase.UUID_APPARENT_WIND_DIRECTION, "Apparent Wind Direction");
        attributesUUID.put(UUIDDatabase.UUID_APPARENT_WIND_SPEED, "Apparent Wind Speed");
        attributesUUID.put(UUIDDatabase.UUID_APPEARANCE, "Appearance");
        attributesUUID.put(UUIDDatabase.UUID_BAROMETRIC_PRESSURE_TREND, "Barometric pressure Trend");
        attributesUUID.put(UUIDDatabase.UUID_BODY_COMPOSITION_FEATURE, "Body Composition Feature");
        attributesUUID.put(UUIDDatabase.UUID_BODY_COMPOSITION_MEASUREMENT, "Body Composition Measurement");
        attributesUUID.put(UUIDDatabase.UUID_BOND_MANAGEMENT_CONTROL_POINT, "Bond Management Control Point");
        attributesUUID.put(UUIDDatabase.UUID_BOND_MANAGEMENT_FEATURE, "Bond Management feature");
        attributesUUID.put(UUIDDatabase.UUID_CGM_FEATURE, "CGM Feature");
        attributesUUID.put(UUIDDatabase.UUID_CENTRAL_ADDRESS_RESOLUTION, "Central Address Resolution");
        attributesUUID.put(UUIDDatabase.UUID_FIRSTNAME, "First Name");
        attributesUUID.put(UUIDDatabase.UUID_GUST_FACTOR, "Gust Factor");
        attributesUUID.put(UUIDDatabase.UUID_CGM_MEASUREMENT, "CGM Measurement");
        attributesUUID.put(UUIDDatabase.UUID_CGM_SESSION_RUN_TIME, "CGM Session Run Time");
        attributesUUID.put(UUIDDatabase.UUID_CGM_SESSION_START_TIME, "CGM Session Start Time");
        attributesUUID.put(UUIDDatabase.UUID_CGM_SPECIFIC_OPS_CONTROL_POINT, "CGM Specific Ops Control Point");
        attributesUUID.put(UUIDDatabase.UUID_CGM_STATUS, "CGM Status");
        attributesUUID.put(UUIDDatabase.UUID_CYCLING_POWER_CONTROL_POINT, "Cycling Power Control Point");
        attributesUUID.put(UUIDDatabase.UUID_CYCLING_POWER_VECTOR, "Cycling Power Vector");
        attributesUUID.put(UUIDDatabase.UUID_CYCLING_POWER_FEATURE, "Cycling Power Feature");
        attributesUUID.put(UUIDDatabase.UUID_CYCLING_POWER_MEASUREMENT, "Cycling Power Measurement");
        attributesUUID.put(UUIDDatabase.UUID_DATABASE_CHANGE_INCREMENT, "Database Change Increment");
        attributesUUID.put(UUIDDatabase.UUID_DATE_OF_BIRTH, "Date Of Birth");
        attributesUUID.put(UUIDDatabase.UUID_DATE_OF_THRESHOLD_ASSESSMENT, "Date Of Threshold Assessment");
        attributesUUID.put(UUIDDatabase.UUID_DATE_TIME, "Date Time");
        attributesUUID.put(UUIDDatabase.UUID_DAY_DATE_TIME, "Day Date Time");
        attributesUUID.put(UUIDDatabase.UUID_DAY_OF_WEEK, "Day Of Week");
        attributesUUID.put(UUIDDatabase.UUID_DESCRIPTOR_VALUE_CHANGED, "Descriptor Value Changed");
        attributesUUID.put(UUIDDatabase.UUID_DEVICE_NAME, "Device Name");
        attributesUUID.put(UUIDDatabase.UUID_DEW_POINT, "Dew Point");
        attributesUUID.put(UUIDDatabase.UUID_DST_OFFSET, "DST Offset");
        attributesUUID.put(UUIDDatabase.UUID_ELEVATION, "Elevation");
        attributesUUID.put(UUIDDatabase.UUID_EMAIL_ADDRESS, "Email Address");
        attributesUUID.put(UUIDDatabase.UUID_EXACT_TIME_256, "Exact Time 256");
        attributesUUID.put(UUIDDatabase.UUID_FAT_BURN_HEART_RATE_LOWER_LIMIT, "Fat Burn Heart Rate lower Limit");
        attributesUUID.put(UUIDDatabase.UUID_FAT_BURN_HEART_RATE_UPPER_LIMIT, "Fat Burn Heart Rate Upper Limit");
        attributesUUID.put(UUIDDatabase.UUID_FIVE_ZONE_HEART_RATE_LIMITS, "Five Zone Heart Rate Limits");
        attributesUUID.put(UUIDDatabase.UUID_GENDER, "Gender");
        attributesUUID.put(UUIDDatabase.UUID_HEART_RATE_MAX, "Heart Rate Max");
        attributesUUID.put(UUIDDatabase.UUID_HEAT_INDEX, "Heat Index");
        attributesUUID.put(UUIDDatabase.UUID_HEIGHT, "Height");
        attributesUUID.put(UUIDDatabase.UUID_HIP_CIRCUMFERENCE, "Hip Circumference");
        attributesUUID.put(UUIDDatabase.UUID_HUMIDITY, "Humidity");
        attributesUUID.put(UUIDDatabase.UUID_INTERMEDIATE_CUFF_PRESSURE, "Intermediate Cuff Pressure");
        attributesUUID.put(UUIDDatabase.UUID_IRRADIANCE, "Irradiance");
        attributesUUID.put(UUIDDatabase.UUID_LANGUAGE, "Language");
        attributesUUID.put(UUIDDatabase.UUID_LAST_NAME, "Last Name");
        attributesUUID.put(UUIDDatabase.UUID_LN_CONTROL_POINT, "LN Control Point");
        attributesUUID.put(UUIDDatabase.UUID_LN_FEATURE, "LN Feature");
        attributesUUID.put(UUIDDatabase.UUID_LOCAL_TIME_INFORMATION, "Local Time Information");
        attributesUUID.put(UUIDDatabase.UUID_LOCATION_AND_SPEED, "Location and Speed");
        attributesUUID.put(UUIDDatabase.UUID_MAGNETIC_DECLINATION, "Magenetic Declination");
        attributesUUID.put(UUIDDatabase.UUID_MAGNETIC_FLUX_DENSITY_2D, "Magentic Flux Density 2D");
        attributesUUID.put(UUIDDatabase.UUID_MAGNETIC_FLUX_DENSITY_3D, "Magentic Flux Density 3D");
        attributesUUID.put(UUIDDatabase.UUID_MAXIMUM_RECOMMENDED_HEART_RATE, "Maximum Recommended Heart Rate");
        attributesUUID.put(UUIDDatabase.UUID_NEW_ALERT, "New Alert");
        attributesUUID.put(UUIDDatabase.UUID_NAVIGATION, "Navigation");
        attributesUUID.put(UUIDDatabase.UUID_PERIPHERAL_PREFERRED_CONNECTION_PARAMETERS, "Peripheral Preferred Connection Parameters");
        attributesUUID.put(UUIDDatabase.UUID_PERIPHERAL_PRIVACY_FLAG, "Peripheral Privacy Flag");
        attributesUUID.put(UUIDDatabase.UUID_POLLEN_CONCENTRATION, "Pollen Concentration");
        attributesUUID.put(UUIDDatabase.UUID_POSITION_QUALITY, "Position Quality");
        attributesUUID.put(UUIDDatabase.UUID_PRESSURE, "Pressure");
        attributesUUID.put(UUIDDatabase.UUID_TEMPERATURE, "Temperature");
        attributesUUID.put(UUIDDatabase.UUID_UV_INDEX, "UV Index");

        // Descriptors
        attributesUUID.put(UUIDDatabase.UUID_CHARACTERISTIC_EXTENDED_PROPERTIES, "Characteristic Extended Properties");
        attributesUUID.put(UUIDDatabase.UUID_CHARACTERISTIC_USER_DESCRIPTION, "Characteristic User Description");
        attributesUUID.put(UUIDDatabase.UUID_CLIENT_CHARACTERISTIC_CONFIG, "Client Characteristic Configuration");
        attributesUUID.put(UUIDDatabase.UUID_SERVER_CHARACTERISTIC_CONFIGURATION, "Server Characteristic Configuration");
        attributesUUID.put(UUIDDatabase.UUID_CHARACTERISTIC_PRESENTATION_FORMAT, "Characteristic Presentation Format");
        attributesUUID.put(UUIDDatabase.UUID_CHARACTERISTIC_AGGREGATE_FORMAT, "Characteristic Aggregate Format");
        attributesUUID.put(UUIDDatabase.UUID_VALID_RANGE, "Valid Range");
        attributesUUID.put(UUIDDatabase.UUID_EXTERNAL_REPORT_REFERENCE, "External Report Reference");
        attributesUUID.put(UUIDDatabase.UUID_REPORT_REFERENCE, "Report Reference");
        attributesUUID.put(UUIDDatabase.UUID_ENVIRONMENTAL_SENSING_CONFIGURATION, "Environmental Sensing Configuration");
        attributesUUID.put(UUIDDatabase.UUID_ENVIRONMENTAL_SENSING_MEASUREMENT, "Environmental Sensing Measurement");
        attributesUUID.put(UUIDDatabase.UUID_ENVIRONMENTAL_SENSING_TRIGGER_SETTING, "Environmental Sensing Trigger Setting");

        // Services Image Mapping.
        attributesImageMap.put(UUIDDatabase.UUID_HEART_RATE_SERVICE, R.drawable.carousel_heart_rate);
        attributesImageMap.put(UUIDDatabase.UUID_HEALTH_THERMOMETER_SERVICE, R.drawable.carousel_health_thermometer);
        attributesImageMap.put(UUIDDatabase.UUID_GENERIC_ACCESS_SERVICE, R.drawable.carousel_gatt_db); // "00001800-0000-1000-8000-00805f9b34fb",
        attributesImageMap.put(UUIDDatabase.UUID_GENERIC_ATTRIBUTE_SERVICE, R.drawable.carousel_gatt_db); // "00001801-0000-1000-8000-00805f9b34fb",
        attributesImageMap.put(UUIDDatabase.UUID_DEVICE_INFORMATION_SERVICE, R.drawable.carousel_device_information); // "0000180a-0000-1000-8000-00805f9b34fb",
        attributesImageMap.put(UUIDDatabase.UUID_BATTERY_SERVICE, R.drawable.carousel_battery); // "0000180f-0000-1000-8000-00805f9b34fb",

        attributesImageMap.put(UUIDDatabase.UUID_CAPSENSE_SERVICE, R.drawable.carousel_capsense);
        attributesImageMap.put(UUIDDatabase.UUID_CAPSENSE_SLIDER, R.drawable.carousel_capsense_slider);
        attributesImageMap.put(UUIDDatabase.UUID_CAPSENSE_PROXIMITY, R.drawable.carousel_capsense_slider);
        attributesImageMap.put(UUIDDatabase.UUID_CAPSENSE_BUTTONS, R.drawable.carousel_capsense_buttons);
        attributesImageMap.put(UUIDDatabase.UUID_RGB_LED_SERVICE, R.drawable.carousel_rgb_led);
        attributesImageMap.put(UUIDDatabase.UUID_CAPSENSE_SERVICE_CUSTOM, R.drawable.carousel_capsense);
        attributesImageMap.put(UUIDDatabase.UUID_CAPSENSE_SLIDER_CUSTOM, R.drawable.carousel_capsense_slider);
        attributesImageMap.put(UUIDDatabase.UUID_CAPSENSE_PROXIMITY_CUSTOM, R.drawable.carousel_capsense_slider);
        attributesImageMap.put(UUIDDatabase.UUID_CAPSENSE_BUTTONS_CUSTOM, R.drawable.carousel_capsense_buttons);
        attributesImageMap.put(UUIDDatabase.UUID_RGB_LED_SERVICE_CUSTOM, R.drawable.carousel_rgb_led);
        attributesImageMap.put(UUIDDatabase.UUID_IMMEDIATE_ALERT_SERVICE, R.drawable.carousel_find_me);
        attributesImageMap.put(UUIDDatabase.UUID_LINK_LOSS_SERVICE, R.drawable.carousel_proximity);
        attributesImageMap.put(UUIDDatabase.UUID_TRANSMISSION_POWER_SERVICE, R.drawable.carousel_proximity);
        attributesImageMap.put(UUIDDatabase.UUID_GLUCOSE_SERVICE, R.drawable.carousel_glucose);
        attributesImageMap.put(UUIDDatabase.UUID_BLOOD_PRESSURE_SERVICE, R.drawable.carousel_blood_pressure);
        attributesImageMap.put(UUIDDatabase.UUID_RSC_SERVICE, R.drawable.carousel_running_speed_and_cadence);
        attributesImageMap.put(UUIDDatabase.UUID_CSC_SERVICE, R.drawable.carousel_cycling_speed_and_cadence);
        attributesImageMap.put(UUIDDatabase.UUID_BAROMETER_SERVICE, R.drawable.carousel_sensor_hub);

        // CapSense Image Mapping
        attributesCapSenseImageMap.put(UUIDDatabase.UUID_CAPSENSE_SERVICE, R.drawable.carousel_capsense);
        attributesCapSenseImageMap.put(UUIDDatabase.UUID_CAPSENSE_PROXIMITY, R.drawable.carousel_capsense_proximity);
        attributesCapSenseImageMap.put(UUIDDatabase.UUID_CAPSENSE_SLIDER, R.drawable.carousel_capsense_slider);
        attributesCapSenseImageMap.put(UUIDDatabase.UUID_CAPSENSE_BUTTONS, R.drawable.carousel_capsense_buttons);
        attributesCapSenseImageMap.put(UUIDDatabase.UUID_CAPSENSE_SERVICE_CUSTOM, R.drawable.carousel_capsense);
        attributesCapSenseImageMap.put(UUIDDatabase.UUID_CAPSENSE_PROXIMITY_CUSTOM, R.drawable.carousel_capsense_proximity);
        attributesCapSenseImageMap.put(UUIDDatabase.UUID_CAPSENSE_SLIDER_CUSTOM, R.drawable.carousel_capsense_slider);
        attributesCapSenseImageMap.put(UUIDDatabase.UUID_CAPSENSE_BUTTONS_CUSTOM, R.drawable.carousel_capsense_buttons);

        attributesImageMap.put(UUIDDatabase.UUID_HID_SERVICE, R.drawable.carousel_unknown);
        attributesImageMap.put(UUIDDatabase.UUID_OTA_UPDATE_SERVICE, R.drawable.carousel_bootloader);
        attributesCapSense.put(UUIDDatabase.UUID_CAPSENSE_SERVICE, "CAPSENSE™ Services");
        attributesCapSense.put(UUIDDatabase.UUID_CAPSENSE_SERVICE_CUSTOM, "CAPSENSE™ Services");
        attributesCapSense.put(UUIDDatabase.UUID_CAPSENSE_BUTTONS, "CAPSENSE™ Button");
        attributesCapSense.put(UUIDDatabase.UUID_CAPSENSE_BUTTONS_CUSTOM, "CAPSENSE™ Button");
        attributesCapSense.put(UUIDDatabase.UUID_CAPSENSE_PROXIMITY, "CAPSENSE™ Proximity");
        attributesCapSense.put(UUIDDatabase.UUID_CAPSENSE_PROXIMITY_CUSTOM, "CAPSENSE™ Proximity");
        attributesCapSense.put(UUIDDatabase.UUID_CAPSENSE_SLIDER, "CAPSENSE™ Slider");
        attributesCapSense.put(UUIDDatabase.UUID_CAPSENSE_SLIDER_CUSTOM, "CAPSENSE™ Slider");

        /*
          Descriptor key value mapping
         */
        descriptorAttributes.put("0", "Reserved For Future Use");
        descriptorAttributes.put("1", "Boolean");
        descriptorAttributes.put("2", "unsigned 2-bit integer");
        descriptorAttributes.put("3", "unsigned 4-bit integer");
        descriptorAttributes.put("4", "unsigned 8-bit integer");
        descriptorAttributes.put("5", "unsigned 12-bit integer");
        descriptorAttributes.put("6", "unsigned 16-bit integer");
        descriptorAttributes.put("7", "unsigned 24-bit integer");
        descriptorAttributes.put("8", "unsigned 32-bit integer");
        descriptorAttributes.put("9", "unsigned 48-bit integer");
        descriptorAttributes.put("10", "unsigned 64-bit integer");
        descriptorAttributes.put("11", "unsigned 128-bit integer");
        descriptorAttributes.put("12", "signed 8-bit integer");
        descriptorAttributes.put("13", "signed 12-bit integer");
        descriptorAttributes.put("14", "signed 16-bit integer");
        descriptorAttributes.put("15", "signed 24-bit integer");
        descriptorAttributes.put("16", "signed 32-bit integer");
        descriptorAttributes.put("17", "signed 48-bit integer");
        descriptorAttributes.put("18", "signed 64-bit integer");
        descriptorAttributes.put("19", "signed 128-bit integer");
        descriptorAttributes.put("20", "IEEE-754 32-bit floating point");
        descriptorAttributes.put("21", "IEEE-754 64-bit floating point");
        descriptorAttributes.put("22", "IEEE-11073 16-bit SFLOAT");
        descriptorAttributes.put("23", "IEEE-11073 32-bit FLOAT");
        descriptorAttributes.put("24", "IEEE-20601 format");
        descriptorAttributes.put("25", "UTF-8 string");
        descriptorAttributes.put("26", "UTF-16 string");
        descriptorAttributes.put("27", "Opaque Structure");

    }

    public static String lookupUUID(UUID uuid, String defaultName) {
        String name = attributesUUID.get(uuid);
        return name == null ? defaultName : name;
    }

    public static int lookupImage(UUID uuid) {
        int imageId = attributesImageMap.get(uuid) != null ? attributesImageMap
                .get(uuid) : R.drawable.carousel_unknown;
        return imageId;
    }

    public static String lookupNameCapSense(UUID uuid, String defaultName) {
        String name = attributesCapSense.get(uuid);
        return name == null ? defaultName : name;
    }

    public static int lookupImageCapSense(UUID uuid) {
        int imageId = attributesCapSenseImageMap.get(uuid) != null ? attributesCapSenseImageMap
                .get(uuid) : R.drawable.carousel_unknown;
        return imageId;
    }

    public static String lookCharacteristicPresentationFormat(String key) {
        String value = descriptorAttributes.get(key);
        return value == null ? "Reserved" : value;
    }

}