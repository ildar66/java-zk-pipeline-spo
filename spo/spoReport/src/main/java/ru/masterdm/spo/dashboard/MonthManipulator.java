package ru.masterdm.spo.dashboard;

import java.util.Calendar;

/**
 * @author pmasalov
 */
class MonthManipulator implements DateManipulator {

    private void changeMonth(DatePeriod datePeriod, int sign) {
        Calendar c = Calendar.getInstance();
        c.setTime(datePeriod.getDateTo());
        c.add(Calendar.MONTH, sign);
        c.set(Calendar.DATE, 1);
        datePeriod.setDateFrom(c.getTime());
        c.set(Calendar.DATE, c.getActualMaximum(Calendar.DATE));
        datePeriod.setDateTo(c.getTime());
    }

    @Override
    public void increment(DatePeriod datePeriod) {
        changeMonth(datePeriod, 1);
    }

    @Override
    public void decrement(DatePeriod datePeriod) {
        changeMonth(datePeriod, -1);
    }
}
