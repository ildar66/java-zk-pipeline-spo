package com.vtb.ejb;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.internet.InternetAddress;

import ru.masterdm.integration.ServiceFactory;
import ru.masterdm.integration.mailer.MailerService;

/**
 * Bean implementation class for Enterprise Bean: SPOMessageActionProcessorFacade
 */

public class SPOMessageActionProcessorFacadeBean implements SPOMessageActionProcessorFacade, SPOMessageActionProcessorFacadeLocal {

	private static final Logger LOGGER = Logger.getLogger(SPOMessageActionProcessorFacadeBean.class.getName());
	
    static final long serialVersionUID = 3206093459760846163L;	

    /**
	 * {@inheritDoc}
	 */
	public void send(String senderMail, String senderName, String recipients, String subject, String body) {
		try {
			LOGGER.warning("Mail sender called. Parameters:\r\n" + "sender='" + senderMail + "',\r\n" + "recipients='" + recipients + "',\r\n"
                    + "subject='" + subject + "',\r\n" + "body='" + body + "'");
					
			LOGGER.warning("============   SPOMessageActionProcessorFacadeBean Deprecated!   ============");
			/*if(recipients==null)
				return;
			InternetAddress from = new InternetAddress(senderMail);
			from.setPersonal(senderName,"utf-8");

			for(String to : recipients.split(";"))
				ServiceFactory.getService(MailerService.class).send("[СПО] ", subject, body, from, new InternetAddress(to));*/
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
}
