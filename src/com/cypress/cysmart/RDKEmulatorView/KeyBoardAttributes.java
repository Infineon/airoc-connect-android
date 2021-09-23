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

package com.cypress.cysmart.RDKEmulatorView;

import java.util.HashMap;

/**
 * Class created to map the keycodes received through Keyboard Report
 */
public class KeyBoardAttributes {

    private static HashMap<Integer, String> m_keyCodes = new HashMap<Integer, String>();

    static {
        m_keyCodes.put(0, "Reserved");
        m_keyCodes.put(1, "ErrorRollOver");
        m_keyCodes.put(2, "POSTFail");
        m_keyCodes.put(3, "ErrorUndefined");
        m_keyCodes.put(4, "A");
        m_keyCodes.put(5, "B");
        m_keyCodes.put(6, "C");
        m_keyCodes.put(7, "D");
        m_keyCodes.put(8, "E");
        m_keyCodes.put(9, "F");
        m_keyCodes.put(10, "G");
        m_keyCodes.put(11, "H");
        m_keyCodes.put(12, "I");
        m_keyCodes.put(13, "J");
        m_keyCodes.put(14, "K");
        m_keyCodes.put(15, "L");
        m_keyCodes.put(16, "M");
        m_keyCodes.put(17, "N");
        m_keyCodes.put(18, "O");
        m_keyCodes.put(19, "P");
        m_keyCodes.put(20, "Q");
        m_keyCodes.put(21, "R");
        m_keyCodes.put(22, "S");
        m_keyCodes.put(23, "T");
        m_keyCodes.put(24, "U");
        m_keyCodes.put(25, "V");
        m_keyCodes.put(26, "W");
        m_keyCodes.put(27, "X");
        m_keyCodes.put(28, "Y");
        m_keyCodes.put(29, "Z");
        m_keyCodes.put(30, "1");
        m_keyCodes.put(31, "2");
        m_keyCodes.put(32, "3");
        m_keyCodes.put(33, "4");
        m_keyCodes.put(34, "5");
        m_keyCodes.put(35, "6");
        m_keyCodes.put(36, "7");
        m_keyCodes.put(37, "8");
        m_keyCodes.put(38, "9");
        m_keyCodes.put(39, "0");
        m_keyCodes.put(40, "Enter");
        m_keyCodes.put(41, "Escape");
        m_keyCodes.put(42, "Delete");
        m_keyCodes.put(43, "Tab");
        m_keyCodes.put(44, "Space");
        m_keyCodes.put(45, "- Minus");
        m_keyCodes.put(46, "= Equals");
        m_keyCodes.put(47, "[ Left Bracket");
        m_keyCodes.put(48, "] Right Bracket");
        m_keyCodes.put(49, "\\ Backslash");
        m_keyCodes.put(50, "Non-US # NonUS Pound");
        m_keyCodes.put(51, "; Semicolon");
        m_keyCodes.put(52, "' Quote");
        m_keyCodes.put(53, "` Grave");
        m_keyCodes.put(54, ", Comma");
        m_keyCodes.put(55, ". Period");
        m_keyCodes.put(56, "/ Slash");
        m_keyCodes.put(57, "Caps Lock");
        m_keyCodes.put(58, "F1");
        m_keyCodes.put(59, "F2");
        m_keyCodes.put(60, "F3");
        m_keyCodes.put(61, "F4");
        m_keyCodes.put(62, "F5");
        m_keyCodes.put(63, "F6");
        m_keyCodes.put(64, "F7");
        m_keyCodes.put(65, "F8");
        m_keyCodes.put(66, "F9");
        m_keyCodes.put(67, "F10");
        m_keyCodes.put(68, "F11");
        m_keyCodes.put(69, "F12");
        m_keyCodes.put(70, "Print Screen");
        m_keyCodes.put(71, "Scroll Lock");
        m_keyCodes.put(72, "Pause");
        m_keyCodes.put(73, "Insert");
        m_keyCodes.put(74, "Home");
        m_keyCodes.put(75, "Page Up");
        m_keyCodes.put(76, "Delete Forward");
        m_keyCodes.put(77, "End");
        m_keyCodes.put(78, "Page Down");
        m_keyCodes.put(79, "Right");
        m_keyCodes.put(80, "Left");
        m_keyCodes.put(81, "Down");
        m_keyCodes.put(82, "Up");
        m_keyCodes.put(83, "Keypad NumLock");
        m_keyCodes.put(84, "Keypad / Keypad Divide");
        m_keyCodes.put(85, "Keypad * Keypad Multiply");
        m_keyCodes.put(86, "Keypad - Keypad Subtract");
        m_keyCodes.put(87, "Keypad + Keypad put");
        m_keyCodes.put(88, "Keypad Enter");
        m_keyCodes.put(89, "Keypad 1");
        m_keyCodes.put(90, "Keypad 2");
        m_keyCodes.put(91, "Keypad 3");
        m_keyCodes.put(92, "Keypad 4");
        m_keyCodes.put(93, "Keypad 5");
        m_keyCodes.put(94, "Keypad 6");
        m_keyCodes.put(95, "Keypad 7");
        m_keyCodes.put(96, "Keypad 8");
        m_keyCodes.put(97, "Keypad 9");
        m_keyCodes.put(98, "Keypad 0");
        m_keyCodes.put(99, "Keypad . Keypad Point");
        m_keyCodes.put(100, "Non-US \\ NonUS Backslash");
        m_keyCodes.put(101, "Application");
        m_keyCodes.put(102, "Power");
        m_keyCodes.put(103, "Keypad = Keypad Equals");
        m_keyCodes.put(104, "F13");
        m_keyCodes.put(105, "F14");
        m_keyCodes.put(106, "F15");
        m_keyCodes.put(107, "F16");
        m_keyCodes.put(108, "F17");
        m_keyCodes.put(109, "F18");
        m_keyCodes.put(110, "F19");
        m_keyCodes.put(111, "F20");
        m_keyCodes.put(112, "F21");
        m_keyCodes.put(113, "F22");
        m_keyCodes.put(114, "F23");
        m_keyCodes.put(115, "F24");
        m_keyCodes.put(116, "Execute");
        m_keyCodes.put(117, "Help");
        m_keyCodes.put(118, "Menu");
        m_keyCodes.put(119, "Select");
        m_keyCodes.put(120, "Stop");
        m_keyCodes.put(121, "Again");
        m_keyCodes.put(122, "Undo");
        m_keyCodes.put(123, "Cut");
        m_keyCodes.put(124, "Copy");
        m_keyCodes.put(125, "Paste");
        m_keyCodes.put(126, "Find");
        m_keyCodes.put(127, "Mute");
        m_keyCodes.put(128, "Volume Up");
        m_keyCodes.put(129, "Volume Down");
        m_keyCodes.put(130, "Locking Caps Lock");
        m_keyCodes.put(131, "Locking Num Lock");
        m_keyCodes.put(132, "Locking Scroll Lock");
        m_keyCodes.put(133, "Keypad Comma");
        m_keyCodes.put(134, "Keypad Equal Sign");
        m_keyCodes.put(135, "International 1");
        m_keyCodes.put(136, "International 2");
        m_keyCodes.put(137, "International 3");
        m_keyCodes.put(138, "International 4");
        m_keyCodes.put(139, "International 5");
        m_keyCodes.put(140, "International 6");
        m_keyCodes.put(141, "International 7");
        m_keyCodes.put(142, "International 8");
        m_keyCodes.put(143, "International 9");
        m_keyCodes.put(144, "Lang 1");
        m_keyCodes.put(145, "Lang 2");
        m_keyCodes.put(146, "Lang 3");
        m_keyCodes.put(147, "Lang 4");
        m_keyCodes.put(148, "Lang 5");
        m_keyCodes.put(149, "Lang 6");
        m_keyCodes.put(150, "Lang 7");
        m_keyCodes.put(151, "Lang 8");
        m_keyCodes.put(152, "Lang 9");
        m_keyCodes.put(153, "Alternate Erase");
        m_keyCodes.put(154, "SysReq/Attention SysReq");
        m_keyCodes.put(155, "Cancel");
        m_keyCodes.put(156, "Clear");
        m_keyCodes.put(157, "Prior");
        m_keyCodes.put(158, "Return");
        m_keyCodes.put(159, "Separator");
        m_keyCodes.put(160, "Out");
        m_keyCodes.put(161, "Oper");
        m_keyCodes.put(162, "Clear/Again Clear");
        m_keyCodes.put(163, "CrSel/Props CrSel");
        m_keyCodes.put(164, "ExSel");
        m_keyCodes.put(176, "Keypad 00");
        m_keyCodes.put(177, "Keypad 000");
        m_keyCodes.put(178, "Thousands Separator");
        m_keyCodes.put(179, "Decimal Separator");
        m_keyCodes.put(180, "Currency Unit");
        m_keyCodes.put(181, "Currency Sub-unit Currency Subunit");
        m_keyCodes.put(182, "Keypad ( Keypad Left Paren");
        m_keyCodes.put(183, "Keypad ) Keypad Right Paren");
        m_keyCodes.put(184, "Keypad { Keypad Left Brace");
        m_keyCodes.put(185, "Keypad } Keypad Right Brace");
        m_keyCodes.put(186, "Keypad Tab");
        m_keyCodes.put(187, "Keypad Backspace");
        m_keyCodes.put(188, "Keypad A");
        m_keyCodes.put(189, "Keypad B");
        m_keyCodes.put(190, "Keypad C");
        m_keyCodes.put(191, "Keypad D");
        m_keyCodes.put(192, "Keypad E");
        m_keyCodes.put(193, "Keypad F");
        m_keyCodes.put(194, "Keypad XOR");
        m_keyCodes.put(195, "Keypad ^ Keypad Caret");
        m_keyCodes.put(196, "Keypad % Keypad Percent");
        m_keyCodes.put(197, "Keypad < Keypad Less Than");
        m_keyCodes.put(198, "Keypad > Keypad Greater Than");
        m_keyCodes.put(199, "Keypad & Keypad And");
        m_keyCodes.put(200, "Keypad && Keypad Double And");
        m_keyCodes.put(201, "Keypad | Keypad Pipe");
        m_keyCodes.put(202, "Keypad || Keypad Double Pipe");
        m_keyCodes.put(203, "Keypad : Keypad Colon");
        m_keyCodes.put(204, "Keypad # Keypad Pound");
        m_keyCodes.put(205, "Keypad Space");
        m_keyCodes.put(206, "Keypad @ Keypad At Sign");
        m_keyCodes.put(207, "Keypad ! Keypad Exclamation");
        m_keyCodes.put(208, "Keypad Memory Store");
        m_keyCodes.put(209, "Keypad Memory Recall");
        m_keyCodes.put(210, "Keypad Memory Clear");
        m_keyCodes.put(211, "Keypad Memory put");
        m_keyCodes.put(212, "Keypad Memory Subtract");
        m_keyCodes.put(213, "Keypad Memory Multiply");
        m_keyCodes.put(214, "Keypad Memory Divide");
        m_keyCodes.put(215, "Keypad +/- Keypad Plus Minus");
        m_keyCodes.put(216, "Keypad Clear");
        m_keyCodes.put(217, "Keypad Clear Entry");
        m_keyCodes.put(218, "Keypad Binary");
        m_keyCodes.put(219, "Keypad Octal");
        m_keyCodes.put(220, "Keypad Decimal");
        m_keyCodes.put(221, "Keypad Hexadecimal");
        m_keyCodes.put(224, "Left Control");
        m_keyCodes.put(225, "Left Shift");
        m_keyCodes.put(226, "Left Alt");
        m_keyCodes.put(227, "Left GUI");
        m_keyCodes.put(228, "Right Control");
        m_keyCodes.put(229, "Right Shift");
        m_keyCodes.put(230, "Right Alt");
        m_keyCodes.put(231, "Right GUI");
    }

    public static String lookupKeycodeDescription(Integer keycode) {
        String name = m_keyCodes.get(keycode);
        return name == null ? "" + keycode : name;
    }
}
