package org.uit.director.action;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.CheckSchema;

import org.apache.crimson.tree.XmlDocument;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.uit.director.action.forms.UploadForm;
import org.uit.director.contexts.WPC;
import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.db.dbobjects.ProcessPacketBean;
import org.uit.director.db.ejb.DBFlexWorkflowCommon;
import org.uit.director.decider.BusinessProcessDecider;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ru.md.spo.util.Config;

import com.ibm.xslt4j.regexp.RE;
import com.ibm.xslt4j.regexp.RESyntaxException;

public class UploadProcessAction extends Action {
	
	private static final Logger logger = Logger.getLogger(UploadProcessAction.class.getName());
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		logger.info("FlexWorkflow: UploadAction action...");

		WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
		if (wsc.isNewContext())
			return (mapping.findForward("start"));

		String target = "errorPage";
		if (!wsc.isAdmin()) {
			wsc.setErrorMessage("Нет прав администратора");
			return mapping.findForward(target);
		}

		if (form instanceof UploadForm) {

			// this line is here for when the input page is upload-utf8.jsp,
			// it sets the correct character encoding for the response
			String encoding = request.getCharacterEncoding();
			if ((encoding != null) && (encoding.equalsIgnoreCase("utf-8"))) {
				response.setContentType("text/html; charset=utf-8");
			}

			UploadForm theForm = (UploadForm) form;

			// retrieve the file representation
			FormFile file = theForm.getUploadFile();

			// retrieve the file name
			// String fileName = file.getFileName();

			try {
				wsc.beginUserTransaction();

				InputStream stream = file.getInputStream();
				ZipFile zipfile = BusinessProcessDecider.writeToZipFile(stream);
				String resultLoad = loadProcessZipPacket(wsc, zipfile, false, request.getRemoteAddr());

				if (resultLoad.startsWith("ok")) {
					target = "textPage";
					//wsc.setWarningMessage("Загрузка нового процесса прошла успешно.");
					wsc.setPageData("Загрузка нового процесса прошла успешно.");
					wsc.commitUserTransaction();
				} else {
					if (resultLoad.startsWith("warning")) {
						request.setAttribute("actionFrom", "commit.upload.do");
						request.setAttribute("message", resultLoad);
						request.getSession().setAttribute("zipfile", zipfile);
						wsc.rollBackUserTransaction();
						target = "commitPage";

					} else {
						target = "errorPage";
						wsc.setErrorMessage(resultLoad);
						wsc.rollBackUserTransaction();
					}
				}

			} catch (FileNotFoundException fnfe) {
				logger.log(Level.SEVERE, fnfe.getMessage(), fnfe);
				fnfe.printStackTrace();
				
				target = "errorPage";
				wsc.setErrorMessage("Файл не найден");
				wsc.rollBackUserTransaction();
				return mapping.findForward(target);
			} catch (IOException ioe) {
				logger.log(Level.SEVERE, ioe.getMessage(), ioe);
				ioe.printStackTrace();
				
				target = "errorPage";
				wsc.rollBackUserTransaction();
				wsc.setErrorMessage("Ошибка ввода вывода");
			} /*
				 * catch (SecurityException e) { wsc.rollBackUserTransaction();
				 * wsc.setErrorMessage("Ошибка "); e.printStackTrace(); }
				 *//*
				 * catch (IllegalStateException e) {
				 * wsc.rollBackUserTransaction(); e.printStackTrace(); }
				 */
			catch (SAXException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
				e.printStackTrace();
				
				wsc.rollBackUserTransaction();
				wsc.setErrorMessage("Ошибка преобразования XML");
			} catch (RESyntaxException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
				e.printStackTrace();
				
				wsc.rollBackUserTransaction();
				wsc.setErrorMessage("Ошибка распарсивания регулярного выражения");
			} catch (SQLException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
				e.printStackTrace();
				
				wsc.rollBackUserTransaction();
				wsc.setErrorMessage("Ошибка SQL операции");
			}
			// destroy the temporary file created
			file.destroy();

			// return a forward to display.jsp
			return mapping.findForward(target);
		}

