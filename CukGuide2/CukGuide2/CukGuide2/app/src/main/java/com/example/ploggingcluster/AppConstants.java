package com.example.ploggingcluster;

import android.os.Handler;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AppConstants {

    public static final int REQ_LOCATION_BY_ADDRESS = 101;
    public static final int REQ_WEATHER_BY_GRID = 102;

    public static final int REQ_PHOTO_CAPTURE = 103;
    public static final int REQ_PHOTO_SELECTION = 104;

    public static final int CONTENT_PHOTO = 105;
    public static final int CONTENT_PHOTO_EX = 106;

    public static String FOLDER_PHOTO;

    public static final String KEY_URI_PHOTO = "URI_PHOTO";

    public static String DATABASE_NAME = "note.db";

    public static final int MODE_INSERT = 1;
    public static final int MODE_MODIFY = 2;


    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
    public static SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH시");
    public static SimpleDateFormat dateFormat3 = new SimpleDateFormat("MM월 dd일");
    public static SimpleDateFormat dateFormat4 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static SimpleDateFormat dateFormat5 = new SimpleDateFormat("yyyy-MM-dd");


    private static Handler handler = new Handler();
    private static final String TAG = "AppConstants";
    public static void println(final String data) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, data);
            }
        });
    }
    private static class TIME_MAXIMUM {
        public static final int SEC = 60;
        public static final int MIN = 60;
        public static final int HOUR = 24;
        public static final int DAY = 30;
        public static final int MONTH = 12;
    }

    public static String calculateTime(String dateString) throws ParseException
    {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date = format.parse(dateString);

        long curTime = System.currentTimeMillis();
        long regTime = date.getTime();
        long diffTime = (curTime - regTime) / 1000;

        //		System.out.println("dateString : "+dateString);
        //		System.out.println("date.getTime : "+date.getTime());
        //		System.out.println("curTime : "+curTime);

        String msg = null;

        if (diffTime < TIME_MAXIMUM.SEC)
        {
            // sec
            msg = diffTime + "초전";
        }
        else if ((diffTime /= TIME_MAXIMUM.SEC) < TIME_MAXIMUM.MIN)
        {
            // min
            System.out.println(diffTime);

            msg = diffTime + "분전";
        }
        else if ((diffTime /= TIME_MAXIMUM.MIN) < TIME_MAXIMUM.HOUR)
        {
            // hour
            msg = (diffTime ) + "시간전";
        }
        else if ((diffTime /= TIME_MAXIMUM.HOUR) < TIME_MAXIMUM.DAY)
        {
            // day
            msg = (diffTime ) + "일전";
        }
        else if ((diffTime /= TIME_MAXIMUM.DAY) < TIME_MAXIMUM.MONTH)
        {
            // day
            msg = (diffTime ) + "달전";
        }
        else
        {
            msg = (diffTime) + "년전";
        }

        return msg;
    }
}
