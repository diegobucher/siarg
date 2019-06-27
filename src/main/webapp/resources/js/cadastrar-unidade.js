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
	carregarDataTableUnidades();
}

function carregarDataTableUnidades(){
	
	if($.fn.DataTable.isDataTable("#dtbUnidade")){
		$("#dtbUnidade").DataTable().destroy;
	}

	if($("#dtbUnidade tbody td").length == 1){
		$("#dtbUnidade tbody").remove();
	}
	
	
//	if($('#dtbUnidade').length > 0 ){
//	}
	$('#dtbUnidade').dataTable({
		
		"columnDefs": [
				{"targets": [0], "width":"10%" },
				{"targets": [0], "responsivePriority": 2 },
				{"targets": [4], "orderable": false},
				{"targets": [4], "responsivePriority": 1},
				{"targets": [4], "className":"text-center text-nowrap" },
				{"targets": [4], "width":"5%" }
			],
			
			"lengthChange":false,
			"autoWidth": false,
			searching: true,  
			responsive: true,
			paging: true,
			order : [ 1, 'asc' ],
			language: languageDatatables
	});
		
}

function carregarDataTableCaixasPostais(){

	if($.fn.DataTable.isDataTable("#grid_cx_postal")){
		$("#grid_cx_postal").DataTable().destroy;
	}

	if($("#grid_cx_postal tbody td").length == 1){
		$("#grid_cx_postal tbody").remove();
	}
	
	$("#grid_cx_postal").dataTable({
		"columnDefs": [
			{"targets": [3], "orderable": false},
		    {"className":"text-center", "targets":[3]},
			{"width":55, "targets":[3]}
		],

		scrollCollapse:false,
		info:true,
		searching: false,
		responsive:false,
		paging: true,
		"autoWidth": false,
		"pagingType":"simple",
		"lengthChange":false,
		"lengthMenu":[ 5 ],		
		language:  languageDatatables 						  

	});
}

function abrirModalNovaUnidade(){
	carregarDataTableCaixasPostais();

	$("#modal_cadastro_unidade").modal({
		backdrop: "static",
		keyboard: false,
	});
}
function abrirModalEditarUnidade(){
	carregarDataTableCaixasPostais();
	
	$("#modal_cadastro_unidade").modal({
		backdrop: "static",
		keyboard: false,
	});
}

function confirmExcluir(rmcFunction){
	
	$("#modal_excluir #confirma_exclusao").unbind('click');
	$("#modal_excluir #confirma_exclusao").bind('click', rmcFunction);
		
	$("#modal_excluir").modal({
		backdrop: "static",
		keyboard: false,
	});
}

function abrirModalFechar(xhr, status, args){
	
	if(args.validationFailed || args.naoValido){
		if(args.limparAnexo == "true"){
			limparAnexo();
		}
		scrollUp();
		return;
	}
}

