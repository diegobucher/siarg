$(document).ready(function() {
    app.initialize();
});

function formatarData() {
	$(".datepicker").datepicker();
}


function montarDataTable(){
	
	var tamanho = $('#irTbOcorrencia').val();
	
  if (tamanho == 0) {
  	$("#tbOcorrencia tbody").remove();
  }
  
	$('#tbOcorrencia').dataTable({
		"columnDefs" : [
			{width: "150px", targets: [3, 4] },
			{"targets" : [ 5 ],"orderable" : false}, 
			{type: "de_date", targets: [ 3 , 4 ]},
			{"responsivePriority" : 0, "targets" : 0}, 
			{"className" : "text-center cell-middle", "targets" : [ 2 ]},
			{"className" : "cellSelect","targets" : [ 2 ]}, 
		],
		"lengthChange" : false,
		searching : true,
		responsive : true,
		paging : true,
		order:[3,"desc"],
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

jQuery.extend( jQuery.fn.dataTableExt.oSort, {
  "de_date-asc": function ( a, b ) {
         var x, y;
         if (jQuery.trim(a) !== '') {
                 var deDatea = jQuery.trim(a).split('/');
                 x = (deDatea[2] + deDatea[1] + deDatea[0]) * 1;
         } else {
                 x = Infinity; // = l'an 1000 ...
         }

         if (jQuery.trim(b) !== '') {
                 var deDateb = jQuery.trim(b).split('/');
                 y = (deDateb[2] + deDateb[1] + deDateb[0]) * 1;
         } else {
                 y = -Infinity;
         }
         var z = ((x < y) ? -1 : ((x > y) ? 1 : 0));
         return z;
  },

  "de_date-desc": function ( a, b ) {
         var x, y;
         if (jQuery.trim(a) !== '') {
                 var deDatea = jQuery.trim(a).split('/');
                 x = (deDatea[2] + deDatea[1] + deDatea[0]) * 1;
         } else {
                 x = -Infinity;
         }

         if (jQuery.trim(b) !== '') {
                 var deDateb = jQuery.trim(b).split('/');
                 y = (deDateb[2] + deDateb[1] + deDateb[0]) * 1;
         } else {
                 y = Infinity;
         }
         var z = ((x < y) ? 1 : ((x > y) ? -1 : 0));
         return z;
  }
} );

function validaDat(campo,valor) {
	var date=valor;
	var ardt=new Array;
	var ExpReg=new RegExp("(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/[0-9][0-9]{3}");
	ardt=date.split("/");
	erro=false;
	if ( date.search(ExpReg)==-1){
		erro = true;
		}
	else if (((ardt[1]==4)||(ardt[1]==6)||(ardt[1]==9)||(ardt[1]==11))&&(ardt[0]>30))
		erro = true;
	else if ( ardt[1]==2) {
		if ((ardt[0]>28)&&((ardt[2]%4)!=0))
			erro = true;
		if ((ardt[0]>29)&&((ardt[2]%4)==0))
			erro = true;
	}
	if (erro) {
		return false;
	}
	return true;
}


var app = {
    initialize: function() {
    	this.bindEvents()
    },
    
    bindEvents: function() {
    	var $self = this
    	
    	$(document).on('change', '.dataExpiracao', function (e){
    		$('#messageModal').remove();
    		app.$alert.close();
    		e.preventDefault();
    		
    		if ($('#idDtExpiracao').val().trim() != ''){
    			if (!validaDat($('.dataExpiracao'),$('.dataExpiracao').val())){
    				app.$alert.show(Messages.get('MA033'), app.$alert.type.ERROR);
    				$('#idDtExpiracao').val('');
    				return;
    			}    		    			
    		}
    	})
    	
    	$(document).on('change', '.dataPublicacao', function (e){
    		$('#messageModal').remove();
    		app.$alert.close();
    		e.preventDefault();
    		
    		if ($('#idDtPublicacao').val().trim() != ''){
    			if (!validaDat($('.dataPublicacao'),$('.dataPublicacao').val())){
    				app.$alert.show(Messages.get('MA033'), app.$alert.type.ERROR);
    				$('#idDtPublicacao').val('');
    				return;
    			}    		    			
    		}
    	})
    	
    },

	  $alert : {
	  	$elm : '#modal-message',
	  	$container : '#modalNovo',
  		type : {
  			INFO : 'alert-info',
  			ERROR : 'alert-danger',
  			WARNING : 'alert-warning',
  			SUCCESS : 'alert-success'
  		},
  		show : function(message, type, scroll) {
  			scroll = scroll || false
  			$(this.$elm).html('<div class="row"><div class="col-md-12"><div class="alert"><p></p></div></div></div>').find('div.alert')
  					.addClass(type).find('p').text(message)
  			if (scroll) {
  				$(this.$container).animate({
  					scrollTop : $(this.$elm).offset().top
  				}, "fast")
  			}
  		},
  		close : function() {
  			$(this.$elm).html('');
  		}
	  },
}