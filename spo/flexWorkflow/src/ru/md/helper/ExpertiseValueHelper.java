package ru.md.helper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uit.director.db.dbobjects.Attribute;
import org.uit.director.db.dbobjects.AttributeStruct;
import org.uit.director.db.dbobjects.BasicAttribute;

import com.vtb.exception.FactoryException;

import ru.md.pup.dbobjects.AttachJPA;
import ru.md.pup.dbobjects.StageJPA;
import ru.md.spo.dbobjects.StandardPeriodGroupJPA;
import ru.md.spo.dbobjects.TaskJPA;
import ru.md.spo.ejb.PupFacadeLocal;
import ru.md.spo.ejb.TaskFacadeLocal;

public class ExpertiseValueHelper {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExpertiseValueHelper.class.getName());

	private static String[] EXPERTISE_DESIRED_ORDERED = { // массив в верхнем регистре
			"Требуется экспертиза Юридическим подразделением (банковские операции)".trim().toUpperCase(), "Требуется экспертиза Юридическим подразделением (инвестиционные и финансовые операции)".trim().toUpperCase(), "Требуется экспертиза подразделения по анализу рисков".trim().toUpperCase(), "Требуется экспертиза подразделения целевых резервов".trim().toUpperCase(), "Требуется экспертиза подразделения по анализу рыночных рисков".trim().toUpperCase(), "Требуется экспертиза Подразделения по обеспечению безопасности".trim().toUpperCase(), "Требуется экспертиза Подразделения по работе с залогами".trim().toUpperCase(), "Требуется экспертиза Казначейства".trim().toUpperCase(), "Требуется экспертиза Центральной бухгалтерии".trim().toUpperCase() };

	private static final String EXPERTISE_TYPE_ELEMENT_VALUE = "100";
	
	public static boolean getVal(String expName, Long idProcess) throws FactoryException {
		PupFacadeLocal pupFacade = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
		String v = pupFacade.getPUPAttributeValue(idProcess,expName);
	    boolean valBox = v.equals("1") || v.equalsIgnoreCase("y") || v.equalsIgnoreCase("true");
	    if (!expName.contains("меморандум") && isActiveExpertise(expName,idProcess)){
                return false;
        }
	    return valBox;
	}
	public static boolean isActiveExpertise(String expName, Long idProcess) throws FactoryException {
		//у экспертизы в дополнении после 100 может идти название этапа нормативных сроков этой экспертизы
		PupFacadeLocal pupFacade = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
		TaskFacadeLocal taskFacade = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
		TaskJPA mdtask = taskFacade.getTaskByPupID(idProcess);
		if(mdtask.getActiveStandardPeriodVersion()==null)
			return false;
		String addition = pupFacade.getAdditionVar(mdtask.getIdTypeProcess(), expName);
		if(!addition.contains(";"))
			return false;
		//TODO экспертиза ДВК должна обрабатываться каким-то особым образом. Но требований пока нет. Ждём...
		for(StandardPeriodGroupJPA group : mdtask.getActiveStandardPeriodVersion().getStandardPeriodGroups())
		    for(String spStageName : addition.split(";"))
			    if(group.getName().equals(spStageName))
					for(StageJPA stage : group.getStages())
					    if(pupFacade.isHasActiveTask(idProcess,stage.getIdStage()))//4 раза дёрнем базу этим запросом в среднем
					        return true;
	    return false;
	}
	/**
	 * Возвращает {@link Iterator итератор} на сортированный {@link List список} {@link BasicAttribute атрибутов}
	 *
	 * @param iterator {@link Iterator итератор}
	 * @return {@link Iterator итератор} на сортированный {@link List список} {@link BasicAttribute атрибутов}
	 * @throws Exception ошибка
	 */
	public static Iterator<BasicAttribute> getSortedAttributes(Iterator<BasicAttribute> iterator) throws Exception {
		try {
			Integer reservedCount = EXPERTISE_DESIRED_ORDERED.length;

			BasicAttribute[] reservedArea = new BasicAttribute[reservedCount];

			List<BasicAttribute> otherAreas = new ArrayList<BasicAttribute>();

			while (iterator.hasNext()) {
				BasicAttribute obj = iterator.next();
				if (!(obj instanceof AttributeStruct))
					continue;

				Attribute attr = ((AttributeStruct) obj).getAttribute();
				String typeElement = attr.getAddition();
				String name = attr.getName();

				if (name != null) {
					StringBuilder expretiseStartWith = (new StringBuilder()).append(EXPERTISE_TYPE_ELEMENT_VALUE).append(";");
					Boolean isExpretise = EXPERTISE_TYPE_ELEMENT_VALUE.equalsIgnoreCase(typeElement) || (typeElement != null && typeElement.startsWith(expretiseStartWith.toString()));

					if (isExpretise) {
						Integer searchIndex = findInArray(name.trim().toUpperCase(), EXPERTISE_DESIRED_ORDERED); // массив в верхнем регистре

						if (searchIndex == -1) {
							otherAreas.add(obj);
						} else
							reservedArea[searchIndex] = obj;
					} else
						otherAreas.add(obj);
				}
			}

			List<BasicAttribute> results = new ArrayList<BasicAttribute>();
			for (BasicAttribute value : reservedArea)
				if (value != null)
					results.add(value);

			results.addAll(otherAreas);
			return results.iterator();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw e;
		}
	}

	/**
	 * Возвращает найденный {@link Integer индекс} элемента в {@link массиве} или значение <code><b>-1</b></code>, если элемент не найден
	 *
	 * @param find строка для поиска
	 * @param whereFind массив строк, среди которых нужно найти
	 * @return найденный {@link Integer индекс} элемента в {@link массиве} или значение <code><b>-1</b></code>, если элемент не найден
	 */
	private static Integer findInArray(String find, String[] whereFind) {
		Integer result = -1;
		for (Integer index = 0; index < whereFind.length; index++) {
			if (find.equals(whereFind[index])) {
				result = index;
				break;
			}
		}
		return result;
	}
}
