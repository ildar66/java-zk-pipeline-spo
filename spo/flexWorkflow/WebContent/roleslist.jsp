<%@ page contentType="text/html; charset=utf-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@page import="java.util.List" %>
<%@page import="java.util.ArrayList" %>
<%@page import="java.util.Collections" %>
<%@page import="ru.md.pup.dbobjects.UserJPA" %>
<%@page import="ru.md.spo.ejb.PupFacadeLocal" %>
<%@page import="org.uit.director.db.dbobjects.WorkflowTypeProcess" %>
<%@page import="org.uit.director.contexts.WPC"%>
<%@page import="ru.masterdm.compendium.model.CompendiumActionProcessor"%>
<html:html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
</head>
<body>
<div id="roleslist"><div style="overflow:auto; height:600px">
<%PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
CompendiumActionProcessor compenduim = (CompendiumActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("Compendium");
String login = request.getParameter("login");
UserJPA user = null;
if(request.getParameter("id")!=null){
	user = pupFacadeLocal.getUser(Long.valueOf(request.getParameter("id")));
} else { 
	user = pupFacadeLocal.getUserByLogin(login);
}
 %>
                        <h3><%=user.getFullName() %></h3>
                        <p>Подразделение: <%=compenduim.findDepartmentFullPath(user.getDepartment().getIdDepartment().intValue(), true) %></p>
                        <table>
                        <tr><td valign="top" style="height: px; scrolling:auto">Роли</td><td>
                        <%
                        for (WorkflowTypeProcess wtp : WPC.getInstance().getTypeProcessesList().getTypesProcessesSorted()){
                            out.println("<a id=\"processtype"+
                            wtp.getIdTypeProcess()+"\">"+wtp.getNameTypeProcess()+"</a><ol>");
                            boolean emptyRoles=true;
                            List<String> roleNames = new ArrayList<String>();
                            for (ru.md.pup.dbobjects.RoleJPA role : user.getRoles()) {
                                if(role.getProcess().getIdTypeProcess().equals(wtp.getId())){
                                    roleNames.add(role.getNameRole());
                                    emptyRoles=false;
                                }
                            }
                            Collections.sort(roleNames);
                            for (String role : roleNames) out.print("<li>" + role +"</li>");
                            if(emptyRoles)out.print("    нет ролей для данного процесса");
                            out.println("</ol>");
                        }
                        %>
                        </td></tr>
                        </table>
                    </div>
</div>
</body>
</html:html>