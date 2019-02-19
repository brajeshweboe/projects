/**
 * 
 */
package com.ofbiz.utility;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author kdua
 *
 */
public class OmsDateTimeUtility {
	
	/**
	 * OLD_FORMAT = "yyyy-MM-dd";
	 * NEW_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
	 * @param date
	 * @param oldFormat
	 * @param newFormat
	 * @return Timestamp
	 * @throws ParseException 
	 */
	public static Timestamp parseDateToTimestamp(String date,String format) throws ParseException{
		DateFormat df=new SimpleDateFormat(format);
		return new Timestamp(df.parse(date).getTime());
	}

}
