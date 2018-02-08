//Zoom для картинок, Написал Сергей Полевич. Работает только в
//интернет-эксплорерах.
function zoomer(img_id) {
	var img = document.getElementById(img_id);
	if (img.style.zoom=='50%') {
		img.style.zoom='100%'
	} else {
		img.style.zoom='50%'
	}
}
