<h1>Список пользователей</h1>
<ul>
<#list model["users"] as user>
  <li><a href="javascript:Go('${user.idStr}');">${user.fullName}</a></li>
</#list>
</ul>
<script language="javascript">
    function Go(strval) {
        var outform = window.opener.document.forms['variables']
        if(outform != null)
        {
            outform['etuserId'].value = unescape(strval);
        }
        
        window.opener.focus();
        window.close();
        if (opener.execScript) {
            opener.execScript('AddExpertTeam2()'); //for IE
        } else {
           eval('self.opener.AddExpertTeam2()'); //for Firefox
        }
    }
</script>