package ru.md.domain.dict;

/**
 * Выдающий банк.
 * @author Sergey Valiev
 */
public class FundingCompany extends CommonDictionary<Long> {

	private Boolean isRunProcess;

	/**
	 * Возвращает признак выполнения заявки СПО по стандартным процессам.
	 * @return <code>true</code> если требуется выполнения заявки СПО по стандартным процессам иначе по процессу Pipeline
	 */
	public Boolean getRunProcess() {
		return isRunProcess;
	}

	/**
	 * Устанавливает признак выполнения заявки СПО по стандартным процессам.
	 * @param isRunProcess признак выполнения заявки СПО по стандартным процессам
	 */
	public void setRunProcess(Boolean isRunProcess) {
		this.isRunProcess = isRunProcess;
	}
}
