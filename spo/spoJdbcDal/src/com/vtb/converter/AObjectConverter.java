package com.vtb.converter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


import com.vtb.util.AFactory;

/**
 * Реализация по умолчанию конвертора из исходного объекта в целевой. 
 * Конвертация производится путем копирования значений свойств из исходного объекта в целевой,
 * при этом типы и наименования свойств целевого объекта должны совпадать с типами и наименованиями исходного объекта
 * 
 * @author sergey
 * @email svaliev@masterdm.ru
 *
 * @param <V> тип исходного объекта
 * @param <E> тип целевого объекта
 */
public abstract class AObjectConverter<V, E> implements IObjectConverter<V, E> {

	protected final Logger LOGGER = Logger.getLogger(this.getClass().getName());
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public E directConvert(V sourceObject, E targetObject, Class<? extends E> targetClass) throws Exception {
		try {
			return (E) this.convert(sourceObject, targetObject, (Class<Object>) targetClass);
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<E> directConvert(List<V> sourceObjectList, Class<? extends E> targetClass) throws Exception {
		ArrayList<E> targetObjectList = null;
		try {
			if (sourceObjectList == null) {
				throw new Exception("sourceObjectList is null");
			}
			
			targetObjectList = new ArrayList<E>();
			
			for (V sourceObject : sourceObjectList) {
				targetObjectList.add(this.directConvert(sourceObject, null, targetClass));
			}
			
			LOGGER.info("sourceObjectList to targetObjectList converted");
			
			return targetObjectList;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public V inverseConvert(E targetObject, V sourceObject, Class<? extends V> sourceClass) throws Exception {
		try {
			return (V) this.convert(targetObject, sourceObject, (Class<Object>) sourceClass);
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<V> inverseConvert(List<E> targetObjectList, Class<? extends V> sourceClass) throws Exception {
		ArrayList<V> sourceObjectList = null;
		try {
			if (targetObjectList == null) {
				throw new Exception("targetObjectList is null");
			}
			
			sourceObjectList = new ArrayList<V>();
			
			for (E targetObject : targetObjectList) {
				sourceObjectList.add(this.inverseConvert(targetObject, null, sourceClass));
			}
			
			LOGGER.info("targetObjectList to sourceObjectList converted");
			
			return sourceObjectList;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * Конвертация путем копирования значений свойств из исходного объекта в целевой,
	 * при этом типы и наименования свойств целевого объекта должны совпадать с типами и наименованиями исходного объекта
	 * 
	 * @param sourceObject исходный объект
	 * @param targetObject целевой объект. Если <code><b>null</b></code> тогда будет создан новый экземпляр
	 * @param targetClass {@link Class класс} целевого объекта
	 * @return целевой объект
	 * @throws Exception
	 */
	private Object convert(Object sourceObject, Object targetObject, Class<Object> targetClass) throws Exception {		
		try {
			boolean copyNulls = true;
			
			if (sourceObject == null) {
				throw new Exception("sourceObject is null");
			}
			
			if (targetObject == null) {
				targetObject = AFactory.newInstance(targetClass);
			} else
				copyNulls = false;
			
			Field[] sourceObjectFields = sourceObject.getClass().getDeclaredFields();
			Field[] targetObjectFields = targetObject.getClass().getDeclaredFields();
			
			Class<?> sourceObjectFieldType;
			String sourceObjectFieldName;
			
			for (Field sourceObjectField : sourceObjectFields) {
				sourceObjectFieldType = sourceObjectField.getType();
				sourceObjectFieldName = sourceObjectField.getName();
				
				for (Field targetObjectField : targetObjectFields) {
					if (sourceObjectFieldName.equals(targetObjectField.getName()) && sourceObjectFieldType.equals(targetObjectField.getType())) {
						LOGGER.info("found identical field \"" + sourceObjectFieldName + "\" with type \"" + sourceObjectFieldType.getName() + "\"");
						
						if (Modifier.isFinal(targetObjectField.getModifiers())) {
							LOGGER.warning("targetObject field \"" + targetObjectField.getName() + "\" is final");
							break;
						}
						
						targetObjectField.setAccessible(true);
						sourceObjectField.setAccessible(true);
						
						if (copyNulls)
							targetObjectField.set(targetObject, sourceObjectField.get(sourceObject));
						else {
							if (sourceObjectField.get(sourceObject) != null)
								targetObjectField.set(targetObject, sourceObjectField.get(sourceObject));
						}
						
						LOGGER.info("copied value to targetObject from field \"" + sourceObjectFieldName + "\" with type \"" + sourceObjectFieldType.getName() + "\"");
						
						break;
					}
				}
			}
			
			LOGGER.info("sourceObject to targetObject converted");
			
			return targetObject;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * Конвертация {@link List списка} исходных объектов в {@link List список} целевых объектов путем копирования значений свойств из исходного объекта в целевой,
	 * при этом типы и наименования свойств целевого объекта должны совпадать с типами и наименованиями исходного объекта
	 * 
	 * @param sourceObjectList {@link List список} исходных объектов
	 * @param targetClass {@link Class класс} целевого объекта
	 * @return {@link List список} целевых объектов
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private List<Object> convert(List<Object> sourceObjectList, Class<Object> targetClass) throws Exception {
		List<Object> targetObjectList = null;
		try {
			if (sourceObjectList == null) {
				throw new Exception("sourceObjectList is null");
			}
			
			targetObjectList = new ArrayList<Object>();
			
			for (Object sourceObject : sourceObjectList) {
				targetObjectList.add(this.convert(sourceObject, null, targetClass));
			}
			
			LOGGER.info("sourceObjectList to targetObjectList converted");
			
			return targetObjectList;
		} catch (Exception e) {
			throw e;
		}
	}
}
