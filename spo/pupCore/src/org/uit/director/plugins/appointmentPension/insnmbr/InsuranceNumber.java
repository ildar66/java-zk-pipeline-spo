package org.uit.director.plugins.appointmentPension.insnmbr;

import java.util.StringTokenizer;

/**
 * Страховой номер человека в системе персонифицированного учета
 */
public class InsuranceNumber {
    private long number = 0;
    private String strNumber;

    public void set(long num) {
        number = num;
    }

    public InsuranceNumber(long num) {
        this.set(num);
    }

    public InsuranceNumber(String insnmbr) {
        strNumber = insnmbr;
        StringTokenizer tokenizer = new StringTokenizer(insnmbr, "- .,_';:!@#$%^&*()+{}[]");

        String number = "";
        while (tokenizer.hasMoreTokens()) {
            number += tokenizer.nextToken();
        }
        try {
            this.set(Long.parseLong(number));
        } catch (Exception e) {
            e.printStackTrace();

        }


    }

    public InsuranceNumber() {
        this.set(0);
    }


    /**
     * Возвращает значение страхового номера
     */
    public long toLong() {
        return (number);
    }

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
        strNumber += " " + this.hashToString();
        return (strNumber);
    }

    /**
     * Возвращает контрольное значение страхового номера
     */
    public int hashToInt() {
        long tmpNum = number;             // Страховой номер
        int position = 0;                       // Позиция цифры в номере
        int amount = 0;                       // Сумма
        while (tmpNum > 0) {
            position++;                         // Переход на следующую позицию
            int lastDigit = (int) (tmpNum % 10); // Получаем цифру числа
            tmpNum /= 10;                       // Удаляем последнюю цифру из числа
            amount += lastDigit * position;
        }
        int hash = amount % 101;
        hash = hash % 100;
        return (hash);
    }

    /**
     * Возвращает контрольное значение страхового номера
     */
    public String hashToString() {
        int intHash = this.hashToInt();
        String strHash = Integer.toString(intHash);
        String hash = new String("");
        if (intHash < 10) {
            hash = "0";
        }
        hash += strHash;
        return (hash);
    }

    public long getMainNumber() {
        StringTokenizer tokenizer = new StringTokenizer(strNumber, "- ");
        strNumber = tokenizer.nextToken();
        strNumber += tokenizer.nextToken();
        strNumber += tokenizer.nextToken();
        return Long.parseLong(strNumber);


    }
}