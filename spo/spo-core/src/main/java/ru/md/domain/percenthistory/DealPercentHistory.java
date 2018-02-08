package ru.md.domain.percenthistory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Хронология изменения общих данных для процентной ставки сделки
 *
 * @author akirilchev@masterdm.ru
 */
public class DealPercentHistory implements Serializable {

	/**
	 * @serial
	 */
	private static final long serialVersionUID = 1L;

	private Long id;
	private Long idCreditDeal;

	private Boolean interestRateDerivative;
	private Boolean interestRateFixed;

	private List<IndrateHistory> indrateHistories;
	private List<FactPercentHistory> factPercentHistories;
	private List<FactPercentHistory> percentHistories;
	private Long indrateHistoryCount;
	
	private Long changeNumber;
	private Long idChangeUser;
	private String changeUserFullName;
	private Date changeDate;
	private Long idChangeDepartment;
	private String departmentName;
    
	/**
	 * Возвращает полный {@link List список} {@link FactPercentHistory объектов хронологии}, где в качестве первого объекта сама сделка
	 *
	 * @return полный {@link List список} {@link FactPercentHistory объектов хронологии}, где в качестве первого объекта сама сделка
	 */
	public List<FactPercentHistory> getPercentHistories() {
		if (percentHistories == null) {
			List<FactPercentHistory> results = new ArrayList<FactPercentHistory>();

			// если период один и сделка фиксированная, то берется ставка размещения из первого периода
			FactPercentHistory dealFactPercentHistory = new FactPercentHistory(); 
			if (Boolean.TRUE.equals(getInterestRateDerivative()))
				dealFactPercentHistory.setIndrateHistories(getIndrateHistories());
			if (getFactPercentHistories() != null && getFactPercentHistories().size() > 0) {
				FactPercentHistory firstPeriod = getFactPercentHistories().get(0);
				dealFactPercentHistory.setInterestRateFixed(this.getInterestRateFixed());
				dealFactPercentHistory.setInterestRateDerivative(this.getInterestRateDerivative());
				dealFactPercentHistory.setRate4(firstPeriod.getRate4());
				dealFactPercentHistory.setRate4Description(firstPeriod.getRate4Description());
				dealFactPercentHistory.setReason(firstPeriod.getReason());
				dealFactPercentHistory.setStartDate(firstPeriod.getStartDate());
				dealFactPercentHistory.setEndDate(firstPeriod.getEndDate());
				dealFactPercentHistory.setRate4StartDate(firstPeriod.getRate4StartDate());
				dealFactPercentHistory.setRate4EndDate(firstPeriod.getRate4EndDate());
				dealFactPercentHistory.setId(firstPeriod.getId());
			}
			results.add(dealFactPercentHistory);

			// периоды добавляются только, если их больше одного. Иначе периоды не показываются на экран
			if (getFactPercentHistories() != null && getFactPercentHistories().size() > 1) {
				Long periodNumber = 1L;
				for (FactPercentHistory factPercentHistory : factPercentHistories)
					factPercentHistory.setPeriodNumber(periodNumber++);
				results.addAll(factPercentHistories);
			}

			// TODO init endDate

			for (FactPercentHistory factPercentHistory : results) {
				if (Boolean.TRUE.equals(factPercentHistory.getInterestRateFixed())) {
					// добавление фиксированной ставки из периода
					IndrateHistory factPercentIndrateHistory = new IndrateHistory(factPercentHistory);
					List<IndrateHistory> newIndrateHistories = new ArrayList<IndrateHistory>();
					newIndrateHistories.add(factPercentIndrateHistory);

					List<IndrateHistory> indrateHistories = factPercentHistory.getIndrateHistories();
					if (indrateHistories != null)
						newIndrateHistories.addAll(indrateHistories);

					factPercentHistory.setIndrateHistories(newIndrateHistories);
				}
				if (factPercentHistory.getIndrateHistories() == null || factPercentHistory.getIndrateHistories().isEmpty()) {
					List<IndrateHistory> newIndrateHistories = new ArrayList<IndrateHistory>();
					newIndrateHistories.add(new IndrateHistory());
					factPercentHistory.setIndrateHistories(newIndrateHistories);
				}
			}
			

			percentHistories = results;
		}
		return percentHistories;
	}

	/**
	 * Возвращает {@link Long первичный ключ}
	 *
	 * @return {@link Long первичный ключ}
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Устанавливает {@link Long первичный ключ}
	 *
	 * @param id {@link Long первичный ключ}
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Возвращает {@link Long id} сделки
	 *
	 * @return {@link Long id} сделки
	 */
	public Long getIdCreditDeal() {
		return idCreditDeal;
	}

	/**
	 * Устанавливает {@link Long id} сделки
	 *
	 * @param idCreditDeal {@link Long id} сделки
	 */
	public void setIdCreditDeal(Long idCreditDeal) {
		this.idCreditDeal = idCreditDeal;
	}

	/**
	 * Возвращает {@link Boolean признак} выставленного плавающего типа ставки
	 *
	 * @return {@link Boolean признак} выставленного плавающего типа ставки
	 */
	public Boolean getInterestRateDerivative() {
		return interestRateDerivative;
	}

	/**
	 * Устанавливает {@link Boolean признак} выставленного плавающего типа ставки
	 *
	 * @param interestRateDerivative {@link Boolean признак} выставленного плавающего типа ставки
	 */
	public void setInterestRateDerivative(Boolean interestRateDerivative) {
		this.interestRateDerivative = interestRateDerivative;
	}

	/**
	 * Возвращает {@link Boolean признак} выставленного фиксированного типа ставки
	 *
	 * @return {@link Boolean признак} выставленного фиксированного типа ставки
	 */
	public Boolean getInterestRateFixed() {
		return interestRateFixed;
	}

