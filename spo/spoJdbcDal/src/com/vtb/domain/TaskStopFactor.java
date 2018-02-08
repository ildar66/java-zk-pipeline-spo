package com.vtb.domain;

import ru.masterdm.compendium.domain.spo.StopFactor;

public class TaskStopFactor   extends VtbObject{
	private Long id;// это айдишник-уникальный ключ. Не меняется. Используется как первичный ключ в БД
	private static final long serialVersionUID = 1L;
	private boolean flag;
	private StopFactor stopFactor;
	public boolean isFlag() {
		return flag;
	}
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	public StopFactor getStopFactor() {
		return stopFactor;
	}
	public void setStopFactor(StopFactor stopFactor) {
		this.stopFactor = stopFactor;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	
	public TaskStopFactor(boolean flag, StopFactor stopFactor) {
		super();
		this.flag = flag;
		this.stopFactor = stopFactor;

        validate();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        if ((stopFactor == null) || (stopFactor.getId() == null))  
            addError("Стоп-фактор. Ссылка на справочник стоп-факторов должна быть задана");
    }
	
}
