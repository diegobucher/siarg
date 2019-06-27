/**
 * RelacaoAssuntosController.java
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
import br.gov.caixa.gitecsa.siarg.dto.DemandasAguardandoUnidadeDTO;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.Assunto;
import br.gov.caixa.gitecsa.siarg.model.dto.UnidadeDTO;
import br.gov.caixa.gitecsa.siarg.service.AssuntoService;
import br.gov.caixa.gitecsa.siarg.service.DemandaService;
import net.sf.jasperreports.engine.JRException;

/**
 * Classe de Controller para tela de Relatório - Demandas Aguardando Atuacao da Uniade Controller.
 * 
 * 
 */
@Named
@ViewScoped
public class DemandasAguardandoUnidadeController extends BaseController implements Serializable {

  /** Serial. */
  private static final long serialVersionUID = 1427777816614033768L;
  
  private Date dataInicial;
  
  private Date dataFinal;
  
  private String dataInicialString;
  
  private String dataFinalString;
  
  private List<DemandasAguardandoUnidadeDTO> listDemandasUnidadeDTO;
  
  @Inject
  private AssuntoService assuntoService;
  
  private List<Assunto> listaTodosAssuntos;
  
  private Abrangencia abrangenciaSelecionada;
  
  private List<UnidadeDTO> listaUnidadesDTOCombo;
  
  private List<UnidadeDTO> listaUnidadesDTO;
  
  private UnidadeDTO unidadeSelecionadaDTO;
  
  private String unidadeSelecionada;
  
  private String tamanhoList;
  
  private Boolean flagPesquisando;
  
  @Inject
  private DemandaService demandaService;

  private List<String> listaUnidadesStringCombo;
  
  private static final String FORMATO_DATA = "dd/MM/yyyy";


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
		  
		  this.listDemandasUnidadeDTO = 
				  this.demandaService.obterDemandasAguardandoUnidade(this.dataInicial, this.dataFinal, this.abrangenciaSelecionada, idUnidade, listaTodosAssuntos);
	  }
	  
  }



  /**
   * Geração do Relatório em PDF paisagem.
   */
  public StreamedContent downloadPdf() throws JRException {
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
	    .getResourceAsStream(String.format("%sDemandasAguardandoAtuacaoUnidade.jasper", Constantes.REPORT_BASE_DIR));
    Collections.sort(this.listDemandasUnidadeDTO);
    return JasperReportUtils.run(jasper, "DemandasAguardandoAtuacaoUnidade.pdf", params, this.listDemandasUnidadeDTO);
  }
  
  /**
   * Geração do Relatório em Excel - paisagem.
   */
  public StreamedContent downloadExcel() throws JRException {
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
	      .getResourceAsStream(String.format("%sDemandasAguardandoAtuacaoUnidade.jrxml", Constantes.REPORT_BASE_DIR));
      
      Collections.sort(this.listDemandasUnidadeDTO);
      return JasperReportUtils.runXLS(jasper, "DemandasAguardandoAtuacaoUnidade", params, this.listDemandasUnidadeDTO);

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
    if ((this.listDemandasUnidadeDTO != null) && !this.listDemandasUnidadeDTO.isEmpty()) {
      this.tamanhoList = String.valueOf((this.listDemandasUnidadeDTO.size()));
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
	
	public List<DemandasAguardandoUnidadeDTO> getListDemandasAguardandoUnidade() {
		return listDemandasUnidadeDTO;
	}
	
	public void setListDemandasAguardandoUnidade(List<DemandasAguardandoUnidadeDTO> listDemandasAguardandoUnidade) {
		this.listDemandasUnidadeDTO = listDemandasAguardandoUnidade;
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

	public List<DemandasAguardandoUnidadeDTO> getlistDemandasUnidadeDTO() {
		return listDemandasUnidadeDTO;
	}

	public void setListDemandasUnidadeDTO(List<DemandasAguardandoUnidadeDTO> listDemandasUnidadeDTO) {
		this.listDemandasUnidadeDTO = listDemandasUnidadeDTO;
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
	
}
