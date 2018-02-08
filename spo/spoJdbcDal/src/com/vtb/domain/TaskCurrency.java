package com.vtb.domain;

import ru.masterdm.compendium.domain.Currency;

public class TaskCurrency extends VtbObject{
	private static final long serialVersionUID = 1L;
	private Long id;
	private boolean flag;
	private Currency currency;
	
	public TaskCurrency(Long id) {
		super();
		this.id = id;
	}
	public boolean isFlag() {
		return flag;
	}
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public TaskCurrency(Long id, boolean flag, Currency currency) {
		super();
		this.id = id;
		this.flag = flag;
		this.currency = currency;
	}
    public Currency getCurrency() {
        return currency;
    }
    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
	
}