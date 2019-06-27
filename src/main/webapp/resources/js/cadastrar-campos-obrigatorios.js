var languageDatatables = {
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
};

$(document).ready(function() {
	carregarComponentes();
});

function carregarComponentes(){
	carregarDataTableCamposObrigatorios();
}

function carregarDataTableCamposObrigatorios(){
	
	if($.fn.DataTable.isDataTable("#dtbCampo")){
		$("#dtbCampo").DataTable().destroy;
	}

	if($("#dtbCampo tbody td").length == 1){
		$("#dtbCampo tbody").remove();
	}
	
	
	$('#dtbCampo').dataTable({
		
		"columnDefs": [
				{"targets": [0], "width":"10%" },
				{"targets": [0], "responsivePriority": 2 },
				{"targets": [4], "orderable": false},
				{"targets": [4], "responsivePriority": 1},
				{"targets": [4], "className":"text-center text-nowrap" },
				{"targets": [4], "width":"5%" }
			],
			
			"lengthChange":false,
			"autoWidth": false,
			searching: true,  
			responsive: false,
			paging: true,
			order : [ 1, 'asc' ],
			language: languageDatatables
	});
		
}


function abrirModalNovosCamposObrigatorios(){
	//carregarDataTableCamposObrigatorios();

	$("#modal_cadastro_campo_obrig").modal({
		backdrop: "static",
		keyboard: false,
	});
	
	
}
function abrirModalEditarCamposObrigatorios(){
	carregarDataTableCamposObrigatorios();
	
	$("#modal_cadastro_campo_obrig").modal({
		backdrop: "static",
		keyboard: false,
	});
	
}

function confirmExcluir(rmcFunction){
	
	$("#modal_excluir #confirma_exclusao").unbind('click');
	$("#modal_excluir #confirma_exclusao").bind('click', rmcFunction);
		
	$("#modal_excluir").modal({
		backdrop: "static",
		keyboard: false,
	});
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

$(".editar_campo").click(function(){
	$("#modal_cadastro_campo_obrig").modal({
		backdrop: "static",
		keyboard: false,
	});
	$(".alert").hide();
	$(".modal-title").html('Editar - Cadastro Campo Obrigatório');
	$("#nome").val('nome');
	$("#descricao").val('descricao');
	$("#tipo").val('tipo');
	$("mascara").val('mascara');
	$("#tamanho").val('50');
});


$("#btn_campo_obrigatorio").click(function(){
	$("#modal_cadastro_campo_obrig").modal({
		backdrop: "static",
		keyboard: false,
	});
	$(".alert").hide();

	$(".modal-title").html('Cadastro Campo Obrigatório');

	$("#nome").val('');
	$("#descricao").val('');
	$("#tipo").val('');
	$("#mascara").val('');
	$("#tamanho").val('');
	
});


$("#salvar_cadastro").click(function(){
	$(".alert").show();
	$(".alert").addClass("alert-success");
	$(".alert").html("<strong>Cadastro realizado com sucesso.</strong>");	
});


$("#mascara").change(function(){
	if($(this).prop("checked")){
		$("#mascara").attr("disabled",false).val("");
		$("#tamanho").attr("disabled",true).val("");		
	}else{
		$("#mascara").attr("disabled",true).val("");
		$("#tamanho").attr("disabled",false).val("");
	};
});