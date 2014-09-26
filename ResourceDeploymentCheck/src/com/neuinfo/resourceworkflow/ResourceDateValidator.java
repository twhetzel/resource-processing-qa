package com.neuinfo.resourceworkflow;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;
 
public class ResourceDateValidator {
 
	private static final Logger LOG = Logger.getLogger(ResourceDateValidator.class.getSimpleName());
		
	public static boolean checkDate(String statusDate) {
 
		String dateToValidate = statusDate;
		//LOG.info("** STATUS DATE: "+statusDate);
		String dateFormat = "yyyy-MM-dd HH:mm:ss"; //Use uppercase M for month, use uppercase H for 24-hour date format
		//date_format in DISCO database 2013-12-20 11:45:46	
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		sdf.setLenient(false);
		
		boolean resourceIsStuck = false;
		
		try {
			Date date = sdf.parse(dateToValidate);
			
			// Status date after 2 days
			Calendar cal = Calendar.getInstance();
	        cal.setTime(date);
	        cal.add(Calendar.DATE, 2); //minus number would decrement the days
	        Date statusDateAfter2Days = cal.getTime();
			LOG.info("** Status Date After2Days: "+statusDateAfter2Days);
		
			// Current date
			Date currentDate = Calendar.getInstance().getTime();
			System.out.println("CURRENT DATE: "+ currentDate);
			
			if (currentDate.after(statusDateAfter2Days)) {
				System.out.println("** Resource is stuck in processing **");
				resourceIsStuck = true;
			}
			else{
				System.out.println("Resource is still processing...no problem");
			}
 
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return resourceIsStuck;
	}
}