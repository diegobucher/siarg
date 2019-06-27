package br.gov.caixa.gitecsa.siarg.controller;

import java.io.InputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import br.gov.caixa.gitecsa.siarg.dto.AnaliticoDemandantesDemandadosDTO;
import br.gov.caixa.gitecsa.siarg.model.Abrangencia;
import br.gov.caixa.gitecsa.siarg.model.Assunto;
import br.gov.caixa.gitecsa.siarg.model.Unidade;
import br.gov.caixa.gitecsa.siarg.service.AssuntoService;
import br.gov.caixa.gitecsa.siarg.service.DemandaService;
import br.gov.caixa.gitecsa.siarg.service.UnidadeService;

@Named
@ViewScoped
public class RelatorioAnaliticoPorAssuntoController extends BaseController implements Serializable {

  /** Serial. */
  private static final long serialVersionUID = -8203179082703760860L;

  /** Constante. */
  public static final Logger LOGGER = Logger.getLogger(RelatorioAnaliticoPorAssuntoController.class);

  @Inject
  private AssuntoService assuntoService;

  @Inject
  private DemandaService demandaService;

  @Inject
  private UnidadeService unidadeService;

  private Date dataInicio;

  private Date datatFim;

  private String dataInicialString;

  private String dataFinalString;

  private List<Assunto> listaTodosAssuntos;

  private List<AnaliticoDemandantesDemandadosDTO> relatorio;
  
  private List<Unidade> listaUnidades;

  private Unidade unidadeSelecionada;
  
  private String tamanhoList;

  @PostConstruct
  public void init() {
    limparCampos();
    Abrangencia abrangenciaSelecionada = (Abrangencia) JSFUtil.getSessionMapValue("abrangencia");
    this.listaTodosAssuntos = this.assuntoService.obterAssuntosFetchPai();
    this.listaUnidades = unidadeService.obterListaUnidadesRelatorioAnaliticoPorAssunto(abrangenciaSelecionada.getId());
    this.unidadeSelecionada = null;
    
    this.datatFim = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    this.dataFinalString = sdf.format(datatFim).toString();
        
    this.dataInicio = DateUtils.addDays(datatFim, -6);
    this.dataInicialString = sdf.format(dataInicio).toString(); 
  }


  public String pesquisar() {
    limparPesquisa();    
    if (validarCampos()) {
      this.relatorio = demandaService.relatorioAnaliticoUnidadesDemandantesXUnidadesDemandadas(dataInicio, datatFim, unidadeSelecionada, listaTodosAssuntos);
    }
    return null;
  }

  private void limparCampos() {
    limparPesquisa();
    this.dataInicio = null;
    this.datatFim = null;
    this.dataInicialString = null;
    this.dataFinalString = null;
  }

  private void limparPesquisa() {
    this.relatorio = new ArrayList<>();    
  }

  private boolean validarCampos() {

    if (StringUtils.isBlank(this.dataInicialString)) {
      this.facesMessager.addMessageError(MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA002")));
      return Boolean.FALSE;
    }

    if (StringUtils.isBlank(this.dataFinalString)) {
      this.facesMessager.addMessageError(MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA002")));
      return Boolean.FALSE;
    }

    try {
      this.datatFim = DateUtils.parseDateStrictly(dataFinalString, "dd/MM/yyyy");
    } catch (ParseException e) {
      this.facesMessager.addMessageError(MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA033")));
      return Boolean.FALSE;
    }

    try {
      this.dataInicio = DateUtils.parseDateStrictly(dataInicialString, "dd/MM/yyyy");
    } catch (ParseException e) {
      this.facesMessager.addMessageError(MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA033")));
      return Boolean.FALSE;
    }

    if (this.dataInicio == null) {
      this.facesMessager.addMessageError(MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA002")));
      return Boolean.FALSE;
    }

    if (this.datatFim == null) {
      this.facesMessager.addMessageError(MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA002")));
      return Boolean.FALSE;
    }

    if (this.datatFim.before(this.dataInicio)) {
      this.facesMessager.addMessageError(MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA034")));
      return Boolean.FALSE;
    }
    
    if (this.datatFim.after(new Date())) {
      this.facesMessager.addMessageError(MensagemUtil.obterMensagem(MensagemUtil.obterMensagem("MA035")));
      return Boolean.FALSE;
    }

    return Boolean.TRUE;
  }

  /**
   * Geração do Relatório em Excel - paisagem.
   */
  public StreamedContent downloadExcel() {
    try {
      HashMap<String, Object> params = new HashMap<String, Object>();

      params.put("LOGO_CAIXA", String.format("%slogoCaixa.png", Constantes.REPORT_IMG_DIR));
      params.put("DATA_INICIO", dataInicio);
      params.put("DATA_FIM", datatFim);
      params.put("NOME_SISTEMA",
          MensagemUtil.obterMensagem("geral.nomesistema") + " - " + MensagemUtil.obterMensagem("geral.subnomesistema"));
      InputStream jasper =
          this.getClass().getClassLoader()
              .getResourceAsStream(String.format("%sVisaoGigovPorAssuntoXLS.jrxml", Constantes.REPORT_BASE_DIR));

      Collections.sort(this.relatorio);
      return JasperReportUtils.runXLS(jasper, "VisaoGigovPorAssunto", params, this.relatorio);
      
    } catch (Exception e) {
      LOGGER.error(e);
    }
    return null;
  }

  
  /**
   * Obter o valor padrão.
   * @return the tamanhoList
   */
  public String getTamanhoList() {
    this.tamanhoList = "0";
    if ((this.relatorio != null) && !this.relatorio.isEmpty()) {
      this.tamanhoList = String.valueOf((this.relatorio.size()));
    }
    return this.tamanhoList;
  }
  
  
  @Override
  public Logger getLogger() {
    return LOGGER;
  }

  /**
   * @return the dataInicio
   */
  public Date getDataInicio() {
    return dataInicio;
  }

  /**
   * @param dataInicio the dataInicio to set
   */
  public void setDataInicio(Date dataInicio) {
    this.dataInicio = dataInicio;
  }

  /**
   * @return the datatFim
   */
  public Date getDatatFim() {
    return datatFim;
  }

  /**
   * @param datatFim the datatFim to set
   */
  public void setDatatFim(Date datatFim) {
    this.datatFim = datatFim;
  }

  /**
   * @return the relatorio
   */
  public List<AnaliticoDemandantesDemandadosDTO> getRelatorio() {
    return relatorio;
  }

  /**
   * @param relatorio the relatorio to set
   */
  public void setRelatorio(List<AnaliticoDemandantesDemandadosDTO> relatorio) {
    this.relatorio = relatorio;
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
   * @return the listaUnidades
   */
  public List<Unidade> getListaUnidades() {
    return listaUnidades;
  }


  /**
   * @param listaUnidades the listaUnidades to set
   */
  public void setListaUnidades(List<Unidade> listaUnidades) {
    this.listaUnidades = listaUnidades;
  }


  /**
   * @return the unidadeSelecionada
   */
  public Unidade getUnidadeSelecionada() {
    return unidadeSelecionada;
  }


  /**
   * @param unidadeSelecionada the unidadeSelecionada to set
   */
  public void setUnidadeSelecionada(Unidade unidadeSelecionada) {
    this.unidadeSelecionada = unidadeSelecionada;
  }


  /**
   * @param tamanhoList the tamanhoList to set
   */
  public void setTamanhoList(String tamanhoList) {
    this.tamanhoList = tamanhoList;
  }

}
