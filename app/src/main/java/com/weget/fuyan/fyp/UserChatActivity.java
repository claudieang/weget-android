package com.weget.fuyan.fyp;


import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sendbird.android.AdminMessage;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.FileMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.PreviousMessageListQuery;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.sendbird.android.UserMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

public class UserChatActivity extends AppCompatActivity {
    private SendBirdChatFragment mSendBirdMessagingFragment;
    String productName, requirement, location, startTime, endTime, status, requestorIdS, requestorName,
            username, password, picture;
    static Toolbar toolbar;
    private View mTopBarContainer;
    private View mSettingsContainer;
    private TextView mHeaderView;
    private String mChannelUrl;
    private String profileId;
    private int dbID;
    private String dbUsername;
    private String dbProfilePic = null;
    private String loggedInId;
    public static String friendId = "";
    private static Context mContext;
    private GroupChannel mGroupChannel;
    private SendBirdMessagingAdapter mAdapter;
    private String authString;
    private static String err;
    private static int friendNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //overridePendingTransition(R.anim.sendbird_slide_in_from_bottom, R.anim.sendbird_slide_out_to_top);
        setContentView(R.layout.activity_userchat);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        //toolbar.setTitle("Request Completed");
//        setActionBar(toolbar);
//
//        getActionBar().setDisplayHomeAsUpEnabled(true);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        Button btn = (Button)findViewById(R.id.create_btn);
//        btn.setVisibility(View.GONE);
//        ImageButton imgBtn = (ImageButton)findViewById(R.id.close_btn);
//
//        imgBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL );
                callIntent.setData(Uri.parse("tel:" + friendNumber));

                if (ContextCompat.checkSelfPermission(getApplication().getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {


                }

                startActivity(callIntent);
            }
        });

        initFragment();
        initUIComponents();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //resizeMenubar();
    }

    private void resizeMenubar() {
        ViewGroup.LayoutParams lp = mTopBarContainer.getLayoutParams();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            lp.height = (int) (28 * getResources().getDisplayMetrics().density);
        } else {
            lp.height = (int) (48 * getResources().getDisplayMetrics().density);
        }
        mTopBarContainer.setLayoutParams(lp);
    }

