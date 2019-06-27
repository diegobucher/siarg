$(document).ready(function() {
	carregarComponentes();
});

function carregarComponentes(){
	carregarMascaras();
	carrregarMultiSelects();
	Summernote.init();
}


function carregarMascaras(){
	$('.numeric').mask('0000');
}

function validarFluxoAssunto(xhr, status, args){

	if(args.validationFailed || args.naoValido){
		scrollUp();
		return;
	}
		
	carregarMascaras();
}

function scrollUp(){
	$(window).scrollTop(0)
}

function salvar(){
	rmcAtualizaEditorSalvar([{name: 'content', value: $('#editor').summernote('code')}]);
}


function carrregarMultiSelects(){
	$(".caixas_postais").multiselect({
		enableFiltering: true,
		inheritClass: true,
		enableCaseInsensitiveFiltering: true,
		maxHeight: 200,
		delimiterText:' / ',
		numberDisplayed:'10',
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
