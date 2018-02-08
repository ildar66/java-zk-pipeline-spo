package ru.masterdm.spo.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import ru.md.domain.pipeline.DealCount;
import ru.md.domain.pipeline.DealVolume;
import ru.md.persistence.IndustryPipelineMapper;

/**
 * Industry PipelineService Implementation.
 * Created by Ildar Shafigullin on 05.10.2017.
 */
@Service("industryPipelineService")
public class IndustryPipelineService implements IIndustryPipelineService {

    @Resource
    private IndustryPipelineMapper industryMapper;

    @Override
    public List<DealVolume> fetchDealVolumes() {
        return industryMapper.fetchDealVolumes();
    }

    @Override
    public long getSumDealVolumes() {
        return industryMapper.getSumDealVolumes();
    }

    @Override
    public List<DealCount> fetchDealCounts(String searchString) {
        return industryMapper.fetchDealCounts(searchString);
    }

    @Override
    public List<DealCount> fetchDealCountsSummary() {
        return industryMapper.fetchDealCountsSummary();
    }

    @Override
    public boolean isPresentDealCounts(String searchString) {
        return industryMapper.getCountDealCountsSummary(searchString) > 0;
    }

    @Override
    public List<DealCount> fetchCompaniesDealCounts(String searchString) {
        return industryMapper.fetchCompaniesDealCounts(searchString);
    }

    @Override
    public List<DealCount> fetchIndustriesDealCounts(String searchString) {
        return industryMapper.fetchIndustriesDealCounts(searchString);
    }

}
