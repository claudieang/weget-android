package com.weget.fuyan.fyp;

/**
 * Created by T.DW on 30/8/16.
 */
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: lalalallal" + remoteMessage.getData());
            String message = remoteMessage.getData().get("message");
            Log.d("hah",message);
            //String tag = remoteMessage.getNotification().getTag();
            //Log.d("taggg",tag);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

        }

        if(remoteMessage.getNotification()!= null) {

            if (remoteMessage.getNotification().getTag().equalsIgnoreCase("request")) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("notification_request_tab", 1);
                intent.putExtra("notification_request_swipe", 0);

                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
                notificationBuilder.setContentTitle("Weget");
                notificationBuilder.setContentText(remoteMessage.getNotification().getBody());
                notificationBuilder.setAutoCancel(true);
                notificationBuilder.setSmallIcon(R.drawable.ic_weget_notif);
                notificationBuilder.setContentIntent(pendingIntent);
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0, notificationBuilder.build());
            }

            if (remoteMessage.getNotification().getTag().equalsIgnoreCase("fulfill")) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("notification_fulfill_tab", 3);
                intent.putExtra("notification_fulfill_swipe", 1);


                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
                notificationBuilder.setContentTitle("Weget");
                notificationBuilder.setContentText(remoteMessage.getNotification().getBody());
                notificationBuilder.setAutoCancel(true);
                notificationBuilder.setSmallIcon(R.drawable.ic_weget_notif);
                notificationBuilder.setContentIntent(pendingIntent);
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0, notificationBuilder.build());
            }

            if (remoteMessage.getNotification().getTag().equalsIgnoreCase("fulfillp")) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("notification_request_tab", 1);
                intent.putExtra("notification_request_swipe", 1);

                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
                notificationBuilder.setContentTitle("Weget");
                notificationBuilder.setContentText(remoteMessage.getNotification().getBody());
                notificationBuilder.setAutoCancel(true);
                notificationBuilder.setSmallIcon(R.drawable.ic_weget_notif);
                notificationBuilder.setContentIntent(pendingIntent);
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0, notificationBuilder.build());
            }


            if (remoteMessage.getNotification().getTag().equalsIgnoreCase("fulfillc")) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("notification_request_tab", 1);
                intent.putExtra("notification_request_swipe", 2);

                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
                notificationBuilder.setContentTitle("Weget");
                notificationBuilder.setContentText(remoteMessage.getNotification().getBody());
                notificationBuilder.setAutoCancel(true);
                notificationBuilder.setSmallIcon(R.drawable.ic_weget_notif);
                notificationBuilder.setContentIntent(pendingIntent);
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0, notificationBuilder.build());
            }

            if (remoteMessage.getNotification().getTag().equalsIgnoreCase("requestp")) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("notification_fulfill_tab", 3);
                intent.putExtra("notification_fulfill_swipe", 1);


                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
                notificationBuilder.setContentTitle("Weget");
                notificationBuilder.setContentText(remoteMessage.getNotification().getBody());
                notificationBuilder.setAutoCancel(true);
                notificationBuilder.setSmallIcon(R.drawable.ic_weget_notif);
                notificationBuilder.setContentIntent(pendingIntent);
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0, notificationBuilder.build());
            }


            if (remoteMessage.getNotification().getTag().equalsIgnoreCase("requestc")) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("notification_fulfill_tab", 3);
                intent.putExtra("notification_fulfill_swipe", 2);


                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
                notificationBuilder.setContentTitle("Weget");
                notificationBuilder.setContentText(remoteMessage.getNotification().getBody());
                notificationBuilder.setAutoCancel(true);
                notificationBuilder.setSmallIcon(R.drawable.ic_weget_notif);
                notificationBuilder.setContentIntent(pendingIntent);
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0, notificationBuilder.build());
            }




        }

        else {
            Log.d("taggg","tesinglkjlk");
            String message = remoteMessage.getData().get("message");
            String name = remoteMessage.getData().get("sender");
            JsonElement payload = new JsonParser().parse(remoteMessage.getData().get("sendbird"));
            sendNotification(message, payload);


        }






        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */

    private void sendNotification(String messageBody,JsonElement payload) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("notification_chat_tab", 4);

        JsonObject message = payload.getAsJsonObject();
        JsonElement name1 = message.get("sender");
        JsonObject message2 = name1.getAsJsonObject();
        String name = message2.get("name").toString();



        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_weget_notif)
                .setContentTitle("Weget")
                .setContentText(name+" sent you a message: "+messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }


}
