package com.vtb.domain;

/**
 * VtbObject "Договоры"
 * 
 * @author ChebanashAnton@masterdm.ru
 * 
 */
public class Contract extends VtbObject{
	private static final long serialVersionUID = 1L;
	
	private String contract; // договоры
	
	/**
	 * Конструктор
	 */
    public Contract() {
        super();
     }

	public Contract(String contract) {
		super();
		this.contract = contract;
	}
    
	public String getContract() {
		return contract;
	}

	public void setContract(String contract) {
		this.contract = contract;
	}
}
