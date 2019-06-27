$(document).ready(function() {
	carregarComponentes();
});

function carregarComponentes(){
	bindingBotoes();
	bindingMascaras();
	carregarDataTable();
}

function abrirModalIncluirUnidade(){
	$(".alert").hide();
	$("#modal_novo").modal({
		backdrop: "static",
		keyboard: false,
	});
	bindingMascaras();
}

function abrirModalEditarUnidade(){
	$(".alert").hide();
	$("#modal_editaData").modal({
		backdrop: "static",
		keyboard: false,
	});
	bindingMascaras();
}

function abrirModalCancelarUnidade(){
	$(".alert").hide();
	$("#modal_excluir").modal({
		backdrop: "static",
		keyboard: false,
	});
}

function validarSalvarNovaUnidade(xhr, status, args){
	if(args.validationFailed || args.naoValido){
		
	} else {
		atualizarUsuarioForm();
		carregarComponentes();
		$("#modal_novo").modal('hide');
	}
}

function validarSalvarEditarUnidade(xhr, status, args){
	if(args.validationFailed || args.naoValido){
		
	} else {
		atualizarUsuarioForm();
		carregarComponentes();
		$("#modal_editaData").modal('hide');
	}
}

function cancelarUnidade(){
	atualizarUsuarioForm();
	carregarComponentes();
	$("#modal_excluir").modal('hide');
}

function salvarUsuario(){
	carregarComponentes();
	scrollUp();
}

function abrirModalFechar(xhr, status, args){
	
	if(args.validationFailed || args.naoValido){
		if(args.limparAnexo == "true"){
			limparAnexo();
		}
		scrollUp();
		return;
	}
}

function scrollUp(){
	$(window).scrollTop(0)
}

function bindingBotoes(){
	
	$(".btn_incluiUnidade").click(function(){
		rmcHandlerAbrirNovaUnidadeModal();
	});
	
	$("#btn_pesquisarUsuario").click(function(){
		$("#btnPesquisarHidden").click();
	});
	
	$("#btn_salva_usuario").click(function(){
//		$("#btnHiddenSalvarUsuario").click();
		rmcSalvarUsuario();
	});

	$("#btn_limpa_usuario").click(function(){
		$("#btnHiddenLimparTela").click();
	});
	
}

function bindingMascaras(){
	 $('#matricula_usuario').mask('C000000', {
		 translation: {
		  'C':{pattern:/[cCpPfF.]/} 
	  	 } 
	   });
     $(".datepicker").datepicker({
    	format: 'dd/MM/yyyy' 
     	}
	 );
     $('.datepicker').mask('00/00/0000', {
		 placeholder: "__/__/____"
     });

}

function carregarDataTable(){
	$('#table_unidadesVinculadas').dataTable({
		"order":[6,'desc'],								  
		"columnDefs": [
			{"targets":[0, 9], "orderable":false},
			{"responsivePriority": 0, "targets": 9 },
			{"className":"cellSelect", "targets":[ 0 ]},
			{"className":"text-center cell-middle","targets":[9]},
			
			],
			"lengthMenu":[ 10,25, 50, 100 ],
			"lengthChange":true,
			searching: true,  
			responsive:true,
			destroy: true,
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