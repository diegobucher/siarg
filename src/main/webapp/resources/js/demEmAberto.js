function reloadPage() {
    stateClearDataTables();
    location.reload(true);
    window.location.reload();
}

function data() {
	$(".datepicker").datepicker();
}

function montarDataTable() {
	
	var tamanho = $('#irDemEmAberto').val();
	
    if (tamanho == 0) {
    	$("#dtDemEmAberto tbody").remove();
    }
    
	$('#dtDemEmAberto').dataTable({
		"columnDefs" : [
 		    {"className" : "cellSelect","targets" : [ 0 ]}, 
 		    {"className" : "text-center","targets" : [ 3 ]},
		],
		"lengthMenu" : [ 10, 25, 50, 100 ],
		"lengthChange" : true,
		searching : true,
		responsive : true,
		destroy : true,
		paging : true,
		order : [ 3, 'asc' ],
		language : {
			"sEmptyTable" : "Nenhum registro encontrado.",
			"sInfo" : "Mostrando de _START_ até _END_ de _TOTAL_ registros",
			"sInfoEmpty" : "Mostrando 0 até 0 de 0 registros",
			"sInfoFiltered" : "(Filtrados de _MAX_ registros)",
			"sInfoPostFix" : "",
			"sInfoThousands" : ".",
			"sLengthMenu" : "_MENU_ Resultados por página",
			"sLoadingRecords" : "Carregando...",
			"sProcessing" : "Processando...",
			"sZeroRecords" : "Nenhum registro encontrado.",
			"sSearch" : "Pesquisar",
			"oPaginate" : {
				"sNext" : "Próximo",
				"sPrevious" : "Anterior",
				"sFirst" : "Primeiro",
				"sLast" : "Último"
			},
			"oAria" : {
				"sSortAscending" : ": Ordenar colunas de forma ascendente",
				"sSortDescending" : ": Ordenar colunas de forma descendente"
			} 
		}
	});
}