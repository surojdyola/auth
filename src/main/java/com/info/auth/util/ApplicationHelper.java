package com.info.auth.util;

import java.util.Calendar;

public class ApplicationHelper {
	
	private ApplicationHelper() {}
	
	public static Boolean isBlocked(final Calendar date) {
		Boolean v = date != null && date.after(Calendar.getInstance());
		return v;
	}
	
	public static boolean isNullOrSpaces(String str) {
		if (str == null || str.isBlank())
			return true;
		return false;
	}

}
