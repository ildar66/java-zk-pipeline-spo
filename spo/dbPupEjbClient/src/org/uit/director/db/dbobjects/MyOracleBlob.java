package org.uit.director.db.dbobjects;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringReader;


public class MyOracleBlob implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String str;


    public MyOracleBlob(String str) {
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
    
    public byte[] getBytes() {
    	return str.getBytes();
    }
    
    public InputStream getStream() {    	
    	ByteArrayInputStream bas = new ByteArrayInputStream(getBytes());
    	return bas;
    }
}
