$(document).ready(function() {
	carregarObservadoresDemanda();
	DemandasFilhasDataTables.init();
	FileStyle.init()
	Summernote.init()
	bindEventsModal();
	gerarGraficoLinhaTempo();
	
	$("#resposta_rapida").easyAutocomplete(optionsAutoComplete);
});

$(".btn_fechar").click(function(){
	$(".alert").hide();
	$("#modal_fechar").modal({
		backdrop: "static",
		keyboard: false,
	});
});

function handlerAbrirModalConfirmarCancelar(){
	rmcAtualizaEditorCancelar([{name: 'content', value: $('#editor').summernote('code')}]);
}

function abrirModalCancelar(xhr, status, args){
	
	if(args.validationFailed || args.naoValido){
		if(args.limparAnexo == "true"){
			limparAnexo();
		}
		scrollUp();
		return;
	}
	
	$(".alert").hide();
	$("#modal_cancelar").modal({
		backdrop: "static",
		keyboard: false,
	});
}

function handlerAbrirModalConfirmarFechar(){
	rmcAtualizaEditorFechar([{name: 'content', value: $('#editor').summernote('code')}]);
}

function abrirModalFechar(xhr, status, args){
	
	if(args.validationFailed || args.naoValido){
		if(args.limparAnexo == "true"){
			limparAnexo();
		}
		scrollUp();
		return;
	}
	
	$(".alert").hide();
	$("#modal_fechar").modal({
		backdrop: "static",
		keyboard: false,
	});
}

function scrollUp(){
	$(window).scrollTop(0)
}

/** Efetua o bind dos eventos dos botoes do modal */
function bindEventsModal(){
	
	/** Modal Consulta: botão concluir seleção das caixa-postais */
	$(document).on('click', '.btn-step2-concluir', function (e){
		
		var prazo = parseInt($('#prazo').val().trim())
		if (typeof prazo === 'undefined' || prazo == '' || isNaN(prazo) || prazo < 1 || prazo > 999) {
			$alert.show(Messages.MA011, $alert.type.ERROR, true);
			return;
		}
		
		if ($('.check-item:checked').length === 0) {
			$alert.show(Messages.MA016, $alert.type.ERROR, true);
			return;
		}
		
		exibirSecaoModal('sec-resumo');
	});

	/** Modal Consulta: voltar para seleção de caixas-postais */
	$(document).on('click', '.btn-step3-voltar', function (){
		$('.check-item, .uncheck-all')
			.prop('checked', false);
		
		$('.uncheck-all')
			.addClass('check-all')
			.removeClass('uncheck-all');
		
		exibirSecaoModal('sec-unidades');
	});
	
	/**  Modal Consulta: marcar todas as caixas-postais */
	$(document).on('click', '.check-all', function(){
		$(this).closest('.row').eq(0).find('input.check-item').prop('checked', true)
		$(this).toggleClass('check-all uncheck-all')
	})
	
	/**  Modal Consulta: desmarcar todas as caixas-postais */
	$(document).on('click', '.uncheck-all', function(){
		$(this).closest('.row').eq(0).find('input.check-item').prop('checked', false)
		$(this).toggleClass('check-all uncheck-all')
	})
	
	/** botão de confirmação da operação */
	$(document).on('click', '.btn-step3-concluir', function (e){
		var param = []
		$('.check-item:checked').each(function(){
			param.push($(this).val())
		})
		
//		rmcSetPrazo([{name:'dias_prazo', value: parseInt($('#prazo').val().trim())}]);
//		rmcMarcarCaixasPostais([{name:'ids_caixas_postais', value: JSON.stringify(param)}]);
		rmcDadosModalConsulta ([{name:'ids_caixas_postais', value: JSON.stringify(param)},
			{name:'dias_prazo', value: parseInt($('#prazo').val().trim())}]
		);
	})
}

/** Variavel para controle das mensagens do modal */
var $alert= {
	$elm: '#modal-message-consulta',
	$container: '#mdlConsulta',
	type: {
		INFO: 'alert-info',
		ERROR: 'alert-danger',
		WARNING: 'alert-warning',
		SUCCESS: 'alert-success'
	},
	show: function (message, type, scroll) {
		scroll = scroll || false
		
    	$(this.$elm).html('<div class="row"><div class="col-md-12"><div class="alert"><p></p></div></div></div>')
    		.find('div.alert')
    		.addClass(type)
    		.find('p')
    		.text(message)
    	
    	if (scroll) { 
    		$('#mdlConsulta').scrollTop(0)
    	}
    },
    close: function () {
    	$(this.$elm).html('')
    }
};

