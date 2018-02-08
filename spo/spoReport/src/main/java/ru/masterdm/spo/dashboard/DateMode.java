package ru.masterdm.spo.dashboard;

/**
 * @author pmasalov
 */
enum DateMode {
    DAY(new DayManipulator()), MONTH(new MonthManipulator()), YEAR(new YearManipulator());

    private DateManipulator dateManipulator;

    DateMode(DateManipulator dateManipulator) {
        this.dateManipulator = dateManipulator;
    }

    /**
     * Returns .
     * @return
     */
    public DateManipulator getDateManipulator() {
        return dateManipulator;
    }
}
