
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
	
}

