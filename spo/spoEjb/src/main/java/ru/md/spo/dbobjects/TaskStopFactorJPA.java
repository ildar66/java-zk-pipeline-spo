package ru.md.spo.dbobjects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "R_MDTASK_STOPFACTOR")
public class TaskStopFactorJPA {
    private static final long serialVersionUID = 1L;
    @ManyToOne
    @JoinColumn(name = "ID_STOPFACTOR", referencedColumnName = "CODE")
    private StopFactorJPA stopFactor;
    
    @Column(name = "FLAG")
    private String flag;
    
    @Id
    @Column(name = "ID")
    private Long id;
    @ManyToOne 
    @JoinColumn(name="id_mdtask")
    private TaskJPA task;
    
    
    /**
     * Возвращает {@link Long первичный ключь}
     * 
     * @return {@link Long первичный ключь}
     */
    public Long getId() {
        return id;
    }

    /**
     * Устанавливает {@link Long первичный ключь}
     * 
     * @return id {@link Long первичный ключь}
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Возвращает {@link StopFactorJPA описание} причин отказа
     * 
     * @return {@link StopFactorJPA описание} причин отказа
     */
    public StopFactorJPA getStopFactor() {
        return stopFactor;
    }
    
    /**
     * Устанавливает {@link StopFactorJPA описание} причин отказа
     * 
     * @param stopFactor {@link StopFactorJPA описание} причин отказа
     */
    public void setStopFactor(StopFactorJPA stopFactor) {
        this.stopFactor = stopFactor;
    }
    
    /**
     * Возвращает {@link String флаг}
     * 
     * @return {@link String флаг}
     */
    public String getFlag() {
        return flag;
    }
    
    /**
     * Устанавливает {@link String флаг}
     * 
     * @param flag {@link String флаг}
     */
    public void setFlag(String flag) {
        this.flag = flag;
    }
    
    /**
     * Возвращает {@link TaskJPA }
     * 
     * @return
     */

    public TaskJPA getTask() {
        return task;
    }
    
    /**
     * Устанавливает {@link TaskJPA }
     * 
     * @param task
     */
    public void setTask(TaskJPA task) {
        this.task = task;
    }
}
