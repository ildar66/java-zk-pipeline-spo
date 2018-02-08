package com.vtb.domain;


public class N6 {
    private Long id;//номер
    private String sum;//Сумма фондир. и валюта
    private String period;//Плановые даты. Период в формате «с <даты> по <дату>».
    private String status;//Статус 
    @Override
    public String toString(){
        return "status: "+status+" sum: "+sum;
    }
    public N6() {
    	super();
    }
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}

}
