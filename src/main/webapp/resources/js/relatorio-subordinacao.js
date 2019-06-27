$(document).ready(function(){
	$('[data-toggle="tooltip"]').tooltip();
	$(".data").mask('00/00/0000',{placeholder:"__/__/____"});
});

function data() {
	$(".datepicker").datepicker();
}
		
function montarDataTable() {

var tamanho = $('#irDtg_demandate_demandada').val();
	
  if (tamanho == 0) {
  	$("#dtg_demandate_demandada tbody").remove();
    $(".btnRelatorios").addClass('disabled');
  } else {
  	$(".btnRelatorios").removeClass('disabled');
  }
  
  
	$('#dtg_demandate_demandada').dataTable({
		"columnDefs" : [ 
			{"responsivePriority" : 0,	"targets" : 1	}, 
			{"className" : "text-center ","targets" : [ 2, 3 ]}, 
		],
		"lengthMenu" : [ [ 10, 25, 50, -1 ], [ 10, 25, 50, "Todas" ] ],
		"lengthChange" : true,
		searching : true,
		responsive : false,
		destroy : true,
		paging : true,
		language : {
			"sEmptyTable" : "Nenhum registro encontrado",
			"sInfo" : "Mostrando de _START_ até _END_ de _TOTAL_ registros",
			"sInfoEmpty" : "Mostrando 0 até 0 de 0 registros",
			"sInfoFiltered" : "(Filtrados de _MAX_ registros)",
			"sInfoPostFix" : "",
			"sInfoThousands" : ".",
			"sLengthMenu" : "_MENU_ Resultados por página",
			"sLoadingRecords" : "Carregando...",
			"sProcessing" : "Processando...",
			"sZeroRecords" : "Nenhum registro encontrado",
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