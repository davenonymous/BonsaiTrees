package com.davenonymous.bonsaitrees3.libnonymous.utils;

public class TickTimeHelper {
	public static String getDuration(int ticks) {
		int fullSeconds = ticks / 20;
		int fullMinutes = fullSeconds / 60;
		int fullHours = fullMinutes / 60;
		int fullDays = fullHours / 24;
		int seconds = fullSeconds % 60;
		int minutes = fullMinutes % 60;
		int hours = fullHours % 24;

		StringBuilder result = new StringBuilder();
		if(fullDays > 0) {
			result.append(fullDays + "d ");
		}
		if(hours > 0) {
			result.append(hours + "h ");
		}
		if(minutes > 0) {
			result.append(minutes + "m ");
		}
		if(seconds > 0) {
			result.append(seconds + "s ");
		}
		result.deleteCharAt(result.length() - 1);
		return result.toString();
	}
}
