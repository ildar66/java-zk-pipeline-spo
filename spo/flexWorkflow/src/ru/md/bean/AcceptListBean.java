/**
 * 
 */
package ru.md.bean;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


import ru.masterdm.compendium.value.Page;
import ru.md.pup.dbobjects.AcceptJPA;
import ru.md.spo.ejb.PupFacadeLocal;
import ru.md.spo.util.Config;

import com.vtb.domain.Task;
import com.vtb.exception.FactoryException;
import com.vtb.model.ActionProcessorFactory;
import com.vtb.model.TaskActionProcessor;
import com.vtb.util.ApplProperties;
import com.vtb.util.Formatter;

/**
 * JSP бин для получения списка акцептов операций
 * 
 * @author imatushak@masterdm.ru
 * 
 */
public class AcceptListBean {

    private final Logger logger = Logger.getLogger(getClass().getName());

    private Long userId = null;
    private List<AcceptJPA> acceptList = null;
    private String pageNum = null;
    private AcceptJPA accept = null;

    /**
     * @return the pageCount
     */
    public Integer getPageCount() {
        try {
            int maxResult = Integer.parseInt(Config.getProperty("PROCESSES_ON_PAGE"));
            int totalCount = getTotalCount();
            return (totalCount == 0) ? 0 : ((totalCount - 1) / maxResult) + 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * @return the totalCount
     */
    public Integer getTotalCount() {
        try {
            PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
            return pupFacadeLocal.getAcceptListSize(userId).intValue();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * @return the activeStageUrl
     */
    public String getActiveStageUrl() {
        if (getTask() == null) {
            return null;
        }
        String activeStageUrl = "file:///" + ApplProperties.getReportsPath() + "Audit/active_stages.rptdesign";
        activeStageUrl = "reportPrintFormRenderAction.do?__format=html&notused=off&__report=" + activeStageUrl
              + "&isDelinquency=-1&correspondingDeps=on&p_idDepartment=-1&mdtaskId=" + getTask().getId_task();
        return activeStageUrl;
    }

    /**
     * @return the task
     */
    public Task getTask() {
        Task task = null;
        try {
            if (accept == null || accept.getTaskInfo() == null || accept.getTaskInfo().getProcess() == null) {
                return task;
            }
            Long processId = accept.getTaskInfo().getProcess().getId();
            TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
            task = processor.findByPupID(processId, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return task;
    }

    public String getTaskSum() {
        Task task = getTask();
        if (task == null) {
            return null;
        }
        return Formatter.format(task.getMain().getSum());
    }

    /**
     * @return the accept
     */
    public AcceptJPA getAccept() {
        return accept;
    }

    /**
     * @param accept
     *            the accept to set
     */
    public void setAccept(AcceptJPA accept) {
        this.accept = accept;
    }

    /**
     * @return the startPosition
     */
    public Integer getStartPosition() {
        Integer startPosition = 1;
        try {
            startPosition = Integer.parseInt(pageNum);
        } catch (NumberFormatException nfe) {
            startPosition = 1;
        }

        return startPosition;
    }

    /**
     * @param pageNum
     *            the pageNum to set
     */
    public void setPageNum(String pageNum) {
        this.pageNum = pageNum;
    }

    /**
     * @return the userId
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * @param userId
     *            the userId to set
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * @return the acceptList
     */
    public List<AcceptJPA> getAcceptList() {
        try {
            PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
            acceptList = pupFacadeLocal.getAcceptList(userId);
            logger.log(Level.INFO, "Accept list by userId '" + userId + "' which size is '" + acceptList.size() + "'");
        } catch (FactoryException e) {
            e.printStackTrace();
        }
        return acceptList;
    }

    /**
     * @param acceptList
     *            the acceptList to set
     */
    public void setAcceptList(List<AcceptJPA> acceptList) {
        this.acceptList = acceptList;
    }

    /**
     * @return the acceptListPage
     */
    @SuppressWarnings("unchecked")
    public Page getAcceptListPage() {
        try {
            int maxResult = Integer.parseInt(Config.getProperty("PROCESSES_ON_PAGE"));
            PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
            acceptList = pupFacadeLocal.getAcceptList(userId, (getStartPosition() - 1) * maxResult + 1, maxResult);

            int totalCount = getTotalCount();

            logger.log(Level.INFO, "Accept list by userId '" + userId + "' which size is '" + acceptList.size() + "'");

            if (totalCount == 0) {
                return Page.EMPTY_PAGE;
            }

            Page returnPage = new Page(acceptList, getStartPosition(),
                    (getStartPosition() + acceptList.size()) < totalCount);
            returnPage.setTotalCount(totalCount);
            return returnPage;

        } catch (FactoryException e) {
            e.printStackTrace();
            return Page.EMPTY_PAGE;
        }
    }

}
