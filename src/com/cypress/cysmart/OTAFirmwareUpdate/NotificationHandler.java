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

package com.cypress.cysmart.OTAFirmwareUpdate;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import com.cypress.cysmart.HomePageActivity;
import com.cypress.cysmart.R;

public class NotificationHandler {

    private static final String CHANNEL_ID_IN_PROGRESS = "com.cypress.cysmart.OTAFirmwareUpdate.InProgress";
    private static final String CHANNEL_ID_DONE = "com.cypress.cysmart.OTAFirmwareUpdate.Done";

    protected final int mNotificationId = 1;
    protected NotificationManager mNotificationManager;
    protected Notification.Builder mBuilder;

    public void initializeNotification(Context context) {
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new Notification.Builder(context)
                .setContentTitle(context.getResources().getString(R.string.ota_notification_title))
                .setContentText(String.format("%s %d%%", context.getResources().getString(R.string.ota_notification_ongoing), 0))
                .setSmallIcon(R.drawable.appicon_monochrome)
                // Make this notification automatically dismissed when the user touches it.
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel inProgressChannel = new NotificationChannel(CHANNEL_ID_IN_PROGRESS, "CySmart OTAFU in progress", NotificationManager.IMPORTANCE_LOW); // IMPORTANCE_LOW = No Sound
            mNotificationManager.createNotificationChannel(inProgressChannel);

            NotificationChannel doneChannel = new NotificationChannel(CHANNEL_ID_DONE, "CySmart OTAFU done", NotificationManager.IMPORTANCE_DEFAULT); // IMPORTANCE_DEFAULT = Makes a sound
            // Creating an Audio Attribute
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            doneChannel.setSound(soundUri(), audioAttributes);
            mNotificationManager.createNotificationChannel(doneChannel);
        }
    }

    public void generatePendingNotification(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mBuilder.setChannelId(CHANNEL_ID_IN_PROGRESS);
        }

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, HomePageActivity.class);

        // This somehow makes sure there is only one CountDownTimer going if the notification is pressed
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // The stack builder object will contain an artificial back stack for the started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(HomePageActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);

        // Make this unique ID to make sure there is not generated just a brand new intent with new extra values:
        int requestID = (int) System.currentTimeMillis();

        // Pass the unique ID to the resultPendingIntent:
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, requestID, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder
                .setContentIntent(resultPendingIntent)
                .setPriority(Notification.PRIORITY_LOW)
                .setSound(null) // No sound
                .setProgress(100, 0, false); // Displays the progress bar for the first time.

        // notificationId allows you to update the notification later on.
        mNotificationManager.notify(mNotificationId, mBuilder.build());
    }

    public void updateProgress(Context context, int limit, int updateLimit, boolean flag) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mBuilder.setChannelId(CHANNEL_ID_IN_PROGRESS);
        }

        mBuilder
                .setContentText(String.format("%s %d%%", context.getResources().getString(R.string.ota_notification_ongoing), updateLimit))
                .setPriority(Notification.PRIORITY_LOW)
                .setSound(null) // No sound
                .setProgress(limit, updateLimit, flag);
        mNotificationManager.notify(mNotificationId, mBuilder.build());
    }

    public void completeProgress(Context context, int contentTextResourceId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mBuilder.setChannelId(CHANNEL_ID_DONE);
        }

        mBuilder
                .setContentText(context.getResources().getText(contentTextResourceId))
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setSound(soundUri()) // Default notification sound
                .setProgress(0, 0, false);
        mNotificationManager.notify(mNotificationId, mBuilder.build());
    }

    public void cancelPendingNotification() {
        mNotificationManager.cancel(mNotificationId);
    }

    private Uri soundUri() {
        return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    }

}
