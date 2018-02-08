package org.uit.director.plugins;

import java.util.List;

import org.uit.director.contexts.WorkflowSessionContext;


public interface PluginInterface {


    /**
     * Инициализация плагина с возможными параметрами
     *
     * @param wsc
     * @param params
     */
    public void init(WorkflowSessionContext wsc, List params);    

    /**
     * Выполнение плагина
     *
     * @return Струтсовское значение forward
     */
    public String execute();


}
