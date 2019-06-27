$(document).ready(function() {
    app.initialize()
});

/** versão case insensitive do selector ":contains" do jQuery. */
$.expr[':'].icontains = function(a, i, m) {
  return jQuery(a).text().toUpperCase()
      .indexOf(m[3].toUpperCase()) >= 0;
};

var app = {
    initialize: function() {
    	Modal.init()
    	EasyTree.init()
    	FileStyle.init()
    	Summernote.init()
    	Multiselect.init()
    	   	
    	this.bindEvents()
    },
    bindEvents: function() {
    	var $self = this
    	
    	/** botão de abertura do modal de assuntos */
    	$(document).on('click', '.btn-modal-assunto', function (e){
        	e.preventDefault()       	
        	$('#mdlAssunto .modal-title').text('Alterar Assunto')
        	$('#mdlAssunto').modal('show')
        	
        	if (($('span.select-tree').length == 0)) {
    	    	$('.btn-step1-concluir')
    				.prop('disabled', true)
    				.addClass('disabled')
    	    }
        	
        	app.$modal.reset()
        })
        
        /** botão de busca de assuntos */
        $(document).on('click', '.btn-search-assunto', function (e){
        	e.preventDefault()
        	EasyTree.search($('#busca').val())
        })
        
        /** botão incluir demanda aberta ou como minuta */
    	$(document).on('click','.btn-save', function (e){
    		e.preventDefault()
    		$self.onSave = function () {
    			$('.btn-fake').click()
    		}
    		rmcSetEditorContent([{name: 'content', value: $('#editor').summernote('code')}])
    	})
    	
    	/** botão incluir demanda como rascunho */
    	$(document).on('click','.btn-save-draft', function (e){
    		e.preventDefault()
    		$self.onSave = function () {
    			$('.btn-fake-draft').click()
    		}
    		rmcSetEditorContent([{name: 'content', value: $('#editor').summernote('code')}])
    	})
    	
    	/** botão remover anexo */
    	$(document).on('click', '.btn-remove-file', function (e){
    		e.preventDefault()
    		rmcLimparAnexo()
    	})
    	
    	/** marcar todas as caixas-postais */
    	$(document).on('click', '.check-all', function(){
    		$(this).closest('.row').eq(0).find('input.check-item').prop('checked', true)
    		$(this).toggleClass('check-all uncheck-all')
    	})
    	
    	/** desmarcar todas as caixas-postais */
    	$(document).on('click', '.uncheck-all', function(){
    		$(this).closest('.row').eq(0).find('input.check-item').prop('checked', false)
    		$(this).toggleClass('check-all uncheck-all')
    	})
    	
    	//*************************** Modal ***************************
    	
    	/** botão concluir seleção do assunto */
    	$(document).on('click', '.btn-step1-concluir', function (e){
    		
    		$('.check-item, .uncheck-all')
				.prop('checked', false)
		
			$('.uncheck-all')
				.addClass('check-all')
				.removeClass('uncheck-all')
    		
    		if (!!$('.select-tree').data('responsavel')) {
    			app.$modal.show('sec-unidades')
    		} else {
        		rmcSelecionarAssunto([{name:'id-assunto', value: $('.select-tree').data('id')}])
        		$('#mdlAssunto').data('cancelable', true).modal('hide')
    		}
    	})
    	
    	/** botão concluir seleção das caixa-postais */
    	$(document).on('click', '.btn-step2-concluir', function (e){
    		var prazo = parseInt($('#prazo').val().trim())
    		if (typeof prazo === 'undefined' || prazo == '' || isNaN(prazo) || prazo < 1 || prazo > 999) {
    			app.$alert.show(Messages.MA011, app.$alert.type.ERROR, true)
    			return
    		}
    		
    		if ($('.check-item:checked').length === 0) {
    			app.$alert.show(Messages.MA016, app.$alert.type.ERROR, true)
    			return
    		}
    		
    		app.$modal.show('sec-resumo')
    	})
    	
    	/** botão de confirmação da operação */
    	$(document).on('click', '.btn-step3-concluir', function (e){
    		var param = []
    		$('.check-item:checked').each(function(){
    			param.push($(this).val())
    		})
    		
    		rmcSetPrazo([{name:'dias_prazo', value: parseInt($('#prazo').val().trim())}])
    		rmcSelecionarAssunto([{name:'id-assunto', value: $('.select-tree').data('id')}])
    		rmcMarcarCaixasPostais([{name:'ids_caixas_postais', value: JSON.stringify(param)}])
    		
    		$('#mdlAssunto').data('cancelable', true).modal('hide')
    	})
    	
    	/** botão voltar para inclusão ou acompanhamento de demanda */
    	$(document).on('click', '.btn-step1-voltar', function (){
    		if (!!$('#mdlAssunto').data('cancelable')) {
    			$('#mdlAssunto').modal('hide')
    		} else { 
    			rmcGoBack()
    		}
    	})
    	
    	/** botão voltar para seleção de assuntos */
    	$(document).on('click', '.btn-step2-voltar', function (){
    		app.$modal.show('sec-assuntos')
    	})
    	
    	/** voltar para seleção de caixas-postais */
    	$(document).on('click', '.btn-step3-voltar', function (){
    		$('.check-item, .uncheck-all')
    			.prop('checked', false)
    		
    		$('.uncheck-all')
    			.addClass('check-all')
    			.removeClass('uncheck-all')
    		
    		app.$modal.show('sec-unidades')
    	})
    },
    $modal: {
    	reset: function () {
    		$('#busca').val('')
    		this.show('sec-assuntos')
    	},
		show: function(section) {
			app.$alert.close()
			
			$('section.' + section).show()
			$('section:not(.' + section + ')').hide()
			
			if (section === 'sec-assuntos') {
				
				if($('#mdlAssunto .modal-title').text() !== 'Alterar Assunto') {
					$('#mdlAssunto .modal-title').text('Assunto')
				}
									
				$('.btn-step1-concluir, .btn-step1-voltar').show()
				$('button[class*="btn-step"]:not(.btn-step1-concluir, .btn-step1-voltar)').hide()
			} else if (section === 'sec-unidades') {
				$('#mdlAssunto .modal-title').text('Selecione as caixas postais')
				$('#prazo').val($('.select-tree').data('prazo'))
				$('.btn-step2-concluir, .btn-step2-voltar').show()
				$('button[class*="btn-step"]:not(.btn-step2-concluir, .btn-step2-voltar)').hide()
			} else if (section === 'sec-resumo') {
				var html = ''
				$('label:has(.check-item:checked)').each(function(){
					html += $(this).text() + '; '
				})
				
				$('#mdlAssunto .modal-title').text('Confirmação')				
				$('.resumo-caixas-postais').html('<strong>' + html.replace(/(;\s+)$/, '') + '</strong>')
				$('.btn-step3-concluir, .btn-step3-voltar').show()
				$('button[class*="btn-step"]:not(.btn-step3-concluir, .btn-step3-voltar)').hide()
			}
		}
	},
	$alert: {
		$elm: '#modal-message',
		$container: '#mdlAssunto',
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
	},
    onCompleteSelecionarAssunto: function () {
    	/* recarrega os plugins */
    	FileStyle.init()
    	Multiselect.init()
    	Summernote.init()
    	
    	/* recupera o código html do input hidden */
    	$('#editor').summernote('code', $('#codigoHtml').val())
    },
    onCompleteChangeDemandante: function (xhr) {
    	if (xhr.status == 'success') {  		
    		EasyTree.init(); 
    		Multiselect.init();
    		    		
    	    if (($('#pnlAssunto:has(li.active)').length == 0)) {
    	    	app.$modal.reset()
    	    	$('#mdlAssunto').data('cancelable', false).modal('show')
    	    	
    	    	$('.btn-step1-concluir')
    				.prop('disabled', true)
    				.addClass('disabled');
    	    }
    	}
    },
    onCompleteLimparAnexo: function () {
    	FileStyle.init()
    }
}

