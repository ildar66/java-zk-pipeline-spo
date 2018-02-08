package ru.masterdm.spo.dashboard.helper;

import java.util.ArrayList;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import ru.masterdm.spo.dashboard.domain.DatePeriod;

/**
 * Created by Andrey Pavlenko on 24.11.2016.
 */
public class DatePeriodSplitter {
    final static int maxAxisXval = 20;

    private Date start;
    private Date to;

    public DatePeriodSplitter(Date start, Date to) {
        this.start = start;
        this.to = to;
    }
    public DatePeriod[] getPeriod() {
        if (start == null || to == null)
            return new DatePeriod[0];
        /*Точек на каждой линии, а также значений на горизонтальной оси дат должно быть не более 20 вне зависимости от длительности объединенного
        периода. Если период менее 20 дней – использовать цену деления 1 календарный день; если период > 20 дней, то цена деления должна рассчитываться
        как округленный до целого, в большую сторону результат деления длительности объединенного интервала в днях на 20. Например, при длительности
        объединенного интервала в 26 дней цна деления 2 дня, точек – 13.*/
        Long duration = new Duration(new DateTime(start), new DateTime(to)).getStandardDays();
        int step = (duration.intValue() - 1) / maxAxisXval + 1;
        ArrayList<DatePeriod> list = new ArrayList<DatePeriod>();
        for (int i = 0; i < maxAxisXval &&  !(new DateTime(start).plusDays(i*step).toDate().after(to)); i++){
            DatePeriod period = new DatePeriod();
            period.from = new DateTime(start).plusDays(i*step).toDate();
            period.to = min(to, new DateTime(start).plusDays( (i+1)*step -1).toDate());
            list.add(period);
        }
        return list.toArray(new DatePeriod[list.size()]);
    }

    public static Date min(Date d1, Date d2){
        if(d2==null)
            return d1;
        if(d1==null)
            return d2;
        return d1.after(d2)?d2:d1;
    }

    public static Date max(Date d1, Date d2){
        if(d2==null)
            return d1;
        if(d1==null)
            return d2;
        return d1.after(d2)?d1:d2;
    }
}
