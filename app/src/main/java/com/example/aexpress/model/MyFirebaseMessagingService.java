package com.example.aexpress.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        System.out.println(( "From: " + remoteMessage.getFrom()));

        // Check if message contains a data payload.
//        if (remoteMessage.getNotification()!=null) {
//            System.out.println( "Message data payload: " + remoteMessage.getData());
//
////            if (/* Check if data needs to be processed by long running job */ true) {
////                // For long-running tasks (10 seconds or more) use WorkManager.
////                scheduleJob();
////            } else {
////                // Handle message within 10 seconds
////                handleNow();
////            }
//
//        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            System.out.println( "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

//        sendNo
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
}
