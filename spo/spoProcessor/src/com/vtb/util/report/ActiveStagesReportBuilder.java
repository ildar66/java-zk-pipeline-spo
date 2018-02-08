package com.vtb.util.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.w3c.dom.Element;

import ru.md.pup.dbobjects.ProcessTypeJPA;
import ru.md.spo.ejb.PupFacadeLocal;

import com.vtb.domain.ActiveStagesReport;
import com.vtb.domain.ActiveStagesReportHeader;
import com.vtb.domain.ActiveStagesReportOperation;
import com.vtb.domain.ActiveStagesReportProcessType;
import com.vtb.exception.MappingException;
import com.vtb.mapping.ActiveStagesReportMapper;
import com.vtb.mapping.ReportTemplateMapper;
import com.vtb.model.ReportBuilderActionProcessor.ReportTemplateParams;

/**
 * Builds an 'Active Stages' (active operations) report
 * @author Michael Kuznetsov
 */
public class ActiveStagesReportBuilder extends AbstractReportBuilder {

	private final Long TEMPLATE_ID = 5L;

	private Long idTypeProcess, idDepartment, idUser, isDelinquency, mdtaskId; // report parameters
	private String idClaim, correspondingDeps, id_Claim_FromList;

	private List<ActiveStagesReportOperation> operations; // data for report
	private ActiveStagesReportHeader header; // header for a report
	private ActiveStagesReportMapper mapper; // mapper for database access.

