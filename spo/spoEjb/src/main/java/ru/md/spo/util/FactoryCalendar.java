package ru.md.spo.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Производственный календарь.
 * Вместо него нужно использовать производственный календарь в компендиуме одобренный Валиевым.
 * @author Andrey Pavlenko
 */
@Deprecated
public class FactoryCalendar {
    private static final Logger LOGGER = Logger.getLogger(FactoryCalendar.class.getName());
    private static transient final SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
    private Set<Date> holiDays = new HashSet<Date>();
    private Set<Date> workWeekends = new HashSet<Date>();
    private volatile static FactoryCalendar instance;
    private static final int[] TIME_FIELDS =
    {
      Calendar.HOUR_OF_DAY,
      Calendar.HOUR,
      Calendar.AM_PM,
      Calendar.MINUTE,
      Calendar.SECOND,
      Calendar.MILLISECOND
    };
    /*
     * Календарь можно скачать. Нормального машиночитаемого формата так и не смог найти.
     * 2010 http://www.klerk.ru/glossary/162591/
     * 2011 http://www.klerk.ru/glossary/198486/
     */
    private FactoryCalendar() {
        //начитать производственный календарь из файла
        String cal = ResourceLoader.getFile("calendar/work.iCal");
        cal=cal.replaceAll("\r\n", ";");
        Pattern p = Pattern.compile("BEGIN:VEVENT;DTSTART:(.*?);SUMMARY:(.*?);END:VEVENT");
        Matcher m = p.matcher(cal);
        while(m.find()){
            if(m.group(2).equalsIgnoreCase("workday"))
                try {
                    workWeekends.add(df.parse(m.group(1)));
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, e.getMessage(), e);
                }
            if(m.group(2).equalsIgnoreCase("rest"))
                try {
                    holiDays.add(df.parse(m.group(1)));
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, e.getMessage(), e);
                }
        }
    }
 
    /**
     * @return разница между днями в РАБОЧИХ днях, а не календарных
     * Осторожно задавайте время.
     * Разница между 11 вечера понедельника и часом ночи вторника - один день.
     * Разница между часом ночи понедельника и 11 вечера понедельника - ноль дней.
     */
    public Long dateDelta(Date date1, Date date2){
        if (date1.after(date2))return dateDelta(date2, date1);
        Calendar calendar1 = Calendar.getInstance();calendar1.setTime(date1);
        Calendar calendar2 = Calendar.getInstance();calendar2.setTime(date2);
        //сбрасываем время, оставляем только дату
        for(int i : TIME_FIELDS)
            calendar1.clear(i);
        for(int i : TIME_FIELDS)
            calendar2.clear(i);
        // Находим разницу между двумя календарями в милисекундах
        long diff = calendar2.getTimeInMillis() - calendar1.getTimeInMillis();
        diff = diff / (1000 * 60 * 60 * 24);
        for(Date holiday: holiDays){
            if(holiday.after(date1) && holiday.before(date2))
                diff -= 1;
        }
        while(calendar1.before(calendar2)){
            calendar1.add(Calendar.DAY_OF_YEAR, 1);
            if ((calendar1.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ||
                    calendar1.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
                    &&!workWeekends.contains(new Date(calendar1.getTimeInMillis())))
                diff -= 1;
        }

        return Long.valueOf(diff);
    }
    public static FactoryCalendar getInstance() {
        if (instance == null) {
            LOGGER.info("create new Instance");
            synchronized (FactoryCalendar.class) {
                if (instance == null) {
                    instance = new FactoryCalendar();
                }
            }
        }
        return instance;
    }
}
