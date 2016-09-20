package com.wegot.fuyan.fyp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

import java.util.ArrayList;

/**
 * Created by Claudie on 9/3/16.
 */
public class MainActivity extends AppCompatActivity {

    ListView mDrawerList;
    RelativeLayout mDrawerPane;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        SpannableString s = new SpannableString("Weget");
        s.setSpan(new TypefaceSpan(this, "Roboto-Regular.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        // Update the action bar title with the TypefaceSpan instance
        //ActionBar actionBar = getActionBar();
        getSupportActionBar().setTitle(s);

        mNavItems.add(new NavItem("Profile", "Edit your profile", R.drawable.ic_account_circle_black_36dp));
        mNavItems.add(new NavItem("Settings", "Manage your preferences", R.drawable.ic_settings_black_24dp));
        mNavItems.add(new NavItem("About", "Get to know Weget", R.drawable.ic_info_outline_black_24dp));
        mNavItems.add(new NavItem("Logout", "Sign out from Weget", R.drawable.ic_exit_to_app_black_24dp));

        // DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        // Populate the Navigtion Drawer with options
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        mDrawerList = (ListView) findViewById(R.id.navList);
        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
        mDrawerList.setAdapter(adapter);

        // Drawer Item click listeners
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFromDrawer(position);
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.app_name, R.string.app_name);

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        String username = pref.getString("username", null);


        TextView nav_tv = (TextView) findViewById(R.id.userName);
        nav_tv.setText(username);
        //set profile pic
        String profilePicture = pref.getString("picture", null);
        ImageView profileImage = (ImageView)findViewById(R.id.avatar);;

        if(profilePicture.equals("")){
            profileImage.setImageResource(R.drawable.ic_profile);
        }else{

            byte[] decodeString = Base64.decode(profilePicture, Base64.NO_WRAP);
            Bitmap decodebitmap = BitmapFactory.decodeByteArray(
                    decodeString, 0, decodeString.length);
            RoundedBitmapDrawable roundDrawable = RoundedBitmapDrawableFactory.create(getResources(), decodebitmap);
            roundDrawable.setCircular(true);
            profileImage.setImageDrawable(roundDrawable);
        }

        BottomNavigationBar bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);

        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.ic_home_black_24dp, "Home"))
                .addItem(new BottomNavigationItem(R.drawable.ic_shopping_basket_black_24dp, "My Requests"))
                .addItem(new BottomNavigationItem(R.drawable.ic_loupe_black_24dp, "Create Request"))
                .addItem(new BottomNavigationItem(R.drawable.ic_directions_run_black_24dp, "My Fulfills"))
                .addItem(new BottomNavigationItem(R.drawable.ic_chat_black_24dp, "Chat"))
                .setMode(BottomNavigationBar.MODE_SHIFTING)
                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC)
                .setActiveColor("#00B0FF")
                .setInActiveColor("#CCCCCC")
                .setBarBackgroundColor("#ECECEC")
                .initialise();


        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener(){
            @Override
            public void onTabSelected(int position) {
                switch(position){
                    case 0:
                        SpannableString s = new SpannableString("Weget");
                        s.setSpan(new TypefaceSpan(getApplicationContext(), "Roboto-Regular.ttf"), 0, s.length(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        getSupportActionBar().setTitle(s);
                        HomeFragment homeFragment = new HomeFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_id, homeFragment).commit();
                        break;
                    case 1:
                        SpannableString s1 = new SpannableString("My Requests");
                        s1.setSpan(new TypefaceSpan(getApplicationContext(), "Roboto-Regular.ttf"), 0, s1.length(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        getSupportActionBar().setTitle(s1);
                        RequestFragment requestFragment = new RequestFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_id, requestFragment).commit();
                        //Intent i = new Intent(MainActivity.this, RequestFragment.class);
                        //startActivity(i);
                        break;
                    case 2:
                        startActivity(new Intent(MainActivity.this, CreateRequestActivity.class));
                        break;
                    case 3:
                        SpannableString s2 = new SpannableString("My Fulfills");
                        s2.setSpan(new TypefaceSpan(getApplicationContext(), "Roboto-Regular.ttf"), 0, s2.length(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        getSupportActionBar().setTitle(s2);
                        FulfillFragment fulfillFragment = new FulfillFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_id, fulfillFragment).commit();
                        break;
                    case 4:
                        SpannableString s3 = new SpannableString("Chat");
                        s3.setSpan(new TypefaceSpan(getApplicationContext(), "Roboto-Regular.ttf"), 0, s3.length(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        getSupportActionBar().setTitle(s3);
                        break;

                }
            }
            @Override
            public void onTabUnselected(int position) {
            }
            @Override
            public void onTabReselected(int position) {
            }
        });
        HomeFragment homeFragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_id, homeFragment).commit();

        Intent i = getIntent();
        int tabToOpen = i.getIntExtra("after_payment_request_tab",-1);
        int tabToOpen2 = i.getIntExtra("after_received_tab",-1);
        int tabToOpen3 = i.getIntExtra("after_delivered_tab",-1);
        int tabToOpen4 = i.getIntExtra("after_dispute_tab",-1);
        int tabToOpen5 = i.getIntExtra("after_dispute_fulfill_tab",-1);
        int tabToOpen6 = i.getIntExtra("updated_request_tab", -1);
        int tabToOpen7 = i.getIntExtra("accepted_fulfill_tab", -1);
        int tabToOpen8 = i.getIntExtra("after_dispute_request_tab", -1);



        if(tabToOpen!=-1){
            bottomNavigationBar.selectTab(tabToOpen);
        }
        if(tabToOpen2!=-1){
            bottomNavigationBar.selectTab(tabToOpen2);
        }
        if(tabToOpen3!=-1){
            bottomNavigationBar.selectTab(tabToOpen3);
        }
        if(tabToOpen4!=-1){
            bottomNavigationBar.selectTab(tabToOpen4);
        }
        if(tabToOpen5!=-1){
            bottomNavigationBar.selectTab(tabToOpen5);
        }
        if(tabToOpen6!=-1){
            bottomNavigationBar.selectTab(tabToOpen6);
        }
        if(tabToOpen7!=-1){
            bottomNavigationBar.selectTab(tabToOpen7);
        }
        if(tabToOpen8!=-1){
            bottomNavigationBar.selectTab(tabToOpen8);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void selectItemFromDrawer(int position) {

        switch(position){

            case 0:
                startActivity(new Intent(this, ProfileActivity.class));
                break;
            case 1:
                startActivity(new Intent(this, update_bank_details.class));
                break;
            case 2:

                break;
            case 3:
                Intent intent = new Intent(this,LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
        }

        mDrawerList.setItemChecked(position, true);
        //setTitle(mNavItems.get(position).mTitle);

        // Close the drawer
        mDrawerLayout.closeDrawer(mDrawerPane);
    }

}
