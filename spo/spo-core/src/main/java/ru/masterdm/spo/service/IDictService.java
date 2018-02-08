package ru.masterdm.spo.service;

import java.math.BigDecimal;

/**
 * Created by drone on 04.12.15.
 */
public interface IDictService {
    /**
     * Возвращает строку для денег, которую можно показать. Например 2 рубля, 5 рублей.
     * @param val - сумма
     * @param currCode -код валюты
     * @return строку для денег, которую можно показать. Например 2 рубля, 5 рублей.
     */
    String moneyDisplay(BigDecimal val, String currCode);
    /**
     * Возвращает строку для валюты, которую можно показать. Например 2 рубля, 5 рублей. Только валюта
     * Она зависит от суммы.
     * @param val - сумма
     * @param currCode -код валюты
     */
    String moneyCurrencyDisplay(BigDecimal val, String currCode);
    String moneyCurrencyDisplay(Double val, String currCode);
    String getEkNameByOrgId(String id);
    String getKzName(String id);
}
