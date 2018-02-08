package com.vtb.util.report.utils;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.logging.Level;

import com.aspose.words.DocumentBuilder;
import com.aspose.words.IMailMergeDataSource;
import ru.masterdm.compendium.domain.VtbObject;
import com.vtb.util.Formatter;

/**
 * Simple version of Custom DataSource to use in the MailMerge operations
 * Contains List<?> value.
 * @author Michael Kuznetsov
 */
public class SimpleMailMergeDataSource<T extends VtbObject> implements IMailMergeDataSource
{
    private String tableName;   // table name
    private String prefix;
    private int columnsCount;   // number of columns in the table. Each column has a name
    private int mRecordIndex;   // index of current row (when iterating through it) 
    private TreeSet<String> columnNames; // names of columns.
    private List<T> rows;   // rows with data.
    protected  final Logger LOGGER = Logger.getLogger(SimpleMailMergeDataSource.class.getName());
    
    /**
     * Constructor
     * @param tableName table name used in <StartTable:tableName> and <EndTable:tableName> doc merge fields.
     */
    public SimpleMailMergeDataSource(String prefix, String tableName, List<T> rows)
    {
        this.prefix = prefix; 
        this.tableName = tableName;
        this.rows = rows;
        if (rows != null) {
            columnNames = createColumnsNames(prefix, rows.get(0));
        } else columnNames = new TreeSet<String>();
        columnsCount = 0;
        // When the data source is initialized, it must be positioned before the first record.
        mRecordIndex = -1;
    }

    /**
     * IMailMergeDataSource getChildDataSource function implementation
     * {@inheritDoc} 
     */
    public IMailMergeDataSource getChildDataSource(String tableName) throws Exception
    {
        // TODO: implement!!!
        return null;
    }
    
    /**
     * Method creates full structured object and fills ALL columns names 
     * @param <T> object -- ancestor of VtbObject  
     */
    private TreeSet<String> createColumnsNames(String prefix, T instance) {
        try {
            TreeMap<String, String> pairs = new TreeMap<String, String>();
            instance.toFlatPairs(prefix, pairs, true);
            return new TreeSet<String>(pairs.keySet());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in createColumns() " + e.getMessage(), e);
            return new TreeSet<String>();
        }        
    }
    
    /***********************************************************************************************************/
    /*                  Base methods that implement IMailMergeDataSource interface                             */
    /***********************************************************************************************************/
//    
//    /** 
//     * Aspose.Words call this to get a value for every data field.(non-Javadoc)
//     * @see com.aspose.words.IMailMergeDataSource#getValue(java.lang.String, java.lang.Object[])
//     */
//    @Override
//    public boolean getValue(String columnName,  Object[] fieldValue)
//    {
//        Integer fieldPos = columnNames.get(columnName);
//        if ((fieldPos != null) && (fieldPos < columnsCount)) {
//            fieldValue[0] = rows.get(mRecordIndex)[fieldPos];
//            return true;
//        } else {
//            // A field with this name was not found,
//            // return false to the Aspose.Words mail merge engine.
//            fieldValue[0] = null;
//            return false;
//        }
//    }

    /**
     * A standard implementation for moving to a next record in a collection.
     */
    @Override
    public boolean moveNext()
    {
        if (isEof()) return false;
        mRecordIndex++;
        return (!isEof());
    }

    /**
     * Check for the EOF  
     */
    private boolean isEof()   { return (mRecordIndex >= rows.size());    }
    
    /**
     * The name of the data source. Used by Aspose.Words only when executing mail merge with repeatable regions.
     */
    @Override
    public String getTableName() {   return tableName;    }

    /**
     * @return columns count of the dataSource
     */
    public int getColumnsCount() { return columnsCount; } 
    
    /**
     * reset cursor position and allow new iteration process.
     */
    public void resetCursor () {
        mRecordIndex = -1;  // to reset iterations and allow new iteration process.
    }

    /***********************************************************************************************************/
    /*                                          Service methods                                                */
    /***********************************************************************************************************/

