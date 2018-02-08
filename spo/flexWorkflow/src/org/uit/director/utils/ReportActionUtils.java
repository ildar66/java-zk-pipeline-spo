package org.uit.director.utils;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.uit.director.action.AbstractAction;
import org.uit.director.contexts.WPC;
import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.db.dbobjects.WorkflowDepartament;
import org.uit.director.db.dbobjects.WorkflowRoles;
import org.uit.director.db.dbobjects.WorkflowStages;
import org.uit.director.db.dbobjects.WorkflowUser;

import ru.masterdm.compendium.domain.Department;
import ru.masterdm.compendium.model.CompendiumActionProcessor;
import ru.md.pup.dbobjects.ProcessTypeJPA;
import ru.md.spo.ejb.PupFacadeLocal;

import com.vtb.exception.ModelException;
import com.vtb.exception.NoSuchDepartmentException;
import com.vtb.util.ApplProperties;
import com.vtb.value.BeanKeys;

public class ReportActionUtils {
    
	private static final Logger logger = Logger.getLogger(ReportActionUtils.class.getName());
    
    /**
     * Предоставить доступ к отчетам только администратору
     * @param request
     * @param mapping
     * @return ActionForward (если доступ закрыт) или null если доступ открыт
     */
    public static ActionForward accessForAdmin(HttpServletRequest request, ActionMapping mapping) {
        WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
        if (wsc.isNewContext())
            return (mapping.findForward("start"));

        //проверить, что текщий пользователь - администратор
        //VTBSPO-699 теперь отчёт открыт всем
        /*if (!wsc.isAdmin()) {
            wsc.setErrorMessage("Отчет доступен только администратору системы");
            return (mapping.findForward("errorPage"));
        }*/

        //доступ открыт
        return null;
    }

