function AddSupplyGarant(addPerson) {
	fieldChanged();
	var param = {
			id:$('#CRMID').val(),
			name:$('#selectedName').val()
	};
	$( "#newGarantTemplate" ).tmpl(param).appendTo( "#idTablesSupplyGar > TBODY" );
	var orgtd_template_id = "GarantTemplateOrg";
	if(addPerson=='y') { orgtd_template_id = "GarantTemplatePerson";}
	$('#orgtd'+$('#CRMID').val()).html( $( "#"+orgtd_template_id ).tmpl( param ) );
	fancyClassSubscribe();
	
	dialogHandler();
	calendarInit();
}
function d_changeIssuer(id){
	fieldChanged();
	$('#d_issuer'+id).val($('#CRMID').val());
	$('#d_issuer_span'+id).html('<a href="clientInfo.html?id='+$('#CRMID').val()+
			'&mdtask='+ $('#mdtaskid').val() + '" target="_blank">'+$('#selectedName').val()+'</a>');
}
function insertSupplyDepositTR(xml){
	fieldChanged();
	var rating = $('rating:first > value:first',xml).text();
	if (rating!='') rating = 'Рейтинг '+rating;
	var nextid=getNextId();
	var param = {
			nextid:nextid, 
			crmid:$('#CRMID').val(),
			orgname:$('#selectedName').val(),
			rating:rating
	};
	$( "#newDepositTemplate" ).tmpl(param).appendTo( "#idTablesSupplyD > TBODY" );
	var orgtd_template_id = "DepositorTemplateOrg";
	try{//делаю так сложно ради IE6. IE6 не поддерживает сравнения объекта со строкой
        if (xml=='person'){
        	orgtd_template_id = "DepositorTemplatePerson";
        }
    } catch (rr)
    {
    	orgtd_template_id = "DepositorTemplateOrg";
    }
	$('#orgtd'+nextid).html( $( "#"+orgtd_template_id ).tmpl( param ) );
	fancyClassSubscribe();
	calendarInit();
}
function insertWarrantyTr(xml) {
	fieldChanged();
	var nextid = getNextId();
    var personmode='n';
    try{//делаю так сложно ради IE6. IE6 не поддерживает сравнения объекта со строкой
        if (xml=='person'){
            personmode='y';
        }
    } catch (rr)
    {
        personmode='n';
    }
    if (personmode=='y'){
        org='<a href=PersonInfo.jsp?id='+
        $("#CRMID").val()+
        ' target=_blank>'+$("#selectedName").val()+'</a>'+
        '<input type="hidden" name="w_contractor"/>'+
        '<input type="hidden" name="w_person" value="'+$("#CRMID").val()+'"/>';
    } else {
        var rating = $('rating:first > value:first',xml).text();
        if (rating != '') rating = '<span class="rating">Рейтинг поручителя '+rating+'</span>';
        org='<a href=clientInfo.html?id='+
        $("#CRMID").val()+
        ' target=_blank>'+$("#selectedName").val()+'</a> '+rating+
        '<input type="hidden" name="w_contractor" value="'+$("#CRMID").val()+'"/>'+
        '<input type="hidden" name="w_person"/>';
    }
    org=org+'<input type="hidden" name="w_guid" value="'+nextid+'"/>';

	var param = {
			id:$('#CRMID').val(),
			name:$('#selectedName').val(),
			guid:nextid
	};
	$( "#newWarrantyTemplate" ).tmpl(param).appendTo( "#idTablesWarranty > TBODY" );
	$('#orgtd'+$('#CRMID').val()).html( org );
	fancyClassSubscribe();
	calendarInit();
}
function AddPromissoryNote() {
	fieldChanged();
	var nextid=getNextId();
	var param = {nextid:nextid};
	$( "#newPromissoryNoteTemplate" ).tmpl(param).appendTo( "#idTable_promissory_note > TBODY" );
	calendarInit();
}
function g_FullSum_change(id) {
	fieldChanged();
	if($('#g_FullSum'+id).prop("checked")){
		$('#g_sum'+id).hide();
		$('#guarantee_sum'+id).val("0.00");
	} else {
		$('#g_sum'+id).show();
	}
}
function w_FullSum_change(guid) {
	fieldChanged();
    if (document.getElementById('w_FullSum'+guid).checked){
    	document.getElementById('w_sum'+guid).style.display = "none";
    	document.getElementById('w_resp'+guid+'a').checked=true;
    }else{
    	document.getElementById('w_sum'+guid).style.display = "";
    }
}
function openDialogAddSupply(addPerson,supply_code) {
	$('#supply_code').val(supply_code);
	var script = 'AddSupplyGarant';
	if(supply_code=='w')script = 'AddWarranty';
	if(supply_code=='d')script = 'AddDeposit';
	var href='popup_org.jsp'; 
	if(addPerson=='y') {
		href='popup_persons.jsp';
		script = script+'(\'y\')'
	} else {
		script = 'openDialogAddSupplyKz()'
	}
	href += '?formName=variables&ek=only&fieldNames=selectedID|selectedName|CRMID&onMySelect='+script +'&first=first';
	window.open(href, 'orgek','top=100, left=100, width=800, height=710');
}
function openDialogAddSupplyKz(){
	$.post('ajax/orgname.html',{id: $('#CRMID').val()}, openDialogAddSupplyKz3);
}
function openDialogAddSupplyKz3(data){
	$('#selectedName').val(data);
	var supply_code = $('#supply_code').val();
	if(supply_code=='w'){
		AddWarranty('n');
	}
	if(supply_code=='d'){
		AddDeposit('n');
	}
	if(supply_code=='g'){
		AddSupplyGarant('n');
	}
}
function AddWarranty(addPerson) {
	fieldChanged();
	if(addPerson=='y') {
		insertWarrantyTr('person');
	} else {
		$.post(
		    'ajax/rating.do',{org: $('#CRMID').val()},
		    insertWarrantyTr
		);
	}
	
	dialogHandler();
}
function AddDeposit(addPerson) {
	fieldChanged();
	if(addPerson=='y') {
		insertSupplyDepositTR('person');
	} else {
		$.post(
				  'ajax/rating.do',{org: $('#CRMID').val()},
				  insertSupplyDepositTR
			);
	}
	
	dialogHandler();
}
