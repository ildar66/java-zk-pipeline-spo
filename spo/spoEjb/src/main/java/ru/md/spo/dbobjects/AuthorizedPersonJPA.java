package ru.md.spo.dbobjects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import ru.md.pup.dbobjects.DepartmentJPA;
import ru.md.pup.dbobjects.ProcessTypeJPA;

/**
 * Уполномоченное лицо.
 * @author Andrey Pavlenko
 *
 */
@Entity
@Table(name = "authorized_person")
public class AuthorizedPersonJPA {
    @Id
    @Column(name = "id_authorized_person")
    @SequenceGenerator(name = "AuthorizedPersonSequenceGenerator", sequenceName = "authorized_person_seq", allocationSize = 1)
    @GeneratedValue(generator = "AuthorizedPersonSequenceGenerator", strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @Column
    private String fullName;

    @ManyToOne @JoinColumn(name="id_department")
    private DepartmentJPA department;

    @Column
    private String position;

    @ManyToOne @JoinColumn(name="ID_TYPE_PROCESS")
    private ProcessTypeJPA processType;

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AuthorizedPersonJPA [id=" + id + ", fullName=" + fullName
				+ ", processType=" + processType + "]";
	}
	public String getDisplayName() {
		String name = fullName + " (" + position;
		if(department!=null) name += ", " + department.getShortName();
		name += ")";
		return name;
	}
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getDepartmentName() {
		return department==null?"":department.getShortName();
	}
	public DepartmentJPA getDepartment() {
		return department;
	}

	public void setDepartment(DepartmentJPA department) {
		this.department = department;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public ProcessTypeJPA getProcessType() {
		return processType;
	}

	public void setProcessType(ProcessTypeJPA processType) {
		this.processType = processType;
	}
    
    
}
