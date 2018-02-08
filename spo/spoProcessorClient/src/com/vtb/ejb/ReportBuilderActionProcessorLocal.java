package com.vtb.ejb;

import javax.ejb.Local;

import com.vtb.model.ReportBuilderActionProcessor;

/**
 * Remote interface for report builder utility
 * @author Michael Kuznetsov
 */
@Local
public interface ReportBuilderActionProcessorLocal extends ReportBuilderActionProcessor {

}
