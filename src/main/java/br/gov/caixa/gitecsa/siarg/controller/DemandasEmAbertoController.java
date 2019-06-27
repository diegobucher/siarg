/**
' * RelacaoAssuntosController.java
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
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
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
import br.gov.caixa.gitecsa.siarg.dto.DemandasEmAbertoDTO;
import br.gov.caixa.gitecsa.siarg.dto.RelacaoAssuntosDTO;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.Assunto;
import br.gov.caixa.gitecsa.siarg.model.CaixaPostal;
import br.gov.caixa.gitecsa.siarg.model.Demanda;
import br.gov.caixa.gitecsa.siarg.model.FluxoAssunto;
import br.gov.caixa.gitecsa.siarg.model.FluxoDemanda;
import br.gov.caixa.gitecsa.siarg.model.comparator.AssuntoResponsavelArvoreCompletaComparator;
import br.gov.caixa.gitecsa.siarg.model.dto.UnidadeDTO;
import br.gov.caixa.gitecsa.siarg.service.AssuntoService;
import br.gov.caixa.gitecsa.siarg.service.DemandaService;
import br.gov.caixa.gitecsa.siarg.service.FluxoAssuntoService;
import br.gov.caixa.gitecsa.siarg.service.FluxoDemandaService;
import net.sf.jasperreports.engine.JRException;

/**
 * Classe de Controller para tela de Relatório - Relacao de Assuntos.
 * 
 * @author f520296
 */
@Named
@ViewScoped
public class DemandasEmAbertoController extends BaseController implements Serializable {

  private static final String EXTERNA = "@EXTERNA";

private static final String FORMATO_DATA = "dd/MM/yyyy";

  /** Serial. */
  private static final long serialVersionUID = 1427777816614033768L;

  /** Service que contém as regras de negócio da entidade Feriado. */

  @Inject
  private AssuntoService assuntoService;

  @Inject
  private FluxoAssuntoService fluxoAssuntoService;

  @Inject
  private DemandaService demandaService;
  
  @Inject
  private FluxoDemandaService fluxoDemandaService;

  /** Variável de Classe. */
  private List<UnidadeDTO> listaUnidadesDTO;

  private List<UnidadeDTO> listaUnidadesDTOCombo;

  private Abrangencia abrangenciaSelecionada;

  private UnidadeDTO unidadeSelecionadaDTO;
  
  private Date dataInicial;
  
  private String dataInicialString;

  private Date dataFinal;
  
  private String dataFinalString;

  private RelacaoAssuntosDTO relacaoAssuntosDTO;

  private List<RelacaoAssuntosDTO> relacaoAssuntosDTOList;

  /** Variável de Classe. */
  private List<Assunto> listaAssuntos;
  
  private List<Assunto> listaAssuntosCombo;

  private List<Assunto> listaTodosAssuntos;

  private List<FluxoAssunto> fluxosAssuntosDemDefinList;

  private String situacaoTransacaoSelecionada;
  
  private Assunto assunto;
  
  private List<Demanda> listaDemandas;
  
  private List<DemandasEmAbertoDTO> listaDemandasDTO;
  
  private String tamanhoList;
  
  private static final String ABERTAS = "ABERTAS";
  private static final String FECHADAS = "FECHADAS";
  private static final String CANCELADAS = "CANCELADAS";
  private static final String RASCUNHADASMINUTADAS = "RASCUNHADASMINUTADAS";
  private static final String TODAS = "TODAS";
  
  private static final Integer TODAS_DEMANDA_EM_ABERTO = 1;
  private static final Integer DEMANDA_DENTRO_PRAZO = 2;
  private static final Integer DEMANDA_PRAZO_VENCIDO = 3;
  
  private List<String> listaUnidadesStringCombo;
  
  private String unidadeSelecionada;
  
  private boolean isListaDemandasCheia;
  
  private Integer situacaoDemandaSelecionada;
  
  /** Lista dos Fluxos da demanda. */
  private List<FluxoDemanda> fluxoDemandaList;


