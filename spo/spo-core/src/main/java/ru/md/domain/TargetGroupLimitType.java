package ru.md.domain;

import java.io.Serializable;

/**
 * Целевое назначение, входящее в лимит группы целевых назначений
 * 
 * @author akirilchev@masterdm.ru
 */
public class TargetGroupLimitType implements Serializable {

	/**
	 * @serial
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Конструктор
	 */
	public TargetGroupLimitType() { }

	/**
	 * Конструктор
	 *
	 * @param id {@link Long первичный ключ}
	 * @param idTarget {@link Long id} {@link OtherGoal цели кредитования} сделки
	 * @param targetTypeName наименование целевого назначения
	 */
	public TargetGroupLimitType(Long id, Long idTarget, String targetTypeName) {
		this.id = id;
		this.idTarget = idTarget;
		this.targetTypeName = targetTypeName;
	}

	private Long id;
	private Long idTarget;
	private String targetTypeName;
	
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
	 * Возвращает {@link Long id} {@link OtherGoal цели кредитования} сделки
	 *
	 * @return {@link Long id} {@link OtherGoal цели кредитования} сделки
	 */
	public Long getIdTarget() {
		return idTarget;
	}

	/**
	 * Устанавливает {@link Long id} {@link OtherGoal цели кредитования} сделки
	 *
	 * @param idTarget {@link Long id} {@link OtherGoal цели кредитования} сделки
	 */
	public void setIdTarget(Long idTarget) {
		this.idTarget = idTarget;
	}

	/**
	 * Возвращает наименование целевого назначения
	 *
	 * @return наименование целевого назначения
	 */
	public String getTargetTypeName() {
		return targetTypeName;
	}

	/**
	 * Устанавливает наименование целевого назначения
	 *
	 * @param targetTypeName наименование целевого назначения
	 */
	public void setTargetTypeName(String targetTypeName) {
		this.targetTypeName = targetTypeName;
	}

	@Override
	public int hashCode() {
		int result = idTarget != null ? idTarget.hashCode() : 0;
		result = 31 * result + (targetTypeName != null ? targetTypeName.hashCode() : 0);
		return result;
	}
}