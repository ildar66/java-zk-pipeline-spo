package ru.md.spo.util;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.masterdm.integration.ServiceFactory;
import ru.masterdm.integration.cps.CpsService;
import ru.masterdm.spo.list.ECpsMemberSectionKey;
import ru.masterdm.spo.utils.SBeanLocator;
import ru.md.persistence.MdTaskMapper;

/**
 * Фасад доступа к сервисам cps.
 * Скрывает сложность, что нужно синхронизировать изменения по всем версиям заявки.
 * Created by drone on 18.01.16.
 */
public class CpsFacade {
    private static final Logger LOGGER = LoggerFactory.getLogger("CpsFacade");

    /**
     * Синхронизация секций участников из {@link Long id} исходной версии сделки в {@link Long id} результирующей версии сделки.
     * - VTBSPO-871 Пример:
     * Есть версии, все одобрены: 
     * 1 1'
     * 2 
     * Создаем 2': проектная команда в 2' копируется из 2, остальные из последней одобренной ССКО - 1'.
     *
     * @param performerId {@link Long id} пользователя
     * @param sourceCreditDealId {@link Long id} исходной версии сделки
     * @param targetCreditDealId {@link Long id} результирующей версии сделки
     */
    public static void syncMembers(Long performerId, Long sourceCreditDealId, Long targetCreditDealId) {
    	try{
	        MdTaskMapper mdTaskMapper = SBeanLocator.singleton().mdTaskMapper();
	        CpsService cpsService = ServiceFactory.getService(CpsService.class);
	        Long targetCppsCreditDealId = mdTaskMapper.getCppsTaskIdByCedTaskId(targetCreditDealId);
	        if (targetCppsCreditDealId != null && !targetCppsCreditDealId.equals(targetCreditDealId)) {
	        	List<String> sectionKeys = ECpsMemberSectionKey.getNotProjectTeamSections();
	        	cpsService.syncMemberBySections(performerId,  sourceCreditDealId, targetCreditDealId, sectionKeys); //все кроме секции ПМ
	        	
	        	sectionKeys = new ArrayList<String>();
	        	sectionKeys.add(ECpsMemberSectionKey.PROJECT_TEAM.name());
	        	cpsService.syncMemberBySections(performerId,  targetCppsCreditDealId, targetCreditDealId, sectionKeys); //секция ПМ
	        }
	        else 
	        	cpsService.syncMemberBySections(performerId, sourceCreditDealId, targetCreditDealId, null); //все секции
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }
    
    public static void addMember(Long performerId, Long creditDealId, String sectionKey, Long userId) {
        ServiceFactory.getService(CpsService.class).addMember(performerId, creditDealId, sectionKey, userId);
        try{
            Long cedTaskId = SBeanLocator.singleton().mdTaskMapper().getCedTaskIdByCppsTaskId(creditDealId);
        	if (cedTaskId != null && !cedTaskId.equals(creditDealId))
                ServiceFactory.getService(CpsService.class).addMember(performerId, cedTaskId, sectionKey, userId);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }
    public static void removeMember(Long performerId, Long creditDealId, String sectionKey, Long userId) {
        ServiceFactory.getService(CpsService.class).removeMember(performerId, creditDealId, sectionKey, userId);
        try{
            Long cedTaskId = SBeanLocator.singleton().mdTaskMapper().getCedTaskIdByCppsTaskId(creditDealId);
        	if (cedTaskId != null && !cedTaskId.equals(creditDealId))
                ServiceFactory.getService(CpsService.class).removeMember(performerId, cedTaskId, sectionKey, userId);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }
    public static void executorSetting(Long performerId, Long creditDealId, Long userId, String spoRoleName) {
        try{
        	ServiceFactory.getService(CpsService.class).executorSetting(performerId, creditDealId, userId, spoRoleName);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        try{
            Long cedTaskId = SBeanLocator.singleton().mdTaskMapper().getCedTaskIdByCppsTaskId(creditDealId);
        	if (cedTaskId != null && !cedTaskId.equals(creditDealId)) 
        		ServiceFactory.getService(CpsService.class).executorSetting(performerId, cedTaskId, userId, spoRoleName); //TODO перед этим сделать addMember
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }
}
