package ru.md.spo.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import ru.md.spo.ejb.PupFacade;

public class ResourceLoader {
    public static String getSQL(String queryName){
        return getFile("sql/" + queryName + ".sql");
    }
    public static String getFile(String fileName){
        BufferedReader reader = null;
        try {
            StringBuffer res = new StringBuffer();
            reader = new BufferedReader(new InputStreamReader(
                    PupFacade.class.getClassLoader().getResourceAsStream(fileName), 
                    "UTF-8"));
            String text = null;
            while ((text = reader.readLine()) != null) {
                res.append(text).append(System.getProperty("line.separator"));
            }
            return res.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Error("getSQL error " + ex.getMessage());
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
