/**
 * Скрипт для главного меню.
 */
$(document).ready(function(){
	var counters = ["not_accept","accept","assign"];
	for(var i=0; i<counters.length; i++) {
		$.post('ajax/counter.do',{type: counters[i]},setCount);
	}
	calendarReInit();
	$.post('user_status.html',{},setUserMenu);
	$('#current_role_dialog').load('roleslist.jsp?login='+$('#login').val()+'&id='+$('#userid').val());
	try{
		$('#statistic').jqm({ajax: 'statistic.html', trigger: 'a.statistic'});
	} catch (e) {
		//ignore
	}
	$("a.fancy, a.supply").fancybox({
        'zoomOpacity'           : true,
        'zoomSpeedIn'           : 500,
        'zoomSpeedOut'          : 500,
        'hideOnContentClick': false,
        'frameWidth': 800, 
        'frameHeight': 600,
        'showCloseButton': true
    });
	$("a.edit-conditions").fancybox({
		'hideOnContentClick': false,
		'width': 500,
		'height': 420,
		'showCloseButton': false,
		'autoDimensions':false,
		'titleShow': false
	});
});
function setUserMenu(json){
	var ans = $.parseJSON(json);
	if(ans.boss) $('.boss_menu').show();
	else $('.boss_menu').hide();
	if(ans.worker) $('.not_worker_menu').hide();
	else $('.not_worker_menu').show();
}
function setCount(xml) {
	$("#"+$('type:first',xml).text()+"count").text(" ("+$('count:first',xml).text()+")");
}
//Очистить форму взято с
//http://www.javascript-coder.com/javascript-form/javascript-clear-form-example.htm
function clearForm(oForm) {
	var elements = oForm.elements;
	oForm.reset();
	for(i=0; i<elements.length; i++) {
		field_type = elements[i].type.toLowerCase();
		switch(field_type) {
			case "text":
			case "password":
			case "textarea":
			case "hidden":

			elements[i].value = "";
			break;

			case "radio":
			case "checkbox":
			if (elements[i].checked) {
			elements[i].checked = false;
			}
			break;

			case "select-one":
			case "select-multi":
			elements[i].selectedIndex = -1;
			break;

			default:
			break;
		}
	}
}
function onClickRefuseButton(id){
	if(confirm('Вы отказываетесь от исполнения заявки. Продолжить?')) {
		window.location='task.accept.do?isAccept=0&id0='+id+'&target=accept';
	}
}
function onClickAcceptButton(id){
	if(confirm('Вы начинаете работу с заявкой в качестве исполнителя. Продолжить?')) {
		window.location='task.accept.do?id0='+id+'&isAccept=1';
	}
}
function calendarReInit(){
	$('input.date').datepicker({
		dateFormat: 'dd.mm.yy',
		forceParse: false,
		changeMonth: true,
		changeYear: true,
		showWeek: true,
		firstDay: 1
	});
	//ll-skin-santiago
}
