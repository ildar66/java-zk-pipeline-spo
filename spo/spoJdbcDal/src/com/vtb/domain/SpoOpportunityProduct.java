package com.vtb.domain;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
/**
 * VtbObject "Информация о продукте кредита из системы CRM".
 * 
 * @author IShafigullin
 * 
 */
public class SpoOpportunityProduct extends VtbObject {
	private static final long serialVersionUID = 1L;
	private String limitid=null;        //В рамках лимита
	private String num="";              //отображаемый номер сделки
	private String id = null;           // id product(id opportunity)
	private BigDecimal quantity = null; // Основной объемный показатель сделки (сумма сделки) 
	private String unit = null;         // Валюта кредитной линии
	
	private BigDecimal quantityVydachi = null;  // Лимит выдачи – сумма
    private boolean LV = false;                 // Флаг использования лимита выдачи (только для кредитных линий)
    private BigDecimal quantityZad = null;      // Лимит задолженности для кредитных линий с лимитом задолженности или смешанных линий
    private boolean LZ = false;                 // Флаг использования лимита задолженности (только для кредитных линий)
	
	private String days = null;// Срок кредитной линии
	private String manager=null;//менеджер сделки
	private String productname=null;//
	private String COMNEISP=null;// за обязательство (процент)
	private String COMUPR=null;// за управление (процент)
	private String COMSCHET=null;// за ведение счета (процент)
	private String COMOPP=null;// прочая комиссия (абсл. в валюте сделки)
	private String COMBVS=null;// прочая комиссия (абсл. в базовой валюте системы)
	//Плановые сроки для кредитной сделки
	private Date activeBegin = null; // с
	private Date activeEnd = null; // до
	
	//логин пользователя
	private String userlogin=null;
	private String userName="";
	
	//Периодичность погашения основного долга
	private String POGAS="";
	private String CONDITIONS="";//условия закодированные
	private HashMap<String,String> conditionMap = new LinkedHashMap<String, String>();//условия
	private BigDecimal UNITCOURSE;//курсу пересчета сумм в рубли
	
	//условия досрочного погашения
    private String DOSROCH_POGAS;
    private String BANK_ACPT;
    private Double COM_DOSR_POGAS;
    //график погашения
    private ArrayList<CrmGraph> graphList = new ArrayList<CrmGraph>();
    //комиссии
    private ArrayList<CrmComiss> comissList = new ArrayList<CrmComiss>();
    private String spravparam;//целевое назначение (название, не ключ)
	/**
     * @return курсу пересчета сумм в рубли
     */
    public BigDecimal getUNITCOURSE() {
        return UNITCOURSE;
    }

    /**
     * @param unitcourse курсу пересчета сумм в рубли
     */
    public void setUNITCOURSE(BigDecimal unitcourse) {
        UNITCOURSE = unitcourse;
    }

    public String getUserlogin() {
		return userlogin;
	}

	public void setUserlogin(String userlogin) {
		this.userlogin = userlogin;
	}

	@Override
	public boolean equals(Object anObject) {
		if (anObject == null) {
			return false;
		}
		if (!(anObject instanceof SpoOpportunityProduct)) {
			return false;
		}
		SpoOpportunityProduct aSpoOpportunityProduct = (SpoOpportunityProduct) anObject;
		return aSpoOpportunityProduct.getId().equals(getId());
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("SpoOpportunityProduct: id=");
		sb.append(getId() + "(quantity=" + getQuantity() + ", unit=" + getUnit() + ", days=" + getDays() + ")");
		// sb.append('\n');
		// sb.append(" IsActive: ");
		// sb.append(getIsActive());

		return sb.toString();
	}
	
