package com.vtb.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class Task4Rating implements Serializable {
	private static final long serialVersionUID = -7049983638227827645L;

	private Long idMdTask;//idMdTask
	private String numberDisplay;
	private BigDecimal sum;
	private Integer period;
	private boolean rateType;
	private Long operationTypeCode;
	private List<Supply4Rating> supplyList;
	public Task4Rating() {
		super();
		supplyList = new ArrayList<Supply4Rating>();
	}
	/**
	 * @return Р РЋРЎС“Р С�Р С�Р В° РЎРѓР Т‘Р ВµР В»Р С”Р С‘ Р Р† РЎР‚РЎС“Р В±Р В»РЎРЏРЎвЂ¦
	 */
	public BigDecimal getSum() {
		return sum;
	}
	/**
	 * @param Р РЋРЎС“Р С�Р С�Р В° РЎРѓР Т‘Р ВµР В»Р С”Р С‘ Р Р† РЎР‚РЎС“Р В±Р В»РЎРЏРЎвЂ¦
	 */
	public void setSum(BigDecimal sum) {
		this.sum = sum;
	}
	/**
	 * @return Р РЋРЎР‚Р С•Р С” РЎРѓР Т‘Р ВµР В»Р С”Р С‘ Р Р† Р Т‘Р Р…РЎРЏРЎвЂ¦
	 */
	public Integer getPeriod() {
		return period;
	}
	/**
	 * @param Р РЋРЎР‚Р С•Р С” РЎРѓР Т‘Р ВµР В»Р С”Р С‘ Р Р† Р Т‘Р Р…РЎРЏРЎвЂ¦
	 */
	public void setPeriod(Integer period) {
		this.period = period;
	}
	/**
	 * @return Р СћР С‘Р С— РЎРѓРЎвЂљР В°Р Р†Р С”Р С‘: РЎвЂћР С‘Р С”РЎРѓР С‘РЎР‚Р С•Р Р†Р В°Р Р…Р Р…Р В°РЎРЏ(true) / Р С—Р В»Р В°Р Р†Р В°РЎР‹РЎвЂ°Р В°РЎРЏ (false)
	 */
	public boolean isRateType() {
		return rateType;
	}
	/**
	 * @param Р СћР С‘Р С— РЎРѓРЎвЂљР В°Р Р†Р С”Р С‘: РЎвЂћР С‘Р С”РЎРѓР С‘РЎР‚Р С•Р Р†Р В°Р Р…Р Р…Р В°РЎРЏ(true) / Р С—Р В»Р В°Р Р†Р В°РЎР‹РЎвЂ°Р В°РЎРЏ (false)
	 */
	public void setRateType(boolean rateType) {
		this.rateType = rateType;
	}
	/**
	 * @return the Р СћР С‘Р С— Р С•Р С—Р ВµРЎР‚Р В°РЎвЂ Р С‘Р С‘
	 */
	public Long getOperationTypeCode() {
		return operationTypeCode;
	}
	/**
	 * @param operationType Р СћР С‘Р С— Р С•Р С—Р ВµРЎР‚Р В°РЎвЂ Р С‘Р С‘
	 */
	public void setOperationTypeCode(Long operationType) {
		this.operationTypeCode = operationType;
	}
	/**
	 * @return Р СљР В°РЎРѓРЎРѓР С‘Р Р†<Р РЋРЎвЂљРЎР‚РЎС“Р С”РЎвЂљРЎС“РЎР‚Р В° Р С•Р В±Р ВµРЎРѓР С—Р ВµРЎвЂЎР ВµР Р…Р С‘РЎРЏ>
	 */
	public List<Supply4Rating> getSupplyList() {
		return supplyList;
	}
	/**
	 * @param supplyList Р СљР В°РЎРѓРЎРѓР С‘Р Р†<Р РЋРЎвЂљРЎР‚РЎС“Р С”РЎвЂљРЎС“РЎР‚Р В° Р С•Р В±Р ВµРЎРѓР С—Р ВµРЎвЂЎР ВµР Р…Р С‘РЎРЏ>
	 */
	public void setSupplyList(List<Supply4Rating> supplyList) {
		this.supplyList = supplyList;
	}
	/**
	 * @return the idMdTask
	 */
	public Long getIdMdTask() {
		return idMdTask;
	}
	/**
	 * @param idMdTask the idMdTask to set
	 */
	public void setIdMdTask(Long idMdTask) {
		this.idMdTask = idMdTask;
	}
	/**
	 * @return Р СњР С•Р С�Р ВµРЎР‚ Р В·Р В°РЎРЏР Р†Р С”Р С‘
	 */
	public String getNumberDisplay() {
		return numberDisplay;
	}
	/**
	 * @param val Р СњР С•Р С�Р ВµРЎР‚ Р В·Р В°РЎРЏР Р†Р С”Р С‘
	 */
	public void setNumberDisplay(String val) {
		this.numberDisplay = val;
	}
	
}
