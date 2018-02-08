package org.uit.director.plugins.appointmentPension.insnmbr;

import java.util.StringTokenizer;

/**
 * Страховой номер предприятия в системе персонифицированного учета
 */
public class EnterpriseNumber {
    private long number = 0;

    /**
     * Устанавливает значение страхового номера в заданное значение
     *
     * @param num Значение страхового номера в численном представлении
     */
    public void set(long num) {
        number = num;
    }

    /**
     * Устанавливает значение страхового номера в заданное значение
     *
     * @param regnum Значение страхового номера в строковом представлении
     *               <p>Строка должна быть вида xxx-yyy-zzzzzz, где xxx - номер региона, yyy - номер района,
     *               zzzzz - номер предприятия.</p><p>Незначащие нули в каждой секции могут опускаться, т.е.
     *               следующие номера равнозначны:</p><p>019-004-000490</p><p>19-4-490</p>
     */

    public void set(String regnum) {
        StringTokenizer st = new StringTokenizer(regnum, "-");
        char mask[] = {'0', '0', '0', '0', '0', '0'};

        StringBuffer sR = new StringBuffer(3);
        String region = st.nextToken();
        sR.insert(0, mask, 0, 3 - region.length());
        sR.append(region);
        region = sR.toString();

        StringBuffer sA = new StringBuffer(3);
        String area = st.nextToken();
        sA.insert(0, mask, 0, 3 - area.length());
        sA.append(area);
        area = sA.toString();

        StringBuffer sN = new StringBuffer(6);
        String number = st.nextToken();
        sN.insert(0, mask, 0, 6 - number.length());
        sN.append(number);
        number = sN.toString();

        String regnumb = region + area + number;
        this.number = Long.parseLong(regnumb);
    }

    /**
     * Создает объект класса EnterpriseNumber с регистрационным номером установленным в 0
     */
    public EnterpriseNumber() {
        this.set(0);
    }

    /**
     * Создает объект класса EnterpriseNumber
     *
     * @param num Значение страхового номера в численном представлении
     */
    public EnterpriseNumber(long num) {
        this.set(num);
    }

    /**
     * Создает объект класса EnterpriseNumber
     *
     * @param num Значение страхового номера в строковом представлении
     *            <p>Строка должна быть вида xxx-yyy-zzzzzz, где xxx - номер региона, yyy - номер района,
     *            zzzzz - номер предприятия.</p><p>Незначащие нули в каждой секции могут опускаться, т.е.
     *            следующие номера равнозначны:</p><p>019-004-000490</p><p>19-4-490</p>
     */
    public EnterpriseNumber(String num) {
        this.set(num);
    }

    /**
     * Возвращает значение номера предприятия в численном представлении
     */
    public long toLong() {
        return (number);
    }

    /**
     * Возвращает строку вида:<p>xxx-yyy-zzzzzz, где xxx - номер региона, yyy - номер района,
     * zzzzz - номер предприятия.
     */

    @Override
	public String toString() {
        String strNumber = new String("");
        long tmpNum = number;
        int position = 0;
        int triad = -1;
        while (tmpNum > 0) {
            triad++;
            triad %= 3;
            position++;
            int lastDigit = (int) (tmpNum % 10);
            tmpNum /= 10;
            if ((triad == 0) && (position > 1)) {
                strNumber = "-" + strNumber;
            }
            strNumber = Integer.toString(lastDigit) + strNumber;
        }
        switch (triad) {
            case 0:
                strNumber = "00" + strNumber;
                break;
            case 1:
                strNumber = "0" + strNumber;
                break;

        }
        strNumber = strNumber.substring(0, 11) + strNumber.substring(12);
        return (strNumber);
    }
}