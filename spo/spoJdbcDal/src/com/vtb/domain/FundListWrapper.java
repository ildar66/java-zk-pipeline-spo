package com.vtb.domain;

import java.util.ArrayList;

/**
 * Интеграционный объект содержащий {@link ArrayList список} {@link TaskFund заявок на фондирование}
 *
 * @author svaliev@masterdm.ru
 */
public class FundListWrapper {

	private ArrayList<TaskFund> funds;

	/**
	 * Возвращает {@link ArrayList список} {@link TaskFund заявок на фондирование}
	 *
	 * @return {@link ArrayList список} {@link TaskFund заявок на фондирование}
	 */
	public ArrayList<TaskFund> getFunds() {
		return funds;
	}

	/**
	 * Устанавливает {@link ArrayList список} {@link TaskFund заявок на фондирование}
	 *
	 * @param funds {@link ArrayList список} {@link TaskFund заявок на фондирование}
	 */
	public void setFunds(ArrayList<TaskFund> funds) {
		this.funds = funds;
	}
}