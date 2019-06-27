/**
 * This sorting plug-in for DataTables will correctly sort data in date time or date
 * format typically used in Germany:
 * date and time:`dd.mm.YYYY HH:mm`
 * just date:`dd.mm.YYYY`.
 *
 * Please note that this plug-in is **deprecated*. The
 * [datetime](//datatables.net/blog/2014-12-18) plug-in provides enhanced
 * functionality and flexibility.
 *
 *  @name Date (dd.mm.YYYY) or date and time (dd.mm.YYYY HH:mm)
 *  @summary Sort date / time in the format `dd.mm.YYYY HH:mm` or `dd.mm.YYYY`.
 *  @author [Ronny Vedrilla](http://www.ambient-innovation.com)
 *  @deprecated
 *
 *  @example
 *    $('#example').dataTable( {
 *       columnDefs: [
 *         { type: 'de_date', targets: 1 }
 *       ]
 *    } );
 */
 
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

 
 jQuery.extend( jQuery.fn.dataTableExt.oSort, {
	    "formatted-num-pre": function ( a ) {
	        a = (a === "/" || a === "") ? 0 : a.replace( /[^\d\-\.]/g, "" );
	        return parseFloat( a );
	    },
	 
	    "formatted-num-asc": function ( a, b ) {
	        return a - b;
	    },
	 
	    "formatted-num-desc": function ( a, b ) {
	        return b - a;
	    }
	} );
 
 var dataTableCompleta = {
	columnDefs : [
	  {width: "315px", targets: [7, 10] },
	  {type: "formatted-num", targets: [1] },
	  {type: "de_date", targets: [4] },
//	  {targets : [ 0, 1], orderable : false}, 
	  {targets : [ 0, 13 ], orderable : false}, 
	  {className : "expansao", targets: [0] },

	  {className : "direita tdClicavel", targets: [1] },
	  {className : "text-nowrap tdClicavel", targets: [2] },
	  {className : "tdClicavel", targets: [3] },
	  {className : "text-nowrap tdClicavel", targets: [4] },
	  {className : "tdClicavel", targets: [5] },
	  
	  {className : "text-nowrap tdClicavel", targets: [6] },
	  {className : "tdClicavel", targets: [7] },
	  {className : "tdClicavel", targets: [10] },

	  {className : "text-nowrap tdClicavel", targets: [11] },
	  {className : "direita tdClicavel", targets: [12] },
	  {className : "cellSelect", targets: [13] },
	  
	  {responsivePriority : 2,targets : 13},
	],	
	lengthMenu : [ 20 ],
	lengthChange : false,
	searching : true,
	stateSave: true,
	responsive : true,
	destroy : true,
	paging : true,
	order:[1,"desc"],
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
};

function montarDtPriori() {
	var tamanho = $('#irTbPriori').val();
	if (tamanho == 0) {
		$("#tbDemandaAtuacao tbody").remove();
	}
	$('#tbDemandaAtuacao').dataTable(dataTableCompleta);
	reloadColorSelector();
	$('#tbDemandaAtuacao').on( 'draw.dt',  function () { reloadColorSelector(); bindingTDsDetalharDemanda(); } ).dataTable();
	
	bindingTDsDetalharDemanda();
}


function montarDtDemais() {
	var tamanho = $('#irTbDemais').val();
	if (tamanho == 0) {
		$("#tbDemaisDemandas tbody").remove();
	}
	$('#tbDemaisDemandas').dataTable(dataTableCompleta);
	reloadColorSelector();
	$('#tbDemaisDemandas').on( 'draw.dt',  function () { reloadColorSelector(); bindingTDsDetalharDemanda(); } ).dataTable();
	
	bindingTDsDetalharDemanda();

}

function bindingTDsDetalharDemanda(){
	$('td.tdClicavel').each(function(){
		$(this).off('click');
		$(this).on('click', function (e){
			tr = $(this).closest('tr');
			tr.find('a.botaoDetalhar').click();
	    });
	});
}

function reloadColorSelector(){
	$('.colorselector').colorselector({
	    callback: function (value, color, title, objeto) {
	        var param = [];
	        param.push(objeto.attr('id'));
	        param.push(value);
	        
	        console.log(objeto.attr('id'));
	        console.log(value);
	        
	        if (objeto.attr('id') == 'headerColorPriori'){
	        	rmcAlterarFiltroCorDemanda([{name:'ids_cores', value: JSON.stringify(param)}]);
	        } else if (objeto.attr('id') == 'headerColorDemais'){
	        	rmcAlterarFiltroCorDemandaDemais([{name:'ids_cores', value: JSON.stringify(param)}]);
	        } else if (objeto.attr('id') != null){
	        	rmcAlterarCorDemanda([{name:'ids_cores', value: JSON.stringify(param)}]);
	        }
	    }
	});
}

function alerta (){
	refresh();
	setTimeout(alerta,1000*(parseInt($('#setTimeRefresh').val())));
}

function reloadPage(){	
	stateClearDataTables();
	location.reload(true);
	window.location.reload();
}

function stateClearDataTables(){
	var tamanho = $('#irTbDemais').val();
	if (tamanho != 0) {
		$('#tbDemaisDemandas').DataTable().state.clear()
	}
	var tamanho = $('#irTbPriori').val();
	if (tamanho != 0) {
		$('#tbDemandaAtuacao').DataTable().state.clear()
	}
}

$(document).ready(function() {
	stateClearDataTables();
	alerta();
	$('[data-toggle="popover"]').popover({ placement : "left", animation : true });
	$('[data-toggle="tooltip"]').tooltip();
});