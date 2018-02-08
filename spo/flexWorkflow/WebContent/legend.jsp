<%@ page import="ru.md.spo.util.Config"%>
<%@ page contentType="text/html; charset=utf-8" %>

<head>
   <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
   <meta http-equiv="X-UA-Compatible" content="IE=Edge" />
</head>



<table align="left" frame="box" width="99%" >
<CAPTION> Легенда </CAPTION>
<tr>
    <td bgcolor="<%=Config.getProperty("COLOR_NOT_EXPIRED")%>">&nbsp;&nbsp;&nbsp;</td> <td><FONT size=1>До исполнения задания заданного срока</FONT></td>
    <td bgcolor="<%=Config.getProperty("COLOR_ONE_DAY")%>">&nbsp;&nbsp;&nbsp;</td> <td><FONT size=1>До исполнения задания меньше заданного срока</FONT></td>
    <td bgcolor="<%=Config.getProperty("COLOR_EXPIRED")%>">&nbsp;&nbsp;&nbsp;</td> <td><FONT size=1>Срок исполнения задания истек</FONT></td>
    <td bgcolor="<%=Config.getProperty("COLOR_EXPIRED_PROCESS")%>">&nbsp;&nbsp;&nbsp;</td> <td><FONT size=1>Срок исполнения процесса истек</FONT></td>
</tr>

</table>
