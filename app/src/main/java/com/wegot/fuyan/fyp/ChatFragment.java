package com.wegot.fuyan.fyp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.PreviousMessageListQuery;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.sendbird.android.UserMessage;

import java.util.List;

/**
 * Created by HP on 9/13/2016.
 */
public class ChatFragment extends Fragment{

    private static final int REQUEST_PICK_IMAGE = 100;
    private static final String identifier = "SendBirdOpenChat";

    //Context context = this;
    String username;
    String password;
    int myId;
    String authString;


    private ListView mListView;

    ChatRowAdapter mAdapter;
    private EditText mEtxtUserInput;
    private Button mBtnSend;
    private ImageButton mBtnUpload;
    private ProgressBar mProgressBtnUpload;
    private String mChannelUrl;
    private GroupChannel mGroupChannel;
    private PreviousMessageListQuery mPrevMessageListQuery;
    private boolean isUploading;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_view_group_user_message, container, false);

        mChannelUrl = getArguments().getString("channel_url");

        initUIComponents(rootView);
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isUploading) {
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
                        mAdapter.appendMessage(baseMessage);
                        mAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onReadReceiptUpdated(GroupChannel groupChannel) {
                }

                @Override
                public void onTypingStatusUpdated(GroupChannel groupChannel) {
                }
            });

            if (SendBird.getConnectionState() != SendBird.ConnectionState.OPEN) {
                SendBird.connect("12", new SendBird.ConnectHandler() {
                    @Override
                    public void onConnected(User user, SendBirdException e) {
                        if (e != null) {
                            Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        //enterChannel(mChannelUrl);

                    }
                });
            } else {
                // enterChannel(mChannelUrl);
            }
        } else {
            isUploading = false;
        }
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

//    private void enterChannel(String channelUrl) {
//        GroupChannel.getChannel(channelUrl, new GroupChannel.GroupChannelGetHandler() {
//            @Override
//            public void onResult(final GroupChannel groupChannel, SendBirdException e) {
//                if(e != null) {
//                    Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                GroupChannel.enter(new Gr.OpenChannelEnterHandler() {
//                    @Override
//                    public void onResult(SendBirdException e) {
//                        if(e != null) {
//                            Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//
//                        mOpenChannel = openChannel;
//                        ((TextView)getActivity().findViewById(R.id.txt_channel_url)).setText(openChannel.getName());
//
//                        loadPrevMessages(true);
//                    }
//                });
//            }
//        });
//
//    }

    private void initUIComponents(View rootView) {

        //initialize row layouts

        //mAdapter = new UserChatAdapter(getActivity());

//        mListView = (ListView) rootView.findViewById(R.id.list);
//        turnOffListViewDecoration(mListView);
//        mListView.setAdapter(mAdapter);
//
//        mBtnSend = (Button) rootView.findViewById(R.id.btn_send);
//        //mBtnUpload = (ImageButton)rootView.findViewById(R.id.btn_upload);
//        //mProgressBtnUpload = (ProgressBar)rootView.findViewById(R.id.progress_btn_upload);
//        mEtxtMessageUser = (TextView) rootView.findViewById(R.id.txt_right_user);
//        mEtxtMessageFriend = (TextView) rootView.findViewById(R.id.txt_left_friend);
//        mTviewTimeUser = (TextView) rootView.findViewById(R.id.txt_right_time_user);
//        mTviewTimeFriend = (TextView) rootView.findViewById(R.id.txt_left_time_friend);
//        mTviewFriendName = (TextView) rootView.findViewById(R.id.txt_left_name_friend);
//        mTViewUserName = (TextView) rootView.findViewById(R.id.txt_right_name);
//        mEtxtUserInput = (EditText) rootView.findViewById(R.id.txt_message);
//

        mBtnSend.setEnabled(false);
        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });


//        mBtnUpload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(Helper.requestReadWriteStoragePermissions(getActivity())) {
//                    isUploading = true;
//
//                    Intent intent = new Intent();
//                    intent.setType("image/*");
//                    intent.setAction(Intent.ACTION_GET_CONTENT);
//                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_PICK_IMAGE);
//                }
//            }
//        });

        mEtxtUserInput.setOnKeyListener(new View.OnKeyListener() {
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
        mEtxtUserInput.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        mEtxtUserInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mBtnSend.setEnabled(s.length() > 0);
            }
        });

//        mListView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                Helper.hideKeyboard(getActivity());
//                return false;
//            }
//        });

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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
//            if(requestCode == REQUEST_PICK_IMAGE && data != null && data.getData() != null) {
//                upload(data.getData());
//            }
        }
    }

    private void send() {
        mGroupChannel.sendUserMessage(mEtxtUserInput.getText().toString(), new BaseChannel.SendUserMessageHandler() {
            @Override
            public void onSent(UserMessage userMessage, SendBirdException e) {
                if (e != null) {
                    Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                mAdapter.appendMessage(userMessage);
                mAdapter.notifyDataSetChanged();

                mEtxtUserInput.setText("");
            }
        });


    }
}
