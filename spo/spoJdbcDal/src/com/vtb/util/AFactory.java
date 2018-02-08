package com.vtb.util;

import java.lang.reflect.Constructor;
import java.util.logging.Logger;

/**
 * Р¤Р°Р±СЂРёРєР° СЃРѕР·РґР°РЅРёСЏ СЌРєР·РµРјРїР»СЏСЂРѕРІ РїРѕ РєР»Р°СЃСЃСѓ РѕР±СЉРµРєС‚Р°
 * 
 * @author alexey
 * @email akirilchev@masterdm.ru
 */
public abstract class AFactory {
	
	private static final Logger LOGGER = Logger.getLogger(AFactory.class.getName());
	
	/**
	 * Р¤Р°Р±СЂРёС‡РЅРёС‹Р№ РјРµС‚РѕРґ СЃРѕР·РґР°РЅРёСЏ СЌРєР·РµРјРїР»СЏСЂРѕРІ РїРѕ РєР»Р°СЃСЃСѓ РѕР±СЉРµРєС‚Р°
	 * 
	 * @param <T> С‚РёРї РѕР±СЉРµРєС‚Р°
	 * @param objectClass {@link Class РєР»Р°СЃСЃ} РѕР±СЉРµРєС‚Р°
	 * @return СЌРєР·РµРјРїР»СЏСЂ, СЃРѕР·РґР°РЅРЅС‹Р№ РїРѕ РєР»Р°СЃСЃСѓ РѕР±СЉРµРєС‚Р°
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<T> objectClass) throws Exception {
		try {
			LOGGER.info("create new instance of " + objectClass.getName());
			
			T instance = null;
			
			try {
				instance = objectClass.newInstance();
			} catch (Exception e) {
				LOGGER.warning(e.getMessage());

				Constructor<T>[] constrList = (Constructor<T>[]) objectClass.getDeclaredConstructors();
				for (Constructor<T> constr : constrList) {
					if (constr.getParameterTypes().length == 0) {
						LOGGER.info("find default constructor");
						
						constr.setAccessible(true);
						instance = constr.newInstance();
					}
				}
			}
			
			return instance;
		} catch (Exception e) {
			throw e;
		}
	}
}
