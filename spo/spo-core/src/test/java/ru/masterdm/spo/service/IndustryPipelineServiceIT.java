package ru.masterdm.spo.service;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.md.domain.pipeline.DealCount;
import ru.md.domain.pipeline.DealVolume;

/**
 * Testing IndustryPipeline Service.
 * Created by Ildar Shafigullin on 06.10.2017.
 */
public class IndustryPipelineServiceIT extends BaseIT {

    private final static Logger logger = LoggerFactory.getLogger(IndustryPipelineServiceIT.class);

    @Resource
    private IIndustryPipelineService industryPipelineService;

    @Test
    public void should_fetch_all_dealVolumes() {
        List<DealVolume> list = industryPipelineService.fetchDealVolumes();
        for (DealVolume dv : list) {
            logger.error("DealVolume: {}", dv);
        }
        logger.error("count : {}", list.size());
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void should_fetch_sum_dealVolumes() {
        long sumDealVolumes = industryPipelineService.getSumDealVolumes();
        logger.error("sum DealVolumes in Industries = {}", sumDealVolumes);
        Assert.assertTrue(sumDealVolumes > 0);
    }

    @Test
    public void should_fetch_dealCounts_by_like_orgName() {
        String searchString = null; // "Строительство"
        List<DealCount> list = industryPipelineService.fetchDealCounts(searchString);
        for (DealCount dc : list) {
            logger.debug("DealCount: {}", dc);
        }
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void should_fetch_all_dealCountsSummary() {
        List<DealCount> list = industryPipelineService.fetchDealCountsSummary();
        for (DealCount dc : list) {
            logger.debug("DealCount: {}", dc);
        }
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void should_present_deal_counts_summary() {
        String searchString = ""; // null; // "Строительство"
        boolean isPresentResult = industryPipelineService.isPresentDealCounts(searchString);
        logger.debug("for searchString = '{}'", searchString);
        logger.debug("isPresentResult = {}", isPresentResult);
        Assert.assertTrue(isPresentResult);
    }

    @Test
    public void should_fetch_searched_companies_dealCounts() {
        String searchString = ""; // null; // "Единый"
        List<DealCount> list = industryPipelineService.fetchCompaniesDealCounts(searchString);
        for (DealCount dc : list) {
            logger.debug("DealCount: {}", dc);
        }
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void should_fetch_searched_industries_dealCounts() {
        String searchString = ""; // null; // "Строительство"
        List<DealCount> list = industryPipelineService.fetchIndustriesDealCounts(searchString);
        for (DealCount dc : list) {
            logger.debug("DealCount: {}", dc);
        }
        Assert.assertTrue(list.size() > 0);
    }

}

