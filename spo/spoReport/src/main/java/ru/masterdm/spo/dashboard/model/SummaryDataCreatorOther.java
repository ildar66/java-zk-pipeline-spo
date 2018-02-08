package ru.masterdm.spo.dashboard.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import ru.md.domain.dashboard.MainReportRow;

import ru.masterdm.spo.list.EDashStatus;
import ru.masterdm.spo.dashboard.PipelineSettings;
import ru.masterdm.spo.dashboard.PipelineVM;
import ru.masterdm.spo.dashboard.domain.SummaryData;

import static ru.masterdm.spo.dashboard.domain.SummaryData._ZERO;

/**
 * Создавальщик данных для taskType = product, waiver, cross-sell
 * @author pmasalov
 */
@SuppressWarnings("Duplicates")
public class SummaryDataCreatorOther extends SummaryDataCreatorAbstract {

    //private static final BigDecimal _HUNDRED = BigDecimal.valueOf(100);

    protected List<SummaryData> l;
    protected PipelineSettings.SummarySelection settings;

    private class SummaryValues {

        protected class StateValues {

            private int count = 0;
            private BigDecimal sumRub = _ZERO;
            private BigDecimal avgSumRub = _ZERO;
            private BigDecimal wavMargin = _ZERO;
            private BigDecimal avgWal = _ZERO;
            private BigDecimal sumLineCount = _ZERO;
            private BigDecimal avgLineCount = _ZERO;
            private BigDecimal sumAvailibleLineVolume = _ZERO;
            private BigDecimal avgPeriodMonth = _ZERO;

            protected void setDatas(MainReportRow row) {
                count = row.getCountAll();
                if (row.getSumRub() != null) sumRub = row.getSumRub();
                if (row.getAvgSumRub() != null) avgSumRub = row.getAvgSumRub();
                if (row.getWavMargin() != null) wavMargin = row.getWavMargin();
                if (row.getAvgWal() != null) avgWal = row.getAvgWal();
                if (row.getSumLineCount() != null) sumLineCount = row.getSumLineCount();
                if (row.getAvgLineCount() != null) avgLineCount = row.getAvgLineCount();
                if (row.getSumAvailibleLineVolume() != null) sumAvailibleLineVolume = row.getSumAvailibleLineVolume();
                if (row.getAvgPeriodMonth() != null) avgPeriodMonth = row.getAvgPeriodMonth();
            }
        }

        protected StateValues _new = new StateValues(),
                fix = new StateValues(),
                selected = new StateValues(),
                lost = new StateValues();
/*
        private BigDecimal avgSumRub = _ZERO; // Средняя сумма сделок, млн. руб.
        private BigDecimal sumRubNew = _ZERO; // Новые, млн. руб./ шт.
        private BigDecimal sumRubContract = _ZERO; // Заключенные, , млн. руб./ шт.
        private BigDecimal sumRubSelect = _ZERO; // Выборка, млн. руб./ шт. – только для Сделки, нет для Измененые и вейверы, Кросс селлы
        private BigDecimal sumRubLost = _ZERO; // Потерянные, млн. руб./ шт.
        private BigDecimal avgPeriodMonth = _ZERO; // Средний срок, мес.
        private BigDecimal wavMargin = _ZERO; // Средняя маржа (NIM), %
        private BigDecimal sumProfit = _ZERO; // Ожидаемый доход, млн. руб.
        private BigDecimal avgWal = _ZERO; // Средневзвешенный срок сделки (WAL), мес.
        private BigDecimal sumLineCount = _ZERO; // Объем утилизации, млн. руб./ шт./% [1]
        private BigDecimal avgLineCount = _ZERO; // Средняя сумма выборки, млн. руб.
        private BigDecimal sumAvailibleLineVolume = _ZERO; // Объем линии, доступный для выборки, млн. руб.
        private BigDecimal sumRubProb = _ZERO; // Сумма сделки с учетом вероятности, млн. руб.
        private BigDecimal avgWeeks = _ZERO; // Среднее кол-во недель в пайплайне

        int newCount = 0;
        int contractCount = 0;
        int selectCount = 0;
        int lostCount = 0;
        int totalCount = 0;

        int stateCount = 0;*/

