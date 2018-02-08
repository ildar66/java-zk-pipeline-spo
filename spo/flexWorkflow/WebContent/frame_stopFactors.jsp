<%@taglib uri="/WEB-INF/pup-tag.tld" prefix="pup"%>
<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>
<%@page import="ru.md.helper.TaskHelper" %>
<%@page import="com.vtb.domain.Task" %>
<%@page import="java.util.ArrayList"%>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
try {

  boolean readOnlyClient = !TaskHelper.isEditMode("Стоп-факторы Клиент",request);
  boolean readOnlySecurity = !TaskHelper.isEditMode("Стоп-факторы Безопасность",request);
  Task task=TaskHelper.findTask(request);
  ArrayList taskClientStopFactorList = task.getTaskClientStopFactorList();
  ArrayList taskSecurityStopFactorList = task.getTaskSecurityStopFactorList();
  boolean isEmptyClient = (taskClientStopFactorList.size()==0);
  boolean isEmptySecurity = (taskSecurityStopFactorList.size()==0);
%>

			<jsp:include flush="true" page="frame_stopFactorsClient.jsp"/>
			<jsp:include flush="true" page="frame_stopFactorsSecurity.jsp"/>
			<!-- frame_stopFactors3.jsp -->
<%
} catch (Exception e) {
	out.println("Ошибка в секции  frame_stopFactors.jsp:" + e.getMessage());
	e.printStackTrace();
}
%>
