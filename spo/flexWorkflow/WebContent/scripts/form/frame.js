/**
 * Функция переключает раскрытие/схлопывание секции в форме заявки.
 * @param mdtaskid - id заявки
 * @param name - кодовое имя секции. По нему ищем id html-элементов
 */
function frameToggle(params,name){
	if($('#section_'+name+'_img').attr("alt")=="+"){// закрыта, открываем
		frameOpen(params,name);
	} else {// открыта, закрываем
		$('#section_'+name+'_tbody').attr("style","display:none;");
		$('#section_'+name+'_img').attr({src: "style/toOpen.gif",alt: "+"});
		$.cookie('section_'+name+$('#idTask').val()+'_'+$('#mdtaskid').val(), 0, { expires : 10 });
	}
}
function frameOpen(params,name){
	$('#section_'+name+'_img').attr({src: "style/toClose.gif",alt: "-"});
	if($('#section_'+name+'_td').html()==""){// нужно грузить секцию аяксом
		reloadFrame(params,name);
	} else {// секция уже была загружена, просто отображаем её
		$('#section_'+name+'_tbody').show();
		$('#section_'+name+'_tbody').attr("style","");
	}
	$.cookie('section_'+name+$('#idTask').val()+'_'+$('#mdtaskid').val(), 1, { expires : 10 });
}
function reloadFrame(params,name){
	var page='frame/'+name+'.jsp';
	if(name.indexOf('pipeline')!=-1){page='frame/'+name+'.html';}
	$('#section_'+name+'_td').load(page+'?'+params,function () {
		$('#section_'+name+'_tbody').show();
		$('#section_'+name+'_tbody').attr("style","");
		try{
			fancyClassSubscribe();
		} catch (e) {
			//ignore
		}
	});
}
/**
 * Функция переключает раскрытие/схлопывание секции в форме сравнения
 * @param params - параметры
 * @param name - кодовое имя секции. По нему ищем id html-элементов
 */
function frameToggleCompare(params,name){
	if($('#section_'+name+'_img').attr("alt")=="+"){// закрыта, открываем
		$('#section_'+name+'_img').attr({src: "style/toClose.gif",alt: "-"});
		if($('#section_'+name+'_td').html()==""){// нужно грузить секцию аяксом
			reloadFrameCompare(params,name);
		} else {// секция уже была загружена, просто отображаем её
			$('#section_'+name+'_tbody').show();
			$('#section_'+name+'_tbody').attr("style","");
		}
		$.cookie('section_'+name+$('#objectType').val()+'_'+$('#ids').val(), 1, { expires : 10 });
	} else {// открыта, закрываем
		$('#section_'+name+'_tbody').attr("style","display:none;");
		$('#section_'+name+'_img').attr({src: "style/toOpen.gif",alt: "+"});
		$.cookie('section_'+name+$('#objectType').val()+'_'+$('#ids').val(), 0, { expires : 10 });
	}
}
function reloadFrameCompare(params,name){
	var page='compare/frameCompare.jsp';
	$('#section_'+name+'_td').load(page+'?'+params,function () {
		$('#section_'+name+'_tbody').show();
		$('#section_'+name+'_tbody').attr("style","");
		try{
			fancyClassSubscribe();
		} catch (e) {
			//ignore
		}
	});
}
/**
 * Загрузить неподгруженные секции вручную. 
 */
function loadFramesManually() {
	var ajaxSections = ["department","supply","conclusion","priceCondition"];//асинхронно подгружаемые секции
	var allSectionLoaded = true;
	for(var i=0; i<ajaxSections.length; i++) {
		if($('#section_'+ajaxSections[i]+'_td').html()=="") {
			allSectionLoaded = false;
			$('#section_'+ajaxSections[i]+'_td').load('frame/'+ajaxSections[i]+'.jsp?' + $('#md_frame_params').val(), 
					{},loadFramesManually);
			break;
		}
	}
	if (allSectionLoaded) {
		unfade();
		return submitDataCallback();
	}
}

