$(document).ready(function() {
	data();
});

function data() {
	$(".datepicker").datepicker();
}

function montarDataTable() {
	
	var tamanho = $('#irDemAguardandoUnidade').val();
	
    if (tamanho == 0) {
    	$("#tableRelatorio tbody").remove();
    }
    
    $('#tableRelatorio').dataTable({
			"order":[0,'asc'],								  
			  "columnDefs": [
				{"className":"vertical-align", "targets":[  0,1,2,4,5,6,7]},
				{"className":"text-center , vertical-align", "targets":[ 3,9]},
				{"className":"text-right , vertical-align", "targets":[ 8,10,11]},
				{"className":"vertical-align", "targets":[0]},
				//{"width": 400, "targets":[1]},
			  ],
			  //"scrollX":true,
			  //"lengthMenu":[ 10,25, 50, 100 ],
			  //"autoWidth":false,
			  //"lengthChange":true,
			  searching: true,  
			  responsive:false,
			  //destroy: true,
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