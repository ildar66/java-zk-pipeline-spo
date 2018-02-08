package com.vtb.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class Supply4Rating implements Serializable {
	private static final long serialVersionUID = -7049983638227827645L;
	private String typeCode;//Вид обеспечения (d=залог, w=поручительство или g=гарантия)
	private BigDecimal sum;//Сумма (сумма залога, учитываемая в рамках сделки или сумма поручительства или гарантии - соответственно)
	private Long depositorFinStatusCode;//Финансовое состояние (залогодателя, поручителя или гаранта)
	private Long liquidityLevelCode;//Категория обеспечения (уровень ликвидности) (из существующего справочника СРР. Можно передавать доменный объект)
	private Long supplyTypeCode;//Вид обеспечения (из существующего справочника СРР. Можно передавать доменный объект)
	public Supply4Rating() {
		super();
	}
	/**
     * описание предмета обеспечения.
     * Использую LinkedHashMap вместо enum, т.к. HPux некорректно работает с сериализацией enum
     * @author Andrey Pavlenko
     * @return код обеспечения, его описание
     */
    public static LinkedHashMap<String,String> getSupplyCodeDescription() {
        LinkedHashMap<String,String> codeList = new LinkedHashMap<String, String>();
        codeList.put("d", "залог");
        codeList.put("w", "поручительство");
        codeList.put("g", "гарантия");
        return codeList;
    }
	/**
	 * @return Вид обеспечения (d=залог, w=поручительство или g=гарантия)
	 */
	public String getTypeCode() {
		return typeCode;
	}
	/**
	 * @param typeCode Вид обеспечения (d=залог, w=поручительство или g=гарантия)
	 */
	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}
	/**
	 * @return Сумма (сумма залога, учитываемая в рамках сделки или сумма поручительства или гарантии - соответственно)
	 */
	public BigDecimal getSum() {
		return sum;
	}
	/**
	 * @param sum Сумма (сумма залога, учитываемая в рамках сделки или сумма поручительства или гарантии - соответственно)
	 */
	public void setSum(BigDecimal sum) {
		this.sum = sum;
	}
	/**
	 * @return Финансовое состояние (залогодателя, поручителя или гаранта)
	 */
	public Long getDepositorFinStatusCode() {
		return depositorFinStatusCode;
	}
	/**
	 * @param depositorFinStatus Финансовое состояние (залогодателя, поручителя или гаранта)
	 */
	public void setDepositorFinStatusCode(Long depositorFinStatus) {
		this.depositorFinStatusCode = depositorFinStatus;
	}
	/**
	 * @return Категория обеспечения (уровень ликвидности)
	 */
	public Long getLiquidityLevelCode() {
		return liquidityLevelCode;
	}
	/**
	 * @param liquidityLevel Категория обеспечения (уровень ликвидности)
	 */
	public void setLiquidityLevelCode(Long liquidityLevel) {
		this.liquidityLevelCode = liquidityLevel;
	}
	/**
	 * @return Вид обеспечения
	 */
	public Long getSupplyTypeCode() {
		return supplyTypeCode;
	}
	/**
	 * @param supplyType Вид обеспечения
	 */
	public void setSupplyTypeCode(Long supplyType) {
		this.supplyTypeCode = supplyType;
	}
	
}
