package ru.md.domain.dict;

/**
 * Справочник размерностей периода срока погашения.
 * ВАЖНО! Позиции в enum принципиальны!
 * @author Sergey Valiev
 */
public enum PeriodDimension {
    DAYS("дн.", 1L),
    MONTH("мес.", 30L),
    YEARS("г./лет", 12L);
    
    private String value;
    private Long coeff;
    
    /**
     * Конструктор.
     * @param value значение периода
     * @param coeff коэффициент пересчета в дни относительно предыдущего значения
     */
    private PeriodDimension(String value, Long coeff) {
        this.value = value;
        this.coeff = coeff;
    }

    /**
     * Возвращает значение периода.
     * @return значение периода
     */
    public String getValue() {
        return value;
    }

    /**
     * Возвращает коэффициент пересчета в дни относительно предыдущего значения.
     * @return коэффициент пересчета в дни относительно предыдущего значения
     */
    public Long getCoeff() {
        return coeff;
    }

    /**
     * Поиск значения по строке.
     * @param value строка
     * @return значение списка
     */
    public static PeriodDimension find(String value) {
        for (PeriodDimension periodDimension : PeriodDimension.values()) {
            if (periodDimension.getValue().equalsIgnoreCase(value)) {
                return periodDimension;
            }
        }
        return null;
    }
    
    /**
     * Поиск значения по порядковому номеру.
     * @param ordinal порядковый номер
     * @return значение списка
     */
    public static PeriodDimension find(int ordinal) {
        for (PeriodDimension periodDimension : PeriodDimension.values()) {
            if (periodDimension.ordinal() == ordinal) {
                return periodDimension;
            }
        }
        return null;
    }
}
