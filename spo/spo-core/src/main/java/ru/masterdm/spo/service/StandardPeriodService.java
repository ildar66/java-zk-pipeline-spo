package ru.masterdm.spo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.md.domain.MdTask;
import ru.md.persistence.MdTaskMapper;
import ru.md.persistence.ReportMapper;

import java.util.Date;
import java.util.List;

/**
 * Сервис для контроля нормативных сроков.
 * Created by Andrey Pavlenko on 05.05.16.
 */
@Service
public class StandardPeriodService implements IStandardPeriodService {

    @Autowired
    private ReportMapper reportMapper;
    @Autowired
    private MdTaskMapper mdTaskMapper;

    @Override
    public Long getStandardPeriodValue(Long mdtaskid, String groupname, Date iterDate, Date iterEnd) {
        MdTask task = mdTaskMapper.getById(mdtaskid);
        //ищем актуальную версию нормативных сроков для начала итерации
        //если даты нет, то смотрю вообще последнюю версию нормативных сроков
        Long version = reportMapper.getActualSPVersion(task.getIdTypeProcess(), iterDate);
        Long period = reportMapper.getLastStandardPeriodValueChange(groupname, mdtaskid, iterEnd);
        if (period != null)
            return period;
        List<Long> periods = reportMapper.getStandardPeriodValueBySPG(version, groupname);
        if (periods.size() == 1)//значит, период определён однозначно
            return periods.get(0);
        return null;
    }
}