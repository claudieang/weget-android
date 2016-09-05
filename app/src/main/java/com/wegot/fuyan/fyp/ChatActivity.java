package com.wegot.fuyan.fyp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelListQuery;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by user on 5/9/16.
 */
public class ChatActivity extends AppCompatActivity {

    public static String VERSION = "3.0.1.0";

    private enum State {DISCONNECTED, CONNECTED}

    public static String sUserId;
    int myId;
    private String mNickname;
    Context mContext;
    String err, authString, username, password;
    private static final String appId = "73152F8B-67D9-4606-802C-A1BED7143436";

    List<String> userIds;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        username = pref.getString("username", null);
        password = pref.getString("password", null);
        myId = pref.getInt("id", 0);
        authString  = username + ":" + password;

        sUserId = myId + "";
        mNickname = username;
        connect();
        Intent intent = new Intent(ChatActivity.this, SendBirdGroupChannelListActivity.class);
        startActivity(intent);
    }


    //sendBird Controller
    private void setState(State state) {
        switch (state) {
            case DISCONNECTED:

                //((Button) findViewById(R.id.btn_connect)).setText("Connect");
                //findViewById(R.id.btn_open_channel_list).setEnabled(false);
                //findViewById(R.id.btn_group_channel_list).setEnabled(false);
                break;

            case CONNECTED:
                //((Button) findViewById(R.id.btn_connect)).setText("Disconnect");
                //findViewById(R.id.btn_open_channel_list).setEnabled(true);
                //findViewById(R.id.btn_group_channel_list).setEnabled(true);
                break;
        }
    }

    private void connect() {
        SendBird.connect(sUserId, new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {
                if (e != null) {
                    Toast.makeText(ChatActivity.this, "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                String nickname = mNickname;

                SendBird.updateCurrentUserInfo(nickname, null, new SendBird.UserInfoUpdateHandler() {
                    @Override
                    public void onUpdated(SendBirdException e) {
                        if (e != null) {
                            Toast.makeText(ChatActivity.this, "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
                        editor.putString("user_id", sUserId);
                        editor.putString("nickname", mNickname);
                        editor.commit();

                        setState(State.CONNECTED);
                    }
                });

                String gcmRegToken = PreferenceManager.getDefaultSharedPreferences(mContext).getString("SendBirdGCMToken", "");
                if (gcmRegToken != null && gcmRegToken.length() > 0) {
                    SendBird.registerPushTokenForCurrentUser(gcmRegToken, new SendBird.RegisterPushTokenHandler() {
                        @Override
                        public void onRegistered(SendBirdException e) {
                            if (e != null) {
                                Toast.makeText(ChatActivity.this, "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    });
                }
            }
        });
        createGrpChannel();
        GroupChannelListQuery mQuery = GroupChannel.createMyGroupChannelListQuery();
        mQuery.setIncludeEmpty(true);
        mQuery.next(new GroupChannelListQuery.GroupChannelListQueryResultHandler() {
            @Override
            public void onResult(List<GroupChannel> list, SendBirdException e) {
                if(e != null) {
                    Toast.makeText(ChatActivity.this, "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

    }

    private void disconnect() {
        SendBird.disconnect(new SendBird.DisconnectHandler() {
            @Override
            public void onDisconnected() {
                setState(State.DISCONNECTED);
            }
        });
    }

    public void createGrpChannel(){

        userIds = new ArrayList<String>();
        userIds.add("2");
        userIds.add("2");

        GroupChannel.createChannelWithUserIds(userIds, false, new GroupChannel.GroupChannelCreateHandler() {
            @Override
            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                if(e != null) {
                    Toast.makeText(ChatActivity.this, "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }




}
