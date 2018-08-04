package com.elitemobiletechnology.tinychat;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by SteveYang on 17/1/21.
 */

public class Util {
    private Util(){}
    public static String getHumanReadableTime(long timestamp){
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss.SSS"); // the format of your date
        sdf.setTimeZone(TimeZone.getDefault()); // give a timezone reference for formating (see comment at the bottom
        return sdf.format(date);
    }

    public static void savePrefValue(String prefKey,long prefVal) {
        Context c = MyApplicaton.getAppContext();
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings =c.getSharedPreferences(c.getPackageName(), Context.MODE_PRIVATE); //1
        editor = settings.edit();
        editor.putLong(prefKey, prefVal);
        editor.commit();
    }

    public static long getPrefValue(String prefKey) {
        Context c = MyApplicaton.getAppContext();
        SharedPreferences settings;
        Long time;
        settings = c.getSharedPreferences(c.getPackageName(), Context.MODE_PRIVATE); //1
        time = settings.getLong(prefKey, 0l);
        return time;
    }}
