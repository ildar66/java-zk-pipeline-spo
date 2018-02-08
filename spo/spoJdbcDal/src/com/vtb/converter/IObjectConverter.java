package com.vtb.converter;

import java.util.ArrayList;
import java.util.List;

/**
 * Интерфейс конвертора из исходного объекта в целевой 
 * 
 * @author sergey
 * @email svaliev@masterdm.ru
 *
 * @param <V> тип исходного объекта
 * @param <E> тип целевого объекта
 */
public interface IObjectConverter<V, E> {
	
	/**
	 * Выполнение конвертации из исходного объекта в целевой.
	 * Если targetObject не <code><b>null</b></code>, тогда из sourceObject будут скопированы значения полей не равные <code><b>null</b></code>
	 * 
	 * @param sourceObject исходный объект
	 * @param targetObject целевой объект. Если <code><b>null</b></code> тогда будет создан новый экземпляр
	 * @param targetClass {@link Class класс} целевого объекта
	 * @return целевой объект
	 * @throws Exception 
	 */
	E directConvert(V sourceObject, E targetObject, Class<? extends E> targetClass) throws Exception;
	
	/**
	 * Выполнение конвертации из {@link List списка} исходных объектов в {@link ArrayList список} целевых
	 * 
	 * @param sourceObjectList {@link List список} исходных объектов
	 * @param targetClass {@link Class класс} целевого объекта
	 * @return {@link ArrayList список} целевых объектов
	 * @throws Exception 
	 */
	ArrayList<E> directConvert(List<V> sourceObjectList, Class<? extends E> targetClass) throws Exception;
	
	/**
	 * Выполнение обратной конвертации из целевого объекта в исходный.
	 * Если sourceObject не <code><b>null</b></code>, тогда из targetObject будут скопированы значения полей не равные <code><b>null</b></code>
	 * 
	 * @param targetObject целевой объект
	 * @param sourceObject исходный объект. Если <code><b>null</b></code> тогда будет создан новый экземпляр
	 * @param sourceClass {@link Class класс} исходного объекта
	 * @return исходный объекта
	 * @throws Exception 
	 */
	V inverseConvert(E targetObject, V sourceObject, Class<? extends V> sourceClass) throws Exception;
	
	/**
	 * Выполнение обратной конвертации из {@link List списка} целевых объектов в {@link ArrayList список} исходных
	 * 
	 * @param targetObjectList {@link List список} целевых объектов
	 * @param sourceClass {@link Class класс} исходного объекта
	 * @return {@link ArrayList список} исходных объектов
	 * @throws Exception 
	 */
	ArrayList<V> inverseConvert(List<E> targetObjectList, Class<? extends V> sourceClass) throws Exception;
}
