/**
 * Created by Andrey Pavlenko on 04.06.15.
 */
(function() {
    var app = angular.module('spoModule', []);

    app.controller('ListController', function($scope, $http){
        var sections = ["accept", "perform", "starred","project_team"];
        //список заявок внизу
        $scope.tasks_hash = {};
        $scope.tasks_pagenum_hash = {};
        for (var i in sections) {
            $scope.tasks_hash[sections[i]]=[];
            $scope.tasks_pagenum_hash[sections[i]]=0;
        }

        this.toPage = function(page,tasktype){
            $scope.tasks_pagenum_hash[tasktype] = page;

            $('#tabs-'+tasktype+'-loading').show();

            $http.get('task_list.html?navigation='+page+'&type='+tasktype
                +'&searchNumber='+$('#searchNumber'+tasktype).val()
                +'&searchStatus='+$('#searchStatus'+tasktype).val()
                +'&searchType='+$('#searchType'+tasktype).val()
                +'&searchProcessType='+$('#searchProcessType'+tasktype).val()
                +'&searchSumFrom='+$('#searchSumFrom'+tasktype).val()
                +'&searchSumTo='+$('#searchSumTo'+tasktype).val()
                +'&searchCurrency='+$('#searchCurrency'+tasktype).val()
                +'&searchCurrOperation='+$('#searchCurrOperation'+tasktype).val()
                +'&searchContractor='+$('#searchContractor'+tasktype).val()
                +'&search='+encodeURIComponent($('#search'+tasktype).val())
            )
                .then(function(res){
                    $scope.tasks_hash[tasktype] = res.data;

                    $('#tabs-'+tasktype+'-loading').hide();
                });
        }

        this.openTaskTab = function(sectionKey) {
            this.toPage(0, sectionKey);
        }

        this.refuseTask = function(idTask){
            $http.get('task.accept.do?isAccept=0&id0='+idTask+'&target=accept').then(function(res){
                $http.get('task_list.html?navigation='+$scope.tasks_pagenum_hash['accept']+'&type=accept')
                    .then(function(res){
                        $scope.tasks_hash['accept'] = res.data;
                    });
            });
        }
        $http.get('task_list.html?navigation=0&type=accept').then(function(res){$scope.tasks_hash['accept'] = res.data;});
        $http.get('task_list.html?navigation=0&type=perform').then(function(res){$scope.tasks_hash['perform'] = res.data;});
        $http.get('task_list.html?navigation=0&type=starred').then(function(res){$scope.tasks_hash['starred'] = res.data;});
        $http.get('task_list.html?navigation=0&type=project_team').then(function(res){$scope.tasks_hash['project_team'] = res.data;});
    });

    app.controller('TabController', function($scope, $http){
        $scope.currtab = [];

        $scope.tabs = [{name:'Заемщики'}];
        $scope.tabs_view = [];

        this.setTab = function(newValue){//открывается обычная секция
            if(this.isSet(newValue)){
                $scope.currtab.splice($scope.currtab.indexOf(newValue), 1);
            } else {
                var existOpenView = false;
                for (var i = 0; i < $scope.tabs_view.length; i++)
                    if(this.isSet($scope.tabs_view[i].code))
                        existOpenView = true;
                if(existOpenView && !this.isSet('allcontr') || $scope.currtab.length==0) {//если открыто представление и оно не конрагенты, то диалог
                    //вопрос о сохранении заявки
                    $('#tabcode').val(newValue);
                    if($('#b1save').size()==0){
                        this.openTabView('false');
                    } else {
                        $('#setViewConfirm').dialog({draggable: false,width: 400});
                    }
                } else {//если ни одно представление не открыто, то просто открываем секцию
                    $scope.currtab.push(newValue);
                    //и закрываем контрагентов
                    if(this.isSet('allcontr'))
                        $scope.currtab.splice($scope.currtab.indexOf('allcontr'), 1);
                }
            }
            //подгрузить аяксом, если нужно
            loadSection(newValue, this.getUrlByCode(newValue));
            this.syncCookie();
        };

        this.getUrlByCode = function(code){
            var all_sections = $scope.tabs.concat($scope.tabs_view);
            for (var i = 0; i < all_sections.length; i++)
                if(all_sections[i].code == code)
                    return all_sections[i].url;
        };

        this.setTabView = function(newValue){//открывается или закрывается представление
            if(this.isSet(newValue)){
                $scope.currtab = [];//закрыли все обычные секции и другие представления
            } else {
                //вопрос о сохранении заявки
                $('#tabcode').val(newValue);
                if($('#b1save').size()==0){
                    this.openTabView('false');
                } else {
                    if(this.isSet('allcontr')){
                        this.openTabView('false');
                    } else {
                        $('#setViewConfirm').dialog({draggable: false,width: 400});
                    }
                }
            }
        };

        this.openTabView = function(save_flag){
            if(save_flag == 'false'){//не сохраняем, просто переключаем
                //нужно ли удалить данные со старых секций? Если да, то циклом переписать загрузку. Жду тестирование от Дили
                $scope.currtab = [];//закрыли все обычные секции и другие представления
                $scope.currtab.push($('#tabcode').val());
                this.syncCookie();
                this.clearAllSection();//очистить все другие секции
                loadSection($('#tabcode').val(), this.getUrlByCode($('#tabcode').val()));
            } else {//сохраняем чтобы потом открыть нужное представление. Работаем с куки
                var all_sections = $scope.tabs.concat($scope.tabs_view);
                for (var i = 0; i < all_sections.length; i++)
                    $.cookie('section_'+all_sections[i].code+'_'+$('#mdtaskid').val(),
                        (all_sections[i].code==$('#tabcode').val())?1:0, { expires : 1 });
                submitData(false);
            }
        };

        this.clearAllSection = function(){
            var all_sections = $scope.tabs.concat($scope.tabs_view);
            for (var i = 0; i < all_sections.length; i++)
                $('#'+all_sections[i].code).html('Идет загрузка данных...');
        };

        this.expandTabs = function() {//открыть все обычные секции и закрыть все представления
            $scope.currtab = [];
            for (var i = 0; i < $scope.tabs.length; i++) {
                $scope.currtab.push($scope.tabs[i].code);
                if($('#'+$scope.tabs[i].code).html().lastIndexOf("Идет загрузка данных", 0) === 0) {
                    var cachebuster = Math.round(new Date().getTime() / 1000);
                    $('#'+$scope.tabs[i].code).load(this.getUrlByCode($scope.tabs[i].code) + ($scope.tabs[i].code=='docs'?'':$('#md_frame_params').val()+'&cb=' +cachebuster));
                }
            }
            document.getElementById('section_data').scrollTop = 10;
            this.syncCookie();
        };

        this.syncCookie = function(){
            var all_sections = $scope.tabs.concat($scope.tabs_view);
            for (var i = 0; i < all_sections.length; i++)
                $.cookie('section_'+all_sections[i].code+'_'+$('#mdtaskid').val(), this.isSet(all_sections[i].code)?1:0, { expires : 1 });
        };

        this.collapseTabs = function(){
            $scope.currtab = [];
            this.syncCookie();
        };

        this.isSet = function(tabName){
            return $.inArray(tabName, $scope.currtab) >= 0;
        };

        $scope.restoreTabs = function(){//при открытии заявки восстанавливаются открытые секции
            var all_sections = $scope.tabs.concat($scope.tabs_view);
            for (var i = 0; i < all_sections.length; i++) {
                var cookie_name = 'section_'+all_sections[i].code+'_'+$('#mdtaskid').val();
                var c = $.cookie(cookie_name);
                if(c==null){
                    c = all_sections[i].code=='contractor'?'1':'0';
                    if ($('#dash_mode').val() == 'true')
                        c = all_sections[i].code=='pm_section'?'1':'0';
                }
                if(c=='1'){
                    $scope.currtab.push(all_sections[i].code);
                    var code = all_sections[i].code;
                    var url = all_sections[i].url;
                    setTimeout('loadSectionSimple(\''+code+'\', \''+url+'\')',1000);
                }
            }

            menu_and_tasklist_tabs_cookies_restore(); //при открытии восстанавливается состояние меню списка секций и списка заявок
        }

        this.isEmptyTab = function(){
            return $scope.currtab.length === 0;
        };

        $http.get('tabs_view.html?mdtaskid='+$('#mdtaskid').val())
            .then(function(res) {
                $scope.tabs_view = res.data;
                $http.get('tabs.html?mdtaskid='+$('#mdtaskid').val())
                    .then(function(res) { $scope.tabs = res.data; $scope.restoreTabs(); });
            });
    });

})();

