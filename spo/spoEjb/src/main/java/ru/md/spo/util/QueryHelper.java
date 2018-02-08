package ru.md.spo.util;

import javax.persistence.Query;

/**
 * Хелпер-класс для составления JPA-запросов
 * 
 * @author alexey
 * @mail akirilchev@masterdm.ru
 */
public class QueryHelper {
	
	/**
	 * Добавляет строковое условие секции where sql-запроса, если значение не равно null или пустой строке. При этом если условие добавляем не в первый раз, то
	 * перед ним добавляется " and ".
	 * 
	 * @param fieldToCheckEmpty {@link Object объект-значение} если оно null или пустая строка, то не добавляем строку к составляемой строке
	 * @param sb {@link StringBuilder составляемая строка} с sql-запросом
	 * @param appendingStr добавляемая строка (например, "c.name = :name")
	 */
	public static void appendWhereSectionIfNotEmpty(Object fieldToCheckEmpty, StringBuilder sb, String appendingStr) {
		if (isNotEmpty(fieldToCheckEmpty)) {
			if (sb.toString().equals(""))
				sb.append(" WHERE " + appendingStr);
			else
				sb.append(" AND " + appendingStr);
		}
	}

	/**
	 * Возвращает результат проверки равно ли значение объекта null или пустой строке
	 * 
	 * @param fieldToCheckEmpty {@link Object объект-значение} для проверки на null или пустую строку
	 * @return {@link boolean результат проверки равно ли значение объекта null или пустой строке}
	 */
	public static boolean isNotEmpty(Object fieldToCheckEmpty) {
		boolean isNotEmptyResult = false;
		try {
			if (fieldToCheckEmpty instanceof String)
				isNotEmptyResult = (fieldToCheckEmpty != null && !((String) fieldToCheckEmpty).trim().equals(""));
			else
				isNotEmptyResult = (fieldToCheckEmpty != null && !fieldToCheckEmpty.toString().trim().equals(""));
		} catch(Exception e) {
			isNotEmptyResult = false;
		}
		return isNotEmptyResult;
	}

	/**
	 * Устанавливает значение для параметра запроса, если это значение не пустое. Для строк используется LIKE и нижний регистр.
	 * 
	 * @param query {@link Query запрос}
	 * @param parameter название параметра
	 * @param value {@link Object значение параметра}
	 */
	public static void setParameterIfNotEmpty(Query query, String parameter, Object value) {
		setParameterIfNotEmpty(query, parameter, value, true, true);
	}
	
	/**
	 * Устанавливает значение для параметра запроса, если это значение не пустое. Для строк используется нижний регистр.
	 * @param query {@link Query запрос}
	 * @param parameter название параметра
	 * @param value {@link Object значение параметра}
	 */
	public static void setParameterEqualExpressionIfNotEmpty(Query query, String parameter, Object value) {
		setParameterIfNotEmpty(query, parameter, value, false, true);
	}	
	
	/**
	 * Устанавливает значение для параметра запроса, если это значение не пустое. Для строк используется LIKE и нижний регистр.
	 * 
	 * @param query {@link Query запрос}
	 * @param parameter название параметра
	 * @param value {@link Object значение параметра}
	 * @param isLikeExpression {@link Boolean признак, что выражение является LIKE-выражением}
	 * @param isLowerCaseExpression {@link Boolean признак, что необходимо привести к нижнему регистру}
	 */
	public static void setParameterIfNotEmpty(Query query, String parameter, Object value, Boolean isLikeExpression, Boolean isLowerCaseExpression) {
		if (isNotEmpty(value)) {
			if (value instanceof String) {
				String stringValue = (String) value;
				if (isLikeExpression)
				    query.setParameter(parameter, "%" + stringValue.toLowerCase().trim() + "%");
				else
					query.setParameter(parameter, stringValue.toLowerCase().trim());
			}
			else 
				query.setParameter(parameter, value);
		}
	}
	
	
	   /**
     * Устанавливает значение для параметра запроса, если это значение не пустое. 
     * Ни регистры, ни like, ни прочая неожиданная мутотень, портящая запрос, не используются
     * @param query {@link Query запрос}
     * @param parameter название параметра
     * @param value {@link Object значение параметра}
     */
    public static void setParameterSimple(Query query, String parameter, Object value) {
        if (isNotEmpty(value))
            query.setParameter(parameter, value);
    }   
}
