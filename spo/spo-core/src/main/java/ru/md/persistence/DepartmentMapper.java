package ru.md.persistence;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ru.md.domain.Department;
import ru.md.domain.DepartmentExt;
import ru.md.domain.PlaceClientRecord;

/**
 * 
 * @author Andrey Pavlenko
 *
 */
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public interface DepartmentMapper {
	Department getById(Long id);
	List<Department> getAll();

	Long getInitialDepartmentCount(@Param("filter") Map<String,Object> filter);
	List<Department> getInitialDepartmentPage(@Param("filter") Map<String,Object> filter, @Param("start") int start, @Param("count") int count);

	/**
	 * Возвращает подразделение для клиентской записи
	 */
	Long getDepId4kz(String kzid);

	List<DepartmentExt> getDepartmentsExtForTree(@Param("rootDepartment") Long rootDepartment);
	Set<Long> getAllowedCommittees(@Param("idDepartment") Integer idDepartment);
}
