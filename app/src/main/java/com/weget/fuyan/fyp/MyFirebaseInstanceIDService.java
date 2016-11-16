package com.weget.fuyan.fyp;

/**
 * Created by T.DW on 30/8/16.
 */
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        sendRegistrationToServer(refreshedToken);


        //send to sendbird server
//        SendBird.registerPushTokenForCurrentUser(refreshedToken, new SendBird.RegisterPushTokenWithStatusHandler() {
//            @Override
//            public void onRegistered(SendBird.PushTokenRegistrationStatus status, SendBirdException e) {
//                if (e != null) {
//                    // Error.
//                    return;
//                }
//
//                if (status == SendBird.PushTokenRegistrationStatus.PENDING) {
//                    // Try registration after connection is established.
//                }
//            }
//        });
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }
}
