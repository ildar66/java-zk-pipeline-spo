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
<%@page import="test_mapperweb.Process"%>
<%@page import="test_mapperweb.RoleStage"%>
<html:html>
<head>
<title>TreeView</title>
<meta http-equiv="Content-Type"
	content="text/html; charset=utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
<meta name="GENERATOR" content="Rational Application Developer">
</head>
<body>
<%
		String id = "";
		ArrayList listProc = new ArrayList();
		try {

			id = request.getParameter("id");

			InitialContext ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup("jdbc/LOANS");
			Connection conn = ds.getConnection();
			Statement stmn = null;
			ResultSet rs = null;
			try {
				stmn = conn.createStatement();
				rs = stmn.executeQuery("select ID_TYPE_PROCESS as id, DESCRIPTION_PROCESS as name from type_process t");
				while (rs.next()) {
					Process node = new Process();
					node.setId(rs.getInt("id"));
					node.setName(rs.getString("name"));
					listProc.add(node);
				}
			} finally {
				if (rs != null)
					rs.close();
		
				if (stmn != null)
					stmn.close();
			}

		} catch (Exception e) {

		}
%>
<table border="1">
	<tbody>
		<%
			for (int i = 0; i < listProc.size(); i++) {
			Process proc = (Process) listProc.get(i);
		%>

		<tr>
			<td><%=proc.getId()%></td>
			<td><%=proc.getName()%></td>
		</tr>
		<%
		}
		%>
	</tbody>
</table>


<%
		String query = "select r.id_role as id_role, r.name_role as name_role, st.id_stage as id_stage, st.description_stage as description_stage"
		+ " from stages st, stages_in_role sr, roles r "
		+ " where st.id_stage=sr.id_stage and r.id_role = sr.id_role and st.id_type_process="
		+ id;

		ArrayList list = new ArrayList();

		Statement stmn = null;
		ResultSet rs = null;
		try {
			InitialContext ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup("jdbc/LOANS");
			Connection conn = ds.getConnection();
			stmn = conn.createStatement();
			rs = stmn.executeQuery(query);
			while (rs.next()) {
				RoleStage node = new RoleStage();
				node.setId_stage(rs.getInt("id_stage"));
				node.setId_role(rs.getInt("id_role"));
				node.setRole(rs.getString("name_role"));
				node.setStage(rs.getString("description_stage"));
				list.add(node);
			}

		} catch (Exception e) {
			System.out.println("ERROR: " + e.getMessage());
			e.printStackTrace();
			out.println("<BR/>ERROR: " + e.getMessage() + "<BR/>");
		} finally {
			if (rs != null)
				rs.close();
			if (stmn != null)
				stmn.close();

		}
%>

<table border="1">
	<thead>
		<th>Роль</th>
		<th>Операция</th>
	</thead>
	<tbody>
		<%
			int old = 0;
			for (int i = 0; i < list.size(); i++) {
				RoleStage r = (RoleStage)list.get(i);
				String role = "";
				if (r.getId_role() != old) {
					old = r.getId_role();
					role = r.getRole();
				}
		%>			
			<tr><td><%=role%></td><td><%=r.getStage()%></td><td>
		<%		
			}
		%>
	</tbody>
</table>



</body>
</html:html>
