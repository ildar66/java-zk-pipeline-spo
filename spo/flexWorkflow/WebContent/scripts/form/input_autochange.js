/********************************************************************************************************************/
/*      переформатирование входных значений												                            */
/*   'money' (3 знака после запятой)-> '123456 789.34 5 4 34' или '123 456789,345 434'	-> '123 456 789,345'        */
/*   'money2digits' (2 знака после запятой) -> '123456 789.34 5 4 34' или '123 456789,345 434'	-> '123 456 789,35' */
/*   'money1digits' (1 знак после запятой) -> '123456 789.34 5 4 34' или '123 456789,345 434'	-> '123 456 789,3'  */

/*   'digitsSpaces' -> '123456 789.34 5 4 34' или '123 456789,345 434'	-> '123 456 789'                     		*/
/*   'date'  -- dd/mm/yyyy, dd,mm,yyyy, dd.mm.yyyy  -> dd.mm.yyyy (with check for validity like 00.13.3009)  		*/
/*   'digits' -- not quite clear. '123456789.345434' или '123456789,345434'	-> '123456789.345434'            		*/
/*   'number' -- not quite clear. '1234567890'  -> '1234567890'                                              		*/
/********************************************************************************************************************/
function input_autochange(myfield, field_type) {
	//alert("myfield.id : " + myfield.id);
	fieldChanged();//устанавливаю флаг изменения поля
	//alert("myfield.value before: " + myfield.value);
	//alert("field_type:" + field_type);
	
	if ((field_type == 'money') || (field_type == 'money1digits') || (field_type == 'money2digits') || (field_type == 'money3digits') 
			|| (field_type == 'digitsSpaces') || (field_type == 'money2digitsOrInt'))  {
		var newnumber = toNumber(myfield, field_type);
		//alert('newnumber:' + newnumber);
		if (newnumber == null) return;
		myfield.value = toFormattedString(newnumber, field_type);
		//alert('myfield.value after:' + myfield.value);
	} else {
		var number_regexp=/^\d*/;
		var digits_regexp=/(^(\d+\s*)*\.\d*)|(\d+\s*)*/;
		var date_regexp = new RegExp("^((0[1-9]|[12][0-9]|3[01]){1}\.(0[1-9]|1[012]){1}\.((19|20)\\d\\d){1})$");
		//change all ',' and '/' to '.' for digits and date
		myfield.value=myfield.value.replace(/\//g,'.').replace(/\,/g,'.');
		//check types
		if (field_type == 'digits') myfield.value = digits_regexp.exec(myfield.value)[0];
	    if (field_type == 'number') myfield.value = number_regexp.exec(myfield.value)[0];
	    if (field_type == 'date') 
	    	if (date_regexp.test(myfield.value) == false) myfield.value = '';
	}
	outputvalue = myfield.value;
	//if (WAR_CONDITIONS == false) 
	//alert ('output: ' + outputvalue);
}

/**
 * Converts input value (as a value, not as an object) to number
 * Returns number if conversion is OK.
 * Returns null, if  conversion is not OK.
 * @param number number, not a field
 */
function toNumberFromString(number, field_type) {
	var numberObject = new Object();
	numberObject.value = number;
	return toNumber(numberObject, field_type);
}

/**
 * Converts input value (as object) to number
 * Returns number if conversion is OK.
 * Returns null, if  conversion is not OK.
 */
function toNumber(myfield, field_type) {
	/********************************************************************************************/
	/* Часть I. Преобразуем строку '123 456 789.345 434' или '123 456 789,345 434' в число      */
	/********************************************************************************************/
	
	//change all '.' and to ','
	//var NotFormatted0 = myfield.value.replace(/ /g,"");
	//var NotFormatted0 = (myfield.value).replace(/^\s*|\s*$/g,'');
	//var NotFormatted0 = (myfield.value).replace(/\s/g, "");
	// убираем пробелы и меняем запятую на точку
	var NotFormatted0 = removeWhiteSpace(myfield.value);
	if (NotFormatted0 == '') {
		myfield.value = '';
		return null;
	}
	var notFormatted = NotFormatted0.replace(/,/g,".");

	// не число!!!
	if (isNaN(notFormatted)) {
		myfield.value = ''
		return null; 
	}
	
	/********************************************************************************************/
	/* Часть II. Округляем число до трех знаков после запятой или до целого							        */
	/********************************************************************************************/
	// округляем число до трех знаков после запятой для типа 'money' и до целого числа для других типов данных. 
	coef = 1;  // для целых чисел
	if (field_type == 'money') coef = 100;  
	if (field_type == 'money3digits') coef = 1000;
	if (field_type == 'money2digits') coef = 100;
	if (field_type == 'money2digitsOrInt') coef = 100;
	if (field_type == 'money1digits') coef = 10;
	newnumber = Math.round(notFormatted*coef)/coef;
	/*
	// Запретим ввод отрицательных значений для типов money и digitsSpaces
	if (newnumber < 0) {
		myfield.value = ''
		return null
	}
	*/
	return newnumber;
}

/**
 * Converts number into String acording to format.
 * @param number top convert to
 * @param field_type type of formatting
 * @return formatted string
 */
function toFormattedString(number, field_type) {
	var WAR_CONDITIONS = false;   // if true, no conversion is performed
	
	/********************************************************************************************/
	/* Часть III. Обратное преобразование в строку: '123 456 789,35' или '123 456 789'          */
	/********************************************************************************************/
	newnumberFormatted = number;		
	// Обратное преобразование: меняем запятые на пробелы (разделитель разрядов) и точку на запятую (дробная часть)
	if (field_type=='money')
		if (WAR_CONDITIONS == false) newnumberFormatted = formatNumber(number, '0,000.00');
	if (field_type=='money3digits')
		if (WAR_CONDITIONS == false) newnumberFormatted = formatNumber(number, '0,000.000');
	if (field_type=='money2digits')
		if (WAR_CONDITIONS == false) newnumberFormatted = formatNumber(number, '0,000.00');
	if (field_type=='money2digitsOrInt'){
		if (WAR_CONDITIONS == false) newnumberFormatted = formatNumber(number, '0,000.00');
		//if(newnumberFormatted.endsWith('.00'))
		newnumberFormatted=newnumberFormatted.replace('\.00','');
	}
	if (field_type=='money1digits')
		if (WAR_CONDITIONS == false) newnumberFormatted = formatNumber(number, '0,000.0');
	
	if (field_type=='digitsSpaces')
		if (WAR_CONDITIONS == false) newnumberFormatted = formatNumber(number, '0,000');
	
	if (WAR_CONDITIONS == true) formattedValue = newnumberFormatted;
	else formattedValue = newnumberFormatted.replace(/,/g," ").replace(/\./gi,",");
	return formattedValue;
}

function removeWhiteSpace(input) {
	var output = "";
	for (var i = 0; i < input.length; i++) {
		//alert(i + ", " + input.charCodeAt(i))
		if (! ((input.charCodeAt(i) == 32) || (input.charCodeAt(i) == 160))) {
			output += input.charAt(i);
		}
	}
	return output;
}

/**
 * Formats the number according to the ‘format’ string; adherses to the american
 * number standard where a comma is inserted after every 3 digits. note: there
 * should be only 1 contiguous number in the format, where a number consists of
 * digits, period, and commas any other characters can be wrapped around this
 * number, including ‘$’, ‘%’, or text examples (123456.789): ‘0 - (123456) show
 * only digits, no precision ‘0.00 - (123456.78) show only digits, 2 precision
 * ‘0.0000 - (123456.7890) show only digits, 4 precision ‘0,000 - (123,456) show
 * comma and digits, no precision ‘0,000.00 - (123,456.78) show comma and
 * digits, 2 precision ‘0,0.00 - (123,456.78) shortcut method, show comma and
 * digits, 2 precision
 * 
 * @method format
 * @param format
 *            {string} the way you would like to format this text
 * @return {string} the formatted number
 * @public
 */
function formatNumber(number, format) {
	if (number == null) return '';
	if (number == '') return '';
	if (typeof format != 'string') return '';

	var hasComma = (format.indexOf(',') > -1);
	// strip non numeric
	var	psplit = format.replace(/[^0-9-.]/g,"");
	psplit = psplit.split('.');
	var	that = number;
	// compute precision
	if (1 < psplit.length) {
		// fix number precision
		that = that.toFixed(psplit[1].length);
	}
	// error: too many periods
	else if (2 < psplit.length) {
		throw('NumberFormatException: invalid format, formats should have no more than 1 period: ' + format);
	}
	// remove precision
	else {
		that = that.toFixed(0);
	}
	// get the string now that precision is correct
	var fnum = that.toString();
	
 
	// format has comma, then compute commas
	if (hasComma) {
		// remove precision for computation
		psplit = fnum.split('.');
 
		var cnum = psplit[0],
			parr = [],
			j = cnum.length,
			m = Math.floor(j / 3),
			n = cnum.length % 3 || 3; // n cannot be ZERO or causes infinite
										// loop
		// break the number into chunks of 3 digits; first chunk may be less
		// than 3
		for (var i = 0; i < j; i += n) {
			if (i != 0) {n = 3;}
			parr[parr.length] = cnum.substr(i, n);
			m -= 1;
		}
 
		// put chunks back together, separated by comma
		fnum = parr.join(',');
 
		// add the precision back in
		if (psplit[1]) {fnum += '.' + psplit[1];}
	}
	// replace the number portion of the format with fnum
	return fnum; // format.replace(/[d,?.?]+/, fnum);
};
