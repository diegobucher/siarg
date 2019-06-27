// JavaScript Document

window.myLineDiary;
window.myLineMonthy;
var config = "";
var controle = "";

window.chartColors = {
	red: 'rgb(255, 99, 132)',
	orange: 'rgb(255, 159, 64)',
	yellow: 'rgb(255, 205, 86)',
	green: 'rgb(75, 192, 192)',
	blue: 'rgb(54, 162, 235)',
	purple: 'rgb(153, 102, 255)',
	grey: 'rgb(231,233,237)'
};


function graficoMes(idTronco) {
	
	controle = document.getElementById("controle");
	var dadosMeses = [];  
	var labelMeses = [];
	url = '/simol/RelatorioIndisponibilidadeMes'
	$.ajax({
		type: "POST",
		async: false,
		data: { 'idTronco' : idTronco},
		url: url,
		success: function(data){
		    for (i = 0; i < data.length; i++){
		    	dadosMeses.push(data[i].qtd);
		    	labelMeses.push(data[i].mes);
		    }
		    config = {
	    		type: 'line',
	    		data: {
	    			labels: labelMeses,
	    			datasets: [{
	    				label: "Tempo de indisponibilidade",
	    				backgroundColor: window.chartColors.red,
	    				borderColor: window.chartColors.red,
	    				data: dadosMeses,
	    				fill: false,
	    				pointRadius: 4,
	    				lineTension: 0,
	    			}]
	    		},
	    		options: {
	    			responsive: true,
	    			tooltips: {
	    				mode: 'index',
	    				intersect: false,
	    			},
	    			hover: {
	    				mode: 'nearest',
	    				intersect: true
	    			},
	    			scales: {
	    				xAxes: [{
	    					display: true,
	    					scaleLabel: {
	    						display: true,
	    						labelString: 'Mês'
	    					}
	    				}],
	    				yAxes: [{
	    					display: true,
	    					ticks: {
	    						beginAtZero: true
	    					},
	    					scaleLabel: {
	    						display: true,
	    						labelString: 'Tempo (Minutos)'
	    					}
	    				}]
	    			}
	    		}
		    };
		    
		    var ctx = document.getElementById("canvas_mes").getContext("2d");
		    window.myLineMonthy = new Chart(ctx, config);
		}
	});
}


function graficoDia(idTronco) {
	
	controle = document.getElementById("controle");
	
	var dadosDias = [];  
	var labelDias = [];
	url = '/simol/RelatorioIndisponibilidadeDia'
		$.ajax({
			type: "POST",
			data: { 'idTronco' : idTronco},
			async: false,
			url: url,
			success: function(data){
				for (i = 0; i < data.length; i++){
			    	dadosDias.push(data[i].qtd);
			    	labelDias.push(data[i].dia);
			    }
			    config = {
			    	type: 'line',
			    	data: {
			    		labels: labelDias,
			    		datasets: [{
			    			label: "Tempo de indisponibilidade",
			    			backgroundColor: window.chartColors.red,
			    			borderColor: window.chartColors.red,
			    			data: dadosDias,
			    			fill: false,
			    			pointRadius: 4,
			    			lineTension: 0,
			    		}]
			    	},
			    	options: {
			    		responsive: true,
			    		tooltips: {
			    			mode: 'index',
			    			intersect: false,
			    		},
			    		hover: {
			    			mode: 'nearest',
			    			intersect: true
			    		},
			    		scales: {
			    			xAxes: [{
			    				display: true,
			    				scaleLabel: {
			    					display: true,
			    					labelString: 'Dias'
			    				}
			    			}],
			    			yAxes: [{
			    				display: true,
			    				ticks: {
		    						beginAtZero: true
		    					},
			    				scaleLabel: {
			    					display: true,
			    					labelString: 'Tempo (Minutos)'
			    				}
			    			}]
			    		}
			    	}
			    };
				    
				var ctx = document.getElementById("canvas_dia").getContext("2d");
					window.myLineMonthy = new Chart(ctx, config);
		}
	});
}

function graficoGerenDisp() {
	var	idTipoGrafico = $('#idTipoGrafico option:selected').val()
	
	if (idTipoGrafico == 0){
		gerarGraficoDispDiario();		
	} else if (idTipoGrafico == 1){
		gerarGraficoDispMensal();
	}
}

