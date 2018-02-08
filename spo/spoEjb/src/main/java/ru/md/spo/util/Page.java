package ru.md.spo.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Страница.
 */
@SuppressWarnings("rawtypes")
public class Page<E extends Object> implements Serializable {

	/**
	 * @serial
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public static final Page EMPTY_PAGE = new Page(new ArrayList(), 0, false);

	private List<E> objects;

	private int start;

	boolean hasNext;
	
	private int totalCount; 

	/**
	 * Конструктор Страницы.
	 * 
	 * @param list {@link List список} {@link VtbObject доменных объектов}
	 * @param start 
	 * @param hasNext {@link Boolean признак}, есть ли следующая {@link Page страница}
	 */
	public Page(List<E> list, int start, boolean hasNext) {
		this.objects = list;
		this.start = start;
		this.hasNext = hasNext;
	}

	/** 
	 * Возращает {@link List список} {@link VtbObject доменных объектов} {@link Page страницы}
	 * 
	 * @return List {@link List список} {@link VtbObject доменных объектов} {@link Page страницы}
	 */
	public List<E> getList() {
		return objects;
	}

	/**
	 * Возвращает {@link Boolean признак}, есть ли следующая {@link Page страница}
	 * 
	 * @return boolean {@link Boolean признак}, есть ли следующая {@link Page страница}
	 */
	public boolean isNextPageAvailable() {
		return hasNext;
	}

	/**
	 * Устанавливает {@link Boolean признак}, есть ли следующая {@link Page страница}
	 * 
	 * @return boolean {@link Boolean признак}, есть ли следующая {@link Page страница}
	 */
	public boolean isPreviousPageAvailable() {
		return start > 0;
	}

	/**
	 * Номер стартовой записи следующей страницы.
	 * @return int
	 */
	public int getStartOfNextPage() {
		return start + objects.size();
	}

	/**
	 * Номер стартовой записи предыдущей страницы.
	 * @return int
	 */
	public int getStartOfPreviousPage() {
		return Math.max(start - objects.size(), 0);
	}

	/**
	 * Размер страницы.
	 * @return int
	 */
	public int getSize() {
		return objects.size();
	}

	/**
	 * Номер стартовой записи.
	 * @return int
	 */
	public int getStart() {
		return start; 
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
}
