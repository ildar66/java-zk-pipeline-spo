package ru.md.persistence;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ru.md.domain.*;

/**
 * Выводит место проведения сделки и привязанную клиентскую запись.
 * @author Sergey Lysenkov
 *
 */
@Transactional(propagation = Propagation.SUPPORTS)
public interface PlaceClientRecordMapper {
	
	Long getPlaceKzPageTotalCount(@Param("filter") Map<String,Object> filter);
	List<PlaceClientRecord> getPlaceKzPage(@Param("filter") Map<String,Object> filter, @Param("start") int start, @Param("count") int count);
	List<String> getClientCategories(@Param("filter") Map<String,Object> filter);
}