/** Carrega o multiselect de observadores da demanda */
function carregarObservadoresDemanda() {
	$("#observadoresDemanda").multiselect({
		enableFiltering : true,
		filterPlaceholder: "Pesquisar...",
		enableCaseInsensitiveFiltering: true,
		inheritClass : true,
		maxHeight : 200,
		delimiterText : ' / ',
		numberDisplayed : '10',
	});

}

/** Summernote web based editor */
var Summernote = {
	init: function () {
		$('#editor').summernote({
    		height: 200,
    		minHeight: null,
    		maxHeight: null,
    		disableDragAndDrop: true,
    		focus: false,
    		lang: 'pt-BR',
    		toolbar:[
    			['style',['bold','italic', 'underline','superscript','subscript','strikethrough', 'color', 'clear']],
    			['link',['link','unlink']],
    			['para',['ul','ol','paragraph']],
    			['table',['table']],
    			['undo',['undo']],
    			['redo',['redo']],
    			['fullscreen',['fullscreen']]
    		]
    	})
    	
    	$('#editor').summernote('code', $('#codigoHtml').val())
	}
}

/** Datatables das demandas vinculadas */
var DemandasFilhasDataTables = {
	init : function(){
		$('#dtb_demandas_filhas').dataTable({
				
			  "columnDefs": [
				{"targets": [0], "orderable": false},
				{"className":"text-center cell-middle", "targets":[ 0 ]},
				{"responsivePriority": 1, "targets": 0 },
	
			  ],
			  "order":[1,'asc'],
			  "lengthMenu":[ 5 ],
			  "lengthChange":false,
			  searching: true,  
			  responsive:true,
			  destroy: true,
			  paging: true,
			  language: {
						  "sEmptyTable": "Nenhum registro encontrado",
						  "sInfo": "Mostrando de _START_ até _END_ de _TOTAL_ registros",
						  "sInfoEmpty": "Mostrando 0 até 0 de 0 registros",
						  "sInfoFiltered": "(Filtrados de _MAX_ registros)",
						  "sInfoPostFix": "",
						  "sInfoThousands": ".",
						  "sLengthMenu": "_MENU_ Resultados por página",
						  "sLoadingRecords": "Carregando...",
						  "sProcessing": "Processando...",
						  "sZeroRecords": "Nenhum registro encontrado",
						  "sSearch": "Pesquisar",
						  "oPaginate": {
							  "sNext": "Próximo",
							  "sPrevious": "Anterior",
							  "sFirst": "Primeiro",
							  "sLast": "Último"
						  },
						  "oAria": {
							  "sSortAscending": ": Ordenar colunas de forma ascendente",
							  "sSortDescending": ": Ordenar colunas de forma descendente"
						  }
			  }						  
		}); 
	}
}

/** FileStyle plugin - Plugin de anexar*/
var FileStyle = {
	init: function () {
		$('#anexo').filestyle({
    		buttonBefore: 'true',
    		buttonName: 'btn-primary',
    		buttonText: 'Anexar',
    		iconName: 'glyphicon glyphicon-paperclip'
    	})
    	
    	$('.bootstrap-filestyle input').val('');
		
    	if ($('#filename').val()) {
    		$('.bootstrap-filestyle input').val($('#filename').val())
    	}
	}
}

/** Limpar o componente de anexo */
function limparAnexo(){
	rmcLimparAnexo();
}

/** Modal de Consulta */
var ModalConsulta = {
	init: function () {
		$('#mdlConsulta').modal({
			backdrop: 'static',
			keyboard: false
		});
	}
}

/** Abre o modal de Consulta */
function abrirModalConsulta(xhr, status, args){
	if(args.validationFailed || args.naoValido){
		if(args.limparAnexo == "true"){
			limparAnexo();
		}
		scrollUp();
		return;
	}
	ModalConsulta.init();
	
	$('.check-item, .uncheck-all')
	.prop('checked', false);

	$('.uncheck-all')
	.addClass('check-all')
	.removeClass('uncheck-all');

	exibirSecaoModal('sec-unidades');
}

