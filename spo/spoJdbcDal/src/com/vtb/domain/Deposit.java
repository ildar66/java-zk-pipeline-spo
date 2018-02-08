package com.vtb.domain;

import java.math.BigDecimal;

import ru.masterdm.compendium.domain.crm.Ensuring;
import ru.masterdm.compendium.domain.crm.Organization;
import ru.masterdm.compendium.domain.spo.Person;

/**
 * Залог.
 * @author Andrey Pavlenko
 *
 */
public class Deposit extends AbstractSupply{
	private static final long serialVersionUID = 1L;
	private String type; //Вид залога - выбор одного значения из следующих: Залог, Заклад, Ипотека, Передача векселя с залоговым индоссаментом
	private String zalogDescription;// Описание предмета залога
	private String oppDescription;// Описание залоговой сделки
	private BigDecimal zalogMarket;// Рыночная стоимость предмета залога
	private BigDecimal zalogTerminate;// Ликвидационная стоимость предмета залога
	private BigDecimal zalog;// Залоговая стоимость
	private BigDecimal discount;// Коэффициент залогового дисконтирования
	private Organization issuer;// Эмитент ценных бумаг
	private String orderDescription;// Порядок определения рыночной стоимости
	private Ensuring zalogObject;// Предмет залога
	private Double transRisk;  // коэффициент транзакционного риска 
	private String cond;//Условия страхования
	private BigDecimal weight;//Удельный вес вида залога
	private Double maxpart;//Максимально возможная доля необеспеченной части сублимита

	
	public Deposit() {
		super();
	}
	/**
	 * @return Вид залога - выбор одного значения из следующих: Залог, Заклад, Ипотека, Передача векселя с залоговым индоссаментом
	 */
	public String getType() {
		return type==null?"":type;
	}
	/**
	 * @param type Вид залога - выбор одного значения из следующих: Залог, Заклад, Ипотека, Передача векселя с залоговым индоссаментом
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return Описание предмета залога
	 */
	public String getZalogDescription() {
		return zalogDescription==null?"":zalogDescription;
	}
	/**
	 * @param zalogDescription Описание предмета залога
	 */
	public void setZalogDescription(String zalogDescription) {
		this.zalogDescription = zalogDescription;
	}
	/**
	 * @return Описание залоговой сделки
	 */
	public String getOppDescription() {
		return oppDescription==null?"":oppDescription;
	}
	/**
	 * @param oppDescription Описание залоговой сделки
	 */
	public void setOppDescription(String oppDescription) {
		this.oppDescription = oppDescription;
	}

	/**
	 * @return Рыночная стоимость предмета залога
	 */
	public BigDecimal getZalogMarket() {
		return zalogMarket;
	}
	/**
	 * @param zalogMarket Рыночная стоимость предмета залога
	 */
	public void setZalogMarket(BigDecimal zalogMarket) {
		this.zalogMarket = zalogMarket;
	}
	/**
	 * @return Ликвидационная стоимость предмета залога
	 */
	public BigDecimal getZalogTerminate() {
		return zalogTerminate;
	}
	/**
	 * @param zalogTerminate Ликвидационная стоимость предмета залога
	 */
	public void setZalogTerminate(BigDecimal zalogTerminate) {
		this.zalogTerminate = zalogTerminate;
	}
	/**
	 * @return Залоговая стоимость
	 */
	public BigDecimal getZalog() {
		return zalog;
	}
	/**
	 * @param zalog Залоговая стоимость
	 */
	public void setZalog(BigDecimal zalog) {
		this.zalog = zalog;
	}
	/**
	 * @return Коэффициент залогового дисконтирования
	 */
	public BigDecimal getDiscount() {
		return discount;
	}
	/**
	 * @param discount Коэффициент залогового дисконтирования
	 */
	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}
	/**
	 * @return Эмитент ценных бумаг
	 */
	public Organization getIssuer() {
		return issuer;
	}
	/**
	 * @param issuer Эмитент ценных бумаг
	 */
	public void setIssuer(Organization issuer) {
		this.issuer = issuer;
	}
	/**
	 * @return Порядок определения рыночной стоимости
	 */
	public String getOrderDescription() {
		return orderDescription==null?"":orderDescription;
	}
	/**
	 * @param orderDescription Порядок определения рыночной стоимости
	 */
	public void setOrderDescription(String orderDescription) {
		this.orderDescription = orderDescription;
	}
	/**
	 * @return Предмет залога
	 */
	public Ensuring getZalogObject() {
		return zalogObject;
	}
	/**
	 * @param zalogObject Предмет залога
	 */
	public void setZalogObject(Ensuring zalogObject) {
		this.zalogObject = zalogObject;
	}
	/**
	 * @return the transRisk
	 */
	public Double getTransRisk() {
		return transRisk;
	}
	/**
	 * @param transRisk the transRisk to set
	 */
	public void setTransRisk(Double transRisk) {
		this.transRisk = transRisk;
	}
	@Override
	public BigDecimal getRating_zalog(BigDecimal exchangeRate, BigDecimal mainSum) {
		return zalog;
	}
	@Override
	public String getSupplyType() {
		return "d";
	}
    public String getCond() {
        if(this.cond==null) return "";
        return cond;
    }
    public void setCond(String cond) {
        this.cond = cond;
    }
    public BigDecimal getWeight() {
    	return weight;
    }
    public BigDecimal getWeightBD() {
        return weight;
    }
    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }
    public Double getMaxpart() {
        return maxpart;
    }
    public void setMaxpart(Double maxpart) {
        this.maxpart = maxpart;
    }
	@Override
	public String getSupplyTypeName() {
		return "Залогодатель";
	}
}
