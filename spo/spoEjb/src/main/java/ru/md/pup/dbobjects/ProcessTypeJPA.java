package ru.md.pup.dbobjects;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import ru.md.spo.dbobjects.SpoRouteVersionJPA;
import ru.md.spo.dbobjects.StandardPeriodVersionJPA;

/**
 * JPA object "Тип процесса"
 *
 */
@Entity
@Table(name="TYPE_PROCESS")
public class ProcessTypeJPA implements Serializable {
	@Id
	@Column(name="ID_TYPE_PROCESS")
	private Long idTypeProcess;

	@Column(name="DESCRIPTION_PROCESS")
	private String descriptionProcess;
	
	@OneToMany(mappedBy = "processType", fetch = FetchType.LAZY) @OrderBy(value="date desc")
	private Set<StandardPeriodVersionJPA> standardPeriodVersions;
	
	@OneToMany(mappedBy = "processType", fetch = FetchType.LAZY) @OrderBy(value="date desc")
    private Set<SpoRouteVersionJPA> spoRouteVersion;
	
	@OneToMany(mappedBy = "processType", fetch = FetchType.LAZY)
    private List<VariableJPA> variables;

	private static final long serialVersionUID = 1L;

	/**
	 * Проверяет, что этот БП по филиалам и с ним работают через портал.
	 */
	public boolean isPortalProcess(){
		for(VariableJPA v : variables){
			if(v.getName().equals("documents"))
				return true;
		}
		return false;
	}
	public String toString(){
	    return descriptionProcess;
	}
	/**
	 * Конструктор JPA object "Тип процесса"
	 */
	public ProcessTypeJPA() {
		super();
	}

	public ProcessTypeJPA(Long idTypeProcess) {
        super();
        this.idTypeProcess = idTypeProcess;
    }

    /**
	 * ID типа процесса
	 * @return
	 */
	public Long getIdTypeProcess() {
		return this.idTypeProcess;
	}

	/**
	 * ID типа процесса
	 * @param idTypeProcess
	 */
	public void setIdTypeProcess(Long idTypeProcess) {
		this.idTypeProcess = idTypeProcess;
	}

	/**
	 * описание типа процесса
	 * @return
	 */
	public String getDescriptionProcess() {
		return this.descriptionProcess==null?"":this.descriptionProcess;
	}

	/**
	 * описание типа процесса
	 * @param descriptionProcess
	 */
	public void setDescriptionProcess(String descriptionProcess) {
		this.descriptionProcess = descriptionProcess;
	}

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + (int) (idTypeProcess ^ (idTypeProcess >>> 32));
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProcessTypeJPA other = (ProcessTypeJPA) obj;
        return this.idTypeProcess.equals(other.idTypeProcess);
    }
	/**
	 * @return Версия нормативных сроков
	 */
	public Set<StandardPeriodVersionJPA> getStandardPeriodVersions() {
		return standardPeriodVersions;
	}
	/**
	 * @return актуальная версия нормативных сроков
	 */
	public StandardPeriodVersionJPA getLastStandardPeriodVersion() {
		Set<StandardPeriodVersionJPA> set = getStandardPeriodVersions();
		if (set.size() > 0)
			return standardPeriodVersions.iterator().next();
		return null;
	}
	/**
	 * @param standardPeriodVersions Версия нормативных сроков
	 */
	public void setStandardPeriodVersions(
			Set<StandardPeriodVersionJPA> standardPeriodVersions) {
		this.standardPeriodVersions = standardPeriodVersions;
	}
	public Set<SpoRouteVersionJPA> getSpoRouteVersion() {
		return spoRouteVersion;
	}
	public void setSpoRouteVersion(Set<SpoRouteVersionJPA> spoRouteVersion) {
		this.spoRouteVersion = spoRouteVersion;
	}
    
}
