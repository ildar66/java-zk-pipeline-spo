package ru.md.load;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.uit.director.contexts.WPC;
import org.uit.director.db.ejb.DBFlexWorkflowCommon;

import ru.md.spo.ejb.PupFacadeLocal;

import com.vtb.domain.TaskListType;
import com.vtb.domain.WorkflowTaskInfo;
import com.vtb.exception.ModelException;
import com.vtb.model.ActionProcessorFactory;
import com.vtb.model.TaskActionProcessor;

/**
 * Загрузчик заявки в СПО. Абстактный класс.
 * @author Andrey Pavlenko
 *
 */
public abstract class AbstractLoader {
    private static final Logger LOGGER = Logger.getLogger(AbstractLoader.class.getName());
    protected TaskActionProcessor processor = null;
    protected Long applicationId = null;//наш числовой номер заявки
    protected String CRMID = null;
    protected Integer idTypeProcess;//тип процесса
    protected DBFlexWorkflowCommon dbFlexDirector;//ссылка на ижевский бин
    
    public AbstractLoader(Integer idTypeProcess, String CRMID, DBFlexWorkflowCommon dbFlex) {
        super();
        this.idTypeProcess = idTypeProcess;
        processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
        try {
            PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
            applicationId = pupFacadeLocal.getNextMdTaskNumber();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        this.CRMID = CRMID;
        dbFlexDirector = dbFlex;
    }
    
    /**
     * Загружает сделку или лимит с номером, указанном в конструкторе класса, из CRM или старого СПО на 6 was 
     * @return строка для пользователя о статусе загрузки
     */
    public abstract String load() throws Exception;
    
    /**
     * Устанавливает атрибуты ПУП.
     */
    protected Object[] assignValuesForProcess(String valueName, Object value) {
        Object param[] = new Object[3];
        param[0] = idTypeProcess;
        param[1] = WPC.getInstance().getIdVariableByDescription(valueName, idTypeProcess);
        param[2] = value;
        
        System.out.println("!!!!!! LoaderFromCRMCommand assignValuesForProcess " + valueName + " = " + value);
        
        return param;
    }
    
    /**
     * Сохраняет результат загрузки в журнал.
     * @param id - ID лимита или сделки
     * @param i - код успешности. 1 - успешно, 2 - ошибка
     * @param message - сообщение
     * @throws ModelException
     */
    public void crmlog(int i,String message) {
        try {
            processor.crmlog(CRMID, i, message);
        } catch (ModelException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
        }
    }
    
    /**
     * Взятие первых задач сразу в работу.
     */
    @SuppressWarnings("unchecked")
    protected void acceptTask(DBFlexWorkflowCommon dbFlexWorkflow, Long idProcess, Long idUser) throws Exception {
        try {
            ArrayList<Long> taskList = dbFlexWorkflow.getWorkList(idUser, TaskListType.NOT_ACCEPT.ordinal(), null);
            
            LOGGER.info( "acceptTask: get task free list");
            
            WorkflowTaskInfo wfTaskInfo = null; 
            Long idTask = null;
            for (int i = 0; i < taskList.size(); i++) {
                idTask = taskList.get(i);
                wfTaskInfo = dbFlexWorkflow.getTaskInfo(idTask);
                
                LOGGER.info( "acceptTask: get wfTaskInfo by idTask = " + idTask);
                
                if (wfTaskInfo.getIdProcess().equals(idProcess)) {
                    dbFlexWorkflow.acceptWork(idTask, idUser, "");
                    
                    LOGGER.info( "acceptTask: acceptWork by idTask = " + idTask);
                }
            }
        } catch (Exception e) {
            LOGGER.info( "acceptTask: Error = " + e.getMessage());
            e.printStackTrace();
            
            throw new ModelException(e, ("Exception caught in acceptTask " + e));
            
        }
    }
}