  @SuppressWarnings("unchecked")
  @PostConstruct
  public void init() {
    this.listaUnidadesStringCombo = new ArrayList<>();
    this.unidadeSelecionadaDTO = (UnidadeDTO) JSFUtil.getSessionMapValue("unidadeSelecionadaDTO");
    this.listaUnidadesDTO = (List<UnidadeDTO>) JSFUtil.getSessionMapValue("unidadesDTOList");
    this.listaTodosAssuntos = this.assuntoService.obterAssuntosFetchPai();

    this.abrangenciaSelecionada = (Abrangencia) JSFUtil.getSessionMapValue("abrangencia");

    this.listaUnidadesDTOCombo = this.obterListaUnidadePorAbrangencia();

    this.relacaoAssuntosDTO = new RelacaoAssuntosDTO();
    this.relacaoAssuntosDTOList = new ArrayList<>();
    
    this.listaAssuntos = assuntoService.pesquisarAssuntosPorAbrangencia(this.abrangenciaSelecionada);
    
    this.listaAssuntosCombo = assuntoService.preencherArvoreAssunto(listaAssuntos, listaTodosAssuntos);
    Collections.sort(listaAssuntosCombo, new AssuntoResponsavelArvoreCompletaComparator());

    Calendar calendarDataFim = Calendar.getInstance();
    Date dataInicio = new Date();
    calendarDataFim.setTime(dataInicio);
    calendarDataFim.add(Calendar.DAY_OF_MONTH, -15);
    SimpleDateFormat sdf = new SimpleDateFormat(FORMATO_DATA);
    Date dataFim = new Date();
    this.dataInicialString = sdf.format(calendarDataFim.getTime());
    this.dataFinalString = sdf.format(dataFim);
    
    if(this.listaUnidadesDTOCombo.size() > 1) {
      listaUnidadesStringCombo.add("TODAS");
    }
    
    for (UnidadeDTO unidade : listaUnidadesDTOCombo) {
      listaUnidadesStringCombo.add(unidade.getSigla());
    }
   
  }
  
  public List<SelectItem> getSituacaoPrazoList() {
    List<SelectItem> tiposUnidadeList = new ArrayList<>();

    tiposUnidadeList.add(new SelectItem(1, "Todas Demandas em Aberto"));
    tiposUnidadeList.add(new SelectItem(2, "Abertas Dentro do Prazo"));
    tiposUnidadeList.add(new SelectItem(3, "Abertas com Prazo Vencido"));

    return tiposUnidadeList;
  }

  /**
   * Método obter lista de unidades por abrangencia.
   * 
   * @return list
   */
  private List<UnidadeDTO> obterListaUnidadePorAbrangencia() {
    List<UnidadeDTO> temp = new ArrayList<>();
    for (UnidadeDTO dto : this.listaUnidadesDTO) {
      if (dto.getAbrangencia() == this.abrangenciaSelecionada.getId()) {
        temp.add(dto);
      }
    }
    return temp;
  }

  public void pesquisar() throws ParseException {
    
    if(validarCampos()) {
      DateFormat formatter = new SimpleDateFormat(FORMATO_DATA);
      this.dataInicial = (Date)formatter.parse(dataInicialString);
      this.dataFinal = (Date)formatter.parse(dataFinalString);
      
      for (UnidadeDTO unidadeDTO : listaUnidadesDTOCombo) {
        if(this.unidadeSelecionada.equals(unidadeDTO.getSigla())) {
          this.unidadeSelecionadaDTO = unidadeDTO;
          break;
        }else {
          this.unidadeSelecionadaDTO = null;
        }
      }
      
      this.listaDemandasDTO =
          this.demandaService.obterDemandasEmAbertoPorAssunto(this.unidadeSelecionadaDTO, this.assunto, this.dataInicial, this.dataFinal, this.abrangenciaSelecionada, this.listaTodosAssuntos);
      
      if(listaDemandasDTO != null && !listaDemandasDTO.isEmpty()) {
    	  
    	for (DemandasEmAbertoDTO demanda : listaDemandasDTO) {
    		
    		 if( EXTERNA.equalsIgnoreCase(demanda.getAreaDemandada())) {
    			 demanda.setAreaDemandateAnterior(buscaDemandanteAnteriorExterna(demanda));
             }else {
            	 demanda.setAreaDemandateAnterior(demanda.getAreaDemandada());
             }
		}  
    	  
        Iterator<DemandasEmAbertoDTO> iterator = listaDemandasDTO.iterator();

        while(iterator.hasNext()) {
          DemandasEmAbertoDTO demandaEmAberto = iterator.next();
         
          if(situacaoDemandaSelecionada.equals(DEMANDA_PRAZO_VENCIDO) && demandaEmAberto.getSituacaoVencimento() <= 0) {
            iterator.remove();
          }
          if(situacaoDemandaSelecionada.equals(DEMANDA_DENTRO_PRAZO) && demandaEmAberto.getSituacaoVencimento() > 0) {
            iterator.remove();
          }
        }
      }
    }
    
  }
  
