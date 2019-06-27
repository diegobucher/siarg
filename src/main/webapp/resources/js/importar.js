function carregarFileStyle(){
	$('.filestyle').filestyle({
		buttonBefore: true,
		buttonName: "btn-primary",
		buttonText: "Selecionar",
		iconName: "glyphicon glyphicon-paperclip"
	});
	
}

function limparInputFile(){
	$('.filestyle').filestyle('clear');
}

function importarPlanilha(){
	$('#btnHidImportarPlanilha').click();
}