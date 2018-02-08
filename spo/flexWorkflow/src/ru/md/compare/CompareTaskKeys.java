package ru.md.compare;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

/**
 * Класс со статичными справочными данными для сравнения заявок
 * @author rislamov
 */
public class CompareTaskKeys {
	
	/**
	 * Блоки, которые не нужно показывать на форме сравнения версий
	 */
	private static List<CompareTaskBlock> excludedBlocks = new ArrayList<CompareTaskBlock>();
	
	static {
		excludedBlocks.add(CompareTaskBlock.STANDARD_PERIOD);
		excludedBlocks.add(CompareTaskBlock.EXPERTUS);
		excludedBlocks.add(CompareTaskBlock.CONCLUSION);
		excludedBlocks.add(CompareTaskBlock.RETURN_STATUS);
		excludedBlocks.add(CompareTaskBlock.DEPARTMENT);
		excludedBlocks.add(CompareTaskBlock.PROJECT_TEAM);
		excludedBlocks.add(CompareTaskBlock.STANDARD_PERIOD);
		excludedBlocks.add(CompareTaskBlock.STOP_FACTORS);
		excludedBlocks.add(CompareTaskBlock.DOCUMENTS);
		excludedBlocks.add(CompareTaskBlock.ACTIVE_DECISION);
		excludedBlocks.add(CompareTaskBlock.EXPERTUS);
		excludedBlocks.add(CompareTaskBlock.DEPARTMENT_AGREEMENT);
		excludedBlocks.add(CompareTaskBlock.FUNDS);
		excludedBlocks.add(CompareTaskBlock.N6);
		excludedBlocks.add(CompareTaskBlock.COMMENTS);
	}

	/**
	 * Блоки для сравнения сделок
	 * @author rislamov
	 */
	public enum CompareTaskBlock {
		/** Шапка карточки заявки */
		MAIN("Заявка"),
		/** Заемщики */
		CONTRACTORS("Заемщики"),
		/** Структура лимита */
		IN_LIMIT("Структура лимита"),
		/** Основные параметры */
		PARAMETERS("Основные параметры"),
		/** Стоимостные условия */
		PRICE_CONDITIONS("Стоимостные условия"),
		/** Договоры */
		CONTRACTS("Договоры"),
		/** Графики платежей */
		GRAPH("Графики платежей"),
		/** Погашение основного долга */
		GRAPH_LIMIT("Погашение основного долга"),
		/** Условия */
		CONDITIONS("Условия"),
		/** Обеспечение */
		SUPPLY("Обеспечение"),
		/** Pipeline */
		PIPELINE("Pipeline"),
		/** Решение уполномоченного органа */
		CONCLUSION("Решение уполномоченного органа"),
		/** Статус Решения по заявке */
		RETURN_STATUS("Статус Решения по заявке"),
		/** Ответственные подразделения */
		DEPARTMENT("Ответственные подразделения"),
		/** Проектная команда */
		PROJECT_TEAM("Проектная команда"),
		/** Сроки прохождения этапов заявки */
		STANDARD_PERIOD("Сроки прохождения этапов заявки"),
		/** Общий лист оценки стоп-факторов */
		STOP_FACTORS("Общий лист оценки стоп-факторов"),
		/** Документы по заявке */
		DOCUMENTS("Документы по заявке"),
		/** Действующие решения */
		ACTIVE_DECISION("Действующие решения"),
		/** Проведение экспертиз */
		EXPERTUS("Проведение экспертиз"),
		/** Справка об отклоненных замечаниях и предолжениях Экспертных подразделений */
		DEPARTMENT_AGREEMENT("Справка об отклоненных замечаниях и предолжениях Экспертных подразделений"),
		/** Заявки на фондирование */
		FUNDS("Заявки на фондирование"),
		/** Заявки на N6 */
		N6("Заявки на N6"),
		/** Комментарии */
		COMMENTS("Комментарии");

		String description;

		CompareTaskBlock(String descr) {
			description = descr;
		}
		
		/**
		 * Возвращает признак того, что блок исключен из формы сравнения версий
		 * @return boolean
		 */
		public static boolean isExcludedBlock(CompareTaskBlock block)
		{
			return excludedBlocks.contains(block);
		}

		/**
		 * Возвращает описание/наименование блока
		 * @return описание/наименование блока
		 */
		public String getDescription() {
			return description;
		}

		/**
		 * Возвращает блок по имени
		 * @param blockName имя блок
		 * @return результат
		 */
		public static CompareTaskBlock getBlock(String blockName) {
			for (CompareTaskBlock block : values())
				if (blockName.equalsIgnoreCase(block.name()))
					return block;
			return MAIN;
		}

		/**
		 * Возвращает блоки для сделки
		 * @return CompareTaskBlock[] блоки для сделки
		 */
		public static CompareTaskBlock[] getProductBlocks() {
			CompareTaskBlock[] res = CompareTaskBlock.values();
			res = ArrayUtils.removeElement(res, CompareTaskBlock.GRAPH_LIMIT);
			return res;
		}

		/**
		 * Возвращает блоки для лимита
		 * @return CompareTaskBlock[] блоки для лимита
		 */
		public static CompareTaskBlock[] getLimitBlocks() {
			CompareTaskBlock[] res = CompareTaskBlock.values();
		    res = ArrayUtils.removeElement(res, CompareTaskBlock.GRAPH);
		//	res = ArrayUtils.removeElement(res, CompareTaskBlock.PIPELINE);
			return res;
		}
	}

