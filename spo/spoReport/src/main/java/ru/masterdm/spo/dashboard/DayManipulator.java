package ru.masterdm.spo.dashboard;

import java.util.Calendar;
import java.util.Date;

/**
 * @author pmasalov
 */
class DayManipulator implements DateManipulator {

    private void changeDay(DatePeriod datePeriod, int sign) {
        Calendar c = Calendar.getInstance();
        c.setTime(datePeriod.getDateFrom());
        c.add(Calendar.DATE, sign);
        Date d = c.getTime();
        datePeriod.setDateFrom(d);
        datePeriod.setDateTo(d);
    }

    @Override
    public void increment(DatePeriod datePeriod) {
        changeDay(datePeriod, 1);
    }

    @Override
    public void decrement(DatePeriod datePeriod) {
        changeDay(datePeriod, -1);
    }
}
