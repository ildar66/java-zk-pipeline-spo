<%@page import="ru.md.helper.TaskHelper"%>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="com.vtb.util.ApplProperties"%>
<%@page import="org.uit.director.action.AbstractAction"%>
<%@ page import="ru.md.persistence.UserMapper" %>
<%@ page import="ru.masterdm.spo.utils.SBeanLocator" %>
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
				<td class="ReverseGradient"><img src="theme/img/gradient2.jpg"></td>
			</tr>
		</table>
		</td>
		</tr>
	</table>
	
<a href="javascript:;" onclick="show_hide_task_list_onclick()"
	style="position: relative; left:650px;top:-25px;z-index: 1;float:left;"><img src="style/images/show_tasklist.png" style="display: none" id="show_task_list_btn"></a>

	<div id="Copyright">
		Разработка ООО «Мастер Домино». 2008–2016 г., Версия <%=ApplProperties.getVersion() %>
		<%
			if(request.getAttribute("startTime")!=null) {
				Long loadTime = System.currentTimeMillis()-(Long)request.getAttribute("startTime");
				out.println("     <em>// Время формирования страницы (секунд): "+com.vtb.util.Formatter.format(Double.valueOf(loadTime)/1000)+"</em>");
			}
		%>
		<%
		ru.md.domain.User user = ((UserMapper) SBeanLocator.singleton().getBean("userMapper")).getUserByLogin(AbstractAction.getUserLogin(request));
			if (user != null) {
                out.print("  // ");
                out.println(user.getDepname());
                out.print("// ");
                out.println("<a href=\"javascript:;\" onclick=\"$('#current_role_dialog').dialog({width:800,draggable: false})\">" +
                        user.getLastName() + " " + user.getFirstName() + "</a>");
                if (TaskHelper.pup().isAdmin(user.getId())) out.println(" <em>(Администратор системы)</em>");
            }
		%>
	<%=TaskHelper.versionCheck() %>
	</div>
<link type="text/css" rel="stylesheet" href="resources/dhtmlgoodies_calendar.css">
<script language="JavaScript" src="scripts/dhtmlgoodies_calendar.js"></script>
<input type="hidden" id="login" value="<%=user==null?"":user.getLogin()%>">
<input type="hidden" id="userid" value="<%=user==null?"":user.getId()%>">
<div id="current_role_dialog" style="display: none" title="Роли пользователя">Ошибка загрузки ролей пользователя</div>
<div id="refuse_dialog" style="display: none" title="Отказ клиента от заявки">загрузка...</div>
<div class="jqmWindow" id="roleslist" style="height:600px;overflow:auto">
    
</div>
<div class="jqmWindow" id="statistic" style="height:200px;overflow:auto">
    
</div>