/** Abre o modal de Consulta */
function abrirModalEncaminharExterna(xhr, status, args){
	if(args.validationFailed || args.naoValido){
		if(args.limparAnexo == "true"){
			limparAnexo();
		}
		scrollUp();
		return;
	}
	
	$(".alert").hide();
	$("#modal_externa").modal({
		backdrop: "static",
		keyboard: false,
	});
}


function handlerAbrirModalConsulta(){
	rmcAtualizaEditorConsultar([{name: 'content', value: $('#editor').summernote('code')}]);
}

function prepararCamposModalEncaminharExterna(){
	var nome = $('#anexo').val();
	var tamanho = $('#anexo').get(0).files.length > 0 ? $('#anexo').get(0).files[0].size : 0;
	
	rmcValidarAbrirModalEncaminharExterna([{name: 'nome', value: nome}, {name: 'tamanho', value: tamanho} ]);
}

function prepararCamposModalConsultar(){
	var nome = $('#anexo').val();
	var tamanho = $('#anexo').get(0).files.length > 0 ? $('#anexo').get(0).files[0].size : 0;
	
	rmcValidarAbrirModalConsulta([{name: 'nome', value: nome}, {name: 'tamanho', value: tamanho} ]);
}

function prepararCamposModalFechar(){
	var nome = $('#anexo').val();
	var tamanho = $('#anexo').get(0).files.length > 0 ? $('#anexo').get(0).files[0].size : 0;
	
	rmcValidarAbrirModalFechar([{name: 'nome', value: nome}, {name: 'tamanho', value: tamanho} ]);
}

function prepararCamposModalCancelar(){
	var nome = $('#anexo').val();
	var tamanho = $('#anexo').get(0).files.length > 0 ? $('#anexo').get(0).files[0].size : 0;
	
	rmcValidarAbrirModalCancelar([{name: 'nome', value: nome}, {name: 'tamanho', value: tamanho} ]);
}

function handlerAbrirModalEncaminharExterna(){
	rmcAtualizaEditorEncaminharExterna([{name: 'content', value: $('#editor').summernote('code')}]);
}

function exibirSecaoModal(section){
	
	$alert.close()
	
	$('section.' + section).show();
	$('section:not(.' + section + ')').hide();
	
	if (section === 'sec-unidades') {
		$('#mdlConsulta .modal-title').text('Selecione as caixas postais')
		$('.btn-step2-concluir, .btn-step2-voltar').show()
		$('button[class*="btn-step"]:not(.btn-step2-concluir, .btn-step2-voltar)').hide()
		
	} else if (section === 'sec-resumo') {
		var html = ''
		$('label:has(.check-item:checked)').each(function(){
			html += $(this).text() + '; '
		})
		
		$('#mdlConsulta .modal-title').text('Confirmação')				
		$('.resumo-caixas-postais').html('<strong>' + html.replace(/(;\s+)$/, '') + '</strong>')
		$('.btn-step3-concluir, .btn-step3-voltar').show()
		$('button[class*="btn-step"]:not(.btn-step3-concluir, .btn-step3-voltar)').hide()
	}
	 
}

function validarEncaminharDemandaExterna(){
	
	if($('#unidade_externa').val() == ''){
		$alertExterna.show(Messages.MA002, $alert.type.ERROR, true);
		return;
	}
	
	encaminharDemandaExterna();
}


/*
 * Inicio dos remoteCommands dos botões de Iteração
 */
function atualizarConteudoEditorHtml(){
	rmcSetEditorContent([{name: 'content', value: $('#editor').summernote('code')}])
}

function salvarRascunho(){
	rmcAtualizaEditorSalvaRascunho([{name: 'content', value: $('#editor').summernote('code')}]);
}

function cancelarDemanda(){
	rmcAtualizaEditorCancelarDemanda([{name: 'content', value: $('#editor').summernote('code')}]);
}

function encaminharDemanda(){
	var proximaExterna = $('#proximaCaixaExterna').val();
	if(proximaExterna === "true"){
		handlerAbrirModalEncaminharExterna();
	} else {
		rmcAtualizaEditorEncaminharDemanda([{name: 'content', value: $('#editor').summernote('code')}]);
	}
}

