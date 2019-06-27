
$(document).ready(function() {
//    montarDataTable();
//	montarDataTableDetalhe();
});

function aplicarMascaras(){
	$(".date").datepicker();
}

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
	
//	dataTable.on( 'draw.dt',  function () { bindActivo(); } );
	
}

function montarDataTableDetalhe() {
	
	$('#dtbDetalhe').dataTable({
		"columnDefs": [
			{"targets": [0,1, 14], "orderable": false},
			
			{"className":"cellSelect", "targets":[ 0, 14 ]},
			{"className":"text-nowrap", "targets":[ 2,3, 11 ]},
			{"className":"text-center", "targets":[1 ]},
			{"responsivePriority": 1, "targets": 14 },
			
			{"width": "90px", "targets": [1] },

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


