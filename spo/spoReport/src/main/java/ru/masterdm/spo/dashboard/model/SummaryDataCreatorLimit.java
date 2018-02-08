package ru.masterdm.spo.dashboard.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import ru.md.domain.dashboard.MainReportRow;

import ru.masterdm.spo.dashboard.PipelineSettings;
import ru.masterdm.spo.dashboard.PipelineVM;
import ru.masterdm.spo.dashboard.domain.SummaryData;
import ru.masterdm.spo.list.EDashStatus;

import static ru.masterdm.spo.dashboard.domain.SummaryData._ZERO;

/**
 * @author pmasalov
 */
@SuppressWarnings("Duplicates")
public class SummaryDataCreatorLimit extends SummaryDataCreatorAbstract {

    protected List<SummaryData> l;
    protected PipelineSettings.SummarySelection settings;

    @SuppressWarnings("Duplicates")
    private class SummaryValues {

        protected class StateValues {

            private int count = 0;
            private BigDecimal sumRub = _ZERO;
            private BigDecimal avgPeriodMonth = _ZERO;
            private BigDecimal avgWeeks = _ZERO;
            private BigDecimal avgSumRub = _ZERO;

            private void setDatas(MainReportRow row) {
                count = row.getCountAll();
                if (row.getSumRub() != null) sumRub = row.getSumRub();
                if (row.getAvgPeriodMonth() != null) avgPeriodMonth = row.getAvgPeriodMonth();
                if (row.getAvgWeeks() != null) avgWeeks = row.getAvgWeeks();
                if (row.getAvgSumRub() != null) avgSumRub = row.getAvgSumRub();
            }
        }

        protected StateValues _new = new StateValues(),
                inprogress = new StateValues(),
                accept = new StateValues(),
                lost = new StateValues();


/*
        private BigDecimal sumRub = _ZERO; // Объем лимитов, млн. руб./ шт.
        //private BigDecimal sumRubNew = _ZERO; // Начало работы по заявке, млн. руб./ шт.
        //private BigDecimal sumRubAnalyse = _ZERO; // Анализ и структурирование, млн. руб./ шт.
        //private BigDecimal sumRubExpert = _ZERO; // Проведение экспертиз, млн. руб./ шт.
        private BigDecimal sumRubAproved = _ZERO; // Одобрено, млн. руб./ шт.
        private BigDecimal sumRubReject = _ZERO; // Отказано, млн. руб./ шт.

        private BigDecimal sumPeriodMonth = _ZERO;
        private BigDecimal sumWeeks = _ZERO;
        private BigDecimal avgSumRub = _ZERO; // Средняя сумма лимита, млн. руб.
        private BigDecimal avgPeriodMonth = _ZERO; // Средний срок, мес.
        private BigDecimal avgWeeks = _ZERO; // Среднее кол-во недель в пайплайне

        int newCount = 0;
        int analyseCount = 0;
        int expertCount = 0;
        int aprovedCount = 0;
        int rejectedCount = 0;
        //int totalCount = 0;
        int inWorkCount = 0;

        //int stateCount = 0;*/

        void processState(MainReportRow row) {
            int idStatus = row.getIdStatus().intValue();
            /*//stateCount++;

            // суммы периода распределённые по состояниям
            //totalCount += row.getCountAll();
            if (idStatus == EDashStatus.LIMIT_NEW.getId()) { // Начало работы по заявке
                newCount = row.getCountAll();
                //if (row.getSumRub() != null) sumRubNew = row.getSumRub();

            } else if (idStatus == EDashStatus.LIMIT_INPROGRESS.getId()) { // Анализ и структурирование
                analyseCount = row.getCountAll();
                //if (row.getSumRub() != null) sumRubAnalyse = row.getSumRub();

            } else if (idStatus == EDashStatus.LIMIT_ACCEPT.getId()) { // Одобрено
                aprovedCount = row.getCountAll();
                if (row.getSumRub() != null) sumRubAproved = row.getSumRub();

            } else if (idStatus == EDashStatus.LIMIT_LOST.getId()) { // Отказано
                rejectedCount = row.getCountAll();
                if (row.getSumRub() != null) sumRubReject = row.getSumRub();
            }

            // прочие показатели периода по всем состояниям
            // расчитываются только для статусов "в работе" -  Начало работы + В работе
            if (idStatus == EDashStatus.LIMIT_INPROGRESS.getId() || idStatus == EDashStatus.LIMIT_NEW.getId()) {
                if (row.getSumRub() != null) sumRub = sumRub.add(row.getSumRub());
                if (row.getSumPeriodMonth() != null) sumPeriodMonth = sumPeriodMonth.add(row.getSumPeriodMonth());
                if (row.getSumWeeks() != null) sumWeeks = sumWeeks.add(row.getSumWeeks());
            }*/
            if (idStatus == EDashStatus.LIMIT_NEW.getId()) { // Начало работы по заявке
                _new.setDatas(row);

            } else if (idStatus == EDashStatus.LIMIT_INPROGRESS.getId()) { // Анализ и структурирование
                inprogress.setDatas(row);

            } else if (idStatus == EDashStatus.LIMIT_ACCEPT.getId()) { // Одобрено
                accept.setDatas(row);

            } else if (idStatus == EDashStatus.LIMIT_LOST.getId()) { // Отказано
                lost.setDatas(row);
            }
        }

