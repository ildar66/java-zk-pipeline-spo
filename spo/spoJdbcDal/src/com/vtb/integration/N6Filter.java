package com.vtb.integration;

/**
 * Фильтр для заявок на Н6
 *
 * @author svaliev@masterdm.ru
 */
public class N6Filter {

	private Long mdTaskId;

	/**
	 * Возвращает {@link Long идентификатор} сделки СПО
	 *
	 * @return {@link Long идентификатор} сделки СПО
	 */
	public Long getMdTaskId() {
		return mdTaskId;
	}

	/**
	 * Устанавливает {@link Long идентификатор} сделки СПО
	 *
	 * @param mdTaskId {@link Long идентификатор} сделки СПО
	 */
	public void setMdTaskId(Long mdTaskId) {
		this.mdTaskId = mdTaskId;
	}
}