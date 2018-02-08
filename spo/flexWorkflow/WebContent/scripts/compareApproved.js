/**
 * 
 */
var compareResults = {};
// здесь происходит загрузка результатов сравнения
function loadCompareResult(compareBlock) {
	if (!compareResults.hasOwnProperty(compareBlock)) {
		var objType = ($('#tasktype').val() != 'Сделка') ? 'limit' : 'product';
		$.post('ajax/compare.html', {
			objectType : objType,
			ids : $('#lastApprovedVersion').val() + '|' + $('#mdtaskid').val(),
			current : 1,
			name : compareBlock
		}, function(data) {
			saveCompareResult(compareBlock, data);
			if (prevApprovedDiffShown)
				displayPrevApprovedDiff();
		});
	}
}

function saveCompareResult(compareBlock, xml) {
	if (!compareResults.hasOwnProperty(compareBlock)) {
		xmlDoc = $.parseXML( xml ),
	  $xml = $( xmlDoc );
		compareResults[compareBlock] = $xml;
		//console.log('Блок сравнения ' + compareBlock + ' загружен: \n' + xml);
	}
}

function prevApprovedDiff() {
	if (prevApprovedDiffShown) {
		hidePrevApprovedDiff();
		prevApprovedDiffShown = false;
	}
	else {
		if (compareResults.length == 0) {
			alert('Секции еще не загружены! Повторите операцию через некоторое время.');
		}
		else {
			displayPrevApprovedDiff();
			prevApprovedDiffShown = true;
		}
	}
	if (prevApprovedDiffShown)
		$("button#compareApprovedButton").text('Скрыть отличия');
	else
		$("button#compareApprovedButton").text('Показать отличия');
}

function hidePrevApprovedDiff() {
	$( ".compare-difference" ).each(function() {
		$( this ).removeClass("compare-difference");
	});
	$( ".compare-list-removed" ).each(function() {
		$( this ).html("");
	});
}

function displayPrevApprovedDiff() {
	var doneList = new Array();
	$.each(compareResults, function(key, xml) {
		$(xml).find('resultObject[id="' + $("#mdtaskid").val() + '"]').find('resultElement').each(
				function() {
					// обработка результата сравнения
					if ($(this).attr('wrong') == 'true') {
						var elemId = $(this).find('htmlName').text();
						$(elemId).addClass('compare-difference');
					}
					var list = $(this).attr('list');
					if (list !== undefined && list != "" && $.inArray(list, doneList) == -1) {
						// обработка списка
						var removedItems = new Array();
						$(xml).find('resultObject[id="' + $("#lastApprovedVersion").val() + '"]').
								find('resultElement[list="' + list + '"]').each(
							function() {
								var listElement = $(this).find('value').text();
								var myXML = $(xml).find('resultObject[id="' + $("#mdtaskid").val() + '"]').
								find('resultElement[list="' + list + '"]').filter(function() {
									return $(this).find('value').text() == listElement;
								});
								if (myXML.length == 0)
									removedItems.push(listElement);
							});
						if (removedItems.length > 0)
							$(list).html("Удалено : " + removedItems.join(', '));
						doneList.push(list);
					}
				});
	});
}