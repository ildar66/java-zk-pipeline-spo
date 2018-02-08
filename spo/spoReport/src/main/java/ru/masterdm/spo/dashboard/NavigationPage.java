package ru.masterdm.spo.dashboard;

import org.zkoss.bind.annotation.NotifyChange;

/**
 * Информация о странице отчёта исользуется в навигаторе выбора страницы
 * @author pmasalov
 */
public abstract class NavigationPage {

    private String title;
    private String includeUri;
    private String subTitle;
    private String taskType;
    private Integer creditDocumentary;

    public NavigationPage(String title, String subTitle, String includeUri, String taskType, Integer creditDocumentary) {
        super();
        this.title = title;
        this.subTitle = subTitle;
        this.includeUri = includeUri;
        this.taskType = taskType;
        this.creditDocumentary = creditDocumentary;
    }

    public abstract boolean isSelected();

    public String getFullTitle() {
        return getTitle() + " " + getSubTitle();
    }
    public String getTitle() {
        return title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public String getIncludeUri() {
        return includeUri;
    }

    public String getTaskType() {
        return taskType;
    }

    /**
     * Returns .
     * @return
     */
    public Integer getCreditDocumentary() {
        return creditDocumentary;
    }

    @Override
    public String toString() {
        return "NavigationPage{" +
                "title='" + title + '\'' +
                ", includeUri='" + includeUri + '\'' +
                ", subTitle='" + subTitle + '\'' +
                ", taskType='" + taskType + '\'' +
                ", creditDocumentary=" + creditDocumentary +
                '}';
    }
}
