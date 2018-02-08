<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="UTF-8"%>

<%@taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@page import="com.vtb.util.ApplProperties"%>
<%@page import="java.util.logging.Logger"%>
<%@page import="ru.md.spo.util.Config"%>


<%@page import="java.net.URL"%><html:html>
<head>
<title>UploadFileLotus</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
<meta name="GENERATOR" content="Rational Application Developer">
<script language="JavaScript" type="text/javascript">
	<%
		Logger LOGGER = Logger.getLogger(this.getClass().getName());
		String ipDomain = "";
		String wasUrl = "";
		try {
			LOGGER.info("ipDomain: " + request.getAttribute("ipDomain").toString());
			ipDomain = request.getAttribute("ipDomain").toString();
			
			if (Config.getProperty("USE_SA").equals("true")) {
				wasUrl = Config.getProperty("SA_HOST");
			} else {
				URL constractedURL = new URL(request.getScheme(), request.getServerName(), request.getServerPort(), "");
				wasUrl = constractedURL.toString().concat("/");
			}
			
		} catch (Exception e) {
			return;
		}
	%>
	var url_was = "<%=wasUrl%><%=ApplProperties.getwebcontextFWF() %>/actionUploadFile.do";
	var lotus_url = "http://<%=ipDomain%>/FileStorage.nsf/uploadControl";
	var lotus_domain = "http://<%=ipDomain%>";
	
	document._domino_target = "_self";
	
	function _doClick(v, o, t, h) {
	  var form = document._uploadControl;
	  if (form.onsubmit) {
	     var retVal = form.onsubmit();
	     if (typeof retVal == "boolean" && retVal == false)
	       return false;
	  }
	  var target = document._domino_target;
	  if (o.href != null) {
	    if (o.target != null)
	       target = o.target;
	  } else {
	    if (t != null)
	      target = t;
	  }
	  form.target = target;
	  form.__Click.value = v;
	  if (h != null)
	    form.action += h;
	  form.action = lotus_domain + form.action;
	  //alert(form.action);
	  form.submit();
	  
	  return false;
	}
</script>
</head>
<body topmargin="0" rightmargin="10" bottommargin="0" leftmargin="10">	
</body>
<script type="text/javascript">
	try {
		var frame, docum, url_text;	
		var xmlhttp = new ActiveXObject("Msxml2.XMLHTTP");	
		xmlhttp.Open("GET", lotus_url, false);						
		xmlhttp.Send();
		document.body.insertAdjacentHTML('beforeEnd',xmlhttp.ResponseText);
		//alert(document.body.innerHTML);
		//document.getElementById('id_group').value = "1";
		//document.getElementById('id_appl').value = "100";
		//document.getElementById('filetype').value = "Устав предприятия";
		document.getElementById('url_WAS').value = url_was;
	} catch (e) {		
		alert("Error name: " + e.name + ".\n Error message: " + e.message); 
	}
</script>
</html:html>
