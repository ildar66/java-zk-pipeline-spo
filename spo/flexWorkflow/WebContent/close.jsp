
<%@ page language="java" contentType="text/html; charset=utf-8" %>
<html>
  <head><title></title></head>
  <body><br><br><center><big>Завершение сеанса</big></center>
        <script>
            setTimeout('window.close()', 2000);
        </script>
  <%
      request.getSession().setMaxInactiveInterval(1);
  %>
  </body>
</html>