function encaminharDemandaExterna(){
	rmcAtualizaEditorEncaminharDemandaExterna([{name: 'content', value: $('#editor').summernote('code')}]);
}
function atualizarDemanda(){
	rmcAtualizaEditorAtualizarDemanda([{name: 'content', value: $('#editor').summernote('code')}]);
}
function questionarDemanda(){
	rmcAtualizaEditorQuestionarDemanda([{name: 'content', value: $('#editor').summernote('code')}]);
}

function responderDemanda(){
	rmcAtualizaEditorResponderDemanda([{name: 'content', value: $('#editor').summernote('code')}]);
}

function reabrirDemanda(){
	rmcAtualizaEditorReabrirDemanda([{name: 'content', value: $('#editor').summernote('code')}]);
}

function fecharDemanda(){
	rmcAtualizaEditorFecharDemanda([{name: 'content', value: $('#editor').summernote('code')}]);
}
/*
 * Fim dos remoteCommands dos botões de Iteração
 */


var $alertExterna= {
		$elm: '#modal-message-externa',
		$container: '#modal_externa',
		type: {
			INFO: 'alert-info',
			ERROR: 'alert-danger',
			WARNING: 'alert-warning',
			SUCCESS: 'alert-success'
		},
		show: function (message, type, scroll) {
			scroll = scroll || false
			
	    	$(this.$elm).html('<div class="row"><div class="col-md-12"><div class="alert"><p></p></div></div></div>')
	    		.find('div.alert')
	    		.addClass(type)
	    		.find('p')
	    		.text(message)
	    	
	    	if (scroll) { 
	    		$(this.$container).animate({ scrollTop: $(this.$elm).offset().top}, "fast")
	    	}
	    },
	    close: function () {
	    	$(this.$elm).html('')
	    }
};

function gerarGraficoLinhaTempo(){
	
	var dataInicioGrafico;
	
	var eventosList = JSON.parse($('#conteudoJsonGrafico').val());
	
	var events = [];
	
	for (i = 0 ; i < eventosList.length ; i++ ){

		var evento = eventosList[i];
		
		var event = {};
		var datas = [];
		var attrs = {}
		
		attrs.fill = evento.cor;
		attrs.stroke = evento.cor;
		
		if(i == 0){
			dataInicioGrafico = moment(evento.dataInicial).toDate();
		}
		datas.push(moment(evento.dataInicial).toDate())
		datas.push(moment(evento.dataFinal).toDate())

		event.dates = datas;
		event.title = evento.descricao;
		event.attrs = attrs;
		
		events.push(event);
	}
	
	if(events.length > 0){
		var timeline1 = new Chronoline(document.getElementById("graficoLinhaTempo"), events,
			{animated: true,
			tooltips: true,
			defaultStartDate: dataInicioGrafico,
			sectionLabelAttrs: {'fill': '#997e3d', 'font-weight': 'bold'},
			});
		
	}

}


var optionsAutoComplete = {
		
	//TODO PASSAR PARAMETROS NECESSARIOS, CAIXA E ASSUNTO
	url: function (phrase){
		return ctx + 'ConsultarRespostaRapidaServlet';
	},
	
	getValue: function(element){
		var respostaLimpa =  element.abreviacao.replace(/<\/?[^>]+(>|$)/g, "");
		return respostaLimpa;
	},
	
	ajaxSettings: {
		dataType: "json",
		method: "POST",
		data: {
			dataType: "json"
		}
	},
	
	preparePostData: function(data){
		data.phrase = $("#resposta_rapida").val();
		data.idAssunto = $("#idAssunto").val();
		return data;
	},
	requestDelay: 400,
	
	list: {
		maxNumberOfElements: 50,
		match:{
			enabled: false
		},
		onChooseEvent: function (){
			
			var resposta = $("#resposta_rapida").getSelectedItemData().resposta;
			resposta = $('#editor').summernote('code') + '<br>' + resposta;

	    	$('#editor').summernote('code', resposta.toString());
		}
	}
	
}

function limparRespostaRapida(){
	 $("#resposta_rapida").val('');
}

function mascaraContrato() {
	var maskBehavior = function(val) {
		valor = val.replace(/\D/g, '');
		return '0000000-00';
	}, options = {
		onKeyPress : function(val, e, field, options) {
			field.mask(maskBehavior.apply({}, arguments), options);
		}
	}
	$('#idInserirContrato').mask(maskBehavior, options);
}
