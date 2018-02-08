package ru.md.domain;

import java.util.Date;

import ru.masterdm.spo.utils.Formatter;

/**
 * выдача.
 * @author Andrey Pavlenko
 */
public class Withdraw {
	private Long id;
	private Long idMdtask;
	private Long idTrance;
	private Double sum;
	private String currency;
	private Date usedatefrom;
	private Date usedateto;
	private Long month;
	private Long quarter;
	private Long year;
	private Long hyear;//полугодие
	private String formatedDates;
	private String sumScope;
	private String periodDimensionFrom; 
	private String periodDimensionBefore; 
	private Long fromPeriod;
	private Long beforePeriod;
	
	
	public Withdraw(Long id_mdtask, Long id_trance, Double sum, String currency,
			Date from, Date to, Long month,Long quarter,Long year,Long hyear, String sumScope, 
			String periodDimensionFrom, String periodDimensionBefore,  Long fromPeriod, Long beforePeriod) {
		super();
		this.idMdtask = id_mdtask;
		this.idTrance = id_trance;
		this.sum = sum;
		this.currency = currency;
		this.usedatefrom=from;
		this.usedateto=to;
		this.month=month;
		this.quarter=quarter;
		this.year=year;
		this.hyear=hyear;
		this.sumScope=sumScope;
		this.periodDimensionFrom=periodDimensionFrom;
		this.periodDimensionBefore=periodDimensionBefore;
		this.fromPeriod=fromPeriod;
		this.beforePeriod=beforePeriod;
		
	}
	
	public Withdraw() {
		super();
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getIdMdtask() {
		return idMdtask;
	}
	public void setIdMdtask(Long id_mdtask) {
		this.idMdtask = id_mdtask;
	}
	public Long getIdTrance() {
		return idTrance;
	}
	public void setIdTrance(Long id_trance) {
		this.idTrance = id_trance;
	}
	public String getSumFormated() {
		return Formatter.format2point(sum);
	}
	public Double getSum() {
		return sum;
	}
	public void setSum(Double sum) {
		this.sum = sum;
	}
	public String getSumCurrency() {
		return getSumFormated()+" "+getCurrency();
	}
	public String getCurrency() {
		return currency==null?"":currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public void generateFormatetDates(String format){
		formatedDates = " ";
		if(format==null)
			return;
		if(format.equals("period"))
			formatedDates = "От " + Formatter.format(fromPeriod) + " " + getPeriodDimensionDisplay(periodDimensionFrom) + " до " +
					Formatter.format(beforePeriod) + " " + getPeriodDimensionDisplay(periodDimensionBefore);
		if(format.equals("month"))
			formatedDates = Formatter.getMonthNameRus(getMonth())+" "+getYearStr()+" года";
		if(format.equals("quarter"))
			formatedDates = getQuarter()+" квартал "+getYearStr()+" года";
		if(format.equals("hyear"))
			formatedDates = getHyear()+" полугодие "+getYearStr()+" года";
		if(format.equals("year"))
			formatedDates = getYearStr()+" год";
		if(format.equals("date")){
			if(getUsedatefrom()!=null)
				formatedDates += "с "+Formatter.format(getUsedatefrom());
			if(getUsedateto()!=null)
				formatedDates += " по "+Formatter.format(getUsedateto()); 
		}
	}
	public String getDates() {
		return formatedDates;
	}
	public String getFrom() {
		return Formatter.format(usedatefrom);
	}
	public String getTo() {
		return Formatter.format(usedateto);
	}
	public Date getUsedatefrom() {
		return usedatefrom;
	}
	public void setUsedatefrom(Date usedatefrom) {
		this.usedatefrom = usedatefrom;
	}
	public Date getUsedateto() {
		return usedateto;
	}
	public void setUsedateto(Date usedateto) {
		this.usedateto = usedateto;
	}
	public Long getMonth() {
		return month==null?1L:month;
	}
	public void setMonth(Long month) {
		this.month = month;
	}
	public Long getQuarter() {
		return quarter==null?1L:quarter;
	}
	public void setQuarter(Long quarter) {
		this.quarter = quarter;
	}
	public String getYearStr() {
		return Formatter.format(year);
	}
	public Long getYear() {
		return year;
	}
	public void setYear(Long year) {
		this.year = year;
	}
	public Long getHyear() {
		return hyear==null?1L:hyear;
	}
	public void setHyear(Long hyear) {
		this.hyear = hyear;
	}

	public String getSumScope() {
		return sumScope;
	}
	public String getSumScopeDisplay() {
		if(sumScope == null)
			return "";
		if(sumScope.equals("noLess"))
			return "Не менее";
		if(sumScope.equals("noMore"))
			return "Не более";
		return sumScope;
	}
	public static String getPeriodDimensionDisplay(String periodDimension){
		if(periodDimension==null)
			return "";
		if(periodDimension.equals("days"))
			return "дн.";
		if(periodDimension.equals("months"))
			return "мес.";
		if(periodDimension.equals("years"))
			return "г./лет";
		return "";
	}

	public void setSumScope(String sumScope) {
		this.sumScope = sumScope;
	}
	
	public String getPeriodDimensionFrom() {
		return periodDimensionFrom;
	}

	public void setPeriodDimensionFrom(String periodDimensionFrom) {
		this.periodDimensionFrom = periodDimensionFrom;
	}
	
	public String getPeriodDimensionBefore() {
		return periodDimensionBefore;
	}

	public void setPeriodDimensionBefore(String periodDimensionBefore) {
		this.periodDimensionBefore = periodDimensionBefore;
	}
	
	public Long getFromPeriod() {
		return fromPeriod;
	}

	public void setFromPeriod(Long fromPeriod) {
		this.fromPeriod = fromPeriod;
	}

	public Long getBeforePeriod() {
		return beforePeriod;
	}

	public void setBeforePeriod(Long beforePeriod) {
		this.beforePeriod = beforePeriod;
	}

	@Override
	public String toString() {
		return "Withdraw [id=" + id + ", idMdtask=" + idMdtask
				+ ", idTrance=" + idTrance + "]";
	}

	@Override
	public int hashCode() {
		int result = sum != null ? sum.hashCode() : 0;
		result = 31 * result + (currency != null ? currency.hashCode() : 0);
		result = 31 * result + (usedatefrom != null ? usedatefrom.hashCode() : 0);
		result = 31 * result + (usedateto != null ? usedateto.hashCode() : 0);
		result = 31 * result + (month != null ? month.hashCode() : 0);
		result = 31 * result + (quarter != null ? quarter.hashCode() : 0);
		result = 31 * result + (year != null ? year.hashCode() : 0);
		result = 31 * result + (hyear != null ? hyear.hashCode() : 0);
		result = 31 * result + (formatedDates != null ? formatedDates.hashCode() : 0);
		result = 31 * result + (sumScope != null ? sumScope.hashCode() : 0);
		result = 31 * result + (periodDimensionFrom != null ? periodDimensionFrom.hashCode() : 0);
		result = 31 * result + (periodDimensionBefore != null ? periodDimensionBefore.hashCode() : 0);
		result = 31 * result + (fromPeriod != null ? fromPeriod.hashCode() : 0);
		result = 31 * result + (beforePeriod != null ? beforePeriod.hashCode() : 0);
		return result;
	}
}
