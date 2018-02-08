package ru.md.spo.dashboard;

import org.junit.Assert;
import org.junit.Test;

import ru.masterdm.spo.dashboard.domain.DatePeriod;
import ru.masterdm.spo.dashboard.helper.DatePeriodSplitter;
import ru.masterdm.spo.utils.Formatter;

/**
 * Created by Andrey Pavlenko on 24.11.2016.
 */
public class DatePeriodSplitterTest extends Assert {
/*Точек на каждой линии, а также значений на горизонтальной оси дат должно быть не более 20 вне зависимости от длительности объединенного
        периода. Если период менее 20 дней – использовать цену деления 1 календарный день;*/
    @Test(timeout=7000)
    public  void testLess20() {
        assertEquals(12, new DatePeriodSplitter(Formatter.parseDate("01.08.2015"),Formatter.parseDate("12.08.2015")).getPeriod().length);
        assertEquals(1, new DatePeriodSplitter(Formatter.parseDate("01.08.2015"),Formatter.parseDate("01.08.2015")).getPeriod().length);
    }

    /* если период > 20 дней, то цена деления должна рассчитываться
        как округленный до целого, в большую сторону результат деления длительности объединенного интервала в днях на 20. Например, при длительности
        объединенного интервала в 26 дней цна деления 2 дня, точек – 13.*/
    @Test(timeout=7000)
    public  void test26() {
        DatePeriod[] periods = new DatePeriodSplitter(Formatter.parseDate("01.08.2015"),Formatter.parseDate("26.08.2015")).getPeriod();
        assertEquals(13, periods.length);
        assertEquals(Formatter.parseDate("01.08.2015"), periods[0].from);
        assertEquals(Formatter.parseDate("02.08.2015"), periods[0].to);
        assertEquals(Formatter.parseDate("25.08.2015"), periods[12].from);
        assertEquals(Formatter.parseDate("26.08.2015"), periods[12].to);
    }

    @Test(timeout=7000)
    public  void test27() {
        DatePeriod[] periods = new DatePeriodSplitter(Formatter.parseDate("01.08.2015"),Formatter.parseDate("27.08.2015")).getPeriod();
        assertEquals(14, periods.length);
        assertEquals(Formatter.parseDate("01.08.2015"), periods[0].from);
        assertEquals(Formatter.parseDate("02.08.2015"), periods[0].to);
        assertEquals(Formatter.parseDate("25.08.2015"), periods[12].from);
        assertEquals(Formatter.parseDate("26.08.2015"), periods[12].to);
        assertEquals(Formatter.parseDate("27.08.2015"), periods[13].from);
        assertEquals(Formatter.parseDate("27.08.2015"), periods[13].to);
    }
}
