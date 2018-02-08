package ru.md.domain;

/**
 * Запись с идентификатором родительского подразделения
 * @author pmasalov
 */
public class DepartmentExt extends Department {

    static final public DepartmentExt ALL_DEPARTMENT = new DepartmentExt(-1L, "ВСЕ ПОДРАЗДЕЛЕНИЯ");

    private int level;
    private Long idDepartmentParent;
    private Long _isDashboardDep;
    private String longName;

    public DepartmentExt() {
        super();
    }

    public DepartmentExt(Long id, String longName) {
        super(id, longName);
        this.longName = longName;
    }

    /**
     * Returns .
     * @return
     */
    public int getLevel() {
        return level;
    }

    /**
     * Sets .
     * @param level
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * Returns .
     * @return
     */
    public Long getIdDepartmentParent() {
        return idDepartmentParent;
    }

    /**
     * Sets .
     * @param idDepartmentParent
     */
    public void setIdDepartmentParent(Long idDepartmentParent) {
        this.idDepartmentParent = idDepartmentParent;
    }

    public boolean isDashboardDep() {
        return _isDashboardDep == null || _isDashboardDep.equals(1L);
    }

    public void setIsDashboardDep(Long isDashboardDep) {
        this._isDashboardDep = isDashboardDep;
    }

    /**
     * Returns .
     * @return
     */
    public String getLongName() {
        return longName;
    }

    /**
     * Sets .
     * @param longName
     */
    public void setLongName(String longName) {
        this.longName = longName;
    }

}
