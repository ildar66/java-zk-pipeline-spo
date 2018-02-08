package ru.md.spo.report;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Заявка. Представление для отчёта
 * Created by Andrey Pavlenko on 14.02.2017.
 */
public class TaskReport {
    public Long number;
    public Long version;
    public String currency;
    public BigDecimal mdtaskSum;
    public ArrayList<String> currencyList = new ArrayList<String>();
    public ArrayList<String> productGroupList = new ArrayList<String>();
    public ArrayList<Contractor> contractors = new ArrayList<Contractor>();
    public String place;
    public String initDepartment;
    public User clientManager;//Клиентский менеджер
    public User structurator;//Структуратор
    public User analist;//Кредитный аналитик
}
