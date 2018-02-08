package ru.masterdm.spo;

import javax.xml.ws.WebFault;
import java.io.Serializable;

/**
 * Ошибка для веб-сервисов
 *
 * @author svaliev@masterdm.ru
 */
@WebFault(name = "SpoException", targetNamespace = "http://ws.spo.integration.masterdm.ru")
public class SpoWsException extends Exception {
	/**
	 * Информация об ошибке
	 *
	 * @author svaliev@masterdm.ru
	 */
	public static class FaultInfo implements Serializable {
		
		/**
		 * @serial
		 */
		private static final long serialVersionUID = 1L;
		private String message;
		
		/**
		 * Возвращает {@link String информация}
		 *
		 * @return {@link String информация}
		 */
		public String getMessage() {
			return message;
		}
		
		/**
		 * Устанавливает {@link String информация}
		 *
		 * @param message {@link String информация}
		 */
		public void setMessage(String message) {
			this.message = message;
		}
	}
	
	/**
	 * @serial
	 */
	private static final long serialVersionUID = 1L;
	private FaultInfo faultInfo;

	/**
	 * Конструктор
	 *
	 * @param message {@link String сообщение}
	 * @param faultInfo {@link Exception ошибка}
	 */
	public SpoWsException(String message, FaultInfo faultInfo) {
		super(message);
		this.faultInfo = faultInfo;
	}
	/**
	 * Конструктор
	 *
	 * @param message {@link String сообщение}
	 * @param cause {@link Throwable причина}
	 */
	public SpoWsException(String message, Throwable cause) {
		super(message, cause);
		this.faultInfo = new FaultInfo();
		faultInfo.setMessage(message);
	}

	/**
	 * Конструктор
	 *
	 * @param message {@link String сообщение}
	 * @param faultInfo {@link Exception ошибка}
	 * @param cause {@link Throwable причина}
	 */
	public SpoWsException(String message, FaultInfo faultInfo, Throwable cause) {
		super(message, cause);
		this.faultInfo = faultInfo;
	}

	/**
	 * Возвращает информацию об {@link Exception ошибке}
	 *
	 * @return {@link Exception ошибка}
	 */
	public FaultInfo getFaultInfo() {
		return faultInfo;
	}
}