    /**
     * Установить ОДИН департамент для вывода в jsp-странице
     * @param request
     */
    public static void setDepartmensToRequest(HttpServletRequest request) throws NoSuchDepartmentException, RemoteException {
        //получение текущего департамента
        WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
        WorkflowUser userInfo = wsc.getCurrentUserInfo();
        WorkflowDepartament currentDepartment = userInfo.getDepartament();


        //по умолчанию будет выбран текущий департамент
        request.setAttribute(BeanKeys.REPORT_FILTER_SELECTED_DEPARMENT, currentDepartment.getShortName());
        request.setAttribute(BeanKeys.REPORT_FILTER_SELECTED_ID_DEPARMENT, currentDepartment.getIdDepartament().toString());
    }

           
    /**
	 * Установить [для пользователя idUser] перечень ВСЕХ департаментов для вывода в jsp-странице [INCLUDE_ALL]
	 * либо только дочерних департаментов [INCLUDE_SUBORDINATE]
	 * либо никаких [INCLUDE_NONE](тогда обычно дается includeSelf) 
	 * В первом случае (для всех департаментов) добавить для департаментов слова 'Все департаменты' [includeAllWords = true]
	 * либо нет. 
	 * Во втором случае (для подчиненных департаментов) добавить для департаментов слова 'Все департаменты' [includeAllWords = true]
	 * либо нет.
	 * В последнем случае [когда включаем только дочерних] -- включить сам департамент, в котором 
	 * работает пользователь [includeSelf = true], либо нет [includeSelf = false]. 
	 * @param request
	 * @param includeFlag  -- установить перечень всех департаментов, подчиненных департаментов или никаких подразделений
	 * @param includeAllWords включать (true) или не включать слова "все департаменты" (в случае, если includeFlag = true)
	 * @param includeSelf -- добавить в список само подразделение, к которому принадлежит пользователь, или нет.
	 * @param idUser  -- id пользователя, для которого получаем список департаментов.
	 * 
	 * TODO: позднее аккуратно слить с процедурой setDepartmensToRequest для выбора департамента [там возвращается только один департамент]. 
     * @throws ModelException 
	 */
	private static void setDepartmentsForUser(HttpServletRequest request, Integer includeFlag, boolean includeAllWords, boolean includeSelf, Long idUser) 
						throws NoSuchDepartmentException, RemoteException, ModelException {
				
		Map<String, String> departmentMap = WPC.setDepartmentsForUser(includeFlag,includeAllWords, includeSelf, idUser);
		//фильтруем кредитные комитеты
		CompendiumActionProcessor compenduim = (CompendiumActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("Compendium");
		Department[] alldep = compenduim.getDepartmentListAll();
		Map<String,String> filteredDep = new LinkedHashMap<String, String>();
		Set<String> set = departmentMap.keySet();
        Iterator<String> iter = set.iterator();
		while(iter.hasNext())
		{
			String code = (String) iter.next();
			// adds ALL DEPARTMENT element, if found
			if(code.equals("-1")) filteredDep.put(code, departmentMap.get("-1"));
			for(Department dep:alldep){
				if(dep.getId().toString().equals(code)){
					filteredDep.put(code, departmentMap.get(code));
				}
			}
		}
	    // иногда вызываем с null значением request (когда он нам не нужен). 
	    // Поэтому устанавливаем параметр, только когда не null 
	    if (request != null) {
	    	request.setAttribute(BeanKeys.REPORT_FILTER_DEPARMENTS, filteredDep);
	    }
	}
    
        
    /**
	 * Установить [для ТЕКУЩЕГО пользователя] перечень ВСЕХ департаментов для вывода в jsp-странице [INCLUDE_ALL]
	 * либо только дочерних департаментов [INCLUDE_SUBORDINATE]
	 * либо никаких [INCLUDE_NONE](тогда обычно дается includeSelf) 
	 * В первом случае (для всех департаментов) добавить для департаментов слова 'Все департаменты' [includeAllWords = true]
	 * либо нет.
	 * В последнем случае [когда включаем только дочерних] -- включить сам департамент, в котором 
	 * работает пользователь [includeSelf = true], либо нет [includeSelf = false]. 
	 * @param request
	 * @param includeFlag  -- установить перечень всех департаментов, подчиненных департаментов или никаких подразделений
	 * @param includeAllWords включать (true) или не включать слова "все департаменты" (в случае, если includeFlag = true)
	 * @param includeSelf -- добавить в список само подразделение, к которому принадлежит пользователь, или нет.
	 * @param idUser  -- id пользователя, для которого получаем список департаментов.     *  
	 * TODO: позднее аккуратно слить с процедурой setDepartmensToRequest для выбора департамента [там возвращается только один департамент]. 
     * @throws ModelException 
	 */
	public static void setDepartmentsForCurrentUser(HttpServletRequest request,Integer includeFlag, boolean includeAllWords, boolean includeSelf) 
						throws NoSuchDepartmentException, RemoteException, ModelException {
		//получение текущего пользователя
		Long currentUser = null;
		try{
	        WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
	        WorkflowUser userInfo = wsc.getCurrentUserInfo();
	        currentUser  = userInfo.getIdUser();
	    }catch(Exception e)
	    { // по каким-то причинам не смогли найти пользователя
	    	currentUser = null;
	    }			
		setDepartmentsForUser(request, includeFlag, includeAllWords, includeSelf, currentUser);
	}
    
    
    /**
     * Установить период для вывода в jsp-странице
     * @param request
     */
    public static void setPeriodInRequest(HttpServletRequest request) {
        //расчет период по умочнанию на последние 7 дней
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Calendar c = Calendar.getInstance();
        String sRightDate = sdf.format(c.getTime());

        c.add(Calendar.DAY_OF_MONTH, -7); //установка неделю назад
        String sLeftDate = sdf.format(c.getTime());

        //по умолчанию будет выбран текущий департамент
        request.setAttribute(BeanKeys.REPORT_FILTER_LEFT_DATE, sLeftDate);
        request.setAttribute(BeanKeys.REPORT_FILTER_RIGHT_DATE, sRightDate);
    }

    /**
     * Установка файла с отчетом
     * @param request текущий http контекст запроса
     * @param fileName файл с отчетом
     */
    public static void setFileReportInRequest(HttpServletRequest request, String fileName) {
        String sFile = "file:///" + ApplProperties.getReportsPath().replace('\\', '/') + fileName;
        request.setAttribute(BeanKeys.REPORT_FILTER_FILE, sFile);
        logger.info("Указан файл отчета " + fileName);
    }

    /**
     * Установить текущего пользователя для вывода в jsp-странице
     * @param request
     */
    public static void setCurrentUserInRequest(HttpServletRequest request) {
        WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
        WorkflowUser userInfo = wsc.getCurrentUserInfo();
        request.setAttribute(BeanKeys.REPORT_FILTER_SELECTDED_USER_ID, userInfo.getIdUser().toString());
        request.setAttribute(BeanKeys.REPORT_FILTER_SELECTDED_USER, userInfo.getFIO());
    }

    /**
     * Установить список ролей с процесссами 
     * @param request текущий http контекст запроса
     * @param includeAll включать (true) или не включать "все роли"
     * @throws ModelException
     * @throws RemoteException
     */
    public static void setRolesByProcess(HttpServletRequest request, long processId) throws ModelException, RemoteException {
        //выборка всех ролей пользователей
        List<WorkflowRoles> roles = WPC.getInstance().getRoles();
        Map<String, String> roleNames = new LinkedHashMap<String, String>();
        roleNames.put("-1", "Все роли");

        for (WorkflowRoles r : roles) {
            if (r.getIdTypeProcess() == processId) {
                roleNames.put(Long.toString(r.getId()), r.getName());
            }
        }
        //по умолчанию будет выбран текущий департамент
        request.setAttribute(BeanKeys.REPORT_FILTER_ROLES, roleNames);
    }

    /**
     * Установить список процесссов 
     * @param request текущий http контекст запроса
     * @param includeAll включать (true) или не включать "все процессы"
     * @throws ModelException
     * @throws RemoteException
     */
    public static void setProcess(HttpServletRequest request, boolean includeAll) throws Exception {
        PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
        Map<String, String> processMap = new LinkedHashMap<String, String>();
        if (includeAll)
            processMap.put("-1", "Все процессы");

        for (ProcessTypeJPA p : pupFacadeLocal.findProcessTypeList()) {
            processMap.put(p.getIdTypeProcess().toString(), p.getDescriptionProcess());
        }
        //по умолчанию будет выбран текущий департамент??? почему департамент?
        request.setAttribute(BeanKeys.REPORT_PROCESSES, processMap);
    }

    public static void setStages(HttpServletRequest request, boolean includeAll) {
        //выборка всех ролей пользователей
        List<WorkflowStages> stages = WPC.getInstance().getStages();
        Map<String, String> roleNames = new LinkedHashMap<String, String>(stages.size() + 1);
        if (includeAll)
            roleNames.put("-1", "Все операции");

        for (WorkflowStages s : stages) {
            if (s.isActive()) {
                roleNames.put(Long.toString(s.getIdStage()), s.getName());
            }
        }
        //по умолчанию будет выбран текущий департамент
        request.setAttribute(BeanKeys.REPORT_STAGES, roleNames);
    }

    /**
     * Установить все стадии текущего процесса
     * @param request
     * @param processId id текущего процесса
     */
    public static void setStagesByProcessId(HttpServletRequest request, long processId) {
        //выборка всех ролей пользователей
        List<WorkflowStages> stages = WPC.getInstance().getStages();
        Map<String, String> roleNames = new LinkedHashMap<String, String>();
        roleNames.put("-1", "Все операции процесса");

        for (WorkflowStages s : stages) {
            if (s.isActive() && s.getIdTypeProcess() == processId) {
                roleNames.put(Long.toString(s.getIdStage()), s.getName());
            }
        }
        //по умолчанию будет выбран текущий департамент
        request.setAttribute(BeanKeys.REPORT_STAGES, roleNames);
    }

    /**
     * Установить текущий id процесса
     * @return текущий id процесса
     * @throws RemoteException 
     * @throws ModelException 
    **/
    public static long setCurrentProcess(HttpServletRequest request) throws Exception {
        String id = request.getParameter(BeanKeys.REPORT_CURRENT_PROCESS);
        long processId;
        if (id == null) {
            //id процесса ранее не был установлен, нужно получить первый id процесс 
            PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
            processId = pupFacadeLocal.findProcessTypeList().get(0).getIdTypeProcess();
        } else
            processId = Long.parseLong(id);

        request.setAttribute(BeanKeys.REPORT_CURRENT_PROCESS, Long.toString(processId));
        //id уже установлен на ReportFullStagesByProcessAction
        return processId;
    }
}
