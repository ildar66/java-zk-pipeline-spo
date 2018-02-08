package ru.md.jsp.tag;

import java.util.LinkedHashMap;
import java.util.List;

import com.vtb.util.Formatter;

import ru.md.domain.Currency;

/**
 * Tag for retrieval of list of currencies that are chosen in the parent task rather than retrievzl of ALL currencies
 * 
 * @author Michail Kuznetsov
 */
public class CurrencyParentTag extends AbstractSelectTag {
	private static final long serialVersionUID = 1L;
	private boolean withoutprocent = false;
	private Long parentTask = null;
	private boolean with_empty_field = false;

	@SuppressWarnings("unchecked")
	@Override
	public LinkedHashMap getHashMap() {
		try {
			LinkedHashMap hashmap = new LinkedHashMap();
			List<Currency> currencies = ru.masterdm.spo.utils.SBeanLocator.singleton().getCurrencyMapper().getTaskCurrencies(getParentTask());
			if (this.with_empty_field)
				hashmap.put("", "   ");
			if (currencies != null)
				for (Currency currency : currencies)
					hashmap.put(currency.getCode(), currency.getCode());
			if (!this.withoutprocent)
				hashmap.put("%  ", "%");
			return hashmap;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Error("CurrencyParentTag doStartTag error " + ex.getMessage(), ex);
		}
	}

	public boolean isWithoutprocent() {
		return withoutprocent;
	}

	public void setWithoutprocent(boolean withoutprocent) {
		this.withoutprocent = withoutprocent;
	}

	public Long getParentTask() {
		return parentTask;
	}

	public void setParentTask(Long parentTask) {
		this.parentTask = parentTask;
	}

	public void setParentTask(String parentTask) {
		this.parentTask = Formatter.parseLong(parentTask);
	}

	public boolean isWith_empty_field() {
		return with_empty_field;
	}

	public void setWith_empty_field(boolean with_empty_field) {
		this.with_empty_field = with_empty_field;
	}
}
