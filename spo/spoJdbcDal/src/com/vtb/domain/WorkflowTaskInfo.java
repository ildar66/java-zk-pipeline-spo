package com.vtb.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WorkflowTaskInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    Long idProcess;
    Integer idTypeProcess;
    String typeProcessName;
    
    String dateOfComming;
    String dateOfTaking;
    String dateOfComplation;
    Date dateOfPlanComplation;  //date of plan complation

    Long idStageTo;
    String stageName;
    
    Long idExecutor;
    String executorFIO; 
    Long idDepartament;
    // MK, 20100119, Added
    Integer idStatus;   // текущий статус операции
    
    Long typeComplation;
    
    
    ArrayList<Long> idStagesFrom;    

    public WorkflowTaskInfo() {
        super();
    }
    public String toString(){
        return "idProcess: "+idProcess+", executor: "+executorFIO;
    }

    public WorkflowTaskInfo(Long idProcess, String dateOfComming, String dateOfTaking, String dateOfComplation, Integer idTypeProcess,
            Long idStageTo, Long idExecutor, String executorFIO, Long idDepartament, ArrayList<Long> idStageFrom, Integer idStatus) {
        this.idProcess = idProcess;
        this.dateOfComming = dateOfComming;
        this.dateOfTaking = dateOfTaking;
        this.dateOfComplation = dateOfComplation;
        this.idTypeProcess = idTypeProcess;
        this.idStageTo = idStageTo;
        this.idExecutor = idExecutor;
        this.executorFIO = executorFIO; 
        this.idStagesFrom = idStageFrom;
        this.idDepartament = idDepartament;
        this.idStatus = idStatus;
    }

    public Long getIdProcess() {
        return idProcess;
    }

    public void setIdProcess(Long idProcess) {
        this.idProcess = idProcess;
    }

    public String getDateOfComming() {
        return dateOfComming;
    }

    public void setDateOfComming(String dateOfComming) {
        this.dateOfComming = dateOfComming;
    }

    public Integer getIdTypeProcess() {
        return idTypeProcess;
    }

    public void setIdTypeProcess(Integer idTypeProcess) {
        this.idTypeProcess = idTypeProcess;
    }

    public Long getIdStageTo() {
        return idStageTo;
    }

    public void setIdStageTo(Long idStageTo) {
        this.idStageTo = idStageTo;
    }

    public  List<Long> getIdStagesFrom() {
        return idStagesFrom;
    }

    public void setIdStageFrom( ArrayList<Long> idStagesFrom) {
        this.idStagesFrom = idStagesFrom;
    }


    public String getDateOfTaking() {
        return dateOfTaking==null?"":dateOfTaking;
    }

    public void setDateOfTaking(String dateOfTaking) {
        this.dateOfTaking = dateOfTaking;
    }

    public String getDateOfComplation() {
        return dateOfComplation == null?"":dateOfComplation;
    }

    public void setDateOfComplation(String dateOfComplation) {
        this.dateOfComplation = dateOfComplation;
    }

    public Long getIdExecutor() {
        return idExecutor;
    }

    public void setIdExecutor(Long idExecutor) {
        this.idExecutor = idExecutor;
    }

    public Long getIdDepartament() {
        return idDepartament;
    }

    public void setIdDepartament(Long idDepartament) {
        this.idDepartament = idDepartament;
    }

    public Integer getIdStatus() {
        return idStatus;
    }

    public void setIdStatus(Integer idStatus) {
        this.idStatus = idStatus;
    }

    public String getExecutorFIO() {
        return executorFIO;
    }

    public void setExecutorFIO(String executorFIO) {
        this.executorFIO = executorFIO;
    }

    public void setIdStagesFrom(ArrayList<Long> idStagesFrom) {
        this.idStagesFrom = idStagesFrom;
    }
    public Date getDateOfPlanComplation() {
        return dateOfPlanComplation;
    }
    public void setDateOfPlanComplation(Date dateOfPlanComplation) {
        this.dateOfPlanComplation = dateOfPlanComplation;
    }
    public Long getTypeComplation() {
        return typeComplation;
    }
    public void setTypeComplation(Long typeComplation) {
        this.typeComplation = typeComplation;
    }
    public String getTypeProcessName() {
        return typeProcessName;
    }
    public void setTypeProcessName(String typeProcessName) {
        this.typeProcessName = typeProcessName;
    }
    public String getStageName() {
        return stageName;
    }
    public void setStageName(String stageName) {
        this.stageName = stageName;
    }
}
