$(document).ready(function() {
	data();
});

function data() {
	$(".datepicker").datepicker();
}

function montarDataTable() {
	
	var tamanho = $('#irQuantidadeIteracoes').val();
	
    if (tamanho == 0) {
    	$("#tableRelatorio tbody").remove();
    }
    
    $('#tableRelatorio').dataTable({
			"order":[0,'asc'],								  
			  "columnDefs": [
				{"className":"vertical-align", "targets":[ 0,1,2,3,4,5,6,7,8]},
			  ],
			  searching: true,  
			  responsive:false,
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

function montarDataTableDetalhe() {
	
	$('#dtbDetalhe').dataTable({
		"columnDefs": [

		  ],
		  "order":[2,'desc'],
		  "lengthMenu":[ 5 ],
		  "lengthChange":false,
		  searching: true,  
		  responsive:true,
		  destroy: true,
		  paging: true,
		  language: languagePtBrDatatables			  
		});	

}

function bindActivo(element){
	cleanActive();
	$("html, body").animate({scrollTop:$('#tableRelatorio_wrapper').height()});
	
	$(element).parent().addClass("active");
}

function cleanActive(){
	$(".linkQtd").parent().removeClass("active");	
}