tinyMCE.init({
// General options
mode: "textareas",
language : "ru",
theme: "advanced",
skin: "o2k7",
plugins: "advhr,contextmenu,fullscreen,inlinepopups,insertdatetime,preview,tabfocus,table",
// Theme options
theme_advanced_buttons1: "bold,italic,underline,strikethrough,|,justifyleft,justifycenter,justifyright,justifyfull,formatselect,fontselect,fontsizeselect,|,forecolor,backcolor,|,undo,redo,|,fullscreen",
theme_advanced_buttons2: "tablecontrols",
theme_advanced_buttons3: "",
theme_advanced_toolbar_location: "top",
theme_advanced_toolbar_align: "left",
theme_advanced_statusbar_location: "bottom",
theme_advanced_resizing: true,
theme_advanced_path : false,
entity_encoding : "raw",
verify_html: false,
cleanup: false,
table_inline_editing: true,
mode: "specific_textareas",
editor_selector : "advanced_textarea",

onchange_callback : "fieldChanged"


});
