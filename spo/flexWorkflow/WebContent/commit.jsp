<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page import="ru.md.spo.dbobjects.StandardPeriodValueJPA"%>
<%@page import="ru.md.spo.dbobjects.StandardPeriodGroupJPA"%>
<%@page import="java.util.ArrayList"%>
<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@page import="ru.md.spo.ejb.StandardPeriodBeanLocal" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  <meta http-equiv="X-UA-Compatible" content="IE=Edge" />
  <title>Подтвердите действие</title>
  <style type="text/css">@import url("resources/stylesheet.css"); </style>
    <link rel="stylesheet" href="style/style.css" />
</head>

<script>
	function commitAction(flag) {
		$('#confirmButton').attr('disabled', 'disabled');
		if(flag === 'true'){
			var err = '';
			$('#warn').hide();$('#warn').text('');
			$('.tabledata > table > tbody > tr > td > select').each(function(){
				if($(this).val()==='-1'){
					err += '<br />'+
					'Заполните подразделение для передачи на операцию '
					+$(this).parent().prev().find('strong').text();
				}
			});
			if(err != ''){
				$('#warn').show();
				$('#warn').html(err);
				$('#confirmButton').removeAttr("disabled");
				return false;
			}
		}
	    document.commitForm.commit.value=flag;
	    document.commitForm.submit();
	}
</script>
<body>
	<jsp:include page="header_and_menu.jsp" />
    <%
	    String actionFrom = (String) request.getAttribute("actionFrom");
	    String message = (String) request.getAttribute("message");
	    if(message.contains("Формирование Кредитного меморандума завершено")){
	        message = message.replace("будет передано на операцию: ","");
	        message = message.replace("(мгновенная отправка)","");
	    }
	    if(message.contains("Подпроцесс завершен")){
	        message = message.replace("будет передано на операцию: ","");
	        message = message.replace("(мгновенная отправка)","");
	        message = message.replace("Подпроцесс завершен","Экспертиза подразделения по анализу рыночных рисков не требуется");
	    }
	    String idTask = (String) request.getAttribute("idTask");
	    Boolean export2cc = (Boolean) request.getAttribute("export2cc");
	    ArrayList<Long> standardPeriodToDefine = (ArrayList<Long>)request.getAttribute("standardPeriodToDefine");
	    String stopfactors = (String) request.getAttribute("stopfactors");
	    String condition_warning = (String) request.getAttribute("condition_warning");
	%>
    <center>
    <FORM name="commitForm" action="<%=actionFrom%>" method="get">
        <%if(stopfactors!=null && !stopfactors.isEmpty()){ %>
            <h2>В процессе обработки заявки были выявлены следующие стоп-факторы: <%=stopfactors %></h2> 
            Необходимо обосновать решение о продолжении работы с заявкой:<br />
            <textarea rows="5" cols="15" name="cmnt" id="cmnt" onkeyup="onCmntKeyUp()"></textarea>
            <script>
            function onCmntKeyUp(){
                if($('#cmnt').val()==''){
		            $('#confirmButton').attr('disabled', 'disabled');
		            $('#confirmButton').addClass('disabled');
                } else {
                    $('#confirmButton').removeAttr("disabled");
                    $('#confirmButton').removeClass('disabled');
                }
            }
            $(document).ready(onCmntKeyUp);
            </script>
        <%} %>
    	<%if(standardPeriodToDefine!=null && !standardPeriodToDefine.isEmpty()) { %>
    	<h2>Необходимо определить критерии дифференциации нормативных сроков для завершения операции.</h2>
	    	<table class="regular"><tbody>
	            <tr><th>Для этапа</th><th>Критерий</th></tr>
	            <%for(Long idGroup : standardPeriodToDefine){
	                StandardPeriodBeanLocal spLocal = com.vtb.util.EjbLocator.getInstance().getReference(StandardPeriodBeanLocal.class);
	                StandardPeriodGroupJPA group = spLocal.getStandardPeriodGroup(idGroup);
	                %>
	                <tr><td><%=group.getName() %></td>
	                <td align="left">
		                <%for(StandardPeriodValueJPA value : group.getValues()){ 
		                    Long valueid = spLocal.getCurrentValueId(idGroup, (Long)request.getAttribute("idPupProcess"));%>
		                    <p><input name="criterium<%=group.getId().toString()%>" value="<%=value.getId()%>" type="radio" 
		                    <%if(valueid!=null && valueid.equals(value.getId())){ %>checked<%} %>>
		                    <%=value.getName() %> (<%=value.getFormatedPeriod() %> раб. дн.)</p>
		                <%} %>
	                </td>
	                </tr>
	            <%} %>
	    	</tbody></table>
    	<%} %>
    	<h3><%=message%></h3>
        <%if (export2cc != null && export2cc) { %>
		    <h2>Заявка будет передана на кредитный комитет</h2>
		    <input type="hidden" name="export2cc" value="true">
		    <br />
    	<%} %>
    	<%if (request.getParameter("refuseMode") != null && request.getParameter("refuseMode").equalsIgnoreCase("true")) {%>
           <input type="hidden" name="refuseMode" value="true">
        <%} %>
        <input type="hidden" name="commit" value="true">
        <input type="hidden" name="id0" value="<%=idTask%>">
        <%if(condition_warning==null || condition_warning.isEmpty()){ %>
		<div style="color: red;font-size: 11pt;display: none" id="warn"></div>
        <button onclick="commitAction('true');return false" id="confirmButton" style="width:6em; height:2em;">Да</button>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <button onclick="commitAction('false');return false" style="width:6em; height:2em;">Нет</button>
        <%}else{ %>
            <div style="color: red;font-size: 11pt;"><%=condition_warning %></div>
            <button onclick="commitAction('false');return false" style="width:16em; height:2em;">Вернуться к заявке</button>
        <%} %>
    </FORM>
    </center>
	<jsp:include flush="true" page="footer.jsp" />
</body>
</html>