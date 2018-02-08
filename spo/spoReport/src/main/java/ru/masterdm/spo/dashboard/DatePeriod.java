package ru.masterdm.spo.dashboard;

import java.util.Date;

/**
 * @author pmasalov
 */
public interface DatePeriod {

    public Date getDateFrom();

    public void setDateFrom(Date dateFrom);

    public Date getDateTo();

    public void setDateTo(Date dateTo);
}
