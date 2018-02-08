package ru.md.spo.ejb;

import java.util.Date;

import javax.ejb.Local;

import ru.md.crm.dbobjects.CRMRating;
import ru.md.crm.dbobjects.LimitQueueTO;
import ru.md.crm.dbobjects.NetworkWagerJPA;
import ru.md.crm.dbobjects.ProductQueueJPA;
import ru.md.pup.dbobjects.UserJPA;

import com.vtb.domain.SPOAcceptType;
import com.vtb.domain.Task;

@Local
public interface CrmFacadeLocal {
    /**
     * Отдает рейтинги в CRM.
     */
    void exportRating(CRMRating rating);
    
    
    /**
     * Возвращает очередь лимитов для загрузки в СПО.
     * @param type - тип элемента в очереди. Загруженные, не загруженные, с ошибками.
     */
    LimitQueueTO[] getLimitQueue(SPOAcceptType type, String from, String to);
    /**
     * Возвращает очередь лимитов для загрузки в СПО.
     * @param type - тип элемента в очереди. Загруженные, не загруженные, с ошибками.
     */
    LimitQueueTO[] getLimitQueue(SPOAcceptType type);
    /**
     * Возвращает очередь лимита для загрузки в СПО.
     */
    LimitQueueTO getLimitQueueById(String id);
    /**
     * Обновляет статус лимита в очереди.
     */
    void updateLimitQueueStatus(String queueID, SPOAcceptType type, String result);
    /**
     * Передает параметры лимита в CRM.
     * Записываем в таблицы FB_SPO_LIMIT, ?FB_SPO_OPP_LIMIT?, FB_SPO_LIMIT_ACCOUNT
     */
    void exportLimit(Task task);
    
    
    /**
     * журнал аудита для сделок.
     */
    void log(Date date, String id, String message);
    /**
     * Возвращает очередь сделок для загрузки в СПО.
     * @param type - тип элемента в очереди. Загруженные, не загруженные, с ошибками.
     */
    ProductQueueJPA[] getProductQueue(SPOAcceptType type);
    ProductQueueJPA getProductQueueById(String id);
    /**
     * Возвращает очередь сделок для загрузки в СПО.
     * @param type - тип элемента в очереди. Загруженные, не загруженные, с ошибками.
     * @param userLogin - логин пользователя для филтрации. Если null, то без фильтра по логину.
     */
    ProductQueueJPA[] getProductQueue(SPOAcceptType type, String from, String to, String userLogin);
    /**
     * Возвращает очередь сделок для удаления из СПО.
     */
    ProductQueueJPA[] getProductSPODELETEQueue();
    /**
     * Обновляет статус сделки в очереди.
     */
    void updateProductQueueStatus(String queueID, SPOAcceptType type, String result);
    /**
     * Передает параметры сделки в CRM перед рассмотрением на КК.
     * Записываем в таблицы FB_SPO_BEFORE_KK
     */
    void exportProductBeforeKK(Task task);
    /**
     * Передает параметры сделки в CRM.
     * Записываем в таблицы
     */
    void exportProduct(Task task);
    void exportProductCommission(Task task);
    void exportProductPaymentSchedule(Task task, String productid);
    void exportProductTrance(Task task);
    void exportProductReason(Task task);
    /**Возвращает список возможных пользователей на кого грузить сделку. */
    UserJPA[] getUserListForLoadProduct(String productid);
    
    NetworkWagerJPA[] getNetworkWagerByProductQueueId(String queueId);
    
    /**
     * 
     * @param queueId
     * @return
     */
    String inLimit(String opportunityId);
}
