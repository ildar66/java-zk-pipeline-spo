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
<title>TreeView</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
<meta name="GENERATOR" content="Rational Application Developer">
<link rel="stylesheet" href="style/style.css" />
</head>
<body>
<table class="error">
<%
String id = "";
ArrayList listProc = new ArrayList();
try {
	
	id = request.getParameter("id");
	
	
	InitialContext ctx = new InitialContext();
	DataSource ds = (DataSource) ctx.lookup("jdbc/LOANS");
	Connection conn = ds.getConnection();
	Statement stmn = conn.createStatement();
	ResultSet rs = stmn.executeQuery("select ID_TYPE_PROCESS as id, DESCRIPTION_PROCESS as name from type_process t");
	while (rs.next()) {
		Process node = new Process();
		node.setId(rs.getInt("id"));
		node.setName(rs.getString("name"));
		listProc.add(node);
	}
    //проверка дублирования переменных	
    rs.close();
    String var_test="select * from (select 'Дублируется атрибут' as err,name_var as n,id_type_process as tp from (select count(id_var) as cnt,name_var,id_type_process from variables group by id_type_process,name_var) where cnt>1";
    //дублирования родителей переменных	
    var_test+=" union all select 'Слишком много предков у атрибута' as err, name_var as n,id_type_process as tp from (select count(id_connection) as cnt, id_var_child from var_connections group by id_var_child) inner join variables on variables.id_var=id_var_child where cnt>1";
    //проверка на случайные пробелы
    var_test+=" union all select 'заканчивается на пробел' as err, name_var as n,id_type_process as tp from variables where name_var like '% ') order by tp";
    
    rs = stmn.executeQuery(var_test);
    while (rs.next()) {
	   	%><tr><td><%=rs.getString("tp") %></td><td><%=rs.getString("n") %></td><td><%=rs.getString("err") %></td></tr><%
	}
} catch (Exception e) {
	
}

%></table>

<table border="1">
	<tbody>
	<%
	for (int i = 0; i<listProc.size(); i++) {
		Process proc = (Process)listProc.get(i);
	%>
	
		<tr>
			<td><a href="TreeView.jsp?id=<%=proc.getId()%>"><%=proc.getId()%></a></td>
			<td><%=proc.getName()%></td>
		</tr>
	<%
	}
	%>
	</tbody>
</table>
<h1>Процесс номер <%=id %></h1>

<%

String query = "select (SELECT MAX(LEVEL) FROM var_nodes n START WITH n.id_var_node = t.id_var CONNECT BY PRIOR n.id_var_root = n.id_var_node) as level1, " +
"(SELECT MAX(LEVEL)||0 FROM var_nodes n START WITH n.id_var_node = t.id_var CONNECT BY PRIOR n.id_var_root = n.id_var_node) as level2, "+
"(select max(conn.id_var_parent) from var_connections conn, variables var where conn.id_var_child(+) = var.id_var and conn.id_var_child=t.id_var ) as id_parent "+
", t.id_var as id_value, t.name_var as value1, t.addition_var as addition, t.id_ds as ds, t.active as active  from variables t where id_TYPE_PROCESS = "+id+" order by level2 ASC";

ArrayList list = new ArrayList();

try {
	InitialContext ctx = new InitialContext();
	DataSource ds = (DataSource) ctx.lookup("jdbc/LOANS");
	Connection conn = ds.getConnection();
	Statement stmn = conn.createStatement();
	ResultSet rs = stmn.executeQuery(query);
	while (rs.next()) {
		Node node = new Node();
		node.setLevel(rs.getInt("level1"));
		node.setId_parent(rs.getInt("id_parent"));
		node.setId_value(rs.getInt("id_value"));
		node.setName(rs.getString("value1"));
		node.setAddition(rs.getString("addition"));
		node.setDs(rs.getInt("ds"));
		node.setActive(rs.getInt("active"));
		
		if (list.size() == 0) {
			list.add(node);
		} else {
			boolean isIns = false;
			for (int i=0; i<list.size(); i++) {
				if (((Node)list.get(i)).getId_value() == node.getId_parent()) {
					list.add(i+1, node);
					isIns = true;
				}				
			}
			if (isIns == false)
				list.add(node);
		}				
	}
	
	
	
}catch(Exception e) {
	System.out.println("ERROR: "+e.getMessage());
	e.printStackTrace();
	out.println("<BR/>ERROR: "+e.getMessage()+"<BR/>");
}

%>


<table border="1">
	<thead>
		<tr>
			<th>#</th>
			<th width="40%">Наименование</th>
			<th>Уровень</th>
			<th>Активный</th>
			<th>№ источника</th>
			<th>Addition</th>
		</tr>
	</thead>
	<tbody>
		<%
for (int i=0; i<list.size(); i++) {
		Node node = (Node)list.get(i);
		String offset = "";
		for (int j=0; j<node.getLevel(); j++) {
			offset = offset + "------";
		}
		//out.println("<BR/>"+offset +"   "+node.getName());
%>
		<tr>
			<td><%=i+1%></td>
			<td><%=offset+node.getName()%></td>
			<td><%=node.getLevel()%></td>
			<td><%=node.getActive()%></td>
			<td><%=node.getDs()==0? "-":String.valueOf(node.getDs())%></td>
			<td><%=node.getAddition() == null? "-" : node.getAddition() %></td>
		</tr>
		<%
	}
%>
	</tbody>
</table>
</body>
</html:html>
