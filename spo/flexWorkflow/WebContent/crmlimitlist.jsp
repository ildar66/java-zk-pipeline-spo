<%@ page contentType="text/html; charset=utf-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page import="org.uit.director.contexts.WorkflowSessionContext"%>
<%@page import="org.uit.director.action.AbstractAction"%>
<%@page import="com.vtb.util.Formatter"%>
<%@page import="ru.md.helper.TaskHelper"%>
<%@page import="ru.md.pup.dbobjects.UserJPA"%>
<%@page import="ru.md.spo.ejb.CrmFacadeLocal" %>
<%@page import="ru.md.crm.dbobjects.LimitQueueTO"%>
<%@page import="com.vtb.domain.SPOAcceptType"%>
<%@page import="com.vtb.util.ApplProperties"%>
<%@page import="org.uit.director.contexts.WPC"%>
<%@page import="ru.masterdm.compendium.model.CompendiumActionProcessor"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
	<title>Список заявок</title>
	<link rel="stylesheet" href="style/style.css" />
	<script language="javascript" src="resources/cal2.js"></script>
    <script language="javascript" src="resources/cal_conf2.js"></script>
</head>
<body class="soria">
<jsp:include page="header_and_menu.jsp" />
<%
String typeParam = request.getParameter("type");
SPOAcceptType type = SPOAcceptType.NOTACCEPT;
if (typeParam!=null && typeParam.equals("1")) type = SPOAcceptType.ACCEPT;
if (typeParam!=null && typeParam.equals("2")) type = SPOAcceptType.ERROR;
CrmFacadeLocal flexWorkflowFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(CrmFacadeLocal.class);
String sendLeftDate=request.getParameter("sendLeftDate")==null?Formatter.format(new java.util.Date()):request.getParameter("sendLeftDate");
String sendRightDate=request.getParameter("sendRightDate")==null?Formatter.format(new java.util.Date()):request.getParameter("sendRightDate");
	LimitQueueTO[] list = flexWorkflowFacadeLocal.getLimitQueue(type,sendLeftDate,sendRightDate);
	%>
	<form method="post" action="crmlimitlist.jsp">
	Статус лимита в очереди: <select name="type" onchange="submit()">
       <option value="0" <%if(type==SPOAcceptType.NOTACCEPT){ %>selected="selected"<%} %>>В очереди загрузки</option>
       <option value="1" <%if(type==SPOAcceptType.ACCEPT){ %>selected="selected"<%} %>>Загружено</option>
       <option value="2" <%if(type==SPOAcceptType.ERROR){ %>selected="selected"<%} %>>Возникли ошибки во время загрузки</option>
	</select>
	<br />Дата выгрузки из CRM
    c <input type="text" class="text date" id="sendLeftDate" name="sendLeftDate" 
    value="<%=sendLeftDate %>" 
    onFocus="displayCalendarWrapper('sendLeftDate', '', false); return false;" />
    по <input type="text" class="text date" id="sendRightDate" name="sendRightDate" 
    value="<%=sendRightDate %>" 
    onFocus="displayCalendarWrapper('sendRightDate', '', false); return false;" />
    <br /><input type="submit" value="Обновить">
	<h1>Лимиты из CRM</h1>
	<table class="regular">
	<thead>
			<tr>
                <th>дата выгрузки из CRM</th>
                <%if(type!=SPOAcceptType.NOTACCEPT){ %><th>дата загрузки в СПО</th><%} %>
                <%if(type==SPOAcceptType.ERROR){ %><th>ошибка</th><%} %>
                <th>номер</th>
				<th>название</th>
				<th>сумма</th>
				<th>контрагенты</th>
				<th>менеджер</th>
				<th>вид лимита</th>
				<%if(type==SPOAcceptType.ERROR){ %><th></th><%} %>
			</tr>
	</thead>
	<tbody>
	<%
	String org="";
	for (LimitQueueTO queueTO : list){
	    %>
	    <tr>
	    <td><%=Formatter.formatDateTime(queueTO.queue.getSendDate()) %></td>
	    <%if(type!=SPOAcceptType.NOTACCEPT){ %>
	    <td><%=Formatter.formatDateTime(queueTO.queue.getAcceptDate()) %></td><%} %>
	    <%if(type==SPOAcceptType.ERROR){ %><td><%=queueTO.queue.getResult() %></td><%} %>
	    <td class="limit_number"><%=queueTO.limit.getCode() %></td>
		<td><%=queueTO.limit.getLimit_name() %></td>
		<td class="number"><%=Formatter.format(queueTO.limit.getSum())%> <%=queueTO.limit.getCurr() %></td>
		<td><%=queueTO.limit.getAccountsName() %></td>
		<td><%=queueTO.limit.getManager().getLastname()+" "+queueTO.limit.getManager().getFirstname()
		+" (<a class=\"login\"  href=\"roleslist.jsp?login="+queueTO.limit.getManager().getLogin()
		+"\">"+queueTO.limit.getManager().getLogin()+"</a>)" %></td>
		<td><%=queueTO.limit.getLimit_vid() %></td>
		<%if(type==SPOAcceptType.ERROR){ %><td><a href="loadLimit.do?id=<%=queueTO.queue.getId() %>">повторить загрузку</a></td><%} %>
	    </tr>
	    <%
	}
	 %>
	 </tbody>
	 </table>
	 </form>
