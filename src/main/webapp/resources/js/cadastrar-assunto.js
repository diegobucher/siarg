var languageDatatables = {
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
};

$(document).ready(function() {
	carregarComponentes();
});

function carregarComponentes(){
	bindingBotoes();
	carregarDataTable();

	exibirCategorias();
}


function scrollUp(){
	$(window).scrollTop(0)
}

function bindingBotoes(){

	$("#btn_inclui_topico").click(function(){
		$("#alert_success").hide();
		rmcHandlerAbrirNovaCategoriaModal();
	});
	
}

function exibirSubcategorias(){
	$('#divSubCategorias').show();
	$('#divCategorias').hide();
	
}

function exibirCategorias(){
	$('#divCategorias').show();
	$('#divSubCategorias').hide();
}

function carregarDataTable(){
	$('#dtbCategorias').dataTable({
		
		  "columnDefs": [
			{"targets":[ 1, 3 ], "orderable":false},
			{"responsivePriority": 0, "targets": 3 },
			{"className":"text-center cell-middle","targets":[ 1, 2, 3 ]},
			{"className":"cellSelect", "targets":[ 3 ]},
		  ],
		  "lengthChange":false,
		  searching: false,  
		  responsive: true,
		  paging: true,
		  
		  language: languageDatatables
	
	});
}

function abrirModalIncluirCategoria(){
	$(".alert").hide();
	$("#modal_novo_topico").modal({
		backdrop: "static",
		keyboard: false,
	});
}


function validarIncluirNovaCategoria(xhr, status, args){
	if(args.validationFailed || args.naoValido){
	} else {
		atualizarForm();
		$("#modal_novo_topico").modal('hide');
	}
}

function abrirModalCancelarCategoria(){
	$(".alert").hide();
	$("#modal_exclui_categoria").modal({
		backdrop: "static",
		keyboard: false,
	});
}
function abrirModalExcluirAssunto(){
	$(".alert").hide();
	$("#modal_exclui_assunto").modal({
		backdrop: "static",
		keyboard: false,
	});
}

function excluirCategoria(){
	atualizarForm();
	$("#modal_exclui_categoria").modal('hide');
}

function excluirAssunto(){
	atualizarForm();
	$("#modal_exclui_assunto").modal('hide');
}