/**
 *
 */
package ru.md.domain.dict;

/**
 * Список обеспечений для pipeline.
 * @author Sergey Valiev
 */
public enum PipelineSupply {
    
    FULL("Полное обеспечение"), PARTIAL("Частичное Обеспечение"), NONE("Без Обеспечения");
    
    private String value;
    
    /**
     * Конструктор.
     * @param value значение
     */
    private PipelineSupply(String value) {
        this.value = value;
    }

    /**
     * Возвращает значение.
     * @return значение
     */
    public String getValue() {
        return value;
    }
}
