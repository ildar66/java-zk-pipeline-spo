package ru.md.persistence;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ru.md.domain.ContitionTemplate;
import ru.md.domain.ContractorType;
import ru.md.domain.ExtraChargeRate;
import ru.md.domain.Org;
import ru.md.domain.Product;
import ru.md.domain.StatusReturn;
import ru.md.domain.TaskKz;
import ru.md.domain.dict.CommonDictionary;
import ru.md.domain.dict.CrossSell;
import ru.md.domain.dict.Contact;
import ru.md.domain.dict.FundingCompany;
import ru.md.domain.dict.PipelineStage;

/**
 * Выводит справочники компендиума. Призван заменить ejb вызовы компениума. Читать напрямую из базы оказалось быстрее и надёжнее.
 * @author Andrey Pavlenko
 *
 */
@Transactional(propagation = Propagation.SUPPORTS)
public interface CompendiumMapper {

    /**
     * Возвращает список плавающих (базовых) ставок.
     * @return список плавающих (базовых) ставок
     */
    List<CommonDictionary<String>> getBaseRates();

	/**
	 * Возвращает виды сделок Кросс селл
	 * @return виды сделок Кросс селл
     */
    List<CrossSell> getCrossSellTypes();

    /**
     * Возвращает список целей финансирования.
     * @return список целей финансирования
     */
    List<String> getFinancingObjectives();

    /**
     * Возвращает список коэффициентов pipeline.
     * @param typeId идентификатор типа
     * @return список коэффициентов pipeline
     */
    List<BigDecimal> getPipelineCoeffs(@Param("typeId") Long typeId);

    /**
     * Возвращает список фондирующих компаний.
     * @return список фондирующих компаний
     */
    List<String> getFundCompanies();

    /**
     * Возвращает список фондирующих компаний.
     * @param isRunProcess признак выполнения заявки СПО по стандартным процессам
     * @return список фондирующих компаний
     */
    List<FundingCompany> getFundCompaniesFull(@Param("isRunProcess") Boolean isRunProcess);

    /**
     * Возвращает список трейдинг деск.
     * @return список трейдинг деск
     */
    List<String> getTradingDesks();

    /**
     * Возвращает список значений "Компенсирующий спрэд за фиксацию процентной ставки".
     * @param periodInDays период в днях
     * @param currency валюта сделки
     * @return список значений "Компенсирующий спрэд за фиксацию процентной ставки".
     */
    List<BigDecimal> getFixingRateSpreads(@Param("periodInDays") Long periodInDays,
                                          @Param("currency") String currency);

    /**
     * Возвращает список значений "Компенсирующий спред за досрочное погашение".
     * @param periodInDays период в днях
     * @param currency валюта сделки
     * @return список значений "Компенсирующий спред за досрочное погашение".
     */
    List<BigDecimal> earlyRepaymentSpreads(@Param("periodInDays") Long periodInDays,
                                           @Param("currency") String currency);

    /**
     * Возвращает список значений "Компенсирующие спрэды в зависимости от срока кредита и моратория".
     * @param periodInDays период в днях
     * @param earlyRepaymentBanPeriod период запрета досрочного погашения
     * @param currency валюта сделки
     * @return список значений "Компенсирующие спрэды в зависимости от срока кредита и моратория".
     */
    List<BigDecimal> moratoriumRateSpreads(@Param("periodInDays") Long periodInDays,
                                           @Param("earlyRepaymentBanPeriod") Long earlyRepaymentBanPeriod,
                                           @Param("currency") String currency);

	Long getMqFileHostTypeByDepId(Long id);
	List<ContitionTemplate> findConditionTemplate(Long id);
	List<StatusReturn> findStatusReturnList();
	List<ContractorType> findContractorTypeList();
	Product getProductById(String id);
	Long getCcResolutionStatusCategoryId(Long idStatus);

	Long getEkPageTotalCount(Map<String,Object> filter);
	Long getKzPageTotalCount(@Param("filter") Map<String,Object> filter);
	List<Org> getEkPage(@Param("filter") Map<String,Object> filter, @Param("start") int start, @Param("count") int count);
	List<Org> getKzPage(@Param("filter") Map<String,Object> filter, @Param("start") int start, @Param("count") int count);
	/**
	 * Возвращает название единого клиента по коду организации. В качестве организации можно использовать ЕК или КЗ
	 */
	String getEkNameByOrgId(String id);
	String getGroupNameByOrgId(String id);
	String getEkGroupId(String idek);
	String getEkGroupName(String idgroup);
	Org getEkById(String id);
	Org getOrgById(String id);
	List<TaskKz> getTaskKzByMdtask(Long idMdtask);
	List<Long> getContractorTypeIdByIdR(Long idMdtask);

	@Transactional(propagation = Propagation.SUPPORTS)
	List<ExtraChargeRate> getExtraChargeRates();
    BigDecimal getCurrencyRate(String currency);
    BigDecimal getCurrencyRate4Date(@Param("currency") String currency,@Param("date") Date date);
    String isUsedInEffStavRecalc(String id);
	List<PipelineStage> getPipelineStage();

	List<Contact> getContactList(@Param("orgID") String orgID);

	List<String> getMoStatusReturnList();
	String getCryptoIssuers();

	List<CommonDictionary<Integer>> getTradeFinance();
}
