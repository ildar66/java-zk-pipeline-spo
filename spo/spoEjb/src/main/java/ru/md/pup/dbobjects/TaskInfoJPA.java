package ru.md.pup.dbobjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.vtb.domain.WorkflowTaskInfo;
@Entity
@Table(name = "TASKS")
public class TaskInfoJPA implements Serializable {
    public enum TypeList {
        accept,perform,noAccept,all
    }
    private static final long serialVersionUID = 1L;
    @Id @Column(name="ID_TASK")
    private Long idTask;
    
    @ManyToOne @JoinColumn(name="ID_PROCESS")
    private ProcessJPA process;
    
    @ManyToOne
    @JoinColumn(name="ID_TYPE_PROCESS")
    private ProcessTypeJPA processType;
    
    @ManyToOne
    @JoinColumn(name="ID_STAGE_TO")
    private StageJPA stage;
    
    @ManyToOne @JoinColumn(name="ID_USER")
    private UserJPA executor;
    
    @Column(name="TYPE_COMPLATION")
    private Long typeComplation;
    
    @Column(name="ID_DEPARTMENT")
    private Long idDepartament;
    
    @Column(name="ID_STATUS")
    private Integer idStatus;
    
    @Column(name = "DT_PLAN_COMPLETION")
    @Temporal(TemporalType.TIMESTAMP)
    private Date planCompletionDate;
    
    @ManyToMany(fetch=FetchType.LAZY) @JoinTable(name="stages_from",
            joinColumns = @JoinColumn(name = "id_task"),
            inverseJoinColumns = @JoinColumn(name = "id_stage"))
    private List<StageJPA> stagesFrom;
    
    @OneToMany(mappedBy="taskInfo",fetch=FetchType.LAZY)
    private Set<TaskEventJPA> events;
    
    public String toString() {
        return "idTask="+idTask+" idStatus="+idStatus +"("+ stage.getDescription()+")";
    }
    public WorkflowTaskInfo toWorkflowTaskInfo() {
        WorkflowTaskInfo ti = new WorkflowTaskInfo();
        ti.setIdProcess(process.getId());
        ti.setIdTypeProcess(processType.getIdTypeProcess().intValue());
        ti.setTypeProcessName(processType.getDescriptionProcess());
        ti.setIdStageTo(stage.getIdStage());
        ti.setStageName(stage.getDescription());
        if(executor != null){
            ti.setIdExecutor(executor.getIdUser());
            ti.setExecutorFIO(executor.getFullName());
        }
        ti.setTypeComplation(typeComplation);
        ti.setIdDepartament(idDepartament);
        ti.setIdStatus(idStatus);
        ti.setDateOfPlanComplation(planCompletionDate);
        
        ArrayList<Long> idStagesFrom = new ArrayList<Long>();
        if (this.getStagesFrom() != null)
            for (StageJPA stage : this.getStagesFrom()){
                idStagesFrom.add(stage.getIdStage());
            }
        ti.setIdStageFrom(idStagesFrom);
        for (TaskEventJPA event : getEvents()){
            String formattedDate = event.getDate().toString();
            if(event.getType().longValue()==1) ti.setDateOfComming(formattedDate);
            if(event.getType().longValue()==3||event.getType().longValue()==4||
                    event.getType().longValue()==5) 
                ti.setDateOfComplation(formattedDate);
            if(event.getType().longValue()==2) ti.setDateOfTaking(formattedDate);
        }
        return ti;
    }
    /**
     * @return idTask
     */
    public Long getIdTask() {
        return idTask;
    }
    /**
     * @param idTask idTask
     */
    public void setIdTask(Long idTask) {
        this.idTask = idTask;
    }

    /**
     * @return idDepartament
     */
    public Long getIdDepartament() {
        return idDepartament;
    }
    /**
     * @param idDepartament idDepartament
     */
    public void setIdDepartament(Long idDepartament) {
        this.idDepartament = idDepartament;
    }
    /**
     * @return idStatus
     */
    public Integer getIdStatus() {
        return idStatus;
    }
    public boolean isInProgress() {
        return idStatus != null && idStatus.intValue()==2;
    }
    /**
     * @param idStatus idStatus
     */
    public void setIdStatus(Integer idStatus) {
        this.idStatus = idStatus;
    }
    /**
     * @return stagesFrom
     */
    public List<StageJPA> getStagesFrom() {
        return stagesFrom;
    }
    /**
     * @param stagesFrom stagesFrom
     */
    public void setStagesFrom(List<StageJPA> stagesFrom) {
        this.stagesFrom = stagesFrom;
    }
    /**
     * @return events
     */
    public Set<TaskEventJPA> getEvents() {
        return events;
    }
    public Long getTypeComplation() {
        return typeComplation;
    }
    public void setTypeComplation(Long typeComplation) {
        this.typeComplation = typeComplation;
    }
    public Date getPlanCompletionDate() {
        return planCompletionDate;
    }
    public void setPlanCompletionDate(Date planCompletionDate) {
        this.planCompletionDate = planCompletionDate;
    }
    public ProcessJPA getProcess() {
        return process;
    }
    public void setProcess(ProcessJPA process) {
        this.process = process;
    }
    public ProcessTypeJPA getProcessType() {
        return processType;
    }
    public void setProcessType(ProcessTypeJPA processType) {
        this.processType = processType;
    }
    public StageJPA getStage() {
        return stage;
    }
    public void setStage(StageJPA stage) {
        this.stage = stage;
    }
    /**
     * @return executor
     */
    public UserJPA getExecutor() {
        return executor;
    }
    public String getExecutorName() {
        if(getExecutor()==null)
            return "";
        return getExecutor().getFullName();
    }
    /**
     * @param executor executor
     */
    public void setExecutor(UserJPA executor) {
        this.executor = executor;
    }
    public Date getStartDate(){
    	for(TaskEventJPA event : events){
    		if(event.getType().equals(1L))
    			return event.getDate();
    	}
    	return null;
    }
    public Date getEndDate(){
    	for(TaskEventJPA event : events){
    		if(event.getType().equals(3L) || event.getType().equals(5L) || event.getType().equals(4L))
    			return event.getDate();
    	}
    	return null;
    }
}
