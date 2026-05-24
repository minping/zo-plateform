package com.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author zhoump
 * @date 2026/5/18
 * @purpose
 */
public class DateKit {

    public static final String YYYY = "yyyy";
    public static final String YYYYMM = "yyyyMM";
    public static final String YYYYMMDD = "yyyyMMdd";
    public static final String YYYYMMDDHH = "yyyyMMddHH";
    public static final String YYYYMMDDHHMM = "yyyyMMddHHmm";
    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public static final String YYYY_MM = "yyyy-MM";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String HH_MM_SS = "HH:mm:ss";

    public static final String YYYY_MM_DD_HH_MM_SS_SLASH = "yyyy/MM/dd HH:mm:ss";
    public static final String YYYY_MM_DD_HH_MM_SLASH = "yyyy/MM/dd HH:mm";
    public static final String YYYY_MM_DD_SLASH = "yyyy/MM/dd";
    public static final String YYYY_MM_SLASH = "yyyy/MM";

    public static final String YYYY_MM_DD_HH_MM_SS_CHINESE = "yyyy年MM月dd日HH时mm分ss秒";
    public static final String YYYY_M_D_H_M_S_CHINESE = "yyyy年M月d日H时m分s秒";
    public static final String YYYY_MM_DD_HH_MM_CHINESE = "yyyy年MM月dd日HH时mm分";
    public static final String YYYY_MM_DD_CStrKitHINESE = "yyyy年MM月dd日";
    public static final String YYYY_MM_CHINESE = "yyyy年MM月";
    public static final String YYYY_CHINESE = "yyyy年";
    public static final long DAY=24*60*60*1000;
    public static final long HOUR=60*60*1000;
    public static final long MINUTE=60*1000;
    private DateKit() {
    }

    /**
     * 获取当前时间字符串
     *
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String getNowTime() {
        return formatDate2Str(new Date(), YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 转换指定格式的字符串为时间
     *
     * @param str
     * @param pattern
     * @return
     */
    public static Date formatStr2Date(String str, String pattern) {
        Date d = null;
        if (null != str && !str.trim().equals("")) {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            try {
                d = sdf.parse(str);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        return d;
    }

    /**
     * 转换时间为指定格式的字符串
     *
     * @param date
     * @return
     */
    public static String formatDate2Str(Date date, String pattern) {
        if (null == date) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String s = sdf.format(date);
        return s;
    }

    /**
     * Calendar转换为Date
     *
     * @param calendar
     * @return
     */
    public static Date calendar2Date(Calendar calendar) {
        if (null == calendar) {
            return null;
        }
        return calendar.getTime();
    }

    /**
     * Date转换为Calendar
     *
     * @param date
     * @return
     */
    public static Calendar date2Calendar(Date date) {
        if (null == date) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static Calendar strToCalendar(String dateStr) {
        Date date = formatStr2Date(dateStr, YYYY_MM_DD_HH_MM_SS);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    /**
     * 计算指定日期是该年中的第几周
     *
     * @param date
     * @return
     */
    public static Integer getWeekOfYear(Date date) {
        if (null == date) {
            return 0;
        }
        return date2Calendar(date).get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 计算指定日期是该年中的第几月
     *
     * @param date
     * @return
     */
    public static Integer getMonthOfYear(Date date) {
        if (null == date) {
            return 0;
        }
        return date2Calendar(date).get(Calendar.MONTH);
    }

    /**
     * 计算指定日期是该年中的第几季度
     *
     * @param date
     * @return
     */
    public static Integer getQuarterOfYear(Date date) {
        Integer month = getMonthOfYear(date);
        if (month >= 0 && month <= 2) {
            return 0;
        } else if (month >= 3 && month <= 5) {
            return 1;
        } else if (month >= 6 && month <= 8) {
            return 2;
        } else {
            return 3;
        }
    }

    /**
     * 计算指定日期是该年中的第几天
     *
     * @param date
     * @return
     */
    public static Integer getDayOfYear(Date date) {
        if (null == date) {
            return 0;
        }
        return date2Calendar(date).get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 计算指定日期是该月中的第几天
     *
     * @param date
     * @return
     */
    public static Integer getDayOfMonth(Date date) {
        if (null == date) {
            return 0;
        }
        return date2Calendar(date).get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 计算指定日期是月中的第几个星期
     *
     * @param date
     * @return
     */
    public static Integer getDayOfWeekInMonth(Date date) {
        if (null == date) {
            return 0;
        }
        return date2Calendar(date).get(Calendar.DAY_OF_WEEK_IN_MONTH);
    }

    /**
     * 计算指定日期是该周中的第几天(星期天1~~~星期六7)
     *
     * @param date
     * @return
     */
    public static Integer getDayOfWeek(Date date) {
        if (null == date) {
            return 0;
        }
        return date2Calendar(date).get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 计算指定日期月份的最大天数
     *
     * @param date
     * @return
     */
    public static int getMaxDayInMonth(Date date) {
        if (date == null) {
            return 0;
        }
        return date2Calendar(date).getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 给指定日期添加或减去指定年
     *
     * @param date
     * @param amount
     * @return
     */
    public static Date addYear(Date date, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, amount);
        return calendar.getTime();
    }

    /**
     * 指定日期的年份
     *
     * @param date
     * @param year
     * @return
     */
    public static Date setYear(Date date, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.YEAR, year);
        return calendar.getTime();
    }

    /**
     * 给指定日期添加或减去指定月
     *
     * @param date
     * @param amount
     * @return
     */
    public static Date addMonth(Date date, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, amount);
        return calendar.getTime();
    }

    /**
     * 指定日期月份
     *
     * @param date
     * @param month
     * @return
     */
    public static Date setMonth(Date date, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MONTH, month);
        return calendar.getTime();
    }

    /**
     * 给指定日期添加或减去指定周
     *
     * @param date
     * @param amount
     * @return
     */
    public static Date addWeek(Date date, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.WEEK_OF_MONTH, amount);
        return calendar.getTime();
    }

    /**
     * 给指定日期添加或减去指定天
     *
     * @param date
     * @param amount
     * @return
     */
    public static Date addDay(Date date, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, amount);
        return calendar.getTime();
    }

    /**
     * 指定日期的天数
     *
     * @param date
     * @param day
     * @return
     */
    public static Date setDay(Date date, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, day);
        return calendar.getTime();
    }

    /**
     * 给指定日期添加或减去指定小时
     *
     * @param date
     * @param amount
     * @return
     */
    public static Date addHour(Date date, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, amount);
        return calendar.getTime();
    }

    /**
     * 指定日期的小时
     *
     * @param date
     * @param hour
     * @return
     */
    public static Date setHour(Date date, int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        return calendar.getTime();
    }

    /**
     * 给指定日期添加或减去指定分钟
     *
     * @param date
     * @param amount
     * @return
     */
    public static Date addMinute(Date date, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, amount);
        return calendar.getTime();
    }

