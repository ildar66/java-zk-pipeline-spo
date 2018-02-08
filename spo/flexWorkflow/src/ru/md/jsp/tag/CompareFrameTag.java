package ru.md.jsp.tag;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Компонент асинхронной загрузки блока сравнения
 * @author rislamov
 */
public class CompareFrameTag extends TagSupport {
	private static final long serialVersionUID = 1L;

	private Boolean empty = false;
	private String name;
	private String header;
	private String ids;
	private String objectType;
	private boolean readOnly = true;
	private String current;

	/**
	 * {@inheritDoc}
	 */
	public int doStartTag() {
		BufferedReader reader = null;
		try {
			StringBuffer template = new StringBuffer();
			reader = new BufferedReader(new InputStreamReader(pageContext.getServletContext()
					.getResourceAsStream("/templates/frame.html"), "UTF-8"));
			String text = null;
			while ((text = reader.readLine()) != null) {
				template.append(text).append(System.getProperty("line.separator"));
			}
			String params = "ids=" + ids + "&objectType=" + objectType + "&name=" + name 
					+ "&readOnly=" + readOnly + "&current=" + current;
			String _ids = ids.replaceAll("\\|", "_");
			pageContext.getOut().print(
					MessageFormat.format(template.toString(), name + "_" + _ids, header, empty ? "class=\"empty\""
							: "", "frameToggleCompare('" + params + "','" + name + "_" + _ids + "')"));
		}
		catch (Exception ex) {
			ex.printStackTrace();
			throw new Error(this.getClass().getSimpleName() + " doEndTag error " + ex.getMessage());
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
	 * {@inheritDoc}
	 */
	public int doEndTag() {
		try {
			JspWriter out = pageContext.getOut();
			out.print("");
		}
		catch (Exception ex) {
			ex.printStackTrace();
			throw new Error(this.getClass().getSimpleName() + " doEndTag error " + ex.getMessage());
		}
		return 0;
	}

	/**
	 * Возвращает {@link Boolean пустой ли} блок
	 * @return {@link Boolean пустой ли} блок
	 */
	public Boolean getEmpty() {
		return empty;
	}

	/**
	 * Устанавливает {@link Boolean пустой ли} блок
	 * @param empty {@link Boolean пустой ли} блок
	 */
	public void setEmpty(Boolean empty) {
		this.empty = empty;
	}

	/**
	 * Возвращает {@link String имя} блока
	 * @return {@link String имя} блока
	 */
	public String getName() {
		return name;
	}

	/**
	 * Устанавливает {@link String имя} блока
	 * @param name {@link String имя} блока
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Возвращает {@link String заголовок} блока
	 * @return {@link String заголовок} блока
	 */
	public String getHeader() {
		return header;
	}

	/**
	 * Устанавливает {@link String заголовок} блока
	 * @param header {@link String заголовок} блока
	 */
	public void setHeader(String header) {
		this.header = header;
	}

	/**
	 * Возвращает boolean только для чтения
	 * @return boolean только для чтения
	 */
	public boolean isReadOnly() {
		return readOnly;
	}

	/**
	 * Устанавливает boolean только для чтения
	 * @param readOnly boolean только для чтения
	 */
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	/**
	 * Возвращает {@link String строка} со списком идентификаторов
	 * @return {@link String строка} со списком идентификаторов
	 */
	public String getIds() {
		return ids;
	}

	/**
	 * Устанавливает {@link String строка} со списком идентификаторов
	 * @param ids {@link String строка} со списком идентификаторов
	 */
	public void setIds(String ids) {
		this.ids = ids;
	}

	/**
	 * Возвращает {@link String тип} сравниваемых объектов
	 * @return {@link String тип} сравниваемых объектов
	 */
	public String getObjectType() {
		return objectType;
	}

	/**
	 * Устанавливает {@link String тип} сравниваемых объектов
	 * @param objectType {@link String тип} сравниваемых объектов
	 */
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getCurrent() {
		return current;
	}

	public void setCurrent(String current) {
		this.current = current;
	}

}