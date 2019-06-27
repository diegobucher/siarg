/**
 * QuantidadeInteracoesController.java
 * Versão: 1.0.0.00
 * 19/01/2018
 * Copyright (c) 2017 CAIXA.
 * Todos os direitos reservados.
 */
package br.gov.caixa.gitecsa.siarg.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.model.StreamedContent;

import br.gov.caixa.gitecsa.arquitetura.controller.BaseController;
import br.gov.caixa.gitecsa.arquitetura.util.Constantes;
import br.gov.caixa.gitecsa.arquitetura.util.JSFUtil;
import br.gov.caixa.gitecsa.arquitetura.util.JasperReportUtils;
import br.gov.caixa.gitecsa.arquitetura.util.MensagemUtil;
import br.gov.caixa.gitecsa.siarg.dto.QuantidadeIteracoesDTO;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.Assunto;
import br.gov.caixa.gitecsa.siarg.model.Demanda;
import br.gov.caixa.gitecsa.siarg.model.dto.DemandaDTO;
import br.gov.caixa.gitecsa.siarg.model.dto.UnidadeDTO;
import br.gov.caixa.gitecsa.siarg.service.AssuntoService;
import br.gov.caixa.gitecsa.siarg.service.AtendimentoService;
import br.gov.caixa.gitecsa.siarg.service.DemandaService;
import net.sf.jasperreports.engine.JRException;

/**
 * Classe de Controller para tela de Relatório - Quantidade de Iterações e Tempo Médio Controller.
 * 
 * 
 */
@Named
@ViewScoped
public class QuantidadeIteracoesController extends BaseController implements Serializable {

  /** Serial. */
  private static final long serialVersionUID = 1427777816614033768L;
  
  private Date dataInicial;
  
  private Date dataFinal;
  
  private String dataInicialString;
  
  private String dataFinalString;
  
  private List<DemandaDTO> demandasPesquisadasDetalhe;
  
  private List<QuantidadeIteracoesDTO> quantidadeIteracoesDTOList;
  
  private QuantidadeIteracoesDTO qtdIteracoesDTO;
  
  @Inject
  private DemandaService demandaService;
  
  @Inject
  private AssuntoService assuntoService;
  
  @Inject
  private AtendimentoService atendimentoService;
  
  private List<Assunto> listaTodosAssuntos;
  
  private Abrangencia abrangenciaSelecionada;
  
  private List<UnidadeDTO> listaUnidadesDTOCombo;
  
  private List<UnidadeDTO> listaUnidadesDTO;
  
  private UnidadeDTO unidadeSelecionadaDTO;
  
  private String unidadeSelecionada;
  
  private String tamanhoList;
  
  private Boolean flagPesquisando;
  
  private List<String> listaUnidadesStringCombo;
  
  private static final String FORMATO_DATA = "dd/MM/yyyy";
  
  private long totalQuestionar;
  
  private long totalConsultar;
  
  private long totalEncaminhar;
  
  private long totalResponder;
  
  private Double tempoMedioQuestionar;
  
  private Double tempoMedioConsultar;
  
  private Double tempoMedioEncaminhar;
  
  private Double tempoMedioResponder;
  
  private String detalheSituacao;
  private String detalheCaixaPostal;
  private String detalheQuantidade;
  private Double detalheTempoMedio;


  @PostConstruct
  public void init() {
	
	flagPesquisando = false;  
	  
	Calendar calendarDataFim = Calendar.getInstance();
    Date dataInicio = new Date();
    calendarDataFim.setTime(dataInicio);
    calendarDataFim.add(Calendar.DAY_OF_MONTH, -15);
    SimpleDateFormat sdf = new SimpleDateFormat(FORMATO_DATA);
    Date dataFim = new Date();
    this.dataInicialString = sdf.format(calendarDataFim.getTime());
    this.dataFinalString = sdf.format(dataFim);
    
    this.listaTodosAssuntos = this.assuntoService.obterAssuntosFetchPai();
    this.listaUnidadesStringCombo = new ArrayList<>();
    this.listaUnidadesDTO = (List<UnidadeDTO>) JSFUtil.getSessionMapValue("unidadesDTOList");
    this.abrangenciaSelecionada = (Abrangencia) JSFUtil.getSessionMapValue("abrangencia");
    
    this.listaUnidadesDTOCombo = this.obterListaUnidadePorAbrangencia();
    		
    if(this.listaUnidadesDTOCombo.size() > 1) {
    	listaUnidadesStringCombo.add("TODAS");
    }
    
    for(UnidadeDTO unidade : listaUnidadesDTOCombo) {
    	listaUnidadesStringCombo.add(unidade.getSigla());
    }
   
  }
  
