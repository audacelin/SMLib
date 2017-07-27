/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smlib.utils;

/*
 *
 */
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 
 * 时间处理类
 * 
 * @author gzit
 */
public class TimeUtils {

	// 一天有多少秒值
	public static long ONE_DAY_IN_MILLISCECOND = 24 * 60 * 60 * 1000;

	// 当天(NOTE:这个要谨慎使用，在android这种多任务的设备上，软件处于不可见状态的，并不意味着软件已经退出。所以从这个域里面取到的数据很可能是几天前的。)
	// public static Date TODAY = new Date();

	// 每个月的天数(闰年2月为29天，平年2月为28天)
	public static final int[] DAY_OF_MONTHS = new int[] { 31, 28, 31, 30, 31,
			30, 31, 31, 30, 31, 30, 31 };
	public final static String[] WEEKDAYS = { "SUN", "MON", "TUE", "WED",
			"THU", "FRI", "SAT" };

	public final static String[] MONTH_STRING = { "Jan", "Feb", "Mar", "Apr",
			"May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

	private TimeUtils() {
	}

	// 判断某个日期是否是一个月的最后一天
	public static boolean isLastDayOfMonth(Date date) {

		Calendar c = Calendar.getInstance();
		c.setTime(date);

		return c.get(Calendar.DAY_OF_MONTH) == howManyDaysOfMonth(date);

	}

	// 两个日期中的天，是不一个月中的同一天
	public static boolean isTheSameDayOfMonth(Date date1, Date date2) {

		Calendar c1 = Calendar.getInstance();
		c1.setTime(date1);

		Calendar c2 = Calendar.getInstance();
		c2.setTime(date2);

		return c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH);

	}

	/**
	 * 得到两日期相差几个月
	 * 
	 * @param String
	 * @return
	 */
	public static int howManyMonthBetweenDate(Date startDate, Date endDate) {
		int monthday;

		Calendar starCal = Calendar.getInstance();
		starCal.setTime(startDate);

		int sYear = starCal.get(Calendar.YEAR);
		int sMonth = starCal.get(Calendar.MONTH);
		int sDay = starCal.get(Calendar.DATE);

		Calendar endCal = Calendar.getInstance();
		endCal.setTime(endDate);
		int eYear = endCal.get(Calendar.YEAR);
		int eMonth = endCal.get(Calendar.MONTH);
		int eDay = endCal.get(Calendar.DATE);

		monthday = ((eYear - sYear) * 12 + (eMonth - sMonth));

		if (sDay < eDay) {
			monthday = monthday + 1;
		}
		return monthday;

	}

	// /////////////////////////////////////////////////////

	// 获得当天时间
	public static Date today() {
		return TimeUtils.ignoreTime(new Date());

	}

