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

package com.infineon.airocbluetoothconnect.OTAFirmwareUpdate;

/**
 * Class created for bootloader command constants
 */
class BootLoaderCommands_v1 {

    /* Command identifier for verifying the checksum value of the bootloadable project. */
    public static final int VERIFY_APP = 0x31;
    /* Command identifier for clearing the target's buffer of data from previous Send Data (0x37, 0x47) commands */
    public static final int SYNC = 0x35;
    /* Command identifier for sending a block of data to the bootloader without doing anything with it yet. */
    public static final int SEND_DATA = 0x37;
    /* Command identifier for starting the boot loader.  All other commands ignored until this is sent. */
    public static final int ENTER_BOOTLOADER = 0x38;
    /* Command identifier for exiting the bootloader and restarting the target program. */
    public static final int EXIT_BOOTLOADER = 0x3B;
    /* Command identifier for sending a block of data to the bootloader without doing anything with it yet. */
    public static final int SEND_DATA_WITHOUT_RESPONSE = 0x47;
    /* Command to program data. */
    public static final int PROGRAM_DATA = 0x49;
    /* Command to verify data */
    public static final int VERIFY_DATA = 0x4A;
    /* Command to set application metadata in bootloader SDK */
    public static final int SET_APP_METADATA = 0x4C;
    /* Command to set encryption initial vector */
    public static final int SET_EIV = 0x4D;

    /*Bulletproof OTAFU*/
    public static final String POST_SYNC_ENTER_BOOTLOADER = "POST_SYNC_ENTER_BOOTLOADER";

    public static final int PACKET_START = 0x01;
    public static final int PACKET_END = 0x17;
    public static final int BASE_CMD_SIZE = 7;//SOP(1) + CmdCode(1) + DataLength(2) + Checksum(2) + EOP(1)
    public static final int WRITE_WITH_RESP_MAX_DATA_SIZE = 133;
    public static final int WRITE_NO_RESP_MAX_DATA_SIZE = 300;
}
