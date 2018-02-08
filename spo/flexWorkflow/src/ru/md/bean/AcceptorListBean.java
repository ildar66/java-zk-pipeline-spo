package ru.md.bean;

import java.util.List;
import java.util.logging.Logger;

import ru.md.pup.dbobjects.AcceptJPA;
import ru.md.spo.ejb.PupFacadeLocal;

import com.vtb.exception.VtbException;

public class AcceptorListBean {
    private final Logger logger = Logger.getLogger(getClass().getName());

    private Long taskId = null;

    /**
     * @return the taskId
     */
    public Long getTaskId() {
        return taskId;
    }

    /**
     * @param taskId
     *            the taskId to set
     */
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public List<AcceptJPA> getAcceptList() throws VtbException {
        if (taskId == null) {
            return null;
        }
        try {
            PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
            return pupFacadeLocal.getApprovedAcceptList(taskId);
        } catch (Exception e) {
            throw new VtbException(e, "Ошибка получения списка акцепторов по операции с идентификатором '" + taskId
                    + "'");
        }
    }
}