	/**
	 * Объекты контекста для преобразований
	 * @author rislamov
	 */
	public enum ContextKey {
		/** JPA-объект */
		JPA,
		/** Информация о заявке */
		TASK_INFO,
		/** Все типы контрагентов */
		ALL_CONTRACTOR_TYPES,
		/** CompendiumCrmActionProcessor */
		COMPENDIUM_CRM,
		/** Статус процесса */
		PUP_STATUS,
		/** Приоритет процесса */
		PUP_PRIORITY,
		/** Все типы лимитов */
		LIMIT_TYPES,
		/** PupFacadeLocal */
		PUP_FACADE_LOCAL,
		/** Для определения индикативной ставки */
		FLOAT_PART_OF_ACTIVE_RATE_LIST,
		/** Типы комиссий */
		COMMISION_TYPES,
		/** Списки сроков */
		DEPENDING_LOANS, 
		/** StandardPeriodBeanLocal */
		STANDARD_PERIOD_LOCAL,
		/** CompendiumActionProcessor */
		COMPENDIUM,
		/** Проекты кредитных решений */
		CREDIT_DECISION_PROJECT_MAP,
		/** Pipeline */
		PIPELINE,
		/** Цели финансирования */
		PIPELINE_FIN_TARGET,
		/** Тип процесса - сделка / лимит */
		PUP_TYPE,
;

	}

	/**
	 * Получение списка атрибутов по блоку
	 * @param block блок
	 * @return атрибуты
	 */
	public static HeaderElement getTaskHeader(CompareTaskBlock block, boolean isOpportunity) {
		return getTaskHeader(block, isOpportunity, true);
	}

