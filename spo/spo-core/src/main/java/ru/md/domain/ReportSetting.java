package ru.md.domain;

/**
 * Created by Admin on 06.02.2017.
 */
public class ReportSetting {
    private Long id;
    private Long userId;
    private Long pub;
    private String name;

    /**
     * Returns .
     * @return
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets .
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns .
     * @return
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * Sets .
     * @param userId
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * Returns .
     * @return
     */
    public Long getPub() {
        return pub;
    }

    /**
     * Sets .
     * @param pub
     */
    public void setPub(Long pub) {
        this.pub = pub;
    }

    /**
     * Returns .
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Sets .
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }
}
