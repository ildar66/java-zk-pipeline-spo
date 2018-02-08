package ru.md.domain.dict;

/**
 * Список применимых прав.
 * @author Sergey Valiev
 */
public enum PipelineLaw {
    ENG("Английское"), RUS("Российское");
    
    private String value;
    
    /**
     * Конструктор.
     * @param value значение
     */
    private PipelineLaw(String value) {
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
