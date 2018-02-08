package com.vtb.util.report.utils;


import java.util.HashSet;
import java.util.Set;

import com.aspose.words.DocumentBuilder;
import com.aspose.words.MergeFieldEventArgs;
import com.aspose.words.MergeFieldEventHandler;

public class HandleMergeFieldInsertHtml implements MergeFieldEventHandler {
    public Set<String> fields = new HashSet<String>();
    
   /**
   * Insert value from field as html   
   */
    public void mergeField(Object sender, MergeFieldEventArgs e) throws Exception
    {
        // All merge fields that expect HTML data should be marked with some prefix, e.g. 'html'.
        if (fields.contains(e.getDocumentFieldName()))
        {
            // Insert the text for this merge field as HTML data, using DocumentBuilder.
            DocumentBuilder builder = new DocumentBuilder(e.getDocument());
            builder.moveToMergeField(e.getDocumentFieldName());
            builder.insertHtml((String)e.getFieldValue());

            // The HTML text itself should not be inserted.
            // We have already inserted it as an HTML.
            e.setText("");
        }
    }    
}