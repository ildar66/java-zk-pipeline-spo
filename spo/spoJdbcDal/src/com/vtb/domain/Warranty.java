package com.vtb.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import ru.masterdm.compendium.domain.Currency;
import ru.masterdm.compendium.domain.crm.Organization;
import ru.masterdm.compendium.domain.spo.Person;

import com.vtb.util.CollectionUtils;
import com.vtb.util.Formatter;
import ru.masterdm.spo.utils.SBeanLocator;

public class Warranty extends AbstractSupply {
	private static final long serialVersionUID = 1L;
	public static final Set<String> WarrantyKind = 
		CollectionUtils.set("Солидарное", "Субсидиарное");
	public static final Map<String,String> ResponsibilityValues =//односимвольный код для базы данных и описание
		CollectionUtils.map("mОсновной долг", "%% комиссии", "pШтрафы", "aВсе обязательства");
	private boolean fullSum = false;//На всю сумму обязательств
	private Double sum;// Предел ответственности
	private String printedSum;// Предел ответственности для отображения в отчете. нет setter, нет getter
	private String printedFullSum;//На всю сумму обязательств
	
	private Currency currency;//Валюта
	private String description;// Описание
	private Double transRisk;  // коэффициент транзакционного риска
	private Set<String> responsibility=new HashSet<String>();//Распределение ответственности
	private String responsibilityString="";//нужно для отчета
	//Выбор одного или нескольких значений из списка: Основной долг, % комиссии, Штрафы, Все обязательства.
	private String kind;//Вид поручительства
	private String fine;//Штрафные санкции к Поручителю
	private String fineListAsString;
	private String add;//Дополнительные обязательства поручителя
	private String guid=null;//уникальный идентификатор для id html элементов
	private ArrayList<Fine> fineList = new ArrayList<Fine>(); // штрафные санкции для поручителя
	
