/*============== | Formulário |===========================*/
$(document).ready(function(){
	hideStatus()	
});

function showStatus() {
	$("#idLoad").show();
	$.blockUI({message: null});
}
 
function hideStatus() {
	$("#idLoad").hide();		
	$.unblockUI();
}

$(function(){
	$.fn.modal.Constructor.DEFAULTS.backdrop = 'static';
});

function somenteNumero(e) {
	var tecla = e.charCode;
	if (tecla == undefined) { // Validação para o IE
		tecla = e.keyCode;
	}
	tecla = String.fromCharCode(tecla);
	if (e.which == 8 || e.which == 0 || /^\-?([0-9]+|Infinity)$/.test(tecla)) {
		return;
	} else {
		e.keyCode = 0;
		e.charCode = 0;
		e.returnValue = false;
		return false;
	}
}

function overSomenteNumeros(element) {
	var valor = $(element).val();
	if (valor != "" && valor != undefined && !/^\-?([0-9]+|Infinity)$/.test(valor)) {
		$(element).val("");

	} else {
		return;
	}
}