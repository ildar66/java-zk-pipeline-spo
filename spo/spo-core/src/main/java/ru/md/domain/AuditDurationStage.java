package ru.md.domain;

import ru.masterdm.spo.utils.Formatter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * данные для отчета Аудит прохождения заявки.
 * @author Andrey Pavlenko
 */
public class AuditDurationStage {
	private Long mdtaskNumber;//Номер заявки
	private String stageName;//Название этапа
	private String stageIter;//Название итерации
	private String ekName;//Контрагент
	private String currency;//Сумма
	private BigDecimal mdtaskSum;
	private String tasktype;//Тип заявки
	private String status;//Статус
	private String initDepName;//Инициирующее подразделение
	private Date stageStart;//Начало
	private Date stageEnd;//Конец
	private Long idMdtask;
	private Long idPupProcess;
	private Long idSpg;

    private String clientManager;//Клиентский менеджер
    private String proguctManager;//Продуктовый менеджер
    private String structurator;//Структуратор
    private String analist;//Кредитный аналитик
    private Long period;//Норматив
    private String cmnt;//Комментарии по стадии
    private String cmntUser;//ФИО разместившего комментарии
    private String cmntDep;//Подразделение разместившего комментарии

	public AuditDurationStage() {
	}
	public AuditDurationStage(AuditDurationStage s) {
		this.mdtaskNumber = s.getMdtaskNumber();
		this.stageName = s.getStageName();
		this.ekName = s.getEkName();
		this.currency = s.getCurrency();
		this.mdtaskSum = s.getMdtaskSum();
		this.tasktype = s.getTasktype();
		this.status = s.getStatus();
		this.initDepName = s.getInitDepName();
		this.stageStart = s.getStageStart();
		this.stageEnd = s.getStageEnd();
		this.idMdtask = s.getIdMdtask();
		this.idPupProcess = s.getIdPupProcess();
		this.idSpg = s.getIdSpg();
	}

	public Long getMdtaskNumber() {
		return mdtaskNumber;
	}

	public void setMdtaskNumber(Long mdtaskNumber) {
		this.mdtaskNumber = mdtaskNumber;
	}

	public String getStageName() {
		return stageName;
	}
	public String getStageNameDisplay() {
		return stageName + Formatter.str(getStageIter());
	}

	public void setStageName(String stageName) {
		this.stageName = stageName;
	}

	public String getEkName() {
		return ekName;
	}

	public void setEkName(String ekName) {
		this.ekName = ekName;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public BigDecimal getMdtaskSum() {
		return mdtaskSum;
	}

	public void setMdtaskSum(BigDecimal mdtaskSum) {
		this.mdtaskSum = mdtaskSum;
	}

	public String getTasktype() {
		return tasktype;
	}
	public String getTasktypeDisplay() {
		return (tasktype == null || tasktype.equalsIgnoreCase("p"))?"Сделка":"Лимит";
	}

	public void setTasktype(String tasktype) {
		this.tasktype = tasktype;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getInitDepName() {
		return initDepName;
	}

	public void setInitDepName(String initDepName) {
		this.initDepName = initDepName;
	}

	public Date getStageStart() {
		return stageStart;
	}

	public void setStageStart(Date stageStart) {
		this.stageStart = stageStart;
	}

	public Date getStageEnd() {
		return stageEnd;
	}

	public void setStageEnd(Date stageEnd) {
		this.stageEnd = stageEnd;
	}

	public Long getIdMdtask() {
		return idMdtask;
	}

	public void setIdMdtask(Long idMdtask) {
		this.idMdtask = idMdtask;
	}

	public Long getIdPupProcess() {
		return idPupProcess;
	}

	public void setIdPupProcess(Long idPupProcess) {
		this.idPupProcess = idPupProcess;
	}

	public Long getIdSpg() {
		return idSpg;
	}

	public void setIdSpg(Long idSpg) {
		this.idSpg = idSpg;
	}

	public String getStageIter() {
		return stageIter;
	}

	public void setStageIter(String stageIter) {
		this.stageIter = stageIter;
	}

    /**
     * Returns .
     * @return
     */
    public String getClientManager() {
        return clientManager;
    }

    /**
     * Sets .
     * @param clientManager
     */
    public void setClientManager(String clientManager) {
        this.clientManager = clientManager;
    }

    /**
     * Returns .
     * @return
     */
    public String getProguctManager() {
        return proguctManager;
    }

    /**
     * Sets .
     * @param proguctManager
     */
    public void setProguctManager(String proguctManager) {
        this.proguctManager = proguctManager;
    }

    /**
     * Returns .
     * @return
     */
    public String getStructurator() {
        return structurator;
    }

    /**
     * Sets .
     * @param structurator
     */
    public void setStructurator(String structurator) {
        this.structurator = structurator;
    }

    /**
     * Returns .
     * @return
     */
    public String getAnalist() {
        return analist;
    }

    /**
     * Sets .
     * @param analist
     */
    public void setAnalist(String analist) {
        this.analist = analist;
    }

    /**
     * Returns .
     * @return
     */
    public Long getPeriod() {
        return period;
    }

    /**
     * Sets .
     * @param period
     */
    public void setPeriod(Long period) {
        this.period = period;
    }

    /**
     * Returns .
     * @return
     */
    public String getCmnt() {
        return Formatter.cut(cmnt, 4000);
    }

    /**
     * Sets .
     * @param cmnt
     */
    public void setCmnt(String cmnt) {
        this.cmnt = cmnt;
    }

    /**
     * Returns .
     * @return
     */
    public String getCmntUser() {
        return Formatter.cut(cmntUser, 4000);
    }

    /**
     * Sets .
     * @param cmntUser
     */
    public void setCmntUser(String cmntUser) {
        this.cmntUser = cmntUser;
    }

    /**
     * Returns .
     * @return
     */
    public String getCmntDep() {
        return Formatter.cut(cmntDep, 200);
    }

    /**
     * Sets .
     * @param cmntDep
     */
    public void setCmntDep(String cmntDep) {
        this.cmntDep = cmntDep;
    }
}
