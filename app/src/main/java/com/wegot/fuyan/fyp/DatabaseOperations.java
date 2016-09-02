package com.wegot.fuyan.fyp;

/**
 * Created by T.DW on 18/6/16.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;
import android.util.Log;


public class DatabaseOperations extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public String CREATE_QUERY = "CREATE TABLE "+Database.TableInfo.TABLE_NAME+"("+ Database.TableInfo.ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+Database.TableInfo.USER_NAME+" TEXT,"+ Database.TableInfo.USER_PASS+" TEXT,"+Database.TableInfo.CONTACT_NO+" INTEGER,"+Database.TableInfo.EMAIL+" TEXT);";

    public DatabaseOperations(Context context) {
        super(context, Database.TableInfo.DATABASE_NAME, null, DATABASE_VERSION);
        Log.d("Database Operations", "Database Created");
    }

    @Override
    public void onCreate(SQLiteDatabase sdb) {
        sdb.execSQL(CREATE_QUERY);
        Log.d("Database Operations", "Table Created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void putInformation(DatabaseOperations dop, String name, String pass, int contact_no, String email){
        SQLiteDatabase SQ = dop.getWritableDatabase();
        ContentValues cv = new ContentValues();
        //cv.put(Database.TableInfo.ID,id);
        cv.put(Database.TableInfo.USER_NAME, name);
        cv.put(Database.TableInfo.USER_PASS, pass);
        cv.put(Database.TableInfo.CONTACT_NO, contact_no);
        cv.put(Database.TableInfo.EMAIL, email);
        long k = SQ.insert(Database.TableInfo.TABLE_NAME, null, cv);
        Log.d("Database Operations", "One Row Inserted");

    }

    public Cursor getInformation(DatabaseOperations dop){
        SQLiteDatabase SQ = dop.getReadableDatabase();
        String[] columns = {Database.TableInfo.USER_NAME, Database.TableInfo.USER_PASS};
        Cursor CR = SQ.query(Database.TableInfo.TABLE_NAME,columns,null,null,null,null,null);
        return CR;
    }
}
