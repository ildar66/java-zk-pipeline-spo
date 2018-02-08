package ru.md.pup.dbobjects;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "stages")
public class StageJPA implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id @Column(name="id_stage")
    private Long idStage;
    
    @Column(name="description_stage")
    private String description;
    
    @ManyToMany(fetch=FetchType.LAZY) @JoinTable(name="stages_in_role",
            joinColumns = @JoinColumn(name = "id_stage"),
            inverseJoinColumns = @JoinColumn(name = "id_role"))
    private List<RoleJPA> roles;
    
    private Long id_type_process;
    
    private Long active;
    
    public String toString(){
        return "idStage="+idStage+" ("+description+")";
    }
    
    /**
     * @return roles
     */
    public List<RoleJPA> getRoles() {
        return roles;
    }

    /**
     * @param roles roles
     */
    public void setRoles(List<RoleJPA> roles) {
        this.roles = roles;
    }

    /**
     * @return idStage
     */
    public Long getIdStage() {
        return idStage;
    }

    /**
     * @param idStage idStage
     */
    public void setIdStage(Long idStage) {
        this.idStage = idStage;
    }

    /**
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return id_type_process
     */
    public Long getId_type_process() {
        return id_type_process;
    }

    /**
     * @param id_type_process id_type_process
     */
    public void setId_type_process(Long id_type_process) {
        this.id_type_process = id_type_process;
    }

	public boolean isActive() {
		return active!=null && active.longValue()==1;
	}

	public void setActive(Long active) {
		this.active = active;
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((idStage == null) ? 0 : idStage.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        StageJPA other = (StageJPA) obj;
        if (idStage == null) {
            if (other.idStage != null)
                return false;
        } else if (!idStage.equals(other.idStage))
            return false;
        return true;
    }

    /**операция для структуратора или руководителя структуратора*/
    public boolean isStructuratorStage(){
        for (RoleJPA role : this.getRoles()){
            if(role.getNameRole().equals("Структуратор")) return true;
            if(role.getNameRole().equals("Руководитель структуратора")) return true;
            if(role.getNameRole().equals("Структуратор (за МО)")) return true;
            if(role.getNameRole().equals("Руководитель структуратора (за МО)")) return true;
        }
        return false;
    }
    /**операция для мидлофиса*/
    public boolean isMidleOfficeStage(){
        for (RoleJPA role : this.getRoles()){
            if(role.getNameRole().equals("Работник мидл-офиса")) return true;
            if(role.getNameRole().equals("Руководитель мидл-офиса")) return true;
        }
        return false;
    }
}
