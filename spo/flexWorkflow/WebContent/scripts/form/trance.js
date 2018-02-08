function AddWithdraw(id) {
    fieldChanged();
    var nextid=getNextId();
    $( "#newWithdrawTemplate" ).tmpl( {unid:nextid,trid:id} ).appendTo( "#withdraw_table"+id+" > TBODY" );
    trance_period_format_change();
	calendarInit();
}
function tranceFlagControl(){
	if($('#trance_hard_graph').is(':checked')){
		$('#trance_limit_use_div').hide();
		$('#trance_limit_excess_div').hide();
	} else {
		$('#trance_limit_use_div').show();
		$('#trance_limit_excess_div').show();
	}
}
function trance_period_format_change(){
	var format = $('#trance_period_format').val();
	$('.trance_all_format').hide();
	$('.trance_'+format).show();
	validateWithdrawPeriod();
	calendarInit();
}
function validateWithdrawPeriod(){
	//Периоды предоставления не могут пересекаться
	$('#validateWithdrawPeriodError').hide();
	var format = $('#trance_period_format').val();
	var dates = [];
	$('.trance_year > input').each(function(i){
		var d ={};
		d.year = $(this).val();
		d.quarter = $(this).parent().parent().find('.trance_quarter > select').val();
		d.month = $(this).parent().parent().find('.trance_month option:selected').text();
		d.hyear = $(this).parent().parent().find('.trance_hyear > select').val();
		d.from = $(this).parent().parent().find('.trance_date > input[name=withdraw_from]').val();
		d.to = $(this).parent().parent().find('.trance_date > input[name=withdraw_to]').val();
		dates.push(d);
	});
	for(var i=0; i<dates.length; i++) {
		for(var j=i+1; j<dates.length; j++) {
			if(format == 'quarter' && dates[i].year == dates[j].year && dates[i].quarter == dates[j].quarter){
				$('#validateWithdrawPeriodError').show();
				$('#validateWithdrawPeriodError').text('Периоды предоставления не могут пересекаться: '+dates[i].quarter+' квартал '+dates[i].year+' года');
			}
			if(format == 'hyear' && dates[i].year == dates[j].year && dates[i].hyear == dates[j].hyear){
				$('#validateWithdrawPeriodError').show();
				$('#validateWithdrawPeriodError').text('Периоды предоставления не могут пересекаться: '+dates[i].hyear+' полугодие '+dates[i].year+' года');
			}
			if(format == 'year' && dates[i].year == dates[j].year){
				$('#validateWithdrawPeriodError').show();
				$('#validateWithdrawPeriodError').text('Периоды предоставления не могут пересекаться: '+dates[i].year+' год');
			}
			if(format == 'month' && dates[i].year == dates[j].year && dates[i].month == dates[j].month){
				$('#validateWithdrawPeriodError').show();
				$('#validateWithdrawPeriodError').text('Периоды предоставления не могут пересекаться: '+dates[i].month+' '+dates[i].year+' года');
			}
			if(format == 'date'){
				$.post('ajax/isCrossDate.html',{from1:dates[i].from,from2:dates[j].from,to1:dates[i].to,to2:dates[j].to},
						function(data){
					        if(data!='OK'){
					        	$('#validateWithdrawPeriodError').show();
					        	$('#validateWithdrawPeriodError').text(data);
					        }
					    });
			}
			
		}
	}
}