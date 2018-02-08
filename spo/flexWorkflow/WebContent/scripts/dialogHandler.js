/**
 * Поиск и установка обработчика открытия диалога по css-классу dialogActivator.
 * Обязательные параметры элемента открытия диалога dialogId - идентификатор диалога.
 * Внутри диалога на элемент <a> вешается событие onclick с закрытием диалога.
 */
function dialogHandler() {
	$(document).find(".dialogActivator[dialogHandle!='on']").each(function() {
		$(this).attr("dialogHandle", "on");
		$(this).on("click", function() {
	        var dialogId = $(this).attr("dialogId");
	        $("#" + dialogId).dialog({draggable:false, modal:true, width:800});
        
	        $(document).find("#" + dialogId + " a").each(function() {
	        	$(this).on("click", function() {
	        		$("#" + dialogId).dialog('close');
                });
            });
        });
    });
}

$(dialogHandler);
$(document).ajaxComplete(dialogHandler);
