package ru.masterdm.spo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.masterdm.spo.utils.Formatter;
import ru.md.domain.Currency;
import ru.md.domain.Org;
import ru.md.persistence.CompendiumMapper;
import ru.md.persistence.CurrencyMapper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * Сервис для справочников. Когда нужно что-то сложнее, чем просто прочитать данные.
 * Created by Andrey Pavlenko on 04.12.15.
 */
@Service
public class DictService implements IDictService {
    @Autowired
    private CurrencyMapper currencyMapper;
    @Autowired
    private CompendiumMapper compendiumMapper;

    @Override
    public String moneyDisplay(BigDecimal val, String currCode) {
        return Formatter.format(val) + " " + moneyCurrencyDisplay(val, currCode);
    }

    @Override
    public String moneyCurrencyDisplay(BigDecimal val, String currCode) {
        if(val == null)
            return "";
        BigDecimal dollar = val.setScale(0, RoundingMode.HALF_DOWN);
        BigInteger cent = val.subtract(dollar).setScale(2, RoundingMode.HALF_UP).multiply(BigDecimal.TEN).multiply(BigDecimal.TEN).toBigInteger();
        Currency cur = getCurrencyByCode(currCode);
        if(cur == null)
            return Formatter.str(currCode);
        if(cent.intValue() != 0)
            return cur.getCurTwo();
        int f1 = dollar.intValue() %10;
        int f2 = dollar.intValue() %100;
        String s = "";
        if (f1==1)
            s = cur.getCurOne();
        if ((f1>=2) && (f1<=4))
            s = cur.getCurTwo();
        if ((f1>=5) && (f1<=9) || (f1==0) || ((f2>=11) && (f2<14)))
            s = cur.getCurMany();
        return s;
    }

    @Override
    public String moneyCurrencyDisplay(Double val, String currCode) {
        if(val == null)
            return "";
        return moneyCurrencyDisplay(BigDecimal.valueOf(val), currCode);
    }

    @Override
    public String getEkNameByOrgId(String id) {
        String ekName = compendiumMapper.getEkNameByOrgId(id);
        if (!Formatter.str(ekName).isEmpty())
            return ekName;
        Org org = compendiumMapper.getOrgById(id);
        if (org != null)
            return Formatter.str(org.getName());
        return "";
    }

    @Override
    public String getKzName(String id) {
        if (id == null || id.isEmpty())
            return "";
        Org org = compendiumMapper.getOrgById(id);
        if (org != null)
            return Formatter.str(org.getName());
        return "";
    }

    private Currency getCurrencyByCode(String code){
        for(Currency cur : currencyMapper.getCurrencyList())
            if(cur.getCode().equalsIgnoreCase(code))
                return cur;
        return null;
    }
}
