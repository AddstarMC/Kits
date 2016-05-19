package com.dragonphase.kits.util;

import org.apache.commons.lang.StringUtils;

import java.util.Date;
import java.util.regex.Pattern;

public class Time {
    private long years, months, days, hours, minutes, seconds, milliseconds;

    public Time() {

    }

    public long getYears() {
        return years;
    }

    public void setYears(long years) {
        this.years = years;
    }

    public long getMonths() {
        return months;
    }

    public void setMonths(long months) {
        this.months = months;
    }

    public long getDays() {
        return days;
    }

    public void setDays(long days) {
        this.days = days;
    }

    public long getHours() {
        return hours;
    }

    public void setHours(long hours) {
        this.hours = hours;
    }

    public long getMinutes() {
        return minutes;
    }

    public void setMinutes(long minutes) {
        this.minutes = minutes;
    }

    public long getSeconds() {
        return seconds;
    }

    public void setSeconds(long seconds) {
        this.seconds = seconds;
    }

    public long getMilliseconds() {
        return milliseconds;
    }

    public void setMilliseconds(long milliseconds) {
        this.milliseconds = milliseconds;
    }

    public long getTotalMilliseconds() {
        long milliseconds = 0;

        milliseconds += getMilliseconds(getYears(), TimeType.YEARS);
        milliseconds += getMilliseconds(getMonths(), TimeType.MONTHS);
        milliseconds += getMilliseconds(getDays(), TimeType.DAYS);
        milliseconds += getMilliseconds(getHours(), TimeType.HOURS);
        milliseconds += getMilliseconds(getMinutes(), TimeType.MINUTES);
        milliseconds += getMilliseconds(getSeconds(), TimeType.SECONDS);
        milliseconds += getMilliseconds(getMilliseconds(), TimeType.MILLISECONDS);

        return milliseconds;
    }

    public String toReadableFormat(boolean showMillis) {
        String result[] = new String[7];

        if (getYears() > 0) {
            result[0] = getYears() + " year" + (getYears() > 1 ? "s" : "");
        }
        if (getMonths() > 0) {
            result[1] = getMonths() + " month" + (getMonths() > 1 ? "s" : "");
        }
        if (getDays() > 0) {
            result[2] = getDays() + " day" + (getDays() > 1 ? "s" : "");
        }
        if (getHours() > 0) {
            result[3] = getHours() + " hour" + (getHours() > 1 ? "s" : "");
        }
        if (getMinutes() > 0) {
            result[4] = getMinutes() + " minute" + (getMinutes() > 1 ? "s" : "");
        }
        if (getSeconds() > 0) {
            result[5] = getSeconds() + " second" + (getSeconds() > 1 ? "s" : "");
        }
        if (getMilliseconds() > 0 && showMillis) {
            result[6] = getMilliseconds() + " millisecond" + (getMilliseconds() > 1 ? "s" : "");
        }

        return StringUtils.join(Utils.clean(result), ", ");
    }

    public static Time fromMilliseconds(long ms) {
        Date date = new Date(ms);

        String[] initDate = Utils.timeFormat.format(Utils.initialDate).split(Pattern.quote(":"));
        String[] timeDate = Utils.timeFormat.format(date).split(Pattern.quote(":"));

        long years = Integer.parseInt(timeDate[0]) - Integer.parseInt(initDate[0]);
        long months = Integer.parseInt(timeDate[1]) - Integer.parseInt(initDate[1]);
        long days = Integer.parseInt(timeDate[2]) - Integer.parseInt(initDate[2]);
        long hours = Integer.parseInt(timeDate[3]) - Integer.parseInt(initDate[3]);
        long minutes = Integer.parseInt(timeDate[4]) - Integer.parseInt(initDate[4]);
        long seconds = Integer.parseInt(timeDate[5]) - Integer.parseInt(initDate[5]);
        long milliseconds = Integer.parseInt(timeDate[6]) - Integer.parseInt(initDate[6]);

        if (Integer.parseInt(timeDate[3]) > 23) days--;

        Time time = new Time();

        time.setYears(years);
        time.setMonths(months);
        time.setDays(days);
        time.setHours(hours);
        time.setMinutes(minutes);
        time.setSeconds(seconds);
        time.setMilliseconds(milliseconds);

        return time;
    }

    public static Time fromExpression(String expression) {
        String[] numbers = Utils.clean(expression.replaceAll("\\W", "").replace("and", "").replace("&", "").split("[^0-9]"));
        String[] types = Utils.clean(expression.replaceAll("\\W", "").replace("and", "").replace("&", "").split("[0-9]"));

        long milliseconds = 0;

        try {
            for (int i = 0; i < types.length; i++) {
                milliseconds += getMilliseconds(Integer.parseInt(numbers[i]), TimeType.match(types[i]));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return fromMilliseconds(milliseconds);
    }

    // Recursive helper method

    private static long getMilliseconds(long time, TimeType type) {
        switch (type) {
            case YEARS:
                time = getMilliseconds(time * 12, TimeType.MONTHS);
                break;
            case MONTHS:
                int offset = 0;
                for (int i = 0; i < time; i++) {
                    int count = (int) (12 * Math.floor(i / 12));
                    int month = i - count;

                    if ((month < 7 && month % 2 == 0) || (month > 6 && month % 2 == 1)) {
                        offset++;
                    }

                    if (month == 1) {
                        offset -= 2;
                    }
                }
                time = getMilliseconds(time * 30 + offset, TimeType.DAYS);
                break;
            case DAYS:
                time = getMilliseconds(time * 24, TimeType.HOURS);
                break;
            case HOURS:
                time = getMilliseconds(time * 60, TimeType.MINUTES);
                break;
            case MINUTES:
                time = getMilliseconds(time * 60, TimeType.SECONDS);
                break;
            case SECONDS:
                time = getMilliseconds(time * 1000, TimeType.MILLISECONDS);
            case MILLISECONDS:
            default:
                break;
        }
        return time;
    }
}
