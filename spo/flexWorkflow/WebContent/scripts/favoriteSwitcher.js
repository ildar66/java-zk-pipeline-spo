/**
 * Подключает обработчик (onclick) отправки асинхронного запроса на изменение статуса избранности заявки.
 * @param attachedHandlerFindPath путь поиска элемента, на который будет повешен обработчик
 * @param switcherFunction функция обработки успешного изменения статуса избранности заявки
 * @see <a href="http://api.jquery.com/Types/#Selector">JQuery Selector</a> 
 */
function favoriteSwitcher(attachedHandlerFindPath, switcherFunction) {
	$(function() {
	    $(document).find(attachedHandlerFindPath).each(function() {
	        $(this).on("click", function() {
	            $.post("ajax/favoriteSwitcher.html", {mdTaskId: $(this).attr("mdTaskId"), userId: $(this).attr("userId")}, switcherFunction);
	        });
	    });
	});
}
    
/**
 * Функция обработки успешного изменения статуса избранности заявки.
 * Обрабатывает тег <img mdTaskId=...>
 * @param mdTaskId возврат с сервера идентификатора заявки, у которой сменился статус избранности 
 */
function imgSwitcher(mdTaskId) {
	$(function() {
	    $(document).find("img[mdTaskId='" + mdTaskId + "']").each(function() {
	        img = $(this);
	        isFavorite = img.attr("favorite");
	        if (isFavorite == 1) {
	            img.attr("favorite", "0");
	            img.attr("src", "style/unfav.png");
	        } else {
	            img.attr("favorite", "1");
	            img.attr("src", "style/fav.png");
	        }
	    });
	});
}

/**
 * Обновление текущей страницы
 * @param mdTaskId возврат с сервера идентификатора заявки, у которой сменился статус избранности 
 */
function refreshCurrentPage(mdTaskId) {
    $(document).find("img[mdTaskId='" + mdTaskId + "']").each(function() {
    	window.location.href = window.location.href;
    });
}
