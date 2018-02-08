package ru.md.spo.util;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vtb.domain.Task;
import com.vtb.model.TaskActionProcessor;

import ru.masterdm.integration.cps.CpsService;
import ru.masterdm.spo.utils.SBeanLocator;
import ru.md.pup.dbobjects.UserJPA;
import ru.md.spo.dbobjects.TaskJPA;
import ru.md.spo.ejb.TaskFacadeLocal;

/**
 * Помощник для создания версий заявок
 * @author rislamov
 */
public class TaskVersionHelper {
	private static final Logger LOGGER = LoggerFactory.getLogger(TaskVersionHelper.class.getName());

	/**
	 * Создание версии заявки для КОД
	 * 
	 * @param idPerformer id пользователя, запустившего создание версии
	 * @param task исходная заявка
	 * @param idParent идентификатор родителя новой  версии
	 * @param version номер новой версии
	 * @param tfl TaskFacadeLocal
	 * @param proc TaskActionProcessor
	 * @return новая версия заявки
	 * @throws Exception возможны ошибки
	 */
	public static TaskJPA createCedVersion (Long idPerformer, Task task, Long idParent, Long version, TaskActionProcessor proc, TaskFacadeLocal tfl) throws Exception {
		Long oldTaskId = task.getId_task();

		TaskJPA newTask = createVersion(task, null, null, null, false, idParent, version, tfl, null, null, null, proc);

		Long newTaskId = newTask.getId();

		LOGGER.info("=======TaskVersionHelper.createCedVersion(idPerformer, task, idParent, version, proc, tfl) syncMembers. idUser '" + idPerformer + "', oldTaskId '" + oldTaskId + "', newTaskId '" + newTaskId + "'");
		
		if (idPerformer != null)
			CpsFacade.syncMembers(idPerformer,  oldTaskId, newTaskId);
		return newTask;
	}

	/**
	 * Создание версии заявки
	 * @param task исходная заявка
	 * @param typeProcess тип процесса новой заявки
	 * @param idUser создатель
	 * @param comment комментарий
	 * @param needAssign требуется назначение
	 * @param idParent идентификатор родителя новой  версии
	 * @param version номер новой версии
	 * @param tfl TaskFacadeLocal
	 * @param oldRoleName роль структуратора в старой версии
	 * @param newRoleName роль структуратора в новой версии
	 * @param newUser новый исполнитель
	 * @param proc TaskActionProcessor
	 * @return новая версия заявки
	 * @throws Exception возможные ошибки
	 */
	public static TaskJPA createVersion (Task task, Integer typeProcess, Long idUser, String comment,
			boolean needAssign, Long idParent, Long version, TaskFacadeLocal tfl, String oldRoleName, 
			String newRoleName, UserJPA newUser, TaskActionProcessor proc) throws Exception {
		Long oldTaskId = task.getId_task();
		LOGGER.debug("Create version for task #" + oldTaskId + ". Step 1");
		task.getMain().setMainLoaded(true);
		Task newTask = tfl.createDomainTaskVersion(task, typeProcess, idUser, comment, needAssign, idParent, version);
		LOGGER.debug("Create version for task #" + oldTaskId + ". Step 2");
		tfl.spoContractorSync(newTask.getId_task());
		LOGGER.debug("Create version for task #" + oldTaskId + ". Step 3");
		TaskJPA newTaskJPA = tfl.copyJpaFieldsToNewVersion(oldTaskId, newTask.getId_task(), 
				idUser, oldRoleName, newRoleName, newUser, newTask.getTranceList());
		SBeanLocator.singleton().mdTaskMapper().copyMyBatisTask(oldTaskId, newTaskJPA.getId());
		if (newTaskJPA.isLimit() || newTaskJPA.isSublimit()) {
			LOGGER.debug("Create sublimits' versions for task #" + oldTaskId);
			ArrayList<Task> sublimits = proc.findTaskByParent(oldTaskId, false, false);
			for (Task child : sublimits) {
				if (child.isSubLimit()) {
					createVersion(child, null, null, null, false, newTaskJPA.getId(), newTaskJPA.getVersion(),
							tfl, null, null, null, proc);
				}
			}
			LOGGER.debug("Sublimits' versions for task #" + oldTaskId + " created");
		}
		LOGGER.debug("Version for task #" + oldTaskId + " created");
		return newTaskJPA;
	}

}