package br.gov.caixa.gitecsa.siarg.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.omnifaces.cdi.ViewScoped;

import br.gov.caixa.gitecsa.arquitetura.controller.BaseController;
import br.gov.caixa.gitecsa.arquitetura.util.DateUtil;
import br.gov.caixa.gitecsa.arquitetura.util.JSFUtil;
import br.gov.caixa.gitecsa.arquitetura.util.MensagemUtil;
import br.gov.caixa.gitecsa.siarg.dto.RelatorioIndicadorReaberturaDTO;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.dto.UnidadeDTO;
import br.gov.caixa.gitecsa.siarg.service.DemandaService;

@Named
@ViewScoped
public class RelatorioIndicadorDemandasReabertasController extends BaseController implements Serializable {

	private static final long serialVersionUID = -5435254291246444163L;

	@Inject
	private DemandaService demandaService;
	
	//Filtros e controle da tela de pesquisa
	private String dtInicial;
	private String dtFinal;
	private List<String> listaUnidadesExibicao;
	private String unidadeSelecionada;
	private Abrangencia abrangenciaSelecionada;
	private Boolean flagPesquisado;
	
	private Date dataInicialPeriodo;
	private Date dataFinalPeriodo;
	private List<UnidadeDTO> listaUnidadesDTOCombo;
	private UnidadeDTO unidadeSelecionadaDTO;
	private List<RelatorioIndicadorReaberturaDTO> listaIndicadorReabertura;
	private Long totalQtdDemandasAbertas;
	private Long totalQtdDemandasReabertas;
	private Double totalPercentualIndicador;

	
	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {
		this.listaUnidadesExibicao = new ArrayList<>(); 
		flagPesquisado = false;
		this.abrangenciaSelecionada = (Abrangencia) JSFUtil.getSessionMapValue("abrangencia");
		List<UnidadeDTO>  listaUnidadesSessaoDTO = (List<UnidadeDTO>) JSFUtil.getSessionMapValue("unidadesDTOList");
		
		this.listaUnidadesDTOCombo = this.obterListaUnidadePorAbrangencia(listaUnidadesSessaoDTO);
		
		if(this.listaUnidadesDTOCombo != null && this.listaUnidadesDTOCombo.size() > 1) {
			listaUnidadesExibicao.add("TODAS");
		}
		
		for (UnidadeDTO unidade : listaUnidadesDTOCombo) {
		  this.listaUnidadesExibicao.add(unidade.getSigla());
		}
		
		//limparTotalizadores();
		inicializarDatas();
	}

	/**
	* Método obter lista de unidades por abrangencia.
	*/
	private List<UnidadeDTO> obterListaUnidadePorAbrangencia(List<UnidadeDTO> listaUnidadesSessao) {
		List<UnidadeDTO> lista = new ArrayList<>();
			for (UnidadeDTO unidade : listaUnidadesSessao) {
				if (unidade.getAbrangencia() == this.abrangenciaSelecionada.getId()) {
					lista.add(unidade);
				}
			}
		return lista;
	}
	
