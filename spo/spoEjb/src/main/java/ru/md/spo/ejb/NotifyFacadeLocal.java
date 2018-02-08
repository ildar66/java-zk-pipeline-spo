package ru.md.spo.ejb;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.Set;

import javax.ejb.Local;

import com.vtb.exception.MappingException;

import freemarker.template.TemplateException;
import ru.masterdm.compendium.domain.User;
import ru.md.domain.MdTask;
import ru.md.domain.dict.InterestRateChange;
import ru.md.spo.dbobjects.MdTaskTO;
import ru.md.spo.dbobjects.TaskJPA;

/**
 * Рассылка уведомлений.
 * @author Andrey Pavlenko
 */
@Local
public interface NotifyFacadeLocal {
    void doNotifyNow();
	String notifyTaskTableTest() throws Exception;
    /**
     * запускает таймер, который делает рассылку о просроченных задачах.
     */
    void startTimer();
    /**
     * @return базовый URL для FlexWorkFlow. Учитывает разные адреса для пользователей в ГО и филиалах
     * @throws MalformedURLException
     */
    String getBaseURL(Long userid) throws MalformedURLException;

    /**
     * Уведомления на завершении этапа редактирования стоимостных условий
     * @param whoChange - id пользователя, который завершил этап
     */
    void onEditPriceCondition(TaskJPA task, Long whoChange);

    /**
     * Отправка уведомлений на завершение операций.
     * @param fromUserId идентификатор отправителя
     * @param mdTaskNumber номер заявки
     * @param mdTaskVersion версия заявки
     * @param mainBorrowerName основной заемщик
     * @throws TemplateException ошибка
     * @throws IOException ошибка
     */
    void onWorkCompletes(Long fromUserId, Long mdTaskNumber, Long mdTaskVersion, String mainBorrowerName) throws TemplateException, IOException;

    /**
     * Уведомление секретарей о начале нового этапа.
     */
    void notifySecretaryNewStage(Long idProcess, User from, Long idNewStage, Long idCurrentStage, Long idDepartament) throws Exception;
    Set<Long> getSecretaryIds(Long idTypeProcess, Long idDepartment);

    /**
     * Уведомление о начале изменения условий по сделке/лимиту.
     */
  	void notifyStartEditProcess(Long taskId, User from) throws Exception;

  	/**
  	 * Уведомление об изменении условий по сделке/лимиту
  	 * @author Дмитрий Кононов estetis21@gmail.com
  	 */
  	String[] notifyEditProcess(Long numberDisplay, Long idTaskInfo, Long version, User from, Long idUserTo, String contractor, String modules) throws Exception;

  	/**
	 * Отправка сообщения через почтовый сервер
	 *
	 * @param fromUserId {@link Long идентификатор} отправителя. Если <code>null</code> то будет взято значение GLOBAL_SETTINGS.MNEMO = 'mail.defaultsender.vtb'
	 * @param recipientId идентификатор получателей
	 * @param subject тема сообщения
	 * @param body тело сообщения
	 * @throws MappingException
	 */
	void send(Long fromUserId, Long recipientId, String subject, String body) throws MappingException;
	Long getMqFileHostTypeByDepId(Long depid);

	/**
	 * Уведомления при изменении процентной ставки.
	 * @param mdTaskId идентификатор заявки
	 * @param interestRateChange изменение процентной ставки
	 */
	void interestRateChangeNotification(Long mdTaskId, InterestRateChange interestRateChange);

	/**
	 * Возвращает название для уведомлений
	 * @return
	 */
	String getName(Long mdTaskId);
	/**
	 * Возвращает название для уведомлений в родительном падеже
	 * @return
	 */
	String getNameGenitive(Long mdTaskId);
	/**
	 * Возвращает название для уведомлений в дательном падеже
	 * @return
	 */
	String getNamePraepositionalis(Long mdTaskId);
	String getNameContractors(Long mdTaskId);
	String getAllContractors(Long mdTaskId);
	/**
	 * Возвращает Описание заявки
	 * @return
	 */
	String getDescriptionTask(Long mdTaskId);
	String getTypeNamePraepositionalis(Long mdTaskId);

	/**
	 * Уведомление об изменении заявки
	 */
	void notifyTaskChange(String[] changedSection, MdTaskTO task) throws Exception;

	void notifyDeleteDoc(Long mdTaskId, String reason, String unid, String orgid) throws Exception;
	void notifyAcceptDoc(Long mdTaskId, String unid, String orgid) throws Exception;
	Date getNextWorkDayBegin();
}