        /*void postProcess() {
            inWorkCount = newCount + analyseCount + expertCount;
            if (inWorkCount > 1) {
                BigDecimal limitsCount = BigDecimal.valueOf(inWorkCount);
                // make average
                avgSumRub = sumRub.divide(limitsCount, 2, RoundingMode.HALF_UP);
                avgPeriodMonth = sumPeriodMonth.divide(limitsCount, 2, RoundingMode.HALF_UP);
                avgWeeks = sumWeeks.divide(limitsCount, 2, RoundingMode.HALF_UP);
            } else if (inWorkCount == 1) {
                avgSumRub = sumRub;
                avgPeriodMonth = sumPeriodMonth;
                avgWeeks = sumWeeks;
            }
        }*/
    }

    public SummaryDataCreatorLimit(PipelineVM pipelineVm) {
        super(pipelineVm);
    }

    protected void add(SummaryFigureLimit figure, BigDecimal mainValue, BigDecimal mainValueCompare, Integer count, Integer countCompare) {
        l.add(new SummaryData(settings.isSelected(figure), figure, mainValue, mainValueCompare, count, countCompare));
    }

    protected void add(SummaryFigureLimit figure, BigDecimal mainValue, BigDecimal mainValueCompare) {
        l.add(new SummaryData(settings.isSelected(figure), figure, mainValue, mainValueCompare));
    }

