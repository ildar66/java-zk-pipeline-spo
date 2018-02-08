<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@page import="ru.md.spo.ejb.TaskFacadeLocal"%>
<%@page import="ru.md.spo.ejb.StandardPeriodBeanLocal" %>
<%
TaskFacadeLocal taskFacade = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
StandardPeriodBeanLocal spLocal = com.vtb.util.EjbLocator.getInstance().getReference(StandardPeriodBeanLocal.class);
%>
<html:html>
<head>
<meta http-equiv="Content-Type"	content="text/html; charset=utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
<link rel="stylesheet" href="style/style.css" />
<link rel="stylesheet" href="style/jqModal.css" />
<link type="text/css" rel="stylesheet" href="theme/stylesheet.css">
<script type="text/javascript" src="scripts/jquery/jquery.min.js"></script>
</head>
<body class="popup">
<div>
<h1>Изменение нормативного срока</h1>
<%for(ru.md.spo.dbobjects.StandardPeriodValueJPA value: spLocal.getStandardPeriodGroup(Long.valueOf(request.getParameter("grid"))).getValues()){%>
    <p><input value="<%=value.getId().toString()%>" name="valueid" class="valueid" type="radio">
    <span id="valuename<%=value.getId().toString()%>"><%=value.getName()%></span> (<span id="valueid<%=value.getId().toString()%>"><%=value.getFormatedPeriod()%></span> дн.)</p>
<%}%>
<br />
Ручной ввод срока: <input id="days" value=""> рабочих дней
<br />
Комментарий: <br />
<textarea rows="5" id="cmnt" onkeyup="checkemptyStPerCmnt()"></textarea>
<br /><a href="#" onclick="StPerChOnClick()" class="jqmClose" id="chStPerLink">изменить</a>
 <a href="#" class="jqmClose">Отмена</a>
<div id="emptyStPerCmnt" class="error">Необходимо заполнить поле комментарий</div>
<input id="grid" value="<%=request.getParameter("grid") %>" type="hidden">
</div>
<script language="javascript">
function StPerChOnClick(){
    if($("#cmnt").val()==''){
        alert('Для изменения нормативного срока необходимо заполнить поле комментарий');
        return false;
    }
    $.post('ajax/changeStandardPeriod.do',
    {vid: $('.valueid:checked').val(), mdtaskid:$("#mdtaskid").val(),cmnt:$("#cmnt").val(),days:$("#days").val(),grid:$("#grid").val()},reloadstandardPeriodFrame);
}
function reloadstandardPeriodFrame(){
    if($('.valueid:checked').size()>0){
        $('#standardPeriodValue'+$("#grid").val()).html($("#valueid"+$('.valueid:checked').val()).html());
    } else {
        $('#standardPeriodValue'+$("#grid").val()).html($("#days").val());
    }
    //reloadFrame($('#md_frame_params').val(),'standardPeriod');
}
function checkemptyStPerCmnt(){
    if($("#cmnt").val()==''){
        $("#chStPerLink").hide();
        $("#emptyStPerCmnt").show();
    } else {
        $("#chStPerLink").show();
        $("#emptyStPerCmnt").hide();
    }
}
checkemptyStPerCmnt();
</script>
</body>
</html:html>