package com.weget.fuyan.fyp.Util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Claudie on 9/19/16.
 */
public class DateFormatter {
    public static String formatDateFull(String inputDate){
        //input date is in format YYYY-MM-DD HH:MM:SS
        //to format to DD MM YYYY, hh:mm AM/PM

        // *** note that it's "yyyy-MM-dd hh:mm:ss" not "yyyy-mm-dd hh:mm:ss"
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = null;
        try {
            date = dt.parse(inputDate);
        }catch (ParseException pe){
            Log.d("Parse Error: ", pe.getMessage().toString());
            return inputDate;
            //pe.printStackTrace();
        }
        // *** same for the format String below
        SimpleDateFormat dt1 = new SimpleDateFormat("d MMMM yyyy h:mm a");
        //System.out.println(dt1.format(date));

        return dt1.format(date).toString();
    }

    public static String formatDate(String inputDate){
        //input date is in format YYYY-MM-DD HH:MM:SS
        //to format to DD MM YYYY, hh:mm AM/PM

        // *** note that it's "yyyy-MM-dd hh:mm:ss" not "yyyy-mm-dd hh:mm:ss"
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = null;
        try {
            date = dt.parse(inputDate);
        }catch (ParseException pe){
            Log.d("Parse Error: ", pe.getMessage().toString());
            return inputDate;
            //pe.printStackTrace();
        }
        // *** same for the format String below
        SimpleDateFormat dt1 = new SimpleDateFormat("d MMM yyyy h:mm a");
        //System.out.println(dt1.format(date));

        return dt1.format(date).toString();
    }
    public static String formatDateShort(String inputDate){
        //input date is in format YYYY-MM-DD HH:MM:SS
        //to format to DD MM YYYY, hh:mm AM/PM

        // *** note that it's "yyyy-MM-dd hh:mm:ss" not "yyyy-mm-dd hh:mm:ss"
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = null;
        try {
            date = dt.parse(inputDate);
        }catch (ParseException pe){
            Log.d("Parse Error: ", pe.getMessage().toString());
            return inputDate;
            //pe.printStackTrace();
        }
        // *** same for the format String below
        SimpleDateFormat dt1 = new SimpleDateFormat("d MMM yyyy");
        //System.out.println(dt1.format(date));

        return dt1.format(date).toString();
    }
}