function loadSection(code, url){//подгрузить аяксом, если нужно
    loadCode = code;
    $('#'+code).prev().addClass('paneractive');
    setTimeout(clearPanerActive, 3000);
    if($('#'+code).html().lastIndexOf("Идет загрузка данных", 0) === 0) {
        var cachebuster = Math.round(new Date().getTime() / 1000);
        setTimeout(sectionTiming, 50);
        $('#'+code).load(url + (code=='docs'?'':$('#md_frame_params').val()+'&cb=' +cachebuster),sectionTiming);
    } else {
        setTimeout(sectionTiming, 300);
    }
}
function loadSectionSimple(code, url){//подгрузить аяксом, если нужно. Без скролинга и расцвечивания заголовков
    loadCode = code;
    if($('#'+code).html().lastIndexOf("Идет загрузка данных", 0) === 0) {
        var cachebuster = Math.round(new Date().getTime() / 1000);
        $('#'+code).load(url + (code=='docs'?'':$('#md_frame_params').val()+'&cb=' +cachebuster),calendarInit);
    }
}
function clearPanerActive(){
    $('.paneractive').removeClass('paneractive');
}
/**
 * Синхронизация в cookies состояния свернутости-развернутости меню списка секций
 * @param visibleVariable <code><b>true</b></code> - если элемент виден на экране, иначе - <code><b>false</b></code>
 */
