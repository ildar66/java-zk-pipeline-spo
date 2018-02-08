
//Define calendar(s): addCalendar ("Unique Calendar Name", "Window title", "Form element's name", Form name")
addCalendar("Calendar1", "Выбор даты начала периода", "leftDate", "mainform");
addCalendar("Calendar2", "Выбор даты конца периода", "rightDate", "mainform");

// default settings for English
// Uncomment desired lines and modify its values
// setFont("verdana", 9);
 setWidth(90, 1, 15, 1);
// setColor("#cccccc", "#cccccc", "#ffffff", "#ffffff", "#333333", "#cccccc", "#333333");
// setFontColor("#333333", "#333333", "#333333", "#ffffff", "#333333");
 setFormat("dd.mm.yyyy");
// setSize(200, 200, -200, 16);

// setWeekDay(0);
 setMonthNames("Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь");
 setDayNames("Воскресенье", "Понедельник", "Вторник","Среда", "Четверг", "Пятница", "Суббота");
 setLinkNames("[Закрыть]", "[Очистить]");
