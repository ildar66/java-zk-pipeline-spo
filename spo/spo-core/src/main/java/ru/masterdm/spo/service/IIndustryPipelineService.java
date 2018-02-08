package ru.masterdm.spo.service;

import java.util.List;

import ru.md.domain.pipeline.DealCount;
import ru.md.domain.pipeline.DealVolume;

/**
 * Industry PipelineService.
 * Created by Ildar Shafigullin on 05.10.2017.
 */
public interface IIndustryPipelineService {

    /**
     * Объем сделок по отраслям.
     * @return Объем сделок.
     */
    List<DealVolume> fetchDealVolumes();

    /**
     * Сумма значений «Объем в млн. руб» по всем отраслям.
     * @return
     */
    long getSumDealVolumes();

    /**
     * Количества и суммы сделок сгруппированным по отраслям и организациям.
     * @param searchString search String.
     * @return лист.
     */
    List<DealCount> fetchDealCounts(String searchString);

    /**
     * Количество и суммы сделок сгруппированным по отраслям.
     * @return лист.
     */
    List<DealCount> fetchDealCountsSummary();

    /**
     * есть ли записи "Количество и суммы сделок".
     * @param searchString
     * @return boolean.
     */
    boolean isPresentDealCounts(String searchString);

    /**
     * @param searchString
     * @return лист кол-во сделок и сумм сгрупированным только по компаниям.
     */
    List<DealCount> fetchCompaniesDealCounts(String searchString);

    /**
     * @param searchString
     * @return лист кол-во сделок и сумм сгрупированным только по отраслям.
     */
    List<DealCount> fetchIndustriesDealCounts(String searchString);
}
