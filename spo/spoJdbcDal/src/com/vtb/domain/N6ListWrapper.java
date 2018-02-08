package com.vtb.domain;

import java.util.ArrayList;

/**
 * Интеграционный объект содержащий {@link ArrayList список} {@link N6 заявок на Н6}
 *
 * @author svaliev@masterdm.ru
 */
public class N6ListWrapper {

	private ArrayList<N6> n6s;

	/**
	 * Возвращает {@link ArrayList список} {@link N6 заявок на Н6}
	 *
	 * @return {@link ArrayList список} {@link N6 заявок на Н6}
	 */
	public ArrayList<N6> getN6s() {
		return n6s;
	}

	/**
	 * Устанавливает {@link ArrayList список} {@link N6 заявок на Н6}
	 *
	 * @param n6s {@link ArrayList список} {@link N6 заявок на Н6}
	 */
	public void setN6s(ArrayList<N6> n6s) {
		this.n6s = n6s;
	}
}