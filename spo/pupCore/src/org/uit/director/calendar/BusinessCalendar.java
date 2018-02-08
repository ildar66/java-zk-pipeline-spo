package org.uit.director.calendar;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;


/**
 * a calendar that knows about business hours.
 */
public class BusinessCalendar implements Serializable {

    private static final long serialVersionUID = 1L;

    Day[] weekDays = null;
    List holidays = null;

    private static Properties businessCalendarProperties = null;

    public static Properties getBusinessCalendarProperties() {
        if (businessCalendarProperties == null) {
            businessCalendarProperties = ClassLoaderUtil.getProperties("business.calendar.properties", "");
        }
        return businessCalendarProperties;
    }

    public BusinessCalendar() {
        try {
            Properties calendarProperties = getBusinessCalendarProperties();
            weekDays = Day.parseWeekDays(calendarProperties, this);
            holidays = Holiday.parseHolidays(calendarProperties, this);

        } catch (Exception e) {
            throw new RuntimeException("couldn't create business calendar", e);
        }
    }


    /**
     * К дате добавляет период времени как стандартный
     * ( например, add(new Date(), new Duration("1 day"))),
     * так и бизнес период,
     * определенный в файле конфигурации
     * ( например, add(new Date(), new Duration("1 business day"))),
     *
     * @param date
     * @param duration
     * @return Астрономическая дата результата прибавления
     */
    public Date add(Date date, Duration duration) {
        Date end = null;
        if (duration.isBusinessTime) {
            DayPart dayPart = findDayPart(date);
            boolean isInbusinessHours = (dayPart != null);
            if (! isInbusinessHours) {
                Object[] result = new Object[2];
                findDay(date).findNextDayPartStart(0, date, result);
                date = (Date) result[0];
                dayPart = (DayPart) result[1];
            }
            end = dayPart.add(date, duration);
        } else {

            end = new Date(date.getTime() + duration.milliseconds);

        }
        return end;
    }


    /**
     * Добавляет к дате заданное количество рабочих дней с учетом выходных
     *
     * @param date
     * @param countOfDay
     * @return
     */
    public Date addBusinessDays(Date date, int countOfDay) {

        Calendar currCal = Calendar.getInstance();
        currCal.setTime(date);

        for (int i = 0; i < countOfDay; i++) {

            currCal.add(Calendar.DAY_OF_MONTH, 1);

            while (isHoliday(currCal.getTime())) {
                currCal.add(Calendar.DAY_OF_MONTH, 1);
            }


        }

        return currCal.getTime();

    }


    public DayPart findNextDayPart(Date date) {
        DayPart nextDayPart = null;
        while (nextDayPart == null) {
            nextDayPart = findDayPart(date);
            if (nextDayPart == null) {
                date = findStartOfNextDay(date);
            }
        }
        return nextDayPart;
    }


    /**
     * Находит следующую рабочую дату в соответсвии с выходными днями
     *
     * @param date
     * @return
     */
    public Date findStartOfNextDay(Date date) {
        Calendar calendar = getCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        date = calendar.getTime();
        while (isHoliday(date)) {
            calendar.setTime(date);
            calendar.add(Calendar.DATE, 1);
            date = calendar.getTime();
        }
        return date;
    }

    public Day findDay(Date date) {
        Calendar calendar = getCalendar();
        calendar.setTime(date);
        return weekDays[calendar.get(Calendar.DAY_OF_WEEK)];
    }

    /**
     * Проверяет является ли введенная дата выходным днем
     *
     * @param date
     * @return
     */
    public boolean isHoliday(Date date) {
        Iterator iter = holidays.iterator();


        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == 1/*воскресенье*/ || dayOfWeek == 7/*суббота*/) {
			return true; // проверка на выходной день
		}

        while (iter.hasNext()) {
            Holiday holiday = (Holiday) iter.next();
            if (holiday.includes(date)) {
                return true;
            }


        }
        return false;
    }

    private DayPart findDayPart(Date date) {
        DayPart dayPart = null;
        if (! isHoliday(date)) {
            Day day = findDay(date);
            for (int i = 0; ((i < day.dayParts.length)
                    && (dayPart == null)); i++) {
                DayPart candidate = day.dayParts[i];
                if (candidate.includes(date)) {
                    dayPart = candidate;
                }
            }
        }
        return dayPart;
    }

    /**
     * Проверяет является ли введенное время рабочим
     *
     * @param date
     * @return
     */
    public boolean isInBusinessHours(Date date) {
        return (findNextDayPart(date) != null);
    }

    public static Calendar getCalendar() {
        return new GregorianCalendar();
    }
}
