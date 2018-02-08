/**
 * 
 */
package com.vtb.model.web;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vtb.domain.ReportTemplate;
import com.vtb.ejb.ReportBuilderActionProcessorLocal;
import com.vtb.ejb.ReportBuilderActionProcessorRemote;
import com.vtb.exception.MappingException;
import com.vtb.exception.NoSuchObjectException;
import com.vtb.model.ReportBuilderActionProcessor;
import com.vtb.report.renderer.ReportRenderer;
import com.vtb.util.EjbLocator;

/**
 * Бизнес-делегат для сессионного бина
 * 
 * @author Michael Kuznetsov
 */
public class ReportBuilderActionProcessorImpl implements ReportBuilderActionProcessor {
	
	private final Logger LOGGER = Logger.getLogger(this.getClass().getName());
	
	private ReportBuilderActionProcessor modelFacade = null;
	
	public ReportBuilderActionProcessorImpl() throws Exception {
		try {
			getReportBuilderLocal();
		} catch (Exception e) {
			try {
				getReportBuilderRemote();
			} catch (Exception e1) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
				e.printStackTrace();
				
				LOGGER.log(Level.SEVERE, e1.getMessage(), e1);
				e1.printStackTrace();
				
				throw new Exception("ReportBuilderActionProcessorFacade not found");
			}
		}
	}
	
	protected void getReportBuilderLocal() throws Exception {
		try {
			modelFacade = EjbLocator.getInstance().getReference(ReportBuilderActionProcessorLocal.class); 
		} catch (Exception e) {
			throw e;
		}
	}

	protected void getReportBuilderRemote() throws Exception {
		try {
			modelFacade = EjbLocator.getInstance().getReference(ReportBuilderActionProcessorRemote.class); 
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Override
	public ReportRenderer getReport (String reportType, Map<String, String[]> parameters) throws Exception {
		return modelFacade.getReport(reportType, parameters);
	}

    @Override
    public ReportRenderer getPrintFormReport(String reportName, String idProcess, boolean dynamicEncoding) throws Exception {
        return modelFacade.getPrintFormReport(reportName, idProcess, dynamicEncoding);
    }

    @Override
    public ReportRenderer getPrintFormWordReport(String reportName, String idProcess) throws Exception {
        return modelFacade.getPrintFormWordReport(reportName, idProcess);
    }

	@Override
	public ReportTemplate findByFilename(String filename) throws MappingException {
		return modelFacade.findByFilename(filename);
	}

	@Override
	public List<ReportTemplate> findByType(String type) throws NoSuchObjectException, MappingException {
		return modelFacade.findByType(type);		
	}

    @Override
    public ReportRenderer getLimitDecisionReport(String idProcess, boolean dynamicEncoding) throws Exception {
        return modelFacade.getLimitDecisionReport(idProcess, dynamicEncoding);
    }

    @Override
    public ReportRenderer getLimitDecisionReportAsWord(String idTask, String templateId, boolean dynamicEncoding) throws Exception {
        return modelFacade.getLimitDecisionReportAsWord(idTask, templateId, dynamicEncoding);
    }

	@Override
	public ReportRenderer getPrintFormExcelReport(String reportName, String mdTaskId, boolean dynamicEncoding) throws Exception {
		return modelFacade.getPrintFormExcelReport(reportName, mdTaskId, dynamicEncoding);
	}

    @Override
    public ReportRenderer getTaskBasedJoinDocumentReport(String mdTaskId, String templateId, boolean dynamicEncoding, Map<String, String> extraParameters, byte[] sourceDoc) throws Exception {
        return modelFacade.getTaskBasedJoinDocumentReport(mdTaskId, templateId, dynamicEncoding, extraParameters, sourceDoc);
    }
}