  private void limparTotalizadores() {
	  this.totalEncaminhar = 0;
	  this.totalConsultar = 0;
	  this.totalQuestionar = 0;
	  this.totalResponder = 0;
	  this.tempoMedioQuestionar = 0d;
	  this.tempoMedioConsultar = 0d;
	  this.tempoMedioEncaminhar = 0d;
	  this.tempoMedioResponder = 0d;
  }
  
  private List<UnidadeDTO> obterListaUnidadePorAbrangencia(){
	  List<UnidadeDTO> unidade = new ArrayList<>();
	  
	  for(UnidadeDTO dto : this.listaUnidadesDTO) {
		  if(dto.getAbrangencia().equals(this.abrangenciaSelecionada.getId())) {
			  unidade.add(dto);
		  }
	  }
	  
	  return unidade;
  }

  public boolean validarCampos() {
    try {
      
      flagPesquisando = true;
      qtdIteracoesDTO = null;
    	
      if(StringUtils.isBlank(this.dataInicialString) 
          || StringUtils.isBlank(this.dataFinalString)) {
        this.facesMessager.addMessageError( MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA002")));
        return Boolean.FALSE;
      }
      
      Date dataIni = DateUtils.parseDateStrictly(this.dataInicialString,FORMATO_DATA);
      Date dataFinalFinal = DateUtils.parseDateStrictly(this.dataFinalString,FORMATO_DATA);
      
      if (dataFinalFinal.before(dataIni)) {
        this.facesMessager.addMessageError(MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA034")));
        return Boolean.FALSE;
      }
    } catch (ParseException e) {
      this.facesMessager.addMessageError( MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA033")));
      return Boolean.FALSE;
    }
    
    return Boolean.TRUE;
  }
  
  public void pesquisar() throws ParseException {
	  
	  if(validarCampos()) {
		  try {
			  this.limparTotalizadores();
			  DateFormat format = new SimpleDateFormat(FORMATO_DATA);
			  this.dataInicial = format.parse(dataInicialString);
			  this.dataFinal = format.parse(dataFinalString);
			  
			  Long idUnidade = null;
			  if(!this.unidadeSelecionada.equals("TODAS")) {
		          for (UnidadeDTO unidadeDTO : listaUnidadesDTOCombo) {
		            if(this.unidadeSelecionada.equals(unidadeDTO.getSigla())) {
		              this.unidadeSelecionadaDTO = unidadeDTO;
		              idUnidade = unidadeSelecionadaDTO.getId();
		              break;
		            }
		          }
	          }
			  
			  this.quantidadeIteracoesDTOList = 
					 this.atendimentoService.obterListaAtendimentosPorUnidade(idUnidade, this.abrangenciaSelecionada, this.dataInicial, this.dataFinal);
		  }catch(ParseException e) {
			  this.facesMessager.addMessageError(MensagemUtil.obterMensagem("MA033"));
		  }
	  }
	  
  }
  
public void exibeDemandasSelecionadas(Date datainicial, Date dataFinal, QuantidadeIteracoesDTO item, String situacao) {
	qtdIteracoesDTO = item;
	String caixaPostal = qtdIteracoesDTO.getCaixaPostal();
    List<Demanda> demandaAuxList = demandaService.obterDemandasPorAcaoAtendimento(datainicial, dataFinal, caixaPostal, situacao);
    
    this.detalheSituacao = situacao;
    this.detalheCaixaPostal = item.getCaixaPostal();
    
    if(situacao.equals("Consultar")) {
    	this.detalheTempoMedio = item.getTempoMedioConsultar();
    	this.detalheQuantidade = String.valueOf(item.getQtdeIteracoesConsultar());
    }else if(situacao.equals("Encaminhar")) {
    	this.detalheTempoMedio = item.getTempoMedioEncaminhar();
    	this.detalheQuantidade = String.valueOf(item.getQtdeIteracoesEncaminhar());
    }else if(situacao.equals("Questionar")) {
    	this.detalheTempoMedio = item.getTempoMedioQuestionar();
    	this.detalheQuantidade = String.valueOf(item.getQtdeIteracoesQuestionar());
    }else if(situacao.equals("Responder")) {
    	this.detalheTempoMedio = item.getTempoMedioResponder();
    	this.detalheQuantidade = String.valueOf(item.getQtdeIteracoesResponder());
    }

    montarDemandaDTO(demandaAuxList);
  }

  private List<DemandaDTO> montarDemandaDTO(List<Demanda> demandaAuxList){
	  if(!demandaAuxList.isEmpty()) {
		  demandasPesquisadasDetalhe = demandaService.obterListaDemandasRelatorioAssuntoPeriodo(demandaAuxList);
	  }
	  return demandasPesquisadasDetalhe;
  }
  
  public long getTotalQtdIteracoesQuestionar() {
	  if(quantidadeIteracoesDTOList != null && !quantidadeIteracoesDTOList.isEmpty()) {
		  for(QuantidadeIteracoesDTO qtdDto : quantidadeIteracoesDTOList) {
			  totalQuestionar += qtdDto.getQtdeIteracoesQuestionar();
		  }
	  }
	  return totalQuestionar;
  }
  
  public Double getTotalTempoMedioQuestionar() {
	  if(quantidadeIteracoesDTOList != null && !quantidadeIteracoesDTOList.isEmpty()) {
		  for(QuantidadeIteracoesDTO qtdDto : quantidadeIteracoesDTOList) {
			  tempoMedioQuestionar += qtdDto.getTempoMedioQuestionar();
		  }
	  }
	  return tempoMedioQuestionar;
  }
  
  public long getTotalQtdIteracoesConsultar() {
	  if(quantidadeIteracoesDTOList != null && !quantidadeIteracoesDTOList.isEmpty()) {
		  for(QuantidadeIteracoesDTO qtdDto : quantidadeIteracoesDTOList) {
			  totalConsultar += qtdDto.getQtdeIteracoesConsultar();
		  }
	  }
	  
	  return totalConsultar;
  }
  
  public Double getTotalTempoMedioConsultar() {
	  if(quantidadeIteracoesDTOList != null && !quantidadeIteracoesDTOList.isEmpty()) {
		  for(QuantidadeIteracoesDTO qtdDto : quantidadeIteracoesDTOList) {
			  tempoMedioConsultar += qtdDto.getTempoMedioConsultar();
		  }
	  }
	  return tempoMedioConsultar;
  }
  
  public long getTotalQtdIteracoesEncaminhar() {
	  if(quantidadeIteracoesDTOList != null && !quantidadeIteracoesDTOList.isEmpty()) {
		  for(QuantidadeIteracoesDTO qtdDto : quantidadeIteracoesDTOList) {
			  totalEncaminhar += qtdDto.getQtdeIteracoesEncaminhar();
		  }
	  }
	  
	  return totalEncaminhar;
  }
  
  public Double getTotalTempoMedioEncaminhar() {
	  if(quantidadeIteracoesDTOList != null && !quantidadeIteracoesDTOList.isEmpty()) {
		  for(QuantidadeIteracoesDTO qtdDto : quantidadeIteracoesDTOList) {
			  tempoMedioEncaminhar += qtdDto.getTempoMedioEncaminhar();
		  }
	  }
	  return tempoMedioEncaminhar;
  }
  
  public long getTotalQtdIteracoesResponder() {
	  if(quantidadeIteracoesDTOList != null && !quantidadeIteracoesDTOList.isEmpty()) {
		  for(QuantidadeIteracoesDTO qtdDto : quantidadeIteracoesDTOList) {
			  totalResponder += qtdDto.getQtdeIteracoesResponder();
		  }
	  }
	  
	  return totalResponder;
  }
  
  public Double getTotalTempoMedioResponder() {
	  if(quantidadeIteracoesDTOList != null && !quantidadeIteracoesDTOList.isEmpty()) {
		  for(QuantidadeIteracoesDTO qtdDto : quantidadeIteracoesDTOList) {
			  tempoMedioResponder += qtdDto.getTempoMedioResponder();
		  }
	  }
	  return tempoMedioResponder;
  }
  
  private void totaisTempoMedioReport() {
	  for(QuantidadeIteracoesDTO qtdDto : quantidadeIteracoesDTOList) {
		  qtdDto.setTotaltempoMedioConsultar(tempoMedioConsultar);
		  qtdDto.setTotaltempoMedioEncaminhar(tempoMedioEncaminhar);
		  qtdDto.setTotaltempoMedioQuestionar(tempoMedioQuestionar);
		  qtdDto.setTotaltempoMedioResponder(tempoMedioResponder);
	  }
  }

  /**
   * Geração do Relatório em PDF paisagem.
   */
  public StreamedContent downloadPdf() throws JRException {
	
	this.totaisTempoMedioReport();  
	
    HashMap<String, Object> params = new HashMap<>();

    String nomeUnidade = "";
    if (this.unidadeSelecionadaDTO != null) {
      nomeUnidade = this.unidadeSelecionadaDTO.getSigla();
    } else {
      nomeUnidade = "Todas";
    }
    
    params.put("LOGO_CAIXA", String.format("%slogoCaixa.png", Constantes.REPORT_IMG_DIR));
    params.put("DATA_INICIO", dataInicial);
    params.put("DATA_FIM", dataFinal);
    params.put("NOME_SISTEMA",
        MensagemUtil.obterMensagem("geral.nomesistema") + " - " + MensagemUtil.obterMensagem("geral.subnomesistema"));
    params.put("NOME_UNIDADE", nomeUnidade);

    InputStream jasper =
        this.getClass().getClassLoader()
	    .getResourceAsStream(String.format("%sRelatorioIteracoesTempoMedio.jasper", Constantes.REPORT_BASE_DIR));
    Collections.sort(this.quantidadeIteracoesDTOList);
    return JasperReportUtils.run(jasper, "RelatorioQtdeIteracoesTempoMedio.pdf", params, this.quantidadeIteracoesDTOList);
  }
  
  /**
   * Geração do Relatório em Excel - paisagem.
   */
  public StreamedContent downloadExcel() throws JRException {
	
	  this.totaisTempoMedioReport();  
    try {
      String nomeUnidade = "";
      if (this.unidadeSelecionadaDTO != null) {
        nomeUnidade = this.unidadeSelecionadaDTO.getSigla();
      } else {
        nomeUnidade = "Todas";
      }

      HashMap<String, Object> params = new HashMap<>();

      params.put("LOGO_CAIXA", String.format("%slogoCaixa.png", Constantes.REPORT_IMG_DIR));
      params.put("NOME_SISTEMA",
          MensagemUtil.obterMensagem("geral.nomesistema") + " - " + MensagemUtil.obterMensagem("geral.subnomesistema"));
      params.put("NOME_UNIDADE", nomeUnidade);
      params.put("DATA_INICIO", dataInicial);
      params.put("DATA_FIM", dataFinal);

      InputStream jasper =
           this.getClass().getClassLoader()
	      .getResourceAsStream(String.format("%sRelatorioIteracoesTempoMedio.jrxml", Constantes.REPORT_BASE_DIR));
      
      Collections.sort(this.quantidadeIteracoesDTOList);
      return JasperReportUtils.runXLS(jasper, "RelatorioQtdeIteracoesTempoMedio", params, this.quantidadeIteracoesDTOList);

    } catch (IOException e) {
      LOGGER.info(e);
      return null;
    }
  }
  
  /**
   * Geração do Relatório em Excel - paisagem.
   */
  public StreamedContent downloadExcelDetalhe() throws JRException {
	
    try {
      HashMap<String, Object> params = new HashMap<>();

      params.put("LOGO_CAIXA", String.format("%slogoCaixa.png", Constantes.REPORT_IMG_DIR));
      params.put("DATA_INICIO", this.dataInicial);
      params.put("DATA_FIM", this.dataFinal);
      params.put("NOME_SISTEMA",
          MensagemUtil.obterMensagem("geral.nomesistema") + " - " + MensagemUtil.obterMensagem("geral.subnomesistema"));

      InputStream jasper =
          this.getClass().getClassLoader()
              .getResourceAsStream(String.format("%sRelatorioIteracoesTempoMedioDetalhe.jrxml", Constantes.REPORT_BASE_DIR));

      return JasperReportUtils.runXLS(jasper, "RelatorioIteracoesTempoMedio", params, this.demandasPesquisadasDetalhe);

    } catch (IOException e) {
      LOGGER.info(e);
      return null;
    }
  }
  
  /**
   * Obter o valor padrão.
   * 
   * @return the tamanhoDTOList
   */
  public String getTamanhoList() {
    this.tamanhoList = "0";
    if ((this.quantidadeIteracoesDTOList != null) && !this.quantidadeIteracoesDTOList.isEmpty()) {
      this.tamanhoList = String.valueOf((this.quantidadeIteracoesDTOList.size()));
    }
    return this.tamanhoList;
  }

	@Override
	public Logger getLogger() {
		return null;
	}
	
	public Date getDataInicial() {
		return dataInicial;
	}
	
	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}
	
	public Date getDataFinal() {
		return dataFinal;
	}
	
	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}
	
	public String getDataInicialString() {
		return dataInicialString;
	}
	
	public void setDataInicialString(String dataInicialString) {
		this.dataInicialString = dataInicialString;
	}
	
	public String getDataFinalString() {
		return dataFinalString;
	}
	
	public void setDataFinalString(String dataFinalString) {
		this.dataFinalString = dataFinalString;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public List<UnidadeDTO> getListaUnidadesDTOCombo() {
		return listaUnidadesDTOCombo;
	}

	public void setListaUnidadesDTOCombo(List<UnidadeDTO> listaUnidadesDTOCombo) {
		this.listaUnidadesDTOCombo = listaUnidadesDTOCombo;
	}

	public Abrangencia getAbrangenciaSelecionada() {
		return abrangenciaSelecionada;
	}

	public void setAbrangenciaSelecionada(Abrangencia abrangenciaSelecionada) {
		this.abrangenciaSelecionada = abrangenciaSelecionada;
	}

	public List<UnidadeDTO> getListaUnidadesDTO() {
		return listaUnidadesDTO;
	}

	public void setListaUnidadesDTO(List<UnidadeDTO> listaUnidadesDTO) {
		this.listaUnidadesDTO = listaUnidadesDTO;
	}

	public List<String> getListaUnidadesStringCombo() {
		return listaUnidadesStringCombo;
	}

	public void setListaUnidadesStringCombo(List<String> listaUnidadesStringCombo) {
		this.listaUnidadesStringCombo = listaUnidadesStringCombo;
	}

	public String getUnidadeSelecionada() {
		return unidadeSelecionada;
	}

	public void setUnidadeSelecionada(String unidadeSelecionada) {
		this.unidadeSelecionada = unidadeSelecionada;
	}

	public void setTamanhoList(String tamanhoList) {
		this.tamanhoList = tamanhoList;
	}

	public UnidadeDTO getUnidadeSelecionadaDTO() {
		return unidadeSelecionadaDTO;
	}

	public void setUnidadeSelecionadaDTO(UnidadeDTO unidadeSelecionadaDTO) {
		this.unidadeSelecionadaDTO = unidadeSelecionadaDTO;
	}

	public AssuntoService getAssuntoService() {
		return assuntoService;
	}

	public void setAssuntoService(AssuntoService assuntoService) {
		this.assuntoService = assuntoService;
	}

	public List<Assunto> getListaTodosAssuntos() {
		return listaTodosAssuntos;
	}

	public void setListaTodosAssuntos(List<Assunto> listaTodosAssuntos) {
		this.listaTodosAssuntos = listaTodosAssuntos;
	}

	public DemandaService getDemandaService() {
		return demandaService;
	}

	public void setDemandaService(DemandaService demandaService) {
		this.demandaService = demandaService;
	}

	public Boolean getFlagPesquisando() {
		return flagPesquisando;
	}

	public void setFlagPesquisando(Boolean flagPesquisando) {
		this.flagPesquisando = flagPesquisando;
	}

	public List<DemandaDTO> getDemandasPesquisadasDetalhe() {
		return demandasPesquisadasDetalhe;
	}

	public void setDemandasPesquisadasDetalhe(List<DemandaDTO> demandasPesquisadasDetalhe) {
		this.demandasPesquisadasDetalhe = demandasPesquisadasDetalhe;
	}

	public List<QuantidadeIteracoesDTO> getQuantidadeIteracoesDTOList() {
		return quantidadeIteracoesDTOList;
	}

	public void setQuantidadeIteracoesDTOList(List<QuantidadeIteracoesDTO> quantidadeIteracoesDTOList) {
		this.quantidadeIteracoesDTOList = quantidadeIteracoesDTOList;
	}

	public long getTotalQuestionar() {
		return totalQuestionar;
	}

	public void setTotalQuestionar(long totalQuestionar) {
		this.totalQuestionar = totalQuestionar;
	}

	public long getTotalConsultar() {
		return totalConsultar;
	}

	public void setTotalConsultar(long totalConsultar) {
		this.totalConsultar = totalConsultar;
	}

	public long getTotalEncaminhar() {
		return totalEncaminhar;
	}

	public void setTotalEncaminhar(long totalEncaminhar) {
		this.totalEncaminhar = totalEncaminhar;
	}

	public long getTotalResponder() {
		return totalResponder;
	}

	public void setTotalResponder(long totalResponder) {
		this.totalResponder = totalResponder;
	}

	public Double getTempoMedioQuestionar() {
		return tempoMedioQuestionar;
	}

	public void setTempoMedioQuestionar(Double tempoMedioQuestionar) {
		this.tempoMedioQuestionar = tempoMedioQuestionar;
	}

	public Double getTempoMedioConsultar() {
		return tempoMedioConsultar;
	}

	public void setTempoMedioConsultar(Double tempoMedioConsultar) {
		this.tempoMedioConsultar = tempoMedioConsultar;
	}

	public Double getTempoMedioEncaminhar() {
		return tempoMedioEncaminhar;
	}

	public void setTempoMedioCaminhar(Double tempoMedioEncaminhar) {
		this.tempoMedioEncaminhar = tempoMedioEncaminhar;
	}

	public Double getTempoMedioResponder() {
		return tempoMedioResponder;
	}

	public void setTempoMedioResponder(Double tempoMedioResponder) {
		this.tempoMedioResponder = tempoMedioResponder;
	}

	public QuantidadeIteracoesDTO getQtdIteracoesDTO() {
		return qtdIteracoesDTO;
	}

	public void setQtdIteracoesDTO(QuantidadeIteracoesDTO qtdIteracoesDTO) {
		this.qtdIteracoesDTO = qtdIteracoesDTO;
	}

	public String getDetalheSituacao() {
		return detalheSituacao;
	}

	public void setDetalheSituacao(String detalheSituacao) {
		this.detalheSituacao = detalheSituacao;
	}

	public String getDetalheCaixaPostal() {
		return detalheCaixaPostal;
	}

	public void setDetalheCaixaPostal(String detalheCaixaPostal) {
		this.detalheCaixaPostal = detalheCaixaPostal;
	}

	public String getDetalheQuantidade() {
		return detalheQuantidade;
	}

	public void setDetalheQuantidade(String detalheQuantidade) {
		this.detalheQuantidade = detalheQuantidade;
	}

	public Double getDetalheTempoMedio() {
		return detalheTempoMedio;
	}

	public void setDetalheTempoMedio(Double detalheTempoMedio) {
		this.detalheTempoMedio = detalheTempoMedio;
	}
}
