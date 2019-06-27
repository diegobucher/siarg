function aplicarMascaras(){
	$(".date").datepicker();
}
/*
function montarDataTable() {
	var dataTable = $('#tableRelatorio').DataTable({
		"order":[0,'asc'],								  
		  "lengthMenu":[ 10,25, 50, 100 ],
		  "lengthChange":true,
		  searching: true,  
		  responsive:true,
		  destroy: true,
		  paging: true,
		  language: languagePtBrDatatables		  
	});			
	
}
*/

function montarDataTable() {
	
	$('#tableRelatorio').dataTable({
		"columnDefs" : [ 
			//{"targets" : [ 3 ],"orderable" : false}, 
			{type: "formatted-num", targets: [0] },
			{type: "numeric-comma", targets: [3] },
			//{type: "de_date", targets: [1] },
//			{"responsivePriority": 0, "targets": 8 },
			//{"className" : "text-nowrap", "targets" : [ 0 ]},
			//{"className" : "text-center , "targets" : [ 3 ]},
			{"className" : "text-right","targets" : [ 3 ]}, 
			//{"className" : "text-right","targets" : [ 3 ]},
		],
		order : [1 , "desc"],
		"lengthMenu" : [ 10, 25, 50, 100 ],
		"lengthChange" : true,
		searching : true,
		responsive : true,
		destroy : true,
		stateSave: true,
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

function cleanActive(){
	$(".linkQtd").parent().removeClass("active");	
};

function bindActivo(element){
	cleanActive();
	$("html, body").animate({scrollTop:$('#tableRelatorio_wrapper').height()});
	
	$(element).parent().addClass("active");
}

jQuery.extend( jQuery.fn.dataTableExt.oSort, {
	  "numeric-comma-pre": function ( a ) {
	      a = (a === "-") ? 0 : a.replace( ".", "");
	      a = a.replace( ",", "." );
	      		
	      return parseFloat( a );
	  },

	  "numeric-comma-asc": function ( a, b ) {
	      return ((a < b) ? -1 : ((a > b) ? 1 : 0));
	  },

	  "numeric-comma-desc": function ( a, b ) {
	      return ((a < b) ? 1 : ((a > b) ? -1 : 0));
	  }
	});