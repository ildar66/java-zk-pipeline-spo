package com.vtb.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * @author Andrey Pavlenko
 * Обеспечение
 */
public class TaskSupply extends VtbObject{
	
	public static final String PermissionAttribute="R_Обеспечение";
	
	private static final long serialVersionUID = 1L;
	private	String additionSupply;//[mdtask.ADDSUPPLY] Дополнительное обеспечение
	private boolean exist;//обеспечение предусмотренно
	private ArrayList<Deposit> deposit;//залог
	private ArrayList<Guarantee> guarantee;//гарантии
	private ArrayList<Warranty> warranty;//поручительства
	private String guaranteeCondition;//Индивидуальные условия гарантий
	private String dCondition;//Индивидуальные условия залоговых сделок
	private String wCondition;//Индивидуальные условия поручителей
	private HashMap<String,String> depositKeyValue;
	private Double cfact;//Фактический коэффициент транзакционного риска

   public TaskSupply() {
        super();
        this.additionSupply="";
        deposit = new ArrayList<Deposit>();
        guarantee = new ArrayList<Guarantee>();
        warranty = new ArrayList<Warranty>();
        depositKeyValue = new LinkedHashMap<String, String>();
    }

   public boolean isEmpty(){
       return emptyDeposit() && emptyGuarantee() && emptyWarranty() 
           && getAdditionSupply().isEmpty();
   }

	/**
	 * @return Фактический коэффициент транзакционного риска
	 */
	public Double getCfact() {
		return cfact;
	}


	/**
	 * @param cfact Фактический коэффициент транзакционного риска
	 */
	public void setCfact(Double cfact) {
		this.cfact = cfact;
	}

	/**
	 * @return the warranty
	 */
	public ArrayList<Warranty> getWarranty() {
		return warranty;
	}
	public ArrayList<AbstractSupply> getAllSupply() {
		ArrayList<AbstractSupply> res = new ArrayList<AbstractSupply>();
		res.addAll(deposit);
		res.addAll(warranty);
		res.addAll(guarantee);
		return res;
	}


	/**
	 * Дополнительное обеспечение
	 */
	public String getAdditionSupply() {
		return additionSupply==null?"":additionSupply;
	}
	
	public void setAdditionSupply(String additionSupply) {
		this.additionSupply = additionSupply;
	}

	/**
	 * @return обеспечение предусмотренно
	 */
	public boolean isExist() {
		return exist;
	}

	/**
	 * @param exist обеспечение предусмотренно
	 */
	public void setExist(boolean exist) {
		this.exist = exist;
	}

	/**
	 * @return the deposit
	 */
	public ArrayList<Deposit> getDeposit() {
		return deposit;
	}

	/**
	 * @return the garantee
	 */
	public ArrayList<Guarantee> getGuarantee() {
		return guarantee;
	}

	/**
	 * @return Индивидуальные условия гарантий
	 */
	public String getGuaranteeCondition() {
		return guaranteeCondition==null?"":guaranteeCondition;
	}

	/**
	 * @param guaranteeCondition Индивидуальные условия гарантий
	 */
	public void setGuaranteeCondition(String guaranteeCondition) {
		this.guaranteeCondition = guaranteeCondition;
	}

	/**
	 * @return Индивидуальные условия залоговых сделок
	 */
	public String getDCondition() {
		return dCondition==null?"":dCondition;
	}

	/**
	 * @param condition Индивидуальные условия залоговых сделок
	 */
	public void setDCondition(String condition) {
		dCondition = condition;
	}

	/**
	 * @return Дополнительные атрибуты залога
	 */
	public HashMap<String, String> getDepositKeyValue() {
		return depositKeyValue;
	}
	
	/**секция гарантии не заполнена*/
    public boolean emptyGuarantee(){
        return getGuarantee().isEmpty() && getGuaranteeCondition().isEmpty();
    }
    
    /**секция залоги не заполнена*/
    public boolean emptyDeposit(){
        return getDeposit().isEmpty() && getDCondition().isEmpty() && getDepositKeyValue().isEmpty();
    }
    
    /**секция поручители не заполнена*/
    public boolean emptyWarranty(){
        return getWarranty().isEmpty();
    }

    public String getWCondition() {
        return wCondition;
    }

    public void setWCondition(String condition) {
        wCondition = condition;
    }

	@Override
	public int hashCode() {
		int result = additionSupply != null ? additionSupply.hashCode() : 0;
		result = 31 * result + (exist ? 1 : 0);
		result = 31 * result + (warranty != null ? warranty.hashCode() : 0);
		result = 31 * result + (guaranteeCondition != null ? guaranteeCondition.hashCode() : 0);
		result = 31 * result + (dCondition != null ? dCondition.hashCode() : 0);
		result = 31 * result + (wCondition != null ? wCondition.hashCode() : 0);
		result = 31 * result + (cfact != null ? cfact.hashCode() : 0);
		return result;
	}
}
