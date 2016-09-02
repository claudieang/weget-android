package com.wegot.fuyan.fyp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FU YAN on 7/13/2016.
 */
public class AccountAdapter extends ArrayAdapter {
    List list = new ArrayList();

    public AccountAdapter(Context context, int resource) {
        super(context, resource);
    }
    static class DataHandler{
        ImageView accountImage;
        TextView accountName;
        TextView accountContact;
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
        return this.list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;

        row = convertView;
        DataHandler handler;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.row_layout,parent,false);
            handler = new DataHandler();
            handler.accountImage = (ImageView)row.findViewById(R.id.request_image);
            handler.accountName = (TextView)row.findViewById(R.id.request_title);
            handler.accountContact = (TextView)row.findViewById(R.id.request_requirement);
            row.setTag(handler);
        }else{
            handler = (DataHandler)row.getTag();
        }
        Account account;
        account = (Account) this.getItem(position);

        if(account.getPicture().equals("")){
            handler.accountImage.setImageResource(R.drawable.defaultdp);
        }else{
            byte[] decodeString = Base64.decode(account.getPicture(), Base64.NO_WRAP);
            Bitmap decodebitmap = BitmapFactory.decodeByteArray(
                    decodeString, 0, decodeString.length);
            handler.accountImage.setImageBitmap(decodebitmap);
        }
        handler.accountName.setText("User : " + account.getUsername());
        handler.accountContact.setText("Contact : " + account.getContactNo());

        return row;
    }
}
