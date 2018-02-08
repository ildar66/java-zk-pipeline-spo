<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@page import="ru.md.spo.ejb.PupFacadeLocal"%>
<%@page import="ru.md.pup.dbobjects.StageJPA"%>
<%
PupFacadeLocal pupFacade = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
ru.md.pup.dbobjects.ProcessTypeJPA pt = pupFacade.getProcessTypeById(Long.valueOf(request.getParameter("ptid")));
java.util.List<StageJPA> list = pupFacade.getStages(pt.getIdTypeProcess());
String grid = request.getParameter("grid");
String routeid = request.getParameter("routeid");
%>
<html:html>
<head>
<title>Выбор операции для бизнес-процесса <%=pt.getDescriptionProcess() %></title>
<meta http-equiv="Content-Type"	content="text/html; charset=utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
<script type="text/javascript" src="scripts/jquery/jquery.min.js"></script>
<script type="text/javascript" src="scripts/jquery/jquery.tmpl.min.js"></script>
<link rel="stylesheet" href="style/style.css" />
<link type="text/css" rel="stylesheet" href="theme/stylesheet.css">
</head>
<body class="popup">
<h1>Выбор операции для бизнес-процесса <%=pt.getDescriptionProcess() %></h1>
<input type="hidden" id="addstage" value="<%=request.getParameter("addstage") %>">
<ul>
<%for(StageJPA s : list){ %>
<li><a href="javascript:;" onclick="go('<%=s.getIdStage()%>','<%=s.getDescription()%>')">
<%=s.getDescription() %></a></li>
<%} %>
</ul>
<script language="javascript">
function go(stage_id,name){
<%if(grid.equals("0")){%>
    window.opener.nextval = window.opener.nextval +1;
    window.opener.$("#newRouteTemplate").tmpl( {id:stage_id,name:name,nextval:window.opener.nextval}).appendTo( "#main" );
<%}else{%>
    var outform = window.opener.document.forms["form1"];
    if($('#addstage').val()=="1") {
        var id = '<%=grid%>_'+stage_id;
        var li = '<li id="'+id+'">'+name+'<input type="hidden" name="stage_'+id+'"> '+
            '<a href="javascript:;" onclick="$(\'#'+id+'\').remove();">исключить</a></li>';
        window.opener.$('#td<%=grid%> > ol').append(li);
    } else {
        var id = 'DecisionStage<%=grid%>_'+stage_id;
        var li = '<li id="'+id+'">'+name+'<input type="hidden" name="decision_id_<%=grid%>" value="'+stage_id+'">'+
            ' <a href="javascript:;" onclick="$(\'#'+id+'\').remove();">исключить</a></li>';
        window.opener.$('#DecisionStageOl<%=grid%>').append(li);
    }
<%}%>
    window.opener.focus();
    window.close();
}
</script>
</body>
</html:html>