        void processState(MainReportRow row) {
            /*int idStatus = row.getIdStatus().intValue();
            stateCount++;

            //if (row.getSumRub() != null) sumRub = sumRub.add(row.getSumRub());
            // суммы периода распределённые по состояниям
            totalCount += row.getCountAll();
            if (idStatus == EDashStatus.PRODUCT_NEW.getId() || idStatus == EDashStatus.CROSS_NEW.getId() || idStatus == EDashStatus.WAIVER_NEW
                    .getId()) { // новые
                newCount = row.getCountAll();
                if (row.getSumRub() != null) sumRubNew = row.getSumRub();

            } else if (idStatus == EDashStatus.CROSS_SELL_ACCEPT.getId() || idStatus == EDashStatus.PRODUCT_FIX.getId()
                    || idStatus == EDashStatus.WAIVER_FIX.getId()) { //заключенные
                contractCount = row.getCountAll();
                if (row.getSumRub() != null) sumRubContract = row.getSumRub();

            } else if (idStatus == EDashStatus.CROSS_SELL_LOST.getId() || idStatus == EDashStatus.PRODUCT_LOST.getId()
                    || idStatus == EDashStatus.WAIVER_LOST.getId()) { // потерянные
                lostCount = row.getCountAll();
                if (row.getSumRub() != null) sumRubLost = row.getSumRub();

            } else if (idStatus == EDashStatus.PRODUCT_TRANCE.getId()) { // выборка
                selectCount = row.getCountAll();
                if (row.getSumRub() != null) sumRubSelect = row.getSumRub();
                getAvgSumRub
            }
            // прочие показатели периода по всем состояниям
            // учитываются только показатели для сделок "в работе" - в состоянии новые для сделок вэйверов и кроссселов
            if (idStatus == EDashStatus.PRODUCT_NEW.getId() || idStatus == EDashStatus.CROSS_NEW.getId() || idStatus == EDashStatus.WAIVER_NEW
                    .getId()) {
                if (row.getAvgSumRub() != null) avgSumRub = avgSumRub.add(row.getAvgSumRub());
                if (row.getAvgPeriodMonth() != null) avgPeriodMonth = avgPeriodMonth.add(row.getAvgPeriodMonth());
                if (row.getWavMargin() != null) wavMargin = wavMargin.add(row.getWavMargin());
                if (row.getSumProfit() != null) sumProfit = sumProfit.add(row.getSumProfit());
                if (row.getAvgWal() != null) avgWal = avgWal.add(row.getAvgWal());
                if (row.getSumLineCount() != null) sumLineCount = sumLineCount.add(row.getSumLineCount());
                if (row.getAvgLineCount() != null) avgLineCount = avgLineCount.add(row.getAvgLineCount());
                if (row.getSumAvailibleLineVolume() != null) sumAvailibleLineVolume = sumAvailibleLineVolume.add(row.getSumAvailibleLineVolume());
                if (row.getSumRubProb() != null) sumRubProb = sumRubProb.add(row.getSumRubProb());
                if (row.getAvgWeeks() != null) avgWeeks = avgWeeks.add(row.getAvgWeeks());
            }*/

            int idStatus = row.getIdStatus().intValue();
            if (idStatus == EDashStatus.PRODUCT_NEW.getId() || idStatus == EDashStatus.CROSS_NEW.getId() || idStatus == EDashStatus.WAIVER_NEW
                    .getId()) {  // новые
                _new.setDatas(row);
            } else if (idStatus == EDashStatus.CROSS_SELL_LOST.getId() || idStatus == EDashStatus.PRODUCT_LOST.getId()
                    || idStatus == EDashStatus.WAIVER_LOST.getId()) { // потерянные
                lost.setDatas(row);
            } else if (idStatus == EDashStatus.CROSS_SELL_ACCEPT.getId() || idStatus == EDashStatus.PRODUCT_FIX.getId()
                    || idStatus == EDashStatus.WAIVER_FIX.getId()) { // заключенные
                fix.setDatas(row);
            } else if (idStatus == EDashStatus.PRODUCT_TRANCE.getId()) { // выборка (выигранные сделки)
                selected.setDatas(row);
            }

        }

        /*void postProcess() {
            if (stateCount > 1) {
                BigDecimal stateCountBig = BigDecimal.valueOf(stateCount);
                // make average
                avgSumRub = avgSumRub.divide(stateCountBig, 2, RoundingMode.HALF_UP);
                avgPeriodMonth = avgPeriodMonth.divide(stateCountBig, 2, RoundingMode.HALF_UP);
                wavMargin = wavMargin.divide(stateCountBig, 2, RoundingMode.HALF_UP);
                avgWal = avgWal.divide(stateCountBig, 2, RoundingMode.HALF_UP);
                avgLineCount = avgLineCount.divide(stateCountBig, 2, RoundingMode.HALF_UP);
                avgWeeks = avgWeeks.divide(stateCountBig, 2, RoundingMode.HALF_UP);
            }
            if (sumLineCount.compareTo(_ZERO) != 0 && sumRub.compareTo(_ZERO) != 0) {
                sumLineCount = sumLineCount.divide(sumRub, 2, RoundingMode.HALF_UP).multiply(_HUNDRED);
            }
        }*/
    }

