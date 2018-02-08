package ru.md.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Лимит группы целевых назначений
 * 
 * @author akirilchev@masterdm.ru
 */
public class TargetGroupLimit implements Serializable {

	/**
	 * @serial
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Конструктор
	 */
	public TargetGroupLimit() {
		super();
	}

	/**
	 * Конструктор
	 *
	 * @param id {@link Long первичный ключ}
	 * @param amount {@link BigDecimal сумма}
	 * @param amountCurrency валюта
	 * @param note комментарии
	 */
	public TargetGroupLimit(Long id, BigDecimal amount, String amountCurrency, String note) {
		this.id = id;
		this.amount = amount;
		this.amountCurrency = amountCurrency;
		this.note = note;
	}

	private Long id;
	private BigDecimal amount;
	private String amountCurrency;
	private String note;
	private List<TargetGroupLimitType> targetGroupLimitTypes;

	/**
	 * Возвращает {@link Long первичный ключ}
	 * 
	 * @return {@link Long первичный ключ}
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * Устанавливает {@link Long первичный ключ}
	 * 
	 * @param id {@link Long первичный ключ}
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Возвращает {@link BigDecimal сумму}
	 *
	 * @return {@link BigDecimal сумма}
	 */
	public BigDecimal getAmount() {
		return amount;
	}

	/**
	 * Устанавливает {@link BigDecimal сумму}
	 *
	 * @param amount {@link BigDecimal сумма}
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	/**
	 * Возвращает валюту
	 *
	 * @return валюта
	 */
	public String getAmountCurrency() {
		return amountCurrency;
	}

	/**
	 * Устанавливает валюту
	 *
	 * @param amountCurrency валюта
	 */
	public void setAmountCurrency(String amountCurrency) {
		this.amountCurrency = amountCurrency;
	}

	/**
	 * Возвращает комментарии
	 *
	 * @return комментарии
	 */
	public String getNote() {
		return note;
	}

	/**
	 * Устанавливает комментарии
	 *
	 * @param note комментарии
	 */
	public void setNote(String note) {
		this.note = note;
	}

	/**
	 * Возвращает {@link List список} {link TargetGroupLimitType целевых назначений}, входящее в лимит группы целевых назначений
	 *
	 * @return {@link List список} {link TargetGroupLimitType целевых назначений}, входящее в лимит группы целевых назначений
	 */
	public List<TargetGroupLimitType> getTargetGroupLimitTypes() {
		return targetGroupLimitTypes;
	}

	/**
	 * Устанавливает {@link List список} {link TargetGroupLimitType целевых назначений}, входящее в лимит группы целевых назначений
	 *
	 * @param targetGroupLimitTypes {@link List список} {link TargetGroupLimitType целевых назначений}, входящее в лимит группы целевых назначений
	 */
	public void setTargetGroupLimitTypes(List<TargetGroupLimitType> targetGroupLimitTypes) {
		this.targetGroupLimitTypes = targetGroupLimitTypes;
	}

	@Override
	public int hashCode() {
		int result = amount != null ? amount.hashCode() : 0;
		result = 31 * result + (amountCurrency != null ? amountCurrency.hashCode() : 0);
		result = 31 * result + (note != null ? note.hashCode() : 0);
		result = 31 * result + (targetGroupLimitTypes != null ? targetGroupLimitTypes.hashCode() : 0);
		return result;
	}
}