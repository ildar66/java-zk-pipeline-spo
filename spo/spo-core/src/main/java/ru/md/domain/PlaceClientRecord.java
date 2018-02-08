package ru.md.domain;

/**
 * Место проведения сделки и привязанная клиентская запись.
 * @author Sergey Lysenkov
 */
public class PlaceClientRecord {
	private Long id; 				//idDepartment проведения сделки
	private String name; 			//название департамента проведения сделки
	private String idUnatedClient;
	private String division;		//имя подразделения проведения сделки
	private String crmId;			//клиентская запись
	private String organizationName;
	private String inn;
	private String clientType;// (Клиент, Бывший клиент, ...)
	private String clientCategory;	// (Крупный бизнес, ...)
	
	public PlaceClientRecord() {
		super();
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
	public String getIdUnatedClient() {
		return idUnatedClient;
	}
	public void setIdUnatedClient(String idUnatedClient) {
		this.idUnatedClient = idUnatedClient;
	}
	public String getDivision() {
		if (division == null)
			return "";
		return division;
	}
	public void setDivision(String division) {
		this.division = division;
	}
	public String getCrmId() {
		return crmId;
	}
	public void setCrmId(String crmId) {
		this.crmId = crmId;
	}
	public String getOrganizationName() {
		return organizationName;
	}
	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}
	public String getInn() {
		return inn;
	}
	public void setInn(String inn) {
		this.inn = inn;
	}
	public String getClientType() {
		return clientType;
	}
	public void setClientType(String clientType) {
		this.clientType = clientType;
	}
	public String getClientCategory() {
		if (clientCategory == null)
			return "";
		return clientCategory;
	}
	public void setClientCategory(String clientCategory) {
		this.clientCategory = clientCategory;
	}
}