	private void inicializarDatas() {    
		Date hoje = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.FORMATO_DATA);
		this.dtFinal = sdf.format(hoje);
		this.dtInicial = sdf.format(DateUtils.addDays(hoje, -15));
	}

	public void pesquisar() {
		if(validarCampos()){
		    try {		      
				flagPesquisado = true;
				
				dataInicialPeriodo = DateUtils.parseDateStrictly(this.dtInicial, DateUtil.FORMATO_DATA);
				dataFinalPeriodo = DateUtils.parseDateStrictly(this.dtFinal, DateUtil.FORMATO_DATA);		      
				Long idUnidade = obterIdUnidadeSelecionada();
		      
				listaIndicadorReabertura = demandaService.obterIndicadorReaberturaPorUnidadePeriodo(idUnidade, abrangenciaSelecionada.getId(), dataInicialPeriodo, dataFinalPeriodo);
				//carregarTotalizadores();
		    } catch (ParseException e) {
		    	this.facesMessager.addMessageError( MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA033")));
		    }
		}
	}

	/*
	private void carregarTotalizadores() {
		limparTotalizadores();
		for (RelatorioIndicadorReaberturaDTO itemIndicador : listaIndicadorReabertura) {
			
			totalQtdDemandasAbertas = totalQtdDemandasAbertas + itemIndicador.getQtdDemandasAbertas();
			totalQtdDemandasReabertas = totalQtdDemandasReabertas + itemIndicador.getQtdDemandasReabertas();
		}

		totalPercentualIndicador = obterTotalizadorIndicadorPercentualReabertura(totalQtdDemandasAbertas, totalQtdDemandasReabertas);
	}
	
	public Double obterTotalizadorIndicadorPercentualReabertura(Long totalQtdDemandasAbertas, Long totalQtdDemandasReabertas) {
		if (totalQtdDemandasAbertas != null && totalQtdDemandasReabertas != null && totalQtdDemandasAbertas > 0) {
			return (double)((totalQtdDemandasReabertas * 100) / totalQtdDemandasAbertas);
		}
		return (double) 0;
	}
	
	private void limparTotalizadores() {
		this.totalQtdDemandasAbertas = 0L;
		this.totalQtdDemandasReabertas = 0L;
		this.totalPercentualIndicador = (double) 0;
	}
*/
	private Long obterIdUnidadeSelecionada() {
		Long idUnidade = null;
		for (UnidadeDTO unidadeDTO : listaUnidadesDTOCombo) {
			if(this.unidadeSelecionada.equals(unidadeDTO.getSigla())) {
				this.unidadeSelecionadaDTO = unidadeDTO;
				idUnidade = unidadeSelecionadaDTO.getId();
				break;
			}else {
				this.unidadeSelecionadaDTO = null;
				idUnidade = null;
			}
		}
		return idUnidade;
	}

	private boolean validarCampos() {
		    
		if(StringUtils.isBlank(this.dtInicial) || StringUtils.isBlank(this.dtFinal)) {
			this.facesMessager.addMessageError( MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA002")));
			return Boolean.FALSE;
		}	    

	    try {
			Date dtIniPerInicial = DateUtils.parseDateStrictly(this.dtInicial,DateUtil.FORMATO_DATA);
			Date dtFimPerFinal = DateUtils.parseDateStrictly(this.dtFinal,DateUtil.FORMATO_DATA);
		
			if(this.unidadeSelecionada.equals("TODAS") && DateUtil.calculaDiferencaEntreDatasEmDias(dtFimPerFinal, dtIniPerInicial) > 30) {
				this.facesMessager.addMessageError(MensagemUtil.obterMensagem("relatorio.demanda.por.situacao"));
				return Boolean.FALSE;   
			}	      
	      
			if (dtFimPerFinal.before(dtIniPerInicial)) {
				this.facesMessager.addMessageError( MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA034")));
				return Boolean.FALSE;
			} 
	    } catch (ParseException e) {
			this.facesMessager.addMessageError( MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA033")));
			return Boolean.FALSE;
	    }
	    
	    return Boolean.TRUE;
	}

	public String getDtInicial() {
		return dtInicial;
	}

	public void setDtInicial(String dtInicial) {
		this.dtInicial = dtInicial;
	}

	public String getDtFinal() {
		return dtFinal;
	}

	public void setDtFinal(String dtFinal) {
		this.dtFinal = dtFinal;
	}

	public List<String> getListaUnidadesExibicao() {
		return listaUnidadesExibicao;
	}

	public void setListaUnidadesExibicao(List<String> listaUnidadesExibicao) {
		this.listaUnidadesExibicao = listaUnidadesExibicao;
	}

	public String getUnidadeSelecionada() {
		return unidadeSelecionada;
	}

	public void setUnidadeSelecionada(String unidadeSelecionada) {
		this.unidadeSelecionada = unidadeSelecionada;
	}

	public Abrangencia getAbrangenciaSelecionada() {
		return abrangenciaSelecionada;
	}

	public void setAbrangenciaSelecionada(Abrangencia abrangenciaSelecionada) {
		this.abrangenciaSelecionada = abrangenciaSelecionada;
	}

	public Boolean getFlagPesquisado() {
		return flagPesquisado;
	}

	public void setFlagPesquisado(Boolean flagPesquisado) {
		this.flagPesquisado = flagPesquisado;
	}

	public Date getDataInicialPeriodo() {
		return dataInicialPeriodo;
	}

	public void setDataInicialPeriodo(Date dataInicialPeriodo) {
		this.dataInicialPeriodo = dataInicialPeriodo;
	}

	public Date getDataFinalPeriodo() {
		return dataFinalPeriodo;
	}

	public void setDataFinalPeriodo(Date dataFinalPeriodo) {
		this.dataFinalPeriodo = dataFinalPeriodo;
	}

	public List<UnidadeDTO> getListaUnidadesDTOCombo() {
		return listaUnidadesDTOCombo;
	}

	public void setListaUnidadesDTOCombo(List<UnidadeDTO> listaUnidadesDTOCombo) {
		this.listaUnidadesDTOCombo = listaUnidadesDTOCombo;
	}

	public UnidadeDTO getUnidadeSelecionadaDTO() {
		return unidadeSelecionadaDTO;
	}

	public void setUnidadeSelecionadaDTO(UnidadeDTO unidadeSelecionadaDTO) {
		this.unidadeSelecionadaDTO = unidadeSelecionadaDTO;
	}

	public List<RelatorioIndicadorReaberturaDTO> getListaIndicadorReabertura() {
		return listaIndicadorReabertura;
	}

	public void setListaIndicadorReabertura(List<RelatorioIndicadorReaberturaDTO> listaIndicadorReabertura) {
		this.listaIndicadorReabertura = listaIndicadorReabertura;
	}

	public Long getTotalQtdDemandasAbertas() {
		return totalQtdDemandasAbertas;
	}

	public void setTotalQtdDemandasAbertas(Long totalQtdDemandasAbertas) {
		this.totalQtdDemandasAbertas = totalQtdDemandasAbertas;
	}

	public Long getTotalQtdDemandasReabertas() {
		return totalQtdDemandasReabertas;
	}

	public void setTotalQtdDemandasReabertas(Long totalQtdDemandasReabertas) {
		this.totalQtdDemandasReabertas = totalQtdDemandasReabertas;
	}

	public Double getTotalPercentualIndicador() {
		return totalPercentualIndicador;
	}

	public void setTotalPercentualIndicador(Double totalPercentualIndicador) {
		this.totalPercentualIndicador = totalPercentualIndicador;
	}

	@Override
	public Logger getLogger() {
		return LOGGER;
	}		
}
