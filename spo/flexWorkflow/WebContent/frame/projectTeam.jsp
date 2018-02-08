<%@page import="com.vtb.util.Formatter"%>
<%@page import="java.util.ArrayList"%>
<%@page import="ru.md.pup.dbobjects.RoleJPA"%>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="ru.md.spo.dbobjects.TaskJPA" %>
<%@page import="java.util.logging.*"%>
<%@page import="org.uit.director.action.AbstractAction"%>
<%@page import="ru.md.spo.ejb.PupFacadeLocal" %>
<%@page import="ru.md.spo.ejb.TaskFacadeLocal" %>
<%@page import="ru.masterdm.compendium.model.CompendiumSpoActionProcessor"%>
<%@taglib uri="/WEB-INF/pup-tag.tld" prefix="pup"%>
<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>
<%
response.addHeader("Pragma", "no-cache");
response.addHeader("Expires", "-1");
response.addHeader("Cache-control", "no-cache");
    Logger LOGGER = Logger.getLogger("projectTeam_jsp");
    java.util.Map<String, String> sections = com.vtb.util.CollectionUtils.map("pПроектная команда" , "mМидл-офис");
    TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
    PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
    CompendiumSpoActionProcessor compenduim = (CompendiumSpoActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumSpo");
    TaskJPA task=taskFacadeLocal.getTask(Long.valueOf(request.getParameter("mdtaskid")));
    Long processType = task.getProcess().getProcessType().getIdTypeProcess();
    Long userid = AbstractAction.getWorkflowSessionContext(request).getIdUser();
    LOGGER.info("current userid=" + userid);
    ru.md.pup.dbobjects.UserJPA user = pupFacadeLocal.getUser(userid);
//отправляем запросы
    boolean showRequestForm = !request.getParameter("readOnly").equals("true");
    for (ru.md.spo.dbobjects.ProjectTeamJPA team : task.getProjectTeam()) {
        if(team.getUser().getIdUser().equals(userid)){showRequestForm=true;}
    }%>

<script>
    $(function() {
        $( "#project_team_tabs" ).tabs();
        restoreTab('project_team_tabs');
    });
    $(document).ready(function() {
        fancyClassSubscribe();
    });
</script>
<div id="project_team_tabs">
    <ul>
        <li><a href="#tabs-p" onclick="storeTab('project_team_tabs',0)">Проектная команда</a></li>
        <li><a href="#tabs-m" onclick="storeTab('project_team_tabs',1)">Мидл-офис</a></li>
        <%if(showRequestForm){%><li><a href="#tabs-3" onclick="storeTab('project_team_tabs',3)">Формирование запроса</a></li><%}%>
        <li><a href="#tabs-4" onclick="storeTab('project_team_tabs',2)">История запросов</a></li>
    </ul>
    <%for(String section : sections.keySet()){
        boolean privilegeAdd = false;
        for (RoleJPA role : user.getRoles()) {
            if(role.getProcess().getIdTypeProcess().equals(processType) &&
                    (section.equals("p") && ru.md.servlet.NewProjectTeamAction.getPrivilegeAddRoles().contains(role.getNameRole())
                            ||  section.equals("m") && role.getNameRole().equals("Руководитель мидл-офиса") )) {
                privilegeAdd = true;
            }
        }

    %>
<div id="tabs-<%=section%>">
    <h3><%=sections.get(section) %></h3>
    <table id="idTablesProjectTeam<%=section %>" class="add" style="width: 99% !important;">
        <tbody>
        <tr><th>Имя</th><th>Подразделение</th>
            <th>Роли</th>
            <%if(section.equals("p")){ %><th>Выполнение операций</th><%} %>
            <%if(privilegeAdd){ %><th></th>
            <%}%></tr>
        <%for (ru.md.spo.dbobjects.ProjectTeamJPA team : task.getProjectTeam(section)) {%>
        <%java.util.List<RoleJPA> roles=new ArrayList<RoleJPA>();
            boolean hasAssignedRole = false;
            for(RoleJPA role : team.getUser().getRoles()) {
                if (!role.getProcess().getIdTypeProcess().equals(processType))
                { continue;}
                if (section.equals("p") && !ru.md.helper.TaskHelper.dict().findProjectTeamRoles().contains(role.getNameRole())){ continue;}
                if (section.equals("m") && !ru.md.helper.TaskHelper.dict().findMiddleOfficeRoles().contains(role.getNameRole())){ continue;}
                roles.add(role);
                if (section.equals("p") && pupFacadeLocal.isAssigned(team.getUser().getIdUser(),role.getIdRole(),task.getProcess().getId())){
                    hasAssignedRole = true;
                }
            }
            if(roles.size()==0){roles.add(new RoleJPA(0L,"нет ролей проектной команды",task.getProcess().getProcessType()));}
            String removeOnClick = hasAssignedRole?
                    "alert('Нельзя исключить из проектной команды пользователя, у которого есть назначение на выполнение операций');return false;"
                    :"return delProjectTeam('"+team.getUser().getIdUser()+"','"+section+"')";
            //провертить removeOnClick есть ли роль  Продуктовый менеджер для руководителя
            // и не забыть про подразделения
            if(section.equals("p")&&!user.isStructurator(processType) &&
                    !(
                            (team.getUser().hasRole(processType, "Продуктовый менеджер")&&user.hasRole(processType, "Руководитель продуктового подразделения")
                                    ||team.getUser().hasRole(processType, "Клиентский менеджер")&&user.hasRole(processType, "Руководитель клиентского подразделения")
                                    ||team.getUser().hasRole(processType, "Клиентский менеджер поддерживающего подразделения")&&user.hasRole(processType, "Руководитель поддерживающего клиентского подразделения")
                            ) && pupFacadeLocal.getSlave(user.getIdUser(), processType).contains(team.getUser())
                    )
                    ){
                removeOnClick = "alert('Вы можете исключать из проектной команды только своего подчиненного');return false;";
            }
            for(RoleJPA role : roles) {
                String assignButton = "";
                if (pupFacadeLocal.isAssigned(team.getUser().getIdUser(),role.getIdRole(),task.getProcess().getId())){
                    assignButton = "<input type=\"checkbox\" checked disabled>";
                } else{
                    if(privilegeAdd&&!role.getIdRole().equals(0L)&&
                            //провертить есть ли роль  Продуктовый менеджер для руководителя
                            (user.isStructurator(processType) || role.getNameRole().equalsIgnoreCase("Продуктовый менеджер")&& user.hasRole(processType, "Руководитель продуктового подразделения")
                                    ||role.getNameRole().equalsIgnoreCase("Клиентский менеджер")&& user.hasRole(processType, "Руководитель клиентского подразделения")
                                    ||role.getNameRole().equalsIgnoreCase("Клиентский менеджер поддерживающего подразделения")&& user.hasRole(processType, "Руководитель поддерживающего клиентского подразделения"))) {
                        assignButton = "<input type=\"checkbox\" onClick=\"AssignProjectTeam('"+team.getUser().getIdUser()+"','"+role.getIdRole()+"')\">";
                    }
                }
                if(role.equals(roles.get(0))){%>
        <tr class="<%=section %>user<%=team.getUser().getIdUser()%>">
            <td rowspan="<%=roles.size()%>"><a class="fancy" href="roleslist.jsp?login=<%=team.getUser().getLogin() %>">
                <%=team.getUser().getFullName() %></a>
                <%=team.getUser().getGss()?"<br />(Менеджер ГСС)":""%>
            </td>
            <td rowspan="<%=roles.size()%>"><%=team.getUser().getDepartment().getShortName() %></td>
            <td><%=role.getNameRole() %></td>
            <%if(section.equals("p")){ %><td><%=assignButton %></td><%} %>
            <%if(privilegeAdd){ %><td rowspan="<%=roles.size()%>"><button
                onclick="<%=removeOnClick%>">исключить<%=hasAssignedRole?".":"" %></button></td><%}%>
        </tr>
        <%}else{ %>
        <tr class="<%=section %>user<%=team.getUser().getIdUser()%>"><td><%=role.getNameRole() %></td><%if(section.equals("p")){ %><td><%=assignButton %></td><%} %></tr>
        <%}} %>
        <%} %>
        </tbody>
    </table>
    <br />
    <%if(privilegeAdd){ %>
    <button onclick="return AddProjectTeam('<%=section%>')">Пригласить в группу '<%=sections.get(section) %>'</button>
    <%}%>
</div>
    <%}%>
    <%if(showRequestForm){%>
    <div id="tabs-3">
                    <b>Адресат:</b><br />
                    <%for (ru.md.spo.dbobjects.ProjectTeamJPA team : task.getProjectTeam()) {%>
                    <input type="checkbox" name="requestTo" value="<%=team.getUser().getIdUser().toString()%>"><%=team.getUser().getFullName() %><br />
                    <%} %>
                    <br />
                    <b>Тема запроса (выберите из списка или введите в поле 'Другая'):</b><br/>
                    <select id="requestSubjectSelect" onchange="onrequestSubjectSelectChange()">
                        <option value=""></option>
                        <%
                            ru.masterdm.compendium.domain.spo.RequestDict[] requestList = {};
                            try {
                                requestList = compenduim.findRequestList("",null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            for(ru.masterdm.compendium.domain.spo.RequestDict r : requestList){%>
                        <option value="<%=r.getName()%>"><%=r.getName() %></option>
                        <%} %>
                    </select>
                    <br/><b>Другая:</b>&nbsp;
                    <input type="text" value="" id="requestSubjectText" width="100px;">
                    <br /><b>Текст запроса:</b><br />
                    <textarea rows="5" id="requestText"></textarea>
                    <button onclick="sendRequest();return false;">сформировать запрос</button>
                    <div id="request_result"></div>
        <script language="javascript">
            function onrequestSubjectSelectChange(){
                if($('#requestSubjectSelect').val()==''){
                    $('#requestSubjectText').show();
                } else {
                    $('#requestSubjectText').hide();
                }
            }
            function requestResult(txt){
                $('#request_result').text(txt);
                $('#requestSubjectText').val('');
                $('#requestText').val('');
            }
            function sendRequest(){
                var users = "";
                $("input[name=requestTo]:checked").each(function(index) {
                    if(users!=""){users+=", ";}
                    users += $(this).val();
                });
                $.post('ajax/sendRequest.do',
                        {users: users, mdtaskid:$("#mdtaskid").val(),requestSubjectSelect:$('#requestSubjectSelect').val(),requestSubjectText:$('#requestSubjectText').val(),requestText:$('#requestText').val()},requestResult);
            }
            onrequestSubjectSelectChange();
        </script>
    </div><%}%>
    <div id="tabs-4">
        <%if(taskFacadeLocal.getRequestLogList(task.getId()).size()==0){ %>
        По заявке не сформировано ни одного запроса.
        <%} %>
        <%for(ru.md.spo.dbobjects.RequestLogJPA r : taskFacadeLocal.getRequestLogList(task.getId())){%>
        <div><%=Formatter.formatDateTime(r.getDate()) %> Пользователем <%=r.getFrom().getFullName() %> направлен запрос пользователю(-ям): <%=r.recepientsToString() %>.<br />
            <%if(!r.getSubject().isEmpty()){%>Тема: <%=r.getSubject()%><br /><%} %>
            Содержание запроса: <%=r.getBody() %></div>
        <br />
        <%} %>    </div>
</div>





