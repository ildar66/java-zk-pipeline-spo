package ru.masterdm.spo.pipeline.util;

/**
 * Represent Navigation Page(sourse from: https://www.zkoss.org/zkdemo/menu/navbar).
 * Add by Ildar Shafigullin on 13.09.2017.
 */
public abstract class NavigationPage {

    private String title;
    private String includeUri;
    private String subTitle;
    private Object data;

    public NavigationPage(String title, String subTitle, String includeUri, Object data) {
        super();
        this.title = title;
        this.subTitle = subTitle;
        this.includeUri = includeUri;
        this.data = data;
    }

    public abstract boolean isSelected();

    public String getTitle() {
        return title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public String getIncludeUri() {
        return includeUri;
    }

    public Object getData() {
        return data;
    }
}
