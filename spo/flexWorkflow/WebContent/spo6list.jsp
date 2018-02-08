<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<%@ page contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@page import="org.uit.director.contexts.WorkflowSessionContext"%>
<%@page import="org.uit.director.action.AbstractAction"%>
<%@page import="com.vtb.value.BeanKeys"%>
<html:html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
		<title>Список заявок боевой СПО 6</title>
		<link rel="stylesheet" href="style/style.css" />
		<%WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);%>
	</head>
	<body class="soria">
		<jsp:include page="header_and_menu.jsp" />
		<h1>Cделки из СПОпром</h1>
		<table class="regular">
			<thead>
					<tr>
						<th></th>
						<th>Заявка №</th>
						<th>сумма</th>
						<th>контрагенты</th>
					</tr>
			</thead>
			<tbody>
				<logic:iterate id="item" name="<%=BeanKeys.PROCESS6_LIST %>" type="com.vtb.domain.Process6">
					<logic:present name="item">
			    		<tr class="a">
							<td><a href="popup_processtype.jsp?spo6pupid=<bean:write name="item" property="idprocess" filter="true"/>">загрузить</a></td>
							<td><bean:write name="item" property="number" filter="true"/></td>
							<td><bean:write name="item" property="sum" filter="true"/></td>
							<td><bean:write name="item" property="org" filter="true"/></td>
			    		</tr>
			    	</logic:present>
			    </logic:iterate>
			 </tbody>
		 </table>
		 
		<jsp:include flush="true" page="footer.jsp" />
	</body>
</html:html>