		return mapping.findForward(target);
	}

	public static String loadProcessZipPacket(WorkflowSessionContext wsc,
			ZipFile zipfile, boolean isUpdate, String remoteAddr)
			throws IOException, SAXException, RESyntaxException, SQLException {
		String res = "error";

		Map<String, Object> xmlDefinitions = new HashMap<String, Object>();
		String resGetXMLDef = BusinessProcessDecider.getMapXMLDefinitions(zipfile, xmlDefinitions);
		if (!resGetXMLDef.equals("ok")) return resGetXMLDef;

		String resCheck = "ok";
		
		if (Config.getProperty("CHECK_UPLOAD_PACKET").equalsIgnoreCase("true")) {
			resCheck = CheckSchema.checkUploadedPacket(
		        (XmlDocument) xmlDefinitions.get(WPC.PROCESS_DEFINITION),
				(XmlDocument) xmlDefinitions.get(WPC.ATTRIBUTES_DEFINITION),
				(XmlDocument) xmlDefinitions.get(WPC.STAGES_DEFINITION),
				(XmlDocument) xmlDefinitions.get(WPC.ROLES_DEFINITION),
				(XmlDocument) xmlDefinitions.get(WPC.SUB_PROCESS_DEFINITION), 
				"<br>");
		}

		/*
		 * BusinessProcessDecider .checkUploadedPacket(xmlDefinitions);
		 */
		if (!resCheck.equalsIgnoreCase("ok")) return resCheck;

		String messageForCommitUpdate = "";

		String checkUnicum = BusinessProcessDecider.checkExistsNameProcesses(
				zipfile, 
				(XmlDocument) xmlDefinitions.get(WPC.PROCESS_DEFINITION),
				(XmlDocument) xmlDefinitions.get(WPC.SUB_PROCESS_DEFINITION));

		if (!isUpdate && !checkUnicum.equals("")) {
			messageForCommitUpdate = "warning: <br> " + checkUnicum + "Хотите обновить?";
			return messageForCommitUpdate;
		}

		String loadResult = loadProcessPacket(wsc, xmlDefinitions, remoteAddr);

		Integer idTypeProcess = null;
		Long idTransaction = null;

		if (!loadResult.startsWith("ok")) {
			return loadResult;
		} else {
			RE re = new RE("ID_PROCESS=(\\d+); ID_TRANSACTION=(\\d+)");
			if (re.match(loadResult)) {
				String idStr = re.getParen(1);
				idTypeProcess = Integer.valueOf(idStr);
				idTransaction = Long.valueOf(re.getParen(2));
			}
		}

		String resLoadSubProc = loadSubProcesses(wsc, zipfile,
				(XmlDocument) xmlDefinitions.get(WPC.SUB_PROCESS_DEFINITION),
				isUpdate, remoteAddr, idTypeProcess);

		if (!resLoadSubProc.equalsIgnoreCase("ok")) {
			if (resLoadSubProc.startsWith("warning")) {
				messageForCommitUpdate += resLoadSubProc;
			} else {
				return resLoadSubProc;
			}
		}

		res = messageForCommitUpdate.equals("") ? loadResult
				: messageForCommitUpdate;

		return res;
	}

	private static String loadSubProcesses(WorkflowSessionContext wsc,
			ZipFile zipfile, XmlDocument subProcDef, boolean isUpdate,
			String remoteAddr, Integer idTypeProcess) throws IOException, SAXException, RESyntaxException, SQLException {

		String res = "error";
		String warnings = "";

		if (subProcDef != null) {
			NodeList sbTags = subProcDef.getElementsByTagName("subProcess");
			for (int i = 0; i < sbTags.getLength(); i++) {
				Node sb = sbTags.item(i);
				String fileName = sb.getAttributes().getNamedItem("fileName").getNodeValue();

				InputStream archStream = zipfile.getInputStream(zipfile.getEntry(fileName));

				ZipFile zipFile = BusinessProcessDecider.writeToZipFile(archStream);

				String loadRes = loadProcessZipPacket(wsc, zipFile, isUpdate, remoteAddr);

				Long idTransaction = null;
				if (!loadRes.startsWith("ok")) {
					if (loadRes.startsWith("warning")) {
						warnings += loadRes;
					} else {
						return loadRes;
					}
				} else {

					RE re = new RE("ID_PROCESS=(\\d+); ID_TRANSACTION=(\\d+)");
					if (re.match(loadRes)) {
						idTransaction = Long.valueOf(re.getParen(2));
					}
				}

				String nameSP = sb.getAttributes().getNamedItem("name").getNodeValue();

				boolean isInVars = false;
				ArrayList<Object[]> paramsForLoadInVars = 
				    (ArrayList<Object[]>) BusinessProcessDecider.getParamsForLoadMapVars(sb, idTypeProcess, nameSP, isInVars);
				ArrayList<Object[]> paramsForLoadOutVars = 
				    (ArrayList<Object[]>) BusinessProcessDecider.getParamsForLoadMapVars(sb, idTypeProcess, nameSP, !isInVars);

				ArrayList<Object[]> paramsForLoadMapRoles = 
				    (ArrayList<Object[]>) BusinessProcessDecider.getParamsForLoadMapRoles(sb, idTypeProcess, nameSP);

				Object[] par = new Object[2];
				par[0] = idTypeProcess;
				par[1] = nameSP;
				wsc.getDbManager().getDbFlexDirector().loadSubProcessData(
						paramsForLoadInVars, paramsForLoadOutVars,
						paramsForLoadMapRoles, par, idTransaction);

			}
		}

		res = warnings.equals("") ? "ok" : warnings;

		return res;
	}

	/**
	 * Десериализует процесс в базу данных.
	 * @param wsc
	 * @param documents
	 * @param ipAddress
	 * @return строка error или ok
	 * @author Ижевцы
	 */
	@SuppressWarnings("unchecked")
	private static String loadProcessPacket(WorkflowSessionContext wsc, Map documents, String ipAddress) {

		String res = "error";
		DBFlexWorkflowCommon dbFlexWorkflow = wsc.getDbManager().getDbFlexDirector();

		try {
			XmlDocument processDef = (XmlDocument) documents.get(WPC.PROCESS_DEFINITION);
			XmlDocument attributesDef = (XmlDocument) documents.get(WPC.ATTRIBUTES_DEFINITION);
			XmlDocument stagesDef = (XmlDocument) documents.get(WPC.STAGES_DEFINITION);
			XmlDocument rolesDef = (XmlDocument) documents.get(WPC.ROLES_DEFINITION);
			XmlDocument subProcessDef = (XmlDocument) documents.get(WPC.SUB_PROCESS_DEFINITION);

			String textImageDef =  (String) documents.get(WPC.IMAGE_DEFINITION);
            StringWriter stringWriter = new StringWriter();
			processDef.write(stringWriter);
			String textProcessDef = stringWriter.toString();

			NamedNodeMap attrProcess = null;
			NodeList chN = processDef.getChildNodes();
			for (int i = 0; i < chN.getLength(); i++) {

				if (chN.item(i) instanceof Element) {
					if (chN.item(i).hasAttributes()) {
						attrProcess = chN.item(i).getAttributes();
						break;
					}
				}

			}

			String nameTypeProcess = attrProcess.getNamedItem("name")
					.getNodeValue();

			int countDay = Integer.parseInt(attrProcess.getNamedItem("limit").getNodeValue());

			ArrayList<Object[]> paramsForLoadAttributes = 
			    BusinessProcessDecider.getParamsForLoadAttributes(attributesDef);

			HashMap<String, HashMap<String, String>> paramsForLoadDataSources = 
			    BusinessProcessDecider.getParamsForLoadDataSources(attributesDef);

			ArrayList<Object[]> paramsForLoadStages = 
			    BusinessProcessDecider.getParamsForLoadStages(stagesDef);

			Object[] parRoles = 
			    BusinessProcessDecider.getParamsForLoadRoles(rolesDef, nameTypeProcess, wsc.getIdUser());

			ArrayList<Object[]> paramsForLoadRoles/*список ролей*/ = (ArrayList<Object[]>) parRoles[0];
			ArrayList<Object[]> paramsForLoadRolesNodes = (ArrayList<Object[]>) parRoles[1];

			ArrayList<Object[]> paramsForLoadStagesInRole = 
			    BusinessProcessDecider.getParamsForLoadStagesInRole(rolesDef);

			ArrayList<Object[]> paramsForLoadRolesPermissions = 
			    BusinessProcessDecider.getParamsForLoadRolesPermissions(rolesDef);

			ArrayList<Object[]> paramsForLoadStagesPermissions = 
			    BusinessProcessDecider.getParamsForLoadStagesPermissions(stagesDef);

			ArrayList<Object[]> paramsForLoadVarConnections = 
			    BusinessProcessDecider.getParamsForLoadVarConnections(attributesDef);

			ArrayList<Object[]> paramsForLoadVarNodes = 
			    BusinessProcessDecider.getParamsForLoadVarNodes(attributesDef);

			ArrayList<Object[]> paramsForLoadSelectVarValues = 
			    BusinessProcessDecider.getParamsForLoadSelectVarValues(attributesDef);

			Object[] edgesParams = 
			    BusinessProcessDecider.getParamsForLoadEdges(processDef);
			ArrayList<Object[]> paramsForLoadEdges = (ArrayList<Object[]>) edgesParams[0];
			ArrayList<Object[]> paramsForLoadEdgeVars = (ArrayList<Object[]>) edgesParams[1];

			ProcessPacketBean packetBean = new ProcessPacketBean();

			packetBean.setCountDays(countDay);
			packetBean.setImageXML(textImageDef);
			packetBean.setIpAddress(ipAddress);
			packetBean.setNameTypeProcess(nameTypeProcess);
			packetBean.setParamsForLoadAttributes(paramsForLoadAttributes);
			packetBean.setParamsForLoadDataSources(paramsForLoadDataSources);
			packetBean.setParamsForLoadRoles(paramsForLoadRoles);
			packetBean.setParamsForLoadStages(paramsForLoadStages);
			packetBean.setParamsForLoadStagesInRole(paramsForLoadStagesInRole);

			packetBean.setParamsForLoadRolesPermissions(paramsForLoadRolesPermissions);
			packetBean.setParamsForLoadStagesPermissions(paramsForLoadStagesPermissions);
			packetBean.setParamsForLoadVarConnections(paramsForLoadVarConnections);
			packetBean.setParamsForLoadVarNodes(paramsForLoadVarNodes);
			packetBean.setParamsForLoadSelectVarValues(paramsForLoadSelectVarValues);

			packetBean.setParamsForLoadRolesNodes(paramsForLoadRolesNodes);

			packetBean.setParamsForLoadEdges(paramsForLoadEdges);
			packetBean.setParamsForLoadEdgeVars(paramsForLoadEdgeVars);

			packetBean.setProcessXML(textProcessDef);
			packetBean.setIdUser(wsc.getIdUser());

			Object[] resLoad = dbFlexWorkflow.loadProcessPacket(packetBean);
			
			if (!resLoad[0].equals("ok")) {
				return (String) resLoad[0];

			}
			;
			res = "ok. ID_PROCESS=" + resLoad[1]
					+ "; ID_TRANSACTION="
					+ resLoad[2];

		} catch (Exception e) {
			// dbFlex.rollback();
			e.printStackTrace();

		} finally {
			// dbFlex.setAutoCommit(true);
		}

		return res;

	}

}