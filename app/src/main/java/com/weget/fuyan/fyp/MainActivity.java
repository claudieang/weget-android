package com.weget.fuyan.fyp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;

import java.util.ArrayList;

/**
 * Created by Claudie on 9/3/16. SHAFIQ SUPPORTS!!!
 */
public class MainActivity extends AppCompatActivity {

    ListView mDrawerList;
    RelativeLayout mDrawerPane;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private int userID;
    Context mContext;
    private String err, password, authString, username;
    ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();
    String URL;
    private Menu optionsMenu;
    private HomeFragment homeFragment;
    final String FAQURL = "https://weget-2015is203g2t2.rhcloud.com/weget/faq";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Log.d("sigh", "checking for activity behaviour");


        SpannableString s = new SpannableString("Weget");
        s.setSpan(new TypefaceSpan(this, "Roboto-Regular.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        // Update the action bar title with the TypefaceSpan instance
        //ActionBar actionBar = getActionBar();
        getSupportActionBar().setTitle(s);

        URL = getString(R.string.webserviceurl);

        mNavItems.add(new NavItem("PROFILE", "Edit your profile", R.drawable.ic_account_circle_black_36dp));
        mNavItems.add(new NavItem("BANK SETTINGS", "Manage your bank accounts", R.drawable.ic_attach_money_white_36dp));
        mNavItems.add(new NavItem("ABOUT", "Get to know Weget", R.drawable.ic_info_outline_black_24dp));
        mNavItems.add(new NavItem("LOGOUT", "Sign out from Weget", R.drawable.ic_exit_to_app_black_24dp));
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

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name, R.string.app_name);

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        username = pref.getString("username", null);

        userID = pref.getInt("id", 0);
        password = pref.getString("password", null);
        authString = username + ":" + password;
        initSendBird();


        TextView nav_tv = (TextView) findViewById(R.id.userName);
        nav_tv.setText(username);
        //set profile pic
        String profilePicture = pref.getString("picture", null);
        ImageView profileImage = (ImageView) findViewById(R.id.avatar);

        //Check whether location is enabled
        LocationManager lm = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setMessage("GPS Network has not been enabled.");
            dialog.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            AlertDialog alt = dialog.create();
            alt.show();
        }



