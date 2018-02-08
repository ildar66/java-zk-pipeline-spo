package ru.md.domain.dict;

/**
 * Справочник периодов комиссий.
 * @author Sergey Valiev
 */
public enum ComissionPeriod {
    ONCE("Единовременно", null),
    END("В конце срока", null),
    WITH_PERCENT("Одновременно с уплатой %", null),
    FINAL_REPAYMENT("На дату окончательного погашения задолженности", null),
    
    MONTH("Ежемесячно", 12l),
    QUARTER("Ежеквартально", 4l),
    HALF_YEAR("Полугодовых", 2l),
    YEAR("Годовых", 1l);
    
    private String value;
    private Long coeff;
    
    /**
     * Конструктор.
     * @param value значение
     * @param coeff коэффициент для пересчета комиссии в % годовых
     */
    private ComissionPeriod(String value, Long coeff) {
        this.value = value;
        this.coeff = coeff;
    }

    /**
     * Возвращает значение.
     * @return значение
     */
    public String getValue() {
        return value;
    }
    
    /**
     * Возвращает коэффициент для пересчета комиссии в % годовых.
     * @return коэффициент для пересчета комиссии в % годовых
     */
    public Long getCoeff() {
        return coeff;
    }

    /**
     * Поиск значения по строке.
     * @param value строка
     * @return значение списка
     */
    public static ComissionPeriod find(String value) {
        for (ComissionPeriod comissionType : ComissionPeriod.values()) {
            if (comissionType.getValue().equalsIgnoreCase(value)) {
                return comissionType;
            }
        }
        return null;
    }
}
