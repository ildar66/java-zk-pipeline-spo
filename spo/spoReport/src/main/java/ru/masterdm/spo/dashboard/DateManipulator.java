package ru.masterdm.spo.dashboard;

/** Внутренний инструмент манипулирования с датами в режиме dateMode */
interface DateManipulator {

    void increment(DatePeriod datePeriod);

    void decrement(DatePeriod datePeriod);
}
