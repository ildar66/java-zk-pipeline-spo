package ru.md.persistence;

import java.util.List;

import ru.md.domain.Currency;

/**
 * 
 * @author Andrey Pavlenko
 *
 */
public interface CurrencyMapper {
	
	List<Currency> getCurrencyList();
	
	/**
	 * Возвращает список валют.
	 * @return список валют
	 */
	List<String> getCurrencies();
	
	/**
	 * Возвращает {@link List список} {@link Currency валют} для {@link Long id} сделки.
	 * @param mdTaskId {@link Long id} сделки
	 * @return {@link List список} {@link Currency валют} для {@link Long id} сделки.
	 */
	List<Currency> getTaskCurrencies(Long mdTaskId);
}
