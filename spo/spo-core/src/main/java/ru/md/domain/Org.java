package ru.md.domain;

/**
 * Организация. Юридическое лицо.
 * @author Andrey Pavlenko
 */
public class Org {
	private String id;
	private String name;
	private String inn;
	private String ogrn;
	private String clientType;
	private String region;
	private String regionId;
	private String division;
	private String groupname;
	private String preliminaryRating;
	private String branch;
	private String idUnitedClient;
	private String industry;
	private String resident;

	public Org() {
		super();
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getInn() {
		return inn;
	}
	public void setInn(String inn) {
		this.inn = inn;
	}
	public String getClientType() {
		return clientType==null?"":clientType;
	}
	public void setClientType(String clientType) {
		this.clientType = clientType;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}	
	public String getRegionId() {
		return regionId;
	}
	public void setRegionId(String regionId) {
		this.regionId = regionId;
	}
	public String getDivision() {
		return division;
	}
	public void setDivision(String division) {
		this.division = division;
	}
	public String getGroupname() {
		return groupname==null?"":groupname;
	}
	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}
    /**
     * Возвращает рейтинг ПКР.
     * @return рейтинг ПКР
     */
    public String getPreliminaryRating() {
        return preliminaryRating;
    }
    /**
     * Устанавливает рейтинг ПКР.
     * @param preliminaryRating рейтинг ПКР
     */
    public void setPreliminaryRating(String preliminaryRating) {
        this.preliminaryRating = preliminaryRating;
    }
    /**
     * Возвращает отрасль.
     * @return отрасль
     */
    public String getBranch() {
        return branch;
    }
    /**
     * Устанавливает отрасль.
     * @param branch отрасль
     */
    public void setBranch(String branch) {
        this.branch = branch;
    }

	/**
	 * Returns .
	 * @return
	 */
	public String getIdUnitedClient() {
		return idUnitedClient;
	}

	/**
	 * Sets .
	 * @param idUnitedClient
	 */
	public void setIdUnitedClient(String idUnitedClient) {
		this.idUnitedClient = idUnitedClient;
	}

	/**
	 * Returns .
	 * @return
	 */
	public String getIndustry() {
		return industry;
	}

	/**
	 * Sets .
	 * @param industry
	 */
	public void setIndustry(String industry) {
		this.industry = industry;
	}

	/**
	 * Returns .
	 * @return
	 */
	public String getOgrn() {
		return ogrn;
	}

	/**
	 * Sets .
	 * @param ogrn
	 */
	public void setOgrn(String ogrn) {
		this.ogrn = ogrn;
	}

	/**
	 * Returns .
	 * @return
	 */
	public String getResident() {
		return resident;
	}

	/**
	 * Sets .
	 * @param resident
	 */
	public void setResident(String resident) {
		this.resident = resident;
	}
}
