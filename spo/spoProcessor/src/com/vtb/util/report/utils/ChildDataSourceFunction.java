package com.vtb.util.report.utils;

import com.aspose.words.IMailMergeDataSource;

/**
 * Closure to define an  getChildDataSource function implementation
 * @author Michael Kuznetsov
 */
public interface ChildDataSourceFunction {

    /**
     * Returns child data source
     * @param tableName name of table for which a child data source should be returned.
     * @param current row index in the parent table
     * @return
     */
    public IMailMergeDataSource getChildDataSource(String tableName, int rowIndex);
}
