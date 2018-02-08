package ru.md.domain.dashboard;

import java.util.Date;

/**
 * Вопрос кредитного комитета.
 * Created by Andrey Pavlenko on 9.10.2016.
 */
public class CCQuestion {
    public Long id;
    public Long idReport;
    public Integer idDep;//кредитный комитет
    public String depName;//кредитный комитет
    public Date meetingDate;//Дата заседания Комитета
    public Integer ccQuestionType;//Классификация вопроса для УО
    public String pkr;//Проект кредитного решения
    public String status;//Статус
    public String protocol;//Номер протокола
    public String resolution;//выписка
    public String mapStatus;
}
