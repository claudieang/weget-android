package com.wegot.fuyan.fyp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sendbird.android.AdminMessage;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.FileMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.SendBird;
import com.sendbird.android.UserMessage;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by HP on 9/14/2016.
 */

public class UserChatAdapter extends BaseAdapter {
    private static final int TYPE_UNSUPPORTED = 0;
    private static final int TYPE_USER_MESSAGE = 1;
    private static final int TYPE_FILE_MESSAGE = 2;
    private static final int TYPE_ADMIN_MESSAGE = 3;

    private final Context mContext;
    private final LayoutInflater mInflater;
    private final ArrayList<Object> mItemList;
    private final GroupChannel mGroupChannel;

    public UserChatAdapter(Context context, GroupChannel groupChannel) {
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mItemList = new ArrayList<Object>();
        mGroupChannel = groupChannel;
    }

    public void delete(Object message) {
        mItemList.remove(message);
    }

    @Override
    public int getCount() {
        return mItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return mItemList.get(position);
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
        Log.d("UCA","it went through the adapter");
        mItemList.add(message);
    }


    @Override
    public int getItemViewType(int position) {
        Object item = mItemList.get(position);
        Log.d("UCA", "item has a value of : " + item);
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
        Log.d("UCA", "check item at line 104 : " + item)    ;

        if (convertView == null || ((ViewHolder) convertView.getTag()).getViewType() != getItemViewType(position)) {
            viewHolder = new ViewHolder();
            viewHolder.setViewType(getItemViewType(position));

            switch (getItemViewType(position)) {
                case TYPE_UNSUPPORTED:
                    convertView = new View(mInflater.getContext());
                    convertView.setTag(viewHolder);
                    break;
                case TYPE_USER_MESSAGE: {
                    Log.d("UCA", "Went through TYPE_USER_MESSAGE");


                    TextView tv;
                    ImageView iv;
                    View v;

                    convertView = mInflater.inflate(R.layout.activity_view_group_user_message, parent, false);
                    tv = (TextView) convertView.findViewById(R.id.txt_message);

                    v = convertView.findViewById(R.id.left_container_friend);
                    viewHolder.setView("left_container", v);
                    iv = (ImageView) convertView.findViewById(R.id.img_left_profile_pic_friend);
                    viewHolder.setView("left_thumbnail", iv);
                    tv = (TextView) convertView.findViewById(R.id.txt_left_friend);
                    viewHolder.setView("left_message", tv);
                    tv = (TextView) convertView.findViewById(R.id.txt_left_name_friend);
                    viewHolder.setView("left_name", tv);
                    tv = (TextView) convertView.findViewById(R.id.txt_left_time_friend);
                    viewHolder.setView("left_time", tv);

                    v = convertView.findViewById(R.id.right_container_user);
                    viewHolder.setView("right_container", v);
                    iv = (ImageView) convertView.findViewById(R.id.img_right_profile_pic_user);
                    viewHolder.setView("right_thumbnail", iv);
                    tv = (TextView) convertView.findViewById(R.id.txt_right_user);
                    viewHolder.setView("right_message", tv);
                    tv = (TextView) convertView.findViewById(R.id.txt_right_name);
                    viewHolder.setView("right_name", tv);
                    tv = (TextView) convertView.findViewById(R.id.txt_right_time_user);
                    viewHolder.setView("right_time", tv);
                    //tv = (TextView) convertView.findViewById(R.id.txtright);
                    //viewHolder.setView("right_status", tv);

                    convertView.setTag(viewHolder);
                    break;
                }
                case TYPE_ADMIN_MESSAGE: {
                    convertView = mInflater.inflate(R.layout.activity_view_group_user_message, parent, false);
                    viewHolder.setView("message", convertView.findViewById(R.id.txt_message));
                    convertView.setTag(viewHolder);
                    break;
                }
                case TYPE_FILE_MESSAGE: {
                    TextView tv;

                    convertView = mInflater.inflate(R.layout.activity_view_group_user_message, parent, false);
                    tv = (TextView) convertView.findViewById(R.id.txt_left_friend);
                    viewHolder.setView("txt_sender_name", tv);

                    viewHolder.setView("img_left_profile_pic_friend", convertView.findViewById(R.id.img_left_profile_pic_friend));
                    viewHolder.setView("txt_left_name_friend", convertView.findViewById(R.id.txt_left_name_friend));
                    viewHolder.setView("txt_left_time_friend", convertView.findViewById(R.id.txt_left_time_friend));
                    viewHolder.setView("txt_left_time_friend", convertView.findViewById(R.id.txt_left_time_friend));

//                    viewHolder.setView("image_container", convertView.findViewById(R.id.image_container));
//                    viewHolder.setView("img_thumbnail", convertView.findViewById(R.id.img_thumbnail));
//                    viewHolder.setView("txt_image_size", convertView.findViewById(R.id.txt_image_size));
//
//                    viewHolder.setView("file_container", convertView.findViewById(R.id.file_container));
//                    viewHolder.setView("txt_file_name", convertView.findViewById(R.id.txt_file_name));
//                    viewHolder.setView("txt_file_size", convertView.findViewById(R.id.txt_file_size));

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

                    //ThemedSpinnerAdapter.Helper.displayUrlImage(viewHolder.getView("right_thumbnail", ImageView.class), message.getSender().getProfileUrl(), true);
                    viewHolder.getView("right_name", TextView.class).setText(message.getSender().getNickname());
                    viewHolder.getView("right_message", TextView.class).setText(message.getMessage());
                    //viewHolder.getView("right_time", TextView.class).setText(Helper.getDisplayDateTime(mContext, message.getCreatedAt()));

                    int unreadCount = mGroupChannel.getReadReceipt(message);
                    if (unreadCount > 1) {
                        //viewHolder.getView("right_status", TextView.class).setText("Unread " + unreadCount);
                    } else if (unreadCount == 1) {
                        //viewHolder.getView("right_status", TextView.class).setText("Unread");
                    } else {
                        //viewHolder.getView("right_status", TextView.class).setText("");
                    }

                } else {
                    viewHolder.getView("left_container", View.class).setVisibility(View.VISIBLE);
                    viewHolder.getView("right_container", View.class).setVisibility(View.GONE);

                    //Helper.displayUrlImage(viewHolder.getView("left_thumbnail", ImageView.class), message.getSender().getProfileUrl(), true);
                    viewHolder.getView("left_name", TextView.class).setText(message.getSender().getNickname());
                    viewHolder.getView("left_message", TextView.class).setText(message.getMessage());
                    //viewHolder.getView("left_time", TextView.class).setText(Helper.getDisplayDateTime(mContext, message.getCreatedAt()));
                }
                break;
            case TYPE_ADMIN_MESSAGE:
                AdminMessage adminMessage = (AdminMessage) item;
                viewHolder.getView("message", TextView.class).setText(Html.fromHtml(adminMessage.getMessage()));
                break;
            case TYPE_FILE_MESSAGE:
//                final FileMessage fileLink = (FileMessage) item;
//
//                viewHolder.getView("txt_sender_name", TextView.class).setText(Html.fromHtml("<font color='#824096'><b>" + fileLink.getSender().getNickname() + "</b></font>: "));
//                if (fileLink.getType().toLowerCase().startsWith("image")) {
//                    viewHolder.getView("file_container").setVisibility(View.GONE);
//
//                    viewHolder.getView("image_container").setVisibility(View.VISIBLE);
//                    viewHolder.getView("txt_image_name", TextView.class).setText(fileLink.getName());
//                    viewHolder.getView("txt_image_size", TextView.class).setText(Helper.readableFileSize(fileLink.getSize()));
//                    Helper.displayUrlImage(viewHolder.getView("img_thumbnail", ImageView.class), fileLink.getUrl());
//                } else {
//                    viewHolder.getView("image_container").setVisibility(View.GONE);
//
//                    viewHolder.getView("file_container").setVisibility(View.VISIBLE);
//                    viewHolder.getView("txt_file_name", TextView.class).setText(fileLink.getName());
//                    viewHolder.getView("txt_file_size", TextView.class).setText("" + fileLink.getSize());
//                }
//                viewHolder.getView("img_file_container").setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        new AlertDialog.Builder(mContext)
//                                .setTitle("SendBird")
//                                .setMessage("Do you want to download this file?")
//                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        try {
//                                            Helper.downloadUrl(fileLink.getUrl(), fileLink.getName(), mContext);
//                                        } catch (IOException e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                })
//                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                    }
//                                })
//                                .create()
//                                .show();
//                    }
//                });
                break;
        }

        return convertView;
    }

        private class ViewHolder {
            private Hashtable<String, View> holder = new Hashtable<String, View>();
            private int type;

            public int getViewType() {
                return this.type;
            }

            public void setViewType(int type) {
                this.type = type;
            }
            public void setView(String k, View v) {
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

