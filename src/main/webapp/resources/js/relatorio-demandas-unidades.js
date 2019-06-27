$(document).ready(function(){
	$('[data-toggle="tooltip"]').tooltip();
	$(".data").mask('00/00/0000',{placeholder:"__/__/____"});
});

function data() {
	$(".datepicker").datepicker();
}
		

function montarDataTable() {
	
	var tamanho = $('#irDtg_demanda_unidade').val();
	
  if (tamanho == 0) {
    $(".btnRelatorios").addClass('disabled');
  } else {
  	$(".btnRelatorios").removeClass('disabled');
  }
  
}