  private String buscaDemandanteAnteriorExterna(DemandasEmAbertoDTO demanda) {
	  this.fluxoDemandaList = fluxoDemandaService.findByIdDemanda(demanda.getNumeroDemanda());
	  String  sigla ="";
	  for(int cont =0 ; cont< this.fluxoDemandaList.size();cont++ ) {
		  if(EXTERNA.equalsIgnoreCase(fluxoDemandaList.get(cont).getCaixaPostal().getSigla())) {
			  sigla = fluxoDemandaList.get(cont-1).getCaixaPostal().getSigla();
		  }
	  }
	  return sigla;
}

private boolean validarCampos() {
    try {
      
      if(StringUtils.isBlank(this.dataInicialString) 
          || StringUtils.isBlank(this.dataFinalString)) {
        this.facesMessager.addMessageError( MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA002")));
        return Boolean.FALSE;
      }
      
      Date dataInicial = DateUtils.parseDateStrictly(this.dataInicialString,FORMATO_DATA);
      Date dataFinalFinal = DateUtils.parseDateStrictly(this.dataFinalString,FORMATO_DATA);
      
      if (dataFinalFinal.before(dataInicial)) {
        this.facesMessager.addMessageError("Data Inicial deve ser anterior a Data Final");
        return Boolean.FALSE;
      }
    } catch (ParseException e) {
      this.facesMessager.addMessageError( MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA033")));
      return Boolean.FALSE;
    }
    
    return Boolean.TRUE;
  }

  public void alterarUnidadeSelecionada() throws ParseException {
    for (UnidadeDTO unidadeDTO : listaUnidadesDTO) {
        if(this.unidadeSelecionada.equals(unidadeDTO.getSigla())) {
          this.unidadeSelecionadaDTO = unidadeDTO;
        }else {
          this.unidadeSelecionadaDTO = null;
        }
    } 
  }

  /*
   * (non-Javadoc)
   * 
   * @see br.gov.caixa.gitecsa.arquitetura.controller.BaseController#getLogger()
   */
  @Override
  public Logger getLogger() {
    return null;
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

      HashMap<String, Object> params = new HashMap<String, Object>();

      params.put("LOGO_CAIXA", String.format("%slogoCaixa.png", Constantes.REPORT_IMG_DIR));
      params.put("NOME_SISTEMA",
          MensagemUtil.obterMensagem("geral.nomesistema") + " - " + MensagemUtil.obterMensagem("geral.subnomesistema"));
      params.put("NOME_UNIDADE", nomeUnidade);

      InputStream jasper =
          this.getClass().getClassLoader()
              .getResourceAsStream(String.format("%sDemandasEmAbertoReportXLS.jrxml", Constantes.REPORT_BASE_DIR));

      Collections.sort(this.listaDemandasDTO);
      return JasperReportUtils.runXLS(jasper, "DemandasEmAberto", params, this.listaDemandasDTO);

    } catch (IOException e) {
      LOGGER.info(e);
      return null;
    }
  }

  /**
   * Getters and Setters.
   */

  /**
   * Obter o valor padrão.
   * 
   * @return the relacaoAssuntosDTO
   */
  public RelacaoAssuntosDTO getRelacaoAssuntosDTO() {
    return this.relacaoAssuntosDTO;
  }

  /**
   * Gravar o valor do parâmetro.
   * 
   * @param relacaoAssuntosDTO
   *          the relacaoAssuntosDTO to set
   */
  public void setRelacaoAssuntosDTO(RelacaoAssuntosDTO relacaoAssuntosDTO) {
    this.relacaoAssuntosDTO = relacaoAssuntosDTO;
  }

  /**
   * Obter o valor padrão.
   * 
   * @return the relacaoAssuntosDTOList
   */
  public List<RelacaoAssuntosDTO> getRelacaoAssuntosDTOList() {
    return this.relacaoAssuntosDTOList;
  }

  /**
   * Gravar o valor do parâmetro.
   * 
   * @param relacaoAssuntosDTOList
   *          the relacaoAssuntosDTOList to set
   */
  public void setRelacaoAssuntosDTOList(List<RelacaoAssuntosDTO> relacaoAssuntosDTOList) {
    this.relacaoAssuntosDTOList = relacaoAssuntosDTOList;
  }

  /**
   * Obter o valor padrão.
   * 
   * @return the unidadeSelecionadaDTO
   */
  public UnidadeDTO getUnidadeSelecionadaDTO() {
    return this.unidadeSelecionadaDTO;
  }

  /**
   * Gravar o valor do parâmetro.
   * 
   * @param unidadeSelecionadaDTO
   *          the unidadeSelecionadaDTO to set
   */
  public void setUnidadeSelecionadaDTO(UnidadeDTO unidadeSelecionadaDTO) {
    this.unidadeSelecionadaDTO = unidadeSelecionadaDTO;
  }

  /**
   * Obter o valor padrão.
   * 
   * @return the listaAssuntos
   */
  public List<Assunto> getListaAssuntos() {
    return this.listaAssuntos;
  }

  /**
   * Gravar o valor do parâmetro.
   * 
   * @param listaAssuntos
   *          the listaAssuntos to set
   */
  public void setListaAssuntos(List<Assunto> listaAssuntos) {
    this.listaAssuntos = listaAssuntos;
  }

  /**
   * Obter o valor padrão.
   * 
   * @return the listaUnidadesDTO
   */
  public List<UnidadeDTO> getListaUnidadesDTO() {
    return this.listaUnidadesDTO;
  }

  /**
   * Gravar o valor do parâmetro.
   * 
   * @param listaUnidadesDTO
   *          the listaUnidadesDTO to set
   */
  public void setListaUnidadesDTO(List<UnidadeDTO> listaUnidadesDTO) {
    this.listaUnidadesDTO = listaUnidadesDTO;
  }

  /**
   * Obter o valor padrão.
   * 
   * @return the abrangenciaSelecionada
   */
  public Abrangencia getAbrangenciaSelecionada() {
    return this.abrangenciaSelecionada;
  }

  /**
   * Gravar o valor do parâmetro.
   * 
   * @param abrangenciaSelecionada
   *          the abrangenciaSelecionada to set
   */
  public void setAbrangenciaSelecionada(Abrangencia abrangenciaSelecionada) {
    this.abrangenciaSelecionada = abrangenciaSelecionada;
  }

  /**
   * Obter o valor padrão.
   * 
   * @return the listaUnidadesDTOCombo
   */
  public List<UnidadeDTO> getListaUnidadesDTOCombo() {
    return this.listaUnidadesDTOCombo;
  }

  /**
   * Gravar o valor do parâmetro.
   * 
   * @param listaUnidadesDTOCombo
   *          the listaUnidadesDTOCombo to set
   */
  public void setListaUnidadesDTOCombo(List<UnidadeDTO> listaUnidadesDTOCombo) {
    this.listaUnidadesDTOCombo = listaUnidadesDTOCombo;
  }

  /**
   * Obter o valor padrão.
   * 
   * @return the listaTodosAssuntos
   */
  public List<Assunto> getListaTodosAssuntos() {
    return this.listaTodosAssuntos;
  }

  /**
   * Gravar o valor do parâmetro.
   * 
   * @param listaTodosAssuntos
   *          the listaTodosAssuntos to set
   */
  public void setListaTodosAssuntos(List<Assunto> listaTodosAssuntos) {
    this.listaTodosAssuntos = listaTodosAssuntos;
  }

  /**
   * Obter o valor padrão.
   * 
   * @return the fluxosAssuntosDemDefinList
   */
  public List<FluxoAssunto> getFluxosAssuntosDemDefinList() {
    return this.fluxosAssuntosDemDefinList;
  }

  /**
   * Gravar o valor do parâmetro.
   * 
   * @param fluxosAssuntosDemDefinList
   *          the fluxosAssuntosDemDefinList to set
   */
  public void setFluxosAssuntosDemDefinList(List<FluxoAssunto> fluxosAssuntosDemDefinList) {
    this.fluxosAssuntosDemDefinList = fluxosAssuntosDemDefinList;
  }

  /**
   * Obter o valor padrão.
   * 
   * @return the tamanhoDTOList
   */
  public String getTamanhoList() {
    this.tamanhoList = "0";
    if ((this.listaDemandasDTO != null) && !this.listaDemandasDTO.isEmpty()) {
      this.tamanhoList = String.valueOf((this.listaDemandasDTO.size()));
    }
    return this.tamanhoList;
  }

  /**
   * Gravar o valor do parâmetro.
   * 
   * @param tamanhoDTOList
   *          the tamanhoDTOList to set
   */
  public void setTamanhoList(String tamanhoList) {
    this.tamanhoList = tamanhoList;
  }

  public List<SelectItem> getSituacaoTransacaoList() {
    List<SelectItem> situacoes = new ArrayList<>();

    situacoes.add(new SelectItem(ABERTAS, "Abertas"));
    situacoes.add(new SelectItem(FECHADAS, "Fechadas"));
    situacoes.add(new SelectItem(CANCELADAS, "Canceladas"));
    situacoes.add(new SelectItem(RASCUNHADASMINUTADAS, "Rascunhadas/Minutadas"));
    situacoes.add(new SelectItem(TODAS, "Todas"));

    return situacoes;
  }

  public String getSituacaoTransacaoSelecionada() {
    return situacaoTransacaoSelecionada;
  }

  public void setSituacaoTransacaoSelecionada(String situacaoTransacaoSelecionada) {
    this.situacaoTransacaoSelecionada = situacaoTransacaoSelecionada;
  }

  public boolean getIsListaDemandasCheia() {
    return isListaDemandasCheia;
  }

  public void setListaDemandasCheia(boolean isListaDemandasCheia) {
    this.isListaDemandasCheia = isListaDemandasCheia;
  }

  /**
   * @return the dataInicial
   */
  public Date getDataInicial() {
    return dataInicial;
  }

  /**
   * @param dataInicial the dataInicial to set
   */
  public void setDataInicial(Date dataInicial) {
    this.dataInicial = dataInicial;
  }

  /**
   * @return the dataInicialString
   */
  public String getDataInicialString() {
    return dataInicialString;
  }

  /**
   * @param dataInicialString the dataInicialString to set
   */
  public void setDataInicialString(String dataInicialString) {
    this.dataInicialString = dataInicialString;
  }

  /**
   * @return the dataFinal
   */
  public Date getDataFinal() {
    return dataFinal;
  }

  /**
   * @param dataFinal the dataFinal to set
   */
  public void setDataFinal(Date dataFinal) {
    this.dataFinal = dataFinal;
  }

  /**
   * @return the dataFinalString
   */
  public String getDataFinalString() {
    return dataFinalString;
  }

  /**
   * @param dataFinalString the dataFinalString to set
   */
  public void setDataFinalString(String dataFinalString) {
    this.dataFinalString = dataFinalString;
  }

  /**
   * @return the assunto
   */
  public Assunto getAssunto() {
    return assunto;
  }

  /**
   * @param assunto the assunto to set
   */
  public void setAssunto(Assunto assunto) {
    this.assunto = assunto;
  }

  /**
   * @return the listaAssuntosCombo
   */
  public List<Assunto> getListaAssuntosCombo() {
    return listaAssuntosCombo;
  }

  /**
   * @param listaAssuntosCombo the listaAssuntosCombo to set
   */
  public void setListaAssuntosCombo(List<Assunto> listaAssuntosCombo) {
    this.listaAssuntosCombo = listaAssuntosCombo;
  }

  /**
   * @return the listaDemandas
   */
  public List<Demanda> getListaDemandas() {
    return listaDemandas;
  }

  /**
   * @param listaDemandas the listaDemandas to set
   */
  public void setListaDemandas(List<Demanda> listaDemandas) {
    this.listaDemandas = listaDemandas;
  }

  /**
   * @return the listaDemandasDTO
   */
  public List<DemandasEmAbertoDTO> getListaDemandasDTO() {
    return listaDemandasDTO;
  }

  /**
   * @param listaDemandasDTO the listaDemandasDTO to set
   */
  public void setListaDemandasDTO(List<DemandasEmAbertoDTO> listaDemandasDTO) {
    this.listaDemandasDTO = listaDemandasDTO;
  }

  /**
   * @return the listaUnidadesStringCombo
   */
  public List<String> getListaUnidadesStringCombo() {
    return listaUnidadesStringCombo;
  }

  /**
   * @param listaUnidadesStringCombo the listaUnidadesStringCombo to set
   */
  public void setListaUnidadesStringCombo(List<String> listaUnidadesStringCombo) {
    this.listaUnidadesStringCombo = listaUnidadesStringCombo;
  }

  /**
   * @return the unidadeSelecionada
   */
  public String getUnidadeSelecionada() {
    return unidadeSelecionada;
  }

  /**
   * @param unidadeSelecionada the unidadeSelecionada to set
   */
  public void setUnidadeSelecionada(String unidadeSelecionada) {
    this.unidadeSelecionada = unidadeSelecionada;
  }

  public Integer getSituacaoDemandaSelecionada() {
    return situacaoDemandaSelecionada;
  }

  public void setSituacaoDemandaSelecionada(Integer situacaoDemandaSelecionada) {
    this.situacaoDemandaSelecionada = situacaoDemandaSelecionada;
  }

public List<FluxoDemanda> getFluxoDemandaList() {
	return fluxoDemandaList;
}

public void setFluxoDemandaList(List<FluxoDemanda> fluxoDemandaList) {
	this.fluxoDemandaList = fluxoDemandaList;
}

}
