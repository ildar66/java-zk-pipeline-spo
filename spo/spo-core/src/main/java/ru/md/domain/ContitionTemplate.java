package ru.md.domain;

public class ContitionTemplate {

	private Long id;
	private Long idType;
	private String name;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		//экранировать кавычки и перевод строки
		return name.replaceAll("\"", "").replaceAll("'", "").replaceAll("\n", " ").replaceAll("\r", " ");
	}
	public void setName(String name) {
		this.name = name;
	}
	public ContitionTemplate(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
	public ContitionTemplate() {
		super();
	}
	public Long getIdType() {
		return idType;
	}
	public void setIdType(Long idType) {
		this.idType = idType;
	}
}
