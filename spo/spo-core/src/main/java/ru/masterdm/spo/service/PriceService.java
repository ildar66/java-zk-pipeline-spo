package ru.masterdm.spo.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.md.domain.Comission;
import ru.md.domain.MdTask;
import ru.md.persistence.MdTaskMapper;

/**
 * Created by Andrey Pavlenko on 16.01.2017.
 */
@Service
public class PriceService implements IPriceService {
    @Autowired
    private MdTaskMapper mdTaskMapper;

    @Override
    public BigDecimal getComissionZaVidSum(Long mdtaskid) {
        BigDecimal comissionSum = BigDecimal.ZERO;
        MdTask task = mdTaskMapper.getPipelineWithinMdTask(mdtaskid);
        for (Comission c : task.getComissions()) {
            comissionSum = comissionSum.add(c.getAnnualValue());
        }
        return comissionSum;
    }

    @Override
    public void setCloseProbabilityByStatus(Long mdTaskId, String status) {
        MdTask mdTask = mdTaskMapper.getPipelineWithinMdTask(mdTaskId);
        if (mdTask.getPipeline().isStatusManual())
            return;
        mdTaskMapper.setCloseProbabilityByStatusName(mdTaskId, status);
    }
}