//    @Override
//    public void finish() {
//        super.finish();
//        overridePendingTransition(R.anim.sendbird_slide_in_from_top, R.anim.sendbird_slide_out_to_bottom);
//    }

    private void initFragment() {
        mSendBirdMessagingFragment = new SendBirdChatFragment();
        mChannelUrl = getIntent().getStringExtra("channel_url");
        mSendBirdMessagingFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mSendBirdMessagingFragment)
                .commit();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Helper.MY_PERMISSION_REQUEST_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void initUIComponents() {
//        mTopBarContainer = findViewById(R.id.top_bar_container);
//
//        mSettingsContainer = findViewById(R.id.settings_container);
//        mSettingsContainer.setVisibility(View.GONE);

//        findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//
//        findViewById(R.id.btn_settings).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mSettingsContainer.getVisibility() != View.VISIBLE) {
//                    mSettingsContainer.setVisibility(View.VISIBLE);
//                } else {
//                    mSettingsContainer.setVisibility(View.GONE);
//                }
//            }
//        });

//        findViewById(R.id.btn_members).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(SendBirdGroupChatActivity.this, SendBirdMemberListActivity.class);
//                intent.putExtra("channel_url", mChannelUrl);
//                startActivity(intent);
//                mSettingsContainer.setVisibility(View.GONE);
//            }
//        });

//        findViewById(R.id.btn_invite).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(SendBirdGroupChatActivity.this, SendBirdUserListActivity.class);
//                mSendBirdMessagingFragment.startActivityForResult(intent, SendBirdChatFragment.REQUEST_INVITE_USERS);
//                mSettingsContainer.setVisibility(View.GONE);
//            }
//        });

//        resizeMenubar();
    }

    public static class SendBirdChatFragment extends Fragment {
        private static final int REQUEST_PICK_IMAGE = 100;
        private static final int REQUEST_INVITE_USERS = 200;
        private static final String identifier = "SendBirdGroupChat";

        private ListView mListView;
        private SendBirdMessagingAdapter mAdapter;
        private EditText mEtxtMessage;
        private Button mBtnSend, mBtnCall;
        private ImageButton mBtnUpload;
        private ProgressBar mProgressBtnUpload;
        private String mChannelUrl;
        private GroupChannel mGroupChannel;
        private PreviousMessageListQuery mPrevMessageListQuery;
        private boolean isUploading;
        private TextView mHeaderView;

        public SendBirdChatFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.sendbird_fragment_group_chat, container, false);

            mChannelUrl = getArguments().getString("channel_url");

            initUIComponents(rootView);
            return rootView;
        }

        private void initGroupChannel() {
            GroupChannel.getChannel(mChannelUrl, new GroupChannel.GroupChannelGetHandler() {
                @Override
                public void onResult(GroupChannel groupChannel, SendBirdException e) {
                    if (e != null) {
                        Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    mGroupChannel = groupChannel;
                    mGroupChannel.markAsRead();

                    mAdapter = new SendBirdMessagingAdapter(getActivity(), mGroupChannel);
                    mListView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();

                    SharedPreferences pref = getActivity().getSharedPreferences("MyPref", 0);

                    int userID = pref.getInt("id",0);
                    String channelTitle = "";

                    for(User u: mGroupChannel.getMembers()){
                        if(!u.getUserId().equals(userID+"")){
                            channelTitle = u.getNickname();
                            friendId = u.getUserId();


                            String username = pref.getString("username", null);
                            String password = pref.getString("password", null);
                            String authString  = username + ":" + password;

                            new initialize().execute(authString);

                        }
                    }





                    SpannableString s = new SpannableString(channelTitle);
                    s.setSpan(new TypefaceSpan(getActivity(), "Roboto-Regular.ttf"), 0, s.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    toolbar.setTitle(s);
                    //mHeaderView.setText(channelTitle);

                    //updateGroupChannelTitle();

                    loadPrevMessages(true);
                }
            });
        }

        private void updateGroupChannelTitle() {
            ((TextView) getActivity().findViewById(R.id.txt_channel_name)).setText(Helper.getDisplayMemberNames(mGroupChannel.getMembers(), true));
        }

        @Override
        public void onPause() {
            super.onPause();
            if (!isUploading) {
                SendBird.removeChannelHandler(identifier);
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            if (!isUploading) {
                SendBird.addChannelHandler(identifier, new SendBird.ChannelHandler() {
                    @Override
                    public void onMessageReceived(BaseChannel baseChannel, BaseMessage baseMessage) {
                        if (baseChannel.getUrl().equals(mChannelUrl)) {
                            if (mAdapter != null) {
                                mGroupChannel.markAsRead();
                                mAdapter.appendMessage(baseMessage);
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onReadReceiptUpdated(GroupChannel groupChannel) {
                        if (groupChannel.getUrl().equals(mChannelUrl)) {
                            mAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onTypingStatusUpdated(GroupChannel groupChannel) {
                        if (groupChannel.getUrl().equals(mChannelUrl)) {
                            mAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onUserJoined(GroupChannel groupChannel, User user) {
                        if (groupChannel.getUrl().equals(mChannelUrl)) {
                            //updateGroupChannelTitle();
                        }
                    }

                    @Override
                    public void onUserLeft(GroupChannel groupChannel, User user) {
                        if (groupChannel.getUrl().equals(mChannelUrl)) {
                            //updateGroupChannelTitle();
                        }
                    }
                });

                initGroupChannel();
            }
        }

        private void initUIComponents(View rootView) {

            //mHeaderView = (TextView)rootView.findViewById(R.id.chat_name);

            mListView = (ListView) rootView.findViewById(R.id.groupchat_list);
            turnOffListViewDecoration(mListView);

            mBtnSend = (Button) rootView.findViewById(R.id.btn_send);
            mBtnCall = (Button) rootView.findViewById(R.id.btn_call);
//            mBtnUpload = (ImageButton) rootView.findViewById(R.id.btn_upload);
//            mProgressBtnUpload = (ProgressBar) rootView.findViewById(R.id.progress_btn_upload);
            mEtxtMessage = (EditText) rootView.findViewById(R.id.etxt_message);

            mBtnCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL );
                    callIntent.setData(Uri.parse("tel:" + friendNumber));

                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {


                    }

                    startActivity(callIntent);
                }
            });

            mBtnSend.setEnabled(false);
            mBtnSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    send();
                }
            });

//            mBtnUpload.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (Helper.requestReadWriteStoragePermissions(getActivity())) {
//                        isUploading = true;
//                        Intent intent = new Intent();
//                        intent.setType("image/*");
//                        intent.setAction(Intent.ACTION_GET_CONTENT);
//                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_PICK_IMAGE);
//                    }
//                }
//            });

            mEtxtMessage.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            send();
                        }
                        return true; // Do not hide keyboard.
                    }

                    return false;
                }
            });
            mEtxtMessage.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
            mEtxtMessage.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    mBtnSend.setEnabled(s.length() > 0);

                    if (s.length() == 1) {
                        mGroupChannel.startTyping();
                    } else if (s.length() <= 0) {
                        mGroupChannel.endTyping();
                    }
                }
            });

            mListView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Helper.hideKeyboard(getActivity());
                    return false;
                }
            });
            mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    if (scrollState == SCROLL_STATE_IDLE) {
                        if (view.getFirstVisiblePosition() == 0 && view.getChildCount() > 0 && view.getChildAt(0).getTop() == 0) {
                            loadPrevMessages(false);
                        }
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                }
            });
        }

        private void loadPrevMessages(final boolean refresh) {
            if (mGroupChannel == null) {
                return;
            }

            if (refresh || mPrevMessageListQuery == null) {
                mPrevMessageListQuery = mGroupChannel.createPreviousMessageListQuery();
            }

            if (mPrevMessageListQuery.isLoading()) {
                return;
            }

            if (!mPrevMessageListQuery.hasMore()) {
                return;
            }

            mPrevMessageListQuery.load(30, true, new PreviousMessageListQuery.MessageListQueryResult() {
                @Override
                public void onResult(List<BaseMessage> list, SendBirdException e) {
                    if (e != null) {
                        Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (refresh) {
                        mAdapter.clear();
                    }

                    for (BaseMessage message : list) {
                        mAdapter.insertMessage(message);
                    }
                    mAdapter.notifyDataSetChanged();
                    mListView.setSelection(list.size());
                }
            });
        }

        private void showUploadProgress(boolean tf) {
            if (tf) {
                mBtnUpload.setEnabled(false);
                mBtnUpload.setVisibility(View.INVISIBLE);
                mProgressBtnUpload.setVisibility(View.VISIBLE);
            } else {
                mBtnUpload.setEnabled(true);
                mBtnUpload.setVisibility(View.VISIBLE);
                mProgressBtnUpload.setVisibility(View.GONE);
            }
        }

        private void turnOffListViewDecoration(ListView listView) {
            listView.setDivider(null);
            listView.setDividerHeight(0);
            listView.setHorizontalFadingEdgeEnabled(false);
            listView.setVerticalFadingEdgeEnabled(false);
            listView.setHorizontalScrollBarEnabled(false);
            listView.setVerticalScrollBarEnabled(true);
            listView.setSelector(new ColorDrawable(0x00ffffff));
            listView.setCacheColorHint(0x00000000); // For Gingerbread scrolling bug fix
        }

        private void invite(String[] userIds) {
            mGroupChannel.inviteWithUserIds(Arrays.asList(userIds), new GroupChannel.GroupChannelInviteHandler() {
                @Override
                public void onResult(SendBirdException e) {
                    if (e != null) {
                        Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            });
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == REQUEST_PICK_IMAGE && data != null && data.getData() != null) {
                    upload(data.getData());
                } else if (requestCode == REQUEST_INVITE_USERS) {
                    String[] userIds = data.getStringArrayExtra("user_ids");
                    invite(userIds);
                }
            }
        }

        private void send() {
            if (mEtxtMessage.getText().length() <= 0) {
                return;
            }

            mGroupChannel.sendUserMessage(mEtxtMessage.getText().toString(), new BaseChannel.SendUserMessageHandler() {
                @Override
                public void onSent(UserMessage userMessage, SendBirdException e) {
                    if (e != null) {
                        Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    mAdapter.appendMessage(userMessage);
                    mAdapter.notifyDataSetChanged();

                    mEtxtMessage.setText("");
                }
            });

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                Helper.hideKeyboard(getActivity());
            }
        }

        private void upload(Uri uri) {
            Hashtable<String, Object> info = Helper.getFileInfo(getActivity(), uri);
            final String path = (String) info.get("path");
            final File file = new File(path);
            final String name = file.getName();
            final String mime = (String) info.get("mime");
            final int size = (Integer) info.get("size");

            if (path == null) {
                Toast.makeText(getActivity(), "Uploading file must be located in local storage.", Toast.LENGTH_LONG).show();
            } else {
                showUploadProgress(true);
                mGroupChannel.sendFileMessage(file, name, mime, size, "", new BaseChannel.SendFileMessageHandler() {
                    @Override
                    public void onSent(FileMessage fileMessage, SendBirdException e) {
                        showUploadProgress(false);
                        if (e != null) {
                            Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        mAdapter.appendMessage(fileMessage);
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        }
    }

    public static class SendBirdMessagingAdapter extends BaseAdapter {
        private static final int TYPE_UNSUPPORTED = 0;
        private static final int TYPE_USER_MESSAGE = 1;
        private static final int TYPE_ADMIN_MESSAGE = 2;
        private static final int TYPE_FILE_MESSAGE = 3;
        private static final int TYPE_TYPING_INDICATOR = 4;

        private final Context mContext;
        private final LayoutInflater mInflater;
        private final ArrayList<Object> mItemList;
        private final GroupChannel mGroupChannel;

        public SendBirdMessagingAdapter(Context context, GroupChannel channel) {
            mContext = context;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mItemList = new ArrayList<>();
            mGroupChannel = channel;
        }

        @Override
        public int getCount() {
            return mItemList.size() + (mGroupChannel.isTyping() ? 1 : 0);
        }

        @Override
        public Object getItem(int position) {
            if (position >= mItemList.size()) {
                List<User> members = mGroupChannel.getTypingMembers();
                ArrayList<String> names = new ArrayList<>();
                for (User member : members) {
                    names.add(member.getNickname());
                }

                return names;
            }
            return mItemList.get(position);
        }

        public void delete(Object object) {
            mItemList.remove(object);
        }

        public void clear() {
            mItemList.clear();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void insertMessage(BaseMessage message) {
            mItemList.add(0, message);
        }

        public void appendMessage(BaseMessage message) {
            mItemList.add(message);
        }

        @Override
        public int getItemViewType(int position) {
            if (position >= mItemList.size()) {
                return TYPE_TYPING_INDICATOR;
            }

            Object item = mItemList.get(position);
            if (item instanceof UserMessage) {
                return TYPE_USER_MESSAGE;
            } else if (item instanceof FileMessage) {
                return TYPE_FILE_MESSAGE;
            } else if (item instanceof AdminMessage) {
                return TYPE_ADMIN_MESSAGE;
            }

            return TYPE_UNSUPPORTED;
        }

        @Override
        public int getViewTypeCount() {
            return 5;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            final Object item = getItem(position);

            if (convertView == null || ((ViewHolder) convertView.getTag()).getViewType() != getItemViewType(position)) {
                viewHolder = new ViewHolder();
                viewHolder.setViewType(getItemViewType(position));

                switch (getItemViewType(position)) {
                    case TYPE_UNSUPPORTED:
                        Log.d("UCA", "Went through TYPE_UNSUPPORTED");
                        convertView = new View(mInflater.getContext());
                        convertView.setTag(viewHolder);
                        break;
                    case TYPE_USER_MESSAGE: {
                        Log.d("UCA", "Went through TYPE_USER_MESSAGE");
                        TextView tv;
                        ImageView iv;
                        View v;

                        convertView = mInflater.inflate(R.layout.sendbird_view_group_user_message, parent, false);

                        Log.d("SB", "Value of viewHolder is : " + viewHolder);

                        v = convertView.findViewById(R.id.left_container_friend);
                        viewHolder.setView("left_container", v);
                        //iv = (ImageView) convertView.findViewById(R.id.img_left_thumbnail);
                        //viewHolder.setView("left_thumbnail", iv);
                        tv = (TextView) convertView.findViewById(R.id.txt_left_friend);
                        viewHolder.setView("left_message", tv);
                        tv = (TextView) convertView.findViewById(R.id.txt_left_name_friend);
                        viewHolder.setView("left_name", tv);
                        tv = (TextView) convertView.findViewById(R.id.txt_left_time_friend);
                        viewHolder.setView("left_time", tv);

                        v = convertView.findViewById(R.id.right_container_user);
                        viewHolder.setView("right_container", v);
                        //iv = (ImageView) convertView.findViewById(R.id.img_right_thumbnail);
                        //viewHolder.setView("right_thumbnail", iv);
                        tv = (TextView) convertView.findViewById(R.id.txt_right_user);
                        viewHolder.setView("right_message", tv);
                        tv = (TextView) convertView.findViewById(R.id.txt_right_name);
                        viewHolder.setView("right_name", tv);
                        tv = (TextView) convertView.findViewById(R.id.txt_right_time_user);
                        viewHolder.setView("right_time", tv);
                        tv = (TextView) convertView.findViewById(R.id.txt_right_status_user);
                        viewHolder.setView("right_status", tv);

                        convertView.setTag(viewHolder);
                        break;
                    }
//                    case TYPE_ADMIN_MESSAGE: {
//                        Log.d("UCA", "Went through TYPE_ADMIN_MESSAGE");
//                        convertView = mInflater.inflate(R.layout.sendbird_view_admin_message, parent, false);
//                        viewHolder.setView("message", convertView.findViewById(R.id.txt_message));
//                        convertView.setTag(viewHolder);
//                        break;
//                    }
//                    case TYPE_FILE_MESSAGE: {
//                        Log.d("UCA", "Went through TYPE_FILE_MESSAGE");
//                        TextView tv;
//                        ImageView iv;
//                        View v;
//
//                        convertView = mInflater.inflate(R.layout.sendbird_view_group_file_message, parent, false);
//
//                        v = convertView.findViewById(R.id.left_container);
//                        viewHolder.setView("left_container", v);
//                        iv = (ImageView) convertView.findViewById(R.id.img_left_thumbnail);
//                        viewHolder.setView("left_thumbnail", iv);
//                        iv = (ImageView) convertView.findViewById(R.id.img_left);
//                        viewHolder.setView("left_image", iv);
//                        tv = (TextView) convertView.findViewById(R.id.txt_left_name);
//                        viewHolder.setView("left_name", tv);
//                        tv = (TextView) convertView.findViewById(R.id.txt_left_time);
//                        viewHolder.setView("left_time", tv);
//
//                        v = convertView.findViewById(R.id.right_container);
//                        viewHolder.setView("right_container", v);
//                        iv = (ImageView) convertView.findViewById(R.id.img_right_thumbnail);
//                        viewHolder.setView("right_thumbnail", iv);
//                        iv = (ImageView) convertView.findViewById(R.id.img_right);
//                        viewHolder.setView("right_image", iv);
//                        tv = (TextView) convertView.findViewById(R.id.txt_right_name);
//                        viewHolder.setView("right_name", tv);
//                        tv = (TextView) convertView.findViewById(R.id.txt_right_time);
//                        viewHolder.setView("right_time", tv);
//                        tv = (TextView) convertView.findViewById(R.id.txt_right_status);
//                        viewHolder.setView("right_status", tv);
//
//                        convertView.setTag(viewHolder);*
//                        break;
//                    }
                    case TYPE_TYPING_INDICATOR: {
                        Log.d("UCA", "Went through TYPE_TYPING_INDICATOR");
                        convertView = mInflater.inflate(R.layout.view_group_typing_indicator, parent, false);
                        viewHolder.setView("message", convertView.findViewById(R.id.user_typing));
                        convertView.setTag(viewHolder);
                        break;
                    }
                }
            }

            viewHolder = (ViewHolder) convertView.getTag();
            switch (getItemViewType(position)) {
                case TYPE_UNSUPPORTED:
                    break;
                case TYPE_USER_MESSAGE:
                    UserMessage message = (UserMessage) item;
                    if (message.getSender().getUserId().equals(SendBird.getCurrentUser().getUserId())) {
                        viewHolder.getView("left_container", View.class).setVisibility(View.GONE);
                        viewHolder.getView("right_container", View.class).setVisibility(View.VISIBLE);

                        //Helper.displayUrlImage(viewHolder.getView("right_thumbnail", ImageView.class), message.getSender().getProfileUrl(), true);
                        viewHolder.getView("right_name", TextView.class).setText(message.getSender().getNickname());
                        viewHolder.getView("right_message", TextView.class).setText(message.getMessage());
                        viewHolder.getView("right_time", TextView.class).setText(Helper.getDisplayDateTime(mContext, message.getCreatedAt()));

                        int unreadCount = mGroupChannel.getReadReceipt(message);
                        if (unreadCount > 1) {
                            viewHolder.getView("right_status", TextView.class).setText("Unread " + unreadCount);
                        } else if (unreadCount == 1) {
                            viewHolder.getView("right_status", TextView.class).setText("Unread");
                        } else {
                            viewHolder.getView("right_status", TextView.class).setText("");
                        }

                    } else {
                        viewHolder.getView("left_container", View.class).setVisibility(View.VISIBLE);
                        viewHolder.getView("right_container", View.class).setVisibility(View.GONE);

                        //Helper.displayUrlImage(viewHolder.getView("left_thumbnail", ImageView.class), icgetProfileUrl(), true);
                        viewHolder.getView("left_name", TextView.class).setText(message.getSender().getNickname());
                        viewHolder.getView("left_message", TextView.class).setText(message.getMessage());
                        viewHolder.getView("left_time", TextView.class).setText(Helper.getDisplayDateTime(mContext, message.getCreatedAt()));
                    }
                    break;
                case TYPE_ADMIN_MESSAGE:
                    AdminMessage adminMessage = (AdminMessage) item;
                    viewHolder.getView("message", TextView.class).setText(Html.fromHtml(adminMessage.getMessage()));
                    break;
                case TYPE_FILE_MESSAGE:
                    final FileMessage fileLink = (FileMessage) item;

                    if (fileLink.getSender().getUserId().equals(SendBird.getCurrentUser().getUserId())) {
                        viewHolder.getView("left_container", View.class).setVisibility(View.GONE);
                        viewHolder.getView("right_container", View.class).setVisibility(View.VISIBLE);

                        //Helper.displayUrlImage(viewHolder.getView("right_thumbnail", ImageView.class), fileLink.getSender().getProfileUrl(), true);
                        viewHolder.getView("right_name", TextView.class).setText(fileLink.getSender().getNickname());
                        if (fileLink.getType().toLowerCase().startsWith("image")) {
                            //
                            //
                            // Helper.displayUrlImage(viewHolder.getView("right_image", ImageView.class), fileLink.getUrl());
                        } else {
                            //viewHolder.getView("right_image", ImageView.class).setImageResource(R.drawable.sendbird_icon_file);
                        }
                        viewHolder.getView("right_time", TextView.class).setText(Helper.getDisplayDateTime(mContext, fileLink.getCreatedAt()));

                        int unreadCount = mGroupChannel.getReadReceipt(fileLink);
                        if (unreadCount > 1) {
                            viewHolder.getView("right_status", TextView.class).setText("Unread " + unreadCount);
                        } else if (unreadCount == 1) {
                            viewHolder.getView("right_status", TextView.class).setText("Unread");
                        } else {
                            viewHolder.getView("right_status", TextView.class).setText("");
                        }

                        viewHolder.getView("right_container").setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new AlertDialog.Builder(mContext)
                                        .setTitle("SendBird")
                                        .setMessage("Do you want to download this file?")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                try {
                                                    Helper.downloadUrl(fileLink.getUrl(), fileLink.getName(), mContext);
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        })
                                        .setNegativeButton("Cancel", null)
                                        .create()
                                        .show();
                            }
                        });
                    } else {
                        viewHolder.getView("left_container", View.class).setVisibility(View.VISIBLE);
                        viewHolder.getView("right_container", View.class).setVisibility(View.GONE);

                        //Helper.displayUrlImage(viewHolder.getView("left_thumbnail", ImageView.class), fileLink.getSender().getProfileUrl(), true);
                        viewHolder.getView("left_name", TextView.class).setText(fileLink.getSender().getNickname());
                        if (fileLink.getType().toLowerCase().startsWith("image")) {
                            //Helper.displayUrlImage(viewHolder.getView("left_image", ImageView.class), fileLink.getUrl());
                        } else {
                            //viewHolder.getView("left_image", ImageView.class).setImageResource(R.drawable.sendbird_icon_file);
                        }
                        viewHolder.getView("left_time", TextView.class).setText(Helper.getDisplayDateTime(mContext, fileLink.getCreatedAt()));

                        viewHolder.getView("left_container").setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new AlertDialog.Builder(mContext)
                                        .setTitle("SendBird")
                                        .setMessage("Do you want to download this file?")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                try {
                                                    Helper.downloadUrl(fileLink.getUrl(), fileLink.getName(), mContext);
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        })
                                        .setNegativeButton("Cancel", null)
                                        .create()
                                        .show();
                            }
                        });
                    }
                    break;

                case TYPE_TYPING_INDICATOR: {
                    int itemCount = ((List) item).size();
                    String typeMsg = ((List) item).get(0)
                            + ((itemCount > 1) ? " +" + (itemCount - 1) : "")
                            + ((itemCount > 1) ? " are " : " is ")
                            + "typing...";
                    viewHolder.getView("message", TextView.class).setText(typeMsg);
                    break;
                }
            }

            return convertView;
        }

        private class ViewHolder {
            private Hashtable<String, View> holder = new Hashtable<>();
            private int type;

            public int getViewType() {
                return this.type;
            }

            public void setViewType(int type) {
                this.type = type;
            }

            public void setView(String k, View v) {
                Log.d("SB", "holder value is : " + holder);
                holder.put(k, v);
            }

            public View getView(String k) {
                return holder.get(k);
            }

            public <T> T getView(String k, Class<T> type) {
                return type.cast(getView(k));
            }
        }
    }

    private static class initialize extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(String... params) {
            final String basicAuth = "Basic " + Base64.encodeToString(params[0].getBytes(), Base64.NO_WRAP);

            boolean success = false;
            String url = "https://weget-2015is203g2t2.rhcloud.com/webservice/" + "account/" + friendId+"/";

            String rst = UtilHttp.doHttpGetBasicAuthentication(mContext, url, basicAuth);
            if (rst == null) {
                err = UtilHttp.err;
                success = false;
            } else {


                try {

                    JSONObject jso = new JSONObject(rst);

                    friendNumber = jso.getInt("contactNo");



                } catch (JSONException e) {
                    e.printStackTrace();
                }
                success = true;
            }
            return success;
        }
        @Override
        protected void onPostExecute(Boolean result) {


            if(result){

            }else {
                Toast.makeText(mContext, err, Toast.LENGTH_SHORT).show();
            }

        }
    }
}