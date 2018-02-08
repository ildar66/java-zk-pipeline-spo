/* Это действие отрабатывает при уходе со страницы — вызывается по события onBeforeUnload. Сейчас просто меняю курсор. */
function loading() {
	//document.body.style.cursor='progress';
	document.body.style.cursor='auto';
}