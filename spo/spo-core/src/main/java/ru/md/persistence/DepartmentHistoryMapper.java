package ru.md.persistence;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ru.md.domain.*;

/**
 * Выводит историю инициирующих подразделений сделки
 * @author Sergey Lysenkov
 *
 */
@Transactional(propagation = Propagation.SUPPORTS)
public interface DepartmentHistoryMapper {
	
	Long getDepartmentHistoryCount(@Param("filter") Map<String,Object> filter);
	List<DepartmentHistory> getDepartmentHistory(@Param("filter") Map<String,Object> filter);
	List<DepartmentHistory> getDepartmentHistoryPage(@Param("filter") Map<String,Object> filter, @Param("start") int start, @Param("count") int count);

	void setDepartmentHistory(@Param("idmdtask") Integer idmdtask,
						 	  @Param("olddepartment") Integer olddepartment,
						 	  @Param("newdepartment") Integer newdepartment,
						 	  @Param("iduser") Integer iduser,
						 	  @Param("date") Date date);
}
