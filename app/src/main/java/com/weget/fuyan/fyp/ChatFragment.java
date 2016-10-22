package com.weget.fuyan.fyp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelListQuery;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;

import java.util.Arrays;
import java.util.List;

/**
 * Created by HP on 9/13/2016.
 */
public class ChatFragment extends Fragment {
    private static final String identifier = "SendBirdGroupChannelList";
    private static final int REQUEST_INVITE_USERS = 100;
    private ListView mListView;
    private SendBirdGroupChannelAdapter mAdapter;
    private GroupChannelListQuery mQuery;
    private String username;
    private String password;
    private int myId;


    public ChatFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.sendbird_fragment_group_channel_list, container, false);
        initUIComponents(rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);
    }

    private void initUIComponents(View rootView) {
        mListView = (ListView) rootView.findViewById(R.id.list);
        mAdapter = new SendBirdGroupChannelAdapter(getActivity());
        mListView.setAdapter(mAdapter);
//            mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GroupChannel channel = mAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), UserChatActivity.class);
                intent.putExtra("channel_url", channel.getUrl());
                startActivity(intent);
            }
        });
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount >= (int) (totalItemCount * 0.8f)) {
                    loadNextChannels();
                }
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final GroupChannel channel = mAdapter.getItem(position);
                new AlertDialog.Builder(getActivity())
                        .setTitle("Leave")
                        .setMessage("Do you want to leave or hide this channel?")
                        .setPositiveButton("Leave", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                channel.leave(new GroupChannel.GroupChannelLeaveHandler() {
                                    @Override
                                    public void onResult(SendBirdException e) {
                                        if (e != null) {
                                            Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        Toast.makeText(getActivity(), "Channel left.", Toast.LENGTH_SHORT).show();
                                        mAdapter.remove(position);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        })
                        .setNeutralButton("Hide", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                channel.hide(new GroupChannel.GroupChannelHideHandler() {
                                    @Override
                                    public void onResult(SendBirdException e) {
                                        if (e != null) {
                                            Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        Toast.makeText(getActivity(), "Channel hidden.", Toast.LENGTH_SHORT).show();
                                        mAdapter.remove(position);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel", null).create().show();
                return true;
            }
        });


    }

    private void loadNextChannels() {
        if (mQuery == null || mQuery.isLoading()) {
            return;
        }

        if (!mQuery.hasNext()) {
            return;
        }

        mQuery.next(new GroupChannelListQuery.GroupChannelListQueryResultHandler() {
            @Override
            public void onResult(List<GroupChannel> list, SendBirdException e) {
                if (e != null) {
                    Toast.makeText(getContext().getApplicationContext(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                mAdapter.addAll(list);
                mAdapter.notifyDataSetChanged();

                if (mAdapter.getCount() == 0) {
                    Toast.makeText(getContext().getApplicationContext(), "No channels found.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void create(final String[] userIds) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.sendbird_view_group_create_channel, null);
        final EditText chName = (EditText) view.findViewById(R.id.etxt_message);
        final CheckBox distinct = (CheckBox) view.findViewById(R.id.chk_distinct);

        new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle("Create Group Channel")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        GroupChannel.createChannelWithUserIds(Arrays.asList(userIds), distinct.isChecked(), chName.getText().toString(), null, null, new GroupChannel.GroupChannelCreateHandler() {
                            @Override
                            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                                if (e != null) {
                                    Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                mAdapter.replace(groupChannel);
                            }

                        });
                    }
                })
                .setNegativeButton("Cancel", null).create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//            if (resultCode == Activity.RESULT_OK) {
//                if (requestCode == REQUEST_INVITE_USERS) {
//                    String[] userIds = data.getStringArrayExtra("user_ids");
//                    create(userIds);
//                }
//            }
    }

    @Override
    public void onPause() {
        super.onPause();
        SendBird.removeChannelHandler(identifier);
    }

    @Override
    public void onResume() {
        super.onResume();
        //check connection from sendbird
        SendBird.addChannelHandler(identifier, new SendBird.ChannelHandler() {
            @Override
            public void onMessageReceived(BaseChannel baseChannel, BaseMessage baseMessage) {
                if (baseChannel instanceof GroupChannel) {
                    GroupChannel groupChannel = (GroupChannel) baseChannel;
                    mAdapter.replace(groupChannel);
                }
            }

            @Override
            public void onUserJoined(GroupChannel groupChannel, User user) {
                // Member changed. Refresh group channel item.
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onUserLeft(GroupChannel groupChannel, User user) {
                // Member changed. Refresh group channel item.
                mAdapter.notifyDataSetChanged();
            }
        });

        mAdapter.clear();
        mAdapter.notifyDataSetChanged();

        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("MyPref", 0);
        username = pref.getString("username", null);
        password = pref.getString("password", null);
        myId = pref.getInt("id", 0);
        String authString = username + ":" + password;

        if (SendBird.getConnectionState() != SendBird.ConnectionState.OPEN) {
            SendBird.connect(myId+"", new SendBird.ConnectHandler() {
                @Override
                public void onConnected(User user, SendBirdException e) {
                    if (e != null) {
                        Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mQuery = GroupChannel.createMyGroupChannelListQuery();
                    mQuery.setIncludeEmpty(true);


                }
            });
        } else {

            mQuery = GroupChannel.createMyGroupChannelListQuery();
            mQuery.setIncludeEmpty(true);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu){
        MenuItem refresh1 = menu.findItem(R.id.action_refresh);
        refresh1.setVisible(false);
    }

}
