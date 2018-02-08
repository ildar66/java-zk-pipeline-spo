package org.uit.director.servletutil;


public class ConvertChars {

    private static char[][] sootv = {
            {'ё', '`'},
            {'й', 'q'},
            {'ц', 'w'},
            {'у', 'e'},
            {'к', 'r'},
            {'е', 't'},
            {'н', 'y'},
            {'г', 'u'},
            {'ш', 'i'},
            {'щ', 'o'},
            {'з', 'p'},
            {'х', '['},
            {'ъ', ']'},
            {'ф', 'a'},
            {'ы', 's'},
            {'в', 'd'},
            {'а', 'f'},
            {'п', 'g'},
            {'р', 'h'},
            {'о', 'j'},
            {'л', 'k'},
            {'д', 'l'},
            {'ж', ';'},
            {'э', '_'},
            {'я', 'z'},
            {'ч', 'x'},
            {'с', 'c'},
            {'м', 'v'},
            {'и', 'b'},
            {'т', 'n'},
            {'ь', 'm'},
            {'б', ','},
            {'ю', '.'},

            {'Ё', '`'},
            {'Й', 'Q'},
            {'Ц', 'W'},
            {'У', 'E'},
            {'К', 'R'},
            {'Е', 'T'},
            {'Н', 'Y'},
            {'Г', 'U'},
            {'Ш', 'I'},
            {'Щ', 'O'},
            {'З', 'P'},
            {'Х', '['},
            {'Ъ', ']'},
            {'Ф', 'A'},
            {'Ы', 'S'},
            {'В', 'D'},
            {'А', 'F'},
            {'П', 'G'},
            {'Р', 'H'},
            {'О', 'J'},
            {'Л', 'K'},
            {'Д', 'L'},
            {'Ж', ';'},
            {'Э', '_'},
            {'Я', 'Z'},
            {'Ч', 'X'},
            {'С', 'C'},
            {'М', 'V'},
            {'И', 'B'},
            {'Т', 'N'},
            {'Ь', 'M'},
            {'Б', ','},
            {'Ю', '.'}};

    public static String convertToKirr(String eng) {

        char[] chars = eng.toCharArray();
        char[] convertMass = new char[eng.length()];
        for (int i = 0; i < chars.length; i++) {
            convertMass[i] = getKirr(chars[i]);
        }

        return new String(convertMass);
    }

    public static String convertToEng(String kirr) {

        char[] chars = kirr.toCharArray();
        char[] convertMass = new char[kirr.length()];
        for (int i = 0; i < chars.length; i++) {
            convertMass[i] = getEng(chars[i]);
        }

        return new String(convertMass);


    }

    public static char getKirr(char eng) {


        for (char[] element : sootv) {
            if (element[1] == eng) {
				return element[0];
			}
        }

        return eng;

    }

    public static char getEng(char kir) {

        for (char[] element : sootv) {
            if (element[0] == kir) {
				return element[1];
			}
        }

        return kir;


    }


}
