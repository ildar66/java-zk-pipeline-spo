package org.uit.director.action.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.upload.FormFile;


/**
 * This class is a placeholder for form values.  In a multipart request, files are represented by
 * set and get methods that use the class org.apache.struts.uploader.FormFile, an interface with
 * basic methods to retrieve file information.  The actual structure of the FormFile is dependant
 * on the underlying impelementation of multipart request handling.  The default implementation
 * that struts uses is org.apache.struts.uploader.CommonsMultipartRequestHandler.
 *
 * @author Mike Schachter
 * @version $Revision: 1.1.1.3 $ $Date: 2005/11/02 06:17:39 $
 */

public class UploadForm extends ActionForm {

	private static final long serialVersionUID = 1L;

	/**
     * The file that the user has uploaded
     */
    private FormFile uploadFile;
    private boolean isUpdate;
    private String commit;

    public UploadForm() {
        super();
        resetFields();
    }

    /**
     * Called by the framework to validate the user has entered the
     * accessNumber and pin fields.
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest req) {
        ActionErrors errors = new ActionErrors();

        // Check and see if the access number is missing
        if (uploadFile == null || uploadFile.getFileSize() == 0) {
            errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("uploadForm.exception"));
        }

        // Return the ActionErrors, in any.
        return errors;
    }

    /**
     * Called by the framework to reset the fields back to their default values.
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        // Clear out the access number and pin number fields
        resetFields();
    }

    /**
     * Reset the fields back to their defaults.
     */
    protected void resetFields() {
        this.uploadFile = null;
    }

    /**
     * Retrieve a representation of the file the user has uploaded
     */
    public FormFile getUploadFile() {
        return this.uploadFile;
    }

    /**
     * Set a representation of the file the user has uploaded
     */
    public void setUploadFile(FormFile uploadFile) {
        this.uploadFile = uploadFile;
    }
    
    
	/**
	 * @return Returns the isUpdate.
	 */
	public boolean getIsUpdate() {
		return isUpdate;
	}
	/**
	 * @param isUpdate The isUpdate to set.
	 */
	public void setIsUpdate(boolean isUpdate) {
		this.isUpdate = isUpdate;
	}

	public String getCommit() {
		return commit;
	}

	public void setCommit(String commit) {
		this.commit = commit;
	}
	
	
}
