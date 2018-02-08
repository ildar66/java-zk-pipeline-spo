package com.vtb.util.report.utils;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.logging.Level;

import com.aspose.words.DocumentBuilder;
import com.aspose.words.IMailMergeDataSource;
import ru.masterdm.compendium.domain.VtbObject;
import com.vtb.util.Formatter;

/**
 * Custom DataSource to use in the MailMerge operations
 * @author Michael Kuznetsov
 */
public class MailMergeDataSource implements IMailMergeDataSource
{
    private String tableName;   // table name
    private int columnsCount;   // number of columns in the table. Each column has a name
    private int mRecordIndex;   // index of current row (when iterating through it) 
    private TreeMap<String, Integer> columnNames; // names of columns.
    private ArrayList<Object[]> rows;   // rows with data.
    // list of child MailMergeDataSource objects for a given row (index i)
    private List<List<MailMergeDataSource>> childDataSources;    
    private boolean oneColumnFlag;    // a oneColumn version is used or not
    protected  final Logger LOGGER = Logger.getLogger(MailMergeDataSource.class.getName());
    private boolean columnsdescriptionCreated = false;    // indicates whether Columns Description is Created or not 
    private ChildDataSourceFunction childDataSourceFunction;  // reference to function getChildDataSource
    
    /**
     * Constructor
     * @param tableName
     */
    public MailMergeDataSource(String tableName)
    {
        this.tableName = tableName;
        columnNames = new TreeMap<String, Integer>();
        rows = new ArrayList<Object[]>();
        childDataSources = new ArrayList<List<MailMergeDataSource>>();
        columnsCount = 0;
        // When the data source is initialized, it must be positioned before the first record.
        mRecordIndex = -1;
        oneColumnFlag = false;
    }

    /**
     * IMailMergeDataSource getChildDataSource function implementation
     * {@inheritDoc} 
     */
    public IMailMergeDataSource getChildDataSource(String tableName) throws Exception
    {
        if (childDataSourceFunction != null) 
            return  childDataSourceFunction.getChildDataSource(tableName, mRecordIndex);
        else return null;
    }
    
    /**
     * Method creates full structured object and fills ALL columns names 
     * @param <T> object -- ancestor of VtbObject  
     */
    public <T extends VtbObject> T createColumns(String prefix, Class<T> clazz) {
        try {
            T instance = (T)clazz.newInstance();
            TreeMap<String, String> pairs = new TreeMap<String, String>();
            instance.toFlatPairs(prefix, pairs, true);
            addColumns(generateRow(pairs, 0).toArray());
            columnsdescriptionCreated = true;
            return instance;
        } catch (Exception e) {
            String className = (clazz != null) ? clazz.getName() : "";
            LOGGER.log(Level.SEVERE, "Error in createColumns() for class " + className + " " + e.getMessage(), e);
            return null;
        }        
    }
    
    /**
     * Fills rows of objects with data. Fills rows if even object is partially created
     * ATTENTION!!! All column names should be created [method createColumns] before call to this method.  
     * @param coll
     */
    public <T extends VtbObject> void fillRows(String prefix, Collection<T> coll) {
        if (coll == null) return;
        // check whether method createColumns was called before
        if (!columnsdescriptionCreated) return;
        TreeMap<String, String> pairs = new TreeMap<String, String>();
        Iterator<T> it = coll.iterator();
        while (it.hasNext()) {
            T obj = (T)it.next();
            pairs.clear();
            obj.toFlatPairs(prefix, pairs, false);
            addRow(pairs);
        }
    }
    
    /**
     * Fills one row 
     * @param obj
     */
    public void fillRows(String prefix, VtbObject  obj) {
        ArrayList<VtbObject> array = new ArrayList<VtbObject>();
        array.add(obj);
        fillRows(prefix, array);
    }

    /***********************************************************************************************************/
    /*    The methods below are used separately from the methods above. Don't mix them in the same code        */
    /***********************************************************************************************************/
    
    /**
     * Adds a row to the dataSource
     * @param pairs -- pair KEY VALUE to insert
     */
    public void addRow(TreeMap<String, String> pairs) {  
        if (!oneColumnFlag) {
            Object[] row = new Object[columnsCount];  // by default set null to Objects
            
            // Looks for index by name and insert value.
            Iterator<Entry<String, String>> it = pairs.entrySet().iterator();
            while (it.hasNext()) {
                Entry<String, String> entry = it.next();
                if (columnNames.containsKey(entry.getKey())) {
                    int idx = columnNames.get(entry.getKey()).intValue();
                    if ((idx >=0) && (idx < columnsCount))
                        row[columnNames.get(entry.getKey()).intValue()] = entry.getValue();
                }
            }
            rows.add(row);
        }
    }
    

    /**
     * Adds a row to the dataSource
     * @param row row to insert
     */
    public void addRow(Object[] row) {  
        if (!oneColumnFlag) rows.add(row);    
    }
    
    /**
     * Adds a row to the dataSource
     * @param row
     */
    public void addRow(List<Object> row) {  
        if (!oneColumnFlag) rows.add(row.toArray());    
    }
    
    /**
     * add column to the metadata of the DataSource
     * @param columnName
     */
    public void addColumn(String columnName) {         
        if (!oneColumnFlag) {
            columnNames.put(columnName, columnsCount);
            columnsCount++;
        }
    }
    