    /**
     * Prints table to console
     * @param logNames -- log only columns names
     * @throws Exception 
     */
    public void print(DocumentBuilder builder, boolean logNames) {
//        try {
//            //System.out.println();
//            builder.writeln(" ");
//            //System.out.println("--------------Table: " + tableName + " ---------------------------------------------------------------");
//            builder.writeln("--------------Table: " + tableName + " ---------------------------------------------------------------");
//            for(int i = 0; i< columnsCount; i++) {
////                System.out.print(
////                        String.format("%1$40.40s", 
////                                findKeyByValue(columnNames, new Integer(i)).replaceAll("\n", " ").replaceAll("\r", " ").replaceAll("\t", " "))
////                           + "| ");
//                builder.write(
//                    String.format("%1$40.40s", 
//                         findKeyByValue(columnNames, new Integer(i)).replaceAll("\n", " ").replaceAll("\r", " ").replaceAll("\t", " "))
//                    + "| ");
//            }
//            if (logNames) return;
//            builder.writeln(" ");
//            for(Object[] row : rows) {
//                for (Object obj : row) { 
////                    System.out.print(
////                            String.format("%1$40.40s", 
////                                    Formatter.str(obj).replaceAll("\n", " ").replaceAll("\r", " ").replaceAll("\t", " "))
////                               + "| ");
//                    builder.write(
//                        String.format("%1$40.40s", 
//                             Formatter.str(obj).replaceAll("\n", " ").replaceAll("\r", " ").replaceAll("\t", " "))
//                        + "| ");
//                }
////                System.out.println(" ");
//                builder.writeln(" ");
//            }
//        } catch (Exception e) {
//            LOGGER.log(Level.SEVERE, "Error in MailMergeDataSource.print() " + e.getMessage(), e);
//        }
    }
    
    
    /**
     * Search for VALUE in the map, and returns KEY.  
     */
    private String findKeyByValue(Map<String, Integer> map, Integer value) {
        Iterator<Entry<String, Integer>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, Integer> entry = it.next();
            if (entry.getValue().equals(value)) 
                return entry.getKey();  
        }
        return null;    
    }
    
    /**
     * Service method. Returns a list of values, iterating through pairs "key-value".
     * If mode = 0, returns a list of keys, if mode = 1, returns a list of values
     * @param pairs
     * @param mode
     * @return
     */
    public static List<Object> generateRow(TreeMap<String, String> pairs, int mode) {
        List<Object> result = new ArrayList<Object>(); 
        Iterator<Entry<String, String>> it = pairs.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, String> pair = it.next();
            if (mode == 0) result.add(pair.getKey());
            if (mode == 1) result.add(pair.getValue());
        }
        return result;
    }
    
//    /**
//     * Makes a copy of the MailMergeDataSource and change table name and column names.  
//     * @param nameFields map with the names to change
//     * Attention!!! this version doesn't create a NEW COPY. It changes the existing one!!!
//     * returns null, if errors are found 
//     */
//    public SimpleMailMergeDataSource copyWithChangeNames(Map<String, String> names) {
//        if (!checkForExistance(names, tableName)) return null;
//        tableName = names.get(tableName);
//        mRecordIndex = -1;  // to reset iterations and allow new iteration process. 
//        // change column names
//        TreeMap<String, Integer> newColumnNames = new TreeMap<String, Integer>();
//        for (Map.Entry<String, Integer> entry : columnNames.entrySet()) { 
//            if (!checkForExistance(names,entry.getKey())) //return null;
//                // make a trick! don't change field name (to avoid track in the database all fields and crach if objects are changed)
//                 newColumnNames.put(entry.getKey(), entry.getValue());
//            else newColumnNames.put(names.get(entry.getKey()), entry.getValue());
//        }
//        columnNames = newColumnNames;
//        return this;
//    }
    
    private boolean checkForExistance(Map<String, String> names, String key) {
        String value = names.get(key);
        if ((value == null) || (value.trim().equals(""))) return false;
        return true;
    }

    @Override
    public boolean getValue(String arg0, Object[] arg1) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }        
 }