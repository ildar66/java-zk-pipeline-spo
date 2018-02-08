package ru.masterdm.spo.dashboard.fn;

import java.util.Date;

/**
 * @author pmasalov
 */
public class CommonFns {

    public static final String formatNumberSafe(Object value, String format) {
        if (value == null)
            return null;
        return org.zkoss.xel.fn.CommonFns.formatNumber(value, format);
    }

    public static final String formatDateSafe(Date date, String pattern) {
        if (date == null)
            return null;
        return org.zkoss.xel.fn.CommonFns.formatDate(date, pattern);
    }
}
