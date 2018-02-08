package ru.md.spo.ejb;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.masterdm.integration.CCStatus;
import ru.masterdm.integration.SPOCCIntegrationFacadeRemote;

import com.vtb.domain.Task;
import com.vtb.mapping.MapperFactory;
import com.vtb.mapping.jdbc.TaskMapper;
import com.vtb.model.ActionProcessorFactory;
import com.vtb.model.TaskActionProcessor;

@Stateless
public class SPOCCIntegrationFacade implements SPOCCIntegrationFacadeRemote {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(SPOCCIntegrationFacade.class.getName());
    @Override @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public void statusNotification(CCStatus status, Long mdtaskid) throws Exception {
        if (status.getMeetingDate()==null)
            throw new Exception("invalid parameter MeetingDate can not be null");
        if (status.getStatus()==null)
            throw new Exception("invalid parameter statusid can not be null");
        TaskMapper mapper = (TaskMapper)MapperFactory.getSystemMapperFactory().getMapper(Task.class);
        mapper.updateCCStatus(status, mdtaskid);
      //решение пришло, можно разблокировать процесс
        TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
        Task task = processor.getTask(new Task(mdtaskid));
        processor.updateAttribute(task.getId_pup_process().longValue(), "Decision", 
                task.getCcStatus().getStatus().getCategoryId().toString());
    }

    @Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public CCStatus getStatus(Long mdtaskid) throws Exception {
        TaskMapper mapper = (TaskMapper)MapperFactory.getSystemMapperFactory().getMapper(Task.class);
        return mapper.getStatus(mdtaskid);
    }

}
