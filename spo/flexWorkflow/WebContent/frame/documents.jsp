<%@page import="ru.md.helper.TaskHelper"%>
<%@page import="ru.md.spo.ejb.PupFacadeLocal" %>
<%@page import="ru.md.spo.ejb.TaskFacadeLocal" %>
<%@page import="java.util.List"%>
<%@page import="com.vtb.util.Formatter"%>
<%@page import="ru.md.pup.dbobjects.DocumentTypeJPA"%>
<%@page import="ru.md.pup.dbobjects.DocumentGroupJPA"%>
<%@page import="ru.md.pup.dbobjects.AttachJPA"%>
<%@page import="ru.md.spo.dbobjects.TaskJPA"%>
<%@ page import="ru.md.controller.ConclusionController" %>
<%@ page import="ru.md.domain.dashboard.CCQuestion" %>
<%@ page import="ru.masterdm.spo.utils.SBeanLocator" %>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<br />
<%  String cryptoIssuers = SBeanLocator.singleton().getCompendiumMapper().getCryptoIssuers();
    if (cryptoIssuers == null)
        cryptoIssuers = "";	%>
<script type="text/javascript">
    var cryptoIssuers = "";
    cryptoIssuers = "<%=cryptoIssuers%>";
</script>
<script type="text/javascript" src="scripts/sign/mdSignature.js"></script>
<%
response.addHeader("Pragma", "no-cache");
response.addHeader("Expires", "-1");
response.addHeader("Cache-control", "no-cache");
//чтобы использовать универсальный FrameTag на форме заявки и контрагента параметры передаются через попу.
Long docType = "null".equals(request.getParameter("pupTaskId")) ? null : Long.valueOf(request.getParameter("pupTaskId"));
Long edcDocType=docType.equals(0L)?3L:docType;
String ownerid= "null".equals(request.getParameter("mdtaskid")) ? null : request.getParameter("mdtaskid");
PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
String edc_on = taskFacadeLocal.getGlobalSetting("edc_on");
boolean showEdc = edc_on.equalsIgnoreCase("1");
    String showOnlyNotExpiredParam = request.getParameter("showOnlyNotExpired");
    boolean showOnlyNotExpired= !(showOnlyNotExpiredParam ==null || "null".equals(showOnlyNotExpiredParam) || showOnlyNotExpiredParam.equals("false"));
Long idTypeProcess=0L;
Long mdtasknumber=0L;
Long mdtaskVersion=1L;
String ogrn="0";
String orgid_param="";
Long mdtaskid = 0L;
if(docType.equals(1L)){
    ogrn = TaskHelper.dict().getOrg(ownerid).getOgrn();
    orgid_param="&crmid="+ownerid;
}

String mdtaskParam = "null".equalsIgnoreCase(request.getParameter("mdtask")) ? null : request.getParameter("mdtask");

if(docType.equals(0L)){
    if(ownerid.startsWith("mdtaskid")){
        mdtaskid = Long.valueOf(ownerid.replaceAll("mdtaskid", ""));
	    TaskJPA task = taskFacadeLocal.getTask(mdtaskid);
	    if(task.getOrgList().size()>0) {ogrn=task.getOrgList().get(0).getOgrn();}
	    mdtasknumber=task.getMdtask_number();
        mdtaskVersion=task.getVersion();
    } else {
        Long ownerIdValue = Long.valueOf(ownerid);
        idTypeProcess=pupFacadeLocal.getProcessById(ownerIdValue).getProcessType().getIdTypeProcess();
	    TaskJPA task = taskFacadeLocal.getTaskByPupID(ownerIdValue);
	    mdtasknumber=task.getMdtask_number();
        mdtaskVersion=task.getVersion();
	    mdtaskid = task.getId();
	    if(task.getOrgList().size()>0) {ogrn=task.getOrgList().get(0).getOgrn();}
    }
} else {
    if (mdtaskParam !=null && !mdtaskParam.isEmpty() && !mdtaskParam.equals("0")) {
        mdtaskid = Long.valueOf(mdtaskParam);
        TaskJPA task = taskFacadeLocal.getTask(mdtaskid);
        mdtasknumber= task.getMdtask_number();
        mdtaskVersion= task.getVersion();
        idTypeProcess= task.getProcess().getProcessType().getIdTypeProcess();
    }
}
boolean editMode= docType.equals(0L)&&
	!TaskHelper.getCurrentUser(request).isReadOnlyUser(idTypeProcess)
		||docType.equals(1L)&&!TaskHelper.getCurrentUser(request).isReadOnlyUser(null)
		||docType.equals(2L);
