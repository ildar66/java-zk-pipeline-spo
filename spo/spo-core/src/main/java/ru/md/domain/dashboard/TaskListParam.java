package ru.md.domain.dashboard;

/**
 * Created by Andrey Pavlenko on 30.09.2016.
 */
public class TaskListParam {
    public Long creditDocumentary;
    public String isTradingDeskOthers;
    public String tradingDeskSelected;
    public String departments;
    public String branch;
    public Long[] departmentsIds;
    public Long[] tradingDeskSelectedIds;

    public TaskListParam() {
        this.creditDocumentary = 0L;
        this.isTradingDeskOthers = "false";
        this.tradingDeskSelected = "";
        this.departments = "";
        this.branch = "";
    }
}
