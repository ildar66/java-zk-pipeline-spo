package ru.masterdm.spo.dashboard;

import java.util.Calendar;

/**
 * @author pmasalov
 */
class YearManipulator implements DateManipulator {

    private void changeYear(DatePeriod datePeriod, int sign) {
        Calendar c = Calendar.getInstance();
        c.setTime(datePeriod.getDateTo());
        c.add(Calendar.YEAR, sign);
        c.set(Calendar.DAY_OF_YEAR, 1);
        datePeriod.setDateFrom(c.getTime());
        c.set(Calendar.MONTH, 11);
        c.set(Calendar.DAY_OF_MONTH, 31);
        datePeriod.setDateTo(c.getTime());
    }

    @Override
    public void increment(DatePeriod datePeriod) {
        changeYear(datePeriod, 1);
    }

    @Override
    public void decrement(DatePeriod datePeriod) {
        changeYear(datePeriod, -1);
    }
}
