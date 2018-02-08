package ru.md.spo.dbobjects;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import ru.md.pup.dbobjects.DepartmentJPA;

/**
 * маршрут БП.
 * @author Andrey Pavlenko
 */
@Entity
@Table(name = "SPO_ROUTE")
public class SpoRouteJPA implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "SpoRouteSequenceGenerator", sequenceName = "SPO_ROUTE_SEQ", allocationSize = 1)
    @GeneratedValue(generator = "SpoRouteSequenceGenerator", strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="ID_VERSION")
    private SpoRouteVersionJPA version;
    
    private String stageName;
    
    @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="ID_DEF_DEP")
    private DepartmentJPA defaultDepartment;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "R_SPO_ROUTE_DEPARTMENT", joinColumns = @JoinColumn(name = "id_spo_route"), inverseJoinColumns = @JoinColumn(name = "ID_DEPARTMENT"))
    private List<DepartmentJPA> initDepartments;
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SpoRouteVersionJPA getVersion() {
		return version;
	}

	public void setVersion(SpoRouteVersionJPA version) {
		this.version = version;
	}

	public String getStageName() {
		return stageName;
	}

	public void setStageName(String stageName) {
		this.stageName = stageName;
	}

	public DepartmentJPA getDefaultDepartment() {
		return defaultDepartment;
	}

	public void setDefaultDepartment(DepartmentJPA defaultDepartment) {
		this.defaultDepartment = defaultDepartment;
	}

	public List<DepartmentJPA> getInitDepartments() {
		return initDepartments;
	}

	public void setInitDepartments(List<DepartmentJPA> initDepartments) {
		this.initDepartments = initDepartments;
	}

	@Override
	public String toString() {
		return "SpoRouteJPA [id=" + id + ", stageName=" + stageName + "]";
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
        SpoRouteJPA other = (SpoRouteJPA) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
