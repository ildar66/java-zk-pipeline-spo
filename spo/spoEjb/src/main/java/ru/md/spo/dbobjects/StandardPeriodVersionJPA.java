package ru.md.spo.dbobjects;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import ru.md.pup.dbobjects.ProcessTypeJPA;

/**
 * Версия нормативных сроков. 
 * @author Andrey Pavlenko
 */
@Entity
@Table(name = "standard_period_version")
public class StandardPeriodVersionJPA implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "ID_SPV")
    @SequenceGenerator(name = "StandardPeriodVersionSequenceGenerator", sequenceName = "standard_period_version_seq", allocationSize = 1)
    @GeneratedValue(generator = "StandardPeriodVersionSequenceGenerator", strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="ID_TYPE_PROCESS")
    private ProcessTypeJPA processType;

    @Column(name="date_version") 
    private Date date;
    
    @OneToMany(mappedBy = "version", fetch = FetchType.LAZY)  @OrderBy(value="id")
    private List<StandardPeriodGroupJPA> standardPeriodGroups;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ProcessTypeJPA getProcessType() {
		return processType;
	}

	public void setProcessType(ProcessTypeJPA processType) {
		this.processType = processType;
	}

	public String getFormattedDate() {
		return com.vtb.util.Formatter.formatDateTime(date);
	}
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public List<StandardPeriodGroupJPA> getStandardPeriodGroupList() {
	    return standardPeriodGroups;
	}
	
	public List<StandardPeriodGroupJPA> getStandardPeriodGroups() {
		return standardPeriodGroups;
	}

	public void setStandardPeriodGroups(
			List<StandardPeriodGroupJPA> standardPeriodGroups) {
		this.standardPeriodGroups = standardPeriodGroups;
	}

	@Override
	public String toString() {
		return "№ "+getId()+" (дата "+getFormattedDate()+")";
	}
    
}
