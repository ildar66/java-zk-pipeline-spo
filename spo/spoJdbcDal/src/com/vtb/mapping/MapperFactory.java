package com.vtb.mapping;

import java.util.HashMap;
import java.util.Map;

import com.vtb.system.AppService;
import com.vtb.system.ClassUtility;
import com.vtb.system.TraceCapable;
import com.vtb.util.ApplProperties;

/**
 * Master mapper factory VTB.
 */
public abstract class MapperFactory {
	private static MapperFactory systemMapperFactory = null;
	private static MapperFactory reserveMapperFactory = null; 
	protected final Map<String, Mapper> mapperCache = new HashMap<String, Mapper>();

	/**
	 * MapperFactory constructor comment.
	 */
	public MapperFactory() {
		super();
	}

	/**
	 *  Select appropriate classname depending on installed
	 *  SystemMapperFactory.
	 */
	private String computeMapperClassName(Class target) {
		String packageName =
			ClassUtility.computePackageName(target, "domain", "mapping");
		// get specific package qualifier from installed Factory
		packageName = packageName + "." + getBackendQualifier();
		String className = ClassUtility.unqualifiedClassName(target) + "Mapper";
		return packageName + "." + className;
	}

	protected abstract String getBackendQualifier();


	/**
	 * Create and return a Mapper instance for the class
	 * This is accomplished by suffixing a class with "Mapper"
	 */
	public <T> Mapper<T> getMapper(Class<T> target) {
		String aMapperName = computeMapperClassName(target);
		
		// check registry cache for broker...
		Mapper<T> aMapper = mapperCache.get(aMapperName);
		if (aMapper != null)
			return aMapper;

		// create the mapper and save
		try {
			Class aClass = Class.forName(aMapperName);
			aMapper = (Mapper<T>) aClass.newInstance();
            mapperCache.put(aMapperName, aMapper);
        } catch (ClassNotFoundException e) {
            AppService.error("Не определен класс для Mapper '" + aMapperName + "'", e);
		} catch (InstantiationException e) {
            AppService.error("Ошибка при создании объекта класса '" + aMapperName + "'", e);
		} catch (IllegalAccessException e) {
            AppService.error("Ошибка при создании объекта класса '" + aMapperName + "'", e);
		}
		return aMapper;
	}

	private static Map<String, String> getMapperFactoryNameMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put(ApplProperties.JPA_MAPPER, "com.vtb.mapping.JpaMapperFactory");
		map.put(ApplProperties.EJB_MAPPER, "com.vtb.mapping.EjbMapperFactory");
		map.put(ApplProperties.JDBC_MAPPER, "com.vtb.mapping.JdbcMapperFactory");
		map.put(ApplProperties.MEMORY_MAPPER, "com.vtb.mapping.MemoryMapperFactory");
		return map;
	}

	public static MapperFactory getSystemMapperFactory() {
		if (systemMapperFactory == null) {
			String className =
				(String) getMapperFactoryNameMap().get(ApplProperties.getCurrentMapperName());
			Class aClass = null;
			try {
				aClass = Class.forName(className);
			} catch (ClassNotFoundException e) {
				AppService.log(TraceCapable.ERROR_LEVEL,"Mapper Factory, " + className + ", not defined...");
			}
			try {
				setSystemMapperFactory((MapperFactory) aClass.newInstance());
			} catch (InstantiationException e) {
				AppService.log(TraceCapable.ERROR_LEVEL,e.toString());
			} catch (IllegalAccessException e) {
				AppService.log(TraceCapable.ERROR_LEVEL,e.toString());
			}

		}
		return systemMapperFactory;

	}

	private static void setSystemMapperFactory(MapperFactory factory) {
		systemMapperFactory = factory;
	}

	/**
	 * @return
	 */
	public static MapperFactory getReserveMapperFactory() {
		if (reserveMapperFactory == null) {
			String className =
				(String) getMapperFactoryNameMap().get(ApplProperties.getReserveMapperName());
			Class aClass = null;
			try {
				aClass = Class.forName(className);
			} catch (ClassNotFoundException e) {
				AppService.log(TraceCapable.ERROR_LEVEL,"Reserve Mapper Factory, " + className + ", not defined...");
			}
			try {
				setReserveMapperFactory((MapperFactory) aClass.newInstance());
			} catch (InstantiationException e) {
				AppService.log(TraceCapable.ERROR_LEVEL,e.toString());
			} catch (IllegalAccessException e) {
				AppService.log(TraceCapable.ERROR_LEVEL,e.toString());
			}

		}
		return reserveMapperFactory;
	}

	/**
	 * @param factory
	 */
	private static void setReserveMapperFactory(MapperFactory factory) {
		reserveMapperFactory = factory;
	}

}