package ru.md.helper;

/**
 * Таб для формы заявки. Не заморачиваюсь с pojo, просто структура.
 * Created by Andrey Pavlenko on 05.06.15.
 */
public class FormSubTab {
    public String name;//отображаемое называние вкладки
    public Long id;//код для html id

    public FormSubTab(String name, Long id) {
        this.name = name;
        this.id = id;
    }
}