<script language="JavaScript" src="/compendium/calendar/dhtmlgoodies_calendar.js"></script>
<script type="text/javascript" src="scripts/form.js"></script>

                            </td>
                            <td class="r"></td>
                        </tr>
                        <tr>
                            <td class="lb"></td>
                            <td class="b"></td>
                            <td class="rb"></td>
                        </tr>
                    </table>
                </td>
            </tr>
        </table>
        <table id="FooterBand">
            <tr>
                <td>ВТБ</td>
                <td class="ReverseGradient"><img src="theme/img/gradient2.jpg"></td>
            </tr>
        </table>
        </td>
        </tr>
    </table>
    <div id="Copyright">
        Разработка ООО «Мастер Домино». 2009–2010 г., Версия <%=ApplProperties.getVersion() %><br />
        <%WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
        CompendiumActionProcessor compenduim = (CompendiumActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("Compendium");

        UserJPA user = TaskHelper.getCurrentUser(request);
        Long id = wsc.getIdUser();
        if (id != null) {
            String login = WPC.getInstance().getUsersMgr().getLoginByIdUser(id);
            if (login != null){
                out.println(compenduim.findDepartmentFullPath(user.getDepartment().getIdDepartment().intValue(), true));
                out.print("// ");
                out.println("<a id=\"bottomUserName\" href=\"roleslist.jsp?login="+login+"\">"+
                    user.getSurname() + " "  + user.getName()+"</a>");
                if(wsc.isAdmin())out.println(" <em>(Администратор системы)</em>");
                %>
                <script type="text/javascript">
                    $(document).ready(function(){
                        $("#upperUserName, #bottomUserName, a.login").fancybox({
                            zoomSpeedIn: 0,
                            zoomSpeedOut:0,
                            frameWidth: 600,
                            frameHeight: 600,
                            'hideOnContentClick': false
                        });
                        $("a.supply").fancybox({
                            'zoomOpacity'           : true,
                            'zoomSpeedIn'           : 500,
                            'zoomSpeedOut'          : 500,
                            'hideOnContentClick': false,
                            'frameWidth': 800, 
                            'frameHeight': 600,
                            'showCloseButton': true
                        });
                        fancyClassSubscribe();
                    });
                </script>
                
                <%
            }
        }
        %>
    </div>
<script type="text/javascript">
    function confirm_window_close() {
        if (confirm("Уверены, что хотите выйти?") ) { self.close() }
    }
</script>
<link type="text/css" rel="stylesheet" href="/compendium/calendar/dhtmlgoodies_calendar.css">
<script language="JavaScript" src="/compendium/calendar/dhtmlgoodies_calendar.js"></script>
</body>
<iframe width=174 height=189 name="gToday:normal:agenda.js" id="gToday:normal:agenda.js" src="<%=request.getContextPath()%>/calendar/ipopeng.jsp" scrolling="no" frameborder="0" style="visibility:visible; z-index:999; position:absolute; left:-500px; top:0px;">
</iframe>
<script>
    //Это для календарика
    function popCalInFrame(dateCtrl) {
        var w = gfPop;
        w.fPopCalendar(dateCtrl);   // pop calendar
    }
    
    function showhide ()
    {
        var style = document.getElementById("sendDate").style
        if (style.display == "none")
            style.display = "";
        else {
            style.display = "none";
            document.getElementById("sendLeftDate").value = "";
        }
    }
 
</script>
</html>