boolean checkAttachSign = false;
try {
    String b = taskFacadeLocal.getGlobalSetting("checkAttachSign");
    checkAttachSign = b.equalsIgnoreCase("true");
}
catch (Exception e) {}
try {
String edcUpdate = Formatter.formatDateTime(taskFacadeLocal.getEdcLastUpdate(docType.toString(), ownerid));
if(showEdc){%>
<div class="msg" id="edcUpdateDate">
<%if(!edcUpdate.isEmpty()){%>
    Синхронизировано с ЭДК: <%=edcUpdate %>
<%}%>
</div>
<br />
<%}
if(docType.equals(0L) && pupFacadeLocal.findDelAttachemntByOwnerAndType(ownerid, docType).size()>0){%>
<a href="deletedDocs.jsp?pupid=<%=ownerid %>" target="_blank">Удаленные документы по заявке</a><br /><br />
<%}
if(editMode){
%>
<button onmouseover="Tip(getToolTip('Удалить документ'))" onmouseout="UnTip()" id="btnRemDoc" 
    title="Удалить отмеченные документы." 
    onclick="deleteDoc();return false">Удалить</button>
<%if(editMode&&docType.equals(0L)&&TaskHelper.getCurrentUser(request).isBoss(idTypeProcess)
||!docType.equals(0L)){ %>
<button onmouseover="Tip(getToolTip('Утвердить документ'))" onmouseout="UnTip()" id="btnAcceptDoc" 
    title="Утвердить отмеченные документы (руководителем)" onclick="acceptDoc();return false">Утвердить</button>
<%} %>
<button id="btnSignDoc" title="Подписать документ" onclick="signDoc();return false">Подписать документ</button>
<button onmouseover="Tip(getToolTip('Проверить ЭП документа'))" onmouseout="UnTip()" id="btnVerifyDoc"
    title="Проверить подлинность электронной подписи" onclick="checkSignSelectedFiles();return false">Проверить ЭП</button>
<%if(showEdc){ %>
<button id="btnEdcUp" onclick="docEdcUp('<%=docType %>','<%=ownerid %>','false');return false">Загрузить из ЭДК</button>
<%if(!ogrn.isEmpty()){ %>
<button id="btnOgrn" onclick="window.open('/compendium/sections/edc/edcSearchList.faces?clearFilter=true&mode=<%=edcDocType %>&ogrn=<%=ogrn %>&mdtaskNum=<%=mdtasknumber %>&version=<%=mdtaskVersion%><%=orgid_param %>','_blank');return false">Поиск в ЭДК</button>
<%}} %>
<%} %>
<div id="doc_error" class="doc_error" style="display: none;"></div>
<input type="hidden" name="frame_document" value="enable">
<input type="hidden" id="ownertype" value="<%=docType%>">
<input type="hidden" id="owner" value="<%=ownerid%>">

<input type="hidden" id="showOnlyNotExpired" value="<%=showOnlyNotExpired%>">
<input type="hidden" id="mdtask" value="<%=request.getParameter("mdtask")==null?"":request.getParameter("mdtask")%>">
<Br>
<input type="radio" <%if(showOnlyNotExpired){%>checked<%} %> name="showOnlyExpired" value="true" onclick="$('#showOnlyNotExpired').val('true');$('input[name=showOnlyExpired]').prop('disabled', true);refreshDocFrame()">Отображать актуальные<Br>
<input type="radio" <%if(!showOnlyNotExpired){%>checked<%} %> name="showOnlyExpired" value="false" onclick="$('#showOnlyNotExpired').val('false');$('input[name=showOnlyExpired]').prop('disabled', true);refreshDocFrame()">Отображать все<Br>
   
<table class="regular" id="attachtable">
<thead>
<tr>
    <td style="width:5%"></td>
    <td style="width:15%">Заголовок</td>
    <% for(CCQuestion q : ConclusionController.getCCQuestion(mdtaskid)){ %>
    <td style="width:5%">Передается на <%=q.depName==null?"КК":q.depName%></td>
    <% }%>
    <td style="width:10%">Срок действия</td>
    <td style="width:15%">Добавил</td>
    <td style="width:15%">Утвердил</td>
    <td style="width:15%">ЭП</td>
    <%if(docType.equals(1L)){%>
    <td style="width:15%">КЗ</td>
    <%}%>
    <td style="width:5%"></td>
    <td style="width:5%"></td>
</tr>
</thead>
<tbody>
<% for (DocumentGroupJPA docGroup : pupFacadeLocal.findDocumentGroupByOwnerTYpe(docType)) {
if (docGroup.getTypes().size()==0){continue;}//если группа пустая, то не выводим её
int totalCount =0;int existCount = 0;
for(DocumentTypeJPA type : docGroup.getTypes()){
    boolean existAttach = pupFacadeLocal.findAttachemnt(ownerid,docType,docGroup.getId(),type.getId(),showOnlyNotExpired).size()>0;
    if(existAttach || type.isActive()&&pupFacadeLocal.isDocumentGroupTypeActive(docGroup.getId(), type.getId())){ totalCount++;}
    if(existAttach){existCount++;}
}
if(existCount==0 && !docGroup.isActive()){continue;}
 %>
<tr><td class="docGroupWhite" colspan="<%=(8+ConclusionController.getCCQuestion(mdtaskid).size())%>">
<a onclick="docGroupToggle(<%=docGroup.getId() %>)" href="javascript:;">
<img src="theme/img/expand.jpg" alt="+" id="docGroupImg<%=docGroup.getId() %>" /> <%=docGroup.getNAME_DOCUMENT_GROUP() %>
(<%=existCount %> из <%=totalCount %> типов документов)</a></td></tr>
<%for(DocumentTypeJPA type : docGroup.getTypes()){
List<AttachJPA> list = pupFacadeLocal.findAttachemnt(ownerid,docType,docGroup.getId(),type.getId(),showOnlyNotExpired); 
if( !pupFacadeLocal.isDocumentGroupTypeActive(docGroup.getId(), type.getId()) && list.size()==0){continue;} %>
<tr class="attach<%=docGroup.getId()%>"  style="display: none;">
    <td colspan="9" <%=list.size()==0?"class=\"attachEmptyTypeWhite\"":""%>><em><%=type.getName() %></em>
    <%if(editMode && pupFacadeLocal.isDocumentGroupTypeActive(docGroup.getId(), type.getId())){ %><a class="modal" 
    href="addAttach.jsp?g=<%=docGroup.getId() %>&t=<%=type.getId()%>&ownertype=<%=docType %>&owner=<%=ownerid%>&mdtaskid=<%=mdtaskid%>" onclick="$('#docgroupid').val('<%=docGroup.getId()%>')">
    <img src="theme/img/plus.png" alt="+"></a><%} %>
</td></tr>
<% for(AttachJPA attach : list){%>
<tr class="attach<%=docGroup.getId()%> <%if(attach.isExpired()){ %> expired<%} %>" style="display: none;">
    <td>
        <input type="checkbox" name="unid" value='<%=attach.getUnid() %>'/>
        <input type="hidden" name="signature" value="<%=attach.getSIGNATURE() %>" id="signature<%=attach.getUnid() %>" />
        <input type="hidden" name="acceptsignature" value="<%=attach.getACCEPT_SIGNATURE() %>" id="acceptsignature<%=attach.getUnid() %>" />
    </td>
    <td><a target="_self" 
    <%if(checkAttachSign && !attach.isFILEURL()){ %>onclick="downloadAttach('<%=attach.getUnid() %>')" href="javascript:;"
    <%}else{ %> href="<%=attach.getFILEURL()%>" <%} %>>
    <img src="theme/img/<%if(attach.isFILEURL()){ %>URL.png<%}else{ %>Document.png<%} %>"> 
    <%=attach.getTitle() %></a></td>
    <% for(CCQuestion q : ConclusionController.getCCQuestion(mdtaskid)){ %>
    <td><input type="checkbox" <%=ConclusionController.isFORCC(q.id, attach.getUnid())?"checked":"" %> name="fileforCC"
               id="file4cc<%=attach.getUnid() %><%=q.id%>"
               value="<%=attach.getUnid() %>"
               <%if(editMode && attach.enable4ccButton()){%>onclick="on4ccClick('<%=attach.getUnid() %>','<%=q.id%>')"<%}else{ %>disabled="disabled"<%} %>
    /></td>
    <% }%>
    <td><%=Formatter.format(attach.getDATE_OF_EXPIRATION()) %></td>
    <td><% if(attach.getWhoAdd()!=null){%><%=attach.getWhoAdd().getFullName() %><%}else{ %>ЭДК<%} %>, <%=Formatter.formatDateTime(attach.getDATE_OF_ADDITION()) %></td>
    <td><% if(attach.getWhoAccepted()!=null){%> <span id="<%=attach.getUnid() %>accepted"><%=attach.getWhoAccepted().getFullName() %>, <%=Formatter.format(attach.getDATE_OF_ACCEPT()) %></span><% }%></td>
    <td id="sgnflg<%=attach.getUnid() %>"><%=attach.getSIGNATURE().equals("")?"<span title='Может быть подписан с компьютера ВТБ ПАО'>не подписан</span>":"подписан" %>
    <br />
        <%if(attach.getWhoSign()!=null){%>
            <%=attach.getWhoSign().getFullName()%>, <%=Formatter.formatDateTime(attach.getDate_of_sign()) %>
        <%}%>
    </td>
    <%if(docType.equals(1L)){%>
    <td><%=SBeanLocator.singleton().getDictService().getKzName(attach.getKz_backup())%></td>
    <%}%>
    <td><%if(TaskHelper.isCanEditDoc(attach,TaskHelper.getCurrentUser(request),idTypeProcess)){ %>
    <a href="javascript:;" onclick="$('#attach_title').val('<%=attach.getTitle() %>');$('#attach_period').val('<%=Formatter.format(attach.getDATE_OF_EXPIRATION()) %>');$('#attach_unid').val('<%=attach.getUnid() %>');$('#attach_docGroup').val('<%=docGroup.getId() %>');$('.attach_doctype').hide();$('#attach_doctype'+$('#attach_docGroup').val()).show();$('#attach_doctype'+$('#attach_docGroup').val()).val('<%=type.getId() %>');$('#editDocumentForm').dialog({width:800, height: 400, draggable: false, modal: true});">
    <img src="theme/img/edit18.png" title="редактировать"></a><%} %></td>
    <td></td>
</tr>
<%}}} %>
<tr><td class="docGroupWhite" colspan="<%=(8+ConclusionController.getCCQuestion(mdtaskid).size())%>">без группы<%if(editMode){ %><a class="modal"
href="addAttach.jsp?ownertype=<%=docType %>&owner=<%=ownerid%>&mdtaskid=<%=mdtaskid%>"><img src="theme/img/plus.png" alt="+" onclick="$('#docgroupid').val('')"></a><%} %></td></tr>
<%List<AttachJPA> list = pupFacadeLocal.findOtherAttachemnt(ownerid,docType,showOnlyNotExpired);
for(AttachJPA attach : list){%>
<tr <%if(attach.isExpired()){ %> class="expired"<%} %>><td colspan="9"><em><%=attach.getFILETYPE() %></em>
</td></tr>
<tr  <%if(attach.isExpired()){ %> class="expired"<%} %>>
    <td>
        <input type="checkbox" name="unid" value='<%=attach.getUnid() %>'/>
        <input type="hidden" name="signature" value="<%=attach.getSIGNATURE() %>" id="signature<%=attach.getUnid() %>" />
        <input type="hidden" name="acceptsignature" value="<%=attach.getACCEPT_SIGNATURE() %>" id="acceptsignature<%=attach.getUnid() %>" />
    </td>
    <td><a target="_self" 
    <%if(checkAttachSign&&!attach.isFILEURL()){ %>onclick="downloadAttach('<%=attach.getUnid() %>')" href="javascript:;"
    <%}else{ %> href="<%=attach.getFILEURL()%>" <%} %>>
    <img src="theme/img/<%if(attach.isFILEURL()){ %>URL.png<%}else{ %>Document.png<%} %>"> 
    <%=attach.getTitle() %></a></td>
    <% for(CCQuestion q : ConclusionController.getCCQuestion(mdtaskid)){ %>
        <td><input type="checkbox" <%=ConclusionController.isFORCC(q.id, attach.getUnid())?"checked":"" %> name="fileforCC"
                   id="file4cc<%=attach.getUnid() %><%=q.id%>"
               value="<%=attach.getUnid() %>"
               <%if(editMode && attach.enable4ccButton()){%>onclick="on4ccClick('<%=attach.getUnid() %>','<%=q.id%>')"<%}else{ %>disabled="disabled"<%} %>
            /></td>
    <% }%>
    <td><%=Formatter.format(attach.getDATE_OF_EXPIRATION()) %></td>
    <td><% if(attach.getWhoAdd()!=null){%> <%=attach.getWhoAdd().getFullName() %><% }else{%>ЭДК<%} %>, <%=Formatter.formatDateTime(attach.getDATE_OF_ADDITION()) %></td>
    <td><% if(attach.getWhoAccepted()!=null){%> <span id="<%=attach.getUnid() %>accepted"><%=attach.getWhoAccepted().getFullName() %>, <%=Formatter.format(attach.getDATE_OF_ACCEPT()) %></span><% }%></td>
    <td id="sgnflg<%=attach.getUnid() %>"><%=attach.getSIGNATURE().equals("")?"<span title='Может быть подписан с компьютера ВТБ ПАО'>не подписан</span>":"подписан" %>
        <br />
        <%if(attach.getWhoSign()!=null){%>
        <%=attach.getWhoSign().getFullName()%>, <%=Formatter.formatDateTime(attach.getDate_of_sign()) %>
        <%}%>
    </td>
    <%if(docType.equals(1L)){%>
    <td><%=SBeanLocator.singleton().getDictService().getKzName(attach.getKz_backup())%></td>
    <%}%>
    <td><%if(TaskHelper.isCanEditDoc(attach,TaskHelper.getCurrentUser(request),idTypeProcess)){ %>
    <a href="javascript:;" onclick="$('#attach_title').val('<%=attach.getTitle() %>');$('#attach_period').val('<%=Formatter.format(attach.getDATE_OF_EXPIRATION()) %>');$('#attach_unid').val('<%=attach.getUnid() %>');$('.attach_doctype').hide();$('#attach_doctype'+$('#attach_docGroup').val()).show();$('#editDocumentForm').dialog({width:800, height: 400, draggable: false, modal: true});"><img src="theme/img/edit18.png" title="редактировать"></a><%} %></td>
    <td></td>
</tr>
<%}%>

    </tbody>
</table>
<input type="hidden" id="isEdsRequiredSPO" value="<%=taskFacadeLocal.getGlobalSetting("isEdsRequiredSPO")%>" />
<script type="text/javascript">
$(document).ready(function() {
    $("a.modal").fancybox({
        'width': 1200, 
        'height': 500,
        'showCloseButton': true,
        'hideOnContentClick': false,
        'type'              : 'iframe',
        onClosed    :   function() {refreshDocFrame();}
    });
<%if(showEdc){ %>
    docEdcUp('<%=docType %>','<%=ownerid %>','true');
<%}%>
<%if(list.size()>0){%>
    try {
        $('#menu_docs > div').addClass('notempty');
    }catch(err) {}
<%}%>
});
</script>
<%
} catch (Exception e) {
	out.println("ERROR ON documents.jsp:" + e.getMessage());
	e.printStackTrace();
}
%>