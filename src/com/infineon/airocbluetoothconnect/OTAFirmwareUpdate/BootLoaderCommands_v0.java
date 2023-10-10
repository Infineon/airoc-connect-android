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

package com.infineon.airocbluetoothconnect.OTAFirmwareUpdate;

/**
 * Class created for bootloader commands constants
 */
class BootLoaderCommands_v0 {

    /* Command identifier for verifying the checksum value of the bootloadable project. */
    public static final int VERIFY_CHECK_SUM = 0x31;
    /* Command identifier for getting the number of flash rows in the target device. */
    public static final int GET_FLASH_SIZE = 0x32;
    /* Command identifier for getting info about the app status. This is only supported on multi app bootloader. */
    public static final int GET_APP_STATUS = 0x33;
    /* Command identifier for setting the active application. This is only supported on multi app bootloader. */
    public static final int SET_ACTIVE_APP = 0x36;
    /* Command identifier for sending a block of data to the bootloader without doing anything with it yet. */
    public static final int SEND_DATA = 0x37;
    /* Command identifier for starting the boot loader.  All other commands ignored until this is sent. */
    public static final int ENTER_BOOTLOADER = 0x38;
    /* Command identifier for programming a single row of flash. */
    public static final int PROGRAM_ROW = 0x39;
    /* Command to verify data */
    public static final int VERIFY_ROW = 0x3A;
    /* Command identifier for exiting the bootloader and restarting the target program. */
    public static final int EXIT_BOOTLOADER = 0x3B;

    public static final int PACKET_START = 0x01;
    public static final int PACKET_END = 0x17;
    public static final int BASE_CMD_SIZE = 0x07;//SOP(1) + CmdCode(1) + DataLength(2) + Checksum(2) + EOP(1)
    public static final int WRITE_WITH_RESP_MAX_DATA_SIZE = 133;
    public static final int WRITE_NO_RESP_MAX_DATA_SIZE = 300;
}
