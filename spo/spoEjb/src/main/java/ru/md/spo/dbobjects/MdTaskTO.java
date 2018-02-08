package ru.md.spo.dbobjects;

import com.vtb.domain.Task;
import ru.md.domain.MdTask;

/**
 * Заявка, полученная разными мапперами.
 * Created by Andrey Pavlenko on 17.10.2016.
 */
public class MdTaskTO {
    public Long id;
    public TaskJPA jpa;
    public Task jdbc;
    public MdTask mybatis;

    public MdTaskTO(Long mdtaskid, TaskJPA jpa, Task jdbc, MdTask mybatis) {
        this.id=mdtaskid;
        this.jpa = jpa;
        this.jdbc = jdbc;
        this.mybatis = mybatis;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MdTaskTO)) return false;

        MdTaskTO mdTaskTO = (MdTaskTO) o;

        if (!id.equals(mdTaskTO.id)) return false;
        if (jdbc != null ? !jdbc.equals(mdTaskTO.jdbc) : mdTaskTO.jdbc != null) return false;
        if (jpa != null ? !jpa.equals(mdTaskTO.jpa) : mdTaskTO.jpa != null) return false;
        if (mybatis != null ? !mybatis.equals(mdTaskTO.mybatis) : mdTaskTO.mybatis != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (jpa != null ? jpa.hashCode() : 0);
        result = 31 * result + (jdbc != null ? jdbc.hashCode() : 0);
        result = 31 * result + (mybatis != null ? mybatis.hashCode() : 0);
        return result;
    }
}
