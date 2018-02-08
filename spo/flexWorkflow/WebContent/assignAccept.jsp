<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page import="org.uit.director.contexts.WorkflowSessionContext"%>
<%@ page contentType="text/html;charset=utf-8" language="java"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<html>
<head>
<meta http-equiv="Content-Type"
	content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
<title>Подтвердите действие</title>
<style type="text/css">
@import url("resources/stylesheet.css");
</style>
<link rel="stylesheet" href="style/style.css" />
</head>
<script type="text/javascript">
function cancel() {
    document.getElementById("assignAcceptForm").action="task.context.do?id=${taskId}";
    document.getElementById("assignAcceptForm").submit();
    return false;
}
function assignAccept() {
    var userId = getCheckedValue();
    if (userId == -1) {
        alert("Выберите пользователя для акцепта операции");
        return false;
    }
    document.getElementById("userId").value=userId;
    document.getElementById("assignAcceptForm").action="assign.accept.do";
    document.getElementById("assignAcceptForm").submit();
    return false;
}
function getCheckedValue() {
    var radioGrp = document['forms']['assignAcceptForm']['group1'];
    for(i=0; i < radioGrp.length; i++){
        if (radioGrp[i].checked == true){
            var radioValue = radioGrp[i].value;
            return radioValue;
        }
    }
    return -1;
}
</script>
<body>
<jsp:include page="header_and_menu.jsp" />
<center>
<h3>Акцепт операции</h3>
<br />
<form name="assignAcceptForm" id="assignAcceptForm" action=""
	method="post">
<h2>Для акцепта операции выберите пользователя и нажмите кнопку
"Да". Для отмены операции нажмити кнопку "Нет"</h2>
<br />
<table>
	<c:forEach var="parentUser" items="${parentUserList}">
		<tr>
			<td><input type="radio" name="group1" id="group1"
				value="${parentUser.idUser}" />&nbsp;${parentUser.fullName}<br />
			</td>
		</tr>
	</c:forEach>
</table>
<br />
<input type="hidden" id="taskId" name="taskId" value="${taskId}">
<input type="hidden" id="userId" name="userId" value=""> <c:if
	test="${not empty approved}">
	<input type="hidden" id="approved" name="approved" value="${approved}">
</c:if> <input type="hidden" name="assigned" id="assigned" value="true">

<button onclick="return assignAccept();"
	style="width: 6em; height: 2em;">Да</button>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<button onclick="return cancel();" style="width: 6em; height: 2em;">Нет</button>
</form>
</center>
<jsp:include flush="true" page="footer.jsp" />
</body>
</html>