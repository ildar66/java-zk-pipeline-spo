package ru.md.spo.dbobjects;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import ru.md.pup.dbobjects.StageJPA;

/**
 * Этап нормативных сроков. 
 * @author Andrey Pavlenko
 */
@Entity
@Table(name = "standard_period_group")
public class StandardPeriodGroupJPA implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "ID_SPG")
    @SequenceGenerator(name = "StandardPeriodGroupSequenceGenerator", sequenceName = "standard_period_group_seq", allocationSize = 1)
    @GeneratedValue(generator = "StandardPeriodGroupSequenceGenerator", strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="ID_SPV")
    private StandardPeriodVersionJPA version;
    
    private String name;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "r_stage_standardgroup", joinColumns = @JoinColumn(name = "id_spg"), inverseJoinColumns = @JoinColumn(name = "id_stage"))
    private List<StageJPA> stages;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "R_DICISIONSTAGE_STANDARDGROUP", joinColumns = @JoinColumn(name = "id_spg"), inverseJoinColumns = @JoinColumn(name = "id_stage"))
    private List<StageJPA> decisionStages;
    
    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    private Set<StandardPeriodValueJPA> values;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public StandardPeriodVersionJPA getVersion() {
		return version;
	}

	public void setVersion(StandardPeriodVersionJPA version) {
		this.version = version;
	}

	public String getName() {
		return name==null?"":name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<StageJPA> getStages() {
		return stages;
	}

	public void setStages(List<StageJPA> stages) {
		this.stages = stages;
	}

	public Set<StandardPeriodValueJPA> getValues() {
		return values;
	}

	public void setValues(Set<StandardPeriodValueJPA> values) {
		this.values = values;
	}

	@Override
	public String toString() {
		return "StandardPeriodGroupJPA [id=" + id + ", name=" + name + "]";
	}

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
        StandardPeriodGroupJPA other = (StandardPeriodGroupJPA) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    /**проверяет, что этап содержит операции- экспертизы*/
    public boolean isExpertGroup(){
        for(StageJPA stage : stages){
            if(stage.getDescription().startsWith("Проведение экспертизы "))
                return true;
            if(stage.getDescription().startsWith("Экспертиза "))
            	return true;
            if(stage.getDescription().startsWith("Контроль технической исполнимости"))
                return true;
        }
        return false;
    }

	public List<StageJPA> getDecisionStages() {
		return decisionStages;
	}

	public void setDecisionStages(List<StageJPA> decisionStages) {
		this.decisionStages = decisionStages;
	}
    
}