    @Override
    public List<SummaryData> createModel() {
        l = new ArrayList<SummaryData>(9);
        List<MainReportRow> rows = getPipelineVM().getGeneralRows();
        List<MainReportRow> compareRows = getPipelineVM().getGeneralRowsCompare();
        SummaryValues mainValues = new SummaryValues(), compareValues = new SummaryValues();

        for (MainReportRow row : rows) {
            int idStatus = row.getIdStatus().intValue();
            MainReportRow compare = findByIdStatus(compareRows, idStatus);

            // накопление данный отчётного периода
            mainValues.processState(row);
            // накопление данных сравнительного периода
            if (compare != null) {
                compareValues.processState(compare);
            }
        }

        //mainValues.postProcess();
        //compareValues.postProcess();

        settings = getPipelineVM().getSettings().getSummarySelection();

        add(SummaryFigureLimit.SUM_RUB_NEW, mainValues._new.sumRub, compareValues._new.sumRub, mainValues._new.count, compareValues._new.count);
        add(SummaryFigureLimit.SUM_RUB_INPROGRESS, mainValues.inprogress.sumRub, compareValues.inprogress.sumRub, mainValues.inprogress.count, compareValues.inprogress.count);
        add(SummaryFigureLimit.SUM_RUB_ACCEPT, mainValues.accept.sumRub, compareValues.accept.sumRub, mainValues.accept.count, compareValues.accept.count);
        add(SummaryFigureLimit.SUM_RUB_LOST, mainValues.lost.sumRub, compareValues.lost.sumRub, mainValues.lost.count, compareValues.lost.count);
        add(SummaryFigureLimit.AVG_PERIOD_MONTH_INPROGRESS, mainValues.inprogress.avgPeriodMonth, compareValues.inprogress.avgPeriodMonth);
        add(SummaryFigureLimit.AVG_WEEKS_INPROGRESS, mainValues.inprogress.avgWeeks, compareValues.inprogress.avgWeeks);
        add(SummaryFigureLimit.AVG_WEEKS_ACCEPT, mainValues.accept.avgWeeks, compareValues.accept.avgWeeks);

        add(SummaryFigureLimit.AVG_SUM_RUB_NEW, mainValues._new.avgSumRub, compareValues._new.avgSumRub);
        add(SummaryFigureLimit.AVG_SUM_RUB_INPROGRESS, mainValues.inprogress.avgSumRub, compareValues.inprogress.avgSumRub);
        add(SummaryFigureLimit.AVG_SUM_RUB_ACCEPT, mainValues.accept.avgSumRub, compareValues.accept.avgSumRub);
        add(SummaryFigureLimit.AVG_SUM_RUB_LOST, mainValues.lost.avgSumRub, compareValues.lost.avgSumRub);
        add(SummaryFigureLimit.AVG_PERIOD_MONTH_NEW, mainValues._new.avgPeriodMonth, compareValues._new.avgPeriodMonth);
        add(SummaryFigureLimit.AVG_PERIOD_MONTH_ACCEPT, mainValues.accept.avgPeriodMonth, compareValues.accept.avgPeriodMonth);
        add(SummaryFigureLimit.AVG_PERIOD_MONTH_LOST, mainValues.lost.avgPeriodMonth, compareValues.lost.avgPeriodMonth);
        add(SummaryFigureLimit.AVG_WEEKS_NEW, mainValues._new.avgWeeks, compareValues._new.avgWeeks);
        add(SummaryFigureLimit.AVG_WEEKS_LOST, mainValues.lost.avgWeeks, compareValues.lost.avgWeeks);

/*        l.add(new SummaryData(settings.isSelected(SummaryFigureLimit.LIMIT_VOLUME), SummaryFigureLimit.LIMIT_VOLUME, mainValues.sumRub,
                              compareValues.sumRub, mainValues.inWorkCount, compareValues.inWorkCount));
        //l.add(new SummaryData(settings.isSelected(SummaryFigureLimit.LIMIT_VOLUME_NEW) , SummaryFigureLimit.LIMIT_VOLUME_NEW, mainValues.sumRubNew, compareValues.sumRubNew, mainValues.newCount,
        //                      compareValues.newCount));
        //l.add(new SummaryData(settings.isSelected(SummaryFigureLimit.LIMIT_VOLUME_ANALYSE), SummaryFigureLimit.LIMIT_VOLUME_ANALYSE, mainValues.sumRubAnalyse, compareValues.sumRubAnalyse,
        //                      mainValues.analyseCount, compareValues.analyseCount));
        //l.add(new SummaryData(settings.isSelected(SummaryFigureLimit.LIMIT_VOLUME_EXPERT), SummaryFigureLimit.LIMIT_VOLUME_EXPERT, mainValues.sumRubExpert, compareValues.sumRubExpert,
        //                      mainValues.expertCount, compareValues.expertCount));
        l.add(new SummaryData(settings.isSelected(SummaryFigureLimit.LIMIT_VOLUME_APROVERD), SummaryFigureLimit.LIMIT_VOLUME_APROVERD,
                              mainValues.sumRubAproved, compareValues.sumRubAproved,
                              mainValues.aprovedCount,
                              compareValues.aprovedCount));
        l.add(new SummaryData(settings.isSelected(SummaryFigureLimit.LIMIT_VOLUME_REJECTED), SummaryFigureLimit.LIMIT_VOLUME_REJECTED,
                              mainValues.sumRubReject, compareValues.sumRubReject,
                              mainValues.rejectedCount,
                              compareValues.rejectedCount));
        l.add(new SummaryData(settings.isSelected(SummaryFigureLimit.LIMIT_AVG_SUM), SummaryFigureLimit.LIMIT_AVG_SUM, mainValues.avgSumRub,
                              compareValues.avgSumRub));
        l.add(new SummaryData(settings.isSelected(SummaryFigureLimit.AVG_PERIOD_MONTH), SummaryFigureLimit.AVG_PERIOD_MONTH,
                              mainValues.avgPeriodMonth, compareValues.avgPeriodMonth));
        l.add(new SummaryData(settings.isSelected(SummaryFigureLimit.AVG_WEEKS), SummaryFigureLimit.AVG_WEEKS, mainValues.avgWeeks,
                              compareValues.avgWeeks));*/

        return l;
    }
}
