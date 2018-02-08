package ru.md.domain.dict;

/**
 * Справочник типов коэффициентов pipeline.
 * @author Sergey Valiev
 */
public enum PipelineCoeffType {
    
    /**
     * Коэффициент Типа Сделки.
     */
    PRODUCT_TYPE_FACTOR(0L), 
    /**
     * Коэффициент по Сроку Погашения.
     */
    PERIOD_FACTOR(1L);
    
    private Long value;
    
    /**
     * Конструктор.
     * @param value значение
     */
    private PipelineCoeffType(Long value) {
        this.value = value;
    }

    /**
     * Возвращает значение.
     * @return значение
     */
    public Long getValue() {
        return value;
    }
}