function menu_cookies_sync(visibleVariable) {
    var menuVisible = visibleVariable;
    if (menuVisible == null)
        menuVisible = $('#section_menu_td').is(':visible');
    $.cookie('section_menu_visible_'+$('#mdtaskid').val(), menuVisible?1:0, { expires : 1 });
}

/**
 * Синхронизация в cookies состояния свернутости-развернутости списка заявок
 * @param visibleVariable <code><b>true</b></code> - если элемент виден на экране, иначе - <code><b>false</b></code>
 */
function tasklist_tabs_cookies_sync(visibleVariable) {
    var taskListTabsVisible = visibleVariable;
    if (taskListTabsVisible == null)
        taskListTabsVisible = $('#tasklist_tabs').is(':visible');
    $.cookie('tasklist_tabs_visible_'+$('#mdtaskid').val(), taskListTabsVisible?1:0, { expires : 1 });
}

/**
 * При открытии заявки восстанавливается состояние свернутости-развернутости меню списка секций и списка заявок
 */
function menu_and_tasklist_tabs_cookies_restore() {
    try {
        var tabsCookieName = 'tasklist_tabs_visible_'+$('#mdtaskid').val();
        var tabsElementId = 'tasklist_tabs';
        cookieBasedShowElement(tabsCookieName, tabsElementId, show_hide_task_list_onclick);

        var menuCookieName = 'section_menu_visible_'+$('#mdtaskid').val();
        var menuElementId = 'section_menu_td';
        cookieBasedShowElement(menuCookieName, menuElementId, show_hide_menu);
        // TODO получается correct_content_height() вызывается дважды (cookieBasedShowElement->show_hide_task_list_onclick, cookieBasedShowElement->show_hide_menu)
    } catch(err) {
        alert("js error when menu_and_tasklist_tabs_cookies_restore '" + ((err.description == undefined) ? err : err.description) + "'");
    }
}

