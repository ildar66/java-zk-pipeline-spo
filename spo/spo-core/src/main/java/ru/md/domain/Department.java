package ru.md.domain;

/**
 * подразделение
 * @author Andrey Pavlenko
 */
public class Department {
	private Long id;
	private String name;
	private String nominativeCaseName;
	private String crmName;
	private Long isActive;
	private Long isCc;
	private Long isInitialDep;
	private Long isDashboardDep;
	private Long ccQuestionCopy;

    public Department() {
    }

    public Department(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCrmName() {
		return crmName;
	}
	public void setCrmName(String crmName) {
		this.crmName = crmName;
	}
	public Long getIsActive() {
		return isActive;
	}
	public void setIsActive(Long isActive) {
		this.isActive = isActive;
	}
	public boolean isInitial() {
		return isInitialDep!=null && isInitialDep.equals(1L);
	}
	public Long getIsInitialDep() {
		return isInitialDep;
	}
	public void setIsInitialDep(Long isInitialDep) {
		this.isInitialDep = isInitialDep;
	}
	public boolean isCc() {
		return isCc != null && isCc.equals(1L);
	}
	public Long getIsCc() {
		return isCc;
	}
	public void setIsCc(Long isCc) {
		this.isCc = isCc;
	}
	@Override
	public String toString() {
		return "Подразделение [id=" + id + ", name=" + name + ", crmName="
				+ crmName + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Department that = (Department) o;

		return id.equals(that.id);

	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	public boolean isDashboardDep() {
		return isDashboardDep != null && isDashboardDep.equals(1L);
	}
	public Long getIsDashboardDep() {
		return isDashboardDep;
	}

	public void setIsDashboardDep(Long isDashboardDep) {
		this.isDashboardDep = isDashboardDep;
	}

	/*public boolean isCcQuestionCopy() {
			return ccQuestionCopy != null && ccQuestionCopy.equals(1L);
	}*/
	public Long getCcQuestionCopy() {
		return ccQuestionCopy;
	}
	public void setCcQuestionCopy(Long ccQuestionCopy) {
		this.ccQuestionCopy = ccQuestionCopy;
	}

	/**
	 * Returns .
	 * @return
	 */
	public String getNominativeCaseName() {
		return nominativeCaseName;
	}

	/**
	 * Sets .
	 * @param nominativeCaseName
	 */
	public void setNominativeCaseName(String nominativeCaseName) {
		this.nominativeCaseName = nominativeCaseName;
	}
}
