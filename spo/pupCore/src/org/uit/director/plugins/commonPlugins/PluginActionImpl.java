package org.uit.director.plugins.commonPlugins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.uit.director.contexts.WPC;
import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.db.dbobjects.Cnst;
import org.uit.director.decider.NextStagesTransition;
import org.uit.director.decider.NextStagesInfo.Statuses;
import org.uit.director.plugins.PluginInterface;

public class PluginActionImpl {

	private static Logger logger = Logger.getLogger(PluginActionImpl.class.getName());
	
	/**
	 * Вызовем операцию плагина для ОДНОЙ операции
	 */
	public static String executePluginAction(WorkflowSessionContext wsc,
			long idStage, String classType) {
		String res = "error";
		try {
			List attrs = new ArrayList();
			String actions = (String) WPC.getInstance().getData(
					Cnst.TBLS.stages, idStage, classType);
			if (!actions.equals("")) {

				parsActionString(actions, attrs);
				for (int i = 0; i < attrs.size(); i++) {
					Map attr = (Map) attrs.get(i);
					String actionClass = (String) attr.get("class");					
					logger.info("plugin class: " + actionClass);					
					List params = (List) attr.get("params");
					Class classPlugin;
				    classPlugin = Class.forName(actionClass);
					PluginInterface pluginInterface;
					pluginInterface = (PluginInterface) classPlugin.newInstance();
					pluginInterface.init(wsc, params);
					res = pluginInterface.execute();
				}
			}
			res = "ok";
		} catch (ClassNotFoundException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			e.printStackTrace();	
		} catch (IllegalAccessException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			e.printStackTrace();
		} catch (InstantiationException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * Выполнить действия перед входом на этапы, определенные в структуре stNext 
	 * @param wsc
	 * @param stNextList -- структура со списком этапов, на которые переходим
	 * @return res
	 */
	public static String executePluginAction(WorkflowSessionContext wsc,
			List<NextStagesTransition> stNextList) {
		String res = "error";
		int l = stNextList.size();
		for (int i = 0; i < l; i++) {
			NextStagesTransition stNext =  stNextList.get(i);
			Statuses st = stNext.getStatus();
			if (st == Statuses.SEND) {
				Long idNextStage = stNext.getIdStage();
				if (idNextStage != null) {
					res = executePluginAction(wsc, idNextStage.longValue(), Cnst.TStages.classEntry);
				}
			}
		}
		return res;
	}

	public static String executePluginAction(WorkflowSessionContext wsc,
			Long idStage, String classType, Object obj) {
		String res = "error";

		try {
			List attrs = new ArrayList();
			String actions = (String) WPC.getInstance().getData(
					Cnst.TBLS.stages, idStage, classType);
			if (!actions.equals("")) {

				parsActionString(actions, attrs);

				for (int i = 0; i < attrs.size(); i++) {
					Map attr = (Map) attrs.get(i);
					String actionClass = (String) attr.get("class");
					List params = (List) attr.get("params");
					params.add(obj);

					Class classPlugin;

					classPlugin = Class.forName(actionClass);

					PluginInterface pluginInterface;
					pluginInterface = (PluginInterface) classPlugin
							.newInstance();
					pluginInterface.init(wsc, params);
					res = pluginInterface.execute();
				}
			}

			res = "ok";
		}

		catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}

		return res;
	}

	private static void parsActionString(String actions, List attrs) {

		StringTokenizer actionToken = new StringTokenizer(actions, "$");
		while (actionToken.hasMoreTokens()) {

			Map attr = new HashMap();
			String token = actionToken.nextToken().trim();

			int idx = token.indexOf('?');
			if (idx == -1) {
				idx = token.length();
			}
			String aClass = token.substring(0, idx);
			attr.put("class", aClass);

			if (idx != token.length()) {

				StringTokenizer strTok = new StringTokenizer(token.substring(
						idx + 1, token.length()), "&");
				List params = new ArrayList();

				while (strTok.hasMoreTokens()) {
					String param = strTok.nextToken().trim();
					params.add(param);
				}

				attr.put("params", params);
			}

			attrs.add(attr);
		}

	}

}
