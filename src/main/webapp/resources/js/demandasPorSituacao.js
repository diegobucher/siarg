
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