	/**
	 * Устанавливает {@link Boolean признак} выставленного фиксированного типа ставки
	 *
	 * @param interestRateFixed {@link Boolean признак} выставленного фиксированного типа ставки
	 */
	public void setInterestRateFixed(Boolean interestRateFixed) {
		this.interestRateFixed = interestRateFixed;
	}

	/**
	 * Возвращает {@link List список} {@link IndrateHistory объектов хронологии изменения индикативной ставки}
	 *
	 * @return {@link List список} {@link IndrateHistory объектов хронологии изменения индикативной ставки}
	 */
	public List<IndrateHistory> getIndrateHistories() {
		return indrateHistories;
	}

	/**
	 * Устанавливает {@link List список} {@link IndrateHistory объектов хронологии изменения индикативной ставки}
	 *
	 * @param indrateHistories {@link List список} {@link IndrateHistory объектов хронологии изменения индикативной ставки}
	 */
	public void setIndrateHistories(List<IndrateHistory> indrateHistories) {
		this.indrateHistories = indrateHistories;
	}

	/**
	 * Возвращает {@link List список} {@link FactPercentHistory объектов хронологии изменения процетной ставки сделки}
	 *
	 * @return {@link List список} {@link FactPercentHistory объектов хронологии изменения процетной ставки сделки}
	 */
	public List<FactPercentHistory> getFactPercentHistories() {
		return factPercentHistories;
	}

	/**
	 * Устанавливает {@link List список} {@link FactPercentHistory объектов хронологии изменения процетной ставки сделки}
	 *
	 * @param factPercentHistories {@link List список} {@link FactPercentHistory объектов хронологии изменения процетной ставки сделки}
	 */
	public void setFactPercentHistories(List<FactPercentHistory> factPercentHistories) {
		this.factPercentHistories = factPercentHistories;
	}

	/**
	 * Возвращает {@link Long количество} объектов хронологии изменения индикативной ставки
	 *
	 * @return {@link Long количество} объектов хронологии изменения индикативной ставки
	 */
	public Long getIndrateHistoryCount() {
		if (indrateHistoryCount == null) {
			Long result = 0L;
			
			List<FactPercentHistory> percents = getPercentHistories();
			for(FactPercentHistory factPercentHistory: percents)
				result = result + factPercentHistory.getIndrateHistoryCount();
			
			indrateHistoryCount = result;
		}
		return indrateHistoryCount;
	}

	/**
	 * Возвращает {@link Long номер} изменения
	 *
	 * @return {@link Long номер} изменения
	 */
	public Long getChangeNumber() {
		return changeNumber;
	}

	/**
	 * Устанавливает {@link Long номер} изменения
	 *
	 * @param changeNumber {@link Long номер} изменения
	 */
	public void setChangeNumber(Long changeNumber) {
		this.changeNumber = changeNumber;
	}

	/**
	 * Возвращает {@link Long id} пользователя, нажавшего акцептовать или одобрить
	 *
	 * @return {@link Long id} пользователя, нажавшего акцептовать или одобрить
	 */
	public Long getIdChangeUser() {
		return idChangeUser;
	}

	/**
	 * Устанавливает {@link Long id} пользователя, нажавшего акцептовать или одобрить
	 *
	 * @param idChangeUser {@link Long id} пользователя, нажавшего акцептовать или одобрить
	 */
	public void setIdChangeUser(Long idChangeUser) {
		this.idChangeUser = idChangeUser;
	}

	/**
	 * Возвращает полное имя пользователя, нажавшего акцептовать или одобрить
	 *
	 * @return полное имя пользователя, нажавшего акцептовать или одобрить
	 */
	public String getChangeUserFullName() {
		return changeUserFullName;
	}

	/**
	 * Устанавливает полное имя пользователя, нажавшего акцептовать или одобрить
	 *
	 * @param changeUserFullName полное имя пользователя, нажавшего акцептовать или одобрить
	 */
	public void setChangeUserFullName(String changeUserFullName) {
		this.changeUserFullName = changeUserFullName;
	}

	/**
	 * Возвращает {@link Date дата} изменений
	 *
	 * @return {@link Date дата} изменений
	 */
	public Date getChangeDate() {
		return changeDate;
	}

	/**
	 * Устанавливает {@link Date дата} изменений
	 *
	 * @param changeDate {@link Date дата} изменений
	 */
	public void setChangeDate(Date changeDate) {
		this.changeDate = changeDate;
	}

	/**
	 * Возвращает {@link Long id} подразделения пользователя, нажавшего акцептовать или одобрить, на момент нажатия
	 *
	 * @return {@link Long id} подразделения пользователя, нажавшего акцептовать или одобрить, на момент нажатия
	 */
	public Long getIdChangeDepartment() {
		return idChangeDepartment;
	}

	/**
	 * Устанавливает {@link Long id} подразделения пользователя, нажавшего акцептовать или одобрить, на момент нажатия
	 *
	 * @param idChangeDepartment {@link Long id} подразделения пользователя, нажавшего акцептовать или одобрить, на момент нажатия
	 */
	public void setIdChangeDepartment(Long idChangeDepartment) {
		this.idChangeDepartment = idChangeDepartment;
	}

	/**
	 * Возвращает полное наименование подразделения пользователя, нажавшего акцептовать или одобрить
	 *
	 * @return полное наименование подразделения пользователя, нажавшего акцептовать или одобрить
	 */
	public String getDepartmentName() {
		return departmentName;
	}

	/**
	 * Устанавливает полное наименование подразделения пользователя, нажавшего акцептовать или одобрить
	 *
	 * @param departmentName полное наименование подразделения пользователя, нажавшего акцептовать или одобрить
	 */
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

}
