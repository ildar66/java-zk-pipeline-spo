<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="java.util.ArrayList"%>
<%@page import="ru.md.compare.CompareSublimitsInfo"%>
<%@page import="java.util.List"%>
<%@page import="ru.md.compare.CompareTaskVersion"%>
<%@page import="ru.md.compare.CompareTaskKeys.CompareTaskBlock"%>
<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>
<%@page contentType="text/html; charset=utf-8"%>
<%@page isELIgnored="true"%>
<html>
<head>
	<base target="_self" />
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
	<link rel="stylesheet" href="style/style.css" />
	<link rel="stylesheet" href="style/jquery-ui-1.10.3.custom.min.css" />
	<link rel="stylesheet" href="style/jquery.fancybox-1.3.4.css" />
	<script type="text/javascript" src="scripts/jquery/jquery-1.10.2.min.js"></script>
	<script type="text/javascript" src="scripts/jquery/jquery-migrate-1.2.1.js"></script>
	<script type="text/javascript" src="scripts/jquery/jquery-ui-1.10.3.custom.min.js"></script>
	<script type="text/javascript" src="scripts/jquery/jquery.cookie.js"></script>
	<script type="text/javascript" src="scripts/jquery/jquery.easing.1.3.js"></script>
	<script type="text/javascript" src="scripts/jquery/jquery.fancybox-1.3.4.pack.js"></script>
	<script type="text/javascript" src="scripts/jquery/jquery.tmpl.min.js"></script>
	<script type="text/javascript" src="scripts/jquery/jquery.autosize.min.js"></script>
	<script type="text/javascript" src="scripts/jquery/jquery.textarea-expander.js"></script>
	<script type="text/javascript" src="scripts/form.js"></script>
	<script type="text/javascript" src="scripts/loading.js"></script>
	<script type="text/javascript" src="scripts/sign/MDLib2.js"></script>
	<script language="javascript" src="scripts/date.js"></script>
	<script language="javascript" src="scripts/validate.js"></script>
	<script language="javascript" src="scripts/applicationScripts.js"></script>
	<title>Сравнение объектов</title>
</head>
<body class="soria" onload="timer()" style="width: 1270px" onBeforeUnload="loading()">
	<table style="border-collapse: collapse">
		<tr>
			<td><jsp:include page="headerVTB.jsp" /></td>
		</tr>
		<tr>
			<td>
				<div id="all">
				<div id="controlPanel">
					<button id="btnReturnToDocs" onclick="history.go(-1);">Вернуться на форму заявки</button>
				</div>
				</div>
			</td>
		</tr>
<%
	String strIds = request.getParameter("ids");
	String objType = request.getParameter("objectType");
	String current = request.getParameter("current");
	CompareTaskBlock[] blocks = new CompareTaskBlock[0];
	if ("limit".equalsIgnoreCase(objType))
		blocks = CompareTaskBlock.getLimitBlocks();
	else if ("product".equalsIgnoreCase(objType))
		blocks = CompareTaskBlock.getProductBlocks();
	
	List<CompareSublimitsInfo> subArray = new ArrayList<CompareSublimitsInfo>();
	subArray.addAll(CompareTaskVersion.getCompareSublimitsInfos(strIds, objType));
	for (CompareSublimitsInfo info : subArray) {
 %>
		<tr>
			<td>
				<div id="all">
					<h4><%=info.getHeader()%></h4>
<%	
		if (blocks.length > 0) {
			for (CompareTaskBlock block : blocks) { 
				// секции временно исключены
				if (CompareTaskBlock.isExcludedBlock(block))
					continue;
	%>
						<md:compareFrame empty="false" readOnly="true" name="<%=block.name().toLowerCase() %>" 
								header="<%=block.getDescription() %>" ids="<%=info.getIds() %>" 
								objectType="<%=objType %>" current="<%=current %>" />
<%			} %>
						<script type="text/javascript">
							$(window).load(function() {
								$('#section_<%=blocks[0].name().toLowerCase() %>_<%=info.getIds().replaceAll("\\|", "_") %> > thead').click();
							});
						</script>
<%		} %>
					</div>
				</td>
			</tr>
<%	} %>

		<tr>
			<td><jsp:include flush="true" page="footer.jsp" /></td>
		</tr>
	</table>
</body>
</html>