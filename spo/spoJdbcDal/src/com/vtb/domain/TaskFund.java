package com.vtb.domain;


public class TaskFund {
    private Long id;//номер
    private String type;//Тип 
    private String category;//Категория 
    private String sum;//Сумма фондир. и валюта
    private String period;//Период выдачи. Период в формате «с <даты> по <дату>».
    private String status;//Статус 
    private String validto;//Заявка действительна до
    @Override
    public String toString(){
        return "status: "+status+" sum: "+sum;
    }
    public TaskFund() {
    	super();
    }
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getSum() {
		return sum;
	}
	public void setSum(String sum) {
		this.sum = sum;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getValidto() {
		return validto==null?"":validto;
	}
	public void setValidto(String validto) {
		this.validto = validto;
	}
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}

}
