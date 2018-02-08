package ru.md.domain.dashboard;

/**
 * Created by Andrey Pavlenko on 30.09.2016.
 */
public class TaskListFilter {
    public Long searchNumber;
    public String searchCurrency;
    public Long searchSumFrom;
    public Long searchSumTo;
    public String searchType;
    public String searchStatus;
    public String searchInitDepartment;
    public String searchPriority;
    public String searchProcessType;
    public String searchContractor;

    @Override
    public String toString() {
        return "TaskListFilter{" +
                "searchNumber=" + searchNumber +
                ", searchCurrency='" + searchCurrency + '\'' +
                ", searchSumFrom=" + searchSumFrom +
                ", searchSumTo=" + searchSumTo +
                ", searchType='" + searchType + '\'' +
                ", searchStatus='" + searchStatus + '\'' +
                ", searchInitDepartment='" + searchInitDepartment + '\'' +
                ", searchPriority='" + searchPriority + '\'' +
                ", searchProcessType='" + searchProcessType + '\'' +
                ", searchContractor='" + searchContractor + '\'' +
                '}';
    }

    public TaskListFilter() {
        this.searchCurrency = "";
        this.searchType = "";
        this.searchStatus = "";
        this.searchInitDepartment = "";
        this.searchPriority = "";
        this.searchProcessType = "";
        this.searchContractor = "";
    }
}
