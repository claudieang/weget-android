package com.weget.fuyan.fyp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sendbird.android.BaseMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HP on 9/12/2016.
 */
public class ChatRowAdapter extends ArrayAdapter {
    List list = new ArrayList();
    String currUserId;

    public ChatRowAdapter(Context context, int resource) {
        super(context, resource);
    }
    static class DataHandler{
        ImageView groupImage;
        TextView friendUsername;
        TextView chatDateTime;
        TextView lastMessageSent;
        TextView unreadMessages;
    }

    @Override
    public void add(Object object) {
        super.add(object);
        list.add(object);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public Object getItem(int position) {
        Log.d("Print", "Lemme check here yo : " + list.size());
        return this.list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;

        row = convertView;
        DataHandler handler;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.row_layout_view_group_channel,parent,false);
            handler = new DataHandler();
            handler.groupImage = (ImageView)row.findViewById(R.id.group_img);
            handler.friendUsername = (TextView)row.findViewById(R.id.txt_username);
            handler.chatDateTime = (TextView)row.findViewById(R.id.txt_date);
            handler.lastMessageSent = (TextView)row.findViewById(R.id.txt_desc);
            handler.unreadMessages = (TextView)row.findViewById(R.id.txt_unread_count);
            row.setTag(handler);
        }else{
            handler = (DataHandler)row.getTag();
        }
        GroupChannel gc;
        gc = (GroupChannel) this.getItem(position);

        List<User> userList = (ArrayList)gc.getMembers();


        String name = "nonexisting";

        for(User u: userList){
            String id = u.getUserId();
            if(!currUserId.equals(id)){
                name = u.getNickname();
            }
        }



//        if(account.getPicture().equals("")){
//            //need to set to the requestors image for visual cue
//            handler.groupImage.setImageResource(R.drawable.defaultdp);
//        }else{
//            byte[] decodeString = Base64.decode(account.getPicture(), Base64.NO_WRAP);
//            Bitmap decodebitmap = BitmapFactory.decodeByteArray(
//                    decodeString, 0, decodeString.length);
//            handler.groupImage.setImageBitmap(decodebitmap);
//        }
        handler.friendUsername.setText("User : " + name);
        handler.chatDateTime.setText("Time");
        handler.lastMessageSent.setText("Hi");
        handler.unreadMessages.setText("1");


        return row;
    }

    public void appendMessage(BaseMessage baseMessage){

    }

    public void insertMessage(BaseMessage baseMessage){

    }

    public void addCurrentUserId(String id){
        this.currUserId = id;
    }


}
