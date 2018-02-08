package ru.md.servlet;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.uit.director.action.AbstractAction;

import com.vtb.domain.AttachmentFile;
import com.vtb.exception.FactoryException;
import com.vtb.model.ActionProcessorFactory;
import com.vtb.model.AttachmentActionProcessor;
import com.vtb.util.Formatter;

import ru.md.pup.dbobjects.AttachJPA;
import ru.md.pup.dbobjects.DocumentGroupJPA;
import ru.md.pup.dbobjects.DocumentTypeJPA;
import ru.md.pup.dbobjects.UserJPA;
import ru.md.spo.ejb.PupFacadeLocal;
import ru.md.spo.ejb.TaskFacadeLocal;
import ru.md.spo.util.Config;

/**
 * Загрузка документа. 
 * @author Andrey Pavlenko
 */
public class UploadAttachAction extends Action {
    private static final Logger LOGGER = Logger.getLogger(AcceptAttachAction.class.getName());
    
    private static final String URL_CONTENT_TYPE = "application/octet-stream";
    
    /**
     * Устанавливает значение свойства в {@link AttachJPA объект}
     *
     * @param a {@link AttachJPA}
     * @param name наименование поля
     * @param val значение поля
     * @throws FactoryException {@link FactoryException ошибка}
     */
    private void setField(AttachJPA a, String name, String val) throws FactoryException{
        //заявка и ownertype 
    	
    	LOGGER.info("=======UploadAttachAction.setField(a, name, val) name '" + name + "', val '" + val + "'");
    	
    	if (val == null || val.equals("null") || val.trim().isEmpty())
    		return;
    	
        if (name.equals("owner")) a.setID_OWNER(val);
        if (name.equals("owner_type")) a.setOWNER_TYPE(Formatter.parseLong(val));
        if (name.equals("sign")) a.setSIGNATURE(val.getBytes());
        if (name.equals("type")) {
            Long id = Formatter.parseLong(val);
            if(id==null) return;
            PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
            DocumentTypeJPA dt = pupFacadeLocal.getDocumentType(id);
            a.setDocumentType(dt);
            a.setFILETYPE(dt.getName());
            a.setFORCC(dt.getForcc());
        }
        if (name.equals("group")) a.setGroup(new DocumentGroupJPA(Formatter.parseLong(val)));
        if (name.equals("file_expdate")) a.setDATE_OF_EXPIRATION(Formatter.parseDate(val));
        if (name.equals("title")) a.setTitle(val);
        if (name.equals("reason")) a.setReason(val);
        if (name.equals("type_name")) a.setFILETYPE(val);
        if (name.equals("url")) a.setFILEURL(val.trim());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
    	AttachJPA a = new AttachJPA();
        try {
        	response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            
            PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
            TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
            boolean isMultipart = ServletFileUpload.isMultipartContent(request);
            if (!isMultipart) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return null;
            }
            
            Long userid = AbstractAction.getWorkflowSessionContext(request).getIdUser();
            
            a.setUnid(java.util.UUID.randomUUID().toString());
            a.setDATE_OF_ADDITION(new Date());
            a.setWhoAdd(new UserJPA(userid));
            a.setISACCEPTED(0l);
            a.setGroup(null);
            
            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setSizeThreshold(1024*1024);// Максимальный размер буфера данных в байтах
            ServletFileUpload upload = new ServletFileUpload(factory);
            
            @SuppressWarnings("unchecked")
            List<FileItem> items = upload.parseRequest(request);
            
            byte[] filedata = null;
            for (FileItem item : items) {
                if (item.isFormField()) {//если принимаемая часть данных является полем формы
                    setField(a,item.getFieldName(),item.getString("UTF-8"));
                } else {//в противном случае рассматриваем как файл
                    LOGGER.info(item.getContentType()+" "+item.getName());
                    a.setCONTENTTYPE(item.getContentType());
                    a.setFILENAME(item.getName());
                    filedata = item.get();
                }
            }
            
            if (a.getCONTENTTYPE() == null)
            	a.setCONTENTTYPE(URL_CONTENT_TYPE);
            if (!a.getSIGNATURE().isEmpty()) {
                a.setWhoSign(new UserJPA(userid));
                a.setDate_of_sign(new Date());
            }
            
            LOGGER.info("=======UploadAttachAction.execute(mapping, form, request, response) signature is not null '" + (a.getSIGNATURE() != null) + "', a.isFILEURL '" + a.isFILEURL() + "', Config.devMode '" + Config.devMode() + "', fileExists '" + (filedata!=null && filedata.length > 0) + "'" );
            
            pupFacadeLocal.persist(a);
            if(filedata!=null){
                AttachmentActionProcessor processor = (AttachmentActionProcessor) ActionProcessorFactory.getActionProcessor("Attachment");
                AttachmentFile file = new AttachmentFile(a.getUnid());
                file.setFilename(a.getFILENAME());
                file.setFiledata(filedata);
                processor.updateAttachmentData(file);
            }

            String ans = getJsonAnswer(a);
            
            response.getWriter().write(ans);
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            
            String answer = getJsonAnswer(a, e);
            
            response.getWriter().write(answer);
//            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, answer);
            
            return null;
        }
    }
    
    /**
     * Возвращает json-ответ по {@link AttachJPA}
     *
     * @param a {@link AttachJPA}
     * @return json-ответ
     */
    private String getJsonAnswer(AttachJPA a) {
    	String answer = "{\"files\": [{\"type_name\": \"%s\",\"file_expdate\": \"%s\",\"title\": \"%s\",\"name\": \"%s\",\"url\": \"%s\"}]}";
    	
    	String typeName = a.getFILETYPE();
    	
    	Date fileExpdate = a.getDATE_OF_EXPIRATION();
    	SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
    	String fileExpdateStr = fileExpdate != null ? sdf.format(fileExpdate) : "";
    	
    	String name = a.getFILENAME();
    	String title = a.getTitle();
    	String url = "";
    	if (a.isFILEURL())
    		url = a.getFILEURL();
    	
    	String result = String.format(answer, typeName, fileExpdateStr, title, name, url);
    	return result;
    }
    
    /**
     * Возвращает json-ответ по {@link AttachJPA}
     *
     * @param a {@link AttachJPA}
     * @return json-ответ
     */
    private String getJsonAnswer(AttachJPA a, Exception e) {
    	String answer = "{\"files\": [{\"type_name\": \"%s\",\"file_expdate\": \"%s\",\"title\": \"%s\",\"name\": \"%s\",\"url\": \"%s\",\"error\": \"%s\"}]}";
    	
    	String typeName = a.getFILETYPE();
    	
    	Date fileExpdate = a.getDATE_OF_EXPIRATION();
    	SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
    	String fileExpdateStr = fileExpdate != null ? sdf.format(fileExpdate) : "";
    	
    	String name = a.getFILENAME();
    	String title = a.getTitle();
    	String url = "";
    	if (a.isFILEURL())
    		url = a.getFILEURL();
    	
    	SimpleDateFormat sdferr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	String curTime = sdferr.format(new Date());
    	
    	
    	StringBuilder message = null;
    	if (e != null) {
	    	message = new StringBuilder();
	    	message.append(curTime).append(" ").append(e.getMessage());
    	}
    	
    	String result = String.format(answer, typeName, fileExpdateStr, title, name, url, message != null ? message.toString() : null);
    	return result;
    }
}
