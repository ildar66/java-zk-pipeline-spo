package org.uit.director.decider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uit.director.db.dbobjects.MyOracleBlob;

import com.vtb.domain.TaskHeader;

import ru.md.spo.dbobjects.TaskJPA;
import ru.md.spo.ejb.DictionaryFacadeLocal;
import ru.md.spo.ejb.PupFacadeLocal;
import ru.md.spo.ejb.TaskFacadeLocal;

/**
 * Created by IntelliJ IDEA. User: PD190390 Date: 01.03.2006 Time: 9:15:07 To
 * change this template use File | Settings | File Templates.
 */
public class NextStagesInfo {
	private static final Logger LOGGER = LoggerFactory.getLogger(NextStagesInfo.class);

	/**
	 * статус отправки на следующий этап
	 */
	public enum Statuses {
		SEND, SEND_SUB_PROCESS, NOT_SEND, COMPLETE
	};

	/*
	 * public class Statuses { public static final int SEND = 0; // отправка
	 * 
	 * public static final int NOT_SEND = 1; // задание не может быть //
	 * отправлено
	 * 
	 * public static final int COMPLETE = 2; // завершение процесса }
	 */

	private List<NextStagesTransition> stages;
	private Long idTask;
	private boolean isExpired;
	private String sign;

	/**
	 * результат: 0 - отправка задания (на следующие этапы, либо завершение
	 * процесса) 1 - отмена отправки (не корректные условия либо ожидание
	 * завершения с других этапов)
	 */
	private int result;

	public boolean isExpired() {
		return isExpired;
	}

	public void setExpired(boolean expired) {
		isExpired = expired;
	}

	public Long getIdTask() {
		return idTask;
	}

	public void setIdTask(Long idTask) {
		this.idTask = idTask;
	}

	public void addStageInfo(NextStagesTransition info) {
		stages.add(info);
	}

	public void addStageInfo(List<NextStagesTransition> stageses) {
		stages.addAll(stageses);
	}

	public List<Long> getStageIds() {
		ArrayList<Long> res= new ArrayList<Long>();
		for(NextStagesTransition stage : stages) res.add(stage.getIdStage());
		return res;
	}
	public List<NextStagesTransition> getStages() {
		return stages;
	}

	public String getMessage() {
		String mes = "";
		for (NextStagesTransition nst : stages) {
			LOGGER.info("NextStagesTransition.idStage: " + nst.getIdStage());
		    if (!nst.isAutoStage && !nst.getMessage().isEmpty()) {
				mes = nst.getMessage();
			}
		}
		return "<div class=\"tabledata\"><table><caption>Задание № "+idTask+"</caption><tr><td>"+mes+
		    "</td></tr></table></div>";
	}

	
	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public NextStagesInfo() {
		stages = new ArrayList<NextStagesTransition>();
	}

	public String getMessageStagesList() {
		String res = "";
		for (NextStagesTransition nst : stages) {
			String nameStage = nst.getNameStage();
			res += "<li> <strong>" + nameStage + ";</strong> ";
			if (nst.isLate)  res += "<small>(поздняя отправка)</small>";
			else             res += "<small>(мгновенная отправка)</small>";
			res += "</li>";
		}
		res += "</strong>";
		return res;
	}

	public ArrayList getParamsForLoad() {
		ArrayList params = new ArrayList<Object[]>();
		try {
			for (NextStagesTransition nst : stages) {
				if (!nst.isAutoStage) {

					Object[] param = new Object[6];
					param[0] = idTask;
					param[1] = (nst.isLate ? new Integer(1) : new Integer(0));

					boolean success = true;

					switch (nst.getStatus()) {
    					case COMPLETE: 
    						    param[2] = "NULL_Integer";
    						    break;
    					case NOT_SEND:
        						success = false;
        						break;
    					case SEND:
        						param[2] = nst.getIdStage();
        						break;
    					case SEND_SUB_PROCESS:
    					        param[2] = nst.getIdStage();
    					        break;
					}

					if (isExpired) param[3] = new Integer(1);
					else param[3] = new Integer(0);

					param[4] = new MyOracleBlob(sign);
					param[5] = nst.getIdDepartament() == null ? "NULL_Long" : nst.getIdDepartament();

					if (success) params.add(param);
				}
			}
			LOGGER.info("Complation tasks...");
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			e.printStackTrace();
		}
		return params;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	
	
}