	/**
	 * @return На всю сумму обязательств
	 */
	public boolean isFullSum() {
		return fullSum;
	}
	/**
	 * @param На всю сумму обязательств
	 */
	public void setFullSum(boolean fullSum) {
		this.fullSum = fullSum;
		if (fullSum) {
			printedSum = null;
			printedFullSum = "Y";
		}
		else {
			String curCode = currency==null?"":currency.getCode();
			if(sum==null)
				printedSum = "";
			else
				printedSum = SBeanLocator.singleton().getDictService().moneyDisplay(new BigDecimal(sum), curCode);
			printedFullSum = "N";
		}
	}
	/**
	 * @return Предел ответственности
	 */
	public Double getSum() {
		return sum;
	}
	/**
	 * @return Предел ответственности
	 */
	public String getFormatedSum() {
		return sum==null?"":Formatter.toMoneyFormat(sum);
	}	
	/**
	 * @param sum Предел ответственности
	 */
	public void setSum(Double sum) {
		this.sum = sum;
		if (fullSum) {
			printedSum = null;
			printedFullSum = "Y";
		}
		else {
			String curCode = currency==null?"":currency.getCode();
			if(sum==null)
				printedSum = "";
			else
				printedSum = SBeanLocator.singleton().getDictService().moneyDisplay(new BigDecimal(sum), curCode);
			printedFullSum = "N";
		}
	}
	/**
	 * @return Валюта
	 */
	public Currency getCurrency() {
		return currency;
	}
	/**
	 * @param currency Валюта
	 */
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}
	/**
	 * @return Описание
	 */
	public String getDescription() {
		return description==null?"":description;
	}
	/**
	 * @param description Описание
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return коэффициент транзакционного риска
	 */
	public Double getTransRisk() {
		return transRisk;
	}
	/**
	 * @param transRisk коэффициент транзакционного риска
	 */
	public void setTransRisk(Double transRisk) {
		this.transRisk = transRisk;
	}
	/**
	 * @return Распределение ответственности
	 */
	public Set<String> getResponsibility() {
		return responsibility;
	}
	/**
	 * @param responsibility Распределение ответственности
	 */
	public void setResponsibility(Set<String> responsibility) {
		this.responsibility = responsibility;
		responsibilityString="";
        for (String s : responsibility){
            if(responsibilityString.length()>0)responsibilityString+=", ";
            responsibilityString += ResponsibilityValues.get(s);
        }
	}
	/**
	 * @param responsibility Распределение ответственности
	 */
	public void setResponsibility(String r) {
		responsibility=new HashSet<String>();
		if(r==null)return;
		for(char c : r.toCharArray()){
			responsibility.add(String.valueOf(c));
		}
		responsibilityString="";
		for (String s : responsibility){
		    if(responsibilityString.length()>0)responsibilityString+=", ";
		    responsibilityString += ResponsibilityValues.get(s);
		}
	}
	/**
	 * @return Вид поручительства
	 */
	public String getKind() {
		return kind;
	}
	/**
	 * @param kind Вид поручительства
	 */
	public void setKind(String kind) {
		this.kind = kind;
	}
	/**
	 * @return Штрафные санкции к Поручителю
	 */
	public String getFine() {
		return fine==null?"":fine;
	}
	/**
	 * @param fine Штрафные санкции к Поручителю
	 */
	public void setFine(String fine) {
		this.fine = fine;
	}
	/**
	 * @return Дополнительные обязательства поручителя
	 */
	public String getAdd() {
		return add==null?"":add;
	}
	/**
	 * @param add Дополнительные обязательства поручителя
	 */
	public void setAdd(String add) {
		this.add = add;
	}
	/**
	 * @return уникальный идентификатор для id html элементов
	 */
	public String getGuid() {
		if(guid==null)guid=java.util.UUID.randomUUID().toString();
		return guid;
	}
	public String getResponsibilityCodes() {
		String res="";
		Iterator<String> it = responsibility.iterator();
		while(it.hasNext())
			res += it.next();
		return res;
	}
	@Override
	public String getSupplyTypeName() {
		return "Поручитель";
	}
	@Override
	public String getSupplyType() {
		return "w";
	}

	@Override
	public BigDecimal getRating_zalog(BigDecimal exchangeRate, BigDecimal mainSum) {
	    if (fullSum) return mainSum;
	    if(currency==null||currency.getCode()==null||sum==null)return null;
		if (currency.getCode().equalsIgnoreCase("RUR"))return new BigDecimal(sum);
		if (exchangeRate==null)return null;
		return exchangeRate.multiply(new BigDecimal(sum));
	}
	public ArrayList<Fine> getFineList() {
		return fineList;
	}
	public void setFineList(ArrayList<Fine> fineList) {
		this.fineList = fineList;
	}
	
	public void generatefineListAsString() {
		fineListAsString = "";
		StringBuilder sb = new StringBuilder();
		if (fineList != null) 
			for (Fine element : fineList) {
				if (fineListAsString.isEmpty()) {
					sb.append(element.getPunitiveMeasure() + " (" + element.generateColumn2() + ")");
				} else
					sb.append("\n\n" + element.getPunitiveMeasure() + " (" + element.generateColumn2() + ")");
			}
		fineListAsString = sb.toString();
	}
	public TreeMap<String, String> getReportMap() {
		TreeMap<String, String> map = new TreeMap<String, String>();
		map.put("П_Поручитель_юр_лицо", "");
		map.put("П_Фамилия_поручителя", "");
		map.put("П_Имя_поручителя", "");
   		map.put("П_Отчество_поручителя", "");
   		if(getOrg() != null)
   			map.put("П_Поручитель_юр_лицо", getOrg().getAccount_name());
   		if(getPerson()!=null){
   			map.put("П_Фамилия_поручителя", getPerson().getLastName());
   			map.put("П_Имя_поручителя", getPerson().getName());
   			map.put("П_Отчество_поручителя", getPerson().getMiddleName());
   		}
   		map.put("П_Предел_ответственности", printedSum);
   		map.put("На_всю_сумму_обязательств", fullSum?"На всю сумму обязательств":"");
   		map.put("Дополнительное_обеспечение", isMain()?"Основное обеспечение":"Дополнительное обеспечение");
   		map.put("П_Рейтинг_поручителя", "");
   		map.put("П_Дополнительные_обязательства_поручителя", add==null?"":add);
   		map.put("П_Штрафные_санкции_к_Поручителю", fineListAsString);
   		return map;
	}

	@Override
	public int hashCode() {
		int result = sum != null ? sum.hashCode() : 0;
		String curCode = currency==null?"":currency.getCode();
		result = 31 * result + curCode.hashCode();
		result = 31 * result + (description != null ? description.hashCode() : 0);
		result = 31 * result + (transRisk != null ? transRisk.hashCode() : 0);
		result = 31 * result + (kind != null ? kind.hashCode() : 0);
		result = 31 * result + (fine != null ? fine.hashCode() : 0);
		result = 31 * result + (add != null ? add.hashCode() : 0);
		return result;
	}
}
