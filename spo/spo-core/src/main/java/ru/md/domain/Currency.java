package ru.md.domain;

/**
 * Валюта
 * @author Andrey Pavlenko
 */
public class Currency {
	private String code;
	private String text;
	private String curOne;//валюта в им. падеже ед.числе (доллар, рубль, евро, крона)
	private String curTwo;//валюта в род. падеже ед. числа: (доллара, рубля, евро, кроны)
	private String curMany;//валюта в род. падеже мн. числа: (долларов, рублей, евро, крон)

	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	@Override
	public String toString() {
		return code;
	}

	public String getCurOne() {
		return curOne;
	}

	public void setCurOne(String curOne) {
		this.curOne = curOne;
	}

	public String getCurTwo() {
		return curTwo;
	}

	public void setCurTwo(String curTwo) {
		this.curTwo = curTwo;
	}

	public String getCurMany() {
		return curMany;
	}

	public void setCurMany(String curMany) {
		this.curMany = curMany;
	}

	/**
	 * является ли эта валюта популярной для нашего Заказчика
	 */
	public static boolean isPopularCurrency(String code){
		if(code==null)
			return false;
		return code.equalsIgnoreCase("RUR") ||code.equalsIgnoreCase("usd") ||code.equalsIgnoreCase("eur");
	}
}