function gerarGraficoDispMensal(){
	
	var	idUnidadeTecnicaResponsavelSelecionada = $('#idUnidadeTecnicaResponsavel option:selected').val()
	var	ufUnidadeSelecionada = $('#idUfUnidade option:selected').val()
	var	idUnidade = $('#idUnidade option:selected').val()
	var	idOperadora = $('#idOperadora option:selected').val()
	var	idTronco = $('#idTronco option:selected').val()
	var	idTipoGrafico = $('#idTipoGrafico option:selected').val()
	var minY = 0
	var dadosDias = [];  
	var labelDias = [];
	url = '/simol/GerenciamentoDisponibilidadeMensal'
		$.ajax({
			type: "POST",
			data: { 
				'idUnidadeTecnicaResponsavelSelecionada' : idUnidadeTecnicaResponsavelSelecionada,
				'ufUnidadeSelecionada' : ufUnidadeSelecionada,
				'idUnidadeSelecionada' : idUnidade,
				'idOperadoraSelecionada' : idOperadora,
				'idTroncoSelecionado' : idTronco,
				'tipoPeriodoSelecionado' : idTipoGrafico
			},
			async: false,
			url: url,
			success: function(data){
				for (i = 0; i < data.length; i++){
			    	dadosDias.push(data[i].qtd);
			    	labelDias.push(data[i].dia);
			    }
				minY = Math.min.apply(null, dadosDias);
				if (minY > 95){
					minY = 95;
				}
			    config = {
			    	type: 'line',
			    	data: {
			    		labels: labelDias,
			    		datasets: [{
			    			label: "Disponibilidade",
			    			backgroundColor: window.chartColors.blue,
			    			borderColor: window.chartColors.blue,
			    			data: dadosDias,
			    			fill: false,
			    			pointRadius: 4,
			    			lineTension: 0,
			    		}]
			    	},
			    	options: {
			    		responsive: true,
						legend: {
							display: false,
						},
			    		tooltips: {
			    			mode: 'index',
			    			intersect: false,
			    		},
			    		hover: {
			    			mode: 'nearest',
			    			intersect: true
			    		},
			    		scales: {
			    			xAxes: [{
			    				display: true,
			    				scaleLabel: {
			    					display: true,
			    					labelString: 'Últimos 30 Dias'
			    				}
			    			}],
			    			yAxes: [{
			    				display: true,
			    				ticks: {
		    						min: minY,
		    						max: 100
		    					},
			    				scaleLabel: {
			    					display: true,
			    					labelString: 'Valores (%)'
			    				}
			    			}]
			    		}
			    	}
			    };
				    
				var ctx = document.getElementById("canvasGraphic").getContext("2d");
					window.myLineMonthy = new Chart(ctx, config);
		}
	});
}

function gerarGraficoDispDiario(){
	
	var	idUnidadeTecnicaResponsavelSelecionada = $('#idUnidadeTecnicaResponsavel option:selected').val()
	var	ufUnidadeSelecionada = $('#idUfUnidade option:selected').val()
	var	idUnidade = $('#idUnidade option:selected').val()
	var	idOperadora = $('#idOperadora option:selected').val()
	var	idTronco = $('#idTronco option:selected').val()
	var	idTipoGrafico = $('#idTipoGrafico option:selected').val()
	var minY = 0
	var dadosDias = [];  
	var labelDias = [];
	url = '/simol/GerenciamentoDisponibilidadeDiario'
		$.ajax({
			type: "POST",
			data: { 
				'idUnidadeTecnicaResponsavelSelecionada' : idUnidadeTecnicaResponsavelSelecionada,
				'ufUnidadeSelecionada' : ufUnidadeSelecionada,
				'idUnidadeSelecionada' : idUnidade,
				'idOperadoraSelecionada' : idOperadora,
				'idTroncoSelecionado' : idTronco,
				'tipoPeriodoSelecionado' : idTipoGrafico
			},
			async: false,
			url: url,
			success: function(data){
				for (i = 0; i < data.length; i++){
					dadosDias.push(data[i].qtd);
					labelDias.push(data[i].hora);
				}
				minY = Math.min.apply(null, dadosDias);
				if (minY > 95){
					minY = 95;
				}
				config = {
						type: 'line',
						data: {
							labels: [	"00:00","01:00","02:00","03:00","04:00","05:00","06:00","07:00",
										"08:00","09:00","10:00","11:00","12:00","13:00","14:00","15:00",
										"16:00","17:00","18:00","19:00","20:00","21:00","22:00","23:00"],
							datasets: [{
								label: "Disponibilidade",
								backgroundColor: window.chartColors.blue,
								borderColor: window.chartColors.blue,
								data: dadosDias,
								fill: false,
								pointRadius: 4,
								lineTension: 0,
							}]
						},
						options: {
							responsive: true,
							legend: {
								display: false,
							},
							tooltips: {
								mode: 'index',
								intersect: false,
							},
							hover: {
								mode: 'nearest',
								intersect: true
							},
							scales: {
								xAxes: [{
									display: true,
									ticks: {
										beginAtZero: true,
										max: 24
									},
									scaleLabel: {
										display: true,
										labelString: 'Últimas 24 horas'
									}
								}],
								yAxes: [{
									display: true,
									ticks: {
										min: minY,
										max: 100
									},
									scaleLabel: {
										display: true,
										labelString: 'Valores (%)'
									}
								}]
							}
						}
				};
				
				var ctx = document.getElementById("canvasGraphic").getContext("2d");
				window.myLineMonthy = new Chart(ctx, config);
			}
		});
}