    /**
     * add columns to the metadata of the DataSource
     * @param columnNames list of column names
     */
    public void addColumns(Object[] names) {         
        if (!oneColumnFlag) {
            for(Object columnName : names) {
                columnNames.put((String)columnName, columnsCount);
                columnsCount++;
            }
        }
    }

    
    /**
     * add column to the metadata of the DataSource
     * This version permits only a ONE-COLUMN DataSet. No other columns cannot be added.
     * @param columnName
     */
    public void addOneColumn(String columnName, Object[] list) {         
        // if already have columns, forbid the action. 
        if (columnsCount > 0) return;
        columnNames.put(columnName, columnsCount);
        columnsCount++;
        for(int i=0; i< list.length; i++) {
            Object[] resultList = new Object[1];
            resultList[0] = list[i]; 
            addRow(resultList);
        }
        oneColumnFlag = true;
    }
 
    /***********************************************************************************************************/
    /*                  Base methods that implement IMailMergeDataSource interface                             */
    /***********************************************************************************************************/
    
    /** 
     * Aspose.Words call this to get a value for every data field.(non-Javadoc)
     * @see com.aspose.words.IMailMergeDataSource#getValue(java.lang.String, java.lang.Object[])
     */
    @Override
    public boolean getValue(String columnName,  Object[] fieldValue)
    {
        Integer fieldPos = columnNames.get(columnName);
        if ((fieldPos != null) && (fieldPos < columnsCount)) {
            fieldValue[0] = rows.get(mRecordIndex)[fieldPos];
            return true;
        } else {
            // A field with this name was not found,
            // return false to the Aspose.Words mail merge engine.
            fieldValue[0] = null;
            return false;
        }
    }

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
    

    /***********************************************************************************************************/
    /*                                          Service methods                                                */
    /***********************************************************************************************************/

    /**
     * Prints table to console
     * @param logNames -- log only columns names
     * @throws Exception 
     */
    public void print(DocumentBuilder builder, boolean logNames) {
        try {
            //System.out.println();
            builder.writeln(" ");
            //System.out.println("--------------Table: " + tableName + " ---------------------------------------------------------------");
            builder.writeln("--------------Table: " + tableName + " ---------------------------------------------------------------");
            for(int i = 0; i< columnsCount; i++) {
//                System.out.print(
//                        String.format("%1$40.40s", 
//                                findKeyByValue(columnNames, new Integer(i)).replaceAll("\n", " ").replaceAll("\r", " ").replaceAll("\t", " "))
//                           + "| ");
                builder.write(
                    String.format("%1$40.40s", 
                         findKeyByValue(columnNames, new Integer(i)).replaceAll("\n", " ").replaceAll("\r", " ").replaceAll("\t", " "))
                    + "| ");
            }
            if (logNames) return;
            builder.writeln(" ");
            for(Object[] row : rows) {
                for (Object obj : row) { 
//                    System.out.print(
//                            String.format("%1$40.40s", 
//                                    Formatter.str(obj).replaceAll("\n", " ").replaceAll("\r", " ").replaceAll("\t", " "))
//                               + "| ");
                    builder.write(
                        String.format("%1$40.40s", 
                             Formatter.str(obj).replaceAll("\n", " ").replaceAll("\r", " ").replaceAll("\t", " "))
                        + "| ");
                }
//                System.out.println(" ");
                builder.writeln(" ");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in MailMergeDataSource.print() " + e.getMessage(), e);
        }
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
    
    /**
     * Makes a copy of the MailMergeDataSource and change table name and column names.  
     * @param nameFields map with the names to change
     * Attention!!! this version doesn't create a NEW COPY. It changes the existing one!!!
     * returns null, if errors are found 
     */
    public MailMergeDataSource copyWithChangeNames(Map<String, String> names) {
        if (!checkForExistance(names, tableName)) return null;
        tableName = names.get(tableName);
        mRecordIndex = -1;  // to reset iterations and allow new iteration process. 
        // change column names
        TreeMap<String, Integer> newColumnNames = new TreeMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : columnNames.entrySet()) { 
            if (!checkForExistance(names,entry.getKey())) //return null;
                // make a trick! don't change field name (to avoid track in the database all fields and crash if objects are changed)
                 newColumnNames.put(entry.getKey(), entry.getValue());
            else newColumnNames.put(names.get(entry.getKey()), entry.getValue());
        }
        columnNames = newColumnNames;
        return this;
    }
    
    private boolean checkForExistance(Map<String, String> names, String key) {
        String value = names.get(key);
        if ((value == null) || (value.trim().equals(""))) return false;
        return true;
    }        
    
    /**
     * reset cursor position and allow new iteration process.
     */
    public void resetCursor() {
        mRecordIndex = -1;  // to reset iterations and allow new iteration process.
    }

    public ChildDataSourceFunction getChildDataSourceFunction() {
        return childDataSourceFunction;
    }

    public void setChildDataSourceFunction(ChildDataSourceFunction childDataSourceFunction) {
        this.childDataSourceFunction = childDataSourceFunction;
    }

    public TreeMap<String, Integer> getColumnNames() {
        return columnNames;
    }
    
    /**
     * Get list of child MailMergeDataSource objects for row i.
     * @param i row index.
     */
    public List<MailMergeDataSource> getChildDataSources(int i) {
        if ((childDataSources != null) && (i< childDataSources.size())) return childDataSources.get(i);
        else return null;
    }

    /**
     * Set list of child MailMergeDataSource objects for row i.
     * @param i row index.
     */
    public void setChildDataSources(List<List<MailMergeDataSource>> children) {
        childDataSources = children;
    }

    /**
     * Returns current row index  
     */
    public int getCurrentIndex() {
        return mRecordIndex;
    }
} 