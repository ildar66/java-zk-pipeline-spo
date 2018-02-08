package ru.md.persistence;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import ru.md.domain.PupTask;
import ru.md.domain.dict.CommonDictionary;

/**
 * ПУП маппер.
 * @author Sergey Valiev
 */
public interface PupMapper {

    /**
     * Возвращает список типов процессов доступных для запуска из процесса Pipeline.
     * @param userId идентификатор пользователя
     * @return список типов процессов
     */
    List<CommonDictionary<Long>> getPipelineProcessTypes(@Param("userId") Long userId);
    List<CommonDictionary<Long>> getAllProcessTypes();

    PupTask getPupTask(Long idPupTask);
    String getPUPAttributeValue(@Param("idProcess") Long idProcess, @Param("nameVar") String nameVar);
}