    /**
     * 指定日期的分钟
     *
     * @param date
     * @param minute
     * @return
     */
    public static Date setMinute(Date date, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MINUTE, minute);
        return calendar.getTime();
    }

    public static Date addSecond(Date date, long amount) {
        return addSecond(date, Integer.valueOf(String.valueOf(amount)));
    }

    /**
     * 给指定日期添加或减去指定秒
     *
     * @param date
     * @param amount
     * @return
     */
    public static Date addSecond(Date date, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, amount);
        return calendar.getTime();
    }

    /**
     * 指定日期的秒
     *
     * @param date
     * @param second
     * @return
     */
    public static Date setSecond(Date date, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.SECOND, second);
        return calendar.getTime();
    }

    /**
     * 给指定日期添加或减去指定毫秒
     *
     * @param date
     * @param amount
     * @return
     */
    public static Date addMillisecond(Date date, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MILLISECOND, amount);
        return calendar.getTime();
    }

    /**
     * 指定日期的毫秒
     *
     * @param date
     * @param millisecond
     * @return
     */
    public static Date setMillisecond(Date date, int millisecond) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MILLISECOND, millisecond);
        return calendar.getTime();
    }

    /**
     * 计算两个日期之间差的多少毫秒，如果日期firstDate在日期secondDate的后面则返回一个正数、如果日期firstDate在日期secondDate的前面则返回一个负数、两个日期相同返回0
     *
     * @param firstDate
     * @param secondDate
     * @return
     */
    public static long compareMillisecond(Date firstDate, Date secondDate) {
        long time1 = firstDate.getTime();
        long time2 = secondDate.getTime();
        return (time1 - time2);
    }

    /**
     * 计算两个日期之间差的多少秒，如果日期firstDate在日期secondDate的后面则返回一个正数、如果日期firstDate在日期secondDate的前面则返回一个负数、两个日期相同返回0
     *
     * @param firstDate
     * @param secondDate
     * @return
     */
    public static long compareSecond(Date firstDate, Date secondDate) {
        long time1 = firstDate.getTime();
        long time2 = secondDate.getTime();
        return ((time1 - time2) / (1000));
    }

    /**
     * 计算两个日期之间差的多少分钟，如果日期firstDate在日期secondDate的后面则返回一个正数、如果日期firstDate在日期secondDate的前面则返回一个负数、两个日期相同返回0
     *
     * @param firstDate
     * @param secondDate
     * @return
     */
    public static long compareMinute(Date firstDate, Date secondDate) {
        long time1 = firstDate.getTime();
        long time2 = secondDate.getTime();
        return ((time1 - time2) / (1000 * 60));
    }

    /**
     * 计算两个日期之间差的多少小时，如果日期firstDate在日期secondDate的后面则返回一个正数、如果日期firstDate在日期secondDate的前面则返回一个负数、两个日期相同返回0
     *
     * @param firstDate
     * @param secondDate
     * @return
     */
    public static long compareHour(Date firstDate, Date secondDate) {
        long time1 = firstDate.getTime();
        long time2 = secondDate.getTime();
        return ((time1 - time2) / (1000 * 60 * 60));
    }

    /**
     * 计算时间差，返回：HH:mm:ss
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return
     */
    public static String formatBetween(Date startDate, Date endDate){
        long consume=endDate.getTime()-startDate.getTime();

        consume=consume%DAY;
        long h=(consume)/HOUR;
        long m=(consume-h*HOUR)/MINUTE;
        long s=(consume-h*HOUR-m*MINUTE)/1000;
        String hh = getFormatTime(h);
        String mm = getFormatTime(m);
        String ss = getFormatTime(s);
        StringBuilder sb = new StringBuilder();
        sb.append(hh).append(":");
        sb.append(mm).append(":");
        sb.append(ss);

        return sb.toString();
    }

    private static String getFormatTime(long time) {
        String timeStr;
        if(time==0){
            timeStr="00";
        }else if(time >0&& time <10){
            timeStr="0"+ time;
        }else{
            timeStr=String.valueOf(time);
        }
        return timeStr;
    }

    /**
     * 计算两个日期之间差的多少天，如果日期firstDate在日期secondDate的后面则返回一个正数、如果日期firstDate在日期secondDate的前面则返回一个负数、两个日期相同返回0
     *
     * @param firstDate
     * @param secondDate
     * @return
     */
    public static long compareDay(Date firstDate, Date secondDate) {
        long time1 = firstDate.getTime();
        long time2 = secondDate.getTime();
        return ((time1 - time2) / (1000 * 60 * 60 * 24));
    }

    // /**
    // *
    // 计算两个日期之间差的多少月，如果日期firstDate在日期secondDate的后面则返回一个正数、如果日期firstDate在日期secondDate的前面则返回一个负数、两个日期相同返回0
    // *
    // * @param firstDate
    // * @param secondDate
    // * @return
    // */
    // public static long compareMonth(Date start, Date end) {
    // // TODO
    // if (start.after(end)) {
    // Date t = start;
    // start = end;
    // end = t;
    // }
    // Calendar startCalendar = Calendar.getInstance();
    // startCalendar.setTime(start);
    // Calendar endCalendar = Calendar.getInstance();
    // endCalendar.setTime(end);
    // Calendar temp = Calendar.getInstance();
    // temp.setTime(end);
    // temp.add(Calendar.DATE, 1);
    //
    // int year = endCalendar.getAttribute(Calendar.YEAR)
    // - startCalendar.getAttribute(Calendar.YEAR);
    // int month = endCalendar.getAttribute(Calendar.MONTH)
    // - startCalendar.getAttribute(Calendar.MONTH);
    //
    // if ((startCalendar.getAttribute(Calendar.DATE) == 1)
    // && (temp.getAttribute(Calendar.DATE) == 1)) {
    // return year * 12 + month + 1;
    // } else if ((startCalendar.getAttribute(Calendar.DATE) != 1)
    // && (temp.getAttribute(Calendar.DATE) == 1)) {
    // return year * 12 + month;
    // } else if ((startCalendar.getAttribute(Calendar.DATE) == 1)
    // && (temp.getAttribute(Calendar.DATE) != 1)) {
    // return year * 12 + month;
    // } else {
    // return (year * 12 + month - 1) < 0 ? 0 : (year * 12 + month);
    // }
    // }

    // /**
    // *
    // 计算两个日期之间差的多少年，如果日期firstDate在日期secondDate的后面则返回一个正数、如果日期firstDate在日期secondDate的前面则返回一个负数、两个日期相同返回0
    // *
    // * @param firstDate
    // * @param secondDate
    // * @return
    // */
    // public static long compareYear(Date start, Date end) {
    // // TODO
    // return 0;
    // }

    /**
     * 获取指定日期对应的年份的第一天的日期
     *
     * @param date
     * @return
     */
    public static Date getFirstDayOfYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_YEAR, 1);// 设为日期为今年的第1天
        return calendar.getTime();
    }

    /**
     * 获取指定日期对应的年份的最后一天的日期
     *
     * @param date
     * @return
     */
    public static Date getLastDayOfYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_YEAR, 1);// 设为日期为今年的第1天
        calendar.add(Calendar.YEAR, 1);// 加上一年，到第二年的第一天
        calendar.add(Calendar.DATE, -1);// 减一天，今年的最后一天
        return calendar.getTime();
    }

    /**
     * 获取指定日期对应的季度的第一天的日期
     *
     * @param date
     * @return
     */
    public static Date getFirstDayOfQuarter(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH);
        if (0 == month || 1 == month || 2 == month) {
            calendar.set(Calendar.MONDAY, 0);
        } else if (3 == month || 4 == month || 5 == month) {
            calendar.set(Calendar.MONDAY, 3);
        } else if (6 == month || 7 == month || 8 == month) {
            calendar.set(Calendar.MONDAY, 6);
        } else {
            calendar.set(Calendar.MONDAY, 9);
        }
        return getFirstDayOfMonth(calendar.getTime());
    }

    /**
     * 获取指定日期对应的季度的最后一天的日期
     *
     * @param date
     * @return
     */
    public static Date getLastDayOfQuarter(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH);
        if (0 == month || 1 == month || 2 == month) {
            calendar.set(Calendar.MONDAY, 2);
        } else if (3 == month || 4 == month || 5 == month) {
            calendar.set(Calendar.MONDAY, 5);
        } else if (6 == month || 7 == month || 8 == month) {
            calendar.set(Calendar.MONDAY, 8);
        } else {
            calendar.set(Calendar.MONDAY, 11);
        }
        return getLastDayOfMonth(calendar.getTime());
    }

    /**
     * 获取指定日期对应的月份的第一天的日期
     *
     * @param date
     * @return
     */
    public static Date getFirstDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, 1);// 设为当前月的1号
        return calendar.getTime();
    }

    /**
     * 获取指定日期对应的月份的最后一天的日期
     *
     * @param date
     * @return
     */
    public static Date getLastDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, 1);// 设为当前月的1号
        calendar.add(Calendar.MONTH, 1);// 加一个月，变为下月的1号
        calendar.add(Calendar.DATE, -1);// 减去一天，变为当月最后一天
        return calendar.getTime();
    }

    public static int getHourOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public static int getMinOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }

    public static int getAllMinsOfDay(Date date) {
        return getHourOfDay(date) * 60 + getMinOfDay(date);
    }

    /**
     * 获取指定日期对应的周的第一天的日期(按中国习惯星期一作为一周的第一天)
     *
     * @param date
     * @return
     */
    public static Date getFirstDayOfWeek(Date date) {
        int dayOfWeek = getDayOfWeek(date);
        if (2 == dayOfWeek) {
            return date;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        while (true) {
            calendar.add(Calendar.DATE, -1);
            if (2 == getDayOfWeek(calendar.getTime())) {
                break;
            }
        }
        return calendar.getTime();
    }

    /**
     * 获取指定日期对应的周的最后一天的日期(按中国习惯星期天作为一周的最后一天)
     *
     * @param date
     * @return
     */
    public static Date getLastDayOfWeek(Date date) {
        int dayOfWeek = getDayOfWeek(date);
        if (1 == dayOfWeek) {
            return date;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        while (true) {
            calendar.add(Calendar.DATE, 1);
            if (1 == getDayOfWeek(calendar.getTime())) {
                break;
            }
        }
        return calendar.getTime();
    }
}
