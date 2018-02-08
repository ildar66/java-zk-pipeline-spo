/**
 * 
 */
package ru.md.jsp.tag;

import javax.servlet.jsp.tagext.TagSupport;

/**
 * @author Sergey
 *
 */
public class PUPTag extends TagSupport {

	private static final long serialVersionUID = 1L;
	//атрибуты тега 
	private String name = "";
	private int index = 0;
	private String style = "";
	private String dojoType = "";
	private boolean readonly = false;
	private String title = "";
	private String id = "";
	private String styleClass = "";	 //css class
	
	//события тега
	private String onChange = "";
	private String onFocus = "";
	private String onBlur = "";
	private String onClick = ""; 
	
	/**
	 * Получение значения атрибута <code>dojoType</code>
	 * @return значениe атрибута <code>dojoType</code>
	 */
	public String getDojoType() {
		return dojoType;
	}
	
	/**
	 * Установка значения атрибута <code>dojoType</code>
	 */
	public void setDojoType(String dojoType) {
		this.dojoType = dojoType;
	}
	
	/**
	 * Получение значения атрибута <code>index</code>
	 * @return значениe атрибута <code>index</code>
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * Установка значения атрибута <code>index</code>
	 */
	public void setIndex(int index) {
		this.index = index;
	}
	
	/**
	 * Получение значения атрибута <code>name</code>
	 * @return значениe атрибута <code>name</code>
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Установка значения атрибута <code>name</code>
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Получение значения атрибута <code>readonly</code>
	 * @return значениe атрибута <code>readonly</code>
	 */
	public boolean isReadonly() {
		return readonly;
	}
	
	/**
	 * Установка значения атрибута <code>readonly</code>
	 */
	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}
	
	/**
	 * Получение значения атрибута <code>style</code>
	 * @return значениe атрибута <code>style</code>
	 */
	public String getStyle() {
		return style;
	}
	
	/**
	 * Установка значения атрибута <code>style</code>
	 */
	public void setStyle(String style) {
		this.style = style;
	}
	
	/**
	 * Получение значения атрибута <code>onBlur</code>
	 * @return значениe атрибута <code>onBlur</code>
	 */
	public String getOnBlur() {
		return onBlur;
	}
	
	/**
	 * Установка значения атрибута <code>onBlur</code>
	 */
	public void setOnBlur(String onBlur) {
		this.onBlur = onBlur;
	}
	
	/**
	 * Получение значения атрибута <code>onChange</code>
	 * @return значениe атрибута <code>onChange</code>
	 */
	public String getOnChange() {
		return onChange;
	}
	
	/**
	 * Установка значения атрибута <code>onChange</code>
	 */
	public void setOnChange(String onChange) {
		this.onChange = onChange;
	}

	/**
	 * Получение значения атрибута <code>onClick</code>
	 * @return значениe атрибута <code>onClick</code>
	 */
	public String getOnClick() {
		return onClick;
	}
	
	/**
	 * Установка значения атрибута <code>onClick</code>
	 */
	public void setOnClick(String onClick) {
		this.onClick = onClick;
	}
	
	/**
	 * Получение значения атрибута <code>onFocus</code>
	 * @return значениe атрибута <code>onFocus</code>
	 */
	public String getOnFocus() {
		return onFocus;
	}
	
	/**
	 * Установка значения атрибута <code>onFocus</code>
	 */
	public void setOnFocus(String onFocus) {
		this.onFocus = onFocus;
	}

	/**
	 * Получение значения атрибута <code>title</code>
	 * @return значениe атрибута <code>title</code>
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Установка значения атрибута <code>title</code>
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * Получение значения атрибута <code>id</code>
	 * @return значениe атрибута <code>id</code>
	 */
	public String getId() {
		return id;
	}

	/**
	 * Установка значения атрибута <code>id</code>
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Получение значения атрибута <code>styleClass</code>
	 * @return значениe атрибута <code>styleClass</code>
	 */
	public String getStyleClass() {
		return styleClass;
	}
	
	/**
	 * Установка значения атрибута <code>styleClass</code>
	 */
	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}
}