/** Summernote web based editor */
var Summernote = {
	init: function () {
		$('#editor').summernote({
    		height: 200,
    		minHeight: null,
    		maxHeight: null,
    		disableDragAndDrop: true,
    		focus: true,
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

/** Multiselect plugin */
var Multiselect = {
	init: function () {
		$('.select-caixas-postais').multiselect({
    		enableFiltering: true,
    		enableCaseInsensitiveFiltering: true,
    		inheritClass: true,
    		maxHeight: 200,
    		delimiterText:' / ',
    		numberDisplayed:'10',
    	})
	}
}

/** FileStyle plugin */
var FileStyle = {
	init: function () {
		$('#anexo').filestyle({
    		buttonBefore: 'true',
    		buttonName: 'btn-primary',
    		buttonText: 'Anexar',
    		iconName: 'glyphicon glyphicon-paperclip'
    	})
    	
    	if ($('#filename').val()) {
    		$('#anexo').filestyle('disabled', true)
    		$('.bootstrap-filestyle input').val($('#filename').val())
    	}
	}
}

/** Modal de seleção de assunto e caixas postais */
var Modal = {
	init: function () {
		var hasAssunto = ($('#pnlAssunto:has(li.active)').length > 0) ? true: false
				
		$('#mdlAssunto').modal({
			backdrop: 'static',
			keyboard: false,
			show: !hasAssunto
		})
		.data('cancelable', hasAssunto)
	}
}

/** árvore de assuntos */
var EasyTree = {
	min: 2,
	init: function () {
		/** marca todos os nós pais */
		$('.tree li:has(ul)').addClass('parent_li')	
			
		/** expand/collapse nodes */
		$('.tree li.parent_li > span').on('click', function (e) {
			var childNodes = $(this).parent('li.parent_li').find(' > ul > li')
			
			if (childNodes.is(':visible')) {
				/** collapse node */
				childNodes.hide('fast')
				$(this).prop('title', 'Abrir')
					.find(' > i')
					.addClass('fa-folder')
					.removeClass('fa-folder-open')
			} else {
				/** expand node */
				childNodes.show('fast')
				$(this).attr('title', 'Fechar')
					.find(' > i')
					.addClass(' fa-folder-open')
					.removeClass('fa-folder')
			}
			e.stopPropagation()
		});
			
		/** seleciona um assunto e habilita o concluir */
		$('li > span:has(i.fa-file-o)').click(function (e){
	    	e.preventDefault()
	    	
	    	$('.select-tree').removeClass('select-tree')
	    	$(this).addClass('select-tree')
	    	
	    	$('.btn-step1-concluir')
	    		.prop('disabled', false)
	    		.removeClass('disabled')
	    });
	},
	search: function (value) {
		$('.tree-item-found').removeClass('tree-item-found')
		app.$alert.close()
		
		if (typeof value == 'undefined' || value.trim().length < this.min) {
			app.$alert.show(Messages.get('MA015'), app.$alert.type.ERROR)
			return
		}
		
		var $self = this
		var nodes = $(".tree > ul li").has("span:icontains('" + value + "')")
		
		if (typeof nodes === 'undefined' || nodes.length == 0) {
			app.$alert.show(Messages.MA009, app.$alert.type.ERROR)
			return
		}
		
		$.each(nodes, function(i, elm){
			$(elm).children("span:icontains('" + value + "')").addClass('tree-item-found')
			$self.expand($(elm))
		})
		
		$('#mdlAssunto').animate({ scrollTop: $('li > span.tree-item-found:eq(0)').offset().top}, "fast")
	},
	expand: function (elm) {
		elm.prop('title', 'Fechar')
			.show('fast')
			.find('> span > i.fa-folder')
			.removeClass('fa-folder')
			.addClass('fa-folder-open')
			
		elm.find('ul:eq(0) > li').show('fast')
	},
	collapseAll: function () {
		$('.select-tree').removeClass('select-tree')
		$('.tree-item-found').removeClass('tree-item-found')
		
		$('li.parent_li, li:has(span > i.fa-file-o)').prop('title', 'Abrir')
			.hide()
			.find('> span > i.fa-folder-open')
			.removeClass('fa-folder-open')
			.addClass('fa-folder')
		
		$('.tree > ul > li.parent_li').show()
		
		$('.btn-step1-concluir')
			.prop('disabled', true)
			.addClass('disabled');
	}
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

function aplicarMascaraCampo(campo, evento, mascara, tipo){
	if (tipo == 'date' || tipo == 'number') {
		var mascaraFormatada = '';
		if (mascara) {
			mascaraFormatada = mascara.replace(/[x]/gi,'#');
		}else{
			mascaraFormatada = '#'.repeat(campo.value.length);
		}
		
		maskIt(campo, evento, mascaraFormatada, false, false);
	}
}

function verificarMascaraValida(campo, mascara, tipo){
	if (tipo != 'text' && mascara) {
		if(mascara.length != campo.value.length){
			campo.value = '';
			return false;
		}		
	}
	return true;
}