	/**
	 * мы используем для разделения десятичных знаков точку, а не запятую
	 * число не должно начинаться с точки. В этом случае впереди должен быть ведущий ноль 
	 * @param from - строка для преобразования
	 * @return правильную строку
	 */
	public String format_num(String from){
		from=from.replace(',', '.');
		if (from.substring(0, 1).equals("."))return "0"+from;
		else return from;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getDays() {
		return days;
	}

	public void setDays(String days) {
		this.days = days;
	}

	public SpoOpportunityProduct(String id) {
		super();
		this.id = id;
	}

	public String getManager() {
		return manager;
	}

	public void setManager(String manager) {
		this.manager = manager;
	}

	public String getProductname() {
		return (productname == null)?"":productname;
	}

	public void setProductname(String productname) {
		this.productname = productname;
	}

	public String getCOMBVS() {
		return (COMBVS == null || COMBVS.equalsIgnoreCase(""))?"0":format_num(COMBVS);
	}

	public void setCOMBVS(String combvs) {
		COMBVS = combvs;
	}

	public String getCOMNEISP() {
		return (COMNEISP == null || COMNEISP.equalsIgnoreCase(""))?"0":format_num(COMNEISP);
	}

	public void setCOMNEISP(String comneisp) {
		COMNEISP = comneisp;
	}

	public String getCOMOPP() {
		return (COMOPP == null || COMOPP.equalsIgnoreCase(""))?"0":format_num(COMOPP);
	}

	public void setCOMOPP(String comopp) {
		COMOPP = comopp;
	}

	public String getCOMSCHET() {
		return (COMSCHET == null || COMSCHET.equalsIgnoreCase(""))?"0":format_num(COMSCHET);
	}

	public void setCOMSCHET(String comschet) {
		COMSCHET = comschet;
	}

	public String getCOMUPR() {
		return (COMUPR == null || COMUPR.equalsIgnoreCase(""))?"0":format_num(COMUPR);
	}

	public void setCOMUPR(String comupr) {
		COMUPR = comupr;
	}

	public Date getActiveBegin() {
		return activeBegin;
	}

	public void setActiveBegin(Date activeBegin) {
		this.activeBegin = activeBegin;
	}

	public Date getActiveEnd() {
		return activeEnd;
	}

	public void setActiveEnd(Date activeEnd) {
		this.activeEnd = activeEnd;
	}

	/**
	 * @return the limitid
	 */
	public String getLimitid() {
		return limitid;
	}

	/**
	 * @param limitid the limitid to set
	 */
	public void setLimitid(String limitid) {
		this.limitid = limitid;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the num
	 */
	public String getNum() {
		return num;
	}

	/**
	 * @param num the num to set
	 */
	public void setNum(String num) {
		this.num = num;
	}

    /**
     * @return the pOGAS
     */
    public String getPOGAS() {
        return POGAS;
    }

    /**
     * @param pogas the pOGAS to set
     */
    public void setPOGAS(String pogas) {
        POGAS = pogas;
    }

    /**
     * @return условия
     */
    public String getCONDITIONS() {
        return CONDITIONS;
    }

    /**
     * @param conditions условия
     */
    public void setCONDITIONS(String conditions) {
        CONDITIONS = conditions;
    }

    /**
     * @return условия
     */
    public HashMap<String, String> getConditionMap() {
        return conditionMap;
    }

    public BigDecimal getQuantityVydachi() {
        return quantityVydachi;
    }

    public void setQuantityVydachi(BigDecimal quantityVydachi) {
        this.quantityVydachi = quantityVydachi;
    }

    public boolean isLV() {
        return LV;
    }

    public void setLV(boolean lv) {
        LV = lv;
    }

    public BigDecimal getQuantityZad() {
        return quantityZad;
    }

    public void setQuantityZad(BigDecimal quantityZad) {
        this.quantityZad = quantityZad;
    }

    public boolean isLZ() {
        return LZ;
    }

    public void setLZ(boolean lz) {
        LZ = lz;
    }

    /**
     * @return разрешено досрочное погашение 
     */
    public boolean isDOSROCH_POGAS() {
        if (DOSROCH_POGAS==null)return false;
        return DOSROCH_POGAS.equalsIgnoreCase("T");
    }

    /**
     * @param dosroch_pogas разрешено досрочное погашение
     */
    public void setDOSROCH_POGAS(String dosroch_pogas) {
        DOSROCH_POGAS = dosroch_pogas;
    }

    /**
     * @return по согласованию с банком
     */
    public boolean isBANK_ACPT() {
        if (BANK_ACPT==null) return false;
        return BANK_ACPT.equalsIgnoreCase("T");
    }

    /**
     * @param bank_acpt по согласованию с банком
     */
    public void setBANK_ACPT(String bank_acpt) {
        BANK_ACPT = bank_acpt;
    }

    /**
     * @return комиссия за досрочное погашение
     */
    public Double getCOM_DOSR_POGAS() {
        return COM_DOSR_POGAS;
    }

    /**
     * @param com_dosr_pogas комиссия за досрочное погашение
     */
    public void setCOM_DOSR_POGAS(Double com_dosr_pogas) {
        COM_DOSR_POGAS = com_dosr_pogas;
    }

    /**
     * @return комиссии
     */
    public ArrayList<CrmComiss> getComissList() {
        return comissList;
    }

    /**
     * @return целевое назначение (название, не ключ)
     */
    public String getSpravparam() {
        return spravparam == null ? "" : spravparam;
    }

    /**
     * @param spravparam целевое назначение (название, не ключ)
     */
    public void setSpravparam(String spravparam) {
        this.spravparam = spravparam;
    }

    /**
     * @return graphList
     */
    public ArrayList<CrmGraph> getGraphList() {
        return graphList;
    }
	
}