        if (profilePicture.equals("")) {
//            profileImage.setImageResource(R.drawable.ic_account_circle_black_36dp);
//            profileImage.setImageTintMode(new );
        } else {
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
                .addItem(new BottomNavigationItem(R.drawable.ic_loupe_black_24dp, "Create Requests"))
                .addItem(new BottomNavigationItem(R.drawable.ic_directions_run_black_24dp, "My Fulfills"))
                .addItem(new BottomNavigationItem(R.drawable.ic_chat_black_24dp, "Chat"))
                .setMode(BottomNavigationBar.MODE_SHIFTING)
                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC)
                .setActiveColor("#00B0FF")
                .setInActiveColor("#CCCCCC")
                .setBarBackgroundColor("#ECECEC")
                .initialise();


        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {

            @Override
            public void onTabSelected(int position) {


                switch (position) {
                    case 0:
                        SpannableString s = new SpannableString("Weget");
                        s.setSpan(new TypefaceSpan(getApplicationContext(), "Roboto-Regular.ttf"), 0, s.length(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        getSupportActionBar().setTitle(s);
                        //HomeFragment homeFragment = new HomeFragment();
                        //MenuItem refresh = optionsMenu.findItem(R.id.action_refresh);
                        //refresh.setVisible(true);
                        if (getSupportFragmentManager().findFragmentByTag("home") != null) {
                            getSupportFragmentManager().beginTransaction()
                                    .show(getSupportFragmentManager().findFragmentByTag("home")).commit();
                        }
                        if (getSupportFragmentManager().findFragmentByTag("request") != null) {
                            getSupportFragmentManager().beginTransaction()
                                    .hide(getSupportFragmentManager().findFragmentByTag("request")).commit();
                        }

                        if (getSupportFragmentManager().findFragmentByTag("fulfill") != null) {
                            getSupportFragmentManager().beginTransaction()
                                    .hide(getSupportFragmentManager().findFragmentByTag("fulfill")).commit();
                        }

                        if (getSupportFragmentManager().findFragmentByTag("chat") != null) {
                            getSupportFragmentManager().beginTransaction()
                                    .hide(getSupportFragmentManager().findFragmentByTag("chat")).commit();
                        }

                        break;
                    case 1:
                        SpannableString s1 = new SpannableString("My Requests");
                        s1.setSpan(new TypefaceSpan(getApplicationContext(), "Roboto-Regular.ttf"), 0, s1.length(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        getSupportActionBar().setTitle(s1);

                        if (getSupportFragmentManager().findFragmentByTag("home") != null) {
                            getSupportFragmentManager().beginTransaction()
                                    .hide(getSupportFragmentManager().findFragmentByTag("home")).commit();
                        }
                        if (getSupportFragmentManager().findFragmentByTag("request") != null) {
                            getSupportFragmentManager().beginTransaction()
                                    .show(getSupportFragmentManager().findFragmentByTag("request")).commit();

                        } else {
                            getSupportFragmentManager().beginTransaction()
                                    .add(R.id.content_id, new RequestFragment(), "request")
                                    //.addToBackStack(null)
                                    .commit();
                        }

                        if (getSupportFragmentManager().findFragmentByTag("fulfill") != null) {
                            getSupportFragmentManager().beginTransaction()
                                    .hide(getSupportFragmentManager().findFragmentByTag("fulfill")).commit();
                        }

                        if (getSupportFragmentManager().findFragmentByTag("chat") != null) {
                            getSupportFragmentManager().beginTransaction()
                                    .hide(getSupportFragmentManager().findFragmentByTag("chat")).commit();
                        }
                        //getSupportFragmentManager().findFragmentByTag()
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
                        //FulfillFragment fulfillFragment = new FulfillFragment();
                        //MenuItem refresh3 = optionsMenu.findItem(R.id.action_refresh);
                        //refresh3.setVisible(false);
                        if (getSupportFragmentManager().findFragmentByTag("home") != null) {
                            getSupportFragmentManager().beginTransaction()
                                    .hide(getSupportFragmentManager().findFragmentByTag("home")).commit();
                        }
                        if (getSupportFragmentManager().findFragmentByTag("request") != null) {
                            getSupportFragmentManager().beginTransaction()
                                    .hide(getSupportFragmentManager().findFragmentByTag("request")).commit();
                        }
                        if (getSupportFragmentManager().findFragmentByTag("fulfill") != null) {
                            getSupportFragmentManager().beginTransaction()
                                    .show(getSupportFragmentManager().findFragmentByTag("fulfill")).commit();

                        } else {
                            getSupportFragmentManager().beginTransaction()
                                    .add(R.id.content_id, new FulfillFragment(), "fulfill")
                                    //.addToBackStack(null)
                                    .commit();
                        }

                        //getSupportFragmentManager().beginTransaction()
                        //        .show(getSupportFragmentManager().findFragmentByTag("fulfill")).commit();
                        if (getSupportFragmentManager().findFragmentByTag("chat") != null) {
                            getSupportFragmentManager().beginTransaction()
                                    .hide(getSupportFragmentManager().findFragmentByTag("chat")).commit();
                        }
                        break;
                    case 4:

                        SpannableString s3 = new SpannableString("Chat");
                        s3.setSpan(new TypefaceSpan(getApplicationContext(), "Roboto-Regular.ttf"), 0, s3.length(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        getSupportActionBar().setTitle(s3);


                        //MenuItem refresh1 = optionsMenu.findItem(R.id.action_refresh);
                        //refresh1.setVisible(false);
                        if (getSupportFragmentManager().findFragmentByTag("home") != null) {
                            getSupportFragmentManager().beginTransaction()
                                    .hide(getSupportFragmentManager().findFragmentByTag("home")).commit();
                        }
                        if (getSupportFragmentManager().findFragmentByTag("request") != null) {
                            getSupportFragmentManager().beginTransaction()
                                    .hide(getSupportFragmentManager().findFragmentByTag("request")).commit();
                        }
                        if (getSupportFragmentManager().findFragmentByTag("fulfill") != null) {
                            getSupportFragmentManager().beginTransaction()
                                    .hide(getSupportFragmentManager().findFragmentByTag("fulfill")).commit();
                        }
//                        getSupportFragmentManager().beginTransaction()
//                                .show(getSupportFragmentManager().findFragmentByTag("chat")).commit();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content_id_2, new ChatFragment(), "chat")
                                //.addToBackStack(null)
                                .commit();

                        //startActivity(new Intent(MainActivity.this, ChatActivity.class));
                        break;

                }
            }

            @Override
            public void onTabUnselected(int position) {
            }

            @Override
            public void onTabReselected(int position) {
                if(position == 2){
                    //if reselect create a request when highlighted
                    startActivity(new Intent(MainActivity.this, CreateRequestActivity.class));
                }
            }
        });

        getSupportFragmentManager().beginTransaction()
                .add(R.id.content_id, new HomeFragment(), "home")
                .commit();

        Intent i = getIntent();
        int tabToOpen = i.getIntExtra("after_payment_request_tab", -1);
        int tabToOpen2 = i.getIntExtra("after_received_tab", -1);
        int tabToOpen3 = i.getIntExtra("after_delivered_tab", -1);
        int tabToOpen4 = i.getIntExtra("after_dispute_tab", -1);
        int tabToOpen5 = i.getIntExtra("after_dispute_fulfill_tab", -1);
        int tabToOpen6 = i.getIntExtra("updated_request_tab", -1);
        int tabToOpen7 = i.getIntExtra("accepted_fulfill_tab", -1);
        int tabToOpen8 = i.getIntExtra("after_dispute_request_tab", -1);

        int notificationRequesttab = i.getIntExtra("notification_request_tab",-1);
        int notification_fulfill_tab = i.getIntExtra("notification_fulfill_tab",-1);
        int notification_chat_tab = i.getIntExtra("notification_chat_tab",-1);



        if (tabToOpen != -1) {
            bottomNavigationBar.selectTab(tabToOpen);
        }
        if (tabToOpen2 != -1) {
            bottomNavigationBar.selectTab(tabToOpen2);
        }
        if (tabToOpen3 != -1) {
            bottomNavigationBar.selectTab(tabToOpen3);
        }
        if (tabToOpen4 != -1) {
            bottomNavigationBar.selectTab(tabToOpen4);
        }
        if (tabToOpen5 != -1) {
            bottomNavigationBar.selectTab(tabToOpen5);
        }
        if (tabToOpen6 != -1) {
            bottomNavigationBar.selectTab(tabToOpen6);
        }
        if (tabToOpen7 != -1) {
            bottomNavigationBar.selectTab(tabToOpen7);
        }
        if (tabToOpen8 != -1) {
            bottomNavigationBar.selectTab(tabToOpen8);
        }

        if (notificationRequesttab != -1) {
            bottomNavigationBar.selectTab(notificationRequesttab);
            Log.d("","Testing Notification-------");
        }
        if (notification_fulfill_tab != -1) {
            bottomNavigationBar.selectTab(notification_fulfill_tab);
            Log.d("","Testing Notification-------");
        }
        if(notification_chat_tab!=-1){
            bottomNavigationBar.selectTab(notification_chat_tab);
            Log.d("","Chat Notification-------");
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        //invalidateOptionsMenu();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        String profilePicture = pref.getString("picture", null);
        ImageView profileImage = (ImageView) findViewById(R.id.avatar);


        if (profilePicture.equals("")) {
            profileImage.setImageResource(R.drawable.ic_profile);
        } else {

            byte[] decodeString = Base64.decode(profilePicture, Base64.NO_WRAP);
            Bitmap decodebitmap = BitmapFactory.decodeByteArray(
                    decodeString, 0, decodeString.length);
            RoundedBitmapDrawable roundDrawable = RoundedBitmapDrawableFactory.create(getResources(), decodebitmap);
            roundDrawable.setCircular(true);
            profileImage.setImageDrawable(roundDrawable);
        }


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.optionsMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_refresh, menu);
        return super.onCreateOptionsMenu(menu);
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
                startActivity(new Intent(this, UpdateBankDetails.class));
                break;

            case 2:
                Uri uriUrl = Uri.parse(FAQURL);
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
                break;

            case 3:

                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
                username = pref.getString("username", null);
                password = pref.getString("password", null);
                authString  = username + ":" + password;

                new logout().execute(authString);
                break;
        }

        mDrawerList.setItemChecked(position, true);
        //setTitle(mNavItems.get(position).mTitle);

        // Close the drawer
        mDrawerLayout.closeDrawer(mDrawerPane);
    }


    private class logout extends AsyncTask<String, Void, Boolean> {


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(String... params) {

            final String basicAuth = "Basic " + Base64.encodeToString(params[0].getBytes(), Base64.NO_WRAP);

            boolean success = false;
            String url = URL + "account/" + userID + "/logout/";

            String rst = UtilHttp.doHttpGetBasicAuthentication(mContext, url, basicAuth);
            if (rst == null) {
                err = UtilHttp.err;
                success = false;
            } else {
                success = true;
            }
            return success;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result == true) {
                //disconnect from sendbird
                SendBird.disconnect(new SendBird.DisconnectHandler() {
                    @Override
                    public void onDisconnected() {
                        Log.d("SB", "sendbird disconnected");
                    }
                });

                //clear the sharedpreferences
                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();

                editor.clear();
                editor.commit();

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();

            }


        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("on received", intent.getAction());
    }

    public void initSendBird(){
        Log.d("mainn","userid is now : " + userID);
        SendBird.init("0ABD752F-9D9A-46DE-95D5-37A00A1B3958", getApplication().getApplicationContext());
        SendBird.connect(userID+"", new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {
                Log.d("main","Sendbird has connected!");
                if (e != null) {
                    Toast.makeText(getApplicationContext(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                //onconnected update information
                Log.d("mainn","Sendbird has a connection state of : " + SendBird.getConnectionState());
                SendBird.updateCurrentUserInfo(username, null, new SendBird.UserInfoUpdateHandler() {
                    @Override
                    public void onUpdated(SendBirdException e) {
                        Log.d("mainn","username after update is now : " + username);
                        if (e != null) {
                            Toast.makeText(getApplicationContext(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });

                //sendbird notification
                if (FirebaseInstanceId.getInstance().getToken() == null) return;
                SendBird.registerPushTokenForCurrentUser(FirebaseInstanceId.getInstance().getToken(),
                        new SendBird.RegisterPushTokenWithStatusHandler() {
                            @Override
                            public void onRegistered(SendBird.PushTokenRegistrationStatus status, SendBirdException e) {
                                if (e != null) {
                                    // Error.
                                    return;
                                }
                            }
                        });
            }
        });

    }

}
