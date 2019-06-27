function reloadPage() {
    stateClearDataTables();
    location.reload(true);
    window.location.reload();
}

function showModalExportarDemandas(){
	$("#modalExportarDemanda").modal({
		backdrop: "static",
		keyboard: false,
	});
}

function montarDataTable() {
	var tamanho = $('#irDtRelacaoAssunto').val();
  if (tamanho == 0) {
  	$("#dtRelacaoAssunto tbody").remove();
    $(".btnRelatorios").addClass('disabled');
  } else {
  	$(".btnRelatorios").removeClass('disabled');
  }
    
  $('#dtRelacaoAssunto').dataTable({
  	"columnDefs" : [ 
	                {targets : [ 0, 6 ], orderable : false},
	                {responsivePriority : 0, targets : [ 5 ]}, 
	                {className : " cellSelect colCinco ", targets : [ 0 ]},
	                {className : " colTrinta ", targets : [ 1 ]},
	                {className : " col50Porcento ", targets : [ 2 ]},
	                {className : " colDuzentosQuarenta ", targets : [ 4 ]},
	                {className : " colDuzentos ", targets : [ 5 ]},
	                {className : " text-nowrap ", targets : [ 6 ]},
	    	],
	  "lengthMenu" : [ 10, 25, 50, 100 ],
	  "lengthChange" : true,
	  searching : true,
	  responsive : true,
	  destroy : true,
	  paging : true,
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

$(document).ready(function() {
    montarDataTable();
});