var selectedTargetId;
var selectedHiddenTargetId;

$(pipelineHandler);
$(document).ajaxComplete(pipelineHandler);

function onPipelineStatusManualChange() {
    if($('#mdTask_pipeline_status_manual').is(':checked'))
		$('#mdTask_pipeline_statuses_change').show();
    else
		$('#mdTask_pipeline_statuses_change').hide();
}
/**
 * Обработчик секции "Секция ПМ". Поиск секции по атрибуту <code>content="pipeline"</code>.
 * На необходимые поля вешаются обработчики-валидаторы.
 */
function pipelineHandler() {
    onPipelineStatusManualChange();
	var pipeline = $(document).find("div[content='pm_section'][pipelineHandle!='on']");
	if (pipeline.length > 0)
		pipeline.attr("pipelineHandle", "on");
	else 
		return;
	
	var $pipeline = $(pipeline);

	//обработка возвращаемых значений диалогов
	$pipeline.find(".dialogActivator").each(function() {
		defineDialogTarget($(this));
	});

	//добавление цели финансирования
	$pipeline.find("#mdTask_pipeline_financingObjectives_addLink").each(function() {
		$(this).on("click", function() {
			addFinancingObjective(this);
		});
	})
	
	//удаление цели финансирования
	$pipeline.find("a[name='mdTask_pipeline_financingObjectives_removeLink']").each(function() {
		$(this).on("click", function() {
			removeFinancingObjective(this);
		});
	})
	
	//обработка диалогов
	$pipeline.find("div.sectionPmDialog a").each(function() {
		$(this).on("click", function() {
			me = $(this);
			setTargetFromDialog(selectedTargetId, me.text(), selectedHiddenTargetId, me.attr("returnValue"));
		});
	});
	
	//обработка отображения плавающей ставки
	$pipeline.find("input[name='mdTask_fixedRate']").each(function() {
		var me = $(this);
		baseRateHandler(me);
		me.on("click", function() {
			baseRateHandler(me);
		});
	});
	
	//обработка изменения полей
	$pipeline.find("input[type!='hidden'], textarea, select").each(function() {
		$(this).on("change", function() {
			fieldChanged(this);
		});
	});
	
	//обработка полей с календарем
	$pipeline.find("input[valueType='date']").each(function() {
		$(this).on("focus", function() {
			displayCalendarWrapper($(this).attr("id"), '', false);
		});
	});
	
	//обработка формата поля
	$pipeline.find("input[valueType]").each(function() {
		$(this).on("blur", function() {
			input_autochange(this, $(this).attr("valueType"));
		});
	});
	dialogHandler();
}

/**
 * Добавляет из шаблона поле для записи новой цели финансирования.
 * @param addLink объект ссылки добавления новой цели финансирования
 */
function addFinancingObjective(addLink) {
	var parent = $(addLink).parent();
	var template = $.trim(parent.find("#mdTask_pipeline_financingObjectives_template").html());
	
	var fo = $(parent).append(template);
	var $fo = $(fo);
	var $textarea;
	$fo.find("textarea").each(function() {
		$textarea = $(this);
		$textarea.attr("name", "mdTask_pipeline_financingObjectives");
		$textarea.uniqueId();
		$textarea.on("change", function () {
			fieldChanged(this);
		});
	});
	$fo.find("a.mdTask_pipeline_financingObjectives_select").each(function() {
		$(this).on("click", function() {
			$('#selectedID').val($textarea.attr("id"));
			$('#mdTask_pipeline_financingObjectives').dialog({draggable:false, modal:true, width:800});
		});
	});
	$fo.find("a[name='mdTask_pipeline_financingObjectives_removeLink']").each(function() {
		$(this).on("click", function() {
			removeFinancingObjective(this);
		});
	});

	$(hideFinancingObjectivesEmptyFieldDiv).hide();
	dialogHandler();
}

function hideFinOdjectiveEmptyField() {
	$("#mdTask_pipeline_financingObjectives_addLink").trigger("click");
	$("#hideFinancingObjectivesEmptyFieldDiv").hide();
}

/**
 * Удалаяет из шаблона поле для записи цели финансирования.
 * @param removeLink объект ссылки удаления цели финансирования
 */
function removeFinancingObjective(removeLink) {
	$(removeLink).parent().remove();
}

/**
 * Установка выбранного значения в целевой элемент с идентификатором.
 * @param targetId идентификатор целевого элемента
 * @param value значение
 * @param hiddenTargetId идентификатор скрытого целевого элемента (используется для передачи идентификатора)
 * @param returnValue значение для записи в скрытый целевой элемент
 */
function setTargetFromDialog(targetId, value, hiddenTargetId, returnValue) {
	if (hiddenTargetId != null && hiddenTargetId != "") {
		var hiddenTarget = $("#" + hiddenTargetId);
		$(hiddenTarget).attr("value", returnValue);
	}
	
	var target = $("#" + targetId);
	var targetTag = $(target).prop("tagName").toLowerCase();
	if (targetTag == "textarea") {
		$(target).text(value);
	} else if (targetTag == "input") {
		$(target).attr("value", value);
	}
}

/**
 * Обработчик изменений поля ставки.
 * @param baseRateGroup jquery объект поля ставки
 */
function baseRateHandler(baseRateGroup) {
	var baseRateSection = $("#mdTask_baseRate_section");
	baseRateSection.show();
	/*if (baseRateGroup.attr("id") == "mdTask_fixedRate_true" && baseRateGroup.attr("checked"))
		baseRateSection.hide();
	else if (baseRateGroup.attr("id") == "mdTask_fixedRate_false" && baseRateGroup.attr("checked"))
		baseRateSection.show();
	else
		baseRateSection.hide();*/
}

/**
 * Определяет идентификаторы для записи значения выбранного в диалоге.
 * @param $dialogActivator jquery объект активатора диалога
 */
function defineDialogTarget($dialogActivator) {
	$dialogActivator.on("click", function() {
		selectedTargetId = $dialogActivator.attr("targetId");
		selectedHiddenTargetId = $dialogActivator.attr("hiddenTargetId");
	});
}
