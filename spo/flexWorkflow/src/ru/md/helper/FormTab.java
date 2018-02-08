package ru.md.helper;

import java.util.ArrayList;

/**
 * Таб для формы заявки. Не заморачиваюсь с pojo, просто структура.
 * Created by Andrey Pavlenko on 05.06.15.
 */
public class FormTab {
    public String name;//отображаемое называние вкладки
    public String code;//код для html id
    public String classes;//class html для заголовка
    public String url;///url откуда аяксом подгружать секцию
    public ArrayList<FormSubTab> subtabs;
    public String subtabs_id;//id элемента с табами на форме заявки

    public FormTab(String name, String code, String url, String classes) {
        this.name = name;
        this.code = code;
        this.url = url;
        this.classes = classes;
    }

    public FormTab(String name, String code, String url, ArrayList<FormSubTab> subtabs, String subtabs_id, String classes) {
        this.name = name;
        this.code = code;
        this.url = url;
        this.subtabs = subtabs;
        this.subtabs_id = subtabs_id;
        this.classes = classes;
    }
}
