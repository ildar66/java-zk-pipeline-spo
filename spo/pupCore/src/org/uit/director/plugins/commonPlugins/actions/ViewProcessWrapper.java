/*
 * Created on 01.10.2007
 * 
 */
package org.uit.director.plugins.commonPlugins.actions;

import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.plugins.PluginInterface;

import ru.masterdm.compendium.domain.User;
import ru.masterdm.compendium.model.CompendiumActionProcessor;
import ru.md.spo.ejb.PupFacadeLocal;

import com.vtb.domain.Task;
import com.vtb.exception.FactoryException;
import com.vtb.model.ActionProcessorFactory;
import com.vtb.model.TaskActionProcessor;
import com.vtb.util.ApplProperties;

public class ViewProcessWrapper implements PluginInterface {

	private static final Logger LOGGER = Logger.getLogger(ViewProcessWrapper.class.getName());
	
	HttpServletRequest request;
	
	HttpServletResponse response;

	WorkflowSessionContext wsc;

	Long idProcess;

	@SuppressWarnings("unchecked")
    public void init(WorkflowSessionContext wsc, List params) {

		request = (HttpServletRequest) params.get(2);
		String idPrStr = request.getParameter("idProcess");
		if (idPrStr != null) {
			idProcess = Long.valueOf(idPrStr);
		}
		response = (HttpServletResponse) params.get(3);
		this.wsc = wsc;
	}

	public String execute() {
		try {
			LOGGER.info("derived idProcess = " + idProcess);
			
			String viewProcessUrl = "plugin.action.do?class=".concat(ViewProcess.class.getName()).concat("&idProcess=").concat(idProcess.toString());
			String jsUrl = "scripts/scheme.js";
			
			StringBuffer output = new StringBuffer("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /><title>Схема выполнения заявки");
			
			try {
				TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
				Task task = processor.findByPupID(idProcess, false);
				
				output = output.append(" №" + task.getNumberDisplay());
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
				e.printStackTrace();
			}
			
			output = output.append("</title><script src=\"")
				.append(jsUrl)
				.append("\" ></script>")
				.append("</head><body>")
				.append("<img style=\"zoom: 50%\" id=\"process\" onclick=\"zoomer('process')\" src=\"")
				.append(viewProcessUrl)
				.append("\" /></body></html>");
			
			response.setCharacterEncoding("utf-8");
			
			PrintWriter out = response.getWriter(); 
			out.print(output.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
 	/**
 	 * @return базовый URL для FlexWorkFlow. Учитывает разные адреса для пользователей в ГО и филиалах
 	 * @throws MalformedURLException
 	 * @throws FactoryException 
 	 */
 	private String getBaseURL(HttpServletRequest request) throws MalformedURLException, FactoryException {
 		URL constractedURL = new URL(request.getScheme(), request.getServerName(), request.getServerPort(), "");
 		CompendiumActionProcessor compendium = (CompendiumActionProcessor) 
 			ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("Compendium");
 		Integer depid = compendium.findUser(new User(wsc.getIdUser().intValue())).getDepartmentID();
 		compendium.getMqSettingsForDepartment(depid).getFileHostType();
 		String baseUrl = "";
 		PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
 		if (pupFacadeLocal.isUseSA() &&compendium.getMqSettingsForDepartment(depid).getFileHostType().equals("1")) {//филиал
 		   baseUrl = pupFacadeLocal.getBaseURL(wsc.getIdUser().longValue()); 
 		} else {
 		   baseUrl = constractedURL.toString().concat("/").concat(ApplProperties.getwebcontextFWF());
 		}
 		return baseUrl;
 	}
}