	/**
	 * Получение списка атрибутов по блоку
	 * @param block блок
	 * @param firstFlag объект является сделкой
	 * @param secondFlag 
	 *		если сделка, то PRICE_CONDITIONS : фактические значения относятся к сделке в целом
	 *		если лимит, то isSublimit();
	 * @return атрибуты
	 */
	public static HeaderElement getTaskHeader(CompareTaskBlock block, boolean firstFlag, boolean secondFlag) {
		HeaderElement res = new HeaderElement();
		HeaderElement subRes = new HeaderElement();
		HeaderElement subSubRes = new HeaderElement();
		switch (block) {
			case MAIN:
				res.getKeys().add(new HeaderElement("Номер заявки"));
				res.getKeys().add(new HeaderElement("Бизнес-процесс"));
				res.getKeys().add(new HeaderElement("Статус"));
				res.getKeys().add(new HeaderElement("В рамках лимита"));
				if (!firstFlag)
					res.getKeys().add(new HeaderElement("Лимит включает Сублимиты"));
				else
					res.getKeys().add(new HeaderElement("Индивидуальные условия"));
				res.getKeys().add(new HeaderElement("Приоритет"));
			break;
			case CONTRACTORS:
				subRes.setKey("Наименование Контрагента");
				subRes.getKeys().add(new HeaderElement("Наименование типа", true));
				subRes.getKeys().add(new HeaderElement("Класс Контрагента"));
				subRes.getKeys().add(new HeaderElement("Отрасль экономики СРР"));
				subRes.getKeys().add(new HeaderElement("Регион СРР"));
				subRes.getKeys().add(new HeaderElement("Рейтинг кредитного подразделения"));
				subRes.getKeys().add(new HeaderElement("Рейтинг подразделения рисков"));
				subRes.getKeys().add(new HeaderElement("Утвержденный рейтинг"));
				subRes.getKeys().add(new HeaderElement("Рейтинг ПКР"));
				subRes.getKeys().add(new HeaderElement("Прикр. док."));
				res.getKeys().add(subRes);
				subRes = new HeaderElement("Наименование группы");
				subRes.getKeys().add(new HeaderElement("Отрасль экономики СРР"));
				subRes.getKeys().add(new HeaderElement("Регион СРР"));
				subRes.getKeys().add(new HeaderElement("Рейтинг КО"));
				subRes.getKeys().add(new HeaderElement("Экспертный рейтинг"));
				subRes.getKeys().add(new HeaderElement("Расчетный рейтинг"));
				subRes.getKeys().add(new HeaderElement("Дата рейтинга"));
				res.getKeys().add(subRes);
				res.getKeys().add(new HeaderElement("Распространяется на третьи лица"));
				res.getKeys().add(new HeaderElement("Страновая принадлежность"));
			break;
			case IN_LIMIT:
				if (firstFlag) {
					res.getKeys().add(new HeaderElement("Сделка проводится в рамках лимита"));
				}
				subRes.setKey("Номер лимита/сублимита/сделки");
				subRes.getKeys().add(new HeaderElement("Контрагент"));
				subRes.getKeys().add(new HeaderElement("Сумма"));
				subRes.getKeys().add(new HeaderElement("Срок, дней"));
				subRes.getKeys().add(new HeaderElement("Сделка сублимита"));
				res.getKeys().add(subRes);
			break;
			case PARAMETERS:
				if (firstFlag) {
					res.getKeys().add(new HeaderElement("Наименование целевого назначения", true));
					res.getKeys().add(new HeaderElement("Вид продукта (сделки)"));
					res.getKeys().add(new HeaderElement("Кредитная линия с лимитом выдачи"));
					res.getKeys().add(new HeaderElement("Кредитная линия с лимитом задолженности"));
					res.getKeys().add(new HeaderElement("Нестандартная сделка"));
					res.getKeys().add(new HeaderElement("Вид продукта сделки (Нестандарт)"));
					res.getKeys().add(new HeaderElement("Контракт"));
					res.getKeys().add(new HeaderElement("Предмет гарантии"));
					res.getKeys().add(new HeaderElement("Бенефициар"));
					res.getKeys().add(new HeaderElement("ОГРН"));
					res.getKeys().add(new HeaderElement("Планируемая дата подписания кредитного соглашения"));
					res.getKeys().add(new HeaderElement("Категория сделки - проектное финансирование"));
					res.getKeys().add(new HeaderElement("Валюта", true));
					res.getKeys().add(new HeaderElement("Срок сделки"));
					res.getKeys().add(new HeaderElement("Срок сделки до даты"));
					res.getKeys().add(new HeaderElement("Комментарий по сроку сделки"));
					res.getKeys().add(new HeaderElement("Сумма сделки"));
					res.getKeys().add(new HeaderElement("Валюта сделки"));
					res.getKeys().add(new HeaderElement("Сумма лимита выдачи"));
					res.getKeys().add(new HeaderElement("Валюта лимита выдачи"));
					res.getKeys().add(new HeaderElement("Сумма лимита задолженности"));
					res.getKeys().add(new HeaderElement("Валюта лимита задолженности"));
					res.getKeys().add(new HeaderElement("Срок использования"));
					res.getKeys().add(new HeaderElement("Срок использования до даты"));
					res.getKeys().add(new HeaderElement("Комментарий по сроку использования"));
					res.getKeys().add(new HeaderElement("Категория качества ссуды"));
					res.getKeys().add(new HeaderElement("Описание категории качества"));
					subRes.setKey("Номер транша");
					subSubRes = new HeaderElement("Номер выдачи", true);
					subSubRes.getKeys().add(new HeaderElement("Сумма"));
					subSubRes.getKeys().add(new HeaderElement("Валюта транша"));
					subSubRes.getKeys().add(new HeaderElement("С даты"));
					subSubRes.getKeys().add(new HeaderElement("По дату"));
					subRes.getKeys().add(subSubRes);
					res.getKeys().add(subRes);
					res.getKeys().add(new HeaderElement("Комментарий по графику использования"));
				}
				else {
					res.getKeys().add(new HeaderElement("Вид Лимита/Сублимита"));
					res.getKeys().add(new HeaderElement("Возобновляемый Лимит/Сублимит"));
					res.getKeys().add(new HeaderElement("Категория лимита - проектное финансирование"));
					res.getKeys().add(new HeaderElement("Наименование группы видов сделок", true));
					res.getKeys().add(new HeaderElement("Наименование целевого назначения", true));
					subRes.setKey("Номер порядка принятия решения");
					subRes.getKeys().add(new HeaderElement("Решения о/об", true));
					subRes.getKeys().add(new HeaderElement("принимаются"));
					subRes.getKeys().add(new HeaderElement("особенности принятия решений"));
					res.getKeys().add(subRes);
					res.getKeys().add(new HeaderElement("Cумма Лимита/Сублимита"));
					res.getKeys().add(new HeaderElement("Валюта Лимита"));
					res.getKeys().add(new HeaderElement("Валюта", true));
					res.getKeys().add(new HeaderElement("Перераспределение остатков между Сублимитами"));
					// поле выводится для сублимита
					if (secondFlag)
						res.getKeys().add(new HeaderElement("Срок сделок до"));
					else
						res.getKeys().add(new HeaderElement("Срок заключения сделок"));
					subRes = new HeaderElement("Наименование группы вида сделки");
					subRes.getKeys().add(new HeaderElement("До"));
					subRes.getKeys().add(new HeaderElement("Комментарий по сроку сделки"));
					res.getKeys().add(subRes);
					res.getKeys().add(new HeaderElement("Категория качества ссуды"));
				}
			break;
			case PRICE_CONDITIONS:
				if (firstFlag) {
					/** Подсекция Процентная ставка */
					res.getKeys().add(new HeaderElement("Тип ставки фиксированная"));
					res.getKeys().add(new HeaderElement("Тип ставки плавающая"));
					res.getKeys().add(new HeaderElement("Индикативная ставка"));
					res.getKeys().add(new HeaderElement("Надбавка к плавающей ставке"));
					res.getKeys().add(new HeaderElement("Ставка зафиксирована"));
					res.getKeys().add(new HeaderElement("Дата фиксации процентной ставки"));
					res.getKeys().add(new HeaderElement("Решение о понижении ставки"));
					res.getKeys()
							.add(new HeaderElement("Компенсирующий спрэд за фиксацию процентной ставки"));
					res.getKeys().add(new HeaderElement("Компенсирующий спрэд за досрочное погашение"));
					res.getKeys().add(new HeaderElement("Покрытие прямых расходов"));
					res.getKeys().add(new HeaderElement("Покрытие общебанковских расходов"));
					res.getKeys().add(new HeaderElement("Комиссия за выдачу"));
					res.getKeys().add(new HeaderElement("Комиссия за сопровождение"));
					/** Раздел Фактические значения */
					if (secondFlag) {
						res.getKeys().add(new HeaderElement("Расчетная ставка"));
						res.getKeys().add(new HeaderElement("Расчетная защищенная ставка"));
						res.getKeys().add(new HeaderElement("Ставка фондирования"));
						res.getKeys().add(new HeaderElement("Тип премии за кредитный риск"));
						res.getKeys().add(new HeaderElement("Величина изменения"));
						res.getKeys().add(new HeaderElement("Премия за кредитный риск"));
						res.getKeys().add(new HeaderElement("Индивидуальные условия"));
						res.getKeys().add(new HeaderElement("Плата за экономический капитал"));
						res.getKeys().add(new HeaderElement("Повыш. коэфф. за риск"));
						res.getKeys().add(new HeaderElement("Ставка размещения"));
						res.getKeys().add(new HeaderElement("Комментарий"));
						res.getKeys().add(new HeaderElement("Эффективная ставка"));
						res.getKeys().add(new HeaderElement("КТР"));
						res.getKeys().add(new HeaderElement("Обеспечение по периоду"));
					}
					subRes.setKey("Период");
					subRes.getKeys().add(new HeaderElement("Период с"));
					subRes.getKeys().add(new HeaderElement("Период по"));
					subRes.getKeys().add(new HeaderElement("Тип ставки фиксированная"));
					subRes.getKeys().add(new HeaderElement("Тип ставки плавающая"));
					subRes.getKeys().add(new HeaderElement("Расчетная ставка"));
					subRes.getKeys().add(new HeaderElement("Индикативная ставка"));
					subRes.getKeys().add(new HeaderElement("Расчетная ставка"));
					subRes.getKeys().add(new HeaderElement("Расчетная защищенная ставка"));
					subRes.getKeys().add(new HeaderElement("Ставка фондирования"));
					subRes.getKeys().add(new HeaderElement("Тип премии за кредитный риск"));
					subRes.getKeys().add(new HeaderElement("Величина изменения"));
					subRes.getKeys().add(new HeaderElement("Премия за кредитный риск"));
					subRes.getKeys().add(new HeaderElement("Индивидуальные условия"));
					subRes.getKeys().add(new HeaderElement("Плата за экономический капитал"));
					subRes.getKeys().add(new HeaderElement("Повыш. коэфф. за риск"));
					subRes.getKeys().add(new HeaderElement("Ставка размещения"));
					subRes.getKeys().add(new HeaderElement("Надбавка к плавающей ставке"));
					subRes.getKeys().add(new HeaderElement("Комментарий"));
					subRes.getKeys().add(new HeaderElement("Эффективная ставка"));
					subRes.getKeys().add(new HeaderElement("КТР"));
					subRes.getKeys().add(new HeaderElement("Обеспечение по периоду"));
					res.getKeys().add(subRes);
					subRes = new HeaderElement("Транш");
					subRes.getKeys().add(new HeaderElement("Ставка фондирования"));
					subRes.getKeys().add(new HeaderElement("Тип премии за кредитный риск"));
					subRes.getKeys().add(new HeaderElement("Величина изменения"));
					subRes.getKeys().add(new HeaderElement("Премия за кредитный риск"));
					subRes.getKeys().add(new HeaderElement("Плата за экономический капитал"));
					subRes.getKeys().add(new HeaderElement("Повыш. коэфф. за риск"));
					subRes.getKeys().add(new HeaderElement("КТР"));
					subRes.getKeys().add(new HeaderElement("Расчетная ставка"));
					subRes.getKeys().add(new HeaderElement("Расчетная защищенная ставка"));
					subRes.getKeys().add(new HeaderElement("Ставка размещения"));
					subRes.getKeys().add(new HeaderElement("Комментарий"));
					subRes.getKeys().add(new HeaderElement("Эффективная ставка"));
					subRes.getKeys().add(new HeaderElement("Компенсирующий спрэд за фиксацию процентной ставки"));
					subRes.getKeys().add(new HeaderElement("Компенсирующий спрэд за досрочное погашение"));
					subRes.getKeys().add(new HeaderElement("Покрытие прямых расходов"));
					subRes.getKeys().add(new HeaderElement("Покрытие общебанковских расходов"));
					subRes.getKeys().add(new HeaderElement("Комиссия за выдачу"));
					subRes.getKeys().add(new HeaderElement("Комиссия за сопровождение"));
					res.getKeys().add(subRes);

					/** Подсекция Комиссии / Вознаграждения */
					subRes = new HeaderElement("Наименование комиссии");
					subRes.getKeys().add(new HeaderElement("Величина комиссии"));
					subRes.getKeys().add(new HeaderElement("Валюта"));
					subRes.getKeys().add(new HeaderElement("Комментарии"));
					subRes.getKeys().add(new HeaderElement("Периодичность оплаты комиссии"));
					subRes.getKeys().add(new HeaderElement("База расчета"));
					subRes.getKeys().add(new HeaderElement("Порядок расчета"));
					subRes.getKeys().add(new HeaderElement("Срок оплаты комиссии"));
					res.getKeys().add(subRes);
				}
				else {
					/** Раздел Процентная ставка */
					res.getKeys().add(new HeaderElement("Тип премии за кредитный риск"));
					res.getKeys().add(new HeaderElement("Величина изменения"));
					res.getKeys().add(new HeaderElement("Индивидуальные условия"));
					res.getKeys().add(new HeaderElement("Премия за кредитный риск"));
					// не выводится для сублимитов
					subRes = new HeaderElement("Номер вознаграждения");
					subRes.getKeys().add(new HeaderElement("Размер вознаграждения"));
					subRes.getKeys().add(new HeaderElement("Тип вознаграждения"));
					res.getKeys().add(subRes);
					res.getKeys().add(new HeaderElement("Порядок уплаты процентов по кредиту/кредитной линии с "
							+ "лимитом выдачи на цели формирования покрытия для осуществления платежей по аккредитивам"));
					res.getKeys().add(new HeaderElement("Порядок уплаты процентов"));
					res.getKeys().add(new HeaderElement("КТР"));
					res.getKeys().add(new HeaderElement("Плата за экономический капитал"));
					/** Раздел Комиссии */
					subRes = new HeaderElement("Тип комиссии");
					subRes.getKeys().add(new HeaderElement("Описание комиссий"));
					subRes.getKeys().add(new HeaderElement("Порядок уплаты комиссий"));
					subRes.getKeys().add(new HeaderElement("Величина комиссии"));
					res.getKeys().add(subRes);
				}
				/** Подсекция и Раздел Санкции (неустойки, штрафы, пени и т.д.) */
				res.getKeys().add(
						new HeaderElement("Надбавка к процентной ставке за поддержание кредитовых"
								+ " оборотов менее установленного размера"));
				subRes = new HeaderElement("Наименование санкции (Тип штрафной санкции)");
				subRes.getKeys().add(new HeaderElement("Описание санкции"));
				subRes.getKeys().add(new HeaderElement("Величина санкции"));
				subRes.getKeys().add(new HeaderElement("Валюта / %"));
				subRes.getKeys().add(new HeaderElement("Период оплаты"));
				subRes.getKeys().add(new HeaderElement("Увеличивает ставку по сделке"));
				res.getKeys().add(subRes);
			break;
			case CONTRACTS:
				res.getKeys().add(new HeaderElement("Текст договора", true));
			break;
			case GRAPH:
				/** Раздел Погашение основного долга */
				res.getKeys().add(new HeaderElement("Периодичность погашения основного долга"));
				res.getKeys().add(new HeaderElement("Первый платеж в месяце выдачи"));
				res.getKeys().add(new HeaderElement("От даты (дата первой оплаты)"));
				res.getKeys().add(new HeaderElement("Дата окончательного погашения ОД"));
				res.getKeys().add(new HeaderElement("Сумма платежа"));
				res.getKeys().add(
						new HeaderElement(
								"Сумма платежа зависит от задолженности на дату окончания срока использования"));
				res.getKeys().add(new HeaderElement("Амортизация ставки"));
				res.getKeys().add(new HeaderElement("Порядок погашения"));
				res.getKeys().add(new HeaderElement("Комментарий к графику погашения"));
				/** Раздел Погашение основного долга */
				// если платежи без траншей
				subRes = new HeaderElement("Номер платежа");
				subRes.getKeys().add(new HeaderElement("Сумма платежа"));
				subRes.getKeys().add(new HeaderElement("Валюта платежа"));
				subRes.getKeys().add(new HeaderElement("Дата оплаты"));
				subRes.getKeys().add(new HeaderElement("Срок периода"));
				subRes.getKeys().add(new HeaderElement("Ставка фондирования по периоду платежа"));
				subRes.getKeys().add(new HeaderElement("Период оплаты (с даты)"));
				subRes.getKeys().add(new HeaderElement("Период оплаты (по дату)"));
				subRes.getKeys().add(new HeaderElement("Порядок расчета"));
				subRes.getKeys().add(new HeaderElement("Описание периода оплаты"));
				res.getKeys().add(subRes);
				// для платежей по траншам
				subSubRes = new HeaderElement("Номер платежа");
				subSubRes.getKeys().add(new HeaderElement("Сумма платежа"));
				subSubRes.getKeys().add(new HeaderElement("Валюта платежа"));
				subSubRes.getKeys().add(new HeaderElement("Период оплаты (с даты)"));
				subSubRes.getKeys().add(new HeaderElement("Период оплаты (по дату)"));
				subSubRes.getKeys().add(new HeaderElement("Срок периода"));
				subSubRes.getKeys().add(new HeaderElement("Ставка фондирования по периоду платежа"));
				subSubRes.getKeys().add(new HeaderElement("Порядок расчета"));
				subSubRes.getKeys().add(new HeaderElement("Описание периода оплаты"));
				subRes = new HeaderElement("Транш", true);
				subRes.getKeys().add(subSubRes);
				res.getKeys().add(subRes);
				/** Раздел График погашения процентов */
				res.getKeys().add(new HeaderElement("Периодичность"));
				res.getKeys().add(new HeaderElement("От даты (дата первой оплаты процентов)"));
				res.getKeys().add(new HeaderElement("До даты (дата окончательного погашения)"));
				res.getKeys().add(new HeaderElement("Число уплаты процентов"));
				res.getKeys().add(new HeaderElement("Порядок погашения процентов"));
				res.getKeys().add(new HeaderElement(
						"Последняя оплата в дату фактического погашения задолженности по основному долгу"));
				res.getKeys().add(new HeaderElement("Комментарий к графику погашения процентов"));
				res.getKeys().add(new HeaderElement("Порядок погашения задолженности"));
			break;
			case GRAPH_LIMIT:
				/** Раздел Погашение основного долга */
				subRes = new HeaderElement("Номер платежа");
				subRes.getKeys().add(new HeaderElement("Сумма платежа"));
				subRes.getKeys().add(new HeaderElement("Валюта платежа"));
				subRes.getKeys().add(new HeaderElement("Период оплаты (с даты)"));
				subRes.getKeys().add(new HeaderElement("Период оплаты (по дату)"));
				subRes.getKeys().add(new HeaderElement("Порядок расчета"));
				subRes.getKeys().add(new HeaderElement("Описание периода оплаты"));
				res.getKeys().add(subRes);
				res.getKeys().add(new HeaderElement("Порядок погашения задолженности"));
			break;
			case CONDITIONS:
				if (firstFlag)
					res.getKeys().add(new HeaderElement("Измененные и дополненные условия"));
				else
					res.getKeys().add(new HeaderElement("Общие условия лимита"));
				res.getKeys().add(new HeaderElement("Определения"));
				/** Подсекция Условия досрочного погашения */
				if (firstFlag) {
					res.getKeys().add(new HeaderElement("С запретом"));
					res.getKeys().add(new HeaderElement("Срок запрета"));
				}
				subRes.setKey("Номер условия");
				subRes.getKeys().add(new HeaderElement("Условие досрочного погашения"));
				subRes.getKeys().add(new HeaderElement("Комиссия"));
				subRes.getKeys().add(new HeaderElement("Комментарий"));
				if (firstFlag)
					subRes.getKeys().add(new HeaderElement("За сколько дней Заемщик должен уведомить Банк о досрочном погашении"));
				res.getKeys().add(subRes);
				/** Подсекция Дополнительные / Отлагательные / Индивидуальные и прочие условия */
				subRes = new HeaderElement("Тип условия");
				subSubRes = new HeaderElement("Условие", true, true);
				subSubRes.getKeys().add(new HeaderElement("Описание условия"));
				subRes.getKeys().add(subSubRes);
				res.getKeys().add(subRes);
			break;
			case SUPPLY:
				res.getKeys().add(new HeaderElement("Обеспечние предусмотрено"));
				res.getKeys().add(
						new HeaderElement("Тип операции (для целей определения транзакционного риска)"));
				res.getKeys().add(new HeaderElement("Применяемый курс при пересчете в руб."));
				res.getKeys().add(new HeaderElement("Дата курса"));
				if (firstFlag) {
					res.getKeys().add(new HeaderElement("Обеспечение из CRM"));
					res.getKeys().add(
							new HeaderElement("Расчетный коэффициент транзакционного риска обеспечения"));
					res.getKeys().add(new HeaderElement("Дата расчета коэффициента"));
					res.getKeys().add(new HeaderElement("Фактический коэффициент транзакционного риска"));
				}
				/** Подсекция Залоги */
				subRes = new HeaderElement("Залогодатель");
				subRes.getKeys().add(new HeaderElement("Основное обеспечение"));
				subRes.getKeys().add(new HeaderElement("Дополнительное обеспечение"));
				subRes.getKeys().add(new HeaderElement("Послед. залог"));
				subRes.getKeys().add(new HeaderElement("Вид залога"));
				subRes.getKeys().add(new HeaderElement("Предмет залога"));
				subRes.getKeys().add(new HeaderElement("Наименование и характеристики предмета залога"));
				subRes.getKeys().add(new HeaderElement("Рыночная стоимость предмета залога (руб.)"));
				subRes.getKeys().add(new HeaderElement("Порядок определения рыночной стоимости"));
				subRes.getKeys().add(new HeaderElement("Коэффициент залогового дисконтирования"));
				subRes.getKeys().add(new HeaderElement("Описание залоговой сделки"));
				subRes.getKeys().add(new HeaderElement("Ликвидационная стоимость предмета залога (руб.)"));
				subRes.getKeys().add(new HeaderElement("Залоговая стоимость"));
				subRes.getKeys().add(
						new HeaderElement("Категория обеспечения (уровень ликвидности залога)"));
				subRes.getKeys().add(new HeaderElement("Финансовое состояние залогодателя"));
				subRes.getKeys().add(new HeaderElement("Группа обеспечения"));
				subRes.getKeys().add(new HeaderElement("Степень обеспечения, %"));
				subRes.getKeys().add(new HeaderElement("Срок залога"));
				subRes.getKeys().add(new HeaderElement("По дату"));
				subRes.getKeys().add(
						new HeaderElement("Максимально возможная доля необеспеченной части сублимита"));
				subRes.getKeys().add(new HeaderElement("Удельный вес вида залога"));
				subRes.getKeys().add(new HeaderElement("Условия страхования"));
				res.getKeys().add(subRes);
				subRes = new HeaderElement("Индивидуальные условия залоговых сделок");
				subRes.getKeys().add(new HeaderElement("Описание условия"));
				res.getKeys().add(subRes);
				subRes = new HeaderElement("Поручитель");
				subRes.getKeys().add(new HeaderElement("Основное обеспечение"));
				subRes.getKeys().add(new HeaderElement("Дополнительное обеспечение"));
				subRes.getKeys().add(new HeaderElement("На всю сумму обязательств"));
				subRes.getKeys().add(new HeaderElement("Предел ответственности"));
				subRes.getKeys().add(new HeaderElement("Тип ответственности", true));
				subRes.getKeys().add(new HeaderElement("Вид поручительства"));
				subSubRes = new HeaderElement("Наименование санкции (Тип штрафной санкции)");
				subSubRes.getKeys().add(new HeaderElement("Описание санкции"));
				subSubRes.getKeys().add(new HeaderElement("Величина санкции"));
				subSubRes.getKeys().add(new HeaderElement("Валюта / %"));
				subSubRes.getKeys().add(new HeaderElement("Период оплаты"));
				subSubRes.getKeys().add(new HeaderElement("Увеличивает ставку по сделке"));
				subRes.getKeys().add(subSubRes);
				subRes.getKeys().add(new HeaderElement("Дополнительные обязательства по Поручителю"));
				subRes.getKeys().add(new HeaderElement("Категория обеспечения"));
				subRes.getKeys().add(new HeaderElement("Финансовое состояние поручителя"));
				subRes.getKeys().add(new HeaderElement("Группа обеспечения"));
				subRes.getKeys().add(new HeaderElement("Степень обеспечения, %"));
				subRes.getKeys().add(new HeaderElement("Срок поручительства"));
				subRes.getKeys().add(new HeaderElement("По дату"));
				res.getKeys().add(subRes);
				subRes = new HeaderElement("Индивидуальные условия поручительства");
				subRes.getKeys().add(new HeaderElement("Описание условия"));
				res.getKeys().add(subRes);
				subRes = new HeaderElement("Гарант");
				subRes.getKeys().add(new HeaderElement("Основное обеспечение"));
				subRes.getKeys().add(new HeaderElement("Дополнительное обеспечение"));
				subRes.getKeys().add(new HeaderElement("На всю сумму обязательств"));
				subRes.getKeys().add(new HeaderElement("Сумма гарантии"));
				subRes.getKeys().add(new HeaderElement("Валюта"));
				subRes.getKeys().add(new HeaderElement("Категория обеспечения"));
				subRes.getKeys().add(new HeaderElement("Финансовое состояние гаранта"));
				subRes.getKeys().add(new HeaderElement("Группа обеспечения"));
				subRes.getKeys().add(new HeaderElement("Степень обеспечения, %"));
				subRes.getKeys().add(new HeaderElement("Срок гарантии"));
				subRes.getKeys().add(new HeaderElement("по дату"));
				res.getKeys().add(subRes);
				subRes = new HeaderElement("Индивидуальные условия гарантии");
				subRes.getKeys().add(new HeaderElement("Описание условия"));
				res.getKeys().add(subRes);
				subRes = new HeaderElement("Векселедержатель векселя Банка");
				subRes.getKeys().add(new HeaderElement("Номинал (вексельная сумма) векселя Банка"));
				subRes.getKeys().add(new HeaderElement("Валюта векселя"));
				subRes.getKeys().add(new HeaderElement("Процентная оговорка по векселю Банка"));
				subRes.getKeys().add(new HeaderElement("Срок платежа по векселю Банка"));
				subRes.getKeys().add(new HeaderElement("Место платежа по векселю Банка"));
				res.getKeys().add(subRes);
			break;
			case PIPELINE:
				res.getKeys().add(new HeaderElement("Плановая Дата Выборки"));
				res.getKeys().add(new HeaderElement("Статус сделки"));
				res.getKeys().add(new HeaderElement("Вероятность Закрытия"));
				res.getKeys().add(new HeaderElement("Применимое Право"));
				res.getKeys().add(new HeaderElement("География"));
				res.getKeys().add(new HeaderElement("Обеспечение"));
				res.getKeys().add(new HeaderElement("Наименование Цели", true));
				res.getKeys().add(new HeaderElement("Описание Сделки, Включая Структуру, Деривативы и т.д."));
				res.getKeys().add(new HeaderElement("Комментарии по Статусу Сделки, Следующие Шаги"));
				res.getKeys().add(new HeaderElement("Дополнительный Бизнес, Сроки, Примерный Объем в млн. дол. США"));
				res.getKeys().add(new HeaderElement("Возможность Синдикации"));
				res.getKeys().add(new HeaderElement("Комментарии по Синдикации"));
				res.getKeys().add(new HeaderElement("Средневзвешенный Срок Погашения (WAL)"));
				res.getKeys().add(new HeaderElement("Минимальная Ставка (Hurdle Rate)"));
				res.getKeys().add(new HeaderElement("Маркап"));
				res.getKeys().add(new HeaderElement("PCs: Кеш, млн. дол. США"));
				res.getKeys().add(new HeaderElement("PCs: Резервы, млн. дол. США"));
				res.getKeys().add(new HeaderElement("PCs: Деривативы, млн. дол. США"));
				res.getKeys().add(new HeaderElement("PCs: Всего, млн. дол. США"));
				res.getKeys().add(new HeaderElement("Выбранный Объем Линии, млн. дол. США"));
				res.getKeys().add(new HeaderElement("Публичная Сделка"));
				res.getKeys().add(new HeaderElement("Приоритет Менеджмента"));
				res.getKeys().add(new HeaderElement("Новый клиент"));
				res.getKeys().add(new HeaderElement("Сделка Flow / Investment"));
				res.getKeys().add(new HeaderElement("Коэффициент Типа Сделки"));
				res.getKeys().add(new HeaderElement("Коэффициент по Сроку Погашения"));
				res.getKeys().add(new HeaderElement("Фондирующая Компания"));
				res.getKeys().add(new HeaderElement("Контрагент со стороны Группы ВТБ"));
				res.getKeys().add(new HeaderElement("Трейдинг Деск"));
				res.getKeys().add(new HeaderElement("Пролонгация"));
				res.getKeys().add(new HeaderElement("Не показывать в отчете"));
			break;
			case CONCLUSION:
				res.getKeys().add(new HeaderElement("Решение принимает"));
				res.getKeys().add(new HeaderElement("Желаемая дата рассмотрения"));
				res.getKeys().add(new HeaderElement("Уполномоченный орган"));
				res.getKeys().add(new HeaderElement("Дата заседания Комитета"));
				res.getKeys().add(new HeaderElement("Классификация вопроса для УО"));
				res.getKeys().add(new HeaderElement("Проект кредитного решения"));
				res.getKeys().add(new HeaderElement("Статус"));
				res.getKeys().add(new HeaderElement("Номер протокола"));
			break;
			case RETURN_STATUS:
				res.getKeys().add(new HeaderElement("Дата принятия решения"));
				res.getKeys().add(new HeaderElement("Уполномоченное лицо"));
				res.getKeys().add(new HeaderElement("Решение уполномоченного органа"));
				res.getKeys().add(new HeaderElement("Статус решения (детализация)"));
				res.getKeys().add(new HeaderElement("Комментарий к решению"));
			break;
			case DEPARTMENT:
				res.getKeys().add(new HeaderElement("Инициирующее Подразделение"));
				res.getKeys().add(new HeaderElement("Менеджер", true));
				res.getKeys().add(new HeaderElement("Место", true));
			break;
			case PROJECT_TEAM:
				subRes = new HeaderElement("Участник Проектной команды");
				subRes.getKeys().add(new HeaderElement("Подразделение"));
				subRes.getKeys().add(new HeaderElement("Роль", true));
				res.getKeys().add(subRes);
				subRes = new HeaderElement("Работник мидл-офиса");
				subRes.getKeys().add(new HeaderElement("Подразделение"));
				subRes.getKeys().add(new HeaderElement("Роль", true));
				res.getKeys().add(subRes);
			// TODO : Подсекции Формирование запроса, История запросов
			break;
			case STANDARD_PERIOD:
				subRes = new HeaderElement("Этап заявки");
				subRes.getKeys().add(new HeaderElement("Нормативный срок (дни)"));
				subRes.getKeys().add(new HeaderElement("Фактический срок (дни)"));
				subRes.getKeys().add(new HeaderElement("Критерий дифференциации срока"));
				subRes.getKeys().add(new HeaderElement("Исполнители, роли"));
				res.getKeys().add(subRes);
			break;
			case STOP_FACTORS:
				res.getKeys().add(
						new HeaderElement("Наименование стоп-фактора клиентского менеджера", true));
				res.getKeys().add(new HeaderElement("Наименование стоп-фактора безопасности", true));
				res.getKeys().add(new HeaderElement(
								"Наименование стоп-фактора подразделения подготовки кредитных заявок", true));
			break;
			case DOCUMENTS:
				subRes = new HeaderElement("Наименование файла");
				subRes.getKeys().add(new HeaderElement("Заголовок"));
				subRes.getKeys().add(new HeaderElement("Передается на Кредитный Комитет"));
				subRes.getKeys().add(new HeaderElement("Срок действия"));
				subRes.getKeys().add(new HeaderElement("Добавил"));
				subRes.getKeys().add(new HeaderElement("Утвердил"));
				subRes.getKeys().add(new HeaderElement("ЭЦП"));
				subRes.getKeys().add(new HeaderElement("Ветка"));
				res.getKeys().add(subRes);
			break;
			case ACTIVE_DECISION:
				res.getKeys().add(new HeaderElement("Действующие решения"));
			break;
			case EXPERTUS:
				subRes = new HeaderElement("Экспертиза");
				subRes.getKeys().add(new HeaderElement("Начало"));
				subRes.getKeys().add(new HeaderElement("Окончание"));
				subRes.getKeys().add(new HeaderElement("Эксперт"));
				subRes.getKeys().add(new HeaderElement("Участник", true));
				res.getKeys().add(subRes);
			break;
			case DEPARTMENT_AGREEMENT:
				subRes = new HeaderElement("Наименование экспертного подразделения");
				subRes.getKeys().add(new HeaderElement("Отклоненные замечания и предложения"));
				subRes.getKeys().add(new HeaderElement("Комментарий (мотивы отклонения)"));
				res.getKeys().add(subRes);
			break;
			case FUNDS:
				subRes = new HeaderElement("Номер заявки на фондирование");
				subRes.getKeys().add(new HeaderElement("Тип"));
				subRes.getKeys().add(new HeaderElement("Категория"));
				subRes.getKeys().add(new HeaderElement("Сумма фондир."));
				subRes.getKeys().add(new HeaderElement("Период выдач"));
				subRes.getKeys().add(new HeaderElement("Статус"));
				subRes.getKeys().add(new HeaderElement("Заявка действительна до"));
				res.getKeys().add(subRes);
			break;
			case N6:
				subRes = new HeaderElement("Номер заявки на Н6");
				subRes.getKeys().add(new HeaderElement("Сумма сделки"));
				subRes.getKeys().add(new HeaderElement("Плановые даты"));
				subRes.getKeys().add(new HeaderElement("Статус"));
				res.getKeys().add(subRes);
			break;
			case COMMENTS:
				subRes = new HeaderElement("Номер комментария");
				subRes.getKeys().add(new HeaderElement("Автор"));
				subRes.getKeys().add(new HeaderElement("Дата"));
				subRes.getKeys().add(new HeaderElement("С операции"));
				subRes.getKeys().add(new HeaderElement("Текст"));
				res.getKeys().add(subRes);
			break;
			default:
			break;
		}
		return res;
	}

}
