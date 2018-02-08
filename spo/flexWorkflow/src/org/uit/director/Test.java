package org.uit.director;


/**
 * Created by IntelliJ IDEA.
 * User: pd190390
 * Date: 20.09.2005
 * Time: 9:20:08
 * To change this template use File | Settings | File Templates.
 */
public class Test {

    public static void main(String[] args) {

        try {

        	String yyy = new String();
        	
        	testProc(yyy);
        	
        	/*InputStream stream = new FileInputStream("d://attributesDefinition.xml");
        	XmlDocument attributesDef = XmlDocument.createXmlDocument(stream, false);
        	
			List<Object[]> vars = BusinessProcessDecider.getParamForLoadVarNodes(attributesDef);
			
			int y = 0;*/
			
			
//            Process p = Runtime.getRuntime().exec("net send 513-1 test message");
//            Mailer mailer = new Mailer();
//            mailer.setServer("10.19.1.12");
//            mailer.setFrom("rust@019.pfr.ru");
//            mailer.addTo("rust@019.pfr.ru");
//            mailer.addTo("power@019.pfr.ru");
//            mailer.setSubject("");
//            mailer.setBody("Сообщения");
//            mailer.doSend();
//            mailer.send("10.19.1.12","rust@019.pfr.ru, power@019.pfr.ru", "power@019.pfr.ru","Test", "this is test message");
            int iii = 0;


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

	private static void testProc(String yyy) {
		yyy = "sdfsdf";
		// TODO Auto-generated method stub
		
	}


}
