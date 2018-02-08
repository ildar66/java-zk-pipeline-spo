<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="java.sql.Connection"%>
<%@page import="javax.naming.InitialContext"%>
<%@page import="javax.sql.DataSource"%>
<%@page import="java.sql.Statement"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.util.ArrayList"%>
<%@page import="test_mapperweb.Node"%>
<%@page import="test_mapperweb.Process"%>
<html:html>
<head>
<title>RolesView</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
</head>
<body>
<%
String id = "";
ArrayList listProc = new ArrayList();
ResultSet rs = null;
Connection conn = null;
try {
	
	id = request.getParameter("id");
	
	
	InitialContext ctx = new InitialContext();
	DataSource ds = (DataSource) ctx.lookup("jdbc/LOANS");
	conn = ds.getConnection();
	Statement stmn = conn.createStatement();
	rs = stmn.executeQuery("select ID_TYPE_PROCESS as id, DESCRIPTION_PROCESS as name from type_process t");
	while (rs.next()) {
		Process node = new Process();
		node.setId(rs.getInt("id"));
		node.setName(rs.getString("name"));
		listProc.add(node);
	}
	

} catch (Exception e) {
	
} finally {
	if (rs != null)
		rs.close();
	if (conn != null)
		conn.close();
}

%>
<table border="1">
	<tbody>
	<%
	for (int i = 0; i<listProc.size(); i++) {
		Process proc = (Process)listProc.get(i);
	%>
	
		<tr>
			<td><a href="RolesView.jsp?id=<%=proc.getId()%>"><%=proc.getId()%></a></td>
			<td><%=proc.getName()%></td>
		</tr>
	<%
	}
	%>
	</tbody>
</table>


<%

StringBuffer query = new StringBuffer();
query.append("select distinct s.description_stage stage, r.name_role role, v.name_var var ");
query.append("from  ");
query.append("       stages s left join stages_in_role sr on sr.id_stage = s.id_stage ");
query.append("       left join roles r on r.id_role = sr.id_role ");
query.append("       left join stages_permissions sp on (s.id_stage = sp.id_stage and sp.id_permission = 3) ");
query.append("       left join variables v on sp.id_var = v.id_var ");
query.append("where s.id_type_process = " + id + " and s.active = 1");
query.append("group by s.description_stage, r.name_role, v.name_var ");
query.append("order by s.description_stage, r.name_role, v.name_var ");

%>
<table border="1">
	<thead>
		<tr>
			<th>#</th>
			<th>Операция</th>
			<th>Роль</th>
			<th>Переменные для редактирования</th>
		</tr>
	</thead>
	<tbody>
		<%
		try {
			int index = 1;
			String stage = null, role = null, var = null;
			boolean nextStage = true; //, nextRole = true, nextVar = true;
			ArrayList listRoles = new ArrayList();
			ArrayList listVars = new ArrayList();
			
			InitialContext ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup("jdbc/LOANS");
			conn = ds.getConnection();
			Statement stmn = conn.createStatement();
			rs = stmn.executeQuery(query.toString());
			
			rs.next();
			stage = rs.getString("stage");
			//role = rs.getString("role");
			//var = rs.getString("var");
			
			do {
				nextStage = !rs.getString("stage").equalsIgnoreCase(stage);
				//nextRole = !rs.getString("role").equalsIgnoreCase(role);
				//nextVar = !rs.getString("var").equalsIgnoreCase(var);
				
				//if (nextRole || nextStage)
				//	listRoles.add(role);
					
				//if (nextVar || nextStage)
				//	listVars.add(var);
					
				if (nextStage) {
		%>
					<tr valign="top">
						<td><%=index++%></td>
						<td><%=stage%></td>
						<td>
							<%
								for (int i = 0; i < listRoles.size(); i++) {
									out.println((String)listRoles.get(i));
									out.println("<br/>");
								}
							%>
						</td>
						<td>
							<%
								for (int i = 0; i < listVars.size(); i++) {
									out.println((String)listVars.get(i));
									out.println("<br/>");
								}
							%>
						</td>
					</tr>
		<%
					listRoles.clear();
					listVars.clear();
				}
				
				stage = rs.getString("stage");
				role = rs.getString("role");
				var = rs.getString("var");
				
				if (!listRoles.contains(role))
					listRoles.add(role);
					
				if (!listVars.contains(var))
					listVars.add(var);
					
			} while (rs.next());
		%>
			<tr valign="top">
				<td><%=index++%></td>
				<td><%=stage%></td>
				<td>
					<%
						for (int i = 0; i < listRoles.size(); i++) {
							out.println((String)listRoles.get(i));
							out.println("<br/>");
						}
					%>
				</td>
				<td>
					<%
						for (int i = 0; i < listVars.size(); i++) {
							out.println((String)listVars.get(i));
							out.println("<br/>");
						}
					%>
				</td>
			</tr>
		<%
		} catch(Exception e) {
			System.out.println("ERROR: "+e.getMessage());
			e.printStackTrace();
			out.println("<BR/>ERROR: "+e.getMessage()+"<BR/>");
		} finally {
			if (rs != null)
				rs.close();
			if (conn != null)
				conn.close();
		}
		%>
	</tbody>
</table>
</body>
</html:html>