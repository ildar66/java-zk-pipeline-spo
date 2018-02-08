package ru.md.jsp.tag;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * класс загружаемых асинхронно секций
 */
public class FrameTag extends TagSupport {
	private static final long serialVersionUID = 1L;

	private Boolean empty = false;
	private Boolean readOnly = false;
	private String frame_name = "name";
	private String mdtaskid = "0";
	private String mdtask = "0";
	private String pupTaskId = "0";
	private String header;
	private String viewType = "";

	/**
	 * doStartTag is called by the JSP container when the tag is encountered {@inheritDoc}
	 */
	public int doStartTag() {
		// if (empty && readOnly) return SKIP_BODY;
		// Необходимо показывать все секции формы заявки на любой операции бизнес процесса независимо от
		// их заполненности Согласно новому "ФТпо СПО_МД_24.08.2012" п. 3.8.1:
		BufferedReader reader = null;
		try {
			StringBuffer template = new StringBuffer();
			reader = new BufferedReader(new InputStreamReader(pageContext.getServletContext()
					.getResourceAsStream("/templates/frame.html"), "UTF-8"));
			String text = null;
			while ((text = reader.readLine()) != null) {
				template.append(text).append(System.getProperty("line.separator"));
			}
			String params = "mdtaskid=" + mdtaskid + "&readOnly=" + readOnly + "&pupTaskId=" + pupTaskId
					+ "&mdtask=" + mdtask
					+ ((viewType != null && !viewType.isEmpty()) ? "&viewtype=" + viewType : "");
			pageContext.getOut().print(
					MessageFormat.format(template.toString(), frame_name, header, empty ? "class=\"empty\""
							: "", "frameToggle('" + params + "','" + frame_name + "')"));
		}
		catch (Exception ex) {
			ex.printStackTrace();
			throw new Error("SelectTag doEndTag error " + ex.getMessage());
		}
		finally {
			try {
				if (reader != null) {
					reader.close();
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return SKIP_BODY;
	}

	/**
	 * doEndTag is called by the JSP container when the tag is closed {@inheritDoc}
	 */
	public int doEndTag() {
		try {
			JspWriter out = pageContext.getOut();
			out.print("");
		}
		catch (Exception ex) {
			ex.printStackTrace();
			throw new Error("SelectTag doEndTag error " + ex.getMessage());
		}
		return 0;
	}

	/**
	 * @return empty
	 */
	public Boolean getEmpty() {
		return empty;
	}

	/**
	 * @param empty empty
	 */
	public void setEmpty(Boolean empty) {
		this.empty = empty;
	}

	/**
	 * @return frame_name
	 */
	public String getFrame_name() {
		return frame_name;
	}

	/**
	 * @param frame_name frame_name
	 */
	public void setFrame_name(String frame_name) {
		this.frame_name = frame_name;
	}

	/**
	 * @return mdtaskid
	 */
	public String getMdtaskid() {
		return mdtaskid;
	}

	/**
	 * @param mdtaskid mdtaskid
	 */
	public void setMdtaskid(String mdtaskid) {
		this.mdtaskid = mdtaskid;
	}

	/**
	 * @return header
	 */
	public String getHeader() {
		return header;
	}

	/**
	 * @param header header
	 */
	public void setHeader(String header) {
		this.header = header;
	}

	/**
	 * @return readOnly
	 */
	public Boolean getReadOnly() {
		return readOnly;
	}

	/**
	 * @param readOnly readOnly
	 */
	public void setReadOnly(Boolean readOnly) {
		this.readOnly = readOnly;
	}

	/**
	 * @return pupTaskId
	 */
	public String getPupTaskId() {
		return pupTaskId;
	}

	/**
	 * @param pupTaskId pupTaskId
	 */
	public void setPupTaskId(String pupTaskId) {
		this.pupTaskId = pupTaskId;
	}

	/**
	 * @return mdtask
	 */
	public String getMdtask() {
		return mdtask;
	}

	/**
	 * @param mdtask mdtask
	 */
	public void setMdtask(String mdtask) {
		this.mdtask = mdtask;
	}

	/**
	 * Возвращает {@link String} viewType
	 * @return {@link String} viewType
	 */
	public String getViewType() {
		return viewType;
	}

	/**
	 * Устанавливает {@link String} viewType
	 * @param viewType {@link String} viewType
	 */
	public void setViewType(String viewType) {
		this.viewType = viewType;
	}

}