package ru.masterdm.spo.dashboard;

/**
 * @author pmasalov
 */
public class PipelineConstants {

    public static final int MIN_SUMMARY_TO_SYNC_HEIGHT = 6;

    /** Характеристики (величины) отображаемые на основных диаграмах */
    public static class Characteristics {

        public static final String SUM_RUB = "Сумма";
        //public static final String SUM_PROFIT = "Ожид. доход";
        public static final String COUNT_ALL = "Количество";
        public static final String WAV_MARGIN = "Маржа";
    }

    public enum CharacteristicsE {
        SUM_RUB, COUNT_ALL, WAV_MARGIN;
    }

    /** Периоды за которые действуют отображения на диаграмах */
    public static class Period {

        public static final String REPORT = "отчетный";
        public static final String COMPARE = "сравнительный";
    }

    public static class UnitOfMeasure {

        public static final String MLN_RUB = "млн. "+"\u0584".toUpperCase();
        public static final String MLN_USD = "млн. $";
        public static final String TERM_MONTH = "мес.";
        public static final String PERCENT = "%";
        public static final String PIECE = "шт.";
    }

    @Deprecated
    public static class GlobalCommand {

        public static final String SETUP_CHARTS = "setupCharts";
    }

    public static class CreditDocumentary {

        public static final String ALL = "Все";
        public static final String CREDIT = "Кредитные";
        public static final String DOCUMENTARY = "Документарные";
    }

    public static class CreditDocumentGenitive {

        public static final String ALL = "всех";
        public static final String CREDIT = "кредитных";
        public static final String DOCUMENTARY = "документарных";
    }

    public static class DetailReportAttribute {

        public static final String STATUS_ID = "idStatus";
        public static final String CATEGORY = "category";
        public static final String BRANCH = "branch";
    }

    public static class Formats {

        public static final String NUMBER_FORMAT = "###,###,###,###.##";
        public static final String INT_FORMAT = "### ### ### ###";
        public static final String DATE_FORMAT = "dd.MM.yyyy";
    }

    public static final String DATAQUEUE = "pipelineDataQueue";
    public static final String NOT_DEFINE_COLOR = "rgba(210,210,210,1)";
    public static final String OTHER_COLOR = "rgba(10,41,80,1)";
    public static final String[] PIE_COLORS = {"rgba(30,75,115,1)","rgba(60,100,150,1)","rgba(80,120,150,1)","rgba(100,120,150,1)",
                                               "rgba(90,90,90,1)","rgba(120,120,120,1)","rgba(150,150,150,1)","rgba(190,190,190,1)",
                                               "rgba(60,100,150,1)","rgba(60,120,150,1)","rgba(80,120,150,1)","rgba(100,120,150,1)","rgba(10,65,115,1)",
                                               "rgba(30,75,115,1)","rgba(40,75,115,1)","rgba(50,75,115,1)","rgba(90,90,90,1)","rgba(60,100,150,1)",
                                               "rgba(60,120,150,1)","rgba(80,120,150,1)","rgba(100,120,150,1)","rgba(120,120,120,1)"};
}
