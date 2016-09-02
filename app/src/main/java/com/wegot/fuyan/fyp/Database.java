package com.wegot.fuyan.fyp;

import android.provider.BaseColumns;

/**
 * Created by T.DW on 18/6/16.
 */
public class Database {
    public Database(){

    }

    public static abstract class TableInfo implements BaseColumns {
        public static final String ID = "id";
        public static final String USER_NAME = "username";
        public static final String USER_PASS = "password";
        public static final String CONTACT_NO = "contact_no";
        public static final String EMAIL = "email";
        public static final String DATABASE_NAME = "WeGet_DB";
        public static final String TABLE_NAME = "account";

    }

}