/**
 * Отображает или скрывает элемент с заданным <code><b>elementId</b></code> в зависимости от значения лежащего в cookie с именем <code><b>cookieName</b></code>. Если значение cookie равно <code><b>1</b></code>, то показать элемент (вызывается функция, заданная по наименованию). Иначе - скрыть.
 *
 * @param cookieName наименование cookie
 * @param elementId id html элемента
 * @param functionName наименование функции переключения видимости элемента
 */
function cookieBasedShowElement(cookieName, elementId, functionName) {
    try {
	   	var cookieValue = $.cookie(cookieName);
	    if (cookieValue == null || cookieValue == undefined)
	    	cookieValue = 1;
	    
	    var element = document.getElementById(elementId);
	    if (element == null)
            return;
	    	//throw "element is null by id '" + elementId + "'";
		var isVisible = (element.style.display == 'none') ? 0 : 1;
	    
	    if (cookieValue != isVisible)
	    	functionName();
    } catch(err) {
        throw "js error when cookieBasedShowElement. cookieName '" + cookieName + "', elementId '" + elementId + "', err '" + ((err.description == undefined) ? err : err.description) + "'";
    }
}

function calendarInit(){
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
function sectionTiming(){
    document.getElementById('section_data').scrollTop =document.getElementById(loadCode).offsetTop - 40;
    calendarInit();
    //var end = new Date().getTime();
    //$('#section_message').text('Время загрузки секции '+loadName+': '+ (end - startTime)/1000 + 'сек.');
    //$('#section_message').show();
    //setTimeout("$('#section_message').hide(1000)", 2000);
}

function show_hide_menu(){
    if($('#section_menu_td').is(':visible')){
        $('#section_menu_td').hide(250);
        $("#show_hide_menu_img").attr("src","style/images/menu_open.png");
        $("#section_data").css("padding-left", "10px");
        $("#show_hide_menu_img").css("left", "0px");
        menu_cookies_sync(false);
    } else {
        $('#section_menu_td').show(250);
        $("#show_hide_menu_img").attr("src","style/images/menu_hide.png");
        $("#section_data").css("padding-left", "1px");
        $("#show_hide_menu_img").css("left", "271px");
        menu_cookies_sync(true);
    }

    correct_content_height();
}
$(function() {
    $( "#tasklist_tabs" ).tabs();
});

function otherTask(idTask){
    globalurl='task.context.do?id='+idTask;
    if(changed){
        $('#goToDiv').jqmShow();
        return false;
    }
    location.href=globalurl;
}
function otherMdTask(idMdtask){
    globalurl='form.jsp?mdtaskid='+idMdtask+'&viewtype=projectteam';
    if(changed){
        $('#goToDiv').jqmShow();
        return false;
    }
    location.href=globalurl;
}
function takeTask(idTask){
    globalurl='task.accept.do?id0='+idTask+'&isAccept=1';
    if(changed){
        $('#goToDiv').jqmShow();
        return false;
    }
    location.href=globalurl;
}
function show_hide_task_list_onclick(){
    if($('#tasklist_tabs').is(':visible')){
        $('#tasklist_tabs').hide();
        $("#show_task_list_btn").show();
        tasklist_tabs_cookies_sync(false);
    } else {
        $('#tasklist_tabs').show();
        $("#show_task_list_btn").hide();
        tasklist_tabs_cookies_sync(true);
    }

    correct_content_height();
}


var resizeListener; //define the variable that will hold a reference to our setTimeout() function
var resizeListenerPause = 500; //the amount of time to wait after the resizing has finished before calling our function
$(window).resize(function(){

    //every time the window resize is called cancel the setTimeout() function
    clearTimeout(resizeListener);

    //set out function to run after a specified amount of time
    resizeListener = setTimeout(function(){ correct_content_height(); },
        resizeListenerPause);

});

$(function() {
    correct_content_height();
});


/**
 * Корректирует высоту области меню и данных
 */
function correct_content_height() {
    try {
        var menuAndContentTableId = 'menuAndContentTable';
        var projectTeamTabId = 'fullSearchFormproject_team';

        var menuAndContentTable = document.getElementById(menuAndContentTableId);
        var headerHeight = topCumulativeOffset(menuAndContentTable);

        var screenHeight = $(window).height();
        var contentAndFooterHeight = screenHeight - headerHeight;

        var testScreenHeight = 681;

        if ($('#tasklist_tabs').is(':visible')) {
            var tabsHeight = jQuery("#tasklist_tabs").height();
            var correction = tabsHeight + 50;

            var searchRowElement = document.getElementById(projectTeamTabId);
            var isHidden = (searchRowElement.style.display == 'none');
            if (isHidden == true)
                ;
            else {
                correction = correction + 1; // заявки развернуты. поиск развернут. Коррекция под IE и chrome
            }
            var d1 = contentAndFooterHeight - correction;

            $("#section_menu").css("height", d1 + "px");
            $("#section_data").css("height", (d1 + 21) + "px");
        } else {
            var correction = testScreenHeight - 635; // заявки свернуты
            var d1 = contentAndFooterHeight - correction;

            $("#section_menu").css("height", d1 + "px");
            $("#section_data").css("height", (d1 + 21) + "px"); //14 до низу
        }
    } catch(err) {
        alert("js error when correct_content_height'" + ((err.description == undefined) ? err : err.description) + "'");
    }
}

/**
 * Возвращает абсолютную Y-координату элемента в окне
 *
 * @param element элемент
 * @returns абсолютная Y-координата элемента в окне
 */
function topCumulativeOffset(element) {
    var top = 0;
    if (element != null) // добавил if (element != null) - иначе возникает js error
        do {
            top += element.offsetTop  || 0;
            element = element.offsetParent;
        } while(element);

    return top;
};

/**
 * Скрывает/показывает строку поиска во всех вкладках заявок в зависимости от текуще видимостисти строки поиска
 *
 * @param section ключ вкладки
 */
function showHideSearch(section) {
    var element = document.getElementById('fullSearchForm'+section);
    var isHidden = (element.style.display == 'none');
    var selector = "#tasklist_tabs table.taskLineList thead tr.search_row";
    if (isHidden)
        jQuery(selector).show();
    else
        jQuery(selector).hide();

    correct_content_height();
}

/*$('html').bind('keypress', function(e)
{
    if(e.keyCode == 13)
    {
        return false;
    }
});*/

function clearSearchFilter(section){
    $('#searchNumber'+section).val('');
    $('#searchContractor'+section).val('');
    $('#selectedName'+section).val('');
    $('#searchCurrency'+section).val('all');
    $('#searchType'+section).val('');
    $('#searchStatus'+section).val('');
    $('#searchProcessType'+section).val('all');
    $('#searchSumFrom'+section).val('');
    $('#searchSumTo'+section).val('');
    $('#searchCurrOperation'+section).val('');
}

function removeCurrency(code){
    $("#currency_list").append('<option value="'+code+'">'+code+'</option>');
    $('#compare_param_currency_'+code).remove();
}
function addCurrency(){
    if($('#currency_list').val()=='')
        return;
    var cur = $('#currency_list').val();
    var curhtml = '<label id="compare_param_currency_'+cur+'"><input type="checkbox" name="main_currencyList" value="'+
        cur+'" checked onClick="fieldChanged(this);removeCurrency(\''+cur+'\')" />'+cur+'</label>';
    //убрать из списка
    $("#currency_list option[value='"+cur+"']").remove();
    $('#compare_list_param_currency').before(curhtml);
}
function restoreTab(tabid){
    var c = $.cookie('current_'+tabid+'_'+$('#mdtaskid').val());
    if(c!=null){
        $("#"+tabid).tabs({ active: c });
    }
}
function storeTab(tabid, tabnumber){
    $.cookie('current_'+tabid+'_'+$('#mdtaskid').val(), tabnumber, { expires : 1 });
}
