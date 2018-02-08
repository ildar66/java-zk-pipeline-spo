package org.uit.director.db.dbobjects;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;


public class MyClob implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String str;


    public MyClob(String str) {
        this.str = str;
    }

    public int getSize() {
        return str.length();
    }


    public String getStr() {
        return str;
    }
    
    public char[] getChar() {
    	
    	char[] ch = new char[getStr().length()];
		StringReader stringReader = new StringReader(getStr());
		int chr = 0;
		int ii = 0;
		try {
			while ((chr = stringReader.read()) != -1) {
				ch[ii] = (char) chr;
				ii++;
			}
		} catch (IOException e) {
		}

    	return ch;
    }
}