	public ActiveStagesReportBuilder(String reportName, ActiveStagesReportMapper mapper,
			ReportTemplateMapper reportMapper) throws Exception {
		super(reportName, reportMapper);
		this.mapper = mapper;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Long getTemplateId() {
		return TEMPLATE_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void getReportParameters(@SuppressWarnings("rawtypes") Map parameters) {
		String idTypeProcessStr = getParameter(parameters, ReportTemplateParams.ID_TYPE_PROCESS
				.getValue());
		try {
			idTypeProcess = (idTypeProcessStr != null) ? Long.parseLong(idTypeProcessStr.trim()) : -1L;
		}
		catch (NumberFormatException e) {
			idTypeProcess = -1L;
		}
		String mdtaskIdStr = getParameter(parameters, ReportTemplateParams.MDTASK_ID.getValue());
		try {
			mdtaskId = (mdtaskIdStr != null) ? Long.parseLong(mdtaskIdStr.trim()) : null;
		}
		catch (NumberFormatException e) {
			mdtaskId = null;
		}
		idClaim = getParameter(parameters, ReportTemplateParams.ID_CLAIM.getValue());
		if (idClaim == null)
			idClaim = "-1";
		else
			idClaim = getParameter(parameters, ReportTemplateParams.ID_CLAIM.getValue()).trim()
					.toUpperCase();

		String idDepartmentStr = getParameter(parameters, ReportTemplateParams.ID_DEPARTMENT.getValue());
		try {
			idDepartment = (idDepartmentStr != null) ? Long.parseLong(idDepartmentStr.trim()) : 0L;
		}
		catch (NumberFormatException e) {
			idDepartment = -1L;
		}
		correspondingDeps = getParameter(parameters, ReportTemplateParams.CORRRESPONDING_DEPS
				.getValue());
		if (correspondingDeps == null)
			correspondingDeps = "off"; // default value

		String idUserStr = getParameter(parameters, ReportTemplateParams.ID_USER.getValue());
		try {
			idUser = (idUserStr != null) ? Long.parseLong(idUserStr.trim()) : -1L;
		}
		catch (NumberFormatException e) {
			idUser = -1L;
		}
		String isDelinquencyStr = getParameter(parameters, "isDelinquency");
		try {
			isDelinquency = (isDelinquencyStr != null) ? Long.parseLong(isDelinquencyStr.trim()) : -1L;
		}
		catch (NumberFormatException e) {
			isDelinquency = -1L;
		}
		try {
			id_Claim_FromList = getParameter(parameters, "id_ClaimFromList").trim().toUpperCase();
		}
		catch (Exception e) {
			id_Claim_FromList = null;
		}

	}

	/**
	 * {@inheritDoc}
	 * @throws MappingException
	 */
	@Override
	protected void getData() throws MappingException {
		try {
			// was passed extra parameter id_Claim_FromList that is a real internal claim Id.
			if (id_Claim_FromList != null)
				idClaim = id_Claim_FromList;
			header = mapper.getHeaderData(idTypeProcess, idClaim, idDepartment, correspondingDeps,
					idUser, isDelinquency, mdtaskId);
			if (id_Claim_FromList != null) {
				if (!header.getCRM_claim_name().equals(header.getInternal_claim_name()))
					// CRM claim
					header.setParam_claim_name(header.getCRM_claim_name() + " ("
							+ header.getInternal_claim_name() + ")");
			}
			else if (!idClaim.equals("-1"))
				idClaim = header.getInternal_claim_name();
		}
		catch (Exception e) {
			header = new ActiveStagesReportHeader();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Element dataToXML() throws Exception {
		List<ProcessTypeJPA> processTypes;
		PupFacadeLocal pupFacade = com.vtb.util.EjbLocator.getInstance().getReference(
				PupFacadeLocal.class);

		// generate a list of process types
		if (idTypeProcess.longValue() == -1L) {
			if (idClaim.equals("-1")) {
				// all type processes are chosen
				processTypes = pupFacade.findProcessTypeList();

			}
			else {
				// if claim is set, not process type. Find an according process type
				processTypes = new ArrayList<ProcessTypeJPA>();
				try {
					Long prTypeId = null;
					if (mdtaskId != null)
						prTypeId = mapper.getProcessTypeIdByMdtaskId(mdtaskId);
					else
						prTypeId = mapper.getProcessTypeId(idClaim);
					ProcessTypeJPA found = pupFacade.getProcessTypeById(prTypeId);
					processTypes.add(found);
				}
				catch (Exception e) {
					e.getMessage();
					LOGGER.log(Level.SEVERE, "Ошибка при получении данных для отчета", e); 
				}
			}
		}
		else {
			// one process type is chosen
			processTypes = new ArrayList<ProcessTypeJPA>();
			try {
				ProcessTypeJPA found = pupFacade.getProcessTypeById(idTypeProcess);
				processTypes.add(found);
			}
			catch (Exception e) {}
		}

		ActiveStagesReport report = new ActiveStagesReport();

		// sets headers of the report.
		report.setHeaders(header);

		// cycle through a process types
		List<ActiveStagesReportProcessType> processes = new ArrayList<ActiveStagesReportProcessType>();
		for (ProcessTypeJPA processType : processTypes) {
			ActiveStagesReportProcessType process = new ActiveStagesReportProcessType();
			process.setIdProcessType(new Long(processType.getIdTypeProcess()));
			process.setDescription(processType.getDescriptionProcess());

			operations = mapper.getReportData(new Long(processType.getIdTypeProcess()), idClaim,
					idDepartment, correspondingDeps, idUser, isDelinquency, mdtaskId);
			List<String> available_users;
			// add missing values to the operation
			for (ActiveStagesReportOperation operation : operations) {
				try {
					List<String> assigned_list = mapper.getAssignedUsers(Long.parseLong(operation
							.getId_process()), Long.parseLong(operation.getId_status()), Long.parseLong(operation
							.getId_stage()));
					operation.setAssigned_users(assigned_list);
				}
				catch (NumberFormatException e) {}
				catch (NullPointerException e) {}
				catch (MappingException e) {}

				available_users = null;
				try {
					available_users = mapper.getAvailableUsers(operation);
					operation.setAcceptable_users(available_users);
				}
				catch (Exception e) {}
			}
			// sets operations of the process.
			process.setOperations(operations);
			processes.add(process);
		}
		report.setProcessTypes(processes);

		// collects all data and generates XML DOM.
		return report.toXML(document, rootElement);
	}
}
