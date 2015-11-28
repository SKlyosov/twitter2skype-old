package com.epam.jm;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateHelper {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
	
	public static String getShiftedDateString(Date date, int numHours) {
		
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR_OF_DAY, numHours);
        Date _date = cal.getTime();
        return sdf.format(_date);
        
	}
}
