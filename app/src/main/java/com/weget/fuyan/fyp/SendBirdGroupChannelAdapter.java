package com.weget.fuyan.fyp;

import android.content.Context;
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
import com.sendbird.android.UserMessage;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by HP on 9/23/2016.
 */
public class SendBirdGroupChannelAdapter extends BaseAdapter {
    private final Context mContext;
    private final LayoutInflater mInflater;
    private final ArrayList<GroupChannel> mItemList;

    public SendBirdGroupChannelAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mItemList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mItemList.size();
    }

    @Override
    public GroupChannel getItem(int position) {
        return mItemList.get(position);
    }

    public void clear() {
        mItemList.clear();
    }

    public GroupChannel remove(int index) {
        return mItemList.remove(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addAll(List<GroupChannel> channels) {
        mItemList.addAll(channels);
    }

    public void replace(GroupChannel newChannel) {
        for (GroupChannel oldChannel : mItemList) {
            if (oldChannel.getUrl().equals(newChannel.getUrl())) {
                mItemList.remove(oldChannel);
                break;
            }
        }

        mItemList.add(0, newChannel);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = mInflater.inflate(R.layout.sendbird_view_group_channel, parent, false);
            viewHolder.setView("img_thumbnail", convertView.findViewById(R.id.img_thumbnail));
            viewHolder.setView("txt_topic", convertView.findViewById(R.id.txt_topic));
//                viewHolder.setView("txt_member_count", convertView.findViewById(R.id.txt_member_count));
            viewHolder.setView("txt_unread_count", convertView.findViewById(R.id.txt_unread_count));
            viewHolder.setView("txt_date", convertView.findViewById(R.id.txt_date));
            viewHolder.setView("txt_desc", convertView.findViewById(R.id.txt_desc));

            convertView.setTag(viewHolder);
        }

        GroupChannel item = getItem(position);
        viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.getView("img_thumbnail", ImageView.class).setImageResource(R.drawable.weget_logo);
        //Helper.displayUrlImage(, Helper.getDisplayCoverImageUrl(item.getMembers()));
        viewHolder.getView("txt_topic", TextView.class).setText(Helper.getDisplayMemberNames(item.getMembers(), false));

        if (item.getUnreadMessageCount() > 0) {
            viewHolder.getView("txt_unread_count", TextView.class).setVisibility(View.VISIBLE);
            viewHolder.getView("txt_unread_count", TextView.class).setText("" + item.getUnreadMessageCount());
        } else {
            viewHolder.getView("txt_unread_count", TextView.class).setVisibility(View.INVISIBLE);
        }

        //viewHolder.getView("txt_member_count", TextView.class).setVisibility(View.VISIBLE);
        //viewHolder.getView("txt_member_count", TextView.class).setText("" + item.getMemberCount());

        BaseMessage message = item.getLastMessage();
        if (message == null) {
            viewHolder.getView("txt_date", TextView.class).setText("");
            viewHolder.getView("txt_desc", TextView.class).setText("");
        } else if (message instanceof UserMessage) {
            viewHolder.getView("txt_date", TextView.class).setText(Helper.getDisplayTimeOrDate(mContext, message.getCreatedAt()));
            viewHolder.getView("txt_desc", TextView.class).setText(((UserMessage) message).getMessage());
        } else if (message instanceof AdminMessage) {
            viewHolder.getView("txt_date", TextView.class).setText(Helper.getDisplayTimeOrDate(mContext, message.getCreatedAt()));
            viewHolder.getView("txt_desc", TextView.class).setText(((AdminMessage) message).getMessage());
        } else if (message instanceof FileMessage) {
            viewHolder.getView("txt_date", TextView.class).setText(Helper.getDisplayTimeOrDate(mContext, message.getCreatedAt()));
            viewHolder.getView("txt_desc", TextView.class).setText("(FILE)");
        }

        return convertView;
    }

    private static class ViewHolder {
        private Hashtable<String, View> holder = new Hashtable<>();

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