package ru.md.persistence;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ru.md.domain.*;

/**
 * Выводит историю мест проведения сделки
 * @author Sergey Lysenkov
 *
 */
@Transactional(propagation = Propagation.SUPPORTS)
public interface PlaceHistoryMapper {
	
	Long getPlaceHistoryCount(@Param("filter") Map<String,Object> filter);
	List<PlaceHistory> getPlaceHistory(@Param("filter") Map<String,Object> filter);
	List<PlaceHistory> getPlaceHistoryPage(@Param("filter") Map<String,Object> filter, @Param("start") int start, @Param("count") int count);
	void setPlaceHistory(@Param("idmdtask") Integer idmdtask,
						 @Param("oldplace") Integer oldplace,
						 @Param("newplace") Integer newplace,
			 			 @Param("iduser") Integer iduser,
						 @Param("date") Date date);
}
