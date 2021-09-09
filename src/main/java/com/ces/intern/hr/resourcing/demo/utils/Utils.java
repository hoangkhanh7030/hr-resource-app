package com.ces.intern.hr.resourcing.demo.utils;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.Calendar;
import java.util.Date;

public class Utils {

    public static int getIndexFromDate(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }



    public static Date toMonDayOfWeek(Date date) {

        Calendar calendar =Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek =calendar.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek==1){
            calendar.add(Calendar.DATE,-6);
        }
        else {
            calendar.add(Calendar.DATE,(2-dayOfWeek));
        }
        return calendar.getTime();
    }
    public static Date toSunDayOfWeek(Date date) {

        Calendar calendar =Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek =calendar.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek==1){
            calendar.add(Calendar.DATE,0);
        }
        else {
            calendar.add(Calendar.DATE,(8-dayOfWeek));
        }
        return calendar.getTime();
    }
    public static Date toSaturDayOfWeek(Date date) {
        Calendar calendar =Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek =calendar.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek==7){
            calendar.add(Calendar.DATE,0);
        }else if (dayOfWeek==1){
            calendar.add(Calendar.DATE,-1);
        }
        else {
            calendar.add(Calendar.DATE,(7-dayOfWeek));
        }
        return calendar.getTime();
    }
    public static Date toFriDayOfWeek(Date date) {

        Calendar calendar =Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek =calendar.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek==1){
            calendar.add(Calendar.DATE,-2);
        }else if (dayOfWeek==6){
            calendar.add(Calendar.DATE,0);
        }
        else {
            calendar.add(Calendar.DATE,(6-dayOfWeek));
        }
        return calendar.getTime();
    }

}
