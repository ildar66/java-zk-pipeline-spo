package ru.md.spo.util;


/**
 * Форматы сообщений для уведомлений.
 * @author Andrey Pavlenko
 *
 * По традиции лучше придерживаться такого порядка переменных для текста
 * 0 - номер заявки
 * 1 - ссылка
 * 2 - основной контрагент
 * 3 - датавремя устаревания
 * 4 - операция
 * 5 - этап
 * 6 - подчиненный
 */
public enum NotifyMessageFormat {
    EXPIRED_SOON("Срок обработки заявки {0} истекает {1}","accept",
            "Срок обработки заявки <a href=\"{1}\">№ {0}</a> истекает {3}. <br />" +
            "Вы являетесь исполнителем по заявке (см. представление «операции в работе»).<br />"+
            NotifyMessageFormat.taskParam),
    NEED2ASSIGN("Срок обработки заявки {0} истек {1}","noAccept",
            "Срок обработки заявки <a href=\"{1}\">№ {0}</a> истек {3}. <br />" +
            "Вы не назначили исполнителя по заявке (см. представление «ожидающие обработки»).<br />"+
            NotifyMessageFormat.taskParam),
    EXPIRED_4_ASSIGNED("Срок обработки {0} истек {1}","perform",
            "Срок обработки {8} <a href=\"{1}\">№ {0}</a> истек {3}. <br />" +
            "Вы являетесь исполнителем по заявке (см. представление «назначенные мне»).<br />"+
            NotifyMessageFormat.taskParam),
    EXPIRED_4_ASSIGNED_BOSS("Срок обработки {0} истек {1}","noAccept",
            "Срок обработки {8} <a href=\"{1}\">№ {0}</a> истек {3}. <br />" +
            "Вы являетесь руководителем исполнителя {6} (см. представление «Ожидающие обработки»).<br />"+
            NotifyMessageFormat.taskParam),
    EXPIRED4EXECUTOR("Срок обработки заявки {0} истек {1}","accept",
                    "Срок обработки заявки <a href=\"{1}\">№ {0}</a> истек {3}. <br />" +
                    "Вы являетесь исполнителем по заявке (см. представление «операции в работе»).<br />"+
                    NotifyMessageFormat.taskParam),
    EXPIRED4BOSS("Срок обработки заявки {0} истек {1}","all",
            "Срок обработки заявки <a href=\"{1}\">№ {0}</a> истек {3}. <br />" +
            "Вы являетесь руководителем исполнителя {6} (см. представление «все заявки»).<br />"+
            NotifyMessageFormat.taskParam);
    final static String taskParam = "Контрагент: {2}.<br />Операция: {4}.<br />Этап: {5}.";
    
    private String subjectFormat;
    private String bodyFormat;
    private String listType;
    public String getSubjectFormat() {
        return subjectFormat;
    }
    public String getBodyFormat() {
        return bodyFormat;
    }
    public String getListType() {
        return listType;
    }
    private NotifyMessageFormat(String subjectFormat, String listType, String bodyFormat) {
        this.subjectFormat = subjectFormat;
        this.bodyFormat = bodyFormat;
        this.listType = listType;
    }
}