	// 获得一周中的某一天
	public static int getDayOfWeek(Date date) {

		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.DAY_OF_WEEK);

	}

	// 忽略时间，留下年月日
	public static Date ignoreTime(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);

		return c.getTime();

	}

	// 判断某年是否为闰年
	public static boolean isLeapYear(int year) {
		return ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0);
	}

	// 为二月份设定天数
	public static void setDays4Feb(int year) {
		if (isLeapYear(year)) {
			DAY_OF_MONTHS[1] = 29;
			return;
		}

		DAY_OF_MONTHS[1] = 28;
	}

	public static String[] getDays(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int days = TimeUtils.howManyDaysOfMonth(date);
		String[] daysStr = new String[days];
		for (int i = 1; i <= days; i++) {
			daysStr[i - 1] = String.valueOf(i);
		}

		return daysStr;

	}

	// 获得中国的日期的月份
	public static int getChineseMonth(Date date) {

		return getMonth(date) + 1; // +1 是获得中国的月份，最大为

	}

	public static int getMonth(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.MONTH);

	}

	public static int getYear(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.YEAR);

	}

	// 一月有多少天
	public static int howManyDaysOfMonth(Date date) {

		Calendar c = Calendar.getInstance();
		c.setTime(date);

		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		if (month + 1 == 2) {// MONTH字段从0开始排到11，2月份下标为1
			if (isLeapYear(year)) {
				return 29;
			}
			return 28;
		}

		return DAY_OF_MONTHS[month];

	}

	// /////////////时间导航//////////////////
	public static Date[] nextDate(Date cDate) {
		return navigateDate(cDate, Calendar.DATE, 1);
	}

	public static Date[] preDate(Date cDate) {

		return navigateDate(cDate, Calendar.DATE, -1);

	}

	public static Date[] nextWeek(Date cDate) {
		Date[] dates = navigateWeek(cDate, Calendar.WEEK_OF_YEAR, 1);
		return dates;

	}

	public static Date[] preWeek(Date cDate) {

		return navigateWeek(cDate, Calendar.WEEK_OF_YEAR, -1);

	}

	public static Date[] nextMonth(Date cDate) {

		return navigateMonth(cDate, Calendar.MONTH, 1);

	}

	public static Date[] preMonth(Date cDate) {

		return navigateMonth(cDate, Calendar.MONTH, -1);

	}

	public static Date[] navigateWeek(Date cDate, int field, int added) {
		Date[] ret = new Date[2];

		Calendar c = Calendar.getInstance();
		c.setTime(cDate);
		c.add(field, added);

		ret[0] = TimeUtils.getDay0Hour(getMonday(c.getTime()));
		ret[1] = TimeUtils.getDay24Hour(getSunday(c.getTime()));
		return ret;

	}

	public static Date[] navigateMonth(Date cDate, int field, int added) {
		Date[] ret = new Date[2];

		Calendar c = Calendar.getInstance();
		c.setTime(cDate);
		c.add(field, added);

		ret[0] = TimeUtils.getDay0Hour(getFirstDateOfMonth(c.getTime()));
		ret[1] = TimeUtils.getDay24Hour(getLastDateOfMonth(c.getTime()));
		return ret;

	}

	public static Date[] navigateDate(Date cDate, int field, int added) {
		Date[] ret = new Date[2];

		Calendar c = Calendar.getInstance();
		c.setTime(cDate);
		c.add(field, added);

		ret[0] = getDay0Hour(c.getTime());
		ret[1] = getDay24Hour(c.getTime());
		return ret;

	}

	public static Date navigateDate2(Date cDate, int field, int added) {

		Calendar c = Calendar.getInstance();
		c.setTime(cDate);
		c.add(field, added);

		return getDay0Hour(c.getTime());

	}

	// /////////////////////////////////

	public static Date getDay0Hour(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);

		return c.getTime();

	}

	public static Date getDay24Hour(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);

		return c.getTime();

	}

	// 获得0点的时间
	public static Date getToday0Hour(boolean cached) {
		return getDay0Hour(/* cached ? TODAY : */new Date());

	}

	// 获得24点的时间
	public static Date getToday24Hour(boolean cached) {
		return getDay24Hour(/* cached ? TODAY : */new Date());

	}

	// 获得本周日的时间
	public static Date getSunday(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);

		int w = c.get(Calendar.DAY_OF_WEEK);

		int day = 7 - w + 1;
		day = day == 7 ? 0 : day;

		return add(c.getTime(), day);

	}

	// 获得本周一的时间
	public static Date getMonday(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);

		int w = c.get(Calendar.DAY_OF_WEEK);

		int day = w - 2;
		day = day < 0 ? 6 : day;

		return sub(c.getTime(), day);

	}

	// 获得本月底的第一天
	public static Date getFirstDateOfMonth(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.HOUR_OF_DAY, 0);// 清空时分秒等字段
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);

		return c.getTime();

	}

	// 获得本月的最后一天
	public static Date getLastDateOfMonth(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DAY_OF_MONTH, TimeUtils.howManyDaysOfMonth(date));

		return c.getTime();
	}

	// 在当前的时间上减去某些天
	public static Date sub(Date originDate, int moveDays) {

		Calendar c = Calendar.getInstance();
		c.setTime(originDate);

		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		setDays4Feb(year);

		int remainDays = DAY_OF_MONTHS[month] - day + moveDays;

		while (true) {

			if (month < 0) {
				month = 11;
				year--;
				setDays4Feb(year);
			}

			remainDays = remainDays - DAY_OF_MONTHS[month];

			if (remainDays < 0) {

				day = -remainDays;
				break;

			} else {
				month--;

			}

		}

		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, day);

		return c.getTime();
	}

	// 在当前的时间上加上某些天
	public static Date add(Date originDate, int moveDays) {

		Calendar c = Calendar.getInstance();
		c.setTime(originDate);

		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		setDays4Feb(year);

		// 剩下的天数
		int remainDays = day + moveDays;

		while (true) {

			if (month > 11) {
				month = 1;
				year++;
				setDays4Feb(year);
			}

			remainDays = remainDays - DAY_OF_MONTHS[month];

			if (remainDays < 0) {

				day = DAY_OF_MONTHS[month] + remainDays;

				break;

			} else {

				month++;

			}

		}

		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, day);

		return c.getTime();
	}

	// 比较两个calandar中的日期
	// c1在c2前的话则返回true，否则返回false
	public static boolean dateBefore(Calendar c1, Calendar c2) {
		if (c1.get(Calendar.YEAR) > c2.get(Calendar.YEAR)) {
			return false;
		}

		// 年相等得话，则比较月
		if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)) {

			if (c1.get(Calendar.MONTH) > c2.get(Calendar.MONTH)) {
				return false;
			}

			// 月相等得话，则比较天
			if (c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)) {

				if (c1.get(Calendar.DAY_OF_MONTH) > c2
						.get(Calendar.DAY_OF_MONTH)) {
					return false;
				}

			}

		}

		return true;

	}

	public static int getDayOfMonth(Date date) {
		return new DateTime(date).getDay();
	}

	public static String getAbbrMonthString(Date date) {
		return MONTH_STRING[new DateTime(date).getMonth()];
	}

	public static String getAbbrMonthString(int month) {
		return MONTH_STRING[month - 1];
	}

	// 将时间格式话为：yyyy-MM-dd的格式
	public static String formatDate(Date date) {
		return new DateTime(date).toDateString("-");
	}

	public static String formatDate(Date date, String splitChar) {
		return new DateTime(date).toDateString(splitChar);
	}

	public static String formateSmallDate(Date date, String splitChar) {
		return new DateTime(date).toSmallDateString(splitChar);
	}

	// 完整地描述时间
	public static String descript(Date date) {
		return new DateTime(date).toString();
	}

	public static String format(Date date) {
		return new DateTime(date).toDateString("-");
	}

	public static String format2Small(Date date) {

		return new DateTime(date).toSmallDateString("");
	}

	public static String toChineseDateString(Date date) {

		return new DateTime(date).toChineseDateString();
	}

	public static String toChineseShortDateString(Date date) {

		return new DateTime(date).toChineseShortDateString();

	}

	// 辅助类
	public static class DateTime {

		public final String timeZone;
		public final int year;
		public final int month;
		public final int day;
		public final int weekday;
		public final int hour;
		public final int minute;
		public final int second;

		public DateTime(Date date) {
			this(date, null);
		}

		public DateTime(Date date, String timeZone) {
			this.timeZone = timeZone;
			Calendar c = timeZone == null ? Calendar.getInstance() : Calendar
					.getInstance(TimeZone.getTimeZone(timeZone));
			c.setTime(date);
			year = c.get(Calendar.YEAR);
			month = c.get(Calendar.MONTH);
			day = c.get(Calendar.DAY_OF_MONTH);
			weekday = c.get(Calendar.DAY_OF_WEEK);
			hour = c.get(Calendar.HOUR_OF_DAY);
			minute = c.get(Calendar.MINUTE);
			second = c.get(Calendar.SECOND);
		}

		public DateTime(long time, String timeZone) {
			this(new Date(time), timeZone);
		}

		public String getTimeZone() {
			return timeZone;
		}

		public int getYear() {
			return year;
		}

		public int getMonth() {
			return month;
		}

		public int getDay() {
			return day;
		}

		public int getWeekday() {
			return weekday;
		}

		public int getHour() {
			return hour;
		}

		public int getMinute() {
			return minute;
		}

		public int getSecond() {
			return second;
		}

		public Date toDate() {
			Calendar c = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
			c.set(Calendar.YEAR, year);
			c.set(Calendar.MONTH, month);
			c.set(Calendar.DAY_OF_MONTH, day);
			c.set(Calendar.HOUR_OF_DAY, hour);
			c.set(Calendar.MINUTE, minute);
			c.set(Calendar.SECOND, second);
			return c.getTime();
		}

		public String toDateAndWeekString() {

			String supm = (month + 1) < 10 ? "0" + (month + 1) : month + 1 + "";
			String supd = day < 10 ? "0" + day : day + "";

			return year + "-" + supm + "-" + supd + "(" + WEEKDAYS[weekday - 1]
					+ ")";
		}

		public String toDateString(String splitChart) {

			String supm = (month + 1) < 10 ? "0" + (month + 1) : month + 1 + "";
			String supd = day < 10 ? "0" + day : day + "";

			return year + splitChart + supm + splitChart + supd;

		}

		// 如0809
		public String toSmallDateString(String splitChart) {
			String supm = (month + 1) < 10 ? "0" + (month + 1) : month + 1 + "";
			String supd = day < 10 ? "0" + day : day + "";

			return supm + splitChart + supd;

		}

		public String toTimeString() {
			return hour + ":" + minute + ":" + second;
		}

		public String toString() {
			return toDateString("-") + " " + toTimeString();
		}

		public String toChineseDateString() {

			String supm = (month + 1) < 10 ? "0" + (month + 1) : month + 1 + "";
			String supd = day < 10 ? "0" + day : day + "";
			return year + "年" + supm + "月" + supd + "日";

		}

		public String toChineseShortDateString() {

			String supm = (month + 1) < 10 ? "0" + (month + 1) : month + 1 + "";
			String supd = day < 10 ? "0" + day : day + "";
			return supm + "月" + supd + "日";

		}

	}
}
