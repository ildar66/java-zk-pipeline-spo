import java.util.HashSet;

import org.nfunk.jep.JEP;





public class Test {

	public enum Perm {q,w,e};
	
    public static void main(String[] args) {      
    		
    	 
    	HashSet<Perm> hs = new HashSet<Perm>(); 
    	hs.add(Perm.q);
    	
    	System.out.println(hs.contains(Perm.q));
    	
    /*	JEP jep = new JEP();
    	jep.addStandardConstants();
    	jep.addStandardFunctions();  	
    	
    	jep.setAllowAssignment(true);
    	
    	jep.addVariable("x", 1);
    	jep.addVariable("y", 2);
    	jep.addVariable("z", 3);
    	
    	
    	jep.parseExpression("x = y+z");     	
		
    	jep.getValue(); 
    	Double val = (Double) jep.getVarValue("x");
		System.out.println(val.doubleValue());
    	
    	jep.parseExpression("x = z + 1");
    	jep.getValue(); 
    	val = (Double) jep.getVarValue("x");
		System.out.println(val.doubleValue());
    	
    	jep.parseExpression("x = x + y");
    	jep.getValue(); 
    	val = (Double) jep.getVarValue("x");
		System.out.println(val.doubleValue());
    	
    	
    	
    	
    	if (!jep.hasError()) {	
    		
    		
    		 val = (Double) jep.getVarValue("x");
    		System.out.println(val.doubleValue());
    		int yyy =  0;
    	} else {
    		System.out.println(jep.getErrorInfo());
    	}*/
    	
    	
    	
    	
    	
    	
    	
           /* Class classDrv = Class.forName("COM.ibm.db2.jdbc.app.DB2Driver");
            Object newDrv = classDrv.newInstance();
            DriverManager.registerDriver((Driver) newDrv);

             connection = DriverManager.getConnection("jdbc:db2:WF");*/
        	 
        	 


//            List res = DataBaseUtils.executeQuery(connection, "call DB2ADMIN.LOAD_ATTRIBUTES_PROCESS('имя', 'String', '', '',3)");


           /* CallableStatement cStm = connection.prepareCall("call DB2ADMIN.LOAD_ATTRIBUTES_PROCESS(?,?,?,?,?)");

            cStm.setLong(1,1094);
            cStm.setString(2, "Имя");
            cStm.registerOutParameter(3, java.sql.Types.INTEGER);


            cStm.execute();
             int res = cStm.getInt(3);*/


           


       
        

    }
}