function fundPane(mdtaskid){
	if($('#fundImg'+mdtaskid).attr("alt")=="+"){
		$('#fund'+mdtaskid).load('ajax/fundList.do?mdtaskid='+mdtaskid,function () {
			$('#fundImg'+mdtaskid).attr({src: "theme/img/collapse.jpg",alt: "-"});
			$('#fund'+mdtaskid).parent().attr("colspan","10");
			$('#fund'+mdtaskid).parent().parent().show();
			$('#fund'+mdtaskid).parent().parent().attr("style","");
		});
	} else {
		$('#fund'+mdtaskid).parent().parent().attr("style","display:none;");
		$('#fund'+mdtaskid).empty();
		$('#fundImg'+mdtaskid).attr({src: "theme/img/expand.jpg",alt: "+"});
	}
}
function showCedTr(mdtaskid){
	$('#ced'+mdtaskid).load('ajax/cedList.html?mdtaskid='+mdtaskid,function () {
		$('#ced'+mdtaskid).parent().attr("colspan","10");
		$('#ced'+mdtaskid).parent().parent().show();
		$('#ced'+mdtaskid).parent().parent().attr("style","");
	});
}
function n6Pane(mdtaskid){
	if($('#n6Img'+mdtaskid).attr("alt")=="+"){
		$('#n6'+mdtaskid).load('ajax/n6List.do?mdtaskid='+mdtaskid,function () {
			$('#n6Img'+mdtaskid).attr({src: "theme/img/collapse.jpg",alt: "-"});
			$('#n6'+mdtaskid).parent().attr("colspan","10");
			$('#n6'+mdtaskid).parent().parent().show();
			$('#n6'+mdtaskid).parent().parent().attr("style","");
		});
	} else {
		$('#n6'+mdtaskid).parent().parent().attr("style","display:none;");
		$('#n6'+mdtaskid).empty();
		$('#n6Img'+mdtaskid).attr({src: "theme/img/expand.jpg",alt: "+"});
	}
}
function fundFrame(mdtaskid){
	if($('#fundImg').attr("alt")=="+"){
		$('#fundFrameDiv').load('ajax/fundList.do?mdtaskid='+mdtaskid,function () {
			$('#fundImg').attr({src: "style/toClose.gif",alt: "-"});
			$('#fundFrameDiv').parent().parent().parent().show();
			$('#fundFrameDiv').parent().parent().parent().attr("style","");
		});
		$.cookie('section_fund'+$('#idTask').val()+'_'+$('#mdtaskid').val(), 1, { expires : 10 });
	} else {
		$('#fundFrameDiv').parent().parent().parent().attr("style","display:none;");
		$('#fundFrameDiv').empty();
		$('#fundImg').attr({src: "style/toOpen.gif",alt: "+"});
		$.cookie('section_fund'+$('#idTask').val()+'_'+$('#mdtaskid').val(), 0, { expires : 10 });
	}
}
function n6Frame(mdtaskid){
	if($('#n6Img').attr("alt")=="+"){
		$('#n6FrameDiv').load('ajax/n6List.do?mdtaskid='+mdtaskid,function () {
			$('#n6Img').attr({src: "style/toClose.gif",alt: "-"});
			$('#n6FrameDiv').parent().parent().parent().show();
			$('#n6FrameDiv').parent().parent().parent().attr("style","");
		});
		$.cookie('section_n6'+$('#idTask').val()+'_'+$('#mdtaskid').val(), 1, { expires : 10 });
	} else {
		$('#n6FrameDiv').parent().parent().parent().attr("style","display:none;");
		$('#n6FrameDiv').empty();
		$('#n6Img').attr({src: "style/toOpen.gif",alt: "+"});
		$.cookie('section_n6'+$('#idTask').val()+'_'+$('#mdtaskid').val(), 0, { expires : 10 });
	}
}
/**
 * Восстанавливает состояние секций (открыта, свёрнута)
 */
function restoreSection(){
	var sections =['Контрагенты','inLimit','limitParam','priceConditionLimit','contract','graph',
	               'условия','supply','conclusion','priceCondition','oppParam',
	               'Комментарии','expertus','documents','standardPeriod','projectTeam','department',
	               'stopfactor','expAgree','fund','n6','stop_factors_3','stop_factors_2','stop_factors_1',
	               'sendRequest','histRequest','promissory_note','Гарантии','Поручительство','Залоги',
	               'Прочие условия','Условия досрочного погашения','fineList','commission',
	               'percentStavka','pipeline'];
	for (var i = 0; i < sections.length; i++) {
		var c = $.cookie('section_'+sections[i]+$('#idTask').val()+'_'+$('#mdtaskid').val());
		if(c==null && (sections[i]=='Контрагенты' || sections[i]=='limitParam'|| sections[i]=='oppParam')){c='1';}
		if(c=='1'){
			$('#section_'+sections[i]+' > thead').click();
		}
	}
	window.setTimeout(restoreSubSection, 4000);
}
/**
 * Подсекции, для которых родительская секций подгружается аяксом. Их гружу после паузы.
 * Лучше по событию, конечно. Но релиз нужно сделать сегодня, а есть еще задачи. Как всегда.
 */
function restoreSubSection(){
	var sections =['sendRequest','histRequest','promissory_note','Гарантии','Поручительство','Залоги'];
	for (var i = 0; i < sections.length; i++) {
		var c = $.cookie('section_'+sections[i]+$('#idTask').val()+'_'+$('#mdtaskid').val());
		if(c==null && (sections[i]=='Контрагенты' || sections[i]=='limitParam'|| sections[i]=='oppParam')){c='1';}
		if(c=='1'){
			$('#section_'+sections[i]+' > thead').click();
		}
	}
}
function doSection(id){
	try{
		var node, image, disp;
		node=getNode(null, 'section_'+id, 'TABLE');
		if (!node) return false;
		image=getNode(node, 'imgSection', 'IMG');
		node=node.tBodies[0];
		disp=(node.style.display=='none');
		if (image){
			image.src='style/'+((disp) ? 'toClose' : 'toOpen')+'.gif';
		}
		node.style.display=(disp) ? 'block' : 'none';
		$.cookie('section_'+id+$('#idTask').val()+'_'+$('#mdtaskid').val(), disp?1:0, { expires : 10 });
	} catch (Err){
		alert(Err.description);
	}
}

/**
 * Разблокирует кнопки по окончании загрузки формы заявки. Убирает панель с надписью 'Загружается'. 
 */
function unfade() {
	document.getElementById('fader').style.display='none';
	document.getElementById('controlPanel').style.display='block';
	var nodes=document.getElementsByTagName('select')
	for(var i=0; i<nodes.length; i++){
		nodes[i].style.visibility='visible';
	}
}

/**
 * Блокирует кнопки при начале загрузки формы заявки или при нажатии на кнопку 'Завершить операцию'. 
 * Показывает панель с надписью 'Загружается'.
 */
function fade() {
	document.getElementById('fader').style.display='';
	document.getElementById('controlPanel').style.display='none';
//	var nodes=document.getElementsByTagName('select')
//	for(var i=0; i<nodes.length; i++){
//		nodes[i].style.visibility='visible';
//	}
}
