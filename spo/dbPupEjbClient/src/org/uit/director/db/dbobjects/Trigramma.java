package org.uit.director.db.dbobjects;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: PD190390
 * Date: 17.03.2006
 * Time: 9:15:12
 * To change this template use File | Settings | File Templates.
 */
public class Trigramma implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static float compareStrings(String s1, String s2) {

        int a = s1.length();
        int b = s2.length();
        String[] trigs1 = new String[a];
        String[] trigs2 = new String[b];
        String _s1_ = " " + s1 + " ";
        String _s2_ = " " + s2 + " ";

        for (int i = 0; i < a; i++) {
            trigs1[i] = _s1_.substring(i, i + 3);
        }

        for (int i = 0; i < b; i++) {
            trigs2[i] = _s2_.substring(i, i + 3);
        }

        int countComp = 0;
        for (int i = 0; i < a; i++) {
            for (int j = 0; j < b; j++) {
                if (trigs2[j].equalsIgnoreCase(trigs1[i])) {
                    countComp++;
                    if (i == j && i < 5) {
                        countComp += i;
                    }
                } else {
                    if (i == j && i < 5) {
                        countComp -= 2;
                    }
                }
            }
        }

        return (float) 2 * countComp / (a + b);

    }

    /**
     * Определяет индекс первого несовпадения двух строк
     *
     * @param s1
     * @param s2
     * @return idx
     */
    public static int getFirstUncoincide(String s1, String s2) {
        int idx = 0;
        while (s1.charAt(idx) == s2.charAt(idx)) {
			idx++;
		}
        return idx;
    }

    public static void main(String[] args) {
        System.out.println(compareStrings("Назначение пенсии по индустриальному району",
//                "Недоимка по индустриальному району"));
                "Назначение пенсии по устиновскому району"));


    }
}
