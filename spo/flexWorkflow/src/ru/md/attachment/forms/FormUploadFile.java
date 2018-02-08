package ru.md.attachment.forms;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import com.vtb.domain.Attachment;
import com.vtb.util.Formatter;

/**
 * Form bean for a Struts application.
 * @version 	1.0
 * @author
 */
public class FormUploadFile extends ActionForm {
	private static final Logger logger = Logger.getLogger(FormUploadFile.class.getName());
	
    private static final long serialVersionUID = 1L;
    private String unid = "";
	private String attachName = "";
	private Long id_group;
	private Long fileGroupId;
	private FormFile attachment = null;
	
	private String fileGroup = "";
	private String filetype = "";
	private String id_appl;
	private String file_expdate;
	private String signature;
	private Long fileIdType;
	
   public Attachment getUploadedAttachment() {
        Attachment attachment = null;
        try {
            logger.info("init Attachment object");
            attachment = new Attachment(unid, attachName, filetype, fileGroup, 
                    (((fileGroupId) != null) ? String.valueOf(fileGroupId) : null), 
                    id_appl, id_group, fileIdType);
            if (signature != null) logger.info("signature = '"+signature+"'");
            attachment.setSignature(signature);
            
            if (file_expdate.equalsIgnoreCase("") || file_expdate == null)
                attachment.setDateOfExpiration(null);
            else 
                attachment.setDateOfExpiration(Formatter.convertToDate(file_expdate).getTime());
        } catch (Exception e) {
            logger.info("Exception occurred when init Attachment object");
            logger.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
        }
        return attachment;
    }
	
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        //this.setAttachment1(null);
    }

    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        logger.info("Validation started");
        if (this.unid == null || this.unid.equals("")) {
            if (this.getAttachment() == null || this.getAttachment().getFileName().equals("")) {
                errors.add("Uploaded file not found", new org.apache.struts.action.ActionError("error.message"));
            } else {
                this.setAttachName(this.getAttachment().getFileName());
                logger.info("Validate: upload name=" + this.getAttachName());
            }
        }
                
        logger.info("Validate OK");
        return errors;
    }

	public String getFile_expdate() {
		return file_expdate;
	}

	public void setFile_expdate(String file_expdate) {
		this.file_expdate = file_expdate;
	}
	
	public String getId_appl() {
		return id_appl;
	}

	public void setId_appl(String id_appl) {
		this.id_appl = id_appl;
	}

	public String getFiletype() {
		return filetype;
	}

	public void setFiletype(String filetype) {
		this.filetype = filetype;
	}

	public String getUnid() {
		return unid;
	}

	public void setUnid(String unid) {
		this.unid = unid;
	}
	
	public FormFile getAttachment() {
		return attachment;
	}

	public void setAttachment(FormFile attachment) {
		this.attachment = attachment;
	}
	
	public String getAttachName() {
		return attachName;
	}

	public void setAttachName(String attachName) {
		this.attachName = attachName;
	}
	
	public Long getId_group() {
		return id_group;
	}

	public void setId_group(Long id_group) {
		this.id_group = id_group;
	}
	
	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

    public String getFileGroup() {
        return fileGroup;
    }

    public void setFileGroup(String filegroup) {
        this.fileGroup = filegroup;
    }

    public Long getFileGroupId() {
        return fileGroupId;
    }

    public void setFileGroupId(Long filegroupid) {
        this.fileGroupId = filegroupid;
    }
    
    public void setFileGroupId(String filegroupid) {
        if ((filegroupid != null) && (!"".equals(filegroupid)))
            this.fileGroupId = Long.parseLong(filegroupid);
    }

    public Long getFileIdType() {
        return fileIdType;
    }

    public void setFileIdType(Long fileIdType) {
        this.fileIdType = fileIdType;
    }
    
    public void setFileIdType(Integer fileIdType) {
        this.fileIdType = new Long(fileIdType.longValue());
    }
}
