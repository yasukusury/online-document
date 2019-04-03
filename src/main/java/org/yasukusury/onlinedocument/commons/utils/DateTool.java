package org.yasukusury.onlinedocument.commons.utils;

import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @author 30254
 * creadtedate:2018/8/23
 */
public class DateTool {
    public static Timestamp getTime() {
        return new Timestamp(System.currentTimeMillis());
    }

    private static DateTimeFormatter formatter = DateTimeFormat.forStyle("MM");

    public static Date getLastMonthDate() {
        DateTime dateTime = DateTime.now().minusMonths(1)
                .withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
        return dateTime.toDate();
    }

    public static String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        return formatter.print(date.getTime());
    }

    public enum TimeUnit {
        YEAR(0), MONTH(1), WEEK(2), DAY(3), HOUR(4), MINUTE(5), SECOND(6);

        private int code;

        TimeUnit(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    public static Date timeOperate(Date base, int diff, TimeUnit unit) {
        Date instant = null;
        DateTime time = new DateTime(base);
//        if(diff>0){
        switch (unit.code) {
            case 0:
                instant = time.plusYears(diff).toDate();
                break;
            case 1:
                instant = time.plusMonths(diff).toDate();
                break;
            case 2:
                instant = time.plusWeeks(diff).toDate();
                break;
            case 3:
                instant = time.plusDays(diff).toDate();
                break;
            case 4:
                instant = time.plusHours(diff).toDate();
                break;
            case 5:
                instant = time.plusMinutes(diff).toDate();
                break;
            case 6:
                instant = time.plusSeconds(diff).toDate();
                break;
            default:
        }
        return instant;
    }

    public static int daysDiff(Date date1, Date date2, TimeUnit unit) throws Exception {
        switch (unit) {
            case YEAR:
                return Years.yearsBetween(new LocalDateTime(date1)
                        , new LocalDateTime(date2)).getYears();
            case MONTH:
                return Months.monthsBetween(new LocalDateTime(date1)
                        , new LocalDateTime(date2)).getMonths();
            case WEEK:
                return Weeks.weeksBetween(new LocalDateTime(date1)
                        , new LocalDateTime(date2)).getWeeks();
            case DAY:
                return Days.daysBetween(new LocalDateTime(date1)
                        , new LocalDateTime(date2)).getDays();
            case HOUR:
                return Hours.hoursBetween(new LocalDateTime(date1)
                        , new LocalDateTime(date2)).getHours();
            default:
                throw new Exception("只能计算小时以上单位的差");
        }
    }

    public static int getUnitData(Date base, TimeUnit unit) {
        LocalDateTime time = new LocalDateTime(base);
        switch (unit.code) {
            case 0:
                return time.getMonthOfYear() - 1;
            case 1:
                return time.getDayOfMonth() - 1;
            case 2:
                return time.getDayOfWeek() - 1;
            case 3:
                return time.getHourOfDay();
            case 4:
                return time.getMinuteOfHour();
            case 5:
                return time.getSecondOfMinute();
                default:
        }
        return 0;
    }
}