    ;

    public SummaryDataCreatorOther(PipelineVM pipelineVm) {
        super(pipelineVm);
    }

    protected void add(SummaryFigureOther figure, BigDecimal mainValue, BigDecimal mainValueCompare, Integer count, Integer countCompare) {
        l.add(new SummaryData(settings.isSelected(figure), figure, mainValue, mainValueCompare, count, countCompare));
    }

    protected void add(SummaryFigureOther figure, BigDecimal mainValue, BigDecimal mainValueCompare) {
        l.add(new SummaryData(settings.isSelected(figure), figure, mainValue, mainValueCompare));
    }

    @Override
    public List<SummaryData> createModel() {
        // instant values
        l = new ArrayList<SummaryData>();
        settings = getPipelineVM().getSettings().getSummarySelection();
        // local values
        List<MainReportRow> rows = getPipelineVM().getGeneralRows();
        List<MainReportRow> compareRows = getPipelineVM().getGeneralRowsCompare();
        SummaryValues mainValues = new SummaryValues(), compareValues = new SummaryValues();

        for (MainReportRow row : rows) {
            int idStatus = row.getIdStatus().intValue();
            MainReportRow compare = findByIdStatus(compareRows, idStatus);

            // накопление данных отчётного периода
            mainValues.processState(row);
            // накопление данных сравнительного периода
            if (compare != null) {
                compareValues.processState(compare);
            }
        }

        //mainValues.postProcess();
        //compareValues.postProcess();

        add(SummaryFigureOther.SUM_RUB_NEW, mainValues._new.sumRub, compareValues._new.sumRub, mainValues._new.count, compareValues._new.count);
        add(SummaryFigureOther.SUM_RUB_FIX, mainValues.fix.sumRub, compareValues.fix.sumRub, mainValues.fix.count, compareValues.fix.count);
        add(SummaryFigureOther.AVG_SUM_RUB_SELECTED, mainValues.selected.avgSumRub, compareValues.selected.avgSumRub);
        add(SummaryFigureOther.SUM_RUB_LOST, mainValues.lost.sumRub, compareValues.lost.sumRub, mainValues.lost.count, compareValues.lost.count);
        add(SummaryFigureOther.WAV_MARGIN_NEW, mainValues._new.wavMargin, compareValues._new.wavMargin);
        add(SummaryFigureOther.AVG_WAL_NEW, mainValues._new.avgWal, compareValues._new.avgWal);
        add(SummaryFigureOther.WAV_MARGIN_FIX, mainValues.fix.wavMargin, compareValues.fix.wavMargin);

        add(SummaryFigureOther.SUM_RUB_SELECTED, mainValues.selected.sumRub, compareValues.selected.sumRub, mainValues.selected.count,
            compareValues.selected.count);
        add(SummaryFigureOther.AVG_SUM_RUB_NEW, mainValues._new.avgSumRub, compareValues._new.avgSumRub);
        add(SummaryFigureOther.AVG_SUM_RUB_FIX, mainValues.fix.avgSumRub, compareValues.fix.avgSumRub);
        add(SummaryFigureOther.AVG_SUM_RUB_LOST, mainValues.lost.avgSumRub, compareValues.lost.avgSumRub);
        add(SummaryFigureOther.AVG_PERIOD_MONTH_NEW, mainValues._new.avgPeriodMonth, compareValues._new.avgPeriodMonth);
        add(SummaryFigureOther.AVG_PERIOD_MONTH_FIX, mainValues.fix.avgPeriodMonth, compareValues.fix.avgPeriodMonth);
        add(SummaryFigureOther.AVG_PERIOD_MONTH_SELECTED, mainValues.selected.avgPeriodMonth, compareValues.selected.avgPeriodMonth);
        add(SummaryFigureOther.AVG_PERIOD_MONTH_LOST, mainValues.lost.avgPeriodMonth, compareValues.lost.avgPeriodMonth);
        add(SummaryFigureOther.WAV_MARGIN_SELECTED, mainValues.selected.wavMargin, compareValues.selected.wavMargin);
        add(SummaryFigureOther.WAV_MARGIN_LOST, mainValues.lost.wavMargin, compareValues.lost.wavMargin);
        add(SummaryFigureOther.AVG_WAL_FIX, mainValues.fix.avgWal, compareValues.fix.avgWal);
        add(SummaryFigureOther.AVG_WAL_SELECTED,mainValues.selected.avgWal, compareValues.selected.avgWal);
        add(SummaryFigureOther.AVG_WAL_LOST, mainValues.lost.avgWal, compareValues.lost.avgWal);
        add(SummaryFigureOther.SUM_LINE_COUNT_FIX, mainValues.fix.sumLineCount, compareValues.fix.sumLineCount);
        add(SummaryFigureOther.AVG_LINE_COUNT_SELECTED, mainValues.selected.avgLineCount, compareValues.selected.avgLineCount);
        add(SummaryFigureOther.SUM_AVAILIBLE_LINE_VOLUME_FIX, mainValues.fix.sumAvailibleLineVolume, compareValues.selected.sumAvailibleLineVolume);

        /*l.add(new SummaryData(settings.isSelected(SummaryFigureOther.DEAL_VOLUME_NEW), SummaryFigureOther.DEAL_VOLUME_NEW,
                              mainValues.sumRubNew, compareValues.sumRubNew,
                              mainValues.newCount, compareValues.newCount));
        l.add(new SummaryData(settings.isSelected(SummaryFigureOther.DEAL_VOLUME_CONTRACT), SummaryFigureOther.DEAL_VOLUME_CONTRACT,
                              mainValues.sumRubContract, compareValues.sumRubContract,
                              mainValues.contractCount, compareValues.contractCount));
        if (ETaskType.PRODUCT.isEqual(getPipelineVM().getTaskType())) {
            l.add(new SummaryData(settings.isSelected(SummaryFigureOther.DEAL_VOLUME_SELECTED), SummaryFigureOther.DEAL_VOLUME_SELECTED,
                                  mainValues.sumRubSelect, compareValues.sumRubSelect,
                                  mainValues.selectCount, compareValues.selectCount));
        }
        l.add(new SummaryData(settings.isSelected(SummaryFigureOther.DEAL_VOLUME_LOST), SummaryFigureOther.DEAL_VOLUME_LOST,
                              mainValues.sumRubLost, compareValues.sumRubLost,
                              mainValues.lostCount, compareValues.lostCount));
        l.add(new SummaryData(settings.isSelected(SummaryFigureOther.WAV_MARGIN), SummaryFigureOther.WAV_MARGIN, mainValues.wavMargin,
                              compareValues.wavMargin));

        // non default
        l.add(new SummaryData(settings.isSelected(SummaryFigureOther.DEAL_AVG_AMOUNT), SummaryFigureOther.DEAL_AVG_AMOUNT, mainValues.avgSumRub,
                              compareValues.avgSumRub));
        l.add(new SummaryData(settings.isSelected(SummaryFigureOther.AVG_PERIOD), SummaryFigureOther.AVG_PERIOD, mainValues.avgPeriodMonth,
                              compareValues.avgPeriodMonth));
        l.add(new SummaryData(settings.isSelected(SummaryFigureOther.PROFIT), SummaryFigureOther.PROFIT, mainValues.sumProfit,
                              compareValues.sumProfit));
        l.add(new SummaryData(settings.isSelected(SummaryFigureOther.WAL), SummaryFigureOther.WAL, mainValues.avgWal, compareValues.avgWal));
        l.add(new SummaryData(settings.isSelected(SummaryFigureOther.LINE_COUNT), SummaryFigureOther.LINE_COUNT, mainValues.sumLineCount,
                              compareValues.sumLineCount));
        l.add(new SummaryData(settings.isSelected(SummaryFigureOther.AVG_LINE_COUNT), SummaryFigureOther.AVG_LINE_COUNT, mainValues.avgLineCount,
                              compareValues.avgLineCount));
        l.add(new SummaryData(settings.isSelected(SummaryFigureOther.AVAILIBLE_LINE_VOLUME), SummaryFigureOther.AVAILIBLE_LINE_VOLUME,
                              mainValues.sumAvailibleLineVolume, compareValues.sumAvailibleLineVolume));
        l.add(new SummaryData(settings.isSelected(SummaryFigureOther.DEAL_AMOUNT_PROB), SummaryFigureOther.DEAL_AMOUNT_PROB, mainValues.sumRubProb,
                              compareValues.sumRubProb));
        l.add(new SummaryData(settings.isSelected(SummaryFigureOther.AVG_WEEKS), SummaryFigureOther.AVG_WEEKS, mainValues.avgWeeks,
                              compareValues.avgWeeks));*/
        return l;
    }

}
