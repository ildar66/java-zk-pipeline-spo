function DelTwoRowWithLast(tableId, chkName) {
	fieldChanged();
	$('#'+tableId+' > tbody > tr > td.delchk > input:checked').parent().parent().next().remove();
	$('#'+tableId+' > tbody > tr > td.delchk > input:checked').parent().parent().remove();
	
	try
	{
	 	 var tbl = document.getElementById(tableId);
	     var body = tbl.getElementsByTagName("TBODY")[0];
		 if (!tbl){
			return
		 }
		var chk = tbl.getElementsByName(chkName);
		var Rows = body.rows;
		for (i=chk.length-1; i>=0; i--) 
			if (chk[i].checked)				
				body.removeChild(Rows[i]);
	}
	catch (Err)
	{
		//alert(Err.description);
		return false;
	}
}