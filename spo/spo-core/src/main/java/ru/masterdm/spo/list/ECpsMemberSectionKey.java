package ru.masterdm.spo.list;

import java.util.ArrayList;
import java.util.List;

/**
 * Список секций участников ССКО
 *
 * @author akirilchev@masterdm.ru
 */
public enum ECpsMemberSectionKey {

    /**
     * Проектная команда.
     */
    PROJECT_TEAM,

    /**
     * Мидл-офис.
     */
    MIDDLE_OFFICE,

    /**
     * Подразделения по работе с залогами.
     */
    DEPOSIT_DEP,

    /**
     * Роли вне секции.
     */
    NOT_MEMBER;
	
	/**
	 * Возвращает {@link List список} ключей секций, отличных от проектной команды и ролей вне секций
	 *
	 * @return {@link List список} ключей секций, отличных от проектной команды и ролей вне секций
	 */
	public static List<String> getNotProjectTeamSections() {
		List<String> results = new ArrayList<String>();
		results.add(MIDDLE_OFFICE.name());
		results.add(DEPOSIT_DEP.name());
		return results;
	}
}