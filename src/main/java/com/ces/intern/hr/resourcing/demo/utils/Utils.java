package com.ces.intern.hr.resourcing.demo.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Utils {
    public static String toFirtDayOfWeek(String date) throws ParseException{
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar =Calendar.getInstance();
        calendar.setTime(simpleDateFormat.parse(date));
        int dayOfWeek =calendar.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek==1){
            calendar.add(Calendar.DATE,-6);
        }
        else {
            calendar.add(Calendar.DATE,(2-dayOfWeek));
        }
        String firtDay=simpleDateFormat.format(calendar.getTime());
        return firtDay;
    }
    public static String toEndDayOfWeek(String date) throws ParseException{
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar =Calendar.getInstance();
        calendar.setTime(simpleDateFormat.parse(date));
        int dayOfWeek =calendar.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek==1){
            calendar.add(Calendar.DATE,dayOfWeek);
        }
        else {
            calendar.add(Calendar.DATE,(8-dayOfWeek));
        }
        String endDay=simpleDateFormat.format(calendar.getTime());
        return endDay;
    }

}
